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
import org.ofbiz.commonapp.common.*;

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
    protected Collection orderStatuses = null;
    protected Collection orderItemPriceInfos = null;
    protected Collection orderItemInventoryReses = null;
    protected Double totalPrice = null;

    protected OrderReadHelper() {
    }

    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    public List getOrderItems() {
        if (orderItems == null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        return (List) orderItems;
    }

    public Collection getOrderItemPriceInfos(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemPriceInfos == null) {
            GenericDelegator delegator = orderHeader.getDelegator();
            try {
                orderItemPriceInfos = delegator.findByAnd("OrderItemPriceInfo", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        String orderItemSeqId = orderItem.getString("orderItemSeqId");
        return EntityUtil.filterByAnd(this.orderItemPriceInfos, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }
    
    public Collection getOrderItemInventoryReses(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemInventoryReses == null) {
            GenericDelegator delegator = orderHeader.getDelegator();
            try {
                orderItemInventoryReses = delegator.findByAnd("OrderItemInventoryRes", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
            }
        }
        String orderItemSeqId = orderItem.getString("orderItemSeqId");
        return EntityUtil.filterByAnd(this.orderItemInventoryReses, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }
    
    public List getAdjustments() {
        if (adjustments == null) {
            try {
                adjustments = orderHeader.getRelated("OrderAdjustment");
            } catch (GenericEntityException e) {
                Debug.logError(e);
            }
            if (adjustments == null)
                adjustments = new ArrayList();
        }
        return (List) adjustments;
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

    public List getOrderStatuses() {
        if (orderStatuses == null) {
            try {
                orderStatuses = orderHeader.getRelated("OrderStatus");
            } catch (GenericEntityException e) {
                Debug.logError(e);
            }
        }
        return (List) orderStatuses;
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
        Collection orderStatusList = this.getOrderHeaderStatuses();
        if (orderStatusList == null) return "";

        Set orderStatusIdSet = new HashSet();
        Iterator orderStatusIter = orderStatusList.iterator();
        while (orderStatusIter.hasNext()) {
            try {
                GenericValue orderStatus = (GenericValue) orderStatusIter.next();
                GenericValue statusItem = (GenericValue) orderStatus.getRelatedOneCache("StatusItem");
                if (statusItem != null) {
                    orderStatusIdSet.add(statusItem.getString("description"));
                } else {
                    orderStatusIdSet.add(orderStatus.getString("statusId"));
                }
            } catch (GenericEntityException gee) {
                Debug.logWarning(gee);
            }
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

    public GenericValue getPlacingParty() {
        GenericDelegator delegator = orderHeader.getDelegator();
        try {
            GenericValue placingRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "PLACING_CUSTOMER")));
            if (placingRole != null) {
                GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", placingRole.getString("partyId")));
                if (person != null)
                    return person;
                else
                    return delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", placingRole.getString("partyId")));
            } else {
                return null;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e);
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
            totalPrice = new Double(getTotalPrice(getOrderItems(), getAdjustments()));
        }//else already set
        return totalPrice.doubleValue();
    }

    public double getTotalItems() {
        List orderItems = getOrderItems();
        double totalItems = 0;
        for (int i=0; i<orderItems.size();i++) {
            GenericValue oi = (GenericValue) orderItems.get(i);
            totalItems += oi.getDouble("quantity").doubleValue();
        }
        return totalItems;
    }

    public static double getTotalPrice(List orderItems, List adjustments) {
        double total = getOrderItemsTotal(orderItems, adjustments);
        double adj = getOrderAdjustments(orderItems, adjustments);
        return total + adj;
    }

    public List getOrderHeaderAdjustments() {
        return getOrderHeaderAdjustments(getAdjustments());
    }
    public static List getOrderHeaderAdjustments(List adjustments) {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        List adj = new LinkedList();
        adj.addAll(EntityUtil.filterByAnd(adjustments, contraints1));
        adj.addAll(EntityUtil.filterByAnd(adjustments, contraints2));
        adj.addAll(EntityUtil.filterByAnd(adjustments, contraints3));
        return adj;
    }

    public List getOrderHeaderStatuses() {
        return getOrderHeaderStatuses(getOrderStatuses());
    }
    public static List getOrderHeaderStatuses(List orderStatuses) {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        List newOrderStatuses = new LinkedList();
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints1));
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints2));
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints3));
        return newOrderStatuses;
    }

    public double getOrderAdjustments() {
        return getOrderAdjustments(getOrderItems(), getAdjustments());
    }
    public static double getOrderAdjustments(List orderItems, List adjustments) {
        return calcOrderAdjustments(getOrderHeaderAdjustments(adjustments), getOrderItemsSubTotal(orderItems, adjustments), true, true, true);
    }

    // ================= Order Adjustments =================

    public static double calcOrderAdjustments(Collection orderHeaderAdjustments, double subTotal, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;
        if (orderHeaderAdjustments != null && orderHeaderAdjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(orderHeaderAdjustments, includeOther, includeTax, includeShipping);
            Iterator adjIt = filteredAdjs.iterator();
            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();
                adjTotal += OrderReadHelper.calcOrderAdjustment(orderAdjustment, subTotal);
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
        return getOrderItemsSubTotal(getOrderItems(), getAdjustments());
    }
    
    public static double getOrderItemsSubTotal(List orderItems, List adjustments) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);
        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemSubTotal((GenericValue) itemIter.next(), adjustments);
        }
        return result;
    }

    public double getOrderItemSubTotal(GenericValue orderItem) {
        return getOrderItemSubTotal(orderItem, getAdjustments());
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemSubTotal(GenericValue orderItem, Collection adjustments) {
        Double unitPrice = orderItem.getDouble("unitPrice");
        Double quantity = orderItem.getDouble("quantity");
        double result = 0.0;
        if (unitPrice == null || quantity == null) {
            Debug.logWarning("[getOrderItemTotal] unitPrice or quantity are null, using 0 for the item base price");
        } else {
            result = unitPrice.doubleValue() * quantity.doubleValue();
        }

        //subtotal also includes non tax and shipping adjustments; tax and shipping will be calculated using this adjusted value
        result += getOrderItemAdjustments(orderItem, adjustments, true, false, false);

        return result;
    }

    public double getOrderItemsTotal() {
        return getOrderItemsTotal(getOrderItems(), getAdjustments());
    }
    public static double getOrderItemsTotal(List orderItems, List adjustments) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);
        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemTotal((GenericValue) itemIter.next(), adjustments);
        }
        return result;
    }

    public double getOrderItemTotal(GenericValue orderItem) {
        return getOrderItemTotal(orderItem, getAdjustments());
    }
    public static double getOrderItemTotal(GenericValue orderItem, List adjustments) {
        //add tax and shipping to subtotal
        return (getOrderItemSubTotal(orderItem, adjustments) + getOrderItemAdjustments(orderItem, adjustments, false, true, true));
    }

    public double getOrderItemTax(GenericValue orderItem) {
        return getOrderItemAdjustments(orderItem, false, true, false);
    }

    public double getOrderItemShipping(GenericValue orderItem) {
        return getOrderItemAdjustments(orderItem, false, false, true);
    }

    public static double getOrderItemsAdjustments(List orderItems, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);
        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemAdjustments((GenericValue) itemIter.next(), adjustments, includeOther, includeTax, includeShipping);
        }
        return result;
    }
    public double getOrderItemAdjustments(GenericValue orderItem, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return getOrderItemAdjustments(orderItem, getAdjustments(), includeOther, includeTax, includeShipping);
    }
    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemAdjustments(GenericValue orderItem, Collection adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return calcItemAdjustments(orderItem, getOrderItemAdjustmentList(orderItem, adjustments), includeOther, includeTax, includeShipping);
    }
    public static Collection getOrderItemAdjustmentList(GenericValue orderItem, Collection adjustments) {
        return EntityUtil.filterByAnd(adjustments, UtilMisc.toMap("orderItemSeqId", orderItem.get("orderItemSeqId")));
    }

    public Collection getOrderItemStatuses(GenericValue orderItem) {
        return getOrderItemStatuses(orderItem, getOrderStatuses());
    }
    public static Collection getOrderItemStatuses(GenericValue orderItem, Collection orderStatuses) {
        return EntityUtil.filterByAnd(orderStatuses, UtilMisc.toMap("orderItemSeqId", orderItem.get("orderItemSeqId")));
    }

    //Order Item Adjs Utility Methods

    public static double calcItemAdjustments(GenericValue orderItem, Collection adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return calcItemAdjustments(orderItem.getDouble("quantity"), orderItem.getDouble("unitPrice"), adjustments, includeOther, includeTax, includeShipping);
    }

    public static double calcItemAdjustments(Double quantity, Double unitPrice, Collection adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;
        if (adjustments != null && adjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(adjustments, includeOther, includeTax, includeShipping);
            Iterator adjIt = filteredAdjs.iterator();
            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();
                adjTotal += OrderReadHelper.calcItemAdjustment(orderAdjustment, quantity, unitPrice);
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

    public static List filterOrderAdjustments(Collection adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        List newOrderAdjustmentsList = new LinkedList();
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
                    newOrderAdjustmentsList.add(orderAdjustment);
                }
            }
        }
        return newOrderAdjustmentsList;
    }
}
