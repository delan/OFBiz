
package org.ofbiz.commonapp.product.product;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Good Identification Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the GoodIdentificationType Entity EJB; acts as a proxy for the Home interface
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
public class GoodIdentificationTypeHelper
{

  /** A static variable to cache the Home object for the GoodIdentificationType EJB */
  private static GoodIdentificationTypeHome goodIdentificationTypeHome = null;

  /** Initializes the goodIdentificationTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The GoodIdentificationTypeHome instance for the default EJB server
   */
  public static GoodIdentificationTypeHome getGoodIdentificationTypeHome()
  {
    if(goodIdentificationTypeHome == null) //don't want to block here
    {
      synchronized(GoodIdentificationTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(goodIdentificationTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.product.GoodIdentificationTypeHome");
            goodIdentificationTypeHome = (GoodIdentificationTypeHome)MyNarrow.narrow(homeObject, GoodIdentificationTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("goodIdentificationType home obtained " + goodIdentificationTypeHome);
        }
      }
    }
    return goodIdentificationTypeHome;
  }




  /** Remove the GoodIdentificationType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    GoodIdentificationType goodIdentificationType = findByPrimaryKey(primaryKey);
    try
    {
      if(goodIdentificationType != null)
      {
        goodIdentificationType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a GoodIdentificationType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The GoodIdentificationType corresponding to the primaryKey
   */
  public static GoodIdentificationType findByPrimaryKey(java.lang.String primaryKey)
  {
    GoodIdentificationType goodIdentificationType = null;
    Debug.logInfo("GoodIdentificationTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      goodIdentificationType = (GoodIdentificationType)MyNarrow.narrow(getGoodIdentificationTypeHome().findByPrimaryKey(primaryKey), GoodIdentificationType.class);
      if(goodIdentificationType != null)
      {
        goodIdentificationType = goodIdentificationType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return goodIdentificationType;
  }

  /** Finds all GoodIdentificationType entities
   *@return    Collection containing all GoodIdentificationType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("GoodIdentificationTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getGoodIdentificationTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a GoodIdentificationType
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static GoodIdentificationType create(String goodIdentificationTypeId, String parentTypeId, String hasTable, String description)
  {
    GoodIdentificationType goodIdentificationType = null;
    Debug.logInfo("GoodIdentificationTypeHelper.create: goodIdentificationTypeId: " + goodIdentificationTypeId);
    if(goodIdentificationTypeId == null) { return null; }

    try { goodIdentificationType = (GoodIdentificationType)MyNarrow.narrow(getGoodIdentificationTypeHome().create(goodIdentificationTypeId, parentTypeId, hasTable, description), GoodIdentificationType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create goodIdentificationType with goodIdentificationTypeId: " + goodIdentificationTypeId);
      Debug.logError(ce);
      goodIdentificationType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return goodIdentificationType;
  }

  /** Updates the corresponding GoodIdentificationType
   *@param  goodIdentificationTypeId                  Field of the GOOD_IDENTIFICATION_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static GoodIdentificationType update(String goodIdentificationTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(goodIdentificationTypeId == null) { return null; }
    GoodIdentificationType goodIdentificationType = findByPrimaryKey(goodIdentificationTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    GoodIdentificationType goodIdentificationTypeValue = new GoodIdentificationTypeValue();

    if(parentTypeId != null) { goodIdentificationTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { goodIdentificationTypeValue.setHasTable(hasTable); }
    if(description != null) { goodIdentificationTypeValue.setDescription(description); }

    goodIdentificationType.setValueObject(goodIdentificationTypeValue);
    return goodIdentificationType;
  }

  /** Removes/deletes the specified  GoodIdentificationType
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
        GoodIdentificationType goodIdentificationType = (GoodIdentificationType) iterator.next();
        Debug.logInfo("Removing goodIdentificationType with parentTypeId:" + parentTypeId);
        goodIdentificationType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GoodIdentificationType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGoodIdentificationTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  GoodIdentificationType
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
        GoodIdentificationType goodIdentificationType = (GoodIdentificationType) iterator.next();
        Debug.logInfo("Removing goodIdentificationType with hasTable:" + hasTable);
        goodIdentificationType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GoodIdentificationType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGoodIdentificationTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
