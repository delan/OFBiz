/*
 * $Id$
 * $Log$
 * Revision 1.4  2002/01/30 06:11:25  jonesde
 * Formatting changes only, in preparation for other changes
 *
 * Revision 1.3  2001/10/26 05:04:47  jonesde
 * Added filter by date so that only ProductAssoc entities within date range will be shown
 *
 * Revision 1.2  2001/10/19 17:11:45  jonesde
 * Added search in a category
 *
 * Revision 1.1  2001/10/05 02:32:39  jonesde
 * Refactored CatalogHelper: split into CatalogWorker and in commonapp CategoryWorker and ProductWorker
 *
 *
 */

package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * <p><b>Title:</b> ProductWorker.java
 * <p><b>Description:</b> Helper class to reduce code in JSPs.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on August 23, 2001, 7:58 PM
 */
public class ProductWorker {

    public static void getProduct(PageContext pageContext, String attributeName) {
        getProduct(pageContext, attributeName, null);
    }

    public static void getProduct(PageContext pageContext, String attributeName, String productId) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        ServletRequest request = pageContext.getRequest();

        if (productId == null)
            productId = UtilFormatOut.checkNull(request.getParameter("product_id"), request.getParameter("PRODUCT_ID"));

        if (productId.equals(""))
            return;

        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            product = null;
        }
        if (product != null)
            pageContext.setAttribute(attributeName, product);
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
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");

        int viewIndex = 0;
        try {
            viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = 10;
        try {
            viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
        } catch (Exception e) {
            viewSize = 10;
        }

        String keywordString = pageContext.getRequest().getParameter("SEARCH_STRING");
        String curFindString = "KeywordSearch:" + keywordString;

        ArrayList productIds = (ArrayList) pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS");
        String resultArrayName = (String) pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS_NAME");
        if (productIds == null || resultArrayName == null || !curFindString.equals(resultArrayName)) { // || viewIndex == 0
            Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
            Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);

            //sort by productId (only available sort for now...)
            Collection unsortedIds = KeywordSearch.productsByKeywords(keywordString, delegator, categoryId);
            if (unsortedIds != null && unsortedIds.size() > 0) {
                TreeSet productIdTree = new TreeSet(unsortedIds);
                productIds = new ArrayList(productIdTree);
            } else {
                productIds = null;
            }

            if (productIds != null) {
                pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS", productIds);
                pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
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
                Debug.logWarning(e.getMessage());
                prod = null;
            }
            if (prod != null)
                products.add(prod);
        }

        pageContext.setAttribute(attributePrefix + "viewIndex", new Integer(viewIndex));
        pageContext.setAttribute(attributePrefix + "viewSize", new Integer(viewSize));
        pageContext.setAttribute(attributePrefix + "lowIndex", new Integer(lowIndex));
        pageContext.setAttribute(attributePrefix + "highIndex", new Integer(highIndex));
        pageContext.setAttribute(attributePrefix + "listSize", new Integer(listSize));
        pageContext.setAttribute(attributePrefix + "keywordString", keywordString);
        if (products.size() > 0)
            pageContext.setAttribute(attributePrefix + "searchProductList",products);
    }

    public static void getAssociatedProducts(PageContext pageContext, String productAttributeName, String assocPrefix) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getServletContext().getAttribute("delegator");
        GenericValue product = (GenericValue) pageContext.getAttribute(productAttributeName);
        if (product == null)
            return;

        try {
            Collection upgradeProducts = product.getRelatedByAndCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_UPGRADE"));
            Collection complementProducts = product.getRelatedByAndCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));
            Collection obsolescenceProducts = product.getRelatedByAndCache("AssocProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_OBSOLESCENCE"));
            Collection obsoleteByProducts = product.getRelatedByAndCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_OBSOLESCENCE"));

            //since ProductAssoc records have a fromDate and thruDate, we can filter by now so that only assocs in the date range are included
            upgradeProducts = EntityUtil.filterByDate(upgradeProducts);
            complementProducts = EntityUtil.filterByDate(complementProducts);
            obsolescenceProducts = EntityUtil.filterByDate(obsolescenceProducts);
            obsoleteByProducts = EntityUtil.filterByDate(obsoleteByProducts);

            if (upgradeProducts != null && upgradeProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
            if (complementProducts != null && complementProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "complement", complementProducts);
            if (obsolescenceProducts != null && obsolescenceProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "obsolescence", obsolescenceProducts);
            if (obsoleteByProducts != null && obsoleteByProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "obsoleteby", obsoleteByProducts);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
    }
}

