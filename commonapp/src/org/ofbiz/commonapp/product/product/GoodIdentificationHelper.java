
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Good Identification Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the GoodIdentification Entity EJB; acts as a proxy for the Home interface
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
public class GoodIdentificationHelper
{

  /** A static variable to cache the Home object for the GoodIdentification EJB */
  private static GoodIdentificationHome goodIdentificationHome = null;

  /** Initializes the goodIdentificationHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The GoodIdentificationHome instance for the default EJB server
   */
  public static GoodIdentificationHome getGoodIdentificationHome()
  {
    if(goodIdentificationHome == null) //don't want to block here
    {
      synchronized(GoodIdentificationHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(goodIdentificationHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.GoodIdentificationHome");
            goodIdentificationHome = (GoodIdentificationHome)MyNarrow.narrow(homeObject, GoodIdentificationHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("goodIdentification home obtained " + goodIdentificationHome);
        }
      }
    }
    return goodIdentificationHome;
  }



  /** Remove the GoodIdentification corresponding to the primaryKey specified by fields
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   */
  public static void removeByPrimaryKey(String goodIdentificationTypeId, String productId)
  {
    if(goodIdentificationTypeId == null || productId == null)
    {
      return;
    }
    GoodIdentificationPK primaryKey = new GoodIdentificationPK(goodIdentificationTypeId, productId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the GoodIdentification corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.product.GoodIdentificationPK primaryKey)
  {
    if(primaryKey == null) return;
    GoodIdentification goodIdentification = findByPrimaryKey(primaryKey);
    try
    {
      if(goodIdentification != null)
      {
        goodIdentification.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a GoodIdentification by its Primary Key, specified by individual fields
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return       The GoodIdentification corresponding to the primaryKey
   */
  public static GoodIdentification findByPrimaryKey(String goodIdentificationTypeId, String productId)
  {
    if(goodIdentificationTypeId == null || productId == null) return null;
    GoodIdentificationPK primaryKey = new GoodIdentificationPK(goodIdentificationTypeId, productId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a GoodIdentification by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The GoodIdentification corresponding to the primaryKey
   */
  public static GoodIdentification findByPrimaryKey(org.ofbiz.commonapp.product.product.GoodIdentificationPK primaryKey)
  {
    GoodIdentification goodIdentification = null;
    Debug.logInfo("GoodIdentificationHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      goodIdentification = (GoodIdentification)MyNarrow.narrow(getGoodIdentificationHome().findByPrimaryKey(primaryKey), GoodIdentification.class);
      if(goodIdentification != null)
      {
        goodIdentification = goodIdentification.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return goodIdentification;
  }

  /** Finds all GoodIdentification entities
   *@return    Collection containing all GoodIdentification entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("GoodIdentificationHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getGoodIdentificationHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a GoodIdentification
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  idValue                  Field of the ID_VALUE column.
   *@return                Description of the Returned Value
   */
  public static GoodIdentification create(String goodIdentificationTypeId, String productId, String idValue)
  {
    GoodIdentification goodIdentification = null;
    Debug.logInfo("GoodIdentificationHelper.create: goodIdentificationTypeId, productId: " + goodIdentificationTypeId + ", " + productId);
    if(goodIdentificationTypeId == null || productId == null) { return null; }

    try { goodIdentification = (GoodIdentification)MyNarrow.narrow(getGoodIdentificationHome().create(goodIdentificationTypeId, productId, idValue), GoodIdentification.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create goodIdentification with goodIdentificationTypeId, productId: " + goodIdentificationTypeId + ", " + productId);
      Debug.logError(ce);
      goodIdentification = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return goodIdentification;
  }

  /** Updates the corresponding GoodIdentification
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  idValue                  Field of the ID_VALUE column.
   *@return                Description of the Returned Value
   */
  public static GoodIdentification update(String goodIdentificationTypeId, String productId, String idValue) throws java.rmi.RemoteException
  {
    if(goodIdentificationTypeId == null || productId == null) { return null; }
    GoodIdentification goodIdentification = findByPrimaryKey(goodIdentificationTypeId, productId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    GoodIdentification goodIdentificationValue = new GoodIdentificationValue();

    if(idValue != null) { goodIdentificationValue.setIdValue(idValue); }

    goodIdentification.setValueObject(goodIdentificationValue);
    return goodIdentification;
  }

  /** Removes/deletes the specified  GoodIdentification
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   */
  public static void removeByGoodIdentificationTypeId(String goodIdentificationTypeId)
  {
    if(goodIdentificationTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGoodIdentificationTypeId(goodIdentificationTypeId));

    while(iterator.hasNext())
    {
      try
      {
        GoodIdentification goodIdentification = (GoodIdentification) iterator.next();
        Debug.logInfo("Removing goodIdentification with goodIdentificationTypeId:" + goodIdentificationTypeId);
        goodIdentification.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GoodIdentification records by the following parameters:
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGoodIdentificationTypeId(String goodIdentificationTypeId)
  {
    Debug.logInfo("findByGoodIdentificationTypeId: goodIdentificationTypeId:" + goodIdentificationTypeId);

    Collection collection = null;
    if(goodIdentificationTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGoodIdentificationHome().findByGoodIdentificationTypeId(goodIdentificationTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  GoodIdentification
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
        GoodIdentification goodIdentification = (GoodIdentification) iterator.next();
        Debug.logInfo("Removing goodIdentification with productId:" + productId);
        goodIdentification.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GoodIdentification records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGoodIdentificationHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  GoodIdentification
   *@param  idValue                  Field of the ID_VALUE column.
   */
  public static void removeByIdValue(String idValue)
  {
    if(idValue == null) return;
    Iterator iterator = UtilMisc.toIterator(findByIdValue(idValue));

    while(iterator.hasNext())
    {
      try
      {
        GoodIdentification goodIdentification = (GoodIdentification) iterator.next();
        Debug.logInfo("Removing goodIdentification with idValue:" + idValue);
        goodIdentification.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GoodIdentification records by the following parameters:
   *@param  idValue                  Field of the ID_VALUE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByIdValue(String idValue)
  {
    Debug.logInfo("findByIdValue: idValue:" + idValue);

    Collection collection = null;
    if(idValue == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGoodIdentificationHome().findByIdValue(idValue), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
