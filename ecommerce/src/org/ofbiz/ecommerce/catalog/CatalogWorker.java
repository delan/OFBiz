/*
 * $Id$
 * $Log$
 * Revision 1.3  2001/10/23 20:06:51  jonesde
 * Added quick add functionality
 *
 * Revision 1.2  2001/10/20 00:26:40  jonesde
 * Finished first pass at multi catalog stuff setup through ecommerce.properties
 *
 * Revision 1.1  2001/10/05 02:29:27  jonesde
 * Refactored CatalogHelper: split into CatalogWorker and in commonapp CategoryWorker and ProductWorker
 *
 * Revision 1.29  2001/10/03 20:22:45  jonesde
 * Added sorting by metric on occurances and quantity to quick reorder
 *
 * Revision 1.28  2001/10/03 05:17:32  jonesde
 * Added reorder products and associated products to sidebars
 *
 * Revision 1.27  2001/10/03 00:12:08  jonesde
 * Added product complements to shopping cart; Formatting cleanup
 *
 * Revision 1.26  2001/09/28 21:57:53  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.25  2001/09/27 15:53:31  epabst
 * refactored code to use getRelatedByAnd, filterByDate
 *
 * Revision 1.24  2001/09/26 05:09:06  jonesde
 * Fixed keyword stuff to work minimally with group name
 *
 * Revision 1.23  2001/09/26 03:00:05  jonesde
 * Started cart assoc prods
 *
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

import java.util.*;
import java.net.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpSession;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

import org.ofbiz.ecommerce.shoppingcart.*;

import org.ofbiz.commonapp.product.category.*;

/**
 * <p><b>Title:</b> CatalogWorker.java
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
public class CatalogWorker {
  
  public static String getEcommercePropertiesValue(PageContext pageContext, String propertyName) {
    return getEcommercePropertiesValue(pageContext.getServletContext(), propertyName);
  }
  public static String getEcommercePropertiesValue(ServletContext servletContext, String propertyName) {
    URL url = (URL)servletContext.getAttribute("ECOMMERCE_PROPERTIES_URL");
    if(url == null) {
      try { url = servletContext.getResource("/WEB-INF/ecommerce.properties"); }
      catch(java.net.MalformedURLException e) { Debug.logError(e); }
      if(url != null) servletContext.setAttribute("ECOMMERCE_PROPERTIES_URL", url);
    }
    if(url == null) return null;
    else return UtilProperties.getPropertyValue(url, propertyName);
  }
  
  public static String getCurrentCatalogId(PageContext pageContext) {
    String catalogId;
    boolean fromSession = false;
    //first see if a new catalog was specified as a parameter
    catalogId = pageContext.getRequest().getParameter("CURRENT_CATALOG_ID");
    //if no parameter, try from session
    if(catalogId == null) {
      catalogId = (String)pageContext.getSession().getAttribute("CURRENT_CATALOG_ID");
      if(catalogId != null) fromSession = true;
    }
    //if nothing else, just use a default top category name
    if(catalogId == null) catalogId = getEcommercePropertiesValue(pageContext, "catalog.id.default");
    if(catalogId == null) catalogId = "catalog1";
    
    if(!fromSession) {
      Debug.logInfo("[CatalogWorker.getCurrentCatalogId] Setting new catalog name: " + catalogId);
      pageContext.getSession().setAttribute("CURRENT_CATALOG_ID", catalogId);
      CategoryWorker.setTrail(pageContext, new ArrayList());
    }
    return catalogId;
  }
  
  public static Collection getCatalogIdsAvailable(PageContext pageContext) {
    String catsList = getEcommercePropertiesValue(pageContext, "catalog.ids.available");
    Collection categoryIds = new LinkedList();
    StringTokenizer tokenizer = new StringTokenizer(catsList, ", ");
    while (tokenizer.hasMoreTokens()) { categoryIds.add(tokenizer.nextToken()); }
    return categoryIds;
  }
  
  public static String getCatalogName(PageContext pageContext) {
    return getCatalogName(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static String getCatalogName(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return null;
    return getEcommercePropertiesValue(pageContext, catalogId + ".name");
  }
  
  public static String getCatalogTopCategoryId(PageContext pageContext) {
    return getCatalogTopCategoryId(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static String getCatalogTopCategoryId(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return null;
    return getEcommercePropertiesValue(pageContext, catalogId + ".top.category");
  }
  
  public static String getCatalogSearchCategoryId(PageContext pageContext) {
    return getCatalogSearchCategoryId(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static String getCatalogSearchCategoryId(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return null;
    return getEcommercePropertiesValue(pageContext, catalogId + ".search.category");
  }
  
  public static String getCatalogPromotionsCategoryId(PageContext pageContext) {
    return getCatalogPromotionsCategoryId(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static String getCatalogPromotionsCategoryId(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return null;
    return getEcommercePropertiesValue(pageContext, catalogId + ".promotions.category");
  }
  
  public static boolean getCatalogQuickaddUse(PageContext pageContext) {
    return getCatalogQuickaddUse(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static boolean getCatalogQuickaddUse(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return false;
    return "true".equals(getEcommercePropertiesValue(pageContext, catalogId + ".quickadd.use"));
  }
  
  public static String getCatalogQuickaddCategoryPrimary(PageContext pageContext) {
    return getCatalogQuickaddCategoryPrimary(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static String getCatalogQuickaddCategoryPrimary(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return null;
    return getEcommercePropertiesValue(pageContext, catalogId + ".quickadd.category.primary");
  }
  
  public static Collection getCatalogQuickaddCategories(PageContext pageContext) {
    return getCatalogQuickaddCategories(pageContext, getCurrentCatalogId(pageContext));
  }
  
  public static Collection getCatalogQuickaddCategories(PageContext pageContext, String catalogId) {
    if(catalogId == null || catalogId.length() <= 0) return null;
    String catsList = getEcommercePropertiesValue(pageContext, catalogId + ".quickadd.categories");
    Collection categoryIds = new LinkedList();
    StringTokenizer tokenizer = new StringTokenizer(catsList, ", ");
    while (tokenizer.hasMoreTokens()) { categoryIds.add(tokenizer.nextToken()); }
    return categoryIds;
  }
  
  public static void getRandomCartProductAssoc(PageContext pageContext, String assocsAttrName) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    ShoppingCart cart = (ShoppingCart)pageContext.getSession().getAttribute("_SHOPPING_CART_");
    if(cart == null || cart.size() <= 0) return;
    
    try {
      Map products = new HashMap();
      
      Iterator cartiter = cart.iterator();
      while(cartiter != null && cartiter.hasNext()) {
        ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
        //Collection upgradeProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_UPGRADE"), null);
        Collection complementProducts = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productId", item.getProductId(), "productAssocTypeId", "PRODUCT_COMPLEMENT"), null);
        //since ProductAssoc records have a fromDate and thruDate, we can filter by now so that only assocs in the date range are included
        complementProducts = EntityUtil.filterByDate(complementProducts);

        //if(upgradeProducts != null && upgradeProducts.size() > 0) pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
        if(complementProducts != null && complementProducts.size() > 0) {
          Iterator complIter = complementProducts.iterator();
          while(complIter.hasNext()) {
            GenericValue productAssoc = (GenericValue)complIter.next();
            GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");
            products.put(product.getString("productId"), product);
          }
        }
      }
      
      //remove all products that are already in the cart
      cartiter = cart.iterator();
      while(cartiter != null && cartiter.hasNext()) {
        ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
        products.remove(item.getProductId());
      }

      ArrayList cartAssocs = new ArrayList(products.values());
      //randomly remove products while there are more than 3
      while(cartAssocs.size() > 3) {
        int toRemove = (int)(Math.random()*(double)(cartAssocs.size()));
        cartAssocs.remove(toRemove);
      }
      pageContext.setAttribute(assocsAttrName, cartAssocs);
    }
    catch(GenericEntityException e) {
      Debug.logWarning(e);
    }
  }

  public static void getQuickReorderProducts(PageContext pageContext, String productsAttrName, String quantitiesAttrName) {
    GenericDelegator delegator = (GenericDelegator)pageContext.getServletContext().getAttribute("delegator");
    GenericValue userLogin = (GenericValue)pageContext.getSession().getAttribute(SiteDefs.USER_LOGIN);
    if(userLogin == null) return;
    
    try {
      Map products = (Map)pageContext.getSession().getAttribute("_QUICK_REORDER_PRODUCTS_");
      Map productQuantities = (Map)pageContext.getSession().getAttribute("_QUICK_REORDER_PRODUCT_QUANTITIES_");
      Map productOccurances = (Map)pageContext.getSession().getAttribute("_QUICK_REORDER_PRODUCT_OCCURANCES_");
      
      if(products == null || productQuantities == null || productOccurances == null) {
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
          while(ordersIter != null && ordersIter.hasNext()) {
            GenericValue orderRole = (GenericValue)ordersIter.next();
            //for each order role get all order items
            Collection orderItems = orderRole.getRelated("OrderItem");
            Iterator orderItemsIter = UtilMisc.toIterator(orderItems);
            while(orderItemsIter != null && orderItemsIter.hasNext()) {
              GenericValue orderItem = (GenericValue)orderItemsIter.next();
              //for each order item get the associated product
              GenericValue product = orderItem.getRelatedOneCache("Product");
              products.put(product.get("productId"), product);

              Integer curQuant = (Integer)productQuantities.get(product.get("productId"));
              if(curQuant == null) curQuant = new Integer(0);
              Double orderQuant = orderItem.getDouble("quantity");
              if(orderQuant == null) orderQuant = new Double(0.0);
              productQuantities.put(product.get("productId"), new Integer(curQuant.intValue() + orderQuant.intValue()));

              Integer curOcc = (Integer)productOccurances.get(product.get("productId"));
              if(curOcc == null) curOcc = new Integer(0);
              productOccurances.put(product.get("productId"), new Integer(curOcc.intValue() + 1));
            }
          }
        }
        
        //go through each product quantity and divide it by the occurances to get the average
        Iterator quantEntries = productQuantities.entrySet().iterator();
        while(quantEntries.hasNext()) {
          Map.Entry entry = (Map.Entry)quantEntries.next();
          Object prodId = entry.getKey();
          Integer quantity = (Integer)entry.getValue();
          Integer occs = (Integer)productOccurances.get(prodId);
          int nqint = quantity.intValue()/occs.intValue();
          if(nqint < 1) nqint = 1;
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
      if(cart != null && cart.size() > 0) {
        Iterator cartiter = cart.iterator();
        while(cartiter.hasNext()) {
          ShoppingCartItem item = (ShoppingCartItem)cartiter.next();
          products.remove(item.getProductId());
          productQuantities.remove(item.getProductId());
          productOccurances.remove(item.getProductId());
        }
      }

      List reorderProds = new ArrayList(products.values());
      /*
      //randomly remove products while there are more than 5
      while(reorderProds.size() > 5) {
        int toRemove = (int)(Math.random()*(double)(reorderProds.size()));
        reorderProds.remove(toRemove);
      }
      */
      
      //sort descending by new metric...
      double occurancesModifier = 1.0;
      double quantityModifier = 1.0;
      Map newMetric = new HashMap();
      Iterator occurEntries = productOccurances.entrySet().iterator();
      while(occurEntries.hasNext()) {
        Map.Entry entry = (Map.Entry)occurEntries.next();
        Object prodId = entry.getKey();
        Integer quantity = (Integer)entry.getValue();
        Integer occs = (Integer)productQuantities.get(prodId);
        double nqdbl = quantity.doubleValue()*quantityModifier + occs.doubleValue()*occurancesModifier;
        newMetric.put(prodId, new Double(nqdbl));
      }      
      reorderProds = productOrderByMap(reorderProds, newMetric, true);
      
      //remove extra products - only return 5
      while(reorderProds.size() > 5) {
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
      if(descending) {
        result = -result;
      }
      return result;
    }
    
    private int compareAsc(GenericEntity prod1, GenericEntity prod2) {
      Object value = orderByMap.get(prod1.get("productId"));
      Object value2 = orderByMap.get(prod2.get("productId"));
      //null is defined as the smallest possible value
      if(value == null) return value2 == null ? 0 : -1;
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
}
