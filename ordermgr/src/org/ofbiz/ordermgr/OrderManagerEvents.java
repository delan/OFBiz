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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Order Manager Events
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class OrderManagerEvents {
    
    public static final String module = OrderManagerEvents.class.getName();
    
    public static String checkOfflinePayments(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (session.getAttribute("OFFLINE_PAYMENTS") == null) 
            return "false";
        return "true";
    }
    
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
                    toBeStored.add(ppref);
                    
                    // create a payment record
                    Map payFields = UtilMisc.toMap("paymentId", delegator.getNextSeqId("Payment").toString());
                    GenericValue payment = delegator.makeValue("Payment", payFields);
                    payment.set("paymentTypeId", "RECEIPT");
                    payment.set("paymentMethodTypeId", ppref.getString("paymentMethodTypeId"));
                    payment.set("paymentPreferenceId", ppref.getString("orderPaymentPreferenceId"));
                    payment.set("amount", ppref.getDouble("maxAmount"));
                    payment.set("effectiveDate", UtilDateTime.nowTimestamp());
                    payment.set("comments", "Payment received offline and manually entered.");
                    payment.set("partyIdTo", "Company"); // change this to be dynamic
                    if (placingCustomer != null) {
                        payment.set("partyIdFrom", placingCustomer.getString("partyId"));
                    } else {
                        payment.set("partyIdFrom", "_NA_"); 
                    }
                    toBeStored.add(payment);                    
                }
                
                // store the updated preferences and newly created payments
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems storing payment information", module);
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problem storing received payment information.");
                    return "error";
                }
                
                // update the status of the order and items
                try {
                    // set the status on the order header
                    Map statusFields = UtilMisc.toMap("orderId", orderId, "statusId", HEADER_APPROVE_STATUS, "userLogin", userLogin);
                    Map statusResult = dispatcher.runSync("changeOrderStatus", statusFields);                               
                    if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
                        Debug.logError("Problem adjust OrderHeader status : " + statusResult.get(ModelService.ERROR_MESSAGE), module);
                        request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems adjusting the order status.");
                        return "error";                                                                       
                    }
                        
                    // set the status on the order item(s)
                    Map itemStatusFields = UtilMisc.toMap("orderId", orderId, "statusId", ITEM_APPROVE_STATUS, "userLogin", userLogin);
                    Map itemStatusResult = dispatcher.runSync("changeOrderItemStatus", itemStatusFields);                        
                    if (itemStatusResult.containsKey(ModelService.ERROR_MESSAGE)) {
                        Debug.logError("Problem adjust OrderItem status : " + itemStatusResult.get(ModelService.ERROR_MESSAGE), module);
                        request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems adjusting the order status.");
                        return "error";
                    }                                                                                                                                                      
               } catch (GenericServiceException e) {
                   Debug.logError(e, "Service invocation error on changing order/item status", module);
                   request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems adjusting the order status.");
                   return "error";                  
               }
            }
        }
        return "success";
    }
    
    

}
