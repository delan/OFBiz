/*
 * $Id$
 * $Log$
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
import org.ofbiz.core.entity.GenericHelper;
import org.ofbiz.core.entity.GenericHelperFactory;

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
 * @author Andy Zeneski (jaz@zsolv.com)
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
        
        if ( parentId == null ) {
            if ( request.getParameter("catalog_id") != null )
                parentId = request.getParameter("catalog_id");
            else if ( request.getParameter("CATALOG_ID") != null )
                parentId = request.getParameter("CATALOG_ID");
            else if ( request.getParameter("category_id") != null )
                parentId = request.getParameter("category_id");
            else if ( request.getParameter("CATEGORY_ID") != null )
                parentId = request.getParameter("CATEGORY_ID");
        }
        
        if ( parentId == null )
            return;
        
        GenericHelper helper = (GenericHelper)pageContext.getServletContext().getAttribute("helper");
        GenericValue requestedCategory = helper.findByPrimaryKey("ProductCategory",UtilMisc.toMap("productCategoryId",parentId));
        if ( requestedCategory.getString("primaryParentCategoryId") != null )
            setTrail(pageContext,parentId,false);
        else
            setTrail(pageContext,parentId,true);
        Collection rollups = helper.findByAnd("ProductCategoryRollup",UtilMisc.toMap("parentProductCategoryId",parentId),null);
        Debug.log("Got rollups...");
        if ( rollups != null && rollups.size() > 0 ) {
            Debug.log("Rollup size: " + rollups.size());
            Iterator ri = rollups.iterator();
            while ( ri.hasNext() ) {
                GenericValue parent = (GenericValue) ri.next();
                Debug.log("Adding children of: " + parent.getString("parentProductCategoryId"));
                Collection cc = helper.getRelated("CurrentProductCategory",parent);
                if ( cc != null && cc.size() > 0 )
                    categories.addAll(cc);
            }
        }
        
        if ( categories.size() > 0 )
            pageContext.setAttribute(attributeName,categories);
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
        GenericHelper helper = (GenericHelper)pageContext.getServletContext().getAttribute("helper");
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
        
        ArrayList products = (ArrayList)pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS");
        String resultArrayName = (String)pageContext.getSession().getAttribute("CACHE_SEARCH_RESULTS_NAME");

        if(products == null || resultArrayName == null || curFindString.compareTo(resultArrayName) != 0 || viewIndex == 0) {
            Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
            Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);
            
            GenericValue category = helper.findByPrimaryKey("ProductCategory",UtilMisc.toMap("productCategoryId",parentId));

            if ( category != null ) {
                products = new ArrayList();
                Collection p = helper.getRelated("PrimaryProduct",category);
                products.addAll(p);
            }
            
            if(products != null) {
                pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS", products);
                pageContext.getSession().setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
            }
        }
        
        int lowIndex = viewIndex*viewSize+1;
        int highIndex = (viewIndex+1)*viewSize;
        int listSize = 0;
        if(products!=null) listSize = products.size();
        if(listSize<highIndex) highIndex=listSize;
        
        ArrayList someProducts = new ArrayList();
        for(int ind=lowIndex; ind<=highIndex; ind++) {
            someProducts.add(products.get(ind-1));
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
        ServletRequest request = pageContext.getRequest();
        
        if ( productId == null ) {
            if ( request.getParameter("product_id") != null )
                productId = request.getParameter("product_id");
            else if ( request.getParameter("PRODUCT_ID") != null )
                productId = request.getParameter("PRODUCT_ID");
        }
        
        if ( productId == null )
            return;
        
        GenericValue product = null;
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        
        product = helper.findByPrimaryKey("Product",UtilMisc.toMap("productId", productId));
        if ( product != null )
            pageContext.setAttribute(attributeName,product);
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
        GenericHelper helper = (GenericHelper)pageContext.getServletContext().getAttribute("helper");
        
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
        if(productIds == null || resultArrayName == null || curFindString.compareTo(resultArrayName) != 0 || viewIndex == 0) {
            Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
            Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);
            
            //sort by productId (only sort for now...)
            Collection unsortedIds = KeywordSearch.productsByKeywords(keywordString, helper.getServerName());
            if(unsortedIds != null && unsortedIds.size() > 0) {
                TreeSet productIdTree = new TreeSet(unsortedIds);
                productIds = new ArrayList(productIdTree);
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
            products.add(helper.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productIds.get(ind-1))));
        }
        
        pageContext.setAttribute(attributePrefix + "viewIndex", new Integer(viewIndex));
        pageContext.setAttribute(attributePrefix + "viewSize", new Integer(viewSize));
        pageContext.setAttribute(attributePrefix + "lowIndex", new Integer(lowIndex));
        pageContext.setAttribute(attributePrefix + "highIndex", new Integer(highIndex));
        pageContext.setAttribute(attributePrefix + "listSize", new Integer(listSize));
        pageContext.setAttribute(attributePrefix + "keywordString", keywordString);
        if(products.size() > 0) pageContext.setAttribute(attributePrefix + "searchProductList",products);
    }
    
    public static void setTrail(PageContext pageContext, String currentCategory, boolean topLevel) {
        ServletRequest request = pageContext.getRequest();
        HttpSession session = pageContext.getSession();
        String previousCategory = request.getParameter("pcategory");
        ArrayList crumb = null;
        
        if ( !topLevel ) {
            if ( previousCategory != null )
                crumb = (ArrayList) session.getAttribute("_BREAD_CRUMB_TRAIL_");
            
            if ( crumb == null )
                crumb = new ArrayList();
            
            if ( crumb.contains(currentCategory) ) {
                Debug.logInfo("Category already set. Aborting.");
                return;
            }
            
            if ( crumb.contains(previousCategory) ) {
                int index = crumb.indexOf(previousCategory);
                if ( index < (crumb.size() - 1) ) {
                    for ( int i = crumb.size() -1; i > index; i-- ) {
                        crumb.remove(i);
                    }
                }
            }
            
            crumb.add(currentCategory);
        }
        else {
            crumb = new ArrayList();
            Debug.logInfo("Created new crumb, added category.");
            crumb.add(currentCategory);
        }
        
        session.setAttribute("_BREAD_CRUMB_TRAIL_",crumb);
    }
               
    public static Collection getTrail(PageContext pageContext) {
        HttpSession session = pageContext.getSession();
        ArrayList crumb = (ArrayList) session.getAttribute("_BREAD_CRUMB_TRAIL_");
        if ( crumb == null )
            return null;
        
        return crumb;
    }
}
