/*
 * $Id: ProductSearch.java,v 1.24 2004/01/19 12:53:04 jonesde Exp $
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelViewEntity.ComplexAlias;
import org.ofbiz.entity.model.ModelViewEntity.ComplexAliasField;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;


/**
 *  Utilities for product search based on various constraints including categories, features and keywords.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.24 $
 * @since      3.0
 */
public class ProductSearch {

    public static final String module = ProductSearch.class.getName();

    public static ArrayList parametricKeywordSearch(Map featureIdByType, String keywordsString, GenericDelegator delegator, String productCategoryId, String visitId, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
        Set featureIdSet = new HashSet();
        if (featureIdByType != null) {
            featureIdSet.addAll(featureIdByType.values());
        }

        return parametricKeywordSearch(featureIdSet, keywordsString, delegator, productCategoryId, true, visitId, anyPrefix, anySuffix, isAnd);
    }

    public static ArrayList parametricKeywordSearch(Set featureIdSet, String keywordsString, GenericDelegator delegator, String productCategoryId, boolean includeSubCategories, String visitId, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
        List productSearchConstraintList = new LinkedList();

        if (UtilValidate.isNotEmpty(productCategoryId)) {
            productSearchConstraintList.add(new CategoryConstraint(productCategoryId, includeSubCategories));
        }

        if (UtilValidate.isNotEmpty(keywordsString)) {
            productSearchConstraintList.add(new KeywordConstraint(keywordsString, anyPrefix, anySuffix, null, isAnd));
        }

        if (featureIdSet != null && featureIdSet.size() > 0) {
            Iterator featureIdIter = featureIdSet.iterator();
            while (featureIdIter.hasNext()) {
                String productFeatureId = (String) featureIdIter.next();
                productSearchConstraintList.add(new FeatureConstraint(productFeatureId));
            }
        }

        return searchProducts(productSearchConstraintList, new SortKeywordRelevancy(), delegator, visitId);
    }

    public static ArrayList searchProducts(List productSearchConstraintList, ResultSortOrder resultSortOrder, GenericDelegator delegator, String visitId) {
        ProductSearchContext productSearchContext = new ProductSearchContext(delegator, visitId);

        productSearchContext.addProductSearchConstraints(productSearchConstraintList);
        productSearchContext.setResultSortOrder(resultSortOrder);

        ArrayList productIds = productSearchContext.doSearch();
        return productIds;
    }

    public static void getAllSubCategoryIds(String productCategoryId, Set productCategoryIdSet, GenericDelegator delegator, Timestamp nowTimestamp) {
        if (nowTimestamp == null) {
            nowTimestamp = UtilDateTime.nowTimestamp();
        }

        // this will use the GenericDelegator cache as much as possible, but not a dedicated cache because it would get stale to easily and is too much of a pain to maintain in development and production

        // first make sure the current category id is in the Set
        productCategoryIdSet.add(productCategoryId);

        // now find all sub-categories, filtered by effective dates, and call this routine for them
        try {
            List productCategoryRollupList = delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId", productCategoryId));
            Iterator productCategoryRollupIter = productCategoryRollupList.iterator();
            while (productCategoryRollupIter.hasNext()) {
                GenericValue productCategoryRollup = (GenericValue) productCategoryRollupIter.next();

                String subProductCategoryId = productCategoryRollup.getString("productCategoryId");
                if (productCategoryIdSet.contains(subProductCategoryId)) {
                    // if this category has already been traversed, no use doing it again; this will also avoid infinite loops
                    continue;
                }

                // do the date filtering in the loop to avoid looping through the list twice
                if (EntityUtil.isValueActive(productCategoryRollup, nowTimestamp)) {
                    getAllSubCategoryIds(subProductCategoryId, productCategoryIdSet, delegator, nowTimestamp);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding sub-categories for product search", module);
        }
    }

    public static class ProductSearchContext {
        public int index = 1;
        public List entityConditionList = new LinkedList();
        public List orderByList = new LinkedList();
        public List fieldsToSelect = UtilMisc.toList("productId");
        public DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        public boolean productIdGroupBy = false;
        public boolean includedKeywordSearch = false;
        public Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        public List keywordFixedOrSetAndList = new LinkedList();
        public Set orKeywordFixedSet = new HashSet();
        public Set andKeywordFixedSet = new HashSet();
        public List productSearchConstraintList = new LinkedList();
        public ResultSortOrder resultSortOrder = null;
        public Integer resultOffset = null;
        public Integer maxResults = null;
        protected GenericDelegator delegator = null;
        protected String visitId = null;
        protected Integer totalResults = null;

        public ProductSearchContext(GenericDelegator delegator, String visitId) {
            this.delegator = delegator;
            this.visitId = visitId;
            dynamicViewEntity.addMemberEntity("PROD", "Product");
        }

        public GenericDelegator getDelegator() {
            return this.delegator;
        }

        public void addProductSearchConstraints(List productSearchConstraintList) {
            // Go through the constraints and add them in
            Iterator productSearchConstraintIter = productSearchConstraintList.iterator();
            while (productSearchConstraintIter.hasNext()) {
                ProductSearchConstraint constraint = (ProductSearchConstraint) productSearchConstraintIter.next();
                constraint.addConstraint(this);
            }
        }

        public void setResultSortOrder(ResultSortOrder resultSortOrder) {
            this.resultSortOrder = resultSortOrder;
        }

        public void setResultOffset(Integer resultOffset) {
            this.resultOffset = resultOffset;
        }

        public void setMaxResults(Integer maxResults) {
            this.maxResults = maxResults;
        }

        public Integer getTotalResults() {
            return this.totalResults;
        }

        public ArrayList doSearch() {
            long startMillis = System.currentTimeMillis();

            // do the query
            EntityListIterator eli = this.doQuery(delegator);
            ArrayList productIds = this.makeProductIdList(eli);

            long endMillis = System.currentTimeMillis();
            double totalMillis = ((double)endMillis - (double)startMillis)/1000.0;

            // store info about results in the database, attached to the user's visitId, if specified
            this.saveSearchResultInfo(new Long(productIds.size()), new Double(totalMillis));

            return productIds;
        }

        public void finishKeywordConstraints() {
            if (orKeywordFixedSet.size() == 0 && andKeywordFixedSet.size() == 0 && keywordFixedOrSetAndList.size() == 0) {
                return;
            }

            // we know we have a keyword search to do, so keep track of that now...
            this.includedKeywordSearch = true;

            // if there is anything in the orKeywordFixedSet add it to the keywordFixedOrSetAndList
            if (orKeywordFixedSet.size() > 0) {
                // put in keywordFixedOrSetAndList to process with other or lists where at least one is required
                keywordFixedOrSetAndList.add(orKeywordFixedSet);
            }

            // remove all or sets from the or set and list where the or set is size 1 and put them in the and list
            Iterator keywordFixedOrSetAndTestIter = keywordFixedOrSetAndList.iterator();
            while (keywordFixedOrSetAndTestIter.hasNext()) {
                Set keywordFixedOrSet = (Set) keywordFixedOrSetAndTestIter.next();
                if (keywordFixedOrSet.size() == 0) {
                    keywordFixedOrSetAndTestIter.remove();
                } else if (keywordFixedOrSet.size() == 1) {
                    // treat it as just another and
                    andKeywordFixedSet.add(keywordFixedOrSet.iterator().next());
                    keywordFixedOrSetAndTestIter.remove();
                }
            }

            boolean doingBothAndOr = (keywordFixedOrSetAndList.size() > 1) || (keywordFixedOrSetAndList.size() > 0 && andKeywordFixedSet.size() > 0);

            Debug.logInfo("Finished initial setup of keywords, doingBothAndOr=" + doingBothAndOr + ", andKeywordFixedSet=" + andKeywordFixedSet + "\n keywordFixedOrSetAndList=" + keywordFixedOrSetAndList, module);

            ComplexAlias relevancyComplexAlias = new ComplexAlias("+");
            if (andKeywordFixedSet.size() > 0) {
                // add up the relevancyWeight fields from all keyword member entities for a total to sort by

                Iterator keywordIter = andKeywordFixedSet.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();

                    // make index based values and increment
                    String entityAlias = "PK" + index;
                    String prefix = "pk" + index;
                    index++;

                    dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                    dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword", "keyword", null, null, null, null);
                    dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                    entityConditionList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));

                    //don't add an alias for this, will be part of a complex alias: dynamicViewEntity.addAlias(entityAlias, prefix + "RelevancyWeight", "relevancyWeight", null, null, null, null);
                    relevancyComplexAlias.addComplexAliasMember(new ComplexAliasField(entityAlias, "relevancyWeight"));
                }

                if (!doingBothAndOr) {
                    dynamicViewEntity.addAlias(null, "totalRelevancy", null, null, null, null, null, relevancyComplexAlias);
                }
            }
            if (keywordFixedOrSetAndList.size() > 0) {
                Iterator keywordFixedOrSetAndIter = keywordFixedOrSetAndList.iterator();
                while (keywordFixedOrSetAndIter.hasNext()) {
                    Set keywordFixedOrSet = (Set) keywordFixedOrSetAndIter.next();
                    // make index based values and increment
                    String entityAlias = "PK" + index;
                    String prefix = "pk" + index;
                    index++;

                    dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                    dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword", "keyword", null, null, null, null);
                    dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                    List keywordOrList = new LinkedList();
                    Iterator keywordIter = keywordFixedOrSet.iterator();
                    while (keywordIter.hasNext()) {
                        String keyword = (String) keywordIter.next();
                        keywordOrList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));
                    }
                    entityConditionList.add(new EntityConditionList(keywordOrList, EntityOperator.OR));

                    productIdGroupBy = true;

                    if (doingBothAndOr) {
                        relevancyComplexAlias.addComplexAliasMember(new ComplexAliasField(entityAlias, "relevancyWeight", "sum"));
                    } else {
                        dynamicViewEntity.addAlias(entityAlias, "totalRelevancy", "relevancyWeight", null, null, null, "sum");
                    }
                }
            }

            if (doingBothAndOr) {
                dynamicViewEntity.addAlias(null, "totalRelevancy", null, null, null, null, null, relevancyComplexAlias);
            }
        }

        public EntityListIterator doQuery(GenericDelegator delegator) {
            // handle the now assembled or and and keyword fixed lists
            this.finishKeywordConstraints();

            if (resultSortOrder != null) {
                resultSortOrder.setSortOrder(this);
            }

            dynamicViewEntity.addAlias("PROD", "productId", null, null, null, new Boolean(productIdGroupBy), null);
            EntityCondition whereCondition = new EntityConditionList(entityConditionList, EntityOperator.AND);
            EntityFindOptions efo = new EntityFindOptions();
            efo.setDistinct(true);
            efo.setResultSetType(EntityFindOptions.TYPE_SCROLL_INSENSITIVE);

            EntityListIterator eli = null;
            try {
                eli = delegator.findListIteratorByCondition(dynamicViewEntity, whereCondition, null, fieldsToSelect, orderByList, efo);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error in product search", module);
                return null;
            }

            return eli;
        }

        public ArrayList makeProductIdList(EntityListIterator eli) {
            ArrayList productIds = new ArrayList(maxResults == null ? 100 : maxResults.intValue());

            try {
                boolean hasResults = false;
                Object initialResult = null;
                
                /* this method has been replaced by the following to address issue with SAP DB and possibly other DBs
                if (resultOffset != null) {
                    Debug.logInfo("Before relative, current index=" + eli.currentIndex(), module);
                    hasResults = eli.relative(resultOffset.intValue());
                } else {
                    initialResult = eli.next();
                    if (initialResult != null) {
                        hasResults = true;
                    }
                }
                 */

                initialResult = eli.next();
                if (initialResult != null) {
                    hasResults = true;
                }
                if (resultOffset != null && resultOffset.intValue() > 1) {
                    Debug.logInfo("Before relative, current index=" + eli.currentIndex(), module);
                    hasResults = eli.relative(resultOffset.intValue() - 1);
                    initialResult = null;
                }
                
                // get the first as the current one
                GenericValue searchResult = null;
                if (hasResults) {
                    if (initialResult != null) {
                        searchResult = (GenericValue) initialResult;
                    } else {
                        searchResult = eli.currentGenericValue();
                    }
                }

                if (searchResult == null) {
                    // nothing to get...
                    int failTotal = 0;
                    if (this.resultOffset != null) {
                        failTotal = this.resultOffset.intValue() - 1;
                    }
                    this.totalResults = new Integer(failTotal);
                    return productIds;
                }
                productIds.add(searchResult.getString("productId"));

                // init numRetreived to one since we have already grabbed the initial one
                int numRetreived = 1;
                int duplicatesFound = 0;

                Set productIdSet = new HashSet();
                while (((searchResult = (GenericValue) eli.next()) != null) && (maxResults == null || numRetreived < maxResults.intValue())) {
                    String productId = searchResult.getString("productId");
                    if (!productIdSet.contains(productId)) {
                        productIds.add(productId);
                        productIdSet.add(productId);
                        numRetreived++;
                    } else {
                        duplicatesFound++;
                    }
                    
                    /*
                    StringBuffer lineMsg = new StringBuffer("Got search result line: ");
                    Iterator fieldsToSelectIter = fieldsToSelect.iterator();
                    while (fieldsToSelectIter.hasNext()) {
                        String fieldName = (String) fieldsToSelectIter.next();
                        lineMsg.append(fieldName);
                        lineMsg.append("=");
                        lineMsg.append(searchResult.get(fieldName));
                        if (fieldsToSelectIter.hasNext()) {
                            lineMsg.append(", ");
                        }
                    }
                    Debug.logInfo(lineMsg.toString(), module);
                    */
                }

                if (searchResult != null) {
                    // we weren't at the end, so go to the end and get the index
                    //Debug.logInfo("Getting totalResults from ending index - before last() currentIndex=" + eli.currentIndex(), module);
                    if (eli.last()) {
                        this.totalResults = new Integer(eli.currentIndex());
                        //Debug.logInfo("Getting totalResults from ending index - after last() currentIndex=" + eli.currentIndex(), module);
                    }
                }
                if (this.totalResults == null || this.totalResults.intValue() == 0) {
                    int total = numRetreived;
                    if (this.resultOffset != null) {
                        total += (this.resultOffset.intValue() - 1);
                    }
                    this.totalResults = new Integer(total);
                }

                Debug.logInfo("Got search values, numRetreived=" + numRetreived + ", totalResults=" + totalResults + ", maxResults=" + maxResults + ", resultOffset=" + resultOffset + ", duplicatesFound(in the current results)=" + duplicatesFound, module);

            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting results from the product search query", module);
            }
            return productIds;
        }

        public void saveSearchResultInfo(Long numResults, Double secondsTotal) {
            // uses entities: ProductSearchResult and ProductSearchConstraint

            try {
                // make sure this is in a transaction
                boolean beganTransaction = TransactionUtil.begin();

                try {

                    GenericValue productSearchResult = delegator.makeValue("ProductSearchResult", null);
                    Long nextPkrSeqId = delegator.getNextSeqId("ProductSearchResult");
                    String productSearchResultId = nextPkrSeqId.toString();

                    productSearchResult.set("productSearchResultId", productSearchResultId);
                    productSearchResult.set("visitId", this.visitId);
                    productSearchResult.set("orderByName", this.resultSortOrder.getOrderName());
                    productSearchResult.set("isAscending", this.resultSortOrder.isAscending() ? "Y" : "N");
                    productSearchResult.set("numResults", numResults);
                    productSearchResult.set("secondsTotal", secondsTotal);
                    productSearchResult.set("searchDate", nowTimestamp);
                    productSearchResult.create();

                    Iterator productSearchConstraintIter = productSearchConstraintList.iterator();
                    int seqId = 1;
                    while (productSearchConstraintIter.hasNext()) {
                        GenericValue productSearchConstraint = (GenericValue) productSearchConstraintIter.next();
                        productSearchConstraint.set("productSearchResultId", productSearchResultId);
                        productSearchConstraint.set("constraintSeqId", Integer.toString(seqId));
                        productSearchConstraint.create();
                        seqId++;
                    }

                    TransactionUtil.commit(beganTransaction);
                } catch (GenericEntityException e1) {
                    TransactionUtil.rollback(beganTransaction);
                    Debug.logError(e1, "Error saving product search result info/stats", module);
                }
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Error saving product search result info/stats", module);
            }
        }
    }

    // ======================================================================
    // Search Constraint Classes
    // ======================================================================

    public static abstract class ProductSearchConstraint {
        public ProductSearchConstraint() { }

        public abstract void addConstraint(ProductSearchContext productSearchContext);
        /** pretty print for log messages and even UI stuff */
        public abstract String prettyPrintConstraint(GenericDelegator delegator, boolean detailed);
    }

    public static class CategoryConstraint extends ProductSearchConstraint {
        public static final String constraintName = "Category";
        protected String productCategoryId;
        protected boolean includeSubCategories;

        public CategoryConstraint(String productCategoryId, boolean includeSubCategories) {
            this.productCategoryId = productCategoryId;
            this.includeSubCategories = includeSubCategories;
        }

        public void addConstraint(ProductSearchContext productSearchContext) {
            List productCategoryIdList = null;
            if (includeSubCategories) {
                // find all sub-categories recursively, make a Set of productCategoryId
                Set productCategoryIdSet = new HashSet();
                ProductSearch.getAllSubCategoryIds(productCategoryId, productCategoryIdSet, productSearchContext.getDelegator(), productSearchContext.nowTimestamp);
                productCategoryIdList = new ArrayList(productCategoryIdSet);
            } else {
                productCategoryIdList = UtilMisc.toList(productCategoryId);
            }

            // make index based values and increment
            String entityAlias = "PCM" + productSearchContext.index;
            String prefix = "pcm" + productSearchContext.index;
            productSearchContext.index++;

            productSearchContext.dynamicViewEntity.addMemberEntity(entityAlias, "ProductCategoryMember");
            productSearchContext.dynamicViewEntity.addAlias(entityAlias, prefix + "ProductCategoryId", "productCategoryId", null, null, null, null);
            productSearchContext.dynamicViewEntity.addAlias(entityAlias, prefix + "FromDate", "fromDate", null, null, null, null);
            productSearchContext.dynamicViewEntity.addAlias(entityAlias, prefix + "ThruDate", "thruDate", null, null, null, null);
            productSearchContext.dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
            productSearchContext.entityConditionList.add(new EntityExpr(prefix + "ProductCategoryId", EntityOperator.IN, productCategoryIdList));
            productSearchContext.entityConditionList.add(new EntityExpr(new EntityExpr(prefix + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr(prefix + "ThruDate", EntityOperator.GREATER_THAN, productSearchContext.nowTimestamp)));
            productSearchContext.entityConditionList.add(new EntityExpr(prefix + "FromDate", EntityOperator.LESS_THAN, productSearchContext.nowTimestamp));

            // add in productSearchConstraint, don't worry about the productSearchResultId or constraintSeqId, those will be fill in later
            productSearchContext.productSearchConstraintList.add(productSearchContext.getDelegator().makeValue("ProductSearchConstraint", UtilMisc.toMap("constraintName", constraintName, "infoString", this.productCategoryId, "includeSubCategories", this.includeSubCategories ? "Y" : "N")));
        }

        /** pretty print for log messages and even UI stuff */
        public String prettyPrintConstraint(GenericDelegator delegator, boolean detailed) {
            GenericValue productCategory = null;
            try {
                productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error finding ProductCategory information for constraint pretty print", module);
            }
            StringBuffer ppBuf = new StringBuffer();
            ppBuf.append("Category: ");
            if (productCategory != null) {
                ppBuf.append(productCategory.getString("description"));
            }
            if (productCategory == null || detailed) {
                ppBuf.append(" [");
                ppBuf.append(productCategoryId);
                ppBuf.append("]");
            }
            if (includeSubCategories) {
                ppBuf.append(" (and all sub-categories)");
            }
            return ppBuf.toString();
        }

        public boolean equals(Object obj) {
            ProductSearchConstraint psc = (ProductSearchConstraint) obj;
            if (psc instanceof CategoryConstraint) {
                CategoryConstraint that = (CategoryConstraint) psc;
                if (this.includeSubCategories != that.includeSubCategories) {
                    return false;
                }
                if (this.productCategoryId == null) {
                    if (that.productCategoryId != null) {
                        return false;
                    }
                } else {
                    if (!this.productCategoryId.equals(that.productCategoryId)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static class FeatureConstraint extends ProductSearchConstraint {
        public static final String constraintName = "Feature";
        protected String productFeatureId;

        public FeatureConstraint(String productFeatureId) {
            this.productFeatureId = productFeatureId;
        }

        public void addConstraint(ProductSearchContext productSearchContext) {
            // make index based values and increment
            String entityAlias = "PFA" + productSearchContext.index;
            String prefix = "pfa" + productSearchContext.index;
            productSearchContext.index++;

            productSearchContext.dynamicViewEntity.addMemberEntity(entityAlias, "ProductFeatureAppl");
            productSearchContext.dynamicViewEntity.addAlias(entityAlias, prefix + "ProductFeatureId", "productFeatureId", null, null, null, null);
            productSearchContext.dynamicViewEntity.addAlias(entityAlias, prefix + "FromDate", "fromDate", null, null, null, null);
            productSearchContext.dynamicViewEntity.addAlias(entityAlias, prefix + "ThruDate", "thruDate", null, null, null, null);
            productSearchContext.dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
            productSearchContext.entityConditionList.add(new EntityExpr(prefix + "ProductFeatureId", EntityOperator.EQUALS, productFeatureId));
            productSearchContext.entityConditionList.add(new EntityExpr(new EntityExpr(prefix + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr(prefix + "ThruDate", EntityOperator.GREATER_THAN, productSearchContext.nowTimestamp)));
            productSearchContext.entityConditionList.add(new EntityExpr(prefix + "FromDate", EntityOperator.LESS_THAN, productSearchContext.nowTimestamp));

            // add in productSearchConstraint, don't worry about the productSearchResultId or constraintSeqId, those will be fill in later
            productSearchContext.productSearchConstraintList.add(productSearchContext.getDelegator().makeValue("ProductSearchConstraint", UtilMisc.toMap("constraintName", constraintName, "infoString", this.productFeatureId)));
        }

        public String prettyPrintConstraint(GenericDelegator delegator, boolean detailed) {
            GenericValue productFeature = null;
            GenericValue productFeatureType = null;
            try {
                productFeature = delegator.findByPrimaryKeyCache("ProductFeature", UtilMisc.toMap("productFeatureId", productFeatureId));
                productFeatureType = productFeature == null ? null : productFeature.getRelatedOne("ProductFeatureType");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error finding ProductFeature and Type information for constraint pretty print", module);
            }
            return (productFeatureType == null ? "Feature: " : productFeatureType.getString("description") + ": ") + (productFeature == null ? "[" + this.productFeatureId + "]" : productFeature.getString("description"));
        }

        public boolean equals(Object obj) {
            ProductSearchConstraint psc = (ProductSearchConstraint) obj;
            if (psc instanceof FeatureConstraint) {
                FeatureConstraint that = (FeatureConstraint) psc;
                if (this.productFeatureId == null) {
                    if (that.productFeatureId != null) {
                        return false;
                    }
                } else {
                    if (!this.productFeatureId.equals(that.productFeatureId)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static class KeywordConstraint extends ProductSearchConstraint {
        public static final String constraintName = "Keyword";
        protected String keywordsString;
        protected boolean anyPrefix;
        protected boolean anySuffix;
        protected boolean isAnd;
        protected boolean removeStems;

        public KeywordConstraint(String keywordsString, boolean anyPrefix, boolean anySuffix, Boolean removeStems, boolean isAnd) {
            this.keywordsString = keywordsString;
            this.anyPrefix = anyPrefix;
            this.anySuffix = anySuffix;
            this.isAnd = isAnd;
            if (removeStems != null) {
                this.removeStems = removeStems.booleanValue();
            } else {
                this.removeStems = UtilProperties.propertyValueEquals("prodsearch", "remove.stems", "true");
            }
        }

        public List makeFullKeywordList(GenericDelegator delegator) {
            List keywordList = KeywordSearch.makeKeywordList(this.keywordsString);
            List fullKeywordList = new ArrayList(keywordList.size()*2);

            // expand the keyword list according to the thesaurus and create a new set of keywords
            Iterator keywordIter = keywordList.iterator();
            while (keywordIter.hasNext()) {
                String keyword = (String) keywordIter.next();
                List expandedList = new LinkedList();
                boolean replaceEntered = KeywordSearch.expandKeyword(keyword, expandedList, delegator);
                fullKeywordList.addAll(expandedList);
                if (!replaceEntered) {
                    fullKeywordList.add(keyword);
                }
            }

            return fullKeywordList;
        }

        public void addConstraint(ProductSearchContext productSearchContext) {
            // just make the fixed keyword lists and put them in the context
            if (isAnd) {
                // when isAnd is true we need to make a list of keyword sets where each set corresponds to one
                //incoming/entered keyword and contains all of the expanded keywords plus the entered keyword if none of
                //the expanded keywords are flagged as replacements; now the tricky part: each set should be or'ed together,
                //but then the sets should be and'ed to produce the overall expression; how to create the SQL for this
                //needs some work as the curret method only support a list of and'ed words and a list of or'ed words, not
                //a list of or'ed sets to be and'ed together
                List keywordList = KeywordSearch.makeKeywordList(this.keywordsString);

                // expand the keyword list according to the thesaurus and create a new set of keywords
                Iterator keywordIter = keywordList.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();
                    List expandedList = new LinkedList();
                    boolean replaceEntered = KeywordSearch.expandKeyword(keyword, expandedList, productSearchContext.getDelegator());
                    if (!replaceEntered) {
                        expandedList.add(keyword);
                    }
                    List fixedList = KeywordSearch.fixKeywords(expandedList, anyPrefix, anySuffix, removeStems, isAnd);
                    Set fixedKeywordSet = new HashSet();
                    fixedKeywordSet.addAll(fixedList);
                    productSearchContext.keywordFixedOrSetAndList.add(fixedKeywordSet);
                }
            } else {
                // when isAnd is false, just add all of the new entries to the big list
                List keywordFirstPass = makeFullKeywordList(productSearchContext.getDelegator());
                List keywordList = KeywordSearch.fixKeywords(keywordFirstPass, anyPrefix, anySuffix, removeStems, isAnd);
                productSearchContext.orKeywordFixedSet.addAll(keywordList);
            }

            // add in productSearchConstraint, don't worry about the productSearchResultId or constraintSeqId, those will be fill in later
            Map valueMap = UtilMisc.toMap("constraintName", constraintName, "infoString", this.keywordsString);
            valueMap.put("anyPrefix", this.anyPrefix ? "Y" : "N");
            valueMap.put("anySuffix", this.anySuffix ? "Y" : "N");
            valueMap.put("isAnd", this.isAnd ? "Y" : "N");
            valueMap.put("removeStems", this.removeStems ? "Y" : "N");
            productSearchContext.productSearchConstraintList.add(productSearchContext.getDelegator().makeValue("ProductSearchConstraint", valueMap));
        }

        /** pretty print for log messages and even UI stuff */
        public String prettyPrintConstraint(GenericDelegator delegator, boolean detailed) {
            return "Keyword(s): \"" + this.keywordsString + "\", where " + (isAnd ? "all words match" : "any word matches");
        }

        public boolean equals(Object obj) {
            ProductSearchConstraint psc = (ProductSearchConstraint) obj;
            if (psc instanceof KeywordConstraint) {
                KeywordConstraint that = (KeywordConstraint) psc;
                if (this.anyPrefix != that.anyPrefix) {
                    return false;
                }
                if (this.anySuffix != that.anySuffix) {
                    return false;
                }
                if (this.isAnd != that.isAnd) {
                    return false;
                }
                if (this.removeStems != that.removeStems) {
                    return false;
                }
                if (this.keywordsString == null) {
                    if (that.keywordsString != null) {
                        return false;
                    }
                } else {
                    if (!this.keywordsString.equals(that.keywordsString)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static class LastUpdatedRangeConstraint extends ProductSearchConstraint {
        public static final String constraintName = "LastUpdatedRange";
        protected Timestamp fromDate;
        protected Timestamp thruDate;

        public LastUpdatedRangeConstraint(Timestamp fromDate, Timestamp thruDate) {
            this.fromDate = fromDate;
            this.thruDate = thruDate;
        }

        public void addConstraint(ProductSearchContext productSearchContext) {
            // TODO: implement LastUpdatedRangeConstraint makeEntityCondition
        }

        /** pretty print for log messages and even UI stuff */
        public String prettyPrintConstraint(GenericDelegator delegator, boolean detailed) {
            // TODO: implement the pretty print for log messages and even UI stuff
            return null;
        }

        public boolean equals(Object obj) {
            ProductSearchConstraint psc = (ProductSearchConstraint) obj;
            if (psc instanceof LastUpdatedRangeConstraint) {
                LastUpdatedRangeConstraint that = (LastUpdatedRangeConstraint) psc;
                if (this.fromDate == null) {
                    if (that.fromDate != null) {
                        return false;
                    }
                } else {
                    if (!this.fromDate.equals(that.fromDate)) {
                        return false;
                    }
                }
                if (this.thruDate == null) {
                    if (that.thruDate != null) {
                        return false;
                    }
                } else {
                    if (!this.thruDate.equals(that.thruDate)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static class ListPriceRangeConstraint extends ProductSearchConstraint {
        public static final String constraintName = "ListPriceRange";
        protected Double lowPrice;
        protected Double highPrice;

        public ListPriceRangeConstraint(Double lowPrice, Double highPrice) {
            this.lowPrice = lowPrice;
            this.highPrice = highPrice;
        }

        public void addConstraint(ProductSearchContext productSearchContext) {
            // TODO: implement ListPriceRangeConstraint makeEntityCondition
        }

        /** pretty print for log messages and even UI stuff */
        public String prettyPrintConstraint(GenericDelegator delegator, boolean detailed) {
            // TODO: implement the pretty print for log messages and even UI stuff
            return null;
        }

        public boolean equals(Object obj) {
            ProductSearchConstraint psc = (ProductSearchConstraint) obj;
            if (psc instanceof ListPriceRangeConstraint) {
                ListPriceRangeConstraint that = (ListPriceRangeConstraint) psc;
                if (this.lowPrice == null) {
                    if (that.lowPrice != null) {
                        return false;
                    }
                } else {
                    if (!this.lowPrice.equals(that.lowPrice)) {
                        return false;
                    }
                }
                if (this.highPrice == null) {
                    if (that.highPrice != null) {
                        return false;
                    }
                } else {
                    if (!this.highPrice.equals(that.highPrice)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    // ======================================================================
    // Result Sort Classes
    // ======================================================================

    public static abstract class ResultSortOrder {
        public ResultSortOrder() {
        }

        public abstract void setSortOrder(ProductSearchContext productSearchContext);
        public abstract String getOrderName();
        public abstract String prettyPrintSortOrder(boolean detailed);
        public abstract boolean isAscending();
    }

    public static class SortKeywordRelevancy extends ResultSortOrder {
        public SortKeywordRelevancy() {
        }

        public void setSortOrder(ProductSearchContext productSearchContext) {
            if (productSearchContext.includedKeywordSearch) {
                // we have to check this in order to be sure that there is a totalRelevancy to sort by...
                productSearchContext.orderByList.add("-totalRelevancy");
                productSearchContext.fieldsToSelect.add("totalRelevancy");
            }
        }

        public String getOrderName() {
            return "KeywordRelevancy";
        }

        public String prettyPrintSortOrder(boolean detailed) {
            return "Keyword Relevancy";
        }

        public boolean isAscending() {
            return false;
        }
    }

    public static class SortProductField extends ResultSortOrder {
        protected String fieldName;
        protected boolean ascending;

        /** Some good field names to try might include:
         * [productName]
         * [totalQuantityOrdered] for most popular or most purchased
         * [lastModifiedDate]
         *
         *  You can also include any other field on the Product entity.
         */
        public SortProductField(String fieldName, boolean ascending) {
            this.fieldName = fieldName;
            this.ascending = ascending;
        }

        public void setSortOrder(ProductSearchContext productSearchContext) {
            productSearchContext.dynamicViewEntity.addAlias("PROD", fieldName);
            if (ascending) {
                productSearchContext.orderByList.add("+" + fieldName);
            } else {
                productSearchContext.orderByList.add("-" + fieldName);
            }
            productSearchContext.fieldsToSelect.add(fieldName);
        }

        public String getOrderName() {
            return "ProductField:" + this.fieldName;
        }

        public String prettyPrintSortOrder(boolean detailed) {
            return this.fieldName;
        }

        public boolean isAscending() {
            return this.ascending;
        }
    }

    public static class SortListPrice extends ResultSortOrder {
        protected boolean ascending;
        public SortListPrice(boolean ascending) {
            this.ascending = ascending;
        }

        public void setSortOrder(ProductSearchContext productSearchContext) {
            // TODO: implement SortListPrice, this will be a bit more complex, need to add a ProductPrice member entity
        }

        public String getOrderName() {
            return "ListPrice";
        }

        public String prettyPrintSortOrder(boolean detailed) {
            return "List Price (" + (this.ascending ? "Low to High)" : "High to Low)");
        }

        public boolean isAscending() {
            return this.ascending;
        }
    }

    /** A rather large and verbose method that doesn't use the cool constraint and sort order objects */
    /*
    public static ArrayList parametricKeywordSearchStandAlone(Set featureIdSet, String keywordsString, GenericDelegator delegator, String productCategoryId, boolean includeSubCategories, String visitId, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
        // TODO: implement this for the new features
        boolean removeStems = UtilProperties.propertyValueEquals("prodsearch", "remove.stems", "true");

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // make view-entity & EntityCondition
        int index = 1;
        List entityConditionList = new LinkedList();
        List orderByList = new LinkedList();
        List fieldsToSelect = UtilMisc.toList("productId");
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
            String entityAlias = "PCM" + index;
            String prefix = "pcm" + index;
            index++;

            dynamicViewEntity.addMemberEntity(entityAlias, "ProductCategoryMember");
            dynamicViewEntity.addAlias(entityAlias, prefix + "ProductCategoryId", "productCategoryId", null, null, null, null);
            dynamicViewEntity.addAlias(entityAlias, prefix + "FromDate", "fromDate", null, null, null, null);
            dynamicViewEntity.addAlias(entityAlias, prefix + "ThruDate", "thruDate", null, null, null, null);
            dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
            entityConditionList.add(new EntityExpr(prefix + "ProductCategoryId", EntityOperator.IN, productCategoryIdList));
            entityConditionList.add(new EntityExpr(new EntityExpr(prefix + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr(prefix + "ThruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            entityConditionList.add(new EntityExpr(prefix + "FromDate", EntityOperator.LESS_THAN, nowTimestamp));
        }

        // Keyword
        List keywordFirstPass = KeywordSearch.makeKeywordList(keywordsString);
        List keywordList = KeywordSearch.fixKeywords(keywordFirstPass, anyPrefix, anySuffix, removeStems, isAnd);

        if (keywordList.size() > 0) {
            if (isAnd) {
                // add up the relevancyWeight fields from all keyword member entities for a total to sort by
                ComplexAlias complexAlias = new ComplexAlias("+");

                Iterator keywordIter = keywordList.iterator();
                while (keywordIter.hasNext()) {
                    String keyword = (String) keywordIter.next();

                    // make index based values and increment
                    String entityAlias = "PK" + index;
                    String prefix = "pk" + index;
                    index++;

                    dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                    dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword", "keyword", null, null, null, null);
                    dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                    entityConditionList.add(new EntityExpr(prefix + "Keyword", EntityOperator.LIKE, keyword));

                    //don't add an alias for this, will be part of a complex alias: dynamicViewEntity.addAlias(entityAlias, prefix + "RelevancyWeight", "relevancyWeight", null, null, null, null);
                    complexAlias.addComplexAliasMember(new ComplexAliasField(entityAlias, "relevancyWeight"));
                }
                dynamicViewEntity.addAlias(null, "totalRelevancy", null, null, null, null, null, complexAlias);
                orderByList.add("-totalRelevancy");
                fieldsToSelect.add("totalRelevancy");
            } else {
                // make index based values and increment
                String entityAlias = "PK" + index;
                String prefix = "pk" + index;
                index++;

                dynamicViewEntity.addMemberEntity(entityAlias, "ProductKeyword");
                dynamicViewEntity.addAlias(entityAlias, "totalRelevancy", "relevancyWeight", null, null, null, "sum");
                dynamicViewEntity.addAlias(entityAlias, prefix + "Keyword", "keyword", null, null, null, null);
                dynamicViewEntity.addViewLink("PROD", entityAlias, Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
                orderByList.add("-totalRelevancy");
                fieldsToSelect.add("totalRelevancy");
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
        if (featureIdSet != null && featureIdSet.size() > 0) {
            Iterator featureIdIter = featureIdSet.iterator();
            while (featureIdIter.hasNext()) {
                String productFeatureId = (String) featureIdIter.next();

                // make index based values and increment
                String entityAlias = "PFA" + index;
                String prefix = "pfa" + index;
                index++;

                dynamicViewEntity.addMemberEntity(entityAlias, "ProductFeatureAppl");
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
        EntityFindOptions efo = new EntityFindOptions();
        efo.setDistinct(true);

        EntityListIterator eli = null;
        try {
            eli = delegator.findListIteratorByCondition(dynamicViewEntity, whereCondition, null, fieldsToSelect, orderByList, efo);
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
     */
}
