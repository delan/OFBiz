
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Facility Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the FacilityType Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */
public class FacilityTypeHelper
{

  /** A static variable to cache the Home object for the FacilityType EJB */
  private static FacilityTypeHome facilityTypeHome = null;

  /** Initializes the facilityTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FacilityTypeHome instance for the default EJB server
   */
  public static FacilityTypeHome getFacilityTypeHome()
  {
    if(facilityTypeHome == null) //don't want to block here
    {
      synchronized(FacilityTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(facilityTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.FacilityTypeHome");
            facilityTypeHome = (FacilityTypeHome)MyNarrow.narrow(homeObject, FacilityTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("facilityType home obtained " + facilityTypeHome);
        }
      }
    }
    return facilityTypeHome;
  }




  /** Remove the FacilityType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    FacilityType facilityType = findByPrimaryKey(primaryKey);
    try
    {
      if(facilityType != null)
      {
        facilityType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a FacilityType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The FacilityType corresponding to the primaryKey
   */
  public static FacilityType findByPrimaryKey(java.lang.String primaryKey)
  {
    FacilityType facilityType = null;
    Debug.logInfo("FacilityTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      facilityType = (FacilityType)MyNarrow.narrow(getFacilityTypeHome().findByPrimaryKey(primaryKey), FacilityType.class);
      if(facilityType != null)
      {
        facilityType = facilityType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityType;
  }

  /** Finds all FacilityType entities
   *@return    Collection containing all FacilityType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FacilityTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFacilityTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a FacilityType
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static FacilityType create(String facilityTypeId, String parentTypeId, String hasTable, String description)
  {
    FacilityType facilityType = null;
    Debug.logInfo("FacilityTypeHelper.create: facilityTypeId: " + facilityTypeId);
    if(facilityTypeId == null) { return null; }

    try { facilityType = (FacilityType)MyNarrow.narrow(getFacilityTypeHome().create(facilityTypeId, parentTypeId, hasTable, description), FacilityType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create facilityType with facilityTypeId: " + facilityTypeId);
      Debug.logError(ce);
      facilityType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityType;
  }

  /** Updates the corresponding FacilityType
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static FacilityType update(String facilityTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(facilityTypeId == null) { return null; }
    FacilityType facilityType = findByPrimaryKey(facilityTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    FacilityType facilityTypeValue = new FacilityTypeValue();

    if(parentTypeId != null) { facilityTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { facilityTypeValue.setHasTable(hasTable); }
    if(description != null) { facilityTypeValue.setDescription(description); }

    facilityType.setValueObject(facilityTypeValue);
    return facilityType;
  }

  /** Removes/deletes the specified  FacilityType
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
        FacilityType facilityType = (FacilityType) iterator.next();
        Debug.logInfo("Removing facilityType with parentTypeId:" + parentTypeId);
        facilityType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  FacilityType
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
        FacilityType facilityType = (FacilityType) iterator.next();
        Debug.logInfo("Removing facilityType with hasTable:" + hasTable);
        facilityType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
