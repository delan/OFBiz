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

package org.ofbiz.ecommerce.catalog;

import java.util.*;
import java.net.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.ecommerce.shoppingcart.*;

import org.ofbiz.commonapp.product.category.*;

/**
 * CatalogWorker - Worker class to reduce code in JSPs.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    August 23, 2001, 7:58 PM
 */
public class CatalogWorker {
/*    
    public static String getEcommercePropertiesValue(PageContext pageContext, String propertyName) {
        return getEcommercePropertiesValue(pageContext.getServletContext(), propertyName);
    }
    public static String getEcommercePropertiesValue(ServletContext servletContext, String propertyName) {
        URL url = (URL)servletContext.getAttribute("ECOMMERCE_PROPERTIES_URL");
        if (url == null) {
            try { url = servletContext.getResource("/WEB-INF/ecommerce.properties"); }
            catch(java.net.MalformedURLException e) { Debug.logError(e); }
            if (url != null) servletContext.setAttribute("ECOMMERCE_PROPERTIES_URL", url);
        }
        if (url == null) return null;
        else return UtilProperties.getPropertyValue(url, propertyName);
    }
*/
    public static String getWebSiteId(PageContext pageContext) {
        return pageContext.getServletContext().getInitParameter("webSiteId");
    }
    
    public static String getWebSiteId(ServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        if (application == null) return null;
        return application.getInitParameter("webSiteId");
    }
    
    public static GenericValue getWebSite(PageContext pageContext) {
        String webSiteId = getWebSiteId(pageContext);
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        
        try {
            return delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up website with id " + webSiteId);
        }
        return null;
    }

    public static Collection getWebSiteCatalogs(PageContext pageContext) {
        return getWebSiteCatalogs(pageContext.getRequest());
    }

    public static Collection getWebSiteCatalogs(ServletRequest request) {
        String webSiteId = getWebSiteId(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        try {
            return EntityUtil.filterByDate(delegator.findByAndCache("WebSiteCatalog", UtilMisc.toMap("webSiteId", webSiteId), UtilMisc.toList("sequenceNum", "prodCatalogId")));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up website catalogs for website with id " + webSiteId);
        }
        return null;
    }
    
    public static Collection getProdCatalogCategories(PageContext pageContext, String prodCatalogId, String prodCatalogCategoryTypeId) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        
        try {
            Collection prodCatalogCategories = EntityUtil.filterByDate(delegator.findByAndCache("ProdCatalogCategory", 
                    UtilMisc.toMap("prodCatalogId", prodCatalogId), 
                    UtilMisc.toList("sequenceNum", "productCategoryId")));
            if (UtilValidate.isNotEmpty(prodCatalogCategoryTypeId) && prodCatalogCategories != null) {
                prodCatalogCategories = EntityUtil.filterByAnd(prodCatalogCategories, 
                        UtilMisc.toMap("prodCatalogCategoryTypeId", prodCatalogCategoryTypeId));
            }
            return prodCatalogCategories;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up ProdCatalogCategories for prodCatalog with id " + prodCatalogId);
        }
        return null;
    }
    
    public static String getCurrentCatalogId(PageContext pageContext) {
        return getCurrentCatalogId(pageContext.getRequest());
    }
    
    public static String getCurrentCatalogId(ServletRequest request) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        String prodCatalogId;
        boolean fromSession = false;
        //first see if a new catalog was specified as a parameter
        prodCatalogId = request.getParameter("CURRENT_CATALOG_ID");
        //if no parameter, try from session
        if (prodCatalogId == null) {
            prodCatalogId = (String) session.getAttribute("CURRENT_CATALOG_ID");
            if (prodCatalogId != null) fromSession = true;
        }
        //get it from the database
        if (prodCatalogId == null) {
            Collection webSiteCatalogs = getWebSiteCatalogs(request);
            if (webSiteCatalogs != null && webSiteCatalogs.size() > 0) {
                GenericValue webSiteCatalog = EntityUtil.getFirst(webSiteCatalogs);
                prodCatalogId = webSiteCatalog.getString("prodCatalogId");
            }
        }
        
        if (!fromSession) {
            Debug.logInfo("[CatalogWorker.getCurrentCatalogId] Setting new catalog name: " + prodCatalogId);
            session.setAttribute("CURRENT_CATALOG_ID", prodCatalogId);
            CategoryWorker.setTrail(request, new ArrayList());
        }
        return prodCatalogId;
    }
    
    public static Collection getCatalogIdsAvailable(PageContext pageContext) {
        Collection categoryIds = new LinkedList();
        Collection webSiteCatalogs = getWebSiteCatalogs(pageContext);
        if (webSiteCatalogs != null && webSiteCatalogs.size() > 0) {
            Iterator wscIter = webSiteCatalogs.iterator();
            while (wscIter.hasNext()) {
                GenericValue webSiteCatalog = (GenericValue) wscIter.next();
                categoryIds.add(webSiteCatalog.getString("prodCatalogId"));
            }
        }
        return categoryIds;
    }
    
    public static String getCatalogName(PageContext pageContext) {
        return getCatalogName(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static String getCatalogName(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        try {
            GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
            if (prodCatalog != null) {
                return prodCatalog.getString("catalogName");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up name for prodCatalog with id " + prodCatalogId);
        }
        
        return null;
    }
    
    public static String getCatalogTopCategoryId(PageContext pageContext) {
        return getCatalogTopCategoryId(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static String getCatalogTopCategoryId(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;
        
        Collection prodCatalogCategories = getProdCatalogCategories(pageContext, prodCatalogId, "PCCT_BROWSE_ROOT");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }
    
    public static String getCatalogSearchCategoryId(PageContext pageContext) {
        return getCatalogSearchCategoryId(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static String getCatalogSearchCategoryId(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        Collection prodCatalogCategories = getProdCatalogCategories(pageContext, prodCatalogId, "PCCT_SEARCH");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }
    
    public static String getCatalogPromotionsCategoryId(PageContext pageContext) {
        return getCatalogPromotionsCategoryId(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static String getCatalogPromotionsCategoryId(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        Collection prodCatalogCategories = getProdCatalogCategories(pageContext, prodCatalogId, "PCCT_PROMOTIONS");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }
    
    public static boolean getCatalogQuickaddUse(PageContext pageContext) {
        return getCatalogQuickaddUse(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static boolean getCatalogQuickaddUse(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return false;
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        try {
            GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
            if (prodCatalog != null) {
                return "Y".equals(prodCatalog.getString("useQuickAdd"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up name for prodCatalog with id " + prodCatalogId);
        }
        return false;
    }
    
    public static String getCatalogQuickaddCategoryPrimary(PageContext pageContext) {
        return getCatalogQuickaddCategoryPrimary(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static String getCatalogQuickaddCategoryPrimary(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        Collection prodCatalogCategories = getProdCatalogCategories(pageContext, prodCatalogId, "PCCT_QUICK_ADD");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }
    
    public static Collection getCatalogQuickaddCategories(PageContext pageContext) {
        return getCatalogQuickaddCategories(pageContext, getCurrentCatalogId(pageContext));
    }
    
    public static Collection getCatalogQuickaddCategories(PageContext pageContext, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        Collection categoryIds = new LinkedList();

        Collection prodCatalogCategories = getProdCatalogCategories(pageContext, prodCatalogId, "PCCT_QUICK_ADD");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            Iterator pccIter = prodCatalogCategories.iterator();
            while (pccIter.hasNext()) {
                GenericValue prodCatalogCategory = (GenericValue) pccIter.next();
                categoryIds.add(prodCatalogCategory.getString("productCategoryId"));
            }
        }

        return categoryIds;
    }
    
    /* ========================================================================================*/
    /* ================================ Catalog Inventory Check ===============================*/
    
    public static boolean isCatalogInventoryAvailable(ServletRequest request, String productId, double quantity) {
        String prodCatalogId = getCurrentCatalogId(request);
        if (prodCatalogId == null || prodCatalogId.length() == 0) {
            Debug.logWarning("No current catalog id found, return false for inventory check");
            return false;
        }
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        return org.ofbiz.commonapp.product.catalog.CatalogWorker.isCatalogInventoryAvailable(prodCatalogId, productId, quantity, delegator, dispatcher);
    }
    
    /* ========================================================================================*/
    /* ============================= Special Data Retreival Methods ===========================*/
    
    public static void getRandomCartProductAssoc(PageContext pageContext, String assocsAttrName) {
        GenericDelegator delegator = (GenericDelegator)pageContext.getRequest().getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart)pageContext.getSession().getAttribute("_SHOPPING_CART_");
        if (cart == null || cart.size() <= 0) return;
        
        try {
            Map products = new HashMap();
            
            Iterator cartiter = cart.iterator();
            while (cartiter != null && cartiter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
                //Collection upgradeProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_UPGRADE"), null);
                Collection complementProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_COMPLEMENT"), null);
                //since ProductAssoc records have a fromDate and thruDate, we can filter by now so that only assocs in the date range are included
                complementProducts = EntityUtil.filterByDate(complementProducts);
                
                //if (upgradeProducts != null && upgradeProducts.size() > 0) pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
                if (complementProducts != null && complementProducts.size() > 0) {
                    Iterator complIter = complementProducts.iterator();
                    while (complIter.hasNext()) {
                        GenericValue productAssoc = (GenericValue)complIter.next();
                        GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");
                        products.put(product.getString("productId"), product);
                    }
                }
            }
            
            //remove all products that are already in the cart
            cartiter = cart.iterator();
            while (cartiter != null && cartiter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
                products.remove(item.getProductId());
            }
            
            ArrayList cartAssocs = new ArrayList(products.values());
            //randomly remove products while there are more than 3
            while (cartAssocs.size() > 3) {
                int toRemove = (int)(Math.random()*(double)(cartAssocs.size()));
                cartAssocs.remove(toRemove);
            }
            if (cartAssocs.size() > 0) {
                pageContext.setAttribute(assocsAttrName, cartAssocs);
            }
        }
        catch(GenericEntityException e) {
            Debug.logWarning(e);
        }
    }
    
    public static void getQuickReorderProducts(PageContext pageContext, String productsAttrName, String quantitiesAttrName) {
        GenericDelegator delegator = (GenericDelegator)pageContext.getRequest().getAttribute("delegator");
        GenericValue userLogin = (GenericValue)pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);
        if (userLogin == null) return;
        
        try {
            Map products = (Map)pageContext.getSession().getAttribute("_QUICK_REORDER_PRODUCTS_");
            Map productQuantities = (Map)pageContext.getSession().getAttribute("_QUICK_REORDER_PRODUCT_QUANTITIES_");
            Map productOccurances = (Map)pageContext.getSession().getAttribute("_QUICK_REORDER_PRODUCT_OCCURANCES_");
            
            if (products == null || productQuantities == null || productOccurances == null) {
                products = new HashMap();
                productQuantities = new HashMap();
                //keep track of how many times a product occurs in order to find averages and rank by purchase amount
                productOccurances = new HashMap();
                
                //get all order role entities for user by customer role type
                //final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};
                final String[] USER_ORDER_ROLE_TYPES = {"PLACING_CUSTOMER"};
                for(int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
                    Collection orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", USER_ORDER_ROLE_TYPES[i]), null);
                    Iterator ordersIter = UtilMisc.toIterator(orderRoles);
                    while (ordersIter != null && ordersIter.hasNext()) {
                        GenericValue orderRole = (GenericValue)ordersIter.next();
                        //for each order role get all order items
                        Collection orderItems = orderRole.getRelated("OrderItem");
                        Iterator orderItemsIter = UtilMisc.toIterator(orderItems);
                        while (orderItemsIter != null && orderItemsIter.hasNext()) {
                            GenericValue orderItem = (GenericValue)orderItemsIter.next();
                            //for each order item get the associated product
                            GenericValue product = orderItem.getRelatedOneCache("Product");
                            products.put(product.get("productId"), product);
                            
                            Integer curQuant = (Integer)productQuantities.get(product.get("productId"));
                            if (curQuant == null) curQuant = new Integer(0);
                            Double orderQuant = orderItem.getDouble("quantity");
                            if (orderQuant == null) orderQuant = new Double(0.0);
                            productQuantities.put(product.get("productId"), new Integer(curQuant.intValue() + orderQuant.intValue()));
                            
                            Integer curOcc = (Integer)productOccurances.get(product.get("productId"));
                            if (curOcc == null) curOcc = new Integer(0);
                            productOccurances.put(product.get("productId"), new Integer(curOcc.intValue() + 1));
                        }
                    }
                }
                
                //go through each product quantity and divide it by the occurances to get the average
                Iterator quantEntries = productQuantities.entrySet().iterator();
                while (quantEntries.hasNext()) {
                    Map.Entry entry = (Map.Entry)quantEntries.next();
                    Object prodId = entry.getKey();
                    Integer quantity = (Integer)entry.getValue();
                    Integer occs = (Integer)productOccurances.get(prodId);
                    int nqint = quantity.intValue()/occs.intValue();
                    if (nqint < 1) nqint = 1;
                    productQuantities.put(prodId, new Integer(nqint));
                }
                
                pageContext.getSession().setAttribute("_QUICK_REORDER_PRODUCTS_", new HashMap(products));
                pageContext.getSession().setAttribute("_QUICK_REORDER_PRODUCT_QUANTITIES_", new HashMap(productQuantities));
                pageContext.getSession().setAttribute("_QUICK_REORDER_PRODUCT_OCCURANCES_", new HashMap(productOccurances));
            }
            else {
                // make a copy since we are going to change them
                products = new HashMap(products);
                productQuantities = new HashMap(productQuantities);
                productOccurances = new HashMap(productOccurances);
            }
            
            //remove all products that are already in the cart
            ShoppingCart cart = (ShoppingCart)pageContext.getSession().getAttribute("_SHOPPING_CART_");
            if (cart != null && cart.size() > 0) {
                Iterator cartiter = cart.iterator();
                while (cartiter.hasNext()) {
                    ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
                    products.remove(item.getProductId());
                    productQuantities.remove(item.getProductId());
                    productOccurances.remove(item.getProductId());
                }
            }
            
            List reorderProds = new ArrayList(products.values());
      /*
      //randomly remove products while there are more than 5
      while (reorderProds.size() > 5) {
        int toRemove = (int)(Math.random()*(double)(reorderProds.size()));
        reorderProds.remove(toRemove);
      }
       */
            
            //sort descending by new metric...
            double occurancesModifier = 1.0;
            double quantityModifier = 1.0;
            Map newMetric = new HashMap();
            Iterator occurEntries = productOccurances.entrySet().iterator();
            while (occurEntries.hasNext()) {
                Map.Entry entry = (Map.Entry)occurEntries.next();
                Object prodId = entry.getKey();
                Integer quantity = (Integer)entry.getValue();
                Integer occs = (Integer)productQuantities.get(prodId);
                double nqdbl = quantity.doubleValue()*quantityModifier + occs.doubleValue()*occurancesModifier;
                newMetric.put(prodId, new Double(nqdbl));
            }
            reorderProds = productOrderByMap(reorderProds, newMetric, true);
            
            //remove extra products - only return 5
            while (reorderProds.size() > 5) {
                reorderProds.remove(reorderProds.size() - 1);
            }
            
            pageContext.setAttribute(productsAttrName, reorderProds);
            pageContext.setAttribute(quantitiesAttrName, productQuantities);
        }
        catch(GenericEntityException e) {
            Debug.logWarning(e);
        }
    }
    
    public static List productOrderByMap(Collection values, Map orderByMap, boolean descending) {
        if (values == null)  return null;
        if (values.size() == 0) return UtilMisc.toList(values);
        
        List result = new ArrayList(values);
        Collections.sort(result, new ProductByMapComparator(orderByMap, descending));
        return result;
    }
    
    static class ProductByMapComparator implements Comparator {
        private Map orderByMap;
        private boolean descending;
        
        ProductByMapComparator(Map orderByMap, boolean descending) {
            this.orderByMap = orderByMap;
            this.descending = descending;
        }
        
        public int compare(java.lang.Object prod1, java.lang.Object prod2) {
            int result = compareAsc((GenericEntity) prod1, (GenericEntity) prod2);
            if (descending) {
                result = -result;
            }
            return result;
        }
        
        private int compareAsc(GenericEntity prod1, GenericEntity prod2) {
            Object value = orderByMap.get(prod1.get("productId"));
            Object value2 = orderByMap.get(prod2.get("productId"));
            //null is defined as the smallest possible value
            if (value == null) return value2 == null ? 0 : -1;
            return ((Comparable)value).compareTo(value2);
        }
        
        public boolean equals(java.lang.Object obj) {
            if ((obj != null) && (obj instanceof ProductByMapComparator)) {
                ProductByMapComparator that = (ProductByMapComparator) obj;
                return this.orderByMap.equals(that.orderByMap) && this.descending == that.descending;
            } else {
                return false;
            }
        }
    }
    
    public static void printSubCategories(GenericValue pcategory, GenericValue category, String curcatid, PageContext pageContext) throws java.io.IOException {
        String controlPath = (String) pageContext.getRequest().getAttribute(SiteDefs.CONTROL_PATH);
        JspWriter out = pageContext.getOut();

        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        String pstr = "";
        if (pcategory != null) pstr = "&pcategory=" + pcategory.getString("productCategoryId");
        
        if (curcatid != null && curcatid.equals(category.getString("productCategoryId"))) {
            out.print("<div style='text-indent: -10px;'><a href='");
            out.print(response.encodeURL(controlPath + "/category?category_id=" + category.getString("productCategoryId") + pstr));
            out.print("' class='buttontextdisabled'>-&nbsp;");
            out.print(category.getString("description"));
            out.println("</a></div>");
        } else {
            out.print("<div style='text-indent: -10px;'><a href='");
            out.print(response.encodeURL(controlPath + "/category?category_id=" + category.getString("productCategoryId") + pstr));
            out.print("' class='buttontext'>-&nbsp;");
            out.print(category.getString("description"));
            out.println("</a></div>");
        }

        if (CategoryWorker.checkTrailItem(pageContext, category.getString("productCategoryId")) || 
                (curcatid != null && curcatid.equals(category.getString("productCategoryId")))) {
            List subCatList = CategoryWorker.getRelatedCategoriesRet(pageContext, "subCatList", category.getString("productCategoryId"), true);
            if (subCatList != null && subCatList.size() > 0) {
                Iterator iter = subCatList.iterator();
                while (iter.hasNext()) {
                    GenericValue subcat = (GenericValue) iter.next();
                    out.println("<div style='margin-left: 10px;'>");
                    printSubCategories(category, subcat, curcatid, pageContext);
                    out.println("</div>");
                }
            }
        }
    }
}
