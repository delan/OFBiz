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
package org.ofbiz.product.product;

import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.config.ProductConfigWrapper.ConfigOption;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import org.apache.commons.collections.map.LinkedMap;

/**
 * Product Worker class to reduce code in JSPs.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
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

    /** 
     * invokes the getInventoryAvailableByFacility service, returns true if specified quantity is available, else false
     * this is only used in the related method that uses a ProductConfigWrapper, until that is refactored into a service as well...
     */
    private static boolean isProductInventoryAvailableByFacility(String productId, String inventoryFacilityId, double quantity, LocalDispatcher dispatcher) throws GenericServiceException {
        Double availableToPromise = null;

        try {
            Map result = dispatcher.runSync("getInventoryAvailableByFacility",
                                            UtilMisc.toMap("productId", productId, "facilityId", inventoryFacilityId));

            availableToPromise = (Double) result.get("availableToPromiseTotal");

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

    /** 
     * Invokes the getInventoryAvailableByFacility service, returns true if specified quantity is available for all the selected parts, else false.
     * Also, set the available flag for all the product configuration's options.
     **/
    public static boolean isProductInventoryAvailableByFacility(ProductConfigWrapper productConfig, String inventoryFacilityId, double quantity, LocalDispatcher dispatcher) throws GenericServiceException {
        boolean available = true;
        List options = productConfig.getSelectedOptions();
        Iterator optionsIt = options.iterator();
        while (optionsIt.hasNext()) {
            ConfigOption ci = (ConfigOption)optionsIt.next();
            List products = ci.getComponents();
            Iterator productsIt = products.iterator();
            while (productsIt.hasNext()) {
                GenericValue product = (GenericValue)productsIt.next();
                String productId = product.getString("productId");
                Double cmpQuantity = product.getDouble("quantity");
                double neededQty = 1.0;
                if (cmpQuantity != null) {
                    neededQty = quantity * cmpQuantity.doubleValue();
                }
                if (!isProductInventoryAvailableByFacility(productId, inventoryFacilityId, neededQty, dispatcher)) {
                    ci.setAvailable(false);
                }
            }
            if (!ci.isAvailable()) {
                available = false;
            }
        }
        return available;
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
    
    /**
     * Gets ProductFeature GenericValue for all distinguishing features of a variant product. 
     * Distinguishing means all features that are selectable on the corresponding virtual product and standard on the variant plus all DISTINGUISHING_FEAT assoc type features on the variant. 
     */
    public static Set getVariantDistinguishingFeatures(GenericValue variantProduct) throws GenericEntityException {
        if (variantProduct == null) {
            return new HashSet();
        }
        if (!"Y".equals(variantProduct.getString("isVariant"))) {
            throw new IllegalArgumentException("Cannot get distinguishing features for a product that is not a variant (ie isVariant!=Y).");
        }
        GenericDelegator delegator = variantProduct.getDelegator();
        String virtualProductId = getVariantVirtualId(variantProduct);
        
        // find all selectable features on the virtual product that are also standard features on the variant
        Set distFeatures = new HashSet();
        
        List variantDistinguishingFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProduct.get("productId"), "productFeatureApplTypeId", "DISTINGUISHING_FEAT"));
        // Debug.logInfo("Found variantDistinguishingFeatures: " + variantDistinguishingFeatures, module);

        Iterator variantDistinguishingFeatureIter = UtilMisc.toIterator(EntityUtil.filterByDate(variantDistinguishingFeatures));
        while (variantDistinguishingFeatureIter != null && variantDistinguishingFeatureIter.hasNext()) {
            GenericValue variantDistinguishingFeature = (GenericValue) variantDistinguishingFeatureIter.next();
            GenericValue dummyFeature = delegator.makeValue("ProductFeature", null);
            dummyFeature.setAllFields(variantDistinguishingFeature, true, null, null);
            distFeatures.add(dummyFeature);
        }

        List virtualSelectableFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", virtualProductId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"));
        // Debug.logInfo("Found virtualSelectableFeatures: " + virtualSelectableFeatures, module);

        Iterator virtualSelectableFeatureIter = UtilMisc.toIterator(EntityUtil.filterByDate(virtualSelectableFeatures));
        Set virtualSelectableFeatureIds = new HashSet();
        while (virtualSelectableFeatureIter != null && virtualSelectableFeatureIter.hasNext()) {
            GenericValue virtualSelectableFeature = (GenericValue) virtualSelectableFeatureIter.next();
            virtualSelectableFeatureIds.add(virtualSelectableFeature.get("productFeatureId"));
        }
        
        List variantStandardFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProduct.get("productId"), "productFeatureApplTypeId", "STANDARD_FEATURE"));
        // Debug.logInfo("Found variantStandardFeatures: " + variantStandardFeatures, module);

        Iterator variantStandardFeatureIter = UtilMisc.toIterator(EntityUtil.filterByDate(variantStandardFeatures));
        while (variantStandardFeatureIter != null && variantStandardFeatureIter.hasNext()) {
            GenericValue variantStandardFeature = (GenericValue) variantStandardFeatureIter.next();
            if (virtualSelectableFeatureIds.contains(variantStandardFeature.get("productFeatureId"))) {
                GenericValue dummyFeature = delegator.makeValue("ProductFeature", null);
                dummyFeature.setAllFields(variantStandardFeature, true, null, null);
                distFeatures.add(dummyFeature);
            }
        }
        
        return distFeatures;
    }

    /** 
     *  Get the name to show to the customer for GWP alternative options.
     *  If the alternative is a variant, find the distinguishing features and show those instead of the name; if it is not a variant then show the PRODUCT_NAME content.
     */
    public static String getGwpAlternativeOptionName(GenericDelegator delegator, String alternativeOptionProductId, Locale locale) {
        try {
            GenericValue alternativeOptionProduct = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", alternativeOptionProductId));
            if (alternativeOptionProduct != null) {
                if ("Y".equals(alternativeOptionProduct.getString("isVariant"))) {
                    Set distFeatures = getVariantDistinguishingFeatures(alternativeOptionProduct);
                    if (distFeatures != null && distFeatures.size() > 0) {
                        // Debug.logInfo("Found distinguishing features: " + distFeatures, module);
                        
                        StringBuffer nameBuf = new StringBuffer();
                        Iterator distFeatIter = distFeatures.iterator();
                        while (distFeatIter.hasNext()) {
                            GenericValue productFeature = (GenericValue) distFeatIter.next();
                            GenericValue productFeatureType = productFeature.getRelatedOneCache("ProductFeatureType");
                            if (productFeatureType != null) {
                                nameBuf.append(productFeatureType.get("description", locale));
                                nameBuf.append(":");
                            }
                            nameBuf.append(productFeature.get("description", locale));
                            if (distFeatIter.hasNext()) {
                                nameBuf.append(", ");
                            }
                        }
                        return nameBuf.toString();
                    }
                }

                // got to here, default to PRODUCT_NAME
                String alternativeProductName = ProductContentWrapper.getProductContentAsText(alternativeOptionProduct, "PRODUCT_NAME", locale);
                // Debug.logInfo("Using PRODUCT_NAME: " + alternativeProductName, module);
                return alternativeProductName;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        // finally fall back to the ID in square braces
        return "[" + alternativeOptionProductId + "]";
    }

    /**
     * gets productFeatures given a productFeatureApplTypeId
     * @param delegator
     * @param productId
     * @param productFeatureApplTypeId - if null, returns ALL productFeatures, regardless of applType
     * @return
     */
    public static List getProductFeaturesByApplTypeId(GenericDelegator delegator, String productId, String productFeatureApplTypeId) {
        if (productId == null) {
            return null;
        }
        try {
            return getProductFeaturesByApplTypeId(delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)), 
                    productFeatureApplTypeId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return null;
    }

    public static List getProductFeaturesByApplTypeId(GenericValue product, String productFeatureApplTypeId) {
        if (product == null) {
            return null;
        }
        List features = new ArrayList();
        try {
            if (product != null) {
                List productAppls;
                if (productFeatureApplTypeId == null) {
                    productAppls = product.getRelated("ProductFeatureAppl");
                } else {
                    productAppls = product.getRelatedByAnd("ProductFeatureAppl",
                            UtilMisc.toMap("productFeatureApplTypeId", productFeatureApplTypeId));
                }
                for (int i = 0; i < productAppls.size(); i++) {
                    GenericValue productAppl = (GenericValue)productAppls.get(i);
                    features.add(productAppl.getRelatedOne("ProductFeature"));
                }
                features = EntityUtil.orderBy(features, UtilMisc.toList("description"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return features;
    }

    public static Map getOptionalProductFeatures(GenericDelegator delegator, String productId) {
        Map featureMap = new LinkedMap();

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

    public static List getCurrentProductCategories(GenericDelegator delegator, String productId) {
        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return getCurrentProductCategories(delegator, product);
    }

    public static List getCurrentProductCategories(GenericDelegator delegator, GenericValue product) {
        if (product == null) {
            return null;
        }
        List categories = new ArrayList();
        try {
            List categoryMembers = product.getRelated("ProductCategoryMember");
            categoryMembers = EntityUtil.filterByDate(categoryMembers);
            categories = EntityUtil.getRelated("ProductCategory", categoryMembers);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return categories;
    }
}

