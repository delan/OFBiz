/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting.thirdparty.gosoftware;

import java.util.Map;
import java.util.Properties;
import java.util.List;
import java.text.DecimalFormat;
import java.io.IOException;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.accounting.payment.PaymentGatewayServices;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.2
 */
public class PcChargeServices {

    public static final String module = PcChargeServices.class.getName();

    public static Map ccAuth(DispatchContext dctx, Map context) {
        Properties props = buildPccProperties(context);
        PcChargeApi api = getApi(props);
        if (api == null) {
            return ServiceUtil.returnError("PCCharge is not configured properly");
        }

        try {
            PcChargeServices.setCreditCardInfo(api, context);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // basic tx info
        api.set(PcChargeApi.TRANS_AMOUNT, getAmountString(context, "processAmount"));
        api.set(PcChargeApi.TICKET_NUM, context.get("orderId"));
        api.set(PcChargeApi.MANUAL_FLAG, "0");
        api.set(PcChargeApi.PRESENT_FLAG, "1");

        // command setting
        if ("true".equalsIgnoreCase(props.getProperty("autoBill"))) {
            // sale
            api.set(PcChargeApi.COMMAND, "1");
        } else {
            // pre-auth
            api.set(PcChargeApi.COMMAND, "4");
        }

        // send the transaction
        PcChargeApi out = null;
        try {
            out = api.send();
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (out != null) {
            Map result = ServiceUtil.returnSuccess();
            String resultCode = out.get(PcChargeApi.RESULT);
            if ("CAPTURED".equals(resultCode)) {
                result.put("authResult", new Boolean(true));
                result.put("captureResult", new Boolean(true));
            } else if ("APPROVED".equals(resultCode)) {
                result.put("authCode", out.get(PcChargeApi.AUTH_CODE));
                result.put("authResult", new Boolean(true));
            } else if ("PROCESSED".equals(resultCode)) {
                result.put("authResult", new Boolean(true));
            } else {
                result.put("authResult", new Boolean(false));
            }

            result.put("authRefNum", out.get(PcChargeApi.TROUTD) != null ? out.get(PcChargeApi.TROUTD) : "");
            result.put("processAmount", context.get("processAmount"));
            result.put("authCode", out.get(PcChargeApi.AUTH_CODE));
            result.put("authFlag", out.get(PcChargeApi.REFERENCE));
            result.put("authMessage", out.get(PcChargeApi.RESULT));
            result.put("cvCode", out.get(PcChargeApi.CVV2_CODE));
            result.put("avsCode", out.get(PcChargeApi.AVS_CODE));

            if (result.get("captureResult") != null) {
                result.put("captureCode", out.get(PcChargeApi.AUTH_CODE));
                result.put("captureFlag", out.get(PcChargeApi.REFERENCE));
                result.put("captureRefNum", out.get(PcChargeApi.TROUTD));
                result.put("captureMessage", out.get(PcChargeApi.RESULT));
            }

            return result;

        } else {
            return ServiceUtil.returnError("Receive a null result from PcCharge");
        }
    }

    public static Map ccCapture(DispatchContext dctx, Map context) {
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");

        //lets see if there is a auth transaction already in context
        GenericValue authTransaction = (GenericValue) context.get("authTrans");

        if(authTransaction == null){
        	authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        }

        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found for the OrderPaymentPreference; cannot capture");
        }

        // setup the PCCharge Interface
        Properties props = buildPccProperties(context);
        PcChargeApi api = getApi(props);
        if (api == null) {
            return ServiceUtil.returnError("PCCharge is not configured properly");
        }

        api.set(PcChargeApi.TROUTD, authTransaction.getString("referenceNum"));
        api.set(PcChargeApi.COMMAND, "5");

        // send the transaction
        PcChargeApi out = null;
        try {
            out = api.send();
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (out != null) {
            Map result = ServiceUtil.returnSuccess();
            String resultCode = out.get(PcChargeApi.RESULT);
            if ("CAPTURED".equals(resultCode)) {
                result.put("captureResult", new Boolean(true));
            } else {
                result.put("captureResult", new Boolean(false));
            }
            result.put("captureAmount", context.get("captureAmount"));
            result.put("captureRefNum", out.get(PcChargeApi.TROUTD) != null ? out.get(PcChargeApi.TROUTD) : "");
            result.put("captureCode", out.get(PcChargeApi.AUTH_CODE));
            result.put("captureFlag", out.get(PcChargeApi.REFERENCE));
            result.put("captureMessage", out.get(PcChargeApi.RESULT));

            return result;
        } else {
            return ServiceUtil.returnError("Receive a null result from PcCharge");
        }
    }

    public static Map ccRelease(DispatchContext dctx, Map context) {
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");

        //lets see if there is a auth transaction already in context
        GenericValue authTransaction = (GenericValue) context.get("authTrans");

        if(authTransaction == null){
        	authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        }

        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found for the OrderPaymentPreference; cannot release");
        }

        // setup the PCCharge Interface
        Properties props = buildPccProperties(context);
        PcChargeApi api = getApi(props);
        if (api == null) {
            return ServiceUtil.returnError("PCCharge is not configured properly");
        }

        api.set(PcChargeApi.TROUTD, authTransaction.getString("referenceNum"));
        api.set(PcChargeApi.COMMAND, "3");

        // check to make sure we are configured for SALE mode
        if (!"true".equalsIgnoreCase(props.getProperty("autoBill"))) {
            return ServiceUtil.returnError("PCCharge does not support releasing pre-auths.");
        }

        // send the transaction
        PcChargeApi out = null;
        try {
            out = api.send();
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (out != null) {
            Map result = ServiceUtil.returnSuccess();
            String resultCode = out.get(PcChargeApi.RESULT);
            if ("VOIDED".equals(resultCode)) {
                result.put("releaseResult", new Boolean(true));
            } else {
                result.put("releaseResult", new Boolean(false));
            }
            result.put("releaseAmount", context.get("releaseAmount"));
            result.put("releaseRefNum", out.get(PcChargeApi.TROUTD) != null ? out.get(PcChargeApi.TROUTD) : "");
            result.put("releaseCode", out.get(PcChargeApi.AUTH_CODE));
            result.put("releaseFlag", out.get(PcChargeApi.REFERENCE));
            result.put("releaseMessage", out.get(PcChargeApi.RESULT));

            return result;
        } else {
            return ServiceUtil.returnError("Receive a null result from PcCharge");
        }
    }

    public static Map ccRefund(DispatchContext dctx, Map context) {
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");

        //lets see if there is a auth transaction already in context
        GenericValue authTransaction = (GenericValue) context.get("authTrans");

        if(authTransaction == null){
        	authTransaction = PaymentGatewayServices.getAuthTransaction(orderPaymentPreference);
        }

        if (authTransaction == null) {
            return ServiceUtil.returnError("No authorization transaction found for the OrderPaymentPreference; cannot refund");
        }

        // setup the PCCharge Interface
        Properties props = buildPccProperties(context);
        PcChargeApi api = getApi(props);
        if (api == null) {
            return ServiceUtil.returnError("PCCharge is not configured properly");
        }

        api.set(PcChargeApi.TROUTD, authTransaction.getString("referenceNum"));
        api.set(PcChargeApi.COMMAND, "2");

        // send the transaction
        PcChargeApi out = null;
        try {
            out = api.send();
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (out != null) {
            Map result = ServiceUtil.returnSuccess();
            String resultCode = out.get(PcChargeApi.RESULT);
            if ("CAPTURED".equals(resultCode)) {
                result.put("refundResult", new Boolean(true));
            } else {
                result.put("refundResult", new Boolean(false));
            }
            result.put("refundAmount", context.get("releaseAmount"));
            result.put("refundRefNum", out.get(PcChargeApi.TROUTD) != null ? out.get(PcChargeApi.TROUTD) : "");
            result.put("refundCode", out.get(PcChargeApi.AUTH_CODE));
            result.put("refundFlag", out.get(PcChargeApi.REFERENCE));
            result.put("refundMessage", out.get(PcChargeApi.RESULT));

            return result;
        } else {
            return ServiceUtil.returnError("Receive a null result from PcCharge");
        }
    }

    private static void setCreditCardInfo(PcChargeApi api, Map context) throws GeneralException {
        GenericValue orderPaymentPreference = (GenericValue) context.get("orderPaymentPreference");
        GenericValue creditCard = (GenericValue) context.get("creditCard");
        if (creditCard != null) {
            List expDateList = StringUtil.split(creditCard.getString("expireDate"), "/");
            String month = (String) expDateList.get(0);
            String year = (String) expDateList.get(1);
            String y2d = year.substring(2);
            String expDate = month + y2d;

            String title = creditCard.getString("titleOnCard");
            String fname = creditCard.getString("firstNameOnCard");
            String mname = creditCard.getString("middleNameOnCard");
            String lname = creditCard.getString("lastNameOnCard");
            String sufix = creditCard.getString("suffixOnCard");
            StringBuffer name = new StringBuffer();
            if (UtilValidate.isNotEmpty(title)) {
                name.append(title + " ");
            }
            if (UtilValidate.isNotEmpty(fname)) {
                name.append(fname + " ");
            }
            if (UtilValidate.isNotEmpty(mname)) {
                name.append(mname + " ");
            }
            if (UtilValidate.isNotEmpty(lname)) {
                name.append(lname + " ");
            }
            if (UtilValidate.isNotEmpty(sufix)) {
                name.append(sufix);
            }
            String nameOnCard = name.toString().trim();
            String acctNumber = creditCard.getString("cardNumber");
            String cvNum = (String) context.get("cardSecurityCode");

            api.set(PcChargeApi.ACCT_NUM, acctNumber);
            api.set(PcChargeApi.EXP_DATE, expDate);
            api.set(PcChargeApi.CARDHOLDER, nameOnCard);
            if (UtilValidate.isNotEmpty(cvNum)) {
                api.set(PcChargeApi.CVV2, cvNum);
            }

            // billing address information
            GenericValue billingAddress = (GenericValue) context.get("billingAddress");
            if (billingAddress != null) {
                api.set(PcChargeApi.STREET, billingAddress.getString("address1"));
                api.set(PcChargeApi.ZIP_CODE, billingAddress.getString("postalCode"));
            } else {
                String zipCode = orderPaymentPreference.getString("billingPostalCode");
                if (UtilValidate.isNotEmpty(zipCode)) {
                    api.set(PcChargeApi.ZIP_CODE, zipCode);
                }
            }
        } else {
            throw new GeneralException("No CreditCard object found");
        }
    }

    private static PcChargeApi getApi(Properties props) {
        if (props == null) {
            Debug.logError("Cannot load API w/ null properties", module);
            return null;
        }
        String host = props.getProperty("host");
        int port = 0;
        try {
            port = Integer.parseInt(props.getProperty("port"));
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        PcChargeApi api = null;
        if (port > 0 && host != null) {
            api = new PcChargeApi(host, port);
        } else {
            api = new PcChargeApi();
        }

        api.set(PcChargeApi.PROCESSOR_ID, props.getProperty("processorID"));
        api.set(PcChargeApi.MERCH_NUM, props.getProperty("merchantID"));
        api.set(PcChargeApi.USER_ID, props.getProperty("userID"));
        return api;
    }

    private static Properties buildPccProperties(Map context) {
        String configString = (String) context.get("paymentConfig");
        if (configString == null) {
            configString = "payment.properties";
        }

        String processorId = UtilProperties.getPropertyValue(configString, "payment.pccharge.processorID");
        String merchantId = UtilProperties.getPropertyValue(configString, "payment.pccharge.merchantID");
        String userId = UtilProperties.getPropertyValue(configString, "payment.pccharge.userID");
        String host = UtilProperties.getPropertyValue(configString, "payment.pccharge.host");
        String port = UtilProperties.getPropertyValue(configString, "payment.pccharge.port");
        String autoBill = UtilProperties.getPropertyValue(configString, "payment.pccharge.autoBill", "true");

        // some property checking
        if (UtilValidate.isEmpty(processorId)) {
            Debug.logWarning("The processorID property in [" + configString + "] is not configured", module);
            return null;
        }
        if (UtilValidate.isEmpty(merchantId)) {
            Debug.logWarning("The merchantID property in [" + configString + "] is not configured", module);
            return null;
        }
        if (UtilValidate.isEmpty(userId)) {
            Debug.logWarning("The userID property in [" + configString + "] is not configured", module);
            return null;
        }

        // create some properties for CS Client
        Properties props = new Properties();
        props.put("processorID", processorId);
        props.put("merchantID", merchantId);
        props.put("userID", userId);
        props.put("host", host);
        props.put("port", port);
        props.put("autoBill", autoBill);
        Debug.log("Returning properties - " + props, module);

        return props;
    }

    private static String getAmountString(Map context, String amountField) {
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        Double processAmount = (Double) context.get(amountField);
        return formatter.format(processAmount);
    }

}
