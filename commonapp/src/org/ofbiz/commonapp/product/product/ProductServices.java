/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project (www.ofbiz.org)
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
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
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
        return new HashMap();
    }

    /**
     * Finds a specific product or products which contain the selected features.
     */
    public static Map prodFindSelectedVariant(DispatchContext dctx. Map context) {
        // * String productId      -- Parent (Virtual) product ID
        // * Map selectedFeatures  -- Selected features
        return new HashMap();
    }

    /**
     * Finds a product by product ID.
     */
    public static Map prodFindProduct(DispatchContext dctx, Map context) {
        // * String productId      -- Product ID to find
        return new HashMap();
    }

    /**
     * Finds associated products by product ID and association ID.
     */
    public static Map prodFindAssociatedByType(DispatchContext dctx, Map context) {
        // * String productId      -- Current Product ID
        // * String type           -- Type of association (ie PRODUCT_UPGRADE, PRODUCT_COMPLEMENT)
        return new HashMap();
    }

}
