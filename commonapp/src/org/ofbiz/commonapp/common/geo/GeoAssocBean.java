
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Geographic Boundary Association Entity
 * <p><b>Description:</b> None
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
public class GeoAssocBean implements EntityBean
{
  /** The variable for the GEO_ID column of the GEO_ASSOC table. */
  public String geoId;
  /** The variable for the GEO_ID_TO column of the GEO_ASSOC table. */
  public String geoIdTo;
  /** The variable for the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public String geoAssocTypeId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the GeoAssocBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key GEO_ID column of the GEO_ASSOC table. */
  public String getGeoId() { return geoId; }

  /** Get the primary key GEO_ID_TO column of the GEO_ASSOC table. */
  public String getGeoIdTo() { return geoIdTo; }

  /** Get the value of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public String getGeoAssocTypeId() { return geoAssocTypeId; }
  /** Set the value of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public void setGeoAssocTypeId(String geoAssocTypeId)
  {
    this.geoAssocTypeId = geoAssocTypeId;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the GeoAssocBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(GeoAssoc valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getGeoAssocTypeId() != null)
      {
        this.geoAssocTypeId = valueObject.getGeoAssocTypeId();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the GeoAssocBean object
   *@return    The ValueObject value
   */
  public GeoAssoc getValueObject()
  {
    if(this.entityContext != null)
    {
      return new GeoAssocValue((GeoAssoc)this.entityContext.getEJBObject(), geoId, geoIdTo, geoAssocTypeId);
    }
    else { return null; }
  }


  /** Get the Main Geo entity corresponding to this entity. */
  public Geo getMainGeo() { return GeoHelper.findByPrimaryKey(geoId); }
  /** Remove the Main Geo entity corresponding to this entity. */
  public void removeMainGeo() { GeoHelper.removeByPrimaryKey(geoId); }

  /** Get the Assoc Geo entity corresponding to this entity. */
  public Geo getAssocGeo() { return GeoHelper.findByPrimaryKey(geoIdTo); }
  /** Remove the Assoc Geo entity corresponding to this entity. */
  public void removeAssocGeo() { GeoHelper.removeByPrimaryKey(geoIdTo); }

  /** Get the  GeoAssocType entity corresponding to this entity. */
  public GeoAssocType getGeoAssocType() { return GeoAssocTypeHelper.findByPrimaryKey(geoAssocTypeId); }
  /** Remove the  GeoAssocType entity corresponding to this entity. */
  public void removeGeoAssocType() { GeoAssocTypeHelper.removeByPrimaryKey(geoAssocTypeId); }


  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.common.geo.GeoAssocPK ejbCreate(String geoId, String geoIdTo, String geoAssocTypeId) throws CreateException
  {
    this.geoId = geoId;
    this.geoIdTo = geoIdTo;
    this.geoAssocTypeId = geoAssocTypeId;
    return null;
  }

  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.common.geo.GeoAssocPK ejbCreate(String geoId, String geoIdTo) throws CreateException
  {
    return ejbCreate(geoId, geoIdTo, null);
  }

  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@param  geoAssocTypeId                  Field of the GEO_ASSOC_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String geoId, String geoIdTo, String geoAssocTypeId) throws CreateException {}

  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoIdTo                  Field of the GEO_ID_TO column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String geoId, String geoIdTo) throws CreateException
  {
    ejbPostCreate(geoId, geoIdTo, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
