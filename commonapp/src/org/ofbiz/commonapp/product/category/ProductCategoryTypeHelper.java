
package org.ofbiz.commonapp.product.category;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Category Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductCategoryType Entity EJB; acts as a proxy for the Home interface
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
public class ProductCategoryTypeHelper
{

  /** A static variable to cache the Home object for the ProductCategoryType EJB */
  private static ProductCategoryTypeHome productCategoryTypeHome = null;

  /** Initializes the productCategoryTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductCategoryTypeHome instance for the default EJB server
   */
  public static ProductCategoryTypeHome getProductCategoryTypeHome()
  {
    if(productCategoryTypeHome == null) //don't want to block here
    {
      synchronized(ProductCategoryTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productCategoryTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.category.ProductCategoryTypeHome");
            productCategoryTypeHome = (ProductCategoryTypeHome)MyNarrow.narrow(homeObject, ProductCategoryTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productCategoryType home obtained " + productCategoryTypeHome);
        }
      }
    }
    return productCategoryTypeHome;
  }




  /** Remove the ProductCategoryType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductCategoryType productCategoryType = findByPrimaryKey(primaryKey);
    try
    {
      if(productCategoryType != null)
      {
        productCategoryType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductCategoryType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductCategoryType corresponding to the primaryKey
   */
  public static ProductCategoryType findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductCategoryType productCategoryType = null;
    Debug.logInfo("ProductCategoryTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productCategoryType = (ProductCategoryType)MyNarrow.narrow(getProductCategoryTypeHome().findByPrimaryKey(primaryKey), ProductCategoryType.class);
      if(productCategoryType != null)
      {
        productCategoryType = productCategoryType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryType;
  }

  /** Finds all ProductCategoryType entities
   *@return    Collection containing all ProductCategoryType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductCategoryTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductCategoryTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductCategoryType
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryType create(String productCategoryTypeId, String parentTypeId, String hasTable, String description)
  {
    ProductCategoryType productCategoryType = null;
    Debug.logInfo("ProductCategoryTypeHelper.create: productCategoryTypeId: " + productCategoryTypeId);
    if(productCategoryTypeId == null) { return null; }

    try { productCategoryType = (ProductCategoryType)MyNarrow.narrow(getProductCategoryTypeHome().create(productCategoryTypeId, parentTypeId, hasTable, description), ProductCategoryType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productCategoryType with productCategoryTypeId: " + productCategoryTypeId);
      Debug.logError(ce);
      productCategoryType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productCategoryType;
  }

  /** Updates the corresponding ProductCategoryType
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductCategoryType update(String productCategoryTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(productCategoryTypeId == null) { return null; }
    ProductCategoryType productCategoryType = findByPrimaryKey(productCategoryTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductCategoryType productCategoryTypeValue = new ProductCategoryTypeValue();

    if(parentTypeId != null) { productCategoryTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { productCategoryTypeValue.setHasTable(hasTable); }
    if(description != null) { productCategoryTypeValue.setDescription(description); }

    productCategoryType.setValueObject(productCategoryTypeValue);
    return productCategoryType;
  }

  /** Removes/deletes the specified  ProductCategoryType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentTypeId(parentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        ProductCategoryType productCategoryType = (ProductCategoryType) iterator.next();
        Debug.logInfo("Removing productCategoryType with parentTypeId:" + parentTypeId);
        productCategoryType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductCategoryType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = UtilMisc.toIterator(findByHasTable(hasTable));

    while(iterator.hasNext())
    {
      try
      {
        ProductCategoryType productCategoryType = (ProductCategoryType) iterator.next();
        Debug.logInfo("Removing productCategoryType with hasTable:" + hasTable);
        productCategoryType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductCategoryType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductCategoryTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
