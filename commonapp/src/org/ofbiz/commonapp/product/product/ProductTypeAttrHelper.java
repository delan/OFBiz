
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductTypeAttr Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:25 MDT 2001
 *@version    1.0
 */
public class ProductTypeAttrHelper
{

  /** A static variable to cache the Home object for the ProductTypeAttr EJB */
  private static ProductTypeAttrHome productTypeAttrHome = null;

  /** Initializes the productTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductTypeAttrHome instance for the default EJB server
   */
  public static ProductTypeAttrHome getProductTypeAttrHome()
  {
    if(productTypeAttrHome == null) //don't want to block here
    {
      synchronized(ProductTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductTypeAttrHome");
            productTypeAttrHome = (ProductTypeAttrHome)MyNarrow.narrow(homeObject, ProductTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productTypeAttr home obtained " + productTypeAttrHome);
        }
      }
    }
    return productTypeAttrHome;
  }



  /** Remove the ProductTypeAttr corresponding to the primaryKey specified by fields
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String productTypeId, String name)
  {
    if(productTypeId == null || name == null)
    {
      return;
    }
    ProductTypeAttrPK primaryKey = new ProductTypeAttrPK(productTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.product.ProductTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductTypeAttr productTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(productTypeAttr != null)
      {
        productTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductTypeAttr by its Primary Key, specified by individual fields
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The ProductTypeAttr corresponding to the primaryKey
   */
  public static ProductTypeAttr findByPrimaryKey(String productTypeId, String name)
  {
    if(productTypeId == null || name == null) return null;
    ProductTypeAttrPK primaryKey = new ProductTypeAttrPK(productTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductTypeAttr corresponding to the primaryKey
   */
  public static ProductTypeAttr findByPrimaryKey(org.ofbiz.commonapp.product.product.ProductTypeAttrPK primaryKey)
  {
    ProductTypeAttr productTypeAttr = null;
    Debug.logInfo("ProductTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productTypeAttr = (ProductTypeAttr)MyNarrow.narrow(getProductTypeAttrHome().findByPrimaryKey(primaryKey), ProductTypeAttr.class);
      if(productTypeAttr != null)
      {
        productTypeAttr = productTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productTypeAttr;
  }

  /** Finds all ProductTypeAttr entities
   *@return    Collection containing all ProductTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductTypeAttr
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static ProductTypeAttr create(String productTypeId, String name)
  {
    ProductTypeAttr productTypeAttr = null;
    Debug.logInfo("ProductTypeAttrHelper.create: productTypeId, name: " + productTypeId + ", " + name);
    if(productTypeId == null || name == null) { return null; }

    try { productTypeAttr = (ProductTypeAttr)MyNarrow.narrow(getProductTypeAttrHome().create(productTypeId, name), ProductTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productTypeAttr with productTypeId, name: " + productTypeId + ", " + name);
      Debug.logError(ce);
      productTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productTypeAttr;
  }

  /** Updates the corresponding ProductTypeAttr
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static ProductTypeAttr update(String productTypeId, String name) throws java.rmi.RemoteException
  {
    if(productTypeId == null || name == null) { return null; }
    ProductTypeAttr productTypeAttr = findByPrimaryKey(productTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductTypeAttr productTypeAttrValue = new ProductTypeAttrValue();


    productTypeAttr.setValueObject(productTypeAttrValue);
    return productTypeAttr;
  }

  /** Removes/deletes the specified  ProductTypeAttr
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
        ProductTypeAttr productTypeAttr = (ProductTypeAttr) iterator.next();
        Debug.logInfo("Removing productTypeAttr with productTypeId:" + productTypeId);
        productTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductTypeAttr records by the following parameters:
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductTypeId(String productTypeId)
  {
    Debug.logInfo("findByProductTypeId: productTypeId:" + productTypeId);

    Collection collection = null;
    if(productTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductTypeAttrHome().findByProductTypeId(productTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductTypeAttr
   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null) return;
    Iterator iterator = UtilMisc.toIterator(findByName(name));

    while(iterator.hasNext())
    {
      try
      {
        ProductTypeAttr productTypeAttr = (ProductTypeAttr) iterator.next();
        Debug.logInfo("Removing productTypeAttr with name:" + name);
        productTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
