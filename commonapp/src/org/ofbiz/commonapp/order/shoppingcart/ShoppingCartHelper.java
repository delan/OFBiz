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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilMisc;

/**
 * A facade over the 
 * {@link org.ofbiz.commonapp.order.shoppingcart.ShoppingCart ShoppingCart}
 * providing catalog and product services to simplify the interaction
 * with the cart directly. 
 *
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ShoppingCartHelper {

    public static String module = ShoppingCartHelper.class.getName();

    /**
     * The shopping cart to manipulate
     */
    private ShoppingCart cart;

    /**
     * The entity engine delegator
     */
    private GenericDelegator delegator;

    /**
     * The service invoker
     */
    private LocalDispatcher dispatcher;

    /**
     * This will create a default, non-interface specific, 
     * <code>ShoppingCart</code> to manipulate. 
     */
    public ShoppingCartHelper(GenericDelegator delegator, LocalDispatcher dispatcher, String productStoreId, String webSiteId) {
        this(delegator, dispatcher, new ShoppingCart(delegator, productStoreId, webSiteId));
    }

    /**
     * Changes will be made to the cart directly, as opposed
     * to a copy of the cart provided.
     * 
     * @param cart The cart to manipulate
     */
    public ShoppingCartHelper(GenericDelegator delegator, LocalDispatcher dispatcher, ShoppingCart cart) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;
        this.cart = cart;
    }

    /** Event to add an item to the shopping cart. */
    public Map addToCart(
        String catalogId,
        String shoppingListId,
        String shoppingListItemSeqId,
        String productId,
        String productCategoryId,
        String itemType,
        String itemDescription,
        double price,
        double quantity,
        Map context) {
        Map result;
        Map attributes = null;

        // price sanity check
        if (productId == null && price < 0) {
            result = ServiceUtil.returnError("Price must be a positive number.");
            return result;
        }

        // quantity sanity check
        if (quantity < 0) {
            result = ServiceUtil.returnError("Quantity must be a positive number.");
            return result;
        }

        // Create a HashMap of product attributes - From ShoppingCartItem.attributeNames[]
        for (int namesIdx = 0; namesIdx < ShoppingCartItem.attributeNames.length; namesIdx++) {
            if (attributes == null)
                attributes = new HashMap();
            if (context.containsKey(ShoppingCartItem.attributeNames[namesIdx])) {
                attributes.put(ShoppingCartItem.attributeNames[namesIdx], context.get(ShoppingCartItem.attributeNames[namesIdx]));
            }
        }

        // Retrieve the catalog ID
        try {
            int itemId;
            if (productId != null) {
                itemId = cart.addOrIncreaseItem(productId, quantity, null, attributes, catalogId, dispatcher);
            } else {
                itemId = cart.addNonProductItem(delegator, itemType, itemDescription, productCategoryId, price, quantity, attributes, catalogId, dispatcher);
            }

            if (shoppingListId != null && shoppingListItemSeqId != null) {
                ShoppingCartItem item = cart.findCartItem(itemId);
                item.setShoppingList(shoppingListId, shoppingListItemSeqId);
            }
        } catch (CartItemModifyException cartException) {
            result = ServiceUtil.returnError(cartException.getMessage());
            return result;
        }

        //Indicate there were no critical errors
        result = ServiceUtil.returnSuccess();
        return result;
    }

    public Map addToCartFromOrder(String catalogId, String orderId, String[] itemIds, boolean addAll) {
        ArrayList errorMsgs = new ArrayList();
        Map result;

        if (orderId == null || orderId.length() <= 0) {
            result = ServiceUtil.returnError("No order specified to add from.");
            return result;
        }

        boolean noItems = true;

        if (addAll) {
            Iterator itemIter = null;

            try {
                itemIter = UtilMisc.toIterator(delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null));
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                itemIter = null;
            }

            if (itemIter != null && itemIter.hasNext()) {
                while (itemIter.hasNext()) {
                    GenericValue orderItem = (GenericValue) itemIter.next();

                    if (orderItem.get("productId") != null && orderItem.get("quantity") != null) {
                        try {
                            this.cart.addOrIncreaseItem(
                                orderItem.getString("productId"),
                                orderItem.getDouble("quantity").doubleValue(),
                                null,
                                null,
                                catalogId,
                                dispatcher);
                            noItems = false;
                        } catch (CartItemModifyException e) {
                            errorMsgs.add(e.getMessage());
                        }
                    }
                }
                if (errorMsgs.size() > 0) {
                    result = ServiceUtil.returnError(errorMsgs);
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                    return result; // don't return error because this is a non-critical error and should go back to the same page
                }
            } else {
                noItems = true;
            }
        } else {
            noItems = true;
            if (itemIds != null) {

                for (int i = 0; i < itemIds.length; i++) {
                    String orderItemSeqId = itemIds[i];
                    GenericValue orderItem = null;

                    try {
                        orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e.getMessage(), module);
                        errorMsgs.add("Order line \"" + orderItemSeqId + "\" not found, so not added.");
                        continue;
                    }
                    if (orderItem != null) {
                        if (orderItem.get("productId") != null && orderItem.get("quantity") != null) {
                            try {
                                this.cart.addOrIncreaseItem(
                                    orderItem.getString("productId"),
                                    orderItem.getDouble("quantity").doubleValue(),
                                    null,
                                    null,
                                    catalogId,
                                    dispatcher);
                                noItems = false;
                            } catch (CartItemModifyException e) {
                                errorMsgs.add(e.getMessage());
                            }
                        }
                    }
                }
                if (errorMsgs.size() > 0) {
                    result = ServiceUtil.returnError(errorMsgs);
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                    return result; // don't return error because this is a non-critical error and should go back to the same page
                }
            } // else no items
        }

        if (noItems) {
            result = ServiceUtil.returnSuccess();
            result.put(SiteDefs.ERROR_MESSAGE, "No items found to add.");
            return result; // don't return error because this is a non-critical error and should go back to the same page
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }

    /** 
     * Adds all products in a category according to quantity request parameter
     * for each; if no parameter for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public Map addToCartBulk(String catalogId, String categoryId, Map context) {
        Map result;

        if (categoryId == null || categoryId.length() <= 0) {
            result = ServiceUtil.returnError("No category specified to add from.");
            return result;
        }

        Collection prodCatMemberCol = null;

        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", categoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            result = ServiceUtil.returnError("Could not get products in category " + categoryId + " to add to cart (read error): " + e.getMessage());
            return result;
        }

        if (prodCatMemberCol == null) {
            result = ServiceUtil.returnError("Could not get products in category " + categoryId + " to add to cart (read error).");
            return result;
        }

        String errMsg = "";
        Iterator pcmIter = prodCatMemberCol.iterator();

        while (pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) pcmIter.next();
            String quantStr = (String) context.get("quantity_" + productCategoryMember.getString("productId"));

            if (quantStr != null && quantStr.length() > 0) {
                double quantity = 0;

                try {
                    quantity = Double.parseDouble(quantStr);
                } catch (NumberFormatException nfe) {
                    quantity = 0;
                }
                if (quantity > 0.0) {
                    try {
                        this.cart.addOrIncreaseItem(productCategoryMember.getString("productId"), quantity, null, null, catalogId, dispatcher);
                    } catch (CartItemModifyException cartException) {
                        result = ServiceUtil.returnError(cartException.getMessage());
                        return result;
                    }
                }
            }
        }

        //Indicate there were no non critical errors
        result = ServiceUtil.returnSuccess();
        return result;
    }

    /**
     * Adds all products in a category according to default quantity on ProductCategoryMember
     * for each; if no default for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public Map addCategoryDefaults(String catalogId, String categoryId) {
        ArrayList errorMsgs = new ArrayList();
        Map result;

        if (categoryId == null || categoryId.length() <= 0) {
            result = ServiceUtil.returnError("No category specified to add from.");
            return result;
        }

        Collection prodCatMemberCol = null;

        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", categoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.toString(), module);
            result = ServiceUtil.returnError("Could not get products in category " + categoryId + " to add to cart (read error): " + e.getMessage());
            return result;
        }

        if (prodCatMemberCol == null) {
            result = ServiceUtil.returnError("Could not get products in category " + categoryId + " to add to cart (read error).");
            return result;
        }

        double totalQuantity = 0;
        Iterator pcmIter = prodCatMemberCol.iterator();

        while (pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) pcmIter.next();
            Double quantity = productCategoryMember.getDouble("quantity");

            if (quantity != null && quantity.doubleValue() > 0.0) {
                try {
                    this.cart.addOrIncreaseItem(productCategoryMember.getString("productId"), quantity.doubleValue(), null, null, catalogId, dispatcher);
                    totalQuantity += quantity.doubleValue();
                } catch (CartItemModifyException e) {
                    errorMsgs.add(e.getMessage());
                }
            }
        }
        if (errorMsgs.size() > 0) {
            result = ServiceUtil.returnError(errorMsgs);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result; // don't return error because this is a non-critical error and should go back to the same page
        }

        result = ServiceUtil.returnSuccess();
        result.put("totalQuantity", new Double(totalQuantity));
        return result;
    }

    /** Delete an item from the shopping cart. */
    public Map deleteFromCart(Map context) {
        Map result;
        Set names = context.keySet();
        Iterator i = names.iterator();
        ArrayList errorMsgs = new ArrayList();

        while (i.hasNext()) {
            String o = (String) i.next();

            if (o.toUpperCase().startsWith("DELETE")) {
                try {
                    String indexStr = o.substring(o.lastIndexOf('_') + 1);
                    int index = Integer.parseInt(indexStr);

                    try {
                        this.cart.removeCartItem(index, dispatcher);
                    } catch (CartItemModifyException e) {
                        errorMsgs.add(e.getMessage());
                    }
                } catch (NumberFormatException nfe) {}
            }
        }

        if (errorMsgs.size() > 0) {
            result = ServiceUtil.returnError(errorMsgs);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result; // don't return error because this is a non-critical error and should go back to the same page
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }

    /** Update the items in the shopping cart. */
    public Map modifyCart(Security security, GenericValue userLogin, Map context, boolean removeSelected, String[] selectedItems) {
        Map result;

        ArrayList deleteList = new ArrayList();
        ArrayList errorMsgs = new ArrayList();

        Set names = context.keySet();
        Iterator i = names.iterator();

        while (i.hasNext()) {
            String o = (String) i.next();
            int underscorePos = o.lastIndexOf('_');

            if (underscorePos >= 0) {
                try {
                    String indexStr = o.substring(underscorePos + 1);
                    int index = Integer.parseInt(indexStr);
                    String quantString = (String) context.get(o);
                    double quantity = NumberFormat.getNumberInstance().parse(quantString).doubleValue();

                    if (quantity < 0) {
                        throw new CartItemModifyException("Quantity must be a positive number.");
                    }

                    if (o.toUpperCase().startsWith("UPDATE")) {
                        if (quantity == 0.0) {
                            deleteList.add(this.cart.findCartItem(index));
                        } else {
                            ShoppingCartItem item = this.cart.findCartItem(index);

                            if (item != null) {
                                try {
                                    item.setQuantity(quantity, dispatcher, this.cart);
                                } catch (CartItemModifyException e) {
                                    errorMsgs.add(e.getMessage());
                                }
                            }
                        }
                    }

                    if (o.toUpperCase().startsWith("PRICE")) {
                        if (security.hasEntityPermission("ORDERMGR", "_CREATE", userLogin)) {
                            ShoppingCartItem item = this.cart.findCartItem(index);
                            item.setBasePrice(quantity); // this is quanity because the parsed number variable is the same as quantity
                        }
                    }

                    if (o.toUpperCase().startsWith("DELETE")) {
                        deleteList.add(this.cart.findCartItem(index));
                    }
                } catch (NumberFormatException nfe) {
                    Debug.logWarning(nfe, "Caught number format exception on cart update.", module);
                } catch (ParseException pe) {
                    Debug.logWarning(pe, "Caught parse exception on cart update.", module);
                } catch (Exception e) {
                    Debug.logWarning(e, "Caught exception on cart update.", module);
                }
            } // else not a parameter we need
        }

        // get a list of the items to delete
        if (removeSelected) {
            for (int si = 0; si < selectedItems.length; si++) {
                String indexStr = selectedItems[si];
                ShoppingCartItem item = null;
                try {
                    int index = Integer.parseInt(indexStr);
                    item = this.cart.findCartItem(index);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems getting the cart item by index", module);
                }
                if (item != null) {
                    deleteList.add(item);
                }
            }
        }

        Iterator di = deleteList.iterator();

        while (di.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) di.next();
            int itemIndex = this.cart.getItemIndex(item);

            if (Debug.infoOn())
                Debug.logInfo("Removing item index: " + itemIndex, module);
            try {
                this.cart.removeCartItem(itemIndex, dispatcher);
            } catch (CartItemModifyException e) {
                ServiceUtil.returnError(new Vector());
                errorMsgs.add(e.getMessage());
            }
        }

        if (context.containsKey("alwaysShowcart")) {
            this.cart.setViewCartOnAdd(true);
        } else {
            this.cart.setViewCartOnAdd(false);
        }

        if (errorMsgs.size() > 0) {
            result = ServiceUtil.returnError(errorMsgs);
            return result;
        }

        result = ServiceUtil.returnSuccess();
        return result;
    }

    /** Empty the shopping cart. */
    public boolean clearCart() {
        this.cart.clear();
        return true;
    }

    /** Returns the shopping cart this helper is wrapping. */
    public ShoppingCart getCartObject() {
        return this.cart;
    }
}
