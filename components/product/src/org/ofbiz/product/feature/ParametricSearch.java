/*
 * $Id: ParametricSearch.java,v 1.1 2003/08/17 18:04:23 ajzeneski Exp $
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.KeywordSearch;

/**
 *  Utilities for parametric search based on features.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      2.1
 */
public class ParametricSearch {
    
    public static final String module = ParametricSearch.class.getName();
    
    /** Gets all features associated with the specified category through: 
     * ProductCategory -> ProductFeatureCategoryAppl -> ProductFeatureCategory -> ProductFeature.
     * Returns a Map of Lists of ProductFeature GenericValue objects organized by productFeatureTypeId. 
     */
    public static Map makeCategoryFeatureLists(String productCategoryId, GenericDelegator delegator) {
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
                        List featuresByType = (List) productFeaturesByTypeMap.get(productFeatureTypeId);
                        if (featuresByType == null) {
                            featuresByType = new LinkedList();
                            productFeaturesByTypeMap.put(productFeatureTypeId, featuresByType);
                        }
                        featuresByType.add(productFeature);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting features associated with the category with ID: " + productCategoryId, module);
        }
        
        // now before returning, order the features in each list by description
        Iterator productFeatureTypeEntries = productFeaturesByTypeMap.entrySet().iterator();
        while (productFeatureTypeEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) productFeatureTypeEntries.next();
            List sortedFeatures = EntityUtil.orderBy((List) entry.getValue(), UtilMisc.toList("description"));
            productFeaturesByTypeMap.put(entry.getKey(), sortedFeatures);
        }
        
        return productFeaturesByTypeMap;
    }
    
    public static Map makeFeatureIdByTypeMap(ServletRequest request) {
        Map featureIdByType = new HashMap();
        
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if (parameterName.startsWith("pft_")) {
                String productFeatureTypeId = parameterName.substring(4);
                String productFeatureId = request.getParameter(parameterName);
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
    
    public static ArrayList parametricKeywordSearch(Map featureIdByType, String keywordsString, GenericDelegator delegator, String categoryId, String visitId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        ArrayList productIds = KeywordSearch.productsByKeywords(keywordsString, delegator, categoryId, visitId, anyPrefix, anySuffix, intraKeywordOperator);
        filterProductIdListByFeatures(productIds, featureIdByType, delegator);
        return productIds;
    }
}
