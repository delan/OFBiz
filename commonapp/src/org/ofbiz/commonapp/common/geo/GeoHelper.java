
package org.ofbiz.commonapp.common.geo;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Geographic Boundary Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Geo Entity EJB; acts as a proxy for the Home interface
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
public class GeoHelper
{

  /** A static variable to cache the Home object for the Geo EJB */
  private static GeoHome geoHome = null;

  /** Initializes the geoHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The GeoHome instance for the default EJB server
   */
  public static GeoHome getGeoHome()
  {
    if(geoHome == null) //don't want to block here
    {
      synchronized(GeoHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(geoHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.geo.GeoHome");
            geoHome = (GeoHome)MyNarrow.narrow(homeObject, GeoHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("geo home obtained " + geoHome);
        }
      }
    }
    return geoHome;
  }




  /** Remove the Geo corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Geo geo = findByPrimaryKey(primaryKey);
    try
    {
      if(geo != null)
      {
        geo.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Geo by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Geo corresponding to the primaryKey
   */
  public static Geo findByPrimaryKey(java.lang.String primaryKey)
  {
    Geo geo = null;
    Debug.logInfo("GeoHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      geo = (Geo)MyNarrow.narrow(getGeoHome().findByPrimaryKey(primaryKey), Geo.class);
      if(geo != null)
      {
        geo = geo.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return geo;
  }

  /** Finds all Geo entities
   *@return    Collection containing all Geo entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("GeoHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getGeoHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Geo
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@param  geoCode                  Field of the GEO_CODE column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@return                Description of the Returned Value
   */
  public static Geo create(String geoId, String geoTypeId, String name, String geoCode, String abbreviation)
  {
    Geo geo = null;
    Debug.logInfo("GeoHelper.create: geoId: " + geoId);
    if(geoId == null) { return null; }

    try { geo = (Geo)MyNarrow.narrow(getGeoHome().create(geoId, geoTypeId, name, geoCode, abbreviation), Geo.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create geo with geoId: " + geoId);
      Debug.logError(ce);
      geo = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return geo;
  }

  /** Updates the corresponding Geo
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@param  geoCode                  Field of the GEO_CODE column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@return                Description of the Returned Value
   */
  public static Geo update(String geoId, String geoTypeId, String name, String geoCode, String abbreviation) throws java.rmi.RemoteException
  {
    if(geoId == null) { return null; }
    Geo geo = findByPrimaryKey(geoId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Geo geoValue = new GeoValue();

    if(geoTypeId != null) { geoValue.setGeoTypeId(geoTypeId); }
    if(name != null) { geoValue.setName(name); }
    if(geoCode != null) { geoValue.setGeoCode(geoCode); }
    if(abbreviation != null) { geoValue.setAbbreviation(abbreviation); }

    geo.setValueObject(geoValue);
    return geo;
  }

  /** Removes/deletes the specified  Geo
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   */
  public static void removeByGeoTypeId(String geoTypeId)
  {
    if(geoTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGeoTypeId(geoTypeId));

    while(iterator.hasNext())
    {
      try
      {
        Geo geo = (Geo) iterator.next();
        Debug.logInfo("Removing geo with geoTypeId:" + geoTypeId);
        geo.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Geo records by the following parameters:
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoTypeId(String geoTypeId)
  {
    Debug.logInfo("findByGeoTypeId: geoTypeId:" + geoTypeId);

    Collection collection = null;
    if(geoTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoHome().findByGeoTypeId(geoTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Geo
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
        Geo geo = (Geo) iterator.next();
        Debug.logInfo("Removing geo with name:" + name);
        geo.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Geo records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Geo
   *@param  geoCode                  Field of the GEO_CODE column.
   */
  public static void removeByGeoCode(String geoCode)
  {
    if(geoCode == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGeoCode(geoCode));

    while(iterator.hasNext())
    {
      try
      {
        Geo geo = (Geo) iterator.next();
        Debug.logInfo("Removing geo with geoCode:" + geoCode);
        geo.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Geo records by the following parameters:
   *@param  geoCode                  Field of the GEO_CODE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGeoCode(String geoCode)
  {
    Debug.logInfo("findByGeoCode: geoCode:" + geoCode);

    Collection collection = null;
    if(geoCode == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoHome().findByGeoCode(geoCode), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  Geo
   *@param  abbreviation                  Field of the ABBREVIATION column.
   */
  public static void removeByAbbreviation(String abbreviation)
  {
    if(abbreviation == null) return;
    Iterator iterator = UtilMisc.toIterator(findByAbbreviation(abbreviation));

    while(iterator.hasNext())
    {
      try
      {
        Geo geo = (Geo) iterator.next();
        Debug.logInfo("Removing geo with abbreviation:" + abbreviation);
        geo.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Geo records by the following parameters:
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@return      Description of the Returned Value
   */
  public static Collection findByAbbreviation(String abbreviation)
  {
    Debug.logInfo("findByAbbreviation: abbreviation:" + abbreviation);

    Collection collection = null;
    if(abbreviation == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getGeoHome().findByAbbreviation(abbreviation), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
