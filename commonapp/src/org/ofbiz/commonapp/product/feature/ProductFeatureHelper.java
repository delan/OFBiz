
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeature Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:27 MDT 2001
 *@version    1.0
 */
public class ProductFeatureHelper
{

  /** A static variable to cache the Home object for the ProductFeature EJB */
  private static ProductFeatureHome productFeatureHome = null;

  /** Initializes the productFeatureHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureHome instance for the default EJB server
   */
  public static ProductFeatureHome getProductFeatureHome()
  {
    if(productFeatureHome == null) //don't want to block here
    {
      synchronized(ProductFeatureHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureHome");
            productFeatureHome = (ProductFeatureHome)MyNarrow.narrow(homeObject, ProductFeatureHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeature home obtained " + productFeatureHome);
        }
      }
    }
    return productFeatureHome;
  }




  /** Remove the ProductFeature corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeature productFeature = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeature != null)
      {
        productFeature.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductFeature by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeature corresponding to the primaryKey
   */
  public static ProductFeature findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductFeature productFeature = null;
    Debug.logInfo("ProductFeatureHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeature = (ProductFeature)MyNarrow.narrow(getProductFeatureHome().findByPrimaryKey(primaryKey), ProductFeature.class);
      if(productFeature != null)
      {
        productFeature = productFeature.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeature;
  }

  /** Finds all ProductFeature entities
   *@return    Collection containing all ProductFeature entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeature
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  numberSpecified                  Field of the NUMBER_SPECIFIED column.
   *@return                Description of the Returned Value
   */
  public static ProductFeature create(String productFeatureId, String productFeatureTypeId, String productFeatureCategoryId, String description, String uomId, Long numberSpecified)
  {
    ProductFeature productFeature = null;
    Debug.logInfo("ProductFeatureHelper.create: productFeatureId: " + productFeatureId);
    if(productFeatureId == null) { return null; }

    try { productFeature = (ProductFeature)MyNarrow.narrow(getProductFeatureHome().create(productFeatureId, productFeatureTypeId, productFeatureCategoryId, description, uomId, numberSpecified), ProductFeature.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeature with productFeatureId: " + productFeatureId);
      Debug.logError(ce);
      productFeature = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeature;
  }

  /** Updates the corresponding ProductFeature
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  numberSpecified                  Field of the NUMBER_SPECIFIED column.
   *@return                Description of the Returned Value
   */
  public static ProductFeature update(String productFeatureId, String productFeatureTypeId, String productFeatureCategoryId, String description, String uomId, Long numberSpecified) throws java.rmi.RemoteException
  {
    if(productFeatureId == null) { return null; }
    ProductFeature productFeature = findByPrimaryKey(productFeatureId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeature productFeatureValue = new ProductFeatureValue();

    if(productFeatureTypeId != null) { productFeatureValue.setProductFeatureTypeId(productFeatureTypeId); }
    if(productFeatureCategoryId != null) { productFeatureValue.setProductFeatureCategoryId(productFeatureCategoryId); }
    if(description != null) { productFeatureValue.setDescription(description); }
    if(uomId != null) { productFeatureValue.setUomId(uomId); }
    if(numberSpecified != null) { productFeatureValue.setNumberSpecified(numberSpecified); }

    productFeature.setValueObject(productFeatureValue);
    return productFeature;
  }

  /** Removes/deletes the specified  ProductFeature
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   */
  public static void removeByProductFeatureTypeId(String productFeatureTypeId)
  {
    if(productFeatureTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureTypeId(productFeatureTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeature productFeature = (ProductFeature) iterator.next();
        Debug.logInfo("Removing productFeature with productFeatureTypeId:" + productFeatureTypeId);
        productFeature.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeature records by the following parameters:
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureTypeId(String productFeatureTypeId)
  {
    Debug.logInfo("findByProductFeatureTypeId: productFeatureTypeId:" + productFeatureTypeId);

    Collection collection = null;
    if(productFeatureTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureHome().findByProductFeatureTypeId(productFeatureTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeature
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   */
  public static void removeByProductFeatureCategoryId(String productFeatureCategoryId)
  {
    if(productFeatureCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureCategoryId(productFeatureCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeature productFeature = (ProductFeature) iterator.next();
        Debug.logInfo("Removing productFeature with productFeatureCategoryId:" + productFeatureCategoryId);
        productFeature.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeature records by the following parameters:
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureCategoryId(String productFeatureCategoryId)
  {
    Debug.logInfo("findByProductFeatureCategoryId: productFeatureCategoryId:" + productFeatureCategoryId);

    Collection collection = null;
    if(productFeatureCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureHome().findByProductFeatureCategoryId(productFeatureCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeature
   *@param  uomId                  Field of the UOM_ID column.
   */
  public static void removeByUomId(String uomId)
  {
    if(uomId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUomId(uomId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeature productFeature = (ProductFeature) iterator.next();
        Debug.logInfo("Removing productFeature with uomId:" + uomId);
        productFeature.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeature records by the following parameters:
   *@param  uomId                  Field of the UOM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomId(String uomId)
  {
    Debug.logInfo("findByUomId: uomId:" + uomId);

    Collection collection = null;
    if(uomId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureHome().findByUomId(uomId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
