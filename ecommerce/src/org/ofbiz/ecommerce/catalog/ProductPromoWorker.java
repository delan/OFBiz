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

package org.ofbiz.ecommerce.catalog;

import java.util.*;
import java.net.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

import org.ofbiz.ecommerce.shoppingcart.*;

/**
 * ProductPromoWorker - Worker class for catalog/product promotion related functionality
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version    1.0
 *@created    June 1, 2002
 */
public class ProductPromoWorker {

    public static void doPromotions(String prodCatalogId, ShoppingCart cart, ShoppingCartItem cartItem, double newQuantity, GenericDelegator delegator) {
        if (cartItem.getQuantity() == newQuantity) {
            //no change, just return
            return;
        }
        
        //if quantity increased, then apply, otherwise unapply
        boolean apply = newQuantity > cartItem.getQuantity();
        
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
        
        //TODO: GET RELATED SORTED BY SEQ IDS...
        
        //there will be a ton of db access, so just do a big catch entity exception block
        try {
            //loop through promotions
            Iterator prodCatalogPromoAppls = UtilMisc.toIterator(EntityUtil.filterByDate(prodCatalog.getRelatedCache("ProdCatalogPromoAppl")));
            while (prodCatalogPromoAppls != null && prodCatalogPromoAppls.hasNext()){
                GenericValue prodCatalogPromoAppl = (GenericValue) prodCatalogPromoAppls.next();
                GenericValue productPromo = prodCatalogPromoAppl.getRelatedOneCache("ProductPromo");
                
                //loop through rules for promotion
                Iterator productPromoRules = UtilMisc.toIterator(productPromo.getRelatedCache("ProductPromoRule"));
                while (productPromoRules != null && productPromoRules.hasNext()) {
                    GenericValue productPromoRule = (GenericValue) productPromoRules.next();
                    
                    boolean performActions = true;
                    //loop through conditions for rule, if any false, set allConditionsTrue to false
                    Iterator productPromoConds = UtilMisc.toIterator(productPromoRule.getRelatedCache("ProductPromoCond"));
                    while (productPromoConds != null && productPromoConds.hasNext()) {
                        GenericValue productPromoCond = (GenericValue) productPromoConds.next();
                        
                        boolean condResult = checkCondition(productPromoCond, cart, cartItem, newQuantity);
                        //if apply, a false condition will cause it to not perform the action
                        //if unapply, a true condition will cause it to not perofrm the action
                        //so, if apply != condResult (ie true/false or false/true) then don't perform actions
                        if (apply != condResult) {
                            performActions = false;
                            break;
                        }
                    }
                    
                    if (performActions) {
                        //perform all actions
                        Iterator productPromoActions = UtilMisc.toIterator(productPromoRule.getRelatedCache("ProductPromoAction"));
                        while (productPromoActions != null && productPromoActions.hasNext()) {
                            GenericValue productPromoAction = (GenericValue) productPromoActions.next();
                            
                            //TODO: perform action
                            if ("PROMO_GWP".equals(productPromoAction.getString("productPromoActionTypeId"))) {
                                if (apply) {
                                } else {
                                }
                            } else if ("PROMO_FREE_SHIPPING".equals(productPromoAction.getString("productPromoActionTypeId"))) {
                            } else if ("PROMO_ITEM_PERCENT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
                            } else if ("PROMO_ITEM_AMOUNT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
                            } else if ("PROMO_ORDER_PERCENT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
                            } else if ("PROMO_ORDER_AMOUNT".equals(productPromoAction.getString("productPromoActionTypeId"))) {
                            }
                        }
                    }
                }
            }
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error looking up promotion data while doing promotions");
        }
    }
    
    public static boolean checkCondition(GenericValue productPromoCond, ShoppingCart cart, ShoppingCartItem cartItem, double newQuantity) {
        int compare = 0;
        if ("PPIP_PRODUCT_ID".equals(productPromoCond.getString("inputParamEnumId"))) {
            compare = cartItem.getProductId().compareTo(productPromoCond.getString("condValue"));
        } else if ("PPIP_ORDER_TOTAL".equals(productPromoCond.getString("inputParamEnumId"))) {
            Double orderSubTotal = new Double(cart.getSubTotal());
            compare = orderSubTotal.compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else if ("PPIP_QUANTITY_ADDED".equals(productPromoCond.getString("inputParamEnumId"))) {
            Double quantityAdded = new Double(newQuantity - cartItem.getQuantity());
            compare = quantityAdded.compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else if ("PPIP_NEW_PROD_QUANT".equals(productPromoCond.getString("inputParamEnumId"))) {
            compare = (new Double(newQuantity)).compareTo(Double.valueOf(productPromoCond.getString("condValue")));
        } else {
            Debug.logWarning("An un-supported productPromoCond input parameter (lhs) was used: " + productPromoCond.getString("inputParamEnumId") + ", returning false, ie check failed");
            return false;
        }

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
}
