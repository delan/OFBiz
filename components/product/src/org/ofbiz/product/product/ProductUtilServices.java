/*
 * $Id: ProductUtilServices.java,v 1.1 2004/01/25 04:02:26 jonesde Exp $
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
package org.ofbiz.product.product;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Product Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public class ProductUtilServices {
    
    public static final String module = ProductUtilServices.class.getName();

    // disc all virtuals that have all disc variants
    public static Map discVirtualsWithDiscVariants(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        try {
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("isVariant", EntityOperator.EQUALS, "Y"),
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.NOT_EQUAL, null),
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)
                    ), EntityOperator.AND);
            EntityListIterator eli = delegator.findListIteratorByCondition("Product", condition, null, null);
            GenericValue product = null;
            while ((product = (GenericValue) eli.next()) != null) {
                String virtualProductId = ProductWorker.getVariantVirtualId(product);
                GenericValue virtualProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", virtualProductId));
                virtualProduct.set("salesDiscontinuationDate", nowTimestamp);
                virtualProduct.store();
            }
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    // for all disc products, remove from category memberships
    public static Map removeCategoryMembersOfDiscProducts(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        try {
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.NOT_EQUAL, null),
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)
                    ), EntityOperator.AND);
            EntityListIterator eli = delegator.findListIteratorByCondition("Product", condition, null, null);
            GenericValue product = null;
            while ((product = (GenericValue) eli.next()) != null) {
                String productId = product.getString("productId");
                List productCategoryMemberList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productId", productId));
                Iterator productCategoryMemberIter = productCategoryMemberList.iterator();
                while (productCategoryMemberIter.hasNext()) {
                    GenericValue productCategoryMember = (GenericValue) productCategoryMemberIter.next();
                    // coded this way rather than a removeByAnd so it can be easily changed...
                    productCategoryMember.remove();
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    // for all virtuals with one variant move all info from virtual to variant and remove virtual, make variant as not a variant
    public static Map makeStandAloneFromSingleVariants(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
/*
    <view-entity entity-name="ProductVirtualAndVariantSimple"
            package-name="org.ofbiz.product.product"
            title="Virtual and Variant Product View Entity">
      <member-entity entity-alias="PVIRT" entity-name="Product"/>
      <member-entity entity-alias="PVA" entity-name="ProductAssoc"/>
      <member-entity entity-alias="PVAR" entity-name="Product"/>
      <alias entity-alias="PVIRT" name="productId"/>
      <alias entity-alias="PVIRT" name="productName"/>
      <alias entity-alias="PVA" name="productAssocTypeId"/>
      <alias entity-alias="PVA" name="fromDate"/>
      <alias entity-alias="PVA" name="thruDate"/>
      <alias entity-alias="PVAR" name="variantProductId" field="productId"/>
      <view-link entity-alias="PVIRT" rel-entity-alias="PVA">
        <key-map field-name="productId"/>
      </view-link>
      <view-link entity-alias="PVA" rel-entity-alias="PVAR">
        <key-map field-name="productIdTo" rel-field-name="productId"/>
      </view-link>
    </view-entity>
 * 
 */
        DynamicViewEntity dve = new DynamicViewEntity();
        dve.addMemberEntity("PVIRT", "Product");
        dve.addMemberEntity("PVA", "ProductAssoc");
        dve.addMemberEntity("PVAR", "Product");
        dve.addViewLink("PVIRT", "PVA", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productId", "productId")));
        dve.addViewLink("PVA", "PVAR", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productIdTo", "productId")));
        
        try {
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.NOT_EQUAL, null),
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)
                    ), EntityOperator.AND);
            EntityListIterator eli = delegator.findListIteratorByCondition("Product", condition, null, null);
            GenericValue product = null;
            while ((product = (GenericValue) eli.next()) != null) {
                String productId = product.getString("productId");
                List productCategoryMemberList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productId", productId));
                Iterator productCategoryMemberIter = productCategoryMemberList.iterator();
                while (productCategoryMemberIter.hasNext()) {
                    GenericValue productCategoryMember = (GenericValue) productCategoryMemberIter.next();
                    // coded this way rather than a removeByAnd so it can be easily changed...
                    productCategoryMember.remove();
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    // for all virtuals with one valid variant move info from virtual to variant, put variant in categories from virtual, remove virtual from all categories but leave "family" otherwise intact, mark variant as not a variant
    
    // reset all product image names with a certain pattern, ex: /images/products/${sizeStr}/${productId}.jpg
    
    
}
