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
package org.ofbiz.commonapp.product.store;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.commonapp.order.shoppingcart.ShoppingCart;
import org.ofbiz.commonapp.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.commonapp.product.catalog.CatalogWorker;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilValidate;

/**
 * ProductStoreWorker - Worker class for store related functionality
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ProductStoreWorker {
    
    public static final String module = ProductStoreWorker.class.getName();

    public static GenericValue getProductStore(String productStoreId, GenericDelegator delegator) {
        GenericValue productStore = null;
        try {
            productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStore entity", module);
        }
        return productStore;
    }
            
    public static GenericValue getProductStore(ServletRequest request) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        return ProductStoreWorker.getProductStore(productStoreId, delegator);               
    }
    
    public static String getProductStoreId(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ShoppingCart cart = (ShoppingCart) httpRequest.getSession().getAttribute("shoppingCart");
        if (cart != null && cart.getProductStoreId() != null) {
            return cart.getProductStoreId();
        } else {
            GenericValue webSite = CatalogWorker.getWebSite(request);
            if (webSite != null) {
                return webSite.getString("productStoreId");
            }
        }
        return null;        
    }
    
    public static boolean isStoreInventoryRequired(String productStoreId, String productId, GenericDelegator delegator) {
        GenericValue product = null;

        if (productId != null) {
            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up product with id " + productId + ", will check the ProdCatalog for inventory required", module);
            }
        }

        return isStoreInventoryRequired(productStoreId, product, delegator);
    }

    public static boolean isStoreInventoryRequired(ShoppingCartItem item) {
        return isStoreInventoryRequired(item.getProdCatalogId(), item.getProduct(), item.getDelegator());
    }
       
    public static boolean isStoreInventoryRequired(String productStoreId, GenericValue product, GenericDelegator delegator) {
        // look at the product first since it over-rides the ProductStore setting; if empty or null use the ProductStore setting
        
        if (product != null && UtilValidate.isNotEmpty(product.getString("requireInventory"))) {
            if ("Y".equals(product.getString("requireInventory"))) {
                return true;
            } else if ("N".equals(product.getString("requireInventory"))) {
                return false;
            }
        }
        // otherwise, check the store...

        GenericValue productStore = getProductStore(productStoreId, delegator);

        if (productStore == null) {
            Debug.logWarning("ProductStore not found with id " + productStoreId + ", returning false for inventory required check", module);
            return false;
        }

        // default to false, so if anything but Y, return false
        return "Y".equals(productStore.getString("requireInventory"));        
    }
    
    public static boolean isStoreInventoryRequired(ServletRequest request, GenericValue product) {
        GenericValue productStore = getProductStore(request);        

        if (productStore == null) {
            Debug.logWarning("No ProductStore found, return false for inventory check", module);
            return false;
        }
        
        String productStoreId = productStore.getString("productStoreId");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        return isStoreInventoryRequired(productStore.getString("productStoreId"), product, delegator);
    }    

    public static boolean isStoreInventoryAvailable(ShoppingCartItem item, LocalDispatcher dispatcher) {
        return isStoreInventoryAvailable(item.getProdCatalogId(), item.getProductId(), item.getQuantity(), item.getDelegator(), dispatcher);
    }
    
    /** check inventory availability for the given catalog, product, quantity, etc */
    public static boolean isStoreInventoryAvailable(String productStoreId, String productId, double quantity, GenericDelegator delegator, LocalDispatcher dispatcher) {
        GenericValue productStore = getProductStore(productStoreId, delegator);

        if (productStore == null) {
            Debug.logWarning("No ProductStore found with id " + productStoreId + ", returning false for inventory available check", module);
            return false;
        }

        // if prodCatalog is set to not check inventory break here
        if ("N".equals(productStore.getString("checkInventory"))) {
            // note: if not set, defaults to yes, check inventory
            if (Debug.verboseOn()) Debug.logVerbose("ProductStore with id " + productStoreId + ", is set to NOT check inventory, returning true for inventory available check", module);
            return true;
        }

        if ("Y".equals(productStore.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = productStore.getString("inventoryFacilityId");

            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("ProductStore with id " + productStoreId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, returning false for inventory check", module);
                return false;
            }

            Double availableToPromise = null;

            try {
                Map result = dispatcher.runSync("getInventoryAvailableByFacility",
                        UtilMisc.toMap("productId", productId, "facilityId", inventoryFacilityId));

                availableToPromise = (Double) result.get("availableToPromise");

                if (availableToPromise == null) {
                    Debug.logWarning("The getInventoryAvailableByFacility service returned a null availableToPromise, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE), module);
                    return false;
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking getInventoryAvailableByFacility service in isCatalogInventoryAvailable", module);
                return false;
            }

            // whew, finally here: now check to see if we got enough back...
            if (availableToPromise.doubleValue() >= quantity) {
                if (Debug.verboseOn()) Debug.logVerbose("Inventory IS available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise, module);
                return true;
            } else {
                if (Debug.verboseOn()) Debug.logVerbose("Returning false because there is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise, module);
                return false;
            }

        } else {
            Debug.logWarning("ProductStore with id " + productStoreId + " uses multiple inventory facilities, which is not yet implemented, return false for inventory check", module);
            return false;

            // TODO: check multiple inventory locations

            // must entire quantity be available in one location?

            // loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }
    
    public static boolean isStoreInventoryAvailable(ServletRequest request, String productId, double quantity) {
        GenericValue productStore = getProductStore(request);       

        if (productStore == null) {
            Debug.logWarning("No ProductStore found, return false for inventory check", module);
            return false;
        }
        
        String productStoreId = productStore.getString("productStoreId");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        return isStoreInventoryAvailable(productStoreId, productId, quantity, delegator, dispatcher);
    }    

    /** tries to reserve the specified quantity, if fails returns quantity that it could not reserve or zero if there was an error, otherwise returns null */
    public static Double reserveStoreInventory(String productStoreId, String productId, Double quantity,
            String orderId, String orderItemSeqId, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher) {

        GenericValue productStore = getProductStore(productStoreId, delegator);
        
        if (productStore == null) {
            Debug.logWarning("No ProductStore found with id " + productStoreId + ", not reserving inventory", module);
            return new Double(0.0);
        }

        // if prodCatalog is set to not reserve inventory, break here
        if ("N".equals(productStore.getString("reserveInventory"))) {
            // note: if not set, defaults to yes, reserve inventory
            if (Debug.verboseOn()) Debug.logVerbose("ProductStore with id " + productStoreId + ", is set to NOT reserve inventory, not reserving inventory", module);
            return null;
        }

        if ("Y".equals(productStore.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = productStore.getString("inventoryFacilityId");

            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("ProductStore with id " + productStoreId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, not reserving inventory", module);
                return new Double(0.0);
            }

            boolean requireInventory = isStoreInventoryRequired(productStoreId, productId, delegator);
            Double quantityNotReserved = null;

            try {
                Map serviceContext = new HashMap();

                serviceContext.put("productId", productId);
                serviceContext.put("facilityId", inventoryFacilityId);
                serviceContext.put("orderId", orderId);
                serviceContext.put("orderItemSeqId", orderItemSeqId);
                serviceContext.put("quantity", quantity);

                if (requireInventory) {
                    serviceContext.put("requireInventory", "Y");
                } else {
                    serviceContext.put("requireInventory", "N");
                }
                serviceContext.put("reserveOrderEnumId", productStore.get("reserveOrderEnumId"));
                serviceContext.put("userLogin", userLogin);

                Map result = dispatcher.runSync("reserveProductInventoryByFacility", serviceContext);

                quantityNotReserved = (Double) result.get("quantityNotReserved");

                if (quantityNotReserved == null) {
                    Debug.logWarning("The reserveProductInventoryByFacility service returned a null quantityNotReserved, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE), module);
                    if (!requireInventory) {
                        return null;
                    } else {
                        return new Double(0.0);
                    }
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking reserveProductInventoryByFacility service", module);
                if (!requireInventory) {
                    return null;
                } else {
                    return new Double(0.0);
                }
            }

            // whew, finally here: now check to see if we were able to reserve...
            if (quantityNotReserved.doubleValue() == 0) {
                if (Debug.verboseOn()) Debug.logVerbose("Inventory IS reserved in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity was " + quantity, module);
                return null;
            } else {
                if (Debug.verboseOn()) Debug.logVerbose("There is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", amount could not reserve is " + quantityNotReserved, module);
                return quantityNotReserved;
            }

        } else {
            Debug.logError("ProductStore with id " + productStoreId + " uses multiple inventory facilities, which is not yet implemented, not reserving inventory", module);
            return new Double(0.0);

            // TODO: check multiple inventory locations

            // must entire quantity be available in one location?

            // loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }    
}