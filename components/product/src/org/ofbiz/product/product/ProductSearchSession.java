/*
 * $Id: ProductSearchSession.java,v 1.1 2003/10/25 04:15:19 jonesde Exp $
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

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.feature.ParametricSearch;
import org.ofbiz.product.product.ProductSearch.*;

/**
 *  Utility class with methods to prepare and perform ProductSearch operations in the content of an HttpSession
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class ProductSearchSession {
    
    public static final String module = ProductSearchSession.class.getName();
    
    public static final String PRODUCT_SEARCH_CONSTRAINT_LIST = "_PRODUCT_SEARCH_CONSTRAINT_LIST_";
    public static final String PRODUCT_SEARCH_SORT_ORDER = "_PRODUCT_SEARCH_SORT_ORDER_";
    
    public static ArrayList searchDo(HttpSession session, GenericDelegator delegator, String prodCatalogId) {
        String visitId = VisitHandler.getVisitId(session);
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        if (productSearchConstraintList == null || productSearchConstraintList.size() == 0) {
            // no constraints, don't do a search...
            return new ArrayList();
        }
        
        // make sure the view allow category is included
        String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
        if (UtilValidate.isNotEmpty(viewProductCategoryId)) {
            ProductSearchConstraint viewAllowConstraint = new CategoryConstraint(viewProductCategoryId, true);
            if (!productSearchConstraintList.contains(viewAllowConstraint)) {
                // don't add to same list, will modify the one in the session, create new list
                productSearchConstraintList = new ArrayList(productSearchConstraintList);
                productSearchConstraintList.add(viewAllowConstraint);
            }
        }
        
        ResultSortOrder resultSortOrder = (ResultSortOrder) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_SORT_ORDER);
        if (resultSortOrder == null) {
            resultSortOrder = new SortKeywordRelevancy();
            searchSetSortOrder(resultSortOrder, session);
        }
        return ProductSearch.searchProducts(productSearchConstraintList, resultSortOrder, delegator, visitId);
    }
    
    public static void searchClear(HttpSession session) {
        session.removeAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        session.removeAttribute(ProductSearchSession.PRODUCT_SEARCH_SORT_ORDER);
    }
    
    public static List searchGetConstraintStrings(boolean detailed, HttpSession session, GenericDelegator delegator) {
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        List constraintStrings = new ArrayList();
        if (productSearchConstraintList == null) {
            return constraintStrings;
        }
        Iterator productSearchConstraintIter = productSearchConstraintList.iterator();
        while (productSearchConstraintIter.hasNext()) {
            ProductSearchConstraint productSearchConstraint = (ProductSearchConstraint) productSearchConstraintIter.next();
            if (productSearchConstraint == null) continue;
            String constraintString = productSearchConstraint.prettyPrintConstraint(delegator, detailed);
            if (UtilValidate.isNotEmpty(constraintString)) {
                constraintStrings.add(constraintString);
            } else {
                constraintStrings.add("Description not available");
            }
        }
        return constraintStrings;
    }
    
    public static String searchGetSortOrderString(boolean detailed, HttpSession session) {
        ResultSortOrder resultSortOrder = (ResultSortOrder) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_SORT_ORDER);
        if (resultSortOrder == null) return "";
        return resultSortOrder.prettyPrintSortOrder(detailed);
    }
    
    public static void searchSetSortOrder(ResultSortOrder resultSortOrder, HttpSession session) {
        session.setAttribute(ProductSearchSession.PRODUCT_SEARCH_SORT_ORDER, resultSortOrder);
    }
    
    public static void searchAddFeatureIdConstraints(Collection featureIds, HttpSession session) {
        if (featureIds == null || featureIds.size() == 0) {
            return;
        }
        Iterator featureIdIter = featureIds.iterator();
        while (featureIdIter.hasNext()) {
            String productFeatureId = (String) featureIdIter.next();
            searchAddConstraint(new FeatureConstraint(productFeatureId), session);
        }
    }
    
    public static void searchAddConstraint(ProductSearchConstraint productSearchConstraint, HttpSession session) {
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        if (productSearchConstraintList == null) {
            productSearchConstraintList = new LinkedList();
            session.setAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST, productSearchConstraintList);
        }
        if (!productSearchConstraintList.contains(productSearchConstraint)) {
            productSearchConstraintList.add(productSearchConstraint);
        }
    }
    
    public static void searchRemoveConstraint(int index, HttpSession session) {
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        if (productSearchConstraintList == null) {
            return;
        } else if (index >= productSearchConstraintList.size()) {
            return;
        } else {
            productSearchConstraintList.remove(index);
        }
    }
    
    public static Map getProductSearchResult(Map parameters, HttpSession session, GenericDelegator delegator, String prodCatalogId) {
        // clear search? by default yes, but if the clearSearch parameter is N then don't
        String clearSearchString = (String) parameters.get("clearSearch");
        if (!"N".equals(clearSearchString)) {
            searchClear(session);
        } else {
            String removeConstraint = (String) parameters.get("removeConstraint");
            if (UtilValidate.isNotEmpty(removeConstraint)) {
                try {
                    searchRemoveConstraint(Integer.parseInt(removeConstraint), session);
                } catch (Exception e) {
                    Debug.logError(e, "Error removing constraint [" + removeConstraint + "]", "keywordsearch.bsh");
                }
            }
        }

        // if there is another category, add a constraint for it
        String searchCategoryId = (String) parameters.get("SEARCH_CATEGORY_ID");
        String searchSubCategories = (String) parameters.get("SEARCH_SUB_CATEGORIES");
        if (UtilValidate.isNotEmpty(searchCategoryId)) {
            searchAddConstraint(new ProductSearch.CategoryConstraint(searchCategoryId, !"N".equals(searchSubCategories)), session);
        }

        // if keywords were specified, add a constraint for them
        String keywordString = (String) parameters.get("SEARCH_STRING");
        String searchOperator = (String) parameters.get("SEARCH_OPERATOR");
        if (searchOperator == null) {
            searchOperator = (String) session.getAttribute("searchOperator");
        } else {
            session.setAttribute("searchOperator", searchOperator);
        }
        if (UtilValidate.isNotEmpty(keywordString)) {
            searchAddConstraint(new ProductSearch.KeywordConstraint(keywordString, true, true, null, "AND".equals(searchOperator)), session);
        }

        // if features were selected add a constraint for each
        Map featureIdByType = ParametricSearch.makeFeatureIdByTypeMap(parameters);
        searchAddFeatureIdConstraints(featureIdByType.values(), session);

        // set the sort order
        String sortOrder = (String) parameters.get("sortOrder");
        String sortAscending = (String) parameters.get("sortAscending");
        boolean ascending = !"N".equals(sortAscending);
        if (sortOrder != null) {
            if (sortOrder.equals("SortKeywordRelevancy")) {
                searchSetSortOrder(new ProductSearch.SortKeywordRelevancy(), session);
            } else if (sortOrder.startsWith("SortProductField:")) {
                String fieldName = sortOrder.substring("SortProductField:".length());
                searchSetSortOrder(new ProductSearch.SortProductField(fieldName, ascending), session);
            } else if (sortOrder.equals("SortListPrice")) {
                searchSetSortOrder(new ProductSearch.SortListPrice(ascending), session);
            }
        }

        // ========== Do the actual search
        List productIds = searchDo(session, delegator, prodCatalogId);

        // ========== Create View Indexes
        int viewIndex = 0;
        int viewSize = 20;
        int highIndex = 0;
        int lowIndex = 0;
        int listSize = 0;

        try {
            String viewIndexStr = (String) parameters.get("VIEW_INDEX");
            if (UtilValidate.isNotEmpty(viewIndexStr)) {
                viewIndex = Integer.valueOf(viewIndexStr).intValue();
            }
        } catch (Exception e) {
            Debug.logError(e, "Error formatting VIEW_INDEX", module);
            viewIndex = 0;
        }

        try {
            String viewSizeStr = (String) parameters.get("VIEW_SIZE");
            if (UtilValidate.isNotEmpty(viewSizeStr)) {
                viewSize = Integer.valueOf(viewSizeStr).intValue();
            }
        } catch (Exception e) {
            Debug.logError(e, "Error formatting VIEW_SIZE", module);
            viewSize = 20;
        }

        if (productIds != null) {
            listSize = productIds.size();
        }

        lowIndex = viewIndex * viewSize;
        highIndex = (viewIndex + 1) * viewSize;
        if (listSize < highIndex) {
            highIndex = listSize;
        }


        // ========== Setup other display info
        GenericValue searchCategory = null;
        try {
            searchCategory = (searchCategoryId == null || searchCategoryId.length() == 0) ? null : 
                    delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", searchCategoryId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up search ProductCategory", module);
        }

        List searchConstraintStrings = searchGetConstraintStrings(false, session, delegator);
        String searchSortOrderString = searchGetSortOrderString(false, session);

        // ========== populate the result Map
        Map result = new HashMap();
        
        result.put("productIds", productIds);
        result.put("viewIndex", new Integer(viewIndex));
        result.put("viewSize", new Integer(viewSize));
        result.put("listSize", new Integer(listSize));
        result.put("lowIndex", new Integer(lowIndex));
        result.put("highIndex", new Integer(highIndex));
        result.put("searchOperator", searchOperator);
        result.put("searchCategory", searchCategory);
        result.put("searchConstraintStrings", searchConstraintStrings);
        result.put("searchSortOrderString", searchSortOrderString);
        
        return result;
    }
}
