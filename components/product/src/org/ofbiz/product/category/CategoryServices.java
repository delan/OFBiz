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
package org.ofbiz.product.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

/**
 * CategoryServices - Category Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class CategoryServices {
    
    public static final String module = CategoryServices.class.getName();

    public static Map getCategoryMembers(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String categoryId = (String) context.get("categoryId");
        GenericValue productCategory = null;
        List members = null;

        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
            members = EntityUtil.filterByDate(productCategory.getRelatedCache("ProductCategoryMember", null, UtilMisc.toList("sequenceNum")), true);
            if (Debug.verboseOn()) Debug.logVerbose("Category: " + productCategory + " Member Size: " + members.size() + " Members: " + members, module);
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

        if (index.intValue() - 1 >= 0 && index.intValue() - 1 < memberList.size()) {
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

        // checkViewAllow defaults to false, must be set to true and pass the prodCatalogId to enable
        Boolean cvaBool = (Boolean) context.get("checkViewAllow");
        boolean checkViewAllow = (cvaBool == null ? false : cvaBool.booleanValue());
        String prodCatalogId = (String) context.get("prodCatalogId");

        boolean useCacheForMembers = true;
        if (context.get("useCacheForMembers") != null) {
            useCacheForMembers = ((Boolean) context.get("useCacheForMembers")).booleanValue();
        }

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
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            productCategory = null;
        }

        List productCategoryMembers = null;
        if (productCategory != null) {
            try {
                if (useCacheForMembers) {
                    productCategoryMembers = productCategory.getRelatedCache("ProductCategoryMember", null, UtilMisc.toList("sequenceNum"));
                } else {
                    productCategoryMembers = productCategory.getRelated("ProductCategoryMember", null, UtilMisc.toList("sequenceNum"));
                }
                productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                
                // first check to see if there is a view allow category and if this product is in it...
                if (checkViewAllow && prodCatalogId != null && productCategoryMembers != null && productCategoryMembers.size() > 0) {
                    String viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
                    if (viewProductCategoryId != null) {
                        productCategoryMembers = CategoryWorker.filterProductsInCategory(delegator, productCategoryMembers, viewProductCategoryId);
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
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
