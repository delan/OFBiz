/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
import java.net.*;
import java.sql.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;

/**
 * PriceServices - Workers and Services class for product price related functionality
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    June 6, 2002
 */
public class PriceServices {
    public static final String module = PriceServices.class.getName();

    /**
     * <p>Calculates the price of a product from pricing rules given the following input, and of course access to the database:</p>
     * <ul>
     *   <li>productId
     *   <li>partyId
     *   <li>prodCatalogId
     *   <li>quantity
     * </ul>
     */
    public static Map calculateProductPrice(DispatchContext dctx, Map context) {
        UtilTimer utilTimer = new UtilTimer();
        utilTimer.setLog(true);
        utilTimer.timerString("Starting price calc", module);
        
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        boolean isSale = false;
        GenericValue product = (GenericValue) context.get("product");
        String productId = product.getString("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");

        //NOTE: partyId CAN be null
        String partyId = (String) context.get("partyId");
        if (partyId == null && context.get("userLogin") != null) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            partyId = userLogin.getString("partyId");
        }
        
        Double quantityDbl = (Double) context.get("quantity");
        if (quantityDbl == null) quantityDbl = new Double(1.0);
        double quantity = quantityDbl.doubleValue();
        
        List orderItemPriceInfos = new LinkedList();
        
        double defaultPrice = product.get("defaultPrice") != null ? product.getDouble("defaultPrice").doubleValue() : 0;
        
        Double listPriceDbl = product.getDouble("listPrice");
        if (listPriceDbl == null) {
            //no list price, use defaultPrice for the final price
            
            // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
            Double maxSellPrice = product.getDouble("maxSellPrice");
            if (maxSellPrice != null && defaultPrice > maxSellPrice.doubleValue()) {
                defaultPrice = maxSellPrice.doubleValue();
            }
            //min price second to override max price, safety net
            Double minSellPrice = product.getDouble("minSellPrice");
            if (minSellPrice != null && defaultPrice < minSellPrice.doubleValue()) {
                defaultPrice = minSellPrice.doubleValue();
            }
            
            result.put("price", new Double(defaultPrice));
        } else {
            try {
                //get some of the base values to calculate with
                double listPrice = listPriceDbl.doubleValue();
                double averageCostPrice = product.get("averageCostPrice") != null ? product.getDouble("averageCostPrice").doubleValue() : listPrice;
                double margin = listPrice - averageCostPrice;
                
                //calculate running sum based on listPrice and rules found
                double price = listPrice;

                // ========= find all rules that must be run for each input type; this is kind of like a pre-filter to slim down the rules to run =========
                utilTimer.timerString("Before create rule id list", module);
                TreeSet productPriceRuleIds = new TreeSet();

                // ------- These are all of them that DON'T depend on the current inputs -------

                //by productCategoryId
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

                //by quantity -- should we really do this one, ie is it necessary? 
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
                
                //by roleTypeId
                Collection roleTypeIdConds = delegator.findByAndCache("ProductPriceCond", 
                        UtilMisc.toMap("inputParamEnumId", "PRIP_ROLE_TYPE"));
                if (roleTypeIdConds != null && roleTypeIdConds.size() > 0) {
                    Iterator roleTypeIdCondsIter = roleTypeIdConds.iterator();
                    while (roleTypeIdCondsIter.hasNext()) {
                        GenericValue roleTypeIdCond = (GenericValue) roleTypeIdCondsIter.next();
                        productPriceRuleIds.add(roleTypeIdCond.getString("productPriceRuleId"));
                    }
                }
                
                //TODO, not supported yet: by groupPartyId
                //TODO, not supported yet: by partyClassificationGroupId
                //later: (by partyClassificationTypeId)

                // ------- These are all of them that DO depend on the current inputs -------

                //by productId
                Collection productIdConds = delegator.findByAndCache("ProductPriceCond", 
                        UtilMisc.toMap("inputParamEnumId", "PRIP_PRODUCT_ID", "condValue", productId));
                if (productIdConds != null && productIdConds.size() > 0) {
                    Iterator productIdCondsIter = productIdConds.iterator();
                    while (productIdCondsIter.hasNext()) {
                        GenericValue productIdCond = (GenericValue) productIdCondsIter.next();
                        productPriceRuleIds.add(productIdCond.getString("productPriceRuleId"));
                    }
                }

                //by prodCatalogId
                Collection prodCatalogIdConds = delegator.findByAndCache("ProductPriceCond", 
                        UtilMisc.toMap("inputParamEnumId", "PRIP_PROD_CLG_ID", "condValue", prodCatalogId));
                if (prodCatalogIdConds != null && prodCatalogIdConds.size() > 0) {
                    Iterator prodCatalogIdCondsIter = prodCatalogIdConds.iterator();
                    while (prodCatalogIdCondsIter.hasNext()) {
                        GenericValue prodCatalogIdCond = (GenericValue) prodCatalogIdCondsIter.next();
                        productPriceRuleIds.add(prodCatalogIdCond.getString("productPriceRuleId"));
                    }
                }

                //by partyId
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


                // ========= go through each price rule by id and eval all conditions =========
                utilTimer.timerString("Before eval rules", module);
                Iterator productPriceRuleIdsIter = productPriceRuleIds.iterator();
                int totalConds = 0;
                int totalActions = 0;
                int totalRules = 0;
                while (productPriceRuleIdsIter.hasNext()) {
                    String productPriceRuleId = (String) productPriceRuleIdsIter.next();
                    GenericValue productPriceRule = delegator.findByPrimaryKeyCache("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
                    if (productPriceRule == null) continue;

                    //check from/thru dates
                    if (productPriceRule.get("fromDate") != null && productPriceRule.getTimestamp("fromDate").after(nowTimestamp)) {
                        //hasn't started yet
                        continue;
                    }
                    if (productPriceRule.get("thruDate") != null && productPriceRule.getTimestamp("thruDate").before(nowTimestamp)) {
                        //already expired
                        continue;
                    }

                    //check all conditions
                    boolean allTrue = true;
                    StringBuffer condsDescription = new StringBuffer();
                    Collection productPriceConds = delegator.findByAndCache("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
                    Iterator productPriceCondsIter = UtilMisc.toIterator(productPriceConds);
                    while (productPriceCondsIter != null && productPriceCondsIter.hasNext()) {
                        GenericValue productPriceCond = (GenericValue) productPriceCondsIter.next();
                        totalConds++;
                        
                        if (!checkPriceCondition(productPriceCond, productId, prodCatalogId, partyId, quantity, delegator)) {
                            allTrue = false;
                            break;
                        }
                        
                        //add condsDescription string entry
                        condsDescription.append("[");
                        condsDescription.append(productPriceCond.getString("inputParamEnumId"));
                        condsDescription.append("::");
                        condsDescription.append(productPriceCond.getString("operatorEnumId"));
                        condsDescription.append("::");
                        condsDescription.append(productPriceCond.getString("condValue"));
                        condsDescription.append("] ");
                    }

                    //add some info about the prices we are calculating from
                    condsDescription.append("[list:");
                    condsDescription.append(listPrice);
                    condsDescription.append(";avgCost:");
                    condsDescription.append(averageCostPrice);
                    condsDescription.append(";margin:");
                    condsDescription.append(margin);
                    condsDescription.append("] ");
                    
                    boolean foundFlatOverride = false;
                    //if all true, perform all actions
                    if (allTrue) {
                        //check isSale
                        if ("Y".equals(productPriceRule.getString("isSale"))) {
                            isSale = true;
                        }

                        Collection productPriceActions = delegator.findByAndCache("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
                        Iterator productPriceActionsIter = UtilMisc.toIterator(productPriceActions);
                        while (productPriceActionsIter != null && productPriceActionsIter.hasNext()) {
                            GenericValue productPriceAction = (GenericValue) productPriceActionsIter.next();
                            totalActions++;

                            //yeah, finally here, perform the action, ie, modify the price
                            double modifyAmount = 0;
                            if ("PRICE_POL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = listPrice * (productPriceAction.getDouble("amount").doubleValue()/100.0);
                                }
                            } else if ("PRICE_POAC".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = averageCostPrice * (productPriceAction.getDouble("amount").doubleValue()/100.0);
                                }
                            } else if ("PRICE_POM".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = margin * (productPriceAction.getDouble("amount").doubleValue()/100.0);
                                }
                            } else if ("PRICE_FOL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = productPriceAction.getDouble("amount").doubleValue();
                                }
                            } else if ("PRICE_FLAT".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                //this one is a bit different, break out of the loop because we now have our final price
                                foundFlatOverride = true;
                                if (productPriceAction.get("amount") != null) {
                                    price = productPriceAction.getDouble("amount").doubleValue();
                                } else {
                                    Debug.logError("ERROR: ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId, module);
                                    price = defaultPrice;
                                }
                            }
                            
                            //add a orderItemPriceInfo element too, without orderId or orderItemId
                            StringBuffer priceInfoDescription = new StringBuffer();
                            priceInfoDescription.append(condsDescription.toString());
                            priceInfoDescription.append("[type:");
                            priceInfoDescription.append(productPriceAction.getString("productPriceActionTypeId"));
                            priceInfoDescription.append("]");

                            GenericValue orderItemPriceInfo = delegator.makeValue("OrderItemPriceInfo", null);
                            orderItemPriceInfo.set("productPriceRuleId", productPriceAction.get("productPriceRuleId"));
                            orderItemPriceInfo.set("productPriceActionSeqId", productPriceAction.get("productPriceActionSeqId"));
                            orderItemPriceInfo.set("modifyAmount", new Double(modifyAmount));
                            orderItemPriceInfo.set("description", priceInfoDescription.toString());
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

                //if no actions were run on the list price, then use the default price
                if (totalActions == 0) {
                    price = defaultPrice;
                }

                // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
                Double maxSellPrice = product.getDouble("maxSellPrice");
                if (maxSellPrice != null && price > maxSellPrice.doubleValue()) {
                    price = maxSellPrice.doubleValue();
                }
                //min price second to override max price, safety net
                Double minSellPrice = product.getDouble("minSellPrice");
                if (minSellPrice != null && price < minSellPrice.doubleValue()) {
                    price = minSellPrice.doubleValue();
                }

                Debug.logInfo("Final Calculated price: " + price + ", rules: " + totalRules + ", conds: " + totalConds + ", actions: " + totalActions, module);
                
                result.put("price", new Double(price));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting rules from the database while calculating price", module);
                return ServiceUtil.returnError("Error getting rules from the database while calculating price: " + e.toString());
            }
        }
        
        result.put("orderItemPriceInfos", orderItemPriceInfos);
        result.put("isSale", new Boolean(isSale));
        utilTimer.timerString("Finished price calc", module);
        return result;
    }

    public static boolean checkPriceCondition(GenericValue productPriceCond, String productId, String prodCatalogId, 
            String partyId, double quantity, GenericDelegator delegator) throws GenericEntityException {
        Debug.logVerbose("Checking price condition: " + productPriceCond, module);
        int compare = 0;
        if ("PRIP_PRODUCT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = productId.compareTo(productPriceCond.getString("condValue"));
        } else if ("PRIP_PROD_CAT_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            //if a ProductCategoryMember exists for this productId and the specified productCategoryId
            Collection productCategoryMembers = delegator.findByAndCache("ProductCategoryMember", 
                    UtilMisc.toMap("productId", productId, "productCategoryId", productPriceCond.getString("condValue")));
            // and from/thru date within range
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers);
            // then 0 (equals), otherwise 1 (not equals)
            if (productCategoryMembers != null && productCategoryMembers.size() > 0) {
                compare = 0;
            } else {
                compare = 1;
            }
        } else if ("PRIP_PROD_CLG_ID".equals(productPriceCond.getString("inputParamEnumId"))) {
            compare = prodCatalogId.compareTo(productPriceCond.getString("condValue"));
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
                //if a PartyRole exists for this partyId and the specified roleTypeId
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
        } else {
            Debug.logWarning("An un-supported productPriceCond input parameter (lhs) was used: " + productPriceCond.getString("inputParamEnumId") + ", returning false, ie check failed", module);
            return false;
        }
        
        Debug.logVerbose("Price Condition compare done, compare=" + compare, module);

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
