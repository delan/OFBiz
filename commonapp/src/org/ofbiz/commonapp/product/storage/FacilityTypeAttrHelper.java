
package org.ofbiz.commonapp.product.storage;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Facility Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the FacilityTypeAttr Entity EJB; acts as a proxy for the Home interface
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
public class FacilityTypeAttrHelper
{

  /** A static variable to cache the Home object for the FacilityTypeAttr EJB */
  private static FacilityTypeAttrHome facilityTypeAttrHome = null;

  /** Initializes the facilityTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The FacilityTypeAttrHome instance for the default EJB server
   */
  public static FacilityTypeAttrHome getFacilityTypeAttrHome()
  {
    if(facilityTypeAttrHome == null) //don't want to block here
    {
      synchronized(FacilityTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(facilityTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.storage.FacilityTypeAttrHome");
            facilityTypeAttrHome = (FacilityTypeAttrHome)MyNarrow.narrow(homeObject, FacilityTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("facilityTypeAttr home obtained " + facilityTypeAttrHome);
        }
      }
    }
    return facilityTypeAttrHome;
  }



  /** Remove the FacilityTypeAttr corresponding to the primaryKey specified by fields
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String facilityTypeId, String name)
  {
    if(facilityTypeId == null || name == null)
    {
      return;
    }
    FacilityTypeAttrPK primaryKey = new FacilityTypeAttrPK(facilityTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the FacilityTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.storage.FacilityTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    FacilityTypeAttr facilityTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(facilityTypeAttr != null)
      {
        facilityTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a FacilityTypeAttr by its Primary Key, specified by individual fields
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The FacilityTypeAttr corresponding to the primaryKey
   */
  public static FacilityTypeAttr findByPrimaryKey(String facilityTypeId, String name)
  {
    if(facilityTypeId == null || name == null) return null;
    FacilityTypeAttrPK primaryKey = new FacilityTypeAttrPK(facilityTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a FacilityTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The FacilityTypeAttr corresponding to the primaryKey
   */
  public static FacilityTypeAttr findByPrimaryKey(org.ofbiz.commonapp.product.storage.FacilityTypeAttrPK primaryKey)
  {
    FacilityTypeAttr facilityTypeAttr = null;
    Debug.logInfo("FacilityTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      facilityTypeAttr = (FacilityTypeAttr)MyNarrow.narrow(getFacilityTypeAttrHome().findByPrimaryKey(primaryKey), FacilityTypeAttr.class);
      if(facilityTypeAttr != null)
      {
        facilityTypeAttr = facilityTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityTypeAttr;
  }

  /** Finds all FacilityTypeAttr entities
   *@return    Collection containing all FacilityTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("FacilityTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getFacilityTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a FacilityTypeAttr
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static FacilityTypeAttr create(String facilityTypeId, String name)
  {
    FacilityTypeAttr facilityTypeAttr = null;
    Debug.logInfo("FacilityTypeAttrHelper.create: facilityTypeId, name: " + facilityTypeId + ", " + name);
    if(facilityTypeId == null || name == null) { return null; }

    try { facilityTypeAttr = (FacilityTypeAttr)MyNarrow.narrow(getFacilityTypeAttrHome().create(facilityTypeId, name), FacilityTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create facilityTypeAttr with facilityTypeId, name: " + facilityTypeId + ", " + name);
      Debug.logError(ce);
      facilityTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return facilityTypeAttr;
  }

  /** Updates the corresponding FacilityTypeAttr
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static FacilityTypeAttr update(String facilityTypeId, String name) throws java.rmi.RemoteException
  {
    if(facilityTypeId == null || name == null) { return null; }
    FacilityTypeAttr facilityTypeAttr = findByPrimaryKey(facilityTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    FacilityTypeAttr facilityTypeAttrValue = new FacilityTypeAttrValue();


    facilityTypeAttr.setValueObject(facilityTypeAttrValue);
    return facilityTypeAttr;
  }

  /** Removes/deletes the specified  FacilityTypeAttr
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
        FacilityTypeAttr facilityTypeAttr = (FacilityTypeAttr) iterator.next();
        Debug.logInfo("Removing facilityTypeAttr with facilityTypeId:" + facilityTypeId);
        facilityTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityTypeAttr records by the following parameters:
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityTypeId(String facilityTypeId)
  {
    Debug.logInfo("findByFacilityTypeId: facilityTypeId:" + facilityTypeId);

    Collection collection = null;
    if(facilityTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityTypeAttrHome().findByFacilityTypeId(facilityTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  FacilityTypeAttr
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
        FacilityTypeAttr facilityTypeAttr = (FacilityTypeAttr) iterator.next();
        Debug.logInfo("Removing facilityTypeAttr with name:" + name);
        facilityTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds FacilityTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getFacilityTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
