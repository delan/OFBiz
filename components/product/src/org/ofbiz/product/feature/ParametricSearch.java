/*
 * $Id: ParametricSearch.java,v 1.10 2004/01/21 19:55:19 jonesde Exp $
 *
 *  Copyright (c) 2001 The Open For Business Project (www.ofbiz.org)
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
package org.ofbiz.product.feature;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

/**
 *  Utilities for parametric search based on features.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.10 $
 * @since      2.1
 */
public class ParametricSearch {
    
    public static final String module = ParametricSearch.class.getName();
    
    public static final int DEFAULT_PER_TYPE_MAX_SIZE = 500;
    
    // caches expire after 10 minutes, a reasonable value hopefully...
    //public static UtilCache featureAllCache = new UtilCache("custom.FeaturePerTypeAll", 0, 600000, true);
    //public static UtilCache featureByCategoryCache = new UtilCache("custom.FeaturePerTypeByCategory", 0, 600000, true);
    
    /** Gets all features associated with the specified category through: 
     * ProductCategory -> ProductFeatureCategoryAppl -> ProductFeatureCategory -> ProductFeature.
     * Returns a Map of Lists of ProductFeature GenericValue objects organized by productFeatureTypeId. 
     */
    public static Map makeCategoryFeatureLists(String productCategoryId, GenericDelegator delegator) {
        return makeCategoryFeatureLists(productCategoryId, delegator, DEFAULT_PER_TYPE_MAX_SIZE);
    }
    
    public static Map makeCategoryFeatureLists(String productCategoryId, GenericDelegator delegator, int perTypeMaxSize) {
        Map productFeaturesByTypeMap = new HashMap();
        try {
            List productFeatureCategoryAppls = delegator.findByAndCache("ProductFeatureCategoryAppl", UtilMisc.toMap("productCategoryId", productCategoryId));
            productFeatureCategoryAppls = EntityUtil.filterByDate(productFeatureCategoryAppls, true);
            if (productFeatureCategoryAppls != null) { 
                Iterator pfcasIter = productFeatureCategoryAppls.iterator();
                while (pfcasIter.hasNext()) {
                    GenericValue productFeatureCategoryAppl = (GenericValue) pfcasIter.next();
                    List productFeatures = delegator.findByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", productFeatureCategoryAppl.get("productFeatureCategoryId")));
                    Iterator pfsIter = productFeatures.iterator();
                    while (pfsIter.hasNext()) {
                        GenericValue productFeature = (GenericValue) pfsIter.next();
                        String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
                        Map featuresByType = (Map) productFeaturesByTypeMap.get(productFeatureTypeId);
                        if (featuresByType == null) {
                            featuresByType = new HashMap();
                            productFeaturesByTypeMap.put(productFeatureTypeId, featuresByType);
                        }
                        if (featuresByType.size() < perTypeMaxSize) {
                            featuresByType.put(productFeature.get("productFeatureId"), productFeature);
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting feature categories associated with the category with ID: " + productCategoryId, module);
        }
           
        try {
            List productFeatureCatGrpAppls = delegator.findByAndCache("ProductFeatureCatGrpAppl", UtilMisc.toMap("productCategoryId", productCategoryId));
            productFeatureCatGrpAppls = EntityUtil.filterByDate(productFeatureCatGrpAppls, true);
            if (productFeatureCatGrpAppls != null) { 
                Iterator pfcgasIter = productFeatureCatGrpAppls.iterator();
                while (pfcgasIter.hasNext()) {
                    GenericValue productFeatureCatGrpAppl = (GenericValue) pfcgasIter.next();
                    List productFeatureGroupAppls = delegator.findByAndCache("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureCatGrpAppl.get("productFeatureGroupId")));
                    Iterator pfgaasIter = productFeatureGroupAppls.iterator();
                    while (pfgaasIter.hasNext()) {
                        GenericValue productFeatureGroupAppl = (GenericValue) pfgaasIter.next();
                        GenericValue productFeature = delegator.findByPrimaryKeyCache("ProductFeature", UtilMisc.toMap("productFeatureId", productFeatureGroupAppl.get("productFeatureId")));
                        
                        String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
                        Map featuresByType = (Map) productFeaturesByTypeMap.get(productFeatureTypeId);
                        if (featuresByType == null) {
                            featuresByType = new HashMap();
                            productFeaturesByTypeMap.put(productFeatureTypeId, featuresByType);
                        }
                        if (featuresByType.size() < perTypeMaxSize) {
                            featuresByType.put(productFeature.get("productFeatureId"), productFeature);
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting feature groups associated with the category with ID: " + productCategoryId, module);
        }
           
        // now before returning, order the features in each list by description
        Iterator productFeatureTypeEntries = productFeaturesByTypeMap.entrySet().iterator();
        while (productFeatureTypeEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) productFeatureTypeEntries.next();
            List sortedFeatures = EntityUtil.orderBy(((Map) entry.getValue()).values(), UtilMisc.toList("description"));
            productFeaturesByTypeMap.put(entry.getKey(), sortedFeatures);
        }
        
        return productFeaturesByTypeMap;
    }
    
    public static Map getAllFeaturesByType(GenericDelegator delegator) {
        return getAllFeaturesByType(delegator, DEFAULT_PER_TYPE_MAX_SIZE);
    }
    public static Map getAllFeaturesByType(GenericDelegator delegator, int perTypeMaxSize) {
        Map productFeaturesByTypeMap = new HashMap();
        try {
            Set typesWithOverflowMessages = new HashSet();
            EntityListIterator productFeatureEli = delegator.findListIteratorByCondition("ProductFeature", null, null, UtilMisc.toList("description"));
            GenericValue productFeature = null;
            while ((productFeature = (GenericValue) productFeatureEli.next()) != null) {
                String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
                List featuresByType = (List) productFeaturesByTypeMap.get(productFeatureTypeId);
                if (featuresByType == null) {
                    featuresByType = new LinkedList();
                    productFeaturesByTypeMap.put(productFeatureTypeId, featuresByType);
                }
                if (featuresByType.size() > perTypeMaxSize) {
                    if (!typesWithOverflowMessages.contains(productFeatureTypeId)) {
                        typesWithOverflowMessages.add(productFeatureTypeId);
                        // TODO: uh oh, how do we pass this message back? no biggie for now 
                    }
                } else {
                    featuresByType.add(productFeature);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting all features", module);
        }
        return productFeaturesByTypeMap;
    }
    
    public static Map makeFeatureIdByTypeMap(ServletRequest request) {
        Map parameters = UtilHttp.getParameterMap((HttpServletRequest) request);
        return makeFeatureIdByTypeMap(parameters);
    }
    
    public static Map makeFeatureIdByTypeMap(Map parameters) {
        Map featureIdByType = new HashMap();
        if (parameters == null) return featureIdByType;
        
        Iterator parameterNameIter = parameters.keySet().iterator();
        while (parameterNameIter.hasNext()) {
            String parameterName = (String) parameterNameIter.next();
            if (parameterName.startsWith("pft_")) {
                String productFeatureTypeId = parameterName.substring(4);
                String productFeatureId = (String) parameters.get(parameterName);
                if (productFeatureId != null && productFeatureId.length() > 0) {
                    featureIdByType.put(productFeatureTypeId, productFeatureId);
                }
            }
        }
        
        return featureIdByType;
    }
    
    public static String makeFeatureIdByTypeString(Map featureIdByType) {
        if (featureIdByType == null || featureIdByType.size() == 0) {
            return "";
        }
        
        StringBuffer outSb = new StringBuffer();
        Iterator fbtIter = featureIdByType.entrySet().iterator();
        while (fbtIter.hasNext()) {
            Map.Entry entry = (Map.Entry) fbtIter.next();
            String productFeatureTypeId = (String) entry.getKey();
            String productFeatureId = (String) entry.getValue();
            outSb.append(productFeatureTypeId);
            outSb.append('=');
            outSb.append(productFeatureId);
            if (fbtIter.hasNext()) {
                outSb.append('&');
            }
        }
        
        return outSb.toString();
    }
    
    /* TODO: DEJ 20031025 delete this if not used in the near future
    public static void filterProductIdListByFeatures(List productIds, Map featureIdByType, GenericDelegator delegator) {
        if (productIds == null || productIds.size() == 0) return;
        //filter search results by features
        
        // the fun part: go through each product and make sure it has all specified features
        Iterator productIdsIter = productIds.iterator();
        while (productIdsIter.hasNext()) {
            String productId = (String) productIdsIter.next();
            
            boolean doRemove = false;
            Iterator requiredFeaturesIter = featureIdByType.values().iterator();
            while (!doRemove && requiredFeaturesIter.hasNext()) {
                String productFeatureId = (String) requiredFeaturesIter.next();
                List productFeatureAppl = null;
                try {
                    // for now only constraining by productId and productFeatureId, so any appl type will be included...
                    productFeatureAppl = delegator.findByAndCache("ProductFeatureAppl", UtilMisc.toMap("productId", productId, "productFeatureId", productFeatureId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error getting feature appls associated with the productId: [" + productId + "] and the productFeatureId [" + productFeatureId + "], removing product from search match list.", module);
                    doRemove = true;
                    continue;
                }
            
                productFeatureAppl = EntityUtil.filterByDate(productFeatureAppl, true);
                if (productFeatureAppl == null || productFeatureAppl.size() == 0) {
                    doRemove = true;
                }
            }
            
            if (doRemove) {
                productIdsIter.remove();
            } 
        }
    }
    
    public static ArrayList parametricKeywordSearch(Map featureIdByType, String keywordsString, GenericDelegator delegator, String categoryId, String visitId, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
        ArrayList productIds = KeywordSearch.productsByKeywords(keywordsString, delegator, categoryId, visitId, anyPrefix, anySuffix, isAnd);
        filterProductIdListByFeatures(productIds, featureIdByType, delegator);
        return productIds;
    }
     */

    /** Get all features associated with products and associate them with a feature group attached to the category for each feature type;
     * includes products associated with this category only, but will also associate all feature groups of sub-categories with this category, optionally calls this method for all sub-categories too
     */
    public static void attachProductFeaturesToCategory(String productCategoryId, Set productFeatureTypeIdsToExclude, GenericDelegator delegator, boolean doSubCategories, Timestamp nowTimestamp) throws GenericEntityException {
        if (nowTimestamp == null) {
            nowTimestamp = UtilDateTime.nowTimestamp();
        }

        // do sub-categories first so all feature groups will be in place
        List subCategoryList = delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("parentCategoryId", productCategoryId));
        if (doSubCategories) {
            Iterator subCategoryIter = subCategoryList.iterator();
            while (subCategoryIter.hasNext()) {
                GenericValue productCategoryRollup = (GenericValue) subCategoryIter.next();
                attachProductFeaturesToCategory(productCategoryRollup.getString("productCategoryId"), productFeatureTypeIdsToExclude, delegator, true, nowTimestamp);
            }
        }

        // now get all features for this category and make associated feature groups
        Map productFeatureIdByTypeIdSetMap = new HashMap();
        List productCategoryMemberList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId));
        Iterator productCategoryMemberIter = productCategoryMemberList.iterator();
        while (productCategoryMemberIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) productCategoryMemberIter.next();
            String productId = productCategoryMember.getString("productId");
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productId", EntityOperator.EQUALS, productId),
                    new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                    new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                    ), EntityOperator.AND);
            EntityListIterator productFeatureAndApplEli = delegator.findListIteratorByCondition("ProductFeatureAndAppl", condition, null, null);
            GenericValue productFeatureAndAppl = null;
            while ((productFeatureAndAppl = (GenericValue) productFeatureAndApplEli.next()) != null) {
                String productFeatureId = productFeatureAndAppl.getString("productFeatureId");
                String productFeatureTypeId = productFeatureAndAppl.getString("productFeatureTypeId");
                if (productFeatureTypeIdsToExclude != null && productFeatureTypeIdsToExclude.contains(productFeatureTypeId)) {
                    continue;
                }
                Set productFeatureIdSet = (Set) productFeatureIdByTypeIdSetMap.get(productFeatureTypeId);
                if (productFeatureIdSet == null) {
                    productFeatureIdSet = new HashSet();
                    productFeatureIdByTypeIdSetMap.put(productFeatureTypeId, productFeatureIdSet);
                }
                productFeatureIdSet.add(productFeatureId);
            }
        }
        
        Iterator productFeatureIdByTypeIdSetIter = productFeatureIdByTypeIdSetMap.entrySet().iterator();
        while (productFeatureIdByTypeIdSetIter.hasNext()) {
            Map.Entry entry = (Map.Entry) productFeatureIdByTypeIdSetIter.next();
            String productFeatureTypeId = (String) entry.getKey();
            Set productFeatureIdSet = (Set) entry.getValue();
            
            String productFeatureGroupId = productCategoryId + "_" + productFeatureTypeId;
            if (productFeatureGroupId.length() > 20) {
                Debug.logWarning("Manufactured productFeatureGroupId was greater than 20 characters, means that we had some long productCategoryId and/or productFeatureTypeId values, at the category part should be unique since it is first, so if the feature type isn't unique it just means more than one type of feature will go into the category...", module);
                productFeatureGroupId = productFeatureGroupId.substring(0, 20);
            }
            
            GenericValue productFeatureGroup = delegator.findByPrimaryKey("ProductFeatureGroup", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId));
            if (productFeatureGroup == null) {
                // auto-create the group
                String description = "Feature Group for type [" + productFeatureTypeId + "] features in category [" + productCategoryId + "]";
                productFeatureGroup = delegator.makeValue("ProductFeatureGroup", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "description", description));
                productFeatureGroup.create();
                
                GenericValue productFeatureCatGrpAppl = delegator.makeValue("ProductFeatureCatGrpAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "productCategoryId", productCategoryId, "fromDate", nowTimestamp));
                productFeatureCatGrpAppl.create();
            }
            
            // now put all of the features in the group, if there is not already a valid feature placement there...
            Iterator productFeatureIdIter = productFeatureIdSet.iterator();
            while (productFeatureIdIter.hasNext()) {
                String productFeatureId = (String) productFeatureIdIter.next();
                EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                        new EntityExpr("productFeatureId", EntityOperator.EQUALS, productFeatureId),
                        new EntityExpr("productFeatureGroupId", EntityOperator.EQUALS, productFeatureGroupId),
                        new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                        new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                        ), EntityOperator.AND);
                if (delegator.findCountByCondition("ProductFeatureGroupAppl", condition, null) == 0) {
                    // if no valid ones, create one
                    GenericValue productFeatureGroupAppl = delegator.makeValue("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "productFeatureId", productFeatureId, "fromDate", nowTimestamp));
                    productFeatureGroupAppl.create();
                }
            }
        }
        
        // now get all feature groups associated with sub-categories and associate them with this category
        Iterator subCategoryIter = subCategoryList.iterator();
        while (subCategoryIter.hasNext()) {
            GenericValue productCategoryRollup = (GenericValue) subCategoryIter.next();
            String subProductCategoryId = productCategoryRollup.getString("productCategoryId");
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productCategoryId", EntityOperator.EQUALS, subProductCategoryId),
                    new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                    new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                    ), EntityOperator.AND);
            EntityListIterator productFeatureCatGrpApplEli = delegator.findListIteratorByCondition("ProductFeatureCatGrpAppl", condition, null, null);
            GenericValue productFeatureCatGrpAppl = null;
            while ((productFeatureCatGrpAppl = (GenericValue) productFeatureCatGrpApplEli.next()) != null) {
                String productFeatureGroupId = productFeatureCatGrpAppl.getString("productFeatureGroupId");
                EntityCondition checkCondition = new EntityConditionList(UtilMisc.toList(
                        new EntityExpr("productCategoryId", EntityOperator.EQUALS, productCategoryId),
                        new EntityExpr("productFeatureGroupId", EntityOperator.EQUALS, productFeatureGroupId),
                        new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                        new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                        ), EntityOperator.AND);
                if (delegator.findCountByCondition("ProductFeatureCatGrpAppl", checkCondition, null) == 0) {
                    // if no valid ones, create one
                    GenericValue productFeatureGroupAppl = delegator.makeValue("ProductFeatureCatGrpAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "productCategoryId", productCategoryId, "fromDate", nowTimestamp));
                    productFeatureGroupAppl.create();
                }
                
            }
        }
    }
}
