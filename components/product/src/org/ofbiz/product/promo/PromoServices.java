/*
 * $Id: PromoServices.java,v 1.1 2003/12/23 00:20:01 jonesde Exp $
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
package org.ofbiz.product.promo;

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
 * @author     Nathan De Graw
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class PromoServices {
    
    public final static String module = PromoServices.class.getName();
    
    public static Map createProductPromoCodeSet(DispatchContext dctx, Map context) {
		GenericDelegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		//String productPromoId = (String) context.get("productPromoId");
		Long quantity = (Long) context.get("quantity");
		//Long useLimitPerCode = (Long) context.get("useLimitPerCode");
		//Long useLimitPerCustomer = (Long) context.get("useLimitPerCustomer");
		//GenericValue promoItem = null;
		//GenericValue newItem = null;
		
		StringBuffer bankOfNumbers = new StringBuffer();
		for (long i=0; i < quantity.longValue(); i++)
		{
			Map createProductPromoCodeMap = null;
			try
			{
				createProductPromoCodeMap = dispatcher.runSync("createProductPromoCode", dctx.makeValidContext("createProductPromoCode", "IN", context));
			}
			catch (GenericServiceException err)
			{
				return ServiceUtil.returnError("Could not create a bank of promo codes", null, null, createProductPromoCodeMap); 
			}
			if (ServiceUtil.isError(createProductPromoCodeMap)) {
				// what to do here? try again?
				return ServiceUtil.returnError("Could not create a bank of promo codes", null, null, createProductPromoCodeMap); 
			}
			bankOfNumbers.append( (String) createProductPromoCodeMap.get("productPromoCodeId") );
			bankOfNumbers.append("<br>");
		}
		
		return ServiceUtil.returnSuccess(bankOfNumbers.toString());
    }
    
    
    
  /*  
                
        
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
             /*
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
    
    public static Map checkInventoryAvailability(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");        
               
        Map ordersToUpdate = new HashMap();
        Map ordersToCancel = new HashMap();       
        
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
                reservations = inventoryItem.getRelated("OrderItemInventoryRes", null, UtilMisc.toList("-reservedDatetime"));
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
            double availableBeforeReserved = inventoryItem.getDouble("availableToPromise").doubleValue();
            
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
    
    */    
}
