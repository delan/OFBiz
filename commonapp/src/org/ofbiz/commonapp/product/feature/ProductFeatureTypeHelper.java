
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeatureType Entity EJB; acts as a proxy for the Home interface
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
public class ProductFeatureTypeHelper
{

  /** A static variable to cache the Home object for the ProductFeatureType EJB */
  private static ProductFeatureTypeHome productFeatureTypeHome = null;

  /** Initializes the productFeatureTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureTypeHome instance for the default EJB server
   */
  public static ProductFeatureTypeHome getProductFeatureTypeHome()
  {
    if(productFeatureTypeHome == null) //don't want to block here
    {
      synchronized(ProductFeatureTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureTypeHome");
            productFeatureTypeHome = (ProductFeatureTypeHome)MyNarrow.narrow(homeObject, ProductFeatureTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeatureType home obtained " + productFeatureTypeHome);
        }
      }
    }
    return productFeatureTypeHome;
  }




  /** Remove the ProductFeatureType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeatureType productFeatureType = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeatureType != null)
      {
        productFeatureType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductFeatureType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeatureType corresponding to the primaryKey
   */
  public static ProductFeatureType findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductFeatureType productFeatureType = null;
    Debug.logInfo("ProductFeatureTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeatureType = (ProductFeatureType)MyNarrow.narrow(getProductFeatureTypeHome().findByPrimaryKey(primaryKey), ProductFeatureType.class);
      if(productFeatureType != null)
      {
        productFeatureType = productFeatureType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureType;
  }

  /** Finds all ProductFeatureType entities
   *@return    Collection containing all ProductFeatureType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeatureType
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureType create(String productFeatureTypeId, String parentTypeId, String hasTable, String description)
  {
    ProductFeatureType productFeatureType = null;
    Debug.logInfo("ProductFeatureTypeHelper.create: productFeatureTypeId: " + productFeatureTypeId);
    if(productFeatureTypeId == null) { return null; }

    try { productFeatureType = (ProductFeatureType)MyNarrow.narrow(getProductFeatureTypeHome().create(productFeatureTypeId, parentTypeId, hasTable, description), ProductFeatureType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeatureType with productFeatureTypeId: " + productFeatureTypeId);
      Debug.logError(ce);
      productFeatureType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureType;
  }

  /** Updates the corresponding ProductFeatureType
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureType update(String productFeatureTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(productFeatureTypeId == null) { return null; }
    ProductFeatureType productFeatureType = findByPrimaryKey(productFeatureTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeatureType productFeatureTypeValue = new ProductFeatureTypeValue();

    if(parentTypeId != null) { productFeatureTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { productFeatureTypeValue.setHasTable(hasTable); }
    if(description != null) { productFeatureTypeValue.setDescription(description); }

    productFeatureType.setValueObject(productFeatureTypeValue);
    return productFeatureType;
  }

  /** Removes/deletes the specified  ProductFeatureType
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
        ProductFeatureType productFeatureType = (ProductFeatureType) iterator.next();
        Debug.logInfo("Removing productFeatureType with parentTypeId:" + parentTypeId);
        productFeatureType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureType
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
        ProductFeatureType productFeatureType = (ProductFeatureType) iterator.next();
        Debug.logInfo("Removing productFeatureType with hasTable:" + hasTable);
        productFeatureType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
