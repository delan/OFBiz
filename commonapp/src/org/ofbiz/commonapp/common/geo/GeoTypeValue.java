
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class GeoTypeValue implements GeoType
{
  /** The variable of the GEO_TYPE_ID column of the GEO_TYPE table. */
  private String geoTypeId;
  /** The variable of the PARENT_TYPE_ID column of the GEO_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the GEO_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the GEO_TYPE table. */
  private String description;

  private GeoType geoType;

  public GeoTypeValue()
  {
    this.geoTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.geoType = null;
  }

  public GeoTypeValue(GeoType geoType) throws RemoteException
  {
    if(geoType == null) return;
  
    this.geoTypeId = geoType.getGeoTypeId();
    this.parentTypeId = geoType.getParentTypeId();
    this.hasTable = geoType.getHasTable();
    this.description = geoType.getDescription();

    this.geoType = geoType;
  }

  public GeoTypeValue(GeoType geoType, String geoTypeId, String parentTypeId, String hasTable, String description)
  {
    if(geoType == null) return;
  
    this.geoTypeId = geoTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.geoType = geoType;
  }


  /** Get the primary key of the GEO_TYPE_ID column of the GEO_TYPE table. */
  public String getGeoTypeId()  throws RemoteException { return geoTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the GEO_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the GEO_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(geoType!=null) geoType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the GEO_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the GEO_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(geoType!=null) geoType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the GEO_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the GEO_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(geoType!=null) geoType.setDescription(description);
  }

  /** Get the value object of the GeoType class. */
  public GeoType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the GeoType class. */
  public void setValueObject(GeoType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(geoType!=null) geoType.setValueObject(valueObject);

    if(geoTypeId == null) geoTypeId = valueObject.getGeoTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(geoType!=null) return geoType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(geoType!=null) return geoType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(geoType!=null) return geoType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(geoType!=null) return geoType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(geoType!=null) geoType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
