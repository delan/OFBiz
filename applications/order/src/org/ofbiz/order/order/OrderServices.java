/*
 * $Id: OrderServices.java 7902 2006-06-29 15:31:01Z jacopo $
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.workflow.WfUtil;

/**
 * Order Processing Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.0
 */

public class OrderServices {

    public static final String module = OrderServices.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    public static Map salesAttributeRoleMap = FastMap.newInstance();
    public static Map purchaseAttributeRoleMap = FastMap.newInstance();
    static {
        salesAttributeRoleMap.put("placingCustomerPartyId", "PLACING_CUSTOMER");
        salesAttributeRoleMap.put("billToCustomerPartyId", "BILL_TO_CUSTOMER");
        salesAttributeRoleMap.put("billFromVendorPartyId", "BILL_FROM_VENDOR");
        salesAttributeRoleMap.put("shipToCustomerPartyId", "SHIP_TO_CUSTOMER");
        salesAttributeRoleMap.put("endUserCustomerPartyId", "END_USER_CUSTOMER");

        purchaseAttributeRoleMap.put("billToCustomerPartyId", "BILL_TO_CUSTOMER");
        purchaseAttributeRoleMap.put("billFromVendorPartyId", "BILL_FROM_VENDOR");
        purchaseAttributeRoleMap.put("shipFromVendorPartyId", "SHIP_FROM_VENDOR");
        purchaseAttributeRoleMap.put("supplierAgentPartyId", "SUPPLIER_AGENT");
    }

    /** Service for creating a new order */
    public static Map createOrder(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        List toBeStored = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        Map successResult = ServiceUtil.returnSuccess();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // get the order type
        String orderTypeId = (String) context.get("orderTypeId");
        String partyId = (String) context.get("partyId");
        String billFromVendorPartyId = (String) context.get("billFromVendorPartyId");

        // check security permissions for order:
        //  SALES ORDERS - if userLogin has ORDERMGR_SALES_CREATE or ORDERMGR_CREATE permission, or if it is same party as the partyId, or
        //                 if it is an AGENT (sales rep) creating an order for his customer
        //  PURCHASE ORDERS - if there is a PURCHASE_ORDER permission
        Map resultSecurity = new HashMap();
        boolean hasPermission = false;
        if (orderTypeId.equals("SALES_ORDER")) {
            if (security.hasEntityPermission("ORDERMGR", "_SALES_CREATE", userLogin)) {
                hasPermission = true;
            } else {
                // check sales agent/customer relationship
                List repsCustomers = new LinkedList();
                try {
                    repsCustomers = EntityUtil.filterByDate(userLogin.getRelatedOne("Party").getRelatedByAnd("FromPartyRelationship",
                            UtilMisc.toMap("roleTypeIdFrom", "AGENT", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId)));
                } catch (GenericEntityException ex) {
                    Debug.logError("Could not determine if " + partyId + " is a customer of user " + userLogin.getString("userLoginId") + " due to " + ex.getMessage(), module);
                }
                if ((repsCustomers != null) && (repsCustomers.size() > 0) && (security.hasEntityPermission("SALESREP", "_ORDER_CREATE", userLogin))) {
                    hasPermission = true;
                }
            }
        } else if ((orderTypeId.equals("PURCHASE_ORDER") && (security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", userLogin)))) {
            hasPermission = true;
        }
        // final check - will pass if userLogin's partyId = partyId for order or if userLogin has ORDERMGR_CREATE permission
        if (!hasPermission) {
            partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, resultSecurity, "ORDERMGR", "_CREATE");
            if (resultSecurity.size() > 0) {
                return resultSecurity;
            }
        }

        // get the product store for the order, but it is required only for sales orders
        String productStoreId = (String) context.get("productStoreId");
        GenericValue productStore = null;
        if ((orderTypeId.equals("SALES_ORDER")) && (UtilValidate.isNotEmpty(productStoreId))) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotFindProductStoreWithID",UtilMisc.toMap("productStoreId",productStoreId),locale)  + e.toString());
            }
        }

        // figure out if the order is immediately fulfilled based on product store settings
        boolean isImmediatelyFulfilled = false;
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        successResult.put("orderTypeId", orderTypeId);

        // lookup the order type entity
        GenericValue orderType = null;
        try {
            orderType = delegator.findByPrimaryKeyCache("OrderType", UtilMisc.toMap("orderTypeId", orderTypeId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorOrderTypeLookupFailed",locale) + e.toString());
        }

        // make sure we have a valid order type
        if (orderType == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorInvalidOrderTypeWithID", UtilMisc.toMap("orderTypeId",orderTypeId), locale));
        }

        // check to make sure we have something to order
        List orderItems = (List) context.get("orderItems");
        if (orderItems.size() < 1) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "items.none", locale));
        }

        // all this marketing pkg auto stuff is deprecated in favor of MARKETING_PKG_AUTO productTypeId and a BOM of MANUF_COMPONENT assocs
        // these need to be retrieved now because they might be needed for exploding MARKETING_PKG_AUTO
        List orderAdjustments = (List) context.get("orderAdjustments");
        List orderItemShipGroupInfo = (List) context.get("orderItemShipGroupInfo");
        List orderItemPriceInfo = (List) context.get("orderItemPriceInfos");

        // explode items which are MARKETINGG_PKG_AUTO
        /*
        if (!orderTypeId.equals("PURCHASE_ORDER")) {
            try {
                explodeMarketingPkgAutoItem(orderItems, orderAdjustments, orderItemShipGroupInfo, orderItemPriceInfo, orderTypeId, delegator, dispatcher, locale);
            } catch (Exception e) {
               Debug.logError(e, "Error calling explodeMarketingPkgAutoItem " + e.getMessage(), module);
               return ServiceUtil.returnError("Error on exploding marketing_pkg_auto item.[" + e.toString() + "]");
            }
        }
        */
        // check inventory and other things for each item
        List errorMessages = FastList.newInstance();
        Map normalizedItemQuantities = FastMap.newInstance();
        Map normalizedItemNames = FastMap.newInstance();
        Map itemValuesBySeqId = FastMap.newInstance();
        Iterator itemIter = orderItems.iterator();
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        //
        // need to run through the items combining any cases where multiple lines refer to the
        // same product so the inventory check will work correctly
        // also count quantities ordered while going through the loop
        while (itemIter.hasNext()) {
            GenericValue orderItem = (GenericValue) itemIter.next();

            // start by putting it in the itemValuesById Map
            itemValuesBySeqId.put(orderItem.getString("orderItemSeqId"), orderItem);

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
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCallingCountProductQuantityOrderedService",locale) + e1.toString());
                }
            }
        }

        if (!"PURCHASE_ORDER".equals(orderTypeId) && productStoreId == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorTheProductStoreIdCanOnlyBeNullForPurchaseOrders",locale));
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

            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if introductionDate hasn't passed yet
                if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
                    String excMsg = UtilProperties.getMessage(resource, "product.not_yet_for_sale",
                            new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if salesDiscontinuationDate has passed
                if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
                    String excMsg = UtilProperties.getMessage(resource, "product.no_longer_for_sale",
                            new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if we have inventory available
                try {
                    Map invReqResult = dispatcher.runSync("isStoreInventoryAvailableOrNotRequired", UtilMisc.toMap("productStoreId", productStoreId, "productId", product.get("productId"), "product", product, "quantity", currentQuantity));
                    if (ServiceUtil.isError(invReqResult)) {
                        errorMessages.add(invReqResult.get(ModelService.ERROR_MESSAGE));
                        errorMessages.addAll((List) invReqResult.get(ModelService.ERROR_MESSAGE_LIST));
                    } else if (!"Y".equals((String) invReqResult.get("availableOrNotRequired"))) {
                        String invErrMsg = UtilProperties.getMessage(resource, "product.out_of_stock",
                                new Object[] { getProductName(product, itemName), currentProductId }, locale);
                        Debug.logWarning(invErrMsg, module);
                        errorMessages.add(invErrMsg);
                        continue;
                    }
                } catch (GenericServiceException e) {
                    String errMsg = "Fatal error calling inventory checking services: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    errorMessages.add(errMsg);
                }
            }
        }

        // add the fixedAsset id to the workefforts map by obtaining the fixed Asset number from the FixedAssetProduct table
        List workEfforts = (List) context.get("workEfforts"); // is an optional parameter from this service but mandatory for rental items
        Iterator orderItemIter = orderItems.iterator();
        while (orderItemIter.hasNext()) {
            GenericValue orderItem = (GenericValue) orderItemIter.next();
            if ("RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {
                // check to see if workefforts are available for this order type.
                if (workEfforts == null || workEfforts.size() == 0)    {
                    String errMsg = "Work Efforts missing for ordertype RENTAL_ORDER_ITEM " + "Product: "  + orderItem.getString("productId");
                    Debug.logError(errMsg, module);
                    errorMessages.add(errMsg);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderRentalOrderItems",locale));
                }
                Iterator we = workEfforts.iterator();  // find the related workEffortItem (workEffortId = orderSeqId)
                while (we.hasNext()) {
                    // create the entity maps required.
                    GenericValue workEffort = (GenericValue) we.next();
                    if (workEffort.getString("workEffortId").equals(orderItem.getString("orderItemSeqId")))    {
                        List selFixedAssetProduct = null;
                        try {
                            List allFixedAssetProduct = delegator.findByAnd("FixedAssetProduct",UtilMisc.toMap("productId",orderItem.getString("productId"),"fixedAssetProductTypeId", "FAPT_USE"));
                            selFixedAssetProduct = EntityUtil.filterByDate(allFixedAssetProduct, nowTimestamp, "fromDate", "thruDate", true);
                        } catch (GenericEntityException e) {
                            String excMsg = "Could not find related Fixed Asset for the product: " + orderItem.getString("productId");
                            Debug.logError(excMsg, module);
                            errorMessages.add(excMsg);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCouldNotFindRelatedFixedAssetForTheProduct",UtilMisc.toMap("productId",orderItem.getString("productId")), locale ));
                        }
                        if (selFixedAssetProduct != null && selFixedAssetProduct.size() > 0) {
                            Iterator firstOne = selFixedAssetProduct.iterator();
                            if(firstOne.hasNext())        {
                                GenericValue fixedAssetProduct = delegator.makeValue("FixedAssetProduct", null);
                                fixedAssetProduct = (GenericValue) firstOne.next();
                                workEffort.set("fixedAssetId",fixedAssetProduct.get("fixedAssetId"));
                                workEffort.set("quantityToProduce",orderItem.get("quantity")); // have quantity easy available later...
                            }
                        }
                        break;  // item found, so go to next orderitem.
                    }
                }
            }
        }

        if (errorMessages.size() > 0) {
            return ServiceUtil.returnError(errorMessages);
        }

        // the inital status for ALL order types
        String initialStatus = "ORDER_CREATED";
        successResult.put("statusId", initialStatus);

        // create the order object
        String orderId = null;
        String orgPartyId = null;
        if (productStore != null) {
            orgPartyId = productStore.getString("payToPartyId");
        } else if (billFromVendorPartyId != null) {
            orgPartyId = billFromVendorPartyId;
        }
        
        if (UtilValidate.isNotEmpty(orgPartyId)) {
            Map getNextOrderIdContext = UtilMisc.toMap("partyId", orgPartyId, "userLogin", userLogin);

            if ((orderTypeId.equals("SALES_ORDER")) || (productStoreId != null)) {
                getNextOrderIdContext.put("productStoreId", productStoreId);
            }
            
            try {
                Map getNextOrderIdResult = dispatcher.runSync("getNextOrderId", getNextOrderIdContext);
                if (ServiceUtil.isError(getNextOrderIdResult)) {
                    return ServiceUtil.returnError("Error getting next orderId while creating order", null, null, getNextOrderIdResult);
                }
                
                orderId = (String) getNextOrderIdResult.get("orderId");
            } catch (GenericServiceException e) {
                String errMsg = "Error creating order while getting orderId: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }

        if (UtilValidate.isEmpty(orderId)) {
            // for purchase orders or when other orderId generation fails, a product store id should not be required to make an order
            orderId = delegator.getNextSeqId("OrderHeader");
        }
        
        String billingAccountId = (String) context.get("billingAccountId");
        Timestamp orderDate = (Timestamp) context.get("orderDate");
        if (orderDate == null) {
            orderDate = nowTimestamp;
        }

        Map orderHeaderMap = UtilMisc.toMap("orderId", orderId, "orderTypeId", orderTypeId,
                "orderDate", orderDate, "entryDate", nowTimestamp,
                "statusId", initialStatus, "billingAccountId", billingAccountId);
        orderHeaderMap.put("orderName", context.get("orderName"));
        if (isImmediatelyFulfilled) {
            // also flag this order as needing inventory issuance so that when it is set to complete it will be issued immediately (needsInventoryIssuance = Y)
            orderHeaderMap.put("needsInventoryIssuance", "Y");
        }
        GenericValue orderHeader = delegator.makeValue("OrderHeader", orderHeaderMap);

        // determine the sales channel
        String salesChannelEnumId = (String) context.get("salesChannelEnumId");
        if ((salesChannelEnumId == null) || salesChannelEnumId.equals("UNKNWN_SALES_CHANNEL")) {
            // try the default store sales channel
            if (orderTypeId.equals("SALES_ORDER") && (productStore != null)) {
                salesChannelEnumId = productStore.getString("defaultSalesChannelEnumId");
            }
            // if there's still no channel, set to unknown channel
            if (salesChannelEnumId == null) {
                salesChannelEnumId = "UNKNWN_SALES_CHANNEL";
            }
        }
        orderHeader.set("salesChannelEnumId", salesChannelEnumId);

        if (context.get("currencyUom") != null) {
            orderHeader.set("currencyUom", context.get("currencyUom"));
        }

        if (context.get("firstAttemptOrderId") != null) {
            orderHeader.set("firstAttemptOrderId", context.get("firstAttemptOrderId"));
        }

        if (context.get("grandTotal") != null) {
            orderHeader.set("grandTotal", context.get("grandTotal"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("visitId"))) {
            orderHeader.set("visitId", context.get("visitId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("internalCode"))) {
            orderHeader.set("internalCode", context.get("internalCode"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("externalId"))) {
            orderHeader.set("externalId", context.get("externalId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("originFacilityId"))) {
            orderHeader.set("originFacilityId", context.get("originFacilityId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("productStoreId"))) {
            orderHeader.set("productStoreId", context.get("productStoreId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("transactionId"))) {
            orderHeader.set("transactionId", context.get("transactionId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("terminalId"))) {
            orderHeader.set("terminalId", context.get("terminalId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("autoOrderShoppingListId"))) {
            orderHeader.set("autoOrderShoppingListId", context.get("autoOrderShoppingListId"));
        }

        if (UtilValidate.isNotEmpty((String) context.get("webSiteId"))) {
            orderHeader.set("webSiteId", context.get("webSiteId"));
        }

        if (userLogin != null && userLogin.get("userLoginId") != null) {
            orderHeader.set("createdBy", userLogin.getString("userLoginId"));
        }

        // first try to create the OrderHeader; if this does not fail, continue.
        try {
            delegator.create(orderHeader);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create OrderHeader entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderOrderCreationFailedPleaseNotifyCustomerService",locale));
        }

        // create the order status record
        String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
        GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
        orderStatus.set("orderId", orderId);
        orderStatus.set("statusId", orderHeader.getString("statusId"));
        orderStatus.set("statusDatetime", nowTimestamp);
        orderStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
        toBeStored.add(orderStatus);

        // before processing orderItems process orderItemGroups so that they'll be in place for the foreign keys and what not
        List orderItemGroups = (List) context.get("orderItemGroups");
        if (orderItemGroups != null && orderItemGroups.size() > 0) {
            Iterator orderItemGroupIter = orderItemGroups.iterator();
            while (orderItemGroupIter.hasNext()) {
                GenericValue orderItemGroup = (GenericValue) orderItemGroupIter.next();
                orderItemGroup.set("orderId", orderId);
                toBeStored.add(orderItemGroup);
            }
        }
        
        // set the order items
        Iterator oi = orderItems.iterator();
        while (oi.hasNext()) {
            GenericValue orderItem = (GenericValue) oi.next();
            orderItem.set("orderId", orderId);
            toBeStored.add(orderItem);

            // create the item status record
            String itemStatusId = delegator.getNextSeqId("OrderStatus");
            GenericValue itemStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", itemStatusId));
            itemStatus.put("statusId", orderItem.get("statusId"));
            itemStatus.put("orderId", orderId);
            itemStatus.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            itemStatus.put("statusDatetime", nowTimestamp);
            itemStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
            toBeStored.add(itemStatus);
        }

        // set the order attributes
        List orderAttributes = (List) context.get("orderAttributes");
        if (orderAttributes != null && orderAttributes.size() > 0) {
            Iterator oattr = orderAttributes.iterator();
            while (oattr.hasNext()) {
                GenericValue oatt = (GenericValue) oattr.next();
                oatt.set("orderId", orderId);
                toBeStored.add(oatt);
            }
        }

        // set the order item attributes
        List orderItemAttributes = (List) context.get("orderItemAttributes");
        if (orderItemAttributes != null && orderItemAttributes.size() > 0) {
            Iterator oiattr = orderItemAttributes.iterator();
            while (oiattr.hasNext()) {
                GenericValue oiatt = (GenericValue) oiattr.next();
                oiatt.set("orderId", orderId);
                toBeStored.add(oiatt);
            }
        }

        // create the order internal notes
        List orderInternalNotes = (List) context.get("orderInternalNotes");
        if (orderInternalNotes != null && orderInternalNotes.size() > 0) {
            Iterator orderInternalNotesIt = orderInternalNotes.iterator();
            while (orderInternalNotesIt.hasNext()) {
                String orderInternalNote = (String) orderInternalNotesIt.next();
                try {
                    Map noteOutputMap = dispatcher.runSync("createOrderNote", UtilMisc.toMap("orderId", orderId, 
                                                                                             "internalNote", "Y",
                                                                                             "note", orderInternalNote,
                                                                                             "userLogin", userLogin));
                    if (ServiceUtil.isError(noteOutputMap)) {
                        return ServiceUtil.returnError("Error creating internal notes while creating order", null, null, noteOutputMap);
                    }
                } catch (GenericServiceException e) {
                    String errMsg = "Error creating internal notes while creating order: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }

        // create the order public notes
        List orderNotes = (List) context.get("orderNotes");
        if (orderNotes != null && orderNotes.size() > 0) {
            Iterator orderNotesIt = orderNotes.iterator();
            while (orderNotesIt.hasNext()) {
                String orderNote = (String) orderNotesIt.next();
                try {
                    Map noteOutputMap = dispatcher.runSync("createOrderNote", UtilMisc.toMap("orderId", orderId,
                                                                                             "internalNote", "N",
                                                                                             "note", orderNote,
                                                                                             "userLogin", userLogin));
                    if (ServiceUtil.isError(noteOutputMap)) {
                        return ServiceUtil.returnError("Error creating notes while creating order", null, null, noteOutputMap);
                    }
                } catch (GenericServiceException e) {
                    String errMsg = "Error creating notes while creating order: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }

        // create the workeffort records
        // and connect them with the orderitem over the WorkOrderItemFulfillment
        // create also the techData calendars to keep track of availability of the fixed asset.
        if (workEfforts != null && workEfforts.size() > 0) {
            Iterator we = workEfforts.iterator();
            while (we.hasNext()) {
                // create the entity maps required.
                GenericValue workEffort = (GenericValue) we.next();
                GenericValue workOrderItemFulfillment = delegator.makeValue("WorkOrderItemFulfillment", null);
                // find fixed asset supplied on the workeffort map
                GenericValue fixedAsset = null;
                Debug.logInfo("find the fixedAsset",module);
                try { fixedAsset = delegator.findByPrimaryKey("FixedAsset",
                        UtilMisc.toMap("fixedAssetId", workEffort.get("fixedAssetId")));
                }
                catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderFixedAssetNotFoundFixedAssetId ", UtilMisc.toMap("fixedAssetId",workEffort.get("fixedAssetId")), locale));
                }
                if (fixedAsset == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderFixedAssetNotFoundFixedAssetId ", UtilMisc.toMap("fixedAssetId",workEffort.get("fixedAssetId")), locale));
                }
                // see if this fixed asset has a calendar, when no create one and attach to fixed asset
                Debug.logInfo("find the techdatacalendar",module);
                GenericValue techDataCalendar = null;
                try { techDataCalendar = fixedAsset.getRelatedOne("TechDataCalendar");
                }
                catch (GenericEntityException e) {
                    Debug.logInfo("TechData calendar does not exist yet so create for fixedAsset: " + fixedAsset.get("fixedAssetId") ,module);
                }
                if(techDataCalendar == null ) {
                    techDataCalendar = delegator.makeValue("TechDataCalendar", null);
                    Debug.logInfo("create techdata calendar because it does not exist",module);
                    String calendarId = delegator.getNextSeqId("techDataCalendar").toString();
                    techDataCalendar.set("calendarId", calendarId);
                    toBeStored.add(techDataCalendar);
                    Debug.logInfo("update fixed Asset",module);
                    fixedAsset.set("calendarId",calendarId);
                    toBeStored.add(fixedAsset);
                }
                // then create the workEffort and the workOrderItemFulfillment to connect to the order and orderItem
                workOrderItemFulfillment.set("orderItemSeqId", workEffort.get("workEffortId").toString()); // orderItemSeqNo is stored here so save first
                // workeffort
                String workEffortId = delegator.getNextSeqId("WorkEffort").toString(); // find next available workEffortId
                workEffort.set("workEffortId", workEffortId);
                workEffort.set("workEffortTypeId", "ASSET_USAGE");
                toBeStored.add(workEffort);  // store workeffort before workOrderItemFulfillment because of workEffortId key constraint
                // workOrderItemFulfillment
                workOrderItemFulfillment.set("workEffortId", workEffortId);
                workOrderItemFulfillment.set("orderId", orderId);
                toBeStored.add(workOrderItemFulfillment);
//                Debug.logInfo("Workeffort "+ workEffortId + " created for asset " + workEffort.get("fixedAssetId") + " and order "+ workOrderItemFulfillment.get("orderId") + "/" + workOrderItemFulfillment.get("orderItemSeqId") + " created", module);
//
                // now create the TechDataExcDay, when they do not exist, create otherwise update the capacity values
                // please note that calendarId is the same for (TechData)Calendar, CalendarExcDay and CalendarExWeek
                Timestamp estimatedStartDate = workEffort.getTimestamp("estimatedStartDate");
                Timestamp estimatedCompletionDate = workEffort.getTimestamp("estimatedCompletionDate");
                long dayCount = (estimatedCompletionDate.getTime() - estimatedStartDate.getTime())/86400000;
                while (--dayCount >= 0)    {
                    GenericValue techDataCalendarExcDay = null;
                    // find an existing Day exception record
                    Timestamp exceptionDateStartTime = new Timestamp((long)(estimatedStartDate.getTime() + (dayCount * 86400000)));
                    try {     techDataCalendarExcDay = delegator.findByPrimaryKey("TechDataCalendarExcDay",
                            UtilMisc.toMap("calendarId", fixedAsset.get("calendarId"), "exceptionDateStartTime", exceptionDateStartTime));
                    }
                    catch (GenericEntityException e) {
                        Debug.logInfo(" techData excday record not found so creating........", module);
                    }
                    if (techDataCalendarExcDay == null)    {
                        techDataCalendarExcDay = delegator.makeValue("TechDataCalendarExcDay", null);
                        techDataCalendarExcDay.set("calendarId", fixedAsset.get("calendarId"));
                        techDataCalendarExcDay.set("exceptionDateStartTime", exceptionDateStartTime);
                        techDataCalendarExcDay.set("usedCapacity",new Double(00.00));  // initialise to zero
                        techDataCalendarExcDay.set("exceptionCapacity", fixedAsset.getDouble("productionCapacity"));
//                       Debug.logInfo(" techData excday record not found creating for calendarId: " + techDataCalendarExcDay.getString("calendarId") +
//                               " and date: " + exceptionDateStartTime.toString(), module);
                    }
                    // add the quantity to the quantity on the date record
                    Double newUsedCapacity = new Double(techDataCalendarExcDay.getDouble("usedCapacity").doubleValue() +
                            workEffort.getDouble("quantityToProduce").doubleValue());
                    // check to see if the requested quantity is available on the requested day but only when the maximum capacity is set on the fixed asset
                    if (fixedAsset.get("productionCapacity") != null)    {
//                       Debug.logInfo("see if maximum not reached, available:  " + techDataCalendarExcDay.getString("exceptionCapacity") +
//                               " already allocated: " + techDataCalendarExcDay.getString("usedCapacity") +
//                                " Requested: " + workEffort.getString("quantityToProduce"), module);
                       if (newUsedCapacity.compareTo(techDataCalendarExcDay.getDouble("exceptionCapacity")) > 0)    {
                            String errMsg = "ERROR: fixed_Asset_sold_out AssetId: " + workEffort.get("fixedAssetId") + " on date: " + techDataCalendarExcDay.getString("exceptionDateStartTime");
                            Debug.logError(errMsg, module);
                            errorMessages.add(errMsg);
                            continue;
                        }
                    }
                    techDataCalendarExcDay.set("usedCapacity", newUsedCapacity);
                    toBeStored.add(techDataCalendarExcDay);
//                  Debug.logInfo("Update success CalendarID: " + techDataCalendarExcDay.get("calendarId").toString() +
//                            " and for date: " + techDataCalendarExcDay.get("exceptionDateStartTime").toString() +
//                            " and for quantity: " + techDataCalendarExcDay.getDouble("usedCapacity").toString(), module);
                }
            }
        }
        if (errorMessages.size() > 0) {
            return ServiceUtil.returnError(errorMessages);
        }

        // set the orderId on all adjustments; this list will include order and
        // item adjustments...
        if (orderAdjustments != null && orderAdjustments.size() > 0) {
            Iterator iter = orderAdjustments.iterator();

            while (iter.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) iter.next();
                try {
                    orderAdjustment.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotGetNextSequenceIdForOrderAdjustmentCannotCreateOrder",locale));
                }

                orderAdjustment.set("orderId", orderId);
                orderAdjustment.set("createdDate", UtilDateTime.nowTimestamp());
                orderAdjustment.set("createdByUserLogin", userLogin.getString("userLoginId"));

                if (orderAdjustment.get("orderItemSeqId") == null || orderAdjustment.getString("orderItemSeqId").length() == 0) {
                    orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                }
                if (orderAdjustment.get("shipGroupSeqId") == null || orderAdjustment.getString("shipGroupSeqId").length() == 0) {
                    orderAdjustment.set("shipGroupSeqId", DataModelConstants.SEQ_ID_NA);
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

        // set the order item ship groups
        if (orderItemShipGroupInfo != null && orderItemShipGroupInfo.size() > 0) {
            Iterator osiInfos = orderItemShipGroupInfo.iterator();
            while (osiInfos.hasNext()) {
                GenericValue valueObj = (GenericValue) osiInfos.next();
                valueObj.set("orderId", orderId);
                if ("OrderItemShipGroup".equals(valueObj.getEntityName())) {
                    // ship group
                    if (valueObj.get("carrierRoleTypeId") == null) {
                        valueObj.set("carrierRoleTypeId", "CARRIER");
                    }
                } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                    // shipping / tax adjustment(s)
                    if (valueObj.get("orderItemSeqId") == null || valueObj.getString("orderItemSeqId").length() == 0) {
                        valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                    }
                    valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                    valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                    valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                }
                toBeStored.add(valueObj);
            }
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
        if (orderItemPriceInfo != null && orderItemPriceInfo.size() > 0) {
            Iterator oipii = orderItemPriceInfo.iterator();

            while (oipii.hasNext()) {
                GenericValue oipi = (GenericValue) oipii.next();
                try {
                    oipi.set("orderItemPriceInfoId", delegator.getNextSeqId("OrderItemPriceInfo"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotGetNextSequenceIdForOrderItemPriceInfoCannotCreateOrder",locale));
                }

                oipi.set("orderId", orderId);
                toBeStored.add(oipi);
            }
        }

        // set the item associations
        List orderItemAssociations = (List) context.get("orderItemAssociations");
        if (orderItemAssociations != null && orderItemAssociations.size() > 0) {
            Iterator oia = orderItemAssociations.iterator();
            while (oia.hasNext()) {
                GenericValue orderItemAssociation = (GenericValue) oia.next();
                orderItemAssociation.set("purchaseOrderId", orderId);
                toBeStored.add(orderItemAssociation);
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

        /* DEJ20050529 the OLD way, where a single party had all roles... no longer doing things this way...
        // define the roles for the order
        List userOrderRoleTypes = null;
        if ("SALES_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER");
        } else if ("PURCHASE_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("SHIP_FROM_VENDOR", "BILL_FROM_VENDOR", "SUPPLIER_AGENT");
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
        */

        // see the attributeRoleMap definition near the top of this file for attribute-role mappings
        Map attributeRoleMap = salesAttributeRoleMap;
        if ("PURCHASE_ORDER".equals(orderTypeId)) {
            attributeRoleMap = purchaseAttributeRoleMap;
        }
        Iterator attributeRoleEntryIter = attributeRoleMap.entrySet().iterator();
        while (attributeRoleEntryIter.hasNext()) {
            Map.Entry attributeRoleEntry = (Map.Entry) attributeRoleEntryIter.next();

            if (UtilValidate.isNotEmpty((String) context.get(attributeRoleEntry.getKey()))) {
                // make sure the party is in the role before adding
                toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", context.get(attributeRoleEntry.getKey()), "roleTypeId", attributeRoleEntry.getValue())));
                toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", context.get(attributeRoleEntry.getKey()), "roleTypeId", attributeRoleEntry.getValue())));
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

        // set the order payment info
        List orderPaymentInfos = (List) context.get("orderPaymentInfo");
        if (orderPaymentInfos != null && orderPaymentInfos.size() > 0) {
            Iterator oppIter = orderPaymentInfos.iterator();
            while (oppIter.hasNext()) {
                GenericValue valueObj = (GenericValue) oppIter.next();
                valueObj.set("orderId", orderId);
                if ("OrderPaymentPreference".equals(valueObj.getEntityName())) {
                    if (valueObj.get("orderPaymentPreferenceId") == null) {
                        valueObj.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString());
                        valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                        valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                    }
                    if (valueObj.get("statusId") == null) {
                        valueObj.set("statusId", "PAYMENT_NOT_RECEIVED");
                    }
                }
                toBeStored.add(valueObj);
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

       // store the OrderTerm entities

       List orderTerms = (List) context.get("orderTerms");
       if (orderTerms != null && orderTerms.size() > 0) {
           Iterator orderTermIter = orderTerms.iterator();
           while (orderTermIter.hasNext()) {
               GenericValue orderTerm = (GenericValue) orderTermIter.next();
               orderTerm.set("orderId", orderId);
               orderTerm.set("orderItemSeqId","_NA_");
               toBeStored.add(orderTerm);
           }
       }

        try {
            // store line items, etc so that they will be there for the foreign key checks
            delegator.storeAll(toBeStored);

            // START inventory reservation
            List resErrorMessages = new LinkedList();
            try {
                reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupInfo, itemValuesBySeqId,
                        orderTypeId, productStoreId, resErrorMessages);
            } catch (GeneralException e) {
                return ServiceUtil.returnError(e.getMessage());
            }

            if (resErrorMessages.size() > 0) {
                return ServiceUtil.returnError(resErrorMessages);
            }
            // END inventory reservation

            successResult.put("orderId", orderId);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem with order storage or reservations", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotCreateOrderWriteError",locale) + e.getMessage() + ").");
        }

        return successResult;
    }

    public static void reserveInventory(GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, List orderItemShipGroupInfo, Map itemValuesBySeqId, String orderTypeId, String productStoreId, List resErrorMessages) throws GeneralException {
        boolean isImmediatelyFulfilled = false;
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
            } catch (GenericEntityException e) {
                throw new GeneralException(UtilProperties.getMessage(resource_error, "OrderErrorCouldNotFindProductStoreWithID", UtilMisc.toMap("productStoreId", productStoreId), locale) + e.toString());
            }
        }
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        boolean reserveInventory = ("SALES_ORDER".equals(orderTypeId));
        if (reserveInventory && isImmediatelyFulfilled) {
            // don't reserve inventory if the product store has isImmediatelyFulfilled set, ie don't if in this store things are immediately fulfilled
            reserveInventory = false;
        }

        if (reserveInventory) {
            // START inventory reservation
            // decrement inventory available for each OrderItemShipGroupAssoc, within the same transaction
            if (orderItemShipGroupInfo != null && orderItemShipGroupInfo.size() > 0) {
                Iterator osiInfos = orderItemShipGroupInfo.iterator();
                while (osiInfos.hasNext()) {
                    GenericValue orderItemShipGroupAssoc = (GenericValue) osiInfos.next();
                    if ("OrderItemShipGroupAssoc".equals(orderItemShipGroupAssoc.getEntityName())) {
                        GenericValue orderItem = (GenericValue) itemValuesBySeqId.get(orderItemShipGroupAssoc.get("orderItemSeqId"));
                        String itemStatus = orderItem.getString("statusId");
                        if ("ITEM_REJECTED".equals(itemStatus) || "ITEM_CANCELLED".equals(itemStatus) || "ITEM_COMPLETED".equals(itemStatus)) {
                            Debug.logInfo("Order item [" + orderItem.getString("orderId") + " / " + orderItem.getString("orderItemSeqId") + "] is not in a proper status for reservation", module);
                            continue;
                        }
                        if (UtilValidate.isNotEmpty(orderItem.getString("productId")) && !"RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId")))
                        { // ignore for rental
                            // only reserve product items; ignore non-product items
                            try {
                                Map reserveInput = new HashMap();
                                reserveInput.put("productStoreId", productStoreId);
                                reserveInput.put("productId", orderItem.getString("productId"));
                                reserveInput.put("orderId", orderItem.getString("orderId"));
                                reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                // use the quantity from the orderItemShipGroupAssoc, NOT the orderItem, these are reserved by item-group assoc
                                reserveInput.put("quantity", orderItemShipGroupAssoc.getDouble("quantity"));
                                reserveInput.put("userLogin", userLogin);
                                Map reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);

                                if (ServiceUtil.isError(reserveResult)) {
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
                            } catch (GenericServiceException e) {
                                String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
                                Debug.logError(e, errMsg, module);
                                resErrorMessages.add(errMsg);
                            }
                        }
                    }
                }
            }
        }
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

    public static String determineSingleFacilityFromOrder(GenericValue orderHeader) {
        if (orderHeader != null) {
            String productStoreId = orderHeader.getString("productStoreId");
            if (productStoreId != null) {
                return ProductStoreWorker.determineSingleFacilityForStore(orderHeader.getDelegator(), productStoreId);
            }
        }
        return null;
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
        Locale locale = (Locale) context.get("locale");
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
                    Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderErrorCannotResetOrderTotals", UtilMisc.toMap("orderId",orderId,"resetResult",ServiceUtil.getErrorMessage(resetResult)), locale), module);
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
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity ",locale) + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderHeaderEntity",locale) + e.getMessage());
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorNoValidOrderHeaderFoundForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        // don't charge tax on purchase orders
        if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
            return ServiceUtil.returnSuccess();
        }

        // remove the tax adjustments
        int removed = 0;
        try {
            removed = delegator.removeByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderAdjustmentTypeId", "SALES_TAX"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to remove SALES_TAX adjustments for order : " + orderId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToRemoveSalesTaxAdjustments",locale));
        }
        Debug.logInfo("Removed : " + removed + " SALES_TAX adjustments for order [" + orderId + "]", module);

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        List shipGroups = orh.getOrderItemShipGroups();
        List toStore = new ArrayList();
        if (shipGroups != null) {
            Iterator itr = shipGroups.iterator();
            while (itr.hasNext()) {
                GenericValue shipGroup = (GenericValue) itr.next();
                String shipGroupSeqId = shipGroup.getString("shipGroupSeqId");

                List validOrderItems = orh.getValidOrderItems(shipGroupSeqId);
                if (validOrderItems != null) {
                    // prepare the inital lists
                    List products = new ArrayList(validOrderItems.size());
                    List amounts = new ArrayList(validOrderItems.size());
                    List shipAmts = new ArrayList(validOrderItems.size());
                    List itPrices = new ArrayList(validOrderItems.size());

                    // adjustments and total
                    List allAdjustments = orh.getAdjustments();
                    List orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(allAdjustments, shipGroupSeqId);
                    double orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(validOrderItems, allAdjustments);

                    // shipping amount
                    BigDecimal orderShipping = new BigDecimal(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

                    // build up the list of tax calc service parameters
                    for (int i = 0; i < validOrderItems.size(); i++) {
                        GenericValue orderItem = (GenericValue) validOrderItems.get(i);
                        String productId = orderItem.getString("productId");
                        try {
                            products.add(i, delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)));  // get the product entity
                            amounts.add(i, new BigDecimal(OrderReadHelper.getOrderItemSubTotal(orderItem, allAdjustments, true, false))); // get the item amount
                            shipAmts.add(i, new BigDecimal(OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, allAdjustments, false, false, true))); // get the shipping amount
                            itPrices.add(i, orderItem.getBigDecimal("unitPrice"));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Cannot read order item entity : " + orderItem, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotReadTheOrderItemEntity",locale));
                        }
                    }

                    GenericValue shippingAddress = orh.getShippingAddress(shipGroupSeqId);
                    if (shippingAddress == null) {
                        // face-to-face order; use the facility address
                        String facilityId = orderHeader.getString("originFacilityId");
                        if (facilityId != null) {
                            List fcp = null;
                            try {
                                fcp = delegator.findByAnd("FacilityContactMechPurpose", UtilMisc.toMap("facilityId",
                                        facilityId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"));
                            } catch (GenericEntityException e) {
                                Debug.logError(e, module);
                            }
                            fcp = EntityUtil.filterByDate(fcp);
                            GenericValue purp = EntityUtil.getFirst(fcp);
                            if (purp != null) {
                                try {
                                    shippingAddress = delegator.findByPrimaryKey("PostalAddress",
                                            UtilMisc.toMap("contactMechId", purp.getString("contactMechId")));
                                } catch (GenericEntityException e) {
                                    Debug.logError(e, module);
                                }
                            }
                        }
                    }

                    // prepare the service context
                    // pass in BigDecimal values instead of Double
                    Map serviceContext = UtilMisc.toMap("productStoreId", orh.getProductStoreId(), "itemProductList", products, "itemAmountList", amounts,
                        "itemShippingList", shipAmts, "itemPriceList", itPrices, "orderShippingAmount", orderShipping);
                    serviceContext.put("shippingAddress", shippingAddress);
                    if (orh.getBillToParty() != null) serviceContext.put("billToPartyId", orh.getBillToParty().getString("partyId"));

                    // invoke the calcTax service
                    Map serviceResult = null;
                    try {
                        serviceResult = dispatcher.runSync("calcTax", serviceContext);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemOccurredInTaxService",locale));
                    }

                    if (ServiceUtil.isError(serviceResult)) {
                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
                    }

                    // the adjustments (returned in order) from the tax service
                    List orderAdj = (List) serviceResult.get("orderAdjustments");
                    List itemAdj = (List) serviceResult.get("itemAdjustments");

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
                                ia.set("shipGroupSeqId", shipGroupSeqId);
                                ia.set("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                toStore.add(ia);
                            }
                        }
                    }
                }
            }

            // store the new adjustments
            try {
                delegator.storeAll(toStore);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToUpdateOrderTaxInformation" ,UtilMisc.toMap("orderId",orderId), locale));
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
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity",locale) + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderHeaderEntity",locale) + e.getMessage());
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorNoValidOrderHeaderFoundForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        List shipGroups = orh.getOrderItemShipGroups();
        if (shipGroups != null) {
            Iterator i = shipGroups.iterator();
            while (i.hasNext()) {
                GenericValue shipGroup = (GenericValue) i.next();
                String shipGroupSeqId = shipGroup.getString("shipGroupSeqId");

                if (shipGroup.get("contactMechId") == null || shipGroup.get("shipmentMethodTypeId") == null) {
                    // not shipped (face-to-face order)
                    continue;
                }

                Map shippingEstMap = ShippingEvents.getShipEstimate(dispatcher, delegator, orh, shipGroupSeqId);
                Double shippingTotal = null;
                if (orh.getValidOrderItems(shipGroupSeqId) == null || orh.getValidOrderItems(shipGroupSeqId).size() == 0) {
                    shippingTotal = new Double(0.00);
                    Debug.log("No valid order items found - " + shippingTotal, module);
                } else {
                    shippingTotal = (Double) shippingEstMap.get("shippingTotal");
                    Debug.log("Got new shipping estimate - " + shippingTotal, module);
                }
                if (Debug.infoOn()) {
                    Debug.log("New Shipping Total [" + orderId + " / " + shipGroupSeqId + "] : " + shippingTotal, module);
                }

                double currentShipping = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orh.getOrderItemAndShipGroupAssoc(shipGroupSeqId), orh.getAdjustments(), false, false, true);
                currentShipping += OrderReadHelper.calcOrderAdjustments(orh.getOrderHeaderAdjustments(shipGroupSeqId), orh.getOrderItemsSubTotal(), false, false, true);

                if (Debug.infoOn()) {
                    Debug.log("Old Shipping Total [" + orderId + " / " + shipGroupSeqId + "] : " + currentShipping, module);
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
                    orderAdjustment.set("shipGroupSeqId", shipGroupSeqId);
                    orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                    orderAdjustment.set("createdDate", UtilDateTime.nowTimestamp());
                    orderAdjustment.set("createdByUserLogin", userLogin.getString("userLoginId"));
                    //orderAdjustment.set("comments", "Shipping Re-Calc Adjustment");
                    try {
                        orderAdjustment.create();
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem creating shipping re-calc adjustment : " + orderAdjustment, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotCreateAdjustment",locale));
                    }
                }

                // TODO: re-balance free shipping adjustment
            }
        }

        return ServiceUtil.returnSuccess();

    }

    /** Service for checking to see if an order is fully completed or canceled */
    public static Map checkItemStatus(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

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
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity",locale) + e.getMessage());
            }
            if (placingCustomer == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
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
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotUpdateNullOrderHeader",UtilMisc.toMap("orderId",orderId),locale));
        }

        // get the order items
        List orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderItem records", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemGettingOrderItemRecords", locale));
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
            Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderReceivedNullForOrderItemRecordsOrderId", UtilMisc.toMap("orderId",orderId),locale), module);
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service to cancel an order item quantity */
    public static Map cancelOrderItem(DispatchContext ctx, Map context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Double cancelQuantity = (Double) context.get("cancelQuantity");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");

        // debugging message info
        String itemMsgInfo = orderId + " / " + orderItemSeqId + " / " + shipGroupSeqId;

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo),locale));
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        Map fields = UtilMisc.toMap("orderId", orderId);
        if (orderItemSeqId != null) {
            fields.put("orderItemSeqId", orderItemSeqId);
        }
        if (shipGroupSeqId != null) {
            fields.put("shipGroupSeqId", shipGroupSeqId);
        }

        List orderItemShipGroupAssocs = null;
        try {
            orderItemShipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", fields);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderItemAssocEntity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
        }

        if (orderItemShipGroupAssocs != null) {
            if (orderItemShipGroupAssocs == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotCancelItemItemNotFound", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
            }

            Iterator i = orderItemShipGroupAssocs.iterator();
            while (i.hasNext()) {
                GenericValue orderItemShipGroupAssoc = (GenericValue) i.next();
                GenericValue orderItem = null;
                try {
                    orderItem = orderItemShipGroupAssoc.getRelatedOne("OrderItem");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }

                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotCancelItemItemNotFound", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                }

                Double availableQuantity = orderItemShipGroupAssoc.getDouble("quantity");
                Double itemQuantity = orderItem.getDouble("quantity");
                if (availableQuantity == null) availableQuantity = new Double(0.0);
                if (itemQuantity == null) itemQuantity = new Double(0.0);

                Double thisCancelQty = null;
                if (cancelQuantity != null) {
                    thisCancelQty = new Double(cancelQuantity.doubleValue());
                } else {
                    thisCancelQty = new Double(availableQuantity.doubleValue());
                }

                if (availableQuantity.doubleValue() >= thisCancelQty.doubleValue()) {
                    orderItem.set("cancelQuantity", thisCancelQty);
                    orderItemShipGroupAssoc.set("cancelQuantity", thisCancelQty);

                    try {
                        List toStore = UtilMisc.toList(orderItem, orderItemShipGroupAssoc);
                        delegator.storeAll(toStore);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToSetCancelQuantity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                    }

                    if (thisCancelQty.doubleValue() >= itemQuantity.doubleValue()) {
                        // all items are cancelled -- mark the item as cancelled
                        Map statusCtx = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToCancelOrderLine", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                        }
                    } else {
                        // reverse the inventory reservation
                        Map invCtx = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId",
                                shipGroupSeqId, "cancelQuantity", thisCancelQty, "userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("cancelOrderItemInvResQty", invCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToUpdateInventoryReservations", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                        }
                    }
                } else {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderInvalidCancelQuantityCannotCancel", UtilMisc.toMap("thisCancelQty",thisCancelQty), locale));
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
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity",locale) + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
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
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderItemEntity ",locale) + e.getMessage());
        }

        if (orderItems != null && orderItems.size() > 0) {
            List toBeStored = new ArrayList();
            Iterator itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = (GenericValue) itemsIterator.next();
                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotChangeItemStatusItemNotFound", locale));
                }
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : Status Change: [" + orderId + "] (" + orderItem.getString("orderItemSeqId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : From Status : " + orderItem.getString("statusId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

                if (orderItem.getString("statusId").equals(statusId)) {
                    continue;
                }

                try {
                    Map statusFields = UtilMisc.toMap("statusId", orderItem.getString("statusId"), "statusIdTo", statusId);
                    GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);

                    if (statusChange == null) {
                        Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderItemStatusNotChangedIsNotAValidChange", UtilMisc.toMap("orderStatusId",orderItem.getString("statusId"),"statusId",statusId), locale), module);
                        continue;
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotChangeItemStatus",locale) + e.getMessage());
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
                changeFields.put("statusUserLogin", userLogin.getString("userLoginId"));
                GenericValue orderStatus = delegator.makeValue("OrderStatus", changeFields);
                toBeStored.add(orderStatus);
            }

            // store the changes
            if (toBeStored.size() > 0) {
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotStoreStatusChanges", locale) + e.getMessage());
                }
            }

        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for changing the status on an order header */
    public static Map setOrderStatus(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        Map successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity", locale) + e.getMessage());
            }
            if (placingCustomer == null)
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        try {
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotChangeOrderStatusOrderCannotBeFound",locale));
            }
            // first save off the old status
            successResult.put("oldStatusId", orderHeader.get("statusId"));

            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"), module);
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

            if (orderHeader.getString("statusId").equals(statusId)) {
                Debug.logWarning(UtilProperties.getMessage(resource_error,"OrderTriedToSetOrderStatusWithTheSameStatusIdforOrderWithId", UtilMisc.toMap("statusId",statusId,"orderId",orderId),locale),module);
                return successResult;
            }
            try {
                Map statusFields = UtilMisc.toMap("statusId", orderHeader.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotChangeOrderStatusStatusIsNotAValidChange",locale));
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
            }

            // update the current status
            orderHeader.set("statusId", statusId);

            // now create a status change
            GenericValue orderStatus = delegator.makeValue("OrderStatus", null);
            orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            orderStatus.put("statusId", statusId);
            orderStatus.put("orderId", orderId);
            orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.put("statusUserLogin", userLogin.getString("userLoginId"));

            orderHeader.store();
            orderStatus.create();

            successResult.put("needsInventoryIssuance", orderHeader.get("needsInventoryIssuance"));
            successResult.put("grandTotal", orderHeader.get("grandTotal"));
            successResult.put("orderTypeId", orderHeader.get("orderTypeId"));
            //Debug.logInfo("For setOrderStatus orderHeader is " + orderHeader, module);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
        }

        // release the inital hold if we are cancelled or approved
        if ("ORDER_CANCELLED".equals(statusId) || "ORDER_APPROVED".equals(statusId)) {
            OrderChangeHelper.releaseInitialOrderHold(ctx.getDispatcher(), orderId);

            // cancel any order processing if we are cancelled
            if ("ORDER_CANCELLED".equals(statusId)) {
                OrderChangeHelper.abortOrderProcessing(ctx.getDispatcher(), orderId);
            }
        }

        successResult.put("orderStatusId", statusId);
        //Debug.logInfo("For setOrderStatus successResult is " + successResult, module);
        return successResult;
    }

    /** Service to update the order tracking number */
    public static Map updateTrackingNumber(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String trackingNumber = (String) context.get("trackingNumber");
        //Locale locale = (Locale) context.get("locale");

        try {
            GenericValue shipGroup = delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId));

            if (shipGroup == null) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: No order shipment preference found!");
            } else {
                shipGroup.set("trackingNumber", trackingNumber);
                shipGroup.store();
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
        //Locale locale = (Locale) context.get("locale");

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
        //Locale locale = (Locale) context.get("locale");

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

    /** Service to email a customer with initial order confirmation */
    public static Map sendOrderConfirmNotification(DispatchContext ctx, Map context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_CONFIRM");
    }

    /** Service to email a customer with order changes */
    public static Map sendOrderCompleteNotification(DispatchContext ctx, Map context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_COMPLETE");
    }

    /** Service to email a customer with order changes */
    public static Map sendOrderBackorderNotification(DispatchContext ctx, Map context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_BACKORDER");
    }

    /** Service to email a customer with order changes */
    public static Map sendOrderChangeNotification(DispatchContext ctx, Map context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_CHANGE");
    }

    /** Service to email a customer with order payment retry results */
    public static Map sendOrderPayRetryNotification(DispatchContext ctx, Map context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_PAYRETRY");
    }

    protected static Map sendOrderNotificationScreen(DispatchContext dctx, Map context, String emailType) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String sendTo = (String) context.get("sendTo");
        String sendCc = (String) context.get("sendCc");
        String note = (String) context.get("note");
        String screenUri = (String) context.get("screenUri");

        // prepare the order information
        Map sendMap = FastMap.newInstance();

        // get the order header and store
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting OrderHeader", module);
        }

        if (orderHeader == null) {
            return ServiceUtil.returnFailure("Could not find OrderHeader with ID [" + orderId + "]");
        }

        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", orderHeader.get("productStoreId"), "emailType", emailType));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + orderHeader.get("productStoreId") + " and emailType=" + emailType, module);
        }
        if (productStoreEmail == null) {
            return ServiceUtil.returnFailure("No valid email setting for store with productStoreId=" + orderHeader.get("productStoreId") + " and emailType=" + emailType);
        }

        // the override screenUri
        if (UtilValidate.isEmpty(screenUri)) {
            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
            }
            sendMap.put("bodyScreenUri", bodyScreenLocation);
            String xslfoAttachScreenLocation = productStoreEmail.getString("xslfoAttachScreenLocation");
            sendMap.put("xslfoAttachScreenLocation", xslfoAttachScreenLocation);
        } else {
            sendMap.put("bodyScreenUri", screenUri);
        }

        // website
        sendMap.put("webSiteId", orderHeader.get("webSiteId"));

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        String emailString = orh.getOrderEmailString();
        if (UtilValidate.isEmpty(emailString)) {
            Debug.logInfo("Customer is not setup to receive emails; no address(s) found [" + orderId + "]", module);
            return ServiceUtil.returnError("No sendTo email address found");
        }

        // where to get the locale... from PLACING_CUSTOMER's UserLogin.lastLocale,
        // or if not available then from ProductStore.defaultLocaleString
        // or if not available then the system Locale
        Locale locale = null;
        GenericValue placingParty = orh.getPlacingParty();
        GenericValue placingUserLogin = placingParty == null ? null : PartyWorker.findPartyLatestUserLogin(placingParty.getString("partyId"), delegator);
        if (locale == null && placingParty != null) {
            locale = PartyWorker.findPartyLastLocale(placingParty.getString("partyId"), delegator);
        }
        GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(orderHeader);
        if (locale == null && productStore != null) {
            String localeString = productStore.getString("defaultLocaleString");
            if (UtilValidate.isNotEmpty(localeString)) {
                locale = UtilMisc.parseLocale(localeString);
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }

        ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("EcommerceUiLabels", locale);
        uiLabelMap.addBottomResourceBundle("OrderUiLabels");
        uiLabelMap.addBottomResourceBundle("CommonUiLabels");

        Map bodyParameters = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "userLogin", placingUserLogin, "uiLabelMap", uiLabelMap, "locale", locale);
        if( placingParty!= null) {
            bodyParameters.put("partyId", placingParty.get("partyId"));
        }
        bodyParameters.put("note", note);
        sendMap.put("bodyParameters", bodyParameters);
        sendMap.put("userLogin",userLogin);

        String subjectString = productStoreEmail.getString("subject");
        sendMap.put("subject", subjectString);

        sendMap.put("contentType", productStoreEmail.get("contentType"));
        sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
        sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
        sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));
        if ((sendTo != null) && UtilValidate.isEmail(sendTo)) {
            sendMap.put("sendTo", sendTo);
        } else {
            sendMap.put("sendTo", emailString);
        }
        if ((sendCc != null) && UtilValidate.isEmail(sendCc)) {
            sendMap.put("sendCc", sendCc);
        } else {
            sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
        }

        // send the notification
        Map sendResp = null;
        try {
            sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
        } catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderServiceExceptionSeeLogs",locale));
        }

        // check for errors
        if (sendResp != null && !ServiceUtil.isError(sendResp)) {
            sendResp.put("emailType", emailType);
        }
        return sendResp;
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
        Locale locale = (Locale) context.get("locale");

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
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemWithEntityLookup", locale));
        }

        // find the assigned user's email address(s)
        GenericValue party = null;
        Collection assignedToEmails = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", assignedToUser));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemWithEntityLookup", locale));
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
        String templateName = ofbizHome + "/applications/order/email/default/emailprocessnotify.ftl";

        Map sendMailContext = new HashMap();
        sendMailContext.put("sendTo", emailList.toString());
        sendMailContext.put("sendFrom", "workflow@ofbiz.org"); // fixme
        sendMailContext.put("subject", "Workflow Notification");
        sendMailContext.put("templateName", templateName);
        sendMailContext.put("templateData", templateData);

        try {
            dispatcher.runAsync("sendGenericNotificationEmail", sendMailContext);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderSendMailServiceFailed", locale) + e.getMessage());
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
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String prefId = null;

        try {
            prefId = delegator.getNextSeqId("OrderPaymentPreference");
        } catch (IllegalArgumentException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotCreateOrderPaymentPreferenceIdGenerationFailure", locale));
        }

        Map fields = UtilMisc.toMap("orderPaymentPreferenceId", prefId, "orderId", orderId, "paymentMethodTypeId",
                paymentMethodTypeId, "paymentMethodId", paymentMethodId, "maxAmount", maxAmount);

        try {
            GenericValue v = delegator.makeValue("OrderPaymentPreference", fields);
            v.set("createdDate", UtilDateTime.nowTimestamp());
            if (userLogin != null) {
                v.set("createdByUserLogin", userLogin.getString("userLoginId"));
            }
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
        Locale locale = (Locale) context.get("locale");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order header detial", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotGetOrderHeader ", locale) + e.getMessage());
        }
        if (orderHeader != null) {
            Map result = ServiceUtil.returnSuccess();
            result.putAll(orderHeader);
            return result;
        }
        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorGettingOrderHeaderInformationNull", locale));
    }

    /** Service to get the total shipping for an order. */
    public static Map getOrderShippingAmount(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCouldNotGetOrderInformation", locale) + e.getMessage() + ").");
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
            result = ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderUnableToFindOrderHeaderCannotGetShippingAmount", locale));
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
        //Locale locale = (Locale) context.get("locale");

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
        String internalNote = (String) context.get("internalNote");
        Map noteCtx = UtilMisc.toMap("note", noteString, "userLogin", userLogin);
        Locale locale = (Locale) context.get("locale");

        // Store the note.
        Map noteRes = org.ofbiz.common.CommonServices.createNote(dctx, noteCtx);

        if (noteRes.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
            return noteRes;

        String noteId = (String) noteRes.get("noteId");

        if (noteId == null || noteId.length() == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemCreatingTheNoteNoNoteIdReturned", locale));
        }

        // Set the order info
        try {
            Map fields = UtilMisc.toMap("orderId", orderId, "noteId", noteId, "internalNote", internalNote);
            GenericValue v = delegator.makeValue("OrderHeaderNote", fields);

            delegator.create(v);
        } catch (GenericEntityException ee) {
            Debug.logError(ee, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem associating note with order (" + ee.getMessage() + ").");
        }

        return result;
    }

    public static Map allowOrderSplit(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map placingCustomerFields = UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetOrderRoleEntity", locale) + e.getMessage());
            }
            if (placingCustomer == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToChangeThisOrdersStatus", locale));
            }
        }

        GenericValue shipGroup = null;
        try {
            Map fields = UtilMisc.toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId);
            shipGroup = delegator.findByPrimaryKey("OrderItemShipGroup", fields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting OrderItemShipGroup for : " + orderId + " / " + shipGroupSeqId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotUpdateProblemGettingOrderShipmentPreference", locale));
        }

        if (shipGroup != null) {
            shipGroup.set("maySplit", "Y");
            try {
                shipGroup.store();
            } catch (GenericEntityException e) {
                Debug.logError("Problem saving OrderItemShipGroup for : " + orderId + " / " + shipGroupSeqId, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotUpdateProblemSettingOrderShipmentPreference", locale));
            }
        } else {
            Debug.logError("ERROR: Got a NULL OrderItemShipGroup", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderCannotUpdateNoAvailableGroupsToChange", locale));
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map cancelFlaggedSalesOrders(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        //Locale locale = (Locale) context.get("locale");

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
                            // TODO: looks like result is ignored here, but we should be looking for errors
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
                            if (nowTimestamp.equals(autoCancelDate) || nowTimestamp.after(autoCancelDate)) {
                                // cancel the order item
                                Map svcCtx = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                                try {
                                    // TODO: check service result for an error return
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
        Locale locale = (Locale) context.get("locale");

        // need the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Unable to get OrderHeader for orderId : " + orderId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetOrderHeaderForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        // get all the items for the order
        List orderItems = null;
        if (orderHeader != null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get OrderItem list for orderId : " + orderId, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetOrderItemListForOrderId", UtilMisc.toMap("orderId",orderId), locale));
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
                    GenericValue productType = null;
                    try {
                        productType = product.getRelatedOne("ProductType");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "ERROR: Unable to get ProductType from Product", module);
                    }

                    if (productType != null) {
                        String isPhysical = productType.getString("isPhysical");
                        String isDigital = productType.getString("isDigital");

                        // check for digital and finished/digital goods
                        if (isDigital != null && "Y".equalsIgnoreCase(isDigital)) {
                            // we only invoice APPROVED items
                            if ("ITEM_APPROVED".equals(item.getString("statusId"))) {
                                digitalItems.add(item);
                            }
                            if (isPhysical == null || !"Y".equalsIgnoreCase(isPhysical)) {
                                // 100% digital goods need status change
                                digitalProducts.put(item, product);
                            }
                        }
                    }
                }
            }
        }

        // now process the digital items
        if (digitalItems.size() > 0) {
            GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(dispatcher.getDelegator(), orderId);
            boolean invoiceItems = true;
            if (productStore != null && productStore.get("autoInvoiceDigitalItems") != null) {
                invoiceItems = "Y".equalsIgnoreCase(productStore.getString("autoInvoiceDigitalItems"));
            }

            if (invoiceItems) {
                // invoice all APPROVED digital goods
                
                // do something tricky here: run as a different user that can actually create an invoice, post transaction, etc
                Map invoiceResult = null;
                try {
                    GenericValue permUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
                    Map invoiceContext = UtilMisc.toMap("orderId", orderId, "billItems", digitalItems, "userLogin", permUserLogin);
                    invoiceResult = dispatcher.runSync("createInvoiceForOrder", invoiceContext);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Unable to invoice digital items", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemWithInvoiceCreationDigitalItemsNotFulfilled", locale));
                } catch (GenericServiceException e) {
                    Debug.logError(e, "ERROR: Unable to invoice digital items", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemWithInvoiceCreationDigitalItemsNotFulfilled", locale));
                }
                if (ModelService.RESPOND_ERROR.equals(invoiceResult.get(ModelService.RESPONSE_MESSAGE))) {
                    return ServiceUtil.returnError((String) invoiceResult.get(ModelService.ERROR_MESSAGE));
                }

                // update the status of digital goods to COMPLETED; leave physical/digital as APPROVED for pick/ship
                Iterator dii = digitalItems.iterator();
                while (dii.hasNext()) {
                    GenericValue productType = null;
                    GenericValue item = (GenericValue) dii.next();
                    GenericValue product = (GenericValue) digitalProducts.get(item);

                    if (product != null) {
                        try {
                            productType = product.getRelatedOne("ProductType");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "ERROR: Unable to get ProductType from Product", module);
                        }
                    }

                    if (product != null && productType != null) {
                        String isPhysical = productType.getString("isPhysical");
                        String isDigital = productType.getString("isDigital");

                        // we were set as a digital good; one more check and change status
                        if ((isDigital != null && "Y".equalsIgnoreCase(isDigital)) && (
                                isPhysical == null || !"Y".equalsIgnoreCase(isPhysical))) {
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
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        //appears to not be used: String orderId = (String) context.get("orderId");
        List orderItems = (List) context.get("orderItems");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        if (orderItems != null && orderItems.size() > 0) {
            // loop through the digital items to fulfill
            Iterator itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = (GenericValue) itemsIterator.next();

                // make sure we have a valid item
                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotCheckForFulfillmentItemNotFound", locale));
                }

                // locate the Product & ProductContent records
                GenericValue product = null;
                List productContent = null;
                try {
                    product = orderItem.getRelatedOne("Product");
                    if (product == null) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotCheckForFulfillmentProductNotFound", locale));
                    }

                    List allProductContent = product.getRelated("ProductContent");
                    
                    // try looking up the parent product if the product has no content and is a variant
                    if (((allProductContent == null) || allProductContent.size() == 0) && ("Y".equals(product.getString("isVariant")))) {
                        GenericValue parentProduct = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                        allProductContent.addAll(parentProduct.getRelated("ProductContent"));
                    }
                    
                    if (allProductContent != null && allProductContent.size() > 0) {
                        // only keep ones with valid dates
                        productContent = EntityUtil.filterByDate(allProductContent, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
                        Debug.logInfo("Product has " + allProductContent.size() + " associations, " +
                                (productContent == null ? "0" : "" + productContent.size()) + " has valid from/thru dates", module);
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorCannotGetProductEntity", locale) + e.getMessage());
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
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderEmailFulfillmentTypeNotYetImplemented", locale));
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

    public static Map addItemToApprovedOrder(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String orderId = (String) context.get("orderId");
        String productId = (String) context.get("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        Double basePrice = (Double) context.get("basePrice");
        Double quantity = (Double) context.get("quantity");
        Double amount = (Double) context.get("amount");
        String overridePrice = (String) context.get("overridePrice");

        if (amount == null) {
            amount = new Double(0.00);
        }

        int shipGroupIdx = -1;
        try {
            shipGroupIdx = Integer.parseInt(shipGroupSeqId);
            shipGroupIdx--;
        } catch (NumberFormatException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (shipGroupIdx < 0) {
            return ServiceUtil.returnError("Invalid shipGroupSeqId [" + shipGroupSeqId + "]");
        }

        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError("ERROR: Null shopping cart object returned!");
        }

        // add in the new product
        try {
            ShoppingCartItem item = ShoppingCartItem.makeItem(null, productId, null, quantity.doubleValue(), null, null, null, null, null, null, null, null, prodCatalogId, null, null, null, dispatcher, cart, null, null);
            if (basePrice != null&&overridePrice!=null) {
                item.setBasePrice(basePrice.doubleValue());
                // special hack to make sure we re-calc the promos after a price change
                item.setQuantity(quantity.doubleValue() + 1, dispatcher, cart, false);
                item.setQuantity(quantity.doubleValue(), dispatcher, cart, false);
                item.setBasePrice(basePrice.doubleValue());
                item.setIsModifiedPrice(true);
            }


            // set the item in the selected ship group
            cart.setItemShipGroupQty(item, item.getQuantity(), shipGroupIdx);
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (ItemNotFoundException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // save all the updated information
        try {
            saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }

    public static Map updateApprovedOrderItems(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String orderId = (String) context.get("orderId");
        Map overridePriceMap = (Map) context.get("overridePriceMap");
        Map itemPriceMap = (Map) context.get("itemPriceMap");
        Map itemQtyMap = (Map) context.get("itemQtyMap");

        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError("ERROR: Null shopping cart object returned!");
        }

        // go through the item map and obtain the totals per item
        Map itemTotals = new HashMap();
        Iterator i = itemQtyMap.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            String quantityStr = (String) itemQtyMap.get(key);
            double groupQty = 0.0;
            try {
                groupQty = Double.parseDouble(quantityStr);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (groupQty == 0) {
                return ServiceUtil.returnError("Quantity must be >0, use cancel item to cancel completely!");
            }

            String[] itemInfo = key.split(":");
            Double tally = (Double) itemTotals.get(itemInfo[0]);
            if (tally == null) {
                tally = new Double(groupQty);
            } else {
                tally = new Double(tally.doubleValue() + groupQty);
            }
            itemTotals.put(itemInfo[0], tally);
        }

        // set the items amount/price
        Iterator iai = itemTotals.keySet().iterator();
        while (iai.hasNext()) {
            String itemSeqId = (String) iai.next();
            ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);

            if (cartItem != null) {
                Double qty = (Double) itemTotals.get(itemSeqId);
                double priceSave = cartItem.getBasePrice();

                // set quantity
                try {
                    cartItem.setQuantity(qty.doubleValue(), dispatcher, cart, true, false); // trigger external ops, don't reset ship groups (and update prices for both PO and SO items)
                } catch (CartItemModifyException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                Debug.log("Set item quantity: [" + itemSeqId + "] " + qty, module);

                if(cartItem.getIsModifiedPrice())
                    cartItem.setBasePrice(priceSave);
                // set price

                if (overridePriceMap.containsKey(itemSeqId)) {
                    String priceStr = (String) itemPriceMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(priceStr)) {
                        double price = -1;
                        //parse the price
                        NumberFormat nf = null;
                        if (locale != null) {
                            nf = NumberFormat.getNumberInstance(locale);
                        } else {
                            nf = NumberFormat.getNumberInstance();
                        }
                        try {
                            price = nf.parse(priceStr).doubleValue();
                        } catch (ParseException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                        cartItem.setBasePrice(price);
                        cartItem.setIsModifiedPrice(true);
                        Debug.log("Set item price: [" + itemSeqId + "] " + price, module);
                    }

                }
            } else {
                Debug.logInfo("Unable to locate shopping cart item for seqId #" + itemSeqId, module);
            }
        }

        // update the group amounts
        Iterator gai = itemQtyMap.keySet().iterator();
        while (gai.hasNext()) {
            String key = (String) gai.next();
            String quantityStr = (String) itemQtyMap.get(key);
            double groupQty = 0.0;
            try {
                groupQty = Double.parseDouble(quantityStr);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            String[] itemInfo = key.split(":");
            int groupIdx = -1;
            try {
                groupIdx = Integer.parseInt(itemInfo[1]);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            // set the group qty
            ShoppingCartItem cartItem = cart.findCartItem(itemInfo[0]);
            if (cartItem != null) {
                Debug.log("Shipping info (before) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
                cart.setItemShipGroupQty(cartItem, groupQty, groupIdx - 1);
                Debug.log("Set ship group qty: [" + itemInfo[0] + " / " + itemInfo[1] + " (" + (groupIdx-1) + ")] " + groupQty, module);
                Debug.log("Shipping info (after) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
            }
        }

        // save all the updated information
        try {
            saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }

    private static ShoppingCart loadCartForUpdate(LocalDispatcher dispatcher, GenericDelegator delegator, GenericValue userLogin, String orderId) throws GeneralException {
        // find ship group associations
        List shipGroupAssocs = null;
        try {
            shipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        // cancel existing inventory reservations
        if (shipGroupAssocs != null) {
            Iterator iri = shipGroupAssocs.iterator();
            while (iri.hasNext()) {
                GenericValue shipGroupAssoc = (GenericValue) iri.next();
                String orderItemSeqId = shipGroupAssoc.getString("orderItemSeqId");
                String shipGroupSeqId = shipGroupAssoc.getString("shipGroupSeqId");

                Map cancelCtx = UtilMisc.toMap("userLogin", userLogin, "orderId", orderId);
                cancelCtx.put("orderItemSeqId", orderItemSeqId);
                cancelCtx.put("shipGroupSeqId", shipGroupSeqId);

                Map cancelResp = null;
                try {
                    cancelResp = dispatcher.runSync("cancelOrderInventoryReservation", cancelCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(cancelResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
                }
            }
        }

        // load the order into a shopping cart
        Map loadCartResp = null;
        try {
            loadCartResp = dispatcher.runSync("loadCartFromOrder", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (ServiceUtil.isError(loadCartResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(loadCartResp));
        }

        ShoppingCart cart = (ShoppingCart) loadCartResp.get("shoppingCart");
        if (cart == null) {
            throw new GeneralException("Error loading shopping cart from order [" + orderId + "]");
        } else {
            cart.setOrderId(orderId);
        }

        return cart;
    }

    private static void saveUpdatedCartToOrder(LocalDispatcher dispatcher, GenericDelegator delegator, ShoppingCart cart, Locale locale, GenericValue userLogin, String orderId) throws GeneralException {
        // get/set the shipping estimates
        int shipGroups = cart.getShipGroupSize();
        for (int gi = 0; gi < shipGroups; gi++) {
            String shipmentMethodTypeId = cart.getShipmentMethodTypeId(gi);
            String carrierPartyId = cart.getCarrierPartyId(gi);
            Debug.log("Getting ship estimate for group #" + gi + " [" + shipmentMethodTypeId + " / " + carrierPartyId + "]", module);
            Map result = ShippingEvents.getShipGroupEstimate(dispatcher, delegator, cart, gi);
            if (ServiceUtil.isError(result)) {
                Debug.logError(ServiceUtil.getErrorMessage(result), module);
                throw new GeneralException(ServiceUtil.getErrorMessage(result));
            }

            Double shippingTotal = (Double) result.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = new Double(0.00);
            }
            cart.setItemShipGroupEstimate(shippingTotal.doubleValue(), gi);
        }

        // calc the sales tax
        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        try {
            coh.calcAndAddTax();
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        // validate the payment methods
        Map validateResp = coh.validatePaymentMethods();
        if (ServiceUtil.isError(validateResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(validateResp));
        }

        // get the new orderItems, adjustments, shipping info and payments from the cart
        List toStore = new LinkedList();
        toStore.addAll(cart.makeOrderItems());
        toStore.addAll(cart.makeAllAdjustments());
        toStore.addAll(cart.makeAllShipGroupInfos());
        toStore.addAll(cart.makeAllOrderPaymentInfos());

        // set the orderId & other information on all new value objects
        Iterator tsi = toStore.iterator();
        while (tsi.hasNext()) {
            GenericValue valueObj = (GenericValue) tsi.next();
            valueObj.set("orderId", orderId);
            if ("OrderItemShipGroup".equals(valueObj.getEntityName())) {
                // ship group
                if (valueObj.get("carrierRoleTypeId") == null) {
                    valueObj.set("carrierRoleTypeId", "CARRIER");
                }
            } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                // shipping / tax adjustment(s)
                if (valueObj.get("orderItemSeqId") == null || valueObj.getString("orderItemSeqId").length() == 0) {
                    valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                }
                valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
            } else if ("OrderPaymentPreference".equals(valueObj.getEntityName())) {
                if (valueObj.get("orderPaymentPreferenceId") == null) {
                    valueObj.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                    valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                    valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                }
                if (valueObj.get("statusId") == null) {
                    valueObj.set("statusId", "PAYMENT_NOT_RECEIVED");
                }
            }
        }
        Debug.log("To Store Contains: " + toStore, module);

        // cancel promo items -- if the promo still qualifies it will be added by the cart
        List promoItems = null;
        try {
            promoItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId, "isPromo", "Y"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (promoItems != null) {
            Iterator pii = promoItems.iterator();
            while (pii.hasNext()) {
                GenericValue promoItem = (GenericValue) pii.next();
                Map cancelPromoCtx = UtilMisc.toMap("orderId", orderId);
                cancelPromoCtx.put("orderItemSeqId", promoItem.getString("orderItemSeqId"));
                cancelPromoCtx.put("userLogin", userLogin);
                Map cancelResp = null;
                try {
                    cancelResp = dispatcher.runSync("cancelOrderItem", cancelPromoCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(cancelResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
                }
            }
        }

        // cancel exiting authorizations
        Map releaseResp = null;
        try {
            releaseResp = dispatcher.runSync("releaseOrderPayments", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (ServiceUtil.isError(releaseResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(releaseResp));
        }

        // cancel other (non-completed and non-cancelled) payments
        List paymentPrefsToCancel = null;
        try {
            List exprs = UtilMisc.toList(new EntityExpr("orderId", EntityOperator.EQUALS, orderId));
            exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_RECEIVED"));
            exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
            exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_DECLINED"));
            exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_SETTLED"));
            EntityCondition cond = new EntityConditionList(exprs, EntityOperator.AND);
            paymentPrefsToCancel = delegator.findByCondition("OrderPaymentPreference", cond, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (paymentPrefsToCancel != null) {
            Iterator oppi = paymentPrefsToCancel.iterator();
            while (oppi.hasNext()) {
                GenericValue opp = (GenericValue) oppi.next();
                try {
                    opp.set("statusId", "PAYMENT_CANCELLED");
                    opp.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
            }
        }

        // remove the adjustments
        try {
            List adjExprs = new LinkedList();
            adjExprs.add(new EntityExpr("orderId", EntityOperator.EQUALS, orderId));
            List exprs = new LinkedList();
            exprs.add(new EntityExpr("orderAdjustmentTypeId", EntityOperator.EQUALS, "PROMOTION_ADJUSTMENT"));
            exprs.add(new EntityExpr("orderAdjustmentTypeId", EntityOperator.EQUALS, "SHIPPING_CHARGES"));
            exprs.add(new EntityExpr("orderAdjustmentTypeId", EntityOperator.EQUALS, "SALES_TAX"));
            adjExprs.add(new EntityConditionList(exprs, EntityOperator.OR));
            EntityCondition cond = new EntityConditionList(adjExprs, EntityOperator.AND);
            delegator.removeByCondition("OrderAdjustment", cond);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        // store the new items/adjustments
        try {
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        // make the order item object map & the ship group assoc list
        List orderItemShipGroupAssoc = new LinkedList();
        Map itemValuesBySeqId = new HashMap();
        Iterator oii = toStore.iterator();
        while (oii.hasNext()) {
            GenericValue v = (GenericValue) oii.next();
            if ("OrderItem".equals(v.getEntityName())) {
                itemValuesBySeqId.put(v.getString("orderItemSeqId"), v);
            } else if ("OrderItemShipGroupAssoc".equals(v.getEntityName())) {
                orderItemShipGroupAssoc.add(v);
            }
        }

        // reserve the inventory
        String productStoreId = cart.getProductStoreId();
        String orderTypeId = cart.getOrderType();
        List resErrorMessages = new LinkedList();
        try {
            Debug.log("Calling reserve inventory...", module);
            reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupAssoc, itemValuesBySeqId,
                    orderTypeId, productStoreId, resErrorMessages);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        if (resErrorMessages.size() > 0) {
            throw new GeneralException(ServiceUtil.getErrorMessage(ServiceUtil.returnError(resErrorMessages)));
        }
    }

    public static Map processOrderPayments(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        String orderId = (String) context.get("orderId");

        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        String productStoreId = orh.getProductStoreId();

        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        // process the payments
        if (!"PURCHASE_ORDER".equals(cart.getOrderType())) {
            GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
            Map paymentResp = null;
            try {
                Debug.log("Calling process payments...", module);
                //Debug.set(Debug.VERBOSE, true);
                paymentResp = coh.processPayment(productStore, userLogin);
                //Debug.set(Debug.VERBOSE, false);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            } catch (GeneralRuntimeException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (ServiceUtil.isError(paymentResp)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(paymentResp));
            }
        }
        return ServiceUtil.returnSuccess();
    }

    // sample test services
    public static Map shoppingCartTest(DispatchContext dctx, Map context) {
        Locale locale = (Locale) context.get("locale");
        ShoppingCart cart = new ShoppingCart(dctx.getDelegator(), "9000", "webStore", locale, "USD");
        try {
            cart.addOrIncreaseItem("GZ-1005", null, 1, null, null, null, null, null, null, null, "DemoCatalog", null, null, null, dctx.getDispatcher());
            } catch (CartItemModifyException e) {
            Debug.logError(e, module);
        } catch (ItemNotFoundException e) {
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

    /**
     * Service to create a payment using an order payment preference.
     * @return Map
     */
    public static Map createPaymentFromPreference(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String paymentRefNum = (String) context.get("paymentRefNum");
        String paymentFromId = (String) context.get("paymentFromId");
        String comments = (String) context.get("comments");
        try {
            // get the order payment preference
            GenericValue orderPaymentPreference = delegator.findByPrimaryKey("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId));
            if (orderPaymentPreference == null) {
                return ServiceUtil.returnError("Failed to create Payment: Cannot find OrderPaymentPreference with orderPaymentPreferenceId " + orderPaymentPreferenceId);
            }

            // get the order header
            GenericValue orderHeader = orderPaymentPreference.getRelatedOne("OrderHeader");
            if (orderHeader == null) {
                return ServiceUtil.returnError("Failed to create Payment: Cannot get related OrderHeader from payment preference");
            }

            // get the store for the order
            GenericValue productStore = orderHeader.getRelatedOne("ProductStore");
            if (productStore == null) {
                return ServiceUtil.returnError("Failed to create Payment: Cannot get the ProductStore for the order header");
            }

            // set the payToPartyId
            String payToPartyId = productStore.getString("payToPartyId");
            if (payToPartyId == null) {
                return ServiceUtil.returnError("Failed to create Payment: Cannot get the ProductStore for the order header");
            }

            // create the payment
            Map paymentParams = new HashMap();
            paymentParams.put("paymentTypeId", "CUSTOMER_PAYMENT");
            paymentParams.put("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
            paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
            paymentParams.put("amount", orderPaymentPreference.getDouble("maxAmount"));
            paymentParams.put("statusId", "PMNT_RECEIVED");
            paymentParams.put("effectiveDate", UtilDateTime.nowTimestamp());
            paymentParams.put("partyIdTo", payToPartyId);
            if (paymentRefNum != null) {
                paymentParams.put("paymentRefNum", paymentRefNum);
            }
            if (paymentFromId != null) {
                paymentParams.put("partyIdFrom", paymentFromId);
            } else {
                paymentParams.put("partyIdFrom", "_NA_");
            }
            if (comments != null) {
                paymentParams.put("comments", comments);
            }
            paymentParams.put("userLogin", userLogin);

            return dispatcher.runSync("createPayment", paymentParams);

        } catch (GenericEntityException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        } catch (GenericServiceException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        }
    }


    
    /** deprecated
     * Explodes MARKET_PKG_AUTO item by replacing it with the underlying items (productIdTo in ProductAssoc.)
     * Also pro-rates the adjustments (sales tax, promotions, etc.) among the underlying items.
     * If there is a price difference between the MARKET_PKG_AUTO parent item and the sum of the underlying items,
     * a new OrderAdjustment, "MKTG_PKG_AUTO_ADJUST" is created, to record the difference.
     *
     * @param orderItems
     * @param orderAdjustments
     * @param orderItemShipGroupInfo
     * @param orderItemPriceInfo
     * @param orderTypeId
     * @param delegator
     * @param dispatcher
     * @param locale
     */
    public static void explodeMarketingPkgAutoItem(List orderItems, List orderAdjustments, List orderItemShipGroupInfo, List orderItemPriceInfo, String orderTypeId,
        GenericDelegator delegator, LocalDispatcher dispatcher, Locale locale) throws Exception {

        Debug.logWarning("explodeMarketingPkgAutoItem is deprecated and will be removed.  Use productTypeId=MARKETING_PKG_AUTO and assocTypeId=MANUF_COMPONENT instead!", module);
        List newOrderItems = new ArrayList();
        List newOrderAdjustments = new ArrayList();
        List newOrderItemShipGroupInfo = new ArrayList();
        Iterator itemIter = orderItems.iterator();

        // loop through all order items to see if the need to be "exploded"
        while (itemIter.hasNext()) {
            GenericValue orderItem = (GenericValue) itemIter.next();
            String productId = orderItem.getString("productId");
            Double quantity = orderItem.getDouble("quantity");
            Double unitPrice = orderItem.getDouble("unitPrice");
            String orderItemSeqId = orderItem.getString("orderItemSeqId");
            String prodCatalogId = orderItem.getString("prodCatalogId");

            // ignore order items which are not products
            if (productId == null || "".equals(productId)) {
                continue;
            }

            // if an order item does not have a quantity, skip it
            if (quantity == null) {
                Debug.logWarning("Order item seq [" + orderItemSeqId + "] has a null quantity, so it cannot be exploded" , module);
                continue;
            }
            
            try {
                //deal with the order adjustment without orderItemSeqId.  These are adjustments of the order, and we just keep them.
                if (orderAdjustments != null && orderAdjustments.size() > 0) {
                    for (int a = 0; orderAdjustments.size() > a; a++) {
                        GenericValue orderAdjustment = (GenericValue) orderAdjustments.get(a);
                        if (UtilValidate.isEmpty(orderAdjustment.getString("orderItemSeqId"))) {
                            newOrderAdjustments.add(orderAdjustment);
                        }//if
                    }//for
                }//if

                //deal with the item ship group without orderItemSeqId.  Similarly, just keep these
                if (orderItemShipGroupInfo != null && orderItemShipGroupInfo.size() > 0) {
                    Iterator osiInfos = orderItemShipGroupInfo.iterator();
                    while (osiInfos.hasNext()) {
                        GenericValue valueObj = (GenericValue) osiInfos.next();
                        if ("OrderAdjustment".equals(valueObj.getEntityName()) || "OrderItemShipGroupAssoc".equals(valueObj.getEntityName())) {
                            if (UtilValidate.isEmpty(valueObj.getString("orderItemSeqId"))) {
                                newOrderItemShipGroupInfo.add(valueObj);
                            }//if
                        } else {
                            newOrderItemShipGroupInfo.add(valueObj);
                        }//if
                    }//while
                }//if

                // find the products which are components of a MARKETING_PKG_AUTO, if any, for this product
                List productAssocList = null;
                Map tmpResult = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", productId, "type", "MARKETING_PKG_AUTO"));
                if (tmpResult.get("assocProducts") != null) {
                    productAssocList = (List) tmpResult.get("assocProducts");
                }
                
                if (productAssocList != null && productAssocList.size() > 0) {
                    Debug.logInfo("The product [" + productId + "] explodes to [" + productAssocList.toString() + "]", module);
                    // now add the associated products to the order
                    List assocOrderItems = new ArrayList();
                    double sumSubItemsPrice = 0;
                    double sumAssocQty = 0;
                    for (int i = 0; productAssocList.size() > i; i++) {
                        GenericValue productAssoc = (GenericValue) productAssocList.get(i);
                        GenericValue productTo = productAssoc.getRelatedOne("AssocProduct");
                        String productIdTo = productTo.getString("productId");
                        String itemDescription = "";
                        
                        Double productToQuantity = productAssoc.getDouble("quantity");
                        
                        if (productToQuantity == null) {
                            productToQuantity = new Double(1.0);
                            Debug.logWarning("For order item [" + orderItemSeqId + "] Product association [" + productAssoc + "] had a null quantity, assuming 1", module);
                        }
                        
                        Double newQuantity = new Double(productToQuantity.doubleValue() * quantity.doubleValue());

                        Double listPrice = new Double(0);
                        Double basePrice = new Double(0);

                        // get the product name
                        if (productTo != null) {
            	           itemDescription = ProductContentWrapper.getProductContentAsText(productTo, "PRODUCT_NAME", locale);
                           // if the product name is null or empty, see if there is an associated virtual product and get the productName of that product
                           if (UtilValidate.isEmpty(itemDescription)) {
                              GenericValue parentProduct = ProductWorker.getParentProduct(productIdTo, delegator);
                              if (parentProduct != null) {
                                itemDescription = ProductContentWrapper.getProductContentAsText(parentProduct, "PRODUCT_NAME", locale);
                              }//if
                           }//if
                        }//if

                        // calculate price of the associated (component) product using calculateProductPrice
                        try {
                            Map priceContext = new HashMap();
                            priceContext.put("product", productTo);
                            priceContext.put("prodCatalogId", prodCatalogId);
                            priceContext.put("quantity", newQuantity);
                            Map priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
                            if (ModelService.RESPOND_ERROR.equals(priceResult.get(ModelService.RESPONSE_MESSAGE))) {
                                Debug.logWarning("There was an error while calculating the price: " + priceResult.get(ModelService.ERROR_MESSAGE), module);
                            }// if

                            Boolean validPriceFound = (Boolean) priceResult.get("validPriceFound");
                            if (!validPriceFound.booleanValue()) {
                                Debug.logWarning("Could not find a valid price for the product with ID [" + productIdTo + "], not adding to cart.", module);
                            }// if

                            if (priceResult.get("listPrice") != null) {
                                listPrice = (Double) priceResult.get("listPrice");
                            }// if
                            if (priceResult.get("price") != null) {
                                basePrice = (Double) priceResult.get("price");
                            }// if
                        } catch (Exception e) {
                            Debug.logWarning(e, "There was an error while calculating the price", module);
                            throw e;
                        }// try
                        Debug.logInfo("The product [" + productIdTo + "] Price [" + basePrice + "]", module);

                        // used to tally up total price of component items, so as to calculate correct adjustment later
                        sumSubItemsPrice = sumSubItemsPrice + newQuantity.doubleValue() * basePrice.doubleValue();
                        sumAssocQty = sumAssocQty + productToQuantity.intValue();

                        // now create the new order item
                        GenericValue newOrderItem = GenericValue.create(orderItem);
                        newOrderItem.set("productId", productIdTo);
                        newOrderItem.set("quantity", newQuantity);
                        newOrderItem.set("unitPrice", basePrice);
                        newOrderItem.set("unitListPrice", listPrice);
                        newOrderItem.set("orderItemSeqId", orderItemSeqId + "-" + i);
                        newOrderItem.set("cancelQuantity", productToQuantity);
                        newOrderItem.set("itemDescription", itemDescription);
                        assocOrderItems.add(newOrderItem);
                        Debug.logInfo("explode OrderItem [" + orderItem.toString() + "] to [" + newOrderItem + "]", module);
                    }// for
                    
                    // If there were a difference between the price of the parent item and the sum of the prices of the marketing package components,
                    // then create an OrderAdjustment for each new order item and pro-rate the difference based on its quantity and the total quantity of all
                    // the component items.
                    double originalItemPrice = quantity.doubleValue() * unitPrice.doubleValue();
                    double adjustmentPrice = originalItemPrice - sumSubItemsPrice;
                    if (adjustmentPrice != 0) {
                        for (int a = 0; assocOrderItems.size() > a; a++) {
                            GenericValue assocOrderItem = (GenericValue) assocOrderItems.get(a);
                            String assocOrderItemSeqId = assocOrderItem.getString("orderItemSeqId");
                            Double productToQuantity = assocOrderItem.getDouble("cancelQuantity");

                            Double percentage = new Double(productToQuantity.doubleValue() / sumAssocQty);
                            Double amount = new Double(adjustmentPrice * percentage.doubleValue());
                            GenericValue newOrderAdjustment = delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentTypeId", "MKTG_PKG_AUTO_ADJUST"));
                            newOrderAdjustment.put("orderItemSeqId", assocOrderItemSeqId);
                            newOrderAdjustment.put("amount", new Double(amount.doubleValue()));
                            newOrderAdjustments.add(newOrderAdjustment);
                            Debug.logInfo("Add new Order Adjustment [" + newOrderAdjustment.toString() + "] for Order Item [" + assocOrderItem.toString() + "]", module);
                        }//for
                    }//if

                    // similarly prop-rate each order adjustment for the parent item to all the marketing package components
                    if (orderAdjustments != null && orderAdjustments.size() > 0) {
                        for (int a = 0; orderAdjustments.size() > a; a++) {
                            GenericValue orderAdjustment = (GenericValue) orderAdjustments.get(a);
                            if (orderItemSeqId.equals(orderAdjustment.getString("orderItemSeqId"))) {
                                Debug.log("Explode Order Adjustment [" + orderAdjustment.toString() + "]", module);
                                for (int b = 0; assocOrderItems.size() > b; b++) {
                                    GenericValue assocOrderItem = (GenericValue) assocOrderItems.get(b);
                                    String assocOrderItemSeqId = assocOrderItem.getString("orderItemSeqId");
                                    Double productToQuantity = assocOrderItem.getDouble("cancelQuantity");
                                    Double amount = orderAdjustment.getDouble("amount");

                                    Double percentage = new Double(productToQuantity.doubleValue() / sumAssocQty);
                                    amount = new Double(amount.doubleValue() * percentage.doubleValue());
                                    GenericValue newOrderAdjustment = GenericValue.create(orderAdjustment);
                                    newOrderAdjustment.put("orderItemSeqId", assocOrderItemSeqId);
                                    newOrderAdjustment.put("amount", amount);
                                    newOrderAdjustments.add(newOrderAdjustment);
                                    Debug.logInfo("Add new Order Adjustment [" + newOrderAdjustment.toString() + "]", module);
                                }//for
                            }//if
                        }//for
                    }//if

                    // assign the new order items to the same ship group as the parent item
                    if (orderItemShipGroupInfo != null && orderItemShipGroupInfo.size() > 0) {
                        Iterator osiInfos = orderItemShipGroupInfo.iterator();
                        while (osiInfos.hasNext()) {
                            GenericValue valueObj = (GenericValue) osiInfos.next();
                            if ("OrderItemShipGroupAssoc".equals(valueObj.getEntityName())) {
                                // assign the ship group
                                if (orderItemSeqId.equals(valueObj.getString("orderItemSeqId"))) {
                                    Debug.log("Explode OrderItemShipGroupAssoc [" + valueObj.toString() + "]", module);
                                    for (int a = 0; assocOrderItems.size() > a; a++) {
                                        GenericValue assocOrderItem = (GenericValue) assocOrderItems.get(a);
                                        String assocOrderItemSeqId = assocOrderItem.getString("orderItemSeqId");
                                        GenericValue newValueObj = GenericValue.create(valueObj);
                                        newValueObj.put("orderItemSeqId", assocOrderItemSeqId);
                                        newValueObj.put("quantity", assocOrderItem.getDouble("quantity"));
                                        newOrderItemShipGroupInfo.add(newValueObj);
                                        Debug.log("Create new  OrderItemShipGroupAssoc [" + newValueObj.toString() + "]", module);
                                    }//for
                                }//if
                            } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                                // orderItemShipGroupInfo might also contain an order adjustment for tax adjustments to this ship group.  In that case,
                                // pro-rate it as well.
                                // TODO: it would be nice to re-factor this with the pro-rating code up above into one common code block.
                                if (orderItemSeqId.equals(valueObj.getString("orderItemSeqId"))) {
                                    Debug.log("Explode OrderAdjustment [" + valueObj.toString() + "] ", module);
                                    for (int a = 0; assocOrderItems.size() > a; a++) {
                                        GenericValue assocOrderItem = (GenericValue) assocOrderItems.get(a);
                                        String assocOrderItemSeqId = assocOrderItem.getString("orderItemSeqId");

                                        Double productToQuantity = assocOrderItem.getDouble("cancelQuantity");
                                        Double amount = valueObj.getDouble("amount");

                                        Double percentage = new Double(productToQuantity.doubleValue() / sumAssocQty);
                                        amount = new Double(amount.doubleValue() * percentage.doubleValue());
                                        GenericValue newValueObj = GenericValue.create(valueObj);
                                        Double itemQuantity = assocOrderItem.getDouble("quantity");
                                        newValueObj.put("sourcePercentage", new Double(amount.doubleValue() / itemQuantity.doubleValue()));
                                        newValueObj.put("orderItemSeqId", assocOrderItemSeqId);
                                        newValueObj.put("amount", amount);
                                        newOrderItemShipGroupInfo.add(newValueObj);
                                        Debug.log("Create new  Order Adjustment [" + newValueObj.toString() + "]", module);
                                    }//for
                                }//if
                            }//if
                        }//while
                    }//if

                    for (int a = 0; assocOrderItems.size() > a; a++) {
                        GenericValue assocOrderItem = (GenericValue) assocOrderItems.get(a);
                        assocOrderItem.remove("cancelQuantity");
                        newOrderItems.add(assocOrderItem);
                    }//for

                    // Deal with price rules: get all price rules for this line item by filtering the input price infos for this order item's seq id
                    List andCondList = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, orderItem.getString("orderItemSeqId")));
                    List lineItemPriceInfos = EntityUtil.filterByAnd(orderItemPriceInfo, andCondList);
                    if ((lineItemPriceInfos != null) && (lineItemPriceInfos.size() > 0)) {

                        // loop through the exploded items
                        Iterator expItemIter = assocOrderItems.iterator();
                        while (expItemIter.hasNext()) {
                            GenericValue expItem = (GenericValue) expItemIter.next();

                            // apply price rules
                            List newOrderItemPriceInfos = new LinkedList();
                            Iterator oipii = lineItemPriceInfos.iterator();
                            while (oipii.hasNext()) {
                                GenericValue oipi = (GenericValue) oipii.next();
                                GenericValue newoipi = (GenericValue) oipi.clone();
                                newoipi.set("orderItemSeqId", expItem.getString("orderItemSeqId"));
                                newOrderItemPriceInfos.add(newoipi);
                                Debug.logInfo("Applying price rule " + oipi.getString("productPriceRuleId") + " to order item seq Id " + expItem.getString("orderItemSeqId"), module);
                            }
                            // add the new order item price infos
                            orderItemPriceInfo.addAll(newOrderItemPriceInfos);
                        }
                        // finally, remove the price infos for the marketing package
                        orderItemPriceInfo.removeAll(lineItemPriceInfos);
                    }
                } else {
                    //deal with the orderItem which cannot be explode - just carry them with their adjustments over in the same ship group
                    newOrderItems.add(orderItem);
                    //order adjustment
                    if (orderAdjustments != null && orderAdjustments.size() > 0) {
                        for (int a = 0; orderAdjustments.size() > a; a++) {
                            GenericValue orderAdjustment = (GenericValue) orderAdjustments.get(a);
                            if (orderItemSeqId.equals(orderAdjustment.getString("orderItemSeqId"))) {
                                newOrderAdjustments.add(orderAdjustment);
                            }//if
                        }//for
                    }//if
                    //order item ship group
                    if (orderItemShipGroupInfo != null && orderItemShipGroupInfo.size() > 0) {
                        Iterator osiInfos = orderItemShipGroupInfo.iterator();
                        while (osiInfos.hasNext()) {
                            GenericValue valueObj = (GenericValue) osiInfos.next();
                            if ("OrderAdjustment".equals(valueObj.getEntityName()) || "OrderItemShipGroupAssoc".equals(valueObj.getEntityName())) {
                                if (orderItemSeqId.equals(valueObj.getString("orderItemSeqId"))) {
                                    newOrderItemShipGroupInfo.add(valueObj);
                                }//if
                            }//if
                        }//while
                    }//if
                } // if
            } catch (Exception e) {
                Debug.logWarning(e, "There was an error in the [explodeMarketingPkgAutoItem]: " + e.getMessage(), module);
                throw e;
            }// try
        }// while

        orderItems.clear();
        orderItems.addAll(newOrderItems);
        orderAdjustments.clear();
        orderAdjustments.addAll(newOrderAdjustments);
        orderItemShipGroupInfo.clear();
        orderItemShipGroupInfo.addAll(newOrderItemShipGroupInfo);
    }// explodeMarketingPkgAutoItem

    public static Map massChangeApproved(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List orderIds = (List) context.get("orderIdList");
        Iterator i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = (String) i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (orderHeader == null) {
                return ServiceUtil.returnError("Order #" + orderId + " was not found.");
            }

            // by changing all the items to approved, the checkOrderItemStatus service will automatically set the order to approved.
            Map ctx = FastMap.newInstance();
            ctx.put("statusId", "ITEM_APPROVED");
            ctx.put("orderId", orderId);
            ctx.put("userLogin", userLogin);
            Map resp = null;
            try {
                resp = dispatcher.runSync("changeOrderItemStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resp));
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map massPickOrders(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        // grouped by facility
        Map facilityOrdersMap = FastMap.newInstance();

        // make the list per facility
        List orderIds = (List) context.get("orderIdList");
        Iterator i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = (String) i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            List invInfo = null;
            try {
                invInfo = delegator.findByAnd("OrderItemAndShipGrpInvResAndItem",
                        UtilMisc.toMap("orderId", orderId, "statusId", "ITEM_APPROVED"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (invInfo != null) {
                Iterator ii = invInfo.iterator();
                while (ii.hasNext()) {
                    GenericValue inv = (GenericValue) ii.next();
                    String facilityId = inv.getString("facilityId");
                    List orderIdsByFacility = (List) facilityOrdersMap.get(facilityId);
                    if (orderIdsByFacility == null) {
                        orderIdsByFacility = new ArrayList();
                    }
                    orderIdsByFacility.add(orderId);
                    facilityOrdersMap.put(facilityId, orderIdsByFacility);
                }
            }
        }

        // now create the pick lists for each facility
        Iterator fi = facilityOrdersMap.keySet().iterator();
        while (fi.hasNext()) {
            String facilityId = (String) fi.next();
            List orderIdList = (List) facilityOrdersMap.get(facilityId);

            Map ctx = FastMap.newInstance();
            ctx.put("userLogin", userLogin);
            ctx.put("orderIdList", orderIdList);
            ctx.put("facilityId", facilityId);

            Map resp = null;
            try {
                resp = dispatcher.runSync("createPicklistFromOrders", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resp));
            }
        }

        return ServiceUtil.returnSuccess();
    }
}
