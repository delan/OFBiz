/*
 * $Id$
 * $Log$
 * Revision 1.4  2001/09/14 21:15:13  epabst
 * cleaned up
 *
 */

package org.ofbiz.commonapp.order.order;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Order Reading Helper
 * <p><b>Description:</b> Utility class for easily extracting important information from orders
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
 *@author     Eric Pabst
 *@created    Sept 7, 2001
 *@version    1.0
 */
public class OrderReadHelper {
    private GenericValue orderHeader;
    private Double totalPrice;
    
    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    public String getShippingMethod() {
      GenericValue shipmentPreference = orderHeader.getRelatedOne("OrderShipmentPreference");
      if(shipmentPreference != null) {
        GenericValue carrierShipmentMethod = shipmentPreference.getRelatedOne("CarrierShipmentMethod");
        if(carrierShipmentMethod != null) {
          GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");
          if(shipmentMethodType != null) {
            return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId")) + " " + UtilFormatOut.checkNull(shipmentMethodType.getString("description"));
          }
        }
        return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId"));
      }
      return "";
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
        
    public double getTotalPrice() {
        if (totalPrice == null) {
            double total = getOrderItemsTotal();
            Iterator iter = getAdjustmentIterator();
            while (iter.hasNext()) {
                Adjustment adjustment = (Adjustment) iter.next();
                total += adjustment.getAmount();
            }
            totalPrice = new Double(total);
        }//else already set
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
        GenericEntity billToRole = getFirst(helper.findByAnd("OrderRole", UtilMisc.toMap(
                "orderId", orderHeader.getString("orderId"), 
                "roleTypeId", "BILL_TO_CUSTOMER"), null));
        if (billToRole != null) {
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
        //FIXME should include adjustments as well
        return result;
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
