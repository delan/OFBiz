/*
 * $Id: ProductSearch.java,v 1.3 2003/10/17 11:34:07 jonesde Exp $
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
 * @version    $Revision: 1.3 $
 * @since      3.0
 */
public class ProductSearch {
    
    public static final String module = ProductSearch.class.getName();
    
    public static ArrayList parametricKeywordSearch(Map featureIdByType, String keywordsString, GenericDelegator delegator, String productCategoryId, String visitId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        // TODO: implement this for the new features
        Collection featureIdCol = featureIdByType.values();
        // TODO: implement the sub categories feature, using pre-query of category list, then an IN condition
        boolean includeSubCategories = true;
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
            // make index based values and increment
            String entityAlias = "PK" + index;
            String prefix = "pk" + index;
            index++;
            
            dynamicViewEntity.addMemberEntity("PCM", "ProductCategoryMember");
            dynamicViewEntity.addAlias(entityAlias, prefix + "ProductCategoryId", "productCategoryId", null, null, null, null);
            dynamicViewEntity.addAlias(entityAlias, prefix + "FromDate", "fromDate", null, null, null, null);
            dynamicViewEntity.addAlias(entityAlias, prefix + "ThruDate", "thruDate", null, null, null, null);
            dynamicViewEntity.addViewLink("PROD", "PCM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
            entityConditionList.add(new EntityExpr("pcmProductCategoryId", EntityOperator.EQUALS, productCategoryId));
            entityConditionList.add(new EntityExpr(new EntityExpr("pcmThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("pcmThruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            entityConditionList.add(new EntityExpr("pcmFromDate", EntityOperator.LESS_THAN, nowTimestamp));
        }
        
        // Keyword
        List keywordFirstPass = KeywordSearch.makeKeywordList(keywordsString);
        List keywordList = KeywordSearch.fixKeywords(keywordFirstPass, anyPrefix, anySuffix, removeStems, intraKeywordOperator);
        
        if (keywordList.size() > 0) {
            if ("OR".equals(intraKeywordOperator)) {
                // make index based values and increment
                String entityAlias = "PK" + index;
                String prefix = "pk" + index;
                index++;
                    
                dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                dynamicViewEntity.addAlias(entityAlias, prefix + "TotalWeight", "relevancyWeight", null, null, null, "sum");
                dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword");
                dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                orderByList.add("-" + prefix + "TotalWeight");
                List keywordOrList = new LinkedList();
                Iterator keywordIter = keywordList.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();
                    keywordOrList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));
                }
                entityConditionList.add(new EntityConditionList(keywordOrList, EntityOperator.OR));
                
                productIdGroupBy = true;
            } else if ("AND".equals(intraKeywordOperator)) {
                Iterator keywordIter = keywordList.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();
                    
                    // make index based values and increment
                    String entityAlias = "PK" + index;
                    String prefix = "pk" + index;
                    index++;
                    
                    dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                    // TODO: find a way to add up the relevancyWeight fields from all keyword member entities for a total to sort by, or do it on the server? nah...
                    dynamicViewEntity.addAlias(entityAlias, prefix + "RelevancyWeight", "relevancyWeight", null, null, null, null);
                    dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword");
                    dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                    entityConditionList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));
                }
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

    public static ArrayList productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, String visitId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        // TODO: implement this for the new features
        return null;
    }
    
    public static ArrayList searchProducts(ProductSearchConstraint productSearchConstraint, GenericDelegator delegator) {
        // TODO: implement this for the new features
        ProductSearchContext productSearchContext = new ProductSearchContext();
        productSearchConstraint.addConstraint(productSearchContext, delegator);
        
        return null;
    }
    
    public static class ProductSearchContext {
        public int index = 1;
        public List entityConditionList = new LinkedList();
        public List orderByList = new LinkedList();
        public List fieldsToSelect = null;
        public DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        public boolean productIdGroupBy = false;
        
        public ProductSearchContext() {
            dynamicViewEntity.addMemberEntity("PROD", "Product");
            dynamicViewEntity.addAlias("PROD", "productName");
        }
    }
    
    public static abstract class ProductSearchConstraint {
        public ProductSearchConstraint() {
        }
        
        public abstract void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator);
    }
    
    public static class ProductSearchConstraintList extends ProductSearchConstraint {
        protected List productSearchConstraints = new LinkedList();
        
        public ProductSearchConstraintList(List productSearchConstraints) {
            this.productSearchConstraints.addAll(productSearchConstraints);
        }
        
        public void addProductSearchConstraint(ProductSearchConstraint productSearchConstraint) {
            this.productSearchConstraints.add(productSearchConstraint);
        }
        
        public void addConstraint(ProductSearchContext productSearchContext, GenericDelegator delegator) {
            // TODO: implement ProductSearchConstraintList makeEntityCondition
        }
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
        protected String intraKeywordOperator;
        
        public KeywordConstraint(String keywordsString, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
            this.keywordsString = keywordsString;
            this.anyPrefix = anyPrefix;
            this.anySuffix = anySuffix;
            this.intraKeywordOperator = intraKeywordOperator.toUpperCase();
            if (this.intraKeywordOperator == null || (!"AND".equals(this.intraKeywordOperator) && !"OR".equals(this.intraKeywordOperator))) {
                Debug.logWarning("intraKeywordOperator [" + this.intraKeywordOperator + "] was not valid, defaulting to OR", module);
                this.intraKeywordOperator = "OR";
            }
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
            // TODO: implement FeatureConstraint makeEntityCondition
        }
    }
}
