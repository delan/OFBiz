/*
 * $Id: ProductWorker.java,v 1.6 2003/10/24 10:45:39 jonesde Exp $
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
package org.ofbiz.product.product;

import java.util.*;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.feature.ParametricSearch;

/**
 * Product Worker class to reduce code in JSPs.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class ProductWorker {
    
    public static final String module = ProductWorker.class.getName();

    public static void getProduct(PageContext pageContext, String attributeName) {
        getProduct(pageContext, attributeName, null);
    }

    public static boolean shippingApplies(GenericValue product) {
        if (product != null) {
            String productTypeId = product.getString("productTypeId");
            if ("SERVICE".equals(productTypeId) || "DIGITAL_GOOD".equals(productTypeId)) {
                // don't charge shipping on services or digital goods
                return false;
            }       
            Boolean chargeShipping = product.getBoolean("chargeShipping");
    
            if (chargeShipping == null) {
                return true;
            } else {
                return chargeShipping.booleanValue();
            }
        } else {
            throw new IllegalArgumentException("Null Product Entity not valid");            
        }                
    }
    
    public static boolean taxApplies(GenericValue product) {
        if (product != null) {        
            Boolean taxable = product.getBoolean("taxable");
    
            if (taxable == null) {
                return true;
            } else {
                return taxable.booleanValue();
            }
        } else {
            throw new IllegalArgumentException("Null Product Entity not valid");            
        }
    }    
    
    public static void getProduct(PageContext pageContext, String attributeName, String productId) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        ServletRequest request = pageContext.getRequest();

        if (productId == null)
            productId = UtilFormatOut.checkNull(request.getParameter("product_id"), request.getParameter("PRODUCT_ID"));

        if (productId.equals(""))
            return;

        GenericValue product = null;

        try {
            product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            product = null;
        }
        if (product != null)
            pageContext.setAttribute(attributeName, product);
    }

    public static String getVariantVirtualId(GenericValue variantProduct) throws GenericEntityException {
        List productAssocs = getVariantVirtualAssocs(variantProduct);
        GenericValue productAssoc = EntityUtil.getFirst(productAssocs);
        if (productAssoc != null) {
            return productAssoc.getString("productId");
        } else {
            return null;
        }
    }

    public static List getVariantVirtualAssocs(GenericValue variantProduct) throws GenericEntityException {
        if (variantProduct != null && "Y".equals(variantProduct.getString("isVariant"))) {
            List productAssocs = EntityUtil.filterByDate(variantProduct.getRelatedByAndCache("AssocProductAssoc", 
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT")), true);
            return productAssocs;
        }
        return null;
    }

    public static void getAssociatedProducts(PageContext pageContext, String productAttributeName, String assocPrefix) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        GenericValue product = (GenericValue) pageContext.getAttribute(productAttributeName);

        if (product == null)
            return;

        try {
            List upgradeProducts = product.getRelatedByAndCache("MainProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_UPGRADE"));

            List complementProducts = product.getRelatedByAndCache("MainProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));

            List obsolescenceProducts = product.getRelatedByAndCache("AssocProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_OBSOLESCENCE"));

            List obsoleteByProducts = product.getRelatedByAndCache("MainProductAssoc",
                    UtilMisc.toMap("productAssocTypeId", "PRODUCT_OBSOLESCENCE"));

            // since ProductAssoc records have a fromDate and thruDate, we can filter by now so that only assocs in the date range are included
            upgradeProducts = EntityUtil.filterByDate(upgradeProducts, true);
            complementProducts = EntityUtil.filterByDate(complementProducts, true);
            obsolescenceProducts = EntityUtil.filterByDate(obsolescenceProducts, true);
            obsoleteByProducts = EntityUtil.filterByDate(obsoleteByProducts, true);

            if (upgradeProducts != null && upgradeProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "upgrade", upgradeProducts);
            if (complementProducts != null && complementProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "complement", complementProducts);
            if (obsolescenceProducts != null && obsolescenceProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "obsolescence", obsolescenceProducts);
            if (obsoleteByProducts != null && obsoleteByProducts.size() > 0)
                pageContext.setAttribute(assocPrefix + "obsoleteby", obsoleteByProducts);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
    }
    
    // product calc methods
    
    public static double calcOrderAdjustments(List orderHeaderAdjustments, double subTotal, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;

        if (orderHeaderAdjustments != null && orderHeaderAdjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(orderHeaderAdjustments, includeOther, includeTax, includeShipping, false, false);
            Iterator adjIt = filteredAdjs.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                adjTotal += calcOrderAdjustment(orderAdjustment, subTotal);
            }
        }
        return adjTotal;
    }

    /** NOTE: DEJ 20031024, not sure how these got here or why they are here, are they used? */
    public static double calcOrderAdjustment(GenericValue orderAdjustment, double orderSubTotal) {
        double adjustment = 0.0;

        if (orderAdjustment.get("amount") != null) {
            adjustment += orderAdjustment.getDouble("amount").doubleValue();
        }
        if (orderAdjustment.get("percentage") != null) {
            adjustment += (orderAdjustment.getDouble("percentage").doubleValue() * orderSubTotal);
        }
        return adjustment;
    }    
    
    public static List filterOrderAdjustments(List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        List newOrderAdjustmentsList = new LinkedList();

        if (adjustments != null && adjustments.size() > 0) {
            Iterator adjIt = adjustments.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                boolean includeAdjustment = false;

                if ("SALES_TAX".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeTax) includeAdjustment = true;
                } else if ("SHIPPING_CHARGES".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeShipping) includeAdjustment = true;
                } else {
                    if (includeOther) includeAdjustment = true;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forTax && "N".equals(orderAdjustment.getString("includeInTax"))) {
                    includeAdjustment = false;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forShipping && "N".equals(orderAdjustment.getString("includeInShipping"))) {
                    includeAdjustment = false;
                }

                if (includeAdjustment) {
                    newOrderAdjustmentsList.add(orderAdjustment);
                }
            }
        }
        return newOrderAdjustmentsList;
    }
}

