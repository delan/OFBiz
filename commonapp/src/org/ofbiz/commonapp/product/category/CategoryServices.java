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

package org.ofbiz.commonapp.product.category;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * CategoryServices - Category Services
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Apr 17, 2002
 * @version    1.0
 */
public class CategoryServices {

    public static Map getCategoryMembers(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String categoryId = (String) context.get("categoryId");
        GenericValue category = null;
        Collection members = null;
        try {
            category = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
            members = EntityUtil.filterByDate(category.getRelatedCache("ProductCategoryMember", null, UtilMisc.toList("sequenceNum")));
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading product categories: " + e.getMessage());
            return result;
        }
        result.put("category", category);
        result.put("categoryMembers", members);
        return result;
    }

    public static Map getNextPreviousCategoryMembers(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String categoryId = (String) context.get("categoryId");
        String productId = (String) context.get("productId");
        Integer index = (Integer) context.get("index");
        if (index == null && productId == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Both Index and ProductID cannot be null.");
            return result;
        }

        Map values = getCategoryMembers(dctx, context);
        if (values.containsKey(ModelService.ERROR_MESSAGE)) {
            return result;
        }
        if (!values.containsKey("categoryMembers") || values.get("categoryMembers") == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading category data.");
            return result;
        }

        Collection memberCol = (Collection) result.get("categoryMembers");
        if (memberCol == null || memberCol.size() == 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading category member data.");
            return result;
        }

        List memberList = new ArrayList(memberCol);

        if (productId != null && index == null) {
            Iterator i = memberList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                if (v.getString("productId").equals(productId))
                    index = new Integer(memberList.indexOf(v));
            }
        }

        result.put("category", values.get("category"));

        String previous = null;
        String next = null;

        if (index.intValue() - 2 > memberList.size())  {
            previous = ((GenericValue) memberList.get(index.intValue() - 1)).getString("productId");
            result.put("previousProductId", previous);
        }
        if (index.intValue() + 2 < memberList.size()) {
            next = ((GenericValue) memberList.get(index.intValue() + 1)).getString("productId");
            result.put("nextProductId", next);
        }
        return result;
    }

}

