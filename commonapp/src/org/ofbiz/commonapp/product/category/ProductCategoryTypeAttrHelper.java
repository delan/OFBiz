
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategoryTypeAttr Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryTypeAttrHelper
{

  /** A static variable to cache the Home object for the ProductCategoryTypeAttr EJB */
  private static ProductCategoryTypeAttrHome productCategoryTypeAttrHome = null;

  /** Initializes the productCategoryTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryTypeAttrHome instance for the default EJB server
   */
  public static ProductCategoryTypeAttrHome getProductCategoryTypeAttrHome()
  {
    if(productCategoryTypeAttrHome == null) //don't want to block here
    {
      synchronized(ProductCategoryTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryTypeAttrHome");
            productCategoryTypeAttrHome = (ProductCategoryTypeAttrHome)MyNarrow.narrow(homeObject, ProductCategoryTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategoryTypeAttr home obtained " + productCategoryTypeAttrHome);
        }
      }
    }
    return productCategoryTypeAttrHome;
  }



  /** Remove the ProductCategoryTypeAttr corresponding to the primaryKey specified by fields
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String productCategoryTypeId, String name)
  {
    if(productCategoryTypeId == null || name == null)
    {
      return;
    }
    ProductCategoryTypeAttrPK primaryKey = new ProductCategoryTypeAttrPK(productCategoryTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductCategoryTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategoryTypeAttr productCategoryTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategoryTypeAttr != null)
      {
        productCategoryTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductCategoryTypeAttr by its Primary Key, specified by individual fields
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The ProductCategoryTypeAttr corresponding to the primaryKey
   */
  public static ProductCategoryTypeAttr findByPrimaryKey(String productCategoryTypeId, String name)
  {
    if(productCategoryTypeId == null || name == null) return null;
    ProductCategoryTypeAttrPK primaryKey = new ProductCategoryTypeAttrPK(productCategoryTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductCategoryTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategoryTypeAttr corresponding to the primaryKey
   */
  public static ProductCategoryTypeAttr findByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryTypeAttrPK primaryKey)
  {
    ProductCategoryTypeAttr productCategoryTypeAttr = null;
    Debug.logInfo("ProductCategoryTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategoryTypeAttr = (ProductCategoryTypeAttr)MyNarrow.narrow(getProductCategoryTypeAttrHome().findByPrimaryKey(primaryKey), ProductCategoryTypeAttr.class);
      if(productCategoryTypeAttr != null)
      {
        productCategoryTypeAttr = productCategoryTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryTypeAttr;
  }

  /** Finds all ProductCategoryTypeAttr entities
   *@return    Collection containing all ProductCategoryTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategoryTypeAttr
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryTypeAttr create(String productCategoryTypeId, String name)
  {
    ProductCategoryTypeAttr productCategoryTypeAttr = null;
    Debug.logInfo("ProductCategoryTypeAttrHelper.create: productCategoryTypeId, name: " + productCategoryTypeId + ", " + name);
    if(productCategoryTypeId == null || name == null) { return null; }

    try { productCategoryTypeAttr = (ProductCategoryTypeAttr)MyNarrow.narrow(getProductCategoryTypeAttrHome().create(productCategoryTypeId, name), ProductCategoryTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategoryTypeAttr with productCategoryTypeId, name: " + productCategoryTypeId + ", " + name);
      Debug.logError(ce);
      productCategoryTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryTypeAttr;
  }

  /** Updates the corresponding ProductCategoryTypeAttr
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryTypeAttr update(String productCategoryTypeId, String name) throws java.rmi.RemoteException
  {
    if(productCategoryTypeId == null || name == null) { return null; }
    ProductCategoryTypeAttr productCategoryTypeAttr = findByPrimaryKey(productCategoryTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategoryTypeAttr productCategoryTypeAttrValue = new ProductCategoryTypeAttrValue();


    productCategoryTypeAttr.setValueObject(productCategoryTypeAttrValue);
    return productCategoryTypeAttr;
  }

  /** Removes/deletes the specified  ProductCategoryTypeAttr
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
        ProductCategoryTypeAttr productCategoryTypeAttr = (ProductCategoryTypeAttr) iterator.next();
        Debug.logInfo("Removing productCategoryTypeAttr with productCategoryTypeId:" + productCategoryTypeId);
        productCategoryTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryTypeAttr records by the following parameters:
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryTypeId(String productCategoryTypeId)
  {
    Debug.logInfo("findByProductCategoryTypeId: productCategoryTypeId:" + productCategoryTypeId);

    Collection collection = null;
    if(productCategoryTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryTypeAttrHome().findByProductCategoryTypeId(productCategoryTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductCategoryTypeAttr
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
        ProductCategoryTypeAttr productCategoryTypeAttr = (ProductCategoryTypeAttr) iterator.next();
        Debug.logInfo("Removing productCategoryTypeAttr with name:" + name);
        productCategoryTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
