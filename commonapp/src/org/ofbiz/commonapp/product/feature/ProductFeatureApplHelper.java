
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Applicability Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeatureAppl Entity EJB; acts as a proxy for the Home interface
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
public class ProductFeatureApplHelper
{

  /** A static variable to cache the Home object for the ProductFeatureAppl EJB */
  private static ProductFeatureApplHome productFeatureApplHome = null;

  /** Initializes the productFeatureApplHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureApplHome instance for the default EJB server
   */
  public static ProductFeatureApplHome getProductFeatureApplHome()
  {
    if(productFeatureApplHome == null) //don't want to block here
    {
      synchronized(ProductFeatureApplHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureApplHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureApplHome");
            productFeatureApplHome = (ProductFeatureApplHome)MyNarrow.narrow(homeObject, ProductFeatureApplHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeatureAppl home obtained " + productFeatureApplHome);
        }
      }
    }
    return productFeatureApplHome;
  }



  /** Remove the ProductFeatureAppl corresponding to the primaryKey specified by fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByPrimaryKey(String productId, String productFeatureId)
  {
    if(productId == null || productFeatureId == null)
    {
      return;
    }
    ProductFeatureApplPK primaryKey = new ProductFeatureApplPK(productId, productFeatureId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductFeatureAppl corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.feature.ProductFeatureApplPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeatureAppl productFeatureAppl = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeatureAppl != null)
      {
        productFeatureAppl.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductFeatureAppl by its Primary Key, specified by individual fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return       The ProductFeatureAppl corresponding to the primaryKey
   */
  public static ProductFeatureAppl findByPrimaryKey(String productId, String productFeatureId)
  {
    if(productId == null || productFeatureId == null) return null;
    ProductFeatureApplPK primaryKey = new ProductFeatureApplPK(productId, productFeatureId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductFeatureAppl by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeatureAppl corresponding to the primaryKey
   */
  public static ProductFeatureAppl findByPrimaryKey(org.ofbiz.commonapp.product.feature.ProductFeatureApplPK primaryKey)
  {
    ProductFeatureAppl productFeatureAppl = null;
    Debug.logInfo("ProductFeatureApplHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeatureAppl = (ProductFeatureAppl)MyNarrow.narrow(getProductFeatureApplHome().findByPrimaryKey(primaryKey), ProductFeatureAppl.class);
      if(productFeatureAppl != null)
      {
        productFeatureAppl = productFeatureAppl.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureAppl;
  }

  /** Finds all ProductFeatureAppl entities
   *@return    Collection containing all ProductFeatureAppl entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureApplHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureApplHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeatureAppl
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureApplTypeId                  Field of the PRODUCT_FEATURE_APPL_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureAppl create(String productId, String productFeatureId, String productFeatureApplTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    ProductFeatureAppl productFeatureAppl = null;
    Debug.logInfo("ProductFeatureApplHelper.create: productId, productFeatureId: " + productId + ", " + productFeatureId);
    if(productId == null || productFeatureId == null) { return null; }

    try { productFeatureAppl = (ProductFeatureAppl)MyNarrow.narrow(getProductFeatureApplHome().create(productId, productFeatureId, productFeatureApplTypeId, fromDate, thruDate), ProductFeatureAppl.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeatureAppl with productId, productFeatureId: " + productId + ", " + productFeatureId);
      Debug.logError(ce);
      productFeatureAppl = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureAppl;
  }

  /** Updates the corresponding ProductFeatureAppl
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureApplTypeId                  Field of the PRODUCT_FEATURE_APPL_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureAppl update(String productId, String productFeatureId, String productFeatureApplTypeId, java.util.Date fromDate, java.util.Date thruDate) throws java.rmi.RemoteException
  {
    if(productId == null || productFeatureId == null) { return null; }
    ProductFeatureAppl productFeatureAppl = findByPrimaryKey(productId, productFeatureId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeatureAppl productFeatureApplValue = new ProductFeatureApplValue();

    if(productFeatureApplTypeId != null) { productFeatureApplValue.setProductFeatureApplTypeId(productFeatureApplTypeId); }
    if(fromDate != null) { productFeatureApplValue.setFromDate(fromDate); }
    if(thruDate != null) { productFeatureApplValue.setThruDate(thruDate); }

    productFeatureAppl.setValueObject(productFeatureApplValue);
    return productFeatureAppl;
  }

  /** Removes/deletes the specified  ProductFeatureAppl
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
        ProductFeatureAppl productFeatureAppl = (ProductFeatureAppl) iterator.next();
        Debug.logInfo("Removing productFeatureAppl with productId:" + productId);
        productFeatureAppl.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureAppl records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureApplHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureAppl
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
        ProductFeatureAppl productFeatureAppl = (ProductFeatureAppl) iterator.next();
        Debug.logInfo("Removing productFeatureAppl with productFeatureId:" + productFeatureId);
        productFeatureAppl.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureAppl records by the following parameters:
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureId(String productFeatureId)
  {
    Debug.logInfo("findByProductFeatureId: productFeatureId:" + productFeatureId);

    Collection collection = null;
    if(productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureApplHome().findByProductFeatureId(productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureAppl
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByProductIdAndProductFeatureId(String productId, String productFeatureId)
  {
    if(productId == null || productFeatureId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdAndProductFeatureId(productId, productFeatureId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureAppl productFeatureAppl = (ProductFeatureAppl) iterator.next();
        Debug.logInfo("Removing productFeatureAppl with productId, productFeatureId:" + productId + ", " + productFeatureId);
        productFeatureAppl.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureAppl records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdAndProductFeatureId(String productId, String productFeatureId)
  {
    Debug.logInfo("findByProductIdAndProductFeatureId: productId, productFeatureId:" + productId + ", " + productFeatureId);

    Collection collection = null;
    if(productId == null || productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureApplHome().findByProductIdAndProductFeatureId(productId, productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureAppl
   *@param  productFeatureApplTypeId                  Field of the PRODUCT_FEATURE_APPL_TYPE_ID column.
   */
  public static void removeByProductFeatureApplTypeId(String productFeatureApplTypeId)
  {
    if(productFeatureApplTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureApplTypeId(productFeatureApplTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductFeatureAppl productFeatureAppl = (ProductFeatureAppl) iterator.next();
        Debug.logInfo("Removing productFeatureAppl with productFeatureApplTypeId:" + productFeatureApplTypeId);
        productFeatureAppl.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureAppl records by the following parameters:
   *@param  productFeatureApplTypeId                  Field of the PRODUCT_FEATURE_APPL_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureApplTypeId(String productFeatureApplTypeId)
  {
    Debug.logInfo("findByProductFeatureApplTypeId: productFeatureApplTypeId:" + productFeatureApplTypeId);

    Collection collection = null;
    if(productFeatureApplTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureApplHome().findByProductFeatureApplTypeId(productFeatureApplTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
