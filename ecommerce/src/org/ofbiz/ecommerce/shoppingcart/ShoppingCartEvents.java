/*
 * $Id$
 * $Log$
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

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.ecommerce.shoppingcart.ShoppingCart;
import org.ofbiz.ecommerce.shoppingcart.ShoppingCartItem;

import org.ofbiz.core.entity.GenericEntity;
import org.ofbiz.core.entity.GenericHelper;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.GenericHelperFactory;
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
        if ( paramMap.containsKey("PRODUCT_ID") )
            productId = (String) paramMap.remove("PRODUCT_ID");
        else if  ( paramMap.containsKey("product_id") )
            productId = (String) paramMap.remove("product_id");
        if ( productId == null ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"No product_id passed.");
            return "error";
        }
        
        if ( paramMap.containsKey("QUANTITY") )
            quantityStr = (String) paramMap.remove("QUANTITY");
        else if ( paramMap.containsKey("quantity") )
            quantityStr = (String) paramMap.remove("quantity");
        if ( quantityStr == null )
            quantityStr = "1";  // default quantity is 1
        
        // parse the quantity        
        try {
            quantity = Double.parseDouble(quantityStr);
        }
        catch ( NumberFormatException nfe ) {
            quantity = 1;
        }
        
        // Create a HashMap of product attributes.
        if ( paramMap.size() > 0 )
            attributes = new HashMap(paramMap);
        
        // Get the product 
        GenericHelper helper = (GenericHelper) request.getAttribute("helper");
        GenericValue product = helper.findByPrimaryKey("Product", 
                UtilMisc.toMap("productId", productId));
        
        if ( product == null ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"No product found.");
            return "error";
        }
                    
        cart.addOrIncreaseItem(product, quantity, attributes);

        if ( cart.viewCartOnAdd() )
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
        GenericHelper helper = (GenericHelper) request.getAttribute("helper");
        
        boolean noItems;
        if ("true".equals(request.getParameter("add_all"))) {
            Iterator itemIter = helper.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null).iterator();
            if (itemIter.hasNext()) {
                noItems = false;
                do {
                    GenericValue orderItem = (GenericValue) itemIter.next();
                    cart.addOrIncreaseItem(orderItem.getRelatedOne("Product"), orderItem.getDouble("quantity").doubleValue(), null);
                } while (itemIter.hasNext());
            } else {
                noItems = true;
            }
        } else {
            noItems = true;
            for (int i = 0; i < itemIds.length; i++) {
                String orderItemSeqId = itemIds[i];
                GenericValue orderItem = helper.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                cart.addOrIncreaseItem(orderItem.getRelatedOne("Product"), orderItem.getDouble("quantity").doubleValue(), null);
                noItems = false;
            }
        }
        
        if (noItems) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No items found to add.");
            return "error";
        }
            
        if ( cart.viewCartOnAdd() )
            return "success";
        else
            return null;
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
            try {
                String indexStr = o.substring(o.lastIndexOf('_')+1);
                int index = Integer.parseInt(indexStr);
                int quantity = Integer.parseInt((String) paramMap.get(o));
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
