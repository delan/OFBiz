/*
 * $Id$
 * $Log$
 * Revision 1.10  2001/09/28 21:57:53  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.9  2001/09/25 23:05:22  jonesde
 * Added cross sell, up sell, and obsolete product association support.
 *
 * Revision 1.8  2001/09/19 08:42:08  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.7  2001/09/06 15:36:31  epabst
 * watch for null value array
 *
 * Revision 1.6  2001/09/06 14:46:30  epabst
 * avoid exception in common case
 *
 * Revision 1.5  2001/08/31 17:44:04  epabst
 * added shopping cart code
 *
 * Revision 1.4  2001/08/30 22:16:10  epabst
 * added new event for adding items from order to cart
 * improved/fixed orderstatus
 *
 * Revision 1.3  2001/08/28 02:24:34  azeneski
 * Updated shopping cart to use store a reference to the product entity, rather then individual attributes.
 * Worked on the equals() method in ShoppingCartItem.java. Might be fixed now.
 *
 * Revision 1.2  2001/08/27 17:29:31  epabst
 * simplified
 *
 * Revision 1.1.1.1  2001/08/24 01:01:43  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import java.util.*;
import java.text.*;

import org.ofbiz.ecommerce.shoppingcart.ShoppingCart;
import org.ofbiz.ecommerce.shoppingcart.ShoppingCartItem;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.GenericEntityException;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> ShoppingCartEvents.java
 * <p><b>Description:</b> Shopping cart events.
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
 * Created on August 4, 2001, 8:21 PM
 */
public class ShoppingCartEvents {
  
  /** Event to add an item to the shopping cart. */
  public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
    String productId = null;
    String quantityStr = null;
    double quantity = 0;
    HashMap attributes = null;
    
    ShoppingCart cart = getCartObject(request);
    
    // Get the parameters as a MAP, remove the productId and quantity params.
    // The rest should be product attributes.This only works w/ servlet api 2.3
    //Map paramMap = request.getParameterMap();
    Map paramMap = UtilMisc.getParameterMap(request);
    if(paramMap.containsKey("ADD_PRODUCT_ID"))
      productId = (String) paramMap.remove("ADD_PRODUCT_ID");
    else if(paramMap.containsKey("add_product_id"))
      productId = (String) paramMap.remove("add_product_id");
    if(productId == null) {
      request.setAttribute(SiteDefs.ERROR_MESSAGE,"No add_product_id passed.");
      return "error";
    }
    
    if(paramMap.containsKey("QUANTITY"))
      quantityStr = (String) paramMap.remove("QUANTITY");
    else if(paramMap.containsKey("quantity"))
      quantityStr = (String) paramMap.remove("quantity");
    if(quantityStr == null)
      quantityStr = "1";  // default quantity is 1
    
    // parse the quantity
    try {
      quantity = Double.parseDouble(quantityStr);
    }
    catch ( NumberFormatException nfe ) {
      quantity = 1;
    }
    
    // Create a HashMap of product attributes.
    if(paramMap.size() > 0)
      attributes = new HashMap(paramMap);
    
    // Get the product
    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    GenericValue product = null;
    try { product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)); }
    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); product = null; }
    
    if(product == null) {
      request.setAttribute(SiteDefs.ERROR_MESSAGE,"No product found.");
      return "error";
    }
    
    cart.addOrIncreaseItem(product, quantity, attributes);
    
    if(cart.viewCartOnAdd())
      return "success";
    else
      return null;
  }
  
  public static String addToCartFromOrder(HttpServletRequest request, HttpServletResponse response) {
    String orderId = request.getParameter("order_id");
    String[] itemIds = request.getParameterValues("item_id");
    
    if (orderId == null) {
      request.setAttribute(SiteDefs.ERROR_MESSAGE, "No order found.");
      return "error";
    }
    
    ShoppingCart cart = getCartObject(request);
    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    
    boolean noItems;
    if("true".equals(request.getParameter("add_all"))) {
      Iterator itemIter = null;
      try { itemIter = UtilMisc.toIterator(delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null)); }
      catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); itemIter = null; }
      if(itemIter != null && itemIter.hasNext()) {
        noItems = false;
        String errMsg = "";
        while(itemIter.hasNext()) {
          GenericValue orderItem = (GenericValue) itemIter.next();
          try {
            GenericValue relProd = orderItem.getRelatedOne("Product");
            cart.addOrIncreaseItem(relProd, orderItem.getDouble("quantity").doubleValue(), null);
          }
          catch(GenericEntityException e) { 
            Debug.logWarning(e.getMessage());
            errMsg += "<li>Product with ID \"" + orderItem.getString("productId") + "\" not found, line " + orderItem.getString("orderItemSeqId") + " not added.";
          }
        }
        if(errMsg.length() > 0) {
          request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
          return "error";
        }
      } 
      else {
        noItems = true;
      }
    } 
    else {
      noItems = true;
      if(itemIds != null) {
        String errMsg = "";
        for(int i=0; i<itemIds.length; i++) {
          String orderItemSeqId = itemIds[i];
          GenericValue orderItem = null;
          try { orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId)); }
          catch(GenericEntityException e) { 
            Debug.logWarning(e.getMessage());
            errMsg += "<li>Order line \"" + orderItemSeqId + "\" not found, so not added.";
            continue;
          }
          if(orderItem != null) {
            try {
              GenericValue relProd = orderItem.getRelatedOne("Product");
              cart.addOrIncreaseItem(relProd, orderItem.getDouble("quantity").doubleValue(), null);
              noItems = false;
            }
            catch(GenericEntityException e) { 
              Debug.logWarning(e.getMessage());
              errMsg += "<li>Product with ID \"" + orderItem.getString("productId") + "\" not found, line " + orderItem.getString("orderItemSeqId") + " not added.";
            }
          }
        }
        if(errMsg.length() > 0) {
          request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
          return "error";
        }
      }//else no items
    }
    
    if(noItems) {
      request.setAttribute(SiteDefs.ERROR_MESSAGE, "No items found to add.");
      return "error";
    }
    
    return "success";
  }
  
  /** Delete an item from the shopping cart. */
  public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response) {
    ShoppingCart cart = getCartObject(request);
    Map paramMap = UtilMisc.getParameterMap(request);
    Set names = paramMap.keySet();
    Iterator i = names.iterator();
    while ( i.hasNext() ) {
      String o = (String) i.next();
      if ( o.toUpperCase().startsWith("DELETE") ) {
        try {
          String indexStr = o.substring(o.lastIndexOf('_')+1);
          int index = Integer.parseInt(indexStr);
          cart.removeCartItem(index);
        }
        catch ( NumberFormatException nfe ) { }
      }
    }
    return "success";
  }
  
  /** Update the items in the shopping cart. */
  public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
    ShoppingCart cart = getCartObject(request);
    ArrayList deleteList = new ArrayList();
    Map paramMap = UtilMisc.getParameterMap(request);
    Set names = paramMap.keySet();
    Iterator i = names.iterator();
    while ( i.hasNext() ) {
      String o = (String) i.next();
      int underscorePos = o.lastIndexOf('_');
      if (underscorePos >= 0) {
        try {
          String indexStr = o.substring(underscorePos+1);
          int index = Integer.parseInt(indexStr);
          String quantString = (String) paramMap.get(o);
          int quantity = NumberFormat.getNumberInstance().parse(quantString).intValue();
          Debug.log("Got index: " + index + "  AND  quantity: " + quantity);
          
          if ( o.toUpperCase().startsWith("UPDATE") ) {
            if ( quantity == 0 ) {
              deleteList.add(cart.findCartItem(index));
              Debug.log("Added index: " + index + " to delete list.");
            }
            else {
              Debug.log("Setting quantity.");
              cart.findCartItem(index).setQuantity(quantity);
            }
          }
          
          if ( o.toUpperCase().startsWith("DELETE") ) {
            deleteList.add(cart.findCartItem(index));
            Debug.log("Added index: " + index + " to delete list.");
          }
        }
        catch ( NumberFormatException nfe ) {
          Debug.log(nfe,"Caught number format exception.");
        }
        catch ( ParseException pe ) {
          Debug.log(pe,"Caught parse exception.");
        }
        catch ( Exception e ) {
          Debug.log(e,"Caught exception.");
        }
      }//else not a parameter we need
    }
    
    Iterator di = deleteList.iterator();
    while ( di.hasNext() ) {
      Object o = di.next();
      Debug.log("Removing item index: " + cart.getItemIndex(o));
      cart.removeCartItem(cart.getItemIndex(o));
    }
    
    if ( !paramMap.containsKey("always_showcart") )
      cart.viewCartOnAdd(false);
    
    return "success";
  }
  
  /** Empty the shopping cart. */
  public static String clearCart(HttpServletRequest request, HttpServletResponse response) {
    ShoppingCart cart = getCartObject(request);
    cart.clear();
    return "success";
  }
  
  
  // Gets the shopping cart from the session. Used by all events.
  public static ShoppingCart getCartObject(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);
    if ( cart == null )
      cart = new ShoppingCart();
    session.setAttribute(SiteDefs.SHOPPING_CART,cart);
    return cart;
  }
}
