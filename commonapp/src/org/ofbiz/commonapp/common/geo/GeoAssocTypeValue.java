
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class GeoAssocTypeValue implements GeoAssocType
{
  /** The variable of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC_TYPE table. */
  private String geoAssocTypeId;
  /** The variable of the DESCRIPTION column of the GEO_ASSOC_TYPE table. */
  private String description;

  private GeoAssocType geoAssocType;

  public GeoAssocTypeValue()
  {
    this.geoAssocTypeId = null;
    this.description = null;

    this.geoAssocType = null;
  }

  public GeoAssocTypeValue(GeoAssocType geoAssocType) throws RemoteException
  {
    if(geoAssocType == null) return;
  
    this.geoAssocTypeId = geoAssocType.getGeoAssocTypeId();
    this.description = geoAssocType.getDescription();

    this.geoAssocType = geoAssocType;
  }

  public GeoAssocTypeValue(GeoAssocType geoAssocType, String geoAssocTypeId, String description)
  {
    if(geoAssocType == null) return;
  
    this.geoAssocTypeId = geoAssocTypeId;
    this.description = description;

    this.geoAssocType = geoAssocType;
  }


  /** Get the primary key of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC_TYPE table. */
  public String getGeoAssocTypeId()  throws RemoteException { return geoAssocTypeId; }

  /** Get the value of the DESCRIPTION column of the GEO_ASSOC_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the GEO_ASSOC_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(geoAssocType!=null) geoAssocType.setDescription(description);
  }

  /** Get the value object of the GeoAssocType class. */
  public GeoAssocType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the GeoAssocType class. */
  public void setValueObject(GeoAssocType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(geoAssocType!=null) geoAssocType.setValueObject(valueObject);

    if(geoAssocTypeId == null) geoAssocTypeId = valueObject.getGeoAssocTypeId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  GeoAssoc related entities. */
  public Collection getGeoAssocs() { return GeoAssocHelper.findByGeoAssocTypeId(geoAssocTypeId); }
  /** Get the  GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getGeoAssoc(String geoId, String geoIdTo) { return GeoAssocHelper.findByPrimaryKey(geoId, geoIdTo); }
  /** Remove  GeoAssoc related entities. */
  public void removeGeoAssocs() { GeoAssocHelper.removeByGeoAssocTypeId(geoAssocTypeId); }
  /** Remove the  GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeGeoAssoc(String geoId, String geoIdTo) { GeoAssocHelper.removeByPrimaryKey(geoId, geoIdTo); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(geoAssocType!=null) return geoAssocType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(geoAssocType!=null) return geoAssocType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(geoAssocType!=null) return geoAssocType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(geoAssocType!=null) return geoAssocType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(geoAssocType!=null) geoAssocType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
