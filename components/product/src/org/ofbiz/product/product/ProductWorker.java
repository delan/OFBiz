/*
 * $Id: ProductWorker.java,v 1.15 2004/05/23 07:57:29 jonesde Exp $
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
package org.ofbiz.product.product;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.OrderedMap;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

/**
 * Product Worker class to reduce code in JSPs.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.15 $
 * @since      2.0
 */
public class ProductWorker {
    
    public static final String module = ProductWorker.class.getName();
    public static final String resource = "ProductUiLabels";

    public static void getProduct(PageContext pageContext, String attributeName) {
        getProduct(pageContext, attributeName, null);
    }

    public static boolean shippingApplies(GenericValue product) {
        String errMsg = null;
        if (product != null) {
            String productTypeId = product.getString("productTypeId");
            if ("SERVICE".equals(productTypeId) || "DIGITAL_GOOD".equals(productTypeId)) {
                // don't charge shipping on services or digital goods
                return false;
            }       
            Boolean chargeShipping = product.getBoolean("chargeShipping");
    
            if (chargeShipping == null) {
                return true;
            } else {
                return chargeShipping.booleanValue();
            }
        } else {
            // todo: Hier noch Uebersetzungen einfuegen?
            //errMsg = UtilProperties.getMessage(resource,"productworker.null_product_entity_not_valid", UtilHttp.getLocale(request));
            throw new IllegalArgumentException(errMsg);
        }                
    }
    
    public static boolean taxApplies(GenericValue product) {
        String errMsg = null;
        if (product != null) {        
            Boolean taxable = product.getBoolean("taxable");
    
            if (taxable == null) {
                return true;
            } else {
                return taxable.booleanValue();
            }
        } else {
            // todo: Hier noch Uebersetzungen einfuegen?
            //errMsg = UtilProperties.getMessage(resource,"productworker.null_product_entity_not_valid", UtilHttp.getLocale(request));
            throw new IllegalArgumentException(errMsg);
        }
    }    
    
    public static void getProduct(PageContext pageContext, String attributeName, String productId) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        ServletRequest request = pageContext.getRequest();

        if (productId == null)
            productId = UtilFormatOut.checkNull(request.getParameter("product_id"), request.getParameter("PRODUCT_ID"));

        if (productId.equals(""))
            return;

        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            product = null;
        }
        if (product != null)
            pageContext.setAttribute(attributeName, product);
    }

    public static String getVariantVirtualId(GenericValue variantProduct) throws GenericEntityException {
        List productAssocs = getVariantVirtualAssocs(variantProduct);
        GenericValue productAssoc = EntityUtil.getFirst(productAssocs);
        if (productAssoc != null) {
            return productAssoc.getString("productId");
        } else {
            return null;
        }
    }

    public static List getVariantVirtualAssocs(GenericValue variantProduct) throws GenericEntityException {
        if (variantProduct != null && "Y".equals(variantProduct.getString("isVariant"))) {
            List productAssocs = EntityUtil.filterByDate(variantProduct.getRelatedByAndCache("AssocProductAssoc", 
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT")), true);
            return productAssocs;
        }
        return null;
    }

    /** invokes the getInventoryAvailableByFacility service, returns true if specified quantity is available, else false **/
    public static boolean isProductInventoryAvailableByFacility(String productId, String inventoryFacilityId, double quantity, LocalDispatcher dispatcher) throws GenericServiceException {
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

        // check to see if we got enough back...
        if (availableToPromise.doubleValue() >= quantity) {
            if (Debug.infoOn()) Debug.logInfo("Inventory IS available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise, module);
            return true;
        } else {
            if (Debug.infoOn()) Debug.logInfo("Returning false because there is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise, module);
            return false;
        }
    }

    /** invokes the reserveProductInventoryByFacility service, returns quantity not reserved, 0 on error, null on success **/
    public static Double reserveProductInventoryByFacility(String productId, Double quantity, String inventoryFacilityId,  String orderId, String reserveOrderEnumId, String orderItemSeqId, boolean requireInventory, GenericValue userLogin, LocalDispatcher dispatcher) throws GenericServiceException {

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
            serviceContext.put("reserveOrderEnumId", reserveOrderEnumId);
            serviceContext.put("userLogin", userLogin);

            Map result = dispatcher.runSync("reserveProductInventoryByFacility", serviceContext);

            quantityNotReserved = (Double) result.get("quantityNotReserved");

            if (quantityNotReserved == null) {
                Debug.logWarning("The reserveProductInventoryByFacility service returned a null quantityNotReserved, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE), module);
                return !requireInventory? null: new Double(0.0);
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "Error invoking reserveProductInventoryByFacility service", module);
            return !requireInventory? null: new Double(0.0);
        }

        // whew, finally here: now check to see if we were able to reserve...
        if (quantityNotReserved.doubleValue() == 0) {
            if (Debug.infoOn()) Debug.logInfo("Inventory IS reserved in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity was " + quantity, module);
            return null;
        } else {
            if (Debug.infoOn()) Debug.logInfo("There is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", amount could not reserve is " + quantityNotReserved, module);
            return quantityNotReserved;
        }
    }
    
    public static void getAssociatedProducts(PageContext pageContext, String productAttributeName, String assocPrefix) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue product = (GenericValue) pageContext.getAttribute(productAttributeName);

        if (product == null)
            return;

        try {
            List upgradeProducts = product.getRelatedByAndCache("MainProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_UPGRADE"));

            List complementProducts = product.getRelatedByAndCache("MainProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));

            List obsolescenceProducts = product.getRelatedByAndCache("AssocProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_OBSOLESCENCE"));

            List obsoleteByProducts = product.getRelatedByAndCache("MainProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_OBSOLESCENCE"));

            // since ProductAssoc records have a fromDate and thruDate, we can filter by now so that only assocs in the date range are included
            upgradeProducts = EntityUtil.filterByDate(upgradeProducts, true);
            complementProducts = EntityUtil.filterByDate(complementProducts, true);
            obsolescenceProducts = EntityUtil.filterByDate(obsolescenceProducts, true);
            obsoleteByProducts = EntityUtil.filterByDate(obsoleteByProducts, true);

            if (upgradeProducts != null && upgradeProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
            if (complementProducts != null && complementProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "complement", complementProducts);
            if (obsolescenceProducts != null && obsolescenceProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "obsolescence", obsolescenceProducts);
            if (obsoleteByProducts != null && obsoleteByProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "obsoleteby", obsoleteByProducts);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
    }

    public static Map getOptionalProductFeatures(GenericDelegator delegator, String productId) {
        Map featureMap = new OrderedMap();

        List productFeatureAppls = null;
        try {
            productFeatureAppls = delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "OPTIONAL_FEATURE"), UtilMisc.toList("productFeatureTypeId", "sequenceNum"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (productFeatureAppls != null) {
            Iterator i = productFeatureAppls.iterator();
            while (i.hasNext()) {
                GenericValue appl = (GenericValue) i.next();
                String featureType = appl.getString("productFeatureTypeId");
                List features = (List) featureMap.get(featureType);
                if (features == null) {
                    features = new LinkedList();
                }
                features.add(appl);
                featureMap.put(featureType, features);
            }
        }

        return featureMap;
    }

    // product calc methods
    
    public static double calcOrderAdjustments(List orderHeaderAdjustments, double subTotal, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;

        if (orderHeaderAdjustments != null && orderHeaderAdjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(orderHeaderAdjustments, includeOther, includeTax, includeShipping, false, false);
            Iterator adjIt = filteredAdjs.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                adjTotal += calcOrderAdjustment(orderAdjustment, subTotal);
            }
        }
        return adjTotal;
    }
    
    public static double calcOrderAdjustment(GenericValue orderAdjustment, double orderSubTotal) {
        double adjustment = 0.0;

        if (orderAdjustment.get("amount") != null) {
            adjustment += orderAdjustment.getDouble("amount").doubleValue();
        }
        if (orderAdjustment.get("percentage") != null) {
            adjustment += (orderAdjustment.getDouble("percentage").doubleValue() * orderSubTotal);
        }
        return adjustment;
    }    
    
    public static List filterOrderAdjustments(List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        List newOrderAdjustmentsList = new LinkedList();

        if (adjustments != null && adjustments.size() > 0) {
            Iterator adjIt = adjustments.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                boolean includeAdjustment = false;

                if ("SALES_TAX".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeTax) includeAdjustment = true;
                } else if ("SHIPPING_CHARGES".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeShipping) includeAdjustment = true;
                } else {
                    if (includeOther) includeAdjustment = true;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forTax && "N".equals(orderAdjustment.getString("includeInTax"))) {
                    includeAdjustment = false;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forShipping && "N".equals(orderAdjustment.getString("includeInShipping"))) {
                    includeAdjustment = false;
                }

                if (includeAdjustment) {
                    newOrderAdjustmentsList.add(orderAdjustment);
                }
            }
        }
        return newOrderAdjustmentsList;
    }

    public static double getAverageProductRating(GenericDelegator delegator, String productId) {
        return getAverageProductRating(delegator, productId, null);
    }
    
    public static double getAverageProductRating(GenericDelegator delegator, String productId, String productStoreId) {
        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return ProductWorker.getAverageProductRating(product, productStoreId);
    }

    public static double getAverageProductRating(GenericValue product, String productStoreId) {
        return getAverageProductRating(product, null, productStoreId);
    }

    public static double getAverageProductRating(GenericValue product, List reviews, String productStoreId) {
        if (product == null) {
            Debug.logWarning("Invalid product entity passed; unable to obtain valid product rating", module);
            return 0.00;
        }

        double productRating = 0.00;
        Double productEntityRating = product.getDouble("productRating");
        String entityFieldType = product.getString("ratingTypeEnum");

        // null check
        if (productEntityRating == null) {
            productEntityRating = new Double(0);
        }
        if (entityFieldType == null) {
            entityFieldType = new String();
        }

        if ("PRDR_FLAT".equals(entityFieldType)) {
            productRating = productEntityRating.doubleValue();
        } else {
            // get the product rating from the ProductReview entity; limit by product store if ID is passed
            Map reviewByAnd = UtilMisc.toMap("statusId", "PRR_APPROVED");
            if (productStoreId != null) {
                reviewByAnd.put("productStoreId", productStoreId);
            }

            // lookup the reviews if we didn't pass them in
            if (reviews == null) {
                try {
                    reviews = product.getRelatedCache("ProductReview", reviewByAnd, UtilMisc.toList("-postedDateTime"));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }

            // tally the average
            double ratingTally = 0;
            double numRatings = 0;
            if (reviews != null) {
                Iterator i = reviews.iterator();
                while (i.hasNext()) {
                    GenericValue productReview = (GenericValue) i.next();
                    Double rating = productReview.getDouble("productRating");
                    if (rating != null) {
                        ratingTally += rating.doubleValue();
                        numRatings++;
                    }
                }
            }
            if (ratingTally > 0 && numRatings > 0) {
                productRating = ratingTally /  numRatings;
            }

            if ("PRDR_MIN".equals(entityFieldType)) {
                // check for min
                if (productEntityRating.doubleValue() > productRating) {
                    productRating = productEntityRating.doubleValue();
                }
            } else if ("PRDR_MAX".equals(entityFieldType)) {
                // check for max
                if (productRating > productEntityRating.doubleValue()) {
                    productRating = productEntityRating.doubleValue();
                }
            }
        }

        return productRating;
    }
}

