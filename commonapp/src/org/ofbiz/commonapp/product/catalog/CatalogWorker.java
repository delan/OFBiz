/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.commonapp.product.catalog;

import java.util.*;
import java.net.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

/**
 * CatalogWorker - Worker class for catalog related functionality
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    May 29, 2002
 */
public class CatalogWorker {

    /** check inventory availability for the given catalog, product, quantity, etc */
    public static boolean isCatalogInventoryAvailable(String prodCatalogId, String productId, double quantity, GenericDelegator delegator, LocalDispatcher dispatcher) {
        GenericValue prodCatalog = null;
        try {
            prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId);
        }
        
        if (prodCatalog == null) {
            Debug.logWarning("No catalog found with id " + prodCatalogId + ", returning false for inventory available check");
            return false;
        }
        
        //if prodCatalog is set to not check inventory, or if inventory is not required for purchase, break here
        if ("N".equals(prodCatalog.getString("checkInventory")) || !"Y".equals(prodCatalog.getString("requireInventory"))) {
            //note: if not set, defaults to yes, check inventory
            Debug.logInfo("Catalog with id " + prodCatalogId + ", is set to NOT check inventory, returning true for inventory available check");
            return true;
        }
        
        if ("Y".equals(prodCatalog.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = prodCatalog.getString("inventoryFacilityId");
            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("Catalog with id " + prodCatalogId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, return false for inventory check");
                return false;
            }
            
            Double availableToPromise = null;
            try {
                Map result = dispatcher.runSync("getInventoryAvailableByFacility",
                UtilMisc.toMap("productId", productId, "facilityId", inventoryFacilityId));
                availableToPromise = (Double) result.get("availableToPromise");
                
                if (availableToPromise == null) {
                    Debug.logWarning("The getInventoryAvailableByFacility service returned a null availableToPromise, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE));
                    return false;
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking getInventoryAvailableByFacility service in isCatalogInventoryAvailable");
                return false;
            }
            
            //whew, finally here: now check to see if we got enough back...
            if (availableToPromise.doubleValue() >= quantity) {
                Debug.logInfo("Inventory IS available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise);
                return true;
            } else {
                Debug.logInfo("Returning false because there is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise);
                return false;
            }
            
        } else {
            Debug.logWarning("Catalog with id " + prodCatalogId + " uses multiple inventory facilities, which is not yet implemented, return false for inventory check");
            return false;
            
            //TODO: check multiple inventory locations
            
            //must entire quantity be available in one location?
            
            //loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }
    
    /** tries to reserve the specified quantity, if fails returns quantity that it could not reserve or zero if there was an error, otherwise returns null */
    public static Double reserveCatalogInventory(String prodCatalogId, String productId, Double quantity,
            String orderId, String orderItemSeqId, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher) {
        
        GenericValue prodCatalog = null;
        try {
            prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId);
        }
        if (prodCatalog == null) {
            Debug.logWarning("No catalog found with id " + prodCatalogId + ", not reserving inventory");
            return new Double(0.0);
        }
        
        //if prodCatalog is set to not reserve inventory, break here
        if ("N".equals(prodCatalog.getString("reserveInventory"))) {
            //note: if not set, defaults to yes, reserve inventory
            Debug.logInfo("Catalog with id " + prodCatalogId + ", is set to NOT reserve inventory, not reserving inventory");
            return null;
        }
        
        if ("Y".equals(prodCatalog.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = prodCatalog.getString("inventoryFacilityId");
            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("Catalog with id " + prodCatalogId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, not reserving inventory");
                return new Double(0.0);
            }
            
            Double quantityNotReserved = null;
            try {
                Map serviceContext = new HashMap();
                serviceContext.put("productId", productId);
                serviceContext.put("facilityId", inventoryFacilityId);
                serviceContext.put("orderId", orderId);
                serviceContext.put("orderItemSeqId", orderItemSeqId);
                serviceContext.put("quantity", quantity);
                serviceContext.put("requireInventory", prodCatalog.get("requireInventory"));
                serviceContext.put("userLogin", userLogin);
                
                Map result = dispatcher.runSync("reserveProductInventoryByFacility", serviceContext);
                quantityNotReserved = (Double) result.get("quantityNotReserved");
                
                if (quantityNotReserved == null) {
                    Debug.logWarning("The getInventoryAvailableByFacility service returned a null availableToPromise, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE));
                    if (!"Y".equals(prodCatalog.getString("requireInventory"))) {
                        return null;
                    } else {
                        return new Double(0.0);
                    }
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking reserveProductInventoryByFacility service");
                if (!"Y".equals(prodCatalog.getString("requireInventory"))) {
                    return null;
                } else {
                    return new Double(0.0);
                }
            }
            
            //whew, finally here: now check to see if we were able to reserve...
            if (quantityNotReserved.doubleValue() == 0) {
                Debug.logInfo("Inventory IS reserved in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity was " + quantity);
                return null;
            } else {
                Debug.logInfo("There is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", amount could not reserve is " + quantityNotReserved);
                return quantityNotReserved;
            }
            
        } else {
            Debug.logError("Catalog with id " + prodCatalogId + " uses multiple inventory facilities, which is not yet implemented, not reserving inventory");
            return new Double(0.0);
            
            //TODO: check multiple inventory locations
            
            //must entire quantity be available in one location?
            
            //loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }
}
