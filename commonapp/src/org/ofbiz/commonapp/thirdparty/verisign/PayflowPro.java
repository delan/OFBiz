/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.thirdparty.verisign;

import java.util.*;
import java.text.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.order.OrderReadHelper;

import com.Verisign.payment.PFProAPI;

/**
 * PayflowPro - Verisign PayFlow Pro <=> OFBiz Service Module
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Apr 12, 2002
 * @version    1.0
 */
public class PayflowPro {

    public static final String module = PayflowPro.class.getName();

    /**
     * Authorize payment service. Service wrapper around PayFlow Pro API.
     * @param dctx Service Engine DispatchContext.
     * @param context Map context of parameters.
     * @return Response map, including RESPMSG, and RESULT keys.
     */
    public static Map authorizePayment(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");

        // get the order header and payment preferences
        GenericValue orderHeader = null;
        Collection paymentPrefs = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            paymentPrefs = orderHeader.getRelatedOrderBy("OrderPaymentPreference", UtilMisc.toList("maxAmount"));

            // filter out payment prefs which have been authorized declined or settled.
            List exprs = UtilMisc.toList(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_AUTHORIZED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_DECLINED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_SETTLED"));
            paymentPrefs = EntityUtil.filterByAnd(paymentPrefs, exprs);
        } catch (GenericEntityException gee) {
            gee.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + gee.getMessage() + ").");
            return result;
        }
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        double amountToBill = orh.getTotalPrice();
        if (Debug.verboseOn()) Debug.logVerbose("Amount to charge is: " + amountToBill, module);

        Iterator payments = paymentPrefs.iterator();
        while (payments.hasNext()) {
            Map data = UtilMisc.toMap("COMMENT1", orderId);
            GenericValue paymentPref = (GenericValue) payments.next();
            GenericValue paymentMethod = null;
            GenericValue cc = null;
            GenericValue eft = null;
            GenericValue ps = null;

            // gather the payment related objects
            try {
                paymentMethod = paymentPref.getRelatedOne("PaymentMethod");
                if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
                    cc = paymentMethod.getRelatedOne("CreditCard");
                    ps = cc.getRelatedOne("PostalAddress");
                    if (UtilProperties.propertyValueEqualsIgnoreCase("payflow", "preAuth", "Y"))
                        data.put("TRXTYPE", "A");
                    else
                        data.put("TRXTYPE", "S");
                    data.put("TENDER", "C");
                } else if (paymentMethod != null && paymentMethod.getString("paymentMethodTypeId").equals("EFT_ACCOUNT")) {
                    eft = paymentMethod.getRelatedOne("EFT_ACCOUNT");
                    ps = eft.getRelatedOne("PostalAddress");
                    data.put("TENDER", "C");
                    data.put("TRXTYPE", "S");
                } else {
                    // not a valid payment method type
                    continue;
                }
            } catch (GenericEntityException gee) {
                gee.printStackTrace();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order payment information (" + gee.getMessage() + ").");
                return result;
            }

            // get the order total
            double thisAmount = amountToBill;
            if (paymentPref.get("maxAmount") != null)
                thisAmount = paymentPref.getDouble("maxAmount").doubleValue();
            amountToBill -= thisAmount;
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            String totalStr = nf.format(thisAmount);
            if (Debug.verboseOn()) Debug.logVerbose("Charging amount: " + totalStr, module);
            if (totalStr != null) {
                data.put("AMT", totalStr.substring(1));
            }

            // get the payment information
            data.put("ACCT", cc.getString("cardNumber"));
            data.put("COMMENT2", cc.getString("nameOnCard"));
            if (cc.get("expireDate") != null) {
                String exp = cc.getString("expireDate");
                String expDate = exp.substring(0, 2);
                expDate = expDate + exp.substring(exp.length() - 2);
                data.put("EXPDATE", expDate);
            }

            // gather the address info
            if (ps != null) {
                String street = ps.getString("address1") +
                        (ps.get("address2") != null && ps.getString("address2").length() > 0 ? " " +
                        ps.getString("address2") : "");
                data.put("STREET", street);
                data.put("ZIP", ps.getString("postalCode"));
            }

            PFProAPI pn = init();

            // get the base params
            StringBuffer params = makeBaseParams();

            // parse the context parameters
            params.append("&" + parseContext(data));

            // transmit the request
            if (Debug.verboseOn()) Debug.logVerbose("Sending to Verisign: " + params.toString(), module);
            String resp = pn.SubmitTransaction(params.toString());
            if (Debug.verboseOn()) Debug.logVerbose("Response from Verisign: " + resp, module);

            // reset for next use
            pn.DestroyContext();

            // check the response
            result = parseResponse(resp, result, paymentPref);
        }

        return result;
    }

    private static Map parseResponse(String resp, Map result, GenericValue paymentPreference) {
        boolean checkAVS = UtilProperties.propertyValueEqualsIgnoreCase("payflow", "checkAvs", "Y");
        Map parameters = new OrderedMap();
        List params = StringUtil.split(resp, "&");
        Iterator i = params.iterator();
        while (i.hasNext()) {
            String str = (String) i.next();
            if (str.length() > 0) {
                List kv = StringUtil.split(str, "=");
                Object k = kv.get(0);
                Object v = kv.get(1);
                if (k != null && v != null)
                    parameters.put(k, v);
            }
        }
        String respCode = (String) parameters.get("RESULT");

        try {
            if (respCode.equals("0")) {
                paymentPreference.set("authCode", parameters.get("AUTHCODE"));
                paymentPreference.set("statusId", "PAYMENT_AUTHORIZED");
                result.put("authResponse", "SUCCESS");
            } else {
                paymentPreference.set("statusId", "PAYMENT_DECLINED");
                result.put("authResponse", "FAIL");
            }
            paymentPreference.set("authRefNum", parameters.get("PNREF"));
            paymentPreference.set("authFlag", parameters.get("RESULT"));
            paymentPreference.set("authMessage", parameters.get("RESPMSG"));
            paymentPreference.set("authDate", UtilDateTime.nowTimestamp());
            paymentPreference.store();
        } catch (GenericEntityException gee) {
            gee.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not store payment info (" + gee.getMessage() + ").");
            return result;
        }
        return result;
    }

    private static String parseContext(Map context) {
        StringBuffer buf = new StringBuffer();
        Set keySet = context.keySet();
        Iterator i = keySet.iterator();
        while (i.hasNext()) {
            Object name = i.next();
            Object value = context.get(name);
            if (value != null && (value instanceof String) && ((String) value).length() == 0) continue;
            buf.append(name + "=");
            buf.append(value);
            if (i.hasNext())
                buf.append("&");
        }
        return buf.toString();
    }

    private static StringBuffer makeBaseParams() {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append("PARTNER=");
            buf.append(UtilProperties.getPropertyValue("payflow", "partner", "VeriSign"));
            buf.append("&");
            buf.append("VENDOR=");
            buf.append(UtilProperties.getPropertyValue("payflow", "vendor", "nobody"));
            buf.append("&");
            buf.append("USER=");
            buf.append(UtilProperties.getPropertyValue("payflow", "user", "nobody"));
            buf.append("&");
            buf.append("PWD=");
            buf.append(UtilProperties.getPropertyValue("payflow", "pwd", "password"));
        } catch (Exception e) {
            return null;
        }
        return buf;
    }

    private static PFProAPI init() {
        String hostAddress = "test-payflow.verisign.com";
        Integer hostPort = Integer.decode("443");
        Integer timeout = Integer.decode("80");
        String proxyAddress = "";
        Integer proxyPort = Integer.decode("0");
        String proxyLogon = "";
        String proxyPassword = "";
        String certsPath = "certs";

        try {
            certsPath = UtilProperties.getPropertyValue("payflow", "certsPath", "certs");
            hostAddress = UtilProperties.getPropertyValue("payflow", "hostAddress", "test-payflow.verisign.com");
            hostPort = Integer.decode(UtilProperties.getPropertyValue("payflow", "hostPort", "443"));
            timeout = Integer.decode(UtilProperties.getPropertyValue("payflow", "timeout", "80"));
            proxyAddress = UtilProperties.getPropertyValue("payflow", "proxyAddress", "");
            proxyPort = Integer.decode(UtilProperties.getPropertyValue("payflow", "proxyPort", "80"));
            proxyLogon = UtilProperties.getPropertyValue("payflow", "proxyLogon", "");
            proxyPassword = UtilProperties.getPropertyValue("payflow", "proxyPassword", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PFProAPI pn = new PFProAPI();

        // Set the certificate path
        pn.SetCertPath(certsPath);

        // Call the client.
        pn.CreateContext(hostAddress,
                hostPort.intValue(),
                timeout.intValue(),
                proxyAddress,
                proxyPort.intValue(),
                proxyLogon,
                proxyPassword);

        return pn;
    }
}

