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

import org.ofbiz.ecommerce.shoppingcart.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

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
     * Authorize event; called to process credit cards from inside a chain.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String authorizeCard(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        if (cart == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No shopping cart found with this transaction.");
            return "error";
        }
        String orderId = (String) request.getAttribute("order_id");
        if (orderId == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>No order number found with this transaction.");
            return "error";
        }

        String successStatus = (String) request.getAttribute("cc_success_status");
        if (successStatus == null) successStatus = "PAID";

        // set the credit card sale info
        Map context = UtilMisc.toMap("TRXTYPE", "S", "TENDER", "C", "COMMENT1", orderId);

        // get the cart total
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String totalStr = nf.format(cart.getGrandTotal());
        if (totalStr != null) {
            Double total = Double.valueOf(totalStr.substring(1));
            context.put("AMT", total);
        }

        try {
            // gather the card info
            GenericValue pm = cart.getPaymentMethod(delegator);
            GenericValue cc = pm.getRelatedOne("CreditCard");
            context.put("ACCT", cc.getString("cardNumber"));
            context.put("COMMENT2", cc.getString("nameOnCard"));
            if (cc.get("expireDate") != null) {
                String exp = cc.getString("expireDate");
                String expDate = exp.substring(0,2);
                expDate = expDate + exp.substring(exp.length()-2);
                context.put("EXPDATE", expDate);
            }

            Debug.logVerbose("Credit Card Object: " + cc, module);

            // gather the address info
            GenericValue ps = cc.getRelatedOne("PostalAddress");
            if (ps != null) {
                String street = ps.getString("address1") +
                        (ps.get("address2") != null && ps.getString("address2").length() > 0 ? " " +
                        ps.getString("address2") : "");
                context.put("STREET", street);
                context.put("ZIP", ps.getString("postalCode"));
            }
        } catch (GenericEntityException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Entity exception occured: " + e.getMessage());
            return "error";
        }

        Map result = null;
        try {
            result = dispatcher.runSync("creditCardAuthorize", context);
        } catch (GenericServiceException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems invoking authorization service: " + e.getMessage());
            return "error";
        }

        String respCode = (String) result.get("RESULT");
        if (!respCode.equals("0")) {
            String respMessage = (String) result.get("RESPMSG");
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Authorization failed: " + respMessage);
            return "error";
        }

        result = null;
        try {
            result = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("orderId", orderId, "statusId",  successStatus));
            if (result.containsKey("errorMessage")) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems adjusting order status please contact customer service: " + result.get("errorMessage"));
                return "error";
            }
        } catch (GenericServiceException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems adjusting order status, please contact customer service: " + e.getMessage());
            return "error";
        }

        return "success";
    }

    /**
     * Authorize payment service. Service wrapper around PayFlow Pro API.
     * @param dctx Service Engine DispatchContext.
     * @param context Map context of parameters.
     * @return Response map, including RESPMSG, and RESULT keys.
     */
    public static Map authorizePayment(DispatchContext dctx, Map context) {
        PFProAPI pn = init();

        // get the base params
        StringBuffer params = makeBaseParams();

        // parse the context parameters
        params.append("&" + parseContext(context));

        // transmit the request
        String resp = pn.SubmitTransaction(params.toString());

        // reset for next use
        pn.DestroyContext();

        // return the parsed response
        return parseResponse(resp);
    }

    private static Map parseResponse(String resp) {
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
                    parameters.put(k,v);
            }
        }
        return parameters;
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
            buf.append(UtilProperties.getPropertyValue("pfpro", "partner", "VeriSign"));
            buf.append("&");
            buf.append("VENDOR=");
            buf.append(UtilProperties.getPropertyValue("pfpro", "vendor", "nobody"));
            buf.append("&");
            buf.append("USER=");
            buf.append(UtilProperties.getPropertyValue("pfpro", "user", "nobody"));
            buf.append("&");
            buf.append("PWD=");
            buf.append(UtilProperties.getPropertyValue("pfpro", "pwd", "password"));
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
            certsPath = UtilProperties.getPropertyValue("pfpro", "certsPath", "certs");
            hostAddress = UtilProperties.getPropertyValue("pfpro", "hostAddress", "test-payflow.verisign.com");
            hostPort = Integer.decode(UtilProperties.getPropertyValue("pfpro", "hostPort", "443"));
            timeout = Integer.decode(UtilProperties.getPropertyValue("pfpro", "timeout", "80"));
            proxyAddress = UtilProperties.getPropertyValue("pfpro", "proxyAddress", "");
            proxyPort = Integer.decode(UtilProperties.getPropertyValue("pfpro", "proxyPort", "80"));
            proxyLogon = UtilProperties.getPropertyValue("pfpro", "proxyLogon", "");
            proxyPassword = UtilProperties.getPropertyValue("pfpro", "proxyPassword", "");
        } catch (Exception e) {
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

