/*
 * $Id: ProductServices.java,v 1.6 2004/02/06 17:04:48 ajzeneski Exp $
 *
 *  Copyright (c) 2002 The Open For Business Project (www.ofbiz.org)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.OrderedMap;
import org.ofbiz.base.util.OrderedSet;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Product Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class ProductServices {
    
    public static final String module = ProductServices.class.getName();

    /**
     * Creates a Collection of product entities which are variant products from the specified product ID.
     */
    public static Map prodFindAllVariants(DispatchContext dctx, Map context) {
        // * String productId      -- Parent (virtual) product ID
        context.put("type", "PRODUCT_VARIANT");
        return prodFindAssociatedByType(dctx, context);
    }

    /**
     * Finds a specific product or products which contain the selected features.
     */
    public static Map prodFindSelectedVariant(DispatchContext dctx, Map context) {
        // * String productId      -- Parent (virtual) product ID
        // * Map selectedFeatures  -- Selected features
        GenericDelegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        Map selectedFeatures = (Map) context.get("selectedFeatures");

        return ServiceUtil.returnError("This service has not yet been implemented.");
    }

    /**
     * Finds product variants based on a product ID and a distinct feature.
     */
    public static Map prodFindDistinctVariants(DispatchContext dctx, Map context) {
        // * String productId      -- Parent (virtual) product ID
        // * String feature        -- Distinct feature name
        GenericDelegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        String feature = (String) context.get("feature");

        return ServiceUtil.returnError("This service has not yet been implemented.");
    }

    /**
     * Finds a Set of feature types in sequence.
     */
    public static Map prodFindFeatureTypes(DispatchContext dctx, Map context) {
        // * String productId      -- Product ID to look up feature types
        GenericDelegator delegator = dctx.getDelegator();
        String productId = (String) context.get("productId");
        Set featureSet = new OrderedSet();

        try {
            Map fields = UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE");
            List order = UtilMisc.toList("sequenceNum", "productFeatureTypeId");
            List features = delegator.findByAndCache("ProductFeatureAndAppl", fields, order);
            Iterator i = features.iterator();
            while (i.hasNext()) {
                featureSet.add(((GenericValue) i.next()).getString("productFeatureTypeId"));
            }
            //if (Debug.infoOn()) Debug.logInfo("" + featureSet, module);
        } catch (GenericEntityException e) {
            String errMsg = "Problem reading product features: " + e.getMessage();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        if (featureSet.size() == 0) {
            String errMsg = "Warning: Problem reading product features: no features found for productId " + productId;
            Debug.logWarning(errMsg, module);
            //return ServiceUtil.returnError(errMsg);
        }
        Map result = ServiceUtil.returnSuccess();
        result.put("featureSet", featureSet);
        return result;
    }

    /**
     * Builds a variant feature tree.
     */
    public static Map prodMakeFeatureTree(DispatchContext dctx, Map context) {
        // * String productId      -- Parent (virtual) product ID
        // * List featureOrder     -- Order of features
        // * String productStoreId -- Product Store ID for Inventory
        String productStoreId = (String) context.get("productStoreId");
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map result = new HashMap();
        List featureOrder = new LinkedList((Collection) context.get("featureOrder"));

        if (featureOrder == null || featureOrder.size() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Empty list of features passed");
            return result;
        }

        Collection variants = (Collection) prodFindAllVariants(dctx, context).get("assocProducts");

        if (variants == null || variants.size() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Empty list of products returned");
            return result;
        }
        List items = new ArrayList();
        Iterator i = variants.iterator();

        while (i.hasNext()) {
            String productIdTo = (String) ((GenericValue) i.next()).get("productIdTo");

            // first check to see if intro and discontinue dates are within range
            GenericValue productTo = null;

            try {
                productTo = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productIdTo));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "Error finding associated variant with ID " + productIdTo + ", error was: " + e.toString());
                return result;
            }
            if (productTo == null) {
                Debug.logWarning("Could not find associated variant with ID " + productIdTo + ", not showing in list", module);
                continue;
            }

            java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

            // check to see if introductionDate hasn't passed yet
            if (productTo.get("introductionDate") != null && nowTimestamp.before(productTo.getTimestamp("introductionDate"))) {
                if (Debug.verboseOn()) {
                    String excMsg = "Tried to view the Product " + productTo.getString("productName") +
                        " (productId: " + productTo.getString("productId") + ") as a variant. This product has not yet been made available for sale, so not adding for view.";

                    Debug.logVerbose(excMsg, module);
                }
                continue;
            }

            // check to see if salesDiscontinuationDate has passed
            if (productTo.get("salesDiscontinuationDate") != null && nowTimestamp.after(productTo.getTimestamp("salesDiscontinuationDate"))) {
                if (Debug.verboseOn()) {
                    String excMsg = "Tried to view the Product " + productTo.getString("productName") +
                        " (productId: " + productTo.getString("productId") + ") as a variant. This product is no longer available for sale, so not adding for view.";

                    Debug.logVerbose(excMsg, module);
                }
                continue;
            }

            // next check inventory for each item: if inventory is not required or is available
            if (!org.ofbiz.product.store.ProductStoreWorker.isStoreInventoryRequired(productStoreId, productIdTo, delegator) ||
                org.ofbiz.product.store.ProductStoreWorker.isStoreInventoryAvailable(productStoreId, productIdTo, 1.0, delegator, dispatcher)) {
                items.add(productIdTo);
            }
        }

        String productId = (String) context.get("productId");

        // Make the selectable feature list
        List selectableFeatures = null;

        try {
            Map fields = UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE");
            List sort = UtilMisc.toList("sequenceNum");

            selectableFeatures = delegator.findByAndCache("ProductFeatureAndAppl", fields, sort);
            selectableFeatures = EntityUtil.filterByDate(selectableFeatures, true);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Empty list of selectable features found");
            return result;
        }
        Map features = new HashMap();
        Iterator sFIt = selectableFeatures.iterator();

        while (sFIt.hasNext()) {
            GenericValue v = (GenericValue) sFIt.next();
            String featureType = v.getString("productFeatureTypeId");
            String feature = v.getString("description");

            if (!features.containsKey(featureType)) {
                List featureList = new LinkedList();

                featureList.add(feature);
                features.put(featureType, featureList);
            } else {
                List featureList = (LinkedList) features.get(featureType);

                featureList.add(feature);
                features.put(featureType, featureList);
            }
        }

        Map tree = null;

        try {
            tree = makeGroup(delegator, features, items, featureOrder, 0);
        } catch (Exception e) {
            Debug.logError(e, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return result;
        }
        if (tree == null || tree.size() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Feature grouping came back empty");
        } else {
            result.put("variantTree", tree);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }

        Map sample = null;

        try {
            sample = makeVariantSample(dctx.getDelegator(), features, items, (String) featureOrder.get(0));
        } catch (Exception e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return result;
        }
        if (sample == null || sample.size() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Feature sample came back empty");
        } else {
            result.put("variantSample", sample);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }

        return result;
    }

    /**
     * Gets the product features of a product.
     */
    public static Map prodGetFeatures(DispatchContext dctx, Map context) {
        // * String productId      -- Product ID to fond
        // * String type           -- Type of feature (STANDARD_FEATURE, SELECTABLE_FEATURE)
        // * String distinct       -- Distinct feature (SIZE, COLOR)
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        String distinct = (String) context.get("distinct");
        String type = (String) context.get("type");
        Collection features = null;

        try {
            Map fields = UtilMisc.toMap("productId", productId);
            List order = UtilMisc.toList("sequenceNum", "productFeatureTypeId");

            if (distinct != null) fields.put("productFeatureType", distinct);
            if (type != null) fields.put("productFeatureApplTypeId", type);
            features = delegator.findByAndCache("ProductFeatureAndAppl", fields, order);
            result.put("productFeatures", features);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading product feature entity: " + e.getMessage());
        }
        return result;
    }

    /**
     * Finds a product by product ID.
     */
    public static Map prodFindProduct(DispatchContext dctx, Map context) {
        // * String productId      -- Product ID to find
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");

        if (productId == null || productId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Invalid productId passed.");
            return result;
        }

        try {
            GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            GenericValue mainProduct = product;

            if (product.get("isVariant") != null && product.getString("isVariant").equalsIgnoreCase("Y")) {
                List c = product.getRelatedByAndCache("AssocProductAssoc",
                        UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"));

                if (c != null) {
                    //if (Debug.infoOn()) Debug.logInfo("Found related: " + c, module);
                    c = EntityUtil.filterByDate(c, true);
                    //if (Debug.infoOn()) Debug.logInfo("Found Filtered related: " + c, module);
                    if (c.size() > 0) {
                        GenericValue asV = (GenericValue) c.iterator().next();

                        //if (Debug.infoOn()) Debug.logInfo("ASV: " + asV, module);
                        mainProduct = asV.getRelatedOneCache("MainProduct");
                        //if (Debug.infoOn()) Debug.logInfo("Main product = " + mainProduct, module);
                    }
                }
            }
            result.put("product", mainProduct);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems reading product entity: " + e.getMessage());
        }

        return result;
    }

    /**
     * Finds associated products by product ID and association ID.
     */
    public static Map prodFindAssociatedByType(DispatchContext dctx, Map context) {
        // * String productId      -- Current Product ID
        // * String type           -- Type of association (ie PRODUCT_UPGRADE, PRODUCT_COMPLEMENT, PRODUCT_VARIANT)
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        String productIdTo = (String) context.get("productIdTo");
        String type = (String) context.get("type");

        Boolean cvaBool = (Boolean) context.get("checkViewAllow");
        boolean checkViewAllow = (cvaBool == null ? false : cvaBool.booleanValue());
        String prodCatalogId = (String) context.get("prodCatalogId");
        
        if (productId == null && productIdTo == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Both productId and productIdTo cannot be null");
            return result;
        }

        if (productId != null && productIdTo != null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Both productId and productIdTo cannot be defined");
            return result;
        }

        productId = productId == null ? productIdTo : productId;
        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems reading product entity: " + e.getMessage());
            return result;
        }

        if (product == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems getting the product entity.");
            return result;
        }

        try {
            List productAssocs = null;

            if (productIdTo == null) {
                productAssocs = product.getRelatedCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", type), UtilMisc.toList("sequenceNum"));
            } else {
                productAssocs = product.getRelatedCache("AssocProductAssoc", UtilMisc.toMap("productAssocTypeId", type), UtilMisc.toList("sequenceNum"));
            }
            // filter the list by date
            productAssocs = EntityUtil.filterByDate(productAssocs, true);
            // first check to see if there is a view allow category and if these producta are in it...
            if (checkViewAllow && prodCatalogId != null && productAssocs != null && productAssocs.size() > 0) {
                String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
                if (viewProductCategoryId != null) {
                    if (productIdTo == null) {
                        productAssocs = CategoryWorker.filterProductsInCategory(delegator, productAssocs, viewProductCategoryId, "productIdTo");
                    } else {
                        productAssocs = CategoryWorker.filterProductsInCategory(delegator, productAssocs, viewProductCategoryId, "productId");
                    }
                }
            }
            
            
            result.put("assocProducts", productAssocs);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems product association relation: " + e.getMessage());
            return result;
        }

        return result;
    }

    // Builds a product feature tree
    private static Map makeGroup(GenericDelegator delegator, Map featureList, List items, List order, int index)
        throws IllegalArgumentException, IllegalStateException {
        //List featureKey = new ArrayList();
        Map tempGroup = new HashMap();
        Map group = new OrderedMap();
        String orderKey = (String) order.get(index);

        if (featureList == null) {
            throw new IllegalArgumentException("Cannot build feature tree: featureList is null");
        }

        if (index < 0) {
            throw new IllegalArgumentException("Invalid index '" + index + "' min index '0'");
        }
        if (index + 1 > order.size()) {
            throw new IllegalArgumentException("Invalid index '" + index + "' max index '" + (order.size() - 1) + "'");
        }

        // loop through items and make the lists
        Iterator itemIterator = items.iterator();

        while (itemIterator.hasNext()) {
            // -------------------------------
            // Gather the necessary data
            // -------------------------------
            String thisItem = (String) itemIterator.next();

            if (Debug.verboseOn()) Debug.logVerbose("ThisItem: " + thisItem, module);
            List features = null;

            try {
                Map fields = UtilMisc.toMap("productId", thisItem, "productFeatureTypeId", orderKey,
                        "productFeatureApplTypeId", "STANDARD_FEATURE");
                List sort = UtilMisc.toList("sequenceNum");

                // get the features and filter out expired dates
                features = delegator.findByAndCache("ProductFeatureAndAppl", fields, sort);
                features = EntityUtil.filterByDate(features, true);
            } catch (GenericEntityException e) {
                throw new IllegalStateException("Problem reading relation: " + e.getMessage());
            }
            if (Debug.verboseOn()) Debug.logVerbose("Features: " + features, module);

            // -------------------------------
            Iterator featuresIterator = features.iterator();

            while (featuresIterator.hasNext()) {
                GenericValue item = (GenericValue) featuresIterator.next();
                Object itemKey = item.get("description");

                if (tempGroup.containsKey(itemKey)) {
                    List itemList = (List) tempGroup.get(itemKey);

                    if (!itemList.contains(thisItem))
                        itemList.add(thisItem);
                } else {
                    List itemList = UtilMisc.toList(thisItem);

                    tempGroup.put(itemKey, itemList);
                }
            }
        }
        if (Debug.verboseOn()) Debug.logVerbose("TempGroup: " + tempGroup, module);

        // Loop through the feature list and order the keys in the tempGroup
        List orderFeatureList = (List) featureList.get(orderKey);

        if (orderFeatureList == null) {
            throw new IllegalArgumentException("Cannot build feature tree: orderFeatureList is null for orderKey=" + orderKey);
        }

        Iterator featureListIt = orderFeatureList.iterator();

        while (featureListIt.hasNext()) {
            String featureStr = (String) featureListIt.next();

            if (tempGroup.containsKey(featureStr))
                group.put(featureStr, tempGroup.get(featureStr));
        }

        if (Debug.verboseOn()) Debug.logVerbose("Group: " + group, module);

        // no groups; no tree
        if (group.size() == 0) {
            return group;
            //throw new IllegalStateException("Cannot create tree from group list; error on '" + orderKey + "'");
        }

        if (index + 1 == order.size()) {
            return group;
        }

        // loop through the keysets and get the sub-groups
        Iterator groupIterator = group.keySet().iterator();
        while (groupIterator.hasNext()) {
            Object key = groupIterator.next();
            List itemList = (List) group.get(key);

            if (itemList != null && itemList.size() > 0) {
                Map subGroup = makeGroup(delegator, featureList, itemList, order, index + 1);
                group.put(key, subGroup);
            } else {
                // do nothing, ie put nothing in the Map
                //throw new IllegalStateException("Cannot create tree from an empty list; error on '" + key + "'");
            }
        }
        return group;
    }

    // builds a variant sample (a single sku for a featureType)
    private static Map makeVariantSample(GenericDelegator delegator, Map featureList, List items, String feature) {
        Map tempSample = new HashMap();
        Map sample = new OrderedMap();
        Iterator itemIt = items.iterator();

        while (itemIt.hasNext()) {
            String productId = (String) itemIt.next();
            List features = null;

            try {
                Map fields = UtilMisc.toMap("productId", productId, "productFeatureTypeId", feature,
                        "productFeatureApplTypeId", "STANDARD_FEATURE");
                List sort = UtilMisc.toList("sequenceNum", "description");

                // get the features and filter out expired dates
                features = delegator.findByAndCache("ProductFeatureAndAppl", fields, sort);
                features = EntityUtil.filterByDate(features, true);
            } catch (GenericEntityException e) {
                throw new IllegalStateException("Problem reading relation: " + e.getMessage());
            }
            Iterator featureIt = features.iterator();

            while (featureIt.hasNext()) {
                GenericValue featureAppl = (GenericValue) featureIt.next();

                try {
                    GenericValue product = delegator.findByPrimaryKeyCache("Product",
                            UtilMisc.toMap("productId", productId));

                    tempSample.put(featureAppl.getString("description"), product);
                } catch (GenericEntityException e) {
                    throw new RuntimeException("Cannot get product entity: " + e.getMessage());
                }
            }
        }

        // Sort the sample based on the feature list.
        List features = (LinkedList) featureList.get(feature);
        Iterator fi = features.iterator();

        while (fi.hasNext()) {
            String f = (String) fi.next();

            if (tempSample.containsKey(f))
                sample.put(f, tempSample.get(f));
        }

        return sample;
    }

}
