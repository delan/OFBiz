/*
 * $Id$
 */

package org.ofbiz.commonapp.order.order;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Order Processing Services
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    December 11, 2001
 *@version    1.0
 */

public class OrderServices {
    
    /** Service for creating a new order */
    public static Map createOrder(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Collection toBeStored = new LinkedList();
        
        // check to make sure we have something to order
        List orderItems = (List) context.get("orderItems");
        if ( orderItems.size() < 1 ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,"ERROR: There are no items to order");
            return result;
        }
        
        // create the order object
        String orderId = delegator.getNextSeqId("OrderHeader").toString();
        String shippingInstructions = (String) context.get("shippingInstructions");
        String billingAccountId = (String) context.get("billingAccountId");
        GenericValue order = delegator.makeValue("OrderHeader",
                UtilMisc.toMap("orderId", orderId, "orderTypeId", "SALES_ORDER",
                "orderDate", UtilDateTime.nowTimestamp(), "entryDate", UtilDateTime.nowTimestamp(),
                "statusId", "ORDERED", "billingAccountId", billingAccountId));
        toBeStored.add(order);
        
        
        // add in discount adjustment
        Double cartDiscount = (Double) context.get("cartDiscount");
        if (cartDiscount.doubleValue() != 0.0) {
            toBeStored.add(delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap( "orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(),
                    "orderAdjustmentTypeId", "DISCOUNT_ADJUSTMENT", "orderId", orderId, "orderItemSeqId", DataModelConstants.SEQ_ID_NA,
                    "percentage", cartDiscount)));
        }
        
        // add in shipping adjustment
        Double shipping = (Double) context.get("shippingAmount");
        toBeStored.add(delegator.makeValue("OrderAdjustment",
                UtilMisc.toMap("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(),
                "orderAdjustmentTypeId", "SHIPPING_CHARGES", "orderId", orderId, "orderItemSeqId", null,
                "amount", shipping)));
        
        // add in tax adjustment
        Double tax = (Double) context.get("taxAmount");
        toBeStored.add(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId",
                delegator.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "SALES_TAX",
                "orderId", orderId, "orderItemSeqId", null, "amount", tax)));
        
        // set the shipping address
        String shippingContactMechId = (String) context.get("shippingContactMechId");
        toBeStored.add(delegator.makeValue("OrderContactMech",
                UtilMisc.toMap( "contactMechId", shippingContactMechId,
                "contactMechPurposeTypeId", "SHIPPING_LOCATION", "orderId", orderId)));
        
        // set the shipment preference
        String shipmentMethodTypeId = (String) context.get("shipmentMethodTypeId");
        String carrierPartyId = (String) context.get("carrierPartyId");
        Boolean maySplit = (Boolean) context.get("maySplit");
        GenericValue orderShipmentPreference = delegator.makeValue("OrderShipmentPreference",
        UtilMisc.toMap("orderId", orderId, "orderItemSeqId", DataModelConstants.SEQ_ID_NA,
        "shipmentMethodTypeId", shipmentMethodTypeId,
        "carrierPartyId", carrierPartyId, "carrierRoleTypeId", "CARRIER" /* XXX */,
        "shippingInstructions", shippingInstructions));
        orderShipmentPreference.set("maySplit", maySplit);
        toBeStored.add(orderShipmentPreference);
        
        // set the order items
        // productId|productName|price|quantity|comment|poNumber
        Iterator oi = orderItems.iterator();
        int seqId = 1;
        while ( oi.hasNext() ) {
            List element = StringUtil.split((String)oi.next(),"|");
            String orderItemSeqId = String.valueOf(seqId++);
            GenericValue orderItem = delegator.makeValue("OrderItem",
            UtilMisc.toMap("orderId", orderId,
            "orderItemSeqId", orderItemSeqId,
            "orderItemTypeId", "SALES_ORDER_ITEM",
            "productId", element.get(0),
            "quantity", new Double((String)element.get(3)),
            "unitPrice", new Double((String)element.get(2))));
            orderItem.set("itemDescription", element.get(1));
            orderItem.set("comments", element.get(4));
            orderItem.set("correspondingPoId", element.get(5));
            orderItem.set("statusId", "Ordered");
            toBeStored.add(orderItem);
        }
        
        // set the roles
        String partyId = (String) context.get("partyId");
        final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER",
        "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};
        
        for (int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
            toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap(
            "orderId", orderId,
            "partyId", partyId,
            "roleTypeId", USER_ORDER_ROLE_TYPES[i])));
        }
        
        // set the distributor
        String distributorId = (String) context.get("distributorId");
        if (UtilValidate.isNotEmpty(distributorId)) {
            toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap(
                    "orderId", orderId, "partyId", distributorId, "roleTypeId", "DISTRIBUTOR")));
        }
        
        // ------- TODO Make this so if we pass credit card info a new ID is created -------
        // set the order status
        toBeStored.add(delegator.makeValue("OrderStatus",
                UtilMisc.toMap("orderStatusId", delegator.getNextSeqId("OrderStatus").toString(),
                "statusId", "ORDERED", "orderId", orderId, "statusDatetime", UtilDateTime.nowTimestamp())));
        
        String creditCardId = (String) context.get("creditCardId");
        if (creditCardId != null) {
            toBeStored.add(delegator.makeValue("OrderPaymentPreference",
                    UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString(),
                    "orderId", orderId, "paymentMethodTypeId", "CREDIT_CARD", "paymentInfoId", creditCardId)));
        }
        else {
            //XXX CASH should not be assumed!!
            toBeStored.add(delegator.makeValue("OrderPaymentPreference",
                    UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString(),
                    "orderId", orderId, "paymentMethodTypeId", "CASH", "paymentInfoId", creditCardId)));
        }
        
        try {
            delegator.storeAll(toBeStored);
            result.put("orderId",orderId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch(GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not create order (write error: " + e.getMessage() + ").");
        }
        return result;
    }
    
    /** Service for changing the status on an order */
    public static Map setOrderStatus(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");        
        try {
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
            if ( orderHeader == null ) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not change order status; order cannot be found.");
            }     
            Debug.logInfo("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"));
            Debug.logInfo("[OrderServices.setOrderStatus] : To Status : " + statusId);
            if ( orderHeader.getString("statusId").equals(statusId) ) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                return result;
            }                
            try {
                Map statusFields = UtilMisc.toMap("statusId",orderHeader.getString("statusId"),"statusIdTo",statusId);
                GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange",statusFields);
                if  ( statusChange == null ) {
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not change order status; status is not a valid change.");
                    return result;
                }
            }
            catch ( GenericEntityException e ) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not change order status (" + e.getMessage() + ").");
                return result;
            }
            orderHeader.set("statusId",statusId);
            Map fields = new HashMap();
            fields.put("orderStatusId", delegator.getNextSeqId("OrderStatus").toString());
            fields.put("statusId", statusId);
            fields.put("orderId", orderId);
            fields.put("statusDatetime", UtilDateTime.nowTimestamp());
            GenericValue orderStatus = delegator.makeValue("OrderStatus",fields);
            Collection c = new ArrayList();
            c.add(orderHeader);
            c.add(orderStatus);
            delegator.storeAll(c);            
        }
        catch ( GenericEntityException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not change order status (" + e.getMessage() + ").");
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Service to add a role type to an order */
    public static Map addRoleType(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String partyId = (String) context.get("partyId");
        String roleTypeId = (String) context.get("roleTypeId");
        Map fields = UtilMisc.toMap("orderId",orderId,"partyId",partyId,"roleTypeId",roleTypeId);
        try {
            GenericValue value = delegator.makeValue("OrderRole",fields);
            delegator.create(value);
        }
        catch ( GenericEntityException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not add role to order (" + e.getMessage() + ").");
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Service to remove a role type from an order */
    public static Map removeRoleType(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String partyId = (String) context.get("partyId");
        String roleTypeId = (String) context.get("roleTypeId");
        Map fields = UtilMisc.toMap("orderId",orderId,"partyId",partyId,"roleTypeId",roleTypeId);
        try {
            GenericValue value = delegator.findByPrimaryKey("OrderRole",fields);
            value.remove();
        }
        catch( GenericEntityException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE,"ERROR: Could not remove role from order (" + e.getMessage() + ").");
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /** Service to email a customer with order status */
    public static Map emailOrder(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        // This may be moved to a different place - email is a common task code this generic
        // implement me
        return result;
    }
    
    
}
