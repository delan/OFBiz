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
package org.ofbiz.commonapp.order.order;

import java.net.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.workflow.*;
import org.ofbiz.core.workflow.client.*;
import org.ofbiz.core.util.*;

/**
 * Order Helper - Helper Methods For Non-Read Actions
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class OrderChangeHelper {
    
    public static final String module = OrderChangeHelper.class.getName();
    
    public static boolean approveOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, URL orderPropertiesUrl) {        
        // get some payment related strings from order.properties.
        final String HEADER_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.approved.status", "ORDER_APPROVED");
        final String ITEM_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.approved.status", "ITEM_APPROVED");
        
        try {
            OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, ITEM_STATUS);                                                                                                                                       
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }
        return true;
    }    
    
    public static boolean rejectOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, URL orderPropertiesUrl) {        
        // get some payment related strings from order.properties.
        final String HEADER_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.declined.status", "ORDER_REJECTED");
        final String ITEM_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.declined.status", "ITEM_REJECTED");
        
        try {
            OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, ITEM_STATUS);
            OrderChangeHelper.cancelInventoryReservations(dispatcher, userLogin, orderId);                                                                                                                    
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }
        return true;
    }        
    
    public static boolean cancelOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, URL orderPropertiesUrl) {
        // get some payment related strings from order.properties.
        final String HEADER_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.cancelled.status", "ORDER_REJECTED");
        final String ITEM_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.cancelled.status", "ITEM_REJECTED");
        
        try {
            OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, ITEM_STATUS);
            OrderChangeHelper.cancelInventoryReservations(dispatcher, userLogin, orderId);                                                                                                                    
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }
        return true;
    }  
    
    public static void orderStatusChanges(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, String orderStatusId, String itemStatusId) throws GenericServiceException {                             
        // set the status on the order header
        Map statusFields = UtilMisc.toMap("orderId", orderId, "statusId", orderStatusId, "userLogin", userLogin);
        Map statusResult = dispatcher.runSync("changeOrderStatus", statusFields);                               
        if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
            Debug.logError("Problems adjusting order header status for order #" + orderId, module);                            
        }
                        
        // set the status on the order item(s)
        Map itemStatusFields = UtilMisc.toMap("orderId", orderId, "statusId", itemStatusId, "userLogin", userLogin);
        Map itemStatusResult = dispatcher.runSync("changeOrderItemStatus", itemStatusFields);                        
        if (itemStatusResult.containsKey(ModelService.ERROR_MESSAGE)) {
            Debug.logError("Problems adjusting order item status for order #" + orderId, module);
        }                                                                                                                        
    } 
    
    public static void cancelInventoryReservations(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) throws GenericServiceException {
        // cancel the inventory reservations
        Map cancelInvFields = UtilMisc.toMap("orderId", orderId, "userLogin", userLogin);
        Map cancelInvResult = dispatcher.runSync("cancelOrderInventoryReservation", cancelInvFields);
        if (ModelService.RESPOND_ERROR.equals((String) cancelInvResult.get(ModelService.RESPONSE_MESSAGE))) {
            Debug.logError("Problems reversing inventory reservations for order #" + orderId, module);
        }                         
    }
    
    public static GenericValue createPaymentFromPreference(GenericValue orderPaymentPreference, String paymentRefNumber, String paymentFromId, String comments) {        
        GenericDelegator delegator = orderPaymentPreference.getDelegator();
        
        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = orderPaymentPreference.getRelatedOne("OrderHeader");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderHeader from payment preference", module);
        }
        
        // get the website payment settings for the order
        GenericValue webSitePaymentSettings = null;
        String paymentConfig = null;
        if (orderHeader != null) {
            try {
                webSitePaymentSettings = delegator.findByPrimaryKey("WebSitePaymentSetting", 
                    UtilMisc.toMap("webSiteId", orderHeader.getString("webSiteId"), 
                        "paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId")));
                if (webSitePaymentSettings != null)
                    paymentConfig = webSitePaymentSettings.getString("paymentConfiguration");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get the WebSitePaymentSetting for the order header", module);
            }
        } else {
            Debug.logWarning("No order header, cannot create payment", module);
            return null;
        }
        
        // set the default payment config
        if (paymentConfig == null) paymentConfig = "payment.properties";
        String payToPartyId = UtilProperties.getPropertyValue(paymentConfig, "payment.general.payTo", "Company");
        
        // create the payment
        Long payId = delegator.getNextSeqId("Payment");
        GenericValue payment = delegator.makeValue("Payment", UtilMisc.toMap("paymentId", payId.toString()));
        payment.set("paymentTypeId", "RECEIPT");
        payment.set("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
        payment.set("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
        payment.set("amount", orderPaymentPreference.getDouble("maxAmount"));
        payment.set("effectiveDate", UtilDateTime.nowTimestamp());        
        payment.set("partyIdTo", payToPartyId); 
        if (paymentRefNumber != null) { 
            payment.set("paymentRefNum", paymentRefNumber);
        } else {
            payment.set("paymentRefNum", orderPaymentPreference.getString("authRefNum"));
        }
        if (paymentFromId != null) {
            payment.set("partyIdFrom", paymentFromId);
        } else {
            payment.set("partyIdFrom", "_NA_"); 
        }
        if (comments != null) {
            payment.set("comments", comments);        
        }        
             
        return payment;
    }

    public static boolean relaeaseOfflineOrderHold(LocalDispatcher dispatcher, String orderId) {
        // get the delegator from the dispatcher
        GenericDelegator delegator = dispatcher.getDelegator();
        
        // find the workEffortId for this order
        GenericValue workEffort = null;
        try {
            List workEfforts = delegator.findByAnd("WorkEffort", UtilMisc.toMap("sourceReferenceId", orderId, "currentStatusId", "WF_SUSPENDED"));
            if (workEfforts != null && workEfforts.size() > 0) {
                Debug.logWarning("More then order suspended activity with order ref number: " + orderId, module);                
            }
            workEffort = EntityUtil.getFirst(workEfforts);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting WorkEffort with order ref number: " + orderId, module);
            return false;
        }        
                
        if (workEffort != null) {
            // get the order header
            String currentStatusId = null;
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems getting the order header for: " + orderId, module);
                return false;
            }
            // get the current order status to pass into the workflow
            if (orderHeader != null) {
                currentStatusId = orderHeader.getString("statusId");
            } else {
                Debug.logWarning("OrderHeader is empty, cannot continue", module);
                return false;
            }
            // attempt to release the order workflow from 'Hold' status (resume workflow)                        
            String workEffortId = workEffort.getString("workEffortId");
            try {            
                WorkflowClient client = new WorkflowClient(dispatcher.getDispatchContext());
                // first send the new order status to the workflow
                client.appendContext(workEffortId, UtilMisc.toMap("orderStatusId", currentStatusId));
                // next resume the activity
                client.resume(workEffortId);                 
            } catch (WfException e) {
                Debug.logError(e, "Problem resuming workflow", module);      
                return false;                 
            }
            return true;
        } 
        return false;               
    }                   
}
