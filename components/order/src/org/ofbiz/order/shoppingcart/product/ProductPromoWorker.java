/*
 * $Id: ProductPromoWorker.java,v 1.42 2004/02/11 12:06:10 jonesde Exp $
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
import org.ofbiz.base.util.UtilProperties;
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
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductSearch;
import org.ofbiz.service.LocalDispatcher;

/**
 * ProductPromoWorker - Worker class for catalog/product promotion related functionality
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.42 $
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

    public static void doPromotions(ShoppingCart cart, LocalDispatcher dispatcher) {
        // this is called when a user logs in so that per customer limits are honored, called by cart when new userlogin is set
        // there is code to store ProductPromoUse information when an order is placed
        // ProductPromoUses are ignored if the corresponding order is cancelled
        // limits sub total for promos to not use gift cards (products with a don't use in promo indicator), also exclude gift cards from all other promotion considerations including subTotals for discounts, etc
        // TODO: (not done, delay, still considering...) add code to check ProductPromoUse limits per promo (customer, promo), and per code (customer, code) to avoid use of promos or codes getting through due to multiple carts getting promos applied at the same time, possibly on totally different servers
        
        GenericDelegator delegator = cart.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // start out by clearing all existing promotions, then we can just add all that apply
        cart.clearAllPromotionInformation();
        
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
            List productStorePromoApplsList = productStore.getRelatedCache("ProductStorePromoAppl", null, UtilMisc.toList("sequenceNum"));
            productStorePromoApplsList = EntityUtil.filterByDate(productStorePromoApplsList, nowTimestamp);

            if (productStorePromoApplsList == null || productStorePromoApplsList.size() == 0) {
                if (Debug.infoOn()) Debug.logInfo("Not doing promotions, none applied to store with ID " + productStoreId, module);
            }

            List productPromoList = new LinkedList();
            Iterator prodCatalogPromoAppls = UtilMisc.toIterator(productStorePromoApplsList);
            while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()) {
                GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");
                productPromoList.add(productPromo);
            }

            // do a calculate only run through the promotions, then order by descending totalDiscountAmount for each promotion
            // NOTE: on this run, with isolatedTestRun passed as false it should not apply any adjustments 
            //  or track which cart items are used for which promotions, but it will track ProductPromoUseInfo and 
            //  useLimits; we are basicly just trying to run each promo "independently" to see how much each is worth
            runProductPromos(productPromoList, cart, delegator, dispatcher, nowTimestamp, true);
            
            // NOTE: after that first pass we could remove any that have a 0 totalDiscountAmount from the run list, but we won't because by the time they are run the cart may have changed enough to get them to go; also, certain actions like free shipping should always be run even though we won't know what the totalDiscountAmount is at the time the promotion is run
            // each ProductPromoUseInfo on the shopping cart will contain it's total value, so add up all totals for each promoId and put them in a List of Maps
            // create a List of Maps with productPromo and totalDiscountAmount, use the Map sorter to sort them descending by totalDiscountAmount
            
            // before sorting split into two lists and sort each list; one list for promos that have a order total condition, and the other list for all promos that don't; then we'll always run the ones that have no condition on the order total first
            List productPromoDiscountMapList = new LinkedList();
            List productPromoDiscountMapListOrderTotal = new LinkedList();
            Iterator productPromoIter = productPromoList.iterator();
            while (productPromoIter.hasNext()) {
                GenericValue productPromo = (GenericValue) productPromoIter.next();
                Map productPromoDiscountMap = UtilMisc.toMap("productPromo", productPromo, "totalDiscountAmount", new Double(cart.getProductPromoUseTotalDiscount(productPromo.getString("productPromoId"))));
                if (hasOrderTotalCondition(productPromo, delegator)) {
                    productPromoDiscountMapListOrderTotal.add(productPromoDiscountMap);
                } else {
                    productPromoDiscountMapList.add(productPromoDiscountMap);
                }
            }
            
            
            // sort the Map List, do it ascending because the discount amounts will be negative, so the lowest number is really the highest discount
            productPromoDiscountMapList = UtilMisc.sortMaps(productPromoDiscountMapList, UtilMisc.toList("+totalDiscountAmount"));
            productPromoDiscountMapListOrderTotal = UtilMisc.sortMaps(productPromoDiscountMapListOrderTotal, UtilMisc.toList("+totalDiscountAmount"));

            productPromoDiscountMapList.addAll(productPromoDiscountMapListOrderTotal);
            
            List sortedProductPromoList = new ArrayList(productPromoDiscountMapList.size());
            Iterator productPromoDiscountMapIter = productPromoDiscountMapList.iterator();
            while (productPromoDiscountMapIter.hasNext()) {
                Map productPromoDiscountMap = (Map) productPromoDiscountMapIter.next();
                GenericValue productPromo = (GenericValue) productPromoDiscountMap.get("productPromo");
                sortedProductPromoList.add(productPromo);
                if (Debug.verboseOn()) Debug.logVerbose("Sorted Promo [" + productPromo.getString("productPromoId") + "] with total discount: " + productPromoDiscountMap.get("totalDiscountAmount"), module);
            }
            
            // okay, all ready, do the real run, clearing the temporary result first...
            cart.clearAllPromotionInformation();
            runProductPromos(sortedProductPromoList, cart, delegator, dispatcher, nowTimestamp, false);
        } catch (NumberFormatException e) {
            Debug.logError(e, "Number not formatted correctly in promotion rules, not completed...", module);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up promotion data while doing promotions", module);
        }
    }
    
    protected static boolean hasOrderTotalCondition(GenericValue productPromo, GenericDelegator delegator) throws GenericEntityException {
        boolean hasOtCond = false;
        List productPromoConds = delegator.findByAndCache("ProductPromoCond", UtilMisc.toMap("productPromoId", productPromo.get("productPromoId")), UtilMisc.toList("productPromoCondSeqId"));
        Iterator productPromoCondIter = productPromoConds.iterator();
        while (productPromoCondIter.hasNext()) {
            GenericValue productPromoCond = (GenericValue) productPromoCondIter.next();
            String inputParamEnumId = productPromoCond.getString("inputParamEnumId");
            if ("PPIP_ORDER_TOTAL".equals(inputParamEnumId)) {
                hasOtCond = true;
                break;
            }
        }
        return hasOtCond;
    }
    
    protected static void runProductPromos(List productPromoList, ShoppingCart cart, GenericDelegator delegator, LocalDispatcher dispatcher, Timestamp nowTimestamp, boolean isolatedTestRun) throws GenericEntityException {
        String partyId = cart.getPartyId();

        // this is our safety net; we should never need to loop through the rules more than a certain number of times, this is that number and may have to be changed for insanely large promo sets...
        long maxIterations = 1000;
        // part of the safety net to avoid infinite iteration
        long numberOfIterations = 0;
        
        // set a max limit on how many times each promo can be run, for cases where there is no use limit this will be the use limit
        //default to 2 times the number of items in the cart
        long maxUseLimit = 2 * Math.round(cart.getTotalQuantity());
        
        try {
            // repeat until no more rules to run: either all rules are run, or no changes to the cart in a loop
            boolean cartChanged = true;
            while (cartChanged) {
                cartChanged = false;
                numberOfIterations++;
                if (numberOfIterations > maxIterations) {
                    Debug.logError("ERROR: While calculating promotions the promotion rules where run more than " + maxIterations + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions.", module);
                    break;
                }

                Iterator productPromoIter = productPromoList.iterator();
                while (productPromoIter.hasNext()) {
                    GenericValue productPromo = (GenericValue) productPromoIter.next();
                    String productPromoId = productPromo.getString("productPromoId");

                    List productPromoRules = productPromo.getRelatedCache("ProductPromoRule", null, null);
                    if (productPromoRules != null && productPromoRules.size() > 0) {
                        // always have a useLimit to avoid unlimited looping, default to 1 if no other is specified
                        Long candidateUseLimit = getProductPromoUseLimit(productPromo, partyId, delegator);
                        Long useLimit = candidateUseLimit;
                        if (Debug.verboseOn()) Debug.logVerbose("Running promotion [" + productPromoId + "], useLimit=" + useLimit + ", # of rules=" + productPromoRules.size(), module);

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
                                while ((useLimit == null || useLimit.longValue() > cart.getProductPromoUseCount(productPromoId)) && productPromoCodeIter.hasNext()) {
                                    GenericValue productPromoCode = (GenericValue) productPromoCodeIter.next();
                                    String productPromoCodeId = productPromoCode.getString("productPromoCodeId");
                                    Long codeUseLimit = getProductPromoCodeUseLimit(productPromoCode, partyId, delegator);
                                    if (runProductPromoRules(cart, cartChanged, useLimit, true, productPromoCodeId, codeUseLimit, maxUseLimit, productPromo, productPromoRules, dispatcher, delegator, nowTimestamp)) {
                                        cartChanged = true;
                                    }
                                    
                                    if (cart.getProductPromoUseCount(productPromoId) > maxUseLimit) {
                                        Debug.logError("ERROR: While calculating promotions the promotion [" + productPromoId + "] action was applied more than " + maxUseLimit + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions.", module);
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (runProductPromoRules(cart, cartChanged, useLimit, false, null, null, maxUseLimit, productPromo, productPromoRules, dispatcher, delegator, nowTimestamp)) {
                                cartChanged = true;
                            }
                        }
                    }
                    
                    // if this is an isolatedTestRun clear out adjustments and cart item promo use info
                    if (isolatedTestRun) {
                        cart.clearAllPromotionAdjustments();
                        cart.clearCartItemUseInPromoInfo();
                    }
                }
                
                // if this is an isolatedTestRun, then only go through it once, never retry
                if (isolatedTestRun) {
                    cartChanged = false;
                }
            }
        } catch (UseLimitException e) {
            Debug.logError(e, e.toString(), module);
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
        
        // Debug.logInfo("Promo [" + productPromoId + "] use limit after per order check: " + candidateUseLimit, module);

        Long useLimitPerCustomer = productPromo.getLong("useLimitPerCustomer");
        // check this whether or not there is a party right now
        if (useLimitPerCustomer != null) {
            // if partyId is not empty check previous usage
            long productPromoCustomerUseSize = 0;
            if (UtilValidate.isNotEmpty(partyId)) {
                // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
                EntityCondition checkCondition = new EntityConditionList(UtilMisc.toList(
                        new EntityExpr("productPromoId", EntityOperator.EQUALS, productPromoId),
                        new EntityExpr("partyId", EntityOperator.EQUALS, partyId),
                        new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                        new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED")), EntityOperator.AND);
                productPromoCustomerUseSize = delegator.findCountByCondition("ProductPromoUseCheck", checkCondition, null);
            }
            long perCustomerThisOrder = useLimitPerCustomer.longValue() - productPromoCustomerUseSize;
            if (candidateUseLimit == null || candidateUseLimit.longValue() > perCustomerThisOrder) {
                candidateUseLimit = new Long(perCustomerThisOrder);
            }
        }

        // Debug.logInfo("Promo [" + productPromoId + "] use limit after per customer check: " + candidateUseLimit, module);

        Long useLimitPerPromotion = productPromo.getLong("useLimitPerPromotion");
        if (useLimitPerPromotion != null) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            EntityCondition checkCondition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productPromoId", EntityOperator.EQUALS, productPromoId),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED")), EntityOperator.AND);
            long productPromoUseSize = delegator.findCountByCondition("ProductPromoUseCheck", checkCondition, null);
            long perPromotionThisOrder = useLimitPerPromotion.longValue() - productPromoUseSize;
            if (candidateUseLimit == null || candidateUseLimit.longValue() > perPromotionThisOrder) {
                candidateUseLimit = new Long(perPromotionThisOrder);
            }
        }

        // Debug.logInfo("Promo [" + productPromoId + "] use limit after per promotion check: " + candidateUseLimit, module);

        return candidateUseLimit;
    }

    public static Long getProductPromoCodeUseLimit(GenericValue productPromoCode, String partyId, GenericDelegator delegator) throws GenericEntityException {
        String productPromoCodeId = productPromoCode.getString("productPromoCodeId");
        Long codeUseLimit = null;

        // check promo code use limits, per customer, code
        Long codeUseLimitPerCustomer = productPromoCode.getLong("useLimitPerCustomer");
        if (codeUseLimitPerCustomer != null && UtilValidate.isNotEmpty(partyId)) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            EntityCondition checkCondition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productPromoCodeId", EntityOperator.EQUALS, productPromoCodeId),
                    new EntityExpr("partyId", EntityOperator.EQUALS, partyId),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED")), EntityOperator.AND);
            long productPromoCustomerUseSize = delegator.findCountByCondition("ProductPromoUseCheck", checkCondition, null);
            long perCustomerThisOrder = codeUseLimitPerCustomer.longValue() - productPromoCustomerUseSize;
            if (codeUseLimit == null || codeUseLimit.longValue() > perCustomerThisOrder) {
                codeUseLimit = new Long(perCustomerThisOrder);
            }
        }

        Long codeUseLimitPerCode = productPromoCode.getLong("useLimitPerCode");
        if (codeUseLimitPerCode != null) {
            // check to see how many times this has been used for other orders for this customer, the remainder is the limit for this order
            EntityCondition checkCondition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productPromoCodeId", EntityOperator.EQUALS, productPromoCodeId),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED")), EntityOperator.AND);
            long productPromoCodeUseSize = delegator.findCountByCondition("ProductPromoUseCheck", checkCondition, null);
            long perCodeThisOrder = codeUseLimitPerCode.longValue() - productPromoCodeUseSize;
            if (codeUseLimit == null || codeUseLimit.longValue() > perCodeThisOrder) {
                codeUseLimit = new Long(perCodeThisOrder);
            }
        }

        return codeUseLimit;
    }
    
    public static String checkCanUsePromoCode(String productPromoCodeId, String partyId, GenericDelegator delegator) {
        try {
            GenericValue productPromoCode = delegator.findByPrimaryKey("ProductPromoCode", UtilMisc.toMap("productPromoCodeId", productPromoCodeId));
            if (productPromoCode == null) {
                return "The promotion code [" + productPromoCodeId + "] is not valid.";
            }
            
            if ("Y".equals(productPromoCode.getString("requireEmailOrParty"))) {
                boolean hasEmailOrParty = false;
                
                // check partyId
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
                
                if (!hasEmailOrParty) {
                    return "This promotion code [" + productPromoCodeId + "] requires you to be associated with it by account or email address and you are not associated with it.";
                }
            }
            
            // check per customer and per promotion code use limits
            Long useLimit = getProductPromoCodeUseLimit(productPromoCode, partyId, delegator);
            if (useLimit != null && useLimit.longValue() <= 0) {
                return "This promotion code [" + productPromoCodeId + "] has reached it's maximum use limit for you and can no longer be used.";
            }
            
            return null;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up ProductPromoCode", module);
            return "Error looking up code [" + productPromoCodeId + "]:" + e.toString();
        }
    }

    public static String makeAutoDescription(GenericValue productPromo, GenericDelegator delegator, Locale locale) throws GenericEntityException {
        if (productPromo == null) {
            return "";
        }
        StringBuffer promoDescBuf = new StringBuffer();
        List productPromoRules = productPromo.getRelatedCache("ProductPromoRule", null, null);
        Iterator promoRulesIter = productPromoRules.iterator();
        while (promoRulesIter != null && promoRulesIter.hasNext()) {
            GenericValue productPromoRule = (GenericValue) promoRulesIter.next();

            List productPromoConds = delegator.findByAndCache("ProductPromoCond", UtilMisc.toMap("productPromoId", productPromo.get("productPromoId")), UtilMisc.toList("productPromoCondSeqId"));
            productPromoConds = EntityUtil.filterByAnd(productPromoConds, UtilMisc.toMap("productPromoRuleId", productPromoRule.get("productPromoRuleId")));
            // using the other method to consolodate cache entries because the same cache is used elsewhere: List productPromoConds = productPromoRule.getRelatedCache("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId"));
            Iterator productPromoCondIter = UtilMisc.toIterator(productPromoConds);
            while (productPromoCondIter != null && productPromoCondIter.hasNext()) {
                GenericValue productPromoCond = (GenericValue) productPromoCondIter.next();

                String equalityOperator = UtilProperties.getMessage("promotext", "operator.equality." + productPromoCond.getString("operatorEnumId"), locale);
                String quantityOperator = UtilProperties.getMessage("promotext", "operator.quantity." + productPromoCond.getString("operatorEnumId"), locale);

                String condValue = "invalid";
                if (UtilValidate.isNotEmpty(productPromoCond.getString("condValue"))) {
                    condValue = productPromoCond.getString("condValue");
                }
                
                Map messageContext = UtilMisc.toMap("condValue", condValue, "equalityOperator", equalityOperator, "quantityOperator", quantityOperator);
                String msgProp = UtilProperties.getMessage("promotext", "condition." + productPromoCond.getString("inputParamEnumId"), messageContext, locale);
                promoDescBuf.append(msgProp);
                promoDescBuf.append(" ");
                
                if (promoRulesIter.hasNext()) {
                    promoDescBuf.append(" and ");
                }
            }

            List productPromoActions = productPromoRule.getRelatedCache("ProductPromoAction", null, UtilMisc.toList("productPromoActionSeqId"));
            Iterator productPromoActionIter = UtilMisc.toIterator(productPromoActions);
            while (productPromoActionIter != null && productPromoActionIter.hasNext()) {
                GenericValue productPromoAction = (GenericValue) productPromoActionIter.next();

                String productId = productPromoAction.getString("productId");
                
                Map messageContext = UtilMisc.toMap("quantity", productPromoAction.get("quantity"), "amount", productPromoAction.get("amount"), "productId", productId, "partyId", productPromoAction.get("partyId"));

                if (UtilValidate.isEmpty((String) messageContext.get("productId"))) messageContext.put("productId", "any");
                if (UtilValidate.isEmpty((String) messageContext.get("partyId"))) messageContext.put("partyId", "any");
                GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
                if (product != null) {
                    messageContext.put("productName", ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale));
                }
                
                String msgProp = UtilProperties.getMessage("promotext", "action." + productPromoAction.getString("productPromoActionEnumId"), messageContext, locale);
                promoDescBuf.append(msgProp);
                promoDescBuf.append(" ");
                
                if (promoRulesIter.hasNext()) {
                    promoDescBuf.append(" and ");
                }
            }
            
            if (promoRulesIter.hasNext()) {
                promoDescBuf.append(" or ");
            }
        }

        if (promoDescBuf.length() > 0) {
            // remove any trailing space
            if (promoDescBuf.charAt(promoDescBuf.length() - 1) == ' ') promoDescBuf.deleteCharAt(promoDescBuf.length() - 1);
            // add a period
            promoDescBuf.append(". ");
            // capitalize the first letter
            promoDescBuf.setCharAt(0, Character.toUpperCase(promoDescBuf.charAt(0)));
        }
        
        if ("Y".equals(productPromo.getString("requireCode"))) {
            promoDescBuf.append("Requires code to use. ");
        }
        if (productPromo.getLong("useLimitPerOrder") != null) {
            promoDescBuf.append("Limit ");
            promoDescBuf.append(productPromo.getLong("useLimitPerOrder"));
            promoDescBuf.append(" per order. ");
        }
        if (productPromo.getLong("useLimitPerCustomer") != null) {
            promoDescBuf.append("Limit ");
            promoDescBuf.append(productPromo.getLong("useLimitPerCustomer"));
            promoDescBuf.append(" per customer. ");
        }
        if (productPromo.getLong("useLimitPerPromotion") != null) {
            promoDescBuf.append("Limit ");
            promoDescBuf.append(productPromo.getLong("useLimitPerPromotion"));
            promoDescBuf.append(" per promotion. ");
        }
        
        return promoDescBuf.toString();
    }
    
    protected static boolean runProductPromoRules(ShoppingCart cart, boolean cartChanged, Long useLimit, boolean requireCode, String productPromoCodeId, Long codeUseLimit, long maxUseLimit,
            GenericValue productPromo, List productPromoRules, LocalDispatcher dispatcher, GenericDelegator delegator, Timestamp nowTimestamp) throws GenericEntityException, UseLimitException {
        String productPromoId = productPromo.getString("productPromoId");
        while ((useLimit == null || useLimit.longValue() > cart.getProductPromoUseCount(productPromoId)) &&
                (!requireCode || UtilValidate.isNotEmpty(productPromoCodeId)) &&
                (codeUseLimit == null || codeUseLimit.longValue() > cart.getProductPromoCodeUse(productPromoCodeId))) {
            boolean promoUsed = false;
            double totalDiscountAmount = 0;
            double quantityLeftInActions = 0;

            Iterator promoRulesIter = productPromoRules.iterator();
            while (promoRulesIter != null && promoRulesIter.hasNext()) {
                GenericValue productPromoRule = (GenericValue) promoRulesIter.next();

                // if apply then performActions when no conditions are false, so default to true
                boolean performActions = true;

                // loop through conditions for rule, if any false, set allConditionsTrue to false
                List productPromoConds = delegator.findByAndCache("ProductPromoCond", UtilMisc.toMap("productPromoId", productPromo.get("productPromoId")), UtilMisc.toList("productPromoCondSeqId"));
                productPromoConds = EntityUtil.filterByAnd(productPromoConds, UtilMisc.toMap("productPromoRuleId", productPromoRule.get("productPromoRuleId")));
                // using the other method to consolodate cache entries because the same cache is used elsewhere: List productPromoConds = productPromoRule.getRelatedCache("ProductPromoCond", null, UtilMisc.toList("productPromoCondSeqId"));
                if (Debug.verboseOn()) Debug.logVerbose("Checking " + productPromoConds.size() + " conditions for rule " + productPromoRule, module);

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
                    if (Debug.verboseOn()) Debug.logVerbose("Performing " + productPromoActions.size() + " actions for rule " + productPromoRule, module);
                    Iterator productPromoActionIter = UtilMisc.toIterator(productPromoActions);
                    while (productPromoActionIter != null && productPromoActionIter.hasNext()) {
                        GenericValue productPromoAction = (GenericValue) productPromoActionIter.next();

                        // Debug.logInfo("Doing action: " + productPromoAction, module);
                        try {
                            ActionResultInfo actionResultInfo = performAction(productPromoAction, cart, delegator, dispatcher, nowTimestamp);
                            totalDiscountAmount += actionResultInfo.totalDiscountAmount;
                            quantityLeftInActions += actionResultInfo.quantityLeftInAction;
                            
                            // only set if true, don't set back to false: implements OR logic (ie if ANY actions change content, redo loop)
                            boolean actionChangedCart = actionResultInfo.ranAction;
                            if (actionChangedCart) {
                                promoUsed = true;
                                cartChanged = true;
                            }
                        } catch (CartItemModifyException e) {
                            Debug.logError("Error modifying the cart while performing promotion action [" + productPromoAction.getPrimaryKey() + "]: " + e.toString(), module);
                        }
                    }
                }
            }

            if (promoUsed) {
                cart.addProductPromoUse(productPromo.getString("productPromoId"), productPromoCodeId, totalDiscountAmount, quantityLeftInActions);
            } else {
                // the promotion was not used, don't try again until we finish a full pass and come back to see the promo conditions are now satisfied based on changes to the cart
                break;
            }

            
            if (cart.getProductPromoUseCount(productPromoId) > maxUseLimit) {
                throw new UseLimitException("ERROR: While calculating promotions the promotion [" + productPromoId + "] action was applied more than " + maxUseLimit + " times, so the calculation has been ended. This should generally never happen unless you have bad rule definitions.");
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

        if ("PPIP_PRODUCT_AMOUNT".equals(inputParamEnumId)) {
            // for this type of promo force the operatorEnumId = PPC_EQ, effectively ignore that setting because the comparison is implied in the code
            operatorEnumId = "PPC_EQ";
            
            // this type of condition requires items involved to not be involved in any other quantity consuming cond/action, and does not pro-rate the price, just uses the base price
            double amountNeeded = 0.0;
            if (UtilValidate.isNotEmpty(condValue)) {
                amountNeeded = Double.parseDouble(condValue);
            }

            // Debug.logInfo("Doing Amount Cond with Value: " + amountNeeded, module);
            
            Set productIds = ProductPromoWorker.getPromoRuleCondProductIds(productPromoCond, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (amountNeeded > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                GenericValue product = cartItem.getProduct();
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && 
                        (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) && 
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {
                    
                    double basePrice = cartItem.getBasePrice();
                    // get a rough price, round it up to an integer
                    double quantityNeeded = Math.ceil(amountNeeded / basePrice);
                    
                    // reduce amount still needed to qualify for promo (amountNeeded)
                    double quantity = cartItem.addPromoQuantityCandidateUse(quantityNeeded, productPromoCond, false);
                    // get pro-rated amount based on discount
                    amountNeeded -= (quantity * basePrice);
                }
            }

            // Debug.logInfo("Doing Amount Cond with Value after finding applicable cart lines: " + amountNeeded, module);
            
            // if amountNeeded > 0 then the promo condition failed, so remove candidate promo uses and increment the promoQuantityUsed to restore it
            if (amountNeeded > 0) {
                // failed, reset the entire rule, ie including all other conditions that might have been done before
                cart.resetPromoRuleUse(productPromoCond.getString("productPromoId"), productPromoCond.getString("productPromoRuleId"));
                compare = -1;
            } else {
                // we got it, the conditions are in place...
                compare = 0;
                // NOTE: don't confirm promo rule use here, wait until actions are complete for the rule to do that
            }
        } else if ("PPIP_PRODUCT_TOTAL".equals(inputParamEnumId)) {
            // this type of condition allows items involved to be involved in other quantity consuming cond/action, and does pro-rate the price
            Double amountNeeded = Double.valueOf(condValue);
            double amountAvailable = 0;

            // Debug.logInfo("Doing Amount Not Counted Cond with Value: " + amountNeeded, module);
            
            Set productIds = ProductPromoWorker.getPromoRuleCondProductIds(productPromoCond, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                GenericValue product = cartItem.getProduct();
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && 
                        (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) && 
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {
                    
                    // just count the entire sub-total of the item
                    amountAvailable += cartItem.getItemSubTotal();
                }
            }

            // Debug.logInfo("Doing Amount Not Counted Cond with Value after finding applicable cart lines: " + amountNeeded, module);
            
            compare = new Double(amountAvailable).compareTo(amountNeeded);
        } else if ("PPIP_PRODUCT_QUANT".equals(inputParamEnumId)) {
            // for this type of promo force the operatorEnumId = PPC_EQ, effectively ignore that setting because the comparison is implied in the code
            operatorEnumId = "PPC_EQ";
            
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
                GenericValue product = cartItem.getProduct();
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && 
                        (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) && 
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    quantityNeeded -= cartItem.addPromoQuantityCandidateUse(quantityNeeded, productPromoCond, false);
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

            /* These aren't supported yet, ie TODO (low priority)
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
            Double orderSubTotal = new Double(cart.getSubTotalForPromotions());
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
    
    public static class ActionResultInfo {
        public boolean ranAction = false;
        public double totalDiscountAmount = 0;
        public double quantityLeftInAction = 0;
    }

    /** returns true if the cart was changed and rules need to be re-evaluted */
    protected static ActionResultInfo performAction(GenericValue productPromoAction, ShoppingCart cart, GenericDelegator delegator, LocalDispatcher dispatcher, Timestamp nowTimestamp) throws GenericEntityException, CartItemModifyException {
        ActionResultInfo actionResultInfo = new ActionResultInfo();
        
        String productPromoActionEnumId = productPromoAction.getString("productPromoActionEnumId");

        if ("PROMO_GWP".equals(productPromoActionEnumId)) {
            // the code was in there for this, so even though I don't think we want to restrict this, just adding this flag to make it easy to change; could make option dynamic, but now implied by the use limit
            boolean allowMultipleGwp = true;
            
            Integer itemLoc = findPromoItem(productPromoAction, cart);
            if (!allowMultipleGwp && itemLoc != null) {
                if (Debug.verboseOn()) Debug.logVerbose("Not adding promo item, already there; action: " + productPromoAction, module);
                actionResultInfo.ranAction = false;
            } else {
                GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productPromoAction.get("productId")));
                double quantity = productPromoAction.get("quantity") == null ? 0.0 : productPromoAction.getDouble("quantity").doubleValue();
                
                // pass null for cartLocation to add to end of cart, pass false for doPromotions to avoid infinite recursion
                ShoppingCartItem gwpItem = null;
                try {
                    // just leave the prodCatalogId null, this line won't be associated with a catalog
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

                actionResultInfo.ranAction = true;
                actionResultInfo.totalDiscountAmount = discountAmount;
            }
        } else if ("PROMO_FREE_SHIPPING".equals(productPromoActionEnumId)) {
            // this may look a bit funny: on each pass all rules that do free shipping will set their own rule id for it,
            // and on unapply if the promo and rule ids are the same then it will clear it; essentially on any pass
            // through the promos and rules if any free shipping should be there, it will be there
            cart.addFreeShippingProductPromoAction(productPromoAction);
            // don't consider this as a cart change?
            actionResultInfo.ranAction = true;
            // should probably set the totalDiscountAmount to something, but we have no idea what it will be, so leave at 0, will still get run
        } else if ("PROMO_PROD_DISC".equals(productPromoActionEnumId)) {
            double quantityDesired = productPromoAction.get("quantity") == null ? 1.0 : productPromoAction.getDouble("quantity").doubleValue();
            double startingQuantity = quantityDesired;
            double discountAmountTotal = 0;

            Set productIds = ProductPromoWorker.getPromoRuleActionProductIds(productPromoAction, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityDesired > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                GenericValue product = cartItem.getProduct();
                String parentProductId = cartItem.getParentProductId();
                if (!cartItem.getIsPromo() && 
                        (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) &&
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    double quantityUsed = cartItem.addPromoQuantityCandidateUse(quantityDesired, productPromoAction, false);
                    if (quantityUsed > 0) {
                        quantityDesired -= quantityUsed;

                        // create an adjustment and add it to the cartItem that implements the promotion action
                        double percentModifier = productPromoAction.get("amount") == null ? 0.0 : (productPromoAction.getDouble("amount").doubleValue()/100.0);
                        double lineAmount = quantityUsed * cartItem.getBasePrice();
                        double discountAmount = -(lineAmount * percentModifier);
                        discountAmountTotal += discountAmount;
                        // not doing this any more, now distributing among conditions and actions (see call below): doOrderItemPromoAction(productPromoAction, cartItem, discountAmount, "amount", delegator);
                    }
                }
            }

            if (quantityDesired == startingQuantity) {
                // couldn't find any cart items to give a discount to, don't consider action run
                actionResultInfo.ranAction = false;
            } else {
                double totalAmount = getCartItemsUsedTotalAmount(cart, productPromoAction);
                if (Debug.verboseOn()) Debug.logVerbose("Applying promo [" + productPromoAction.getPrimaryKey() + "]\n totalAmount=" + totalAmount + ", discountAmountTotal=" + discountAmountTotal, module);
                distributeDiscountAmount(discountAmountTotal, totalAmount, getCartItemsUsed(cart, productPromoAction), productPromoAction, delegator);
                actionResultInfo.ranAction = true;
                actionResultInfo.totalDiscountAmount = discountAmountTotal;
                actionResultInfo.quantityLeftInAction = quantityDesired;
            }
        } else if ("PROMO_PROD_AMDISC".equals(productPromoActionEnumId)) {
            double quantityDesired = productPromoAction.get("quantity") == null ? 1.0 : productPromoAction.getDouble("quantity").doubleValue();
            double startingQuantity = quantityDesired;
            double discountAmountTotal = 0;
            
            Set productIds = ProductPromoWorker.getPromoRuleActionProductIds(productPromoAction, delegator, nowTimestamp);

            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityDesired > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                String parentProductId = cartItem.getParentProductId();
                GenericValue product = cartItem.getProduct();
                if (!cartItem.getIsPromo() && 
                        (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) &&
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    double quantityUsed = cartItem.addPromoQuantityCandidateUse(quantityDesired, productPromoAction, false);
                    quantityDesired -= quantityUsed;

                    // create an adjustment and add it to the cartItem that implements the promotion action
                    double discount = productPromoAction.get("amount") == null ? 0.0 : productPromoAction.getDouble("amount").doubleValue();
                    // don't allow the discount to be greater than the price
                    if (discount > cartItem.getBasePrice()) {
                        discount = cartItem.getBasePrice();
                    }
                    double discountAmount = -(quantityUsed * discount);
                    discountAmountTotal += discountAmount;
                    // not doing this any more, now distributing among conditions and actions (see call below): doOrderItemPromoAction(productPromoAction, cartItem, discountAmount, "amount", delegator);
                }
            }

            if (quantityDesired == startingQuantity) {
                // couldn't find any cart items to give a discount to, don't consider action run
                actionResultInfo.ranAction = false;
            } else {
                double totalAmount = getCartItemsUsedTotalAmount(cart, productPromoAction);
                if (Debug.verboseOn()) Debug.logVerbose("Applying promo [" + productPromoAction.getPrimaryKey() + "]\n totalAmount=" + totalAmount + ", discountAmountTotal=" + discountAmountTotal, module);
                distributeDiscountAmount(discountAmountTotal, totalAmount, getCartItemsUsed(cart, productPromoAction), productPromoAction, delegator);
                actionResultInfo.ranAction = true;
                actionResultInfo.totalDiscountAmount = discountAmountTotal;
                actionResultInfo.quantityLeftInAction = quantityDesired;
            }
        } else if ("PROMO_PROD_PRICE".equals(productPromoActionEnumId)) {
            // with this we want the set of used items to be one price, so total the price for all used items, subtract the amount we want them to cost, and create an adjustment for what is left
            double quantityDesired = productPromoAction.get("quantity") == null ? 1.0 : productPromoAction.getDouble("quantity").doubleValue();
            double desiredAmount = productPromoAction.get("amount") == null ? 0.0 : productPromoAction.getDouble("amount").doubleValue();
            double totalAmount = 0;

            Set productIds = ProductPromoWorker.getPromoRuleActionProductIds(productPromoAction, delegator, nowTimestamp);

            List cartItemsUsed = new LinkedList();
            List lineOrderedByBasePriceList = cart.getLineListOrderedByBasePrice(false);
            Iterator lineOrderedByBasePriceIter = lineOrderedByBasePriceList.iterator();
            while (quantityDesired > 0 && lineOrderedByBasePriceIter.hasNext()) {
                ShoppingCartItem cartItem = (ShoppingCartItem) lineOrderedByBasePriceIter.next();
                // only include if it is in the productId Set for this check and if it is not a Promo (GWP) item
                String parentProductId = cartItem.getParentProductId();
                GenericValue product = cartItem.getProduct();
                if (!cartItem.getIsPromo() && (productIds.contains(cartItem.getProductId()) || (parentProductId != null && productIds.contains(parentProductId))) &&
                        (product == null || !"N".equals(product.getString("includeInPromotions")))) {
                    // reduce quantity still needed to qualify for promo (quantityNeeded)
                    double quantityUsed = cartItem.addPromoQuantityCandidateUse(quantityDesired, productPromoAction, false);
                    if (quantityUsed > 0) {
                        quantityDesired -= quantityUsed;
                        totalAmount += quantityUsed * cartItem.getBasePrice();
                        cartItemsUsed.add(cartItem);
                    }
                }
            }

            if (totalAmount > desiredAmount && quantityDesired == 0) {
                double discountAmountTotal = -(totalAmount - desiredAmount);
                distributeDiscountAmount(discountAmountTotal, totalAmount, cartItemsUsed, productPromoAction, delegator);
                actionResultInfo.ranAction = true;
                actionResultInfo.totalDiscountAmount = discountAmountTotal;
                // no use setting the quantityLeftInAction because that does not apply for buy X for $Y type promotions, it is all or nothing
            } else {
                actionResultInfo.ranAction = false;
                // clear out any action uses for this so they don't become part of anything else
                cart.resetPromoRuleUse(productPromoAction.getString("productPromoId"), productPromoAction.getString("productPromoRuleId"));
            }
        } else if ("PROMO_ORDER_PERCENT".equals(productPromoActionEnumId)) {
            double percentage = -(productPromoAction.get("amount") == null ? 0.0 : (productPromoAction.getDouble("amount").doubleValue() / 100.0));
            double amount = cart.getSubTotalForPromotions() * percentage;
            if (amount != 0) {
                doOrderPromoAction(productPromoAction, cart, amount, "amount", delegator);
                actionResultInfo.ranAction = true;
                actionResultInfo.totalDiscountAmount = amount;
            }
        } else if ("PROMO_ORDER_AMOUNT".equals(productPromoActionEnumId)) {
            double amount = -(productPromoAction.get("amount") == null ? 0.0 : productPromoAction.getDouble("amount").doubleValue());
            // if amount is greater than the order sub total, set equal to order sub total, this normally wouldn't happen because there should be a condition that the order total be above a certain amount, but just in case...
            double subTotal = cart.getSubTotalForPromotions();
            if (-amount > subTotal) {
                amount = -subTotal;
            }
            if (amount != 0) {
                doOrderPromoAction(productPromoAction, cart, amount, "amount", delegator);
                actionResultInfo.ranAction = true;
                actionResultInfo.totalDiscountAmount = amount;
            }
        } else {
            Debug.logError("An un-supported productPromoActionType was used: " + productPromoActionEnumId + ", not performing any action", module);
            actionResultInfo.ranAction = false;
        }

        if (actionResultInfo.ranAction) {
            // in action, if doesn't have enough quantity to use the promo at all, remove candidate promo uses and increment promoQuantityUsed; this should go for all actions, if any action runs we confirm
            cart.confirmPromoRuleUse(productPromoAction.getString("productPromoId"), productPromoAction.getString("productPromoRuleId"));
        } else {
            cart.resetPromoRuleUse(productPromoAction.getString("productPromoId"), productPromoAction.getString("productPromoRuleId"));
        }

        return actionResultInfo;
    }
    
    protected static List getCartItemsUsed(ShoppingCart cart, GenericValue productPromoAction) {
        List cartItemsUsed = new LinkedList();
        Iterator cartItemsIter = cart.iterator();
        while (cartItemsIter.hasNext()) {
            ShoppingCartItem cartItem = (ShoppingCartItem) cartItemsIter.next();
            double quantityUsed = cartItem.getPromoQuantityCandidateUseActionAndAllConds(productPromoAction);
            if (quantityUsed > 0) {
                cartItemsUsed.add(cartItem);
            }
        }
        return cartItemsUsed;
    }
    
    protected static double getCartItemsUsedTotalAmount(ShoppingCart cart, GenericValue productPromoAction) {
        double totalAmount = 0;
        Iterator cartItemsIter = cart.iterator();
        while (cartItemsIter.hasNext()) {
            ShoppingCartItem cartItem = (ShoppingCartItem) cartItemsIter.next();
            double quantityUsed = cartItem.getPromoQuantityCandidateUseActionAndAllConds(productPromoAction);
            if (quantityUsed > 0) {
                totalAmount += quantityUsed * cartItem.getBasePrice();
            }
        }
        return totalAmount;
    }
    
    protected static void distributeDiscountAmount(double discountAmountTotal, double totalAmount, List cartItemsUsed, GenericValue productPromoAction, GenericDelegator delegator) {
        double discountAmount = discountAmountTotal;
        // distribute the discount evenly weighted according to price over the order items that the individual quantities came from; avoids a number of issues with tax/shipping calc, inclusion in the sub-total for other promotions, etc
        Iterator cartItemsUsedIter = cartItemsUsed.iterator();
        while (cartItemsUsedIter.hasNext()) {
            ShoppingCartItem cartItem = (ShoppingCartItem) cartItemsUsedIter.next();
            // to minimize rounding issues use the remaining total for the last one, otherwise use a calculated value
            if (cartItemsUsedIter.hasNext()) {
                double quantityUsed = cartItem.getPromoQuantityCandidateUseActionAndAllConds(productPromoAction);
                double ratioOfTotal = (quantityUsed * cartItem.getBasePrice()) / totalAmount;
                double weightedAmount = ratioOfTotal * discountAmountTotal;
                // round the weightedAmount to 2 decimal places, ie a whole number of cents or 2 decimal place monetary units
                weightedAmount = weightedAmount * 100.0;
                long roundedAmount = Math.round(weightedAmount);
                weightedAmount = ((double) roundedAmount) / 100.0;
                discountAmount -= weightedAmount;
                doOrderItemPromoAction(productPromoAction, cartItem, weightedAmount, "amount", delegator);
            } else {
                // last one, just use discountAmount
                doOrderItemPromoAction(productPromoAction, cartItem, discountAmount, "amount", delegator);
            }
        }
        // this is the old way that causes problems: doOrderPromoAction(productPromoAction, cart, discountAmount, "amount", delegator);
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
        Map productCategoryGroupSetListMap = new HashMap();
        
        Iterator productPromoCategoryIter = productPromoCategories.iterator();
        while (productPromoCategoryIter.hasNext()) {
            GenericValue productPromoCategory = (GenericValue) productPromoCategoryIter.next();
            if (productPromoApplEnumId.equals(productPromoCategory.getString("productPromoApplEnumId"))) {
                Set tempCatIdSet = new HashSet();
                if ("Y".equals(productPromoCategory.getString("includeSubCategories"))) {
                    ProductSearch.getAllSubCategoryIds(productPromoCategory.getString("productCategoryId"), tempCatIdSet, delegator, nowTimestamp);
                } else {
                    tempCatIdSet.add(productPromoCategory.getString("productCategoryId"));
                }
                
                String andGroupId = productPromoCategory.getString("andGroupId");
                if ("_NA_".equals(andGroupId)) {
                    productCategoryIds.addAll(tempCatIdSet);
                } else {
                    List catIdSetList = (List) productCategoryGroupSetListMap.get(andGroupId);
                    if (catIdSetList == null) {
                        catIdSetList = new LinkedList();
                    }
                    catIdSetList.add(tempCatIdSet);
                }
            }
        }
        
        // for the ones with andGroupIds, if there is only one category move it to the productCategoryIds Set
        // also remove all empty SetLists and Sets
        Iterator pcgslmeIter = productCategoryGroupSetListMap.entrySet().iterator();
        while (pcgslmeIter.hasNext()) {
            Map.Entry entry = (Map.Entry) pcgslmeIter.next();
            List catIdSetList = (List) entry.getValue();
            if (catIdSetList.size() == 0) {
                pcgslmeIter.remove();
            } else if (catIdSetList.size() == 1) {
                Set catIdSet = (Set) catIdSetList.iterator().next();
                if (catIdSet.size() == 0) {
                    pcgslmeIter.remove();
                } else {
                    // if there is only one set in the list since the set will be or'ed anyway, just add them all to the productCategoryIds Set
                    productCategoryIds.addAll(catIdSet);
                    pcgslmeIter.remove();
                }
            }
        }

        // now that the category Set and Map are setup, take care of the productCategoryIds Set first
        getAllProductIds(productCategoryIds, productIds, delegator, nowTimestamp, include);
        
        // now handle the productCategoryGroupSetListMap
        // if a set has more than one category (because of an include sub-cats) then do an or
        // all lists will have more than category because of the pre-pass that was done, so and them together
        Iterator pcgslmIter = productCategoryGroupSetListMap.entrySet().iterator();
        while (pcgslmIter.hasNext()) {
            Map.Entry entry = (Map.Entry) pcgslmIter.next();
            List catIdSetList = (List) entry.getValue();
            // get all productIds for this catIdSetList
            List productIdSetList = new LinkedList();
            
            Iterator cidslIter = catIdSetList.iterator();
            while (cidslIter.hasNext()) {
                // make a Set of productIds including all ids from all categories
                Set catIdSet = (Set) cidslIter.next();
                Set groupProductIdSet = new HashSet();
                getAllProductIds(catIdSet, groupProductIdSet, delegator, nowTimestamp, true);
                productIdSetList.add(groupProductIdSet);
            }
            
            // now go through all productId sets and only include IDs that are in all sets
            // by definition if each id must be in all categories, then it must be in the first, so go through the first and drop each one that is not in all others
            Set firstProductIdSet = (Set) productIdSetList.remove(0);
            Iterator productIdSetIter = productIdSetList.iterator();
            while (productIdSetIter.hasNext()) {
                Set productIdSet = (Set) productIdSetIter.next();
                firstProductIdSet.retainAll(productIdSet);
            }
            
            /* the old way of doing it, not as efficient, recoded above using the retainAll operation, pretty handy
            Iterator firstProductIdIter = firstProductIdSet.iterator();
            while (firstProductIdIter.hasNext()) {
                String curProductId = (String) firstProductIdIter.next();
                
                boolean allContainProductId = true;
                Iterator productIdSetIter = productIdSetList.iterator();
                while (productIdSetIter.hasNext()) {
                    Set productIdSet = (Set) productIdSetIter.next();
                    if (!productIdSet.contains(curProductId)) {
                        allContainProductId = false;
                        break;
                    }
                }
                
                if (!allContainProductId) {
                    firstProductIdIter.remove();
                }
            }
             */
            
            if (firstProductIdSet.size() >= 0) {
                if (include) {
                    productIds.addAll(firstProductIdSet);
                } else {
                    productIds.removeAll(firstProductIdSet);
                }
            }
        }
    }
    
    protected static void getAllProductIds(Set productCategoryIdSet, Set productIdSet, GenericDelegator delegator, Timestamp nowTimestamp, boolean include) throws GenericEntityException {
        Iterator productCategoryIdIter = productCategoryIdSet.iterator();
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
                    productIdSet.add(productId);
                } else {
                    productIdSet.remove(productId);
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
    
    protected static class UseLimitException extends Exception {
        public UseLimitException(String str) {
            super(str);
        }
    }
}
