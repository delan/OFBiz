/*
 * $Id$
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
package org.ofbiz.commonapp.product.catalog;

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.commonapp.product.category.*;
import org.ofbiz.commonapp.order.shoppingcart.*;

/**
 * CatalogWorker - Worker class for catalog related functionality
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class CatalogWorker {
    
    public static final String module = CatalogWorker.class.getName();

    public static boolean isCatalogInventoryRequired(String prodCatalogId, String productId, GenericDelegator delegator) {
        GenericValue product = null;

        if (productId != null) {
            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up product with id " + productId + ", will check the ProdCatalog for inventory required", module);
            }
        }

        return isCatalogInventoryRequired(prodCatalogId, product, delegator);
    }

    public static boolean isCatalogInventoryRequired(ShoppingCartItem item) {
        return isCatalogInventoryRequired(item.getProdCatalogId(), item.getProduct(), item.getDelegator());
    }
       
    public static boolean isCatalogInventoryRequired(String prodCatalogId, GenericValue product, GenericDelegator delegator) {
        // look at the product first since it over-rides the prodCatalog setting; if empty or null use the prodCatalog setting
        try {
            if (product != null && UtilValidate.isNotEmpty(product.getString("requireInventory"))) {
                if ("Y".equals(product.getString("requireInventory"))) {
                    return true;
                } else if ("N".equals(product.getString("requireInventory"))) {
                    return false;
                }
            }
            // otherwise, check the prodCatalog...

            GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));

            if (prodCatalog == null) {
                Debug.logWarning("ProdCatalog not found with id " + prodCatalogId + ", returning false for inventory required check", module);
                return false;
            }

            // default to false, so if anything but Y, return false
            return "Y".equals(prodCatalog.getString("requireInventory"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId + ", returning false for inventory required", module);
            return false;
        }
    }

    public static boolean isCatalogInventoryAvailable(ShoppingCartItem item, LocalDispatcher dispatcher) {
        return isCatalogInventoryAvailable(item.getProdCatalogId(), item.getProductId(), item.getQuantity(), item.getDelegator(), dispatcher);
    }
    
    /** check inventory availability for the given catalog, product, quantity, etc */
    public static boolean isCatalogInventoryAvailable(String prodCatalogId, String productId, double quantity, GenericDelegator delegator, LocalDispatcher dispatcher) {
        GenericValue prodCatalog = null;

        try {
            prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId, module);
        }

        if (prodCatalog == null) {
            Debug.logWarning("No catalog found with id " + prodCatalogId + ", returning false for inventory available check", module);
            return false;
        }

        // if prodCatalog is set to not check inventory break here
        if ("N".equals(prodCatalog.getString("checkInventory"))) {
            // note: if not set, defaults to yes, check inventory
            if (Debug.infoOn()) Debug.logInfo("Catalog with id " + prodCatalogId + ", is set to NOT check inventory, returning true for inventory available check", module);
            return true;
        }

        if ("Y".equals(prodCatalog.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = prodCatalog.getString("inventoryFacilityId");

            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("Catalog with id " + prodCatalogId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, returning false for inventory check", module);
                return false;
            }

            Double availableToPromise = null;

            try {
                Map result = dispatcher.runSync("getInventoryAvailableByFacility",
                        UtilMisc.toMap("productId", productId, "facilityId", inventoryFacilityId));

                availableToPromise = (Double) result.get("availableToPromise");

                if (availableToPromise == null) {
                    Debug.logWarning("The getInventoryAvailableByFacility service returned a null availableToPromise, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE), module);
                    return false;
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking getInventoryAvailableByFacility service in isCatalogInventoryAvailable", module);
                return false;
            }

            // whew, finally here: now check to see if we got enough back...
            if (availableToPromise.doubleValue() >= quantity) {
                if (Debug.infoOn()) Debug.logInfo("Inventory IS available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise, module);
                return true;
            } else {
                if (Debug.infoOn()) Debug.logInfo("Returning false because there is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", available quantity is " + availableToPromise, module);
                return false;
            }

        } else {
            Debug.logWarning("Catalog with id " + prodCatalogId + " uses multiple inventory facilities, which is not yet implemented, return false for inventory check", module);
            return false;

            // TODO: check multiple inventory locations

            // must entire quantity be available in one location?

            // loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }

    /** tries to reserve the specified quantity, if fails returns quantity that it could not reserve or zero if there was an error, otherwise returns null */
    public static Double reserveCatalogInventory(String prodCatalogId, String productId, Double quantity,
        String orderId, String orderItemSeqId, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher) {

        GenericValue prodCatalog = null;

        try {
            prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId, module);
        }
        if (prodCatalog == null) {
            Debug.logWarning("No catalog found with id " + prodCatalogId + ", not reserving inventory", module);
            return new Double(0.0);
        }

        // if prodCatalog is set to not reserve inventory, break here
        if ("N".equals(prodCatalog.getString("reserveInventory"))) {
            // note: if not set, defaults to yes, reserve inventory
            if (Debug.infoOn()) Debug.logInfo("Catalog with id " + prodCatalogId + ", is set to NOT reserve inventory, not reserving inventory", module);
            return null;
        }

        if ("Y".equals(prodCatalog.getString("oneInventoryFacility"))) {
            String inventoryFacilityId = prodCatalog.getString("inventoryFacilityId");

            if (UtilValidate.isEmpty(inventoryFacilityId)) {
                Debug.logWarning("Catalog with id " + prodCatalogId + " has Y for oneInventoryFacility but inventoryFacilityId is empty, not reserving inventory", module);
                return new Double(0.0);
            }

            boolean requireInventory = isCatalogInventoryRequired(prodCatalogId, productId, delegator);
            Double quantityNotReserved = null;

            try {
                Map serviceContext = new HashMap();

                serviceContext.put("productId", productId);
                serviceContext.put("facilityId", inventoryFacilityId);
                serviceContext.put("orderId", orderId);
                serviceContext.put("orderItemSeqId", orderItemSeqId);
                serviceContext.put("quantity", quantity);

                if (requireInventory) {
                    serviceContext.put("requireInventory", "Y");
                } else {
                    serviceContext.put("requireInventory", "N");
                }
                serviceContext.put("reserveOrderEnumId", prodCatalog.get("reserveOrderEnumId"));
                serviceContext.put("userLogin", userLogin);

                Map result = dispatcher.runSync("reserveProductInventoryByFacility", serviceContext);

                quantityNotReserved = (Double) result.get("quantityNotReserved");

                if (quantityNotReserved == null) {
                    Debug.logWarning("The reserveProductInventoryByFacility service returned a null quantityNotReserved, the error message was:\n" + result.get(ModelService.ERROR_MESSAGE), module);
                    if (!requireInventory) {
                        return null;
                    } else {
                        return new Double(0.0);
                    }
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, "Error invoking reserveProductInventoryByFacility service", module);
                if (!requireInventory) {
                    return null;
                } else {
                    return new Double(0.0);
                }
            }

            // whew, finally here: now check to see if we were able to reserve...
            if (quantityNotReserved.doubleValue() == 0) {
                if (Debug.infoOn()) Debug.logInfo("Inventory IS reserved in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity was " + quantity, module);
                return null;
            } else {
                if (Debug.infoOn()) Debug.logInfo("There is insufficient inventory available in facility with id " + inventoryFacilityId + " for product id " + productId + "; desired quantity is " + quantity + ", amount could not reserve is " + quantityNotReserved, module);
                return quantityNotReserved;
            }

        } else {
            Debug.logError("Catalog with id " + prodCatalogId + " uses multiple inventory facilities, which is not yet implemented, not reserving inventory", module);
            return new Double(0.0);

            // TODO: check multiple inventory locations

            // must entire quantity be available in one location?

            // loop through all facilities attached to this catalog and check for individual or cumulative sufficient inventory
        }
    }

    // ----------------------------
    // These came from ecommerce
    // ----------------------------

    public static String getWebSiteId(PageContext pageContext) {
        return pageContext.getServletContext().getInitParameter("webSiteId");
    }

    public static String getWebSiteId(ServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));

        if (application == null) return null;
        return application.getInitParameter("webSiteId");
    }
    
    public static GenericValue getWebSite(PageContext pageContext) {
        return getWebSite(pageContext.getRequest());
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

    public static List getWebSiteCatalogs(PageContext pageContext) {
        return getWebSiteCatalogs(pageContext.getRequest());
    }

    public static List getWebSiteCatalogs(ServletRequest request) {
        String webSiteId = getWebSiteId(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        try {
            return EntityUtil.filterByDate(delegator.findByAndCache("WebSiteCatalog", UtilMisc.toMap("webSiteId", webSiteId), UtilMisc.toList("sequenceNum", "prodCatalogId")), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up website catalogs for website with id " + webSiteId, module);
        }
        return null;
    }

    public static List getPartyCatalogs(PageContext pageContext) {
        return getPartyCatalogs((HttpServletRequest) pageContext.getRequest());
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

    public static List getProdCatalogCategories(PageContext pageContext, String prodCatalogId, String prodCatalogCategoryTypeId) {
            return getProdCatalogCategories(pageContext.getRequest(), prodCatalogId, prodCatalogCategoryTypeId);
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

    public static String getCurrentCatalogId(PageContext pageContext) {
        return getCurrentCatalogId(pageContext.getRequest());
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

    public static List getCatalogIdsAvailable(PageContext pageContext) {
        return getCatalogIdsAvailable((HttpServletRequest) pageContext.getRequest());
    }
    
    public static List getCatalogIdsAvailable(ServletRequest request) {
        List categoryIds = new LinkedList();
        List partyCatalogs = getPartyCatalogs(request);
        List webSiteCatalogs = getWebSiteCatalogs(request);
        List allCatalogLinks = new ArrayList((webSiteCatalogs == null ? 0 : webSiteCatalogs.size()) + (partyCatalogs == null ? 0 : partyCatalogs.size()));
        if (partyCatalogs != null) allCatalogLinks.addAll(partyCatalogs);
        if (webSiteCatalogs != null) allCatalogLinks.addAll(webSiteCatalogs);
        
        if (allCatalogLinks.size() > 0) {
            Iterator aclIter = allCatalogLinks.iterator();
            while (aclIter.hasNext()) {
                GenericValue catalogLink = (GenericValue) aclIter.next();
                categoryIds.add(catalogLink.getString("prodCatalogId"));
            }
        }
        return categoryIds;
    }

    public static String getCatalogName(PageContext pageContext) {
        return getCatalogName(pageContext.getRequest());
    }
    
    public static String getCatalogName(PageContext pageContext, String prodCatalogId) {
        return getCatalogName(pageContext.getRequest(), prodCatalogId);
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

    public static String getContentPathPrefix(PageContext pageContext) {
        return getContentPathPrefix(pageContext.getRequest());
    }
    
    public static String getContentPathPrefix(ServletRequest request) {
        GenericValue prodCatalog = getProdCatalog(request, getCurrentCatalogId(request));

        if (prodCatalog == null) return "";
        String contentPathPrefix = prodCatalog.getString("contentPathPrefix");

        return StringUtil.cleanUpPathPrefix(contentPathPrefix);
    }

    public static String getTemplatePathPrefix(PageContext pageContext) {
        return getTemplatePathPrefix(pageContext.getRequest());
    }
        
    public static String getTemplatePathPrefix(ServletRequest request) {
        GenericValue prodCatalog = getProdCatalog(request, getCurrentCatalogId(request));

        if (prodCatalog == null) return "";
        String templatePathPrefix = prodCatalog.getString("templatePathPrefix");

        return StringUtil.cleanUpPathPrefix(templatePathPrefix);
    }

    public static GenericValue getProdCatalog(PageContext pageContext) {
        return getProdCatalog(pageContext, getCurrentCatalogId(pageContext));
    }

    public static GenericValue getProdCatalog(ServletRequest request) {
        return getProdCatalog(request, getCurrentCatalogId(request));
    }

    public static GenericValue getProdCatalog(PageContext pageContext, String prodCatalogId) {
        return getProdCatalog(pageContext.getRequest(), prodCatalogId);
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

    public static String getCatalogTopCategoryId(PageContext pageContext) {
        return getCatalogTopCategoryId(pageContext.getRequest());
    }
    
    public static String getCatalogTopCategoryId(ServletRequest request) {
        return getCatalogTopCategoryId(request, getCurrentCatalogId(request));
    }
    
    public static String getCatalogTopCategoryId(PageContext pageContext, String prodCatalogId) {
        return getCatalogTopCategoryId(pageContext.getRequest(), prodCatalogId);            
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
    public static String getCatalogSearchCategoryId(PageContext pageContext) {
        return getCatalogSearchCategoryId(pageContext.getRequest());
    }
    
    public static String getCatalogSearchCategoryId(ServletRequest request) {
        return getCatalogSearchCategoryId(request, getCurrentCatalogId(request));
    }
    
    public static String getCatalogSearchCategoryId(PageContext pageContext, String prodCatalogId) {
        return getCatalogSearchCategoryId(pageContext.getRequest(), prodCatalogId);
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

    public static String getCatalogPromotionsCategoryId(PageContext pageContext) {
        return getCatalogPromotionsCategoryId(pageContext.getRequest());
    }
    
    public static String getCatalogPromotionsCategoryId(ServletRequest request) {
        return getCatalogPromotionsCategoryId(request, getCurrentCatalogId(request));
    }

    public static String getCatalogPromotionsCategoryId(PageContext pageContext, String prodCatalogId) {
        return getCatalogPromotionsCategoryId(pageContext.getRequest(), prodCatalogId);
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

    public static boolean getCatalogQuickaddUse(PageContext pageContext) {
        return getCatalogQuickaddUse(pageContext, getCurrentCatalogId(pageContext));
    }

    public static boolean getCatalogQuickaddUse(ServletRequest request) {
        return getCatalogQuickaddUse(request, getCurrentCatalogId(request));
    }

    public static boolean getCatalogQuickaddUse(PageContext pageContext, String prodCatalogId) {
        return getCatalogQuickaddUse(pageContext.getRequest(), prodCatalogId);
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

    public static String getCatalogQuickaddCategoryPrimary(PageContext pageContext) {
        return getCatalogQuickaddCategoryPrimary(pageContext.getRequest());
    }
               
    public static String getCatalogQuickaddCategoryPrimary(ServletRequest request) {
        return getCatalogQuickaddCategoryPrimary(request, getCurrentCatalogId(request));
    }

    public static String getCatalogQuickaddCategoryPrimary(PageContext pageContext, String prodCatalogId) {
        return getCatalogQuickaddCategoryPrimary(pageContext.getRequest(), prodCatalogId);
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

    public static Collection getCatalogQuickaddCategories(PageContext pageContext) {
        return getCatalogQuickaddCategories(pageContext.getRequest());
    }
                    
    public static Collection getCatalogQuickaddCategories(ServletRequest request) {
        return getCatalogQuickaddCategories(request, getCurrentCatalogId(request));
    }

    public static Collection getCatalogQuickaddCategories(PageContext pageContext, String prodCatalogId) {
        return getCatalogQuickaddCategories(pageContext.getRequest(), prodCatalogId);
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
    
    /* ================================ Catalog Inventory Check ===============================*/

    public static boolean isCatalogInventoryRequired(ServletRequest request, GenericValue product) {
        String prodCatalogId = getCurrentCatalogId((HttpServletRequest) request);

        if (prodCatalogId == null || prodCatalogId.length() == 0) {
            Debug.logWarning("No current catalog id found, return false for inventory check", module);
            return false;
        }
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        return CatalogWorker.isCatalogInventoryRequired(prodCatalogId, product, delegator);
    }

    public static boolean isCatalogInventoryAvailable(ServletRequest request, String productId, double quantity) {
        String prodCatalogId = getCurrentCatalogId((HttpServletRequest) request);

        if (prodCatalogId == null || prodCatalogId.length() == 0) {
            Debug.logWarning("No current catalog id found, return false for inventory check", module);
            return false;
        }
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        return CatalogWorker.isCatalogInventoryAvailable(prodCatalogId, productId, quantity, delegator, dispatcher);
    }

    /* ========================================================================================*/
    
    /* ============================= Special Data Retreival Methods ===========================*/

    public static void getRandomCartProductAssoc(PageContext pageContext, String assocsAttrName, boolean checkViewAllow) {
        List returnList = getRandomCartProductAssoc(pageContext.getRequest(), checkViewAllow);
        if (returnList != null) pageContext.setAttribute(assocsAttrName, returnList);
    }
                
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

    public static void getQuickReorderProducts(PageContext pageContext, String productsAttrName, String quantitiesAttrName) {
        Map results = getQuickReorderProducts(pageContext.getRequest());
        pageContext.setAttribute(productsAttrName, results.get("products"));
        pageContext.setAttribute(quantitiesAttrName, results.get("quantities"));
    }
                
    public static Map getQuickReorderProducts(ServletRequest request) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        GenericValue userLogin = (GenericValue) httpRequest.getSession().getAttribute(SiteDefs.USER_LOGIN);
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
            ShoppingCart cart = (ShoppingCart) httpRequest.getSession().getAttribute(SiteDefs.SHOPPING_CART);
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
                        
    public static void printSubCategories(GenericValue pcategory, GenericValue category, String curcatid, PageContext pageContext) throws java.io.IOException {
        String controlPath = (String) pageContext.getRequest().getAttribute(SiteDefs.CONTROL_PATH);
        JspWriter out = pageContext.getOut();

        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        String pstr = "";

        if (pcategory != null) pstr = "&pcategory=" + pcategory.getString("productCategoryId");

        if (curcatid != null && curcatid.equals(category.getString("productCategoryId"))) {
            out.print("<div class='browsecategorytext'>-&nbsp;<a href='");
            out.print(response.encodeURL(controlPath + "/category?category_id=" + category.getString("productCategoryId") + pstr));
            out.print("' class='browsecategorybuttondisabled'>");
            out.print(category.getString("description"));
            out.println("</a></div>");
        } else {
            out.print("<div class='browsecategorytext'>-&nbsp;<a href='");
            out.print(response.encodeURL(controlPath + "/category?category_id=" + category.getString("productCategoryId") + pstr));
            out.print("' class='browsecategorybutton'>");
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
