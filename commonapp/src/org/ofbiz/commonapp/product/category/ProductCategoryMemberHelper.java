
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Member Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategoryMember Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryMemberHelper
{

  /** A static variable to cache the Home object for the ProductCategoryMember EJB */
  private static ProductCategoryMemberHome productCategoryMemberHome = null;

  /** Initializes the productCategoryMemberHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryMemberHome instance for the default EJB server
   */
  public static ProductCategoryMemberHome getProductCategoryMemberHome()
  {
    if(productCategoryMemberHome == null) //don't want to block here
    {
      synchronized(ProductCategoryMemberHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryMemberHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryMemberHome");
            productCategoryMemberHome = (ProductCategoryMemberHome)MyNarrow.narrow(homeObject, ProductCategoryMemberHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategoryMember home obtained " + productCategoryMemberHome);
        }
      }
    }
    return productCategoryMemberHome;
  }



  /** Remove the ProductCategoryMember corresponding to the primaryKey specified by fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   */
  public static void removeByPrimaryKey(String productCategoryId, String productId)
  {
    if(productCategoryId == null || productId == null)
    {
      return;
    }
    ProductCategoryMemberPK primaryKey = new ProductCategoryMemberPK(productCategoryId, productId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductCategoryMember corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryMemberPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategoryMember productCategoryMember = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategoryMember != null)
      {
        productCategoryMember.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductCategoryMember by its Primary Key, specified by individual fields
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return       The ProductCategoryMember corresponding to the primaryKey
   */
  public static ProductCategoryMember findByPrimaryKey(String productCategoryId, String productId)
  {
    if(productCategoryId == null || productId == null) return null;
    ProductCategoryMemberPK primaryKey = new ProductCategoryMemberPK(productCategoryId, productId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductCategoryMember by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategoryMember corresponding to the primaryKey
   */
  public static ProductCategoryMember findByPrimaryKey(org.ofbiz.commonapp.product.category.ProductCategoryMemberPK primaryKey)
  {
    ProductCategoryMember productCategoryMember = null;
    Debug.logInfo("ProductCategoryMemberHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategoryMember = (ProductCategoryMember)MyNarrow.narrow(getProductCategoryMemberHome().findByPrimaryKey(primaryKey), ProductCategoryMember.class);
      if(productCategoryMember != null)
      {
        productCategoryMember = productCategoryMember.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryMember;
  }

  /** Finds all ProductCategoryMember entities
   *@return    Collection containing all ProductCategoryMember entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryMemberHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryMemberHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategoryMember
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  primaryFlag                  Field of the PRIMARY_FLAG column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryMember create(String productCategoryId, String productId, java.util.Date fromDate, java.util.Date thruDate, String primaryFlag, String comment)
  {
    ProductCategoryMember productCategoryMember = null;
    Debug.logInfo("ProductCategoryMemberHelper.create: productCategoryId, productId: " + productCategoryId + ", " + productId);
    if(productCategoryId == null || productId == null) { return null; }

    try { productCategoryMember = (ProductCategoryMember)MyNarrow.narrow(getProductCategoryMemberHome().create(productCategoryId, productId, fromDate, thruDate, primaryFlag, comment), ProductCategoryMember.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategoryMember with productCategoryId, productId: " + productCategoryId + ", " + productId);
      Debug.logError(ce);
      productCategoryMember = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryMember;
  }

  /** Updates the corresponding ProductCategoryMember
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  primaryFlag                  Field of the PRIMARY_FLAG column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryMember update(String productCategoryId, String productId, java.util.Date fromDate, java.util.Date thruDate, String primaryFlag, String comment) throws java.rmi.RemoteException
  {
    if(productCategoryId == null || productId == null) { return null; }
    ProductCategoryMember productCategoryMember = findByPrimaryKey(productCategoryId, productId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategoryMember productCategoryMemberValue = new ProductCategoryMemberValue();

    if(fromDate != null) { productCategoryMemberValue.setFromDate(fromDate); }
    if(thruDate != null) { productCategoryMemberValue.setThruDate(thruDate); }
    if(primaryFlag != null) { productCategoryMemberValue.setPrimaryFlag(primaryFlag); }
    if(comment != null) { productCategoryMemberValue.setComment(comment); }

    productCategoryMember.setValueObject(productCategoryMemberValue);
    return productCategoryMember;
  }

  /** Removes/deletes the specified  ProductCategoryMember
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
        ProductCategoryMember productCategoryMember = (ProductCategoryMember) iterator.next();
        Debug.logInfo("Removing productCategoryMember with productCategoryId:" + productCategoryId);
        productCategoryMember.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryMember records by the following parameters:
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductCategoryId(String productCategoryId)
  {
    Debug.logInfo("findByProductCategoryId: productCategoryId:" + productCategoryId);

    Collection collection = null;
    if(productCategoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryMemberHome().findByProductCategoryId(productCategoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductCategoryMember
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
        ProductCategoryMember productCategoryMember = (ProductCategoryMember) iterator.next();
        Debug.logInfo("Removing productCategoryMember with productId:" + productId);
        productCategoryMember.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryMember records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryMemberHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
