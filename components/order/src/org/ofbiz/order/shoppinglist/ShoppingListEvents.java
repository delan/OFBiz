/*
 * $Id: ShoppingListEvents.java,v 1.2 2004/02/22 00:32:40 jonesde Exp $
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.shoppinglist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Shopping cart events.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      2.2
 */
public class ShoppingListEvents {
    
    public static final String module = ShoppingListEvents.class.getName();
    public static final String resource = "OrderUiLabels";

    public static String addBulkFromCart(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");        
        
        String shoppingListId = request.getParameter("shoppingListId");
        String selectedCartItems[] = request.getParameterValues("selectedItem");
        String errMsg = null;

        if (selectedCartItems == null || selectedCartItems.length == 0) {
            errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.select_items_to_add_to_list", cart.getLocale());
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg );
            return "error";
        }
                
        if (shoppingListId == null || shoppingListId.equals("")) {
            // create a new shopping list
            Map newListResult = null;
            try {
                newListResult = dispatcher.runSync("createShoppingList", UtilMisc.toMap("userLogin", userLogin));    
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems creating new ShoppingList", module);
                errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.cannot_create_new_shopping_list", cart.getLocale());
                request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
                return "error";
            }
            
            // get the new list id
            if (newListResult != null) {
                shoppingListId = (String) newListResult.get("shoppingListId");
            }
            
            // if no list was created throw an error
            if (shoppingListId == null || shoppingListId.equals("")) {            
                errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.shoppingListId_is_required_parameter", cart.getLocale());
                request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
                return "error";
            }
        }
                
        for (int i = 0; i < selectedCartItems.length; i++) {
            Integer cartIdInt = null;
            try {            
                cartIdInt = new Integer(selectedCartItems[i]);
            } catch (Exception e) {
                Debug.logWarning(e, "Illegal character in selectedItem field", module);
            }
            if (cartIdInt != null) {            
                ShoppingCartItem item = cart.findCartItem(cartIdInt.intValue());
                Map serviceResult = null;
                try {
                    Map ctx = UtilMisc.toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", item.getProductId(), "quantity", new Double(item.getQuantity()));
                    serviceResult = dispatcher.runSync("createShoppingListItem", ctx);                    
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problems creating ShoppingList item entity", module);
                    errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.error_adding_item_to_shopping_list", cart.getLocale());
                    request.setAttribute("_ERROR_MESSAGE_", errMsg);
                    return "error";
                }
            }           
        }
        
        // return the shoppinglist id 
        request.setAttribute("shoppingListId", shoppingListId);                
        return "success";
    }
    
    public static String addListToCart(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);        
        
        String shoppingListId = request.getParameter("shoppingListId");
        String includeChild = request.getParameter("includeChild");
        String prodCatalogId =  CatalogWorker.getCurrentCatalogId(request);

        String errMsg = null;

        // no list; no add
        if (shoppingListId == null) {
            errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.choose_shopping_list", cart.getLocale());
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
            return "error";
        }
        
        // get the shopping list 
        GenericValue shoppingList = null;
        List shoppingListItems = null;
        try {
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListId));
            shoppingListItems = shoppingList.getRelated("ShoppingListItem");
            
            // include all items of child lists if flagged to do so
            if (includeChild != null) {            
                List childShoppingLists = shoppingList.getRelated("ChildShoppingList");
                Iterator ci = childShoppingLists.iterator();
                while (ci.hasNext()) {
                    GenericValue v = (GenericValue) ci.next();
                    List items = v.getRelated("ShoppingListItem");
                    shoppingListItems.addAll(items);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting ShoppingList and ShoppingListItem records", module);
            errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.error_getting_shopping_list_and_items", cart.getLocale());
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg);
            return "error";
        }
        
        // no items; not an error; just mention that nothing was added
        if (shoppingListItems == null || shoppingListItems.size() == 0) {
            errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.no_items_added", cart.getLocale());
            request.setAttribute("_EVENT_MESSAGE_", "<li>" + errMsg);
            return "success";
        }
        
        // add the items
        StringBuffer eventMessage = new StringBuffer();                       
        Iterator i = shoppingListItems.iterator();
        while (i.hasNext()) {
            GenericValue shoppingListItem = (GenericValue) i.next();
            String productId = shoppingListItem.getString("productId");
            Double quantity = shoppingListItem.getDouble("quantity");
            try {
                Map attributes = UtilMisc.toMap("shoppingListId", shoppingListItem.getString("shoppingListId"), "shoppingListItemSeqId", shoppingListItem.getString("shoppingListItemSeqId"));
                int itemId = cart.addOrIncreaseItem(productId, quantity.doubleValue(), null, attributes, prodCatalogId, dispatcher);
                Map messageMap = UtilMisc.toMap("productId", productId);
                errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.added_product_to_cart", messageMap, cart.getLocale());
                eventMessage.append(errMsg + "\n");
            } catch (CartItemModifyException e) {
                Debug.logWarning(e, "Problems adding item from list to cart", module);
                Map messageMap = UtilMisc.toMap("productId", productId);
                errMsg = UtilProperties.getMessage(resource,"shoppinglistevents.problem_adding_product_to_cart", messageMap, cart.getLocale());
                eventMessage.append(errMsg + "\n");
            }            
        }
        
        if (eventMessage.length() > 0) {
            request.setAttribute("_EVENT_MESSAGE_", eventMessage.toString());
        }
        
        // all done
        return "success";
    }

    public static String replaceShoppingListItem(HttpServletRequest request, HttpServletResponse response) {
        String quantityStr = request.getParameter("quantity");

        // just call the updateShoppingListItem service
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        Double quantity = null;
        try {
            quantity = Double.valueOf(quantityStr);
        } catch (Exception e) {
            // do nothing, just won't pass to service if it is null
        }
        
        Map serviceInMap = new HashMap();
        serviceInMap.put("shoppingListId", request.getParameter("shoppingListId"));
        serviceInMap.put("shoppingListItemSeqId", request.getParameter("shoppingListItemSeqId"));
        serviceInMap.put("productId", request.getParameter("add_product_id"));
        serviceInMap.put("userLogin", userLogin);
        if (quantity != null) serviceInMap.put("quantity", quantity);
        Map result = null;
        try {
            result = dispatcher.runSync("updateShoppingListItem", serviceInMap);
        } catch (GenericServiceException e) {
            String errMsg = "Error calling the updateShoppingListItem in handleShoppingListItemVariant: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        
        ServiceUtil.getMessages(request, result, "", "", "", "", "", "", "");
        if ("error".equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            return "error";
        } else {
            return "success";
        }
    }
}
