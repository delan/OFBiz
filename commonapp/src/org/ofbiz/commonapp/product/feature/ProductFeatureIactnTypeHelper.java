
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Product Feature Interaction Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ProductFeatureIactnType Entity EJB; acts as a proxy for the Home interface
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
public class ProductFeatureIactnTypeHelper
{

  /** A static variable to cache the Home object for the ProductFeatureIactnType EJB */
  private static ProductFeatureIactnTypeHome productFeatureIactnTypeHome = null;

  /** Initializes the productFeatureIactnTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ProductFeatureIactnTypeHome instance for the default EJB server
   */
  public static ProductFeatureIactnTypeHome getProductFeatureIactnTypeHome()
  {
    if(productFeatureIactnTypeHome == null) //don't want to block here
    {
      synchronized(ProductFeatureIactnTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(productFeatureIactnTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.ProductFeatureIactnTypeHome");
            productFeatureIactnTypeHome = (ProductFeatureIactnTypeHome)MyNarrow.narrow(homeObject, ProductFeatureIactnTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("productFeatureIactnType home obtained " + productFeatureIactnTypeHome);
        }
      }
    }
    return productFeatureIactnTypeHome;
  }




  /** Remove the ProductFeatureIactnType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ProductFeatureIactnType productFeatureIactnType = findByPrimaryKey(primaryKey);
    try
    {
      if(productFeatureIactnType != null)
      {
        productFeatureIactnType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ProductFeatureIactnType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ProductFeatureIactnType corresponding to the primaryKey
   */
  public static ProductFeatureIactnType findByPrimaryKey(java.lang.String primaryKey)
  {
    ProductFeatureIactnType productFeatureIactnType = null;
    Debug.logInfo("ProductFeatureIactnTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      productFeatureIactnType = (ProductFeatureIactnType)MyNarrow.narrow(getProductFeatureIactnTypeHome().findByPrimaryKey(primaryKey), ProductFeatureIactnType.class);
      if(productFeatureIactnType != null)
      {
        productFeatureIactnType = productFeatureIactnType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureIactnType;
  }

  /** Finds all ProductFeatureIactnType entities
   *@return    Collection containing all ProductFeatureIactnType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ProductFeatureIactnTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getProductFeatureIactnTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ProductFeatureIactnType
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureIactnType create(String productFeatureIactnTypeId, String parentTypeId, String hasTable, String description)
  {
    ProductFeatureIactnType productFeatureIactnType = null;
    Debug.logInfo("ProductFeatureIactnTypeHelper.create: productFeatureIactnTypeId: " + productFeatureIactnTypeId);
    if(productFeatureIactnTypeId == null) { return null; }

    try { productFeatureIactnType = (ProductFeatureIactnType)MyNarrow.narrow(getProductFeatureIactnTypeHome().create(productFeatureIactnTypeId, parentTypeId, hasTable, description), ProductFeatureIactnType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create productFeatureIactnType with productFeatureIactnTypeId: " + productFeatureIactnTypeId);
      Debug.logError(ce);
      productFeatureIactnType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return productFeatureIactnType;
  }

  /** Updates the corresponding ProductFeatureIactnType
   *@param  productFeatureIactnTypeId                  Field of the PRODUCT_FEATURE_IACTN_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static ProductFeatureIactnType update(String productFeatureIactnTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(productFeatureIactnTypeId == null) { return null; }
    ProductFeatureIactnType productFeatureIactnType = findByPrimaryKey(productFeatureIactnTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ProductFeatureIactnType productFeatureIactnTypeValue = new ProductFeatureIactnTypeValue();

    if(parentTypeId != null) { productFeatureIactnTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { productFeatureIactnTypeValue.setHasTable(hasTable); }
    if(description != null) { productFeatureIactnTypeValue.setDescription(description); }

    productFeatureIactnType.setValueObject(productFeatureIactnTypeValue);
    return productFeatureIactnType;
  }

  /** Removes/deletes the specified  ProductFeatureIactnType
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
        ProductFeatureIactnType productFeatureIactnType = (ProductFeatureIactnType) iterator.next();
        Debug.logInfo("Removing productFeatureIactnType with parentTypeId:" + parentTypeId);
        productFeatureIactnType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureIactnType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureIactnTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ProductFeatureIactnType
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
        ProductFeatureIactnType productFeatureIactnType = (ProductFeatureIactnType) iterator.next();
        Debug.logInfo("Removing productFeatureIactnType with hasTable:" + hasTable);
        productFeatureIactnType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ProductFeatureIactnType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getProductFeatureIactnTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
