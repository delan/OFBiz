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

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Inventory Services 
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class InventoryServices {
	
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

}
