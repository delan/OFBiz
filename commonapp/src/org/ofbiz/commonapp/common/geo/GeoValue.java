
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Geographic Boundary Entity
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
public class GeoValue implements Geo
{
  /** The variable of the GEO_ID column of the GEO table. */
  private String geoId;
  /** The variable of the GEO_TYPE_ID column of the GEO table. */
  private String geoTypeId;
  /** The variable of the NAME column of the GEO table. */
  private String name;
  /** The variable of the GEO_CODE column of the GEO table. */
  private String geoCode;
  /** The variable of the ABBREVIATION column of the GEO table. */
  private String abbreviation;

  private Geo geo;

  public GeoValue()
  {
    this.geoId = null;
    this.geoTypeId = null;
    this.name = null;
    this.geoCode = null;
    this.abbreviation = null;

    this.geo = null;
  }

  public GeoValue(Geo geo) throws RemoteException
  {
    if(geo == null) return;
  
    this.geoId = geo.getGeoId();
    this.geoTypeId = geo.getGeoTypeId();
    this.name = geo.getName();
    this.geoCode = geo.getGeoCode();
    this.abbreviation = geo.getAbbreviation();

    this.geo = geo;
  }

  public GeoValue(Geo geo, String geoId, String geoTypeId, String name, String geoCode, String abbreviation)
  {
    if(geo == null) return;
  
    this.geoId = geoId;
    this.geoTypeId = geoTypeId;
    this.name = name;
    this.geoCode = geoCode;
    this.abbreviation = abbreviation;

    this.geo = geo;
  }


  /** Get the primary key of the GEO_ID column of the GEO table. */
  public String getGeoId()  throws RemoteException { return geoId; }

  /** Get the value of the GEO_TYPE_ID column of the GEO table. */
  public String getGeoTypeId() throws RemoteException { return geoTypeId; }
  /** Set the value of the GEO_TYPE_ID column of the GEO table. */
  public void setGeoTypeId(String geoTypeId) throws RemoteException
  {
    this.geoTypeId = geoTypeId;
    if(geo!=null) geo.setGeoTypeId(geoTypeId);
  }

  /** Get the value of the NAME column of the GEO table. */
  public String getName() throws RemoteException { return name; }
  /** Set the value of the NAME column of the GEO table. */
  public void setName(String name) throws RemoteException
  {
    this.name = name;
    if(geo!=null) geo.setName(name);
  }

  /** Get the value of the GEO_CODE column of the GEO table. */
  public String getGeoCode() throws RemoteException { return geoCode; }
  /** Set the value of the GEO_CODE column of the GEO table. */
  public void setGeoCode(String geoCode) throws RemoteException
  {
    this.geoCode = geoCode;
    if(geo!=null) geo.setGeoCode(geoCode);
  }

  /** Get the value of the ABBREVIATION column of the GEO table. */
  public String getAbbreviation() throws RemoteException { return abbreviation; }
  /** Set the value of the ABBREVIATION column of the GEO table. */
  public void setAbbreviation(String abbreviation) throws RemoteException
  {
    this.abbreviation = abbreviation;
    if(geo!=null) geo.setAbbreviation(abbreviation);
  }

  /** Get the value object of the Geo class. */
  public Geo getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Geo class. */
  public void setValueObject(Geo valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(geo!=null) geo.setValueObject(valueObject);

    if(geoId == null) geoId = valueObject.getGeoId();
    geoTypeId = valueObject.getGeoTypeId();
    name = valueObject.getName();
    geoCode = valueObject.getGeoCode();
    abbreviation = valueObject.getAbbreviation();
  }


  /** Get the  GeoType entity corresponding to this entity. */
  public GeoType getGeoType() { return GeoTypeHelper.findByPrimaryKey(geoTypeId); }
  /** Remove the  GeoType entity corresponding to this entity. */
  public void removeGeoType() { GeoTypeHelper.removeByPrimaryKey(geoTypeId); }

  /** Get a collection of Main GeoAssoc related entities. */
  public Collection getMainGeoAssocs() { return GeoAssocHelper.findByGeoId(geoId); }
  /** Get the Main GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getMainGeoAssoc(String geoIdTo) { return GeoAssocHelper.findByPrimaryKey(geoId, geoIdTo); }
  /** Remove Main GeoAssoc related entities. */
  public void removeMainGeoAssocs() { GeoAssocHelper.removeByGeoId(geoId); }
  /** Remove the Main GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeMainGeoAssoc(String geoIdTo) { GeoAssocHelper.removeByPrimaryKey(geoId, geoIdTo); }

  /** Get a collection of Assoc GeoAssoc related entities. */
  public Collection getAssocGeoAssocs() { return GeoAssocHelper.findByGeoIdTo(geoId); }
  /** Get the Assoc GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getAssocGeoAssoc(String geoId) { return GeoAssocHelper.findByPrimaryKey(geoId, geoId); }
  /** Remove Assoc GeoAssoc related entities. */
  public void removeAssocGeoAssocs() { GeoAssocHelper.removeByGeoIdTo(geoId); }
  /** Remove the Assoc GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocGeoAssoc(String geoId) { GeoAssocHelper.removeByPrimaryKey(geoId, geoId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(geo!=null) return geo.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(geo!=null) return geo.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(geo!=null) return geo.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(geo!=null) return geo.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(geo!=null) geo.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
