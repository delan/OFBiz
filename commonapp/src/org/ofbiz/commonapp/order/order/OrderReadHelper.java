/*
 * OrderHelper.java
 *
 * Created on August 28, 2001, 11:25 AM
 */

package org.ofbiz.commonapp.order.order;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *
 * XXX
 * adjustment:  
 * order item/amount:  multiply by quantity (unit price adjustment)
 * order item/percentage: multiply by unit price * quantity
 * order header/amount: flat amount
 * order header/percentage: multiply by order subtotal (sum of item totals)
 *
 * adjustments MUST only be a percentage or an amount
 *
 * @author  epabst
 * @version 
 */
public class OrderReadHelper {
    private GenericValue orderHeader;
    private Double totalPrice;
    
    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    public String getShippingMethod() {
        GenericValue shipmentPreference = orderHeader.getRelatedOne("OrderShipmentPreference");
        return shipmentPreference.getString("carrierPartyId") + " " 
                + shipmentPreference.getRelatedOne("CarrierShipmentMethod").getRelatedOne("ShipmentMethodType").getString("description");
    }
    
    private static GenericValue getFirst(Collection values) {
        if ((values != null) && (values.size() > 0)) {
            return (GenericValue) values.iterator().next();
        } else {
            return null;
        }
    }
    
    public GenericValue getShippingAddress() {
        GenericHelper helper = orderHeader.getHelper();
        GenericValue orderContactMech = getFirst(helper.findByAnd("OrderContactMech", UtilMisc.toMap("contactMechPurposeTypeId", 
                "SHIPPING_LOCATION", "orderId", orderHeader.getString("orderId")), null));
        if (orderContactMech != null) {
            GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
            if (contactMech != null) {
                return contactMech.getRelatedOne("PostalAddress");
            }
        }
        return null;
    }
        
    public GenericValue getBillingAddress() {
        GenericHelper helper = orderHeader.getHelper();
        GenericValue orderContactMech = getFirst(helper.findByAnd("OrderContactMech", UtilMisc.toMap("contactMechPurposeTypeId", 
                "BILLING_LOCATION", "orderId", orderHeader.getString("orderId")), null));
        if (orderContactMech != null) {
            GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
            if (contactMech != null) {
                return contactMech.getRelatedOne("PostalAddress");
            }
        }
        return null;
    }
        
    public double getShippingTotal() {
        GenericHelper helper = orderHeader.getHelper();
        Iterator shippingChargeIter = helper.findByAnd("OrderAdjustment", UtilMisc.toMap(
              "orderId", orderHeader.getString("orderId"), 
                "orderAdjustmentTypeId", "SHIPPING_CHARGES"), null).iterator();
        double result = 0.0;
        while (shippingChargeIter.hasNext()) {
            GenericValue shippingCharge = (GenericValue) shippingChargeIter.next();
            //FIXME should check percentage and watch for null amount
            result += shippingCharge.getDouble("amount").doubleValue();
        }
        return result;
    }
    
    public double getTotalPrice() {
        if (totalPrice == null) {
            double total = getOrderItemsTotal();
            Debug.log("getTotalPrice: itemsTotal=" + total);
            Iterator iter = getAdjustmentIterator();
            while (iter.hasNext()) {
                Adjustment adjustment = (Adjustment) iter.next();
                Debug.log("getTotalPrice: adjustment=" + adjustment.getAmount());
                total += adjustment.getAmount();
            }
            totalPrice = new Double(total);
        }//else already set
        Debug.log("getTotalPrice: result=" + totalPrice);
        return totalPrice.doubleValue();
    }
    
    public String getStatusString() {
        Collection orderStatusList = orderHeader.getRelated("OrderStatus");
        Set orderStatusIdSet = new HashSet();
        Iterator orderStatusIter = orderStatusList.iterator();
        while (orderStatusIter.hasNext()) {
            orderStatusIdSet.add(((GenericValue) orderStatusIter.next()).getString("statusId"));
        }
        Iterator orderStatusIdIter = orderStatusIdSet.iterator();
        String orderStatusIds;
        if (orderStatusIdIter.hasNext()) {
            orderStatusIds = orderStatusIdIter.next().toString();
            while (orderStatusIdIter.hasNext()) {
                orderStatusIds += "/" + orderStatusIdIter.next().toString();
            }
        } else {
            orderStatusIds = "(unspecified)";
        }
        return orderStatusIds;
    }
    
    public GenericValue getBillToPerson() {
        GenericHelper helper = orderHeader.getHelper();
        Collection billToRoleList = helper.findByAnd("OrderRole", UtilMisc.toMap(
                "orderId", orderHeader.getString("orderId"), 
                "roleTypeId", "BILL_TO_CUSTOMER"), null);
        if (billToRoleList.size() > 0) {
            GenericValue billToRole = (GenericValue) billToRoleList.iterator().next();
            //XXX this will fail: 
            //  return billToRole.getRelatedOne("Party").getRelatedOne("Person");
            return helper.findByPrimaryKey("Person", UtilMisc.toMap("partyId", billToRole.getString("partyId")));
        } else {
            return null;
        }
    }
    
    public double getOrderItemsTotal() {
        double result = 0.0;
        Iterator itemIter = orderHeader.getRelated("OrderItem").iterator();
        while (itemIter.hasNext()) {
            result += getOrderItemTotal((GenericValue) itemIter.next());
        }
        return result;
    }
   
    
    public double getOrderItemTotal(GenericValue orderItem) {
        double result = orderItem.getDouble("unitPrice").doubleValue() * orderItem.getDouble("quantity").doubleValue();
        Debug.log("getOrderItemTotal: itemTotal=" + result);
        return result;
        //FIXME should include adjustments as well
    }
    
    /** Iterator of OrderReadHelper.Adjustment */
    public Iterator getAdjustmentIterator() {
        return new AdjustmentIterator(orderHeader.getRelated("OrderAdjustment"), getOrderItemsTotal());
    }
    
    private class AdjustmentIterator implements Iterator {
        private Iterator orderAdjustmentIter;
        private double basePrice;
        
        private AdjustmentIterator(Collection orderAdjustments, double basePrice) {
            this.basePrice = basePrice;
            this.orderAdjustmentIter = orderAdjustments.iterator();
        }
        
        public boolean hasNext() {
            return orderAdjustmentIter.hasNext();
        }
        
        public Object next() {
            GenericValue orderAdjustment = (GenericValue) orderAdjustmentIter.next();
            GenericValue orderAdjustmentType = orderAdjustment.getRelatedOne("OrderAdjustmentType");
            String description;
            if (orderAdjustmentType != null) {
                description = orderAdjustmentType.getString("description");
            } else {
                //if not linked to an adjustment type, leave the description generic
                description = "Adjustment"; 
            }
            Adjustment result = new Adjustment(description, orderAdjustment.getDouble("amount"), orderAdjustment.getDouble("percentage"), basePrice);
            if ("SHIPPING_CHARGES".equals(orderAdjustmentType.getString("orderAdjustmentTypeId"))
                    && (getShippingMethod() != null)) {
                //put the shipping method in the adjustment description
                result.prependDescription(getShippingMethod() + " ");
            }//else keep as is
            return result;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
    }
}
