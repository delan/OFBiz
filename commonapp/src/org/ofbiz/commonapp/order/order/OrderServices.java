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
        GenericDelegator delegator = ctx.getDelegator();
        
        // create the order object
        String orderId = delegator.getNextSeqId("OrderHeader").toString();
        String shippingInstructions = (String) context.get("shippingInstructions");
        String billingAccountId = (String) context.get("billingAccountId");
        GenericValue order = delegator.makeValue("OrderHeader",
        UtilMisc.toMap("orderId", orderId, "orderTypeId", "SALES_ORDER",
        "orderDate", UtilDateTime.nowTimestamp(), "entryDate", UtilDateTime.nowTimestamp(),
        "statusId", "Ordered", "shippingInstructions", shippingInstructions));
        order.set("billingAccountId", billingAccountId);
        
        // add in discount adjustment
        Double cartDiscount = (Double) context.get("cartDiscount");
        if (cartDiscount.doubleValue() != 0.0) {
            order.preStoreOther(delegator.makeValue("OrderAdjustment",
            UtilMisc.toMap( "orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(),
            "orderAdjustmentTypeId", "DISCOUNT_ADJUSTMENT", "orderId", orderId, "orderItemSeqId", "NA",
            "percentage", cartDiscount)));
        }
        
        // add in shipping adjustment
        Double shipping = (Double) context.get("shippingAmount");
        order.preStoreOther(delegator.makeValue("OrderAdjustment",
        UtilMisc.toMap("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment").toString(),
        "orderAdjustmentTypeId", "SHIPPING_CHARGES", "orderId", orderId, "orderItemSeqId", null,
        "amount", shipping)));
        
        // add in tax adjustment
        Double tax = (Double) context.get("taxAmount");
        order.preStoreOther(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId",
        delegator.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "SALES_TAX",
        "orderId", orderId, "orderItemSeqId", null, "amount", tax)));
        
        // set the shipping address
        String shippingContactMechId = (String) context.get("shippingContactMechId");
        order.preStoreOther(delegator.makeValue("OrderContactMech",
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
        order.preStoreOther(orderShipmentPreference);
        
        // set the order items
        Collection orderItems = (Collection) context.get("orderItems");
        order.preStoreOthers(orderItems);
        
        // set the roles
        String partyId = (String) context.get("partyId");
        final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER",
        "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};
        
        for (int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
            order.preStoreOther(delegator.makeValue("OrderRole", UtilMisc.toMap(
            "orderId", orderId,
            "partyId", partyId,
            "roleTypeId", USER_ORDER_ROLE_TYPES[i])));
        }
        
        // set the distributor
        String distributorId = (String) context.get("distributorId");
        if (UtilValidate.isNotEmpty(distributorId)) {
            order.preStoreOther(delegator.makeValue("OrderRole", UtilMisc.toMap(
            "orderId", orderId,
            "partyId", distributorId,
            "roleTypeId", "DISTRIBUTOR")));
        }
        
        // set the order status
        order.preStoreOther(delegator.makeValue("OrderStatus",
        UtilMisc.toMap("orderStatusId", delegator.getNextSeqId("OrderStatus").toString(),
        "statusId", "Requested", "orderId", orderId, "statusDatetime", UtilDateTime.nowTimestamp())));
        
        String creditCardId = (String) context.get("creditCardId");
        if (creditCardId != null) {
            order.preStoreOther(delegator.makeValue("OrderPaymentPreference",
            UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString(),
            "orderId", orderId, "paymentMethodTypeId", "CREDIT_CARD", "paymentInfoId", creditCardId)));
        }
        else {
            //XXX CASH should not be assumed!!
            order.preStoreOther(delegator.makeValue("OrderPaymentPreference",
            UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString(),
            "orderId", orderId, "paymentMethodTypeId", "CASH", "paymentInfoId", creditCardId)));
        }
        
        try {
            delegator.create(order);
            context.put("orderId",orderId);
            context.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        catch(GenericEntityException e) {
            context.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            context.put(ModelService.ERROR_MESSAGE,"ERROR: Could not create order (write error: " + e.getMessage() + ").");
        }
        return context;
    }
    
}