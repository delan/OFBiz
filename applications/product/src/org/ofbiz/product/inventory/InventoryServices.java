/*
 * $Id$
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
package org.ofbiz.product.inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Inventory Services 
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 * @version    $Rev$
 * @since      2.0
 */
public class InventoryServices {
    
    public final static String module = InventoryServices.class.getName();
    
    public static Map prepareInventoryTransfer(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String inventoryItemId = (String) context.get("inventoryItemId");
        Double xferQty = (Double) context.get("xferQty");   
        GenericValue inventoryItem = null;
        GenericValue newItem = null;        
        
        try {           
            inventoryItem = delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory item lookup problem [" + e.getMessage() + "]");
        }
        
        if (inventoryItem == null) {
            return ServiceUtil.returnError("Cannot locate inventory item.");
        }

        try {
            Map results = ServiceUtil.returnSuccess();
            
            String inventoryType = inventoryItem.getString("inventoryItemTypeId");
            if (inventoryType.equals("NON_SERIAL_INV_ITEM")) {
                Double atp = inventoryItem.getDouble("availableToPromiseTotal");
                Double qoh = inventoryItem.getDouble("quantityOnHandTotal");
                
                if (atp == null) {
                    return ServiceUtil.returnError("The request transfer amount is not available, there is no available to promise on the Inventory Item with ID " + inventoryItem.getString("inventoryItemId"));
                }
                if (qoh == null) {
                    qoh = atp;
                }
                
                // first make sure we have enough to cover the request transfer amount
                if (xferQty.doubleValue() > atp.doubleValue()) {
                    return ServiceUtil.returnError("The request transfer amount is not available, the available to promise [" + atp + "] is not sufficient for the desired transfer quantity [" + xferQty + "] on the Inventory Item with ID " + inventoryItem.getString("inventoryItemId"));
                }
                            
                /*
                 * atp < qoh - split and save the qoh - atp
                 * xferQty < atp - split and save atp - xferQty
                 * atp < qoh && xferQty < atp - split and save qoh - atp + atp - xferQty
                 */
    
                // at this point we have already made sure that the xferQty is less than or equals to the atp, so if less that just create a new inventory record for the quantity to be moved
                // NOTE: atp should always be <= qoh, so if xfer < atp, then xfer < qoh, so no need to check/handle that
                // however, if atp < qoh && atp == xferQty, then we still need to split; oh, but no need to check atp == xferQty in the second part because if it isn't greater and isn't less, then it is equal
                if (xferQty.doubleValue() < atp.doubleValue() || atp.doubleValue() < qoh.doubleValue()) {
                    Double negXferQty = new Double(-xferQty.doubleValue());
                    newItem = new GenericValue(inventoryItem);
                    String newSeqId = null;
                    try {
                        newSeqId = delegator.getNextSeqId("InventoryItem");
                    } catch (IllegalArgumentException e) {
                        return ServiceUtil.returnError("ERROR: Could not get next sequence id for InventoryItem, cannot create item.");
                    }
                    
                    newItem.set("inventoryItemId", newSeqId);
                    newItem.create();
                    results.put("inventoryItemId", newItem.get("inventoryItemId"));
    
                    // TODO: how do we get this here: "inventoryTransferId", inventoryTransferId
                    Map createNewDetailMap = UtilMisc.toMap("availableToPromiseDiff", xferQty, "quantityOnHandDiff", xferQty,
                            "inventoryItemId", newItem.get("inventoryItemId"));
                    Map createUpdateDetailMap = UtilMisc.toMap("availableToPromiseDiff", negXferQty, "quantityOnHandDiff", negXferQty,
                            "inventoryItemId", inventoryItem.get("inventoryItemId"));
                    
                    try {
                        Map resultNew = dctx.getDispatcher().runSync("createInventoryItemDetail", createNewDetailMap);
                        if (ServiceUtil.isError(resultNew)) {
                            return ServiceUtil.returnError("Inventory Item Detail create problem in prepare inventory transfer", null, null, resultNew);
                        }
                        Map resultUpdate = dctx.getDispatcher().runSync("createInventoryItemDetail", createUpdateDetailMap);
                        if (ServiceUtil.isError(resultNew)) {
                            return ServiceUtil.returnError("Inventory Item Detail create problem in prepare inventory transfer", null, null, resultUpdate);
                        }
                    } catch (GenericServiceException e1) {
                        return ServiceUtil.returnError("Inventory Item Detail create problem in prepare inventory transfer: [" + e1.getMessage() + "]");
                    }
                } else {
                    results.put("inventoryItemId", inventoryItem.get("inventoryItemId"));
                }
            } else if (inventoryType.equals("SERIALIZED_INV_ITEM")) {
                if (!"INV_AVAILABLE".equals(inventoryItem.getString("statusId"))) {
                    return ServiceUtil.returnError("Serialized inventory is not available for transfer.");
                }
            }       
                    
            // setup values so that no one will grab the inventory during the move
            // if newItem is not null, it is the item to be moved, otherwise the original inventoryItem is the one to be moved
            if (inventoryType.equals("NON_SERIAL_INV_ITEM")) {
                // set the transfered inventory item's atp to 0 and the qoh to the xferQty; at this point atp and qoh will always be the same, so we can safely zero the atp for now
                GenericValue inventoryItemToClear = newItem == null ? inventoryItem : newItem;
                double atp = inventoryItemToClear.get("availableToPromiseTotal") == null ? 0 : inventoryItemToClear.getDouble("availableToPromiseTotal").doubleValue();
                if (atp != 0) { 
                    Map createDetailMap = UtilMisc.toMap("availableToPromiseDiff", new Double(-atp), 
                            "inventoryItemId", inventoryItemToClear.get("inventoryItemId"));
                    try {
                        Map result = dctx.getDispatcher().runSync("createInventoryItemDetail", createDetailMap);
                        if (ServiceUtil.isError(result)) {
                            return ServiceUtil.returnError("Inventory Item Detail create problem in complete inventory transfer", null, null, result);
                        }
                    } catch (GenericServiceException e1) {
                        return ServiceUtil.returnError("Inventory Item Detail create problem in complete inventory transfer: [" + e1.getMessage() + "]");
                    }
                }
            } else if (inventoryType.equals("SERIALIZED_INV_ITEM")) {
                // set the status to avoid re-moving or something
            	if (newItem != null) {
            		newItem.set("statusId", "INV_BEING_TRANSFERED");
            	} else {
            		inventoryItem.set("statusId", "INV_BEING_TRANSFERED");
            	}
            }
                                    
            inventoryItem.store();
            return results;     
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory store/create problem [" + e.getMessage() + "]");
        }                                                                                                   
    }
    
    public static Map completeInventoryTransfer(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String inventoryTransferId = (String) context.get("inventoryTransferId");
        GenericValue inventoryTransfer = null;
        GenericValue inventoryItem = null;
        
        try {
            inventoryTransfer = delegator.findByPrimaryKey("InventoryTransfer", 
                    UtilMisc.toMap("inventoryTransferId", inventoryTransferId));
            inventoryItem = inventoryTransfer.getRelatedOne("InventoryItem");  
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory Item/Transfer lookup problem [" + e.getMessage() + "]");
        }
        
        if (inventoryTransfer == null || inventoryItem == null) {
            return ServiceUtil.returnError("ERROR: Lookup of InventoryTransfer and/or InventoryItem failed!");
        }
            
        String inventoryType = inventoryItem.getString("inventoryItemTypeId");
        
        // set the fields on the transfer record            
        if (inventoryTransfer.get("receiveDate") == null) {
            inventoryTransfer.set("receiveDate", UtilDateTime.nowTimestamp());
        }
            
        // set the fields on the item
        inventoryItem.set("facilityId", inventoryTransfer.get("facilityIdTo"));
        inventoryItem.set("containerId", inventoryTransfer.get("containerIdTo"));
        inventoryItem.set("locationSeqId", inventoryTransfer.get("locationSeqIdTo"));
        
        if (inventoryType.equals("NON_SERIAL_INV_ITEM")) { 
            // add an adjusting InventoryItemDetail so set ATP back to QOH: ATP = ATP + (QOH - ATP), diff = QOH - ATP
            double atp = inventoryItem.get("availableToPromiseTotal") == null ? 0 : inventoryItem.getDouble("availableToPromiseTotal").doubleValue();
            double qoh = inventoryItem.get("quantityOnHandTotal") == null ? 0 : inventoryItem.getDouble("quantityOnHandTotal").doubleValue();
            Map createDetailMap = UtilMisc.toMap("availableToPromiseDiff", new Double(qoh - atp), 
                    "inventoryTransferId", inventoryTransferId, "inventoryItemId", inventoryItem.get("inventoryItemId"));
            try {
                Map result = dctx.getDispatcher().runSync("createInventoryItemDetail", createDetailMap);
                if (ServiceUtil.isError(result)) {
                    return ServiceUtil.returnError("Inventory Item Detail create problem in complete inventory transfer", null, null, result);
                }
            } catch (GenericServiceException e1) {
                return ServiceUtil.returnError("Inventory Item Detail create problem in complete inventory transfer: [" + e1.getMessage() + "]");
            }
        } else if (inventoryType.equals("SERIALIZED_INV_ITEM")) {
            inventoryItem.set("statusId", "INV_AVAILABLE");
        }
        
        // store the entities
        try {
            inventoryTransfer.store();
            inventoryItem.store();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory store problem [" + e.getMessage() + "]");
        }
         
        return ServiceUtil.returnSuccess();
    }    
    
    public static Map cancelInventoryTransfer(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String inventoryTransferId = (String) context.get("inventoryTransferId");
        GenericValue inventoryTransfer = null;
        GenericValue inventoryItem = null;

        try {
            inventoryTransfer = delegator.findByPrimaryKey("InventoryTransfer",
                    UtilMisc.toMap("inventoryTransferId", inventoryTransferId));
            inventoryItem = inventoryTransfer.getRelatedOne("InventoryItem");
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory Item/Transfer lookup problem [" + e.getMessage() + "]");
        }

        if (inventoryTransfer == null || inventoryItem == null) {
            return ServiceUtil.returnError("ERROR: Lookup of InventoryTransfer and/or InventoryItem failed!");
        }
            
        String inventoryType = inventoryItem.getString("inventoryItemTypeId");
        
        // re-set the fields on the item
        if (inventoryType.equals("NON_SERIAL_INV_ITEM")) {
            // add an adjusting InventoryItemDetail so set ATP back to QOH: ATP = ATP + (QOH - ATP), diff = QOH - ATP
            double atp = inventoryItem.get("availableToPromiseTotal") == null ? 0 : inventoryItem.getDouble("availableToPromiseTotal").doubleValue();
            double qoh = inventoryItem.get("quantityOnHandTotal") == null ? 0 : inventoryItem.getDouble("quantityOnHandTotal").doubleValue();
            Map createDetailMap = UtilMisc.toMap("availableToPromiseDiff", new Double(qoh - atp), 
                    "inventoryTransferId", inventoryTransferId, "inventoryItemId", inventoryItem.get("inventoryItemId"));
            try {
                Map result = dctx.getDispatcher().runSync("createInventoryItemDetail", createDetailMap);
                if (ServiceUtil.isError(result)) {
                    return ServiceUtil.returnError("Inventory Item Detail create problem in cancel inventory transfer", null, null, result);
                }
            } catch (GenericServiceException e1) {
                return ServiceUtil.returnError("Inventory Item Detail create problem in cancel inventory transfer: [" + e1.getMessage() + "]");
            }
        } else if (inventoryType.equals("SERIALIZED_INV_ITEM")) {
            inventoryItem.set("statusId", "INV_AVAILABLE");
            // store the entity
            try {
                inventoryItem.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("Inventory item store problem in cancel inventory transfer: [" + e.getMessage() + "]");
            }
        }
                                
        return ServiceUtil.returnSuccess();
    }

    /** In spite of the generic name this does the very specific task of checking availability of all back-ordered items and sends notices, etc */
    public static Map checkInventoryAvailability(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");        
               
        Map ordersToUpdate = new HashMap();
        Map ordersToCancel = new HashMap();       
        
        // find all inventory items w/ a negative ATP
        List inventoryItems = null;
        try {
            List exprs = UtilMisc.toList(new EntityExpr("availableToPromiseTotal", EntityOperator.LESS_THAN, new Double(0)));
            inventoryItems = delegator.findByAnd("InventoryItem", exprs);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting inventory items", module);
            return ServiceUtil.returnError("Problem getting InventoryItem records");
        }
                        
        if (inventoryItems == null) {
            Debug.logInfo("No items out of stock; no backorders to worry about", module);
            return ServiceUtil.returnSuccess();
        }
        
        Debug.log("OOS Inventory Items: " + inventoryItems.size(), module);
        
        Iterator itemsIter = inventoryItems.iterator();
        while (itemsIter.hasNext()) {
            GenericValue inventoryItem = (GenericValue) itemsIter.next();
            
            // get the incomming shipment information for the item
            List shipmentAndItems = null;
            try {
                List exprs = new ArrayList();
                exprs.add(new EntityExpr("productId", EntityOperator.EQUALS, inventoryItem.get("productId")));
                exprs.add(new EntityExpr("destinationFacilityId", EntityOperator.EQUALS, inventoryItem.get("facilityId")));
                exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_DELIVERED"));
                exprs.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
                shipmentAndItems = delegator.findByAnd("ShipmentAndItem", exprs, UtilMisc.toList("estimatedArrivalDate"));  
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting ShipmentAndItem records", module);
                return ServiceUtil.returnError("Problem getting ShipmentAndItem records");
            }
            
            // get the reservations in order of newest first
            List reservations = null;
            try {
                reservations = inventoryItem.getRelated("OrderItemShipGrpInvRes", null, UtilMisc.toList("-reservedDatetime"));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting related reservations", module);
                return ServiceUtil.returnError("Problem getting related reservations");
            }
            
            if (reservations == null) {
                Debug.logWarning("No outstanding reservations for this inventory item, why is it negative then?", module);
                continue;
            }
            
            Debug.log("Reservations for item: " + reservations.size(), module);
            
            // available at the time of order
            double availableBeforeReserved = inventoryItem.getDouble("availableToPromiseTotal").doubleValue();
            
            // go through all the reservations in order
            Iterator ri = reservations.iterator();
            while (ri.hasNext()) {
                GenericValue reservation = (GenericValue) ri.next();
                String orderId = reservation.getString("orderId");
                String orderItemSeqId = reservation.getString("orderItemSeqId");
                Timestamp promisedDate = reservation.getTimestamp("promisedDatetime");
                Timestamp currentPromiseDate = reservation.getTimestamp("currentPromisedDate");
                Timestamp actualPromiseDate = currentPromiseDate;
                if (actualPromiseDate == null) {
                    actualPromiseDate = promisedDate;
                }
                
                Debug.log("Promised Date: " + actualPromiseDate, module);
                                                               
                // find the next possible ship date
                Timestamp nextShipDate = null;
                double availableAtTime = 0.00;
                Iterator si = shipmentAndItems.iterator();
                while (si.hasNext()) {
                    GenericValue shipmentItem = (GenericValue) si.next();
                    availableAtTime += shipmentItem.getDouble("quantity").doubleValue();
                    if (availableAtTime >= availableBeforeReserved) {
                        nextShipDate = shipmentItem.getTimestamp("estimatedArrivalDate");
                        break;
                    }
                }
                
                Debug.log("Next Ship Date: " + nextShipDate, module);
                                                
                // create a modified promise date (promise date - 1 day)
                Calendar pCal = Calendar.getInstance();
                pCal.setTimeInMillis(actualPromiseDate.getTime());
                pCal.add(Calendar.DAY_OF_YEAR, -1);
                Timestamp modifiedPromisedDate = new Timestamp(pCal.getTimeInMillis());
                Timestamp now = UtilDateTime.nowTimestamp();
                
                Debug.log("Promised Date + 1: " + modifiedPromisedDate, module);
                Debug.log("Now: " + now, module);
                             
                // check the promised date vs the next ship date
                if (nextShipDate == null || nextShipDate.after(actualPromiseDate)) {
                    if (nextShipDate == null && modifiedPromisedDate.after(now)) {
                        // do nothing; we are okay to assume it will be shipped on time
                        Debug.log("No ship date known yet, but promised date hasn't approached, assuming it will be here on time", module);
                    } else {                    
                        // we cannot ship by the promised date; need to notify the customer
                        Debug.log("We won't ship on time, getting notification info", module);
                        Map notifyItems = (Map) ordersToUpdate.get(orderId);
                        if (notifyItems == null) {
                            notifyItems = new HashMap();
                        }
                        notifyItems.put(orderItemSeqId, nextShipDate);
                        ordersToUpdate.put(orderId, notifyItems);
                        
                        // need to know if nextShipDate is more then 30 days after promised
                        Calendar sCal = Calendar.getInstance();
                        sCal.setTimeInMillis(actualPromiseDate.getTime());
                        sCal.add(Calendar.DAY_OF_YEAR, 30);
                        Timestamp farPastPromised = new Timestamp(sCal.getTimeInMillis());
                        
                        // check to see if this is >30 days or second run, if so flag to cancel
                        boolean needToCancel = false;                       
                        if (nextShipDate == null || nextShipDate.after(farPastPromised)) {
                            // we cannot ship until >30 days after promised; using cancel rule
                            Debug.log("Ship date is >30 past the promised date", module);
                            needToCancel = true;
                        }
                        if (currentPromiseDate != null && actualPromiseDate.equals(currentPromiseDate)) {
                            // this is the second notification; using cancel rule
                            needToCancel = true;
                        }
                        
                        // add the info to the cancel map if we need to schedule a cancel
                        if (needToCancel) {                        
                            // queue the item to be cancelled
                            Debug.log("Flagging the item to auto-cancel", module);
                            Map cancelItems = (Map) ordersToCancel.get(orderId);
                            if (cancelItems == null) {
                                cancelItems = new HashMap();
                            }
                            cancelItems.put(orderItemSeqId, farPastPromised);
                            ordersToCancel.put(orderId, cancelItems);
                        }
                        
                        // store the updated promiseDate as the nextShipDate
                        try {
                            reservation.set("currentPromisedDate", nextShipDate);
                            reservation.store();
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Problem storing reservation : " + reservation, module);
                        }
                    }                    
                }
                                
                // subtract our qty from reserved to get the next value
                availableBeforeReserved -= reservation.getDouble("quantity").doubleValue();
            }
        }
                                        
        // all items to cancel will also be in the notify list so start with that
        List ordersToNotify = new ArrayList();
        Set orderSet = ordersToUpdate.keySet();
        Iterator orderIter = orderSet.iterator();
        while (orderIter.hasNext()) {
            String orderId = (String) orderIter.next();
            Map backOrderedItems = (Map) ordersToUpdate.get(orderId);
            Map cancelItems = (Map) ordersToCancel.get(orderId);
            
            // TODO: change to get all OrderItemShipGroup records for the order, then get items per ship group
            
            GenericValue orderShipPref = null;
            List orderItems = null;
            try {
                orderShipPref = delegator.findByPrimaryKey("OrderShipmentPreference", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", "_NA_"));
                orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));                
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get order shipment preference or items", module);
            }
            
            // check the split pref
            boolean maySplit = false;
            if (orderShipPref != null && orderShipPref.get("maySplit") != null) {
                maySplit = orderShipPref.getBoolean("maySplit").booleanValue();
            }
            
            // figure out if we must cancel all items
            boolean cancelAll = false;
            Timestamp cancelAllTime = null;            
            if (!maySplit && cancelItems != null) {
                cancelAll = true;                
                Set cancelSet = cancelItems.keySet();
                cancelAllTime = (Timestamp) cancelItems.get(cancelSet.iterator().next());
            }
            
            // if there are none to cancel just create an empty map
            if (cancelItems == null) {
                cancelItems = new HashMap();
            }
            
            if (orderItems != null) {            
                List toBeStored = new ArrayList();
                Iterator orderItemsIter = orderItems.iterator();
                while (orderItemsIter.hasNext()) {
                    GenericValue orderItem = (GenericValue) orderItemsIter.next();
                    String orderItemSeqId = orderItem.getString("orderItemSeqId");
                    Timestamp shipDate = (Timestamp) backOrderedItems.get(orderItemSeqId);
                    Timestamp cancelDate = (Timestamp) cancelItems.get(orderItemSeqId);
                    Timestamp currentCancelDate = (Timestamp) orderItem.getTimestamp("autoCancelDate");
                    
                    if (backOrderedItems.containsKey(orderItemSeqId)) {
                        orderItem.set("estimatedShipDate", shipDate);
                    
                        if (currentCancelDate == null) {                        
                            if (cancelAll || cancelDate != null) {
                                if (orderItem.get("dontCancelSetUserLogin") == null && orderItem.get("dontCancelSetDate") == null) {                            
                                    if (cancelAllTime != null) {
                                        orderItem.set("autoCancelDate", cancelAllTime);
                                    } else {
                                        orderItem.set("autoCancelDate", cancelDate);
                                    }
                                }
                            }
                            // only notify orders which have not already sent the final notice
                            ordersToNotify.add(orderId);                        
                        }
                        toBeStored.add(orderItem);                        
                    }
                }
                if (toBeStored.size() > 0) {
                    try {
                        delegator.storeAll(toBeStored);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem storing order items", module);
                    }
                }
            }
        }
        
        // send off a notification for each order        
        Iterator orderNotifyIter = ordersToNotify.iterator();
        while (orderNotifyIter.hasNext()) {                       
            String orderId = (String) orderNotifyIter.next();                                  
                       
            try {
                dispatcher.runAsync("sendOrderBackorderNotification", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems sending off the notification", module);
                continue;
            }
        }        
                                                          
        return ServiceUtil.returnSuccess();
    }    
}
