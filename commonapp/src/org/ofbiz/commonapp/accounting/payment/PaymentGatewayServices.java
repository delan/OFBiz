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

import java.text.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.order.order.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;

/**
 * PaymentGatewayServices
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class PaymentGatewayServices {

    public static final String module = PaymentGatewayServices.class.getName();

    /**
     * Processes payments through service calls to 'ccProcessor' and 'eftProcessor'.
     * @returns APPROVED|FAILED|ERROR for complete processing of ALL payment methods.
     */
    public static Map processPayments(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String webSiteId = (String) context.get("webSiteId");
        Map result = new HashMap();

        // get the order header and payment preferences
        GenericValue orderHeader = null;
        List paymentPrefs = null;

        try {                       
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            
            Map lookupMap = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_AUTH");
            List orderList = UtilMisc.toList("maxAmount");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", lookupMap, orderList);
        } catch (GenericEntityException gee) {
            gee.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + gee.getMessage() + ").");
            return result;
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError("Could not find OrderHeader with orderId: " + orderId + "; not processing payments.");
        }
                     
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        double amountToBill = orh.getOrderGrandTotal();

        if (Debug.verboseOn())
            Debug.logVerbose("Amount to charge is: " + amountToBill, module);

        List responseMessages = new ArrayList();
        Iterator payments = paymentPrefs.iterator();

        while (payments.hasNext()) {
            GenericValue paymentPref = (GenericValue) payments.next();
            GenericValue paymentMethod = null;
            GenericValue creditCard = null;
            GenericValue eftAccount = null;
            GenericValue billingAddress = null;
            Double processAmount = null;            

            // gather the payment related objects.
            try {
                paymentMethod = paymentPref.getRelatedOne("PaymentMethod");
                if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
                    // type credit card
                    creditCard = paymentMethod.getRelatedOne("CreditCard");
                    billingAddress = creditCard.getRelatedOne("PostalAddress");
                } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("EFT_ACCOUNT")) {
                    // type eft
                    eftAccount = paymentMethod.getRelatedOne("EFT_ACCOUNT");
                    billingAddress.getRelatedOne("PostalAddress");
                } else {
                    // add other payment types here; i.e. gift cards, etc.
                    // unknown payment type; ignoring.
                    continue;
                }
            } catch (GenericEntityException gee) {
                gee.printStackTrace();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order payment information (" + gee.getMessage() + ").");
                return result;
            }

            // get the process amount.
            double thisAmount = amountToBill;

            if (paymentPref.get("maxAmount") != null)
                thisAmount = paymentPref.getDouble("maxAmount").doubleValue();
            NumberFormat nf = NumberFormat.getCurrencyInstance();

            processAmount = new Double(thisAmount);
            if (Debug.verboseOn())
                Debug.logVerbose("Charging amount: " + processAmount, module);

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

            GenericValue paymentSettings = PaymentWorker.getPaymentSetting(delegator, webSiteId, paymentMethod.getString("paymentMethodTypeId"));
            String serviceName = paymentSettings != null && paymentSettings.get("paymentService") != null ? paymentSettings.getString("paymentService") : null;
            String configUrl = paymentSettings != null && paymentSettings.get("paymentConfiguration") != null ? paymentSettings.getString("paymentConfiguration") : null;
            Map processContext = new HashMap();
            
            if (serviceName == null) {
                Debug.logError("Invalid payment processor set for [" + paymentMethod.getString("paymentMethodTypeId") + "] on website [" + webSiteId + "]", module);
                continue;
            }

            processContext.put("orderId", orderId);
            processContext.put("orderItems", orh.getOrderItems());
            processContext.put("configUrl", configUrl);
            processContext.put("processAmount", processAmount);
            processContext.put("contactPerson", contactPerson);
            processContext.put("contactEmail", contactEmail);
            processContext.put("billingAddress", billingAddress);
            processContext.put("shippingAddress", orh.getShippingAddress());
            processContext.put("currency", context.get("currency"));
            
            // use pre-defined names for the services; just override the service in the definition file.
            if (creditCard != null) {
                processContext.put("creditCard", creditCard);               
            } else if (eftAccount != null) {
                processContext.put("eftAccount", eftAccount);                
            } // Add additional processed payment types here.

            // invoke the processor.
            Map processorResult = null;

            try {
                LocalDispatcher dispatcher = dctx.getDispatcher();
                processorResult = dispatcher.runSync(serviceName, processContext);
            } catch (GenericServiceException gse) {
                Debug.logError(gse, "Problems invoking payment processor!" + "(" + orderId + ")", module);
                Debug.logError("Error occured on: " + serviceName + " => " + processContext, module);
                continue;
            }
            
            // add the response message to the list
            if (processorResult != null && processorResult.containsKey(ModelService.RESPONSE_MESSAGE))
                responseMessages.add(processorResult.get(ModelService.RESPONSE_MESSAGE));
            else if (processorResult != null)
                responseMessages.add(ModelService.RESPOND_SUCCESS);

            try {
                // pass the payTo partyId to the result processor; we just add it to the result context.
                processorResult.put("payToPartyId", context.get("payToPartyId"));
                if (processResult(dctx, processorResult, paymentPref))
                    amountToBill -= thisAmount;
            } catch (GeneralException ge) {
                Debug.logError(ge, "Problem processing the result: " + processorResult, module);
                continue;
            }
        }

        // if all attempts failed then there is a processor problem.
        boolean somePassed = false;
        Iterator messageIterator = responseMessages.iterator();
        while (!somePassed && messageIterator.hasNext()) {
            String message = (String) messageIterator.next();
            if (!message.equals(ModelService.RESPOND_ERROR))
                somePassed = true;
        }
        if (!somePassed) {
            Debug.logWarning("All payment attempts faild due to a processor error.", module);
            return ServiceUtil.returnError("All payment attempts failed due to a processor error.");
        } else {        
            // we can determine if all was good if amountToBill is now zero.
            if (amountToBill > 0) {
                Debug.logError("Problem! Could not authorize funds for entire amount to bill. If multiple payment methods were used a partial payment may have been authorized. (" + orderId + ")", module);                
                result.put("processResult", "FAILED");
            }
    
            if (amountToBill == 0) {
                if (Debug.verboseOn()) Debug.logVerbose("All payment methods were processed successfully. (" + orderId + ")", module);
                result.put("processResult", "APPROVED");
            }
    
            if (amountToBill < 0) {
                Debug.logError("Something really weird happened. We processed more then expected! (" + orderId + ")", module);
                result.put("processResult", "ERROR");
            }
            result.put("orderId", orderId);
        }
        return result;
    }

    private static boolean processResult(DispatchContext dctx, Map result, GenericValue paymentPreference) throws GeneralException {
        Boolean authResult = (Boolean) result.get("authResult");
        Boolean captureResult = (Boolean) result.get("captureResult");
        boolean resultPassed = false;

        if (authResult != null) {
            processAuthResult(dctx, result, paymentPreference);
            resultPassed = authResult.booleanValue();
        }
        if (captureResult != null) {
            processCaptureResult(dctx, result, paymentPreference);
            if (!resultPassed)
                resultPassed = captureResult.booleanValue();
        }
        return resultPassed;
    }

    private static void processAuthResult(DispatchContext dctx, Map result, GenericValue paymentPreference) throws GeneralException {
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
        paymentPreference.set("authRefNum", result.get("authRefNum"));
        paymentPreference.set("authFlag", result.get("authFlag"));
        paymentPreference.set("authMessage", result.get("authMessage"));
        paymentPreference.set("authDate", UtilDateTime.nowTimestamp());
        paymentPreference.store();
    }

    private static void processCaptureResult(DispatchContext dctx, Map result, GenericValue paymentPreference) throws GeneralException {
        Boolean captureResult = (Boolean) result.get("captureResult");
        String payTo = (String) result.get("payToPartyId");

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
            payment.set("paymentPreference", paymentPreference.get("orderPaymentPreferenceId"));
            payment.set("amount", result.get("processAmount"));
            payment.set("paymentRefNum", result.get("captureRefNum"));
            payment.set("effectiveDate", UtilDateTime.nowTimestamp());
            delegator.create(payment);

            paymentPreference.set("authRefNum", result.get("authRefNum"));
            paymentPreference.set("statusId", "PAYMENT_SETTLED");
            paymentPreference.store();

        } else if (result != null && !captureResult.booleanValue()) {// declined
            // TODO re-auth the card and capture.
        } else {// error
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

}
