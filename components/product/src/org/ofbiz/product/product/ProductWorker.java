/*
 * $Id: ProductWorker.java,v 1.2 2003/08/18 17:03:08 ajzeneski Exp $
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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.feature.ParametricSearch;

/**
 * Product Worker class to reduce code in JSPs.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.2 $
 * @since      2.0
 */
public class ProductWorker {
    
    public static final String module = ProductWorker.class.getName();

    public static void getProduct(PageContext pageContext, String attributeName) {
        getProduct(pageContext, attributeName, null);
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
     * Puts the following into the pageContext attribute list with a prefix if specified:
     *  searchProductList, keywordString, viewIndex, viewSize, lowIndex, highIndex, listSize
     * Puts the following into the session attribute list:
     *  CACHE_SEARCH_RESULTS, CACHE_SEARCH_RESULTS_NAME
     *@param pageContext The pageContext of the calling JSP
     *@param attributePrefix A prefix to put on each attribute name in the pageContext
     */
    public static void getKeywordSearchProducts(PageContext pageContext, String attributePrefix) {
        getKeywordSearchProducts(pageContext, attributePrefix, null);
    }

    /**
     * Puts the following into the pageContext attribute list with a prefix if specified:
     *  searchProductList, keywordString, viewIndex, viewSize, lowIndex, highIndex, listSize
     * Puts the following into the session attribute list:
     *  CACHE_SEARCH_RESULTS, CACHE_SEARCH_RESULTS_NAME
     *@param pageContext The pageContext of the calling JSP
     *@param attributePrefix A prefix to put on each attribute name in the pageContext
     *@param categoryId The keyword search group name for this search
     */
    public static void getKeywordSearchProducts(PageContext pageContext, String attributePrefix, String categoryId) {
        getKeywordSearchProducts(pageContext, attributePrefix, categoryId, false, false, "OR");
    }
                
    /**
     * Puts the following into the pageContext attribute list with a prefix if specified:
     *  searchProductList, keywordString, viewIndex, viewSize, lowIndex, highIndex, listSize
     * Puts the following into the session attribute list:
     *  CACHE_SEARCH_RESULTS, CACHE_SEARCH_RESULTS_NAME
     *@param pageContext The pageContext of the calling JSP
     *@param attributePrefix A prefix to put on each attribute name in the pageContext
     *@param categoryId The keyword search group name for this search
     */    
    public static void getKeywordSearchProducts(PageContext pageContext, String attributePrefix, String categoryId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        getKeywordSearchProducts(pageContext.getRequest(), attributePrefix, categoryId, anyPrefix, anySuffix, intraKeywordOperator);
    } 
       
    public static void getKeywordSearchProducts(ServletRequest request, String attributePrefix, String categoryId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Map requestParameters = UtilHttp.getParameterMap(httpRequest);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        if (intraKeywordOperator == null || (!"AND".equalsIgnoreCase(intraKeywordOperator) && !"OR".equalsIgnoreCase(intraKeywordOperator))) {
            Debug.logWarning("intraKeywordOperator [" + intraKeywordOperator + "] was not valid, defaulting to OR", module);
            intraKeywordOperator = "OR";
        }

        int viewIndex = 0;

        try {
            viewIndex = Integer.valueOf((String) requestParameters.get("VIEW_INDEX")).intValue();
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 10;

        try {
            viewSize = Integer.valueOf((String) requestParameters.get("VIEW_SIZE")).intValue();
        } catch (Exception e) {
            viewSize = 10;
        }

        if (categoryId == null) categoryId = "";
        String keywordString = (String) requestParameters.get("SEARCH_STRING");

        Map featureIdByType = ParametricSearch.makeFeatureIdByTypeMap(request);
        String featureIdByTypeString = ParametricSearch.makeFeatureIdByTypeString(featureIdByType);

        String curFindString = "KeywordSearch:" + keywordString + "::" + categoryId + "::" + anyPrefix + "::" + anySuffix + "::" + intraKeywordOperator + "::" + featureIdByTypeString;

        ArrayList productIds = (ArrayList) httpRequest.getSession().getAttribute("CACHE_SEARCH_RESULTS");
        String resultArrayName = (String) httpRequest.getSession().getAttribute("CACHE_SEARCH_RESULTS_NAME");

        if (productIds == null || resultArrayName == null || !curFindString.equals(resultArrayName)) { // || viewIndex == 0
            if (Debug.infoOn()) Debug.logInfo("KeywordSearch productId Array not found in session, getting new one...", module);
            if (Debug.infoOn()) Debug.logInfo("curFindString:" + curFindString + " resultArrayName:" + resultArrayName, module);

            // productIds will be pre-sorted
            if (featureIdByType.size() > 0) {
                productIds = ParametricSearch.parametricKeywordSearch(featureIdByType, keywordString, delegator, categoryId, VisitHandler.getVisitId(httpRequest.getSession()), anyPrefix, anySuffix, intraKeywordOperator);
            } else {
                productIds = KeywordSearch.productsByKeywords(keywordString, delegator, categoryId, VisitHandler.getVisitId(httpRequest.getSession()), anyPrefix, anySuffix, intraKeywordOperator);
            }
            

            if (productIds != null) {
                httpRequest.getSession().setAttribute("CACHE_SEARCH_RESULTS", productIds);
                httpRequest.getSession().setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
            } else {
                httpRequest.getSession().removeAttribute("CACHE_SEARCH_RESULTS");
                httpRequest.getSession().removeAttribute("CACHE_SEARCH_RESULTS_NAME");
            }
        }

        int lowIndex = viewIndex * viewSize + 1;
        int highIndex = (viewIndex + 1) * viewSize;
        int listSize = 0;

        if (productIds != null)
            listSize = productIds.size();
        if (listSize < highIndex)
            highIndex = listSize;

        ArrayList products = new ArrayList();

        for (int ind = lowIndex; ind <= highIndex; ind++) {
            GenericValue prod = null;

            try {
                prod = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productIds.get(ind - 1)));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                prod = null;
            }
            if (prod != null) products.add(prod);
        }

        request.setAttribute(attributePrefix + "viewIndex", new Integer(viewIndex));
        request.setAttribute(attributePrefix + "viewSize", new Integer(viewSize));
        request.setAttribute(attributePrefix + "lowIndex", new Integer(lowIndex));
        request.setAttribute(attributePrefix + "highIndex", new Integer(highIndex));
        request.setAttribute(attributePrefix + "listSize", new Integer(listSize));
        request.setAttribute(attributePrefix + "keywordString", keywordString);
        request.setAttribute(attributePrefix + "featureIdByType", featureIdByType);
        if (products.size() > 0) request.setAttribute(attributePrefix + "searchProductList", products);
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
}

