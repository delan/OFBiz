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
    /** Updates Product information according to UPDATE_MODE parameter
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String updateProduct(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        
        String updateMode = request.getParameter("UPDATE_MODE");
        if (updateMode == null || updateMode.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Update Mode was not specified, but is required.");
            Debug.logWarning("[ProductEvents.updateProduct] Update Mode was not specified, but is required");
            return "error";
        }

        //check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");

        //if this is a delete, do that before getting all of the non-pk parameters and validating them
        /* DEJ 01-09-10: Don't allow a delete for now, probably don't ever want to delete, just
         * remove from category(ies) and set discontinuation dates
        if(updateMode.equals("DELETE")) {
          GenericValue delProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
          if(delProduct != null) {
            //Remove associated/dependent entries from other tables here
            delProduct.removeRelated("ProductCategoryMember");
            delProduct.removeRelated("ProductKeyword");
            //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
            delProduct.remove();
            return "success";
          } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not find Product with ID" + productId + ", product not deleted.");
            return "error";
          }
         }
         */

        String primaryProductCategoryId = request.getParameter("PRIMARY_PRODUCT_CATEGORY_ID");
        String manufacturerPartyId = request.getParameter("MANUFACTURER_PARTY_ID");

        String introductionDateStr = request.getParameter("INTRODUCTION_DATE");
        java.sql.Date introductionDate = null;
        String salesDiscontinuationDateStr = request.getParameter("SALES_DISCONTINUATION_DATE");
        java.sql.Date salesDiscontinuationDate = null;
        String supportDiscontinuationDateStr = request.getParameter("SUPPORT_DISCONTINUATION_DATE");
        java.sql.Date supportDiscontinuationDate = null;

        String name = request.getParameter("NAME");
        String comment = request.getParameter("COMMENT");
        String description = request.getParameter("DESCRIPTION");
        String longDescription = request.getParameter("LONG_DESCRIPTION");

        String smallImageUrl = request.getParameter("SMALL_IMAGE_URL");
        String largeImageUrl = request.getParameter("LARGE_IMAGE_URL");

        String defaultPriceStr = request.getParameter("DEFAULT_PRICE");
        Double defaultPrice = null;

        String quantityUomId = request.getParameter("QUANTITY_UOM_ID");
        String quantityIncludedStr = request.getParameter("QUANTITY_INCLUDED");
        Double quantityIncluded = null;

        String weightUomId = request.getParameter("WEIGHT_UOM_ID");
        String weightStr = request.getParameter("WEIGHT");
        Double weight = null;

        String taxable = request.getParameter("TAXABLE");
        String autoCreateKeywords = request.getParameter("AUTO_CREATE_KEYWORDS");

        if (UtilValidate.isNotEmpty(introductionDateStr)) {
            try {
                introductionDate = UtilDateTime.toSqlDate(introductionDateStr);
            } catch (Exception e) {
                errMsg += "<li>Introduction Date is not a valid Date.";
            }
        }
        if (UtilValidate.isNotEmpty(salesDiscontinuationDateStr)) {
            try {
                salesDiscontinuationDate = UtilDateTime.toSqlDate(salesDiscontinuationDateStr);
            } catch (Exception e) {
                errMsg += "<li>Sales Discontinuation Date is not a valid Date.";
            }
        }
        if (UtilValidate.isNotEmpty(supportDiscontinuationDateStr)) {
            try {
                supportDiscontinuationDate = UtilDateTime.toSqlDate(supportDiscontinuationDateStr);
            } catch (Exception e) {
                errMsg += "<li>Support Discontinuation Date is not a valid Date.";
            }
        }

        if (UtilValidate.isNotEmpty(defaultPriceStr)) {
            try {
                defaultPrice = Double.valueOf(defaultPriceStr);
            } catch (Exception e) {
                errMsg += "<li>Default Price is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(quantityIncludedStr)) {
            try {
                quantityIncluded = Double.valueOf(quantityIncludedStr);
            } catch (Exception e) {
                errMsg += "<li>Quantity Included is not a valid number.";
            }
        }
        if (UtilValidate.isNotEmpty(weightStr)) {
            try {
                weight = Double.valueOf(weightStr);
            } catch (Exception e) {
                errMsg += "<li>Weight is not a valid number.";
            }
        }

        if (!UtilValidate.isNotEmpty(name))
            errMsg += "<li>Name is missing.";
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        Collection toBeStored = new LinkedList();
        GenericValue product = delegator.makeValue("Product", null);
        toBeStored.add(product);
        product.set("productId", productId);
        product.set("primaryProductCategoryId", primaryProductCategoryId);
        product.set("manufacturerPartyId", manufacturerPartyId);
        product.set("introductionDate", introductionDate);
        product.set("salesDiscontinuationDate", salesDiscontinuationDate);
        product.set("supportDiscontinuationDate", supportDiscontinuationDate);
        product.set("productName", name);
        product.set("comments", comment);
        product.set("description", description);
        product.set("longDescription", longDescription);
        product.set("smallImageUrl", smallImageUrl);
        product.set("largeImageUrl", largeImageUrl);
        product.set("defaultPrice", defaultPrice);
        product.set("quantityUomId", quantityUomId);
        product.set("quantityIncluded", quantityIncluded);
        product.set("weightUomId", weightUomId);
        product.set("weight", weight);
        product.set("taxable", taxable);
        product.set("autoCreateKeywords", autoCreateKeywords);

        if (UtilValidate.isNotEmpty(primaryProductCategoryId)) {
            GenericValue dummyValue = null;
            try {
                Collection dummyCol = EntityUtil.filterByDate(
                        delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productId", productId, "productCategoryId", primaryProductCategoryId), null));
                dummyValue = EntityUtil.getFirst(dummyCol);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                dummyValue = null;
            }
            if (dummyValue == null) {
                toBeStored.add( delegator.makeValue("ProductCategoryMember",
                        UtilMisc.toMap("productId", productId, "productCategoryId", primaryProductCategoryId, "fromDate", UtilDateTime.nowTimestamp())));
                delegator.clearCacheLine("ProductCategoryMember", UtilMisc.toMap("productCategoryId", primaryProductCategoryId));
            }
        }

        if (updateMode.equals("CREATE")) {
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create product (write error)");
                return "error";
            }
            if (product != null && !"n".equalsIgnoreCase(product.getString("autoCreateKeywords"))) {
                try {
                    KeywordSearch.induceKeywords(product);
                } catch (GenericEntityException e) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create keywords (write error).");
                    return "error";
                }
            }
        } else if (updateMode.equals("UPDATE")) {
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not update product (write error)");
                Debug.logWarning("[ProductEvents.updateProduct] Could not update product (write error); message: " + e.getMessage());
                return "error";
            }
            if (product != null && !"n".equalsIgnoreCase(product.getString("autoCreateKeywords"))) {
                try {
                    KeywordSearch.induceKeywords(product);
                } catch (GenericEntityException e) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not create keywords (write error).");
                    return "error";
                }
            }
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Specified update mode: \"" + updateMode + "\" is not supported.");
            return "error";
        }

        return "success";
    }

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
            GenericValue productKeyword = delegator.makeValue("ProductKeyword", UtilMisc.toMap("productId", productId, "keyword", keyword));
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
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo));
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
        Timestamp thruDate = null;
        Double quantity = null;

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
        if (errMsg.length() > 0) {
            errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg);
            return "error";
        }

        tempProductAssoc.set("thruDate", thruDate);
        tempProductAssoc.set("reason", reason);
        tempProductAssoc.set("instruction", instruction);
        tempProductAssoc.set("quantity", quantity);

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
}
