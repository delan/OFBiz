
package org.ofbiz.commonapp.product.feature;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Feature Data Object Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the FeatureDataObject Entity EJB; acts as a proxy for the Home interface
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
public class FeatureDataObjectHelper
{

  /** A static variable to cache the Home object for the FeatureDataObject EJB */
  private static FeatureDataObjectHome featureDataObjectHome = null;

  /** Initializes the featureDataObjectHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FeatureDataObjectHome instance for the default EJB server
   */
  public static FeatureDataObjectHome getFeatureDataObjectHome()
  {
    if(featureDataObjectHome == null) //don't want to block here
    {
      synchronized(FeatureDataObjectHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(featureDataObjectHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.feature.FeatureDataObjectHome");
            featureDataObjectHome = (FeatureDataObjectHome)MyNarrow.narrow(homeObject, FeatureDataObjectHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("featureDataObject home obtained " + featureDataObjectHome);
        }
      }
    }
    return featureDataObjectHome;
  }



  /** Remove the FeatureDataObject corresponding to the primaryKey specified by fields
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByPrimaryKey(String dataObjectId, String productFeatureId)
  {
    if(dataObjectId == null || productFeatureId == null)
    {
      return;
    }
    FeatureDataObjectPK primaryKey = new FeatureDataObjectPK(dataObjectId, productFeatureId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the FeatureDataObject corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.feature.FeatureDataObjectPK primaryKey)
  {
    if(primaryKey == null) return;
    FeatureDataObject featureDataObject = findByPrimaryKey(primaryKey);
    try
    {
      if(featureDataObject != null)
      {
        featureDataObject.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a FeatureDataObject by its Primary Key, specified by individual fields
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return       The FeatureDataObject corresponding to the primaryKey
   */
  public static FeatureDataObject findByPrimaryKey(String dataObjectId, String productFeatureId)
  {
    if(dataObjectId == null || productFeatureId == null) return null;
    FeatureDataObjectPK primaryKey = new FeatureDataObjectPK(dataObjectId, productFeatureId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a FeatureDataObject by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The FeatureDataObject corresponding to the primaryKey
   */
  public static FeatureDataObject findByPrimaryKey(org.ofbiz.commonapp.product.feature.FeatureDataObjectPK primaryKey)
  {
    FeatureDataObject featureDataObject = null;
    Debug.logInfo("FeatureDataObjectHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      featureDataObject = (FeatureDataObject)MyNarrow.narrow(getFeatureDataObjectHome().findByPrimaryKey(primaryKey), FeatureDataObject.class);
      if(featureDataObject != null)
      {
        featureDataObject = featureDataObject.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return featureDataObject;
  }

  /** Finds all FeatureDataObject entities
   *@return    Collection containing all FeatureDataObject entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FeatureDataObjectHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFeatureDataObjectHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a FeatureDataObject
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return                Description of the Returned Value
   */
  public static FeatureDataObject create(String dataObjectId, String productFeatureId)
  {
    FeatureDataObject featureDataObject = null;
    Debug.logInfo("FeatureDataObjectHelper.create: dataObjectId, productFeatureId: " + dataObjectId + ", " + productFeatureId);
    if(dataObjectId == null || productFeatureId == null) { return null; }

    try { featureDataObject = (FeatureDataObject)MyNarrow.narrow(getFeatureDataObjectHome().create(dataObjectId, productFeatureId), FeatureDataObject.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create featureDataObject with dataObjectId, productFeatureId: " + dataObjectId + ", " + productFeatureId);
      Debug.logError(ce);
      featureDataObject = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return featureDataObject;
  }

  /** Updates the corresponding FeatureDataObject
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return                Description of the Returned Value
   */
  public static FeatureDataObject update(String dataObjectId, String productFeatureId) throws java.rmi.RemoteException
  {
    if(dataObjectId == null || productFeatureId == null) { return null; }
    FeatureDataObject featureDataObject = findByPrimaryKey(dataObjectId, productFeatureId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    FeatureDataObject featureDataObjectValue = new FeatureDataObjectValue();


    featureDataObject.setValueObject(featureDataObjectValue);
    return featureDataObject;
  }

  /** Removes/deletes the specified  FeatureDataObject
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   */
  public static void removeByDataObjectId(String dataObjectId)
  {
    if(dataObjectId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByDataObjectId(dataObjectId));

    while(iterator.hasNext())
    {
      try
      {
        FeatureDataObject featureDataObject = (FeatureDataObject) iterator.next();
        Debug.logInfo("Removing featureDataObject with dataObjectId:" + dataObjectId);
        featureDataObject.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FeatureDataObject records by the following parameters:
   *@param  dataObjectId                  Field of the DATA_OBJECT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByDataObjectId(String dataObjectId)
  {
    Debug.logInfo("findByDataObjectId: dataObjectId:" + dataObjectId);

    Collection collection = null;
    if(dataObjectId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFeatureDataObjectHome().findByDataObjectId(dataObjectId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  FeatureDataObject
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   */
  public static void removeByProductFeatureId(String productFeatureId)
  {
    if(productFeatureId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductFeatureId(productFeatureId));

    while(iterator.hasNext())
    {
      try
      {
        FeatureDataObject featureDataObject = (FeatureDataObject) iterator.next();
        Debug.logInfo("Removing featureDataObject with productFeatureId:" + productFeatureId);
        featureDataObject.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FeatureDataObject records by the following parameters:
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductFeatureId(String productFeatureId)
  {
    Debug.logInfo("findByProductFeatureId: productFeatureId:" + productFeatureId);

    Collection collection = null;
    if(productFeatureId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFeatureDataObjectHome().findByProductFeatureId(productFeatureId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
