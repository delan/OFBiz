
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Classification Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategoryClass Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryClassHelper
{

  /** A static variable to cache the Home object for the ProductCategoryClass EJB */
  private static ProductCategoryClassHome productCategoryClassHome = null;

  /** Initializes the productCategoryClassHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryClassHome instance for the default EJB server
   */
  public static ProductCategoryClassHome getProductCategoryClassHome()
  {
    if(productCategoryClassHome == null) //don't want to block here
    {
      synchronized(ProductCategoryClassHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryClassHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryClassHome");
            productCategoryClassHome = (ProductCategoryClassHome)MyNarrow.narrow(homeObject, ProductCategoryClassHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategoryClass home obtained " + productCategoryClassHome);
        }
      }
    }
    return productCategoryClassHome;
  }



  /** Remove the ProductCategoryClass corresponding to the primaryKey specified by fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   */
  public static void removeByPrimaryKey(String productCategoryId, String productCategoryTypeId)
  {
    if(productCategoryId == null || productCategoryTypeId == null)
    {
      return;
    }
    ProductCategoryClassPK primaryKey = new ProductCategoryClassPK(productCategoryId, productCategoryTypeId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductCategoryClass corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryClassPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategoryClass productCategoryClass = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategoryClass != null)
      {
        productCategoryClass.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductCategoryClass by its Primary Key, specified by individual fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@return       The ProductCategoryClass corresponding to the primaryKey
   */
  public static ProductCategoryClass findByPrimaryKey(String productCategoryId, String productCategoryTypeId)
  {
    if(productCategoryId == null || productCategoryTypeId == null) return null;
    ProductCategoryClassPK primaryKey = new ProductCategoryClassPK(productCategoryId, productCategoryTypeId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductCategoryClass by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategoryClass corresponding to the primaryKey
   */
  public static ProductCategoryClass findByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryClassPK primaryKey)
  {
    ProductCategoryClass productCategoryClass = null;
    Debug.logInfo("ProductCategoryClassHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategoryClass = (ProductCategoryClass)MyNarrow.narrow(getProductCategoryClassHome().findByPrimaryKey(primaryKey), ProductCategoryClass.class);
      if(productCategoryClass != null)
      {
        productCategoryClass = productCategoryClass.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryClass;
  }

  /** Finds all ProductCategoryClass entities
   *@return    Collection containing all ProductCategoryClass entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryClassHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryClassHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategoryClass
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryClass create(String productCategoryId, String productCategoryTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    ProductCategoryClass productCategoryClass = null;
    Debug.logInfo("ProductCategoryClassHelper.create: productCategoryId, productCategoryTypeId: " + productCategoryId + ", " + productCategoryTypeId);
    if(productCategoryId == null || productCategoryTypeId == null) { return null; }

    try { productCategoryClass = (ProductCategoryClass)MyNarrow.narrow(getProductCategoryClassHome().create(productCategoryId, productCategoryTypeId, fromDate, thruDate), ProductCategoryClass.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategoryClass with productCategoryId, productCategoryTypeId: " + productCategoryId + ", " + productCategoryTypeId);
      Debug.logError(ce);
      productCategoryClass = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryClass;
  }

  /** Updates the corresponding ProductCategoryClass
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryClass update(String productCategoryId, String productCategoryTypeId, java.util.Date fromDate, java.util.Date thruDate) throws java.rmi.RemoteException
  {
    if(productCategoryId == null || productCategoryTypeId == null) { return null; }
    ProductCategoryClass productCategoryClass = findByPrimaryKey(productCategoryId, productCategoryTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategoryClass productCategoryClassValue = new ProductCategoryClassValue();

    if(fromDate != null) { productCategoryClassValue.setFromDate(fromDate); }
    if(thruDate != null) { productCategoryClassValue.setThruDate(thruDate); }

    productCategoryClass.setValueObject(productCategoryClassValue);
    return productCategoryClass;
  }

  /** Removes/deletes the specified  ProductCategoryClass
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   */
  public static void removeByProductCategoryId(String productCategoryId)
  {
    if(productCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductCategoryId(productCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        ProductCategoryClass productCategoryClass = (ProductCategoryClass) iterator.next();
        Debug.logInfo("Removing productCategoryClass with productCategoryId:" + productCategoryId);
        productCategoryClass.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryClass records by the following parameters:
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryId(String productCategoryId)
  {
    Debug.logInfo("findByProductCategoryId: productCategoryId:" + productCategoryId);

    Collection collection = null;
    if(productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryClassHome().findByProductCategoryId(productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductCategoryClass
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   */
  public static void removeByProductCategoryTypeId(String productCategoryTypeId)
  {
    if(productCategoryTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductCategoryTypeId(productCategoryTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductCategoryClass productCategoryClass = (ProductCategoryClass) iterator.next();
        Debug.logInfo("Removing productCategoryClass with productCategoryTypeId:" + productCategoryTypeId);
        productCategoryClass.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryClass records by the following parameters:
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryTypeId(String productCategoryTypeId)
  {
    Debug.logInfo("findByProductCategoryTypeId: productCategoryTypeId:" + productCategoryTypeId);

    Collection collection = null;
    if(productCategoryTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryClassHome().findByProductCategoryTypeId(productCategoryTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
