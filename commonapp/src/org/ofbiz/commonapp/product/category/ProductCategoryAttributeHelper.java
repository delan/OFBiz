
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategoryAttribute Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryAttributeHelper
{

  /** A static variable to cache the Home object for the ProductCategoryAttribute EJB */
  private static ProductCategoryAttributeHome productCategoryAttributeHome = null;

  /** Initializes the productCategoryAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryAttributeHome instance for the default EJB server
   */
  public static ProductCategoryAttributeHome getProductCategoryAttributeHome()
  {
    if(productCategoryAttributeHome == null) //don't want to block here
    {
      synchronized(ProductCategoryAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryAttributeHome");
            productCategoryAttributeHome = (ProductCategoryAttributeHome)MyNarrow.narrow(homeObject, ProductCategoryAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategoryAttribute home obtained " + productCategoryAttributeHome);
        }
      }
    }
    return productCategoryAttributeHome;
  }



  /** Remove the ProductCategoryAttribute corresponding to the primaryKey specified by fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String productCategoryId, String name)
  {
    if(productCategoryId == null || name == null)
    {
      return;
    }
    ProductCategoryAttributePK primaryKey = new ProductCategoryAttributePK(productCategoryId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductCategoryAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategoryAttribute productCategoryAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategoryAttribute != null)
      {
        productCategoryAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductCategoryAttribute by its Primary Key, specified by individual fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The ProductCategoryAttribute corresponding to the primaryKey
   */
  public static ProductCategoryAttribute findByPrimaryKey(String productCategoryId, String name)
  {
    if(productCategoryId == null || name == null) return null;
    ProductCategoryAttributePK primaryKey = new ProductCategoryAttributePK(productCategoryId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductCategoryAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategoryAttribute corresponding to the primaryKey
   */
  public static ProductCategoryAttribute findByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryAttributePK primaryKey)
  {
    ProductCategoryAttribute productCategoryAttribute = null;
    Debug.logInfo("ProductCategoryAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategoryAttribute = (ProductCategoryAttribute)MyNarrow.narrow(getProductCategoryAttributeHome().findByPrimaryKey(primaryKey), ProductCategoryAttribute.class);
      if(productCategoryAttribute != null)
      {
        productCategoryAttribute = productCategoryAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryAttribute;
  }

  /** Finds all ProductCategoryAttribute entities
   *@return    Collection containing all ProductCategoryAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategoryAttribute
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryAttribute create(String productCategoryId, String name, String value)
  {
    ProductCategoryAttribute productCategoryAttribute = null;
    Debug.logInfo("ProductCategoryAttributeHelper.create: productCategoryId, name: " + productCategoryId + ", " + name);
    if(productCategoryId == null || name == null) { return null; }

    try { productCategoryAttribute = (ProductCategoryAttribute)MyNarrow.narrow(getProductCategoryAttributeHome().create(productCategoryId, name, value), ProductCategoryAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategoryAttribute with productCategoryId, name: " + productCategoryId + ", " + name);
      Debug.logError(ce);
      productCategoryAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryAttribute;
  }

  /** Updates the corresponding ProductCategoryAttribute
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryAttribute update(String productCategoryId, String name, String value) throws java.rmi.RemoteException
  {
    if(productCategoryId == null || name == null) { return null; }
    ProductCategoryAttribute productCategoryAttribute = findByPrimaryKey(productCategoryId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategoryAttribute productCategoryAttributeValue = new ProductCategoryAttributeValue();

    if(value != null) { productCategoryAttributeValue.setValue(value); }

    productCategoryAttribute.setValueObject(productCategoryAttributeValue);
    return productCategoryAttribute;
  }

  /** Removes/deletes the specified  ProductCategoryAttribute
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
        ProductCategoryAttribute productCategoryAttribute = (ProductCategoryAttribute) iterator.next();
        Debug.logInfo("Removing productCategoryAttribute with productCategoryId:" + productCategoryId);
        productCategoryAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryAttribute records by the following parameters:
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryId(String productCategoryId)
  {
    Debug.logInfo("findByProductCategoryId: productCategoryId:" + productCategoryId);

    Collection collection = null;
    if(productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryAttributeHome().findByProductCategoryId(productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductCategoryAttribute
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
        ProductCategoryAttribute productCategoryAttribute = (ProductCategoryAttribute) iterator.next();
        Debug.logInfo("Removing productCategoryAttribute with name:" + name);
        productCategoryAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
