
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Facility Role Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the FacilityRoleType Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */
public class FacilityRoleTypeHelper
{

  /** A static variable to cache the Home object for the FacilityRoleType EJB */
  private static FacilityRoleTypeHome facilityRoleTypeHome = null;

  /** Initializes the facilityRoleTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FacilityRoleTypeHome instance for the default EJB server
   */
  public static FacilityRoleTypeHome getFacilityRoleTypeHome()
  {
    if(facilityRoleTypeHome == null) //don't want to block here
    {
      synchronized(FacilityRoleTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(facilityRoleTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.FacilityRoleTypeHome");
            facilityRoleTypeHome = (FacilityRoleTypeHome)MyNarrow.narrow(homeObject, FacilityRoleTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("facilityRoleType home obtained " + facilityRoleTypeHome);
        }
      }
    }
    return facilityRoleTypeHome;
  }




  /** Remove the FacilityRoleType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    FacilityRoleType facilityRoleType = findByPrimaryKey(primaryKey);
    try
    {
      if(facilityRoleType != null)
      {
        facilityRoleType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a FacilityRoleType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The FacilityRoleType corresponding to the primaryKey
   */
  public static FacilityRoleType findByPrimaryKey(java.lang.String primaryKey)
  {
    FacilityRoleType facilityRoleType = null;
    Debug.logInfo("FacilityRoleTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      facilityRoleType = (FacilityRoleType)MyNarrow.narrow(getFacilityRoleTypeHome().findByPrimaryKey(primaryKey), FacilityRoleType.class);
      if(facilityRoleType != null)
      {
        facilityRoleType = facilityRoleType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityRoleType;
  }

  /** Finds all FacilityRoleType entities
   *@return    Collection containing all FacilityRoleType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FacilityRoleTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFacilityRoleTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a FacilityRoleType
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static FacilityRoleType create(String facilityRoleTypeId, String description)
  {
    FacilityRoleType facilityRoleType = null;
    Debug.logInfo("FacilityRoleTypeHelper.create: facilityRoleTypeId: " + facilityRoleTypeId);
    if(facilityRoleTypeId == null) { return null; }

    try { facilityRoleType = (FacilityRoleType)MyNarrow.narrow(getFacilityRoleTypeHome().create(facilityRoleTypeId, description), FacilityRoleType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create facilityRoleType with facilityRoleTypeId: " + facilityRoleTypeId);
      Debug.logError(ce);
      facilityRoleType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityRoleType;
  }

  /** Updates the corresponding FacilityRoleType
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static FacilityRoleType update(String facilityRoleTypeId, String description) throws java.rmi.RemoteException
  {
    if(facilityRoleTypeId == null) { return null; }
    FacilityRoleType facilityRoleType = findByPrimaryKey(facilityRoleTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    FacilityRoleType facilityRoleTypeValue = new FacilityRoleTypeValue();

    if(description != null) { facilityRoleTypeValue.setDescription(description); }

    facilityRoleType.setValueObject(facilityRoleTypeValue);
    return facilityRoleType;
  }


}
