
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Classification Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductClass Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */
public class ProductClassHelper
{

  /** A static variable to cache the Home object for the ProductClass EJB */
  private static ProductClassHome productClassHome = null;

  /** Initializes the productClassHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductClassHome instance for the default EJB server
   */
  public static ProductClassHome getProductClassHome()
  {
    if(productClassHome == null) //don't want to block here
    {
      synchronized(ProductClassHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productClassHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductClassHome");
            productClassHome = (ProductClassHome)MyNarrow.narrow(homeObject, ProductClassHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productClass home obtained " + productClassHome);
        }
      }
    }
    return productClassHome;
  }



  /** Remove the ProductClass corresponding to the primaryKey specified by fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   */
  public static void removeByPrimaryKey(String productId, String productTypeId)
  {
    if(productId == null || productTypeId == null)
    {
      return;
    }
    ProductClassPK primaryKey = new ProductClassPK(productId, productTypeId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductClass corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.product.ProductClassPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductClass productClass = findByPrimaryKey(primaryKey);
    try
    {
      if(productClass != null)
      {
        productClass.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductClass by its Primary Key, specified by individual fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@return       The ProductClass corresponding to the primaryKey
   */
  public static ProductClass findByPrimaryKey(String productId, String productTypeId)
  {
    if(productId == null || productTypeId == null) return null;
    ProductClassPK primaryKey = new ProductClassPK(productId, productTypeId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductClass by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductClass corresponding to the primaryKey
   */
  public static ProductClass findByPrimaryKey(org.ofbiz.commonapp.product.product.ProductClassPK primaryKey)
  {
    ProductClass productClass = null;
    Debug.logInfo("ProductClassHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productClass = (ProductClass)MyNarrow.narrow(getProductClassHome().findByPrimaryKey(primaryKey), ProductClass.class);
      if(productClass != null)
      {
        productClass = productClass.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productClass;
  }

  /** Finds all ProductClass entities
   *@return    Collection containing all ProductClass entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductClassHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductClassHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductClass
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static ProductClass create(String productId, String productTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    ProductClass productClass = null;
    Debug.logInfo("ProductClassHelper.create: productId, productTypeId: " + productId + ", " + productTypeId);
    if(productId == null || productTypeId == null) { return null; }

    try { productClass = (ProductClass)MyNarrow.narrow(getProductClassHome().create(productId, productTypeId, fromDate, thruDate), ProductClass.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productClass with productId, productTypeId: " + productId + ", " + productTypeId);
      Debug.logError(ce);
      productClass = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productClass;
  }

  /** Updates the corresponding ProductClass
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static ProductClass update(String productId, String productTypeId, java.util.Date fromDate, java.util.Date thruDate) throws java.rmi.RemoteException
  {
    if(productId == null || productTypeId == null) { return null; }
    ProductClass productClass = findByPrimaryKey(productId, productTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductClass productClassValue = new ProductClassValue();

    if(fromDate != null) { productClassValue.setFromDate(fromDate); }
    if(thruDate != null) { productClassValue.setThruDate(thruDate); }

    productClass.setValueObject(productClassValue);
    return productClass;
  }

  /** Removes/deletes the specified  ProductClass
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
        ProductClass productClass = (ProductClass) iterator.next();
        Debug.logInfo("Removing productClass with productId:" + productId);
        productClass.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductClass records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductClassHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductClass
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   */
  public static void removeByProductTypeId(String productTypeId)
  {
    if(productTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductTypeId(productTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductClass productClass = (ProductClass) iterator.next();
        Debug.logInfo("Removing productClass with productTypeId:" + productTypeId);
        productClass.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductClass records by the following parameters:
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductTypeId(String productTypeId)
  {
    Debug.logInfo("findByProductTypeId: productTypeId:" + productTypeId);

    Collection collection = null;
    if(productTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductClassHome().findByProductTypeId(productTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
