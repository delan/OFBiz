
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
public class GeoAssocValue implements GeoAssoc
{
  /** The variable of the GEO_ID column of the GEO_ASSOC table. */
  private String geoId;
  /** The variable of the GEO_ID_TO column of the GEO_ASSOC table. */
  private String geoIdTo;
  /** The variable of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  private String geoAssocTypeId;

  private GeoAssoc geoAssoc;

  public GeoAssocValue()
  {
    this.geoId = null;
    this.geoIdTo = null;
    this.geoAssocTypeId = null;

    this.geoAssoc = null;
  }

  public GeoAssocValue(GeoAssoc geoAssoc) throws RemoteException
  {
    if(geoAssoc == null) return;
  
    this.geoId = geoAssoc.getGeoId();
    this.geoIdTo = geoAssoc.getGeoIdTo();
    this.geoAssocTypeId = geoAssoc.getGeoAssocTypeId();

    this.geoAssoc = geoAssoc;
  }

  public GeoAssocValue(GeoAssoc geoAssoc, String geoId, String geoIdTo, String geoAssocTypeId)
  {
    if(geoAssoc == null) return;
  
    this.geoId = geoId;
    this.geoIdTo = geoIdTo;
    this.geoAssocTypeId = geoAssocTypeId;

    this.geoAssoc = geoAssoc;
  }


  /** Get the primary key of the GEO_ID column of the GEO_ASSOC table. */
  public String getGeoId()  throws RemoteException { return geoId; }

  /** Get the primary key of the GEO_ID_TO column of the GEO_ASSOC table. */
  public String getGeoIdTo()  throws RemoteException { return geoIdTo; }

  /** Get the value of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public String getGeoAssocTypeId() throws RemoteException { return geoAssocTypeId; }
  /** Set the value of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public void setGeoAssocTypeId(String geoAssocTypeId) throws RemoteException
  {
    this.geoAssocTypeId = geoAssocTypeId;
    if(geoAssoc!=null) geoAssoc.setGeoAssocTypeId(geoAssocTypeId);
  }

  /** Get the value object of the GeoAssoc class. */
  public GeoAssoc getValueObject() throws RemoteException { return this; }
  /** Set the value object of the GeoAssoc class. */
  public void setValueObject(GeoAssoc valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(geoAssoc!=null) geoAssoc.setValueObject(valueObject);

    if(geoId == null) geoId = valueObject.getGeoId();
    if(geoIdTo == null) geoIdTo = valueObject.getGeoIdTo();
    geoAssocTypeId = valueObject.getGeoAssocTypeId();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(geoAssoc!=null) return geoAssoc.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(geoAssoc!=null) return geoAssoc.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(geoAssoc!=null) return geoAssoc.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(geoAssoc!=null) return geoAssoc.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(geoAssoc!=null) geoAssoc.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
