
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Facility Contact Mechanism Entity
 * <p><b>Description:</b> Data Type Of: Contact Mechanism
 * <p>The Helper class from the FacilityContactMechanism Entity EJB; acts as a proxy for the Home interface
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
public class FacilityContactMechanismHelper
{

  /** A static variable to cache the Home object for the FacilityContactMechanism EJB */
  private static FacilityContactMechanismHome facilityContactMechanismHome = null;

  /** Initializes the facilityContactMechanismHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FacilityContactMechanismHome instance for the default EJB server
   */
  public static FacilityContactMechanismHome getFacilityContactMechanismHome()
  {
    if(facilityContactMechanismHome == null) //don't want to block here
    {
      synchronized(FacilityContactMechanismHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(facilityContactMechanismHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.FacilityContactMechanismHome");
            facilityContactMechanismHome = (FacilityContactMechanismHome)MyNarrow.narrow(homeObject, FacilityContactMechanismHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("facilityContactMechanism home obtained " + facilityContactMechanismHome);
        }
      }
    }
    return facilityContactMechanismHome;
  }



  /** Remove the FacilityContactMechanism corresponding to the primaryKey specified by fields
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   */
  public static void removeByPrimaryKey(String facilityId, String contactMechanismId)
  {
    if(facilityId == null || contactMechanismId == null)
    {
      return;
    }
    FacilityContactMechanismPK primaryKey = new FacilityContactMechanismPK(facilityId, contactMechanismId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the FacilityContactMechanism corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.storage.FacilityContactMechanismPK primaryKey)
  {
    if(primaryKey == null) return;
    FacilityContactMechanism facilityContactMechanism = findByPrimaryKey(primaryKey);
    try
    {
      if(facilityContactMechanism != null)
      {
        facilityContactMechanism.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a FacilityContactMechanism by its Primary Key, specified by individual fields
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return       The FacilityContactMechanism corresponding to the primaryKey
   */
  public static FacilityContactMechanism findByPrimaryKey(String facilityId, String contactMechanismId)
  {
    if(facilityId == null || contactMechanismId == null) return null;
    FacilityContactMechanismPK primaryKey = new FacilityContactMechanismPK(facilityId, contactMechanismId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a FacilityContactMechanism by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The FacilityContactMechanism corresponding to the primaryKey
   */
  public static FacilityContactMechanism findByPrimaryKey(org.ofbiz.commonapp.product.storage.FacilityContactMechanismPK primaryKey)
  {
    FacilityContactMechanism facilityContactMechanism = null;
    Debug.logInfo("FacilityContactMechanismHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      facilityContactMechanism = (FacilityContactMechanism)MyNarrow.narrow(getFacilityContactMechanismHome().findByPrimaryKey(primaryKey), FacilityContactMechanism.class);
      if(facilityContactMechanism != null)
      {
        facilityContactMechanism = facilityContactMechanism.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityContactMechanism;
  }

  /** Finds all FacilityContactMechanism entities
   *@return    Collection containing all FacilityContactMechanism entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FacilityContactMechanismHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFacilityContactMechanismHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a FacilityContactMechanism
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return                Description of the Returned Value
   */
  public static FacilityContactMechanism create(String facilityId, String contactMechanismId)
  {
    FacilityContactMechanism facilityContactMechanism = null;
    Debug.logInfo("FacilityContactMechanismHelper.create: facilityId, contactMechanismId: " + facilityId + ", " + contactMechanismId);
    if(facilityId == null || contactMechanismId == null) { return null; }

    try { facilityContactMechanism = (FacilityContactMechanism)MyNarrow.narrow(getFacilityContactMechanismHome().create(facilityId, contactMechanismId), FacilityContactMechanism.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create facilityContactMechanism with facilityId, contactMechanismId: " + facilityId + ", " + contactMechanismId);
      Debug.logError(ce);
      facilityContactMechanism = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityContactMechanism;
  }

  /** Updates the corresponding FacilityContactMechanism
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return                Description of the Returned Value
   */
  public static FacilityContactMechanism update(String facilityId, String contactMechanismId) throws java.rmi.RemoteException
  {
    if(facilityId == null || contactMechanismId == null) { return null; }
    FacilityContactMechanism facilityContactMechanism = findByPrimaryKey(facilityId, contactMechanismId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    FacilityContactMechanism facilityContactMechanismValue = new FacilityContactMechanismValue();


    facilityContactMechanism.setValueObject(facilityContactMechanismValue);
    return facilityContactMechanism;
  }

  /** Removes/deletes the specified  FacilityContactMechanism
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
        FacilityContactMechanism facilityContactMechanism = (FacilityContactMechanism) iterator.next();
        Debug.logInfo("Removing facilityContactMechanism with facilityId:" + facilityId);
        facilityContactMechanism.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityContactMechanism records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityId(String facilityId)
  {
    Debug.logInfo("findByFacilityId: facilityId:" + facilityId);

    Collection collection = null;
    if(facilityId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityContactMechanismHome().findByFacilityId(facilityId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  FacilityContactMechanism
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   */
  public static void removeByContactMechanismId(String contactMechanismId)
  {
    if(contactMechanismId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByContactMechanismId(contactMechanismId));

    while(iterator.hasNext())
    {
      try
      {
        FacilityContactMechanism facilityContactMechanism = (FacilityContactMechanism) iterator.next();
        Debug.logInfo("Removing facilityContactMechanism with contactMechanismId:" + contactMechanismId);
        facilityContactMechanism.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityContactMechanism records by the following parameters:
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByContactMechanismId(String contactMechanismId)
  {
    Debug.logInfo("findByContactMechanismId: contactMechanismId:" + contactMechanismId);

    Collection collection = null;
    if(contactMechanismId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityContactMechanismHome().findByContactMechanismId(contactMechanismId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
