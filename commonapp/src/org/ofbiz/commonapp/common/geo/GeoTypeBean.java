
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Geographic Boundary Type Entity
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
public class GeoTypeBean implements EntityBean
{
  /** The variable for the GEO_TYPE_ID column of the GEO_TYPE table. */
  public String geoTypeId;
  /** The variable for the PARENT_TYPE_ID column of the GEO_TYPE table. */
  public String parentTypeId;
  /** The variable for the HAS_TABLE column of the GEO_TYPE table. */
  public String hasTable;
  /** The variable for the DESCRIPTION column of the GEO_TYPE table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the GeoTypeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key GEO_TYPE_ID column of the GEO_TYPE table. */
  public String getGeoTypeId() { return geoTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the GEO_TYPE table. */
  public String getParentTypeId() { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the GEO_TYPE table. */
  public void setParentTypeId(String parentTypeId)
  {
    this.parentTypeId = parentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the HAS_TABLE column of the GEO_TYPE table. */
  public String getHasTable() { return hasTable; }
  /** Set the value of the HAS_TABLE column of the GEO_TYPE table. */
  public void setHasTable(String hasTable)
  {
    this.hasTable = hasTable;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the GEO_TYPE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the GEO_TYPE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the GeoTypeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(GeoType valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getParentTypeId() != null)
      {
        this.parentTypeId = valueObject.getParentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getHasTable() != null)
      {
        this.hasTable = valueObject.getHasTable();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the GeoTypeBean object
   *@return    The ValueObject value
   */
  public GeoType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new GeoTypeValue((GeoType)this.entityContext.getEJBObject(), geoTypeId, parentTypeId, hasTable, description);
    }
    else { return null; }
  }


  /** Get the Parent GeoType entity corresponding to this entity. */
  public GeoType getParentGeoType() { return GeoTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent GeoType entity corresponding to this entity. */
  public void removeParentGeoType() { GeoTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child GeoType related entities. */
  public Collection getChildGeoTypes() { return GeoTypeHelper.findByParentTypeId(geoTypeId); }
  /** Get the Child GeoType keyed by member(s) of this class, and other passed parameters. */
  public GeoType getChildGeoType(String geoTypeId) { return GeoTypeHelper.findByPrimaryKey(geoTypeId); }
  /** Remove Child GeoType related entities. */
  public void removeChildGeoTypes() { GeoTypeHelper.removeByParentTypeId(geoTypeId); }
  /** Remove the Child GeoType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildGeoType(String geoTypeId) { GeoTypeHelper.removeByPrimaryKey(geoTypeId); }

  /** Get a collection of  Geo related entities. */
  public Collection getGeos() { return GeoHelper.findByGeoTypeId(geoTypeId); }
  /** Get the  Geo keyed by member(s) of this class, and other passed parameters. */
  public Geo getGeo(String geoId) { return GeoHelper.findByPrimaryKey(geoId); }
  /** Remove  Geo related entities. */
  public void removeGeos() { GeoHelper.removeByGeoTypeId(geoTypeId); }
  /** Remove the  Geo keyed by member(s) of this class, and other passed parameters. */
  public void removeGeo(String geoId) { GeoHelper.removeByPrimaryKey(geoId); }


  /** Description of the Method
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String geoTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {
    this.geoTypeId = geoTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String geoTypeId) throws CreateException
  {
    return ejbCreate(geoTypeId, null, null, null);
  }

  /** Description of the Method
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String geoTypeId, String parentTypeId, String hasTable, String description) throws CreateException {}

  /** Description of the Method
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String geoTypeId) throws CreateException
  {
    ejbPostCreate(geoTypeId, null, null, null);
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
