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
 *@author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 *@version    1.0
 *@created    June 1, 2002
 */
public class ProductPromoWorker {

    public static final String module = ProductPromoWorker.class.getName();

    public static List getCatalogProductPromos(GenericDelegator delegator, ServletRequest request) {
        List productPromos = new LinkedList();

        try {
            String prodCatalogId = CatalogWorker.getCurrentCatalogId(request);

            if (prodCatalogId != null) {
                GenericValue prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));

                if (prodCatalog != null) {
                    Iterator prodCatalogPromoAppls = UtilMisc.toIterator(EntityUtil.filterByDate(prodCatalog.getRelatedCache("ProdCatalogPromoAppl", null, UtilMisc.toList("sequenceNum")), true));

                    while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()) {
                        GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                        GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");
                        Collection productPromoRules = productPromo.getRelatedCache("ProductPromoRule", null, null);

                        // get the ShoppingCart out of the session.
                        HttpServletRequest req = null;
                        ShoppingCart cart = null;

                        try {
                            req = (HttpServletRequest) request;
                            cart = ShoppingCartEvents.getCartObject(req);
                        } catch (ClassCastException cce) {
                            Debug.logInfo("Not a HttpServletRequest, no shopping cart found.", module);
                        }

                        boolean condResult = true;

                        if (productPromoRules != null) {
                            Iterator promoRulesItr = productPromoRules.iterator();

                            while (condResult && promoRulesItr != null && promoRulesItr.hasNext()) {
                                GenericValue promoRule = (GenericValue) promoRulesItr.next();
                                Iterator productPromoConds = UtilMisc.toIterator(promoRule.getRelatedCache("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId")));

                                while (condResult && productPromoConds != null && productPromoConds.hasNext()) {
                                    GenericValue productPromoCond = (GenericValue) productPromoConds.next();

                                    // evaluate the party related conditions; so we don't show the promo if it doesn't apply.
                                    if ("PPIP_PARTY_ID".equals(productPromoCond.getString("inputParamEnumId")))
                                        condResult = checkCondition(prodCatalogId, productPromoCond, cart, null, 0, delegator);
                                    else if ("PRIP_PARTY_GRP_MEM".equals(productPromoCond.getString("inputParamEnumId")))
                                        condResult = checkCondition(prodCatalogId, productPromoCond, cart, null, 0, delegator);
                                    else if ("PRIP_PARTY_CLASS".equals(productPromoCond.getString("inputParamEnumId")))
                                        condResult = checkCondition(prodCatalogId, productPromoCond, cart, null, 0, delegator);
                                    else if ("PPIP_ROLE_TYPE".equals(productPromoCond.getString("inputParamEnumId")))
                                        condResult = checkCondition(prodCatalogId, productPromoCond, cart, null, 0, delegator);
                                }
                            }
                            if (!condResult) productPromo = null;
                        }
                        if (productPromo != null) productPromos.add(productPromo);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return productPromos;
    }

    public static void doPromotions(String prodCatalogId, ShoppingCart cart, ShoppingCartItem cartItem, double oldQuantity, GenericDelegator delegator, LocalDispatcher dispatcher) {
        // this is our safety net; we should never need to loop through the rules more than a certain number of times, this is that number and may have to be changed for insanely large promo sets...
        int maxIterations = 1000;

        if (cartItem.getQuantity() == oldQuantity) {
            // no change, just return
            Debug.logInfo("Cart quantity did not change, not doing promos.", module);
            return;
        }

        // if quantity increased, then apply, otherwise unapply
        boolean apply = oldQuantity < cartItem.getQuantity();

        if (Debug.verboseOn()) Debug.logVerbose("Doing Promotions; apply=" + apply + ", oldQuantity=" + oldQuantity + ", newQuantity=" + cartItem.getQuantity() + ", productId=" + cartItem.getProductId(), module);

        GenericValue prodCatalog = null;

        try {
            prodCatalog = delegator.findByPrimaryKeyCache("ProdCatalog", UtilMisc.toMap("prodCatalogId", prodCatalogId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up prodCatalog with id " + prodCatalogId, module);
        }
        if (prodCatalog == null) {
            Debug.logWarning("No prodCatalog found with id " + prodCatalogId + ", not doing promotions", module);
            return;
        }

        // there will be a ton of db access, so just do a big catch entity exception block
        try {
            // loop through promotions and get a list of all of the rules...
            Collection prodCatalogPromoApplsCol = EntityUtil.filterByDate(prodCatalog.getRelatedCache("ProdCatalogPromoAppl", null, UtilMisc.toList("sequenceNum")), true);

            if (prodCatalogPromoApplsCol == null || prodCatalogPromoApplsCol.size() == 0) {
                if (Debug.infoOn()) Debug.logInfo("Not doing promotions, none applied to prodCatalog with ID " + prodCatalogId, module);
            }

            List allPromoRules = new LinkedList();
            Iterator prodCatalogPromoAppls = UtilMisc.toIterator(prodCatalogPromoApplsCol);

            while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()) {
                GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");

                Collection productPromoRules = productPromo.getRelatedCache("ProductPromoRule", null, null);

                if (productPromoRules != null) {
                    allPromoRules.addAll(productPromoRules);
                }
            }

            // This isn't necessary, just removing run rules from list: Set firedRules = new HashSet();

            // part of the safety net to avoid infinite iteration
            int numberOfIterations = 0;
            // repeat until no more rules to run: either all rules are run, or no changes to the cart in a loop
            boolean cartChanged = true;

            while (cartChanged) {
                cartChanged = false;

                numberOfIterations++;
                if (numberOfIterations > maxIterations) {
                    Debug.logError("ERROR: While calculating promotions the promotion rules where run more than " + maxIterations + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions (or LOTS of promos/rules).", module);
                    break;
                }

                Iterator allPromoRulesIter = allPromoRules.iterator();

                while (allPromoRulesIter != null && allPromoRulesIter.hasNext()) {
                    GenericValue productPromoRule = (GenericValue) allPromoRulesIter.next();

                    /* This isn't necessary, just removing run rules from list: 
                     GenericPK productPromoRulePK = productPromoRule.getPrimaryKey();
                     //check to see if this rule has already been fired, if so don't fire it again
                     if (firedRules.contains(productPromoRulePK)) {
                     //go onto the next rule in this promo...
                     continue;
                     }*/

                    // by default we start with whatever apply is because:
                    // if apply then performActions when no conditions are false, so default to true
                    // if !apply then performActions when no contitions are true, so default to false
                    boolean performActions = apply;
                    // loop through conditions for rule, if any false, set allConditionsTrue to false
                    Iterator productPromoConds = UtilMisc.toIterator(productPromoRule.getRelatedCache("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId")));

                    while (productPromoConds != null && productPromoConds.hasNext()) {
                        GenericValue productPromoCond = (GenericValue) productPromoConds.next();

                        boolean condResult = checkCondition(prodCatalogId, productPromoCond, cart, cartItem, oldQuantity, delegator);

                        // if apply, any false condition will cause it to NOT perform the action
                        // if !apply, any false condition will cause it to PERFORM the action
                        // so, if a condition is found to be false then performActions = !apply
                        if (condResult == false) {
                            performActions = !apply;
                            break;
                        }
                    }

                    if (performActions) {
                        // perform all actions, either apply or unapply
                        if (Debug.verboseOn()) Debug.logVerbose((apply ? "Performing" : "Un-performing") + " actions for rule " + productPromoRule + "; apply=" + apply, module);

                        /* This isn't necessary, just removing run rules from list: 
                         //rule done, add to list so it won't get done again
                         firedRules.add(productPromoRulePK);*/

                        // rule done, remove from list so it won't get done again
                        allPromoRulesIter.remove();

                        Iterator productPromoActions = UtilMisc.toIterator(productPromoRule.getRelatedCache("ProductPromoAction", null, UtilMisc.toList("productPromoActionSeqId")));

                        while (productPromoActions != null && productPromoActions.hasNext()) {
                            GenericValue productPromoAction = (GenericValue) productPromoActions.next();

                            // Debug.logInfo("Doing action: " + productPromoAction, module);

                            try {
                                boolean actionChangedCart = performAction(apply, productPromoAction, cart, cartItem, oldQuantity, prodCatalogId, delegator, dispatcher);

                                // if cartChanged is already true then don't set it again: implements OR logic (ie if ANY actions change content, redo loop)
                                if (!cartChanged) {
                                    cartChanged = actionChangedCart;
                                }
                            } catch (CartItemModifyException e) {
                                Debug.logWarning("Possible error modifying the cart in perform promotion action: " + e.toString(), module);
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            Debug.logError(e, "Number not formatted correctly in promotion rules, not completed...", module);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up promotion data while doing promotions", module);
        }
    }

    public static boolean checkCondition(String prodCatalogId, GenericValue productPromoCond, ShoppingCart cart, ShoppingCartItem cartItem, double oldQuantity, GenericDelegator delegator) throws GenericEntityException {
        GenericValue userLogin = null;
        String partyId = null;

        if (cart != null) userLogin = cart.getUserLogin();
        if (cart != null && userLogin == null) userLogin = cart.getAutoUserLogin();
        if (userLogin != null && userLogin.get("partyId") != null)
            partyId = userLogin.getString("partyId");

        if (Debug.verboseOn()) Debug.logVerbose("Checking promotion condition: " + productPromoCond, module);
        int compare = 0;

        if ("PPIP_PRODUCT_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
            compare = cartItem.getProductId().compareTo(productPromoCond.getString("condValue"));
        } else if ("PPIP_PRODUCT_ID_IC".equals(productPromoCond.getString("inputParamEnumId"))) {
            String candidateProductId = productPromoCond.getString("condValue");

            if (candidateProductId == null) {
                // if null, then it's not in the cart
                compare = 1;
            } else {
                if (candidateProductId.equals(cartItem.getProductId())) {
                    compare = 0;
                } else {
                    // Debug.logInfo("Testing to see if productId \"" + candidateProductId + "\" is in the cart", module);
                    List productCartItems = cart.findAllCartItems(candidateProductId);

                    if (productCartItems.size() > 0) {
                        // Debug.logInfo("Item with productId \"" + candidateProductId + "\" IS in the cart", module);
                        compare = 0;
                    } else {
                        // Debug.logInfo("Item with productId \"" + candidateProductId + "\" IS NOT in the cart", module);
                        compare = 1;
                    }
                }
            }
        } else if ("PPIP_CATEGORY_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
            // if a ProductCategoryMember exists for this productId and the specified productCategoryId
            List productCategoryMembers = delegator.findByAndCache("ProductCategoryMember",
                    UtilMisc.toMap("productId", cartItem.getProductId(), "productCategoryId", productPromoCond.getString("condValue")));

            // and from/thru date within range
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (productCategoryMembers != null && productCategoryMembers.size() > 0) {
                compare = 0;
            } else {
                compare = 1;
            }
        } else if ("PPIP_PARTY_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
            if (partyId != null) {
                compare = partyId.compareTo(productPromoCond.getString("condValue"));
            } else {
                compare = 1;
            }

            /* These aren't supported yet, ie TODO
             } else if ("PRIP_PARTY_GRP_MEM".equals(productPriceCond.getString("inputParamEnumId"))) {
             } else if ("PRIP_PARTY_CLASS".equals(productPriceCond.getString("inputParamEnumId"))) {
             */
        } else if ("PPIP_ROLE_TYPE".equals(productPromoCond.getString("inputParamEnumId"))) {
            if (partyId != null) {
                // if a PartyRole exists for this partyId and the specified roleTypeId
                GenericValue partyRole = delegator.findByPrimaryKeyCache("PartyRole",
                        UtilMisc.toMap("partyId", partyId, "roleTypeId", productPromoCond.getString("condValue")));

                // then 0 (equals), otherwise 1 (not equals)
                if (partyRole != null) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            } else {
                compare = 1;
            }
        } else if ("PPIP_ORDER_TOTAL".equals(productPromoCond.getString("inputParamEnumId"))) {
            Double orderSubTotal = new Double(cart.getSubTotal());

            if (Debug.verboseOn()) Debug.logVerbose("Doing order total compare: orderSubTotal=" + orderSubTotal, module);
            compare = orderSubTotal.compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else if ("PPIP_QUANTITY_ADDED".equals(productPromoCond.getString("inputParamEnumId"))) {
            Double quantityAdded = new Double(cartItem.getQuantity() - oldQuantity);

            compare = quantityAdded.compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else if ("PPIP_NEW_PROD_QUANT".equals(productPromoCond.getString("inputParamEnumId"))) {
            compare = (new Double(cartItem.getQuantity())).compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else {
            Debug.logWarning("An un-supported productPromoCond input parameter (lhs) was used: " + productPromoCond.getString("inputParamEnumId") + ", returning false, ie check failed", module);
            return false;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Condition compare done, compare=" + compare, module);

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
            Debug.logWarning("An un-supported productPromoCond condition was used: " + productPromoCond.getString("operatorEnumId") + ", returning false, ie check failed", module);
            return false;
        }
        return false;
    }

    /** returns true if the cart was changed and rules need to be re-evaluted */
    public static boolean performAction(boolean apply, GenericValue productPromoAction, ShoppingCart cart, ShoppingCartItem cartItem, double oldQuantity, String prodCatalogId, GenericDelegator delegator, LocalDispatcher dispatcher) throws GenericEntityException, CartItemModifyException {
        if ("PROMO_GWP".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            if (apply) {
                Integer itemLoc = findPromoItem(productPromoAction, cart);

                if (itemLoc != null) {
                    if (Debug.verboseOn()) Debug.logVerbose("Not adding promo item, already there; action: " + productPromoAction, module);
                    return false;
                }

                GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productPromoAction.get("productId")));
                double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();

                // pass null for cartLocation to add to end of cart, pass false for doPromotions to avoid infinite recursion
                ShoppingCartItem gwpItem = ShoppingCartItem.makeItem(null, product, quantity, null, null, prodCatalogId, dispatcher, cart, false);

                double discountAmount = quantity * gwpItem.getBasePrice();
                GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                        UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", "amount", new Double(-discountAmount),
                            "productPromoId", productPromoAction.get("productPromoId"),
                            "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                            "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

                // if an orderAdjustmentTypeId was included, override the default
                if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
                    orderAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
                }

                // set promo after create; note that to setQuantity we must clear this flag, setQuantity, then re-set the flag
                gwpItem.setIsPromo(true);
                gwpItem.addAdjustment(orderAdjustment);
                if (Debug.verboseOn()) Debug.logVerbose("gwpItem adjustments: " + gwpItem.getAdjustments(), module);

                // ProductPromoWorker.doPromotions(prodCatalogId, cart, gwpItem, 0, delegator, dispatcher);
                return true;
            } else {
                // how to remove this? find item that isPromo with the product id and has adjustment with the same promo/rule id, then remove it

                Integer itemLoc = findPromoItem(productPromoAction, cart);

                if (itemLoc != null) {
                    // gwp was setup by this promo/rule, so go ahead and clear it

                    // before clearing it, set it to a non-promo so that the setQuantity won't throw an exception
                    ShoppingCartItem cartItemToRemove = cart.findCartItem(itemLoc.intValue());

                    cartItemToRemove.setIsPromo(false);
                    if (Debug.verboseOn()) Debug.logVerbose("About to remove cart item at location " + itemLoc.intValue(), module);
                    cart.removeCartItem(itemLoc.intValue(), dispatcher);
                    // ProductPromoWorker.doPromotions(prodCatalogId, cart, cartItem, oldQuantity, delegator, dispatcher);
                    return true;
                } else {
                    if (Debug.verboseOn()) Debug.logVerbose("Could not find cart item for the action " + productPromoAction, module);
                    return false;
                }
            }
        } else if ("PROMO_FREE_SHIPPING".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            // this may look a bit funny: on each pass all rules that do free shipping will set their own rule id for it,
            // and on unapply if the promo and rule ids are the same then it will clear it; essentially on any pass
            // through the promos and rules if any free shipping should be there, it will be there
            if (apply) {
                cart.addFreeShippingProductPromoAction(productPromoAction);
                // don't consider this as a cart change...
                return false;
            } else {
                cart.removeFreeShippingProductPromoAction(productPromoAction.getPrimaryKey());
                return false;
            }
        } else if ("PROMO_ITEM_PERCENT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            return doItemPromoAction(apply, productPromoAction, cartItem, "percentage", delegator);
        } else if ("PROMO_ITEM_AMOUNT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            return doItemPromoAction(apply, productPromoAction, cartItem, "amount", delegator);
        } else if ("PROMO_ITEM_AMNTPQ".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            return doItemPromoAction(apply, productPromoAction, cartItem, "amountPerQuantity", delegator);
        } else if ("PROMO_ORDER_PERCENT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            return doOrderPromoAction(apply, productPromoAction, cart, "percentage", delegator);
        } else if ("PROMO_ORDER_AMOUNT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
            return doOrderPromoAction(apply, productPromoAction, cart, "amount", delegator);
        } else {
            Debug.logError("An un-supported productPromoActionType was used: " + productPromoAction.getString("productPromoActionTypeId") + ", not performing any action", module);
            return false;
        }
    }

    public static Integer findPromoItem(GenericValue productPromoAction, ShoppingCart cart) {
        List cartItems = cart.items();

        for (int i = 0; i < cartItems.size(); i++) {
            ShoppingCartItem checkItem = (ShoppingCartItem) cartItems.get(i);

            if (checkItem.getIsPromo() && checkItem.getProductId().equals(productPromoAction.get("productId"))) {
                // found a promo item with the productId, see if it has a matching adjustment on it
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

    public static boolean doItemPromoAction(boolean apply, GenericValue productPromoAction, ShoppingCartItem cartItem, String quantityField, GenericDelegator delegator) {
        if (apply) {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cartItem.getAdjustments());

            if (adjLoc != null) {
                if (Debug.verboseOn()) Debug.logVerbose("Not adding promo adjustment, already there; action: " + productPromoAction, module);
                return false;
            }

            double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();
            GenericValue itemAdjustment = delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", quantityField, new Double(quantity),
                        "productPromoId", productPromoAction.get("productPromoId"),
                        "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                        "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

            // if an orderAdjustmentTypeId was included, override the default
            if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
                itemAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
            }

            cartItem.addAdjustment(itemAdjustment);
            return true;
        } else {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cartItem.getAdjustments());

            if (adjLoc != null) {
                // Debug.logInfo("Found adjustment on cartItem for productId " + cartItem.getProductId() + "removing.", module);
                cartItem.removeAdjustment(adjLoc.intValue());
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean doOrderPromoAction(boolean apply, GenericValue productPromoAction, ShoppingCart cart, String quantityField, GenericDelegator delegator) {
        // Debug.logInfo("Starting doOrderPromoAction: apply=" + apply + ", productPromoAction=" + productPromoAction, module);
        if (apply) {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cart.getAdjustments());

            if (adjLoc != null) {
                if (Debug.infoOn()) Debug.logInfo("Not adding promo adjustment, already there; action: " + productPromoAction, module);
                return false;
            }

            double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();
            GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", quantityField, new Double(quantity),
                        "productPromoId", productPromoAction.get("productPromoId"),
                        "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                        "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

            // if an orderAdjustmentTypeId was included, override the default
            if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
                orderAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
            }

            cart.addAdjustment(orderAdjustment);
            return true;
        } else {
            Integer adjLoc = findAdjustment(productPromoAction, (List) cart.getAdjustments());

            // Debug.logInfo("Finding adjustment, adjLoc=" + adjLoc, module);
            if (adjLoc != null) {
                cart.removeAdjustment(adjLoc.intValue());
                return true;
            } else {
                return false;
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
