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
package org.ofbiz.commonapp.product.price;

import java.util.*;
import java.sql.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

import org.ofbiz.commonapp.product.product.*;

/**
 * PriceServices - Workers and Services class for product price related functionality
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class PriceServices {
    public static final String module = PriceServices.class.getName();

    /**
     * <p>Calculates the price of a product from pricing rules given the following input, and of course access to the database:</p>
     * <ul>
     *   <li>productId
     *   <li>partyId
     *   <li>prodCatalogId
     *   <li>webSiteId
     *   <li>facilityGroupId
     *   <li>quantity
     * </ul>
     */
    public static Map calculateProductPrice(DispatchContext dctx, Map context) {
        boolean optimizeForLargeRuleSet = false;

        // UtilTimer utilTimer = new UtilTimer();
        // utilTimer.timerString("Starting price calc", module);
        // utilTimer.setLog(false);

        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        boolean isSale = false;
        List orderItemPriceInfos = new LinkedList();

        GenericValue product = (GenericValue) context.get("product");
        String productId = product.getString("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        String webSiteId = (String) context.get("webSiteId");
        
        String facilityGroupId = (String) context.get("facilityGroupId");
        if (UtilValidate.isEmpty(facilityGroupId)) facilityGroupId = "_NA_";

        // if currency uom is null get from properties file, if still null assume USD (USD: American Dollar) for now
        String currencyUomId = (String) context.get("currencyUomId");

        if (currencyUomId == null || currencyUomId.length() == 0) {
            currencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD");
        }

        // if this product is variant, find the virtual product and apply checks to it as well
        String virtualProductId = null;

        try {
            virtualProductId = ProductWorker.getVariantVirtualId(product);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error getting virtual product id from the database while calculating price", module);
            return ServiceUtil.returnError("Error getting virtual product id from the database while calculating price: " + e.toString());
        }

        // get prices for virtual product if one is found; get all ProductPrice entities for this productId and currencyUomId
        List virtualProductPrices = null;
        if (virtualProductId != null) {
            try {
                virtualProductPrices = delegator.findByAndCache("ProductPrice", UtilMisc.toMap("productId", virtualProductId, "currencyUomId", currencyUomId, "facilityGroupId", facilityGroupId), UtilMisc.toList("-fromDate"));
            } catch (GenericEntityException e) {
                Debug.logError(e, "An error occurred while getting the product prices", module);
            }
            virtualProductPrices = EntityUtil.filterByDate(virtualProductPrices, true);
        }

        // NOTE: partyId CAN be null
        String partyId = (String) context.get("partyId");

        if (partyId == null && context.get("userLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            partyId = userLogin.getString("partyId");
        }

        // check for auto-userlogin for price rules
        if (partyId == null && context.get("autoUserLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("autoUserLogin");

            partyId = userLogin.getString("partyId");
        }

        Double quantityDbl = (Double) context.get("quantity");

        if (quantityDbl == null) quantityDbl = new Double(1.0);
        double quantity = quantityDbl.doubleValue();

        // for prices, get all ProductPrice entities for this productId and currencyUomId
        List productPrices = null;

        try {
            productPrices = delegator.findByAndCache("ProductPrice", UtilMisc.toMap("productId", productId, "currencyUomId", currencyUomId, "facilityGroupId", facilityGroupId), UtilMisc.toList("-fromDate"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "An error occurred while getting the product prices", module);
        }
        productPrices = EntityUtil.filterByDate(productPrices, true);

        // ===== get the prices we need: list, default, average cost, promo, min, max =====
        List listPrices = EntityUtil.filterByAnd(productPrices, UtilMisc.toMap("productPriceTypeId", "LIST_PRICE"));
        GenericValue listPriceValue = EntityUtil.getFirst(listPrices);

        if (listPrices != null && listPrices.size() > 1) {
            Debug.logWarning("There is more than one LIST_PRICE with the currencyUomId " + currencyUomId + " and productId " + productId + ", using the latest found with price: " + listPriceValue.getDouble("price"));
        }

        List defaultPrices = EntityUtil.filterByAnd(productPrices, UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
        GenericValue defaultPriceValue = EntityUtil.getFirst(defaultPrices);

        if (defaultPrices != null && defaultPrices.size() > 1) {
            Debug.logWarning("There is more than one DEFAULT_PRICE with the currencyUomId " + currencyUomId + " and productId " + productId + ", using the latest found with price: " + defaultPriceValue.getDouble("price"));
        }

        List averageCosts = EntityUtil.filterByAnd(productPrices, UtilMisc.toMap("productPriceTypeId", "AVERAGE_COST"));
        GenericValue averageCostValue = EntityUtil.getFirst(averageCosts);

        if (averageCosts != null && averageCosts.size() > 1) {
            Debug.logWarning("There is more than one AVERAGE_COST with the currencyUomId " + currencyUomId + " and productId " + productId + ", using the latest found with price: " + averageCostValue.getDouble("price"));
        }

        List promoPrices = EntityUtil.filterByAnd(productPrices, UtilMisc.toMap("productPriceTypeId", "PROMO_PRICE"));
        GenericValue promoPriceValue = EntityUtil.getFirst(promoPrices);

        if (promoPrices != null && promoPrices.size() > 1) {
            Debug.logWarning("There is more than one PROMO_PRICE with the currencyUomId " + currencyUomId + " and productId " + productId + ", using the latest found with price: " + promoPriceValue.getDouble("price"));
        }

        List minimumPrices = EntityUtil.filterByAnd(productPrices, UtilMisc.toMap("productPriceTypeId", "MINIMUM_PRICE"));
        GenericValue minimumPriceValue = EntityUtil.getFirst(minimumPrices);

        if (minimumPrices != null && minimumPrices.size() > 1) {
            Debug.logWarning("There is more than one MINIMUM_PRICE with the currencyUomId " + currencyUomId + " and productId " + productId + ", using the latest found with price: " + minimumPriceValue.getDouble("price"));
        }

        List maximumPrices = EntityUtil.filterByAnd(productPrices, UtilMisc.toMap("productPriceTypeId", "MAXIMUM_PRICE"));
        GenericValue maximumPriceValue = EntityUtil.getFirst(maximumPrices);

        if (maximumPrices != null && maximumPrices.size() > 1) {
            Debug.logWarning("There is more than one MAXIMUM_PRICE with the currencyUomId " + currencyUomId + " and productId " + productId + ", using the latest found with price: " + maximumPriceValue.getDouble("price"));
        }

        // if any of these prices is missing and this product is a variant, default to the corresponding price on the virtual product
        if (virtualProductPrices != null && virtualProductPrices.size() > 0) {
            if (listPriceValue == null) {
                List virtualTempPrices = EntityUtil.filterByAnd(virtualProductPrices, UtilMisc.toMap("productPriceTypeId", "LIST_PRICE"));

                listPriceValue = EntityUtil.getFirst(virtualTempPrices);
                if (virtualTempPrices != null && virtualTempPrices.size() > 1) {
                    Debug.logWarning("There is more than one LIST_PRICE with the currencyUomId " + currencyUomId + " and productId " + virtualProductId + ", using the latest found with price: " + listPriceValue.getDouble("price"));
                }
            }
            if (defaultPriceValue == null) {
                List virtualTempPrices = EntityUtil.filterByAnd(virtualProductPrices, UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));

                defaultPriceValue = EntityUtil.getFirst(virtualTempPrices);
                if (virtualTempPrices != null && virtualTempPrices.size() > 1) {
                    Debug.logWarning("There is more than one DEFAULT_PRICE with the currencyUomId " + currencyUomId + " and productId " + virtualProductId + ", using the latest found with price: " + defaultPriceValue.getDouble("price"));
                }
            }
            if (averageCostValue == null) {
                List virtualTempPrices = EntityUtil.filterByAnd(virtualProductPrices, UtilMisc.toMap("productPriceTypeId", "AVERAGE_COST"));

                averageCostValue = EntityUtil.getFirst(virtualTempPrices);
                if (virtualTempPrices != null && virtualTempPrices.size() > 1) {
                    Debug.logWarning("There is more than one AVERAGE_COST with the currencyUomId " + currencyUomId + " and productId " + virtualProductId + ", using the latest found with price: " + averageCostValue.getDouble("price"));
                }
            }
            if (promoPriceValue == null) {
                List virtualTempPrices = EntityUtil.filterByAnd(virtualProductPrices, UtilMisc.toMap("productPriceTypeId", "PROMO_PRICE"));

                promoPriceValue = EntityUtil.getFirst(virtualTempPrices);
                if (virtualTempPrices != null && virtualTempPrices.size() > 1) {
                    Debug.logWarning("There is more than one PROMO_PRICE with the currencyUomId " + currencyUomId + " and productId " + virtualProductId + ", using the latest found with price: " + promoPriceValue.getDouble("price"));
                }
            }
            if (minimumPriceValue == null) {
                List virtualTempPrices = EntityUtil.filterByAnd(virtualProductPrices, UtilMisc.toMap("productPriceTypeId", "MINIMUM_PRICE"));

                minimumPriceValue = EntityUtil.getFirst(virtualTempPrices);
                if (virtualTempPrices != null && virtualTempPrices.size() > 1) {
                    Debug.logWarning("There is more than one MINIMUM_PRICE with the currencyUomId " + currencyUomId + " and productId " + virtualProductId + ", using the latest found with price: " + minimumPriceValue.getDouble("price"));
                }
            }
            if (maximumPriceValue == null) {
                List virtualTempPrices = EntityUtil.filterByAnd(virtualProductPrices, UtilMisc.toMap("productPriceTypeId", "MAXIMUM_PRICE"));

                maximumPriceValue = EntityUtil.getFirst(virtualTempPrices);
                if (virtualTempPrices != null && virtualTempPrices.size() > 1) {
                    Debug.logWarning("There is more than one MAXIMUM_PRICE with the currencyUomId " + currencyUomId + " and productId " + virtualProductId + ", using the latest found with price: " + maximumPriceValue.getDouble("price"));
                }
            }
        }

        double promoPrice = (promoPriceValue != null && promoPriceValue.get("price") != null) ? promoPriceValue.getDouble("price").doubleValue() : 0;
        double defaultPrice = (defaultPriceValue != null && defaultPriceValue.get("price") != null) ? defaultPriceValue.getDouble("price").doubleValue() : 0;
        Double listPriceDbl = listPriceValue != null ? listPriceValue.getDouble("price") : null;

        if (listPriceDbl == null) {
            // no list price, use defaultPrice for the final price

            // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
            Double maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getDouble("price") : null;

            if (maxSellPrice != null && defaultPrice > maxSellPrice.doubleValue()) {
                defaultPrice = maxSellPrice.doubleValue();
            }
            // min price second to override max price, safety net
            Double minSellPrice = minimumPriceValue != null ? minimumPriceValue.getDouble("price") : null;

            if (minSellPrice != null && defaultPrice < minSellPrice.doubleValue()) {
                defaultPrice = minSellPrice.doubleValue();
            }

            result.put("price", new Double(defaultPrice));
            result.put("defaultPrice", new Double(defaultPrice));
            result.put("averageCost", averageCostValue != null ? averageCostValue.getDouble("price") : null);
        } else {
            try {
                // get some of the base values to calculate with
                double listPrice = listPriceDbl.doubleValue();
                double averageCost = (averageCostValue != null && averageCostValue.get("price") != null) ? averageCostValue.getDouble("price").doubleValue() : listPrice;
                double margin = listPrice - averageCost;

                // calculate running sum based on listPrice and rules found
                double price = listPrice;

                Collection productPriceRules = null;

                // At this point we have two options: optimize for large ruleset, or optimize for small ruleset
                // NOTE: This only effects the way that the rules to be evaluated are selected.
                // For large rule sets we can do a cached pre-filter to limit the rules that need to be evaled for a specific product.
                // Genercally I don't think that rule sets will get that big though, so the default is optimize for smaller rule set.
                if (optimizeForLargeRuleSet) {
                    // ========= find all rules that must be run for each input type; this is kind of like a pre-filter to slim down the rules to run =========
                    // utilTimer.timerString("Before create rule id list", module);
                    TreeSet productPriceRuleIds = new TreeSet();

                    // ------- These are all of the conditions that DON'T depend on the current inputs -------

                    // by productCategoryId
                    // for we will always include any rules that go by category, shouldn't be too many to iterate through each time and will save on cache entries
                    // note that we always want to put the category, quantity, etc ones that find all rules with these conditions in separate cache lists so that they can be easily cleared
                    Collection productCategoryIdConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CAT_ID"));

                    if (productCategoryIdConds != null && productCategoryIdConds.size() > 0) {
                        Iterator productCategoryIdCondsIter = productCategoryIdConds.iterator();

                        while (productCategoryIdCondsIter.hasNext()) {
                            GenericValue productCategoryIdCond = (GenericValue) productCategoryIdCondsIter.next();

                            productPriceRuleIds.add(productCategoryIdCond.getString("productPriceRuleId"));
                        }
                    }

                    // by quantity -- should we really do this one, ie is it necessary?
                    // we could say that all rules with quantity on them must have one of these other values
                    // but, no we'll do it the other way, any that have a quantity will always get compared
                    Collection quantityConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_QUANTITY"));

                    if (quantityConds != null && quantityConds.size() > 0) {
                        Iterator quantityCondsIter = quantityConds.iterator();

                        while (quantityCondsIter.hasNext()) {
                            GenericValue quantityCond = (GenericValue) quantityCondsIter.next();

                            productPriceRuleIds.add(quantityCond.getString("productPriceRuleId"));
                        }
                    }

                    // by roleTypeId
                    Collection roleTypeIdConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_ROLE_TYPE"));

                    if (roleTypeIdConds != null && roleTypeIdConds.size() > 0) {
                        Iterator roleTypeIdCondsIter = roleTypeIdConds.iterator();

                        while (roleTypeIdCondsIter.hasNext()) {
                            GenericValue roleTypeIdCond = (GenericValue) roleTypeIdCondsIter.next();

                            productPriceRuleIds.add(roleTypeIdCond.getString("productPriceRuleId"));
                        }
                    }

                    // TODO, not supported yet: by groupPartyId
                    // TODO, not supported yet: by partyClassificationGroupId
                    // later: (by partyClassificationTypeId)

                    // by listPrice
                    Collection listPriceConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_LIST_PRICE"));

                    if (listPriceConds != null && listPriceConds.size() > 0) {
                        Iterator listPriceCondsIter = listPriceConds.iterator();

                        while (listPriceCondsIter.hasNext()) {
                            GenericValue listPriceCond = (GenericValue) listPriceCondsIter.next();

                            productPriceRuleIds.add(listPriceCond.getString("productPriceRuleId"));
                        }
                    }

                    // ------- These are all of them that DO depend on the current inputs -------

                    // by productId
                    Collection productIdConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", productId));

                    if (productIdConds != null && productIdConds.size() > 0) {
                        Iterator productIdCondsIter = productIdConds.iterator();

                        while (productIdCondsIter.hasNext()) {
                            GenericValue productIdCond = (GenericValue) productIdCondsIter.next();

                            productPriceRuleIds.add(productIdCond.getString("productPriceRuleId"));
                        }
                    }

                    // by virtualProductId, if not null
                    if (virtualProductId != null) {
                        Collection virtualProductIdConds = delegator.findByAndCache("ProductPriceCond",
                                UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", virtualProductId));

                        if (virtualProductIdConds != null && virtualProductIdConds.size() > 0) {
                            Iterator virtualProductIdCondsIter = virtualProductIdConds.iterator();

                            while (virtualProductIdCondsIter.hasNext()) {
                                GenericValue virtualProductIdCond = (GenericValue) virtualProductIdCondsIter.next();

                                productPriceRuleIds.add(virtualProductIdCond.getString("productPriceRuleId"));
                            }
                        }
                    }

                    // by prodCatalogId
                    Collection prodCatalogIdConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CLG_ID", "condValue", prodCatalogId));

                    if (prodCatalogIdConds != null && prodCatalogIdConds.size() > 0) {
                        Iterator prodCatalogIdCondsIter = prodCatalogIdConds.iterator();

                        while (prodCatalogIdCondsIter.hasNext()) {
                            GenericValue prodCatalogIdCond = (GenericValue) prodCatalogIdCondsIter.next();

                            productPriceRuleIds.add(prodCatalogIdCond.getString("productPriceRuleId"));
                        }
                    }
                    
                    // by webSiteId
                    Collection webSiteIdConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_WEBSITE_ID", "condValue", webSiteId));
                    
                    if (webSiteIdConds != null && webSiteIdConds.size() > 0) {
                        Iterator webSiteIdCondsIter = webSiteIdConds.iterator();
                        
                        while (webSiteIdCondsIter.hasNext()) {
                            GenericValue webSiteIdCond = (GenericValue) webSiteIdCondsIter.next();
                            
                            productPriceRuleIds.add(webSiteIdCond.getString("productPriceRuleId"));
                        }
                    }
                   
                    // by partyId
                    if (partyId != null) {
                        Collection partyIdConds = delegator.findByAndCache("ProductPriceCond",
                                UtilMisc.toMap("inputParamEnumId", "PRIP_PARTY_ID", "condValue", partyId));

                        if (partyIdConds != null && partyIdConds.size() > 0) {
                            Iterator partyIdCondsIter = partyIdConds.iterator();

                            while (partyIdCondsIter.hasNext()) {
                                GenericValue partyIdCond = (GenericValue) partyIdCondsIter.next();

                                productPriceRuleIds.add(partyIdCond.getString("productPriceRuleId"));
                            }
                        }
                    }

                    // by currencyUomId
                    Collection currencyUomIdConds = delegator.findByAndCache("ProductPriceCond",
                            UtilMisc.toMap("inputParamEnumId", "PRIP_CURRENCY_UOMID", "condValue", currencyUomId));

                    if (currencyUomIdConds != null && currencyUomIdConds.size() > 0) {
                        Iterator currencyUomIdCondsIter = currencyUomIdConds.iterator();

                        while (currencyUomIdCondsIter.hasNext()) {
                            GenericValue currencyUomIdCond = (GenericValue) currencyUomIdCondsIter.next();

                            productPriceRuleIds.add(currencyUomIdCond.getString("productPriceRuleId"));
                        }
                    }

                    productPriceRules = new LinkedList();
                    Iterator productPriceRuleIdsIter = productPriceRuleIds.iterator();

                    while (productPriceRuleIdsIter.hasNext()) {
                        String productPriceRuleId = (String) productPriceRuleIdsIter.next();
                        GenericValue productPriceRule = delegator.findByPrimaryKeyCache("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));

                        if (productPriceRule == null) continue;
                        productPriceRules.add(productPriceRule);
                    }
                } else {
                    // this would be nice, but we can't cache this so easily...
                    // List pprExprs = UtilMisc.toList(new EntityExpr("thruDate", EntityOperator.EQUALS, null),
                    // new EntityExpr("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
                    // productPriceRules = delegator.findByOr("ProductPriceRule", pprExprs);

                    productPriceRules = delegator.findAllCache("ProductPriceRule");
                    if (productPriceRules == null) productPriceRules = new LinkedList();
                }

                // ========= go through each price rule by id and eval all conditions =========
                // utilTimer.timerString("Before eval rules", module);
                int totalConds = 0;
                int totalActions = 0;
                int totalRules = 0;

                Iterator productPriceRulesIter = productPriceRules.iterator();

                while (productPriceRulesIter.hasNext()) {
                    GenericValue productPriceRule = (GenericValue) productPriceRulesIter.next();
                    String productPriceRuleId = productPriceRule.getString("productPriceRuleId");

                    // check from/thru dates
                    java.sql.Timestamp fromDate = productPriceRule.getTimestamp("fromDate");
                    java.sql.Timestamp thruDate = productPriceRule.getTimestamp("thruDate");

                    if (fromDate != null && fromDate.after(nowTimestamp)) {
                        // hasn't started yet
                        continue;
                    }
                    if (thruDate != null && thruDate.before(nowTimestamp)) {
                        // already expired
                        continue;
                    }

                    // check all conditions
                    boolean allTrue = true;
                    StringBuffer condsDescription = new StringBuffer();
                    Collection productPriceConds = delegator.findByAndCache("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
                    Iterator productPriceCondsIter = UtilMisc.toIterator(productPriceConds);

                    while (productPriceCondsIter != null && productPriceCondsIter.hasNext()) {
                        GenericValue productPriceCond = (GenericValue) productPriceCondsIter.next();

                        totalConds++;

                        if (!checkPriceCondition(productPriceCond, productId, prodCatalogId, webSiteId, partyId, quantity, listPrice, currencyUomId, delegator)) {
                            // if there is a virtualProductId, try that given that this one has failed
                            if (virtualProductId != null) {
                                if (!checkPriceCondition(productPriceCond, virtualProductId, prodCatalogId, webSiteId, partyId, quantity, listPrice, currencyUomId, delegator)) {
                                    allTrue = false;
                                    break;
                                }
                                // otherwise, okay, this one made it so carry on checking
                            } else {
                                allTrue = false;
                                break;
                            }
                        }

                        // add condsDescription string entry
                        condsDescription.append("[");
                        GenericValue inputParamEnum = productPriceCond.getRelatedOneCache("InputParamEnumeration");

                        condsDescription.append(inputParamEnum.getString("enumCode"));
                        // condsDescription.append(":");
                        GenericValue operatorEnum = productPriceCond.getRelatedOneCache("OperatorEnumeration");

                        condsDescription.append(operatorEnum.getString("description"));
                        // condsDescription.append(":");
                        condsDescription.append(productPriceCond.getString("condValue"));
                        condsDescription.append("] ");
                    }

                    // add some info about the prices we are calculating from
                    condsDescription.append("[list:");
                    condsDescription.append(listPrice);
                    condsDescription.append(";avgCost:");
                    condsDescription.append(averageCost);
                    condsDescription.append(";margin:");
                    condsDescription.append(margin);
                    condsDescription.append("] ");

                    boolean foundFlatOverride = false;

                    // if all true, perform all actions
                    if (allTrue) {
                        // check isSale
                        if ("Y".equals(productPriceRule.getString("isSale"))) {
                            isSale = true;
                        }

                        Collection productPriceActions = delegator.findByAndCache("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
                        Iterator productPriceActionsIter = UtilMisc.toIterator(productPriceActions);

                        while (productPriceActionsIter != null && productPriceActionsIter.hasNext()) {
                            GenericValue productPriceAction = (GenericValue) productPriceActionsIter.next();

                            totalActions++;

                            // yeah, finally here, perform the action, ie, modify the price
                            double modifyAmount = 0;

                            if ("PRICE_POL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = listPrice * (productPriceAction.getDouble("amount").doubleValue() / 100.0);
                                }
                            } else if ("PRICE_POAC".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = averageCost * (productPriceAction.getDouble("amount").doubleValue() / 100.0);
                                }
                            } else if ("PRICE_POM".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = margin * (productPriceAction.getDouble("amount").doubleValue() / 100.0);
                                }
                            } else if ("PRICE_FOL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = productPriceAction.getDouble("amount").doubleValue();
                                }
                            } else if ("PRICE_FLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                // this one is a bit different, break out of the loop because we now have our final price
                                foundFlatOverride = true;
                                if (productPriceAction.get("amount") != null) {
                                    price = productPriceAction.getDouble("amount").doubleValue();
                                } else {
                                    Debug.logError("ERROR: ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                    price = defaultPrice;
                                }
                            } else if ("PRICE_PFLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                // this one is a bit different too, break out of the loop because we now have our final price
                                foundFlatOverride = true;
                                price = promoPrice;
                                if (productPriceAction.get("amount") != null) {
                                    price += productPriceAction.getDouble("amount").doubleValue();
                                }
                                if (price == 0.00) {
                                    Debug.logError("ERROR: PromoPrice and ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                    price = defaultPrice;
                                }
                            }

                            // add a orderItemPriceInfo element too, without orderId or orderItemId
                            StringBuffer priceInfoDescription = new StringBuffer();

                            priceInfoDescription.append(condsDescription.toString());
                            priceInfoDescription.append("[type:");
                            priceInfoDescription.append(productPriceAction.getString("productPriceActionTypeId"));
                            priceInfoDescription.append("]");

                            GenericValue orderItemPriceInfo = delegator.makeValue("OrderItemPriceInfo", null);

                            orderItemPriceInfo.set("productPriceRuleId", productPriceAction.get("productPriceRuleId"));
                            orderItemPriceInfo.set("productPriceActionSeqId", productPriceAction.get("productPriceActionSeqId"));
                            orderItemPriceInfo.set("modifyAmount", new Double(modifyAmount));
                            // make sure description is <= than 250 chars
                            String priceInfoDescriptionString = priceInfoDescription.toString();

                            if (priceInfoDescriptionString.length() > 250) {
                                priceInfoDescriptionString = priceInfoDescriptionString.substring(0, 250);
                            }
                            orderItemPriceInfo.set("description", priceInfoDescriptionString);
                            orderItemPriceInfos.add(orderItemPriceInfo);

                            if (foundFlatOverride) {
                                break;
                            } else {
                                price += modifyAmount;
                            }
                        }
                    }

                    totalRules++;

                    if (foundFlatOverride) {
                        break;
                    }
                }

                if (Debug.verboseOn()) {
                    Debug.logVerbose("Unchecked Calculated price: " + price, module);
                    Debug.logVerbose("PriceInfo:", module);
                    Iterator orderItemPriceInfosIter = orderItemPriceInfos.iterator();

                    while (orderItemPriceInfosIter.hasNext()) {
                        GenericValue orderItemPriceInfo = (GenericValue) orderItemPriceInfosIter.next();

                        Debug.logVerbose(" --- " + orderItemPriceInfo.toString(), module);
                    }
                }

                // if no actions were run on the list price, then use the default price
                if (totalActions == 0) {
                    price = defaultPrice;
                }

                // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
                Double maxSellPrice = maximumPriceValue != null ? maximumPriceValue.getDouble("price") : null;

                if (maxSellPrice != null && price > maxSellPrice.doubleValue()) {
                    price = maxSellPrice.doubleValue();
                }
                // min price second to override max price, safety net
                Double minSellPrice = minimumPriceValue != null ? minimumPriceValue.getDouble("price") : null;

                if (minSellPrice != null && price < minSellPrice.doubleValue()) {
                    price = minSellPrice.doubleValue();
                }

                if (Debug.verboseOn()) Debug.logVerbose("Final Calculated price: " + price + ", rules: " + totalRules + ", conds: " + totalConds + ", actions: " + totalActions, module);

                result.put("price", new Double(price));
                result.put("listPrice", new Double(listPrice));
                result.put("defaultPrice", new Double(defaultPrice));
                result.put("averageCost", new Double(averageCost));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting rules from the database while calculating price", module);
                return ServiceUtil.returnError("Error getting rules from the database while calculating price: " + e.toString());
            }
        }

        result.put("orderItemPriceInfos", orderItemPriceInfos);
        result.put("isSale", new Boolean(isSale));
        // utilTimer.timerString("Finished price calc [productId=" + productId + "]", module);
        return result;
    }

    public static boolean checkPriceCondition(GenericValue productPriceCond, String productId, String prodCatalogId, String webSiteId,
            String partyId, double quantity, double listPrice, String currencyUomId, GenericDelegator delegator) throws GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("Checking price condition: " + productPriceCond, module);
        int compare = 0;

        if ("PRIP_PRODUCT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = productId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_PROD_CAT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            // if a ProductCategoryMember exists for this productId and the specified productCategoryId
            List productCategoryMembers = delegator.findByAndCache("ProductCategoryMember",
                    UtilMisc.toMap("productId", productId, "productCategoryId", productPriceCond.getString("condValue")));

            // and from/thru date within range
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
            // then 0 (equals), otherwise 1 (not equals)
            if (productCategoryMembers != null && productCategoryMembers.size() > 0) {
                compare = 0;
            } else {
                compare = 1;
            }
        } else if ("PRIP_PROD_CLG_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = prodCatalogId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_WEBSITE_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = webSiteId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_QUANTITY".equals(productPriceCond.getString("inputParamEnumId"))) {
            Double quantityValue = new Double(quantity);

            compare = quantityValue.compareTo(Double.valueOf(productPriceCond.getString("condValue")));
        } else if ("PRIP_PARTY_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (partyId != null) {
                compare = partyId.compareTo(productPriceCond.getString("condValue"));
            } else {
                compare = 1;
            }

            /* These aren't supported yet, ie TODO
             } else if ("PRIP_PARTY_GRP_MEM".equals(productPriceCond.getString("inputParamEnumId"))) {
             } else if ("PRIP_PARTY_CLASS".equals(productPriceCond.getString("inputParamEnumId"))) {
             */
        } else if ("PRIP_ROLE_TYPE".equals(productPriceCond.getString("inputParamEnumId"))) {
            if (partyId != null) {
                // if a PartyRole exists for this partyId and the specified roleTypeId
                GenericValue partyRole = delegator.findByPrimaryKeyCache("PartyRole",
                        UtilMisc.toMap("partyId", partyId, "roleTypeId", productPriceCond.getString("condValue")));

                // then 0 (equals), otherwise 1 (not equals)
                if (partyRole != null) {
                    compare = 0;
                } else {
                    compare = 1;
                }
            } else {
                compare = 1;
            }
        } else if ("PRIP_LIST_PRICE".equals(productPriceCond.getString("inputParamEnumId"))) {
            Double listPriceValue = new Double(listPrice);

            compare = listPriceValue.compareTo(Double.valueOf(productPriceCond.getString("condValue")));
        } else if ("PRIP_CURRENCY_UOMID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = currencyUomId.compareTo(productPriceCond.getString("condValue"));
        } else {
            Debug.logWarning("An un-supported productPriceCond input parameter (lhs) was used: " + productPriceCond.getString("inputParamEnumId") + ", returning false, ie check failed", module);
            return false;
        }

        if (Debug.verboseOn()) Debug.logVerbose("Price Condition compare done, compare=" + compare, module);

        if ("PRC_EQ".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare == 0) return true;
        } else if ("PRC_NEQ".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare != 0) return true;
        } else if ("PRC_LT".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare < 0) return true;
        } else if ("PRC_LTE".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare <= 0) return true;
        } else if ("PRC_GT".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare > 0) return true;
        } else if ("PRC_GTE".equals(productPriceCond.getString("operatorEnumId"))) {
            if (compare >= 0) return true;
        } else {
            Debug.logWarning("An un-supported productPriceCond condition was used: " + productPriceCond.getString("operatorEnumId") + ", returning false, ie check failed", module);
            return false;
        }
        return false;
    }
}
