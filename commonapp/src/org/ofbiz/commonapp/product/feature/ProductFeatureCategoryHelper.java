
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Category Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeatureCategory Entity EJB; acts as a proxy for the Home interface
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     David E. Jones
 *@created    Fri Jul 27 01:18:28 MDT 2001
 *@version    1.0
 */
public class ProductFeatureCategoryHelper
{

  /** A static variable to cache the Home object for the ProductFeatureCategory EJB */
  private static ProductFeatureCategoryHome productFeatureCategoryHome = null;

  /** Initializes the productFeatureCategoryHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureCategoryHome instance for the default EJB server
   */
  public static ProductFeatureCategoryHome getProductFeatureCategoryHome()
  {
    if(productFeatureCategoryHome == null) //don't want to block here
    {
      synchronized(ProductFeatureCategoryHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureCategoryHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureCategoryHome");
            productFeatureCategoryHome = (ProductFeatureCategoryHome)MyNarrow.narrow(homeObject, ProductFeatureCategoryHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeatureCategory home obtained " + productFeatureCategoryHome);
        }
      }
    }
    return productFeatureCategoryHome;
  }




  /** Remove the ProductFeatureCategory corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeatureCategory productFeatureCategory = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeatureCategory != null)
      {
        productFeatureCategory.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductFeatureCategory by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeatureCategory corresponding to the primaryKey
   */
  public static ProductFeatureCategory findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductFeatureCategory productFeatureCategory = null;
    Debug.logInfo("ProductFeatureCategoryHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeatureCategory = (ProductFeatureCategory)MyNarrow.narrow(getProductFeatureCategoryHome().findByPrimaryKey(primaryKey), ProductFeatureCategory.class);
      if(productFeatureCategory != null)
      {
        productFeatureCategory = productFeatureCategory.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureCategory;
  }

  /** Finds all ProductFeatureCategory entities
   *@return    Collection containing all ProductFeatureCategory entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureCategoryHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureCategoryHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeatureCategory
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@param  parentCategoryId                  Field of the PARENT_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureCategory create(String productFeatureCategoryId, String parentCategoryId, String description)
  {
    ProductFeatureCategory productFeatureCategory = null;
    Debug.logInfo("ProductFeatureCategoryHelper.create: productFeatureCategoryId: " + productFeatureCategoryId);
    if(productFeatureCategoryId == null) { return null; }

    try { productFeatureCategory = (ProductFeatureCategory)MyNarrow.narrow(getProductFeatureCategoryHome().create(productFeatureCategoryId, parentCategoryId, description), ProductFeatureCategory.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeatureCategory with productFeatureCategoryId: " + productFeatureCategoryId);
      Debug.logError(ce);
      productFeatureCategory = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureCategory;
  }

  /** Updates the corresponding ProductFeatureCategory
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@param  parentCategoryId                  Field of the PARENT_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureCategory update(String productFeatureCategoryId, String parentCategoryId, String description) throws java.rmi.RemoteException
  {
    if(productFeatureCategoryId == null) { return null; }
    ProductFeatureCategory productFeatureCategory = findByPrimaryKey(productFeatureCategoryId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeatureCategory productFeatureCategoryValue = new ProductFeatureCategoryValue();

    if(parentCategoryId != null) { productFeatureCategoryValue.setParentCategoryId(parentCategoryId); }
    if(description != null) { productFeatureCategoryValue.setDescription(description); }

    productFeatureCategory.setValueObject(productFeatureCategoryValue);
    return productFeatureCategory;
  }

  /** Removes/deletes the specified  ProductFeatureCategory
   *@param  parentCategoryId                  Field of the PARENT_CATEGORY_ID column.
   */
  public static void removeByParentCategoryId(String parentCategoryId)
  {
    if(parentCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentCategoryId(parentCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureCategory productFeatureCategory = (ProductFeatureCategory) iterator.next();
        Debug.logInfo("Removing productFeatureCategory with parentCategoryId:" + parentCategoryId);
        productFeatureCategory.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureCategory records by the following parameters:
   *@param  parentCategoryId                  Field of the PARENT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentCategoryId(String parentCategoryId)
  {
    Debug.logInfo("findByParentCategoryId: parentCategoryId:" + parentCategoryId);

    Collection collection = null;
    if(parentCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureCategoryHome().findByParentCategoryId(parentCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
