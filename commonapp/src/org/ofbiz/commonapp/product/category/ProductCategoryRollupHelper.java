
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Rollup Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategoryRollup Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryRollupHelper
{

  /** A static variable to cache the Home object for the ProductCategoryRollup EJB */
  private static ProductCategoryRollupHome productCategoryRollupHome = null;

  /** Initializes the productCategoryRollupHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryRollupHome instance for the default EJB server
   */
  public static ProductCategoryRollupHome getProductCategoryRollupHome()
  {
    if(productCategoryRollupHome == null) //don't want to block here
    {
      synchronized(ProductCategoryRollupHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryRollupHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryRollupHome");
            productCategoryRollupHome = (ProductCategoryRollupHome)MyNarrow.narrow(homeObject, ProductCategoryRollupHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategoryRollup home obtained " + productCategoryRollupHome);
        }
      }
    }
    return productCategoryRollupHome;
  }



  /** Remove the ProductCategoryRollup corresponding to the primaryKey specified by fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   */
  public static void removeByPrimaryKey(String productCategoryId, String parentProductCategoryId)
  {
    if(productCategoryId == null || parentProductCategoryId == null)
    {
      return;
    }
    ProductCategoryRollupPK primaryKey = new ProductCategoryRollupPK(productCategoryId, parentProductCategoryId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductCategoryRollup corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryRollupPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategoryRollup productCategoryRollup = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategoryRollup != null)
      {
        productCategoryRollup.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductCategoryRollup by its Primary Key, specified by individual fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   *@return       The ProductCategoryRollup corresponding to the primaryKey
   */
  public static ProductCategoryRollup findByPrimaryKey(String productCategoryId, String parentProductCategoryId)
  {
    if(productCategoryId == null || parentProductCategoryId == null) return null;
    ProductCategoryRollupPK primaryKey = new ProductCategoryRollupPK(productCategoryId, parentProductCategoryId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductCategoryRollup by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategoryRollup corresponding to the primaryKey
   */
  public static ProductCategoryRollup findByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryRollupPK primaryKey)
  {
    ProductCategoryRollup productCategoryRollup = null;
    Debug.logInfo("ProductCategoryRollupHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategoryRollup = (ProductCategoryRollup)MyNarrow.narrow(getProductCategoryRollupHome().findByPrimaryKey(primaryKey), ProductCategoryRollup.class);
      if(productCategoryRollup != null)
      {
        productCategoryRollup = productCategoryRollup.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryRollup;
  }

  /** Finds all ProductCategoryRollup entities
   *@return    Collection containing all ProductCategoryRollup entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryRollupHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryRollupHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategoryRollup
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryRollup create(String productCategoryId, String parentProductCategoryId)
  {
    ProductCategoryRollup productCategoryRollup = null;
    Debug.logInfo("ProductCategoryRollupHelper.create: productCategoryId, parentProductCategoryId: " + productCategoryId + ", " + parentProductCategoryId);
    if(productCategoryId == null || parentProductCategoryId == null) { return null; }

    try { productCategoryRollup = (ProductCategoryRollup)MyNarrow.narrow(getProductCategoryRollupHome().create(productCategoryId, parentProductCategoryId), ProductCategoryRollup.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategoryRollup with productCategoryId, parentProductCategoryId: " + productCategoryId + ", " + parentProductCategoryId);
      Debug.logError(ce);
      productCategoryRollup = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryRollup;
  }

  /** Updates the corresponding ProductCategoryRollup
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryRollup update(String productCategoryId, String parentProductCategoryId) throws java.rmi.RemoteException
  {
    if(productCategoryId == null || parentProductCategoryId == null) { return null; }
    ProductCategoryRollup productCategoryRollup = findByPrimaryKey(productCategoryId, parentProductCategoryId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategoryRollup productCategoryRollupValue = new ProductCategoryRollupValue();


    productCategoryRollup.setValueObject(productCategoryRollupValue);
    return productCategoryRollup;
  }

  /** Removes/deletes the specified  ProductCategoryRollup
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
        ProductCategoryRollup productCategoryRollup = (ProductCategoryRollup) iterator.next();
        Debug.logInfo("Removing productCategoryRollup with productCategoryId:" + productCategoryId);
        productCategoryRollup.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryRollup records by the following parameters:
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryId(String productCategoryId)
  {
    Debug.logInfo("findByProductCategoryId: productCategoryId:" + productCategoryId);

    Collection collection = null;
    if(productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryRollupHome().findByProductCategoryId(productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductCategoryRollup
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   */
  public static void removeByParentProductCategoryId(String parentProductCategoryId)
  {
    if(parentProductCategoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentProductCategoryId(parentProductCategoryId));

    while(iterator.hasNext())
    {
      try
      {
        ProductCategoryRollup productCategoryRollup = (ProductCategoryRollup) iterator.next();
        Debug.logInfo("Removing productCategoryRollup with parentProductCategoryId:" + parentProductCategoryId);
        productCategoryRollup.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryRollup records by the following parameters:
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentProductCategoryId(String parentProductCategoryId)
  {
    Debug.logInfo("findByParentProductCategoryId: parentProductCategoryId:" + parentProductCategoryId);

    Collection collection = null;
    if(parentProductCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryRollupHome().findByParentProductCategoryId(parentProductCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
