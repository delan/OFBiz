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
package org.ofbiz.order.shoppingcart;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * A facade over the 
 * {@link org.ofbiz.order.shoppingcart.ShoppingCart ShoppingCart}
 * providing catalog and product services to simplify the interaction
 * with the cart directly. 
 *
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ShoppingCartHelper {

    public static final String resource = "OrderUiLabels";
    public static String module = ShoppingCartHelper.class.getName();

    // The shopping cart to manipulate
    private ShoppingCart cart = null;

    // The entity engine delegator
    private GenericDelegator delegator = null;

    // The service invoker
    private LocalDispatcher dispatcher = null;

    /**
     * Changes will be made to the cart directly, as opposed
     * to a copy of the cart provided.
     * 
     * @param cart The cart to manipulate
     */
    public ShoppingCartHelper(GenericDelegator delegator, LocalDispatcher dispatcher, ShoppingCart cart) {
        this.dispatcher = dispatcher;
        this.delegator = delegator;
        this.cart = cart;

        if (delegator == null) {
            this.delegator = dispatcher.getDelegator();
        }
        if (dispatcher == null) {
            throw new IllegalArgumentException("Dispatcher argument is null");
        }
        if (cart == null) {
            throw new IllegalArgumentException("ShoppingCart argument is null");
        }
    }

    /** Event to add an item to the shopping cart. */
    public Map addToCart(String catalogId, String shoppingListId, String shoppingListItemSeqId, String productId,
            String productCategoryId, String itemType, String itemDescription, double price,
            double amount, double quantity, Map context) {
        Map result;
        Map attributes = null;
        String errMsg = null;
	
        // price sanity check
        if (productId == null && price < 0) {
            errMsg = UtilProperties.getMessage(resource, "cart.price_not_positive_number", this.cart.getLocale());
            result = ServiceUtil.returnError(errMsg);
            return result;
        }

        // quantity sanity check
        if (quantity < 1) {
            errMsg = UtilProperties.getMessage(resource, "cart.quantity_not_positive_number", this.cart.getLocale());
            result = ServiceUtil.returnError(errMsg);
            return result;
        }

        // amount sanity check
        if (amount < 0) {
            amount = 0;
        }

        // check desiredDeliveryDate syntax and remove if empty
        String ddDate = (String) context.get("itemDesiredDeliveryDate");
        if (!UtilValidate.isEmpty(ddDate)) {
            try {
                java.sql.Timestamp.valueOf((String) context.get("itemDesiredDeliveryDate"));
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError("Invalid Desired Delivery Date: Syntax Error");
            }
        } else {
            context.remove("itemDesiredDeliveryDate");
        }

        // remove an empty comment
        String comment = (String) context.get("itemComment");
        if (UtilValidate.isEmpty(comment)) {
            context.remove("itemComment");
        }

        // stores the default desired delivery date in the cart if need
        if (!UtilValidate.isEmpty((String) context.get("useAsDefaultDesiredDeliveryDate"))) {
            cart.setDefaultItemDeliveryDate((String) context.get("itemDesiredDeliveryDate"));
        } else {
            // do we really want to clear this if it isn't checked?
            cart.setDefaultItemDeliveryDate(null);
        }

        // stores the default comment in session if need
        if (!UtilValidate.isEmpty((String) context.get("useAsDefaultComment"))) {
            cart.setDefaultItemComment((String) context.get("itemComment"));
        } else {
            // do we really want to clear this if it isn't checked?
            cart.setDefaultItemComment(null);
        }

        // Create a HashMap of product attributes - From ShoppingCartItem.attributeNames[]
        for (int namesIdx = 0; namesIdx < ShoppingCartItem.attributeNames.length; namesIdx++) {
            if (attributes == null)
                attributes = new HashMap();
            if (context.containsKey(ShoppingCartItem.attributeNames[namesIdx])) {
                attributes.put(ShoppingCartItem.attributeNames[namesIdx], context.get(ShoppingCartItem.attributeNames[namesIdx]));
            }
        }

        // check for required amount flag; if amount and no flag set to 0
        GenericValue product = null;
        if (productId != null) {
            try {
                product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Unable to lookup product : " + productId, module);
            }
            if (product == null || product.get("requireAmount") == null || "N".equals(product.getString("requireAmount"))) {
                amount = 0;
            }
        }

        // Retrieve the catalog ID
        try {
            int itemId = -1;
            if (productId != null) {
                itemId = cart.addOrIncreaseItem(productId, amount, quantity, null, attributes, catalogId, dispatcher);
            } else {
                itemId = cart.addNonProductItem(itemType, itemDescription, productCategoryId, price, quantity, attributes, catalogId, dispatcher);
            }

            // set the shopping list info
            if (itemId > -1 && shoppingListId != null && shoppingListItemSeqId != null) {
                ShoppingCartItem item = cart.findCartItem(itemId);
                item.setShoppingList(shoppingListId, shoppingListItemSeqId);
            }
        } catch (CartItemModifyException e) {
            result = ServiceUtil.returnError(e.getMessage());
            return result;
        } catch (ItemNotFoundException e) {
            result = ServiceUtil.returnError(e.getMessage());
            return result;
        }

        // Indicate there were no critical errors
        result = ServiceUtil.returnSuccess();
        return result;
    }

    public Map addToCartFromOrder(String catalogId, String orderId, String[] itemIds, boolean addAll) {
        ArrayList errorMsgs = new ArrayList();
        Map result;
        String errMsg = null;

        if (orderId == null || orderId.length() <= 0) {
            errMsg = UtilProperties.getMessage(resource,"cart.order_not_specified_to_add_from", this.cart.getLocale());
            result = ServiceUtil.returnError(errMsg);
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
                    // never read: int itemId = -1;
                    if (orderItem.get("productId") != null && orderItem.get("quantity") != null) {
                        double amount = 0.00;
                        if (orderItem.get("selectedAmount") != null) {
                            amount = orderItem.getDouble("selectedAmount").doubleValue();
                        }
                        try {
                            this.cart.addOrIncreaseItem(orderItem.getString("productId"),
                                    amount, orderItem.getDouble("quantity").doubleValue(), null, null, catalogId, dispatcher);
                            noItems = false;
                        } catch (CartItemModifyException e) {
                            errorMsgs.add(e.getMessage());
                        } catch (ItemNotFoundException e) {
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
                            double amount = 0.00;
                            if (orderItem.get("selectedAmount") != null) {
                                amount = orderItem.getDouble("selectedAmount").doubleValue();
                            }
                            try {
                                this.cart.addOrIncreaseItem(orderItem.getString("productId"), amount,
                                        orderItem.getDouble("quantity").doubleValue(), null, null, catalogId, dispatcher);
                                noItems = false;
                            } catch (CartItemModifyException e) {
                                errorMsgs.add(e.getMessage());
                            } catch (ItemNotFoundException e) {
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
            result.put("_ERROR_MESSAGE_", "No items found to add.");
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
        String keyPrefix = "quantity_";
        
        // iterate through the context and find all keys that start with "quantity_"
        Iterator entryIter = context.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIter.next();
            String productId = null;
            if (entry.getKey() instanceof String) {
                String key = (String) entry.getKey();
                //Debug.logInfo("Bulk Key: " + key, module);
                if (key.startsWith(keyPrefix)) {
                    productId = key.substring(keyPrefix.length());
                } else {
                    continue;
                }
            } else {
                continue;
            }
            String quantStr = (String) entry.getValue();

            if (quantStr != null && quantStr.length() > 0) {
                double quantity = 0;

                try {
                    quantity = Double.parseDouble(quantStr);
                } catch (NumberFormatException nfe) {
                    quantity = 0;
                }
                if (quantity > 0.0) {
                    try {
                        if (Debug.verboseOn()) Debug.logVerbose("Bulk Adding to cart [" + quantity + "] of [" + productId + "]", module);
                        this.cart.addOrIncreaseItem(productId, 0.00, quantity, null, null, catalogId, dispatcher);
                    } catch (CartItemModifyException e) {
                        return ServiceUtil.returnError(e.getMessage());
                    } catch (ItemNotFoundException e) {
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }
            }
        }

        //Indicate there were no non critical errors
        return ServiceUtil.returnSuccess();
    }

    /**
     * Adds all products in a category according to default quantity on ProductCategoryMember
     * for each; if no default for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public Map addCategoryDefaults(String catalogId, String categoryId) {
        ArrayList errorMsgs = new ArrayList();
        Map result = null;
        String errMsg = null;

        if (categoryId == null || categoryId.length() <= 0) {
            errMsg = UtilProperties.getMessage(resource,"cart.category_not_specified_to_add_from", this.cart.getLocale());
            result = ServiceUtil.returnError(errMsg);
//          result = ServiceUtil.returnError("No category specified to add from.");
            return result;
        }

        Collection prodCatMemberCol = null;

        try {
            prodCatMemberCol = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", categoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.toString(), module);
            Map messageMap = UtilMisc.toMap("categoryId", categoryId);
            messageMap.put("message", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"cart.could_not_get_products_in_category_cart", messageMap, this.cart.getLocale());
            result = ServiceUtil.returnError(errMsg);
            return result;
        }

        if (prodCatMemberCol == null) {
            Map messageMap = UtilMisc.toMap("categoryId", categoryId);
            errMsg = UtilProperties.getMessage(resource,"cart.could_not_get_products_in_category", messageMap, this.cart.getLocale());
            result = ServiceUtil.returnError(errMsg);
            return result;
        }

        double totalQuantity = 0;
        Iterator pcmIter = prodCatMemberCol.iterator();

        while (pcmIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) pcmIter.next();
            Double quantity = productCategoryMember.getDouble("quantity");

            if (quantity != null && quantity.doubleValue() > 0.0) {
                try {
                    this.cart.addOrIncreaseItem(productCategoryMember.getString("productId"), 0.00, quantity.doubleValue(), null, null, catalogId, dispatcher);
                    totalQuantity += quantity.doubleValue();
                } catch (CartItemModifyException e) {
                    errorMsgs.add(e.getMessage());
                } catch (ItemNotFoundException e) {
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
        Map result = null;
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
        Map result = null;

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
                    double quantity = -1;

                    // get the cart item
                    ShoppingCartItem item = this.cart.findCartItem(index);

                    if (o.toUpperCase().startsWith("OPTION")) {
                        if (quantString.toUpperCase().startsWith("NO^")) {
                            if (quantString.length() > 2) { // the length of the prefix
                                String featureTypeId = this.getRemoveFeatureTypeId(o);
                                if (featureTypeId != null) {
                                    item.removeAdditionalProductFeatureAndAppl(featureTypeId);
                                }
                            }
                        } else {
                            GenericValue featureAppl = this.getFeatureAppl(item.getProductId(), o, quantString);
                            if (featureAppl != null) {
                                item.putAdditionalProductFeatureAndAppl(featureAppl);
                            }
                        }
                    } else {
                        quantity = NumberFormat.getNumberInstance().parse(quantString).doubleValue();
                        if (quantity < 0) {
                            throw new CartItemModifyException("Quantity must be a positive number.");
                        }
                    }

                    if (o.toUpperCase().startsWith("UPDATE")) {
                        if (quantity == 0.0) {
                            deleteList.add(item);
                        } else {
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
                            if (item != null) {
                                item.setBasePrice(quantity); // this is quanity because the parsed number variable is the same as quantity
                            }
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
                result = ServiceUtil.returnError(new Vector());
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

    public GenericValue getFeatureAppl(String productId, String optionField, String featureId) {
        if (delegator == null) {
            throw new IllegalArgumentException("No delegator available to lookup ProductFeature");
        }

        Map fields = UtilMisc.toMap("productId", productId, "productFeatureId", featureId);
        if (optionField != null) {
            int featureTypeStartIndex = optionField.indexOf('^') + 1;
            int featureTypeEndIndex = optionField.lastIndexOf('_');
            if (featureTypeStartIndex > 0 && featureTypeEndIndex > 0) {
                fields.put("productFeatureTypeId", optionField.substring(featureTypeStartIndex, featureTypeEndIndex));
            }
        }

        GenericValue productFeatureAppl = null;
        List features = null;
        try {
            features = delegator.findByAnd("ProductFeatureAndAppl", fields, UtilMisc.toList("-fromDate"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return null;
        }

        if (features != null) {
            if (features.size() > 1) {
                features = EntityUtil.filterByDate(features);
            }
            productFeatureAppl = EntityUtil.getFirst(features);
        }

        return productFeatureAppl;
    }

    public String getRemoveFeatureTypeId(String optionField) {
        if (optionField != null) {
            int featureTypeStartIndex = optionField.indexOf('^') + 1;
            int featureTypeEndIndex = optionField.lastIndexOf('_');
            if (featureTypeStartIndex > 0 && featureTypeEndIndex > 0) {
                return optionField.substring(featureTypeStartIndex, featureTypeEndIndex);
            }
        }
        return null;
    }
}
