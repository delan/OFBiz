
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Association Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductAssoc Entity EJB; acts as a proxy for the Home interface
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
public class ProductAssocHelper
{

  /** A static variable to cache the Home object for the ProductAssoc EJB */
  private static ProductAssocHome productAssocHome = null;

  /** Initializes the productAssocHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductAssocHome instance for the default EJB server
   */
  public static ProductAssocHome getProductAssocHome()
  {
    if(productAssocHome == null) //don't want to block here
    {
      synchronized(ProductAssocHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productAssocHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductAssocHome");
            productAssocHome = (ProductAssocHome)MyNarrow.narrow(homeObject, ProductAssocHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productAssoc home obtained " + productAssocHome);
        }
      }
    }
    return productAssocHome;
  }



  /** Remove the ProductAssoc corresponding to the primaryKey specified by fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   */
  public static void removeByPrimaryKey(String productId, String productIdTo, String productAssocTypeId)
  {
    if(productId == null || productIdTo == null || productAssocTypeId == null)
    {
      return;
    }
    ProductAssocPK primaryKey = new ProductAssocPK(productId, productIdTo, productAssocTypeId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the ProductAssoc corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.product.ProductAssocPK primaryKey)
  {
    if(primaryKey == null) return;
    ProductAssoc productAssoc = findByPrimaryKey(primaryKey);
    try
    {
      if(productAssoc != null)
      {
        productAssoc.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a ProductAssoc by its Primary Key, specified by individual fields
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@return       The ProductAssoc corresponding to the primaryKey
   */
  public static ProductAssoc findByPrimaryKey(String productId, String productIdTo, String productAssocTypeId)
  {
    if(productId == null || productIdTo == null || productAssocTypeId == null) return null;
    ProductAssocPK primaryKey = new ProductAssocPK(productId, productIdTo, productAssocTypeId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a ProductAssoc by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductAssoc corresponding to the primaryKey
   */
  public static ProductAssoc findByPrimaryKey(org.ofbiz.commonapp.product.product.ProductAssocPK primaryKey)
  {
    ProductAssoc productAssoc = null;
    Debug.logInfo("ProductAssocHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productAssoc = (ProductAssoc)MyNarrow.narrow(getProductAssocHome().findByPrimaryKey(primaryKey), ProductAssoc.class);
      if(productAssoc != null)
      {
        productAssoc = productAssoc.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productAssoc;
  }

  /** Finds all ProductAssoc entities
   *@return    Collection containing all ProductAssoc entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductAssocHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductAssocHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductAssoc
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reason                  Field of the REASON column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  instruction                  Field of the INSTRUCTION column.
   *@return                Description of the Returned Value
   */
  public static ProductAssoc create(String productId, String productIdTo, String productAssocTypeId, java.util.Date fromDate, java.util.Date thruDate, String reason, Double quantity, String instruction)
  {
    ProductAssoc productAssoc = null;
    Debug.logInfo("ProductAssocHelper.create: productId, productIdTo, productAssocTypeId: " + productId + ", " + productIdTo + ", " + productAssocTypeId);
    if(productId == null || productIdTo == null || productAssocTypeId == null) { return null; }

    try { productAssoc = (ProductAssoc)MyNarrow.narrow(getProductAssocHome().create(productId, productIdTo, productAssocTypeId, fromDate, thruDate, reason, quantity, instruction), ProductAssoc.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productAssoc with productId, productIdTo, productAssocTypeId: " + productId + ", " + productIdTo + ", " + productAssocTypeId);
      Debug.logError(ce);
      productAssoc = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productAssoc;
  }

  /** Updates the corresponding ProductAssoc
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reason                  Field of the REASON column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  instruction                  Field of the INSTRUCTION column.
   *@return                Description of the Returned Value
   */
  public static ProductAssoc update(String productId, String productIdTo, String productAssocTypeId, java.util.Date fromDate, java.util.Date thruDate, String reason, Double quantity, String instruction) throws java.rmi.RemoteException
  {
    if(productId == null || productIdTo == null || productAssocTypeId == null) { return null; }
    ProductAssoc productAssoc = findByPrimaryKey(productId, productIdTo, productAssocTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductAssoc productAssocValue = new ProductAssocValue();

    if(fromDate != null) { productAssocValue.setFromDate(fromDate); }
    if(thruDate != null) { productAssocValue.setThruDate(thruDate); }
    if(reason != null) { productAssocValue.setReason(reason); }
    if(quantity != null) { productAssocValue.setQuantity(quantity); }
    if(instruction != null) { productAssocValue.setInstruction(instruction); }

    productAssoc.setValueObject(productAssocValue);
    return productAssoc;
  }

  /** Removes/deletes the specified  ProductAssoc
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
        ProductAssoc productAssoc = (ProductAssoc) iterator.next();
        Debug.logInfo("Removing productAssoc with productId:" + productId);
        productAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssoc records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductAssoc
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   */
  public static void removeByProductIdTo(String productIdTo)
  {
    if(productIdTo == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdTo(productIdTo));

    while(iterator.hasNext())
    {
      try
      {
        ProductAssoc productAssoc = (ProductAssoc) iterator.next();
        Debug.logInfo("Removing productAssoc with productIdTo:" + productIdTo);
        productAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssoc records by the following parameters:
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdTo(String productIdTo)
  {
    Debug.logInfo("findByProductIdTo: productIdTo:" + productIdTo);

    Collection collection = null;
    if(productIdTo == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocHome().findByProductIdTo(productIdTo), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductAssoc
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   */
  public static void removeByProductAssocTypeId(String productAssocTypeId)
  {
    if(productAssocTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductAssocTypeId(productAssocTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductAssoc productAssoc = (ProductAssoc) iterator.next();
        Debug.logInfo("Removing productAssoc with productAssocTypeId:" + productAssocTypeId);
        productAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssoc records by the following parameters:
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductAssocTypeId(String productAssocTypeId)
  {
    Debug.logInfo("findByProductAssocTypeId: productAssocTypeId:" + productAssocTypeId);

    Collection collection = null;
    if(productAssocTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocHome().findByProductAssocTypeId(productAssocTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductAssoc
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   */
  public static void removeByProductIdAndProductIdTo(String productId, String productIdTo)
  {
    if(productId == null || productIdTo == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdAndProductIdTo(productId, productIdTo));

    while(iterator.hasNext())
    {
      try
      {
        ProductAssoc productAssoc = (ProductAssoc) iterator.next();
        Debug.logInfo("Removing productAssoc with productId, productIdTo:" + productId + ", " + productIdTo);
        productAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssoc records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdAndProductIdTo(String productId, String productIdTo)
  {
    Debug.logInfo("findByProductIdAndProductIdTo: productId, productIdTo:" + productId + ", " + productIdTo);

    Collection collection = null;
    if(productId == null || productIdTo == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocHome().findByProductIdAndProductIdTo(productId, productIdTo), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductAssoc
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   */
  public static void removeByProductIdAndProductAssocTypeId(String productId, String productAssocTypeId)
  {
    if(productId == null || productAssocTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdAndProductAssocTypeId(productId, productAssocTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductAssoc productAssoc = (ProductAssoc) iterator.next();
        Debug.logInfo("Removing productAssoc with productId, productAssocTypeId:" + productId + ", " + productAssocTypeId);
        productAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssoc records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdAndProductAssocTypeId(String productId, String productAssocTypeId)
  {
    Debug.logInfo("findByProductIdAndProductAssocTypeId: productId, productAssocTypeId:" + productId + ", " + productAssocTypeId);

    Collection collection = null;
    if(productId == null || productAssocTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocHome().findByProductIdAndProductAssocTypeId(productId, productAssocTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductAssoc
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   */
  public static void removeByProductIdToAndProductAssocTypeId(String productIdTo, String productAssocTypeId)
  {
    if(productIdTo == null || productAssocTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductIdToAndProductAssocTypeId(productIdTo, productAssocTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductAssoc productAssoc = (ProductAssoc) iterator.next();
        Debug.logInfo("Removing productAssoc with productIdTo, productAssocTypeId:" + productIdTo + ", " + productAssocTypeId);
        productAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssoc records by the following parameters:
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductIdToAndProductAssocTypeId(String productIdTo, String productAssocTypeId)
  {
    Debug.logInfo("findByProductIdToAndProductAssocTypeId: productIdTo, productAssocTypeId:" + productIdTo + ", " + productAssocTypeId);

    Collection collection = null;
    if(productIdTo == null || productAssocTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocHome().findByProductIdToAndProductAssocTypeId(productIdTo, productAssocTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
