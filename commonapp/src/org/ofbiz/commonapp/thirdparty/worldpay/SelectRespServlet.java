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
package org.ofbiz.commonapp.thirdparty.worldpay;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.order.*;
import org.ofbiz.commonapp.order.shoppingcart.*;

import com.worldpay.select.*;
import com.worldpay.select.merchant.*;

/**
 * WorldPay Select Pro Response Servlet
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class SelectRespServlet extends SelectServlet implements SelectDefs {
    
    public static final String module = SelectRespServlet.class.getName();
    
    private ServletContext sctx = null;
    private GenericDelegator delegator = null;
    private LocalDispatcher dispatcher = null;
    private URL orderPropertiesUrl = null;
    private GenericValue userLogin = null;   
    
    protected void doRequest(SelectServletRequest request, SelectServletResponse response) throws ServletException, IOException {
        Debug.logInfo("Request receive from worldpay..", module);
        
        String orderPropertiesString = request.getParameter("M_orderProperties");
        String webSiteId = request.getParameter("M_webSiteId");
        String delegatorName = request.getParameter("M_delegatorName");
        String dispatchName = request.getParameter("M_dispatchName");
        String userLoginId = request.getParameter("M_userLoginId");
                     
        // get the delegator
        delegator = GenericDelegator.getGenericDelegator(delegatorName);
        
        // get the dispatcher
        ServiceDispatcher serviceDisp = ServiceDispatcher.getInstance(dispatchName, delegator);
        DispatchContext dctx = serviceDisp.getLocalContext(dispatchName);
        dispatcher = dctx.getDispatcher();  
        
        // get the userLogin
        try {
            userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));      
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get admin UserLogin entity", module);
            callError(request);
        }
                
        // get the properties file       
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
        Debug.logInfo("Got the payment configuration", module);    
        
        String orderId = request.getParameter(SelectDefs.SEL_cartId);
        String authAmount = request.getParameter(SelectDefs.SEL_authAmount);
        String transStatus = request.getParameter(SelectDefs.SEL_transStatus);
        
        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get the order header for the returned orderId", module);
            callError(request);
        }
        
        // the order total MUST match the auth amount or we do not process
        Double wpTotal = new Double(authAmount);
        Double orderTotal = orderHeader != null ? orderHeader.getDouble("grandTotal") : null;
        if (orderTotal != null && wpTotal != null) {
            if (orderTotal.doubleValue() != wpTotal.doubleValue())
                callError(request);
        }
        
        // store some stuff for calling existing events
        HttpSession session = request.getSession(true);
        session.setAttribute(SiteDefs.USER_LOGIN, userLogin);
        
        request.setAttribute("delegator", delegator);
        request.setAttribute("dispatcher", dispatcher);
        request.setAttribute("order_id", orderId);
        request.setAttribute("orderProperties", request.getParameter("M_orderProperties"));
        request.setAttribute("ecommerceProperties", request.getParameter("M_ecommerceProperties"));
        request.setAttribute(SiteDefs.CONTROL_PATH, request.getParameter("M_controlPath"));
        
        // load the order.properties file.        
        try {
            orderPropertiesUrl = new URL(orderPropertiesString);
        } catch (MalformedURLException e) {
            Debug.logWarning(e, "Problems loading order.properties", module);
        }    
        
        // attempt to start a transaction
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
        } catch (GenericTransactionException gte) {
            Debug.logError(gte, "Unable to begin transaction", module);
        }                
        
        boolean okay = false;
        if (transStatus.equalsIgnoreCase("Y")) {
            // order was approved
            Debug.logInfo("Order #" + orderId + " approved", module);
            okay = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);                  
        } else {
            // order was cancelled
            Debug.logInfo("Order #" + orderId + " cancelled", module);
            okay = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
        }
        
        if (okay) {        
            // set the payment preference
            okay = setPaymentPreferences(orderId, request);
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
        OrderChangeHelper.relaeaseOfflineOrderHold(dispatcher, orderId); 
                
        // call the existing confirm order events (calling direct)
        String confirm = CheckOutEvents.renderConfirmOrder(request, response);
        String email = CheckOutEvents.emailOrder(request, response);
        
        // set up the output stream for the page
        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
        String content = (String) request.getAttribute("confirmorder");
        if (content != null)
            out.println(content); 
        else
            out.println("Error getting content");                                         
    }
               
    private boolean setPaymentPreferences(String orderId, ServletRequest request) {
        List paymentPrefs = null;
        boolean okay = true;
        try {
            Map paymentFields = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_RECEIVED");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment preferences for order #" + orderId, module);
        }
        if (paymentPrefs != null && paymentPrefs.size() > 0) {
            Iterator i = paymentPrefs.iterator();            
            while (okay && i.hasNext()) {
                GenericValue pref = (GenericValue) i.next();
                okay = setPaymentPreference(pref, request);
            }
        }
        return okay;
    }
        
    private boolean setPaymentPreference(GenericValue paymentPreference, ServletRequest request) {
        String transId = request.getParameter(SelectDefs.SEL_transId);       
        String transTime = request.getParameter(SelectDefs.SEL_transTime);
        String transStatus = request.getParameter(SelectDefs.SEL_transStatus);
        String avsCode = request.getParameter("AVS");  // why is this not in SelectDefs??
        String authCode = request.getParameter(SelectDefs.SEL_authCode);
        String authAmount = request.getParameter(SelectDefs.SEL_authAmount); 
        String rawAuthMessage = request.getParameter(SelectDefs.SEL_rawAuthMessage);
        
        if (transStatus.equalsIgnoreCase("Y")) {
            paymentPreference.set("authCode", authCode);
            paymentPreference.set("statusId", "PAYMENT_RECEIVED");
        } else {
            paymentPreference.set("statusId", "PAYMENT_CANCELLED");
        }
        Long transTimeLong = new Long(transTime);
        java.sql.Timestamp authDate = new java.sql.Timestamp(transTimeLong.longValue());
        
        paymentPreference.set("avsCode", avsCode);
        paymentPreference.set("authRefNum", transId);
        paymentPreference.set("authDate", authDate);
        paymentPreference.set("authFlag", transStatus);
        paymentPreference.set("authMessage", rawAuthMessage);
        paymentPreference.set("maxAmount", new Double(authAmount));
        
        // create a payment record too
        GenericValue payment = OrderChangeHelper.createPaymentFromPreference(paymentPreference, null, null, "Payment received via WorldPay");
        
        try {
            paymentPreference.store();
            payment.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot set payment preference/payment info", module);
            return false;
        } 
        return true;                  
    }  
    
    private void callError(ServletRequest request) throws ServletException {
        Enumeration e = request.getParameterNames();
        Debug.logError("###### SelectRespServlet Error:", module);
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = request.getParameter(name);
            Debug.logError("### Parameter: " + name + " => " + value, module);  
        }
        Debug.logError("###### The order was not processed!", module);
        throw new ServletException("Order Error");
    }
}
