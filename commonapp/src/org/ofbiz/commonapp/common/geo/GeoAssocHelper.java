
package org.ofbiz.commonapp.common.geo;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Geographic Boundary Association Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the GeoAssoc Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:21 MDT 2001
 *@version    1.0
 */
public class GeoAssocHelper
{

  /** A static variable to cache the Home object for the GeoAssoc EJB */
  private static GeoAssocHome geoAssocHome = null;

  /** Initializes the geoAssocHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The GeoAssocHome instance for the default EJB server
   */
  public static GeoAssocHome getGeoAssocHome()
  {
    if(geoAssocHome == null) //don't want to block here
    {
      synchronized(GeoAssocHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(geoAssocHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.geo.GeoAssocHome");
            geoAssocHome = (GeoAssocHome)MyNarrow.narrow(homeObject, GeoAssocHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("geoAssoc home obtained " + geoAssocHome);
        }
      }
    }
    return geoAssocHome;
  }



  /** Remove the GeoAssoc corresponding to the primaryKey specified by fields
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   */
  public static void removeByPrimaryKey(String geoId, String geoIdTo)
  {
    if(geoId == null || geoIdTo == null)
    {
      return;
    }
    GeoAssocPK primaryKey = new GeoAssocPK(geoId, geoIdTo);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the GeoAssoc corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.common.geo.GeoAssocPK primaryKey)
  {
    if(primaryKey == null) return;
    GeoAssoc geoAssoc = findByPrimaryKey(primaryKey);
    try
    {
      if(geoAssoc != null)
      {
        geoAssoc.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a GeoAssoc by its Primary Key, specified by individual fields
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@return       The GeoAssoc corresponding to the primaryKey
   */
  public static GeoAssoc findByPrimaryKey(String geoId, String geoIdTo)
  {
    if(geoId == null || geoIdTo == null) return null;
    GeoAssocPK primaryKey = new GeoAssocPK(geoId, geoIdTo);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a GeoAssoc by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The GeoAssoc corresponding to the primaryKey
   */
  public static GeoAssoc findByPrimaryKey(org.ofbiz.commonapp.common.geo.GeoAssocPK primaryKey)
  {
    GeoAssoc geoAssoc = null;
    Debug.logInfo("GeoAssocHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      geoAssoc = (GeoAssoc)MyNarrow.narrow(getGeoAssocHome().findByPrimaryKey(primaryKey), GeoAssoc.class);
      if(geoAssoc != null)
      {
        geoAssoc = geoAssoc.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return geoAssoc;
  }

  /** Finds all GeoAssoc entities
   *@return    Collection containing all GeoAssoc entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("GeoAssocHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getGeoAssocHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a GeoAssoc
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@return                Description of the Returned Value
   */
  public static GeoAssoc create(String geoId, String geoIdTo, String geoAssocTypeId)
  {
    GeoAssoc geoAssoc = null;
    Debug.logInfo("GeoAssocHelper.create: geoId, geoIdTo: " + geoId + ", " + geoIdTo);
    if(geoId == null || geoIdTo == null) { return null; }

    try { geoAssoc = (GeoAssoc)MyNarrow.narrow(getGeoAssocHome().create(geoId, geoIdTo, geoAssocTypeId), GeoAssoc.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create geoAssoc with geoId, geoIdTo: " + geoId + ", " + geoIdTo);
      Debug.logError(ce);
      geoAssoc = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return geoAssoc;
  }

  /** Updates the corresponding GeoAssoc
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@return                Description of the Returned Value
   */
  public static GeoAssoc update(String geoId, String geoIdTo, String geoAssocTypeId) throws java.rmi.RemoteException
  {
    if(geoId == null || geoIdTo == null) { return null; }
    GeoAssoc geoAssoc = findByPrimaryKey(geoId, geoIdTo);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    GeoAssoc geoAssocValue = new GeoAssocValue();

    if(geoAssocTypeId != null) { geoAssocValue.setGeoAssocTypeId(geoAssocTypeId); }

    geoAssoc.setValueObject(geoAssocValue);
    return geoAssoc;
  }

  /** Removes/deletes the specified  GeoAssoc
   *@param  geoId                  Field of the GEO_ID column.
   */
  public static void removeByGeoId(String geoId)
  {
    if(geoId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGeoId(geoId));

    while(iterator.hasNext())
    {
      try
      {
        GeoAssoc geoAssoc = (GeoAssoc) iterator.next();
        Debug.logInfo("Removing geoAssoc with geoId:" + geoId);
        geoAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GeoAssoc records by the following parameters:
   *@param  geoId                  Field of the GEO_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoId(String geoId)
  {
    Debug.logInfo("findByGeoId: geoId:" + geoId);

    Collection collection = null;
    if(geoId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoAssocHome().findByGeoId(geoId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  GeoAssoc
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   */
  public static void removeByGeoIdTo(String geoIdTo)
  {
    if(geoIdTo == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGeoIdTo(geoIdTo));

    while(iterator.hasNext())
    {
      try
      {
        GeoAssoc geoAssoc = (GeoAssoc) iterator.next();
        Debug.logInfo("Removing geoAssoc with geoIdTo:" + geoIdTo);
        geoAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GeoAssoc records by the following parameters:
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoIdTo(String geoIdTo)
  {
    Debug.logInfo("findByGeoIdTo: geoIdTo:" + geoIdTo);

    Collection collection = null;
    if(geoIdTo == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoAssocHome().findByGeoIdTo(geoIdTo), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  GeoAssoc
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   */
  public static void removeByGeoAssocTypeId(String geoAssocTypeId)
  {
    if(geoAssocTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGeoAssocTypeId(geoAssocTypeId));

    while(iterator.hasNext())
    {
      try
      {
        GeoAssoc geoAssoc = (GeoAssoc) iterator.next();
        Debug.logInfo("Removing geoAssoc with geoAssocTypeId:" + geoAssocTypeId);
        geoAssoc.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds GeoAssoc records by the following parameters:
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoAssocTypeId(String geoAssocTypeId)
  {
    Debug.logInfo("findByGeoAssocTypeId: geoAssocTypeId:" + geoAssocTypeId);

    Collection collection = null;
    if(geoAssocTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoAssocHome().findByGeoAssocTypeId(geoAssocTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
