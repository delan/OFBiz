/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.ordermgr;

import java.net.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.order.*;

/**
 * Order Manager Events
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class OrderManagerEvents {
    
    public static final String module = OrderManagerEvents.class.getName();
        
    public static String processOfflinePayments(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");       
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);


        // load the order.properties file.
        URL orderPropertiesUrl = null;
        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }
                
        // get some payment related strings from order.properties.
        final String HEADER_APPROVE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.approved.status", "ORDER_APPROVED");
        final String ITEM_APPROVE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.approved.status", "ITEM_APPROVED");
             
        if (session.getAttribute("OFFLINE_PAYMENTS") != null) {
            String orderId = (String) request.getAttribute("order_id");
            List toBeStored = new LinkedList();                     
            List paymentPrefs = null;
            GenericValue placingCustomer = null;
            try {                
                paymentPrefs = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId));
                List pRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
                if (pRoles != null && pRoles.size() > 0)
                    placingCustomer = EntityUtil.getFirst(pRoles);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems looking up order payment preferences", module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Error processing offline payments.");
                return "error";            
            }
            if (paymentPrefs != null) {
                Iterator i = paymentPrefs.iterator();                
                while (i.hasNext()) {
                    // update the preference to received
                    GenericValue ppref = (GenericValue) i.next();
                    ppref.set("statusId", "PAYMENT_RECEIVED");
                    ppref.set("authDate", UtilDateTime.nowTimestamp());
                    toBeStored.add(ppref);
                    
                    // create a payment record
                    toBeStored.add(OrderChangeHelper.createPaymentFromPreference(ppref, null, placingCustomer.getString("partyId"), "Payment received offline and manually entered."));                                  
                }
                
                // store the updated preferences and newly created payments
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems storing payment information", module);
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problem storing received payment information.");
                    return "error";
                }
                
                // set the status of the order to approved
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);             
            }
        }
        return "success";
    }
    
    public static String receiveOfflinePayment(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");       
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);

        // load the order.properties file.
        URL orderPropertiesUrl = null;
        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }
                
        String orderId = request.getParameter("orderId");
        String workEffortId = request.getParameter("workEffortId");
        
        // get the order header & payment preferences
        GenericValue orderHeader = null;
        List currentPaymentPrefs = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));  
            currentPaymentPrefs = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toList(new EntityExpr("orderId", EntityOperator.EQUALS, orderId), new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")));      
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems reading order header from datasource.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems reading order header information.");
            return "error";
        }
        
        OrderReadHelper orh = null;
        if (orderHeader != null)
            orh = new OrderReadHelper(orderHeader);
        double grandTotal = orh.getOrderGrandTotal();
            
        // get the payment types to receive
        List paymentMethodTypes = null;
        
        try {
            List pmtFields = UtilMisc.toList(new EntityExpr("paymentMethodTypeId", EntityOperator.NOT_EQUAL, "EXT_OFFLINE"));
            paymentMethodTypes = delegator.findByAnd("PaymentMethodType", pmtFields);                 
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting payment types", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems with PaymentType lookup.");
            return "error";
        }
        
        if (paymentMethodTypes == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems with PaymentType lookup.");
            return "error";
        }
                
        List toBeStored = new LinkedList();
        GenericValue placingCustomer = null;
        try {                            
            List pRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
            if (pRoles != null && pRoles.size() > 0)
                placingCustomer = EntityUtil.getFirst(pRoles);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up order payment preferences", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Error processing offline payments.");
            return "error";            
        }        
        
        Iterator pmti = paymentMethodTypes.iterator();
        double paymentTally = 0.00;
        while (pmti.hasNext()) {
            GenericValue paymentMethodType = (GenericValue) pmti.next();
            String paymentMethodTypeId = paymentMethodType.getString("paymentMethodTypeId");
            String amountStr = request.getParameter(paymentMethodTypeId + "_amount");
            String paymentReference = request.getParameter(paymentMethodTypeId + "_reference");
            if (!UtilValidate.isEmpty(amountStr)) {
                double paymentTypeAmount = 0.00;
                try {                                                                                
                    paymentTypeAmount = NumberFormat.getNumberInstance().parse(amountStr).doubleValue();                                                           
                } catch (java.text.ParseException pe) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems payment parsing amount.");
                    return "error";
                }
                if (paymentTypeAmount > 0.00) {
                    paymentTally += paymentTypeAmount;
                    java.sql.Timestamp now = UtilDateTime.nowTimestamp();
                    
                    // create the OrderPaymentPreference
                    Map prefFields = UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString());
                    GenericValue paymentPreference = delegator.makeValue("OrderPaymentPreference", prefFields);
                    paymentPreference.set("paymentMethodTypeId", paymentMethodType.getString("paymentMethodTypeId"));
                    paymentPreference.set("maxAmount", new Double(paymentTypeAmount));                    
                    paymentPreference.set("statusId", "PAYMENT_RECEIVED");
                    paymentPreference.set("authRefNum", paymentReference);
                    paymentPreference.set("authDate", now);
                    paymentPreference.set("orderId", orderId);
                    toBeStored.add(paymentPreference);
                    
                    // create a payment record
                    toBeStored.add(OrderChangeHelper.createPaymentFromPreference(paymentPreference, null, placingCustomer.getString("partyId"), "Payment received offline and manually entered."));                               
                }
            }
        }
                                                                      
        // now finish up
        if (paymentTally == grandTotal) {
            // cancel the old payment preferences
            if (currentPaymentPrefs != null && currentPaymentPrefs.size() > 0) {
                Iterator cppi = currentPaymentPrefs.iterator();
                while (cppi.hasNext()) {
                    GenericValue ppf = (GenericValue) cppi.next();
                    ppf.set("statusId", "PAYMENT_CANCELLED");
                    toBeStored.add(ppf);
                }
            }
            
            // store the status changes and the newly created payment preferences and payments
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems storing payment information", module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problem storing received payment information.");
                return "error";
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>The payment amount(s) do not match the order total.");
            return "error";
        }
                
        // update the status of the order and items
        OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
        
        // attempt to release the order workflow from 'Hold' status (resume workflow)
        OrderChangeHelper.relaeaseOfflineOrderHold(dispatcher, orderId);
                    
        return "success";
    }    

}
