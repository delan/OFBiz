/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.catalog;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;

import org.ofbiz.commonapp.product.product.*;

/**
 * Product Information Related Events
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 21, 2001
 *@version    1.0
 */
public class ProductEvents {
    /** Updates ProductKeyword information according to UPDATE_MODE parameter, only support CREATE and DELETE, no modify becuse all fields are PKs
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateProductKeyword(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Update Mode was not specified, but is required.");
            Debug.logWarning("[ProductEvents.updateProductKeyword] Update Mode was not specified, but is required");
            return "error";
        }

        //check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");
        String keyword = request.getParameter("KEYWORD");
        String relevancyWeight = request.getParameter("relevancyWeight");

        if (!UtilValidate.isNotEmpty(productId))
            errMsg += "<li>Product ID is missing.";
        if (!UtilValidate.isNotEmpty(keyword))
            errMsg += "<li>Keyword is missing.";
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        if (updateMode.equals("CREATE")) {
            GenericValue productKeyword = delegator.makeValue("ProductKeyword", UtilMisc.toMap("productId", productId, "keyword", keyword, "relevancyWeight", relevancyWeight));
            GenericValue newValue = null;
            try {
                newValue = delegator.findByPrimaryKey(productKeyword.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                newValue = null;
            }

            if (newValue != null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create product-keyword entry (already exists)");
                return "error";
            }

            try {
                productKeyword = productKeyword.create();
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                productKeyword = null;
            }
            if (productKeyword == null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create product-keyword entry (write error)");
                return "error";
            }
        } else if (updateMode.equals("DELETE")) {
            GenericValue productKeyword = null;
            try {
                productKeyword = delegator.findByPrimaryKey("ProductKeyword", UtilMisc.toMap("productId", productId, "keyword", keyword));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                productKeyword = null;
            }
            if (productKeyword == null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove product-keyword (does not exist)");
                return "error";
            }
            try {
                productKeyword.remove();
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove product-keyword (write error)");
                Debug.logWarning("[ProductEvents.updateProductKeyword] Could not remove product-keyword (write error); message: " + e.getMessage());
                return "error";
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }

    /** Update (create/induce or delete) all keywords for a given Product
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateProductKeywords(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Update Mode was not specified, but is required.");
            Debug.logWarning("[ProductEvents.updateProductKeywords] Update Mode was not specified, but is required");
            return "error";
        }

        //check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");
        if (!UtilValidate.isNotEmpty(productId)) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No Product ID specified, cannot update keywords.");
            return "error";
        }

        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            product = null;
        }
        if (product == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Product with ID \"" + productId + "\", not found; cannot update keywords.");
            return "error";
        }

        if (updateMode.equals("CREATE")) {
            try {
                KeywordSearch.induceKeywords(product);
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create keywords (write error).");
                return "error";
            }
        } else if (updateMode.equals("DELETE")) {
            try {
                product.removeRelated("ProductKeyword");
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove product-keywords (write error)");
                Debug.logWarning("[ProductEvents.updateProductKeywords] Could not remove product-keywords (write error); message: " + e.getMessage());
                return "error";
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }

    /** Updates/adds keywords for all products
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateAllKeywords(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = "CREATE";
        //check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
            return "error";
        }

        Iterator iterator = null;
        try {
            iterator = UtilMisc.toIterator(delegator.findAll("Product", null));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            iterator = null;
        }

        int numProds = 0;
        while (iterator != null && iterator.hasNext()) {
            GenericValue product = (GenericValue) iterator.next();
            if (product != null && !"n".equalsIgnoreCase(product.getString("autoCreateKeywords"))) {
                try {
                    KeywordSearch.induceKeywords(product);
                } catch (GenericEntityException e) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create keywords (write error).");
                    Debug.logWarning("[ProductEvents.updateAllKeywords] Could not create product-keyword (write error); message: " + e.getMessage());
                    return "error";
                }
            }
            numProds++;
        }

        request.setAttribute(SiteDefs.EVENT_MESSAGE, "Keyword creation complete for " + numProds + " products.");
        return "success";
    }

    /** Updates ProductAssoc information according to UPDATE_MODE parameter
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateProductAssoc(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Update Mode was not specified, but is required.");
            Debug.logWarning("[ProductEvents.updateProductAssoc] Update Mode was not specified, but is required");
            return "error";
        }

        //check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");
        String productIdTo = request.getParameter("PRODUCT_ID_TO");
        String productAssocTypeId = request.getParameter("PRODUCT_ASSOC_TYPE_ID");
        String fromDateStr = request.getParameter("FROM_DATE");
        Timestamp fromDate = null;

        try {
            if (delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)) == null)
                errMsg += "<li>Product with id " + productId + " not found.";
            if (delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productIdTo)) == null)
                errMsg += "<li>Product To with id " + productIdTo + " not found.";
        } catch (GenericEntityException e) {
            //if there is an exception for either, the other probably wont work
            Debug.logWarning(e);
        }

        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = Timestamp.valueOf(fromDateStr);
            } catch (Exception e) {
                errMsg += "<li>From Date not formatted correctly.";
            }
        }
        if (!UtilValidate.isNotEmpty(productId))
            errMsg += "<li>Product ID is missing.";
        if (!UtilValidate.isNotEmpty(productIdTo))
            errMsg += "<li>Product ID To is missing.";
        if (!UtilValidate.isNotEmpty(productAssocTypeId))
            errMsg += "<li>Association Type ID is missing.";
        //from date is only required if update mode is not CREATE
        if (!updateMode.equals("CREATE") && !UtilValidate.isNotEmpty(fromDateStr))
            errMsg += "<li>From Date is missing.";
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        //clear some cache entries
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", productAssocTypeId));

        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId));

        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productAssocTypeId", productAssocTypeId));
        delegator.clearCacheLine("ProductAssoc",
                UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));

        GenericValue tempProductAssoc = delegator.makeValue("ProductAssoc",
                UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));
        if (updateMode.equals("DELETE")) {
            GenericValue productAssoc = null;
            try {
                productAssoc = delegator.findByPrimaryKey(tempProductAssoc.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                productAssoc = null;
            }
            if (productAssoc == null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove product association (does not exist)");
                return "error";
            }
            try {
                productAssoc.remove();
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not remove product association (write error)");
                Debug.logWarning("[ProductEvents.updateProductAssoc] Could not remove product association (write error); message: " + e.getMessage());
                return "error";
            }
            return "success";
        }

        String thruDateStr = request.getParameter("THRU_DATE");
        String reason = request.getParameter("REASON");
        String instruction = request.getParameter("INSTRUCTION");
        String quantityStr = request.getParameter("QUANTITY");
        String sequenceNumStr = request.getParameter("SEQUENCE_NUM");
        Timestamp thruDate = null;
        Double quantity = null;
        Long sequenceNum = null;

        if (UtilValidate.isNotEmpty(thruDateStr)) {
            try {
                thruDate = Timestamp.valueOf(thruDateStr);
            } catch (Exception e) {
                errMsg += "<li>Thru Date not formatted correctly.";
            }
        }
        if (UtilValidate.isNotEmpty(quantityStr)) {
            try {
                quantity = Double.valueOf(quantityStr);
            } catch (Exception e) {
                errMsg += "<li>Quantity not formatted correctly.";
            }
        }
        if (UtilValidate.isNotEmpty(sequenceNumStr)) {
            try {
                sequenceNum = Long.valueOf(sequenceNumStr);
            } catch (Exception e) {
                errMsg += "<li>SequenceNum not formatted correctly.";
            }
        }
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        tempProductAssoc.set("thruDate", thruDate);
        tempProductAssoc.set("reason", reason);
        tempProductAssoc.set("instruction", instruction);
        tempProductAssoc.set("quantity", quantity);
        tempProductAssoc.set("sequenceNum", sequenceNum);

        if (updateMode.equals("CREATE")) {
            //if no from date specified, set to now
            if (fromDate == null) {
                fromDate = new Timestamp(new java.util.Date().getTime());
                tempProductAssoc.set("fromDate", fromDate);
                request.setAttribute("ProductAssocCreateFromDate", fromDate);
            }

            GenericValue productAssoc = null;
            try {
                productAssoc = delegator.findByPrimaryKey(tempProductAssoc.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                productAssoc = null;
            }
            if (productAssoc != null) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create product association (already exists)");
                return "error";
            }
            try {
                productAssoc = tempProductAssoc.create();
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create product association (write error)");
                Debug.logWarning("[ProductEvents.updateProductAssoc] Could not create product association (write error); message: " + e.getMessage());
                return "error";
            }
        } else if (updateMode.equals("UPDATE")) {
            try {
                tempProductAssoc.store();
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not update product association (write error)");
                Debug.logWarning("[ProductEvents.updateProductAssoc] Could not update product association (write error); message: " + e.getMessage());
                return "error";
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }

    public static String updateAttribute(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Update Mode was not specified, but is required.");
            Debug.logWarning("[CategoryEvents.updateCategory] Update Mode was not specified, but is required");
            return "error";
        }

        //check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");
        String attrName = request.getParameter("ATTRIBUTE_NAME");
        String attrValue = request.getParameter("ATTRIBUTE_VALUE");
        String attrType = request.getParameter("ATTRIBUTE_TYPE");

        if (!UtilValidate.isNotEmpty(productId))
            errMsg += "<li>Product ID is missing.";
        if (!UtilValidate.isNotEmpty(productId))
            errMsg += "<li>Attribute name is missing.";
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        List toBeStored = new LinkedList();
        GenericValue attribute = delegator.makeValue("ProductAttribute", null);
        toBeStored.add(attribute);
        attribute.set("productId", productId);
        attribute.set("attrName", attrName);
        attribute.set("attrValue", attrValue);
        attribute.set("attrType", attrType);

        if (updateMode.equals("CREATE")) {
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create attribute (write error)");
                return "error";
            }
        } else if (updateMode.equals("UPDATE")) {
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not update attribute (write error)");
                Debug.logWarning("[ProductEvents.updateAttribute] Could not update attribute (write error); message: " + e.getMessage());
                return "error";
            }
        } else if (updateMode.equals("DELETE")) {
            try {
                delegator.removeByAnd("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", attrName));
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not delete attribute (write error)");
                Debug.logWarning("[ProductEvents.updateAttribute] Could not delete attribute (write error); message: " + e.getMessage());
                return "error";
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }
}
