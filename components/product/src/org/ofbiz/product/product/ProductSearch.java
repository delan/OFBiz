/*
 * $Id: ProductSearch.java,v 1.6 2003/10/18 05:13:07 jonesde Exp $
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
package org.ofbiz.product.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Timestamp;

import javax.servlet.ServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.product.product.KeywordSearch;

/**
 *  Utilities for product search based on various constraints including categories, features and keywords.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      3.0
 */
public class ProductSearch {
    
    public static final String module = ProductSearch.class.getName();
    
    /** This cache contains a Set with the IDs of the entire sub-category tree, including the current productCategoryId */
    public static UtilCache subCategoryCache = new UtilCache("product.SubCategory", 0, 0, true);
    
    public static void getAllSubCategoryIds(String productCategoryId, Set productCategoryIdSet, GenericDelegator delegator, Timestamp nowTimestamp) {
        // TODO: cache the sub-category so this will run faster
        Set subCategoryIdSet = (Set) subCategoryCache.get(productCategoryId);
        if (subCategoryIdSet == null) {
            synchronized (ProductSearch.class) {
                subCategoryIdSet = (Set) subCategoryCache.get(productCategoryId);
                if (subCategoryIdSet == null) {
                    subCategoryIdSet = new HashSet();
                    
                    // first make sure the current category id is in the Set
                    subCategoryIdSet.add(productCategoryId);

                    // now find all sub-categories, filtered by effective dates, and call this routine for them
                    List conditions = new LinkedList();
                    conditions.add(new EntityExpr("parentProductCategoryId", EntityOperator.EQUALS, productCategoryId));
                    conditions.add(new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                    conditions.add(new EntityExpr("fromDate", EntityOperator.LESS_THAN, nowTimestamp));
                    try {
                        List productCategoryRollupList = delegator.findByCondition("ProductCategoryRollup", new EntityConditionList(conditions, EntityOperator.AND), null, null);
                        Iterator productCategoryRollupIter = productCategoryRollupList.iterator();
                        while (productCategoryRollupIter.hasNext()) {
                            GenericValue productCategoryRollup = (GenericValue) productCategoryRollupIter.next();
                            getAllSubCategoryIds(productCategoryRollup.getString("productCategoryId"), subCategoryIdSet, delegator, nowTimestamp);
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Error finding sub-categories for product search", module);
                    }
                    
                    subCategoryCache.put(productCategoryId, subCategoryIdSet);
                }
            }
        }
        if (subCategoryIdSet != null) {
            productCategoryIdSet.addAll(subCategoryIdSet);
        }
    }
    
    public static ArrayList parametricKeywordSearch(Map featureIdByType, String keywordsString, GenericDelegator delegator, String productCategoryId, boolean includeSubCategories, String visitId, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
        // TODO: implement this for the new features
        Collection featureIdCol = featureIdByType == null ? null : featureIdByType.values();
        boolean removeStems = UtilProperties.propertyValueEquals("prodsearch", "remove.stems", "true");
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        // make view-entity & EntityCondition
        int index = 1;
        List entityConditionList = new LinkedList();
        List orderByList = new LinkedList();
        //List fieldsToSelect = UtilMisc.toList("productId");
        List fieldsToSelect = null;
        DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        dynamicViewEntity.addMemberEntity("PROD", "Product");
        dynamicViewEntity.addAlias("PROD", "productName");
        boolean productIdGroupBy = false;
        
        // Category
        if (productCategoryId != null && productCategoryId.length() > 0) {
            List productCategoryIdList = null;
            if (includeSubCategories) {
                // find all sub-categories recursively, make a Set of productCategoryId
                Set productCategoryIdSet = new HashSet();
                getAllSubCategoryIds(productCategoryId, productCategoryIdSet, delegator, nowTimestamp);
                productCategoryIdList = new ArrayList(productCategoryIdSet);
            } else {
                productCategoryIdList = UtilMisc.toList(productCategoryId);
            }
            
            // make index based values and increment
            String entityAlias = "PK" + index;
            String prefix = "pk" + index;
            index++;
            
            dynamicViewEntity.addMemberEntity("PCM", "ProductCategoryMember");
            dynamicViewEntity.addAlias(entityAlias, prefix + "ProductCategoryId", "productCategoryId", null, null, null, null);
            dynamicViewEntity.addAlias(entityAlias, prefix + "FromDate", "fromDate", null, null, null, null);
            dynamicViewEntity.addAlias(entityAlias, prefix + "ThruDate", "thruDate", null, null, null, null);
            dynamicViewEntity.addViewLink("PROD", "PCM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
            entityConditionList.add(new EntityExpr(prefix + "ProductCategoryId", EntityOperator.IN, productCategoryIdList));
            entityConditionList.add(new EntityExpr(new EntityExpr(prefix + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr(prefix + "ThruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            entityConditionList.add(new EntityExpr(prefix + "FromDate", EntityOperator.LESS_THAN, nowTimestamp));
        }
        
        // Keyword
        List keywordFirstPass = KeywordSearch.makeKeywordList(keywordsString);
        List keywordList = KeywordSearch.fixKeywords(keywordFirstPass, anyPrefix, anySuffix, removeStems, isAnd);
        
        if (keywordList.size() > 0) {
            if (isAnd) {
                // TODO: find a way to add up the relevancyWeight fields from all keyword member entities for a total to sort by, or do it on the server? nah...
                Iterator keywordIter = keywordList.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();
                    
                    // make index based values and increment
                    String entityAlias = "PK" + index;
                    String prefix = "pk" + index;
                    index++;
                    
                    dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                    dynamicViewEntity.addAlias(entityAlias, prefix + "RelevancyWeight", "relevancyWeight", null, null, null, null);
                    dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword", "keyword", null, null, null, null);
                    dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                    entityConditionList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));
                }
            } else {
                // make index based values and increment
                String entityAlias = "PK" + index;
                String prefix = "pk" + index;
                index++;
                    
                dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                dynamicViewEntity.addAlias(entityAlias, prefix + "TotalRelevancy", "relevancyWeight", null, null, null, "sum");
                dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword", "keyword", null, null, null, null);
                dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                orderByList.add("-" + prefix + "TotalRelevancy");
                List keywordOrList = new LinkedList();
                Iterator keywordIter = keywordList.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();
                    keywordOrList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));
                }
                entityConditionList.add(new EntityConditionList(keywordOrList, EntityOperator.OR));
                
                productIdGroupBy = true;
            }
        }
        
        // Features
        if (featureIdCol != null && featureIdCol.size() > 0) {
            Iterator featureIdIter = featureIdCol.iterator();
            while (featureIdIter.hasNext()) {
                String productFeatureId = (String) featureIdIter.next();
                
                // make index based values and increment
                String entityAlias = "PFA" + index;
                String prefix = "pfa" + index;
                index++;
                    
                dynamicViewEntity.addMemberEntity(entityAlias, "ProductCategoryMember");
                dynamicViewEntity.addAlias(entityAlias, prefix + "ProductFeatureId", "productFeatureId", null, null, null, null);
                dynamicViewEntity.addAlias(entityAlias, prefix + "FromDate", "fromDate", null, null, null, null);
                dynamicViewEntity.addAlias(entityAlias, prefix + "ThruDate", "thruDate", null, null, null, null);
                dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                entityConditionList.add(new EntityExpr(prefix + "ProductFeatureId", EntityOperator.EQUALS, productFeatureId));
                entityConditionList.add(new EntityExpr(new EntityExpr(prefix + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr(prefix + "ThruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                entityConditionList.add(new EntityExpr(prefix + "FromDate", EntityOperator.LESS_THAN, nowTimestamp));
            }
        }
        
        dynamicViewEntity.addAlias("PROD", "productId", null, null, null, new Boolean(productIdGroupBy), null);
        EntityCondition whereCondition = new EntityConditionList(entityConditionList, EntityOperator.AND);
        
        EntityListIterator eli = null;
        try {
            eli = delegator.findListIteratorByCondition(dynamicViewEntity, whereCondition, null, fieldsToSelect, orderByList, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error in product search", module);
            return null;
        }

        ArrayList productIds = new ArrayList(100);
        Set productIdSet = new HashSet();
        GenericValue searchResult = null;
        while ((searchResult = (GenericValue) eli.next()) != null) {
            String productId = searchResult.getString("productId");
            if (!productIdSet.contains(productId)) {
                productIds.add(productId);
                productIdSet.add(productId);
            }
        }
/* TODO: store results of search
            if (Debug.infoOn()) Debug.logInfo("[KeywordSearch] got " + pbkList.size() + " results found for search string: [" + keywordsString + "], keyword combine operator is " + intraKeywordOperator + ", categoryId=" + categoryId + ", anyPrefix=" + anyPrefix + ", anySuffix=" + anySuffix + ", removeStems=" + removeStems, module);
            //if (Debug.infoOn()) Debug.logInfo("pbkList=" + pbkList, module);

            try {
                GenericValue productKeywordResult = delegator.makeValue("ProductKeywordResult", null);
                Long nextPkrSeqId = delegator.getNextSeqId("ProductKeywordResult");

                productKeywordResult.set("productKeywordResultId", nextPkrSeqId.toString());
                productKeywordResult.set("visitId", visitId);
                if (useCategory) productKeywordResult.set("productCategoryId", categoryId);
                productKeywordResult.set("searchString", keywordsString);
                productKeywordResult.set("intraKeywordOperator", intraKeywordOperator);
                productKeywordResult.set("anyPrefix", new Boolean(anyPrefix));
                productKeywordResult.set("anySuffix", new Boolean(anySuffix));
                productKeywordResult.set("removeStems", new Boolean(removeStems));
                productKeywordResult.set("numResults", new Long(pbkList.size()));
                productKeywordResult.create();
            } catch (Exception e) {
                Debug.logError(e, "Error saving keyword result stats", module);
                Debug.logError("[KeywordSearch] Stats are: got " + pbkList.size() + " results found for search string: [" + keywordsString + "], keyword combine operator is " + intraKeywordOperator + ", categoryId=" + categoryId + ", anyPrefix=" + anyPrefix + ", anySuffix=" + anySuffix + ", removeStems=" + removeStems, module);
            }
 */
        return productIds;
    }
    
        // AND EXAMPLE:
        // SELECT DISTINCT P1.PRODUCT_ID, (P1.RELEVANCY_WEIGHT + P2.RELEVANCY_WEIGHT + P3.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3
        // WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD = '1003027' ORDER BY TOTAL_WEIGHT DESC
        // AND EXAMPLE WITH CATEGORY CONSTRAINT:
        // SELECT DISTINCT P1.PRODUCT_ID, PCM.SEQUENCE_NUM AS CAT_SEQ_NUM, TOTAL_WEIGHT = P1.RELEVANCY_WEIGHT + P2.RELEVANCY_WEIGHT + P3.RELEVANCY_WEIGHT FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3, PRODUCT_CATEGORY_MEMBER PCM
        // WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD = '1003027' AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo' AND (PCM.THRU_DATE IS NULL OR PCM.THRU_DATE > ?) ORDER BY CAT_SEQ_NUM, TOTAL_WEIGHT DESC

        // ORs are a little more complicated, so get individual results group them by PRODUCT_ID and sum the RELEVANCY_WEIGHT
        // OR EXAMPLE:
        // SELECT DISTINCT P1.PRODUCT_ID, SUM(P1.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT FROM PRODUCT_KEYWORD P1
        // WHERE (P1.KEYWORD LIKE 'TI%' OR P1.KEYWORD LIKE 'HOUS%' OR P1.KEYWORD = '1003027') GROUP BY P1.PRODUCT_ID ORDER BY TOTAL_WEIGHT DESC
        // OR EXAMPLE WITH CATEGORY CONSTRAINT:
        // SELECT DISTINCT P1.PRODUCT_ID, MIN(PCM.SEQUENCE_NUM) AS CAT_SEQ_NUM, TOTAL_WEIGHT = SUM(P1.RELEVANCY_WEIGHT) FROM PRODUCT_KEYWORD P1, PRODUCT_CATEGORY_MEMBER PCM
        // WHERE (P1.KEYWORD LIKE 'TI%' OR P1.KEYWORD LIKE 'HOUS%' OR P1.KEYWORD = '1003027') AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo' AND (PCM.THRU_DATE IS NULL OR PCM.THRU_DATE > ?) GROUP BY P1.PRODUCT_ID ORDER BY CAT_SEQ_NUM, TOTAL_WEIGHT DESC

    public static ArrayList searchProducts(List productSearchConstraintList, GenericDelegator delegator, String visitId) {
        // TODO: implement this for the new features
        ProductSearchContext productSearchContext = new ProductSearchContext();
        
        // TODO: Get all Keyword constraints and do them together, otherwise the relevancy sort gets pretty confusing
        
        // TODO: Go through the rest of the constraints and add them in
        
        //productSearchConstraint.addConstraint(productSearchContext, delegator);
        
        // TODO: apply the sort order
        
        // TODO: do the query
        
        // TODO: store info about results in the database, attached to the user's visitId, if specified
        
        return null;
    }
    
    public static class ProductSearchContext {
        public int index = 1;
        public List entityConditionList = new LinkedList();
        public List orderByList = new LinkedList();
        public List fieldsToSelect = null;
        public DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        public boolean productIdGroupBy = false;
        public boolean includedKeywordSearch = false;
        
        public ProductSearchContext() {
            dynamicViewEntity.addMemberEntity("PROD", "Product");
            dynamicViewEntity.addAlias("PROD", "productName");
        }
    }
    
    // ======================================================================
    // Search Constraint Classes
    // ======================================================================
    
    public static abstract class ProductSearchConstraint {
        public ProductSearchConstraint() {
        }
        
        public abstract void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator);
    }
    
    public static class CategoryConstraint extends ProductSearchConstraint {
        protected String productCategoryId;
        protected boolean includeSubCategories;
        
        public CategoryConstraint(String productCategoryId, boolean includeSubCategories) {
            this.productCategoryId = productCategoryId;
            this.includeSubCategories = includeSubCategories;
        }
        
        public void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement CategoryConstraint makeEntityCondition
        }
    }
    
    public static class FeatureConstraint extends ProductSearchConstraint {
        protected String productFeatureId;
        
        public FeatureConstraint(String productFeatureId) {
            this.productFeatureId = productFeatureId;
        }
        
        public void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement FeatureConstraint makeEntityCondition
        }
    }
    
    public static class KeywordConstraint extends ProductSearchConstraint {
        protected String keywordsString;
        protected boolean anyPrefix;
        protected boolean anySuffix;
        protected boolean isAnd;
        
        public KeywordConstraint(String keywordsString, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
            this.keywordsString = keywordsString;
            this.anyPrefix = anyPrefix;
            this.anySuffix = anySuffix;
            this.isAnd = isAnd;
        }
        
        public void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement KeywordConstraint makeEntityCondition
        }
    }
    
    public static class LastUpdatedRangeConstraint extends ProductSearchConstraint {
        protected Timestamp fromDate;
        protected Timestamp thruDate;
        
        public LastUpdatedRangeConstraint(Timestamp fromDate, Timestamp thruDate) {
            this.fromDate = fromDate;
            this.thruDate = thruDate;
        }
        
        public void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement LastUpdatedRangeConstraint makeEntityCondition
        }
    }

    public static class ListPriceRangeConstraint extends ProductSearchConstraint {
        protected Double lowPrice;
        protected Double highPrice;
        
        public ListPriceRangeConstraint(Double lowPrice, Double highPrice) {
            this.lowPrice = lowPrice;
            this.highPrice = highPrice;
        }
        
        public void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement ListPriceRangeConstraint makeEntityCondition
        }
    }

    // ======================================================================
    // Result Sort Classes
    // ======================================================================
    
    public static abstract class ResultSortOrder {
        public ResultSortOrder() {
        }

        public abstract void setSortOrder(ProductSearchContext productSearchContext, GenericDelegator delegator);
    }
    
    public static class SortKeywordRelevancy extends ResultSortOrder {
        public SortKeywordRelevancy() {
        }

        public void setSortOrder(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement SortKeywordRelevancy
        }
    }
    
    public static class SortProductName extends ResultSortOrder {
        public SortProductName() {
        }

        public void setSortOrder(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement SortProductName
        }
    }
    
    public static class SortLastUpdateDate extends ResultSortOrder {
        protected boolean ascending;
        public SortLastUpdateDate(boolean ascending) {
            this.ascending = ascending;
        }

        public void setSortOrder(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement SortLastUpdateDate
        }
    }
    
    public static class SortListPrice extends ResultSortOrder {
        protected boolean ascending;
        public SortListPrice(boolean ascending) {
            this.ascending = ascending;
        }

        public void setSortOrder(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement SortListPrice
        }
    }
    
    public static class SortMostOrdered extends ResultSortOrder {
        public SortMostOrdered(boolean ascending) {
        }

        public void setSortOrder(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement SortMostOrdered
        }
    }
}
