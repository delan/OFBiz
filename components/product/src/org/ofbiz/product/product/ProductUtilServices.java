/*
 * $Id: ProductUtilServices.java,v 1.4 2004/01/27 06:16:10 jonesde Exp $
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FlexibleStringExpander;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Product Services
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.4 $
 * @since      2.0
 */
public class ProductUtilServices {
    
    public static final String module = ProductUtilServices.class.getName();

    /** First expirt all ProductAssocs for all disc variants, then disc all virtuals that have all expired variant ProductAssocs */
    public static Map discVirtualsWithDiscVariants(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        try {
            EntityCondition conditionOne = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("isVariant", EntityOperator.EQUALS, "Y"),
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.NOT_EQUAL, null),
                    new EntityExpr("salesDiscontinuationDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)
                    ), EntityOperator.AND);
            EntityListIterator eliOne = delegator.findListIteratorByCondition("Product", conditionOne, null, null);
            GenericValue productOne = null;
            int numSoFarOne = 0;
            while ((productOne = (GenericValue) eliOne.next()) != null) {
                String virtualProductId = ProductWorker.getVariantVirtualId(productOne);
                GenericValue virtualProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", virtualProductId));
                if (virtualProduct == null) {
                    continue;
                }
                List passocList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", virtualProductId, "productIdTo", productOne.get("productId"), "productAssocTypeId", "PRODUCT_VARIANT"));
                passocList = EntityUtil.filterByDate(passocList, nowTimestamp);
                if (passocList.size() > 0) {
                    Iterator passocIter = passocList.iterator();
                    while (passocIter.hasNext()) {
                        GenericValue passoc = (GenericValue) passocIter.next();
                        passoc.set("thruDate", nowTimestamp);
                        passoc.store();
                    }
                    
                    numSoFarOne++;
                    if (numSoFarOne % 500 == 0) {
                        Debug.logInfo("Expired variant ProductAssocs for " + numSoFarOne + " sales discontinued variant products.", module);
                    }
                }
            }

            // get all non-discontinued virtuals, see if all variant ProductAssocs are expired, if discontinue
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("isVirtual", EntityOperator.EQUALS, "Y"),
                    new EntityExpr(new EntityExpr("salesDiscontinuationDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("salesDiscontinuationDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                    ), EntityOperator.AND);
            EntityListIterator eli = delegator.findListIteratorByCondition("Product", condition, null, null);
            GenericValue product = null;
            int numSoFar = 0;
            while ((product = (GenericValue) eli.next()) != null) {
                List passocList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", product.get("productId"), "productAssocTypeId", "PRODUCT_VARIANT"));
                passocList = EntityUtil.filterByDate(passocList, nowTimestamp);
                if (passocList.size() == 0) {
                    product.set("salesDiscontinuationDate", nowTimestamp);
                    
                    numSoFar++;
                    if (numSoFar % 500 == 0) {
                        Debug.logInfo("Sales discontinued " + numSoFar + " virtual products that have no valid variants.", module);
                    }
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    /** for all disc products, remove from category memberships */
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
            int numSoFar = 0;
            while ((product = (GenericValue) eli.next()) != null) {
                String productId = product.getString("productId");
                List productCategoryMemberList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productId", productId));
                if (productCategoryMemberList.size() > 0) {
                    Iterator productCategoryMemberIter = productCategoryMemberList.iterator();
                    while (productCategoryMemberIter.hasNext()) {
                        GenericValue productCategoryMember = (GenericValue) productCategoryMemberIter.next();
                        // coded this way rather than a removeByAnd so it can be easily changed...
                        productCategoryMember.remove();
                    }
                    numSoFar++;
                    if (numSoFar % 500 == 0) {
                        Debug.logInfo("Removed category members for " + numSoFar + " sales discontinued products.", module);
                    }
                }
            }
            Debug.logInfo("Completed - Removed category members for " + numSoFar + " sales discontinued products.", module);
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map makeStandAloneFromSingleVariantVirtuals(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        Debug.logInfo("Starting makeStandAloneFromSingleVariantVirtuals", module);
        
        DynamicViewEntity dve = new DynamicViewEntity();
        dve.addMemberEntity("PVIRT", "Product");
        dve.addMemberEntity("PVA", "ProductAssoc");
        //dve.addMemberEntity("PVAR", "Product");
        dve.addViewLink("PVIRT", "PVA", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productId", "productId")));
        //dve.addViewLink("PVA", "PVAR", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productIdTo", "productId")));
        dve.addAlias("PVIRT", "productId", null, null, null, Boolean.TRUE, null);
        dve.addAlias("PVA", "productAssocTypeId", null, null, null, null, null);
        dve.addAlias("PVA", "fromDate", null, null, null, null, null);
        dve.addAlias("PVA", "thruDate", null, null, null, null, null);
        dve.addAlias("PVA", "productIdToCount", "productIdTo", null, null, null, "count-distinct");
        //dve.addAlias("PVAR", "variantProductId", "productId", null, null, null, null);
        
        try {
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_VARIANT")
                    ), EntityOperator.AND);
            EntityCondition havingCond = new EntityExpr("productIdToCount", EntityOperator.EQUALS, new Long(1));
            EntityListIterator eliOne = delegator.findListIteratorByCondition(dve, condition, havingCond, UtilMisc.toList("productId", "productIdToCount"), null, null);
            GenericValue value = null;
            int numWithOneOnly = 0;
            while ((value = (GenericValue) eliOne.next()) != null) {
                // has only one variant period, is it valid? should already be discontinued if not
                
                String productId = value.getString("productId");
                GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                
                List paList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"));
                // verify the query; tested on a bunch, looks good
                if (paList.size() != 1) {
                    Debug.logInfo("Virtual product with ID " + productId + " should have 1 assoc, has " + paList.size(), module);
                } else {
                    // for all virtuals with one variant move all info from virtual to variant and remove virtual, make variant as not a variant
                    GenericValue productAssoc = EntityUtil.getFirst(paList);
                    // remove the productAssoc before getting down so it isn't copied over...
                    productAssoc.remove();
                    String variantProductId = productAssoc.getString("productIdTo");
                    
                    // Product
                    GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", variantProductId));
                    // start with the values from the virtual product, override from the variant...
                    GenericValue newVariantProduct = delegator.makeValue("Product", product);
                    newVariantProduct.setAllFields(variantProduct, false, "", null);
                    newVariantProduct.store();
                    
                    // ProductCategoryMember
                    duplicateRelated(product, "ProductCategoryMember", variantProductId, nowTimestamp, true, delegator);
                    
                    // ProductFeature
                    duplicateRelated(product, "ProductFeature", variantProductId, nowTimestamp, true, delegator);
                    
                    // ProductContent
                    duplicateRelated(product, "ProductContent", variantProductId, nowTimestamp, true, delegator);
                    
                    // ProductPrice
                    duplicateRelated(product, "ProductPrice", variantProductId, nowTimestamp, true, delegator);
                    
                    // GoodIdentification
                    duplicateRelated(product, "GoodIdentification", variantProductId, nowTimestamp, true, delegator);
                    
                    // ProductAttribute
                    duplicateRelated(product, "ProductAttribute", variantProductId, nowTimestamp, true, delegator);
                    
                    // ProductAssoc
                    duplicateRelated(product, "ProductAssoc", variantProductId, nowTimestamp, true, delegator);
                    
                    product.remove();
                    
                    numWithOneOnly++;
                    if (numWithOneOnly % 100 == 0) {
                        Debug.logInfo("Made " + numWithOneOnly + " virtual products with only one valid variant stand-alone products.", module);
                    }
                }
            }
            
            EntityCondition conditionWithDates = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_VARIANT"),
                    new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                    new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                    ), EntityOperator.AND);
            EntityListIterator eliMulti = delegator.findListIteratorByCondition(dve, conditionWithDates, havingCond, UtilMisc.toList("productId", "productIdToCount"), null, null);
            int numWithOneValid = 0;
            while ((value = (GenericValue) eliMulti.next()) != null) {
                // has only one valid variant
                String productId = value.getString("productId");
                
                GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                
                List paList = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"));
                // verify the query; tested on a bunch, looks good
                if (paList.size() != 1) {
                    Debug.logInfo("Virtual product with ID " + productId + " should have 1 assoc, has " + paList.size(), module);
                } else {
                    // for all virtuals with one valid variant move info from virtual to variant, put variant in categories from virtual, remove virtual from all categories but leave "family" otherwise intact, mark variant as not a variant
                    GenericValue productAssoc = EntityUtil.getFirst(paList);
                    String variantProductId = productAssoc.getString("productIdTo");
                    
                    // Product
                    GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", variantProductId));
                    // start with the values from the virtual product, override from the variant...
                    GenericValue newVariantProduct = delegator.makeValue("Product", product);
                    newVariantProduct.setAllFields(variantProduct, false, "", null);
                    newVariantProduct.store();
                    
                    // ProductCategoryMember
                    duplicateRelated(product, "ProductCategoryMember", variantProductId, nowTimestamp, false, delegator);
                    
                    // ProductFeature
                    duplicateRelated(product, "ProductFeature", variantProductId, nowTimestamp, false, delegator);
                    
                    // ProductContent
                    duplicateRelated(product, "ProductContent", variantProductId, nowTimestamp, false, delegator);
                    
                    // ProductPrice
                    duplicateRelated(product, "ProductPrice", variantProductId, nowTimestamp, false, delegator);
                    
                    // GoodIdentification
                    duplicateRelated(product, "GoodIdentification", variantProductId, nowTimestamp, false, delegator);
                    
                    // ProductAttribute
                    duplicateRelated(product, "ProductAttribute", variantProductId, nowTimestamp, false, delegator);
                    
                    // ProductAssoc
                    duplicateRelated(product, "ProductAssoc", variantProductId, nowTimestamp, false, delegator);
                    
                    numWithOneValid++;
                    if (numWithOneValid % 100 == 0) {
                        Debug.logInfo("Made " + numWithOneValid + " virtual products with one valid variant stand-alone products.", module);
                    }
                }
            }
            
            Debug.logInfo("Found virtual products with one valid variant: " + numWithOneValid + ", with one variant only: " + numWithOneOnly, module);
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static void duplicateRelated(GenericValue product, String relatedEntityName, String variantProductId, Timestamp nowTimestamp, boolean removeOld, GenericDelegator delegator) throws GenericEntityException {
        List relatedList = EntityUtil.filterByDate(product.getRelated(relatedEntityName), nowTimestamp);
        Iterator relatedIter = relatedList.iterator();
        while (relatedIter.hasNext()) {
            GenericValue relatedValue = (GenericValue) relatedIter.next();
            
            // create a new one? see if one already exists with different from/thru dates
            ModelEntity modelEntity = relatedValue.getModelEntity();
            if (modelEntity.isField("fromDate")) {
                GenericPK findValue = relatedValue.getPrimaryKey();
                findValue.set("fromDate", null);
                List existingValueList = EntityUtil.filterByDate(delegator.findByAnd(relatedEntityName, findValue));
                if (existingValueList.size() > 0) {
                    continue;
                }
            }
            
            GenericValue newRelatedValue = (GenericValue) relatedValue.clone();
            newRelatedValue.set("productId", variantProductId);
            newRelatedValue.create();
        }
        if (removeOld) {
            product.removeRelated(relatedEntityName);
        }
    }
    
    
    /** reset all product image names with a certain pattern, ex: /images/products/${sizeStr}/${productId}.jpg
     * NOTE: only works on fields of Product right now
     */
    public static Map setAllProductImageNames(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String pattern = (String) context.get("pattern");
        
        try {
            EntityListIterator eli = delegator.findListIteratorByCondition("Product", null, null, null);
            GenericValue product = null;
            int numSoFar = 0;
            while ((product = (GenericValue) eli.next()) != null) {
                String productId = (String) product.get("productId");
                Map smallMap = UtilMisc.toMap("sizeStr", "small", "productId", productId);
                Map mediumMap = UtilMisc.toMap("sizeStr", "medium", "productId", productId);
                Map largeMap = UtilMisc.toMap("sizeStr", "large", "productId", productId);
                Map detailMap = UtilMisc.toMap("sizeStr", "detail", "productId", productId);
                
                product.set("smallImageUrl", FlexibleStringExpander.expandString(pattern, smallMap));
                product.set("mediumImageUrl", FlexibleStringExpander.expandString(pattern, mediumMap));
                product.set("largeImageUrl", FlexibleStringExpander.expandString(pattern, largeMap));
                product.set("detailImageUrl", FlexibleStringExpander.expandString(pattern, detailMap));
                
                product.store();
                numSoFar++;
                if (numSoFar % 500 == 0) {
                    Debug.logInfo("Image URLs set for " + numSoFar + " products.", module);
                }
            }
            Debug.logInfo("Completed - Image URLs set for " + numSoFar + " products.", module);
        } catch (GenericEntityException e) {
            String errMsg = "Entity error running salesDiscontinuationDate: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }

    // set category descriptions from longDescriptions
    /*
allCategories = delegator.findAll("ProductCategory");
allCatIter = allCategories.iterator();
while (allCatIter.hasNext()) {
   cat = allCatIter.next();
   if (UtilValidate.isEmpty(cat.getString("description"))) {
       StringBuffer description = new StringBuffer(cat.getString("longDescription").toLowerCase());
       description.setCharAt(0, Character.toUpperCase(description.charAt(0)));
       for (int i=0; i<description.length() - 1; i++) {
           if (description.charAt(i) == ' ') {
               description.setCharAt(i+1, Character.toUpperCase(description.charAt(i+1)));
           }
       }
       Debug.logInfo("new description: " + description, "ctc.bsh");
              cat.put("description", description.toString());
       cat.store();
   }
}
     */
    


    public static Map attachProductFeaturesToCategory(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get("productCategoryId");
        String doSubCategoriesStr = (String) context.get("doSubCategories");
        // default to true
        boolean doSubCategories = !"N".equals(doSubCategoriesStr);
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        Set productFeatureTypeIdsToExclude = new HashSet();
        String excludeProp = UtilProperties.getPropertyValue("prodsearch", "attach.feature.type.exclude");
        if (UtilValidate.isNotEmpty(excludeProp)) {
            List typeList = StringUtil.split(excludeProp, ",");
            productFeatureTypeIdsToExclude.add(typeList);
        }

        try {
            attachProductFeaturesToCategory(productCategoryId, productFeatureTypeIdsToExclude, delegator, doSubCategories, nowTimestamp);
        } catch (GenericEntityException e) {
            String errMsg = "Error in attachProductFeaturesToCategory" + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    /** Get all features associated with products and associate them with a feature group attached to the category for each feature type;
     * includes products associated with this category only, but will also associate all feature groups of sub-categories with this category, optionally calls this method for all sub-categories too
     */
    public static void attachProductFeaturesToCategory(String productCategoryId, Set productFeatureTypeIdsToExclude, GenericDelegator delegator, boolean doSubCategories, Timestamp nowTimestamp) throws GenericEntityException {
        if (nowTimestamp == null) {
            nowTimestamp = UtilDateTime.nowTimestamp();
        }

        // do sub-categories first so all feature groups will be in place
        List subCategoryList = delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId", productCategoryId));
        if (doSubCategories) {
            Iterator subCategoryIter = subCategoryList.iterator();
            while (subCategoryIter.hasNext()) {
                GenericValue productCategoryRollup = (GenericValue) subCategoryIter.next();
                attachProductFeaturesToCategory(productCategoryRollup.getString("productCategoryId"), productFeatureTypeIdsToExclude, delegator, true, nowTimestamp);
            }
        }

        // now get all features for this category and make associated feature groups
        Map productFeatureIdByTypeIdSetMap = new HashMap();
        List productCategoryMemberList = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId));
        Iterator productCategoryMemberIter = productCategoryMemberList.iterator();
        while (productCategoryMemberIter.hasNext()) {
            GenericValue productCategoryMember = (GenericValue) productCategoryMemberIter.next();
            String productId = productCategoryMember.getString("productId");
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productId", EntityOperator.EQUALS, productId),
                    new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                    new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
            ), EntityOperator.AND);
            EntityListIterator productFeatureAndApplEli = delegator.findListIteratorByCondition("ProductFeatureAndAppl", condition, null, null);
            GenericValue productFeatureAndAppl = null;
            while ((productFeatureAndAppl = (GenericValue) productFeatureAndApplEli.next()) != null) {
                String productFeatureId = productFeatureAndAppl.getString("productFeatureId");
                String productFeatureTypeId = productFeatureAndAppl.getString("productFeatureTypeId");
                if (productFeatureTypeIdsToExclude != null && productFeatureTypeIdsToExclude.contains(productFeatureTypeId)) {
                    continue;
                }
                Set productFeatureIdSet = (Set) productFeatureIdByTypeIdSetMap.get(productFeatureTypeId);
                if (productFeatureIdSet == null) {
                    productFeatureIdSet = new HashSet();
                    productFeatureIdByTypeIdSetMap.put(productFeatureTypeId, productFeatureIdSet);
                }
                productFeatureIdSet.add(productFeatureId);
            }
        }
        
        Iterator productFeatureIdByTypeIdSetIter = productFeatureIdByTypeIdSetMap.entrySet().iterator();
        while (productFeatureIdByTypeIdSetIter.hasNext()) {
            Map.Entry entry = (Map.Entry) productFeatureIdByTypeIdSetIter.next();
            String productFeatureTypeId = (String) entry.getKey();
            Set productFeatureIdSet = (Set) entry.getValue();
            
            String productFeatureGroupId = productCategoryId + "_" + productFeatureTypeId;
            if (productFeatureGroupId.length() > 20) {
                Debug.logWarning("Manufactured productFeatureGroupId was greater than 20 characters, means that we had some long productCategoryId and/or productFeatureTypeId values, at the category part should be unique since it is first, so if the feature type isn't unique it just means more than one type of feature will go into the category...", module);
                productFeatureGroupId = productFeatureGroupId.substring(0, 20);
            }
            
            GenericValue productFeatureGroup = delegator.findByPrimaryKey("ProductFeatureGroup", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId));
            if (productFeatureGroup == null) {
                // auto-create the group
                String description = "Feature Group for type [" + productFeatureTypeId + "] features in category [" + productCategoryId + "]";
                productFeatureGroup = delegator.makeValue("ProductFeatureGroup", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "description", description));
                productFeatureGroup.create();
                
                GenericValue productFeatureCatGrpAppl = delegator.makeValue("ProductFeatureCatGrpAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "productCategoryId", productCategoryId, "fromDate", nowTimestamp));
                productFeatureCatGrpAppl.create();
            }
            
            // now put all of the features in the group, if there is not already a valid feature placement there...
            Iterator productFeatureIdIter = productFeatureIdSet.iterator();
            while (productFeatureIdIter.hasNext()) {
                String productFeatureId = (String) productFeatureIdIter.next();
                EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                        new EntityExpr("productFeatureId", EntityOperator.EQUALS, productFeatureId),
                        new EntityExpr("productFeatureGroupId", EntityOperator.EQUALS, productFeatureGroupId),
                        new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                        new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                ), EntityOperator.AND);
                if (delegator.findCountByCondition("ProductFeatureGroupAppl", condition, null) == 0) {
                    // if no valid ones, create one
                    GenericValue productFeatureGroupAppl = delegator.makeValue("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "productFeatureId", productFeatureId, "fromDate", nowTimestamp));
                    productFeatureGroupAppl.create();
                }
            }
        }
        
        // now get all feature groups associated with sub-categories and associate them with this category
        Iterator subCategoryIter = subCategoryList.iterator();
        while (subCategoryIter.hasNext()) {
            GenericValue productCategoryRollup = (GenericValue) subCategoryIter.next();
            String subProductCategoryId = productCategoryRollup.getString("productCategoryId");
            EntityCondition condition = new EntityConditionList(UtilMisc.toList(
                    new EntityExpr("productCategoryId", EntityOperator.EQUALS, subProductCategoryId),
                    new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                    new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
            ), EntityOperator.AND);
            EntityListIterator productFeatureCatGrpApplEli = delegator.findListIteratorByCondition("ProductFeatureCatGrpAppl", condition, null, null);
            GenericValue productFeatureCatGrpAppl = null;
            while ((productFeatureCatGrpAppl = (GenericValue) productFeatureCatGrpApplEli.next()) != null) {
                String productFeatureGroupId = productFeatureCatGrpAppl.getString("productFeatureGroupId");
                EntityCondition checkCondition = new EntityConditionList(UtilMisc.toList(
                        new EntityExpr("productCategoryId", EntityOperator.EQUALS, productCategoryId),
                        new EntityExpr("productFeatureGroupId", EntityOperator.EQUALS, productFeatureGroupId),
                        new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp),
                        new EntityExpr(new EntityExpr("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp))
                ), EntityOperator.AND);
                if (delegator.findCountByCondition("ProductFeatureCatGrpAppl", checkCondition, null) == 0) {
                    // if no valid ones, create one
                    GenericValue productFeatureGroupAppl = delegator.makeValue("ProductFeatureCatGrpAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId, "productCategoryId", productCategoryId, "fromDate", nowTimestamp));
                    productFeatureGroupAppl.create();
                }
                
            }
        }
    }
}
