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

package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * CategoryWorker - Worker class to reduce code in JSPs.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.0
 * @created    August 23, 2001, 7:58 PM
 */
public class CategoryWorker {

    public static String getCatalogTopCategory(PageContext pageContext, String defaultTopCategory) {
        String topCatName;
        boolean fromSession = false;
        //first see if a new category was specified as a parameter
        topCatName = pageContext.getRequest().getParameter("CATALOG_TOP_CATEGORY");
        //if no parameter, try from session
        if (topCatName == null) {
            topCatName = (String) pageContext.getSession().getAttribute("CATALOG_TOP_CATEGORY");
            if (topCatName != null)
                fromSession = true;
        }
        //if nothing else, just use a default top category name
        if (topCatName == null)
            topCatName = defaultTopCategory;
        if (topCatName == null)
            topCatName = "CATALOG1";

        if (!fromSession) {
            if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.getCatalogTopCategory] Setting new top category: " + topCatName);
            pageContext.getSession().setAttribute("CATALOG_TOP_CATEGORY", topCatName);
        }
        return topCatName;
    }

    public static void getCategoriesWithNoParent(PageContext pageContext, String attributeName) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        Collection results = new LinkedList();
        try {
            Collection allCategories = delegator.findAll("ProductCategory");
            if (allCategories == null)
                return;
            Iterator aciter = allCategories.iterator();
            while (aciter.hasNext()) {
                GenericValue curCat = (GenericValue) aciter.next();
                Collection parentCats = curCat.getRelatedCache("CurrentProductCategoryRollup");
                if (parentCats == null || parentCats.size() <= 0)
                    results.add(curCat);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
        pageContext.setAttribute(attributeName, results);
    }

    public static void getRelatedCategories(PageContext pageContext, String attributeName, boolean limitView) {
        ServletRequest request = pageContext.getRequest();
        String requestId = null;
        requestId = UtilFormatOut.checkNull(request.getParameter("catalog_id"), request.getParameter("CATALOG_ID"),
                                            request.getParameter("category_id"),
                request.getParameter("CATEGORY_ID"));
        if (requestId.equals(""))
            return;
        if (Debug.infoOn()) Debug.logInfo("[CatalogHelper.getRelatedCategories] RequestID: " + requestId);
        getRelatedCategories(pageContext, attributeName, requestId, limitView);
    }

    public static void getRelatedCategories(PageContext pageContext, String attributeName, String parentId, boolean limitView) {
        ArrayList categories = getRelatedCategoriesRet(pageContext, attributeName, parentId, limitView);
        if (categories.size() > 0)
            pageContext.setAttribute(attributeName, categories);
    }
    
    public static ArrayList getRelatedCategoriesRet(PageContext pageContext, String attributeName, String parentId, boolean limitView) {
        ArrayList categories = new ArrayList();
        ServletRequest request = pageContext.getRequest();

        if (Debug.verboseOn()) Debug.logVerbose("[CatalogHelper.getRelatedCategories] ParentID: " + parentId);

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        List rollups = null;
        try {
            rollups = delegator.findByAndCache("ProductCategoryRollup",
                                               UtilMisc.toMap("parentProductCategoryId",parentId),
                                               UtilMisc.toList("sequenceNum"));
            if (limitView) {
                rollups = EntityUtil.filterByDate(rollups, true);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            rollups = null;
        }
        if (rollups != null && rollups.size() > 0) {
            //Debug.log("Rollup size: " + rollups.size());
            Iterator ri = rollups.iterator();
            while (ri.hasNext()) {
                GenericValue parent = (GenericValue) ri.next();
                //Debug.log("Adding child of: " + parent.getString("parentProductCategoryId"));
                GenericValue cv = null;
                try {
                    cv = parent.getRelatedOneCache("CurrentProductCategory");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage());
                    cv = null;
                }
                if (cv != null)
                    categories.add(cv);
            }
        }
        return categories;
    }

    public static void setTrail(PageContext pageContext, String currentCategory) {
        setTrail(pageContext.getRequest(), currentCategory);
    }
    
    public static void setTrail(ServletRequest request, String currentCategory) {
        String previousCategory = request.getParameter("pcategory");
        if (Debug.verboseOn()) Debug.logVerbose("[CatalogHelper.setTrail] Start: previousCategory=" + previousCategory +
                      " currentCategory=" + currentCategory);

        //if there is no current category, just return and do nothing to that the last settings will stay
        if (currentCategory == null || currentCategory.length() <= 0)
            return;

        //always get the last crumb list
        ArrayList crumb = getTrail(request);
        if (crumb == null)
            crumb = new ArrayList();

        //if no previous category was specified, check to see if currentCategory is in the list
        if (previousCategory == null || previousCategory.length() <= 0) {
            if (crumb.contains(currentCategory)) {
                //if cur category is in crumb, remove everything after it and return
                int cindex = crumb.lastIndexOf(currentCategory);
                if (cindex < (crumb.size() - 1)) {
                    for (int i = crumb.size() - 1; i > cindex; i--) {
                        String deadCat = (String) crumb.remove(i);
                        if (Debug.infoOn()) Debug.logInfo("[CatalogHelper.setTrail] Removed after current category index: " + i +
                                      " catname: " + deadCat);
                    }
                }
                return;
            } else {
                //current category is not in the list, and no previous category was specified, go back to the beginning
                previousCategory = "TOP";
                crumb.clear();
                crumb.add(previousCategory);
                if (Debug.infoOn()) Debug.logInfo("[CatalogHelper.setTrail] Starting new list, added previousCategory: " + previousCategory);
            }
        }

        if (!crumb.contains(previousCategory)) {
            //previous category was NOT in the list, ERROR, start over
            if (Debug.infoOn()) Debug.logInfo("[CatalogHelper.setTrail] ERROR: previousCategory (" + previousCategory +
                          ") was not in the crumb list, position is lost, starting over with TOP");
            previousCategory = "TOP";
            crumb.clear();
            crumb.add(previousCategory);
        } else {
            //remove all categories after the previous category, preparing for adding the current category
            int index = crumb.indexOf(previousCategory);
            if (index < (crumb.size() - 1)) {
                for (int i = crumb.size() - 1; i > index; i--) {
                    String deadCat = (String) crumb.remove(i);
                    if (Debug.infoOn()) Debug.logInfo("[CatalogHelper.setTrail] Removed after previous category index: " + i +
                                  " catname: " + deadCat);
                }
            }
        }

        //add the current category to the end of the list
        crumb.add(currentCategory);
        if (Debug.verboseOn()) Debug.logVerbose("[CatalogHelper.setTrail] Continuing list: Added currentCategory: " + currentCategory);
        setTrail(request, crumb);
    }

    public static ArrayList getTrail(PageContext pageContext) {
        return getTrail(pageContext.getRequest());
    }
    public static ArrayList getTrail(ServletRequest request) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        ArrayList crumb = (ArrayList) session.getAttribute("_BREAD_CRUMB_TRAIL_");
        return crumb;
    }

    public static void setTrail(PageContext pageContext, ArrayList crumb) {
        setTrail(pageContext.getRequest(), crumb);
    }
    public static void setTrail(ServletRequest request, ArrayList crumb) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        session.setAttribute("_BREAD_CRUMB_TRAIL_", crumb);
    }

    public static boolean checkTrailItem(PageContext pageContext, String category) {
        return checkTrailItem(pageContext.getRequest(), category);
    }
    public static boolean checkTrailItem(ServletRequest request, String category) {
        ArrayList crumb = getTrail(request);
        if (crumb != null && crumb.contains(category))
            return true;
        else
            return false;
    }

    public static String lastTrailItem(PageContext pageContext) {
        return lastTrailItem(pageContext.getRequest());
    }
    public static String lastTrailItem(ServletRequest request) {
        ArrayList crumb = getTrail(request);
        if (crumb != null && crumb.size() > 0) {
            return (String) crumb.get(crumb.size() - 1);
        } else {
            return null;
        }
    }
}

