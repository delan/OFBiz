
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategory Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryHelper
{

  /** A static variable to cache the Home object for the ProductCategory EJB */
  private static ProductCategoryHome productCategoryHome = null;

  /** Initializes the productCategoryHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryHome instance for the default EJB server
   */
  public static ProductCategoryHome getProductCategoryHome()
  {
    if(productCategoryHome == null) //don't want to block here
    {
      synchronized(ProductCategoryHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryHome");
            productCategoryHome = (ProductCategoryHome)MyNarrow.narrow(homeObject, ProductCategoryHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategory home obtained " + productCategoryHome);
        }
      }
    }
    return productCategoryHome;
  }




  /** Remove the ProductCategory corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategory productCategory = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategory != null)
      {
        productCategory.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductCategory by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategory corresponding to the primaryKey
   */
  public static ProductCategory findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductCategory productCategory = null;
    Debug.logInfo("ProductCategoryHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategory = (ProductCategory)MyNarrow.narrow(getProductCategoryHome().findByPrimaryKey(primaryKey), ProductCategory.class);
      if(productCategory != null)
      {
        productCategory = productCategory.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategory;
  }

  /** Finds all ProductCategory entities
   *@return    Collection containing all ProductCategory entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategory
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductCategory create(String productCategoryId, String description)
  {
    ProductCategory productCategory = null;
    Debug.logInfo("ProductCategoryHelper.create: productCategoryId: " + productCategoryId);
    if(productCategoryId == null) { return null; }

    try { productCategory = (ProductCategory)MyNarrow.narrow(getProductCategoryHome().create(productCategoryId, description), ProductCategory.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategory with productCategoryId: " + productCategoryId);
      Debug.logError(ce);
      productCategory = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategory;
  }

  /** Updates the corresponding ProductCategory
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductCategory update(String productCategoryId, String description) throws java.rmi.RemoteException
  {
    if(productCategoryId == null) { return null; }
    ProductCategory productCategory = findByPrimaryKey(productCategoryId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategory productCategoryValue = new ProductCategoryValue();

    if(description != null) { productCategoryValue.setDescription(description); }

    productCategory.setValueObject(productCategoryValue);
    return productCategory;
  }


}
