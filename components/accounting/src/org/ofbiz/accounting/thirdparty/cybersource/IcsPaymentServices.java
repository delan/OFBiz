/*
 * $Id: IcsPaymentServices.java,v 1.1 2003/10/28 20:41:08 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting.thirdparty.cybersource;

import java.util.*;
import java.text.NumberFormat;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.Debug;

import com.cybersource.ws.client.axis.basic.Client;
import com.cybersource.ws.client.axis.basic.BasicClientException;
import com.cybersource.ws.client.axis.AxisFaultException;

/**
 * CyberSource WS Integration Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class IcsPaymentServices {

    public static final String module = IcsPaymentServices.class.getName();

    public static Map ccAuth(DispatchContext dctx, Map context) {
        // generate the request/properties
        String orderId = (String) context.get("orderId");
        Properties props = buildCsProperties(context);
        Map request = buildAuthRequest(context);
        request.put("merchantID", props.get("merchantID"));
        request.put("merchantReferenceCode", orderId);

        // transmit the request
        Map reply = null;
        try {
            reply = Client.runTransaction(request, props);
        } catch (AxisFaultException e) {
            Debug.logError(e, "ERROR: Exception from Axis to CyberSource", module);
            return ServiceUtil.returnError("Unable to communicate with CyberSource");
        } catch (BasicClientException e) {
            Debug.logError(e, "ERROR: CyberSource Client exception", module);
            return ServiceUtil.returnError("Unable to communicate with CyberSource");
        }

        // process the reply
        Map result = ServiceUtil.returnSuccess();
        processAuthResult(reply, result);
        return result;
    }

    public static Map ccReAuth(DispatchContext dctx, Map context) {
        return ServiceUtil.returnSuccess();
    }

    public static Map ccCapture(DispatchContext dctx, Map context) {
        return ServiceUtil.returnSuccess();
    }

    public static Map ccRelease(DispatchContext dctx, Map context) {
        return ServiceUtil.returnSuccess();
    }

    public static Map ccRefund(DispatchContext dctx, Map context) {
        return ServiceUtil.returnSuccess();
    }

    public static Map ccCredit(DispatchContext dctx, Map context) {
        return ServiceUtil.returnSuccess();
    }

    private static Properties buildCsProperties(Map context) {
        String configString = (String) context.get("paymentConfig");
        if (configString == null) {
            configString = "payment.properties";
        }

        String merchantId = UtilProperties.getPropertyValue(configString, "payment.cybersource.merchantID", "_NA_");
        String serverName = UtilProperties.getPropertyValue(configString, "payment.cybersource.serverName", "CyberSource_SJC_US");
        String serverUrl = UtilProperties.getPropertyValue(configString, "payment.cybersource.serverURL", "http://ics2test.ic3.com:80/");

        // TODO: make this relative to ofbiz.home
        String keysPath = UtilProperties.getPropertyValue(configString, "payment.cybersource.keysPath", "");
        String debugLevel = UtilProperties.getPropertyValue(configString, "payment.cybersource.debugLevel", "0");
        String debugFile = UtilProperties.getPropertyValue(configString, "payment.cybersource.debugFile", "debug.log");

        // create some properties for CS Client
        Properties props = new Properties();
        props.put("merchantID", merchantId);
        props.put("serverName", serverName);
        props.put("serverURL", serverUrl);
        props.put("ics.keysPath", keysPath);
        props.put("debugLevel", debugLevel);
        props.put("debugFile", debugFile);

        return props;
    }

    private static Map buildAuthRequest(Map context) {
        // make the request map
        Map request = new HashMap();
        request.put("ccAuthService_run", "true"); // run auth service
        appendFullBillingInfo(request, context);  // add in all address info
        appendItemLineInfo(request, context);     // add in the item info
        return request;
    }

    private static void appendFullBillingInfo(Map request, Map context) {
        // person info
        GenericValue person = (GenericValue) context.get("contactPerson");
        request.put("billTo_firstName", person.getString("firstName"));
        request.put("billTo_lastName", person.getString("lastName"));

        // contact info
        GenericValue email = (GenericValue) context.get("contactEmail");
        request.put("billTo_email", email.getString("infoString"));

        // phone number seems to not be used; possibly only for reporting.

        // payment Info
        GenericValue creditCard = (GenericValue) context.get("creditCard");
        List expDateList = StringUtil.split(creditCard.getString("expireDate"), "/");

        request.put("card_accountNumber", creditCard.getString("cardNumber"));
        request.put("card_expirationMonth", (String) expDateList.get(0));
        request.put("card_expirationYear", (String) expDateList.get(1));

        // payment contact info
        GenericValue billingAddress = (GenericValue) context.get("billingAddress");

        request.put("billTo_street1", billingAddress.getString("address1"));
        if (billingAddress.get("address2") != null) {
            request.put("billTo_street2", billingAddress.getString("address2"));
        }
        request.put("billTo_city", billingAddress.getString("city"));
        String bCountry = billingAddress.get("countryGeoId") != null ? billingAddress.getString("countryGeoId") : "USA";

        request.put("billTo_country", bCountry);
        request.put("billTo_postalCode", billingAddress.getString("postalCode"));
        if (billingAddress.get("stateProvinceGeoId") != null) {
            request.put("billTo_state", billingAddress.getString("stateProvinceGeoId"));
        }

        // order shipping information
        GenericValue shippingAddress = (GenericValue) context.get("shippingAddress");

        // TODO: add first/last name
        request.put("shipTo_street1", shippingAddress.getString("address1"));
        if (shippingAddress.get("address2") != null) {
            request.put("shipTo_street2", shippingAddress.getString("address2"));
        }
        request.put("shipTo_city", shippingAddress.getString("city"));
        String sCountry = shippingAddress.get("countryGeoId") != null ? shippingAddress.getString("countryGeoId") : "USA";

        request.put("shipTo_country", sCountry);
        request.put("shipTo_postalCode", shippingAddress.getString("postalCode"));
        if (shippingAddress.get("stateProvinceGeoId") != null) {
            request.put("shipTo_state", shippingAddress.getString("stateProvinceGeoId"));
        }
    }

    private static void appendItemLineInfo(Map request, Map context) {
        // send over a line item total offer w/ the total for billing; don't trust CyberSource for calc.
        Double processAmount = (Double) context.get("processAmount");
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setGroupingUsed(false);
        String totalStr = nf.format(processAmount);

        int lineNumber = 0;
        request.put("item_" + lineNumber + "_unitPrice", totalStr.substring(1));

        // create the offers (one for each line item)
        List orderItems = (List) context.get("orderItems");
        Iterator itemIterator = orderItems.iterator();

        while (itemIterator.hasNext()) {
            lineNumber++;
            GenericValue item = (GenericValue) itemIterator.next();
            GenericValue product = null;
            try {
                product = item.getRelatedOne("Product");
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get Product from OrderItem, not passing info to CyberSource");
            }

            if (product != null) {
                request.put("item_" + lineNumber + "_productName", product.getString("productName"));
                request.put("item_" + lineNumber + "_productSKU", product.getString("productId"));
            } else {
                // no product; just send the item description -- non product items
                request.put("item_" + lineNumber + "_productName", item.getString("description"));
            }

            // get the quantity..
            Double quantity = item.getDouble("quantity");

            // test quantity if INT pass as is; if not pass as 1
            long roundQ = Math.round(quantity.doubleValue());
            Double rounded = new Double(new Long(roundQ).toString());

            if (rounded.doubleValue() != quantity.doubleValue()) {
                request.put("item_" + lineNumber + "_quantity", "1");
            } else {
                request.put("", new Integer(quantity.intValue()).toString());
            }

            // set the amount to 0.0000 -- we will send a total too.
            request.put("item_" + lineNumber + "_unitPrice", "0.0000");
        }
    }

    private static void processAuthResult(Map reply, Map result) {
        String decision = (String) reply.get("decision");
        if ("ACCEPTED".equalsIgnoreCase(decision)) {
            result.put("authCode", reply.get("ccAuthReply_authorizationCode"));
            result.put("authResult", new Boolean(true));
        } else {
            result.put("authCode", decision);
            result.put("authResult", new Boolean(false));
        }

        if (reply.get("ccAuthReply_amount") != null) {
            result.put("processAmount", new Double((String) reply.get("ccAuthReply_amount")));
        } else {
            result.put("processAmount", new Double(0.00));
        }

        result.put("authRefNum", reply.get("requestID"));
        result.put("authFlag", reply.get("ccAuthReply_reasonCode"));
        result.put("authMessage", reply.get("ccAuthReply_processorResponse"));
        result.put("avsCode", reply.get("ccAuthReply_avsCode"));
        result.put("scoreCode", reply.get("ccAuthReply_authFactorCode"));
    }
}
