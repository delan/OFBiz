/*
 * $Id: OrderServices.java,v 1.48 2004/08/16 16:56:54 ajzeneski Exp $
 *
 *  Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.order;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldMap;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericResultWaiter;
import org.ofbiz.workflow.WfUtil;

/**
 * Order Processing Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.48 $
 * @since      2.0
 */

public class OrderServices {

    public static final String module = OrderServices.class.getName();
    public static final String resource = "org.ofbiz.order.order.PackageMessages";

    /** Service for creating a new order */
    public static Map createOrder(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        List toBeStored = new LinkedList();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // check security
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "ORDERMGR", "_CREATE");

        if (result.size() > 0) {
            return result;
        }

        // get the order type
        String orderTypeId = (String) context.get("orderTypeId");
        result.put("orderTypeId", orderTypeId);

        // lookup the order type entity
        GenericValue orderType = null;
        try {
            orderType = delegator.findByPrimaryKeyCache("OrderType", UtilMisc.toMap("orderTypeId", orderTypeId));
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: OrderType lookup failed: " + e.toString());
            return result;
        }

        // make sure we have a valid order type
        if (orderType == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Invalid OrderType");
            return result;
        }

        // check to make sure we have something to order
        List orderItems = (List) context.get("orderItems");

        if (orderItems.size() < 1) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource, "items.none", locale));
            return result;
        }

        // check inventory and other things for each item
        String productStoreId = (String) context.get("productStoreId");
        List errorMessages = new LinkedList();
        Map normalizedItemQuantities = new HashMap();
        Map normalizedItemNames = new HashMap();
        Iterator itemIter = orderItems.iterator();
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // need to run through the items combining any cases where multiple lines refer to the
        // same product so the inventory check will work correctly
        // also count quantities ordered while going through the loop
        while (itemIter.hasNext()) {
            GenericValue orderItem = (GenericValue) itemIter.next();
            String currentProductId = (String) orderItem.get("productId");
            if (currentProductId != null) {
                // only normalize items with a product associated (ignore non-product items)
                if (normalizedItemQuantities.get(currentProductId) == null) {
                    normalizedItemQuantities.put(currentProductId, new Double(orderItem.getDouble("quantity").doubleValue()));
                    normalizedItemNames.put(currentProductId, new String(orderItem.getString("itemDescription")));
                } else {
                    Double currentQuantity = (Double) normalizedItemQuantities.get(currentProductId);
                    normalizedItemQuantities.put(currentProductId, new Double(currentQuantity.doubleValue() + orderItem.getDouble("quantity").doubleValue()));
                }
                
                try {
                    // count product ordered quantities
                    // run this synchronously so it will run in the same transaction
                    dispatcher.runSync("countProductQuantityOrdered", UtilMisc.toMap("productId", currentProductId, "quantity", orderItem.getDouble("quantity"), "userLogin", userLogin));
                } catch (GenericServiceException e1) {
                    Debug.logError(e1, "Error calling countProductQuantityOrdered service", module);
                    return ServiceUtil.returnError("Error calling countProductQuantityOrdered service: " + e1.toString());
                }
            }
        }

        if (!"PURCHASE_ORDER".equals(orderTypeId) && productStoreId == null) {
            return ServiceUtil.returnError("ERROR: The productStoreId can only be null for purchase orders");
        }

        Iterator normalizedIter = normalizedItemQuantities.keySet().iterator();
        while (normalizedIter.hasNext()) {
            // lookup the product entity for each normalized item; error on products not found
            String currentProductId = (String) normalizedIter.next();
            Double currentQuantity = (Double) normalizedItemQuantities.get(currentProductId);
            String itemName = (String) normalizedItemNames.get(currentProductId);
            GenericValue product = null;

            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", currentProductId));
            } catch (GenericEntityException e) {
                String errMsg = UtilProperties.getMessage(resource, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(e, errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if (product == null) {
                String errMsg = UtilProperties.getMessage(resource, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {
                // check to see if introductionDate hasn't passed yet
                if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
                    String excMsg = UtilProperties.getMessage(resource, "product.not_yet_for_sale",
                    		new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {
                // check to see if salesDiscontinuationDate has passed
                if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
                    String excMsg = UtilProperties.getMessage(resource, "product.no_longer_for_sale",
                    		new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {
                // check to see if we have inventory available
                if (ProductStoreWorker.isStoreInventoryRequired(productStoreId, product, delegator)) {
                    if (!ProductStoreWorker.isStoreInventoryAvailable(productStoreId, currentProductId,
                    		currentQuantity.doubleValue(), delegator, dispatcher)) {
                        String invErrMsg = UtilProperties.getMessage(resource, "product.out_of_stock",
                        		new Object[] { getProductName(product, itemName), currentProductId }, locale);
                        Debug.logWarning(invErrMsg, module);
                        errorMessages.add(invErrMsg);
                        continue;
                    }
                }
            }
        }

        if (errorMessages.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);
            return result;
        }

        // the inital status for ALL order types
        String initialStatus = "ORDER_CREATED";
        result.put("statusId", initialStatus);

        // create the order object
        String orderId = ProductStoreWorker.makeProductStoreOrderId(delegator, productStoreId);
        String billingAccountId = (String) context.get("billingAccountId");
        GenericValue order = delegator.makeValue("OrderHeader",
                UtilMisc.toMap("orderId", orderId, "orderTypeId", orderTypeId,
                    "orderDate", nowTimestamp, "entryDate", nowTimestamp,
                    "statusId", initialStatus, "billingAccountId", billingAccountId));

        if (context.get("currencyUom") != null) {
            order.set("currencyUom", context.get("currencyUom"));
        }

        if (context.get("firstAttemptOrderId") != null) {
            order.set("firstAttemptOrderId", context.get("firstAttemptOrderId"));
        }

        if (context.get("grandTotal") != null) {
            order.set("grandTotal", context.get("grandTotal"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("visitId"))) {
            order.set("visitId", context.get("visitId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("productStoreId"))) {
            order.set("productStoreId", context.get("productStoreId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("webSiteId"))) {
            order.set("webSiteId", context.get("webSiteId"));
        }

        if (userLogin != null && userLogin.get("userLoginId") != null) {
            order.set("createdBy", userLogin.getString("userLoginId"));
        }

        // first try to create the OrderHeader; if this does not fail, continue.
        try {
            delegator.create(order);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create OrderHeader entity; problems with insert", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Order creation failed; please notify customer service.");
            return result;
        }

        // create the order status record
        String orderStatusSeqId = delegator.getNextSeqId("OrderStatus").toString();
        GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
        orderStatus.set("orderId", orderId);
        orderStatus.set("statusId", order.getString("statusId"));
        orderStatus.set("statusDatetime", nowTimestamp);
        toBeStored.add(orderStatus);

        // set the orderId on all adjustments; this list will include order and item adjustments...
        List orderAdjustments = (List) context.get("orderAdjustments");
        if (orderAdjustments != null && orderAdjustments.size() > 0) {
            Iterator iter = orderAdjustments.iterator();

            while (iter.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) iter.next();
                try {
                    orderAdjustment.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError("ERROR: Could not get next sequence id for OrderAdjustment, cannot create order.");
                }
                orderAdjustment.set("orderId", orderId);

                if (orderAdjustment.get("orderItemSeqId") == null || orderAdjustment.getString("orderItemSeqId").length() == 0) {
                    orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA); // set the orderItemSeqId to _NA_ if not alredy set...
                }

                toBeStored.add(orderAdjustment);
            }
        }

        // set the order contact mechs
        List orderContactMechs = (List) context.get("orderContactMechs");
        if (orderContactMechs != null && orderContactMechs.size() > 0) {
            Iterator ocmi = orderContactMechs.iterator();

            while (ocmi.hasNext()) {
                GenericValue ocm = (GenericValue) ocmi.next();
                ocm.set("orderId", orderId);
                toBeStored.add(ocm);
            }
        }

        // set the order item contact mechs
        List orderItemContactMechs = (List) context.get("orderItemContactMechs");
        if (orderItemContactMechs != null && orderItemContactMechs.size() > 0) {
            Iterator oicmi = orderItemContactMechs.iterator();

            while (oicmi.hasNext()) {
                GenericValue oicm = (GenericValue) oicmi.next();
                oicm.set("orderId", orderId);
                toBeStored.add(oicm);
            }
        }

        // set the shipment preferences
        List orderShipmentPreferences = (List) context.get("orderShipmentPreferences");
        if (orderShipmentPreferences != null && orderShipmentPreferences.size() > 0) {
            Iterator oshprefs = orderShipmentPreferences.iterator();

            while (oshprefs.hasNext()) {
                GenericValue orderShipmentPreference = (GenericValue) oshprefs.next();
                orderShipmentPreference.set("orderId", orderId);
                orderShipmentPreference.set("carrierRoleTypeId", "CARRIER");
                if (orderShipmentPreference.get("orderItemSeqId") == null || orderShipmentPreference.getString("orderItemSeqId").length() == 0) {
                    orderShipmentPreference.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA); // set the orderItemSeqId to _NA_ if not alredy set...
                }
                toBeStored.add(orderShipmentPreference);
            }
        }

        // set the order items
        Iterator oi = orderItems.iterator();
        while (oi.hasNext()) {
            GenericValue orderItem = (GenericValue) oi.next();
            orderItem.set("orderId", orderId);
            toBeStored.add(orderItem);

            // create the item status record
            String itemStatusId = delegator.getNextSeqId("OrderStatus").toString();
            GenericValue itemStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", itemStatusId));
            itemStatus.put("statusId", orderItem.get("statusId"));
            itemStatus.put("orderId", orderId);
            itemStatus.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            itemStatus.put("statusDatetime", nowTimestamp);
            toBeStored.add(itemStatus);
        }
	
        // set the additional party roles
        Map additionalPartyRole = (Map) context.get("orderAdditionalPartyRoleMap");
        if (additionalPartyRole != null) {
            Iterator aprIt = additionalPartyRole.entrySet().iterator();
            while (aprIt.hasNext()) {
                Map.Entry entry = (Map.Entry) aprIt.next();
                String additionalRoleTypeId = (String) entry.getKey();
                List parties = (List) entry.getValue();
                if (parties != null) {
                    Iterator apIt = parties.iterator();
                    while (apIt.hasNext()) {
                        String additionalPartyId = (String) apIt.next();
                        toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", additionalPartyId, "roleTypeId", additionalRoleTypeId)));
                        toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", additionalPartyId, "roleTypeId", additionalRoleTypeId)));                        
                    }
                }
            }
        }

        // set the item survey responses
        List surveyResponses = (List) context.get("orderItemSurveyResponses");
        if (surveyResponses != null && surveyResponses.size() > 0) {
            Iterator oisr = surveyResponses.iterator();
            while (oisr.hasNext()) {
                GenericValue surveyResponse = (GenericValue) oisr.next();
                surveyResponse.set("orderId", orderId);
                toBeStored.add(surveyResponse);
            }
        }

        // set the item price info; NOTE: this must be after the orderItems are stored for referential integrity
        List orderItemPriceInfo = (List) context.get("orderItemPriceInfos");
        if (orderItemPriceInfo != null && orderItemPriceInfo.size() > 0) {
            Iterator oipii = orderItemPriceInfo.iterator();

            while (oipii.hasNext()) {
                GenericValue oipi = (GenericValue) oipii.next();
                try {
                    oipi.set("orderItemPriceInfoId", delegator.getNextSeqId("OrderItemPriceInfo"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError("ERROR: Could not get next sequence id for OrderItemPriceInfo, cannot create order.");
                }

                oipi.set("orderId", orderId);
                toBeStored.add(oipi);
            }
        }

        // store the orderProductPromoUseInfos
        List orderProductPromoUses = (List) context.get("orderProductPromoUses");
        if (orderProductPromoUses != null && orderProductPromoUses.size() > 0) {
            Iterator orderProductPromoUseIter = orderProductPromoUses.iterator();
            while (orderProductPromoUseIter.hasNext()) {
                GenericValue productPromoUse = (GenericValue) orderProductPromoUseIter.next();
                productPromoUse.set("orderId", orderId);
                toBeStored.add(productPromoUse);
            }
        }

        // define the roles for the order
        List userOrderRoleTypes = null;
        if ("SALES_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER");
        } else if ("PURCHASE_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("SHIP_FROM_VENDOR", "BILL_FROM_VENDOR", "SUPPLIER_AGENT");
        } else if ("WORK_ORDER".equals(orderTypeId)) {
            // TODO: set the work order roles
        } else {
            // TODO: some default behavior
        }

        // now add the roles
        if (userOrderRoleTypes != null) {
            Iterator i = userOrderRoleTypes.iterator();
            while (i.hasNext()) {
                String roleType = (String) i.next();
                String thisParty = partyId;
                if (thisParty == null) {
                    thisParty = "_NA_";  // will always set these roles so we can query
                }
                // make sure the party is in the role before adding
                toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleType)));
                toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleType)));
            }
        }

        // set the affiliate -- This is going to be removed...
        String affiliateId = (String) context.get("affiliateId");
        if (UtilValidate.isNotEmpty(affiliateId)) {
            toBeStored.add(delegator.makeValue("OrderRole",
            		UtilMisc.toMap("orderId", orderId, "partyId", affiliateId, "roleTypeId", "AFFILIATE")));
        }

        // set the distributor
        String distributorId = (String) context.get("distributorId");
        if (UtilValidate.isNotEmpty(distributorId)) {
            toBeStored.add(delegator.makeValue("OrderRole",
            		UtilMisc.toMap("orderId", orderId, "partyId", distributorId, "roleTypeId", "DISTRIBUTOR")));
        }

        // find all parties in role VENDOR associated with WebSite OR ProductStore (where WebSite overrides, if specified), associated first valid with the Order
        if (UtilValidate.isNotEmpty((String) context.get("productStoreId"))) {
            try {
                List productStoreRoles = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("roleTypeId", "VENDOR", "productStoreId", context.get("productStoreId")), UtilMisc.toList("-fromDate"));
                productStoreRoles = EntityUtil.filterByDate(productStoreRoles, true);
                GenericValue productStoreRole = EntityUtil.getFirst(productStoreRoles);
                if (productStoreRole != null) {
                    toBeStored.add(delegator.makeValue("OrderRole",
                            UtilMisc.toMap("orderId", orderId, "partyId", productStoreRole.get("partyId"), "roleTypeId", "VENDOR")));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up Vendor for the current Product Store", module);
            }

        }
        if (UtilValidate.isNotEmpty((String) context.get("webSiteId"))) {
            try {
                List webSiteRoles = delegator.findByAnd("WebSiteRole", UtilMisc.toMap("roleTypeId", "VENDOR", "webSiteId", context.get("webSiteId")), UtilMisc.toList("-fromDate"));
                webSiteRoles = EntityUtil.filterByDate(webSiteRoles, true);
                GenericValue webSiteRole = EntityUtil.getFirst(webSiteRoles);
                if (webSiteRole != null) {
                    toBeStored.add(delegator.makeValue("OrderRole",
                            UtilMisc.toMap("orderId", orderId, "partyId", webSiteRole.get("partyId"), "roleTypeId", "VENDOR")));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up Vendor for the current Web Site", module);
            }

        }

        // set the order payment preferences
        List paymentPreferences = (List) context.get("orderPaymentPreferences");
        if (paymentPreferences != null && paymentPreferences.size() > 0) {
            Iterator oppIter = paymentPreferences.iterator();
            while (oppIter.hasNext()) {
                GenericValue paymentPreference = (GenericValue) oppIter.next();
                if (paymentPreference.get("orderPaymentPreferenceId") == null)
                    paymentPreference.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString());
                if (paymentPreference.get("statusId") == null)
                    paymentPreference.set("statusId", "PAYMENT_NOT_RECEIVED");
                paymentPreference.set("orderId", orderId);
                toBeStored.add(paymentPreference);
            }
        }

        // store the trackingCodeOrder entities
        List trackingCodeOrders = (List) context.get("trackingCodeOrders");
        if (trackingCodeOrders != null && trackingCodeOrders.size() > 0) {
            Iterator tkcdordIter = trackingCodeOrders.iterator();
            while (tkcdordIter.hasNext()) {
                GenericValue trackingCodeOrder = (GenericValue) tkcdordIter.next();
                trackingCodeOrder.set("orderId", orderId);
                toBeStored.add(trackingCodeOrder);
            }
        }

        try {
            // store line items, etc so that they will be there for the foreign key checks
            delegator.storeAll(toBeStored);

            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {
                // START inventory reservation
                // decrement inventory available for each item, within the same transaction
                List resErrorMessages = new LinkedList();
                Iterator invDecItemIter = orderItems.iterator();

                while (invDecItemIter.hasNext()) {
                    GenericValue orderItem = (GenericValue) invDecItemIter.next();
                    if (orderItem.get("productId") != null) {
                        // only reserve product items; ignore non-product items
                        Double inventoryNotReserved = ProductStoreWorker.reserveStoreInventory(productStoreId,
                            orderItem.getString("productId"), orderItem.getDouble("quantity"),
                            orderItem.getString("orderId"), orderItem.getString("orderItemSeqId"),
                            userLogin, delegator, dispatcher);

                        if (inventoryNotReserved != null) {
                            // if inventoryNotReserved is not 0.0 then that is the amount that it couldn't reserve
                            GenericValue product = null;
                            try {
                                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", orderItem.getString("productId")));
                            } catch (GenericEntityException e) {
                                Debug.logError(e, "Error when looking up product in createOrder service, product failed inventory reservation", module);
                            }
                            String invErrMsg = "The product ";

                            if (product != null) {
                                invErrMsg += getProductName(product, orderItem);
                            }
                            invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                            resErrorMessages.add(invErrMsg);
                        }
                    }
                }

                if (resErrorMessages.size() > 0) {
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE_LIST, resErrorMessages);
                    return result;
                }
                // END inventory reservation
            }

            result.put("orderId", orderId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem with reservations", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create order (write error: " + e.getMessage() + ").");
        }

        return result;
    }

    public static String getProductName(GenericValue product, GenericValue orderItem) {
        if (UtilValidate.isNotEmpty(product.getString("productName"))) {
            return product.getString("productName");
        } else {
            return orderItem.getString("itemDescription");
        }
    }

    public static String getProductName(GenericValue product, String orderItemName) {
    	if (UtilValidate.isNotEmpty(product.getString("productName"))) {
    		return product.getString("productName");
    	} else {
    		return orderItemName;
    	}
    }

    /** Service for resetting the OrderHeader grandTotal */
    public static Map resetGrandTotal(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        //appears to not be used: GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            String errMsg = "ERROR: Could not set grantTotal on OrderHeader entity: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            Double currentTotal = orderHeader.getDouble("grandTotal");
            Double currentSubTotal = orderHeader.getDouble("remainingSubTotal");

            // get the new grand total
            double updatedTotal = orh.getOrderGrandTotal();

            // calculate subTotal as grandTotal - returnsTotal - (tax + shipping of items not returned)
            double remainingSubTotal = updatedTotal - orh.getOrderReturnedTotal() - orh.getOrderNonReturnedTaxAndShipping();            

            if (currentTotal == null || currentSubTotal == null || updatedTotal != currentTotal.doubleValue() ||
                    remainingSubTotal != currentSubTotal.doubleValue()) {
                orderHeader.set("grandTotal", UtilFormatOut.formatPriceNumber(updatedTotal));
                orderHeader.set("remainingSubTotal", UtilFormatOut.formatPriceNumber(remainingSubTotal));
                try {
                    orderHeader.store();
                } catch (GenericEntityException e) {
                    String errMsg = "ERROR: Could not set grandTotal on OrderHeader entity: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for setting the OrderHeader grandTotal for all OrderHeaders with no grandTotal */
    public static Map setEmptyGrandTotals(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Boolean forceAll = (Boolean) context.get("forceAll");
        if (forceAll == null) {
            forceAll = new Boolean(false);
        }

        EntityCondition cond = null;
        if (!forceAll.booleanValue()) {
            List exprs = UtilMisc.toList(new EntityExpr("grandTotal", EntityOperator.EQUALS, null),
                    new EntityExpr("remainingSubTotal", EntityOperator.EQUALS, null));
            cond = new EntityConditionList(exprs, EntityOperator.OR);
        }
        List fields = UtilMisc.toList("orderId");

        EntityListIterator eli = null;
        try {
            eli = delegator.findListIteratorByCondition("OrderHeader", cond, fields, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (eli != null) {
            // reset each order
            GenericValue orderHeader = null;
            while ((orderHeader = (GenericValue) eli.next()) != null) {
                String orderId = orderHeader.getString("orderId");
                Map resetResult = null;
                try {
                    resetResult = dispatcher.runSync("resetGrandTotal", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
                } catch (GenericServiceException e) {
                    Debug.logError(e, "ERROR: Cannot reset order totals - " + orderId, module);
                }

                if (resetResult != null && ServiceUtil.isError(resetResult)) {
                    Debug.logWarning("ERROR: Cannot reset order totals - " + orderId + " : " + ServiceUtil.getErrorMessage(resetResult), module);
                }
            }

            // close the ELI
            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logInfo("No orders found for reset processing", module);
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for checking and re-clac the tax amount */
    public static Map recalcOrderTax(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("ERROR: Cannot get OrderHeader entity: " + e.getMessage());
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError("ERROR: No valid order header found for orderId : " + orderId);
        }

        // remove the tax adjustments
        int removed = 0;
        try {
            removed = delegator.removeByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderAdjustmentTypeId", "SALES_TAX"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to remove SALES_TAX adjustments for order : " + orderId, module);
            return ServiceUtil.returnError("Unable to remove SALES_TAX adjustments");
        }
        Debug.logInfo("Removed : " + removed + " SALES_TAX adjustments for order [" + orderId + "]", module);

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        List validOrderItems = orh.getValidOrderItems();
        if (validOrderItems != null) {
            // prepare the inital lists
            List products = new ArrayList(validOrderItems.size());
            List amounts = new ArrayList(validOrderItems.size());
            List shipAmts = new ArrayList(validOrderItems.size());

            // adjustments and total
            List allAdjustments = orh.getAdjustments();
            List orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(allAdjustments);
            double orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(validOrderItems, allAdjustments);

            // shipping amount
            Double orderShipping = new Double(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

            // build up the list of tax calc service parameters
            for (int i = 0; i < validOrderItems.size(); i++) {
                GenericValue orderItem = (GenericValue) validOrderItems.get(i);

                try {
                    products.add(i, orderItem.getRelatedOne("Product"));  // get the product entity
                    amounts.add(i, new Double(OrderReadHelper.getOrderItemSubTotal(orderItem, allAdjustments, true, false))); // get the item amount
                    shipAmts.add(i, new Double(OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, allAdjustments, false, false, true))); // get the shipping amount
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot read order item entity : " + orderItem, module);
                    return ServiceUtil.returnError("Cannot read the order item entity");
                }
            }

            // prepare the service context
            Map serviceContext = UtilMisc.toMap("productStoreId", orh.getProductStoreId(), "itemProductList", products, "itemAmountList", amounts,
                "itemShippingList", shipAmts, "orderShippingAmount", orderShipping, "shippingAddress", orh.getShippingAddress());

            // invoke the calcTax service
            Map serviceResult = null;
            try {
                serviceResult = dispatcher.runSync("calcTax", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Problem occurred in tax service");
            }

            if (ServiceUtil.isError(serviceResult)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
            }

            // the adjustments (returned in order) from the tax service
            List orderAdj = (List) serviceResult.get("orderAdjustments");
            List itemAdj = (List) serviceResult.get("itemAdjustments");

            // the toStore List
            List toStore = new ArrayList();

            // set the order adjustments
            if (orderAdj != null && orderAdj.size() > 0) {
                Iterator oai = orderAdj.iterator();
                while (oai.hasNext()) {
                    GenericValue oa = (GenericValue) oai.next();
                    oa.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                    oa.set("orderId", orderId);
                    toStore.add(oa);
                }
            }

            // set the item adjustments
            if (itemAdj != null && itemAdj.size() > 0) {
                for (int i = 0; i < validOrderItems.size(); i++) {
                    GenericValue orderItem = (GenericValue) validOrderItems.get(i);
                    List itemAdjustments = (List) itemAdj.get(i);
                    Iterator ida = itemAdjustments.iterator();
                    while (ida.hasNext()) {
                        GenericValue ia = (GenericValue) ida.next();
                        ia.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                        ia.set("orderId", orderId);
                        ia.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                        toStore.add(ia);
                    }
                }
            }

            // store the new adjustments
            try {
                delegator.storeAll(toStore);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("Unable to update order tax information : " + orderId);
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for checking and re-calc the shipping amount */
    public static Map recalcOrderShipping(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("ERROR: Cannot get OrderHeader entity: " + e.getMessage());
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError("ERROR: No valid order header found for orderId : " + orderId);
        }

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        if (Debug.verboseOn()) {
            Debug.logVerbose("Shippable Total : " + orh.getShippableTotal(), module);
        }

        Map shippingEstMap = ShippingEvents.getShipEstimate(dispatcher, delegator, orh);

        Double shippingTotal = null;
        if (orh.getValidOrderItems() == null || orh.getValidOrderItems().size() == 0) {
            shippingTotal = new Double(0.00);
        } else {
            shippingTotal = (Double) shippingEstMap.get("shippingTotal");
        }
        if (Debug.verboseOn()) {
            Debug.logVerbose("New Shipping Total : " + shippingTotal, module);
        }

        double currentShipping = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orh.getOrderItems(), orh.getAdjustments(), false, false, true);
        currentShipping += OrderReadHelper.calcOrderAdjustments(orh.getOrderHeaderAdjustments(), orh.getOrderItemsSubTotal(), false, false, true);

        if (Debug.verboseOn()) {
            Debug.log("Old Shipping Total : " + currentShipping);
        }

        List errorMessageList = (List) shippingEstMap.get(ModelService.ERROR_MESSAGE_LIST);
        if (errorMessageList != null) {
            return ServiceUtil.returnError(errorMessageList);
        }

        if (shippingTotal.doubleValue() != currentShipping) {
            // place the difference as a new shipping adjustment
            Double adjustmentAmount = new Double(shippingTotal.doubleValue() - currentShipping);
            String adjSeqId = delegator.getNextSeqId("OrderAdjustment").toString();
            GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", adjSeqId));
            orderAdjustment.set("orderAdjustmentTypeId", "SHIPPING_CHARGES");
            orderAdjustment.set("amount", adjustmentAmount);
            orderAdjustment.set("orderId", orh.getOrderId());
            orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
            //orderAdjustment.set("comments", "Shipping Re-Calc Adjustment");
            try {
                orderAdjustment.create();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating shipping re-calc adjustment : " + orderAdjustment, module);
                return ServiceUtil.returnError("ERROR: Cannot create adjustment");
            }
        }

        // TODO: re-balance free shipping adjustment

        return ServiceUtil.returnSuccess();

    }

    /** Service for checking to see if an order is fully completed or canceled */
    public static Map checkItemStatus(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
            }
            if (placingCustomer == null) {
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
            }
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderHeader record", module);
        }
        if (orderHeader == null) {
            Debug.logError("OrderHeader came back as null", module);
            return ServiceUtil.returnError("Cannot update null order header [" + orderId + "]");
        }

        // get the order items
        List orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderItem records", module);
            return ServiceUtil.returnError("Problem getting OrderItem records");
        }

        boolean allCanceled = true;
        boolean allComplete = true;
        boolean allApproved = true;
        if (orderItems != null) {
            Iterator itemIter = orderItems.iterator();
            while (itemIter.hasNext()) {
                GenericValue item = (GenericValue) itemIter.next();
                String statusId = item.getString("statusId");
                //Debug.log("Item Status: " + statusId, module);
                if (!"ITEM_CANCELLED".equals(statusId)) {
                    //Debug.log("Not set to cancel", module);
                    allCanceled = false;
                    if (!"ITEM_COMPLETED".equals(statusId)) {
                        //Debug.log("Not set to complete", module);
                        allComplete = false;
                        if (!"ITEM_APPROVED".equals(statusId)) {
                            //Debug.log("Not set to approve", module);
                            allApproved = false;
                            break;
                        }
                    }
                }
            }

            // find the next status to set to (if any)
            String newStatus = null;
            if (allCanceled) {
                newStatus = "ORDER_CANCELLED";
            } else if (allComplete) {
                newStatus = "ORDER_COMPLETED";
            } else if (allApproved) {
                if (!"ORDER_SENT".equals(orderHeader.getString("statusId"))) {
                    newStatus = "ORDER_APPROVED";
                }
            }

            // now set the new order status
            if (newStatus != null) {
                Map serviceContext = UtilMisc.toMap("orderId", orderId, "statusId", newStatus, "userLogin", userLogin);
                Map newSttsResult = null;
                try {
                    newSttsResult = dispatcher.runSync("changeOrderStatus", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                }
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
            }
        } else {
            Debug.logWarning("Received NULL for OrderItem records orderId : " + orderId, module);
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service to cancel an order item quantity */
    public static Map cancelOrderItem(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Double cancelQuantity = (Double) context.get("cancelQuantity");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");

        // debugging message info
        String itemMsgInfo = orderId + " / " + orderItemSeqId;

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + itemMsgInfo);
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
        }

        Map fields = UtilMisc.toMap("orderId", orderId);
        if (orderItemSeqId != null) {
            fields.put("orderItemSeqId", orderItemSeqId);
        }

        List orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", fields);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("ERROR: Cannot get OrderItem entity: " + itemMsgInfo);
        }

        if (orderItems != null && orderItems.size() > 0) {
            Iterator itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = (GenericValue) itemsIterator.next();
                if (orderItem == null) {
                    return ServiceUtil.returnError("ERROR: Cannot cancel item; item not found : " + itemMsgInfo);
                }

                Double quantity = orderItem.getDouble("quantity");
                Double cancelQty = orderItem.getDouble("cancelQuantity");
                if (quantity == null) quantity = new Double(0.0);
                if (cancelQty == null) cancelQty = new Double(0.0);
                Double thisCancelQty = null;
                if (cancelQuantity != null) {
                    thisCancelQty = new Double(cancelQuantity.doubleValue());
                } else {
                    thisCancelQty = new Double(quantity.doubleValue());
                }
                orderItem.set("cancelQuantity", thisCancelQty);
                try {
                    orderItem.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to set cancel quantity : " + itemMsgInfo);
                }
                if (thisCancelQty.doubleValue() >= quantity.doubleValue()) {
                    // all items are cancelled -- mark the item as cancelled
                    Map statusCtx = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                    try {
                        dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError("Unable to cancel order line : " + itemMsgInfo);
                    }
                } else {
                    // reverse the inventory reservation
                    Map invCtx = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "cancelQuantity", thisCancelQty, "userLogin", userLogin);
                    try {
                        dispatcher.runSyncIgnore("cancelOrderItemInvResQty", invCtx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError("Unable to update inventory reservations : " + itemMsgInfo);
                    }
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for changing the status on order item(s) */
    public static Map setItemStatus(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String fromStatusId = (String) context.get("fromStatusId");
        String statusId = (String) context.get("statusId");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
        }

        Map fields = UtilMisc.toMap("orderId", orderId);
        if (orderItemSeqId != null)
            fields.put("orderItemSeqId", orderItemSeqId);
        if (fromStatusId != null)
            fields.put("statusId", fromStatusId);

        List orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", fields);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("ERROR: Cannot get OrderItem entity: " + e.getMessage());
        }

        if (orderItems != null && orderItems.size() > 0) {
            List toBeStored = new ArrayList();
            Iterator itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = (GenericValue) itemsIterator.next();
                if (orderItem == null) {
                    return ServiceUtil.returnError("ERROR: Cannot change item status; item not found.");
                }
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : Status Change: [" + orderId + "] (" + orderItem.getString("orderItemSeqId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setIte,Status] : From Status : " + orderItem.getString("statusId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

                if (orderItem.getString("statusId").equals(statusId)) {
                    continue;
                }

                try {
                    Map statusFields = UtilMisc.toMap("statusId", orderItem.getString("statusId"), "statusIdTo", statusId);
                    GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);

                    if (statusChange == null) {
                        Debug.logWarning("Item status not changed " + orderItem.getString("statusId") + " -> " + statusId + " is not a valid change.", module);
                        continue;
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError("ERROR: Could not change item status: " + e.getMessage());
                }

                orderItem.set("statusId", statusId);
                toBeStored.add(orderItem);

                // now create a status change
                Map changeFields = new HashMap();
                changeFields.put("orderStatusId", delegator.getNextSeqId("OrderStatus").toString());
                changeFields.put("statusId", statusId);
                changeFields.put("orderId", orderId);
                changeFields.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                changeFields.put("statusDatetime", UtilDateTime.nowTimestamp());
                GenericValue orderStatus = delegator.makeValue("OrderStatus", changeFields);
                toBeStored.add(orderStatus);
            }

            // store the changes
            if (toBeStored.size() > 0) {
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError("ERROR: Cannot store status changes: " + e.getMessage());
                }
            }

        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for changing the status on an order header */
    public static Map setOrderStatus(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
        }

        try {
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            if (orderHeader == null) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change order status; order cannot be found.");
                return result;
            }
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"), module);
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

            if (orderHeader.getString("statusId").equals(statusId)) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                return result;
            }
            try {
                Map statusFields = UtilMisc.toMap("statusId", orderHeader.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);

                if (statusChange == null) {
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change order status; status is not a valid change.");
                    return result;
                }
            } catch (GenericEntityException e) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change order status (" + e.getMessage() + ").");
                return result;
            }

            // update the current status
            orderHeader.set("statusId", statusId);

            // now create a status change
            Map fields = new HashMap();
            fields.put("orderStatusId", delegator.getNextSeqId("OrderStatus").toString());
            fields.put("statusId", statusId);
            fields.put("orderId", orderId);
            fields.put("statusDatetime", UtilDateTime.nowTimestamp());
            GenericValue orderStatus = delegator.makeValue("OrderStatus", fields);
            List toBeStored = new ArrayList();

            toBeStored.add(orderHeader);
            toBeStored.add(orderStatus);
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not change order status (" + e.getMessage() + ").");
            return result;
        }

        // release the inital hold if we are cancelled or approved
        if ("ORDER_CANCELLED".equals(statusId) || "ORDER_APPROVED".equals(statusId)) {
            OrderChangeHelper.releaseInitialOrderHold(ctx.getDispatcher(), orderId);

            // cancel any order processing if we are cancelled
            if ("ORDER_CANCELLED".equals(statusId)) {
                OrderChangeHelper.abortOrderProcessing(ctx.getDispatcher(), orderId);
            }
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put("orderStatusId", statusId);
        return result;
    }

    /** Service to update the order tracking number */
    public static Map updateTrackingNumber(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String trackingNumber = (String) context.get("trackingNumber");

        if (orderItemSeqId == null || orderItemSeqId.length() == 0)
            orderItemSeqId = DataModelConstants.SEQ_ID_NA;
        try {
            GenericValue shipPref = delegator.findByPrimaryKey("OrderShipmentPreference", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));

            if (shipPref == null) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: No order shipment preference found!");
            } else {
                shipPref.set("trackingNumber", trackingNumber);
                shipPref.store();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not set tracking number (" + e.getMessage() + ").");
        }
        return result;
    }

    /** Service to add a role type to an order */
    public static Map addRoleType(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String partyId = (String) context.get("partyId");
        String roleTypeId = (String) context.get("roleTypeId");
        Boolean removeOld = (Boolean) context.get("removeOld");

        if (removeOld != null && removeOld.booleanValue()) {
            try {
                delegator.removeByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", roleTypeId));
            } catch (GenericEntityException e) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not remove old roles (" + e.getMessage() + ").");
                return result;
            }
        }

        Map fields = UtilMisc.toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleTypeId);

        try {
            // first check and see if we are already there; if so, just return success
            GenericValue testValue = delegator.findByPrimaryKey("OrderRole", fields);
            if (testValue != null) {
                ServiceUtil.returnSuccess();
            } else {
                GenericValue value = delegator.makeValue("OrderRole", fields);
                delegator.create(value);
            }
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not add role to order (" + e.getMessage() + ").");
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
        Map fields = UtilMisc.toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleTypeId);

        GenericValue testValue = null;

        try {
            testValue = delegator.findByPrimaryKey("OrderRole", fields);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not add role to order (" + e.getMessage() + ").");
            return result;
        }

        if (testValue == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result;
        }

        try {
            GenericValue value = delegator.findByPrimaryKey("OrderRole", fields);

            value.remove();
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not remove role from order (" + e.getMessage() + ").");
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Service to prepare notification data */
    public static Map prepareOrderEmailData(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");

        try {
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            List orderItems = orh.getOrderItems();
            List orderAdjustments = orh.getAdjustments();
            List orderHeaderAdjustments = orh.getOrderHeaderAdjustments();
            double orderSubTotal = orh.getOrderItemsSubTotal();
            List headerAdjustmentsToShow = orh.getOrderHeaderAdjustmentsToShow();

            //templateContext.put("localOrderReadHelper", orh);
            result.put("orderId", orderId);
            result.put("orderItemSeqId", context.get("orderItemSeqId"));
            result.put("orderHeader", orderHeader);
            result.put("orderItems", orh.getOrderItems());
            result.put("statusString", orh.getStatusString());
            result.put("orderAdjustments", orderAdjustments);
            result.put("orderHeaderAdjustments", orderHeaderAdjustments);
            result.put("orderSubTotal", new Double(orderSubTotal));
            result.put("headerAdjustmentsToShow", headerAdjustmentsToShow);

            double shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
            shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);
            result.put("orderShippingTotal", new Double(shippingAmount));

            double taxAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
            taxAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false);
            result.put("orderTaxTotal", new Double(taxAmount));
            result.put("orderGrandTotal", new Double(OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments)));

            List placingCustomerOrderRoles = delegator.findByAnd("OrderRole",UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
            GenericValue placingCustomerOrderRole = EntityUtil.getFirst(placingCustomerOrderRoles);
            GenericValue placingCustomerPerson = placingCustomerOrderRole == null ? null : delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", placingCustomerOrderRole.getString("partyId")));
            result.put("placingCustomerPerson", placingCustomerPerson);


            GenericValue shippingAddress = orh.getShippingAddress();
            result.put("shippingAddress", shippingAddress);
            GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount");
            result.put("billingAccount", billingAccount);

            Iterator orderPaymentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderPaymentPreference"));
            if (orderPaymentPreferences != null) {
                List paymentMethods = new ArrayList();
                while (orderPaymentPreferences.hasNext()) {
                    GenericValue opp = (GenericValue) orderPaymentPreferences.next();
                    GenericValue paymentMethod = opp.getRelatedOne("PaymentMethod");
                    if (paymentMethod != null) {
                        paymentMethods.add(paymentMethod);
                    } else {
                        GenericValue paymentMethodType = opp.getRelatedOne("PaymentMethodType");
                        if (paymentMethodType != null) {
                            result.put("paymentMethodType", paymentMethodType);
                        }
                    }
                }
                result.put("paymentMethods", paymentMethods);
            }
           
            Iterator orderShipmentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
            if (orderShipmentPreferences != null && orderShipmentPreferences.hasNext()) {
                GenericValue shipmentPreference = (GenericValue) orderShipmentPreferences.next();
                result.put("carrierPartyId", shipmentPreference.getString("carrierPartyId"));
                result.put("shipmentMethodTypeId", shipmentPreference.getString("shipmentMethodTypeId"));
                GenericValue shipmentMethodType = delegator.findByPrimaryKey("ShipmentMethodType", UtilMisc.toMap("shipmentMethodTypeId", shipmentPreference.getString("shipmentMethodTypeId")));
                result.put("shipMethDescription", shipmentMethodType.getString("description"));
                result.put("shippingInstructions", shipmentPreference.getString("shippingInstructions"));
                result.put("maySplit", shipmentPreference.getBoolean("maySplit"));
                result.put("giftMessage", shipmentPreference.getString("giftMessage"));
                result.put("isGift", shipmentPreference.getBoolean("isGift"));
                result.put("trackingNumber", shipmentPreference.getString("trackingNumber"));
            }

            Iterator orderItemPOIter = UtilMisc.toIterator(orderItems);
            if (orderItemPOIter != null && orderItemPOIter.hasNext()) {
                GenericValue orderItemPo = (GenericValue) orderItemPOIter.next();
                result.put("customerPoNumber", orderItemPo.getString("correspondingPoId"));
            }

            // get Shipment tracking info
            EntityCondition osisCond = new EntityFieldMap(UtilMisc.toMap("orderId", orderId), EntityOperator.AND);
            List osisOrder = UtilMisc.toList("shipmentId", "shipmentRouteSegmentId", "shipmentPackageSeqId");
            List osisFields = UtilMisc.toList("shipmentId", "shipmentRouteSegmentId", "carrierPartyId", "shipmentMethodTypeId");
            osisFields.add("shipmentPackageSeqId");
            osisFields.add("trackingCode"); osisFields.add("boxNumber");

            EntityFindOptions osisFindOptions = new EntityFindOptions();
            osisFindOptions.setDistinct(true);

            List orderShipmentInfoSummaryList = null;
            EntityListIterator osisEli = delegator.findListIteratorByCondition("OrderShipmentInfoSummary", osisCond, null, osisFields, osisOrder, osisFindOptions);
            if (osisEli != null) {
                orderShipmentInfoSummaryList = osisEli.getCompleteList();
                osisEli.close();
            }
            result.put("orderShipmentInfoSummaryList", orderShipmentInfoSummaryList);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity read error", module);
            return ServiceUtil.returnError("Problem with entity lookup, see error log");
        }
        return result;
    }

    /** Service to email a customer with initial order confirmation */
    public static Map prepareOrderConfirmation(DispatchContext ctx, Map context) {
        context.put("emailType", "PRDS_ODR_CONFIRM");
        return prepareOrderEmail(ctx, context);
    }

    /** Service to email a customer with order changes */
    public static Map prepareOrderComplete(DispatchContext ctx, Map context) {
        context.put("emailType", "PRDS_ODR_COMPLETE");
        return prepareOrderEmail(ctx, context);
    }

    /** Service to email a customer with order changes */
    public static Map prepareOrderBackorder(DispatchContext ctx, Map context) {
        context.put("emailType", "PRDS_ODR_BACKORDER");
        return prepareOrderEmail(ctx, context);
    }

    /** Service to email a customer with order changes */
    public static Map prepareOrderChange(DispatchContext ctx, Map context) {
        context.put("emailType", "PRDS_ODR_CHANGE");
        return prepareOrderEmail(ctx, context);
    }

    /** Service to email a customer with order payment retry results */
    public static Map prepareOrderPayRetry(DispatchContext ctx, Map context) {
        context.put("emailType", "PRDS_ODR_PAYRETRY");
        return prepareOrderEmail(ctx, context);
    }

    public static Map prepareOrderEmail(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String emailType = (String) context.get("emailType");
        String ofbizHome = System.getProperty("ofbiz.home");

        // get the order header and store
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting OrderHeader", module);
        }

        GenericValue productStoreEmail = null;
        if (orderHeader != null) {
            try {
                productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", orderHeader.get("productStoreId"), "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting the ProductStoreEmailSetting", module);
            }
        }

        if (productStoreEmail == null) {
            return ServiceUtil.returnError("No valid email setting for store");
        }

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        String emailString = orh.getOrderEmailString();
        if (UtilValidate.isEmpty(emailString)) {
            Debug.logInfo("Customer is not setup to receive emails; no address(s) found [" + orderId + "]", module);
            return ServiceUtil.returnError("No sendTo email address found");
        }

        // prepare the parsed subject
        Map orderEmailData = prepareOrderEmailData(ctx, UtilMisc.toMap("orderId", orderId));
        String subjectString = productStoreEmail.getString("subject");
        subjectString = FlexibleStringExpander.expandString(subjectString, orderEmailData);

        result.put("templateName", ofbizHome + productStoreEmail.get("templatePath"));
        result.put("emailType", emailType);
        result.put("subject", subjectString);
        result.put("contentType", productStoreEmail.get("contentType"));
        result.put("sendFrom", productStoreEmail.get("fromAddress"));
        result.put("sendCc", productStoreEmail.get("ccAddress"));
        result.put("sendBcc", productStoreEmail.get("bccAddress"));
        result.put("sendTo", emailString);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

        return result;
    }

    /** Service to email order notifications for pending actions */
    public static Map sendProcessNotification(DispatchContext ctx, Map context) {
        //appears to not be used: Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        String adminEmailList = (String) context.get("adminEmailList");
        String assignedToUser = (String) context.get("assignedPartyId");
        //appears to not be used: String assignedToRole = (String) context.get("assignedRoleTypeId");
        String workEffortId = (String) context.get("workEffortId");

        GenericValue workEffort = null;
        GenericValue orderHeader = null;
        //appears to not be used: String assignedEmail = null;

        // get the order/workflow info
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
            String sourceReferenceId = workEffort.getString("sourceReferenceId");
            if (sourceReferenceId != null)
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", sourceReferenceId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Problem with entity lookup");
        }

        // find the assigned user's email address(s)
        GenericValue party = null;
        Collection assignedToEmails = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", assignedToUser));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Problem with entity lookup");
        }
        if (party != null)
            assignedToEmails = ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false);

        Map templateData = new HashMap(context);
        String omgStatusId = WfUtil.getOMGStatus(workEffort.getString("currentStatusId"));
        templateData.putAll(orderHeader);
        templateData.putAll(workEffort);
        templateData.put("omgStatusId", omgStatusId);

        // get the assignments
        List assignments = null;
        if (workEffort != null) {
            try {
                assignments = workEffort.getRelated("WorkEffortPartyAssignment");
            } catch (GenericEntityException e1) {
                Debug.logError(e1, "Problems getting assignements", module);
            }
        }
        templateData.put("assignments", assignments);

        StringBuffer emailList = new StringBuffer();
        if (assignedToEmails != null) {
            Iterator aei = assignedToEmails.iterator();
            while (aei.hasNext()) {
                GenericValue ct = (GenericValue) aei.next();
                if (ct != null && ct.get("infoString") != null) {
                    if (emailList.length() > 1)
                        emailList.append(",");
                    emailList.append(ct.getString("infoString"));
                }
            }
        }
        if (adminEmailList != null) {
            if (emailList.length() > 1)
                emailList.append(",");
            emailList.append(adminEmailList);
        }

        // prepare the mail info
        String ofbizHome = System.getProperty("ofbiz.home");
        String templateName = ofbizHome + "/components/order/email/default/emailprocessnotify.ftl";

        Map sendMailContext = new HashMap();
        sendMailContext.put("sendTo", emailList.toString());
        sendMailContext.put("sendFrom", "workflow@ofbiz.org"); // fixme
        sendMailContext.put("subject", "Workflow Notification");
        sendMailContext.put("templateName", templateName);
        sendMailContext.put("templateData", templateData);

        try {
            dispatcher.runAsync("sendGenericNotificationEmail", sendMailContext);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError("SendMail service failed: " + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }


    /** Service to create an order payment preference */
    public static Map createPaymentPreference(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
        String paymentMethodId = (String) context.get("paymentMethodId");
        Double maxAmount = (Double) context.get("maxAmount");

        String prefId = null;
        
        try {
            prefId = delegator.getNextSeqId("OrderPaymentPreference");
        } catch (IllegalArgumentException e) {
            return ServiceUtil.returnError("ERROR: Could not create OrderPaymentPreference (id generation failure)");
        }

        Map fields = UtilMisc.toMap("orderPaymentPreferenceId", prefId, "orderId", orderId, "paymentMethodTypeId",
                paymentMethodTypeId, "paymentMethodId", paymentMethodId, "maxAmount", maxAmount);

        try {
            GenericValue v = delegator.makeValue("OrderPaymentPreference", fields);

            delegator.create(v);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create OrderPaymentPreference (" + e.getMessage() + ").");
            return result;
        }
        result.put("orderPaymentPreferenceId", prefId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Service to get order header information as standard results. */
    public static Map getOrderHeaderInformation(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order header detial", module);
            return ServiceUtil.returnError("Cannot get order header : " + e.getMessage());
        }
        if (orderHeader != null) {
            Map result = ServiceUtil.returnSuccess();
            result.putAll(orderHeader);
            return result;
        }
        return ServiceUtil.returnError("Error getting order header information; null");
    }

    /** Service to get basic order information. */
    public static Map getOrderInformation(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");

        try {
            GenericValue orderHeader = delegator.findByPrimaryKeyCache("OrderHeader", UtilMisc.toMap("orderId", orderId));
            Collection orderItems = orderHeader.getRelatedCache("OrderItem");
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            String statusString = orh.getStatusString();
            Double totalItems = new Double(orh.getTotalOrderItemsQuantity());
            Double totalPrice = new Double(orh.getOrderGrandTotal());
            GenericValue shipAddress = orh.getShippingAddress();
            GenericValue billAddress = orh.getBillingAddress();
            GenericValue billTo = orh.getBillToPerson();
            String affilId = orh.getAffiliateId();
            String distId = orh.getDistributorId();

            result.put("orderId", orderId);
            result.put("orderHeader", orderHeader);
            result.put("orderItems", orderItems);
            result.put("totalItems", totalItems);
            result.put("totalPrice", totalPrice);
            result.put("shippingAddress", shipAddress);
            result.put("billingAddress", billAddress);
            result.put("billToPerson", billTo);
            result.put("affiliateId", affilId);
            result.put("distributorId", distId);
            result.put("statusString", statusString);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get order information (" + e.getMessage() + ").");
        }
        return result;
    }

    /** Service to get the total shipping for an order. */
    public static Map getOrderShippingAmount(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("ERROR: Could not get order information (" + e.getMessage() + ").");
        }

        Map result = null;
        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            List orderItems = orh.getValidOrderItems();
            List orderAdjustments = orh.getAdjustments();
            List orderHeaderAdjustments = orh.getOrderHeaderAdjustments();
            double orderSubTotal = orh.getOrderItemsSubTotal();

            double shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
            shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);

            result = ServiceUtil.returnSuccess();
            result.put("shippingAmount", new Double(shippingAmount));
        } else {
            result = ServiceUtil.returnError("Unable to find OrderHeader; cannot get shipping amount");
        }
        return result;
    }

    /** Service to get an order contact mech. */
    public static Map getOrderAddress(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        //appears to not be used: GenericValue v = null;
        String purpose[] = { "BILLING_LOCATION", "SHIPPING_LOCATION" };
        String outKey[] = { "billingAddress", "shippingAddress" };
        GenericValue orderHeader = null;

        try {
            orderHeader = delegator.findByPrimaryKeyCache("OrderHeader", UtilMisc.toMap("orderId", orderId));
            if (orderHeader != null)
                result.put("orderHeader", orderHeader);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get OrderHeader (" + e.getMessage() + ").");
            return result;
        }
        if (orderHeader == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could get the OrderHeader.");
            return result;
        }
        for (int i = 0; i < purpose.length; i++) {
            try {
                GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech",
                            UtilMisc.toMap("contactMechPurposeTypeId", purpose[i])));
                GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");

                if (contactMech != null) {
                    result.put(outKey[i], contactMech.getRelatedOne("PostalAddress"));
                }
            } catch (GenericEntityException e) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Problems getting contact mech (" + e.getMessage() + ").");
                return result;
            }
        }

        result.put("orderId", orderId);
        return result;
    }

    /** Service to create a order header note. */
    public static Map createOrderNote(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String noteString = (String) context.get("note");
        String orderId = (String) context.get("orderId");
        Map noteCtx = UtilMisc.toMap("note", noteString, "userLogin", userLogin);

        // Store the note.
        Map noteRes = org.ofbiz.common.CommonServices.createNote(dctx, noteCtx);

        if (noteRes.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
            return noteRes;

        String noteId = (String) noteRes.get("noteId");

        if (noteId == null || noteId.length() == 0) {
            return ServiceUtil.returnError("Problem creating the note, no noteId returned.");
        }

        // Set the order info
        try {
            Map fields = UtilMisc.toMap("orderId", orderId, "noteId", noteId);
            GenericValue v = delegator.makeValue("OrderHeaderNote", fields);

            delegator.create(v);
        } catch (GenericEntityException ee) {
            Debug.logError(ee, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem associating note with order (" + ee.getMessage() + ").");
        }

        return result;
    }

    /** Null tax calc service. */
    public static Map nullTaxCalc(DispatchContext dctx, Map context) {
        return UtilMisc.toMap("orderAdjustments", UtilMisc.toList(null), "itemAdjustments", UtilMisc.toList(null));
    }

    /** Simple tax calc service. */
    public static Map simpleTaxCalc(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        List itemProductList = (List) context.get("itemProductList");
        List itemAmountList = (List) context.get("itemAmountList");
        List itemShippingList = (List) context.get("itemShippingList");
        Double orderShippingAmount = (Double) context.get("orderShippingAmount");
        GenericValue shippingAddress = (GenericValue) context.get("shippingAddress");

        // Simple Tax Calc only uses the state from the address and the SalesTaxLookup entity.
        String countryCode = shippingAddress.getString("countryGeoId");
        String stateCode = shippingAddress.getString("stateProvinceGeoId");

        // Setup the return lists.
        List orderAdjustments = new ArrayList();
        List itemAdjustments = new ArrayList();

        // Loop through the products; get the taxCategory; and lookup each in the cache.
        for (int i = 0; i < itemProductList.size(); i++) {
            GenericValue product = (GenericValue) itemProductList.get(i);
            Double itemAmount = (Double) itemAmountList.get(i);
            Double shippingAmount = (Double) itemShippingList.get(i);
            List taxList = getTaxAmount(delegator, product, productStoreId, countryCode, stateCode, itemAmount.doubleValue(), shippingAmount.doubleValue());
            itemAdjustments.add(taxList);
        }
        if (orderShippingAmount.doubleValue() > 0) {
            List taxList = getTaxAmount(delegator, null, productStoreId, countryCode, stateCode, 0.00, orderShippingAmount.doubleValue());
            orderAdjustments.addAll(taxList);
        }

        Map result = UtilMisc.toMap("orderAdjustments", orderAdjustments, "itemAdjustments", itemAdjustments);

        return result;

    }

    private static List getTaxAmount(GenericDelegator delegator, GenericValue item, String productStoreId, String countryCode, String stateCode, double itemAmount, double shippingAmount) {
        List adjustments = new ArrayList();

        // store expr
        EntityCondition storeCond = new EntityExpr("productStoreId", EntityOperator.EQUALS, productStoreId);

        // build the country expressions
        List countryExprs = UtilMisc.toList(new EntityExpr("countryGeoId", EntityOperator.EQUALS, countryCode), new EntityExpr("countryGeoId", EntityOperator.EQUALS, "_NA_"));
        EntityCondition countryCond = new EntityConditionList(countryExprs, EntityOperator.OR);

        // build the state expression
        List stateExprs = UtilMisc.toList(new EntityExpr("stateProvinceGeoId", EntityOperator.EQUALS, stateCode), new EntityExpr("stateProvinceGeoId", EntityOperator.EQUALS, "_NA_"));
        EntityCondition stateCond = new EntityConditionList(stateExprs, EntityOperator.OR);

        // build the tax cat expression
        List taxCatExprs = UtilMisc.toList(new EntityExpr("taxCategory", EntityOperator.EQUALS, "_NA_"));
        if (item != null && item.get("taxCategory") != null) {
            taxCatExprs.add(new EntityExpr("taxCategory", EntityOperator.EQUALS, item.getString("taxCategory")));
        }
        EntityCondition taxCatCond = new EntityConditionList(taxCatExprs, EntityOperator.OR);

        // build the main condition clause
        List mainExprs = UtilMisc.toList(storeCond, countryCond, stateCond);
        if (taxCatExprs.size() > 1) {
            mainExprs.add(taxCatCond);
        } else {
            mainExprs.add(taxCatExprs.get(0));
        }
        EntityCondition mainCondition = new EntityConditionList(mainExprs, EntityOperator.AND);

        // create the orderby clause
        List orderList = UtilMisc.toList("minPurchase", "fromDate");

        try {
            List lookupList = delegator.findByCondition("SimpleSalesTaxLookup", mainCondition, null, orderList);
            List filteredList = EntityUtil.filterByDate(lookupList);

            if (filteredList.size() == 0) {
                Debug.logWarning("SimpleTaxCalc: No State/TaxCategory pair found (with or without taxCat).", module);
                return adjustments;
            }

            // find the right entry(s) based on purchase amount
            Iterator flIt = filteredList.iterator();
            while (flIt.hasNext()) {
                GenericValue taxLookup = (GenericValue) flIt.next();
                //Debug.logInfo("Testing " + itemAmount + " with : " + taxLookup, module);
                if (itemAmount >= taxLookup.getDouble("minPurchase").doubleValue()) {
                    //Debug.logInfo("TaxLookup: " + taxLookup, module);

                    double taxRate = taxLookup.get("salesTaxPercentage") != null ? taxLookup.getDouble("salesTaxPercentage").doubleValue() : 0;
                    double taxable = 0.00;

                    if (item != null && (item.get("taxable") == null || (item.get("taxable") != null && item.getBoolean("taxable").booleanValue()))) {
                        taxable += itemAmount;
                    }
                    if (taxLookup != null && (taxLookup.get("taxShipping") == null || (taxLookup.get("taxShipping") != null && taxLookup.getBoolean("taxShipping").booleanValue()))) {
                        taxable += shippingAmount;
                    }

                    String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
                    DecimalFormat formatter = new DecimalFormat(currencyFormat);
                    double taxTotal = taxable * taxRate;
                    String amountStr = formatter.format(taxTotal);
                    Double taxAmount = null;
                    try {
                        taxAmount = new Double(formatter.parse(amountStr).doubleValue());
                    } catch (ParseException e) {
                        throw new GeneralException("Problem getting parsed amount from string", e);
                    }

                    adjustments.add(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("amount", taxAmount, "orderAdjustmentTypeId", "SALES_TAX", "comments", taxLookup.getString("description"))));
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up tax rates", module);
            return new ArrayList();
        } catch (GeneralException e) {
            Debug.logError(e, "Problems looking up tax rates", module);
            return new ArrayList();
        }

        return adjustments;
    }

    // return / refund services

    // helper method for sending return notifications
    private static Map sendReturnNotification(DispatchContext ctx, Map context, String emailType) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String returnId = (String) context.get("returnId");

        // get the return header
        GenericValue returnHeader = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("ERROR: Unable to get ReturnHeader for ID: " + returnId);
        }

        // get the return items
        List returnItems = null;
        try {
            returnItems = returnHeader.getRelated("ReturnItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("ERROR: Unable to get ReturnItem records from ReturnHeader");
        }

        // set the email template context
        Map templateContext = UtilMisc.toMap("returnHeader", returnHeader, "returnItems", returnItems);

        // get the order header -- the first item will determine which product store to use from the order
        String productStoreId = null;
        String emailAddress = null;
        if (returnItems != null) {
            GenericValue firstItem = EntityUtil.getFirst(returnItems);
            GenericValue orderHeader = null;
            try {
                orderHeader = firstItem.getRelatedOne("OrderHeader");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("ERROR: Unable to get OrderHeader from ReturnItem");
            }

            if (orderHeader != null && UtilValidate.isNotEmpty(orderHeader.getString("productStoreId"))) {
                OrderReadHelper orh = new OrderReadHelper(orderHeader);
                productStoreId = orh.getProductStoreId();
                emailAddress = orh.getOrderEmailString();
            }
        }

        // get the email setting and send the mail
        if (productStoreId != null && productStoreId.length() > 0) {
            GenericValue emailSetting = null;
            try {
                emailSetting = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("ERROR: Unable to locate email setting : " + productStoreId + " / " + emailType);
            }

            if (emailSetting != null && emailAddress != null) {
                String subjectString = emailSetting.getString("subject");
                subjectString = FlexibleStringExpander.expandString(subjectString, templateContext);

                Map emailCtx = new HashMap();
                emailCtx.put("templateName", emailSetting.get("templatePath"));
                emailCtx.put("templateData", templateContext);
                emailCtx.put("sendTo", emailAddress);
                emailCtx.put("contentType", emailSetting.get("contentType"));
                emailCtx.put("sendFrom", emailSetting.get("fromAddress"));
                emailCtx.put("sendCc", emailSetting.get("ccAddress"));
                emailCtx.put("sendBcc", emailSetting.get("bccAddress"));
                emailCtx.put("subject", subjectString);
                emailCtx.put("userLogin", userLogin);

                // send off the email async so we will retry on failed attempts
                try {
                    dispatcher.runAsync("sendGenericNotificationEmail", emailCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem sending mail", module);
                    return ServiceUtil.returnError("Problem sending email");
                }

                // all done
                return ServiceUtil.returnSuccess();
            }
        }

        return ServiceUtil.returnError("ERROR: Problem with Product Store email setting : " + productStoreId + " / " + emailType);
    }

    // return request notification
    public static Map sendReturnAcceptNotification(DispatchContext ctx, Map context) {
        return sendReturnNotification(ctx, context, "PRDS_RTN_ACCEPT");
    }

    // return complete notification
    public static Map sendReturnCompleteNotification(DispatchContext ctx, Map context) {
        return sendReturnNotification(ctx, context, "PRDS_RTN_COMPLETE");
    }

    // return cancel notification
    public static Map sendReturnCancelNotification(DispatchContext ctx, Map context) {
        return sendReturnNotification(ctx, context, "PRDS_RTN_CANCEL");
    }

    // get the returnable quantiy for an order item
    public static Map getReturnableQuantity(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue orderItem = (GenericValue) context.get("orderItem");
        GenericValue product = null;
        if (orderItem.get("productId") != null) {
            try {
                product = orderItem.getRelatedOne("Product");
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get Product from OrderItem", module);
            }
        }

        // check returnable status
        boolean returnable = true;

        // first check returnable flag
        if (product != null && product.get("returnable") != null &&
                "N".equalsIgnoreCase(product.getString("returnable"))) {
            // the product is not returnable at all
            returnable = false;
        }

        // next check support discontinuation
        if (product != null && product.get("supportDiscontinuationDate") != null &&
                !UtilDateTime.nowTimestamp().before(product.getTimestamp("supportDiscontinuationDate"))) {
            // support discontinued either now or in the past
            returnable = false;
        }

        String itemStatus = orderItem.getString("statusId");
        double orderQty = orderItem.getDouble("quantity").doubleValue();

        // get the returnable quantity
        double returnableQuantity = 0.00;
        if (returnable && itemStatus.equals("ITEM_COMPLETED")) {
            List returnedItems = null;
            try {
                returnedItems = orderItem.getRelated("ReturnItem");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("ERROR: Unable to get return item information");
            }
            if (returnedItems == null || returnedItems.size() == 0) {
                returnableQuantity = orderQty;
            } else {
                double returnedQty = 0.00;
                Iterator ri = returnedItems.iterator();
                while (ri.hasNext()) {
                    GenericValue returnItem = (GenericValue) ri.next();
                    GenericValue returnHeader = null;
                    try {
                        returnHeader = returnItem.getRelatedOne("ReturnHeader");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError("ERROR: Unable to get return header from item");
                    }
                    String returnStatus = returnHeader.getString("statusId");
                    if (!returnStatus.equals("RETURN_CANCELLED")) {
                        returnedQty += returnItem.getDouble("returnQuantity").doubleValue();
                    }
                }
                if (returnedQty < orderQty) {
                    returnableQuantity = orderQty - returnedQty;
                }
            }
        }

        // get the returnable price
        double returnablePrice = 0.00;
        if (returnableQuantity > 0) {
            // get all order adjustments
            List orderAdjustments = null;
            try {
                orderAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderItem.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("ERROR: Unable to get order adjustments from item");
            }
            returnablePrice = OrderReadHelper.getOrderItemTotal(orderItem, orderAdjustments);
            returnablePrice = (returnablePrice / orderQty);
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("returnableQuantity", new Double(returnableQuantity));
        result.put("returnablePrice", new Double(returnablePrice));
        return result;
    }

    // get a map of returnable items (items not already returned) and quantities
    public static Map getReturnableItems(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("ERROR: Unable to get order information.");
        }

        Map returnable = new HashMap();
        if (orderHeader != null) {
            List orderItems = null;
            try {
                orderItems = orderHeader.getRelatedByAnd("OrderItem", UtilMisc.toMap("statusId", "ITEM_COMPLETED"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError("ERROR: Unable to get order item information.");
            }
            if (orderItems != null) {
                Iterator i = orderItems.iterator();
                while (i.hasNext()) {
                    GenericValue item = (GenericValue) i.next();
                    Map serviceResult = null;
                    try {
                        serviceResult = dispatcher.runSync("getReturnableQuantity", UtilMisc.toMap("orderItem", item));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError("ERROR: Unable to get the item returnable quantity.");
                    }
                    if (serviceResult.containsKey(ModelService.ERROR_MESSAGE)) {
                        return ServiceUtil.returnError((String) serviceResult.get(ModelService.ERROR_MESSAGE));
                    } else {
                        Map returnInfo = new HashMap();
                        returnInfo.put("returnableQuantity", serviceResult.get("returnableQuantity"));
                        returnInfo.put("returnablePrice", serviceResult.get("returnablePrice"));
                        returnable.put(item, returnInfo);
                    }
                }
            } else {
                return ServiceUtil.returnError("ERROR: No order items found.");
            }
        } else {
            return ServiceUtil.returnError("ERROR: Unable to find order header.");
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("returnableItems", returnable);
        return result;
    }

    // check return items status and update return header status
    public static Map checkReturnComplete(DispatchContext ctx, Map context) {
        //appears to not be used: LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String returnId = (String) context.get("returnId");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelated("ReturnItem");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError("Error getting ReturnHeader/Item information");
        }

        // if already completed just return
        if (returnHeader != null && returnHeader.get("statusId") != null) {
            String currentStatus = returnHeader.getString("statusId");
            if ("RETURN_COMPLETED".equals(currentStatus) || "RETURN_CANCELLED".equals(currentStatus)) {
                return ServiceUtil.returnSuccess();
            }
        }

        // now; to be used for all timestamps
        Timestamp now = UtilDateTime.nowTimestamp();

        List completedItems = new ArrayList();
        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            Iterator itemsIter = returnItems.iterator();
            while (itemsIter.hasNext()) {
                GenericValue item = (GenericValue) itemsIter.next();
                String itemStatus = item != null ? item.getString("statusId") : null;
                if (itemStatus != null) {
                    // both completed and cancelled items qualify for completed status change
                    if ("RETURN_COMPLETED".equals(itemStatus) || "RETURN_CANCELLED".equals(itemStatus)) {
                        completedItems.add(item);
                    }
                }
            }

            // if all items are completed/cancelled these should match
            if (completedItems.size() == returnItems.size()) {
                List toStore = new LinkedList();
                returnHeader.set("statusId", "RETURN_COMPLETED");
                toStore.add(returnHeader);

                // create the status change history and set it to be stored
                String returnStatusId = delegator.getNextSeqId("ReturnStatus").toString();
                GenericValue returnStatus = delegator.makeValue("ReturnStatus", UtilMisc.toMap("returnStatusId", returnStatusId));
                returnStatus.set("statusId", "RETURN_COMPLETED");
                returnStatus.set("returnId", returnId);
                returnStatus.set("statusDatetime", now);
                toStore.add(returnStatus);
                try {
                    delegator.storeAll(toStore);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("ERROR: Unable to create ReturnStatus history");
                }
            }

        }

        Map result = ServiceUtil.returnSuccess();
        result.put("statusId", returnHeader.get("statusId"));
        return result;
    }

    // credit (billingAccount) return
    public static Map processCreditReturn(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String returnId = (String) context.get("returnId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelatedByAnd("ReturnItem", UtilMisc.toMap("returnTypeId", "RTN_CREDIT"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError("Error getting ReturnHeader/Item information");
        }

        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            String billingAccountId = returnHeader.getString("billingAccountId");
            String fromPartyId = returnHeader.getString("fromPartyId");
            if (billingAccountId == null) {
                // create new BillingAccount w/ 0 balance
                try {
                    Map newBa = dispatcher.runSync("createBillingAccount", UtilMisc.toMap("accountLimit", new Double(0.00), "description", "Credit Account", "userLogin", userLogin));
                    if (!newBa.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                        billingAccountId = (String) newBa.get("billingAccountId");
                        if (billingAccountId != null) {
                            // set the role on the account
                            Map newBaR = dispatcher.runSync("createBillingAccountRole", UtilMisc.toMap("billingAccountId", billingAccountId, "partyId", fromPartyId, "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin));
                            if (newBaR.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                                Debug.logError("Error with createBillingAccountRole: " + newBaR.get(ModelService.ERROR_MESSAGE), module);
                                return ServiceUtil.returnError("Error with createBillingAccountRole: " + newBaR.get(ModelService.ERROR_MESSAGE));
                            }
                        }
                    } else {
                        Debug.logError("Error with createBillingAccount: " + newBa.get(ModelService.ERROR_MESSAGE), module);
                        return ServiceUtil.returnError("Error with createBillingAccount: " + newBa.get(ModelService.ERROR_MESSAGE));
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problems creating BillingAccount", module);
                    return ServiceUtil.returnError("Problems creating billing account");
                }
            }

            // double check; make sure we have a billingAccount
            if (billingAccountId == null) {
                Debug.logError("No available billing account, none was created", module);
                return ServiceUtil.returnError("No available billing account");
            }

            // now; to be used for all timestamps
            Timestamp now = UtilDateTime.nowTimestamp();

            // start the response creation
            String itemResponseId = delegator.getNextSeqId("ReturnItemResponse").toString();
            GenericValue itemResponse = delegator.makeValue("ReturnItemResponse", UtilMisc.toMap("returnItemResponseId", itemResponseId));

            // need a total for the credit
            List toBeStored = new ArrayList();
            double creditTotal = 0.00;
            Iterator itemsIter = returnItems.iterator();
            while (itemsIter.hasNext()) {
                GenericValue item = (GenericValue) itemsIter.next();
                Double quantity = item.getDouble("returnQuantity");
                Double price = item.getDouble("returnPrice");
                if (quantity == null) quantity = new Double(0);
                if (price == null) price = new Double(0);
                creditTotal += price.doubleValue() * quantity.doubleValue();

                // set the response on the item and flag the item to be stored
                item.set("returnItemResponseId", itemResponseId);
                item.set("statusId", "RETURN_COMPLETED");
                toBeStored.add(item);

                // create the status change history and set it to be stored
                String returnStatusId = delegator.getNextSeqId("ReturnStatus").toString();
                GenericValue returnStatus = delegator.makeValue("ReturnStatus", UtilMisc.toMap("returnStatusId", returnStatusId));
                returnStatus.set("statusId", item.get("statusId"));
                returnStatus.set("returnId", item.get("returnId"));
                returnStatus.set("returnItemSeqId", item.get("returnItemSeqId"));
                returnStatus.set("statusDatetime", now);
                toBeStored.add(returnStatus);
            }

            // create a Double object for the amount
            Double creditAmount = new Double(creditTotal);

            // create a Payment record for this credit; will look just like a normal payment
            String paymentId = delegator.getNextSeqId("Payment").toString();
            GenericValue payment = delegator.makeValue("Payment", UtilMisc.toMap("paymentId", paymentId));
            payment.set("paymentTypeId", "RECEIPT");
            payment.set("paymentMethodTypeId", "EXT_BILLACT");
            payment.set("partyIdFrom", fromPartyId);
            payment.set("partyIdTo", "Company"); // TODO: need to fix this and find a partyId to use
            payment.set("effectiveDate", now);
            payment.set("amount", creditAmount);
            payment.set("comments", "Return Credit");
            try {
                delegator.create(payment);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating Payment record", module);
                return ServiceUtil.returnError("Problem creating Payment record");
            }

            // create the PaymentApplication
            String paId = delegator.getNextSeqId("PaymentApplication").toString();
            GenericValue pa = delegator.makeValue("PaymentApplication", UtilMisc.toMap("paymentApplicationId", paId));
            pa.set("paymentId", paymentId);
            pa.set("billingAccountId", billingAccountId);
            pa.set("amountApplied", creditAmount);
            try {
                delegator.create(pa);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating PaymentApplication record", module);
                return ServiceUtil.returnError("Problem creating PaymentApplication record");
            }

            // fill in the response fields
            itemResponse.set("paymentId", paymentId);
            itemResponse.set("billingAccountId", billingAccountId);
            itemResponse.set("responseAmount", creditAmount);
            itemResponse.set("responseDate", now);
            try {
                delegator.create(itemResponse);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating ReturnItemResponse record", module);
                return ServiceUtil.returnError("Problem creating ReturnItemResponse record");
            }

            // store the item changes (attached responseId)
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem storing ReturnItem updates", module);
                return ServiceUtil.returnError("Problem storing ReturnItem updates");
            }
        }

        return ServiceUtil.returnSuccess();
    }

    // refund (cash/charge) return
    public static Map processRefundReturn(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String returnId = (String) context.get("returnId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelatedByAnd("ReturnItem", UtilMisc.toMap("returnTypeId", "RTN_REFUND"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError("Error getting ReturnHeader/Item information");
        }

        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            Map itemsByOrder = new HashMap();
            Map totalByOrder = new HashMap();
            groupReturnItemsByOrder(returnItems, itemsByOrder, totalByOrder);

            // process each one by order
            Set itemSet = itemsByOrder.entrySet();
            Iterator itemByOrderIt = itemSet.iterator();
            while (itemByOrderIt.hasNext()) {
                Map.Entry entry = (Map.Entry) itemByOrderIt.next();
                String orderId = (String) entry.getKey();
                List items = (List) entry.getValue();
                Double orderTotal = (Double) totalByOrder.get(orderId);

                // get order header & payment prefs
                GenericValue orderHeader = null;
                List orderPayPrefs = null;
                try {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                    // sort these desending by maxAmount
                    orderPayPrefs = orderHeader.getRelated("OrderPaymentPreference", null, UtilMisc.toList("-maxAmount"));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get Order details for #" + orderId, module);
                    continue;
                }

                // get the payment prefs to use (will use them in order of amount charged)
                List prefsToUse = new ArrayList();
                Map prefsAmount = new HashMap();
                double neededAmount = orderTotal.doubleValue();
                if (orderPayPrefs != null && orderPayPrefs.size() > 0) {
                    Iterator payPrefIter = orderPayPrefs.iterator();
                    do {
                        GenericValue pref = (GenericValue) payPrefIter.next();
                        Double maxAmount = pref.getDouble("maxAmount");
                        if (maxAmount == null || maxAmount.doubleValue() == 0.00) {
                            prefsToUse.add(pref);
                            prefsAmount.put(pref, orderTotal);
                            neededAmount = 0.00;
                        } else if (maxAmount.doubleValue() > orderTotal.doubleValue()) {
                            prefsToUse.add(pref);
                            prefsAmount.put(pref, orderTotal);
                            neededAmount = 0.00;
                        } else {
                            prefsToUse.add(pref);
                            if (maxAmount.doubleValue() > neededAmount) {
                                prefsAmount.put(pref, new Double(maxAmount.doubleValue() - neededAmount));
                            } else {
                                prefsAmount.put(pref, maxAmount);
                            }
                            neededAmount -= maxAmount.doubleValue();
                        }
                    } while (neededAmount > 0 && payPrefIter.hasNext());
                }

                if (neededAmount != 0) {
                    Debug.logError("Was not able to find needed payment preferences for the order RTN: " + returnId + " ORD: " + orderId, module);
                    continue;
                }

                Map prefSplitMap = new HashMap();
                if (prefsToUse == null || prefsToUse.size() == 0) {
                    Debug.logError("We didn't find any possible payment prefs to use for RTN: " + returnId + " ORD: " + orderId, module);
                    continue;
                } else if (prefsToUse.size() > 1) {
                    // we need to spit the items up to log which pref it was refunded to
                    // TODO: add the split of items for multiple payment prefs
                } else {
                    // single payment / single refund
                    prefSplitMap.put(prefsToUse.get(0), items);
                }

                // now process all items for each preference
                Set prefItemSet = prefSplitMap.entrySet();
                Iterator prefItemIt = prefItemSet.iterator();
                while (prefItemIt.hasNext()) {
                    Map.Entry prefItemEntry = (Map.Entry) prefItemIt.next();
                    GenericValue orderPayPref = (GenericValue) prefItemEntry.getKey();
                    List itemList = (List) prefItemEntry.getValue();

                    Double thisRefundAmount = (Double) prefsAmount.get(orderPayPref);
                    String paymentId = null;

                    // this can be extended to support additional electronic types
                    List electronicTypes = UtilMisc.toList("CREDIT_CARD", "EFT_ACCOUNT", "GIFT_CARD");
                    //List electronicTypes = new ArrayList();

                    if (electronicTypes.contains(orderPayPref.getString("paymentMethodTypeId"))) {
                        // call the refund service to refund the payment
                        try {
                            Map serviceResult = dispatcher.runSync("refundPayment", UtilMisc.toMap("orderPaymentPreference", orderPayPref, "refundAmount", thisRefundAmount, "userLogin", userLogin));
                            paymentId = (String) serviceResult.get("paymentId");
                        } catch (GenericServiceException e) {
                            Debug.logError(e, "Problem running the refundPayment service", module);
                            return ServiceUtil.returnError("Problems with the refund; see logs");
                        }
                    } else {
                        // TODO: handle manual refunds (accounts payable)
                    }

                    //Debug.log("Finished handing refund payments", module);

                    // now; for all timestamps
                    Timestamp now = UtilDateTime.nowTimestamp();

                    // create a new response entry
                    String responseId = delegator.getNextSeqId("ReturnItemResponse").toString();
                    GenericValue response = delegator.makeValue("ReturnItemResponse", UtilMisc.toMap("returnItemResponseId", responseId));
                    response.set("orderPaymentPreferenceId", orderPayPref.getString("orderPaymentPreferenceId"));
                    response.set("responseAmount", thisRefundAmount);
                    response.set("responseDate", now);
                    if (paymentId != null) {
                        // a null payment ID means no electronic refund was available; manual refund needed
                        response.set("paymentId", paymentId);
                    }

                    //Debug.log("About to create return response", module);

                    try {
                        delegator.create(response);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problems creating new ReturnItemResponse entity", module);
                        return ServiceUtil.returnError("Problems creating ReturnItemResponse entity");
                    }

                    //Debug.log("Return response created", module);

                    // set the response on each item
                    Iterator itemsIter = itemList.iterator();
                    while (itemsIter.hasNext()) {
                        GenericValue item = (GenericValue) itemsIter.next();
                        item.set("returnItemResponseId", responseId);
                        item.set("statusId", "RETURN_COMPLETED");

                        // create the status history
                        String returnStatusId = delegator.getNextSeqId("ReturnStatus").toString();
                        GenericValue returnStatus = delegator.makeValue("ReturnStatus", UtilMisc.toMap("returnStatusId", returnStatusId));
                        returnStatus.set("statusId", item.get("statusId"));
                        returnStatus.set("returnId", item.get("returnId"));
                        returnStatus.set("returnItemSeqId", item.get("returnItemSeqId"));
                        returnStatus.set("statusDatetime", now);

                        //Debug.log("Updating item status", module);
                        try {
                            item.store();
                            delegator.create(returnStatus);
                        } catch (GenericEntityException e) {
                            Debug.logError("Problem updating the ReturnItem entity", module);
                            return ServiceUtil.returnError("Problem updating ReturnItem (returnItemResponseId)");
                        }

                        //Debug.log("Item status and return status history created", module);
                    }
                }
            }
        }

        //Debug.log("Finished refund process");
        return ServiceUtil.returnSuccess();
    }

    public static Map processReplacementReturn(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        String returnId = (String) context.get("returnId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelatedByAnd("ReturnItem", UtilMisc.toMap("returnTypeId", "RTN_REPLACE"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError("Error getting ReturnHeader/Item information");
        }

        List createdOrderIds = new ArrayList();
        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            Map itemsByOrder = new HashMap();
            Map totalByOrder = new HashMap();
            groupReturnItemsByOrder(returnItems, itemsByOrder, totalByOrder);

            // process each one by order
            Set itemSet = itemsByOrder.entrySet();
            Iterator itemByOrderIt = itemSet.iterator();
            while (itemByOrderIt.hasNext()) {
                Map.Entry entry = (Map.Entry) itemByOrderIt.next();
                String orderId = (String) entry.getKey();
                List items = (List) entry.getValue();

                // get order header & payment prefs
                GenericValue orderHeader = null;
                try {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get Order details for #" + orderId, module);
                    continue;
                }

                OrderReadHelper orh = new OrderReadHelper(orderHeader);

                // create the replacement order
                Map orderMap = UtilMisc.toMap("userLogin", userLogin);
                GenericValue placingParty = orh.getPlacingParty();
                String placingPartyId = null;
                if (placingParty != null) {
                    placingPartyId = placingParty.getString("partyId");
                }

                orderMap.put("orderTypeId", "SALES_ORDER");
                orderMap.put("partyId", placingPartyId);
                orderMap.put("productStoreId", orderHeader.get("productStoreId"));
                orderMap.put("webSiteId", orderHeader.get("webSiteId"));
                orderMap.put("visitId", orderHeader.get("visitId"));
                orderMap.put("currencyUom", orderHeader.get("currencyUom"));
                orderMap.put("grandTotal",  new Double(0.00));

                // make the contact mechs
                List contactMechs = new ArrayList();
                List orderCm = null;
                try {
                    orderCm = orderHeader.getRelated("OrderContactMech");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
                if (orderCm != null) {
                    Iterator orderCmi = orderCm.iterator();
                    while (orderCmi.hasNext()) {
                        GenericValue v = (GenericValue) orderCmi.next();
                        contactMechs.add(new GenericValue(v));
                    }
                    orderMap.put("orderContactMechs", contactMechs);
                }

                // make the shipment prefs
                List shipmentPrefs = new ArrayList();
                List orderSp = null;
                try {
                    orderSp = orderHeader.getRelated("OrderShipmentPreference");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
                if (orderSp != null) {
                    Iterator orderSpi = orderSp.iterator();
                    while (orderSpi.hasNext()) {
                        GenericValue v = (GenericValue) orderSpi.next();
                        shipmentPrefs.add(new GenericValue(v));
                    }
                    orderMap.put("orderShipmentPreferences", shipmentPrefs);
                }

                // make the order items
                double itemTotal = 0.00;
                List orderItems = new ArrayList();
                if (items != null) {
                    Iterator ri = items.iterator();
                    int itemCount = 1;
                    while (ri.hasNext()) {
                        GenericValue returnItem = (GenericValue) ri.next();
                        GenericValue orderItem = null;
                        try {
                            orderItem = returnItem.getRelatedOne("OrderItem");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                            continue;
                        }
                        if (orderItem != null) {
                            Double quantity = returnItem.getDouble("returnQuantity");
                            Double unitPrice = returnItem.getDouble("returnPrice");
                            if (quantity != null && unitPrice != null) {
                                itemTotal = (quantity.doubleValue() * unitPrice.doubleValue());
                                GenericValue newItem = delegator.makeValue("OrderItem", UtilMisc.toMap("orderItemSeqId", new Integer(itemCount).toString()));

                                newItem.set("orderItemTypeId", "PRODUCT_ORDER_ITEM");
                                newItem.set("productId", orderItem.get("productId"));
                                newItem.set("productFeatureId", orderItem.get("productFeatureId"));
                                newItem.set("prodCatalogId", orderItem.get("prodCatalogId"));
                                newItem.set("productCategoryId", orderItem.get("productCategoryId"));
                                newItem.set("quantity", quantity);
                                newItem.set("unitPrice", unitPrice);
                                newItem.set("unitListPrice", orderItem.get("unitListPrice"));
                                newItem.set("itemDescription", orderItem.get("itemDescription"));
                                newItem.set("comments", orderItem.get("comments"));
                                newItem.set("correspondingPoId", orderItem.get("correspondingPoId"));
                                newItem.set("statusId", "ITEM_CREATED");
                                orderItems.add(newItem);
                            }
                        }
                    }
                    orderMap.put("orderItems", orderItems);
                } else {
                    Debug.logError("No return items found??", module);
                    continue;
                }

                // create the replacement adjustment
                GenericValue adj = delegator.makeValue("OrderAdjustment", new HashMap());
                adj.set("orderAdjustmentTypeId", "REPLACE_ADJUSTMENT");
                adj.set("amount", new Double(itemTotal * -1));
                adj.set("comments", "Replacement Item Return #" + returnId);
                orderMap.put("orderAdjustments", UtilMisc.toList(adj));

                // create the order
                String createdOrderId = null;
                Map orderResult = null;
                try {
                    orderResult = dispatcher.runSync("storeOrder", orderMap);
                } catch (GenericServiceException e) {
                    Debug.logInfo(e, "Problem creating the order!", module);
                }
                if (orderResult != null) {
                    createdOrderId = (String) orderResult.get("orderId");
                    createdOrderIds.add(createdOrderId);
                }

                // since there is no payments required; order is ready for processing/shipment
                if (createdOrderId != null) {
                    OrderChangeHelper.approveOrder(dispatcher, userLogin, createdOrderId);
                }
            }
        }

        StringBuffer successMessage = new StringBuffer();
        if (createdOrderIds.size() > 0) {
            successMessage.append("The following new orders have been created : ");
            Iterator i = createdOrderIds.iterator();
            while (i.hasNext()) {
                successMessage.append(i.next());
                if (i.hasNext()) {
                    successMessage.append(", ");
                }
            }
        } else {
            successMessage.append("No orders were created.");
        }

        return ServiceUtil.returnSuccess(successMessage.toString());
    }

    public static void groupReturnItemsByOrder(List returnItems, Map itemsByOrder, Map totalByOrder) {
        Iterator itemIt = returnItems.iterator();
        while (itemIt.hasNext()) {
            GenericValue item = (GenericValue) itemIt.next();
            String orderId = item.getString("orderId");
            if (orderId != null) {
                if (itemsByOrder != null) {
                    List orderList = (List) itemsByOrder.get(orderId);
                    Double totalForOrder = null;
                    if (totalByOrder != null) {
                        totalForOrder = (Double) totalByOrder.get(orderId);
                    }
                    if (orderList == null) {
                        orderList = new ArrayList();
                    }
                    if (totalForOrder == null) {
                        totalForOrder = new Double(0.00);
                    }

                    // add to the items list
                    orderList.add(item);
                    itemsByOrder.put(orderId, orderList);

                    if (totalByOrder != null) {
                        // add on the total for this line
                        Double quantity = item.getDouble("returnQuantity");
                        Double amount = item.getDouble("returnPrice");
                        if (quantity == null) {
                            quantity = new Double(0);
                        }
                        if (amount == null) {
                            amount = new Double(0.00);
                        }
                        double thisTotal = amount.doubleValue() * quantity.doubleValue();
                        double existingTotal = totalForOrder.doubleValue();
                        Double newTotal = new Double(existingTotal + thisTotal);
                        totalByOrder.put(orderId, newTotal);
                    }
                }
            }
        }
    }

    public static Map allowOrderSplit(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        if (orderItemSeqId == null) {
            orderItemSeqId = "_NA_";
        }

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError("You do not have permission to change this order's status.");
        }

        GenericValue shipPref = null;
        try {
            Map fields = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId);
            shipPref = delegator.findByPrimaryKey("OrderShipmentPreference", fields);
            if (shipPref == null) {
                fields.put("orderItemSeqId", "_NA_");
                shipPref = delegator.findByPrimaryKey("OrderShipmentPreference", fields);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting OrderShipmentPreference for : " + orderId + " / " + orderItemSeqId, module);
            return ServiceUtil.returnError("Cannot update; Problem getting OrderShipmentPreference");
        }

        if (shipPref != null) {
            shipPref.set("maySplit", "Y");
            try {
                shipPref.store();
            } catch (GenericEntityException e) {
                Debug.logError("Problem saving OrdeShipmentPreference for : " + orderId + " / " + orderItemSeqId, module);
                return ServiceUtil.returnError("Cannot update; Problem setting OrderShipmentPreference");
            }
        } else {
            Debug.logError("ERROR: Got a NULL OrderShipmentPreference", module);
            return ServiceUtil.returnError("Cannot update; No available preference to change");
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map cancelFlaggedSalesOrders(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        List ordersToCheck = null;
        List exprs = new ArrayList();

        // create the query expressions
        exprs.add(new EntityExpr("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
        exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"));
        exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
        exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));

        // get the orders
        try {
            ordersToCheck = delegator.findByAnd("OrderHeader", exprs, UtilMisc.toList("orderDate"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order headers", module);
        }

        if (ordersToCheck == null || ordersToCheck.size() == 0) {
            Debug.logInfo("No orders to check, finished", module);
            return ServiceUtil.returnSuccess();
        }

        Iterator i = ordersToCheck.iterator();
        while (i.hasNext()) {
            GenericValue orderHeader = (GenericValue) i.next();
            String orderId = orderHeader.getString("orderId");
            String orderStatus = orderHeader.getString("statusId");

            if (orderStatus.equals("ORDER_CREATED")) {
                // first check for un-paid orders
                Timestamp orderDate = orderHeader.getTimestamp("entryDate");

                // need the store for the order
                GenericValue productStore = null;
                try {
                    productStore = orderHeader.getRelatedOne("ProductStore");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to get ProductStore from OrderHeader", module);
                }

                // default days to cancel
                int daysTillCancel = 30;

                // get the value from the store
                if (productStore != null && productStore.get("daysToCancelNonPay") != null) {
                    daysTillCancel = productStore.getLong("daysToCancelNonPay").intValue();
                }

                if (daysTillCancel > 0) {
                    // 0 days means do not auto-cancel
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(orderDate.getTime());
                    cal.add(Calendar.DAY_OF_YEAR, daysTillCancel);
                    Date cancelDate = cal.getTime();
                    Date nowDate = new Date();
                    //Debug.log("Cancel Date : " + cancelDate, module);
                    //Debug.log("Current Date : " + nowDate, module);
                    if (cancelDate.equals(nowDate) || nowDate.after(cancelDate)) {
                        // cancel the order item(s)
                        Map svcCtx = UtilMisc.toMap("orderId", orderId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                        try {
                            Map ores = dispatcher.runSync("changeOrderItemStatus", svcCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, "Problem calling change item status service : " + svcCtx, module);
                        }
                    }
                }
            } else {
                // check for auto-cancel items
                List orderItems = null;
                try {
                    orderItems = orderHeader.getRelated("OrderItem");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting order item records", module);
                }
                if (orderItems != null && orderItems.size() > 0) {
                    Iterator oii = orderItems.iterator();
                    while (oii.hasNext()) {
                        GenericValue orderItem = (GenericValue) oii.next();
                        String orderItemSeqId = orderItem.getString("orderItemSeqId");
                        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                        Timestamp autoCancelDate = orderItem.getTimestamp("autoCancelDate");
                        Timestamp dontCancelDate = orderItem.getTimestamp("dontCancelSetDate");
                        String dontCancelUserLogin = orderItem.getString("dontCancelSetUserLogin");

                        if (dontCancelUserLogin == null && dontCancelDate == null && autoCancelDate != null) {
                            if (autoCancelDate.equals(nowTimestamp) || autoCancelDate.after(nowTimestamp)) {
                                // cancel the order item
                                Map svcCtx = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                                try {
                                    Map res = dispatcher.runSync("changeOrderItemStatus", svcCtx);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, "Problem calling change item status service : " + svcCtx, module);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map checkDigitalItemFulfillment(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");

        // need the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Unable to get OrderHeader for orderId : " + orderId, module);
            return ServiceUtil.returnError("ERROR: Unable to get OrderHeader for orderId : " + orderId);
        }

        // get all the items for the order
        List orderItems = null;
        if (orderHeader != null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get OrderItem list for orderId : " + orderId, module);
                return ServiceUtil.returnError("ERROR: Unable to get OrderItem list for orderId : " + orderId);
            }
        }

        // find any digital goods
        Map digitalProducts = new HashMap();
        List digitalItems = new ArrayList();
        if (orderItems != null && orderItems.size() > 0) {
            Iterator i = orderItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Unable to get Product from OrderItem", module);
                }
                if (product != null) {
                    String productType = product.getString("productTypeId");
                    // check for digital and finished/digital goods
                    if ("DIGITAL_GOOD".equals(productType) || "FINDIG_GOOD".equals(productType)) {
                        // we only invoice APPROVED items
                        if ("ITEM_APPROVED".equals(item.getString("statusId"))) {
                            digitalItems.add(item);
                        }
                        if ("DIGITAL_GOOD".equals(productType)) {
                            // 100% digital goods need status change
                            digitalProducts.put(item, product);
                        }
                    }
                }
            }
        }

        // now process the digital items
        if (digitalItems.size() > 0) {
            // invoice all APPROVED digital goods
            Map invoiceContext = UtilMisc.toMap("orderId", orderId, "billItems", digitalItems, "userLogin", userLogin);
            Map invoiceResult = null;
            try {
                invoiceResult = dispatcher.runSync("createInvoiceForOrder", invoiceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "ERROR: Unable to invoice digital items", module);
                return ServiceUtil.returnError("Problem with invoice creation; digital items not fulfilled.");
            }
            if (ModelService.RESPOND_ERROR.equals(invoiceResult.get(ModelService.RESPONSE_MESSAGE))) {
                return ServiceUtil.returnError((String) invoiceResult.get(ModelService.ERROR_MESSAGE));
            }

            // update the status of DIGITAL_GOOD to COMPLETED; leave FINDIG as APPROVED for pick/ship
            Iterator dii = digitalItems.iterator();
            while (dii.hasNext()) {
                GenericValue item = (GenericValue) dii.next();
                GenericValue product = (GenericValue) digitalProducts.get(item);
                if (product != null) {
                    // we were set as a digital good; one more check and change status
                    if ("DIGITAL_GOOD".equals(product.getString("productTypeId"))) {
                        Map statusCtx = new HashMap();
                        statusCtx.put("orderId", item.getString("orderId"));
                        statusCtx.put("orderItemSeqId", item.getString("orderItemSeqId"));
                        statusCtx.put("statusId", "ITEM_COMPLETED");
                        statusCtx.put("userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, "ERROR: Problem setting the status to COMPLETED : " + item, module);
                        }
                    }
                }
            }

            // fulfill the digital goods
            Map fulfillContext = UtilMisc.toMap("orderId", orderId, "orderItems", digitalItems, "userLogin", userLogin);
            Map fulfillResult = null;
            try {
                // will be running in an isolated transaction to prevent rollbacks
                fulfillResult = dispatcher.runSync("fulfillDigitalItems", fulfillContext, 300, true);
            } catch (GenericServiceException e) {
                Debug.logError(e, "ERROR: Unable to fulfill digital items", module);
            }
            if (ModelService.RESPOND_ERROR.equals(fulfillResult.get(ModelService.RESPONSE_MESSAGE))) {
                // this service cannot return error at this point or we will roll back the invoice
                // since payments are already captured; errors should have been logged already.
                // the response message here will be passed as an error to the user.
                return ServiceUtil.returnSuccess((String)fulfillResult.get(ModelService.ERROR_MESSAGE));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map fulfillDigitalItems(DispatchContext ctx, Map context) {
        //appears to not be used: GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        //appears to not be used: String orderId = (String) context.get("orderId");
        List orderItems = (List) context.get("orderItems");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        if (orderItems != null && orderItems.size() > 0) {
            // loop through the digital items to fulfill
            Iterator itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = (GenericValue) itemsIterator.next();

                // make sure we have a valid item
                if (orderItem == null) {
                    return ServiceUtil.returnError("ERROR: Cannot check for fulfillment; item not found.");
                }

                // locate the Product & ProductContent records
                GenericValue product = null;
                List productContent = null;
                try {
                    product = orderItem.getRelatedOne("Product");
                    if (product == null) {
                        return ServiceUtil.returnError("ERROR: Cannot check for fulfillment; product not found.");
                    }

                    List allProductContent = product.getRelated("ProductContent");
                    if (allProductContent != null && allProductContent.size() > 0) {
                        // only keep ones with valid dates
                        productContent = EntityUtil.filterByDate(allProductContent, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
                        Debug.logInfo("Product has " + allProductContent.size() + " associations, " +
                                (productContent == null ? "0" : "" + productContent.size()) + " has valid from/thru dates", module);
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError("ERROR: Cannot get Product entity: " + e.getMessage());
                }

                // now use the ProductContent to fulfill the item
                if (productContent != null && productContent.size() > 0) {
                    Iterator prodcontentIterator = productContent.iterator();
                    while (prodcontentIterator.hasNext()) {
                        GenericValue productContentItem = (GenericValue) prodcontentIterator.next();
                        GenericValue content = null;
                        try {
                            content = productContentItem.getRelatedOne("Content");
                        } catch (GenericEntityException e) {
                            Debug.logError(e,"ERROR: Cannot get Content entity: " + e.getMessage(),module);
                            continue;
                        }

                        String fulfillmentType = productContentItem.getString("productContentTypeId");
                        if ("FULFILLMENT_EXTASYNC".equals(fulfillmentType) || "FULFILLMENT_EXTSYNC".equals(fulfillmentType)) {
                            // enternal service fulfillment
                            String fulfillmentService = (String) content.get("serviceName");
                            if (fulfillmentService == null) {
                                Debug.logError("ProductContent of type FULFILLMENT_EXTERNAL had Content with empty serviceName, can not run fulfillment", module);
                            }
                            Map serviceCtx = UtilMisc.toMap("userLogin", userLogin, "orderItem", orderItem);
                            serviceCtx.putAll(productContentItem.getPrimaryKey());
                            try {
                                Debug.logInfo("Running external fulfillment '" + fulfillmentService + "'", module);
                                if ("FULFILLMENT_EXTASYNC".equals(fulfillmentType)) {
                                    dispatcher.runAsync(fulfillmentService, serviceCtx, true);
                                } else if ("FULFILLMENT_EXTSYNC".equals(fulfillmentType)) {
                                    Map resp = dispatcher.runSync(fulfillmentService, serviceCtx);
                                    if (ServiceUtil.isError(resp)) {
                                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resp));
                                    }
                                }
                            } catch (GenericServiceException e) {
                                Debug.logError(e, "ERROR: Could not run external fulfillment service '" + fulfillmentService + "'; " + e.getMessage(), module);
                            }
                        } else if("FULFILLMENT_EMAIL".equals(fulfillmentType)) {
                            // digital email fulfillment
                            // TODO: Add support for fulfillment email
                            return ServiceUtil.returnError("Email Fulfillment type not yet implemented");
                        } else if("DIGITAL_DOWNLOAD".equals(fulfillmentType)) {
                            // digital download fulfillment

                            // Nothing to do for here. Downloads are made available to the user
                            // though a query of OrderItems with related ProductContent.
                        } else {
                            Debug.logError("Invalid fulfillment type : " + fulfillmentType + " not supported.", module);
                        }
                    }
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }

    // sample test services
    public static Map shoppingCartTest(DispatchContext dctx, Map context) {
        Locale locale = (Locale) context.get("locale");
        ShoppingCart cart = new ShoppingCart(dctx.getDelegator(), "9000", "webStore", locale, "USD");
        try {
            cart.addOrIncreaseItem("GZ-1005", 1, null, null, "DemoCatalog", dctx.getDispatcher());
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
        }

        try {
            dctx.getDispatcher().runAsync("shoppingCartRemoteTest", UtilMisc.toMap("cart", cart), true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map shoppingCartRemoteTest(DispatchContext dctx, Map context) {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        Debug.log("Product ID : " + cart.findCartItem(0).getProductId(), module);
        return ServiceUtil.returnSuccess();
    }
}
