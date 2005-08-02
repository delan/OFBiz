/*
 * $Id$
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.accounting.payment;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.3
 */
public class GiftCertificateServices {

    public static final String module = GiftCertificateServices.class.getName();
    public static final int CARD_NUMBER_LENGTH = 14;
    public static final int PIN_NUMBER_LENGTH = 6;

    // Base Gift Certificate Services
    public static Map createGiftCertificate(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        Double initialAmount = (Double) context.get("initialAmount");

        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId)) {
            partyId = "_NA_";
        }
        String currencyUom = (String) context.get("currency");
        if (UtilValidate.isEmpty(currencyUom)) {
            currencyUom = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        String cardNumber = null;
        String pinNumber = null;
        String refNum = null;
        try {
            final String accountName = "Gift Certificate Account";
            final String accountType = "GIFTCERT_ACCOUNT";
            final String deposit = "DEPOSIT";

            cardNumber = generateNumber(delegator, CARD_NUMBER_LENGTH, true);
            pinNumber = generateNumber(delegator, PIN_NUMBER_LENGTH, false);

            // create the FinAccount
            Map acctCtx = UtilMisc.toMap("finAccountId", cardNumber);
            acctCtx.put("finAccountTypeId", accountType);
            acctCtx.put("finAccountName", accountName);
            acctCtx.put("finAccountCode", pinNumber);
            acctCtx.put("userLogin", userLogin);
            Map acctResult = dispatcher.runSync("createFinAccount", acctCtx);
            if (ServiceUtil.isError(acctResult)) {
                String error = ServiceUtil.getErrorMessage(acctResult);
                return ServiceUtil.returnError(error);
            }

            // create the initial (deposit) transaction
            refNum = GiftCertificateServices.createTransaction(delegator, dispatcher, userLogin, initialAmount,
                    productStoreId, partyId, currencyUom, deposit, cardNumber);

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to create gift certificate number.");
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to create gift certificate.");
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("cardNumber", cardNumber);
        result.put("pinNumber", pinNumber);
        result.put("initialAmount", initialAmount);
        result.put("processResult", Boolean.TRUE);
        result.put("responseCode", "1");
        result.put("referenceNum", refNum);
        Debug.log("Create GC Result - " + result, module);
        return result;
    }

    public static Map addFundsToGiftCertificate(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        final String deposit = "DEPOSIT";

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String cardNumber = (String) context.get("cardNumber");
        String pinNumber = (String) context.get("pinNumber");
        Double amount = (Double) context.get("amount");

        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId)) {
            partyId = "_NA_";
        }
        String currencyUom = (String) context.get("currency");
        if (UtilValidate.isEmpty(currencyUom)) {
            currencyUom = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        // validate the pin
        if (!validatePin(delegator, cardNumber, pinNumber)) {
            return ServiceUtil.returnError("PIN number is not valid!");
        }

        // get the previous balance
        double previousBalance = 0.00;
        try {
            previousBalance = GiftCertificateServices.getBalance(delegator, cardNumber);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // create the transaction
        double balance = 0.00;
        String refNum = null;
        try {
            refNum = GiftCertificateServices.createTransaction(delegator, dispatcher, userLogin, amount,
                    productStoreId, partyId, currencyUom, deposit, cardNumber);
            balance = GiftCertificateServices.getBalance(delegator, cardNumber);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("previousBalance", new Double(previousBalance));
        result.put("balance", new Double(balance));
        result.put("amount", amount);
        result.put("processResult", Boolean.TRUE);
        result.put("responseCode", "1");
        result.put("referenceNum", refNum);
        Debug.log("Add Funds GC Result - " + result, module);
        return result;
    }

    public static Map redeemGiftCertificate(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        final String withdrawl = "WITHDRAWAL";

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String cardNumber = (String) context.get("cardNumber");
        String pinNumber = (String) context.get("pinNumber");
        Double amount = (Double) context.get("amount");

        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId)) {
            partyId = "_NA_";
        }
        String currencyUom = (String) context.get("currency");
        if (UtilValidate.isEmpty(currencyUom)) {
            currencyUom = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        // validate the amount
        if (amount.doubleValue() < -1) {
            return ServiceUtil.returnError("Amount should be a positive number.");
        }

        // validate the pin
        if (!validatePin(delegator, cardNumber, pinNumber)) {
            return ServiceUtil.returnError("PIN number is not valid!");
        }
        Debug.log("Attempting to redeem GC for " + amount, module);

        // create the transaction
        double previousBalance = 0.00;
        try {
            previousBalance = GiftCertificateServices.getBalance(delegator, cardNumber);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        double balance = 0.00;
        String refNum = null;
        Boolean procResult;
        if (previousBalance >= amount.doubleValue()) {
            try {
                refNum = GiftCertificateServices.createTransaction(delegator, dispatcher, userLogin, amount,
                        productStoreId, partyId, currencyUom, withdrawl, cardNumber);
                balance = GiftCertificateServices.getBalance(delegator, cardNumber);
                procResult = Boolean.TRUE;
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        } else {
            procResult = Boolean.FALSE;
            balance = previousBalance;
            refNum = "N/A";
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("previousBalance", new Double(previousBalance));
        result.put("balance", new Double(balance));
        result.put("amount", amount);
        result.put("processResult", procResult);
        result.put("responseCode", "2");
        result.put("referenceNum", refNum);
        Debug.log("Redeem GC Result - " + result, module);
        return result;
    }

    public static Map checkGiftCertificateBalance(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String cardNumber = (String) context.get("cardNumber");
        String pinNumber = (String) context.get("pinNumber");

        // validate the pin
        if (!validatePin(delegator, cardNumber, pinNumber)) {
            return ServiceUtil.returnError("PIN number is not valid!");
        }

        // get the balance
        double balance = 0.00;
        try {
            balance = GiftCertificateServices.getBalance(delegator, cardNumber);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("balance", new Double(balance));
        Debug.log("GC Balance Result - " + result, module);
        return result;
    }

    // Fullfilment Services
    public static Map giftCertificateProcessor(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        GenericValue giftCard = (GenericValue) context.get("giftCard");
        GenericValue party = (GenericValue) context.get("billToParty");
        String currency = (String) context.get("currency");
        String orderId = (String) context.get("orderId");
        Double amount = (Double) context.get("processAmount");

        // make sure we have a currency
        if (currency == null) {
            currency = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        // obtain the order information
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);

        Map redeemCtx = new HashMap();
        redeemCtx.put("userLogin", userLogin);
        redeemCtx.put("productStoreId", orh.getProductStoreId());
        //redeemCtx.put("paymentConfig", paymentConfig);
        redeemCtx.put("cardNumber", giftCard.get("cardNumber"));
        redeemCtx.put("pinNumber", giftCard.get("pinNumber"));
        redeemCtx.put("currency", currency);
        //redeemCtx.put("orderId", orderId);
        redeemCtx.put("partyId", party.get("partyId"));
        redeemCtx.put("amount", amount);

        // invoke the redeem service
        Map redeemResult = null;
        try {
            redeemResult = dispatcher.runSync("redeemGiftCertificate", redeemCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the redeem service", module);
            return ServiceUtil.returnError("Redeem service failed");
        }

        Map result = ServiceUtil.returnSuccess();
        if (redeemResult != null) {
            Boolean processResult = (Boolean) redeemResult.get("processResult");
            result.put("processAmount", amount);
            result.put("authFlag", redeemResult.get("responseCode"));
            result.put("authResult", processResult);
            result.put("captureResult", processResult);
            result.put("authCode", "A");
            result.put("captureCode", "C");
            result.put("authRefNum", redeemResult.get("referenceNum"));
            result.put("captureRefNum", redeemResult.get("referenceNum"));
        }

        return result;
    }

    public static Map giftCertificateRefund(DispatchContext dctx, Map context) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        String currency = (String) context.get("currency");
        Double amount = (Double) context.get("refundAmount");
        return giftCertificateRestore(dctx, userLogin, paymentPref, amount, currency, "refund");
    }

    public static Map giftCertificateRelease(DispatchContext dctx, Map context) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue paymentPref = (GenericValue) context.get("orderPaymentPreference");
        String currency = (String) context.get("currency");
        Double amount = (Double) context.get("releaseAmount");
        return giftCertificateRestore(dctx, userLogin, paymentPref, amount, currency, "release");
    }

    private static Map giftCertificateRestore(DispatchContext dctx, GenericValue userLogin, GenericValue paymentPref, Double amount, String currency, String resultPrefix) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();

        // get the orderId for tracking
        String orderId = paymentPref.getString("orderId");
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        String productStoreId = orh.getProductStoreId();

        // party ID for tracking
        GenericValue placingParty = orh.getPlacingParty();
        String partyId = null;
        if (placingParty != null) {
            partyId = placingParty.getString("partyId");
        }

        // get the GiftCard VO
        GenericValue giftCard = null;
        try {
            giftCard = paymentPref.getRelatedOne("GiftCard");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get GiftCard from OrderPaymentPreference", module);
            return ServiceUtil.returnError("Unable to locate GiftCard Information");
        }

        if (giftCard == null) {
            return ServiceUtil.returnError("Attempt to release GiftCard payment faild; not a valid GiftCard record");
        }

        // make sure we have a currency
        if (currency == null) {
            currency = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        Map refundCtx = new HashMap();
        refundCtx.put("productStoreId", productStoreId);
        refundCtx.put("currency", currency);
        refundCtx.put("partyId", partyId);
        //reloadCtx.put("orderId", orderId);
        refundCtx.put("cardNumber", giftCard.get("cardNumber"));
        refundCtx.put("pinNumber", giftCard.get("pinNumber"));
        refundCtx.put("amount", amount);
        refundCtx.put("userLogin", userLogin);

        Map restoreGcResult = null;
        try {
            restoreGcResult = dispatcher.runSync("addFundsToGiftCertificate", refundCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to call refund service!");
        }
        if (ServiceUtil.isError(restoreGcResult)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(restoreGcResult));
        }

        Map result = ServiceUtil.returnSuccess();
        if (restoreGcResult != null) {
            Boolean processResult = (Boolean) restoreGcResult.get("processResult");
            result.put(resultPrefix + "Amount", amount);
            result.put(resultPrefix + "Result", processResult);
            result.put(resultPrefix + "Code", "R");
            result.put(resultPrefix + "Flag", restoreGcResult.get("responseCode"));
            result.put(resultPrefix + "RefNum", restoreGcResult.get("referenceNum"));
        }

        return result;
    }

    public static Map giftCertificatePurchase(DispatchContext dctx, Map context) {
        // this service should always be called via FULFILLMENT_EXTASYNC
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue orderItem = (GenericValue) context.get("orderItem");
        Locale locale = (Locale) context.get("locale");

        // order ID for tracking
        String orderId = orderItem.getString("orderId");

        // the order header for store info
        GenericValue orderHeader = null;
        try {
            orderHeader = orderItem.getRelatedOne("OrderHeader");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get OrderHeader from OrderItem",module);
            return ServiceUtil.returnError("Unable to get OrderHeader from OrderItem");
        }

        // get the order read helper
        OrderReadHelper orh = new OrderReadHelper(orderHeader);

        // get the currency
        String currency = orh.getCurrency();

        // make sure we have a currency
        if (currency == null) {
            currency = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        // get the product store
        String productStoreId = null;
        if (orderHeader != null) {
            productStoreId = orh.getProductStoreId();
        }
        if (productStoreId == null) {
            return ServiceUtil.returnError("Unable to process gift card purchase; no productStoreId on OrderHeader : " + orderId);
        }

        // payment config
        GenericValue paymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "GIFT_CARD", null, true);
        String paymentConfig = null;
        if (paymentSetting != null) {
            paymentConfig = paymentSetting.getString("paymentPropertiesPath");
        }
        if (paymentConfig == null) {
            return ServiceUtil.returnError("Unable to get payment configuration file");
        }

        // party ID for tracking
        GenericValue placingParty = orh.getPlacingParty();
        String partyId = null;
        if (placingParty != null) {
            partyId = placingParty.getString("partyId");
        }

        // amount/quantity of the gift card(s)
        Double amount = orderItem.getDouble("unitPrice");
        Double quantity = orderItem.getDouble("quantity");

        // the product entity needed for information
        GenericValue product = null;
        try {
            product = orderItem.getRelatedOne("Product");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get Product from OrderItem", module);
        }
        if (product == null) {
            return ServiceUtil.returnError("No product associated with OrderItem, cannot fulfill gift card");
        }

        // survey information
        String surveyId = UtilProperties.getPropertyValue(paymentConfig, "payment.giftcert.purchase.surveyId");

        // get the survey response
        GenericValue surveyResponse = null;
        try {
            Map fields = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId"), "surveyId", surveyId);
            List order = UtilMisc.toList("-responseDate");
            List responses = delegator.findByAnd("SurveyResponse", fields, order);
            // there should be only one
            surveyResponse = EntityUtil.getFirst(responses);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to get survey response information; cannot fulfill gift card");
        }
        if (surveyResponse == null) {
            return ServiceUtil.returnError("Survey response came back null from the database!");
        }

        // get the response answers
        List responseAnswers = null;
        try {
            responseAnswers = surveyResponse.getRelated("SurveyResponseAnswer");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to get survey response answers from survey response; cannot fulfill gift card");
        }

        // make a map of answer info
        Map answerMap = new HashMap();
        if (responseAnswers != null) {
            Iterator rai = responseAnswers.iterator();
            while (rai.hasNext()) {
                GenericValue answer = (GenericValue) rai.next();
                GenericValue question = null;
                try {
                    question = answer.getRelatedOne("SurveyQuestion");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to get survey question from answer");
                }
                if (question != null) {
                    String desc = question.getString("description");
                    String ans = answer.getString("textResponse");  // only support text response types for now
                    answerMap.put(desc, ans);
                }
            }
        }

        // get the send to email address - key defined in properties file
        String sendToKey = UtilProperties.getPropertyValue(paymentConfig, "payment.giftcert.purchase.survey.sendToEmail");
        String sendToEmail = (String) answerMap.get(sendToKey);

        // get the copyMe flag and set the order email address
        String orderEmails = orh.getOrderEmailString();
        String copyMeField = UtilProperties.getPropertyValue(paymentConfig, "payment.giftcert.purchase.survey.copyMe");
        String copyMeResp = copyMeField != null ? (String) answerMap.get(copyMeField) : null;
        boolean copyMe = (UtilValidate.isNotEmpty(copyMeField)
                && UtilValidate.isNotEmpty(copyMeResp) && "true".equalsIgnoreCase(copyMeResp)) ? true : false;

        int qtyLoop = quantity.intValue();
        for (int i = 0; i < qtyLoop; i++) {
            // create a gift certificate
            Map createGcCtx = new HashMap();
            //createGcCtx.put("paymentConfig", paymentConfig);
            createGcCtx.put("productStoreId", productStoreId);
            createGcCtx.put("currency", currency);
            createGcCtx.put("partyId", partyId);
            //createGcCtx.put("orderId", orderId);
            createGcCtx.put("initialAmount", amount);
            createGcCtx.put("userLogin", userLogin);

            Map createGcResult = null;
            try {
                createGcResult = dispatcher.runSync("createGiftCertificate", createGcCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to create gift certificate!");
            }

            // create the fulfillment record
            Map gcFulFill = new HashMap();
            gcFulFill.put("typeEnumId", "GC_ACTIVATE");
            gcFulFill.put("partyId", partyId);
            gcFulFill.put("orderId", orderId);
            gcFulFill.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            gcFulFill.put("surveyResponseId", surveyResponse.get("surveyResponseId"));
            gcFulFill.put("cardNumber", createGcResult.get("cardNumber"));
            gcFulFill.put("pinNumber", createGcResult.get("pinNumber"));
            gcFulFill.put("amount", createGcResult.get("initialAmount"));
            gcFulFill.put("responseCode", createGcResult.get("responseCode"));
            gcFulFill.put("referenceNum", createGcResult.get("referenceNum"));
            gcFulFill.put("userLogin", userLogin);
            try {
                dispatcher.runAsync("createGcFulFillmentRecord", gcFulFill, true);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to store fulfillment info");
            }

            if (ServiceUtil.isError(createGcResult)) {
                return ServiceUtil.returnError("Create Gift Certificate Failed!");
            }

            // add some information to the answerMap for the email
            answerMap.put("cardNumber", createGcResult.get("cardNumber"));
            answerMap.put("pinNumber", createGcResult.get("pinNumber"));
            answerMap.put("amount", createGcResult.get("initialAmount"));

            // get the email setting for this email type
            GenericValue productStoreEmail = null;
            String emailType = "PRDS_GC_PURCHASE";
            try {
                productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Unable to get product store email setting for gift card purchase", module);
            }
            if (productStoreEmail == null) {
                Debug.logError("No gift card purchase email setting found for this store; cannot send gift card information", module);
            } else {
                ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("EcommerceUiLabels", locale);
                uiLabelMap.addBottomResourceBundle("OrderUiLabels");
                uiLabelMap.addBottomResourceBundle("CommonUiLabels");
                answerMap.put("uiLabelMap", uiLabelMap);
                answerMap.put("locale", locale);

                // set the bcc address(s)
                String bcc = productStoreEmail.getString("bccAddress");
                if (copyMe) {
                    if (UtilValidate.isNotEmpty(bcc)) {
                        bcc = bcc + "," + orderEmails;
                    } else {
                        bcc = orderEmails;
                    }
                }

                Map emailCtx = new HashMap();
                String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
                if (UtilValidate.isEmpty(bodyScreenLocation)) {
                    bodyScreenLocation = OrderServices.getDefaultProductStoreEmailScreenLocation(emailType);
                }
                emailCtx.put("bodyScreenUri", bodyScreenLocation);
                emailCtx.put("bodyParameters", answerMap);
                emailCtx.put("sendTo", sendToEmail);
                emailCtx.put("contentType", productStoreEmail.get("contentType"));
                emailCtx.put("sendFrom", productStoreEmail.get("fromAddress"));
                emailCtx.put("sendCc", productStoreEmail.get("ccAddress"));
                emailCtx.put("sendBcc", bcc);
                emailCtx.put("subject", productStoreEmail.getString("subject"));
                emailCtx.put("userLogin", userLogin);

                // send off the email async so we will retry on failed attempts
                try {
                    dispatcher.runAsync("sendMailFromScreen", emailCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem sending mail", module);
                    // this is fatal; we will rollback and try again later
                    return ServiceUtil.returnError("Error sending Gift Card notice email: " + e.toString());
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map giftCertificateReload(DispatchContext dctx, Map context) {
        // this service should always be called via FULFILLMENT_EXTSYNC
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue orderItem = (GenericValue) context.get("orderItem");
        Locale locale = (Locale) context.get("locale");

        // order ID for tracking
        String orderId = orderItem.getString("orderId");

        // the order header for store info
        GenericValue orderHeader = null;
        try {
            orderHeader = orderItem.getRelatedOne("OrderHeader");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get OrderHeader from OrderItem",module);
            return ServiceUtil.returnError("Unable to get OrderHeader from OrderItem");
        }

        // get the order read helper
        OrderReadHelper orh = new OrderReadHelper(orderHeader);

        // get the currency
        String currency = orh.getCurrency();

        // make sure we have a currency
        if (currency == null) {
            currency = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        }

        // get the product store
        String productStoreId = null;
        if (orderHeader != null) {
            productStoreId = orh.getProductStoreId();
        }
        if (productStoreId == null) {
            return ServiceUtil.returnError("Unable to process gift card reload; no productStoreId on OrderHeader : " + orderId);
        }

        // payment config
        GenericValue paymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "GIFT_CARD", null, true);
        String paymentConfig = null;
        if (paymentSetting != null) {
            paymentConfig = paymentSetting.getString("paymentPropertiesPath");
        }
        if (paymentConfig == null) {
            return ServiceUtil.returnError("Unable to get payment configuration file");
        }

        // party ID for tracking
        GenericValue placingParty = orh.getPlacingParty();
        String partyId = null;
        if (placingParty != null) {
            partyId = placingParty.getString("partyId");
        }

        // amount of the gift card reload
        Double amount = orderItem.getDouble("unitPrice");

        // survey information
        String surveyId = UtilProperties.getPropertyValue(paymentConfig, "payment.giftcert.reload.surveyId");

        // get the survey response
        GenericValue surveyResponse = null;
        try {
            Map fields = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId"), "surveyId", surveyId);
            List order = UtilMisc.toList("-responseDate");
            List responses = delegator.findByAnd("SurveyResponse", fields, order);
            // there should be only one
            surveyResponse = EntityUtil.getFirst(responses);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to get survey response information; cannot fulfill gift card reload");
        }

        // get the response answers
        List responseAnswers = null;
        try {
            responseAnswers = surveyResponse.getRelated("SurveyResponseAnswer");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to get survey response answers from survey response; cannot fulfill gift card reload");
        }

        // make a map of answer info
        Map answerMap = new HashMap();
        if (responseAnswers != null) {
            Iterator rai = responseAnswers.iterator();
            while (rai.hasNext()) {
                GenericValue answer = (GenericValue) rai.next();
                GenericValue question = null;
                try {
                    question = answer.getRelatedOne("SurveyQuestion");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to get survey question from answer");
                }
                if (question != null) {
                    String desc = question.getString("description");
                    String ans = answer.getString("textResponse");  // only support text response types for now
                    answerMap.put(desc, ans);
                }
            }
        }

        String cardNumberKey = UtilProperties.getPropertyValue(paymentConfig, "payment.giftcert.reload.survey.cardNumber");
        String pinNumberKey = UtilProperties.getPropertyValue(paymentConfig, "payment.giftcert.reload.survey.pinNumber");
        String cardNumber = (String) answerMap.get(cardNumberKey);
        String pinNumber = (String) answerMap.get(pinNumberKey);

        // reload the gift card
        Map reloadCtx = new HashMap();
        reloadCtx.put("productStoreId", productStoreId);
        reloadCtx.put("currency", currency);
        reloadCtx.put("partyId", partyId);
        //reloadCtx.put("orderId", orderId);
        reloadCtx.put("cardNumber", cardNumber);
        reloadCtx.put("pinNumber", pinNumber);
        reloadCtx.put("amount", amount);
        reloadCtx.put("userLogin", userLogin);

        String errorMessage = null;
        Map reloadGcResult = null;
        try {
            reloadGcResult = dispatcher.runSync("addFundsToGiftCertificate", reloadCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            errorMessage = "Unable to call reload service!";
        }
        if (ServiceUtil.isError(reloadGcResult)) {
            errorMessage = ServiceUtil.getErrorMessage(reloadGcResult);
        }

        // create the fulfillment record
        Map gcFulFill = new HashMap();
        gcFulFill.put("typeEnumId", "GC_RELOAD");
        gcFulFill.put("userLogin", userLogin);
        gcFulFill.put("partyId", partyId);
        gcFulFill.put("orderId", orderId);
        gcFulFill.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
        gcFulFill.put("surveyResponseId", surveyResponse.get("surveyResponseId"));
        gcFulFill.put("cardNumber", cardNumber);
        gcFulFill.put("pinNumber", pinNumber);
        gcFulFill.put("amount", amount);
        if (reloadGcResult != null) {
            gcFulFill.put("responseCode", reloadGcResult.get("responseCode"));
            gcFulFill.put("referenceNum", reloadGcResult.get("referenceNum"));
        }
        try {
            dispatcher.runAsync("createGcFulFillmentRecord", gcFulFill, true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to store fulfillment info");
        }

        if (errorMessage != null) {
            // there was a problem
            Debug.logError("Reload Failed Need to Refund : " + reloadGcResult, module);

            // process the return
            try {
                Map refundCtx = UtilMisc.toMap("orderItem", orderItem, "partyId", partyId, "userLogin", userLogin);
                dispatcher.runAsync("refundGcPurchase", refundCtx, null, true, 300, true);
            } catch (GenericServiceException e) {
                Debug.logError(e, "ERROR! Unable to call create refund service; this failed reload will NOT be refunded", module);
            }

            return ServiceUtil.returnError(errorMessage);
        }

        // add some information to the answerMap for the email
        answerMap.put("processResult", reloadGcResult.get("processResult"));
        answerMap.put("responseCode", reloadGcResult.get("responseCode"));
        answerMap.put("previousAmount", reloadGcResult.get("previousBalance"));
        answerMap.put("amount", reloadGcResult.get("amount"));

        // get the email setting for this email type
        GenericValue productStoreEmail = null;
        String emailType = "PRDS_GC_RELOAD";
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get product store email setting for gift card purchase", module);
        }
        if (productStoreEmail == null) {
            Debug.logError("No gift card purchase email setting found for this store; cannot send gift card information", module);
        } else {
            ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("EcommerceUiLabels", locale);
            uiLabelMap.addBottomResourceBundle("OrderUiLabels");
            uiLabelMap.addBottomResourceBundle("CommonUiLabels");
            answerMap.put("uiLabelMap", uiLabelMap);
            answerMap.put("locale", locale);

            Map emailCtx = new HashMap();
            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = OrderServices.getDefaultProductStoreEmailScreenLocation(emailType);
            }
            emailCtx.put("bodyScreenUri", bodyScreenLocation);
            emailCtx.put("bodyParameters", answerMap);
            emailCtx.put("sendTo", orh.getOrderEmailString());
            emailCtx.put("contentType", productStoreEmail.get("contentType"));
            emailCtx.put("sendFrom", productStoreEmail.get("fromAddress"));
            emailCtx.put("sendCc", productStoreEmail.get("ccAddress"));
            emailCtx.put("sendBcc", productStoreEmail.get("bccAddress"));
            emailCtx.put("subject", productStoreEmail.getString("subject"));
            emailCtx.put("userLogin", userLogin);

            // send off the email async so we will retry on failed attempts
            try {
                dispatcher.runAsync("sendMailFromScreen", emailCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem sending mail", module);
                // this is fatal; we will rollback and try again later
                return ServiceUtil.returnError("Error sending Gift Card notice email: " + e.toString());
            }
        }

        return ServiceUtil.returnSuccess();
    }

    // Tracking Service
    public static Map createFulfillmentRecord(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();

        // create the fulfillment record
        GenericValue gcFulFill = delegator.makeValue("GiftCardFulfillment", null);
        gcFulFill.set("fulfillmentId", delegator.getNextSeqId("GiftCardFulfillment"));
        gcFulFill.set("typeEnumId", context.get("typeEnumId"));
        gcFulFill.set("merchantId", context.get("merchantId"));
        gcFulFill.set("partyId", context.get("partyId"));
        gcFulFill.set("orderId", context.get("orderId"));
        gcFulFill.set("orderItemSeqId", context.get("orderItemSeqId"));
        gcFulFill.set("surveyResponseId", context.get("surveyResponseId"));
        gcFulFill.set("cardNumber", context.get("cardNumber"));
        gcFulFill.set("pinNumber", context.get("pinNumber"));
        gcFulFill.set("amount", context.get("amount"));
        gcFulFill.set("responseCode", context.get("responseCode"));
        gcFulFill.set("referenceNum", context.get("referenceNum"));
        gcFulFill.set("authCode", context.get("authCode"));
        gcFulFill.set("fulfillmentDate", UtilDateTime.nowTimestamp());
        try {
            delegator.create(gcFulFill);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to store fulfillment info");
        }
        return ServiceUtil.returnSuccess();
    }

    // Refund Service
    public static Map refundGcPurchase(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        GenericValue orderItem = (GenericValue) context.get("orderItem");
        String partyId = (String) context.get("partyId");

        // refresh the item object for status changes
        try {
            orderItem.refresh();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        Map returnableInfo = null;
        try {
            returnableInfo = dispatcher.runSync("getReturnableQuantity", UtilMisc.toMap("orderItem", orderItem, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Unable to get returnable infomation for order item : " + orderItem);
        }

        if (returnableInfo != null) {
            Double returnableQuantity = (Double) returnableInfo.get("returnableQuantity");
            Double returnablePrice = (Double) returnableInfo.get("returnablePrice");
            Debug.logInfo("Returnable INFO : " + returnableQuantity + " @ " + returnablePrice + " :: " + orderItem, module);

            // create the return header
            Map returnHeaderInfo = new HashMap();
            returnHeaderInfo.put("fromPartyId", partyId);
            returnHeaderInfo.put("userLogin", userLogin);
            Map returnHeaderResp = null;
            try {
                returnHeaderResp = dispatcher.runSync("createReturnHeader", returnHeaderInfo);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to create return header");
            }

            if (returnHeaderResp != null) {
                String errorMessage = ServiceUtil.getErrorMessage(returnHeaderResp);
                if (errorMessage != null) {
                    return ServiceUtil.returnError(errorMessage);
                }
            }

            String returnId = null;
            if (returnHeaderResp != null) {
                returnId = (String) returnHeaderResp.get("returnId");
            }

            if (returnId == null) {
                return ServiceUtil.returnError("Create return did not return a valid return id");
            }

            // create the return item
            Map returnItemInfo = new HashMap();
            returnItemInfo.put("returnId", returnId);
            returnItemInfo.put("returnReasonId", "RTN_DIG_FILL_FAIL");
            returnItemInfo.put("returnTypeId", "RTN_REFUND");
            returnItemInfo.put("returnItemType", "ITEM");
            returnItemInfo.put("description", orderItem.get("itemDescription"));
            returnItemInfo.put("orderId", orderItem.get("orderId"));
            returnItemInfo.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            returnItemInfo.put("returnQuantity", returnableQuantity);
            returnItemInfo.put("returnPrice", returnablePrice);
            returnItemInfo.put("userLogin", userLogin);
            Map returnItemResp = null;
            try {
                returnItemResp = dispatcher.runSync("createReturnItem", returnItemInfo);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to create return item");
            }

            if (returnItemResp != null) {
                String errorMessage = ServiceUtil.getErrorMessage(returnItemResp);
                if (errorMessage != null) {
                    return ServiceUtil.returnError(errorMessage);
                }
            }

            String returnItemSeqId = null;
            if (returnItemResp != null) {
                returnItemSeqId = (String) returnItemResp.get("returnItemSeqId");
            }

            if (returnItemSeqId == null) {
                return ServiceUtil.returnError("Create return item did not return a valid sequence id");
            } else {
                Debug.logVerbose("Created return item : " + returnId + " / " + returnItemSeqId, module);
            }

            // need the admin userLogin to "fake" out the update service
            GenericValue admin = null;
            try {
                admin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to look up UserLogin from database");
            }

            // update the status to received so it can process
            Map updateReturnInfo = new HashMap();
            updateReturnInfo.put("returnId", returnId);
            updateReturnInfo.put("statusId", "RETURN_RECEIVED");
            updateReturnInfo.put("currentStatusId", "RETURN_REQUESTED");
            updateReturnInfo.put("userLogin", admin);
            Map updateReturnResp = null;
            try {
                updateReturnResp = dispatcher.runSync("updateReturnHeader", updateReturnInfo);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to update return header status");
            }

            if (updateReturnResp != null) {
                String errorMessage = ServiceUtil.getErrorMessage(updateReturnResp);
                if (errorMessage != null) {
                    return ServiceUtil.returnError(errorMessage);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    // Private worker methods
    private static boolean validatePin(GenericDelegator delegator, String cardNumber, String pinNumber) {
        GenericValue finAccount = null;
        try {
            finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", cardNumber));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (finAccount != null) {
            String dbPin = finAccount.getString("finAccountCode");
            Debug.log("GC Pin Validation: [Sent: " + pinNumber + "] [Actual: " + dbPin + "]", module);
            if (dbPin != null && dbPin.equals(pinNumber)) {
                return true;
            }
        } else {
            Debug.logInfo("GC FinAccount record not found (" + cardNumber + ")", module);
        }
        return false;
    }

    private static double getBalance(GenericDelegator delegator, String cardNumber) throws GeneralException {
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);

        double balance = 0.00;
        List transactions = null;
        try {
            transactions = delegator.findByAnd("FinAccountTrans", UtilMisc.toMap("finAccountId", cardNumber));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException("Unable to obtain financial account transaction history!", e);
        }

        if (transactions != null) {
            Iterator i = transactions.iterator();
            while (i.hasNext()) {
                GenericValue trans = (GenericValue) i.next();
                GenericValue payment = null;
                try {
                    payment = trans.getRelatedOne("Payment");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralException("Unable to obtain payment record for transaction - " + trans.getString("finAccountTransId"), e);
                }
                if (payment != null) {
                    String type = payment.getString("paymentTypeId");
                    Double amount = payment.getDouble("amount");
                    if (amount == null) {
                        amount = new Double(0);
                    }
                    if ("RECEIPT".equals(type)) {
                        balance += amount.doubleValue();
                    } else if ("DISBURSEMENT".equals(type)) {
                        balance -= amount.doubleValue();
                    }
                }
            }
        }

        String balanceStr = formatter.format(balance);
        double formattedBalance = 0.00;
        try {
            formattedBalance = formatter.parse(balanceStr).doubleValue();
        } catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed balance total.", module);
        }
        return formattedBalance;
    }

    private static String createTransaction(GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Double amount,
            String productStoreId, String partyId, String currencyUom, String txType, String finAccountId) throws GeneralException {
        final String coParty = getPayToPartyId(delegator, productStoreId);
        final String paymentMethodType = "GIFT_CERTIFICATE";

        if (UtilValidate.isEmpty(partyId)) {
            partyId = "_NA_";
        }

        String paymentType = null;
        String partyIdFrom = null;
        String partyIdTo = null;
        if ("DEPOSIT".equals(txType)) {
            paymentType = "RECEIPT";
            partyIdFrom = partyId;
            partyIdTo = coParty;
        } else if ("WITHDRAWAL".equals(txType)) {
            paymentType = "DISBURSEMENT";
            partyIdFrom = coParty;
            partyIdTo = partyId;
        } else {
            throw new GeneralException("Unable to create financial account transaction!");
        }

        // create the payment for the transaction
        Map paymentCtx = UtilMisc.toMap("paymentTypeId", paymentType);
        paymentCtx.put("paymentMethodTypeId", paymentMethodType);
        //paymentCtx.put("paymentMethodId", "");
        //paymentCtx.put("paymentGatewayResponseId", "");
        paymentCtx.put("partyIdTo", partyIdTo);
        paymentCtx.put("partyIdFrom", partyIdFrom);
        paymentCtx.put("statusId", "PMNT_RECEIVED");
        //paymentCtx.put("paymentPreferenceId", "");
        paymentCtx.put("currencyUomId", currencyUom);
        paymentCtx.put("amount", amount);
        paymentCtx.put("userLogin", userLogin);
        paymentCtx.put("paymentRefNum", "N/A");

        String paymentId = null;
        Map payResult = null;
        try {
            payResult = dispatcher.runSync("createPayment", paymentCtx);
        } catch (GenericServiceException e) {
            throw new GeneralException(e);
        }
        if (payResult == null) {
            throw new GeneralException("Unknow error in creating financial account transaction!");
        }
        if (ServiceUtil.isError(payResult)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(payResult));
        } else {
            paymentId = (String) payResult.get("paymentId");
        }

        // create the initial transaction
        Map transCtx = UtilMisc.toMap("finAccountTransTypeId", txType);
        transCtx.put("finAccountId", finAccountId);
        transCtx.put("partyId", userLogin.getString("partyId"));
        transCtx.put("userLogin", userLogin);
        transCtx.put("paymentId", paymentId);

        Map transResult = null;
        String txId = null;
        try {
            transResult = dispatcher.runSync("createFinAccountTrans", transCtx);
        } catch (GenericServiceException e) {
            throw new GeneralException(e);
        }
        if (transResult == null) {
            throw new GeneralException("Unknown error in creating financial account transaction!");
        }
        if (ServiceUtil.isError(transResult)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(transResult));
        } else {
            txId = (String) transResult.get("finAccountTransId");
        }

        return txId;
    }

    private static String generateNumber(GenericDelegator delegator, int length, boolean isId) throws GenericEntityException {
        if (length > 19) {
            length = 19;
        }

        Random rand = new Random();
        boolean isValid = false;
        String number = null;
        while (!isValid) {
            number = "";
            for (int i = 0; i < length; i++) {
                int randInt = rand.nextInt(9);
                number = number + randInt;
            }

            if (isId) {
                int check = UtilValidate.getLuhnCheckDigit(number);
                number = number + check;

                // validate the number
                if (checkCardNumber(number)) {
                    // make sure this number doens't already exist
                    isValid = checkNumberInDatabase(delegator, number);
                }
            } else {
                isValid = true;
            }
        }
        return number;
    }

    private static boolean checkNumberInDatabase(GenericDelegator delegator, String number) throws GenericEntityException {
        GenericValue finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", number));
        if (finAccount == null) {
            return true;
        }
        return false;
    }

    private static boolean checkCardNumber(String number) {
        number = number.replaceAll("\\D", "");
        return UtilValidate.sumIsMod10(UtilValidate.getLuhnSum(number));
    }

    private static String getPayToPartyId(GenericDelegator delegator, String productStoreId) {
        String payToPartyId = "Company"; // default value
        GenericValue productStore = null;
        try {
            productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to locate ProductStore (" + productStoreId + ")", module);
            return null;
        }
        if (productStore != null && productStore.get("payToPartyId") != null) {
            payToPartyId = productStore.getString("payToPartyId");
        }
        return payToPartyId;
    }
}
