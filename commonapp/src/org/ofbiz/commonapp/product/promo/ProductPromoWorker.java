/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.commonapp.product.promo;

import java.util.*;
import java.net.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.order.shoppingcart.*;
import org.ofbiz.commonapp.product.catalog.*;

/**
 * ProductPromoWorker - Worker class for catalog/product promotion related functionality
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    June 1, 2002
 */
public class ProductPromoWorker {

    public static List getCatalogProductPromos(GenericDelegator delegator, ServletRequest request) {
        List productPromos = new LinkedList();
        try {
            GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", CatalogWorker.getCurrentCatalogId(request)));
            Iterator prodCatalogPromoAppls = UtilMisc.toIterator(EntityUtil.filterByDate(prodCatalog.getRelatedCache("ProdCatalogPromoAppl", null, UtilMisc.toList("sequenceNum")), true));
            while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()) {
                GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");
                if (productPromo != null) productPromos.add(productPromo);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e);
        }
        return productPromos;
    }
    
    public static void doPromotions(String prodCatalogId, ShoppingCart cart, ShoppingCartItem cartItem, double oldQuantity, GenericDelegator delegator, LocalDispatcher dispatcher) {
        if (cartItem.getQuantity() == oldQuantity) {
            //no change, just return
            Debug.logInfo("Cart quantity did not change, not doing promos.");
            return;
        }
        
        //if quantity increased, then apply, otherwise unapply
        boolean apply = oldQuantity < cartItem.getQuantity();
        if (Debug.verboseOn()) Debug.logVerbose("Doing Promotions; apply=" + apply + ", oldQuantity=" + oldQuantity + ", newQuantity=" + cartItem.getQuantity() + ", productId=" + cartItem.getProductId()); 
        
        GenericValue prodCatalog = null;
        try {
            prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId);
        }
        if (prodCatalog == null) {
            Debug.logWarning("No prodCatalog found with id " + prodCatalogId + ", not doing promotions");
            return;
        }
        
        //there will be a ton of db access, so just do a big catch entity exception block
        try {
            //loop through promotions
            Iterator prodCatalogPromoAppls = UtilMisc.toIterator(EntityUtil.filterByDate(prodCatalog.getRelatedCache("ProdCatalogPromoAppl", null, UtilMisc.toList("sequenceNum")), true));
            if (prodCatalogPromoAppls == null || !prodCatalogPromoAppls.hasNext()) {
                if (Debug.infoOn()) Debug.logInfo("Not doing promotions, none applied to prodCatalog with ID " + prodCatalogId);
            }
            
            while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()){
                GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");
                
                //loop through rules for promotion
                Iterator productPromoRules = UtilMisc.toIterator(productPromo.getRelatedCache("ProductPromoRule", null, UtilMisc.toList("productPromoActionSeqId")));
                while (productPromoRules != null && productPromoRules.hasNext()) {
                    GenericValue productPromoRule = (GenericValue) productPromoRules.next();
                    
                    boolean performActions = true;
                    //loop through conditions for rule, if any false, set allConditionsTrue to false
                    Iterator productPromoConds = UtilMisc.toIterator(productPromoRule.getRelatedCache("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId")));
                    while (productPromoConds != null && productPromoConds.hasNext()) {
                        GenericValue productPromoCond = (GenericValue) productPromoConds.next();
                        
                        boolean condResult = checkCondition(productPromoCond, cart, cartItem, oldQuantity, delegator);
                        //if apply, a false condition will cause it to not perform the action
                        //if unapply, a true condition will cause it to not perofrm the action
                        //so, if apply != condResult (ie true/false or false/true) then don't perform actions
                        if (apply != condResult) {
                            performActions = false;
                            break;
                        }
                    }
                    
                    if (performActions) {
                        //perform all actions, either apply or unapply
                        if (Debug.verboseOn()) Debug.logVerbose("Rule Conditions all true, performing actions for rule " + productPromoRule);
                        
                        Iterator productPromoActions = UtilMisc.toIterator(productPromoRule.getRelatedCache("ProductPromoAction", null, UtilMisc.toList("productPromoActionSeqId")));
                        while (productPromoActions != null && productPromoActions.hasNext()) {
                            GenericValue productPromoAction = (GenericValue) productPromoActions.next();
                            
                            try {
                                performAction(apply, productPromoAction, cart, cartItem, oldQuantity, prodCatalogId, delegator, dispatcher);
                            } catch (CartItemModifyException e) {
                                Debug.logWarning("Possible error modifying the cart in perform promotion action: " + e.toString());
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            Debug.logError(e, "Number not formatted correctly in promotion rules, not completed...");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up promotion data while doing promotions");
        }
    }
    
    public static boolean checkCondition(GenericValue productPromoCond, ShoppingCart cart, ShoppingCartItem cartItem, double oldQuantity, GenericDelegator delegator) throws GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("Checking promotion condition: " + productPromoCond);
        int compare = 0;
        if ("PPIP_PRODUCT_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
            compare = cartItem.getProductId().compareTo(productPromoCond.getString("condValue"));
        } else if ("PPIP_CATEGORY_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
            //if a ProductCategoryMember exists for this productId and the specified productCategoryId
            Collection productCategoryMembers = delegator.findByAndCache("ProductCategoryMember", 
                    UtilMisc.toMap("productId", cartItem.getProductId(), "productCategoryId", productPromoCond.getString("condValue")));
            // and from/thru date within range
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (productCategoryMembers != null && productCategoryMembers.size() > 0) {
                compare = 0;
            } else {
                compare = 1;
            }
        } else if ("PPIP_ORDER_TOTAL".equals(productPromoCond.getString("inputParamEnumId"))) {
            Double orderSubTotal = new Double(cart.getSubTotal());
            if (Debug.verboseOn()) Debug.logVerbose("Doing order total compare: orderSubTotal=" + orderSubTotal);
            compare = orderSubTotal.compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else if ("PPIP_QUANTITY_ADDED".equals(productPromoCond.getString("inputParamEnumId"))) {
            Double quantityAdded = new Double(cartItem.getQuantity() - oldQuantity);
            compare = quantityAdded.compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else if ("PPIP_NEW_PROD_QUANT".equals(productPromoCond.getString("inputParamEnumId"))) {
            compare = (new Double(cartItem.getQuantity())).compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else {
            Debug.logWarning("An un-supported productPromoCond input parameter (lhs) was used: " + productPromoCond.getString("inputParamEnumId") + ", returning false, ie check failed");
            return false;
        }
        
        if (Debug.verboseOn()) Debug.logVerbose("Condition compare done, compare=" + compare);

        if ("PPC_EQ".equals(productPromoCond.getString("operatorEnumId"))) {
            if (compare == 0) return true;
        } else if ("PPC_NEQ".equals(productPromoCond.getString("operatorEnumId"))) {
            if (compare != 0) return true;
        } else if ("PPC_LT".equals(productPromoCond.getString("operatorEnumId"))) {
            if (compare < 0) return true;
        } else if ("PPC_LTE".equals(productPromoCond.getString("operatorEnumId"))) {
            if (compare <= 0) return true;
        } else if ("PPC_GT".equals(productPromoCond.getString("operatorEnumId"))) {
            if (compare > 0) return true;
        } else if ("PPC_GTE".equals(productPromoCond.getString("operatorEnumId"))) {
            if (compare >= 0) return true;
        } else {
            Debug.logWarning("An un-supported productPromoCond condition was used: " + productPromoCond.getString("operatorEnumId") + ", returning false, ie check failed");
            return false;
        }
        return false;
    }
    
    public static void performAction(boolean apply, GenericValue productPromoAction, ShoppingCart cart, ShoppingCartItem cartItem, double oldQuantity, String prodCatalogId, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException, CartItemModifyException {
        if ("PROMO_GWP".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            if (apply) {
                Integer itemLoc = findPromoItem(productPromoAction, cart);
                if (itemLoc != null) {
                    if (Debug.infoOn()) Debug.logInfo("Not adding promo item, already there; action: " + productPromoAction);
                    return;
                }

                GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productPromoAction.get("productId")));
                double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();
                
                //pass null for cartLocation to add to end of cart, pass false for doPromotions to avoid infinite recursion
                ShoppingCartItem gwpItem = ShoppingCartItem.makeItem(null, product, quantity, null, null, prodCatalogId, dispatcher, cart, false);

                double discountAmount = quantity * gwpItem.getBasePrice();
                GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                        UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", "amount", new Double(-discountAmount),
                        "productPromoId", productPromoAction.get("productPromoId"), 
                        "productPromoRuleId", productPromoAction.get("productPromoRuleId"), 
                        "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

                //if an orderAdjustmentTypeId was included, override the default
                if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
                    orderAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
                }
                
                //set promo after create; note that to setQuantity we must clear this flag, setQuantity, then re-set the flag
                gwpItem.setIsPromo(true);
                gwpItem.addAdjustment(orderAdjustment);
                
                if (Debug.infoOn()) Debug.logInfo("qwpItem adjustments: " + gwpItem.getAdjustments());
            } else {
                //how to remove this? find item that isPromo with the product id and has adjustment with the same promo/rule id, then remove it

                Integer itemLoc = findPromoItem(productPromoAction, cart);
                if (itemLoc != null) {
                    //gwp was setup by this promo/rule, so go ahead and clear it

                    //before clearing it, set it to a non-promo so that the setQuantity won't throw an exception
                    ShoppingCartItem cartItemToRemove = cart.findCartItem(itemLoc.intValue());
                    cartItemToRemove.setIsPromo(false);
                    cart.removeCartItem(itemLoc.intValue(), dispatcher);
                }
            }
        } else if ("PROMO_FREE_SHIPPING".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            //this may look a bit funny: on each pass all rules that do free shipping will set their own rule id for it,
            //  and on unapply if the promo and rule ids are the same then it will clear it; essentially on any pass
            //  through the promos and rules if any free shipping should be there, it will be there
            if (apply) {
                cart.setFreeShippingInfo(UtilMisc.toMap("productPromoId", productPromoAction.getString("productPromoId"), 
                        "productPromoRuleId", productPromoAction.getString("productPromoRuleId"),
                        "productPromoActionSeqId", productPromoAction.getString("productPromoActionSeqId"),
                        "orderAdjustmentTypeId", productPromoAction.getString("orderAdjustmentTypeId")));
            } else {
                Map freeShippingInfo = cart.getFreeShippingInfo();
                if (freeShippingInfo != null && freeShippingInfo.get("productPromoId") != null && freeShippingInfo.get("productPromoRuleId") != null ) {
                    if (productPromoAction.getString("productPromoId").equals(freeShippingInfo.get("productPromoId")) &&
                            productPromoAction.getString("productPromoRuleId").equals(freeShippingInfo.get("productPromoRuleId")) &&
                            productPromoAction.getString("productPromoActionSeqId").equals(freeShippingInfo.get("productPromoActionSeqId"))) {
                        //free shipping was setup by this promo/rule, so go ahead and clear it
                        cart.setFreeShippingInfo(null);
                    }
                }
            }
        } else if ("PROMO_ITEM_PERCENT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            doItemPromoAction(apply, productPromoAction, cartItem, "percentage", delegator);
        } else if ("PROMO_ITEM_AMOUNT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            doItemPromoAction(apply, productPromoAction, cartItem, "amount", delegator);
        } else if ("PROMO_ITEM_AMNTPQ".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            doItemPromoAction(apply, productPromoAction, cartItem, "amountPerQuantity", delegator);
        } else if ("PROMO_ORDER_PERCENT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            doOrderPromoAction(apply, productPromoAction, cart, "percentage", delegator);
        } else if ("PROMO_ORDER_AMOUNT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            doOrderPromoAction(apply, productPromoAction, cart, "amount", delegator);
        } else {
            Debug.logError("An un-supported productPromoActionType was used: " + productPromoAction.getString("productPromoActionTypeId") + ", not performing any action");
        }
    }

    public static Integer findPromoItem(GenericValue productPromoAction, ShoppingCart cart) {
        List cartItems = cart.items();
        for (int i = 0; i < cartItems.size(); i++) {
            ShoppingCartItem checkItem = (ShoppingCartItem) cartItems.get(i);

            if (checkItem.getIsPromo() && checkItem.getProductId().equals(productPromoAction.get("productId"))) {
                //found a promo item with the productId, see if it has a matching adjustment on it
                Iterator checkOrderAdjustments = UtilMisc.toIterator(checkItem.getAdjustments());
                while (checkOrderAdjustments != null && checkOrderAdjustments.hasNext()) {
                    GenericValue checkOrderAdjustment = (GenericValue) checkOrderAdjustments.next();

                    if (productPromoAction.getString("productPromoId").equals(checkOrderAdjustment.get("productPromoId")) &&
                            productPromoAction.getString("productPromoRuleId").equals(checkOrderAdjustment.get("productPromoRuleId")) &&
                            productPromoAction.getString("productPromoActionSeqId").equals(checkOrderAdjustment.get("productPromoActionSeqId"))) {
                        return new Integer(i);
                    }
                }
            }
        }
        return null;
    }

    public static void doItemPromoAction(boolean apply, GenericValue productPromoAction, ShoppingCartItem cartItem, String quantityField, GenericDelegator delegator) {
        if (apply) {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cartItem.getAdjustments());
            if (adjLoc != null) {
                if (Debug.infoOn()) Debug.logInfo("Not adding promo adjustment, already there; action: " + productPromoAction);
                return;
            }
            
            double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();
            GenericValue itemAdjustment = delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", quantityField, new Double(quantity),
                    "productPromoId", productPromoAction.get("productPromoId"), 
                    "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                    "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));
            //if an orderAdjustmentTypeId was included, override the default
            if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
                itemAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
            }

            cartItem.addAdjustment(itemAdjustment);
        } else {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cartItem.getAdjustments());
            if (adjLoc != null) {
                cartItem.removeAdjustment(adjLoc.intValue());
            }
        }
    }
    
    public static void doOrderPromoAction(boolean apply, GenericValue productPromoAction, ShoppingCart cart, String quantityField, GenericDelegator delegator) {
        if (apply) {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cart.getAdjustments());
            if (adjLoc != null) {
                if (Debug.infoOn()) Debug.logInfo("Not adding promo adjustment, already there; action: " + productPromoAction);
                return;
            }
            
            double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();
            GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", quantityField, new Double(quantity),
                    "productPromoId", productPromoAction.get("productPromoId"), 
                    "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                    "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));
            //if an orderAdjustmentTypeId was included, override the default
            if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
                orderAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
            }

            cart.addAdjustment(orderAdjustment);
        } else {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cart.getAdjustments());
            if (adjLoc != null) {
                cart.removeAdjustment(adjLoc.intValue());
            }
        }
    }
    
    public static Integer findAdjustment(GenericValue productPromoAction, List adjustments) {
        for (int i = 0; i < adjustments.size(); i++) {
            GenericValue checkOrderAdjustment = (GenericValue) adjustments.get(i);

            if (productPromoAction.getString("productPromoId").equals(checkOrderAdjustment.get("productPromoId")) &&
                    productPromoAction.getString("productPromoRuleId").equals(checkOrderAdjustment.get("productPromoRuleId")) &&
                    productPromoAction.getString("productPromoActionSeqId").equals(checkOrderAdjustment.get("productPromoActionSeqId"))) {
                return new Integer(i);
            }
        }
        return null;
    }
}
