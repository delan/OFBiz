
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

public interface GeoAssoc extends EJBObject
{
  /** Get the primary key of the GEO_ID column of the GEO_ASSOC table. */
  public String getGeoId() throws RemoteException;
  
  /** Get the primary key of the GEO_ID_TO column of the GEO_ASSOC table. */
  public String getGeoIdTo() throws RemoteException;
  
  /** Get the value of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public String getGeoAssocTypeId() throws RemoteException;
  /** Set the value of the GEO_ASSOC_TYPE_ID column of the GEO_ASSOC table. */
  public void setGeoAssocTypeId(String geoAssocTypeId) throws RemoteException;
  

  /** Get the value object of this GeoAssoc class. */
  public GeoAssoc getValueObject() throws RemoteException;
  /** Set the values in the value object of this GeoAssoc class. */
  public void setValueObject(GeoAssoc geoAssocValue) throws RemoteException;


  /** Get the Main Geo entity corresponding to this entity. */
  public Geo getMainGeo() throws RemoteException;
  /** Remove the Main Geo entity corresponding to this entity. */
  public void removeMainGeo() throws RemoteException;  

  /** Get the Assoc Geo entity corresponding to this entity. */
  public Geo getAssocGeo() throws RemoteException;
  /** Remove the Assoc Geo entity corresponding to this entity. */
  public void removeAssocGeo() throws RemoteException;  

  /** Get the  GeoAssocType entity corresponding to this entity. */
  public GeoAssocType getGeoAssocType() throws RemoteException;
  /** Remove the  GeoAssocType entity corresponding to this entity. */
  public void removeGeoAssocType() throws RemoteException;  

}
