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
        GenericValue productCategory = null;
        Collection members = null;
        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
            members = EntityUtil.filterByDate(productCategory.getRelatedCache("ProductCategoryMember", null, UtilMisc.toList("sequenceNum")), true);
            if (Debug.verboseOn()) Debug.logVerbose("Category: " + productCategory + " Member Size: " + members.size() + " Members: " + members);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading product categories: " + e.getMessage());
            return result;
        }
        result.put("category", productCategory);
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

        Collection memberCol = (Collection) values.get("categoryMembers");
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

        if (index == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Product not found in the current category.");
            return result;
        }

        result.put("category", values.get("category"));

        String previous = null;
        String next = null;

        if (index.intValue() - 1 >= 0 && index.intValue() -1 < memberList.size())  {
            previous = ((GenericValue) memberList.get(index.intValue() - 1)).getString("productId");
            result.put("previousProductId", previous);
        } else {
            previous = ((GenericValue) memberList.get(memberList.size() - 1)).getString("productId");
            result.put("previousProductId", previous);
        }

        if (index.intValue() + 1 < memberList.size()) {
            next = ((GenericValue) memberList.get(index.intValue() + 1)).getString("productId");
            result.put("nextProductId", next);
        } else {
            next = ((GenericValue) memberList.get(0)).getString("productId");
            result.put("nextProductId", next);
        }
        return result;
    }

    public static Map getProductCategoryAndLimitedMembers(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get("productCategoryId");
        boolean limitView = ((Boolean) context.get("limitView")).booleanValue();
        int defaultViewSize = ((Integer) context.get("defaultViewSize")).intValue();

        int viewIndex = 0;
        try {
            viewIndex = Integer.valueOf((String) context.get("viewIndexString")).intValue();
        } catch (Exception e) {
            viewIndex = 0;
        }
        int viewSize = defaultViewSize;
        try {
            viewSize = Integer.valueOf((String) context.get("viewSizeString")).intValue();
        } catch (Exception e) {
            viewSize = defaultViewSize;
        }

        GenericValue productCategory = null;
        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory",UtilMisc.toMap("productCategoryId",productCategoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            productCategory = null;
        }

        Collection productCategoryMembers = null;
        if (productCategory != null) {
            try {
                productCategoryMembers = EntityUtil.filterByDate(productCategory.getRelatedCache("ProductCategoryMember", null, UtilMisc.toList("sequenceNum")), true);
            } catch (GenericEntityException e) {
                Debug.logError(e);
            }
        }

        int lowIndex;
        int highIndex;
        int listSize = 0;
        if (productCategoryMembers != null) {
            listSize = productCategoryMembers.size();
        }

        if (limitView) {
            lowIndex = viewIndex * viewSize + 1;
            highIndex = (viewIndex + 1) * viewSize;
            if (listSize < highIndex) highIndex = listSize;
        } else {
            lowIndex = 1;
            highIndex = listSize;
        }

        Map result = new HashMap();
        result.put("viewIndex", new Integer(viewIndex));
        result.put("viewSize", new Integer(viewSize));
        result.put("lowIndex", new Integer(lowIndex));
        result.put("highIndex", new Integer(highIndex));
        result.put("listSize", new Integer(listSize));
        if (productCategory != null) result.put("productCategory", productCategory);
        if (productCategoryMembers != null) result.put("productCategoryMembers", productCategoryMembers);
        return result;
    }
}
