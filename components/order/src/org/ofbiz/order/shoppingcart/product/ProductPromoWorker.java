/*
 * $Id: ProductPromoWorker.java,v 1.21 2003/11/25 12:41:26 jonesde Exp $
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
package org.ofbiz.order.shoppingcart.product;

import java.util.*;
import java.sql.Timestamp;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.product.ProductSearch;
import org.ofbiz.service.LocalDispatcher;

/**
 * ProductPromoWorker - Worker class for catalog/product promotion related functionality
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.21 $
 * @since      2.0
 */
public class ProductPromoWorker {

    public static final String module = ProductPromoWorker.class.getName();

    public static List getStoreProductPromos(GenericDelegator delegator, ServletRequest request) {
        List productPromos = new LinkedList();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        try {
            GenericValue productStore = ProductStoreWorker.getProductStore(request);

            if (productStore != null) {
                String productStoreId = productStore.getString("productStoreId");
                Iterator productStorePromoAppls = UtilMisc.toIterator(EntityUtil.filterByDate(productStore.getRelatedCache("ProductStorePromoAppl", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum")), true));

                while (productStorePromoAppls != null && productStorePromoAppls.hasNext()) {
                    GenericValue productStorePromoAppl = (GenericValue) productStorePromoAppls.next();
                    GenericValue productPromo = productStorePromoAppl.getRelatedOneCache("ProductPromo");
                    List productPromoRules = productPromo.getRelatedCache("ProductPromoRule", null, null);

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
                                if ("PPIP_PARTY_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
                                    condResult = checkCondition(productPromoCond, cart, delegator, nowTimestamp);
                                } else if ("PRIP_PARTY_GRP_MEM".equals(productPromoCond.getString("inputParamEnumId"))) {
                                    condResult = checkCondition(productPromoCond, cart, delegator, nowTimestamp);
                                } else if ("PRIP_PARTY_CLASS".equals(productPromoCond.getString("inputParamEnumId"))) {
                                    condResult = checkCondition(productPromoCond, cart, delegator, nowTimestamp);
                                } else if ("PPIP_ROLE_TYPE".equals(productPromoCond.getString("inputParamEnumId"))) {
                                    condResult = checkCondition(productPromoCond, cart, delegator, nowTimestamp);
                                }
                            }
                        }
                        if (!condResult) productPromo = null;
                    }
                    if (productPromo != null) productPromos.add(productPromo);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return productPromos;
    }

    public static void doPromotions(ShoppingCart cart, GenericDelegator delegator, LocalDispatcher dispatcher) {
        // TODO: make sure this is called when a user logs in so that per customer limits are honored
        // TODO: add code to store ProductPromoUse information when an order is placed
        // TODO: add code to remove ProductPromoUse if an order is cancelled
        // TODO: add code to check ProductPromoUse limits per promo (customer, promo), and per code (customer, code) to avoid use of promos or codes getting through due to multiple carts getting promos applied at the same time, possibly on totally different servers
        String partyId = cart.getPartyId();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // start out by clearing all existing promotions, then we can just add all that apply
        clearAllPromotions(cart);

        // this is our safety net; we should never need to loop through the rules more than a certain number of times, this is that number and may have to be changed for insanely large promo sets...
        int maxIterations = 1000;

        String productStoreId = cart.getProductStoreId();
        GenericValue productStore = null;

        try {
            productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up store with id " + productStoreId, module);
        }
        if (productStore == null) {
            Debug.logWarning("No store found with id " + productStoreId + ", not doing promotions", module);
            return;
        }

        // there will be a ton of db access, so just do a big catch entity exception block
        try {
            // loop through promotions and get a list of all of the rules...
            Collection productStorePromoApplsCol = productStore.getRelatedCache("ProductStorePromoAppl", null, UtilMisc.toList("sequenceNum"));
            productStorePromoApplsCol = EntityUtil.filterByDate((List) productStorePromoApplsCol, nowTimestamp);

            if (productStorePromoApplsCol == null || productStorePromoApplsCol.size() == 0) {
                if (Debug.infoOn()) Debug.logInfo("Not doing promotions, none applied to store with ID " + productStoreId, module);
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

                Iterator prodCatalogPromoAppls = UtilMisc.toIterator(productStorePromoApplsCol);
                while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()) {
                    GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                    GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");
                    String productPromoId = productPromo.getString("productPromoId");

                    List productPromoRules = productPromo.getRelatedCache("ProductPromoRule", null, null);
                    if (productPromoRules != null && productPromoRules.size() > 0) {
                        // always have a useLimit to avoid unlimited looping, default to 1 if no other is specified
                        Long candidateUseLimit = getProductPromoUseLimit(productPromo, partyId, delegator);

                        long useLimit = candidateUseLimit == null ? 1 : candidateUseLimit.longValue();
                        if (Debug.infoOn()) Debug.logInfo("Running promotion [" + productPromoId + "], useLimit=" + useLimit + ", # of rules=" + productPromoRules.size(), module);

                        boolean requireCode = "Y".equals(productPromo.getString("requireCode"));
                        // check if promo code required
                        if (requireCode) {
                            Set enteredCodes = cart.getProductPromoCodesEntered();
                            if (enteredCodes.size() > 0) {
                                // get all promo codes entered, do a query with an IN condition to see if any of those are related
                                EntityCondition codeCondition = new EntityExpr(new EntityExpr("productPromoId", EntityOperator.EQUALS, productPromoId), EntityOperator.AND, new EntityExpr("productPromoCodeId", EntityOperator.IN, enteredCodes));
                                // may want to sort by something else to decide which code to use if there is more than one candidate
                                List productPromoCodeList = delegator.findByCondition("ProductPromoCode", codeCondition, null, UtilMisc.toList("productPromoCodeId"));
                                Iterator productPromoCodeIter = productPromoCodeList.iterator();
                                // support multiple promo codes for a single promo, ie if we run into a use limit for one code see if we can find another for this promo
                                // check the use limit before each pass so if the promo use limit has been hit we don't keep on trying for the promo code use limit, if there is one of course
                                while ((useLimit > cart.getProductPromoUseCount(productPromoId)) && productPromoCodeIter.hasNext()) {
                                    GenericValue productPromoCode = (GenericValue) productPromoCodeIter.next();
                                    String productPromoCodeId = productPromoCode.getString("productPromoCode");
                                    Long codeUseLimit = getProductPromoCodeUseLimit(productPromoCode, partyId, delegator);
                                    cartChanged = runProductPromoRules(cart, cartChanged, useLimit, true, productPromoCodeId, codeUseLimit, productPromo, productPromoRules, dispatcher, delegator, nowTimestamp);
                                }
                            }
                        } else {
                            cartChanged = runProductPromoRules(cart, cartChanged, useLimit, false, null, null, productPromo, productPromoRules, dispatcher, delegator, nowTimestamp);
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

    /** calculate low use limit for this promo for the current "order", check per order, customer, promo */
    public static Long getProductPromoUseLimit(GenericValue productPromo, String partyId, GenericDelegator delegator) throws GenericEntityException {
        String productPromoId = productPromo.getString("productPromoId");
        Long candidateUseLimit = null;

        Long useLimitPerOrder = productPromo.getLong("useLimitPerOrder");
        if (useLimitPerOrder != null) {
            if (candidateUseLimit == null || candidateUseLimit.longValue() > useLimitPerOrder.longValue()) {
                candidateUseLimit = useLimitPerOrder;
            }
        }

        Long useLimitPerCustomer = productPromo.getLong("useLimitPerCustomer");
        if (useLimitPerCustomer != null && UtilValidate.isNotEmpty(partyId)) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            long productPromoCustomerUseSize = delegator.findCountByAnd("ProductPromoUse", UtilMisc.toMap("productPromoId", productPromoId, "partyId", partyId));
            long perCustomerThisOrder = useLimitPerCustomer.longValue() - productPromoCustomerUseSize;
            if (candidateUseLimit == null || candidateUseLimit.longValue() > perCustomerThisOrder) {
                candidateUseLimit = new Long(perCustomerThisOrder);
            }
        }

        Long useLimitPerPromotion = productPromo.getLong("useLimitPerPromotion");
        if (useLimitPerPromotion != null) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            long productPromoUseSize = delegator.findCountByAnd("ProductPromoUse", UtilMisc.toMap("productPromoId", productPromoId));
            long perPromotionThisOrder = useLimitPerPromotion.longValue() - productPromoUseSize;
            if (candidateUseLimit == null || candidateUseLimit.longValue() > perPromotionThisOrder) {
                candidateUseLimit = new Long(perPromotionThisOrder);
            }
        }

        return candidateUseLimit;
    }

    public static Long getProductPromoCodeUseLimit(GenericValue productPromoCode, String partyId, GenericDelegator delegator) throws GenericEntityException {
        String productPromoCodeId = productPromoCode.getString("productPromoCodeId");
        Long codeUseLimit = null;

        // check promo code use limits, per customer, code
        Long codeUseLimitPerCustomer = productPromoCode.getLong("useLimitPerCustomer");
        if (codeUseLimitPerCustomer != null && UtilValidate.isNotEmpty(partyId)) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            long productPromoCustomerUseSize = delegator.findCountByAnd("ProductPromoUse", UtilMisc.toMap("productPromoCodeId", productPromoCodeId, "partyId", partyId));
            long perCustomerThisOrder = codeUseLimitPerCustomer.longValue() - productPromoCustomerUseSize;
            if (codeUseLimit == null || codeUseLimit.longValue() > perCustomerThisOrder) {
                codeUseLimit = new Long(perCustomerThisOrder);
            }
        }

        Long codeUseLimitPerCode = productPromoCode.getLong("useLimitPerCode");
        if (codeUseLimitPerCode != null) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            long productPromoCodeUseSize = delegator.findCountByAnd("ProductPromoUse", UtilMisc.toMap("productPromoCodeId", productPromoCodeId));
            long perCodeThisOrder = codeUseLimitPerCode.longValue() - productPromoCodeUseSize;
            if (codeUseLimit == null || codeUseLimit.longValue() > perCodeThisOrder) {
                codeUseLimit = new Long(perCodeThisOrder);
            }
        }

        return codeUseLimit;
    }
    
    public static String checkCanUsePromoCode(String productPromoCodeId, GenericValue userLogin, GenericDelegator delegator) {
        try {
            GenericValue productPromoCode = delegator.findByPrimaryKey("ProductPromoCode", UtilMisc.toMap("productPromoCodeId", productPromoCodeId));
            if (productPromoCode == null) {
                return "The promotion code [" + productPromoCodeId + "] is not valid.";
            }
            
            if ("Y".equals(productPromoCode.getString("requireEmailOrParty"))) {
                boolean hasEmailOrParty = false;
                
                if (userLogin != null) {
                    // check partyId
                    String partyId = userLogin.getString("partyId");
                    if (UtilValidate.isNotEmpty(partyId)) {
                        if (delegator.findByPrimaryKey("ProductPromoCodeParty", UtilMisc.toMap("productPromoCodeId", productPromoCodeId, "partyId", partyId)) != null) {
                            // found party associated with the code, looks good...
                            return null;
                        }
                        
                        // check email address in ProductPromoCodeEmail
                        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                        List validEmailCondList = new ArrayList();
                        validEmailCondList.add(new EntityExpr("partyId", EntityOperator.EQUALS, partyId));
                        validEmailCondList.add(new EntityExpr("productPromoCodeId", EntityOperator.EQUALS, productPromoCodeId));
                        validEmailCondList.add(new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                        validEmailCondList.add(new EntityExpr(new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp), 
                                EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.EQUALS, null)));
                        EntityCondition validEmailCondition = new EntityConditionList(validEmailCondList, EntityOperator.AND);
                        long validEmailCount = delegator.findCountByCondition("ProductPromoCodeEmailParty", validEmailCondition, null);
                        if (validEmailCount > 0) {
                            // there was an email in the list, looks good... 
                            return null;
                        }
                    }
                }
                
                if (!hasEmailOrParty) {
                    return "This promotion code [" + productPromoCodeId + "] requires you to be associated with it by account or email address and you are not associated with it.";
                }
            }
            
            return null;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up ProductPromoCode", module);
            return "Error looking up code [" + productPromoCodeId + "]:" + e.toString();
        }
    }

    protected static boolean runProductPromoRules(ShoppingCart cart, boolean cartChanged, long useLimit, boolean requireCode, String productPromoCodeId, Long codeUseLimit,
            GenericValue productPromo, List productPromoRules, LocalDispatcher dispatcher, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        String productPromoId = productPromo.getString("productPromoId");
        while ((useLimit > cart.getProductPromoUseCount(productPromoId)) &&
                (!requireCode || UtilValidate.isNotEmpty(productPromoCodeId)) &&
                (codeUseLimit == null || codeUseLimit.intValue() > cart.getProductPromoCodeUse(productPromoCodeId))) {
            boolean promoUsed = false;

            Iterator promoRulesIter = productPromoRules.iterator();
            while (promoRulesIter != null && promoRulesIter.hasNext()) {
                GenericValue productPromoRule = (GenericValue) promoRulesIter.next();

                // if apply then performActions when no conditions are false, so default to true
                boolean performActions = true;

                // loop through conditions for rule, if any false, set allConditionsTrue to false
                List productPromoConds = productPromoRule.getRelatedCache("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId"));
                if (Debug.infoOn()) Debug.logInfo("Checking " + productPromoConds.size() + " conditions for rule " + productPromoRule, module);

                Iterator productPromoCondIter = UtilMisc.toIterator(productPromoConds);
                while (productPromoCondIter != null && productPromoCondIter.hasNext()) {
                    GenericValue productPromoCond = (GenericValue) productPromoCondIter.next();

                    boolean condResult = checkCondition(productPromoCond, cart, delegator, nowTimestamp);

                    // any false condition will cause it to NOT perform the action
                    if (condResult == false) {
                        performActions = false;
                        break;
                    }
                }

                if (performActions) {
                    // perform all actions, either apply or unapply

                    List productPromoActions = productPromoRule.getRelatedCache("ProductPromoAction", null, UtilMisc.toList("productPromoActionSeqId"));
                    if (Debug.infoOn()) Debug.logInfo("Performing " + productPromoActions.size() + " actions for rule " + productPromoRule, module);
                    Iterator productPromoActionIter = UtilMisc.toIterator(productPromoActions);
                    while (productPromoActionIter != null && productPromoActionIter.hasNext()) {
                        GenericValue productPromoAction = (GenericValue) productPromoActionIter.next();

                        // Debug.logInfo("Doing action: " + productPromoAction, module);
                        try {
                            boolean actionChangedCart = performAction(productPromoAction, cart, delegator, dispatcher, nowTimestamp);

                            // only set if true, don't set back to false: implements OR logic (ie if ANY actions change content, redo loop)
                            if (actionChangedCart) {
                                promoUsed = true;
                                cartChanged = true;
                            }
                        } catch (CartItemModifyException e) {
                            Debug.logError("Error modifying the cart in perform promotion action: " + e.toString(), module);
                        }
                    }
                }
            }

            if (promoUsed) {
                cart.addProductPromoUse(productPromo.getString("productPromoId"), productPromoCodeId);
            } else {
                // the promotion was not used, don't try again until we finish a full pass and come back to see the promo conditions are now satisfied based on changes to the cart
                break;
            }
        }

        return cartChanged;
    }

    protected static boolean checkCondition(GenericValue productPromoCond, ShoppingCart cart, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        String condValue = productPromoCond.getString("condValue");
        String inputParamEnumId = productPromoCond.getString("inputParamEnumId");
        String operatorEnumId = productPromoCond.getString("operatorEnumId");

        String partyId = cart.getPartyId();

        if (Debug.verboseOn()) Debug.logVerbose("Checking promotion condition: " + productPromoCond, module);
        int compare = 0;

        if ("PPIP_PRODUCT_QUANT".equals(inputParamEnumId)) {
            double quantityNeeded = 1.0;
            if (UtilValidate.isNotEmpty(condValue)) {
                quantityNeeded = Double.parseDouble(condValue);
            }

            Set productIds = ProductPromoWorker.getPromoRuleCondProductIds(productPromoCond, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityNeeded > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                if (!cartItem.getIsPromo() && productIds.contains(cartItem.getProductId())) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    quantityNeeded -= cartItem.addPromoQuantityCandidateUse(quantityNeeded, productPromoCond);
                }
            }

            // if quantityNeeded > 0 then the promo condition failed, so remove candidate promo uses and increment the promoQuantityUsed to restore it
            if (quantityNeeded > 0) {
                // failed, reset the entire rule, ie including all other conditions that might have been done before
                cart.resetPromoRuleUse(productPromoCond.getString("productPromoId"), productPromoCond.getString("productPromoRuleId"));
                compare = -1;
            } else {
                // we got it, the conditions are in place...
                compare = 0;
                // NOTE: don't confirm rpomo rule use here, wait until actions are complete for the rule to do that
            }


        /* replaced by PPIP_PRODUCT_QUANT
        } else if ("PPIP_PRODUCT_ID_IC".equals(inputParamEnumId)) {
            String candidateProductId = condValue;

            if (candidateProductId == null) {
                // if null, then it's not in the cart
                compare = 1;
            } else {
                // Debug.logInfo("Testing to see if productId \"" + candidateProductId + "\" is in the cart", module);
                List productCartItems = cart.findAllCartItems(candidateProductId);

                // don't count promotion items in this count...
                Iterator pciIter = productCartItems.iterator();
                while (pciIter.hasNext()) {
                    ShoppingCartItem productCartItem = (ShoppingCartItem) pciIter.next();
                    if (productCartItem.getIsPromo()) pciIter.remove();
                }

                if (productCartItems.size() > 0) {
                    //Debug.logError("Item with productId \"" + candidateProductId + "\" IS in the cart", module);
                    compare = 0;
                } else {
                    //Debug.logError("Item with productId \"" + candidateProductId + "\" IS NOT in the cart", module);
                    compare = 1;
                }
            }
        } else if ("PPIP_CATEGORY_ID_IC".equals(inputParamEnumId)) {
            String productCategoryId = condValue;
            Set productIds = new HashSet();

            Iterator cartItemIter = cart.iterator();
            while (cartItemIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) cartItemIter.next();
                if (!cartItem.getIsPromo()) {
                    productIds.add(cartItem.getProductId());
                }
            }

            compare = 1;
            // NOTE: this technique is efficient for a smaller number of items in the cart, if there are a lot of lines
            //in the cart then a non-cached query with a set of productIds using the IN operator would be better
            Iterator productIdIter = productIds.iterator();
            while (productIdIter.hasNext()) {
                String productId = (String) productIdIter.next();

                // if a ProductCategoryMember exists for this productId and the specified productCategoryId
                List productCategoryMembers = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productId", productId, "productCategoryId", productCategoryId));
                // and from/thru date within range
                productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, nowTimestamp);
                if (productCategoryMembers != null && productCategoryMembers.size() > 0) {
                    // if any product is in category, set true and break
                    // then 0 (equals), otherwise 1 (not equals)
                    compare = 0;
                    break;
                }
            }
        */
        } else if ("PPIP_NEW_ACCT".equals(inputParamEnumId)) {
            Double acctDays = cart.getPartyDaysSinceCreated(nowTimestamp);
            if (acctDays == null) {
                // condition always fails if we don't know how many days since account created
                return false;
            }
            compare = acctDays.compareTo(Double.valueOf(condValue));
        } else if ("PPIP_PARTY_ID".equals(inputParamEnumId)) {
            if (partyId != null) {
                compare = partyId.compareTo(condValue);
            } else {
                compare = 1;
            }

            /* These aren't supported yet, ie TODO
             } else if ("PRIP_PARTY_GRP_MEM".equals(inputParamEnumId)) {
             } else if ("PRIP_PARTY_CLASS".equals(inputParamEnumId)) {
             */
        } else if ("PPIP_ROLE_TYPE".equals(inputParamEnumId)) {
            if (partyId != null) {
                // if a PartyRole exists for this partyId and the specified roleTypeId
                GenericValue partyRole = delegator.findByPrimaryKeyCache("PartyRole",
                        UtilMisc.toMap("partyId", partyId, "roleTypeId", condValue));

                // then 0 (equals), otherwise 1 (not equals)
                if (partyRole != null) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            } else {
                compare = 1;
            }
        } else if ("PPIP_ORDER_TOTAL".equals(inputParamEnumId)) {
            Double orderSubTotal = new Double(cart.getSubTotal());
            if (Debug.verboseOn()) Debug.logVerbose("Doing order total compare: orderSubTotal=" + orderSubTotal, module);
            compare = orderSubTotal.compareTo(Double.valueOf(condValue));
        } else {
            Debug.logWarning("An un-supported productPromoCond input parameter (lhs) was used: " + productPromoCond.getString("inputParamEnumId") + ", returning false, ie check failed", module);
            return false;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Condition compare done, compare=" + compare, module);

        if ("PPC_EQ".equals(operatorEnumId)) {
            if (compare == 0) return true;
        } else if ("PPC_NEQ".equals(operatorEnumId)) {
            if (compare != 0) return true;
        } else if ("PPC_LT".equals(operatorEnumId)) {
            if (compare < 0) return true;
        } else if ("PPC_LTE".equals(operatorEnumId)) {
            if (compare <= 0) return true;
        } else if ("PPC_GT".equals(operatorEnumId)) {
            if (compare > 0) return true;
        } else if ("PPC_GTE".equals(operatorEnumId)) {
            if (compare >= 0) return true;
        } else {
            Debug.logWarning("An un-supported productPromoCond condition was used: " + operatorEnumId + ", returning false, ie check failed", module);
            return false;
        }
        return false;
    }

    /** returns true if the cart was changed and rules need to be re-evaluted */
    protected static boolean performAction(GenericValue productPromoAction, ShoppingCart cart, GenericDelegator delegator, LocalDispatcher dispatcher, Timestamp nowTimestamp) throws GenericEntityException, CartItemModifyException {
        boolean ranAction = false;

        String productPromoActionEnumId = productPromoAction.getString("productPromoActionEnumId");

        if ("PROMO_GWP".equals(productPromoActionEnumId)) {
            Integer itemLoc = findPromoItem(productPromoAction, cart);

            if (itemLoc != null) {
                if (Debug.verboseOn()) Debug.logVerbose("Not adding promo item, already there; action: " + productPromoAction, module);
                ranAction = false;
            } else {
                GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productPromoAction.get("productId")));
                double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();

                // pass null for cartLocation to add to end of cart, pass false for doPromotions to avoid infinite recursion
                ShoppingCartItem gwpItem = null;
                try {
                    // TODO: where should we REALLY get the prodCatalogId?
                    String prodCatalogId = null;
                    gwpItem = ShoppingCartItem.makeItem(null, product, quantity, null, null, prodCatalogId, dispatcher, cart, false);
                } catch (CartItemModifyException e) {
                    int gwpItemIndex = cart.getItemIndex(gwpItem);
                    cart.removeCartItem(gwpItemIndex, dispatcher);
                    throw e;
                }

                double discountAmount = -(quantity * gwpItem.getBasePrice());
                doOrderItemPromoAction(productPromoAction, gwpItem, discountAmount, "amount", delegator);

                // set promo after create; note that to setQuantity we must clear this flag, setQuantity, then re-set the flag
                gwpItem.setIsPromo(true);
                if (Debug.verboseOn()) Debug.logVerbose("gwpItem adjustments: " + gwpItem.getAdjustments(), module);

                ranAction = true;
            }
        } else if ("PROMO_FREE_SHIPPING".equals(productPromoActionEnumId)) {
            // this may look a bit funny: on each pass all rules that do free shipping will set their own rule id for it,
            // and on unapply if the promo and rule ids are the same then it will clear it; essentially on any pass
            // through the promos and rules if any free shipping should be there, it will be there
            cart.addFreeShippingProductPromoAction(productPromoAction);
            // don't consider this as a cart change...
            ranAction = true;
        } else if ("PROMO_PROD_DISC".equals(productPromoActionEnumId)) {
            double quantityDesired = productPromoAction.get("quantity") == null ? 1.0 : productPromoAction.getDouble("quantity").doubleValue();
            double startingQuantity = quantityDesired;

            Set productIds = ProductPromoWorker.getPromoRuleActionProductIds(productPromoAction, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityDesired > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId)))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    double quantityUsed = cartItem.addPromoQuantityCandidateUse(quantityDesired, productPromoAction);
                    quantityDesired -= quantityUsed;

                    // create an adjustment and add it to the cartItem that implements the promotion action
                    double percentModifier = productPromoAction.get("amount") == null ? 0.0 : (productPromoAction.getDouble("amount").doubleValue()/100.0);
                    double discountAmount = -(quantityUsed * cartItem.getBasePrice() * percentModifier);
                    doOrderItemPromoAction(productPromoAction, cartItem, discountAmount, "amount", delegator);
                }
            }

            if (quantityDesired == startingQuantity) {
                // couldn't find any cart items to give a discount to, don't consider action run
                ranAction = false;
            } else {
                ranAction = true;
            }
        } else if ("PROMO_PROD_AMDISC".equals(productPromoActionEnumId)) {
            double quantityDesired = productPromoAction.get("quantity") == null ? 1.0 : productPromoAction.getDouble("quantity").doubleValue();
            double startingQuantity = quantityDesired;

            Set productIds = ProductPromoWorker.getPromoRuleActionProductIds(productPromoAction, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityDesired > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId)))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    double quantityUsed = cartItem.addPromoQuantityCandidateUse(quantityDesired, productPromoAction);
                    quantityDesired -= quantityUsed;

                    // create an adjustment and add it to the cartItem that implements the promotion action
                    double discount = productPromoAction.get("amount") == null ? 0.0 : productPromoAction.getDouble("amount").doubleValue();
                    // don't allow the discount to be greater than the price
                    if (discount > cartItem.getBasePrice()) {
                        discount = cartItem.getBasePrice();
                    }
                    double discountAmount = -(quantityUsed * discount);
                    doOrderItemPromoAction(productPromoAction, cartItem, discountAmount, "amount", delegator);
                }
            }

            if (quantityDesired == startingQuantity) {
                // couldn't find any cart items to give a discount to, don't consider action run
                ranAction = false;
            } else {
                ranAction = true;
            }
        } else if ("PROMO_PROD_PRICE".equals(productPromoActionEnumId)) {
            // with this we want the set of used items to be one price, so total the price for all used items, subtract the amount we want them to cost, and create an adjustment for what is left
            double quantityDesired = productPromoAction.get("quantity") == null ? 1.0 : productPromoAction.getDouble("quantity").doubleValue();

            double desiredAmount = productPromoAction.get("amount") == null ? 0.0 : productPromoAction.getDouble("amount").doubleValue();
            double totalAmount = 0;

            Set productIds = ProductPromoWorker.getPromoRuleActionProductIds(productPromoAction, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityDesired > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId)))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    double quantityUsed = cartItem.addPromoQuantityCandidateUse(quantityDesired, productPromoAction);
                    if (quantityUsed > 0) {
                        quantityDesired -= quantityUsed;
                        totalAmount += quantityUsed * cartItem.getBasePrice();
                    }
                }
            }

            ranAction = false;
            if (totalAmount > desiredAmount && quantityDesired == 0) {
                ranAction = true;
                double discountAmount = -(totalAmount - desiredAmount);
                doOrderPromoAction(productPromoAction, cart, discountAmount, "amount", delegator);
            } else {
                // clear out any action uses for this so they don't become part of anything else
                cart.resetPromoRuleUse(productPromoAction.getString("productPromoId"), productPromoAction.getString("productPromoRuleId"));
            }
        } else if ("PROMO_ORDER_PERCENT".equals(productPromoActionEnumId)) {
            ranAction = true;
            double amount = -(productPromoAction.get("amount") == null ? 0.0 : (productPromoAction.getDouble("amount").doubleValue() / 100.0));
            doOrderPromoAction(productPromoAction, cart, amount, "percentage", delegator);
        } else if ("PROMO_ORDER_AMOUNT".equals(productPromoActionEnumId)) {
            ranAction = true;
            double amount = -(productPromoAction.get("amount") == null ? 0.0 : productPromoAction.getDouble("amount").doubleValue());
            doOrderPromoAction(productPromoAction, cart, amount, "amount", delegator);
        } else {
            Debug.logError("An un-supported productPromoActionType was used: " + productPromoActionEnumId + ", not performing any action", module);
            ranAction = false;
        }

        if (ranAction) {
            // in action, if doesn't have enough quantity to use the promo at all, remove candidate promo uses and increment promoQuantityUsed; this should go for all actions, if any action runs we confirm
            cart.confirmPromoRuleUse(productPromoAction.getString("productPromoId"), productPromoAction.getString("productPromoRuleId"));
        } else {
            cart.resetPromoRuleUse(productPromoAction.getString("productPromoId"), productPromoAction.getString("productPromoRuleId"));
        }

        return ranAction;
    }

    protected static Integer findPromoItem(GenericValue productPromoAction, ShoppingCart cart) {
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

    protected static void clearAllPromotions(ShoppingCart cart) {
        // remove cart adjustments from promo actions
        List cartAdjustments = cart.getAdjustments();
        if (cartAdjustments != null) {
            Iterator cartAdjustmentIter = cartAdjustments.iterator();
            while (cartAdjustmentIter.hasNext()) {
                GenericValue checkOrderAdjustment = (GenericValue) cartAdjustmentIter.next();
                if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                    cartAdjustmentIter.remove();
                }
            }
        }

        // remove cart lines that are promos (ie GWPs) and cart line adjustments from promo actions
        Iterator cartItemIter = cart.items().iterator();
        while (cartItemIter.hasNext()) {
            ShoppingCartItem checkItem = (ShoppingCartItem) cartItemIter.next();
            if (checkItem.getIsPromo()) {
                cartItemIter.remove();
            } else {
                // found a promo item with the productId, see if it has a matching adjustment on it
                Iterator checkOrderAdjustments = UtilMisc.toIterator(checkItem.getAdjustments());
                while (checkOrderAdjustments != null && checkOrderAdjustments.hasNext()) {
                    GenericValue checkOrderAdjustment = (GenericValue) checkOrderAdjustments.next();
                    if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                            UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                            UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                        checkOrderAdjustments.remove();
                    }
                }
            }
        }

        // remove all free shipping promo actions
        cart.removeAllFreeShippingProductPromoActions();

        // clear promo uses & reset promo code uses, and reset info about cart items used for promos (ie qualifiers and benefiters)
        cart.clearProductPromoUseInfo();
    }

    public static void doOrderItemPromoAction(GenericValue productPromoAction, ShoppingCartItem cartItem, double amount, String amountField, GenericDelegator delegator) {
        GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", amountField, new Double(amount),
                    "productPromoId", productPromoAction.get("productPromoId"),
                    "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                    "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

        // if an orderAdjustmentTypeId was included, override the default
        if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
            orderAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
        }

        cartItem.addAdjustment(orderAdjustment);
    }

    public static void doOrderPromoAction(GenericValue productPromoAction, ShoppingCart cart, double amount, String amountField, GenericDelegator delegator) {
        GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", amountField, new Double(amount),
                    "productPromoId", productPromoAction.get("productPromoId"),
                    "productPromoRuleId", productPromoAction.get("productPromoRuleId"),
                    "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId")));

        // if an orderAdjustmentTypeId was included, override the default
        if (UtilValidate.isNotEmpty(productPromoAction.getString("orderAdjustmentTypeId"))) {
            orderAdjustment.set("orderAdjustmentTypeId", productPromoAction.get("orderAdjustmentTypeId"));
        }

        cart.addAdjustment(orderAdjustment);
    }

    protected static Integer findAdjustment(GenericValue productPromoAction, List adjustments) {
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

    public static Set getPromoRuleCondProductIds(GenericValue productPromoCond, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        // get a cached list for the whole promo and filter it as needed, this for better efficiency in caching
        List productPromoCategoriesAll = delegator.findByAndCache("ProductPromoCategory", UtilMisc.toMap("productPromoId", productPromoCond.get("productPromoId")));
        List productPromoCategories = EntityUtil.filterByAnd(productPromoCategoriesAll, UtilMisc.toMap("productPromoRuleId", "_NA_", "productPromoCondSeqId", "_NA_"));
        productPromoCategories.addAll(EntityUtil.filterByAnd(productPromoCategoriesAll, UtilMisc.toMap("productPromoRuleId", productPromoCond.get("productPromoRuleId"), "productPromoCondSeqId", productPromoCond.get("productPromoCondSeqId"))));

        List productPromoProductsAll = delegator.findByAndCache("ProductPromoProduct", UtilMisc.toMap("productPromoId", productPromoCond.get("productPromoId")));
        List productPromoProducts = EntityUtil.filterByAnd(productPromoProductsAll, UtilMisc.toMap("productPromoRuleId", "_NA_", "productPromoCondSeqId", "_NA_"));
        productPromoProducts.addAll(EntityUtil.filterByAnd(productPromoProductsAll, UtilMisc.toMap("productPromoRuleId", productPromoCond.get("productPromoRuleId"), "productPromoCondSeqId", productPromoCond.get("productPromoCondSeqId"))));

        Set productIds = new HashSet();
        makeProductPromoIdSet(productIds, productPromoCategories, productPromoProducts, delegator, nowTimestamp);
        return productIds;
    }

    public static Set getPromoRuleActionProductIds(GenericValue productPromoAction, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        // get a cached list for the whole promo and filter it as needed, this for better efficiency in caching
        List productPromoCategoriesAll = delegator.findByAndCache("ProductPromoCategory", UtilMisc.toMap("productPromoId", productPromoAction.get("productPromoId")));
        List productPromoCategories = EntityUtil.filterByAnd(productPromoCategoriesAll, UtilMisc.toMap("productPromoRuleId", "_NA_", "productPromoActionSeqId", "_NA_"));
        productPromoCategories.addAll(EntityUtil.filterByAnd(productPromoCategoriesAll, UtilMisc.toMap("productPromoRuleId", productPromoAction.get("productPromoRuleId"), "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId"))));

        List productPromoProductsAll = delegator.findByAndCache("ProductPromoProduct", UtilMisc.toMap("productPromoId", productPromoAction.get("productPromoId")));
        List productPromoProducts = EntityUtil.filterByAnd(productPromoProductsAll, UtilMisc.toMap("productPromoRuleId", "_NA_", "productPromoActionSeqId", "_NA_"));
        productPromoProducts.addAll(EntityUtil.filterByAnd(productPromoProductsAll, UtilMisc.toMap("productPromoRuleId", productPromoAction.get("productPromoRuleId"), "productPromoActionSeqId", productPromoAction.get("productPromoActionSeqId"))));

        Set productIds = new HashSet();
        makeProductPromoIdSet(productIds, productPromoCategories, productPromoProducts, delegator, nowTimestamp);
        return productIds;
    }

    public static void makeProductPromoIdSet(Set productIds, List productPromoCategories, List productPromoProducts, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        // do the includes
        handleProductPromoCategories(productIds, productPromoCategories, "PPPA_INCLUDE", delegator, nowTimestamp);
        handleProductPromoProducts(productIds, productPromoProducts, "PPPA_INCLUDE");

        // do the excludes
        handleProductPromoCategories(productIds, productPromoCategories, "PPPA_EXCLUDE", delegator, nowTimestamp);
        handleProductPromoProducts(productIds, productPromoProducts, "PPPA_EXCLUDE");

        // do the always includes
        handleProductPromoCategories(productIds, productPromoCategories, "PPPA_ALWAYS", delegator, nowTimestamp);
        handleProductPromoProducts(productIds, productPromoProducts, "PPPA_ALWAYS");
    }

    public static void makeProductPromoCondActionIdSets(String productPromoId, Set productIdsCond, Set productIdsAction, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        if (nowTimestamp == null) {
            nowTimestamp = UtilDateTime.nowTimestamp();
        }

        List productPromoCategoriesAll = delegator.findByAndCache("ProductPromoCategory", UtilMisc.toMap("productPromoId", productPromoId));
        List productPromoProductsAll = delegator.findByAndCache("ProductPromoProduct", UtilMisc.toMap("productPromoId", productPromoId));

        List productPromoProductsCond = new LinkedList();
        List productPromoCategoriesCond = new LinkedList();
        List productPromoProductsAction = new LinkedList();
        List productPromoCategoriesAction = new LinkedList();

        Iterator productPromoProductsAllIter = productPromoProductsAll.iterator();
        while (productPromoProductsAllIter.hasNext()) {
            GenericValue productPromoProduct = (GenericValue) productPromoProductsAllIter.next();
            // if the rule id is null then this is a global promo one, so always include
            if (!"_NA_".equals(productPromoProduct.getString("productPromoCondSeqId")) || "_NA_".equals(productPromoProduct.getString("productPromoRuleId"))) {
                productPromoProductsCond.add(productPromoProduct);
            }
            if (!"_NA_".equals(productPromoProduct.getString("productPromoActionSeqId")) || "_NA_".equals(productPromoProduct.getString("productPromoRuleId"))) {
                productPromoProductsAction.add(productPromoProduct);
            }
        }
        Iterator productPromoCategoriesAllIter = productPromoCategoriesAll.iterator();
        while (productPromoCategoriesAllIter.hasNext()) {
            GenericValue productPromoCategory = (GenericValue) productPromoCategoriesAllIter.next();
            if (!"_NA_".equals(productPromoCategory.getString("productPromoCondSeqId")) || "_NA_".equals(productPromoCategory.getString("productPromoRuleId"))) {
                productPromoCategoriesCond.add(productPromoCategory);
            }
            if (!"_NA_".equals(productPromoCategory.getString("productPromoActionSeqId")) || "_NA_".equals(productPromoCategory.getString("productPromoRuleId"))) {
                productPromoCategoriesAction.add(productPromoCategory);
            }
        }

        makeProductPromoIdSet(productIdsCond, productPromoCategoriesCond, productPromoProductsCond, delegator, nowTimestamp);
        makeProductPromoIdSet(productIdsAction, productPromoCategoriesAction, productPromoProductsAction, delegator, nowTimestamp);
    }

    protected static void handleProductPromoCategories(Set productIds, List productPromoCategories, String productPromoApplEnumId, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException {
        boolean include = !"PPPA_EXCLUDE".equals(productPromoApplEnumId);
        Set productCategoryIds = new HashSet();
        Iterator productPromoCategoryIter = productPromoCategories.iterator();
        while (productPromoCategoryIter.hasNext()) {
            GenericValue productPromoCategory = (GenericValue) productPromoCategoryIter.next();
            if (productPromoApplEnumId.equals(productPromoCategory.getString("productPromoApplEnumId"))) {
                if ("Y".equals(productPromoCategory.getString("includeSubCategories"))) {
                    ProductSearch.getAllSubCategoryIds(productPromoCategory.getString("productCategoryId"), productCategoryIds, delegator, nowTimestamp);
                } else {
                    productCategoryIds.add(productPromoCategory.getString("productCategoryId"));
                }
            }
        }

        Iterator productCategoryIdIter = productCategoryIds.iterator();
        while (productCategoryIdIter.hasNext()) {
            String productCategoryId = (String) productCategoryIdIter.next();
            // get all product category memebers, filter by date
            List productCategoryMembers = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId));
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, nowTimestamp);
            Iterator productCategoryMemberIter = productCategoryMembers.iterator();
            while (productCategoryMemberIter.hasNext()) {
                GenericValue productCategoryMember = (GenericValue) productCategoryMemberIter.next();
                String productId = productCategoryMember.getString("productId");
                if (include) {
                    productIds.add(productId);
                } else {
                    productIds.remove(productId);
                }
            }
        }
    }

    protected static void handleProductPromoProducts(Set productIds, List productPromoProducts, String productPromoApplEnumId) {
        boolean include = !"PPPA_EXCLUDE".equals(productPromoApplEnumId);
        Iterator productPromoProductIter = productPromoProducts.iterator();
        while (productPromoProductIter.hasNext()) {
            GenericValue productPromoProduct = (GenericValue) productPromoProductIter.next();
            if (productPromoApplEnumId.equals(productPromoProduct.getString("productPromoApplEnumId"))) {
                String productId = productPromoProduct.getString("productId");
                if (include) {
                    productIds.add(productId);
                } else {
                    productIds.remove(productId);
                }
            }
        }
    }
}
