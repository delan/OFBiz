/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.ftl.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.WfUtil;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.party.contact.*;
import org.ofbiz.commonapp.product.catalog.*;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.WrappingTemplateModel;

/**
 * Order Processing Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Revision$
 * @since      2.0
 */

public class OrderServices {

    public static final String module = OrderServices.class.getName();
    public static final String resource = "org.ofbiz.commonapp.order.order.PackageMessages";

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
        
        // list of out-of-stock productIds
        List productOutOfStock = new ArrayList();

        // check inventory and other things for each item
        String prodCatalogId = (String) context.get("prodCatalogId");
        List errorMessages = new LinkedList();
        Map normalizedItemQuantities = new HashMap();
        Map normalizedItemNames = new HashMap();
        Iterator itemIter = orderItems.iterator();        
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        // need to run through the items combining any cases where multiple lines refer to the 
        // same product so the inventory check will work correctly
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
            }
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
                Debug.logError(e, errMsg);
                errorMessages.add(errMsg);
                continue;
            }

            if (product == null) {
                String errMsg = UtilProperties.getMessage(resource, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(errMsg);
                errorMessages.add(errMsg);
                continue;
            }
            
            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {            
                // check to see if introductionDate hasn't passed yet
                if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
                    String excMsg = UtilProperties.getMessage(resource, "product.not_yet_for_sale", 
                    		new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {
                // check to see if salesDiscontinuationDate has passed
                if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
                    String excMsg = UtilProperties.getMessage(resource, "product.no_longer_for_sale", 
                    		new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg);
                    errorMessages.add(excMsg);
                    continue;
                }
            }
                 
            if ("SALES_ORDER".equals(orderTypeId) || "WORK_ORDER".equals(orderTypeId)) {
                // check to see if we have inventory available  
                boolean isCatalogInventoryRequired = CatalogWorker.isCatalogInventoryRequired(prodCatalogId, product, delegator);
                boolean isCatalogInventoryAvailable = CatalogWorker.isCatalogInventoryAvailable(prodCatalogId, currentProductId, currentQuantity.doubleValue(), delegator, dispatcher);               
                if (isCatalogInventoryRequired) {                                
                    if (!isCatalogInventoryAvailable) {
                        String invErrMsg = UtilProperties.getMessage(resource, "product.out_of_stock", 
                                new Object[] { getProductName(product, itemName), currentProductId }, locale);
                        Debug.logWarning(invErrMsg);
                        errorMessages.add(invErrMsg);
                        continue;
                    }
                } else if (!isCatalogInventoryAvailable) {
                    productOutOfStock.add(currentProductId);
                    Debug.log("Added item: " + currentProductId + " to OOS list", module);
                }
            }
        }

        if (errorMessages.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);
            return result;
        }
        
        String initialStatus = "ORDER_ORDERED";
        if ("PURCHASE_ORDER".equals(orderTypeId)) {
            initialStatus = "ORDER_CREATED";
        }

        // create the order object
        String orderId = delegator.getNextSeqId("OrderHeader").toString();
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
                Long adjSeqId = delegator.getNextSeqId("OrderAdjustment");
    
                if (adjSeqId == null) {
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get next sequence id for OrderAdjustment, cannot create order.");
                    return result;
                }
                orderAdjustment.set("orderAdjustmentId", adjSeqId.toString());
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
            if (productOutOfStock.contains(orderItem.getString("productId"))) {
                orderItem.set("statusId", "ITEM_BACKORDERED");
            }
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

        // set the item price info; NOTE: this must be after the orderItems are stored for referential integrity
        List orderItemPriceInfo = (List) context.get("orderItemPriceInfos");
        if (orderItemPriceInfo != null && orderItemPriceInfo.size() > 0) {
            Iterator oipii = orderItemPriceInfo.iterator();

            while (oipii.hasNext()) {
                GenericValue oipi = (GenericValue) oipii.next();
                Long oipiSeqId = delegator.getNextSeqId("OrderItemPriceInfo");

                if (oipiSeqId == null) {
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get next sequence id for OrderItemPriceInfo, cannot create order.");
                    return result;
                }
                oipi.set("orderItemPriceInfoId", oipiSeqId.toString());
                oipi.set("orderId", orderId);
                toBeStored.add(oipi);
            }
        }

        // define the roles for the order
        List userOrderRoleTypes = null;
        if ("SALES_ORDER".equals(orderTypeId)) {  
            userOrderRoleTypes = UtilMisc.toList("END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER");    
        } else if ("PURCHASE_ORDER".equals(orderTypeId)) {
            userOrderRoleTypes = UtilMisc.toList("SHIP_FROM_VENDOR", "BILL_FROM_VENDOR");
        } else if ("WORK_ORDER".equals(orderTypeId)) {
            // TODO: set the work order roles
        } else {
            // TODO: some default behavior
        } 
                                           
        // now add the roles
        if (userOrderRoleTypes != null && partyId != null && !"_NA_".equals(partyId)) {
            Iterator i = userOrderRoleTypes.iterator();
            while (i.hasNext()) {
                String roleType = (String) i.next();
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
        
        // find all parties in role VENDOR associated with WebSite, associated first valid with the Order
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
                Debug.logError(e, "Error looking up Vendor for the current Web Site");
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
            boolean beganTransaction = TransactionUtil.begin();                                    
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
                            Double inventoryNotReserved = CatalogWorker.reserveCatalogInventory(prodCatalogId,
                                orderItem.getString("productId"), orderItem.getDouble("quantity"),
                                orderItem.getString("orderId"), orderItem.getString("orderItemSeqId"),
                                userLogin, delegator, dispatcher);                        
    
                            if (inventoryNotReserved != null) {
                                // if inventoryNotReserved is not 0.0 then that is the amount that it couldn't reserve
                                GenericValue product = null;
        
                                try {
                                    product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", orderItem.getString("productId")));
                                } catch (GenericEntityException e) {
                                    Debug.logError(e, "Error when looking up product in createOrder service, product failed inventory reservation");
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
                        TransactionUtil.rollback(beganTransaction);
                        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        result.put(ModelService.ERROR_MESSAGE_LIST, resErrorMessages);
                        return result;
                    }
                    // END inventory reservation
                }

                TransactionUtil.commit(beganTransaction);

                result.put("orderId", orderId);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            } catch (GenericEntityException e) {
                TransactionUtil.rollback(beganTransaction);
                Debug.logError(e);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create order (write error: " + e.getMessage() + ").");
            }
        } catch (GenericTransactionException e) {
            Debug.logError(e);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not create order (transaction error on write: " + e.getMessage() + ").");
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
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");         
                
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));   
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("ERROR: Cannot get OrderHeader entity: " + e.getMessage());
        }
        
        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            Double currentTotal = orderHeader.getDouble("grandTotal");
            if (orh.getOrderGrandTotal() != currentTotal.doubleValue()) {
                orderHeader.set("grandTotal", new Double(orh.getOrderGrandTotal()));            
                try {
                    orderHeader.store();
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError("ERROR: Cannot write OrderHeader entity: " + e.getMessage());
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
            return ServiceUtil.returnError("ERROR: Cannot get OrderRole entity: " + e.getMessage());
        }
        
        if (orderItems != null && orderItems.size() > 0) {
            List toBeStored = new ArrayList();
            Iterator itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = (GenericValue) itemsIterator.next();
                if (orderItem == null)
                    ServiceUtil.returnError("ERROR: Cannot change item status; item not found.");
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : Status Change: [" + orderId + "] (" + orderItem.getString("orderItemSeqId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setIte,Status] : From Status : " + orderItem.getString("statusId"));
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId);
                
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
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"));
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId);
            
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
            Debug.logError(e);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not set tracking number (" + e.getMessage() + ").");
        }
        return result;
    }
    
    /** Service to check and make sure no items are in BACKORDER status */
    public static Map checkBackOrderItems(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        
        List backOrderedItems = new ArrayList();
        List orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderItem entities", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not get OrderItems (" + e.getMessage() + ").");
        }
               
        if (orderItems != null) {
            Iterator i = orderItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                if (item != null && item.get("statusId") != null) {
                    String status = item.getString("statusId");
                    if (status.equals("ITEM_BACKORDERED"))
                        backOrderedItems.add(item);
                }
            }
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put("backOrderedItems", backOrderedItems);
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
            List headerAdjustmentsToShow = OrderReadHelper.getOrderHeaderAdjustmentToShow(orderHeaderAdjustments, orderSubTotal);
           
            //templateContext.put("localOrderReadHelper", orh);
            result.put("orderId", orderId);
            result.put("orderItemSeqId", context.get("orderItemSeqId"));
            result.put("orderHeader", OrderReadHelper.getOrderHeaderDisplay(orderHeader, orderHeaderAdjustments, orderSubTotal));
            result.put("orderItems", OrderReadHelper.getOrderItemDisplay(orderItems, orderAdjustments));
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
            if (orderPaymentPreferences != null && orderPaymentPreferences.hasNext()) {
                GenericValue orderPaymentPreference = (GenericValue) orderPaymentPreferences.next();
                GenericValue paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");        
                GenericValue paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType");
                result.put("paymentMethod", paymentMethod);
                result.put("paymentMethodType", paymentMethodType);
          
                if (paymentMethod != null && "CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                    GenericValue creditCard = (GenericValue) paymentMethod.getRelatedOneCache("CreditCard");
                    result.put("creditCard", creditCard);
                    result.put("formattedCardNumber", ContactHelper.formatCreditCard(creditCard));
                } else if (paymentMethod != null && "EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                    GenericValue eftAccount = (GenericValue) paymentMethod.getRelatedOneCache("EftAccount");
                    result.put("eftAccount", eftAccount);
                }        
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
      
        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity read error", module);
            ServiceUtil.returnError("Problem with entity lookup, see error log");            
        }             
        return result;
    }
    
    /** Service to email a customer with initial order confirmation */
    public static Map prepareOrderConfirmation(DispatchContext ctx, Map context) {
        return prepareOrderEmail(ctx, (String) context.get("orderId"), "WES_ODR_CONFIRM");
    }
    
    /** Service to email a customer with order status changes */
    public static Map prepareOrderNotification(DispatchContext ctx, Map context) {  
        return prepareOrderEmail(ctx, (String) context.get("orderId"), "WES_ODR_CHANGE");         
    }    
    
    private static Map prepareOrderEmail(DispatchContext ctx, String orderId, String emailType) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();                                         
        String ofbizHome = System.getProperty("ofbiz.home");
        
        // get the order header and website
        GenericValue orderHeader = null;        
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting OrderHeader", module);
        }
        
        GenericValue webSiteEmail = null;
        if (orderHeader != null) {
            try {
                webSiteEmail = delegator.findByPrimaryKey("WebSiteEmailSetting", UtilMisc.toMap("webSiteId", orderHeader.get("webSiteId"), "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting the WebSiteEmailSetting", module);
            }
        }
        
        if (webSiteEmail == null) {
            return ServiceUtil.returnError("No valid email setting for website");
        } 
                    
        // get the email addresses from the order contact mech(s)
        List orderContactMechs = null;
        try {
            Map ocFields = UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "ORDER_EMAIL");
            orderContactMechs = delegator.findByAnd("OrderContactMech", ocFields);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting order contact mechs", module);
        }        

        StringBuffer emails = new StringBuffer();        
        if (orderContactMechs != null) {
            Iterator oci = orderContactMechs.iterator();
            while (oci.hasNext()) {
                try {
                    GenericValue orderContactMech = (GenericValue) oci.next();
                    GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
                    emails.append(emails.length() > 0 ? "," : "").append(contactMech.getString("infoString"));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "Problems getting contact mech from order contact mech", module);
                }                        
            }
        }  
        
        // prepare the parsed subject
        Map orderEmailData = prepareOrderEmailData(ctx, UtilMisc.toMap("orderId", orderId));
        String subjectString = webSiteEmail.getString("subject");
        subjectString = FlexibleStringExpander.expandString(subjectString, orderEmailData);
        
        result.put("templateName", ofbizHome + webSiteEmail.get("templatePath"));
        result.put("emailType", emailType);
        result.put("subject", subjectString);
        result.put("sendFrom", webSiteEmail.get("fromAddress"));        
        result.put("sendCc", webSiteEmail.get("ccAddress"));
        result.put("sendBcc", webSiteEmail.get("ccAddress"));
        result.put("sendTo", emails.toString());
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);            
                   
        return result;     
    }    
            
    /** Service to email order notifications for pending actions */
    public static Map sendProcessNotification(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator(); 
        LocalDispatcher dispatcher = ctx.getDispatcher();            
        String adminEmailList = (String) context.get("adminEmailList");
        String assignedToUser = (String) context.get("assignedPartyId");
        String assignedToRole = (String) context.get("assignedRoleTypeId");
        String workEffortId = (String) context.get("workEffortId");
        
        GenericValue workEffort = null;
        GenericValue orderHeader = null;
        String assignedEmail = null;
        
        // get the order/workflow info
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
            String sourceReferenceId = workEffort.getString("sourceReferenceId");
            if (sourceReferenceId != null)              
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", sourceReferenceId));
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Problem with entity lookup");
        }
                    
        // find the assigned user's email address(s)
        GenericValue party = null;
        Collection assignedToEmails = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", assignedToUser));
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Problem with entity lookup");
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
        String templateName = ofbizHome + "/commonapp/src/org/ofbiz/commonapp/order/order/emailprocessnotify.ftl";
        
        Map sendMailContext = new HashMap();
        sendMailContext.put("sendTo", emailList.toString());
        sendMailContext.put("sendFrom", "workflow@ofbiz.org"); // fixme
        sendMailContext.put("subject", "Workflow Notification");
        sendMailContext.put("templateName", templateName);
        sendMailContext.put("templateData", templateData);
        
        try {
            dispatcher.runAsync("sendGenericNotificationEmail", sendMailContext);
        } catch (GenericServiceException e) {
            ServiceUtil.returnError("SendMail service failed: " + e.getMessage());
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
        Long newId = delegator.getNextSeqId("OrderPaymentPreference");

        if (newId == null) {
            return ServiceUtil.returnError("ERROR: Could not create OrderPaymentPreference (id generation failure)");
        } else {
            prefId = newId.toString();
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

    /** Service to get an order contact mech. */
    public static Map getOrderAddress(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        GenericValue v = null;
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
        Map noteRes = org.ofbiz.commonapp.common.CommonServices.createNote(dctx, noteCtx);

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
            Debug.logError(ee);
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
            List taxList = getTaxAmount(delegator, product, countryCode, stateCode, itemAmount.doubleValue(), shippingAmount.doubleValue());
            itemAdjustments.add(taxList);          
        }
        if (orderShippingAmount.doubleValue() > 0) {
            List taxList = getTaxAmount(delegator, null, countryCode, stateCode, 0.00, orderShippingAmount.doubleValue());
            orderAdjustments.addAll(taxList);
        }

        Map result = UtilMisc.toMap("orderAdjustments", orderAdjustments, "itemAdjustments", itemAdjustments);

        return result;

    }

    private static List getTaxAmount(GenericDelegator delegator, GenericValue item, String countryCode, String stateCode, double itemAmount, double shippingAmount) {                              
        List adjustments = new ArrayList();
       
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
        List mainExprs = UtilMisc.toList(countryCond, stateCond);
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
                Debug.logInfo("Testing " + itemAmount + " with : " + taxLookup, module);
                if (itemAmount >= taxLookup.getDouble("minPurchase").doubleValue()) {                
                    Debug.logInfo("TaxLookup: " + taxLookup, module);
                    
                    double taxRate = taxLookup.get("salesTaxPercentage") != null ? taxLookup.getDouble("salesTaxPercentage").doubleValue() : 0;
                    double taxable = 0.00;
                    
                    if (item != null && (item.get("taxable") == null || (item.get("taxable") != null && item.getBoolean("taxable").booleanValue()))) {            
                        taxable += itemAmount;
                    }
                    if (taxLookup != null && (taxLookup.get("taxShipping") == null || (taxLookup.get("taxShipping") != null && taxLookup.getBoolean("taxShipping").booleanValue()))) {            
                        taxable += shippingAmount;
                    }
                    
                    Double taxAmount = new Double(taxable * taxRate);
                    adjustments.add(delegator.makeValue("OrderAdjustment", UtilMisc.toMap("amount", taxAmount, "orderAdjustmentTypeId", "SALES_TAX", "comments", taxLookup.getString("description"))));                        
                }                                              
            }                                                                                             
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up tax rates", module);
            return new ArrayList();            
        }
        
        return adjustments;
    }  
    
    // return / refund services
                    
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
                Debug.logError(e, "Problem creating Payment record");
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
                Debug.logError(e, "Problem storing ReturnItem updates");
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
                    List electronicTypes = UtilMisc.toList("CREDIT_CARD", "EFT_ACCOUNT");
                    
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
                    try {
                        delegator.create(response);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problems creating new ReturnItemResponse entity", module);
                        return ServiceUtil.returnError("Problems creating ReturnItemResponse entity");
                    }
                    
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
                        
                        try {
                            item.store();
                            delegator.create(returnStatus);
                        } catch (GenericEntityException e) {
                            Debug.logError("Problem updating the ReturnItem entity", module);
                            return ServiceUtil.returnError("Problem updating ReturnItem (returnItemResponseId)");
                        }
                    }
                }                                                       
            }
        } 
             
        return ServiceUtil.returnSuccess();
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
}
