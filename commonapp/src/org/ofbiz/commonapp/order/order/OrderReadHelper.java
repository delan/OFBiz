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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.commonapp.common.DataModelConstants;
import org.ofbiz.core.entity.EntityExpr;
import org.ofbiz.core.entity.EntityOperator;
import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntity;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilFormatOut;
import org.ofbiz.core.util.UtilMisc;

/**
 * Utility class for easily extracting important information from orders
 *
 * <p>NOTE: in the current scheme order adjustments are never included in tax or shipping,
 * but order item adjustments ARE included in tax and shipping calcs unless they are
 * tax or shipping adjustments or the includeInTax or includeInShipping are set to N.</p>
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     Eric Pabst
 * @author     <a href="mailto:ray.barlow@whatsthe-point.com">Ray Barlow</a>
 * @version    $Revision$
 * @since      2.0
 */
public class OrderReadHelper {

    public static final String module = OrderReadHelper.class.getName();

    protected GenericValue orderHeader = null;
    protected List orderItems = null;
    protected List adjustments = null;
    protected List paymentPrefs = null;
    protected List orderStatuses = null;
    protected List orderItemPriceInfos = null;
    protected List orderItemInventoryReses = null;
    protected List orderItemIssuances = null;
    protected Double totalPrice = null;

    protected OrderReadHelper() {}
    
    public OrderReadHelper(GenericValue orderHeader, List adjustments, List orderItems) {
        this.orderHeader = orderHeader;
        this.adjustments = adjustments;
        this.orderItems = orderItems;
    }

    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    // ==========================================
    // ========== Order Header Methods ==========
    // ==========================================

    public String getOrderId() {
        return orderHeader.getString("orderId");
    }

    public String getWebSiteId() {
        return orderHeader.getString("webSiteId");
    }

    public String getCurrency() {
        return orderHeader.getString("currencyUom");
    }

    public List getAdjustments() {
        if (adjustments == null) {
            try {
                adjustments = orderHeader.getRelated("OrderAdjustment");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (adjustments == null)
                adjustments = new ArrayList();
        }
        return (List) adjustments;
    }

    public List getPaymentPreferences() {
        if (paymentPrefs == null) {
            try {
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        return paymentPrefs;
    }

    public List getOrderStatuses() {
        if (orderStatuses == null) {
            try {
                orderStatuses = orderHeader.getRelated("OrderStatus");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
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
            Debug.logWarning(e, module);
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
            Debug.logWarning(e, module);
        }
        return null;
    }

    public GenericValue getBillingAddress() {
        // TODO: if null; get address from payment
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
            Debug.logWarning(e, module);
        }
        return null;
    }

    public String getCurrentStatusString() {
        GenericValue statusItem = null;
        try {
            statusItem = orderHeader.getRelatedOneCache("StatusItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (statusItem != null) {
            return statusItem.getString("description");
        } else {
            return orderHeader.getString("statusId");
        }
    }

    public String getStatusString() {
        List orderStatusList = this.getOrderHeaderStatuses();

        if (orderStatusList == null) return "";

        Iterator orderStatusIter = orderStatusList.iterator();
        StringBuffer orderStatusIds = new StringBuffer(50);
        boolean statusSet = false;

        while (orderStatusIter.hasNext()) {
            try {
                GenericValue orderStatus = (GenericValue) orderStatusIter.next();
                GenericValue statusItem = orderStatus.getRelatedOneCache("StatusItem");

                if ( false == statusSet ) {
                    statusSet = true;
                } else {
                    orderStatusIds.append( "/" );
                }

                if (statusItem != null) {
                    orderStatusIds.append(statusItem.getString("description"));
                } else {
                    orderStatusIds.append(orderStatus.getString("statusId"));
                }
            } catch (GenericEntityException gee) {
                Debug.logWarning(gee, module);
            }
        }

        if (false == statusSet) {
            orderStatusIds.append("(unspecified)");
        }
        return orderStatusIds.toString();
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
            Debug.logWarning(e, module);
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
            Debug.logError(e, module);
        }
        return null;
    }

    public String getDistributorId() {
        GenericDelegator delegator = orderHeader.getDelegator();

        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "DISTRIBUTOR")));

            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public String getAffiliateId() {
        GenericDelegator delegator = orderHeader.getDelegator();

        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "AFFILIATE")));

            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public double getOrderGrandTotal() {
        if (totalPrice == null) {
            totalPrice = new Double(getOrderGrandTotal(getValidOrderItems(), getAdjustments()));
        }// else already set
        return totalPrice.doubleValue();
    }

    public List getOrderHeaderAdjustments() {
        return getOrderHeaderAdjustments(getAdjustments());
    }

    public List getOrderHeaderAdjustmentsToShow() {
        return filterOrderAdjustments(getOrderHeaderAdjustments(), true, false, false, false, false);
    }

    public List getOrderHeaderStatuses() {
        return getOrderHeaderStatuses(getOrderStatuses());
    }

    public double getOrderAdjustmentsTotal() {
        return getOrderAdjustmentsTotal(getValidOrderItems(), getAdjustments());
    }

    public double getOrderAdjustmentTotal(GenericValue adjustment) {
        return calcOrderAdjustment(adjustment, getOrderItemsSubTotal());
    }

    // ========================================
    // ========== Order Item Methods ==========
    // ========================================

    public List getOrderItems() {
        if (orderItems == null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        return (List) orderItems;
    }

    public List getValidOrderItems() {
        List exprs = UtilMisc.toList(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
        return EntityUtil.filterByAnd(getOrderItems(), exprs);
    }

    public List getOrderItemAdjustments(GenericValue orderItem) {
        return getOrderItemAdjustmentList(orderItem, getAdjustments());
    }

    public String getCurrentItemStatus(GenericValue orderItem) {
        GenericValue statusItem = null;
        try {
            statusItem = orderItem.getRelatedOne("StatusItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting StatusItem : " + orderItem, module);
        }
        if (statusItem == null || statusItem.get("description") == null) {
            return "Not Available";
        } else {
            return statusItem.getString("description");
        }
    }

    public List getOrderItemPriceInfos(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemPriceInfos == null) {
            GenericDelegator delegator = orderHeader.getDelegator();

            try {
                orderItemPriceInfos = delegator.findByAnd("OrderItemPriceInfo", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        String orderItemSeqId = (String) orderItem.get("orderItemSeqId");

        return EntityUtil.filterByAnd(this.orderItemPriceInfos, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }

    public List getOrderItemInventoryReses(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemInventoryReses == null) {
            GenericDelegator delegator = orderHeader.getDelegator();

            try {
                orderItemInventoryReses = delegator.findByAnd("OrderItemInventoryRes", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Trouble getting OrderItemInventoryRes(s)", module);
            }
        }
        String orderItemSeqId = (String) orderItem.get("orderItemSeqId");

        return EntityUtil.filterByAnd(this.orderItemInventoryReses, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }

    public List getOrderItemIssuances(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemIssuances == null) {
            GenericDelegator delegator = orderHeader.getDelegator();

            try {
                orderItemIssuances = delegator.findByAnd("ItemIssuance", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Trouble getting ItemIssuance(s)", module);
            }
        }
        String orderItemSeqId = (String) orderItem.get("orderItemSeqId");

        return EntityUtil.filterByAnd(this.orderItemIssuances, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }

    public double getItemShippedQuantity(GenericValue orderItem) {
        double quantityShipped = 0.00;
        List issuance = getOrderItemIssuances(orderItem);
        if (issuance != null) {
            Iterator i = issuance.iterator();
            while (i.hasNext()) {
                GenericValue issue = (GenericValue) i.next();
                Double issueQty = issue.getDouble("quantity");
                if (issueQty == null) {
                    quantityShipped += issueQty.doubleValue();
                }
            }
        }
        return quantityShipped;
    }

    public double getItemReservedQuantity(GenericValue orderItem) {
        double reserved = 0.00;

        List reses = getOrderItemInventoryReses(orderItem);
        if (reses != null) {
            Iterator i = reses.iterator();
            while (i.hasNext()) {
                GenericValue res = (GenericValue) i.next();
                Double quantity = res.getDouble("quantity");
                if (quantity != null) {
                    reserved += quantity.doubleValue();
                }
            }
        }
        return reserved;
    }

    public double getItemBackorderedQuantity(GenericValue orderItem) {
        double backOrdered = 0.00;

        Timestamp shipDate = orderItem.getTimestamp("estimatedShipDate");
        Timestamp autoCancel = orderItem.getTimestamp("autoCancelDate");

        List reses = getOrderItemInventoryReses(orderItem);
        if (reses != null) {
            Iterator i = reses.iterator();
            while (i.hasNext()) {
                GenericValue res = (GenericValue) i.next();
                Timestamp promised = res.getTimestamp("currentPromisedDate");
                if (promised == null) {
                    promised = res.getTimestamp("promisedDatetime");
                }
                if (autoCancel != null || (shipDate != null && shipDate.after(promised))) {
                    Double resQty = res.getDouble("quantity");
                    if (resQty != null) {
                        backOrdered += resQty.doubleValue();
                    }
                }
            }
        }
        return backOrdered;
    }

    public double getItemPendingShipmentQuantity(GenericValue orderItem) {
        double reservedQty = getItemReservedQuantity(orderItem);
        double backordered = getItemBackorderedQuantity(orderItem);
        return (reservedQty - backordered);
    }

    public double getItemCanceledQuantity(GenericValue orderItem) {
        double orderedQty = orderItem.getDouble("quantity").doubleValue();
        double shippedQty = getItemShippedQuantity(orderItem);
        double reservedQty = getItemReservedQuantity(orderItem);
        return (orderedQty - shippedQty - reservedQty);
    }

    public double getTotalOrderItemsQuantity() {
        List orderItems = getValidOrderItems();
        double totalItems = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            GenericValue oi = (GenericValue) orderItems.get(i);

            totalItems += oi.getDouble("quantity").doubleValue();
        }
        return totalItems;
    }

    public double getOrderItemsSubTotal() {
        return getOrderItemsSubTotal(getValidOrderItems(), getAdjustments());
    }

    public double getOrderItemSubTotal(GenericValue orderItem) {
        return getOrderItemSubTotal(orderItem, getAdjustments());
    }

    public double getOrderItemsTotal() {
        return getOrderItemsTotal(getOrderItems(), getAdjustments());
    }

    public double getOrderItemTotal(GenericValue orderItem) {
        return getOrderItemTotal(orderItem, getAdjustments());
    }

    public double getOrderItemTax(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, false, true, false);
    }

    public double getOrderItemShipping(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, false, false, true);
    }

    public double getOrderItemAdjustmentsTotal(GenericValue orderItem, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return getOrderItemAdjustmentsTotal(orderItem, getAdjustments(), includeOther, includeTax, includeShipping);
    }

    public double getOrderItemAdjustmentsTotal(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, true, false, false);
    }

    public double getOrderItemAdjustmentTotal(GenericValue orderItem, GenericValue adjustment) {
        return calcItemAdjustment(adjustment, orderItem);
    }

    public String getAdjustmentType(GenericValue adjustment) {
        GenericValue adjustmentType = null;
        try {
            adjustmentType = adjustment.getRelatedOne("OrderAdjustmentType");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems with order adjustment", module);
        }
        if (adjustmentType == null || adjustmentType.get("description") == null) {
            return "";
        } else {
            return adjustmentType.getString("description");
        }
    }

    public List getOrderItemStatuses(GenericValue orderItem) {
        return getOrderItemStatuses(orderItem, getOrderStatuses());
    }
    
    public String getCurrentItemStatusString(GenericValue orderItem) {
        GenericValue statusItem = null;
        try {
            statusItem = orderItem.getRelatedOneCache("StatusItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (statusItem != null) {
            return statusItem.getString("description");
        } else {
            return orderHeader.getString("statusId");
        }
    }    

    /**
     * Checks to see if this user has read permission on this order
     * @param userLogin The UserLogin value object to check
     * @return boolean True if we have read permission
     */
    public boolean hasPermission(Security security, GenericValue userLogin) {
        return OrderReadHelper.hasPermission(security, userLogin, orderHeader);
    }

    /**
     * Getter for property orderHeader.
     * @return Value of property orderHeader.
     */
    public GenericValue getOrderHeader() {
        return orderHeader;
    }

    // ======================================================
    // =================== Static Methods ===================
    // ======================================================

    public static double getOrderGrandTotal(List orderItems, List adjustments) {
        double total = getOrderItemsTotal(orderItems, adjustments);
        double adj = getOrderAdjustmentsTotal(orderItems, adjustments);

        return total + adj;
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

    public static List getOrderHeaderStatuses(List orderStatuses) {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        List newOrderStatuses = new LinkedList();

        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints1));
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints2));
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints3));
        newOrderStatuses = EntityUtil.orderBy(newOrderStatuses, UtilMisc.toList("statusDatetime"));
        return newOrderStatuses;
    }

    public static double getOrderAdjustmentsTotal(List orderItems, List adjustments) {
        return calcOrderAdjustments(getOrderHeaderAdjustments(adjustments), getOrderItemsSubTotal(orderItems, adjustments), true, true, true);
    }

    // ================= Order Adjustments =================

    public static double calcOrderAdjustments(List orderHeaderAdjustments, double subTotal, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;

        if (orderHeaderAdjustments != null && orderHeaderAdjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(orderHeaderAdjustments, includeOther, includeTax, includeShipping, false, false);
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

    public static double getOrderItemsSubTotal(List orderItems, List adjustments) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemSubTotal((GenericValue) itemIter.next(), adjustments);
        }
        return result;
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemSubTotal(GenericValue orderItem, List adjustments) {
        return getOrderItemSubTotal(orderItem, adjustments, false, false);
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemSubTotal(GenericValue orderItem, List adjustments, boolean forTax, boolean forShipping) {
        Double unitPrice = orderItem.getDouble("unitPrice");
        Double quantity = orderItem.getDouble("quantity");
        double result = 0.0;

        if (unitPrice == null || quantity == null) {
            Debug.logWarning("[getOrderItemTotal] unitPrice or quantity are null, using 0 for the item base price", module);
        } else {
            result = unitPrice.doubleValue() * quantity.doubleValue();
        }

        // subtotal also includes non tax and shipping adjustments; tax and shipping will be calculated using this adjusted value
        result += getOrderItemAdjustmentsTotal(orderItem, adjustments, true, false, false, forTax, forShipping);

        return result;
    }

    public static double getOrderItemsTotal(List orderItems, List adjustments) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemTotal((GenericValue) itemIter.next(), adjustments);
        }
        return result;
    }

    public static double getOrderItemTotal(GenericValue orderItem, List adjustments) {
        // add tax and shipping to subtotal
        return (getOrderItemSubTotal(orderItem, adjustments) + getOrderItemAdjustmentsTotal(orderItem, adjustments, false, true, true));
    }

    public static double getAllOrderItemsAdjustmentsTotal(List orderItems, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemAdjustmentsTotal((GenericValue) itemIter.next(), adjustments, includeOther, includeTax, includeShipping);
        }
        return result;
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemAdjustmentsTotal(GenericValue orderItem, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return getOrderItemAdjustmentsTotal(orderItem, adjustments, includeOther, includeTax, includeShipping, false, false);
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemAdjustmentsTotal(GenericValue orderItem, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        return calcItemAdjustments(orderItem.getDouble("quantity"), orderItem.getDouble("unitPrice"),
                getOrderItemAdjustmentList(orderItem, adjustments),
                includeOther, includeTax, includeShipping, false, false);
    }

    public static List getOrderItemAdjustmentList(GenericValue orderItem, List adjustments) {
        return EntityUtil.filterByAnd(adjustments, UtilMisc.toMap("orderItemSeqId", orderItem.get("orderItemSeqId")));
    }

    public static List getOrderItemStatuses(GenericValue orderItem, List orderStatuses) {
        return EntityUtil.filterByAnd(orderStatuses, UtilMisc.toMap("orderItemSeqId", orderItem.get("orderItemSeqId")));
    }

    // Order Item Adjs Utility Methods

    public static double calcItemAdjustments(Double quantity, Double unitPrice, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        double adjTotal = 0.0;

        if (adjustments != null && adjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(adjustments, includeOther, includeTax, includeShipping, forTax, forShipping);
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
        if (Debug.verboseOn())
            Debug.logVerbose("calcItemAdjustment: " + itemAdjustment + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", adjustment=" + adjustment, module);
        return adjustment;
    }

    public static List filterOrderAdjustments(List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
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

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forTax && "N".equals(orderAdjustment.getString("includeInTax"))) {
                    includeAdjustment = false;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forShipping && "N".equals(orderAdjustment.getString("includeInShipping"))) {
                    includeAdjustment = false;
                }

                if (includeAdjustment) {
                    newOrderAdjustmentsList.add(orderAdjustment);
                }
            }
        }
        return newOrderAdjustmentsList;
    }

    /**
     * Checks to see if this user has read permission on the specified order
     * @param userLogin The UserLogin value object to check
     * @param orderHeader The OrderHeader for the specified order
     * @return boolean True if we have read permission
     */
    public static boolean hasPermission(Security security, GenericValue userLogin, GenericValue orderHeader) {
        if (userLogin == null || orderHeader == null)
            return false;

        if (security.hasEntityPermission("ORDERMGR", "_VIEW", userLogin)) {
            return true;
        } else if (security.hasEntityPermission("ORDERMGR", "_ROLEVIEW", userLogin)) {
            List orderRoles = null;
            try {
                orderRoles = orderHeader.getRelatedByAnd("OrderRole",
                        UtilMisc.toMap("partyId", userLogin.getString("partyId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get OrderRole from OrderHeader", module);
            }

            if (orderRoles.size() > 0) {
                // we are in at least one role
                return true;
            }
        }

        return false;
    }

    public static OrderReadHelper getHelper(GenericValue orderHeader) {
        return new OrderReadHelper(orderHeader);
    }
}
