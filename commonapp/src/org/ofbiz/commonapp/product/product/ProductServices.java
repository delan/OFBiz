/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project (www.ofbiz.org)
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

package org.ofbiz.commonapp.product.product;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Product Services
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.0
 * @created    April 1, 2002
 */
public class ProductServices {

    /**
     * Creates a Collection of product entities which are variant products from the specified product ID.
     */
    public static Map prodFindAllVariants(DispatchContext dctx, Map context) {
        // * String productId      -- Parent (virtual) product ID
        context.put("type", "PRODUCT_VARIANT");
        return prodFindAssociatedByType(dctx, context);
    }

    /**
     * Finds a specific product or products which contain the selected features.
     */
    public static Map prodFindSelectedVariant(DispatchContext dctx. Map context) {
        // * String productId      -- Parent (virtual) product ID
        // * Map selectedFeatures  -- Selected features
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        Map selectedFeatures = (Map) context.get("selectedFeatures");

        return result;
    }

    /**
     * Finds product variants based on a product ID and a distinct feature.
     */
    public static Map prodFindDistinctVariants(DispatchContext dctx, Map context) {
        // * String productId      -- Parent (virtual) product ID
        // * String feature        -- Distinct feature name
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        String feature = (String) context.get("feature");

        return result;
    }

    /**
     * Gets the product features of a product.
     */
    public static Map prodGetFeatures(DispatchContext dctx, Map context) {
        // * String productId      -- Product ID to fond
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        Collection features = null;
        try {
            features = delegator.findByAnd("ProductFeature", UtilMisc.toMap("productId", productId));
            result.put("productFeatures", features);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e ) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading product feature entity: " + e.getMessage());
        }
        return result;
    }

    /**
     * Finds a product by product ID.
     */
    public static Map prodFindProduct(DispatchContext dctx, Map context) {
        // * String productId      -- Product ID to find
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        if (productId == null || productId.length() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Invalid productId passed.");
            return result;
        }

        try {
            GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            result.put("product", product);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems reading product entity: " + e.getMessage());
        }

        return result;
    }

    /**
     * Finds associated products by product ID and association ID.
     */
    public static Map prodFindAssociatedByType(DispatchContext dctx, Map context) {
        // * String productId      -- Current Product ID
        // * String type           -- Type of association (ie PRODUCT_UPGRADE, PRODUCT_COMPLEMENT)
        GenericDelegator delegator = dctx.getDelegator();
        Map result = new HashMap();
        String productId = (String) context.get("productId");
        String type = (String) context.get("type");
        GenericValue product = null;
        try {
            product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems reading product entity: " + e.getMessage());
            return result;
        }

        if (product == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems getting the product entity.");
            return result;
        }

        try {
            Collection c = product.getRelatedByAndCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", type));
            c = EntityUtil.filterByDate(c);
            result.put("assocProducts", c);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problems product association relation: " + e.getMessage());
            return result;
        }

        return result;
    }

}
