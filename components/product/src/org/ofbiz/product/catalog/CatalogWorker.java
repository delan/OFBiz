/*
 * $Id: CatalogWorker.java,v 1.1 2003/08/17 18:04:23 ajzeneski Exp $
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
package org.ofbiz.product.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 * CatalogWorker - Worker class for catalog related functionality
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class CatalogWorker {
    
    public static final String module = CatalogWorker.class.getName();

    public static String getWebSiteId(ServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));

        if (application == null) return null;
        return application.getInitParameter("webSiteId");
    }
    
    public static GenericValue getWebSite(ServletRequest request) {
        String webSiteId = getWebSiteId(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            return delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up website with id " + webSiteId, module);
        }
        return null;
    }

    public static List getStoreCatalogs(ServletRequest request) {
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            return EntityUtil.filterByDate(delegator.findByAndCache("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "prodCatalogId")), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up website catalogs for store with id " + productStoreId, module);
        }
        return null;
    }

    public static List getPartyCatalogs(ServletRequest request) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin == null) userLogin = (GenericValue) session.getAttribute("autoUserLogin");
        if (userLogin == null) return null;
        String partyId = userLogin.getString("partyId");
        if (partyId == null) return null;
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            return EntityUtil.filterByDate(delegator.findByAndCache("ProdCatalogRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CUSTOMER"), UtilMisc.toList("sequenceNum", "prodCatalogId")), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up ProdCatalog Roles for party with id " + partyId, module);
        }
        return null;
    }
    
    public static List getProdCatalogCategories(ServletRequest request, String prodCatalogId, String prodCatalogCategoryTypeId) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        return getProdCatalogCategories(delegator, prodCatalogId, prodCatalogCategoryTypeId);
    }

    public static List getProdCatalogCategories(GenericDelegator delegator, String prodCatalogId, String prodCatalogCategoryTypeId) {
        try {
            List prodCatalogCategories = EntityUtil.filterByDate(delegator.findByAndCache("ProdCatalogCategory",
                        UtilMisc.toMap("prodCatalogId", prodCatalogId),
                        UtilMisc.toList("sequenceNum", "productCategoryId")), true);

            if (UtilValidate.isNotEmpty(prodCatalogCategoryTypeId) && prodCatalogCategories != null) {
                prodCatalogCategories = EntityUtil.filterByAnd(prodCatalogCategories,
                            UtilMisc.toMap("prodCatalogCategoryTypeId", prodCatalogCategoryTypeId));
            }
            return prodCatalogCategories;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up ProdCatalogCategories for prodCatalog with id " + prodCatalogId, module);
        }
        return null;
    }

    public static String getCurrentCatalogId(ServletRequest request) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        Map requestParameters = UtilHttp.getParameterMap((HttpServletRequest) request);
        String prodCatalogId = null;
        boolean fromSession = false;

        // first see if a new catalog was specified as a parameter
        prodCatalogId = (String) requestParameters.get("CURRENT_CATALOG_ID");
        // if no parameter, try from session
        if (prodCatalogId == null) {
            prodCatalogId = (String) session.getAttribute("CURRENT_CATALOG_ID");
            if (prodCatalogId != null) fromSession = true;
        }
        // get it from the database
        if (prodCatalogId == null) {
            List catalogIds = getCatalogIdsAvailable(request);
            if (catalogIds != null && catalogIds.size() > 0) prodCatalogId = (String) catalogIds.get(0);
        }

        if (!fromSession) {
            if (Debug.verboseOn()) Debug.logVerbose("[CatalogWorker.getCurrentCatalogId] Setting new catalog name: " + prodCatalogId, module);
            session.setAttribute("CURRENT_CATALOG_ID", prodCatalogId);
            CategoryWorker.setTrail(request, new ArrayList());
        }
        return prodCatalogId;
    }
    
    public static List getCatalogIdsAvailable(ServletRequest request) {
        List categoryIds = new LinkedList();
        List partyCatalogs = getPartyCatalogs(request);
        List storeCatalogs = getStoreCatalogs(request);
        List allCatalogLinks = new ArrayList((storeCatalogs == null ? 0 : storeCatalogs.size()) + (partyCatalogs == null ? 0 : partyCatalogs.size()));
        if (partyCatalogs != null) allCatalogLinks.addAll(partyCatalogs);
        if (storeCatalogs != null) allCatalogLinks.addAll(storeCatalogs);
        
        if (allCatalogLinks.size() > 0) {
            Iterator aclIter = allCatalogLinks.iterator();
            while (aclIter.hasNext()) {
                GenericValue catalogLink = (GenericValue) aclIter.next();
                categoryIds.add(catalogLink.getString("prodCatalogId"));
            }
        }
        return categoryIds;
    }
    
    public static String getCatalogName(ServletRequest request) {
        return getCatalogName(request, getCurrentCatalogId(request));
    }

    public static String getCatalogName(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));

            if (prodCatalog != null) {
                return prodCatalog.getString("catalogName");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up name for prodCatalog with id " + prodCatalogId, module);
        }

        return null;
    }

    public static String getContentPathPrefix(ServletRequest request) {
        GenericValue prodCatalog = getProdCatalog(request, getCurrentCatalogId(request));

        if (prodCatalog == null) return "";
        String contentPathPrefix = prodCatalog.getString("contentPathPrefix");

        return StringUtil.cleanUpPathPrefix(contentPathPrefix);
    }
        
    public static String getTemplatePathPrefix(ServletRequest request) {
        GenericValue prodCatalog = getProdCatalog(request, getCurrentCatalogId(request));

        if (prodCatalog == null) return "";
        String templatePathPrefix = prodCatalog.getString("templatePathPrefix");

        return StringUtil.cleanUpPathPrefix(templatePathPrefix);
    }

    public static GenericValue getProdCatalog(ServletRequest request) {
        return getProdCatalog(request, getCurrentCatalogId(request));
    }

    public static GenericValue getProdCatalog(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            return delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up name for prodCatalog with id " + prodCatalogId, module);
            return null;
        }
    }
    
    public static String getCatalogTopCategoryId(ServletRequest request) {
        return getCatalogTopCategoryId(request, getCurrentCatalogId(request));
    }
        
    public static String getCatalogTopCategoryId(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        List prodCatalogCategories = getProdCatalogCategories(request, prodCatalogId, "PCCT_BROWSE_ROOT");

        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);

            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }
    
    public static String getCatalogSearchCategoryId(ServletRequest request) {
        return getCatalogSearchCategoryId(request, getCurrentCatalogId(request));
    }
        
    public static String getCatalogSearchCategoryId(ServletRequest request, String prodCatalogId) {
        return getCatalogSearchCategoryId((GenericDelegator) request.getAttribute("delegator"), prodCatalogId);
    }
    public static String getCatalogSearchCategoryId(GenericDelegator delegator, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        List prodCatalogCategories = getProdCatalogCategories(delegator, prodCatalogId, "PCCT_SEARCH");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }

    public static String getCatalogViewAllowCategoryId(GenericDelegator delegator, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        List prodCatalogCategories = getProdCatalogCategories(delegator, prodCatalogId, "PCCT_VIEW_ALLW");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }

    public static String getCatalogPurchaseAllowCategoryId(GenericDelegator delegator, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        List prodCatalogCategories = getProdCatalogCategories(delegator, prodCatalogId, "PCCT_PURCH_ALLW");
        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);
            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }

    public static String getCatalogPromotionsCategoryId(ServletRequest request) {
        return getCatalogPromotionsCategoryId(request, getCurrentCatalogId(request));
    }
         
    public static String getCatalogPromotionsCategoryId(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        List prodCatalogCategories = getProdCatalogCategories(request, prodCatalogId, "PCCT_PROMOTIONS");

        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);

            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }

    public static boolean getCatalogQuickaddUse(ServletRequest request) {
        return getCatalogQuickaddUse(request, getCurrentCatalogId(request));
    }

    public static boolean getCatalogQuickaddUse(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return false;
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));

            if (prodCatalog != null) {
                return "Y".equals(prodCatalog.getString("useQuickAdd"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up name for prodCatalog with id " + prodCatalogId, module);
        }
        return false;
    }
               
    public static String getCatalogQuickaddCategoryPrimary(ServletRequest request) {
        return getCatalogQuickaddCategoryPrimary(request, getCurrentCatalogId(request));
    }
                   
    public static String getCatalogQuickaddCategoryPrimary(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        List prodCatalogCategories = getProdCatalogCategories(request, prodCatalogId, "PCCT_QUICK_ADD");

        if (prodCatalogCategories != null && prodCatalogCategories.size() > 0) {
            GenericValue prodCatalogCategory = EntityUtil.getFirst(prodCatalogCategories);

            return prodCatalogCategory.getString("productCategoryId");
        } else {
            return null;
        }
    }
          
    public static Collection getCatalogQuickaddCategories(ServletRequest request) {
        return getCatalogQuickaddCategories(request, getCurrentCatalogId(request));
    }
                
    public static Collection getCatalogQuickaddCategories(ServletRequest request, String prodCatalogId) {
        if (prodCatalogId == null || prodCatalogId.length() <= 0) return null;

        Collection categoryIds = new LinkedList();

        Collection prodCatalogCategories = getProdCatalogCategories(request, prodCatalogId, "PCCT_QUICK_ADD");

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
    
    /* ============================= Special Data Retreival Methods ===========================*/
        
    public static List getRandomCartProductAssoc(ServletRequest request, boolean checkViewAllow) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ShoppingCart cart = (ShoppingCart) httpRequest.getSession().getAttribute("shoppingCart");

        if (cart == null || cart.size() <= 0) return null;

        ArrayList cartAssocs = null;
        try {
            Map products = new HashMap();

            Iterator cartiter = cart.iterator();

            while (cartiter != null && cartiter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) cartiter.next();
                // Collection upgradeProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_UPGRADE"), null);
                List complementProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_COMPLEMENT"), null);
                // since ProductAssoc records have a fromDate and thruDate, we can filter by now so that only assocs in the date range are included
                complementProducts = EntityUtil.filterByDate(complementProducts, true);
                
                List productsCategories = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productId", item.getProductId()), null);
                productsCategories = EntityUtil.filterByDate(productsCategories, true);
                if (productsCategories != null) {
                    Iterator productsCategoriesIter = productsCategories.iterator();
                    while (productsCategoriesIter.hasNext()) {
                        GenericValue productsCategoryMember = (GenericValue) productsCategoriesIter.next();
                        GenericValue productsCategory = productsCategoryMember.getRelatedOneCache("ProductCategory");
                        if ("CROSS_SELL_CATEGORY".equals(productsCategory.getString("productCategoryTypeId"))) {
                            List curPcms = productsCategory.getRelatedCache("ProductCategoryMember");
                            if (curPcms != null) {
                                Iterator curPcmsIter = curPcms.iterator();
                                while (curPcmsIter.hasNext()) {
                                    GenericValue curPcm = (GenericValue) curPcmsIter.next();
                                    if (!products.containsKey(curPcm.getString("productId"))) {
                                        GenericValue product = curPcm.getRelatedOneCache("Product");
                                        products.put(product.getString("productId"), product);
                                    }
                                }
                            }
                        }
                    }
                }

                if (complementProducts != null && complementProducts.size() > 0) {
                    Iterator complIter = complementProducts.iterator();
                    while (complIter.hasNext()) {
                        GenericValue productAssoc = (GenericValue) complIter.next();
                        if (!products.containsKey(productAssoc.getString("productIdTo"))) {
                            GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");
                            products.put(product.getString("productId"), product);
                        }
                    }
                }
            }

            // remove all products that are already in the cart
            cartiter = cart.iterator();
            while (cartiter != null && cartiter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) cartiter.next();
                products.remove(item.getProductId());
            }

            // if desired check view allow category
            if (checkViewAllow) {
                String currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
                String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, currentCatalogId);
                if (viewProductCategoryId != null) {
                    List tempList = new ArrayList(products.values());
                    tempList = CategoryWorker.filterProductsInCategory(delegator, tempList, viewProductCategoryId, "productId");
                    cartAssocs = new ArrayList(tempList);
                }
            }
            
            if (cartAssocs == null) {
                cartAssocs = new ArrayList(products.values());
            }

            // randomly remove products while there are more than 3
            while (cartAssocs.size() > 3) {
                int toRemove = (int) (Math.random() * (double) (cartAssocs.size()));
                cartAssocs.remove(toRemove);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        
        if (cartAssocs != null && cartAssocs.size() > 0) {
            return cartAssocs;
        } else {
            return null;
        }
    }
                
    public static Map getQuickReorderProducts(ServletRequest request) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        GenericValue userLogin = (GenericValue) httpRequest.getSession().getAttribute("userLogin");
        Map results = new HashMap();

        if (userLogin == null) userLogin = (GenericValue) httpRequest.getSession().getAttribute("autoUserLogin");
        if (userLogin == null) return results;

        try {
            Map products = (Map) httpRequest.getSession().getAttribute("_QUICK_REORDER_PRODUCTS_");
            Map productQuantities = (Map) httpRequest.getSession().getAttribute("_QUICK_REORDER_PRODUCT_QUANTITIES_");
            Map productOccurances = (Map) httpRequest.getSession().getAttribute("_QUICK_REORDER_PRODUCT_OCCURANCES_");

            if (products == null || productQuantities == null || productOccurances == null) {
                products = new HashMap();
                productQuantities = new HashMap();
                // keep track of how many times a product occurs in order to find averages and rank by purchase amount
                productOccurances = new HashMap();

                // get all order role entities for user by customer role type
                // final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};
                final String[] USER_ORDER_ROLE_TYPES = {"PLACING_CUSTOMER"};

                for (int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
                    Collection orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", USER_ORDER_ROLE_TYPES[i]), null);
                    Iterator ordersIter = UtilMisc.toIterator(orderRoles);

                    while (ordersIter != null && ordersIter.hasNext()) {
                        GenericValue orderRole = (GenericValue) ordersIter.next();
                        // for each order role get all order items
                        Collection orderItems = orderRole.getRelated("OrderItem");
                        Iterator orderItemsIter = UtilMisc.toIterator(orderItems);

                        while (orderItemsIter != null && orderItemsIter.hasNext()) {
                            GenericValue orderItem = (GenericValue) orderItemsIter.next();
                            // for each order item get the associated product
                            GenericValue product = orderItem.getRelatedOneCache("Product");

                            products.put(product.get("productId"), product);

                            Integer curQuant = (Integer) productQuantities.get(product.get("productId"));

                            if (curQuant == null) curQuant = new Integer(0);
                            Double orderQuant = orderItem.getDouble("quantity");

                            if (orderQuant == null) orderQuant = new Double(0.0);
                            productQuantities.put(product.get("productId"), new Integer(curQuant.intValue() + orderQuant.intValue()));

                            Integer curOcc = (Integer) productOccurances.get(product.get("productId"));

                            if (curOcc == null) curOcc = new Integer(0);
                            productOccurances.put(product.get("productId"), new Integer(curOcc.intValue() + 1));
                        }
                    }
                }

                // go through each product quantity and divide it by the occurances to get the average
                Iterator quantEntries = productQuantities.entrySet().iterator();

                while (quantEntries.hasNext()) {
                    Map.Entry entry = (Map.Entry) quantEntries.next();
                    Object prodId = entry.getKey();
                    Integer quantity = (Integer) entry.getValue();
                    Integer occs = (Integer) productOccurances.get(prodId);
                    int nqint = quantity.intValue() / occs.intValue();

                    if (nqint < 1) nqint = 1;
                    productQuantities.put(prodId, new Integer(nqint));
                }

                httpRequest.getSession().setAttribute("_QUICK_REORDER_PRODUCTS_", new HashMap(products));
                httpRequest.getSession().setAttribute("_QUICK_REORDER_PRODUCT_QUANTITIES_", new HashMap(productQuantities));
                httpRequest.getSession().setAttribute("_QUICK_REORDER_PRODUCT_OCCURANCES_", new HashMap(productOccurances));
            } else {
                // make a copy since we are going to change them
                products = new HashMap(products);
                productQuantities = new HashMap(productQuantities);
                productOccurances = new HashMap(productOccurances);
            }

            // remove all products that are already in the cart
            ShoppingCart cart = (ShoppingCart) httpRequest.getSession().getAttribute("shoppingCart");
            if (cart != null && cart.size() > 0) {
                Iterator cartiter = cart.iterator();
                while (cartiter.hasNext()) {
                    ShoppingCartItem item = (ShoppingCartItem) cartiter.next();
                    String productId = item.getProductId();
                    products.remove(productId);
                    productQuantities.remove(productId);
                    productOccurances.remove(productId);
                }
            }

            // if desired check view allow category
            //if (checkViewAllow) {
                Set prodKeySet = products.keySet();
                String currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
                String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, currentCatalogId);
                if (viewProductCategoryId != null) {
                    Iterator valIter = prodKeySet.iterator();
                    while (valIter.hasNext()) {
                        String productId = (String) valIter.next();
                        if (!CategoryWorker.isProductInCategory(delegator, productId, viewProductCategoryId)) {
                            products.remove(productId);
                            productQuantities.remove(productId);
                            productOccurances.remove(productId);
                        }
                    }
                }
            //}
            
            List reorderProds = new ArrayList(products.values());

            /*
             //randomly remove products while there are more than 5
             while (reorderProds.size() > 5) {
             int toRemove = (int)(Math.random()*(double)(reorderProds.size()));
             reorderProds.remove(toRemove);
             }
             */

            // sort descending by new metric...
            double occurancesModifier = 1.0;
            double quantityModifier = 1.0;
            Map newMetric = new HashMap();
            Iterator occurEntries = productOccurances.entrySet().iterator();

            while (occurEntries.hasNext()) {
                Map.Entry entry = (Map.Entry) occurEntries.next();
                Object prodId = entry.getKey();
                Integer quantity = (Integer) entry.getValue();
                Integer occs = (Integer) productQuantities.get(prodId);
                double nqdbl = quantity.doubleValue() * quantityModifier + occs.doubleValue() * occurancesModifier;

                newMetric.put(prodId, new Double(nqdbl));
            }
            reorderProds = productOrderByMap(reorderProds, newMetric, true);

            // remove extra products - only return 5
            while (reorderProds.size() > 5) {
                reorderProds.remove(reorderProds.size() - 1);
            }

            results.put("products", reorderProds);
            results.put("quantities", productQuantities);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        
        return results;
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

            // null is defined as the smallest possible value
            if (value == null) return value2 == null ? 0 : -1;
            return ((Comparable) value).compareTo(value2);
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
}
