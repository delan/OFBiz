/*
 * $Id$
 * $Log$
 * Revision 1.22  2001/09/25 23:05:22  jonesde
 * Added cross sell, up sell, and obsolete product association support.
 *
 * Revision 1.21  2001/09/25 14:42:12  epabst
 * added password hint
 * added getCurrentPartyContactMechList helper method
 * created (via refactoring) setPassword method to validate and set it and the hint
 * fixed minor bugs
 * fixed minor formatting
 *
 * Revision 1.20  2001/09/23 12:19:31  jonesde
 * Added comment
 *
 * Revision 1.19  2001/09/23 12:06:46  jonesde
 * Fixed, and finished, the side category box for complete hierarchy and position maintenance.
 *
 * Revision 1.18  2001/09/19 08:42:08  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.17  2001/09/12 18:49:45  jonesde
 * Fixed subsequent keyword search error.
 *
 * Revision 1.16  2001/09/12 14:55:55  jonesde
 * Cleanups, added cache optimizations.
 *
 * Revision 1.15  2001/09/11 00:51:46  jonesde
 * A few changes to correspond with the moving of the KeywordSearch file, etc.
 *
 * Revision 1.14  2001/09/06 16:23:19  jonesde
 * Fixed null pointer problem in the crumb/trail stuff
 *
 * Revision 1.13  2001/09/06 03:48:29  azeneski
 * Catalog trail partially working. Need to add in recursion.
 *
 * Revision 1.12  2001/09/05 20:45:54  jonesde
 * Major speedup by adding cache calls.
 *
 * Revision 1.11  2001/09/05 18:49:12  azeneski
 * Updates (not finished)
 *
 * Revision 1.10  2001/09/05 16:52:44  jonesde
 * Changed get related products to use ProductCategoryMember instead of Product.primaryProductCategoryId
 *
 * Revision 1.9  2001/09/05 16:34:06  jonesde
 * Added result set prev/next in cateogry
 *
 * Revision 1.8  2001/09/05 04:04:44  azeneski
 * Updated. (not finished)
 *
 * Revision 1.7  2001/09/05 01:35:22  azeneski
 * Update.. (not finished)
 *
 * Revision 1.6  2001/09/05 00:30:14  jonesde
 * Initial keyword search implementation in place.
 *
 * Revision 1.5  2001/09/04 20:48:57  azeneski
 * Catalog updates. (not finished)
 *
 * Revision 1.4  2001/09/04 18:09:49  azeneski
 * Updated CatalogHelper to allow passing the name of the attribute to store
 * catalog data in. Used in the CORE taglibs.
 *
 * Revision 1.3  2001/09/01 01:59:44  azeneski
 * Cleaned up catalog files, using new taglibs.
 *
 * Revision 1.2  2001/08/27 17:29:31  epabst
 * simplified
 *
 * Revision 1.1.1.1  2001/08/24 01:01:43  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.TreeSet;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilFormatOut;

import org.ofbiz.core.entity.GenericPK;
import org.ofbiz.core.entity.GenericEntity;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;

import org.ofbiz.commonapp.product.product.KeywordSearch;

import org.ofbiz.ecommerce.shoppingcart.*;

/**
 * <p><b>Title:</b> CatalogHelper.java
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
public class CatalogHelper {
  
  public static void getRelatedCategories(PageContext pageContext, String attributeName) {
    getRelatedCategories(pageContext,attributeName,null);
  }
  
  public static void getRelatedCategories(PageContext pageContext, String attributeName, String parentId) {
    ArrayList categories = new ArrayList();
    ServletRequest request = pageContext.getRequest();
    String requestId = null;
    
    requestId = UtilFormatOut.checkNull(request.getParameter("catalog_id"), request.getParameter("CATALOG_ID"),
    request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));
    if(parentId == null && requestId.equals("")) return;
    if(parentId == null) parentId = requestId;
    
    Debug.logInfo("[CatalogHelper.getRelatedCategories] ParentID: " + parentId);
    Debug.logInfo("[CatalogHelper.getRelatedCategories] RequestID: " + requestId);
    if(parentId.equals(requestId)) setTrail(pageContext,parentId);
    
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    Collection rollups = null;
    try { rollups = delegator.findByAndCache("ProductCategoryRollup",UtilMisc.toMap("parentProductCategoryId",parentId),null); }
    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); rollups = null; }
    if(rollups != null && rollups.size() > 0) {
      //Debug.log("Rollup size: " + rollups.size());
      Iterator ri = rollups.iterator();
      while ( ri.hasNext() ) {
        GenericValue parent = (GenericValue) ri.next();
        //Debug.log("Adding child of: " + parent.getString("parentProductCategoryId"));
        GenericValue cv = null;
        try { cv = parent.getRelatedOneCache("CurrentProductCategory"); }
        catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); cv = null; }
        if(cv != null) categories.add(cv);
      }
    }
    
    if(categories.size() > 0) pageContext.setAttribute(attributeName,categories);
  }
  
  public static void getRelatedProducts(PageContext pageContext, String attributeName) {
    getRelatedProducts(pageContext,attributeName,null);
  }
  
  /**
   * Puts the following into the pageContext attribute list with a prefix, if specified:
   *  productList, categoryId, viewIndex, viewSize, lowIndex, highIndex, listSize
   * Puts the following into the session attribute list:
   *  CACHE_SEARCH_RESULTS, CACHE_SEARCH_RESULTS_NAME
   *@param pageContext The pageContext of the calling JSP
   *@param attributePrefix A prefix to put on each attribute name in the pageContext
   *@param parentId The ID of the parent category
   */
  public static void getRelatedProducts(PageContext pageContext, String attributePrefix, String parentId) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    ServletRequest request = pageContext.getRequest();
    if(attributePrefix == null) attributePrefix = "";
    
    int viewIndex = 0;
    try { viewIndex = Integer.valueOf((String)pageContext.getRequest().getParameter("VIEW_INDEX")).intValue(); }
    catch (Exception e) { viewIndex = 0; }
    
    int viewSize = 10;
    try { viewSize = Integer.valueOf((String)pageContext.getRequest().getParameter("VIEW_SIZE")).intValue(); }
    catch (Exception e) { viewSize = 10; }
    
    if(parentId == null)
      parentId = UtilFormatOut.checkNull(request.getParameter("catalog_id"), request.getParameter("CATALOG_ID"),
              request.getParameter("category_id"), request.getParameter("CATEGORY_ID"));
    if(parentId == null || parentId.length() <= 0) return;
    
    String curFindString = "ProductCategoryByParentId:" + parentId;
    
    ArrayList prodCatMembers = (ArrayList)pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS");
    String resultArrayName = (String)pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS_NAME");
    
    if(prodCatMembers == null || resultArrayName == null || !curFindString.equals(resultArrayName) || viewIndex == 0) {
      //since cache is invalid, should not use prodCatMembers
      prodCatMembers = null;
      
      Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
      Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);
      
      GenericValue category = null;
      try { category = delegator.findByPrimaryKeyCache("ProductCategory",UtilMisc.toMap("productCategoryId",parentId)); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); category = null; }
      
      if(category != null) {
        Collection prodCatMemberCol = null;
        try { prodCatMemberCol = category.getRelated("ProductCategoryMember"); }
        catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); prodCatMemberCol = null; }
        if(prodCatMemberCol != null) prodCatMembers = new ArrayList(prodCatMemberCol);
      } 
      
      if(prodCatMembers != null) {
        //XXX should be synchronized from multiple clients thrashing eachother (?) - how?
        pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS", prodCatMembers);
        pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
      }
    }
    
    int lowIndex = viewIndex*viewSize+1;
    int highIndex = (viewIndex+1)*viewSize;
    int listSize = 0;
    if(prodCatMembers!=null) listSize = prodCatMembers.size();
    if(listSize<highIndex) highIndex=listSize;
    
    ArrayList someProducts = new ArrayList();
    if(prodCatMembers != null) {
      for(int ind=lowIndex; ind<=highIndex; ind++) {
        GenericValue prodCatMember = (GenericValue)prodCatMembers.get(ind-1);
        GenericValue prod = null;
        try { prod = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", prodCatMember.get("productId"))); }
        catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); prod = null; }
        if(prod != null) someProducts.add(prod);
      }
    }
    
    pageContext.setAttribute(attributePrefix + "viewIndex", new Integer(viewIndex));
    pageContext.setAttribute(attributePrefix + "viewSize", new Integer(viewSize));
    pageContext.setAttribute(attributePrefix + "lowIndex", new Integer(lowIndex));
    pageContext.setAttribute(attributePrefix + "highIndex", new Integer(highIndex));
    pageContext.setAttribute(attributePrefix + "listSize", new Integer(listSize));
    pageContext.setAttribute(attributePrefix + "categoryId", parentId);
    if(someProducts.size() > 0) pageContext.setAttribute(attributePrefix + "productList",someProducts);
  }
  
  public static void getProduct(PageContext pageContext, String attributeName) {
    getProduct(pageContext,attributeName,null);
  }
  
  public static void getProduct(PageContext pageContext, String attributeName, String productId) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    ServletRequest request = pageContext.getRequest();
    
    if ( productId == null )
      productId = UtilFormatOut.checkNull(request.getParameter("product_id"),request.getParameter("PRODUCT_ID"));
    
    if ( productId.equals("") )
      return;
    
    GenericValue product = null;
    try { product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId", productId)); }
    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); product = null; }
    if (product != null) pageContext.setAttribute(attributeName,product);
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
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    
    int viewIndex = 0;
    try { viewIndex = Integer.valueOf((String)pageContext.getRequest().getParameter("VIEW_INDEX")).intValue(); }
    catch (Exception e) { viewIndex = 0; }
    
    int viewSize = 10;
    try { viewSize = Integer.valueOf((String)pageContext.getRequest().getParameter("VIEW_SIZE")).intValue(); }
    catch (Exception e) { viewSize = 10; }
    
    String keywordString = pageContext.getRequest().getParameter("SEARCH_STRING");
    String curFindString = "KeywordSearch:" + keywordString;
    
    ArrayList productIds = (ArrayList)pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS");
    String resultArrayName = (String)pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS_NAME");
    if(productIds == null || resultArrayName == null || !curFindString.equals(resultArrayName) || viewIndex == 0) {
      Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
      Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);
      
      //sort by productId (only available sort for now...)
      Collection unsortedIds = KeywordSearch.productsByKeywords(keywordString, delegator);
      if(unsortedIds != null && unsortedIds.size() > 0) {
        TreeSet productIdTree = new TreeSet(unsortedIds);
        productIds = new ArrayList(productIdTree);
      }
      else {
        productIds = null;
      }
      
      if(productIds != null) {
        pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS", productIds);
        pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
      }
    }
    
    int lowIndex = viewIndex*viewSize+1;
    int highIndex = (viewIndex+1)*viewSize;
    int listSize = 0;
    if(productIds!=null) listSize = productIds.size();
    if(listSize<highIndex) highIndex=listSize;
    
    ArrayList products = new ArrayList();
    for(int ind=lowIndex; ind<=highIndex; ind++) {
      GenericValue prod = null;
      try { prod = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productIds.get(ind-1))); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); prod = null; }
      if(prod != null) products.add(prod);
    }
    
    pageContext.setAttribute(attributePrefix + "viewIndex", new Integer(viewIndex));
    pageContext.setAttribute(attributePrefix + "viewSize", new Integer(viewSize));
    pageContext.setAttribute(attributePrefix + "lowIndex", new Integer(lowIndex));
    pageContext.setAttribute(attributePrefix + "highIndex", new Integer(highIndex));
    pageContext.setAttribute(attributePrefix + "listSize", new Integer(listSize));
    pageContext.setAttribute(attributePrefix + "keywordString", keywordString);
    if(products.size() > 0) pageContext.setAttribute(attributePrefix + "searchProductList",products);
  }
  
  public static void setTrail(PageContext pageContext, String currentCategory) {
    String previousCategory = pageContext.getRequest().getParameter("pcategory");
    Debug.logInfo("[CatalogHelper.setTrail] Start: previousCategory=" + previousCategory + " currentCategory=" + currentCategory);

    //if there is no current category, just return and do nothing to that the last settings will stay
    if(currentCategory == null || currentCategory.length() <= 0) return;
    
    //always get the last crumb list
    ArrayList crumb = getTrail(pageContext);
    if(crumb == null) crumb = new ArrayList();
        
    //if no previous category was specified, check to see if currentCategory is in the list
    if(previousCategory == null || previousCategory.length() <= 0) {
      if(crumb.contains(currentCategory)) {
        //if cur category is in crumb, remove everything after it and return
        int cindex = crumb.lastIndexOf(currentCategory);
        if(cindex < (crumb.size() - 1)) {
          for(int i = crumb.size() -1; i>cindex; i--) {
            String deadCat = (String)crumb.remove(i);
            Debug.logInfo("[CatalogHelper.setTrail] Removed after current category index: " + i + " catname: " + deadCat);
          }
        }
        return;
      }
      else {
        //current category is not in the list, and no previous category was specified, go back to the beginning
        previousCategory = "TOP";
        crumb.clear();
        crumb.add(previousCategory);
        Debug.logInfo("[CatalogHelper.setTrail] Starting new list, added previousCategory: " + previousCategory);
      }
    }
    
    if(!crumb.contains(previousCategory)) {
      //previous category was NOT in the list, ERROR, start over
      Debug.logInfo("[CatalogHelper.setTrail] ERROR: previousCategory (" + previousCategory + ") was not in the crumb list, position is lost, starting over with TOP");
      previousCategory = "TOP";
      crumb.clear();
      crumb.add(previousCategory);
    }
    else {    
      //remove all categories after the previous category, preparing for adding the current category
      int index = crumb.indexOf(previousCategory);
      if(index < (crumb.size() - 1)) {
        for(int i = crumb.size() -1; i>index; i--) {
          String deadCat = (String)crumb.remove(i);
          Debug.logInfo("[CatalogHelper.setTrail] Removed after previous category index: " + i + " catname: " + deadCat);
        }
      }
    }
    
    //add the current category to the end of the list
    crumb.add(currentCategory);
    Debug.logInfo("[CatalogHelper.setTrail] Continuing list: Added currentCategory: " + currentCategory);
    setTrail(pageContext, crumb);
  }
  
  public static ArrayList getTrail(PageContext pageContext) {
    HttpSession session = pageContext.getSession();
    ArrayList crumb = (ArrayList)session.getAttribute("_BREAD_CRUMB_TRAIL_");
    return crumb;
  }
  
  public static void setTrail(PageContext pageContext, ArrayList crumb) {
    HttpSession session = pageContext.getSession();
    session.setAttribute("_BREAD_CRUMB_TRAIL_", crumb);
  }
  
  public static boolean checkTrailItem(PageContext pageContext,String category) {
    ArrayList crumb = getTrail(pageContext);
    if(crumb != null && crumb.contains(category)) return true;
    else return false;
  }

  public static void getAssociatedProducts(PageContext pageContext, String productAttributeName, String assocPrefix) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    GenericValue product = (GenericValue)pageContext.getAttribute(productAttributeName);
    if(product == null) return;
    
    try {
      Collection upgradeProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_UPGRADE"), null);
      Collection complementProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_COMPLEMENT"), null);
      Collection obsolescenceProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productIdTo", product.get("productId"), "productAssocTypeId", "PRODUCT_OBSOLESCENCE"), null);
      Collection obsoleteByProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_OBSOLESCENCE"), null);

      if(upgradeProducts != null && upgradeProducts.size() > 0) pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
      if(complementProducts != null && complementProducts.size() > 0) pageContext.setAttribute(assocPrefix + "complement", complementProducts);
      if(obsolescenceProducts != null && obsolescenceProducts.size() > 0) pageContext.setAttribute(assocPrefix + "obsolescence", obsolescenceProducts);
      if(obsoleteByProducts != null && obsoleteByProducts.size() > 0) pageContext.setAttribute(assocPrefix + "obsoleteby", obsoleteByProducts);
    }
    catch(GenericEntityException e) {
      Debug.logWarning(e);
    }
  }

  public static void getRandomCartProductAssoc(PageContext pageContext, String assocsAttrName) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    ShoppingCart cart = (ShoppingCart)pageContext.getSession().getAttribute("_SHOPPING_CART_");
    if(cart == null || cart.size() <= 0) return;
    
    try {
      TreeSet cartAssocs = new TreeSet();
      
      Iterator cartiter = cart.iterator();
      while(cartiter != null && cartiter.hasNext()) {
        ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
        Collection upgradeProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_UPGRADE"), null);
        //Collection complementProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_COMPLEMENT"), null);

        //if(upgradeProducts != null && upgradeProducts.size() > 0) pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
        //if(complementProducts != null && complementProducts.size() > 0) pageContext.setAttribute(assocPrefix + "complement", complementProducts);
      }

      pageContext.setAttribute(assocsAttrName, (Collection)cartAssocs);
    }
    catch(GenericEntityException e) {
      Debug.logWarning(e);
    }
  }
}
