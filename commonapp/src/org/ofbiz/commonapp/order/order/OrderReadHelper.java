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
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;

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
    protected Double totalPrice = null;

    protected OrderReadHelper() {}

    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
    }

    public String getOrderId() {
        return orderHeader.getString("orderId");
    }
    
    public String getWebSiteId() {
        return orderHeader.getString("webSiteId");
    }
    
    public String getCurrency() {
        return orderHeader.getString("currencyUom");
    }
    
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
        String orderItemSeqId = orderItem.getString("orderItemSeqId");

        return EntityUtil.filterByAnd(this.orderItemPriceInfos, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }

    public List getOrderItemInventoryReses(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemInventoryReses == null) {
            GenericDelegator delegator = orderHeader.getDelegator();

            try {
                orderItemInventoryReses = delegator.findByAnd("OrderItemInventoryRes", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
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

    public double getTotalOrderItemsQuantity() {
        List orderItems = getValidOrderItems();
        double totalItems = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            GenericValue oi = (GenericValue) orderItems.get(i);

            totalItems += oi.getDouble("quantity").doubleValue();
        }
        return totalItems;
    }

    public double getOrderGrandTotal() {
        if (totalPrice == null) {
            totalPrice = new Double(getOrderGrandTotal(getValidOrderItems(), getAdjustments()));
        }// else already set
        return totalPrice.doubleValue();
    }

    public static double getOrderGrandTotal(List orderItems, List adjustments) {
        double total = getOrderItemsTotal(orderItems, adjustments);
        double adj = getOrderAdjustmentsTotal(orderItems, adjustments);

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
        newOrderStatuses = EntityUtil.orderBy(newOrderStatuses, UtilMisc.toList("statusDatetime"));
        return newOrderStatuses;
    }

    public double getOrderAdjustmentsTotal() {
        return getOrderAdjustmentsTotal(getValidOrderItems(), getAdjustments());
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

    public double getOrderItemsSubTotal() {
        return getOrderItemsSubTotal(getValidOrderItems(), getAdjustments());
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
        // add tax and shipping to subtotal
        return (getOrderItemSubTotal(orderItem, adjustments) + getOrderItemAdjustmentsTotal(orderItem, adjustments, false, true, true));
    }

    public double getOrderItemTax(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, false, true, false);
    }

    public double getOrderItemShipping(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, false, false, true);
    }
    
    public static Map getOrderHeaderDisplay(GenericValue orderHeader, List orderHeaderAdjustments, double orderSubTotal) {
        Map newMap = new HashMap(orderHeader);               
        newMap.put("headerAdjustmentsToShow", getOrderHeaderAdjustmentToShow(orderHeaderAdjustments, orderSubTotal));
        return newMap;            
    }
    
    public static List getOrderHeaderAdjustmentToShow(List orderHeaderAdjustments, double orderSubTotal) {        
        List headerAdjustmentsToShow = filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);
        Iterator ai = headerAdjustmentsToShow.iterator();
        List hats = new LinkedList();
        while (ai.hasNext()) {
            GenericValue adj = (GenericValue) ai.next();
            Map adjMap = new HashMap(adj);
            try {
                GenericValue type = adj.getRelatedOne("OrderAdjustmentType");
                adjMap.put("typeDescription", type.getString("description"));
                adjMap.put("adjustmentCalc", new Double(calcOrderAdjustment(adj, orderSubTotal)));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems getting adjustment type", module);
            }
            hats.add(adjMap);
        }  
        return hats;                          
    }
    
    public static List getOrderItemDisplay(List orderItems, List orderAdjustments) {
        List itemList = new LinkedList();      
        Iterator itemIt = orderItems.iterator();
        while (itemIt.hasNext()) {
            GenericValue gv = (GenericValue) itemIt.next();
            Map newMap = new HashMap(gv);            
            double itemAdjTotal = getOrderItemAdjustmentsTotal(gv, orderAdjustments, true, false, false);
            double itemSubTotal = getOrderItemSubTotal(gv, orderAdjustments);
            newMap.put("itemAdjTotal", new Double(itemAdjTotal));
            newMap.put("itemSubTotal", new Double(itemSubTotal));
            newMap.put("itemAdjustments", getOrderItemAdjustmentDisplay(gv, getOrderItemAdjustmentList(gv, orderAdjustments)));
            itemList.add(newMap);
        }
        return itemList;
    }
    
    public static List getOrderItemAdjustmentDisplay(GenericValue orderItem, List orderItemAdjustments) {       
        List adjList = new LinkedList();
        Iterator ai = orderItemAdjustments.iterator();
        while (ai.hasNext()) {            
            GenericValue adj = (GenericValue) ai.next();
            Map newMap = new HashMap(adj);           
            try {
                GenericValue adjType = adj.getRelatedOne("OrderAdjustmentType");
                newMap.put("typeDescription", adjType.getString("description"));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems with order adjustment", module);
            }            
            double adjTotal = calcItemAdjustment(adj, orderItem);
            newMap.put("itemAdjTotal", new Double(adjTotal));      
            adjList.add(newMap);      
        }        
        return adjList;
        
    }

    public static double getAllOrderItemsAdjustmentsTotal(List orderItems, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemAdjustmentsTotal((GenericValue) itemIter.next(), adjustments, includeOther, includeTax, includeShipping);
        }
        return result;
    }

    public double getOrderItemAdjustmentsTotal(GenericValue orderItem, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return getOrderItemAdjustmentsTotal(orderItem, getAdjustments(), includeOther, includeTax, includeShipping);
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

    public List getOrderItemStatuses(GenericValue orderItem) {
        return getOrderItemStatuses(orderItem, getOrderStatuses());
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
}
