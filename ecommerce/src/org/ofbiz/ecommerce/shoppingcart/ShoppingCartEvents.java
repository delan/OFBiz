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

package org.ofbiz.ecommerce.shoppingcart;

import java.util.*;
import java.text.*;
import javax.servlet.http.*;
import javax.servlet.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.ecommerce.catalog.*;


/**
 * Shopping cart events.
 *
 * @author     Andy Zeneski (jaz@zsolv.com)
 * @version    1.0
 * @created    August 4, 2001, 8:21 PM
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
        if (paramMap.containsKey("ADD_PRODUCT_ID")) {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } else if (paramMap.containsKey("add_product_id")) {
            productId = (String) paramMap.remove("add_product_id");
        }
        if (productId == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"No add_product_id passed.");
            return "error";
        }
        
        if (paramMap.containsKey("QUANTITY")) {
            quantityStr = (String) paramMap.remove("QUANTITY");
        } else if (paramMap.containsKey("quantity")) {
            quantityStr = (String) paramMap.remove("quantity");
        }
        if (quantityStr == null)
            quantityStr = "1";  // default quantity is 1
        
        // parse the quantity
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException nfe) {
            quantity = 1;
        }
        
        // Create a HashMap of product attributes.
        if (paramMap.size() > 0)
            attributes = new HashMap(paramMap);
        
        // Get the product
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            product = null;
        }
        
        if (product == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,"Product not found, cannot add to cart. [productId: " + product.getString("productId") + "]");
            return "error";
        }
        
        if ("Y".equals(product.getString("isVirtual"))) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Cannot add a Virtual Product to the cart [productId: " + product.getString("productId") + "]");
            return "error";
        }
        
        //check inventory
        if (!CatalogWorker.isCatalogInventoryAvailable(request, productId, quantity)) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Sorry, we do not have enough of the product " + product.getString("productName") + " [product ID: " + productId + "] in stock. Please try back later or call customer service for more information.");
            //return success since this isn't really a critical error...
            return "success";
        }
        
        cart.addOrIncreaseItem(product, quantity, attributes);
        
        if (cart.viewCartOnAdd())
            return "success";
        else
            return null;
    }
    
    public static String addToCartFromOrder(HttpServletRequest request, HttpServletResponse response) {
        String orderId = request.getParameter("order_id");
        String[] itemIds = request.getParameterValues("item_id");
        
        if (orderId == null || orderId.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No order specified to add from.");
            return "error";
        }
        
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        boolean noItems = true;
        if ("true".equals(request.getParameter("add_all"))) {
            Iterator itemIter = null;
            try {
                itemIter = UtilMisc.toIterator(delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                itemIter = null;
            }
            
            if (itemIter != null && itemIter.hasNext()) {
                String errMsg = "";
                while(itemIter.hasNext()) {
                    GenericValue orderItem = (GenericValue) itemIter.next();
                    try {
                        GenericValue relProd = orderItem.getRelatedOne("Product");
                        if ("Y".equals(relProd.getString("isVirtual"))) {
                            errMsg += "<li>Did not add Virtual Product to the cart [productId: " + relProd.getString("productId") + "]";
                        } else {
                            if (orderItem.get("quantity") != null) {
                                //check inventory
                                if (!CatalogWorker.isCatalogInventoryAvailable(request, relProd.getString("productId"), orderItem.getDouble("quantity").doubleValue())) {
                                    errMsg += "<li>Sorry, we do not have enough of the product " + relProd.getString("productName") + " [product ID: " + relProd.getString("productId") + "] in stock.";
                                } else {
                                    cart.addOrIncreaseItem(relProd, orderItem.getDouble("quantity").doubleValue(), null);
                                    noItems = false;
                                }
                            }
                        }
                    } catch(GenericEntityException e) {
                        Debug.logWarning(e.getMessage());
                        errMsg += "<li>Product with ID \"" + orderItem.getString("productId") + "\" not found, line " + orderItem.getString("orderItemSeqId") + " not added.";
                    }
                }
                if (errMsg.length() > 0) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
                    return "error";
                }
            } else {
                noItems = true;
            }
        } else {
            noItems = true;
            if (itemIds != null) {
                String errMsg = "";
                for(int i=0; i<itemIds.length; i++) {
                    String orderItemSeqId = itemIds[i];
                    GenericValue orderItem = null;
                    try {
                        orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                    } catch(GenericEntityException e) {
                        Debug.logWarning(e.getMessage());
                        errMsg += "<li>Order line \"" + orderItemSeqId + "\" not found, so not added.";
                        continue;
                    }
                    if (orderItem != null) {
                        try {
                            GenericValue relProd = orderItem.getRelatedOne("Product");
                            if ("Y".equals(relProd.getString("isVirtual"))) {
                                errMsg += "<li>Did not add Virtual Product to the cart [productId: " + relProd.getString("productId") + "]";
                            } else {
                                if (orderItem.get("quantity") != null) {
                                    //check inventory
                                    if (!CatalogWorker.isCatalogInventoryAvailable(request, relProd.getString("productId"), orderItem.getDouble("quantity").doubleValue())) {
                                        errMsg += "<li>Sorry, we do not have enough of the product " + relProd.getString("productName") + " [product ID: " + relProd.getString("productId") + "] in stock.";
                                    } else {
                                        cart.addOrIncreaseItem(relProd, orderItem.getDouble("quantity").doubleValue(), null);
                                        noItems = false;
                                    }
                                }
                            }
                        } catch(GenericEntityException e) {
                            Debug.logWarning(e.getMessage());
                            errMsg += "<li>Product with ID \"" + orderItem.getString("productId") + "\" not found, line " + orderItem.getString("orderItemSeqId") + " not added.";
                        }
                    }
                }
                if (errMsg.length() > 0) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
                    return "error";
                }
            }//else no items
        }
        
        if (noItems) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No items found to add.");
            return "error";
        }
        
        return "success";
    }
    
    /** Adds all products in a category according to quantity request parameter
     * for each; if no parameter for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addToCartBulk(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        
        if (categoryId == null || categoryId.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No category specified to add from.");
            return "error";
        }
        
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        Collection prodCatMemberCol = null;
        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember",UtilMisc.toMap("productCategoryId",categoryId));
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not get products in category " + categoryId + " to add to cart (read error): " + e.getMessage());
            return "error";
        }
        
        if (prodCatMemberCol == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not get products in category " + categoryId + " to add to cart (read error).");
            return "error";
        }
        
        String errMsg = "";
        Iterator pcmIter = prodCatMemberCol.iterator();
        while(pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue)pcmIter.next();
            String quantStr = request.getParameter("quantity_" + productCategoryMember.getString("productId"));
            if (quantStr != null && quantStr.length() > 0) {
                double quantity = 0;
                try { quantity = Double.parseDouble(quantStr); }
                catch(NumberFormatException nfe) { quantity = 0; }
                if (quantity > 0.0) {
                    GenericValue product = null;
                    try { product = productCategoryMember.getRelatedOneCache("Product"); }
                    catch(GenericEntityException e) { Debug.logWarning(e.getMessage()); product = null; }
                    if (product == null) {
                        errMsg += "<li>Did not add product with id \"" + productCategoryMember.getString("productId") + "\" because a product with that ID could not be found.";
                    } else {
                        if ("Y".equals(product.getString("isVirtual"))) {
                            errMsg += "<li>Did not add Virtual Product named " + product.getString("productName") + " to the cart [productId: " + product.getString("productId") + "]";
                        } else {
                            //check inventory
                            if (!CatalogWorker.isCatalogInventoryAvailable(request, product.getString("productId"), quantity)) {
                                errMsg += "<li>Sorry, we do not have enough of the product " + product.getString("productName") + " [product ID: " + product.getString("productId") + "] in stock.";
                            } else {
                                cart.addOrIncreaseItem(product, quantity, null);
                            }
                        }
                    }
                }
            }
        }
        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
            return "error";
        }
        
        return "success";
    }
    
    /** Adds all products in a category according to default quantity on ProductCategoryMember
     * for each; if no default for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addCategoryDefaults(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        
        if (categoryId == null || categoryId.length() <= 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No category specified to add from.");
            return "error";
        }
        
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        Collection prodCatMemberCol = null;
        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember",UtilMisc.toMap("productCategoryId",categoryId));
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not get products in category " + categoryId + " to add to cart (read error): " + e.getMessage());
            return "error";
        }
        
        if (prodCatMemberCol == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not get products in category " + categoryId + " to add to cart (read error).");
            return "error";
        }
        
        String errMsg = "";
        double totalQuantity = 0;
        Iterator pcmIter = prodCatMemberCol.iterator();
        while(pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue)pcmIter.next();
            Double quantity = productCategoryMember.getDouble("quantity");
            if (quantity != null && quantity.doubleValue() > 0.0) {
                GenericValue product = null;
                try {
                    product = productCategoryMember.getRelatedOneCache("Product");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage());
                    product = null;
                }
                if (product == null) {
                    errMsg += "<li>Did not add product with id \"" + productCategoryMember.getString("productId") + "\" because a product with that ID could not be found.";
                } else {
                    if ("Y".equals(product.getString("isVirtual"))) {
                        errMsg += "<li>Did not add Virtual Product named " + product.getString("productName") + " to the cart [productId: " + product.getString("productId") + "]";
                    } else {
                        //check inventory
                        if (!CatalogWorker.isCatalogInventoryAvailable(request, product.getString("productId"), quantity.doubleValue())) {
                            errMsg += "<li>Sorry, we do not have enough of the product " + product.getString("productName") + " [product ID: " + product.getString("productId") + "] in stock.";
                        } else {
                            cart.addOrIncreaseItem(product, quantity.doubleValue(), null);
                            totalQuantity += quantity.doubleValue();
                        }
                    }
                }
            }
        }
        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
            return "error";
        }
        
        request.setAttribute(SiteDefs.EVENT_MESSAGE, "Added " + UtilFormatOut.formatQuantity(totalQuantity) + " items to the cart.");
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
        String errMsg = "";
        
        Set names = paramMap.keySet();
        Iterator i = names.iterator();
        while (i.hasNext()) {
            String o = (String) i.next();
            int underscorePos = o.lastIndexOf('_');
            if (underscorePos >= 0) {
                try {
                    String indexStr = o.substring(underscorePos+1);
                    int index = Integer.parseInt(indexStr);
                    String quantString = (String) paramMap.get(o);
                    double quantity = NumberFormat.getNumberInstance().parse(quantString).doubleValue();
                    Debug.logInfo("Got index: " + index + "  AND  quantity: " + quantity);
                    
                    if (o.toUpperCase().startsWith("UPDATE")) {
                        if (quantity == 0.0) {
                            deleteList.add(cart.findCartItem(index));
                            Debug.logInfo("Added index: " + index + " to delete list.");
                        } else {
                            ShoppingCartItem item = cart.findCartItem(index);
                            if (item != null) {
                                GenericValue product = item.getProduct();
                                
                                //check inventory
                                if (quantity > item.getQuantity() && !CatalogWorker.isCatalogInventoryAvailable(request, product.getString("productId"), quantity)) {
                                    errMsg += "<li>Sorry, we do not have enough of the product " + product.getString("productName") + " [product ID: " + product.getString("productId") + "] in stock, not updating quantity.";
                                } else {
                                    Debug.logInfo("Setting quantity.");
                                    item.setQuantity(quantity);
                                }
                            }
                        }
                    }
                    
                    if (o.toUpperCase().startsWith("DELETE")) {
                        deleteList.add(cart.findCartItem(index));
                        Debug.logInfo("Added index: " + index + " to delete list.");
                    }
                } catch (NumberFormatException nfe) {
                    Debug.logWarning(nfe, "Caught number format exception on cart update.");
                } catch (ParseException pe) {
                    Debug.logWarning(pe, "Caught parse exception on cart update.");
                } catch (Exception e) {
                    Debug.logWarning(e, "Caught exception on cart update.");
                }
            }//else not a parameter we need
        }
        
        Iterator di = deleteList.iterator();
        while (di.hasNext()) {
            Object o = di.next();
            Debug.logInfo("Removing item index: " + cart.getItemIndex(o));
            cart.removeCartItem(cart.getItemIndex(o));
        }
        
        if (!paramMap.containsKey("always_showcart")) {
            cart.viewCartOnAdd(false);
        }

        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
            return "error";
        }
        
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
        if (cart == null)
            cart = new ShoppingCart();
        session.setAttribute(SiteDefs.SHOPPING_CART,cart);
        return cart;
    }
}
