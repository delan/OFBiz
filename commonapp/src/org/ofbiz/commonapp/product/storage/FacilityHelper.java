
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Facility Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Facility Entity EJB; acts as a proxy for the Home interface
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
public class FacilityHelper
{

  /** A static variable to cache the Home object for the Facility EJB */
  private static FacilityHome facilityHome = null;

  /** Initializes the facilityHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FacilityHome instance for the default EJB server
   */
  public static FacilityHome getFacilityHome()
  {
    if(facilityHome == null) //don't want to block here
    {
      synchronized(FacilityHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(facilityHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.FacilityHome");
            facilityHome = (FacilityHome)MyNarrow.narrow(homeObject, FacilityHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("facility home obtained " + facilityHome);
        }
      }
    }
    return facilityHome;
  }




  /** Remove the Facility corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Facility facility = findByPrimaryKey(primaryKey);
    try
    {
      if(facility != null)
      {
        facility.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Facility by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Facility corresponding to the primaryKey
   */
  public static Facility findByPrimaryKey(java.lang.String primaryKey)
  {
    Facility facility = null;
    Debug.logInfo("FacilityHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      facility = (Facility)MyNarrow.narrow(getFacilityHome().findByPrimaryKey(primaryKey), Facility.class);
      if(facility != null)
      {
        facility = facility.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return facility;
  }

  /** Finds all Facility entities
   *@return    Collection containing all Facility entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FacilityHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFacilityHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Facility
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  facilityName                  Field of the FACILITY_NAME column.
   *@param  squareFootage                  Field of the SQUARE_FOOTAGE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static Facility create(String facilityId, String facilityTypeId, String facilityName, Long squareFootage, String description)
  {
    Facility facility = null;
    Debug.logInfo("FacilityHelper.create: facilityId: " + facilityId);
    if(facilityId == null) { return null; }

    try { facility = (Facility)MyNarrow.narrow(getFacilityHome().create(facilityId, facilityTypeId, facilityName, squareFootage, description), Facility.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create facility with facilityId: " + facilityId);
      Debug.logError(ce);
      facility = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return facility;
  }

  /** Updates the corresponding Facility
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  facilityName                  Field of the FACILITY_NAME column.
   *@param  squareFootage                  Field of the SQUARE_FOOTAGE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static Facility update(String facilityId, String facilityTypeId, String facilityName, Long squareFootage, String description) throws java.rmi.RemoteException
  {
    if(facilityId == null) { return null; }
    Facility facility = findByPrimaryKey(facilityId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Facility facilityValue = new FacilityValue();

    if(facilityTypeId != null) { facilityValue.setFacilityTypeId(facilityTypeId); }
    if(facilityName != null) { facilityValue.setFacilityName(facilityName); }
    if(squareFootage != null) { facilityValue.setSquareFootage(squareFootage); }
    if(description != null) { facilityValue.setDescription(description); }

    facility.setValueObject(facilityValue);
    return facility;
  }

  /** Removes/deletes the specified  Facility
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   */
  public static void removeByFacilityTypeId(String facilityTypeId)
  {
    if(facilityTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityTypeId(facilityTypeId));

    while(iterator.hasNext())
    {
      try
      {
        Facility facility = (Facility) iterator.next();
        Debug.logInfo("Removing facility with facilityTypeId:" + facilityTypeId);
        facility.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Facility records by the following parameters:
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityTypeId(String facilityTypeId)
  {
    Debug.logInfo("findByFacilityTypeId: facilityTypeId:" + facilityTypeId);

    Collection collection = null;
    if(facilityTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityHome().findByFacilityTypeId(facilityTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Facility
   *@param  facilityName                  Field of the FACILITY_NAME column.
   */
  public static void removeByFacilityName(String facilityName)
  {
    if(facilityName == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityName(facilityName));

    while(iterator.hasNext())
    {
      try
      {
        Facility facility = (Facility) iterator.next();
        Debug.logInfo("Removing facility with facilityName:" + facilityName);
        facility.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Facility records by the following parameters:
   *@param  facilityName                  Field of the FACILITY_NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityName(String facilityName)
  {
    Debug.logInfo("findByFacilityName: facilityName:" + facilityName);

    Collection collection = null;
    if(facilityName == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityHome().findByFacilityName(facilityName), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
