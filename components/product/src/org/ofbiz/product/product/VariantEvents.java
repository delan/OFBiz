/*
 * $Id: VariantEvents.java,v 1.1 2003/08/17 18:04:22 ajzeneski Exp $
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
package org.ofbiz.product.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;

/**
 * Product Variant Related Events
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class VariantEvents {
    
    public static final String module = VariantEvents.class.getName();

    /** Creates variant products from a virtual product and a combination of selectable features
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String quickAddChosenVariant(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String productId = request.getParameter("productId");
        String variantProductId = request.getParameter("variantProductId");
        String featureTypeSizeStr = request.getParameter("featureTypeSize");

        if (UtilValidate.isEmpty(productId)) {
            request.setAttribute("_ERROR_MESSAGE_", "productId is required but missing");
            return "error";
        }

        if (UtilValidate.isEmpty(variantProductId)) {
            request.setAttribute("_ERROR_MESSAGE_", "variantProductId is required but missing, please enter an id for the new variant product");
            return "error";
        }

        int featureTypeSize = 0;

        try {
            featureTypeSize = Integer.parseInt(featureTypeSizeStr);
        } catch (NumberFormatException e) {
            request.setAttribute("_ERROR_MESSAGE_", "featureTypeSize is not a number: " + featureTypeSizeStr);
            return "error";
        }

        try {
            boolean beganTransacton = TransactionUtil.begin();

            try {
                // read the product, duplicate it with the given id
                GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));

                if (product == null) {
                    TransactionUtil.rollback(beganTransacton);
                    request.setAttribute("_ERROR_MESSAGE_", "Product not found with ID: " + productId);
                    return "error";
                }

                GenericValue variantProduct = new GenericValue(product);

                variantProduct.set("productId", variantProductId);
                variantProduct.set("isVirtual", "N");
                variantProduct.set("isVariant", "Y");
                variantProduct.set("primaryProductCategoryId", null);
                variantProduct.create();

                // add an association from productId to variantProductId of the PRODUCT_VARIANT
                GenericValue productAssoc = delegator.makeValue("ProductAssoc",
                        UtilMisc.toMap("productId", productId, "productIdTo", variantProductId,
                            "productAssocTypeId", "PRODUCT_VARIANT", "fromDate", UtilDateTime.nowTimestamp()));

                productAssoc.create();

                // add the selected standard features to the new product given the productFeatureIds
                for (int i = 0; i < featureTypeSize; i++) {
                    String productFeatureId = request.getParameter("feature_" + i);

                    if (productFeatureId == null) {
                        TransactionUtil.rollback(beganTransacton);
                        request.setAttribute("_ERROR_MESSAGE_", "The productFeatureId for feature type number " + i + " was not found");
                        return "error";
                    }

                    GenericValue productFeature = delegator.findByPrimaryKey("ProductFeature", UtilMisc.toMap("productFeatureId", productFeatureId));

                    GenericValue productFeatureAppl = delegator.makeValue("ProductFeatureAppl",
                            UtilMisc.toMap("productId", variantProductId, "productFeatureId", productFeatureId,
                                "productFeatureApplTypeId", "STANDARD_FEATURE", "fromDate", UtilDateTime.nowTimestamp()));

                    // set the default seq num if it's there...
                    if (productFeature != null) {
                        productFeatureAppl.set("sequenceNum", productFeature.get("defaultSequenceNum"));
                    }

                    productFeatureAppl.create();
                }

                TransactionUtil.commit(beganTransacton);
            } catch (GenericEntityException e) {
                TransactionUtil.rollback(beganTransacton);
                Debug.logError(e, "Entity error creating quick add variant data", module);
                request.setAttribute("_ERROR_MESSAGE_", "Entity error quick add variant data: " + e.toString());
                return "error";
            }
        } catch (GenericTransactionException e) {
            Debug.logError(e, "Transaction error creating quick add variant data", module);
            request.setAttribute("_ERROR_MESSAGE_", "Transaction error creating quick add variant data: " + e.toString());
            return "error";
        }

        request.setAttribute("_EVENT_MESSAGE_", "Successfully created variant product with id: " + variantProductId + " (includes association, and standard features for the variant)");
        return "success";
    }
}
