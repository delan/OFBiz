
package org.ofbiz.commonapp.common.geo;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Geographic Boundary Association Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the GeoAssocType Entity EJB; acts as a proxy for the Home interface
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
public class GeoAssocTypeHelper
{

  /** A static variable to cache the Home object for the GeoAssocType EJB */
  private static GeoAssocTypeHome geoAssocTypeHome = null;

  /** Initializes the geoAssocTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The GeoAssocTypeHome instance for the default EJB server
   */
  public static GeoAssocTypeHome getGeoAssocTypeHome()
  {
    if(geoAssocTypeHome == null) //don't want to block here
    {
      synchronized(GeoAssocTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(geoAssocTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.geo.GeoAssocTypeHome");
            geoAssocTypeHome = (GeoAssocTypeHome)MyNarrow.narrow(homeObject, GeoAssocTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("geoAssocType home obtained " + geoAssocTypeHome);
        }
      }
    }
    return geoAssocTypeHome;
  }




  /** Remove the GeoAssocType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    GeoAssocType geoAssocType = findByPrimaryKey(primaryKey);
    try
    {
      if(geoAssocType != null)
      {
        geoAssocType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a GeoAssocType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The GeoAssocType corresponding to the primaryKey
   */
  public static GeoAssocType findByPrimaryKey(java.lang.String primaryKey)
  {
    GeoAssocType geoAssocType = null;
    Debug.logInfo("GeoAssocTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      geoAssocType = (GeoAssocType)MyNarrow.narrow(getGeoAssocTypeHome().findByPrimaryKey(primaryKey), GeoAssocType.class);
      if(geoAssocType != null)
      {
        geoAssocType = geoAssocType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return geoAssocType;
  }

  /** Finds all GeoAssocType entities
   *@return    Collection containing all GeoAssocType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("GeoAssocTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getGeoAssocTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a GeoAssocType
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static GeoAssocType create(String geoAssocTypeId, String description)
  {
    GeoAssocType geoAssocType = null;
    Debug.logInfo("GeoAssocTypeHelper.create: geoAssocTypeId: " + geoAssocTypeId);
    if(geoAssocTypeId == null) { return null; }

    try { geoAssocType = (GeoAssocType)MyNarrow.narrow(getGeoAssocTypeHome().create(geoAssocTypeId, description), GeoAssocType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create geoAssocType with geoAssocTypeId: " + geoAssocTypeId);
      Debug.logError(ce);
      geoAssocType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return geoAssocType;
  }

  /** Updates the corresponding GeoAssocType
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static GeoAssocType update(String geoAssocTypeId, String description) throws java.rmi.RemoteException
  {
    if(geoAssocTypeId == null) { return null; }
    GeoAssocType geoAssocType = findByPrimaryKey(geoAssocTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    GeoAssocType geoAssocTypeValue = new GeoAssocTypeValue();

    if(description != null) { geoAssocTypeValue.setDescription(description); }

    geoAssocType.setValueObject(geoAssocTypeValue);
    return geoAssocType;
  }


}
