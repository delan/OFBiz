/*
 * $Id: ProductSearchSession.java,v 1.8 2004/03/13 03:59:11 jonesde Exp $
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
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.feature.ParametricSearch;
import org.ofbiz.product.product.ProductSearch.*;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 *  Utility class with methods to prepare and perform ProductSearch operations in the content of an HttpSession
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.8 $
 * @since      3.0
 */
public class ProductSearchSession {

    public static final String module = ProductSearchSession.class.getName();

    public static final String PRODUCT_SEARCH_CONSTRAINT_LIST = "_PRODUCT_SEARCH_CONSTRAINT_LIST_";
    public static final String PRODUCT_SEARCH_SORT_ORDER = "_PRODUCT_SEARCH_SORT_ORDER_";
    public static final String PRODUCT_SEARCH_VIEW_INDEX = "_PRODUCT_SEARCH_VIEW_INDEX_";
    public static final String PRODUCT_SEARCH_VIEW_SIZE = "_PRODUCT_SEARCH_VIEW_SIZE_";

    /** A ControlServlet event method used to check to see if there is an override for any of the current keywords in the search */
    public static final String checkDoKeywordOverride(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map requestParams = UtilHttp.getParameterMap(request);
        ProductSearchSession.processSearchParameters(requestParams, session);

        // get the current productStoreId
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        if (productStoreId != null) {
            // get a Set of all keywords in the search, if there are any...
            Set keywords = new HashSet();
            List constraintList = (List) session.getAttribute(PRODUCT_SEARCH_CONSTRAINT_LIST);
            if (constraintList != null) {
	            Iterator constraintIter = constraintList.iterator();
	            while (constraintIter.hasNext()) {
	                Object constraint = constraintIter.next();
	                if (constraint instanceof KeywordConstraint) {
	                    KeywordConstraint keywordConstraint = (KeywordConstraint) constraint;
	                    Set keywordSet = keywordConstraint.makeFullKeywordSet(delegator);
	                    if (keywordSet != null) keywords.addAll(keywordSet);
	                }
	            }
            }

            if (keywords.size() > 0) {
                List productStoreKeywordOvrdList = null;
                try {
                    productStoreKeywordOvrdList = delegator.findByAndCache("ProductStoreKeywordOvrd", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("-fromDate"));
                    productStoreKeywordOvrdList = EntityUtil.filterByDate(productStoreKeywordOvrdList, true);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error reading ProductStoreKeywordOvrd list, not doing keyword override", module);
                }

                if (productStoreKeywordOvrdList != null && productStoreKeywordOvrdList.size() > 0) {
                    Iterator productStoreKeywordOvrdIter = productStoreKeywordOvrdList.iterator();
                    while (productStoreKeywordOvrdIter.hasNext()) {
                        GenericValue productStoreKeywordOvrd = (GenericValue) productStoreKeywordOvrdIter.next();
                        String ovrdKeyword = productStoreKeywordOvrd.getString("keyword");
                        if (keywords.contains(ovrdKeyword)) {
                            String targetTypeEnumId = productStoreKeywordOvrd.getString("targetTypeEnumId");
                            String target = productStoreKeywordOvrd.getString("target");
                            ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
                            RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
                            if ("KOTT_PRODCAT".equals(targetTypeEnumId)) {
                                String requestName = "/category/~category_id=" + target;
                                target = rh.makeLink(request, response, requestName, false, false, false);
                            } else if ("KOTT_PRODUCT".equals(targetTypeEnumId)) {
                                String requestName = "/product/~product_id=" + target;
                                target = rh.makeLink(request, response, requestName, false, false, false);
                            } else if ("KOTT_OFBURL".equals(targetTypeEnumId)) {
                                target = rh.makeLink(request, response, target, false, false, false);
                            } else if ("KOTT_AURL".equals(targetTypeEnumId)) {
                                // do nothing, is absolute URL
                            } else {
                                Debug.logError("The targetTypeEnumId [] is not recognized, not doing keyword override", module);
                                // might as well see if there are any others...
                                continue;
                            }
                            try {
                                response.sendRedirect(target);
                                return "none";
                            } catch (IOException e) {
                                Debug.logError(e, "Could not send redirect to: " + target, module);
                                continue;
                            }
                        }
                    }
                }
            }
        }

        return "success";
    }

    public static ArrayList searchDo(HttpSession session, GenericDelegator delegator, String prodCatalogId) {
        String visitId = VisitHandler.getVisitId(session);
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        if (productSearchConstraintList == null || productSearchConstraintList.size() == 0) {
            // no constraints, don't do a search...
            return new ArrayList();
        }

        // make sure the view allow category is included
        productSearchConstraintList = ensureViewAllowConstraint(productSearchConstraintList, prodCatalogId, delegator);
        ResultSortOrder resultSortOrder = getResultSortOrder(session);
        return ProductSearch.searchProducts(productSearchConstraintList, resultSortOrder, delegator, visitId);
    }

    public static List ensureViewAllowConstraint(List productSearchConstraintList, String prodCatalogId, GenericDelegator delegator) {
        String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
        if (UtilValidate.isNotEmpty(viewProductCategoryId)) {
            ProductSearchConstraint viewAllowConstraint = new CategoryConstraint(viewProductCategoryId, true);
            if (!productSearchConstraintList.contains(viewAllowConstraint)) {
                // don't add to same list, will modify the one in the session, create new list
                productSearchConstraintList = new ArrayList(productSearchConstraintList);
                productSearchConstraintList.add(viewAllowConstraint);
            }
        }
        return productSearchConstraintList;
    }

    public static ResultSortOrder getResultSortOrder(HttpSession session) {
        ResultSortOrder resultSortOrder = (ResultSortOrder) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_SORT_ORDER);
        if (resultSortOrder == null) {
            resultSortOrder = new SortKeywordRelevancy();
            searchSetSortOrder(resultSortOrder, session);
        }
        return resultSortOrder;
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

    public static void processSearchParameters(Map parameters, HttpSession session) {
        boolean constraintsChanged = false;
        
        // clear search? by default yes, but if the clearSearch parameter is N then don't
        String clearSearchString = (String) parameters.get("clearSearch");
        if (!"N".equals(clearSearchString)) {
            searchClear(session);
            constraintsChanged = true;
        } else {
            String removeConstraint = (String) parameters.get("removeConstraint");
            if (UtilValidate.isNotEmpty(removeConstraint)) {
                try {
                    searchRemoveConstraint(Integer.parseInt(removeConstraint), session);
                    constraintsChanged = true;
                } catch (Exception e) {
                    Debug.logError(e, "Error removing constraint [" + removeConstraint + "]", module);
                }
            }
        }

        // if there is another category, add a constraint for it
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_CATEGORY_ID"))) {
            String searchCategoryId = (String) parameters.get("SEARCH_CATEGORY_ID");
            String searchSubCategories = (String) parameters.get("SEARCH_SUB_CATEGORIES");
            searchAddConstraint(new ProductSearch.CategoryConstraint(searchCategoryId, !"N".equals(searchSubCategories)), session);
            constraintsChanged = true;
        }
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_CATEGORY_ID2"))) {
            String searchCategoryId = (String) parameters.get("SEARCH_CATEGORY_ID2");
            String searchSubCategories = (String) parameters.get("SEARCH_SUB_CATEGORIES2");
            searchAddConstraint(new ProductSearch.CategoryConstraint(searchCategoryId, !"N".equals(searchSubCategories)), session);
            constraintsChanged = true;
        }
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_CATEGORY_ID3"))) {
            String searchCategoryId = (String) parameters.get("SEARCH_CATEGORY_ID3");
            String searchSubCategories = (String) parameters.get("SEARCH_SUB_CATEGORIES3");
            searchAddConstraint(new ProductSearch.CategoryConstraint(searchCategoryId, !"N".equals(searchSubCategories)), session);
            constraintsChanged = true;
        }

        // if keywords were specified, add a constraint for them
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_STRING"))) {
            String keywordString = (String) parameters.get("SEARCH_STRING");
            String searchOperator = (String) parameters.get("SEARCH_OPERATOR");
            searchAddConstraint(new ProductSearch.KeywordConstraint(keywordString, true, true, null, "AND".equals(searchOperator)), session);
            constraintsChanged = true;
        }
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_STRING2"))) {
            String keywordString = (String) parameters.get("SEARCH_STRING2");
            String searchOperator = (String) parameters.get("SEARCH_OPERATOR2");
            searchAddConstraint(new ProductSearch.KeywordConstraint(keywordString, true, true, null, "AND".equals(searchOperator)), session);
            constraintsChanged = true;
        }
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_STRING3"))) {
            String keywordString = (String) parameters.get("SEARCH_STRING3");
            String searchOperator = (String) parameters.get("SEARCH_OPERATOR3");
            searchAddConstraint(new ProductSearch.KeywordConstraint(keywordString, true, true, null, "AND".equals(searchOperator)), session);
            constraintsChanged = true;
        }

        // get independently defined features, ie not with a type parameter
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_FEAT"))) {
            searchAddConstraint(new ProductSearch.FeatureConstraint((String) parameters.get("SEARCH_FEAT")), session);
            constraintsChanged = true;
        }
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_FEAT2"))) {
            searchAddConstraint(new ProductSearch.FeatureConstraint((String) parameters.get("SEARCH_FEAT2")), session);
            constraintsChanged = true;
        }
        if (UtilValidate.isNotEmpty((String) parameters.get("SEARCH_FEAT3"))) {
            searchAddConstraint(new ProductSearch.FeatureConstraint((String) parameters.get("SEARCH_FEAT3")), session);
            constraintsChanged = true;
        }
        
        // if features were selected add a constraint for each
        Map featureIdByType = ParametricSearch.makeFeatureIdByTypeMap(parameters);
        if (featureIdByType.size() > 0) {
            constraintsChanged = true;
            searchAddFeatureIdConstraints(featureIdByType.values(), session);
        }

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
        
        if (constraintsChanged) {
            // query changed, clear out the VIEW_INDEX & VIEW_SIZE
            session.removeAttribute(ProductSearchSession.PRODUCT_SEARCH_VIEW_INDEX);
            session.removeAttribute(ProductSearchSession.PRODUCT_SEARCH_VIEW_SIZE);
        }

        String viewIndexStr = (String) parameters.get("VIEW_INDEX");
        if (UtilValidate.isNotEmpty(viewIndexStr)) {
            try {
                session.setAttribute(PRODUCT_SEARCH_VIEW_INDEX, Integer.valueOf(viewIndexStr));
            } catch (Exception e) {
                Debug.logError(e, "Error formatting VIEW_INDEX, setting to 0", module);
                // we could just do nothing here, but we know something was specified so we don't want to use the previous value from the session
                session.setAttribute(PRODUCT_SEARCH_VIEW_INDEX, new Integer(0));
            }
        }

        String viewSizeStr = (String) parameters.get("VIEW_SIZE");
        if (UtilValidate.isNotEmpty(viewSizeStr)) {
            try {
                session.setAttribute(PRODUCT_SEARCH_VIEW_SIZE, Integer.valueOf(viewSizeStr));
            } catch (Exception e) {
                Debug.logError(e, "Error formatting VIEW_SIZE, setting to 20", module);
                session.setAttribute(PRODUCT_SEARCH_VIEW_SIZE, new Integer(20));
            }
        }
    }

    public static Map getProductSearchResult(HttpSession session, GenericDelegator delegator, String prodCatalogId) {

        // ========== Create View Indexes
        int viewIndex = 0;
        int viewSize = 20;
        int highIndex = 0;
        int lowIndex = 0;
        int listSize = 0;

        Integer viewIndexInteger = (Integer) session.getAttribute(PRODUCT_SEARCH_VIEW_INDEX);
        if (viewIndexInteger != null) viewIndex = viewIndexInteger.intValue();
        Integer viewSizeInteger = (Integer) session.getAttribute(PRODUCT_SEARCH_VIEW_SIZE);
        if (viewSizeInteger != null) viewSize = viewSizeInteger.intValue();

        lowIndex = viewIndex * viewSize;
        highIndex = (viewIndex + 1) * viewSize;

        // setup resultOffset and maxResults, noting that resultOffset is 1 based, not zero based as these numbers
        Integer resultOffset = new Integer(lowIndex + 1);
        Integer maxResults = new Integer(viewSize);

        // ========== Do the actual search
        ArrayList productIds = null;
        String visitId = VisitHandler.getVisitId(session);
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        // if no constraints, don't do a search...
        if (productSearchConstraintList != null && productSearchConstraintList.size() > 0) {
            productSearchConstraintList = ensureViewAllowConstraint(productSearchConstraintList, prodCatalogId, delegator);
            ResultSortOrder resultSortOrder = getResultSortOrder(session);

            ProductSearchContext productSearchContext = new ProductSearchContext(delegator, visitId);
            productSearchContext.addProductSearchConstraints(productSearchConstraintList);
            productSearchContext.setResultSortOrder(resultSortOrder);
            productSearchContext.setResultOffset(resultOffset);
            productSearchContext.setMaxResults(maxResults);

            productIds = productSearchContext.doSearch();

            Integer totalResults = productSearchContext.getTotalResults();
            if (totalResults != null) {
                listSize = totalResults.intValue();
            }
        }

        if (listSize < highIndex) {
            highIndex = listSize;
        }

        // ========== Setup other display info
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
        result.put("searchConstraintStrings", searchConstraintStrings);
        result.put("searchSortOrderString", searchSortOrderString);

        return result;
    }
}
