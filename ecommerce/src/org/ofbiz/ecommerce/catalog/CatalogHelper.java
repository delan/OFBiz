/*
 * $Id$
 * $Log$
 * Revision 1.3  2001/09/01 01:59:44  azeneski
 * Cleaned up catalog files, using new taglibs.
 *
 * Revision 1.2  2001/08/27 17:29:31  epabst
 * simplified
 *
 * Revision 1.1.1.1  2001/08/24 01:01:43  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.servlet.jsp.PageContext;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilMisc;

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
    
    public static void getRelatedCategories(PageContext pageContext, String attributeName, String parentId) {        
        ArrayList categories = new ArrayList();
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        Collection rollups = helper.findByAnd("ProductCategoryRollup",UtilMisc.toMap("parentProductCategoryId",parentId),null);
        
        Debug.log("Got rollups...");
        if ( rollups != null && rollups.size() > 0 ) {
            Debug.log("Rollup size: " + rollups.size());            
            Iterator ri = rollups.iterator();
            while ( ri.hasNext() ) {               
                GenericValue parent = (GenericValue) ri.next();
                Debug.log("Adding children of: " + parent.getString("parentProductCategoryId"));
                Collection cc = helper.getRelated("CurrentProductCategory",parent);
                if ( cc != null && cc.size() > 0 )
                    categories.addAll(cc);                
            }
        }
        
        if ( categories.size() > 0 ) 
            pageContext.setAttribute(attributeName,categories);
    }
    
    public static void getRelatedProducts(PageContext pageContext, String attributeName, String parentId) {                
        ArrayList products = new ArrayList();
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        GenericValue category = helper.findByPrimaryKey("ProductCategory",UtilMisc.toMap("productCategoryId",parentId));
        
        if ( category != null ) {
            Collection p = helper.getRelated("PrimaryProduct",category);
            products.addAll(p);
        }            
        
        if ( products.size() > 0 )
            pageContext.setAttribute(attributeName,products);
    }
    
    public static void getProduct(PageContext pageContext, String attributeName, String productId) {    
        GenericValue product = null;
        GenericHelper helper = GenericHelperFactory.getDefaultHelper();
        product = helper.findByPrimaryKey("Product",UtilMisc.toMap("productId", productId));
        if ( product != null )
            pageContext.setAttribute(attributeName,product);
    }
    
}