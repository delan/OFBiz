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
package org.ofbiz.commonapp.product.inventory;

import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Inventory Services 
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
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
        
        if (inventoryItem == null)
            return ServiceUtil.returnError("Cannot locate inventory item.");

        String inventoryType = inventoryItem.getString("inventoryItemTypeId");
        if (inventoryType.equals("NON_SERIAL_INV_ITEM")) {
            Double atp = inventoryItem.getDouble("availableToPromise");
            Double qoh = inventoryItem.getDouble("quantityOnHand");
            
            if (atp == null)
                return ServiceUtil.returnError("The request transfer amount is not available.");
            if (qoh == null)
                qoh = atp;
            
            // first make sure we have enough to cover the request transfer amount
            if (xferQty.doubleValue() > atp.doubleValue())
                return ServiceUtil.returnError("The requested transfer amount is not available.");
                        
            /*
             * atp < qoh - split and save the qoh - atp
             * xferQty < atp - split and save atp - xferQty
             * atp < qoh && xferQty < atp - split and save qoh - atp + atp - xferQty
             */
             
            if (atp.doubleValue() < qoh.doubleValue()) { 
                // split and save the difference
                double diff = qoh.doubleValue() - atp.doubleValue();
                newItem = new GenericValue(inventoryItem);
                newItem.set("availableToPromise", new Double(0.0));
                newItem.set("quantityOnHand", new Double(diff));
            }
            
            if (xferQty.doubleValue() < atp.doubleValue()) {
                double diff = atp.doubleValue() - xferQty.doubleValue();
                if (newItem == null) {
                    newItem = new GenericValue(inventoryItem);
                    newItem.set("availableToPromise", new Double(diff));
                    newItem.set("quantityOnHand", new Double(diff));
                } else {
                    double newAtp = newItem.getDouble("availableToPromise").doubleValue();
                    double newQoh = newItem.getDouble("quantityOnHand").doubleValue();
                    newItem.set("availableToPromise", new Double(newAtp));
                    newItem.set("quantityOnHand", new Double(newQoh));
                }
            }
        } else if (inventoryType.equals("SERIALIZED_INV_ITEM")) {
            if (!inventoryItem.getString("statusId").equals("INV_AVAILABLE"))
                return ServiceUtil.returnError("Serialized inventory is not available for transfer.");                                      
        }       
                
        if (inventoryType.equals("NON_SERIAL_INV_ITEM")) {
            // set the transfered inventory item's atp to 0 and the qoh to the xferQty
            inventoryItem.set("availableToPromise", new Double(0.0));
            inventoryItem.set("quantityOnHand", xferQty);
        } else if (inventoryType.equals("SERIALIZED_INV_ITEM")) {
            // set the status to not_available
            inventoryItem.set("statusId", "INV_BEING_TRANSFERED");
        }
                                    
        try {
            inventoryItem.store();
            if (newItem != null) {          
                Long newSeqId = delegator.getNextSeqId("InventoryItem");
                if (newSeqId == null)  
                    return ServiceUtil.returnError("ERROR: Could not get next sequence id for InventoryItem, cannot create item.");
                
                newItem.set("inventoryItemId", newSeqId.toString());
                delegator.create(newItem);
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory store/create problem [" + e.getMessage() + "]");
        }                                                                                                   
                                        
        return ServiceUtil.returnSuccess();     
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
        
        if (inventoryTransfer == null || inventoryItem == null)
            return ServiceUtil.returnError("ERROR: Lookup of InventoryTransfer and/or InventoryItem failed!");
            
        String inventoryType = inventoryItem.getString("inventoryItemTypeId");
        
        // set the fields on the transfer record            
        if (inventoryTransfer.get("receiveDate") == null)
            inventoryTransfer.set("receiveDate", UtilDateTime.nowTimestamp());
            
        // set the fields on the item
        inventoryItem.set("facilityId", inventoryTransfer.get("facilityIdTo"));
        inventoryItem.set("containerId", inventoryTransfer.get("containerIdTo"));
        inventoryItem.set("locationSeqId", inventoryTransfer.get("locationSeqIdTo"));
        
        if (inventoryType.equals("NON_SERIAL_INV_ITEM")) 
            inventoryItem.set("availableToPromise", inventoryItem.get("quantityOnHand"));
        else if (inventoryType.equals("SERIALIZED_INV_ITEM"))
            inventoryItem.set("statusId", "INV_AVAILABLE");
        
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

        if (inventoryTransfer == null || inventoryItem == null)
            return ServiceUtil.returnError("ERROR: Lookup of InventoryTransfer and/or InventoryItem failed!");
            
        String inventoryType = inventoryItem.getString("inventoryItemTypeId");
        
        // re-set the fields on the item
        if (inventoryType.equals("NON_SERIAL_INV_ITEM"))
            inventoryItem.set("availableToPromise", inventoryItem.get("quantityOnHand"));
        else if (inventoryType.equals("SERIALIZED_INV_ITEM"))
                    inventoryItem.set("statusId", "INV_AVAILABLE");
                                
        // store the entity
        try {
            inventoryItem.store();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Inventory item store problem [" + e.getMessage() + "]");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map checkBackorderAvailability(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
               
        Map ordersToNotify = new HashMap();
        Map ordersToCancel = new HashMap();
        Map ordersPromised = new HashMap();
        
        // find all inventory items w/ a negative ATP
        List inventoryItems = null;
        try {
            List exprs = UtilMisc.toList(new EntityExpr("availableToPromise", EntityOperator.LESS_THAN, new Double(0)));
            inventoryItems = delegator.findByAnd("InventoryItem", exprs);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting inventory items", module);
            return ServiceUtil.returnError("Problem getting InventoryItem records");
        }
        
        if (inventoryItems == null) {
            Debug.logInfo("No items out of stock; no backorders to worry about", module);
            return ServiceUtil.returnSuccess();
        }
        
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
                reservations = inventoryItem.getRelated("OrderItemInventoryRes", null, UtilMisc.toList("-reservedDatetime"));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting related reservations", module);
                return ServiceUtil.returnError("Problem getting related reservations");
            }
            
            // available at the time of order
            double availableBeforeReserved = inventoryItem.getDouble("availableToPromise").doubleValue();
            
            // go through all the reservations in order
            Iterator ri = reservations.iterator();
            while (ri.hasNext()) {
                GenericValue reservation = (GenericValue) ri.next();
                String orderId = reservation.getString("orderId");
                String orderItemSeqId = reservation.getString("orderItemSeqId");
                Timestamp promisedDate = reservation.getTimestamp("promisedDatetime");
                
                // store the promised date for later use
                Map itemsPromised = (Map) ordersPromised.get(orderId);
                if (itemsPromised == null) {
                    itemsPromised = new HashMap();                    
                }
                itemsPromised.put(orderItemSeqId, promisedDate);
                ordersPromised.put(orderId, itemsPromised);
                               
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
                
                // create a modified promise date (promise date - 1 day)
                Calendar pCal = Calendar.getInstance();
                pCal.setTimeInMillis(promisedDate.getTime());
                pCal.add(Calendar.DAY_OF_YEAR, 1);
                Timestamp modifiedPromisedDate = new Timestamp(pCal.getTimeInMillis());
                             
                // check the promised date vs the next ship date
                if (nextShipDate == null || nextShipDate.after(promisedDate)) {
                    if (nextShipDate == null && modifiedPromisedDate.before(UtilDateTime.nowTimestamp())) {
                        // do nothing; we are okay to assume it will be shipped on time
                    } else {                    
                        // we cannot ship by the promised date; need to notify the customer
                        Map notifyItems = (Map) ordersToNotify.get(orderId);
                        if (notifyItems == null) {
                            notifyItems = new HashMap();
                        }
                        notifyItems.put(orderItemSeqId, nextShipDate);
                        ordersToNotify.put(orderId, notifyItems);
                        
                        // need to know if nextShipDate is more then 30 days after promised
                        Calendar sCal = Calendar.getInstance();
                        sCal.setTimeInMillis(promisedDate.getTime());
                        sCal.add(Calendar.DAY_OF_YEAR, 30);
                        Timestamp farPastPromised = new Timestamp(sCal.getTimeInMillis());
                        
                        if (nextShipDate == null || nextShipDate.after(farPastPromised)) {
                            // queue the item to be cancelled
                            Map cancelItems = (Map) ordersToCancel.get(orderId);
                            if (cancelItems == null) {
                                cancelItems = new HashMap();
                            }
                            cancelItems.put(orderItemSeqId, farPastPromised);
                            ordersToCancel.put(orderId, cancelItems);
                        }
                    }                    
                }
                                
                // subtract our qty from reserved to get the next value
                availableBeforeReserved -= reservation.getDouble("quantity").doubleValue();
            }
        }
                                
        // send off a notification for each order
        Set orderNotifySet = ordersToNotify.keySet();
        Iterator orderNotifyIter = orderNotifySet.iterator();
        while (orderNotifyIter.hasNext()) {                       
            String orderId = (String) orderNotifyIter.next();
            Map backOrderedItems = (Map) ordersToNotify.get(orderId);
            Map cancelDateItems = (Map) ordersToCancel.get(orderId);
            Map itemsPromised = (Map) ordersPromised.get(orderId);
            
            Map serviceContext = new HashMap();
            serviceContext.put("orderId", orderId);
            serviceContext.put("extraFields", UtilMisc.toMap("itemsPromised", itemsPromised, 
                    "backOrderedItems", backOrderedItems, "cancelDateItems", cancelDateItems));
                        
            try {
                dispatcher.runAsync("sendOrderNotification", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems sending off the notification", module);
                continue;
            }
        }
        
        // set the cancel date on the items
        Set orderCancelSet = ordersToCancel.keySet();
        Iterator orderCancelIter = orderCancelSet.iterator();
        while (orderCancelIter.hasNext()) {
            String orderId = (String) orderCancelIter.next();
            Map cancelItems = (Map) ordersToCancel.get(orderId);
            if (cancelItems != null) {            
                GenericValue orderShipPref = null;
                List orderItems = null;                      
                try {
                    orderShipPref = delegator.findByPrimaryKey("OrderShipmentPreference", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", "_NA_"));
                    orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));                
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get order shipment preference or items", module);
                }
                
                List toBeStored = new ArrayList();                
                Set cancelItemKeys = cancelItems.keySet();      
                if (orderShipPref == null || orderShipPref.getString("maySplit").equals("N")) {
                    // not splitting so cancel entire order
                    // use just the first date for all
                    Timestamp cancelDate = (Timestamp) cancelItems.get(cancelItemKeys.iterator().next());
                    Iterator oi = orderItems.iterator();
                    while (oi.hasNext()) {
                        GenericValue item = (GenericValue) oi.next();
                        item.set("autoCancelDate", cancelDate);
                        toBeStored.add(item);
                    }
                    
                } else {
                    // only cancel specific items
                    Iterator oi = orderItems.iterator();
                    while (oi.hasNext()) {
                        GenericValue item = (GenericValue) oi.next();
                        Timestamp cancelDate = (Timestamp) cancelItems.get(item.getString("orderItemSeqId"));
                        if (cancelDate != null) {
                            item.set("autoCancelDate", cancelDate);
                            toBeStored.add(item);
                        }
                    }                     
                }
                
                if (toBeStored.size() > 0) {
                    try {
                        delegator.storeAll(toBeStored);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem storing autoCancelDate on items", module);
                    }
                }
            }
        }
                                           
        return ServiceUtil.returnSuccess();
    }

}
