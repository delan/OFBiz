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

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.party.contact.*;
import org.ofbiz.commonapp.product.catalog.*;

/**
 * Order Processing Services
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
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

        // check to make sure we have something to order
        List orderItems = (List) context.get("orderItems");

        if (orderItems.size() < 1) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource, "items.none", locale));
            return result;
        }

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
            if (normalizedItemQuantities.get(currentProductId) == null) {
                normalizedItemQuantities.put(currentProductId, new Double(orderItem.getDouble("quantity").doubleValue()));
                normalizedItemNames.put(currentProductId, new String(orderItem.getString("itemDescription")));
            } else {
                Double currentQuantity = (Double) normalizedItemQuantities.get(currentProductId);
                normalizedItemQuantities.put(currentProductId, new Double(currentQuantity.doubleValue() + orderItem.getDouble("quantity").doubleValue()));
            }
        }

		Iterator normalizedIter = normalizedItemQuantities.keySet().iterator();   
        while (normalizedIter.hasNext()) {
            // if the item is out of stock, return an error to that effect
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

            // check to see if introductionDate hasn't passed yet
            if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
                String excMsg = UtilProperties.getMessage(resource, "product.not_yet_for_sale", 
                		new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                Debug.logWarning(excMsg);
                errorMessages.add(excMsg);
                continue;
            }

            // check to see if salesDiscontinuationDate has passed
            if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {
                String excMsg = UtilProperties.getMessage(resource, "product.no_longer_for_sale", 
                		new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                Debug.logWarning(excMsg);
                errorMessages.add(excMsg);
                continue;
            }
                     
            if (CatalogWorker.isCatalogInventoryRequired(prodCatalogId, product, delegator)) {
                if (!CatalogWorker.isCatalogInventoryAvailable(prodCatalogId, currentProductId, 
                		currentQuantity.doubleValue(), delegator, dispatcher)) {
                    String invErrMsg = UtilProperties.getMessage(resource, "product.out_of_stock", 
                    		new Object[] { getProductName(product, itemName), currentProductId }, locale);
                    Debug.logWarning(invErrMsg);
                    errorMessages.add(invErrMsg);
                    continue;
                }
            }
        }

        if (errorMessages.size() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);
            return result;
        }

        // create the order object
        String orderId = delegator.getNextSeqId("OrderHeader").toString();
        String billingAccountId = (String) context.get("billingAccountId");
        GenericValue order = delegator.makeValue("OrderHeader",
                UtilMisc.toMap("orderId", orderId, "orderTypeId", "SALES_ORDER",
                    "orderDate", UtilDateTime.nowTimestamp(), "entryDate", UtilDateTime.nowTimestamp(),
                    "statusId", "ORDER_ORDERED", "billingAccountId", billingAccountId));

        if (context.get("grandTotal") != null)
            order.set("grandTotal", context.get("grandTotal"));
            
        if (UtilValidate.isNotEmpty((String) context.get("visitId"))) 
        	order.set("visitId", context.get("visitId"));
       
        if (UtilValidate.isNotEmpty((String) context.get("webSiteId")))
            order.set("webSiteId", context.get("webSiteId"));
        	
        if (userLogin != null && userLogin.get("userLoginId") != null)
            order.set("createdBy", userLogin.getString("userLoginId"));
            
        toBeStored.add(order);

        // set the orderId on all adjustments; this list will include order and item adjustments...
        List orderAdjustments = (List) context.get("orderAdjustments");
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

        // set the roles
        final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER",
                "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};

        for (int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
            // make sure the party is in the role before adding it...
            toBeStored.add(delegator.makeValue("PartyRole", 
            		UtilMisc.toMap("partyId", partyId, "roleTypeId", USER_ORDER_ROLE_TYPES[i])));
                                                
            toBeStored.add(delegator.makeValue("OrderRole", 
            		UtilMisc.toMap("orderId", orderId, "partyId", partyId, "roleTypeId", USER_ORDER_ROLE_TYPES[i])));                                                                        
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

        // set the order status
        toBeStored.add(delegator.makeValue("OrderStatus",
                UtilMisc.toMap("orderStatusId", delegator.getNextSeqId("OrderStatus").toString(),
                    "statusId", "ORDER_ORDERED", "orderId", orderId, "statusDatetime", UtilDateTime.nowTimestamp())));

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

                // START inventory reservation
                // decrement inventory available for each item, within the same transaction
                List resErrorMessages = new LinkedList();
                Iterator invDecItemIter = orderItems.iterator();

                while (invDecItemIter.hasNext()) {
                    GenericValue orderItem = (GenericValue) invDecItemIter.next();

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

                if (resErrorMessages.size() > 0) {
                    TransactionUtil.rollback(beganTransaction);
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE_LIST, resErrorMessages);
                    return result;
                }
                // END inventory reservation

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
        String removeOld = (String) context.get("removeOld");

        if (removeOld != null && removeOld.equalsIgnoreCase("true")) {
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
    
    /** Service to email a customer with order status */
    public static Map emailOrder(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();

        // This may be moved to a different place - email is a common task code this generic
        // implement me
        return result;
    }
    
    /** Service to email order notifications for pending actions */
    public static Map sendNotification(DispatchContext ctx, Map context) {
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
         
        String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", "localhost");
        String port = UtilProperties.getPropertyValue("url.properties", "port.http", "80");
        
        StringBuffer serverRoot = new StringBuffer();
        serverRoot.append("http://");
        serverRoot.append(server);
        if (!"80".equals(port)) {
            serverRoot.append(":");
            serverRoot.append(port);
        }
        serverRoot.append("/ordermgr/control/view/wfOrderNotify"); // maybe make this more dynamic
        
        Map parameters = new HashMap(context);
        parameters.putAll(orderHeader);
        parameters.putAll(workEffort);
        
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
                            
        // send the mail
        Map sendMailContext = new HashMap();
        sendMailContext.put("sendTo", emailList.toString());
        sendMailContext.put("sendFrom", "workflow@ofbiz.org"); // fixme
        sendMailContext.put("subject", "Workflow Notification");
        sendMailContext.put("bodyUrl", serverRoot.toString());
        sendMailContext.put("bodyUrlParameters", parameters);
        
        try {
            dispatcher.runAsync("sendMailFromUrl", sendMailContext);
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
        String stateCode = shippingAddress.getString("stateProvinceGeoId");

        // Setup the return lists.
        List orderAdjustments = new ArrayList();
        List itemAdjustments = new ArrayList();

        // Loop through the products; get the taxCategory; and lookup each in the cache.
        for (int i = 0; i < itemProductList.size(); i++) {
            GenericValue product = (GenericValue) itemProductList.get(i);
            Double itemAmount = (Double) itemAmountList.get(i);
            Double shippingAmount = (Double) itemShippingList.get(i);
            Double taxAmount = getTaxAmount(delegator, product, stateCode, itemAmount.doubleValue(), shippingAmount.doubleValue());

            itemAdjustments.add(UtilMisc.toList(delegator.makeValue("OrderAdjustment",
                        UtilMisc.toMap("amount", taxAmount, "orderAdjustmentTypeId", "SALES_TAX", "comments", stateCode))));
        }
        if (orderShippingAmount.doubleValue() > 0) {
            Double taxAmount = getTaxAmount(delegator, null, stateCode, 0.00, orderShippingAmount.doubleValue());

            orderAdjustments.add(delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap("amount", taxAmount, "orderAdjustmentTypeId", "SALES_TAX", "comments", stateCode)));
        }

        Map result = UtilMisc.toMap("orderAdjustments", orderAdjustments, "itemAdjustments", itemAdjustments);

        return result;

    }

    private static Double getTaxAmount(GenericDelegator delegator, GenericValue item, String stateCode, double itemAmount, double shippingAmount) {
        Map lookupMap = null;

        if (item != null)
            lookupMap = UtilMisc.toMap("stateProvinceGeoId", stateCode, "taxCategory", item.get("taxCategory"));
        else
            lookupMap = UtilMisc.toMap("stateProvinceGeoId", stateCode, "taxCategory", "_NA_");
        List orderList = UtilMisc.toList("-fromDate");

        try {
            List lookupList = delegator.findByAndCache("SimpleSalesTaxLookup", lookupMap, orderList);

            if (lookupList.size() == 0 && !"_NA_".equals((String) lookupMap.get("taxCategory"))) {
                lookupMap.put("taxCategory", "_NA_");
                lookupList = delegator.findByAndCache("SimpleSalesTaxLookup", lookupMap, orderList);
            }
            List filteredList = EntityUtil.filterByDate(lookupList);

            if (filteredList.size() == 0) {
                Debug.logWarning("SimpleTaxCalc: No State/TaxCategory pair found (with or without taxCat).");
                return new Double(0.00);
            }
            GenericValue taxLookup = (GenericValue) filteredList.get(0);
            double taxRate = taxLookup.get("salesTaxPercentage") != null ? taxLookup.getDouble("salesTaxPercentage").doubleValue() : 0;
            double taxable = 0.00;

            if (item != null && (item.get("taxable") == null || (item.get("taxable") != null && item.getBoolean("taxable").booleanValue())))
                taxable += itemAmount;
            if (taxLookup != null && (taxLookup.get("taxShipping") == null || (taxLookup.get("taxShipping") != null && taxLookup.getBoolean("taxShipping").booleanValue())))
                taxable += shippingAmount;
            return new Double(taxable * taxRate);
        } catch (GenericEntityException e) {
            Debug.logError(e);
            return new Double(0.00);
        }
    }
    
    
    
    
}
