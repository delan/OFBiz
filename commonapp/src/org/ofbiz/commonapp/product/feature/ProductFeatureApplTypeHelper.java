
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Applicability Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeatureApplType Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:28 MDT 2001
 *@version    1.0
 */
public class ProductFeatureApplTypeHelper
{

  /** A static variable to cache the Home object for the ProductFeatureApplType EJB */
  private static ProductFeatureApplTypeHome productFeatureApplTypeHome = null;

  /** Initializes the productFeatureApplTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureApplTypeHome instance for the default EJB server
   */
  public static ProductFeatureApplTypeHome getProductFeatureApplTypeHome()
  {
    if(productFeatureApplTypeHome == null) //don't want to block here
    {
      synchronized(ProductFeatureApplTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureApplTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureApplTypeHome");
            productFeatureApplTypeHome = (ProductFeatureApplTypeHome)MyNarrow.narrow(homeObject, ProductFeatureApplTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeatureApplType home obtained " + productFeatureApplTypeHome);
        }
      }
    }
    return productFeatureApplTypeHome;
  }




  /** Remove the ProductFeatureApplType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeatureApplType productFeatureApplType = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeatureApplType != null)
      {
        productFeatureApplType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductFeatureApplType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeatureApplType corresponding to the primaryKey
   */
  public static ProductFeatureApplType findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductFeatureApplType productFeatureApplType = null;
    Debug.logInfo("ProductFeatureApplTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeatureApplType = (ProductFeatureApplType)MyNarrow.narrow(getProductFeatureApplTypeHome().findByPrimaryKey(primaryKey), ProductFeatureApplType.class);
      if(productFeatureApplType != null)
      {
        productFeatureApplType = productFeatureApplType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureApplType;
  }

  /** Finds all ProductFeatureApplType entities
   *@return    Collection containing all ProductFeatureApplType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureApplTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureApplTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeatureApplType
   *@param  productFeatureApplTypeId                  Field of the PRODUCT_FEATURE_APPL_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureApplType create(String productFeatureApplTypeId, String parentTypeId, String hasTable, String description)
  {
    ProductFeatureApplType productFeatureApplType = null;
    Debug.logInfo("ProductFeatureApplTypeHelper.create: productFeatureApplTypeId: " + productFeatureApplTypeId);
    if(productFeatureApplTypeId == null) { return null; }

    try { productFeatureApplType = (ProductFeatureApplType)MyNarrow.narrow(getProductFeatureApplTypeHome().create(productFeatureApplTypeId, parentTypeId, hasTable, description), ProductFeatureApplType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeatureApplType with productFeatureApplTypeId: " + productFeatureApplTypeId);
      Debug.logError(ce);
      productFeatureApplType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureApplType;
  }

  /** Updates the corresponding ProductFeatureApplType
   *@param  productFeatureApplTypeId                  Field of the PRODUCT_FEATURE_APPL_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureApplType update(String productFeatureApplTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(productFeatureApplTypeId == null) { return null; }
    ProductFeatureApplType productFeatureApplType = findByPrimaryKey(productFeatureApplTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeatureApplType productFeatureApplTypeValue = new ProductFeatureApplTypeValue();

    if(parentTypeId != null) { productFeatureApplTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { productFeatureApplTypeValue.setHasTable(hasTable); }
    if(description != null) { productFeatureApplTypeValue.setDescription(description); }

    productFeatureApplType.setValueObject(productFeatureApplTypeValue);
    return productFeatureApplType;
  }

  /** Removes/deletes the specified  ProductFeatureApplType
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
        ProductFeatureApplType productFeatureApplType = (ProductFeatureApplType) iterator.next();
        Debug.logInfo("Removing productFeatureApplType with parentTypeId:" + parentTypeId);
        productFeatureApplType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureApplType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureApplTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureApplType
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
        ProductFeatureApplType productFeatureApplType = (ProductFeatureApplType) iterator.next();
        Debug.logInfo("Removing productFeatureApplType with hasTable:" + hasTable);
        productFeatureApplType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureApplType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureApplTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
