/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.commonapp.order.order;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * Utility class for easily extracting important information from orders
 *
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     Eric Pabst
 *@created    Sept 7, 2001
 *@version    1.0
 */
public class OrderReadHelper {

    protected GenericValue orderHeader = null;
    protected Collection orderItems = null;
    protected Collection adjustments = null;
    protected Collection paymentPrefs = null;
    protected Double totalPrice = null;

    protected OrderReadHelper() {
    }

    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    public Collection getOrderItems() {
        if (orderItems == null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        return orderItems;
    }

    public Collection getAdjustments() {
        if (adjustments == null) {
            try {
                adjustments = orderHeader.getRelated("OrderAdjustment");
            } catch (GenericEntityException e) {
                Debug.logError(e);
            }
            if (adjustments == null)
                adjustments = new ArrayList();
        }
        return adjustments;
    }

    public Collection getPaymentPreferences() {
        if (paymentPrefs == null) {
            try {
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
            } catch (GenericEntityException e) {
                Debug.logError(e);
            }
        }
        return paymentPrefs;
    }

    public String getShippingMethod() {
        try {
            GenericValue shipmentPreference = null;
            Iterator tempIter = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
            if (tempIter != null && tempIter.hasNext()) {
                shipmentPreference = (GenericValue) tempIter.next();
            }
            if (shipmentPreference != null) {
                GenericValue carrierShipmentMethod = shipmentPreference.getRelatedOne("CarrierShipmentMethod");
                if (carrierShipmentMethod != null) {
                    GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");
                    if (shipmentMethodType != null) {
                        return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId")) + " " + UtilFormatOut.checkNull(shipmentMethodType.getString("description"));
                    }
                }
                return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId"));
            }
            return "";
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        return "";
    }

    public GenericValue getShippingAddress() {
        GenericDelegator delegator = orderHeader.getDelegator();
        try {
            GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech", UtilMisc.toMap(
                    "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
            if (orderContactMech != null) {
                GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
                if (contactMech != null) {
                    return contactMech.getRelatedOne("PostalAddress");
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        return null;
    }

    public GenericValue getBillingAddress() {
        GenericDelegator delegator = orderHeader.getDelegator();
        try {
            GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech", UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION")));
            if (orderContactMech != null) {
                GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
                if (contactMech != null) {
                    return contactMech.getRelatedOne("PostalAddress");
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        return null;
    }

    public String getStatusString() {
        Collection orderStatusList = null;
        try {
            orderStatusList = orderHeader.getRelated("OrderStatus");
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
            return "";
        }

        Set orderStatusIdSet = new HashSet();
        Iterator orderStatusIter = orderStatusList.iterator();
        while (orderStatusIter.hasNext()) {
            orderStatusIdSet.add(((GenericValue) orderStatusIter.next()).getString("statusCode"));
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
        GenericDelegator delegator = orderHeader.getDelegator();
        try {
            GenericEntity billToRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER")));
            if (billToRole != null) {
                return delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", billToRole.getString("partyId")));
            } else {
                return null;
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        return null;
    }

    public String getDistributorId() {
        GenericDelegator delegator = orderHeader.getDelegator();
        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "DISTRIBUTOR")));
            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        return null;
    }

    public String getAffiliateId() {
        GenericDelegator delegator = orderHeader.getDelegator();
        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "AFFILIATE")));
            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        return null;
    }

    public double getTotalPrice() {
        if (totalPrice == null) {
            double total = getOrderItemsTotal();
            double adj = getOrderAdjustments();
            totalPrice = new Double(total + adj);
        }//else already set
        return totalPrice.doubleValue();
    }

    public Iterator getAdjustmentIterator() {
        return getOrderAdjustmentCollection().iterator();
    }

    public List getOrderAdjustmentCollection() {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, "_NA_"));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        ArrayList adj = new ArrayList();
        adj.addAll(EntityUtil.filterByAnd(getAdjustments(), contraints1));
        adj.addAll(EntityUtil.filterByAnd(getAdjustments(), contraints2));
        adj.addAll(EntityUtil.filterByAnd(getAdjustments(), contraints3));
        return adj;
    }

    public double getOrderAdjustments() {
        return calcOrderAdjustments(getOrderAdjustmentCollection(), getOrderItemsSubTotal(), true, true, true);
    }

    // ================= Order Adjustments =================

    public static double calcOrderAdjustments(Collection adjustments, double subTotal, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;
        if (adjustments != null && adjustments.size() > 0) {
            Iterator adjIt = adjustments.iterator();
            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                boolean includeAdjustment = false;
                if ("SALES_TAX".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeTax) includeAdjustment = true;
                } else if ("SHIPPING_CHARGES".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeShipping) includeAdjustment = true;
                } else {
                    if (includeOther) includeAdjustment = true;
                }
                
                if (includeAdjustment) {
                    adjTotal += OrderReadHelper.calcOrderAdjustment(orderAdjustment, subTotal);
                }
            }
        }
        return adjTotal;
    }

    public static double calcOrderAdjustment(GenericValue orderAdjustment, double orderSubTotal) {
        double adjustment = 0.0;
        if (orderAdjustment.get("amount") != null) {
            adjustment += orderAdjustment.getDouble("amount").doubleValue();
        }
        if (orderAdjustment.get("percentage") != null) {
            adjustment += (orderAdjustment.getDouble("percentage").doubleValue() * orderSubTotal);
        }
        return adjustment;
    }

    // ================= Order Item Adjustments =================
    
    public double getOrderItemsSubTotal() {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(getOrderItems());
        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemSubTotal((GenericValue) itemIter.next());
        }
        return result;
    }

    public double getOrderItemSubTotal(GenericValue orderItem) {
        Double unitPrice = orderItem.getDouble("unitPrice");
        Double quantity = orderItem.getDouble("quantity");
        if (unitPrice == null || quantity == null) {
            Debug.logWarning("[getOrderItemTotal] unitPrice or quantity are null, return 0 for the item total price");
            return 0.0;
        }
        double result = unitPrice.doubleValue() * quantity.doubleValue();
        
        //subtotal also includes non tax and shipping adjustments; tax and shipping will be calculated using this adjusted value
        result += getOrderItemAdjustments(orderItem, true, false, false);
        
        return result;
    }

    public double getOrderItemsTotal() {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(getOrderItems());
        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemTotal((GenericValue) itemIter.next());
        }
        return result;
    }

    public double getOrderItemTotal(GenericValue orderItem) {
        //add tax and shipping to subtotal
        return (getOrderItemSubTotal(orderItem) + getOrderItemAdjustments(orderItem, false, true, true));
    }

    public double getOrderItemAdjustments(GenericValue orderItem, boolean includeOther, boolean includeTax, boolean includeShipping) {
        List contraints = new LinkedList();
        contraints.add(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, orderItem.get("orderItemSeqId")));
        Collection adj = EntityUtil.filterByAnd(getAdjustments(), contraints);
        return calcItemAdjustments(orderItem, adj, includeOther, includeTax, includeShipping);
    }

    public double getOrderItemTax(GenericValue orderItem) {
        return getOrderItemAdjustments(orderItem, false, true, false);
    }

    public double getOrderItemShipping(GenericValue orderItem) {
        return getOrderItemAdjustments(orderItem, false, false, true);
    }

    //Order Item Adjs Utility Methods
    
    public static double calcItemAdjustments(GenericValue orderItem, Collection adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return calcItemAdjustments(orderItem.getDouble("quantity"), orderItem.getDouble("unitPrice"), adjustments, includeOther, includeTax, includeShipping);
    }
    
    public static double calcItemAdjustments(Double quantity, Double unitPrice, Collection adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;
        if (adjustments != null && adjustments.size() > 0) {
            Iterator adjIt = adjustments.iterator();
            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();
                
                boolean includeAdjustment = false;
                if ("SALES_TAX".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeTax) includeAdjustment = true;
                } else if ("SHIPPING_CHARGES".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeShipping) includeAdjustment = true;
                } else {
                    if (includeOther) includeAdjustment = true;
                }
                
                if (includeAdjustment) {
                    adjTotal += OrderReadHelper.calcItemAdjustment(orderAdjustment, quantity, unitPrice);
                }
            }
        }
        return adjTotal;
    }

    public static double calcItemAdjustment(GenericValue itemAdjustment, GenericValue item) {
        return calcItemAdjustment(itemAdjustment, item.getDouble("quantity"), item.getDouble("unitPrice"));
    }
    
    public static double calcItemAdjustment(GenericValue itemAdjustment, Double quantity, Double unitPrice) {
        double adjustment = 0.0;
        if (itemAdjustment.get("amount") != null) {
            adjustment += itemAdjustment.getDouble("amount").doubleValue();
        }
        if (itemAdjustment.get("amountPerQuantity") != null && quantity != null) {
            adjustment += itemAdjustment.getDouble("amountPerQuantity").doubleValue() * quantity.doubleValue();
        }
        if (itemAdjustment.get("percentage") != null && unitPrice != null) {
            adjustment += (itemAdjustment.getDouble("percentage").doubleValue() * unitPrice.doubleValue());
        }
        Debug.logVerbose("calcItemAdjustment: " + itemAdjustment + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", adjustment=" + adjustment);
        return adjustment;
    }
}
