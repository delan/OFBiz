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
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        boolean isSale = false;
        GenericValue product = (GenericValue) context.get("product");
        String productId = product.getString("productId");
        String partyId = (String) context.get("partyId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        double quantity = ((Double) context.get("quantity")).doubleValue();
        List orderItemPriceInfos = new LinkedList();
        
        double defaultPrice = product.get("defaultPrice") != null ? product.getDouble("defaultPrice").doubleValue() : 0;
        
        Double listPriceDbl = product.getDouble("listPrice");
        if (listPriceDbl == null) {
            //no list price, use defaultPrice for the final price
            result.put("price", new Double(defaultPrice));
        } else {
            try {
                //get some of the base values to calculate with
                double listPrice = listPriceDbl.doubleValue();
                double averageCostPrice = product.get("averageCostPrice") != null ? product.getDouble("averageCostPrice").doubleValue() : listPrice;
                double margin = listPrice - averageCostPrice;
                
                //calculate running sum based on listPrice and rules found
                double price = listPrice;

                // ========= find all rules that must be run for each input type: =========
                TreeSet productPriceRuleIds = new TreeSet();

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

                //TODO: by productCategoryId
                //TODO: by prodCatalogId
                //TODO: by quantity -- should we really do this one, ie is it necessary? we could say that all rules with quantity on them must have one of these other values
                //TODO: by partyId
                //TODO: by groupPartyId
                //TODO: by partyClassificationGroupId
                //later: (by partyClassificationTypeId)
                //TODO: by roleTypeId


                // ========= go through each price rule by id and eval all conditions =========
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

                    //check isSale
                    if ("Y".equals(productPriceRule.getString("isSale"))) {
                        isSale = true;
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
                        Collection productPriceActions = delegator.findByAndCache("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
                        Iterator productPriceActionsIter = UtilMisc.toIterator(productPriceActions);
                        while (productPriceActionsIter != null && productPriceActionsIter.hasNext()) {
                            GenericValue productPriceAction = (GenericValue) productPriceActionsIter.next();
                            totalActions++;

                            //yeah, finally here, perform the action, ie, modify the price
                            double modifyAmount = 0;
                            if ("PRICE_POL".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = listPrice * productPriceAction.getDouble("amount").doubleValue();
                                }
                            } else if ("PRICE_POAC".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = averageCostPrice * productPriceAction.getDouble("amount").doubleValue();
                                }
                            } else if ("PRICE_POM".equals(productPriceAction.getString("productPriceActionTypeId"))) {
                                if (productPriceAction.get("amount") != null) {
                                    modifyAmount = margin * productPriceAction.getDouble("amount").doubleValue();
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
                                    Debug.logError("ERROR: ProductPriceAction had null amount, using default price: " + defaultPrice + " for product with id " + productId);
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
                            orderItemPriceInfo.set("productPriceRuleId", productPriceAction.get("productPriceAction"));
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
                    Debug.logVerbose("Unchecked Calculated price: " + price);
                    Debug.logVerbose("PriceInfo:");
                    Iterator orderItemPriceInfosIter = orderItemPriceInfos.iterator();
                    while (orderItemPriceInfosIter.hasNext()) {
                        GenericValue orderItemPriceInfo = (GenericValue) orderItemPriceInfosIter.next();
                        Debug.logVerbose(" --- " + orderItemPriceInfo.toString());
                    }
                }

                // ========= ensure calculated price is not below minSalePrice or above maxSalePrice =========
                Double maxSellPrice = product.getDouble("maxSellPrice");
                if (maxSellPrice != null && price > maxSellPrice.doubleValue()) {
                    price = maxSellPrice.doubleValue();
                }
                Double minSellPrice = product.getDouble("minSellPrice");
                if (minSellPrice != null && price < minSellPrice.doubleValue()) {
                    price = minSellPrice.doubleValue();
                }

                Debug.logInfo("Final Calculated price: " + price + ", rules: " + totalRules + ", conds: " + totalConds + ", actions: " + totalActions);
                
                result.put("price", new Double(price));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error getting rules from the database while calculating price");
                return ServiceUtil.returnError("Error getting rules from the database while calculating price: " + e.toString());
            }
        }
        
        result.put("orderItemPriceInfos", orderItemPriceInfos);
        result.put("isSale", new Boolean(isSale));
        return result;
    }

    public static boolean checkPriceCondition(GenericValue productPriceCond, String productId, String prodCatalogId, 
            String partyId, double quantity, GenericDelegator delegator) throws GenericEntityException {
        Debug.logVerbose("Checking price condition: " + productPriceCond);
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
            compare = partyId.compareTo(productPriceCond.getString("condValue"));
        /* These aren't supported yet, ie TODO
        } else if ("PRIP_PARTY_GRP_MEM".equals(productPriceCond.getString("inputParamEnumId"))) {
        } else if ("PRIP_PARTY_CLASS".equals(productPriceCond.getString("inputParamEnumId"))) {
        */
        } else if ("PRIP_ROLE_TYPE".equals(productPriceCond.getString("inputParamEnumId"))) {
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
            Debug.logWarning("An un-supported productPriceCond input parameter (lhs) was used: " + productPriceCond.getString("inputParamEnumId") + ", returning false, ie check failed");
            return false;
        }
        
        Debug.logVerbose("Price Condition compare done, compare=" + compare);

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
            Debug.logWarning("An un-supported productPriceCond condition was used: " + productPriceCond.getString("operatorEnumId") + ", returning false, ie check failed");
            return false;
        }
        return false;
    }
}
