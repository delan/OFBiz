/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ofbiz.product.inventory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;

public class InventoryWorker {
    
    public final static String module = InventoryWorker.class.getName();

    /**
     * Finds all outstanding Purchase orders for a productId.  The orders and the items cannot be completed, cancelled, or rejected
     * @param productId
     * @param delegator
     * @return
     */
    public static List<GenericValue> getOutstandingPurchaseOrders(String productId, GenericDelegator delegator) {
        try {
            List<EntityCondition> purchaseOrderConditions = UtilMisc.<EntityCondition>toList(EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"),
                    EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
                    EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                    EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_COMPLETED"),
                    EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                    EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
            purchaseOrderConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
            purchaseOrderConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            List<GenericValue> purchaseOrders = delegator.findList("OrderHeaderAndItems", EntityCondition.makeCondition(purchaseOrderConditions, EntityOperator.AND), 
                    null, UtilMisc.toList("estimatedDeliveryDate DESC", "orderDate"), null, false);
            return purchaseOrders;
        } catch (GenericEntityException ex) {
            Debug.logError("Unable to find outstanding purchase orders for product [" + productId + "] due to " + ex.getMessage() + " - returning null", module);
            return null;
        }
    }
    
    /**
     * Finds the net outstanding ordered quantity for a productId, netting quantity on outstanding purchase orders against cancelQuantity
     * @param productId
     * @param delegator
     * @return
     */
    public static BigDecimal getOutstandingPurchasedQuantity(String productId, GenericDelegator delegator) {
    	BigDecimal qty = BigDecimal.ZERO;
        List<GenericValue> purchaseOrders = getOutstandingPurchaseOrders(productId, delegator);
        if (UtilValidate.isEmpty(purchaseOrders)) {
            return qty;
        } else {
            for (GenericValue nextOrder: purchaseOrders) {
                if (nextOrder.get("quantity") != null) {
                	BigDecimal itemQuantity = nextOrder.getBigDecimal("quantity");
                	BigDecimal cancelQuantity = BigDecimal.ZERO;
                    if (nextOrder.get("cancelQuantity") != null) {
                        cancelQuantity = nextOrder.getBigDecimal("cancelQuantity");
                    }
                    itemQuantity = itemQuantity.subtract(cancelQuantity);
                    if (itemQuantity.compareTo(BigDecimal.ZERO) >= 0) {
                        qty = qty.add(itemQuantity);
                    }
                }
            }
        }

        return qty;
    }

    /**
     * Gets the quanitty of each product in the order that is outstanding across all orders of the given input type.
     * Uses the OrderItemQuantityReportGroupByProduct view entity.
     *
     * @param   productIds  Collection of disticnt productIds in an order. Use OrderReadHelper.getOrderProductIds()
     * @param   orderTypeId Either "SALES_ORDER" or "PURCHASE_ORDER"
     * @param   delegator   The delegator to use
     * @return  Map of productIds to quantities outstanding.
     */
    public static Map<String, Double> getOutstandingProductQuantities(Collection<String> productIds, String orderTypeId, GenericDelegator delegator) {
        Set<String> fieldsToSelect = UtilMisc.toSet("productId", "quantityOpen");
        List<EntityCondition> condList = UtilMisc.<EntityCondition>toList(
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, orderTypeId),
                EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"),
                EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                EntityCondition.makeCondition("orderStatusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED")
                );     
        if (productIds.size() > 0) {
            condList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
        }
        condList.add(EntityCondition.makeCondition("orderItemStatusId", EntityOperator.NOT_EQUAL, "ITEM_COMPLETED"));
        condList.add(EntityCondition.makeCondition("orderItemStatusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
        condList.add(EntityCondition.makeCondition("orderItemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
        EntityConditionList conditions = EntityCondition.makeCondition(condList, EntityOperator.AND);

        Map<String, Double> results = FastMap.newInstance();
        try {
            List<GenericValue> orderedProducts = delegator.findList("OrderItemQuantityReportGroupByProduct", conditions, fieldsToSelect, null, null, false);
            for (GenericValue value: orderedProducts) {
                results.put(value.getString("productId"), value.getBigDecimal("quantityOpen"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return results;
    }

    /** As above, but for sales orders */
    public static Map<String, Double> getOutstandingProductQuantitiesForSalesOrders(Collection<String> productIds, GenericDelegator delegator) {
        return getOutstandingProductQuantities(productIds, "SALES_ORDER", delegator);
    }

    /** As above, but for purhcase orders */
    public static Map<String, Double> getOutstandingProductQuantitiesForPurchaseOrders(Collection<String> productIds, GenericDelegator delegator) {
        return getOutstandingProductQuantities(productIds, "PURCHASE_ORDER", delegator);
    }
}    
