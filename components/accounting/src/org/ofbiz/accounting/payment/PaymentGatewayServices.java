/*
 * $Id: PaymentGatewayServices.java,v 1.18 2003/11/06 22:11:35 ajzeneski Exp $
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
package org.ofbiz.accounting.payment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * PaymentGatewayServices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.18 $
 * @since      2.0
 */
public class PaymentGatewayServices {

    public static final String module = PaymentGatewayServices.class.getName();
    public static final String AUTH_SERVICE_TYPE = "PRDS_PAY_AUTH";
    public static final String REAUTH_SERVICE_TYPE = "PRDS_PAY_REAUTH";
    public static final String RELEASE_SERVICE_TYPE = "PRDS_PAY_RELEASE";
    public static final String CAPTURE_SERVICE_TYPE = "PRDS_PAY_CAPTURE";
    public static final String REFUND_SERVICE_TYPE = "PRDS_PAY_REFUND";
    public static final String CREDIT_SERVICE_TYPE = "PRDS_PAY_CREDIT";

    /**
     * Processes payments through service calls to the defined processing service for the ProductStore/PaymentMethodType
     * @return APPROVED|FAILED|ERROR for complete processing of ALL payment methods.
     */
    public static Map authOrderPayments(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
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
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        String grandTotalString = formatter.format(orh.getOrderGrandTotal());
        Double grandTotal = null;
        try {
            grandTotal = new Double(formatter.parse(grandTotalString).doubleValue());
        } catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed grand total amount", module);
            return ServiceUtil.returnError("ERROR: Cannot parse grand total from formatted string; see logs");
        }

        double totalRemaining = grandTotal.doubleValue();

        // loop through and auth each payment
        List finished = new ArrayList();
        List hadError = new ArrayList();
        Iterator payments = paymentPrefs.iterator();
        while (payments.hasNext()) {
            GenericValue paymentPref = (GenericValue) payments.next();
            boolean reAuth = false;

            // if we are already authorized, then this is a re-auth request
            if (paymentPref.get("statusId") != null && "PAYMENT_AUTHORIZED".equals(paymentPref.getString("statusId"))) {
                reAuth = true;
            }

            // check the maxAmount for 0.00
            Double maxAmount = paymentPref.getDouble("maxAmount");
            if (maxAmount == null || maxAmount.doubleValue() > 0) {
                // call the authPayment method
                Map processorResult = authPayment(dispatcher, orh, paymentPref, totalRemaining, reAuth);

                // handle the response
                if (processorResult != null) {
                    // not null result means either an approval or decline; null would mean error
                    GenericValue paymentSettings = (GenericValue) processorResult.get("paymentSettings");
                    Double thisAmount = (Double) processorResult.get("processAmount");

                    // process the auth results
                    boolean processResult = false;
                    try {
                        processResult = processResult(dctx, processorResult, userLogin, paymentPref, paymentSettings);
                        if (processResult) {
                            totalRemaining -= thisAmount.doubleValue();
                            finished.add(processorResult);
                        }
                    } catch (GeneralException e) {
                        Debug.logError(e, "Trouble processing the result; processorResult: " + processorResult, module);
                        hadError.add(paymentPref);
                        ServiceUtil.returnError("Trouble processing the auth results");
                    }
                } else {
                    // error with payment processor; will try later
                    hadError.add(paymentPref);
                    continue;
                }
            } else {
                Debug.logInfo("Invalid OrderPaymentPreference; maxAmount is 0", module);
                paymentPref.set("statusId", "PAYMENT_CANCELLED");
                try {
                    paymentPref.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Problem setting OrderPaymentPreference status to CANCELLED", module);
                }
                finished.add(null);
            }
        }

        Debug.logInfo("Finished with auth(s) checking results", module);

        if (hadError.size() > 0) {
            Debug.logError("Error(s) (" + hadError.size() + ") during auth; returning ERROR", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "ERROR");
            return result;

        } else if (finished.size() == paymentPrefs.size()) {
            Debug.logInfo("All auth(s) passed total remaining : " + totalRemaining, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "APPROVED");
            return result;
        } else {
            Debug.logInfo("Only (" + finished.size() + ") passed auth; returning FAILED", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "FAILED");
            return result;
        }
    }

    private static Map authPayment(LocalDispatcher dispatcher, OrderReadHelper orh, GenericValue paymentPref, double totalRemaining, boolean reauth) {
        String paymentConfig = null;
        String serviceName = null;

        // get the payment settings i.e. serviceName and config properties file name
        String serviceType = AUTH_SERVICE_TYPE;
        if (reauth) {
            serviceType = REAUTH_SERVICE_TYPE;
        }

        GenericValue paymentSettings = getPaymentSettings(orh.getOrderHeader(), paymentPref, serviceType, false);
        if (paymentSettings != null) {
            serviceName = paymentSettings.getString("paymentService");
            paymentConfig = paymentSettings.getString("paymentPropertiesPath");
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
            processContext = makeAuthContext(orh, paymentPref, paymentConfig, totalRemaining);
        } catch (GeneralException e) {
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
            Debug.logError(gse, "Problems invoking payment processor! Will retry later." + "(" + orh.getOrderId() + ")", module);
            return null;
        }

        if (processorResult != null) {
            // check for errors from the processor implementation
            String resultResponseCode = (String) processorResult.get(ModelService.RESPONSE_MESSAGE);
            if (resultResponseCode != null && resultResponseCode.equals(ModelService.RESPOND_ERROR)) {
                Debug.logError("Processor failed; will retry later : " + processorResult.get(ModelService.ERROR_MESSAGE), module);
                return null;
            }

            // pass the payTo partyId to the result processor; we just add it to the result context.
            String payToPartyId = getPayToPartyId(orh.getOrderHeader());
            processorResult.put("payToPartyId", payToPartyId);

            // add paymentSettings to result; for use by later processors
            processorResult.put("paymentSettings", paymentSettings);
        }

        return processorResult;
    }

    private static GenericValue getPaymentSettings(GenericValue orderHeader, GenericValue paymentPreference, String paymentServiceType, boolean anyServiceType) {
        GenericDelegator delegator = orderHeader.getDelegator();
        GenericValue paymentSettings = null;
        GenericValue paymentMethod = null;
        try {
            paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting PaymentMethod from OrderPaymentPreference", module);
        }
        if (paymentMethod != null) {
            String productStoreId = orderHeader.getString("productStoreId");
            String paymentMethodTypeId = paymentMethod.getString("paymentMethodTypeId");
            if (productStoreId != null && paymentMethodTypeId != null) {
                paymentSettings = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, paymentMethodTypeId, paymentServiceType, anyServiceType);
            }
        }
        return paymentSettings;
    }

    private static String getPayToPartyId(GenericValue orderHeader) {
        String payToPartyId = "Company"; // default value
        GenericValue productStore = null;
        try {
            productStore = orderHeader.getRelatedOne("ProductStore");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get ProductStore from OrderHeader", module);
            return null;
        }
        if (productStore != null && productStore.get("payToPartyId") != null) {
            payToPartyId = productStore.getString("payToPartyId");
        }
        return payToPartyId;
    }

    private static Map makeAuthContext(OrderReadHelper orh, GenericValue paymentPreference, String paymentConfig, double totalRemaining) throws GeneralException {
        Map processContext = new HashMap();

        processContext.put("orderId", orh.getOrderId());
        processContext.put("orderItems", orh.getOrderItems());
        processContext.put("shippingAddress", orh.getShippingAddress());
        processContext.put("paymentConfig", paymentConfig);
        processContext.put("currency", orh.getCurrency());
        processContext.put("orderPaymentPreference", paymentPreference);

        // get the billing information
        getBillingInformation(orh, paymentPreference, processContext);

        // get the process amount
        double thisAmount = totalRemaining;
        if (paymentPreference.get("maxAmount") != null) {
            thisAmount = paymentPreference.getDouble("maxAmount").doubleValue();
        }

        // don't authorized more then what is required
        if (thisAmount > totalRemaining) {
            thisAmount = totalRemaining;
        }

        // format the decimal
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        String amountString = formatter.format(thisAmount);
        Double processAmount = null;
        try {
            processAmount = new Double(formatter.parse(amountString).doubleValue());
        } catch (ParseException e) {
            Debug.logError(e, "Problems parsing string formatted double to Double", module);
            throw new GeneralException("ParseException in number format", e);
        }

        if (Debug.verboseOn())
            Debug.logVerbose("Charging amount: " + processAmount, module);
        processContext.put("processAmount", processAmount);

        return processContext;
    }

    private static String getBillingInformation(OrderReadHelper orh, GenericValue paymentPreference, Map toContext) throws GenericEntityException {
        // gather the payment related objects.
        GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
        if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
            // type credit card
            GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
            GenericValue billingAddress = creditCard.getRelatedOne("PostalAddress");
            toContext.put("creditCard", creditCard);
            toContext.put("billingAddress", billingAddress);
        } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("EFT_ACCOUNT")) {
            // type eft
            GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");
            GenericValue billingAddress = eftAccount.getRelatedOne("PostalAddress");
            toContext.put("eftAccount", eftAccount);
            toContext.put("billingAddress", billingAddress);
        } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("GIFT_CARD")) {
            // type gift card
            GenericValue giftCard = paymentMethod.getRelatedOne("GiftCard");
            toContext.put("giftCard", giftCard);
        } else {
            // add other payment types here; i.e. gift cards, etc.
            // unknown payment type; ignoring.
            Debug.logError("ERROR: Unsupported PaymentMethodType passed for authorization", module);
            return null;
        }

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

        toContext.put("contactPerson", contactPerson);
        toContext.put("contactEmail", contactEmail);

        return contactPerson.getString("partyId");
    }

    /**
     *
     * Releases authorizations through service calls to the defined processing service for the ProductStore/PaymentMethodType
     * @return COMPLETE|FAILED|ERROR for complete processing of ALL payments.
     */
    public static Map releaseOrderPayments(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String invoiceId = (String) context.get("invoiceId");
        Double captureAmount = (Double) context.get("captureAmount");

        Map result = new HashMap();

        // get the order header and payment preferences
        GenericValue orderHeader = null;
        List paymentPrefs = null;

        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            // get the payment prefs
            Map lookupMap = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_AUTHORIZED");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", lookupMap);
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
        String productStoreId = orh.getProductStoreId();
        String currency = orh.getCurrency();

        // iterate over the prefs and release each one
        List finished = new ArrayList();
        Iterator payments = paymentPrefs.iterator();
        while (payments.hasNext()) {
            GenericValue paymentPref = (GenericValue) payments.next();

            // look up the payment configuration settings
            String serviceName = null;
            String paymentConfig = null;

            // get the payment settings i.e. serviceName and config properties file name
            GenericValue paymentSettings = getPaymentSettings(orh.getOrderHeader(), paymentPref, RELEASE_SERVICE_TYPE, false);
            if (paymentSettings != null) {
                paymentConfig = paymentSettings.getString("paymentPropertiesPath");
                serviceName = paymentSettings.getString("paymentService");
                if (serviceName == null) {
                    Debug.logError("Service name is null for payment setting; cannot process for : " + paymentPref, module);
                }
            } else {
                Debug.logError("Invalid payment settings entity, no payment settings found for : " + paymentPref, module);
            }

            if (paymentConfig == null || paymentConfig.length() == 0) {
                paymentConfig = "payment.properties";
            }

            Map releaseContext = new HashMap();
            releaseContext.put("orderPaymentPreference", paymentPref);
            releaseContext.put("currency", currency);
            releaseContext.put("paymentConfig", paymentConfig);

            // run the defined service
            Map releaseResult = null;
            try {
                releaseResult = dispatcher.runSync(serviceName, releaseContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem releasing payment", module);
            }

            // get the release result code
            Boolean releaseResponse = (Boolean) releaseResult.get("releaseResult");

            // create the PaymentGatewayResponse
            String responseId = delegator.getNextSeqId("PaymentGatewayResponse").toString();
            GenericValue response = delegator.makeValue("PaymentGatewayResponse", null);
            response.set("paymentGatewayResponseId", responseId);
            response.set("paymentServiceTypeEnumId", RELEASE_SERVICE_TYPE);
            response.set("orderPaymentPreferenceId", paymentPref.get("orderPaymentPreferenceId"));
            response.set("paymentMethodTypeId", paymentPref.get("paymentMethodTypeId"));
            response.set("paymentMethodTypeId", paymentPref.get("paymentMethodId"));

            // set the auth info
            response.set("referenceNum", releaseResult.get("releaseRefNum"));
            response.set("gatewayCode", releaseResult.get("releaseCode"));
            response.set("gatewayFlag", releaseResult.get("releaseFlag"));
            response.set("gatewayMessage", releaseResult.get("releaseMessage"));
            response.set("transactionDate", UtilDateTime.nowTimestamp());

            // store the gateway response
            try {
                delegator.create(response);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem storing PaymentGatewayResponse entity; authorization was released! : " + response, module);
            }

            if (releaseResponse != null && releaseResponse.booleanValue()) {
                paymentPref.set("statusId", "PAYMENT_CANCELLED");
                try {
                    paymentPref.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem storing updated payment preference; authorization was released!", module);
                }
                finished.add(paymentPref);
            } else {
                Debug.logError("Release failed for pref : " + paymentPref, module);
            }
        }

        result = ServiceUtil.returnSuccess();
        if (finished.size() == paymentPrefs.size()) {
            result.put("processResult", "COMPLETE");
        } else {
            result.put("processResult", "FAILED");
        }

        return result;
    }

    /**
     * Captures payments through service calls to the defined processing service for the ProductStore/PaymentMethodType
     * @return COMPLETE|FAILED|ERROR for complete processing of ALL payment methods.
     */
    public static Map capturePaymentsByInvoice(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String invoiceId = (String) context.get("invoiceId");

        // lookup the invoice
        GenericValue invoice = null;
        try {
            invoice = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble looking up Invoice #" + invoiceId, module);
            return ServiceUtil.returnError("Trouble looking up Invoice #" + invoiceId);
        }

        if (invoice == null) {
            Debug.logError("Could not locate invoice #" + invoiceId, module);
            return ServiceUtil.returnError("Could not locate invoice #" + invoiceId);
        }

        // get the OrderItemBilling records for this invoice
        List orderItemBillings = null;
        try {
            orderItemBillings = invoice.getRelated("OrderItemBilling");
        } catch (GenericEntityException e) {
            Debug.logError("Trouble getting OrderItemBilling(s) from Invoice #" + invoiceId, module);
            return ServiceUtil.returnError("Trouble getting OrderItemBilling(s) from Invoice #" + invoiceId);
        }

        // check for an associated billing account
        String billingAccountId = invoice.getString("billingAccountId");

        // make sure they are all for the same order
        String testOrderId = null;
        boolean allSameOrder = true;
        if (orderItemBillings != null) {
            Iterator oii = orderItemBillings.iterator();
            while (oii.hasNext()) {
                GenericValue oib = (GenericValue) oii.next();
                String orderId = oib.getString("orderId");
                if (testOrderId == null) {
                    testOrderId = orderId;
                } else {
                    if (!orderId.equals(testOrderId)) {
                        allSameOrder = false;
                        break;
                    }
                }
            }
        }

        if (testOrderId == null || !allSameOrder) {
            Debug.logWarning("Attempt to settle Invoice #" + invoiceId + " which contained none/multiple orders", module);
            return ServiceUtil.returnSuccess();
        }

        // get the invoice amount (amount to bill)
        double invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice);
        Debug.log("Invoice total: " + invoiceTotal, module);

        // now capture the order
        Map serviceContext = UtilMisc.toMap("userLogin", userLogin, "orderId", testOrderId, "invoiceId", invoiceId, "captureAmount", new Double(invoiceTotal));
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            serviceContext.put("billingAccountId", billingAccountId);
        }
        try {
            return dispatcher.runSync("captureOrderPayments", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Trouble running captureOrderPayments service", module);
            return ServiceUtil.returnError("Trouble running captureOrderPayments service");
        }
    }

    /**
     * Captures payments through service calls to the defined processing service for the ProductStore/PaymentMethodType
     * @return COMPLETE|FAILED|ERROR for complete processing of ALL payment methods.
     */
    public static Map captureOrderPayments(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String invoiceId = (String) context.get("invoiceId");
        String billingAccountId = (String) context.get("billingAccountId");
        Double captureAmount = (Double) context.get("captureAmount");

        Map result = new HashMap();

        // get the order header and payment preferences
        GenericValue orderHeader = null;
        List paymentPrefs = null;

        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            // get the payment prefs
            Map lookupMap = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_AUTHORIZED");
            List orderList = UtilMisc.toList("-authAmount");
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
        double orderTotal = orh.getOrderGrandTotal();
        double totalPayments = PaymentWorker.getPaymentsTotal(orh.getOrderPayments());
        double remainingTotal = orderTotal - totalPayments;
        Debug.log("Remaining Total: " + remainingTotal, module);

        // re-format the remaining total
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        String remainingTotalString = formatter.format(remainingTotal);
        try {
            Number remaining = formatter.parse(remainingTotalString);
            if (remaining != null) {
                remainingTotal = remaining.doubleValue();
            }
        } catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed remaining total", module);
            return ServiceUtil.returnError("ERROR: Cannot parse grand total from formatted string; see logs");
        }

        if (captureAmount == null) {
            captureAmount = new Double(remainingTotal);
        }
        Debug.log("Formatted Remaining total : " + remainingTotal, module);

        double amountToCapture = captureAmount.doubleValue();
        Debug.log("Expected Capture Amount : " + amountToCapture, module);

        // if we have a billing account get balance/limit and available
        GenericValue billingAccount = null;
        Double billingAccountBalance = null;
        Double billingAccountAvail = null;
        Map billingAccountInfo = null;
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            try {
                billingAccountInfo = dispatcher.runSync("calcBillingAccountBalance", UtilMisc.toMap("billingAccountId", billingAccountId));
            } catch (GenericServiceException e) {
                Debug.logError(e, "Unable to get billing account information for #" + billingAccountId, module);
            }
        }
        if (billingAccountInfo != null) {
            billingAccount = (GenericValue) billingAccountInfo.get("billingAccount");
            billingAccountBalance = (Double) billingAccountInfo.get("accountBalance");
        }
        if (billingAccount != null && billingAccountBalance != null) {
            Double accountLimit = billingAccount.getDouble("accountLimit");
            if (accountLimit == null) {
                accountLimit = new Double(0.00);
            }
            billingAccountAvail = new Double(accountLimit.doubleValue() - billingAccountBalance.doubleValue());
        }

        // iterate over the prefs and capture each one until we meet our total
        List finished = new ArrayList();
        Iterator payments = paymentPrefs.iterator();
        while (payments.hasNext()) {
            GenericValue paymentPref = (GenericValue) payments.next();
            GenericValue authTrans = getAuthTransaction(paymentPref);
            if (authTrans == null) {
                continue;
            }

            Double authAmount = authTrans.getDouble("amount");
            if (authAmount == null) authAmount = new Double(0.00);
            if (authAmount.doubleValue() == 0.00) {
                // nothing to capture
                Debug.log("Nothing to capture; authAmount = 0", module);
                continue;
            }
            Debug.log("Actual Auth amount : " + authAmount, module);

            // if the authAmount is more then the remaining total; just use remaining total
            if (authAmount.doubleValue() > remainingTotal) {
                authAmount = new Double(remainingTotal);
            }

            // if we have a billing account; total up auth + account available
            double amountToBillAccount = 0.00;
            if (billingAccountAvail != null) {
                amountToBillAccount = authAmount.doubleValue() + billingAccountAvail.doubleValue();
            }

            // the amount for *this* capture
            double amountThisCapture = 0.00;

            // determine how much for *this* capture
            if (authAmount.doubleValue() >= amountToCapture) {
                // if the auth amount is more then expected capture just capture what is expected
                amountThisCapture = amountToCapture;
            } else if (payments.hasNext()) {
                // if we have more payments to capture; just capture what was authorized
                amountThisCapture = authAmount.doubleValue();
            } else if (billingAccountAvail != null && amountToBillAccount >= amountToCapture) {
                // the provided billing account will cover the remaining; just capture what was autorized
                amountThisCapture = authAmount.doubleValue();
            } else {
                // we need to capture more then what was authorized; re-auth for the new amount
                // TODO: add what the billing account cannot support to the re-auth amount
                // TODO: add support for re-auth for additional funds
                // just in case; we will capture the authorized amount here; until this is implemented
                Debug.logError("The amount to capture was more then what was authorized; we only captured the authorized amount : " + paymentPref, module);
                amountThisCapture = authAmount.doubleValue();
            }

            Map captureResult = capturePayment(dispatcher, orh, paymentPref, amountThisCapture);
            if (captureResult != null) {
                GenericValue paymentSettings = (GenericValue) captureResult.get("paymentSettings");
                Double amountCaptured = (Double) captureResult.get("captureAmount");
                if (amountCaptured != null) amountToCapture -= amountCaptured.doubleValue();
                finished.add(captureResult);

                // add the invoiceId to the result for processing
                captureResult.put("invoiceId", invoiceId);

                Debug.log("Capture result : " + captureResult, module);

                // process the capture's results
                boolean processResult = false;
                try {
                    processResult = processResult(dctx, captureResult, userLogin, paymentPref, paymentSettings);
                } catch (GeneralException e) {
                    Debug.logError(e, "Trouble processing the result; captureResult: " + captureResult, module);
                    ServiceUtil.returnError("Trouble processing the capture results");
                }

                // create any splits which are needed
                if (authAmount.doubleValue() > amountThisCapture) {
                    // create a new payment preference and authorize it
                    Debug.log("Creating payment preference split", module);
                    double newAmount = authAmount.doubleValue() - amountThisCapture;
                    String newPrefId = delegator.getNextSeqId("OrderPaymentPreference").toString();
                    GenericValue newPref = delegator.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", newPrefId));
                    newPref.set("orderId", paymentPref.get("orderId"));
                    newPref.set("paymentMethodTypeId", paymentPref.get("paymentMethodTypeId"));
                    newPref.set("paymentMethodId", paymentPref.get("paymentMethodId"));
                    newPref.set("maxAmount", paymentPref.get("maxAmount"));
                    newPref.set("statusId", "PAYMENT_NOT_AUTH");
                    Debug.log("New preference : " + newPref, module);
                    try {
                        // create the new payment preference
                        delegator.create(newPref);

                        // authorize the new preference
                        Map processorResult = authPayment(dispatcher, orh, newPref, newAmount, false);
                        if (processorResult != null) {
                            GenericValue pSetting = (GenericValue) processorResult.get("paymentSettings");
                            Double thisAmount = (Double) processorResult.get("processAmount");

                            // process the auth results
                            boolean authResult = false;
                            try {
                                authResult = processResult(dctx, processorResult, userLogin, newPref, pSetting);
                                if (!authResult) {
                                    Debug.logError("Authorization failed : " + newPref + " : " + processorResult, module);
                                }
                            } catch (GeneralException e) {
                                Debug.logError(e, "Trouble processing the auth result : " + newPref + " : " + processorResult, module);
                            }
                        } else {
                            Debug.logError("Payment not authorized : " + newPref + " : " + processorResult, module);
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "ERROR: cannot create new payment preference : " + newPref, module);
                    }
                }
            } else {
                Debug.logError("Payment not captured", module);
                continue;
            }
        }

        if (amountToCapture > 0.00) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "FAILED");
            return result;
        } else {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put("processResult", "COMPLETE");
            return result;
        }
    }

    private static Map capturePayment(LocalDispatcher dispatcher, OrderReadHelper orh, GenericValue paymentPref, double amount) {
        // look up the payment configuration settings
        String serviceName = null;
        String paymentConfig = null;

        // get the payment settings i.e. serviceName and config properties file name
        GenericValue paymentSettings = getPaymentSettings(orh.getOrderHeader(), paymentPref, CAPTURE_SERVICE_TYPE, false);
        if (paymentSettings != null) {
            paymentConfig = paymentSettings.getString("paymentPropertiesPath");
            serviceName = paymentSettings.getString("paymentService");
            if (serviceName == null) {
                Debug.logError("Service name is null for payment setting; cannot process", module);
                return null;
            }
        } else {
            Debug.logError("Invalid payment settings entity, no payment settings found", module);
            return null;
        }

        if (paymentConfig == null || paymentConfig.length() == 0) {
            paymentConfig = "payment.properties";
        }

        // prepare the context for the capture service (must follow the ccCaptureInterface
        Map captureContext = new HashMap();
        captureContext.put("orderPaymentPreference", paymentPref);
        captureContext.put("paymentConfig", paymentConfig);
        captureContext.put("captureAmount", new Double(amount));
        captureContext.put("currency", orh.getCurrency());

        Debug.log("Capture : " + captureContext, module);

        // now invoke the capture service
        Map captureResult = null;
        try {
            captureResult = dispatcher.runSync(serviceName, captureContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Could not capture payment ... serviceName: " + serviceName + " ... context: " + captureContext, module);
            return null;
        }

        // pass the payTo partyId to the result processor; we just add it to the result context.
        String payToPartyId = getPayToPartyId(orh.getOrderHeader());
        captureResult.put("payToPartyId", payToPartyId);

        // add paymentSettings to result; for use by later processors
        captureResult.put("paymentSettings", paymentSettings);

        return captureResult;
    }

    private static boolean processResult(DispatchContext dctx, Map result, GenericValue userLogin, GenericValue paymentPreference, GenericValue paymentSettings) throws GeneralException {
        Boolean authResult = (Boolean) result.get("authResult");
        Boolean captureResult = (Boolean) result.get("captureResult");
        boolean resultPassed = false;
        boolean fromAuth = false;

        if (authResult != null) {
            processAuthResult(dctx, result, userLogin, paymentPreference, paymentSettings);
            resultPassed = authResult.booleanValue();
            fromAuth = true;
        }
        if (captureResult != null) {
            processCaptureResult(dctx, result, userLogin, paymentPreference, paymentSettings, fromAuth);
            if (!resultPassed)
                resultPassed = captureResult.booleanValue();
        }
        return resultPassed;
    }

    private static void processAuthResult(DispatchContext dctx, Map result, GenericValue userLogin, GenericValue paymentPreference, GenericValue paymentSettings) throws GeneralException {
        Boolean authResult = (Boolean) result.get("authResult");
        GenericDelegator delegator = paymentPreference.getDelegator();

        // type of auth this was can be determined by the previous status
        String authType = paymentPreference.getString("statusId").equals("PAYMENT_NOT_AUTH") ? AUTH_SERVICE_TYPE : REAUTH_SERVICE_TYPE;

        // create the PaymentGatewayResponse
        String responseId = delegator.getNextSeqId("PaymentGatewayResponse").toString();
        GenericValue response = delegator.makeValue("PaymentGatewayResponse", null);
        response.set("paymentGatewayResponseId", responseId);
        response.set("paymentServiceTypeEnumId", authType);
        response.set("orderPaymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"));
        response.set("paymentMethodTypeId", paymentPreference.get("paymentMethodTypeId"));
        response.set("paymentMethodId", paymentPreference.get("paymentMethodId"));

        // set the avs/fraud result
        response.set("gatewayAvsResult", result.get("avsCode"));
        response.set("gatewayScoreResult", result.get("scoreCode"));

        // set the auth info
        response.set("amount", result.get("processAmount"));
        response.set("referenceNum", result.get("authRefNum"));
        response.set("gatewayCode", result.get("authCode"));
        response.set("gatewayFlag", result.get("authFlag"));
        response.set("gatewayMessage", result.get("authMessage"));
        response.set("transactionDate", UtilDateTime.nowTimestamp());
        delegator.create(response);

        if (response.getDouble("amount").doubleValue() != ((Double) result.get("processAmount")).doubleValue()) {
            Debug.logWarning("The authorized amount does not match the max amount : Response - " + response + " : result - " + result, module);
        }

        // set the status of the OrderPaymentPreference
        if (result != null && authResult.booleanValue()) {
            paymentPreference.set("statusId", "PAYMENT_AUTHORIZED");
        } else if (result != null && !authResult.booleanValue()) {
            paymentPreference.set("statusId", "PAYMENT_DECLINED");
        } else {
            paymentPreference.set("statusId", "PAYMENT_ERROR");
        }
        paymentPreference.store();
    }

    private static void processCaptureResult(DispatchContext dctx, Map result, GenericValue userLogin, GenericValue paymentPreference, GenericValue paymentSettings) throws GeneralException {
        processCaptureResult(dctx, result, userLogin, paymentPreference, paymentSettings, false);
    }

    private static void processCaptureResult(DispatchContext dctx, Map result, GenericValue userLogin, GenericValue paymentPreference, GenericValue paymentSettings, boolean fromAuth) throws GeneralException {
        Boolean captureResult = (Boolean) result.get("captureResult");
        String invoiceId = (String) result.get("invoiceId");
        String payTo = (String) result.get("payToPartyId");
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Double amount = null;
        if (result.get("captureAmount") != null) {
            amount = (Double) result.get("captureAmount");
        } else if (result.get("processAmount") != null) {
            amount = (Double) result.get("processAmount");
        }

        if (amount == null) {
            throw new GeneralException("Unable to process null capture amount");
        }

        Debug.log("Invoice ID: " + invoiceId, module);

        if (payTo == null)
            payTo = "Company";

        String serviceType = fromAuth ? "AUTH" : CAPTURE_SERVICE_TYPE;
        if (serviceType.equals("AUTH")) {
            serviceType = paymentPreference.getString("statusId").equals("PAYMENT_NOT_AUTH") ? AUTH_SERVICE_TYPE : REAUTH_SERVICE_TYPE;
        }

        if (result != null && captureResult.booleanValue()) {
            // create the PaymentGatewayResponse record
            String responseId = delegator.getNextSeqId("PaymentGatewayResponse").toString();
            GenericValue response = delegator.makeValue("PaymentGatewayResponse", null);
            response.set("paymentGatewayResponseId", responseId);
            response.set("paymentServiceTypeEnumId", serviceType);
            response.set("orderPaymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"));
            response.set("paymentMethodTypeId", paymentPreference.get("paymentMethodTypeId"));
            response.set("paymentMethodId", paymentPreference.get("paymentMethodId"));
            if (result.get("authRefNum") != null) {
                response.set("subReference", result.get("authRefNum"));
            }

            // set the capture info
            response.set("amount", amount);
            response.set("referenceNum", result.get("captureRefNum"));
            response.set("gatewayCode", result.get("captureCode"));
            response.set("gatewayFlag", result.get("captureFlag"));
            response.set("gatewayMessage", result.get("captureMessage"));
            response.set("transactionDate", UtilDateTime.nowTimestamp());
            delegator.create(response);

            String orderId = paymentPreference.getString("orderId");
            GenericValue orderRole = EntityUtil.getFirst(delegator.findByAnd("OrderRole",
                        UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER")));

            Map paymentCtx = UtilMisc.toMap("paymentTypeId", "RECEIPT");
            paymentCtx.put("paymentMethodTypeId", paymentPreference.get("paymentMethodTypeId"));
            paymentCtx.put("paymentMethodId", paymentPreference.get("paymentMethodId"));
            paymentCtx.put("paymentGatewayResponseId", responseId);
            paymentCtx.put("partyIdTo", payTo);
            paymentCtx.put("partyIdFrom", orderRole.get("partyId"));
            paymentCtx.put("statusId", "PMNT_RECEIVED");
            paymentCtx.put("paymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"));
            paymentCtx.put("amount", amount);
            paymentCtx.put("userLogin", userLogin);
            paymentCtx.put("paymentRefNum", result.get("captureRefNum"));

            Map payRes = dispatcher.runSync("createPayment", paymentCtx);
            String paymentId = (String) payRes.get("paymentId");

            paymentPreference.set("statusId", "PAYMENT_SETTLED");
            paymentPreference.store();

            // create the PaymentApplication if invoiceId is available
            if (invoiceId != null) {
                Debug.log("Processing Invoice #" + invoiceId, module);
                Map paCtx = UtilMisc.toMap("paymentId", paymentId, "invoiceId", invoiceId);
                paCtx.put("amountApplied", result.get("captureAmount"));
                paCtx.put("userLogin", userLogin);
                Map paRes = dispatcher.runSync("createPaymentApplication", paCtx);
            }
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
                Map authPayRes = authPayment(dispatcher, orh, paymentPreference, amount.doubleValue(), true);
                if (authPayRes != null) {
                    Boolean authResp = (Boolean) result.get("authResult");
                    Boolean capResp = (Boolean) result.get("captureResult");
                    if (authResp != null) {
                        processAuthResult(dctx, authPayRes, userLogin, paymentPreference, paymentSettings);
                        if (authResp.booleanValue()) {
                            // first make sure we didn't already capture - probably not
                            if (capResp != null && capResp.booleanValue()) {
                                processCaptureResult(dctx, result, userLogin, paymentPreference, paymentSettings);
                            } else {
                                // lets try to capture the funds now
                                Map capPayRes = capturePayment(dispatcher, orh, paymentPreference, amount.doubleValue());
                                if (capPayRes != null) {
                                    Boolean capPayResp = (Boolean) result.get("captureResult");
                                    if (capPayResp != null && capPayResp.booleanValue()) {
                                        // it was successful
                                        processCaptureResult(dctx, result, userLogin, paymentPreference, paymentSettings);
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

    public static Map refundPayment(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        Double refundAmount = (Double) context.get("refundAmount");

        GenericValue orderHeader = null;
        try {
            orderHeader = paymentPref.getRelatedOne("OrderHeader");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderHeader from OrderPaymentPreference", module);
            return ServiceUtil.returnError("Problems getting OrderHeader from OrderPaymentPreference: " + e.getMessage());
        }

        OrderReadHelper orh = new OrderReadHelper(orderHeader);

        GenericValue paymentSettings = null;
        if (orderHeader != null) {
            paymentSettings = getPaymentSettings(orderHeader, paymentPref, REFUND_SERVICE_TYPE, false);
        }

        if (paymentSettings != null) {
            String paymentConfig = paymentSettings.getString("paymentPropertiesPath");
            String serviceName = paymentSettings.getString("paymentService");
            if (serviceName != null) {
                Map serviceContext = new HashMap();
                serviceContext.put("paymentConfig", paymentConfig);
                serviceContext.put("currency", orh.getCurrency());

                // get the creditCard/address/email
                String payToPartyId = null;
                try {
                    payToPartyId = getBillingInformation(orh, paymentPref, serviceContext);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems getting billing information", module);
                    return ServiceUtil.returnError("Problems getting billing information");
                }

                // format the price
                String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
                DecimalFormat formatter = new DecimalFormat(currencyFormat);
                String amountString = formatter.format(refundAmount);
                Double processAmount = null;
                try {
                    processAmount = new Double(formatter.parse(amountString).doubleValue());
                } catch (ParseException e) {
                    Debug.logError(e, "Problem parsing amount using DecimalFormat", module);
                    return ServiceUtil.returnError("Refund processor problems; see logs");
                }
                serviceContext.put("refundAmount", processAmount);

                // call the service
                Map refundResponse = null;
                try {
                    refundResponse = dispatcher.runSync(serviceName, serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem refunding payment through processor", module);
                    return ServiceUtil.returnError("Refund processor problems; see logs");
                }

                // get the pay-from party
                if (paymentConfig == null || paymentConfig.length() == 0) {
                    paymentConfig = "payment.properties";
                }
                String payFromPartyId = getPayToPartyId(orderHeader);

                // create the PaymentGatewayResponse
                String responseId = delegator.getNextSeqId("PaymentGatewayResponse").toString();
                GenericValue response = delegator.makeValue("PaymentGatewayResponse", null);
                response.set("paymentGatewayResponseId", responseId);
                response.set("paymentServiceTypeEnumId", REFUND_SERVICE_TYPE);
                response.set("orderPaymentPreferenceId", paymentPref.get("orderPaymentPreferenceId"));
                response.set("paymentMethodTypeId", paymentPref.get("paymentMethodTypeId"));
                response.set("paymentMethodTypeId", paymentPref.get("paymentMethodId"));

                // set the refund info
                response.set("amount", refundResponse.get("refundAmount"));
                response.set("referenceNum", refundResponse.get("refundRefNum"));
                response.set("gatewayCode", refundResponse.get("refundCode"));
                response.set("gatewayFlag", refundResponse.get("refundFlag"));
                response.set("gatewayMessage", refundResponse.get("refundMessage"));
                response.set("transactionDate", UtilDateTime.nowTimestamp());

                // store the gateway response
                try {
                    delegator.create(response);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem creating PaymentGatewayResponse entity : " + response, module);
                }

                // handle the (reverse) payment
                Boolean refundResult = (Boolean) refundResponse.get("refundResult");
                if (refundResult != null && refundResult.booleanValue()) {
                    // create a payment record
                    Map payment = new HashMap();
                    payment.put("paymentTypeId", "DISBURSEMENT");
                    payment.put("paymentMethodTypeId", paymentPref.get("paymentMethodTypeId"));
                    payment.put("paymentMethodId", paymentPref.get("paymentMethodId"));
                    payment.put("paymentGatewayResponseId", responseId);
                    payment.put("partyIdFrom", payFromPartyId);
                    payment.put("partyIdTo", payToPartyId);
                    payment.put("userLogin", userLogin);
                    payment.put("statusId", "PMNT_SENT");
                    payment.put("paymentRefNum", refundResponse.get("refundRefNum"));
                    payment.put("amount", refundResponse.get("refundAmount"));
                    payment.put("comments", "Refund : " + refundResponse.get("refundMessage"));
                    String paymentId = null;
                    try {
                        Map payRes = dispatcher.runSync("createPayment", payment);
                        paymentId = (String) payRes.get("paymentId");
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Problem creating Payment", module);
                        return ServiceUtil.returnError("Problem creating Payment");
                    }

                    // return the paymentId
                    result.put("paymentId", paymentId);
                } else {
                    return ServiceUtil.returnError("The refund failed");
                }

                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                return result;
            } else {
                return ServiceUtil.returnError("No refund service defined");
            }
        } else {
            return ServiceUtil.returnError("No payment settings found");
        }
    }

    public static Map retryFailedAuths(DispatchContext dctx, Map context) {
        return ServiceUtil.returnSuccess();
    }

    public static GenericValue getAuthTransaction(GenericValue orderPaymentPreference) {
        GenericValue authTrans = null;
        try {
            List order = UtilMisc.toList("transactionDate");
            List transactions = orderPaymentPreference.getRelated("PaymentGatewayResponse", null, order);

            List exprs = UtilMisc.toList(new EntityExpr("paymentServiceTypeEnumId", EntityOperator.EQUALS, "PRDS_PAY_AUTH"),
                    new EntityExpr("paymentServiceTypeEnumId", EntityOperator.EQUALS, "PRDS_PAY_REAUTH"));

            List authTransactions = EntityUtil.filterByOr(transactions, exprs);
            authTrans = EntityUtil.getFirst(authTransactions);
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Problem getting authorization information from PaymentGatewayResponse", module);
        }
        return authTrans;
    }

    // ****************************************************
    // Test Services
    // ****************************************************

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
        Debug.logInfo("Test Processor Approving Credit Card", module);

        result.put("authResult", new Boolean(true));
        result.put("processAmount", context.get("processAmount"));
        result.put("authRefNum", new Long(nowTime).toString());
        result.put("authCode", "100");
        result.put("authFlag", "A");
        result.put("authMessage", "This is a test processor; no payments were captured or authorized.");
        return result;
    }

    public static Map alwaysApproveWithCapture(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        long nowTime = new Date().getTime();
        String refNum = new Long(nowTime).toString();
        Debug.logInfo("Test Processor Approving Credit Card with Capture", module);

        result.put("authResult", new Boolean(true));
        result.put("captureResult", new Boolean(true));
        result.put("processAmount", context.get("processAmount"));
        result.put("authRefNum", refNum);
        result.put("captureRefNum", refNum);
        result.put("authCode", "100");
        result.put("captureCode", "200");
        result.put("authFlag", "A");
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
        Debug.logInfo("Test Processor Declining Credit Card", module);

        result.put("authResult", new Boolean(false));
        result.put("processAmount", context.get("processAmount"));
        result.put("authRefNum", new Long(nowTime).toString());
        result.put("authFlag", "D");
        result.put("authMessage", "This is a test processor; no payments were captured or authorized");
        return result;
    }

    /**
     * Always fail (error) processor
     */
    public static Map alwaysFailProcessor(DispatchContext dctx, Map context) {
        return ServiceUtil.returnError("Unable to communicate with bla");
    }

    public static Map testRelease(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        long nowTime = new Date().getTime();

        result.put("releaseResult", new Boolean(true));
        result.put("releaseRefNum", new Long(nowTime).toString());
        result.put("releaseFlag", "U");
        result.put("releaseMessage", "This is a test release; no authorizations exist");
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
         result.put("captureFlag", "C");
         result.put("captureMessage", "This is a test capture; no money was transferred");
         return result;
     }

     /**
      * Test refund service (returns true)
      */
     public static Map testRefund(DispatchContext dctx, Map context) {
         Map result = new HashMap();
         long nowTime = new Date().getTime();

         result.put("refundResult", new Boolean(true));
         result.put("refundAmount", context.get("refundAmount"));
         result.put("refundRefNum", new Long(nowTime).toString());
         result.put("refundFlag", "R");
         result.put("refundMessage", "This is a test refund; no money was transferred");
         return result;
     }

}
