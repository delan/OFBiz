/*
 * $Id: PayflowPro.java,v 1.3 2004/07/01 07:57:55 jonesde Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting.thirdparty.verisign;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.collections.OrderedMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.Verisign.payment.PFProAPI;

/**
 * PayflowPro - Verisign PayFlow Pro <=> OFBiz Service Module
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision: 1.3 $
 * @since      2.0
 */
public class PayflowPro {

    public static final String module = PayflowPro.class.getName();

    /**
     * Authorize credit card payment service. Service wrapper around PayFlow Pro API.
     * @param dctx Service Engine DispatchContext.
     * @param context Map context of parameters.
     * @return Response map, including RESPMSG, and RESULT keys.
     */
    public static Map ccProcessor(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        String orderId = (String) context.get("orderId");
        Double processAmount = (Double) context.get("processAmount");
        GenericValue cc = (GenericValue) context.get("creditCard");
        GenericValue ps = (GenericValue) context.get("billingAddress");
        String configString = (String) context.get("paymentConfig");
        if (configString == null)
            configString = "payment.properties";

        Map data = UtilMisc.toMap("COMMENT1", orderId);

        if (UtilProperties.propertyValueEqualsIgnoreCase(configString, "payment.verisign.preAuth", "Y"))
            data.put("TRXTYPE", "A");
        else
            data.put("TRXTYPE", "S");
        data.put("TENDER", "C");

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String totalStr = nf.format(processAmount);

        if (Debug.verboseOn()) Debug.logVerbose("Charging amount: " + totalStr, module);
        if (totalStr != null) {
            data.put("AMT", totalStr.substring(1));
        }

        // get the payment information
        data.put("ACCT", cc.getString("cardNumber"));
        data.put("COMMENT2", cc.getString("firstNameOnCard") + " " + cc.getString("lastNameOnCard"));
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
               
        PFProAPI pn = init(configString);

        // get the base params
        StringBuffer params = makeBaseParams(configString);

        // parse the context parameters
        params.append("&" + parseContext(data));

        // transmit the request
        if (Debug.verboseOn()) Debug.logVerbose("Sending to Verisign: " + params.toString(), module);
        String resp = pn.SubmitTransaction(params.toString());

        if (Debug.verboseOn()) Debug.logVerbose("Response from Verisign: " + resp, module);

        // reset for next use
        pn.DestroyContext();

        // check the response
        parseResponse(resp, result, configString);
        result.put("processAmount", processAmount);
        return result;
    }

    private static void parseResponse(String resp, Map result, String resource) {
        boolean checkAVS = UtilProperties.propertyValueEqualsIgnoreCase(resource, "payment.verisign.checkAvs", "Y");
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

        if (respCode.equals("0")) {
            result.put("authResult", new Boolean(true));
            result.put("authCode", parameters.get("AUTHCODE"));
        } else {
            result.put("authResult", new Boolean(false));
        }
        result.put("authRefNum", parameters.get("PNREF"));
        result.put("authFlag", parameters.get("RESULT"));
        result.put("authMessage", parameters.get("RESPMSG"));
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

    private static StringBuffer makeBaseParams(String resource) {
        StringBuffer buf = new StringBuffer();

        try {
            buf.append("PARTNER=");
            buf.append(UtilProperties.getPropertyValue(resource, "payment.verisign.partner", "VeriSign"));
            buf.append("&");
            buf.append("VENDOR=");
            buf.append(UtilProperties.getPropertyValue(resource, "payment.verisign.vendor", "nobody"));
            buf.append("&");
            buf.append("USER=");
            buf.append(UtilProperties.getPropertyValue(resource, "payment.verisign.user", "nobody"));
            buf.append("&");
            buf.append("PWD=");
            buf.append(UtilProperties.getPropertyValue(resource, "payment.verisign.pwd", "password"));
        } catch (Exception e) {
            return null;
        }
        return buf;
    }

    private static PFProAPI init(String resource) {                                           
        String certsPath = UtilProperties.getPropertyValue(resource, "payment.verisign.certsPath", "certs");                        
        String hostAddress = UtilProperties.getPropertyValue(resource, "payment.verisign.hostAddress", "test-payflow.verisign.com");
        Integer hostPort = Integer.decode(UtilProperties.getPropertyValue(resource, "payment.verisign.hostPort", "443"));
        Integer timeout = Integer.decode(UtilProperties.getPropertyValue(resource, "payment.verisign.timeout", "80"));
        String proxyAddress = UtilProperties.getPropertyValue(resource, "payment.verisign.proxyAddress", "");
        Integer proxyPort = Integer.decode(UtilProperties.getPropertyValue(resource, "payment.verisign.proxyPort", "80"));
        String proxyLogon = UtilProperties.getPropertyValue(resource, "payment.verisign.proxyLogon", "");
        String proxyPassword = UtilProperties.getPropertyValue(resource, "payment.verisign.proxyPassword", "");                                    

        PFProAPI pn = new PFProAPI();

        // Set the certificate path
        pn.SetCertPath(certsPath);
        // Call the client.
        pn.CreateContext(hostAddress, hostPort.intValue(), timeout.intValue(), proxyAddress, proxyPort.intValue(), proxyLogon, proxyPassword);                                                                       
        return pn;
    }
}

