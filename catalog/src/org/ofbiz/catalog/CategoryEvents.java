package org.ofbiz.catalog;

import javax.servlet.http.*;
import javax.servlet.*;
import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;

/**
 * <p><b>Title:</b> Product Category Related Events
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 21, 2001
 *@version    1.0
 */
public class CategoryEvents {
  /** Updates Category information according to UPDATE_MODE parameter
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String updateCategory(HttpServletRequest request, HttpServletResponse response) {
    String errMsg = "";
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    Security security = (Security)request.getAttribute("security");

    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
      Debug.logWarning("[CategoryEvents.updateCategory] Update Mode was not specified, but is required");
      return "error";
    }
    
    //check permissions before moving on...
    if(!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
      request.setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
      return "error";
    }
    
    String productCategoryId = request.getParameter("PRODUCT_CATEGORY_ID");

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    /* DEJ 01-09-11: Don't allow a delete for now, probably don't ever want to delete, just 
     * remove all tables associating/relating the category to other things...
    if(updateMode.equals("DELETE")) {
      GenericValue delCategory = delegator.findByPrimaryKey("Category", UtilMisc.toMap("productCategoryId", productCategoryId));
      if(delCategory != null) {
        //Remove associated/dependent entries from other tables here
        delCategory.removeRelated("ProductCategoryMember");
        //delCategory.removeRelated("...");
        //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
        delCategory.remove();
        return "success";
      }
      else {
        request.setAttribute("ERROR_MESSAGE", "Could not find Product Category with ID" + productCategoryId + ", category not deleted.");
        return "error";
      }
    }
    */
    
    String primaryParentCategoryId = request.getParameter("PRIMARY_PARENT_CATEGORY_ID");
    String description = request.getParameter("DESCRIPTION");
    String longDescription = request.getParameter("LONG_DESCRIPTION");
    String categoryImageUrl = request.getParameter("CATEGORY_IMAGE_URL");

    if(!UtilValidate.isNotEmpty(productCategoryId)) errMsg += "<li>Product Category ID is missing.";
    if(errMsg.length() > 0) {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }

    GenericValue category = delegator.makeValue("ProductCategory", null);
    category.set("productCategoryId", productCategoryId);
    category.set("primaryParentCategoryId", primaryParentCategoryId);
    category.set("description", description);
    category.set("longDescription", longDescription);
    category.set("categoryImageUrl", categoryImageUrl);
    
    if(UtilValidate.isNotEmpty(primaryParentCategoryId)) {
      category.preStoreOther(delegator.makeValue("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", primaryParentCategoryId)));
    }
    
    if(updateMode.equals("CREATE")) {
      GenericValue newCategory = null;
      try { newCategory = delegator.create(category); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); newCategory = null; }
      if(newCategory == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not create category (write error)");
        return "error";
      }
    }
    else if(updateMode.equals("UPDATE")) {
      try { category.store(); }
      catch(GenericEntityException e) {
        request.setAttribute("ERROR_MESSAGE", "Could not update category (write error)");
        Debug.logWarning("[CategoryEvents.updateCategory] Could not update category (write error); message: " + e.getMessage());
        return "error";
      }
    }
    else {
      request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
      return "error";
    }
    
    return "success";
  }
  
  /** Updates Product Category Member information according to UPDATE_MODE parameter
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String updateProductCategoryMember(HttpServletRequest request, HttpServletResponse response) {
    String errMsg = "";
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    Security security = (Security)request.getAttribute("security");

    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
      Debug.logWarning("[ProductEvents.updateProductCategoryMember] Update Mode was not specified, but is required");
      return "error";
    }

    //check permissions before moving on...
    if(!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
      request.setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
      return "error";
    }

    String productId = request.getParameter("PRODUCT_ID");
    String productCategoryId = request.getParameter("PRODUCT_CATEGORY_ID");
    
    if(!UtilValidate.isNotEmpty(productId)) errMsg += "<li>Product ID is missing.";
    if(!UtilValidate.isNotEmpty(productCategoryId)) errMsg += "<li>Product Category ID is missing.";
    if(errMsg.length() > 0) {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
        
    if(updateMode.equals("CREATE")) {
      GenericValue productCategoryMember = delegator.makeValue("ProductCategoryMember", UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId));
      GenericValue dummyValue = null;
      try { dummyValue = delegator.findByPrimaryKey(productCategoryMember.getPrimaryKey()); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); dummyValue = null; }
      if(dummyValue != null) {
        request.setAttribute("ERROR_MESSAGE", "Could not create product-category entry (already exists)");
        return "error";
      }
      try { productCategoryMember = productCategoryMember.create(); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); productCategoryMember = null; }
      if(productCategoryMember == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not create product-category entry (write error)");
        return "error";
      }
    }
    else if(updateMode.equals("DELETE")) {
      GenericValue productCategoryMember = null;
      try { productCategoryMember = delegator.findByPrimaryKey("ProductCategoryMember", UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId)); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); productCategoryMember = null; }
      if(productCategoryMember == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not remove product-category (does not exist)");
        return "error";
      }
      try { productCategoryMember.remove(); }
      catch(GenericEntityException e) {
        request.setAttribute("ERROR_MESSAGE", "Could not remove product-category (write error)");
        Debug.logWarning("[ProductEvents.updateProductCategoryMember] Could not remove product-category (write error); message: " + e.getMessage());
        return "error";
      }
    }
    else {
      request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
      return "error";
    }
    
    return "success";
  }

  /** Updates Product Category Rollup information according to UPDATE_MODE parameter
   *@param request The HTTPRequest object for the current request
   *@param response The HTTPResponse object for the current request
   *@return String specifying the exit status of this event
   */
  public static String updateProductCategoryRollup(HttpServletRequest request, HttpServletResponse response) {
    String errMsg = "";
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    Security security = (Security)request.getAttribute("security");

    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
      Debug.logWarning("[ProductEvents.updateProductCategoryRollup] Update Mode was not specified, but is required");
      return "error";
    }

    //check permissions before moving on...
    if(!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
      request.setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " CATALOG (CATALOG_" + updateMode + " or CATALOG_ADMIN needed).");
      return "error";
    }

    String productCategoryId = request.getParameter("UPDATE_PRODUCT_CATEGORY_ID");
    String parentProductCategoryId = request.getParameter("UPDATE_PARENT_PRODUCT_CATEGORY_ID");
    
    if(!UtilValidate.isNotEmpty(productCategoryId)) errMsg += "<li>Product Category ID is missing.";
    if(!UtilValidate.isNotEmpty(parentProductCategoryId)) errMsg += "<li>Parent Product Category ID is missing.";
    if(errMsg.length() > 0) {
      errMsg = "<b>The following errors occured:</b><br><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
        
    if(updateMode.equals("CREATE")) {
      GenericValue productCategoryRollup = delegator.makeValue("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", parentProductCategoryId));
      GenericValue dummyValue = null;
      try { dummyValue = delegator.findByPrimaryKey(productCategoryRollup.getPrimaryKey()); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); dummyValue = null; }
      if(dummyValue != null) {
        request.setAttribute("ERROR_MESSAGE", "Could not create product-category entry (already exists)");
        return "error";
      }
      delegator.clearCacheLine("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId", productCategoryRollup.get("parentProductCategoryId")));
      try { productCategoryRollup = productCategoryRollup.create(); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); productCategoryRollup = null; }
      if(productCategoryRollup == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not create product-category entry (write error)");
        return "error";
      }
    }
    else if(updateMode.equals("DELETE")) {
      GenericValue productCategoryRollup = null;
      try { productCategoryRollup = delegator.findByPrimaryKey("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId, "parentProductCategoryId", parentProductCategoryId)); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); productCategoryRollup = null; }
      if(productCategoryRollup == null) {
        request.setAttribute("ERROR_MESSAGE", "Could not remove product-category (does not exist)");
        return "error";
      }
      delegator.clearCacheLine("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId", productCategoryRollup.get("parentProductCategoryId")));
      try { productCategoryRollup.remove(); }
      catch(GenericEntityException e) {
        request.setAttribute("ERROR_MESSAGE", "Could not remove product-category (write error)");
        Debug.logWarning("[ProductEvents.updateProductCategoryRollup] Could not remove product-category (write error); message: " + e.getMessage());
        return "error";
      }
    }
    else {
      request.setAttribute("ERROR_MESSAGE", "Specified update mode: \"" + updateMode + "\" is not supported.");
      return "error";
    }
    
    return "success";
  }
}
