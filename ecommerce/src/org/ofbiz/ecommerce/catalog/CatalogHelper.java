/*
 * $Id$
 * $Log$
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

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilMisc;

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
    
    public static void getRelatedCategories(PageContext pageContext, String attributeName, String parentId) {        
        ArrayList categories = new ArrayList();                        
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
    
    public static void getRelatedProducts(PageContext pageContext, String attributeName, String parentId) {                
        ArrayList products = new ArrayList();
        GenericHelper helper = (GenericHelper)pageContext.getServletContext().getAttribute("helper");
        GenericValue category = helper.findByPrimaryKey("ProductCategory",UtilMisc.toMap("productCategoryId",parentId));
        
        if ( category != null ) {
            Collection p = helper.getRelated("PrimaryProduct",category);
            products.addAll(p);
        }            
        
        if ( products.size() > 0 )
            pageContext.setAttribute(attributeName,products);
    }
    
    public static void getProduct(PageContext pageContext, String attributeName, String productId) {    
      GenericValue product = null;
      GenericHelper helper = (GenericHelper)pageContext.getServletContext().getAttribute("helper");
      product = helper.findByPrimaryKey("Product",UtilMisc.toMap("productId", productId));
      if ( product != null )
        pageContext.setAttribute(attributeName,product);
    }

    /**
     * Puts the following into the pageContext attribute list:
     *  searchProductList, keywordString, viewIndex, viewSize, lowIndex, highIndex, listSize
     * Puts the following into the session attribute list:
     *  CACHE_SEARCH_RESULTS, CACHE_SEARCH_RESULTS_NAME
     *@param pageContext The pageContext of the calling JSP
     */
    public static void getKeywordSearchProducts(PageContext pageContext) {
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
      if(productIds == null || resultArrayName == null || curFindString.compareTo(resultArrayName) != 0 || viewIndex == 0)
      {
        Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
        Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);

        //sort by productId (only sort for now...)
        Collection unsortedIds = KeywordSearch.productsByKeywords(keywordString, helper.getServerName());
        if(unsortedIds != null && unsortedIds.size() > 0)
        {
          TreeSet productIdTree = new TreeSet(unsortedIds);
          productIds = new ArrayList(productIdTree);
        }

        if(productIds != null)
        {
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
      
      pageContext.setAttribute("viewIndex", new Integer(viewIndex));
      pageContext.setAttribute("viewSize", new Integer(viewSize));
      pageContext.setAttribute("lowIndex", new Integer(lowIndex));
      pageContext.setAttribute("highIndex", new Integer(highIndex));
      pageContext.setAttribute("listSize", new Integer(listSize));
      pageContext.setAttribute("keywordString", keywordString);
      if(products.size() > 0) pageContext.setAttribute("searchProductList",products);
    }    
    
    public static void setTrail(PageContext pageContext, String category, boolean topLevel) {
        HttpSession session = pageContext.getSession();
        ArrayList crumb = null;
        if ( !topLevel ) {
            crumb = (ArrayList) session.getAttribute("_BREAD_CRUMB_TRAIL_");
            if ( crumb != null ) {
                Debug.logInfo("Appended category to crumb.");
                crumb.add(category);
            }
            else {
                crumb = new ArrayList();
                Debug.logInfo("Created new crumb, added category.");
                crumb.add(category);
            }            
        }
        else {
            crumb = new ArrayList();
            crumb.add(category);
            Debug.logInfo("New crumb force, created and added category.");
        }
        if ( crumb != null )
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
