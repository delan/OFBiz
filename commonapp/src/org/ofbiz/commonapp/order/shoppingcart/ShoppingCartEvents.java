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
package org.ofbiz.commonapp.order.shoppingcart;

import java.util.*;
import java.text.*;

import javax.servlet.http.*;

import org.ofbiz.core.service.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.catalog.*;

/**
 * Shopping cart events.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ShoppingCartEvents {

    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);

        String productId = null;
        String quantityStr = null;
        double quantity = 0;
        HashMap attributes = null;

        // Get the parameters as a MAP, remove the productId and quantity params.
        // The rest should be product attributes.This only works w/ servlet api 2.3
        // Map paramMap = request.getParameterMap();
        Map paramMap = UtilMisc.getParameterMap(request);

        if (paramMap.containsKey("ADD_PRODUCT_ID")) {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } else if (paramMap.containsKey("add_product_id")) {
            productId = (String) paramMap.remove("add_product_id");
        }
        if (productId == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No add_product_id passed, not adding anything to cart.");
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
        
        if (quantity < 0) {
        	request.setAttribute(SiteDefs.ERROR_MESSAGE, "Quantity must be a positive number.");
        	return "error";
        }

        // Create a HashMap of product attributes.
        /*
         * commenting this out because this pulls in parameters that we don't want; we need some way to specify which parameters to put into attributes...
         if (paramMap.size() > 0) {
         attributes = new HashMap(paramMap);
         }
         */

        try {
            cart.addOrIncreaseItem(productId, quantity, null, attributes, CatalogWorker.getCurrentCatalogId(request), dispatcher);
        } catch (CartItemModifyException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, e.getMessage());
            return "success"; // don't return error because this is a non-critical error and should go back to the same page
        }

        if (cart.viewCartOnAdd()) {
            return "viewcart";
        } else {
            return "success";
        }
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
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

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

                while (itemIter.hasNext()) {
                    GenericValue orderItem = (GenericValue) itemIter.next();

                    if (orderItem.get("productId") != null && orderItem.get("quantity") != null) {
                        try {
                            cart.addOrIncreaseItem(orderItem.getString("productId"), orderItem.getDouble("quantity").doubleValue(), null, null, CatalogWorker.getCurrentCatalogId(request), dispatcher);
                            noItems = false;
                        } catch (CartItemModifyException e) {
                            errMsg += "<li>" + e.getMessage();
                        }
                    }
                }
                if (errMsg.length() > 0) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
                    return "success"; // don't return error because this is a non-critical error and should go back to the same page
                }
            } else {
                noItems = true;
            }
        } else {
            noItems = true;
            if (itemIds != null) {
                String errMsg = "";

                for (int i = 0; i < itemIds.length; i++) {
                    String orderItemSeqId = itemIds[i];
                    GenericValue orderItem = null;

                    try {
                        orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e.getMessage());
                        errMsg += "<li>Order line \"" + orderItemSeqId + "\" not found, so not added.";
                        continue;
                    }
                    if (orderItem != null) {
                        if (orderItem.get("productId") != null && orderItem.get("quantity") != null) {
                            try {
                                cart.addOrIncreaseItem(orderItem.getString("productId"), orderItem.getDouble("quantity").doubleValue(), null, null, CatalogWorker.getCurrentCatalogId(request), dispatcher);
                                noItems = false;
                            } catch (CartItemModifyException e) {
                                errMsg += "<li>" + e.getMessage();
                            }
                        }
                    }
                }
                if (errMsg.length() > 0) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
                    return "success"; // don't return error because this is a non-critical error and should go back to the same page
                }
            }// else no items
        }

        if (noItems) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "No items found to add.");
            return "success"; // don't return error because this is a non-critical error and should go back to the same page
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
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        Collection prodCatMemberCol = null;

        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", categoryId));
        } catch (GenericEntityException e) {
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

        while (pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) pcmIter.next();
            String quantStr = request.getParameter("quantity_" + productCategoryMember.getString("productId"));

            if (quantStr != null && quantStr.length() > 0) {
                double quantity = 0;

                try {
                    quantity = Double.parseDouble(quantStr);
                } catch (NumberFormatException nfe) {
                    quantity = 0;
                }
                if (quantity > 0.0) {
                    try {
                        cart.addOrIncreaseItem(productCategoryMember.getString("productId"), quantity, null, null, CatalogWorker.getCurrentCatalogId(request), dispatcher);
                    } catch (CartItemModifyException e) {
                        errMsg += "<li>" + e.getMessage();
                    }
                }
            }
        }
        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
            return "success"; // don't return error because this is a non-critical error and should go back to the same page
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
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        Collection prodCatMemberCol = null;

        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", categoryId));
        } catch (GenericEntityException e) {
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

        while (pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) pcmIter.next();
            Double quantity = productCategoryMember.getDouble("quantity");

            if (quantity != null && quantity.doubleValue() > 0.0) {
                try {
                    cart.addOrIncreaseItem(productCategoryMember.getString("productId"), quantity.doubleValue(), null, null, CatalogWorker.getCurrentCatalogId(request), dispatcher);
                    totalQuantity += quantity.doubleValue();
                } catch (CartItemModifyException e) {
                    errMsg += "<li>" + e.getMessage();
                }
            }
        }
        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
            return "success"; // don't return error because this is a non-critical error and should go back to the same page
        }

        request.setAttribute(SiteDefs.EVENT_MESSAGE, "Added " + UtilFormatOut.formatQuantity(totalQuantity) + " items to the cart.");
        return "success";
    }

    /** Delete an item from the shopping cart. */
    public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map paramMap = UtilMisc.getParameterMap(request);
        Set names = paramMap.keySet();
        Iterator i = names.iterator();

        String errMsg = "";

        while (i.hasNext()) {
            String o = (String) i.next();

            if (o.toUpperCase().startsWith("DELETE")) {
                try {
                    String indexStr = o.substring(o.lastIndexOf('_') + 1);
                    int index = Integer.parseInt(indexStr);

                    try {
                        cart.removeCartItem(index, dispatcher);
                    } catch (CartItemModifyException e) {
                        errMsg += "<li>" + e.getMessage();
                    }
                } catch (NumberFormatException nfe) {}
            }
        }

        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<ul>" + errMsg + "</ul>");
            return "success"; // don't return error because this is a non-critical error and should go back to the same page
        }

        return "success";
    }

    /** Update the items in the shopping cart. */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
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
                    String indexStr = o.substring(underscorePos + 1);
                    int index = Integer.parseInt(indexStr);
                    String quantString = (String) paramMap.get(o);
                    double quantity = NumberFormat.getNumberInstance().parse(quantString).doubleValue();
                    
                    if (quantity < 0) {
        				request.setAttribute(SiteDefs.ERROR_MESSAGE, "Quantity must be a positive number.");
        				return "error";
        			}

                    if (Debug.infoOn()) Debug.logInfo("Got index: " + index + "  AND  quantity: " + quantity);

                    if (o.toUpperCase().startsWith("UPDATE")) {
                        if (quantity == 0.0) {
                            deleteList.add(cart.findCartItem(index));
                            if (Debug.infoOn()) Debug.logInfo("Added index: " + index + " to delete list.");
                        } else {
                            ShoppingCartItem item = cart.findCartItem(index);

                            if (item != null) {
                                try {
                                    Debug.logInfo("Setting quantity.");
                                    item.setQuantity(quantity, dispatcher, cart);
                                } catch (CartItemModifyException e) {
                                    errMsg += "<li>" + e.getMessage();
                                }
                            }
                        }
                    }

                    if (o.toUpperCase().startsWith("DELETE")) {
                        deleteList.add(cart.findCartItem(index));
                        if (Debug.infoOn()) Debug.logInfo("Added index: " + index + " to delete list.");
                    }
                } catch (NumberFormatException nfe) {
                    Debug.logWarning(nfe, "Caught number format exception on cart update.");
                } catch (ParseException pe) {
                    Debug.logWarning(pe, "Caught parse exception on cart update.");
                } catch (Exception e) {
                    Debug.logWarning(e, "Caught exception on cart update.");
                }
            }// else not a parameter we need
        }

        Iterator di = deleteList.iterator();

        while (di.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) di.next();
            int itemIndex = cart.getItemIndex(item);

            if (Debug.infoOn()) Debug.logInfo("Removing item index: " + itemIndex);
            try {
                cart.removeCartItem(itemIndex, dispatcher);
            } catch (CartItemModifyException e) {
                errMsg += "<li>" + e.getMessage();
            }
        }

        if (!paramMap.containsKey("always_showcart")) {
            cart.setViewCartOnAdd(false);
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
    
    /** Totally wipe out the cart, removes all stored info. */
    public static String destroyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        clearCart(request, response);
        session.removeAttribute(SiteDefs.SHOPPING_CART);
        session.removeAttribute("orderPartyId");
        return "success";
    }

    /** Gets the shopping cart from the session. Used by all events. */
    public static ShoppingCart getCartObject(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        return getCartObject(session, delegator);
    }
    
    /** Gets the shopping cart from the session. Used by all events. Will create a ShoppingCart object if none exists and if a delegator is passed. */
    public static ShoppingCart getCartObject(HttpSession session, GenericDelegator delegator) {
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);
        if (cart == null && delegator != null) {
            cart = new ShoppingCart(delegator, session);
            session.setAttribute(SiteDefs.SHOPPING_CART, cart);
        }
        return cart;
    }
}
