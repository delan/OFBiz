
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Facility Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the FacilityAttribute Entity EJB; acts as a proxy for the Home interface
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
public class FacilityAttributeHelper
{

  /** A static variable to cache the Home object for the FacilityAttribute EJB */
  private static FacilityAttributeHome facilityAttributeHome = null;

  /** Initializes the facilityAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FacilityAttributeHome instance for the default EJB server
   */
  public static FacilityAttributeHome getFacilityAttributeHome()
  {
    if(facilityAttributeHome == null) //don't want to block here
    {
      synchronized(FacilityAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(facilityAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.FacilityAttributeHome");
            facilityAttributeHome = (FacilityAttributeHome)MyNarrow.narrow(homeObject, FacilityAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("facilityAttribute home obtained " + facilityAttributeHome);
        }
      }
    }
    return facilityAttributeHome;
  }



  /** Remove the FacilityAttribute corresponding to the primaryKey specified by fields
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String facilityId, String name)
  {
    if(facilityId == null || name == null)
    {
      return;
    }
    FacilityAttributePK primaryKey = new FacilityAttributePK(facilityId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the FacilityAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.storage.FacilityAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    FacilityAttribute facilityAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(facilityAttribute != null)
      {
        facilityAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a FacilityAttribute by its Primary Key, specified by individual fields
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The FacilityAttribute corresponding to the primaryKey
   */
  public static FacilityAttribute findByPrimaryKey(String facilityId, String name)
  {
    if(facilityId == null || name == null) return null;
    FacilityAttributePK primaryKey = new FacilityAttributePK(facilityId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a FacilityAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The FacilityAttribute corresponding to the primaryKey
   */
  public static FacilityAttribute findByPrimaryKey(org.ofbiz.commonapp.product.storage.FacilityAttributePK primaryKey)
  {
    FacilityAttribute facilityAttribute = null;
    Debug.logInfo("FacilityAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      facilityAttribute = (FacilityAttribute)MyNarrow.narrow(getFacilityAttributeHome().findByPrimaryKey(primaryKey), FacilityAttribute.class);
      if(facilityAttribute != null)
      {
        facilityAttribute = facilityAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityAttribute;
  }

  /** Finds all FacilityAttribute entities
   *@return    Collection containing all FacilityAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FacilityAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFacilityAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a FacilityAttribute
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static FacilityAttribute create(String facilityId, String name, String value)
  {
    FacilityAttribute facilityAttribute = null;
    Debug.logInfo("FacilityAttributeHelper.create: facilityId, name: " + facilityId + ", " + name);
    if(facilityId == null || name == null) { return null; }

    try { facilityAttribute = (FacilityAttribute)MyNarrow.narrow(getFacilityAttributeHome().create(facilityId, name, value), FacilityAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create facilityAttribute with facilityId, name: " + facilityId + ", " + name);
      Debug.logError(ce);
      facilityAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityAttribute;
  }

  /** Updates the corresponding FacilityAttribute
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static FacilityAttribute update(String facilityId, String name, String value) throws java.rmi.RemoteException
  {
    if(facilityId == null || name == null) { return null; }
    FacilityAttribute facilityAttribute = findByPrimaryKey(facilityId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    FacilityAttribute facilityAttributeValue = new FacilityAttributeValue();

    if(value != null) { facilityAttributeValue.setValue(value); }

    facilityAttribute.setValueObject(facilityAttributeValue);
    return facilityAttribute;
  }

  /** Removes/deletes the specified  FacilityAttribute
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
        FacilityAttribute facilityAttribute = (FacilityAttribute) iterator.next();
        Debug.logInfo("Removing facilityAttribute with facilityId:" + facilityId);
        facilityAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityAttribute records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityId(String facilityId)
  {
    Debug.logInfo("findByFacilityId: facilityId:" + facilityId);

    Collection collection = null;
    if(facilityId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityAttributeHome().findByFacilityId(facilityId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  FacilityAttribute
   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null) return;
    Iterator iterator = UtilMisc.toIterator(findByName(name));

    while(iterator.hasNext())
    {
      try
      {
        FacilityAttribute facilityAttribute = (FacilityAttribute) iterator.next();
        Debug.logInfo("Removing facilityAttribute with name:" + name);
        facilityAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
