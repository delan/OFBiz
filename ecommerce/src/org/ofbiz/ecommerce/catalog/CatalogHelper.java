/*
 * $Id$
 * $Log$
 */

package org.ofbiz.ecommerce.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;

import org.ofbiz.core.entity.GenericPK;
import org.ofbiz.core.entity.GenericEntity;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.entity.GenericHelper;
import org.ofbiz.core.entity.GenericHelperFactory;

/**
 * <p><b>Title:</b> CatalogHelper.java
 * <p><b>Description:</b> Helper class to reduce code in JSPs.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 23, 2001, 7:58 PM
 */
public class CatalogHelper {
    
    public static Collection getRelatedCategories(String parentId) {
        if ( parentId == null )
            return null;
        
        ArrayList categories = new ArrayList();
        HashMap ifm = new HashMap();
        ifm.put("parentProductCategoryId",parentId);
        
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        Collection rollups = helper.findByAnd("ProductCategoryRollup",ifm,null);
        
        Iterator ri = rollups.iterator();
        while ( ri.hasNext() ) {
            HashMap cfm = new HashMap();
            GenericValue value = (GenericValue) ri.next();
            cfm.put("productCategoryId",value.getString("productCategoryId"));
            Collection cc = helper.findByAnd("ProductCategory",cfm,null);
            if ( cc != null )
                categories.addAll(cc);
        }
        
        return categories;
    }
    
    public static Collection getRelatedProducts(String parentId) {
        if ( parentId == null )
            return null;
        
        Collection products = null;
        HashMap ifm = new HashMap();
        ifm.put("productCategoryId",parentId);
        
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        List category = (List) helper.findByAnd("ProductCategory",ifm,null);
        if ( category != null ) {
            GenericValue value = (GenericValue) category.get(0);
            Debug.log("CatalogHelper: " + value.getEntityName());
            products = helper.getRelated("PrimaryProduct",value);
            Debug.log("CatalogHelper: " + products.size());
        }
        else {
            Debug.log("CatalogHelper: List is null.");
        }
        
        return products;
    }
    
    public static GenericValue getProduct(String productId) {
        if ( productId == null )
            return null;
        HashMap ifm = new HashMap();
        ifm.put("productId",productId);
        
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        List productList = (List) helper.findByAnd("Product",ifm,null);
        if ( productList == null )
            return null;
        
        return (GenericValue) productList.get(0);
    }
    
}