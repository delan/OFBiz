/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.commonapp.accounting.payment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.commonapp.order.order.OrderReadHelper;
import org.ofbiz.commonapp.party.contact.ContactHelper;
import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.DispatchContext;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.GeneralException;
import org.ofbiz.core.util.UtilDateTime;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilProperties;

/**
 * PaymentGatewayServices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class PaymentGatewayServices {

    public static final String module = PaymentGatewayServices.class.getName();

    /**
     * Processes payments through service calls to the defined processing service for the website/paymentMethodType
     * @returns APPROVED|FAILED|ERROR for complete processing of ALL payment methods.
     */
    public static Map authOrderPayments(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String orderId = (String) context.get("orderId");        
        Map result = new HashMap();

        // get the order header and payment preferences
        GenericValue orderHeader = null;
        List paymentPrefs = null;

        try {    
            // get the OrderHeader                   
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            
            // get the payments to auth
            Map lookupMap = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_AUTH");
            List orderList = UtilMisc.toList("maxAmount");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", lookupMap, orderList);
        } catch (GenericEntityException gee) {
            Debug.logError(gee, "Problems getting the order information", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + gee.getMessage() + ").");
            return result;
        }

        // make sure we have a OrderHeader
        if (orderHeader == null) {
            return ServiceUtil.returnError("Could not find OrderHeader with orderId: " + orderId + "; not processing payments.");
        }
                 
        // get the order amounts                                 
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        DecimalFormat formatter = new DecimalFormat("##0.00");
        String grandTotalString = formatter.format(orh.getOrderGrandTotal());
        Double grandTotal = new Double(grandTotalString);
        double amountToBill = grandTotal.doubleValue();        
                      
        // loop through and auth each payment   
        List finished = new ArrayList();     
        Iterator payments = paymentPrefs.iterator();
        while (payments.hasNext()) {
            GenericValue paymentPref = (GenericValue) payments.next();
            Map processorResult = authPayment(dispatcher, orh, paymentPref, amountToBill, false);
            if (processorResult != null) {
                GenericValue paymentSettings = (GenericValue) processorResult.get("paymentSettings");
                Double thisAmount = (Double) processorResult.get("processAmount");
                finished.add(processorResult);

                // process the auth results             
                boolean processResult = false;
                try {
                    processResult = processResult(dctx, processorResult, paymentPref, paymentSettings);
                    if (processResult)
                        amountToBill -= thisAmount.doubleValue();
                } catch (GeneralException e) {
                    Debug.logError(e, "Trouble processing the result; processorResult: " + processorResult, module);
                    ServiceUtil.returnError("Trouble processing the auth results");                     
                }                                                                                                                                              
            } else {
                Debug.logError("Payment not authorized", module);
                continue;             
            }
        }

        if (finished.size() == paymentPrefs.size()) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "APPROVED");
            return result;
        } else {                            
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "FAILED");
            return result;        
        }         
    }
                   
    private static Map authPayment(LocalDispatcher dispatcher, OrderReadHelper orh, GenericValue paymentPref, double authTotal, boolean reauth) {
        String paymentConfig = null;
        String serviceName = null;        
            
        // get the payment settings i.e. serviceName and config properties file name
        GenericValue paymentSettings = getPaymentSettings(orh.getOrderHeader(), paymentPref);            
        if (paymentSettings != null) {
            if (!reauth) {            
                serviceName = paymentSettings.getString("paymentAuthService");
            } else {             
                serviceName = paymentSettings.getString("paymentReAuthService");
            }
            paymentConfig = paymentSettings.getString("paymentConfiguration");                                
        } else {
            Debug.logError("Invalid payment settings entity, no payment settings found", module);
            return null;                
        }
            
        // make sure the service name is not null                           
        if (serviceName == null) {
            Debug.logError("Invalid payment processor: + " + paymentSettings, module);
            return null;
        }
            
        // get the process context
        Map processContext = null;
        try {
            processContext = makeAuthContext(orh, paymentPref, paymentConfig, authTotal);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems creating the context for the auth service", module);
            return null;                
        }
            
        // the amount of this transaction
        Double thisAmount = (Double) processContext.get("processAmount");
                                    
        // invoke the processor.
        Map processorResult = null;
        try {            
            processorResult = dispatcher.runSync(serviceName, processContext);
        } catch (GenericServiceException gse) {
            Debug.logError("Error occurred on: " + serviceName + " => " + processContext, module);
            Debug.logError(gse, "Problems invoking payment processor!" + "(" + orh.getOrderId() + ")", module);                
            return null;
        } 
        
        if (processorResult != null) {
            // pass the payTo partyId to the result processor; we just add it to the result context.
            String payToPartyId = UtilProperties.getPropertyValue(paymentConfig, "payment.general.payTo", "Company");
            processorResult.put("payToPartyId", payToPartyId);  
        
            // add paymentSettings to result; for use by later processors
            processorResult.put("paymentSettings", paymentSettings);         
        }
        
        return processorResult;              
    }
    
    private static GenericValue getPaymentSettings(GenericValue orderHeader, GenericValue paymentPreference) {
        GenericDelegator delegator = orderHeader.getDelegator();
        GenericValue paymentSettings = null;
        GenericValue paymentMethod = null;
        try {
            paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");                
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting PaymentMethod from OrderPaymentPreference", module);
        }
        if (paymentMethod != null) {
            String webSiteId = orderHeader.getString("webSiteId");
            String paymentMethodTypeId = paymentMethod.getString("paymentMethodTypeId");
            if (webSiteId != null && paymentMethodTypeId != null) {
                paymentSettings = PaymentWorker.getPaymentSetting(delegator, webSiteId, paymentMethodTypeId);
            }            
        }
        return paymentSettings;        
    }
            
    private static Map makeAuthContext(OrderReadHelper orh, GenericValue paymentPreference, String paymentConfig, double authTotal) throws GenericEntityException {
        Map processContext = new HashMap();        
                
        processContext.put("orderId", orh.getOrderId());
        processContext.put("orderItems", orh.getOrderItems());
        processContext.put("shippingAddress", orh.getShippingAddress());
        processContext.put("paymentConfig", paymentConfig);
        processContext.put("currency", orh.getCurrency());
        
        GenericValue paymentMethod = null;
        GenericValue creditCard = null;
        GenericValue eftAccount = null;
        GenericValue billingAddress = null;
                    
        // gather the payment related objects.        
        paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
        if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
            // type credit card
            creditCard = paymentMethod.getRelatedOne("CreditCard");
            billingAddress = creditCard.getRelatedOne("PostalAddress");
            processContext.put("creditCard", creditCard);
        } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("EFT_ACCOUNT")) {
            // type eft
            eftAccount = paymentMethod.getRelatedOne("EFT_ACCOUNT");
            billingAddress.getRelatedOne("PostalAddress");
        } else {
            // add other payment types here; i.e. gift cards, etc.
            // unknown payment type; ignoring.
            return null;
        }
        processContext.put("billingAddress", billingAddress);
                       
        // get some contact info.
        GenericValue contactPerson = orh.getBillToPerson();
        GenericValue contactEmail = null;
        Collection emails = null;

        try {
            emails = ContactHelper.getContactMech(contactPerson.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
        } catch (GenericEntityException gee) {
            Debug.logError("Problems getting contact information: " + gee.getMessage(), module);
        }
        if (emails != null && emails.size() > 0)
            contactEmail = (GenericValue) emails.iterator().next();        
                                      
        processContext.put("contactPerson", contactPerson);
        processContext.put("contactEmail", contactEmail); 

        // get the process amount.
        double thisAmount = authTotal;

        // check if there is already a authAmount; if so this is a re-auth and we should use this amount.
        if (paymentPreference.get("authAmount") != null) {
            thisAmount = paymentPreference.getDouble("authAmount").doubleValue(); 
        } else if (paymentPreference.get("maxAmount") != null) {                    
            thisAmount = paymentPreference.getDouble("maxAmount").doubleValue();
        }
        
        // format the decimal
        DecimalFormat formatter = new DecimalFormat("##0.00");
        String amountString = formatter.format(thisAmount);        
        Double processAmount = new Double(amountString);
        
        if (Debug.verboseOn())
            Debug.logVerbose("Charging amount: " + processAmount, module);                  
        processContext.put("processAmount", processAmount);    
                                
        return processContext;
    }
           
    /**
     * Captures payments through service calls to the defined processing service for the website/paymentMethodType
     * @returns COMPLETE|FAILED|ERROR for complete processing of ALL payment methods.
     */
    public static Map captureOrderPayments(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String orderId = (String) context.get("orderId");        
        Map result = new HashMap();               

        // get the order header and payment preferences
        GenericValue orderHeader = null;
        List paymentPrefs = null;

        try {                       
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            
            // get the payment prefs
            Map lookupMap = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_AUTHORIZED");
            List orderList = UtilMisc.toList("maxAmount");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", lookupMap, orderList);
        } catch (GenericEntityException gee) {
            Debug.logError(gee, "Problems getting entity record(s), see stack trace", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + gee.getMessage() + ").");
            return result;
        }

        // error if no order was found
        if (orderHeader == null) {
            return ServiceUtil.returnError("Could not find OrderHeader with orderId: " + orderId + "; not processing payments.");
        }  
                                                                   
        // return complete if no payment prefs were found
        if (paymentPrefs == null || paymentPrefs.size() == 0) {
            Debug.logWarning("No orderPaymentPreferences available to capture", module);
            result.put("processResult", "COMPLETE");
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result;   
        }
        
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        
        // iterate over the prefs and capture each one
        List finished = new ArrayList();
        Iterator payments = paymentPrefs.iterator();
        while (payments.hasNext()) {
            GenericValue paymentPref = (GenericValue) payments.next();
            Map captureResult = capturePayment(dispatcher, orh, paymentPref);
            if (captureResult != null) {                           
                GenericValue paymentSettings = (GenericValue) captureResult.get("paymentSettings");
                Double captureAmount = (Double) captureResult.get("captureAmount");                                       
                finished.add(captureResult);
                
                // process the capture's results             
                boolean processResult = false;
                try {
                    processResult = processResult(dctx, captureResult, paymentPref, paymentSettings);                                           
                } catch (GeneralException e) {
                    Debug.logError(e, "Trouble processing the result; captureResult: " + captureResult, module);
                    ServiceUtil.returnError("Trouble processing the capture results");                     
                }
            } else {
                Debug.logError("Payment not captured", module);
                continue;
            }
        }
        
        if (finished.size() == paymentPrefs.size()) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "COMPLETE");
            return result;
        } else {                            
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "FAILED");
            return result;        
        } 
    }
    
    private static Map capturePayment(LocalDispatcher dispatcher, OrderReadHelper orh, GenericValue paymentPref) {
        // get the capture amount.
        Double captureAmount = paymentPref.getDouble("authAmount");                                  
        if (Debug.verboseOn())
            Debug.logVerbose("Charging amount: " + captureAmount, module);
                            
        // look up the payment configuration settings                                   
        String serviceName = null;
        String paymentConfig = null;
            
        // get the payment settings i.e. serviceName and config properties file name
        GenericValue paymentSettings = getPaymentSettings(orh.getOrderHeader(), paymentPref);            
        if (paymentSettings != null) {
            serviceName = paymentSettings.getString("paymentCaptureService");
            paymentConfig = paymentSettings.getString("paymentConfiguration");                                
        } else {
            Debug.logError("Invalid payment settings entity, no payment settings found", module);
            return null;             
        }
            
        // prepare the context for the capture service (must follow the ccCaptureInterface
        Map captureContext = new HashMap();
        captureContext.put("orderPaymentPreference", paymentPref);
        captureContext.put("paymentConfig", paymentConfig);
        captureContext.put("captureAmount", captureAmount);
        captureContext.put("currency", orh.getCurrency());
            
        // now invoke the capture service
        Map captureResult = null;
        try {
            captureResult = dispatcher.runSync(serviceName, captureContext);                               
        } catch (GenericServiceException e) {
            Debug.logError(e, "Could not capture payment ... serviceName: " + serviceName + " ... context: " + captureContext, module);
            return null;     
        } 
            
        // pass the payTo partyId to the result processor; we just add it to the result context.
        String payToPartyId = UtilProperties.getPropertyValue(paymentConfig, "payment.general.payTo", "Company");
        captureResult.put("payToPartyId", payToPartyId);  
        
        // add paymentSettings to result; for use by later processors
        captureResult.put("paymentSettings", paymentSettings);
         
        return captureResult;     
    }

    private static boolean processResult(DispatchContext dctx, Map result, GenericValue paymentPreference, GenericValue paymentSettings) throws GeneralException {
        Boolean authResult = (Boolean) result.get("authResult");
        Boolean captureResult = (Boolean) result.get("captureResult");
        boolean resultPassed = false;

        if (authResult != null) {
            processAuthResult(dctx, result, paymentPreference, paymentSettings);
            resultPassed = authResult.booleanValue();
        }
        if (captureResult != null) {
            processCaptureResult(dctx, result, paymentPreference, paymentSettings);
            if (!resultPassed)
                resultPassed = captureResult.booleanValue();
        }
        return resultPassed;
    }

    private static void processAuthResult(DispatchContext dctx, Map result, GenericValue paymentPreference, GenericValue paymentSettings) throws GeneralException {
        Boolean authResult = (Boolean) result.get("authResult");

        if (result != null && authResult.booleanValue()) {
            paymentPreference.set("authCode", result.get("authCode"));
            paymentPreference.set("statusId", "PAYMENT_AUTHORIZED");
        } else if (result != null && !authResult.booleanValue()) {
            paymentPreference.set("statusId", "PAYMENT_DECLINED");
        } else {
            paymentPreference.set("statusId", "PAYMENT_ERROR");
        }
        // set the avs/fraud result
        paymentPreference.set("avsCode", result.get("avsCode"));
        paymentPreference.set("scoreCode", result.get("scoreCode"));
        // set the auth info
        paymentPreference.set("authAmount", result.get("processAmount"));
        paymentPreference.set("authRefNum", result.get("authRefNum"));
        paymentPreference.set("authFlag", result.get("authFlag"));
        paymentPreference.set("authMessage", result.get("authMessage"));
        paymentPreference.set("authDate", UtilDateTime.nowTimestamp());
        paymentPreference.store();
    }

    private static void processCaptureResult(DispatchContext dctx, Map result, GenericValue paymentPreference, GenericValue paymentSettings) throws GeneralException {        
        Boolean captureResult = (Boolean) result.get("captureResult");
        String payTo = (String) result.get("payToPartyId");
        LocalDispatcher dispatcher = dctx.getDispatcher();

        if (payTo == null)
            payTo = "Company";

        GenericDelegator delegator = dctx.getDelegator();
        GenericValue payment = null;

        if (result != null && captureResult.booleanValue()) {
            // captured
            Long paymentId = delegator.getNextSeqId("Payment");

            if (paymentId == null)
                throw new GenericEntityException("Cannot get sequence ID for Payment entity.");
            GenericValue orderRole = EntityUtil.getFirst(delegator.findByAnd("OrderRole",
                        UtilMisc.toMap("orderId", paymentPreference.get("orderId"), "roleTypeId", "BILL_TO_CUSTOMER")));

            payment = delegator.makeValue("Payment", UtilMisc.toMap("paymentId", paymentId.toString(),
                            "paymentTypeId", "RECEIPT", "paymentMethodTypeId", paymentPreference.get("paymentMethodTypeId"),
                            "paymentMethodId", paymentPreference.get("paymentMethodId"), "partyIdTo", payTo,
                            "partyIdFrom", orderRole.get("partyId")));
            payment.set("paymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"));
            payment.set("amount", result.get("captureAmount"));
            payment.set("paymentRefNum", result.get("captureRefNum"));
            payment.set("effectiveDate", UtilDateTime.nowTimestamp());
            delegator.create(payment);

            if (result.get("authRefNum") != null) {            
                paymentPreference.set("authRefNum", result.get("authRefNum"));
            }
            paymentPreference.set("statusId", "PAYMENT_SETTLED");
            paymentPreference.store();

        } else if (result != null && !captureResult.booleanValue()) {
            // problem with the capture lets get some needed info
            OrderReadHelper orh = null;
            try {
                GenericValue orderHeader = paymentPreference.getRelatedOne("OrderHeader");
                if (orderHeader != null)
                    orh = new OrderReadHelper(orderHeader);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems getting OrderHeader; cannot re-auth the payment", module);                
            }
            
            if (orh != null) {
                // first lets re-auth the card
                Map authPayRes = authPayment(dispatcher, orh, paymentPreference, 0.00, true);
                if (authPayRes != null) {
                    Boolean authResp = (Boolean) result.get("authResult");
                    Boolean capResp = (Boolean) result.get("captureResult");
                    if (authResp != null) {
                        processAuthResult(dctx, authPayRes, paymentPreference, paymentSettings); 
                        if (authResp.booleanValue()) {
                            // first make sure we didn't already capture - probably not
                            if (capResp != null && capResp.booleanValue()) {
                                processCaptureResult(dctx, result, paymentPreference, paymentSettings);
                            } else {
                                // lets try to capture the funds now
                                Map capPayRes = capturePayment(dispatcher, orh, paymentPreference);
                                if (capPayRes != null) {
                                    Boolean capPayResp = (Boolean) result.get("captureResult");
                                    if (capPayResp != null && capPayResp.booleanValue()) {
                                        // it was successful
                                        processCaptureResult(dctx, result, paymentPreference, paymentSettings);
                                    } else {
                                        // not successful; log it
                                        Debug.logError("Capture of authorized payment failed: " + paymentPreference, module);
                                    }
                                } else {
                                    Debug.logError("Problems trying to capture payment (null result): " + paymentPreference, module);
                                }                                                                                                        
                            }
                        } else {
                            Debug.logError("Payment authorization failed:  " + paymentPreference, module);                                                                                                                
                        }
                    } else {
                        Debug.logError("Payment authorization failed (null result):  " + paymentPreference, module);                                                                                                                
                    }
                } else {
                    Debug.logError("Problems trying to re-authorize the payment (null result): " + paymentPreference, module);
                }
            } else {
                Debug.logError("Null OrderReadHelper cannot process", module);           
            }
        } else {
            Debug.logError("Result pass is null, no capture available", module);
        }
    }

    /**
     * Simple test processor; declines all orders < 100.00; approves all orders > 100.00
     */
    public static Map testProcessor(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Double processAmount = (Double) context.get("processAmount");

        if (processAmount != null && processAmount.doubleValue() >= 100.00)
            result.put("authResult", new Boolean(true));
        if (processAmount != null && processAmount.doubleValue() < 100.00)
            result.put("authResult", new Boolean(false));
        if (processAmount == null)
            result.put("authResult", null);

        long nowTime = new Date().getTime();

        result.put("processAmount", context.get("processAmount"));
        result.put("authCode", context.get("processResult"));
        result.put("authRefNum", new Long(nowTime).toString());
        result.put("authFlag", "X");
        result.put("authMessage", "This is a test processor; no payments were captured or authorized.");
        return result;
    }

    /**
     * Always approve processor.
     */
    public static Map alwaysApproveProcessor(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Double processAmount = (Double) context.get("processAmount");
        long nowTime = new Date().getTime();

        result.put("authResult", new Boolean(true));
        result.put("processAmount", context.get("processAmount"));
        result.put("authCode", context.get("processResult"));
        result.put("authRefNum", new Long(nowTime).toString());
        result.put("authFlag", "X");
        result.put("authMessage", "This is a test processor; no payments were captured or authorized.");
        return result;
    }

    /**
     * Always decline processor.
     */
    public static Map alwaysDeclineProcessor(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        Double processAmount = (Double) context.get("processAmount");
        long nowTime = new Date().getTime();

        result.put("authResult", new Boolean(false));
        result.put("processAmount", context.get("processAmount"));
        result.put("authCode", context.get("processResult"));
        result.put("authRefNum", new Long(nowTime).toString());
        result.put("authFlag", "X");
        result.put("authMessage", "This is a test processor; no payments were captured or authorized.");
        return result;
    }
    
    /**
     * Test capture service (returns true)
     */
     public static Map testCapture(DispatchContext dctx, Map context) {         
         Map result = new HashMap();
         long nowTime = new Date().getTime();
         
         result.put("captureResult", new Boolean(true));
         result.put("captureAmount", context.get("captureAmount"));
         result.put("captureRefNum", new Long(nowTime).toString());
         
         return result;
     }

}
