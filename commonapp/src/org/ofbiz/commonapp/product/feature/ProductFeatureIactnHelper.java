
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Interaction Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeatureIactn Entity EJB; acts as a proxy for the Home interface
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
public class ProductFeatureIactnHelper
{

  /** A static variable to cache the Home object for the ProductFeatureIactn EJB */
  private static ProductFeatureIactnHome productFeatureIactnHome = null;

  /** Initializes the productFeatureIactnHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureIactnHome instance for the default EJB server
   */
  public static ProductFeatureIactnHome getProductFeatureIactnHome()
  {
    if(productFeatureIactnHome == null) //don't want to block here
    {
      synchronized(ProductFeatureIactnHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureIactnHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureIactnHome");
            productFeatureIactnHome = (ProductFeatureIactnHome)MyNarrow.narrow(homeObject, ProductFeatureIactnHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeatureIactn home obtained " + productFeatureIactnHome);
        }
      }
    }
    return productFeatureIactnHome;
  }



  /** Remove the ProductFeatureIactn corresponding to the primaryKey specified by fields
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   */
  public static void removeByPrimaryKey(String productFeatureId, String productFeatureIdTo)
  {
    if(productFeatureId == null || productFeatureIdTo == null)
    {
      return;
    }
    ProductFeatureIactnPK primaryKey = new ProductFeatureIactnPK(productFeatureId, productFeatureIdTo);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductFeatureIactn corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.feature.ProductFeatureIactnPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeatureIactn productFeatureIactn = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeatureIactn != null)
      {
        productFeatureIactn.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductFeatureIactn by its Primary Key, specified by individual fields
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@return       The ProductFeatureIactn corresponding to the primaryKey
   */
  public static ProductFeatureIactn findByPrimaryKey(String productFeatureId, String productFeatureIdTo)
  {
    if(productFeatureId == null || productFeatureIdTo == null) return null;
    ProductFeatureIactnPK primaryKey = new ProductFeatureIactnPK(productFeatureId, productFeatureIdTo);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductFeatureIactn by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeatureIactn corresponding to the primaryKey
   */
  public static ProductFeatureIactn findByPrimaryKey(org.ofbiz.commonapp.product.feature.ProductFeatureIactnPK primaryKey)
  {
    ProductFeatureIactn productFeatureIactn = null;
    Debug.logInfo("ProductFeatureIactnHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeatureIactn = (ProductFeatureIactn)MyNarrow.narrow(getProductFeatureIactnHome().findByPrimaryKey(primaryKey), ProductFeatureIactn.class);
      if(productFeatureIactn != null)
      {
        productFeatureIactn = productFeatureIactn.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureIactn;
  }

  /** Finds all ProductFeatureIactn entities
   *@return    Collection containing all ProductFeatureIactn entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureIactnHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureIactnHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeatureIactn
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureIactn create(String productFeatureId, String productFeatureIdTo, String productFeatureIactnTypeId, String productId)
  {
    ProductFeatureIactn productFeatureIactn = null;
    Debug.logInfo("ProductFeatureIactnHelper.create: productFeatureId, productFeatureIdTo: " + productFeatureId + ", " + productFeatureIdTo);
    if(productFeatureId == null || productFeatureIdTo == null) { return null; }

    try { productFeatureIactn = (ProductFeatureIactn)MyNarrow.narrow(getProductFeatureIactnHome().create(productFeatureId, productFeatureIdTo, productFeatureIactnTypeId, productId), ProductFeatureIactn.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeatureIactn with productFeatureId, productFeatureIdTo: " + productFeatureId + ", " + productFeatureIdTo);
      Debug.logError(ce);
      productFeatureIactn = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureIactn;
  }

  /** Updates the corresponding ProductFeatureIactn
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureIactn update(String productFeatureId, String productFeatureIdTo, String productFeatureIactnTypeId, String productId) throws java.rmi.RemoteException
  {
    if(productFeatureId == null || productFeatureIdTo == null) { return null; }
    ProductFeatureIactn productFeatureIactn = findByPrimaryKey(productFeatureId, productFeatureIdTo);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeatureIactn productFeatureIactnValue = new ProductFeatureIactnValue();

    if(productFeatureIactnTypeId != null) { productFeatureIactnValue.setProductFeatureIactnTypeId(productFeatureIactnTypeId); }
    if(productId != null) { productFeatureIactnValue.setProductId(productId); }

    productFeatureIactn.setValueObject(productFeatureIactnValue);
    return productFeatureIactn;
  }

  /** Removes/deletes the specified  ProductFeatureIactn
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByProductFeatureId(String productFeatureId)
  {
    if(productFeatureId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureId(productFeatureId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureIactn productFeatureIactn = (ProductFeatureIactn) iterator.next();
        Debug.logInfo("Removing productFeatureIactn with productFeatureId:" + productFeatureId);
        productFeatureIactn.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureIactn records by the following parameters:
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureId(String productFeatureId)
  {
    Debug.logInfo("findByProductFeatureId: productFeatureId:" + productFeatureId);

    Collection collection = null;
    if(productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureIactnHome().findByProductFeatureId(productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureIactn
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   */
  public static void removeByProductFeatureIdTo(String productFeatureIdTo)
  {
    if(productFeatureIdTo == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureIdTo(productFeatureIdTo));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureIactn productFeatureIactn = (ProductFeatureIactn) iterator.next();
        Debug.logInfo("Removing productFeatureIactn with productFeatureIdTo:" + productFeatureIdTo);
        productFeatureIactn.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureIactn records by the following parameters:
   *@param  productFeatureIdTo                  Field of the PRODUCT_FEATURE_ID_TO column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureIdTo(String productFeatureIdTo)
  {
    Debug.logInfo("findByProductFeatureIdTo: productFeatureIdTo:" + productFeatureIdTo);

    Collection collection = null;
    if(productFeatureIdTo == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureIactnHome().findByProductFeatureIdTo(productFeatureIdTo), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureIactn
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   */
  public static void removeByProductFeatureIactnTypeId(String productFeatureIactnTypeId)
  {
    if(productFeatureIactnTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureIactnTypeId(productFeatureIactnTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureIactn productFeatureIactn = (ProductFeatureIactn) iterator.next();
        Debug.logInfo("Removing productFeatureIactn with productFeatureIactnTypeId:" + productFeatureIactnTypeId);
        productFeatureIactn.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureIactn records by the following parameters:
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureIactnTypeId(String productFeatureIactnTypeId)
  {
    Debug.logInfo("findByProductFeatureIactnTypeId: productFeatureIactnTypeId:" + productFeatureIactnTypeId);

    Collection collection = null;
    if(productFeatureIactnTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureIactnHome().findByProductFeatureIactnTypeId(productFeatureIactnTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureIactn
   *@param  productId                  Field of the PRODUCT_ID column.
   */
  public static void removeByProductId(String productId)
  {
    if(productId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductId(productId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureIactn productFeatureIactn = (ProductFeatureIactn) iterator.next();
        Debug.logInfo("Removing productFeatureIactn with productId:" + productId);
        productFeatureIactn.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureIactn records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureIactnHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
