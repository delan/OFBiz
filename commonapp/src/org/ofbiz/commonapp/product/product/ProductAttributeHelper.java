
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductAttribute Entity EJB; acts as a proxy for the Home interface
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
public class ProductAttributeHelper
{

  /** A static variable to cache the Home object for the ProductAttribute EJB */
  private static ProductAttributeHome productAttributeHome = null;

  /** Initializes the productAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductAttributeHome instance for the default EJB server
   */
  public static ProductAttributeHome getProductAttributeHome()
  {
    if(productAttributeHome == null) //don't want to block here
    {
      synchronized(ProductAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductAttributeHome");
            productAttributeHome = (ProductAttributeHome)MyNarrow.narrow(homeObject, ProductAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productAttribute home obtained " + productAttributeHome);
        }
      }
    }
    return productAttributeHome;
  }



  /** Remove the ProductAttribute corresponding to the primaryKey specified by fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String productId, String name)
  {
    if(productId == null || name == null)
    {
      return;
    }
    ProductAttributePK primaryKey = new ProductAttributePK(productId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.product.ProductAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    ProductAttribute productAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(productAttribute != null)
      {
        productAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductAttribute by its Primary Key, specified by individual fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The ProductAttribute corresponding to the primaryKey
   */
  public static ProductAttribute findByPrimaryKey(String productId, String name)
  {
    if(productId == null || name == null) return null;
    ProductAttributePK primaryKey = new ProductAttributePK(productId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductAttribute corresponding to the primaryKey
   */
  public static ProductAttribute findByPrimaryKey(org.ofbiz.commonapp.product.product.ProductAttributePK primaryKey)
  {
    ProductAttribute productAttribute = null;
    Debug.logInfo("ProductAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productAttribute = (ProductAttribute)MyNarrow.narrow(getProductAttributeHome().findByPrimaryKey(primaryKey), ProductAttribute.class);
      if(productAttribute != null)
      {
        productAttribute = productAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productAttribute;
  }

  /** Finds all ProductAttribute entities
   *@return    Collection containing all ProductAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductAttribute
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static ProductAttribute create(String productId, String name, String value)
  {
    ProductAttribute productAttribute = null;
    Debug.logInfo("ProductAttributeHelper.create: productId, name: " + productId + ", " + name);
    if(productId == null || name == null) { return null; }

    try { productAttribute = (ProductAttribute)MyNarrow.narrow(getProductAttributeHome().create(productId, name, value), ProductAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productAttribute with productId, name: " + productId + ", " + name);
      Debug.logError(ce);
      productAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productAttribute;
  }

  /** Updates the corresponding ProductAttribute
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static ProductAttribute update(String productId, String name, String value) throws java.rmi.RemoteException
  {
    if(productId == null || name == null) { return null; }
    ProductAttribute productAttribute = findByPrimaryKey(productId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductAttribute productAttributeValue = new ProductAttributeValue();

    if(value != null) { productAttributeValue.setValue(value); }

    productAttribute.setValueObject(productAttributeValue);
    return productAttribute;
  }

  /** Removes/deletes the specified  ProductAttribute
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
        ProductAttribute productAttribute = (ProductAttribute) iterator.next();
        Debug.logInfo("Removing productAttribute with productId:" + productId);
        productAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAttribute records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAttributeHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductAttribute
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
        ProductAttribute productAttribute = (ProductAttribute) iterator.next();
        Debug.logInfo("Removing productAttribute with name:" + name);
        productAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
