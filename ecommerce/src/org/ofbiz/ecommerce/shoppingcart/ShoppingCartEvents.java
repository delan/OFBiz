/*
 * $Id$
 * $Log$
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
        String description = null;
        double quantity = 0;
        double price = 0.00;
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
        HashMap fieldMap = new HashMap();
        fieldMap.put("productId",productId);
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        List products = (List) helper.findByAnd("Product",fieldMap,null);
        GenericEntity product = (GenericEntity) products.get(0);
        
        if ( product == null ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"No product found.");
            return "error";
        }
        
        // Here is where more detailed price finding will go.
        try {
            description = product.getString("description");
            price = product.getDouble("defaultPrice").doubleValue();
        }
        catch ( Exception e ) { }
         
        // create a new shopping cart item.
        ShoppingCartItem newItem = new ShoppingCartItem(productId,description,price,quantity,attributes);
        Debug.log("New item created: " + newItem.getProductId());
        
        // Check for existing cart item.
        boolean foundMatch = false;
        Debug.log("Cart size: " + cart.size());
        if ( cart.size() > 0 ) {
            Iterator i = cart.iterator();
            while ( i.hasNext() ) {
                ShoppingCartItem sci = (ShoppingCartItem) i.next();
                Debug.log("Comparing to item: " + sci.getProductId());
                if ( sci.equals(newItem) ) {
                    foundMatch = true;
                    Debug.log("Found a match, updating quantity.");
                    sci.setQuantity(sci.getQuantity() + quantity);
                }
            }
        }
                        
        // Add the item to the shopping cart if it wasn't found.
        if ( !foundMatch )     
            cart.addItem(0,newItem);

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
            Object o = i.next();
            if ( ((String)o).toUpperCase().startsWith("DELETE") ) {
                try {
                    String indexStr = ((String)o).substring(((String)o).lastIndexOf('_')+1);
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
            Object o = i.next();
            try {
                String indexStr = ((String)o).substring(((String)o).lastIndexOf('_')+1);
                int index = Integer.parseInt(indexStr);
                int quantity = Integer.parseInt((String) paramMap.get(o));
                Debug.log("Got index: " + index + "  AND  quantity: " + quantity);
                
                if ( ((String)o).toUpperCase().startsWith("UPDATE") ) {
                    if ( quantity == 0 ) {                        
                        deleteList.add(cart.findCartItem(index));
                        Debug.log("Added index: " + index + " to delete list.");
                    }
                    else {
                        Debug.log("Setting quantity.");
                        cart.findCartItem(index).setQuantity(quantity);
                    }
                }
                
                if ( ((String)o).toUpperCase().startsWith("DELETE") ) {                    
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
    private static ShoppingCart getCartObject(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);
        if ( cart == null )
            cart = new ShoppingCart();
        session.setAttribute(SiteDefs.SHOPPING_CART,cart);        
        return cart;
    }        
}
