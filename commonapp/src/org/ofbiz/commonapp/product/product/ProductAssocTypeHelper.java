
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Association Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductAssocType Entity EJB; acts as a proxy for the Home interface
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
public class ProductAssocTypeHelper
{

  /** A static variable to cache the Home object for the ProductAssocType EJB */
  private static ProductAssocTypeHome productAssocTypeHome = null;

  /** Initializes the productAssocTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductAssocTypeHome instance for the default EJB server
   */
  public static ProductAssocTypeHome getProductAssocTypeHome()
  {
    if(productAssocTypeHome == null) //don't want to block here
    {
      synchronized(ProductAssocTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productAssocTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.ProductAssocTypeHome");
            productAssocTypeHome = (ProductAssocTypeHome)MyNarrow.narrow(homeObject, ProductAssocTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productAssocType home obtained " + productAssocTypeHome);
        }
      }
    }
    return productAssocTypeHome;
  }




  /** Remove the ProductAssocType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductAssocType productAssocType = findByPrimaryKey(primaryKey);
    try
    {
      if(productAssocType != null)
      {
        productAssocType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductAssocType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductAssocType corresponding to the primaryKey
   */
  public static ProductAssocType findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductAssocType productAssocType = null;
    Debug.logInfo("ProductAssocTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productAssocType = (ProductAssocType)MyNarrow.narrow(getProductAssocTypeHome().findByPrimaryKey(primaryKey), ProductAssocType.class);
      if(productAssocType != null)
      {
        productAssocType = productAssocType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productAssocType;
  }

  /** Finds all ProductAssocType entities
   *@return    Collection containing all ProductAssocType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductAssocTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductAssocTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductAssocType
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductAssocType create(String productAssocTypeId, String parentTypeId, String hasTable, String description)
  {
    ProductAssocType productAssocType = null;
    Debug.logInfo("ProductAssocTypeHelper.create: productAssocTypeId: " + productAssocTypeId);
    if(productAssocTypeId == null) { return null; }

    try { productAssocType = (ProductAssocType)MyNarrow.narrow(getProductAssocTypeHome().create(productAssocTypeId, parentTypeId, hasTable, description), ProductAssocType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productAssocType with productAssocTypeId: " + productAssocTypeId);
      Debug.logError(ce);
      productAssocType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productAssocType;
  }

  /** Updates the corresponding ProductAssocType
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductAssocType update(String productAssocTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(productAssocTypeId == null) { return null; }
    ProductAssocType productAssocType = findByPrimaryKey(productAssocTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductAssocType productAssocTypeValue = new ProductAssocTypeValue();

    if(parentTypeId != null) { productAssocTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { productAssocTypeValue.setHasTable(hasTable); }
    if(description != null) { productAssocTypeValue.setDescription(description); }

    productAssocType.setValueObject(productAssocTypeValue);
    return productAssocType;
  }

  /** Removes/deletes the specified  ProductAssocType
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
        ProductAssocType productAssocType = (ProductAssocType) iterator.next();
        Debug.logInfo("Removing productAssocType with parentTypeId:" + parentTypeId);
        productAssocType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductAssocType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductAssocTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
