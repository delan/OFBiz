
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Data Object Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductDataObject Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:26 MDT 2001
 *@version    1.0
 */
public class ProductDataObjectHelper
{

  /** A static variable to cache the Home object for the ProductDataObject EJB */
  private static ProductDataObjectHome productDataObjectHome = null;

  /** Initializes the productDataObjectHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductDataObjectHome instance for the default EJB server
   */
  public static ProductDataObjectHome getProductDataObjectHome()
  {
    if(productDataObjectHome == null) //don't want to block here
    {
      synchronized(ProductDataObjectHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productDataObjectHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductDataObjectHome");
            productDataObjectHome = (ProductDataObjectHome)MyNarrow.narrow(homeObject, ProductDataObjectHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productDataObject home obtained " + productDataObjectHome);
        }
      }
    }
    return productDataObjectHome;
  }



  /** Remove the ProductDataObject corresponding to the primaryKey specified by fields
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   */
  public static void removeByPrimaryKey(String dataObjectId, String productId)
  {
    if(dataObjectId == null || productId == null)
    {
      return;
    }
    ProductDataObjectPK primaryKey = new ProductDataObjectPK(dataObjectId, productId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductDataObject corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.product.ProductDataObjectPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductDataObject productDataObject = findByPrimaryKey(primaryKey);
    try
    {
      if(productDataObject != null)
      {
        productDataObject.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductDataObject by its Primary Key, specified by individual fields
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return       The ProductDataObject corresponding to the primaryKey
   */
  public static ProductDataObject findByPrimaryKey(String dataObjectId, String productId)
  {
    if(dataObjectId == null || productId == null) return null;
    ProductDataObjectPK primaryKey = new ProductDataObjectPK(dataObjectId, productId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductDataObject by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductDataObject corresponding to the primaryKey
   */
  public static ProductDataObject findByPrimaryKey(org.ofbiz.commonapp.product.product.ProductDataObjectPK primaryKey)
  {
    ProductDataObject productDataObject = null;
    Debug.logInfo("ProductDataObjectHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productDataObject = (ProductDataObject)MyNarrow.narrow(getProductDataObjectHome().findByPrimaryKey(primaryKey), ProductDataObject.class);
      if(productDataObject != null)
      {
        productDataObject = productDataObject.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productDataObject;
  }

  /** Finds all ProductDataObject entities
   *@return    Collection containing all ProductDataObject entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductDataObjectHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductDataObjectHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductDataObject
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                Description of the Returned Value
   */
  public static ProductDataObject create(String dataObjectId, String productId)
  {
    ProductDataObject productDataObject = null;
    Debug.logInfo("ProductDataObjectHelper.create: dataObjectId, productId: " + dataObjectId + ", " + productId);
    if(dataObjectId == null || productId == null) { return null; }

    try { productDataObject = (ProductDataObject)MyNarrow.narrow(getProductDataObjectHome().create(dataObjectId, productId), ProductDataObject.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productDataObject with dataObjectId, productId: " + dataObjectId + ", " + productId);
      Debug.logError(ce);
      productDataObject = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productDataObject;
  }

  /** Updates the corresponding ProductDataObject
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                Description of the Returned Value
   */
  public static ProductDataObject update(String dataObjectId, String productId) throws java.rmi.RemoteException
  {
    if(dataObjectId == null || productId == null) { return null; }
    ProductDataObject productDataObject = findByPrimaryKey(dataObjectId, productId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductDataObject productDataObjectValue = new ProductDataObjectValue();


    productDataObject.setValueObject(productDataObjectValue);
    return productDataObject;
  }

  /** Removes/deletes the specified  ProductDataObject
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   */
  public static void removeByDataObjectId(String dataObjectId)
  {
    if(dataObjectId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByDataObjectId(dataObjectId));

    while(iterator.hasNext())
    {
      try
      {
        ProductDataObject productDataObject = (ProductDataObject) iterator.next();
        Debug.logInfo("Removing productDataObject with dataObjectId:" + dataObjectId);
        productDataObject.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductDataObject records by the following parameters:
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByDataObjectId(String dataObjectId)
  {
    Debug.logInfo("findByDataObjectId: dataObjectId:" + dataObjectId);

    Collection collection = null;
    if(dataObjectId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductDataObjectHome().findByDataObjectId(dataObjectId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductDataObject
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
        ProductDataObject productDataObject = (ProductDataObject) iterator.next();
        Debug.logInfo("Removing productDataObject with productId:" + productId);
        productDataObject.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductDataObject records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductDataObjectHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
