/*
 * $Id$
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
package org.ofbiz.commonapp.thirdparty.paypal;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.product.catalog.*;
import org.ofbiz.commonapp.order.order.*;
import org.ofbiz.commonapp.order.shoppingcart.*;

/**
 * PayPal Events
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class PayPalEvents {
    
    public static final String module = PayPalEvents.class.getName();
    
    /** Initiate PayPal Request */
    public static String callPayPal(HttpServletRequest request, HttpServletResponse response) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN); 
                
        // get the orderId
        String orderId = (String) request.getAttribute("order_id");
        
        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get the order header for order: " + orderId, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting order header.");
            return "error";
        }
        
        // get the order total
        String orderTotal = UtilFormatOut.formatPrice(orderHeader.getDouble("grandTotal"));
            
        // get the webSiteId
        String webSiteId = CatalogWorker.getWebSiteId(request);
        
        // get the payment properties file
        String configString = null;
        try {
            GenericValue webSitePayment = delegator.findByPrimaryKey("WebSitePaymentSetting", UtilMisc.toMap("webSiteId", webSiteId, "paymentMethodTypeId", "EXT_WORLDPAY"));
            if (webSitePayment != null)
                configString = webSitePayment.getString("paymentConfiguration");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Cannot find webSitePayment Settings", module);
        }
        if (configString == null)
        configString = "payment.properties";
        
        // get the company name
        String company = UtilProperties.getPropertyValue(configString, "payment.general.company");
        
        // create the item name
        String itemName = "Order #" + orderId + (company != null ? " from " + company : "");
        String itemNumber = "0";
        
        // get the redirect url
        String redirectUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.redirect");
        
        // get the notify url
        String notifyUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.notify");
        
        // get the return urls
        String returnUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.return");
        String cancelReturnUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.cancelReturn");
        
        // get the image url
        String imageUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.image");        
        
        // get the paypal account
        String payPalAccount = UtilProperties.getPropertyValue(configString, "payment.paypal.business");
                
        // create the redirect string
        Map parameters = new OrderedMap();
        parameters.put("cmd", "_xclick");
        parameters.put("business", payPalAccount);
        parameters.put("item_name", itemName);
        parameters.put("item_number", itemNumber);
        parameters.put("invoice", orderId);
        parameters.put("custom", userLogin.getString("userLoginId"));
        parameters.put("amount", orderTotal);        
        parameters.put("return", returnUrl);
        parameters.put("cancel_return", cancelReturnUrl);
        parameters.put("notify_url", notifyUrl);
        parameters.put("image_url", imageUrl);
        parameters.put("no_note", "1");        // no notes allowed in paypal (not passed back)
        parameters.put("no_shipping", "1");    // no shipping address required (local shipping used)
        
        HttpClient hclient = new HttpClient();
        String encodedParameters = hclient.encodeArgs(parameters);
        String redirectString = redirectUrl + "?" + encodedParameters;   
        
        // set the order in the session for cancelled orders
        request.getSession().setAttribute("PAYPAL_ORDER", orderId); 
        
        // redirect to paypal
        try {
            response.sendRedirect(redirectString);
        } catch (IOException e) {
            Debug.logError(e, "Problems redirecting to PayPal", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems connecting with PayPal, please contact customer service.");
            return "error";
        }
        
        return "success";   
    }
    
    /** PayPal Call-Back Event */
    public static String payPalIPN(HttpServletRequest request, HttpServletResponse response) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");   
        
        // get the webSiteId
        String webSiteId = CatalogWorker.getWebSiteId(request);
        
        // get the payment properties file
        String configString = null;
        try {
            GenericValue webSitePayment = delegator.findByPrimaryKey("WebSitePaymentSetting", UtilMisc.toMap("webSiteId", webSiteId, "paymentMethodTypeId", "EXT_PAYPAL"));
            if (webSitePayment != null)
                configString = webSitePayment.getString("paymentConfiguration");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Cannot find webSitePayment Settings", module);
        }
        if (configString == null)
        configString = "payment.properties";
               
        // get the confirm URL
        String confirmUrl = UtilProperties.getPropertyValue(configString, "payment.paypal.confirm");
        if (confirmUrl == null) {
            Debug.logError("Payment properties is not configured properly, no confirm URL defined!", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>PayPal has not been configured, please contact customer service.");
            return "error";       
        }
                
        // first verify this is valid from PayPal
        Map parametersMap = UtilMisc.getParameterMap(request);
        parametersMap.put("cmd", "_notify-validate");  
        
        // send off the confirm request
        HttpClient hclient = new HttpClient(confirmUrl, parametersMap);
        String confirmResp = null;
        try {
            confirmResp = hclient.post();
        } catch (HttpClientException e) {
            Debug.logError(e, "Problems connection to PayPal confirm URL", module);
            return "error";
        }
        
        if (confirmResp.trim().equals("VERIFIED")) {
            // we passed verification
            Debug.logInfo("Got verification from PayPal, processing..", module);
        } else {
            Debug.logError("###### PayPal did not verify this request, need investigation!", module);
            Debug.logError(" --- " + confirmResp, module);
            Set keySet = parametersMap.keySet();
            Iterator i = keySet.iterator();
            while (i.hasNext()) {
                String name = (String) i.next();
                String value = request.getParameter(name);
                Debug.logError("### Param: " + name + " => " + value, module);
            }
        }
        
        // get the user
        GenericValue userLogin = null;
        String userLoginId = request.getParameter("custom");
        if (userLoginId == null) 
            userLoginId = "admin";
        try {
            userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting authentication user.");
            return "error";
        }
        
        // get order.properties        
        URL orderPropertiesUrl = CheckOutEvents.getOrderProperties(request);   
                
        // get the orderId
        String orderId = request.getParameter("invoice");
        
        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get the order header for order: " + orderId, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting order header.");
            return "error";
        }
                        
        // get payment data
        String paymentCurrency = request.getParameter("mc_currency");
        String paymentAmount = request.getParameter("mc_gross");
        String paymentFee = request.getParameter("mc_fee");
        String transactionId = request.getParameter("txn_id");
                
        // get the transaction status
        String paymentStatus = request.getParameter("payment_status");        
        
        // attempt to start a transaction
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
        } catch (GenericTransactionException gte) {
            Debug.logError(gte, "Unable to begin transaction", module);
        }                        
                
        boolean okay = false;        
        if (paymentStatus.equals("Completed")) {
            okay = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);            
        } else if (paymentStatus.equals("Failed") || paymentStatus.equals("Denied")) {
            okay = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);            
        }
        
        if (okay) {                                     
            // set the payment preference
            okay = setPaymentPreferences(delegator, orderId, request);        
        }
        
        if (okay) {                
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException gte) {
                Debug.logError(gte, "Unable to commit transaction", module);
            }
        } else {
            try {
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericTransactionException gte) {
                Debug.logError(gte, "Unable to rollback transaction", module);
            }
        }
        
        // attempt to release the offline hold on the order (workflow)
        if (okay) 
            OrderChangeHelper.relaeaseOfflineOrderHold(dispatcher, orderId, orderPropertiesUrl);        
                                
        return "success";
    }
    
    private static boolean setPaymentPreferences(GenericDelegator delegator, String orderId, ServletRequest request) {
        List paymentPrefs = null;
        try {
            Map paymentFields = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_RECEIVED");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment preferences for order #" + orderId, module);
        }
        if (paymentPrefs != null && paymentPrefs.size() > 0) {
            Iterator i = paymentPrefs.iterator();
            while (i.hasNext()) {
                GenericValue pref = (GenericValue) i.next();
                setPaymentPreference(pref, request);
            }
        }
        return false;
    }  
    
    /** Event called when customer cancels a paypal order */
    public static String cancelPayPalOrder(HttpServletRequest request, HttpServletResponse response) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN); 
        
        // get the stored order id from the session
        String orderId = (String) request.getSession().getAttribute("PAYPAL_ORDER");
                
        // attempt to start a transaction
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
        } catch (GenericTransactionException gte) {
            Debug.logError(gte, "Unable to begin transaction", module);
        }   
        
        // get order.properties        
        URL orderPropertiesUrl = CheckOutEvents.getOrderProperties(request);   
                
        // cancel the order
        boolean okay = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
        
        if (okay) {                
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException gte) {
                Debug.logError(gte, "Unable to commit transaction", module);
            }
        } else {
            try {
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericTransactionException gte) {
                Debug.logError(gte, "Unable to rollback transaction", module);
            }
        }  
        
        // attempt to release the offline hold on the order (workflow)
        if (okay) 
            OrderChangeHelper.relaeaseOfflineOrderHold(dispatcher, orderId, orderPropertiesUrl);  
            
        request.setAttribute(SiteDefs.EVENT_MESSAGE, "<li>Previous PayPal order has been cancelled.");                                            
        return "success";        
    }    
    
    private static boolean setPaymentPreference(GenericValue paymentPreference, ServletRequest request) {
        String paymentDate = request.getParameter("payment_date");  
        String paymentType = request.getParameter("payment_type");      
        String paymentAmount = request.getParameter("mc_gross");    
        String paymentStatus = request.getParameter("payment_status");        
        String transactionId = request.getParameter("txn_id");
        
        // PayPal returns the timestamp in the format 'hh:mm:ss Jan 1, 2000 PST'
        // Parse this into a valid Timestamp Object
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss MMM d, yyyy z");
        java.sql.Timestamp authDate = null;
        try {        
            authDate = new java.sql.Timestamp(sdf.parse(paymentDate).getTime());
        } catch (ParseException e) {
            Debug.logError(e, "Canno parse date string: " + paymentDate, module);
            authDate = UtilDateTime.nowTimestamp();
        } catch (NullPointerException e) {
            Debug.logError(e, "Canno parse date string: " + paymentDate, module);
            authDate = UtilDateTime.nowTimestamp();
        }
        
        if (paymentStatus.equals("Completed")) {           
            paymentPreference.set("statusId", "PAYMENT_RECEIVED");
        } else {
            paymentPreference.set("statusId", "PAYMENT_CANCELLED");
        }        
                
        paymentPreference.set("authRefNum", transactionId);
        paymentPreference.set("authDate", authDate);
        paymentPreference.set("authFlag", paymentStatus.substring(0,1));
        paymentPreference.set("authMessage", paymentType);
        paymentPreference.set("maxAmount", new Double(paymentAmount));
                
        try {
            paymentPreference.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot set payment preference info", module);
            return false;
        } 
        return true;             
    }

}
