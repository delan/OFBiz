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

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.commonapp.product.catalog.CatalogWorker;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilFormatOut;
import org.ofbiz.core.util.UtilHttp;

/**
 * Shopping cart events.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ShoppingCartEvents {
    
    public static String module = ShoppingCartEvents.class.getName();
    
    private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";

    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");        
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;
        String productId = null;
        String itemType = null;
        String itemDescription = null; 
        String productCategoryId = null; 
        String priceStr = null;
        double price = 0.00;
    
        String quantityStr = null;
        double quantity = 0;
        Map attributes = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        
        // Get the parameters as a MAP, remove the productId and quantity params.        
        Map paramMap = UtilHttp.getParameterMap(request);
        
            // Get shoppingList info if passed
        String shoppingListId = request.getParameter("shoppingListId");
        String shoppingListItemSeqId = request.getParameter("shoppingListItemSeqId");
        
        if (paramMap.containsKey("ADD_PRODUCT_ID")) {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } else if (paramMap.containsKey("add_product_id")) {
            productId = (String) paramMap.remove("add_product_id");
        }
        
        if (paramMap.containsKey("ADD_CATEGORY_ID")) {
            productCategoryId = (String) paramMap.remove("ADD_CATEGORY_ID");
        } else if (paramMap.containsKey("add_category_id")) {
            productCategoryId = (String) paramMap.remove("add_category_id");
        }
        if (productCategoryId != null && productCategoryId.length() == 0) {
            productCategoryId = null;
        }
        
        
        if (productId == null) {
            // before returning error; check make sure we aren't adding a special item type
            if (paramMap.containsKey("ADD_ITEM_TYPE")) {
                itemType = (String) paramMap.remove("ADD_ITEM_TYPE");
            } else if (paramMap.containsKey("add_item_type")) {
                itemType = (String) paramMap.remove("add_item_type");
            } else {            
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "No product information passed, not adding anything to cart.");
                return "success"; // not critical return to same page
            }
        }
    
        // check for an itemDescription
        if (paramMap.containsKey("ADD_ITEM_DESCRIPTION")) {
            itemDescription = (String) paramMap.remove("ADD_ITEM_DESCRIPTION");
        } else if (paramMap.containsKey("add_item_description")) {
            itemDescription = (String) paramMap.remove("add_item_description");
        }
        if (itemDescription != null && itemDescription.length() == 0) {
            itemDescription = null;
        }
    
        // get the override price
        if (paramMap.containsKey("PRICE")) {
            priceStr = (String) paramMap.remove("PRICE");
        } else if (paramMap.containsKey("price")) {
            priceStr = (String) paramMap.remove("price");
        }
        if (priceStr == null) {
            priceStr = "0.00";  // default price is 0.00
        }
    
        // get the quantity
        if (paramMap.containsKey("QUANTITY")) {
            quantityStr = (String) paramMap.remove("QUANTITY");
        } else if (paramMap.containsKey("quantity")) {
            quantityStr = (String) paramMap.remove("quantity");
        }
        if (quantityStr == null) {
            quantityStr = "1";  // default quantity is 1
        }
    
        // parse the price
        try {
            price = NumberFormat.getNumberInstance().parse(priceStr).doubleValue();
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
            price = 0.00;   
        }
    
        // parse the quantity
        try {
            quantity = NumberFormat.getNumberInstance().parse(quantityStr).doubleValue();
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
            quantity = 1;
        }
        
        // Translate the parameters and add to the cart
        result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId, productCategoryId, 
            itemType, itemDescription, price, quantity, paramMap);
        controlDirective = processResult(result, request);
        
        //Determine where to send the browser
        if (controlDirective.equals(NON_CRITICAL_ERROR)) {
            return "success";
        } else if (controlDirective.equals(ERROR)) {
            return "error";
        } else if (cart.viewCartOnAdd()) {
            return "viewcart";
        } else {
            return "success";
        }
    }

    public static String addToCartFromOrder(HttpServletRequest request, HttpServletResponse response) {
        String orderId = request.getParameter("order_id");
        String[] itemIds = request.getParameterValues("item_id");

        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Map result;
        String controlDirective;
        
        boolean addAll = ("true".equals(request.getParameter("add_all")));
        result = cartHelper.addToCartFromOrder(catalogId, orderId, itemIds, addAll);
        controlDirective = processResult(result, request);
    
        //Determine where to send the browser 
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds all products in a category according to quantity request parameter
     * for each; if no parameter for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addToCartBulk(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;

        //Convert the params to a map to pass in
        Map paramMap = UtilHttp.getParameterMap(request);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        result = cartHelper.addToCartBulk(catalogId, categoryId, paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds all products in a category according to default quantity on ProductCategoryMember
     * for each; if no default for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addCategoryDefaults(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        ShoppingCart cart = getCartObject(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map result;
        Double totalQuantity;

        result = cartHelper.addCategoryDefaults(catalogId, categoryId);
        controlDirective = processResult(result, request);
        
        //Determine where to send the browser 
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            totalQuantity = (Double)result.get("totalQuantity");
            request.setAttribute(SiteDefs.EVENT_MESSAGE, "Added " + UtilFormatOut.formatQuantity(totalQuantity) + " items to the cart.");
            return "success";
        }
    }

    /** Delete an item from the shopping cart. */
    public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map result;
        Map paramMap = UtilHttp.getParameterMap(request);
        
        //Delegate the cart helper
        result = cartHelper.deleteFromCart(paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Update the items in the shopping cart. */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = getCartObject(request);        
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = (Security) request.getAttribute("security");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map result;
        
        Map paramMap = UtilHttp.getParameterMap(request);

        String removeSelectedFlag = request.getParameter("removeSelected");
        String selectedItems[] = request.getParameterValues("selectedItem");
        boolean removeSelected = ("true".equals(removeSelectedFlag) && selectedItems != null && selectedItems.length > 0);
        result = cartHelper.modifyCart(security, userLogin, paramMap, removeSelected, selectedItems);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
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
        session.removeAttribute("orderMode");
        return "success";
    }

    /** Gets the shopping cart from the session. Used by all events. */
    public static ShoppingCart getCartObject(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);
        
        if (cart == null) {
            cart = new WebShoppingCart(request);
            session.setAttribute(SiteDefs.SHOPPING_CART, cart);
        }
        return cart;
    }
    
    /**
     * This should be called to translate the error messages of the
     * <code>ShoppingCartHelper</code> to an appropriately formatted
     * <code>String</code> in the request object and indicate whether
     * the result was an error or not and whether the errors were
     * critical or not
     * 
     * @param result    The result returned from the 
     * <code>ShoppingCartHelper</code> 
     * @param request The servlet request instance to set the error messages
     * in
     * @return one of NON_CRITICAL_ERROR, ERROR or NO_ERROR.
     */
    private static String processResult(Map result, HttpServletRequest request) {
        //Check for errors
        StringBuffer errMsg = new StringBuffer();
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            List errorMsgs = (List)result.get(ModelService.ERROR_MESSAGE_LIST);
            Iterator iterator = errorMsgs.iterator();
            errMsg.append("<ul>");
            while (iterator.hasNext()) {
                errMsg.append("<li>");
                errMsg.append(iterator.next());
                errMsg.append("</li>");
            }
            errMsg.append("</ul>");
        } else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
            errMsg.append(result.get(ModelService.ERROR_MESSAGE));
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg.toString());
        }
        
        //See whether there was an error
        if (errMsg.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errMsg.toString());
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return NON_CRITICAL_ERROR;
            } else {
                return ERROR;
            }   
        } else {        
            return NO_ERROR;
        }
    }
}
