/*
 * $Id: OrderChangeHelper.java,v 1.12 2004/07/03 19:54:23 jonesde Exp $
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.order;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.workflow.WfException;
import org.ofbiz.workflow.client.WorkflowClient;

/**
 * Order Helper - Helper Methods For Non-Read Actions
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.12 $
 * @since      2.0
 */
public class OrderChangeHelper {
    
    public static final String module = OrderChangeHelper.class.getName();
    
    public static boolean approveOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) {        
        GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(dispatcher.getDelegator(), orderId);
        String HEADER_STATUS = "ORDER_PROCESSING";
        String ITEM_STATUS = "ITEM_CREATED";
        String DIGITAL_ITEM_STATUS = "ITEM_APPROVED";
        if (productStore.get("headerApprovedStatus") != null) {
            HEADER_STATUS = productStore.getString("headerApprovedStatus");
        }
        if (productStore.get("itemApprovedStatus") != null) {
            ITEM_STATUS = productStore.getString("itemApprovedStatus");
        }
        if (productStore.get("digitalItemApprovedStatus") != null) {
            DIGITAL_ITEM_STATUS = productStore.getString("digitalItemApprovedStatus");
        }
        
        try {
            OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, "ITEM_CREATED", ITEM_STATUS, DIGITAL_ITEM_STATUS);
            OrderChangeHelper.releaseInitialOrderHold(dispatcher, orderId);

            // call the service to check/run digial fulfillment
            Map checkDigi = dispatcher.runSync("checkDigitalItemFulfillment", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
            // this service will return a message with success if there were any problems. Get this message and return it to the user
            String message = (String) checkDigi.get(ModelService.SUCCESS_MESSAGE);
            if (UtilValidate.isNotEmpty(message)) {
                throw new GeneralRuntimeException(message);
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }
        
        return true;
    }    
    
    public static boolean rejectOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) {        
        GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(dispatcher.getDelegator(), orderId);
        String HEADER_STATUS = "ORDER_REJECTED";
        String ITEM_STATUS = "ITEM_REJECTED";
        if (productStore.get("headerDeclinedStatus") != null) {
              HEADER_STATUS = productStore.getString("headerDeclinedStatus");
          }
          if (productStore.get("itemDeclinedStatus") != null) {
              ITEM_STATUS = productStore.getString("itemDeclinedStatus");
          }        
        
        try {
            OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, null, ITEM_STATUS, null);
            OrderChangeHelper.cancelInventoryReservations(dispatcher, userLogin, orderId);
            OrderChangeHelper.releasePaymentAuthorizations(dispatcher, userLogin,orderId);
            OrderChangeHelper.releaseInitialOrderHold(dispatcher, orderId);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }
        return true;
    }        
    
    public static boolean cancelOrder(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) {
        GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(dispatcher.getDelegator(), orderId);
        String HEADER_STATUS = "ORDER_CANCELLED";
        String ITEM_STATUS = "ITEM_CANCELLED";
        if (productStore.get("headerCancelStatus") != null) {
              HEADER_STATUS = productStore.getString("headerCancelStatus");
          }
          if (productStore.get("itemCancelStatus") != null) {
              ITEM_STATUS = productStore.getString("itemCancelStatus");
          }                  
        
        try {
            OrderChangeHelper.orderStatusChanges(dispatcher, userLogin, orderId, HEADER_STATUS, null, ITEM_STATUS, null);
            OrderChangeHelper.cancelInventoryReservations(dispatcher, userLogin, orderId);
            OrderChangeHelper.releasePaymentAuthorizations(dispatcher, userLogin,orderId);
            OrderChangeHelper.releaseInitialOrderHold(dispatcher, orderId);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return false;
        }
        return true;
    }  
    
    public static void orderStatusChanges(LocalDispatcher dispatcher, GenericValue userLogin, String orderId, String orderStatus, String fromItemStatus, String toItemStatus, String digitalItemStatus) throws GenericServiceException {                             
        // set the status on the order header
        Map statusFields = UtilMisc.toMap("orderId", orderId, "statusId", orderStatus, "userLogin", userLogin);
        Map statusResult = dispatcher.runSync("changeOrderStatus", statusFields);                               
        if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
            Debug.logError("Problems adjusting order header status for order #" + orderId, module);                            
        }
                        
        // set the status on the order item(s)
        Map itemStatusFields = UtilMisc.toMap("orderId", orderId, "statusId", toItemStatus, "userLogin", userLogin);
        if (fromItemStatus != null) {
            itemStatusFields.put("fromStatusId", fromItemStatus);
        }
        Map itemStatusResult = dispatcher.runSync("changeOrderItemStatus", itemStatusFields);                        
        if (itemStatusResult.containsKey(ModelService.ERROR_MESSAGE)) {
            Debug.logError("Problems adjusting order item status for order #" + orderId, module);
        }
        
        // now set the status for digital items
        if (digitalItemStatus != null && !digitalItemStatus.equals(toItemStatus)) {
            GenericDelegator delegator = dispatcher.getDelegator();
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get OrderHeader for OrderID : " + orderId, module);
            }
            if (orderHeader != null) {
                List orderItems = null;
                try {
                    orderItems = orderHeader.getRelated("OrderItem");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Unable to get OrderItem records for OrderHeader : " + orderId, module);
                }
                if (orderItems != null && orderItems.size() > 0) {
                    Iterator oii = orderItems.iterator();
                    while (oii.hasNext()) {
                        GenericValue orderItem = (GenericValue) oii.next();
                        String orderItemSeqId = orderItem.getString("orderItemSeqId");
                        GenericValue product = null;
                        try {
                            product = orderItem.getRelatedOne("Product");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "ERROR: Unable to get Product record for OrderItem : " + orderId + "/" + orderItemSeqId, module);
                        }
                        if (product != null) {
                            String productType = product.getString("productTypeId");
                            if ("DIGITAL_GOOD".equals(productType) || "FINDIG_GOOD".equals(productType)) {
                                // update the status
                                Map digitalStatusFields = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "statusId", digitalItemStatus, "userLogin", userLogin);
                                Map digitalStatusChange = dispatcher.runSync("changeOrderItemStatus", digitalStatusFields);
                                if (ModelService.RESPOND_ERROR.equals(digitalStatusChange.get(ModelService.RESPONSE_MESSAGE))) {
                                    Debug.logError("Problems with digital product status change : " + product, module);
                                }
                            }
                        }
                    }
                }
            }
        }
    } 
    
    public static void cancelInventoryReservations(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) throws GenericServiceException {
        // cancel the inventory reservations
        Map cancelInvFields = UtilMisc.toMap("orderId", orderId, "userLogin", userLogin);
        Map cancelInvResult = dispatcher.runSync("cancelOrderInventoryReservation", cancelInvFields);
        if (ModelService.RESPOND_ERROR.equals(cancelInvResult.get(ModelService.RESPONSE_MESSAGE))) {
            Debug.logError("Problems reversing inventory reservations for order #" + orderId, module);
        }                         
    }

    public static void releasePaymentAuthorizations(LocalDispatcher dispatcher, GenericValue userLogin, String orderId) throws GenericServiceException {
        Map releaseFields = UtilMisc.toMap("orderId", orderId, "userLogin", userLogin);
        Map releaseResult = dispatcher.runSync("releaseOrderPayments", releaseFields);
        if (ModelService.RESPOND_ERROR.equals(releaseResult.get(ModelService.RESPONSE_MESSAGE))) {
            Debug.logError("Problems releasing payment authorizations for order #" + orderId, module);
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
        
        // get the store for the order
        GenericValue productStore = null;        
        if (orderHeader != null) {
            try {                
                productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", orderHeader.getString("productStoreId")));                                                   
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get the ProductStore for the order header", module);
            }
        } else {
            Debug.logWarning("No order header, cannot create payment", module);
            return null;
        }
        
        if (productStore == null) {
            Debug.logWarning("No product store, cannot create payment", module);
            return null;
        }
        
        // set the payToPartyId       
        String payToPartyId = productStore.getString("payToPartyId");
        if (payToPartyId == null) {
            Debug.logWarning("No payToPartyId set on ProductStore : " + productStore.getString("productStoreId"), module);
            return null;
        }
        
        // create the payment
        GenericValue payment = delegator.makeValue("Payment", UtilMisc.toMap("paymentId", delegator.getNextSeqId("Payment")));
        payment.set("paymentTypeId", "RECEIPT");
        payment.set("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
        payment.set("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
        payment.set("amount", orderPaymentPreference.getDouble("maxAmount"));
        payment.set("statusId", "PMNT_RECEIVED");
        payment.set("effectiveDate", UtilDateTime.nowTimestamp());        
        payment.set("partyIdTo", payToPartyId); 
        if (paymentRefNumber != null) { 
            payment.set("paymentRefNum", paymentRefNumber);
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

    public static boolean releaseInitialOrderHold(LocalDispatcher dispatcher, String orderId) {
        // get the delegator from the dispatcher
        GenericDelegator delegator = dispatcher.getDelegator();
        
        // find the workEffortId for this order
        List workEfforts = null;
        try {
            workEfforts = delegator.findByAnd("WorkEffort", UtilMisc.toMap("currentStatusId", "WF_SUSPENDED", "sourceReferenceId", orderId));            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting WorkEffort with order ref number: " + orderId, module);
            return false;
        }        
                         
        if (workEfforts != null) {            
            // attempt to release the order workflow from 'Hold' status (resume workflow)
            boolean allPass = true; 
            Iterator wei = workEfforts.iterator();
            while (wei.hasNext()) {
                GenericValue workEffort = (GenericValue) wei.next();                             
                String workEffortId = workEffort.getString("workEffortId");
                try {                                           
                    if (workEffort.getString("currentStatusId").equals("WF_SUSPENDED")) {
                        WorkflowClient client = new WorkflowClient(dispatcher.getDispatchContext());                
                        client.resume(workEffortId);
                    } else {
                        Debug.logVerbose("Current : --{" + workEffort + "}-- not resuming", module);
                    }                
                } catch (WfException e) {
                    Debug.logError(e, "Problem resuming activity : " + workEffortId, module);
                    allPass = false;                                     
                }
            }
            return allPass;
        } else {
            Debug.logWarning("No WF found for order ID : " + orderId, module);
        }
        return false;               
    }
    
    public static boolean abortOrderProcessing(LocalDispatcher dispatcher, String orderId) {
        Debug.logInfo("Aborting workflow for order " + orderId, module);
        GenericDelegator delegator = dispatcher.getDelegator();
        
        // find the workEffortId for this order
        GenericValue workEffort = null;
        try {
            List workEfforts = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortTypeId", "WORK_FLOW", "sourceReferenceId", orderId));
            if (workEfforts != null && workEfforts.size() > 1) {
                Debug.logWarning("More then one workflow found for defined order: " + orderId, module);                
            }
            workEffort = EntityUtil.getFirst(workEfforts);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting WorkEffort with order ref number: " + orderId, module);
            return false;
        }  
        
        if (workEffort != null) {
            String workEffortId = workEffort.getString("workEffortId");            
            if (workEffort.getString("currentStatusId").equals("WF_RUNNING")) {
                Debug.logInfo("WF is running; trying to abort", module);
                WorkflowClient client = new WorkflowClient(dispatcher.getDispatchContext());
                try {
                    client.abortProcess(workEffortId);
                } catch (WfException e) {
                    Debug.logError(e, "Problem aborting workflow", module);
                    return false;
                }
                return true;               
            }             
        }                              
        return false;
    }
}
