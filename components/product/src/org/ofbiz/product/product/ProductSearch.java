/*
 * $Id: ProductSearch.java,v 1.1 2003/10/16 09:23:26 jonesde Exp $
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
package components.product.src.org.ofbiz.product.product;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import javax.servlet.ServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.product.product.KeywordSearch;

/**
 *  Utilities for product search based on various constraints including categories, features and keywords.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class ProductSearch {
    
    public static final String module = ProductSearch.class.getName();
    
    public static ArrayList parametricKeywordSearch(Map featureIdByType, String keywordsString, GenericDelegator delegator, String productCategoryId, String visitId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        // TODO: implement this for the new features
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        // make view-entity & EntityCondition
        List entityConditionList = new LinkedList();
        DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
        
        dynamicViewEntity.addMemberEntity("PCM", "ProductCategoryMember");
        dynamicViewEntity.addAliasAll("PCM", "pcm");
        entityConditionList.add(new EntityExpr("pcmProductCategoryId", EntityOperator.EQUALS, productCategoryId));
        entityConditionList.add(new EntityExpr(new EntityExpr("pcmThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, new EntityExpr("pcmThruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
        entityConditionList.add(new EntityExpr("pcmFromDate", EntityOperator.LESS_THAN, nowTimestamp));
        
        
        if ("OR".equals(intraKeywordOperator)) {
            dynamicViewEntity.addMemberEntity("PK1", "ProductKeyword");
            dynamicViewEntity.addAlias("PK1", "totalWeight", "relevancyWeight", null, null, null, "sum");
            
        } else if ("AND".equals(intraKeywordOperator)) {
        }
        
        
        return null;
    }

    public static ArrayList productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, String visitId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        // TODO: implement this for the new features
        return null;
    }
    
    public static ArrayList searchProducts(ProductSearchConstraint productSearchConstraint, GenericDelegator delegator) {
        // TODO: implement this for the new features
        EntityCondition entityCondition = productSearchConstraint.makeEntityCondition(delegator);
        
        return null;
    }
    
    public static abstract class ProductSearchConstraint {
        public ProductSearchConstraint() {
        }
        
        public abstract EntityCondition makeEntityCondition(GenericDelegator delegator);
    }
    
    public static class ProductSearchConstraintList extends ProductSearchConstraint {
        protected List productSearchConstraints = new LinkedList();
        
        public ProductSearchConstraintList(List productSearchConstraints) {
            this.productSearchConstraints.addAll(productSearchConstraints);
        }
        
        public void addProductSearchConstraint(ProductSearchConstraint productSearchConstraint) {
            this.productSearchConstraints.add(productSearchConstraint);
        }
        
        public EntityCondition makeEntityCondition(GenericDelegator delegator) {
            // TODO: implement ProductSearchConstraintList makeEntityCondition
            return null;
        }
    }
    
    public static class CategoryConstraint extends ProductSearchConstraint {
        protected String productCategoryId;
        protected boolean includeSubCategories;
        
        public CategoryConstraint(String productCategoryId, boolean includeSubCategories) {
            this.productCategoryId = productCategoryId;
            this.includeSubCategories = includeSubCategories;
        }
        
        public EntityCondition makeEntityCondition(GenericDelegator delegator) {
            // TODO: implement CategoryConstraint makeEntityCondition
            return null;
        }
    }
    
    public static class FeatureConstraint extends ProductSearchConstraint {
        protected String productFeatureId;
        
        public FeatureConstraint(String productFeatureId) {
            this.productFeatureId = productFeatureId;
        }
        
        public EntityCondition makeEntityCondition(GenericDelegator delegator) {
            // TODO: implement FeatureConstraint makeEntityCondition
            return null;
        }
    }
    
    public static class KeywordConstraint extends ProductSearchConstraint {
        protected String keywordsString;
        protected boolean anyPrefix;
        protected boolean anySuffix;
        protected String intraKeywordOperator;
        
        public KeywordConstraint(String keywordsString, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
            this.keywordsString = keywordsString;
            this.anyPrefix = anyPrefix;
            this.anySuffix = anySuffix;
            this.intraKeywordOperator = intraKeywordOperator.toUpperCase();
            if (this.intraKeywordOperator == null || (!"AND".equals(this.intraKeywordOperator) && !"OR".equals(this.intraKeywordOperator))) {
                Debug.logWarning("intraKeywordOperator [" + this.intraKeywordOperator + "] was not valid, defaulting to OR", module);
                this.intraKeywordOperator = "OR";
            }
        }
        
        public EntityCondition makeEntityCondition(GenericDelegator delegator) {
            // TODO: implement KeywordConstraint makeEntityCondition
            return null;
        }
    }
    
    public static class LastUpdatedRangeConstraint extends ProductSearchConstraint {
        protected Timestamp fromDate;
        protected Timestamp thruDate;
        
        public LastUpdatedRangeConstraint(Timestamp fromDate, Timestamp thruDate) {
            this.fromDate = fromDate;
            this.thruDate = thruDate;
        }
        
        public EntityCondition makeEntityCondition(GenericDelegator delegator) {
            // TODO: implement FeatureConstraint makeEntityCondition
            return null;
        }
    }
}
