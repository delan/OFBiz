/*
 * $Id: ProductSearchEvents.java,v 1.1 2003/12/22 21:47:13 jonesde Exp $
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.product.product.ProductSearch.ProductSearchContext;
import org.ofbiz.product.product.ProductSearch.ResultSortOrder;

/**
 * Product Search Related Events
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class ProductSearchEvents {
    
    public static final String module = ProductSearchEvents.class.getName();

    /** PUT DESCRIPTION HERE 
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String searchRemoveFromCategory(HttpServletRequest request, HttpServletResponse response) {
        EntityListIterator eli = getProductSearchResults(request);
        if (eli == null) {
            request.setAttribute("_ERROR_MESSAGE_", "No results found, probably because there was an error or were no constraints.");
            return "error";
        }
        
        try {
            boolean beganTransaction = TransactionUtil.begin();
            try {
                
                GenericValue searchResultView = null;
                while ((searchResultView = (GenericValue) eli.next()) != null) {
                    // TODO: do the operation here
                }
                
                eli.close();
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                String errMsg = "Error getting search results: " + e.toString();
                Debug.logError(e, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                TransactionUtil.rollback(beganTransaction);
                return "error";
            }
        } catch (GenericTransactionException e) {
            String errMsg = "Error getting search results: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        return "success";
    }
    
    public static EntityListIterator getProductSearchResults(HttpServletRequest request) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String visitId = VisitHandler.getVisitId(session);
        
        List productSearchConstraintList = (List) session.getAttribute(ProductSearchSession.PRODUCT_SEARCH_CONSTRAINT_LIST);
        // if no constraints, don't do a search...
        if (productSearchConstraintList != null && productSearchConstraintList.size() > 0) {
            ResultSortOrder resultSortOrder = ProductSearchSession.getResultSortOrder(session);
            ProductSearchContext productSearchContext = new ProductSearchContext(delegator, visitId);
            productSearchContext.addProductSearchConstraints(productSearchConstraintList);
            productSearchContext.setResultSortOrder(resultSortOrder);

            return productSearchContext.doQuery(delegator);
        } else {
            return null;
        }
    }
}
