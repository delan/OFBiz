
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Facility Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyFacility Entity EJB; acts as a proxy for the Home interface
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
public class PartyFacilityHelper
{

  /** A static variable to cache the Home object for the PartyFacility EJB */
  private static PartyFacilityHome partyFacilityHome = null;

  /** Initializes the partyFacilityHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyFacilityHome instance for the default EJB server
   */
  public static PartyFacilityHome getPartyFacilityHome()
  {
    if(partyFacilityHome == null) //don't want to block here
    {
      synchronized(PartyFacilityHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyFacilityHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.PartyFacilityHome");
            partyFacilityHome = (PartyFacilityHome)MyNarrow.narrow(homeObject, PartyFacilityHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyFacility home obtained " + partyFacilityHome);
        }
      }
    }
    return partyFacilityHome;
  }



  /** Remove the PartyFacility corresponding to the primaryKey specified by fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   */
  public static void removeByPrimaryKey(String partyId, String facilityId)
  {
    if(partyId == null || facilityId == null)
    {
      return;
    }
    PartyFacilityPK primaryKey = new PartyFacilityPK(partyId, facilityId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the PartyFacility corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.storage.PartyFacilityPK primaryKey)
  {
    if(primaryKey == null) return;
    PartyFacility partyFacility = findByPrimaryKey(primaryKey);
    try
    {
      if(partyFacility != null)
      {
        partyFacility.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PartyFacility by its Primary Key, specified by individual fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return       The PartyFacility corresponding to the primaryKey
   */
  public static PartyFacility findByPrimaryKey(String partyId, String facilityId)
  {
    if(partyId == null || facilityId == null) return null;
    PartyFacilityPK primaryKey = new PartyFacilityPK(partyId, facilityId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PartyFacility by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyFacility corresponding to the primaryKey
   */
  public static PartyFacility findByPrimaryKey(org.ofbiz.commonapp.product.storage.PartyFacilityPK primaryKey)
  {
    PartyFacility partyFacility = null;
    Debug.logInfo("PartyFacilityHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyFacility = (PartyFacility)MyNarrow.narrow(getPartyFacilityHome().findByPrimaryKey(primaryKey), PartyFacility.class);
      if(partyFacility != null)
      {
        partyFacility = partyFacility.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyFacility;
  }

  /** Finds all PartyFacility entities
   *@return    Collection containing all PartyFacility entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyFacilityHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyFacilityHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyFacility
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@return                Description of the Returned Value
   */
  public static PartyFacility create(String partyId, String facilityId, String facilityRoleTypeId)
  {
    PartyFacility partyFacility = null;
    Debug.logInfo("PartyFacilityHelper.create: partyId, facilityId: " + partyId + ", " + facilityId);
    if(partyId == null || facilityId == null) { return null; }

    try { partyFacility = (PartyFacility)MyNarrow.narrow(getPartyFacilityHome().create(partyId, facilityId, facilityRoleTypeId), PartyFacility.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyFacility with partyId, facilityId: " + partyId + ", " + facilityId);
      Debug.logError(ce);
      partyFacility = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyFacility;
  }

  /** Updates the corresponding PartyFacility
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@return                Description of the Returned Value
   */
  public static PartyFacility update(String partyId, String facilityId, String facilityRoleTypeId) throws java.rmi.RemoteException
  {
    if(partyId == null || facilityId == null) { return null; }
    PartyFacility partyFacility = findByPrimaryKey(partyId, facilityId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyFacility partyFacilityValue = new PartyFacilityValue();

    if(facilityRoleTypeId != null) { partyFacilityValue.setFacilityRoleTypeId(facilityRoleTypeId); }

    partyFacility.setValueObject(partyFacilityValue);
    return partyFacility;
  }

  /** Removes/deletes the specified  PartyFacility
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyId(partyId));

    while(iterator.hasNext())
    {
      try
      {
        PartyFacility partyFacility = (PartyFacility) iterator.next();
        Debug.logInfo("Removing partyFacility with partyId:" + partyId);
        partyFacility.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyFacility records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyFacilityHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyFacility
   *@param  facilityId                  Field of the FACILITY_ID column.
   */
  public static void removeByFacilityId(String facilityId)
  {
    if(facilityId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityId(facilityId));

    while(iterator.hasNext())
    {
      try
      {
        PartyFacility partyFacility = (PartyFacility) iterator.next();
        Debug.logInfo("Removing partyFacility with facilityId:" + facilityId);
        partyFacility.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyFacility records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityId(String facilityId)
  {
    Debug.logInfo("findByFacilityId: facilityId:" + facilityId);

    Collection collection = null;
    if(facilityId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyFacilityHome().findByFacilityId(facilityId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyFacility
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   */
  public static void removeByFacilityRoleTypeId(String facilityRoleTypeId)
  {
    if(facilityRoleTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityRoleTypeId(facilityRoleTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PartyFacility partyFacility = (PartyFacility) iterator.next();
        Debug.logInfo("Removing partyFacility with facilityRoleTypeId:" + facilityRoleTypeId);
        partyFacility.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyFacility records by the following parameters:
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityRoleTypeId(String facilityRoleTypeId)
  {
    Debug.logInfo("findByFacilityRoleTypeId: facilityRoleTypeId:" + facilityRoleTypeId);

    Collection collection = null;
    if(facilityRoleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyFacilityHome().findByFacilityRoleTypeId(facilityRoleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyFacility
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   */
  public static void removeByFacilityIdAndFacilityRoleTypeId(String facilityId, String facilityRoleTypeId)
  {
    if(facilityId == null || facilityRoleTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityIdAndFacilityRoleTypeId(facilityId, facilityRoleTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PartyFacility partyFacility = (PartyFacility) iterator.next();
        Debug.logInfo("Removing partyFacility with facilityId, facilityRoleTypeId:" + facilityId + ", " + facilityRoleTypeId);
        partyFacility.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyFacility records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityIdAndFacilityRoleTypeId(String facilityId, String facilityRoleTypeId)
  {
    Debug.logInfo("findByFacilityIdAndFacilityRoleTypeId: facilityId, facilityRoleTypeId:" + facilityId + ", " + facilityRoleTypeId);

    Collection collection = null;
    if(facilityId == null || facilityRoleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyFacilityHome().findByFacilityIdAndFacilityRoleTypeId(facilityId, facilityRoleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
