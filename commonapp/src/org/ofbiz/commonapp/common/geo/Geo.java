
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


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

public interface Geo extends EJBObject
{
  /** Get the primary key of the GEO_ID column of the GEO table. */
  public String getGeoId() throws RemoteException;
  
  /** Get the value of the GEO_TYPE_ID column of the GEO table. */
  public String getGeoTypeId() throws RemoteException;
  /** Set the value of the GEO_TYPE_ID column of the GEO table. */
  public void setGeoTypeId(String geoTypeId) throws RemoteException;
  
  /** Get the value of the NAME column of the GEO table. */
  public String getName() throws RemoteException;
  /** Set the value of the NAME column of the GEO table. */
  public void setName(String name) throws RemoteException;
  
  /** Get the value of the GEO_CODE column of the GEO table. */
  public String getGeoCode() throws RemoteException;
  /** Set the value of the GEO_CODE column of the GEO table. */
  public void setGeoCode(String geoCode) throws RemoteException;
  
  /** Get the value of the ABBREVIATION column of the GEO table. */
  public String getAbbreviation() throws RemoteException;
  /** Set the value of the ABBREVIATION column of the GEO table. */
  public void setAbbreviation(String abbreviation) throws RemoteException;
  

  /** Get the value object of this Geo class. */
  public Geo getValueObject() throws RemoteException;
  /** Set the values in the value object of this Geo class. */
  public void setValueObject(Geo geoValue) throws RemoteException;


  /** Get the  GeoType entity corresponding to this entity. */
  public GeoType getGeoType() throws RemoteException;
  /** Remove the  GeoType entity corresponding to this entity. */
  public void removeGeoType() throws RemoteException;  

  /** Get a collection of Main GeoAssoc related entities. */
  public Collection getMainGeoAssocs() throws RemoteException;
  /** Get the Main GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getMainGeoAssoc(String geoIdTo) throws RemoteException;
  /** Remove Main GeoAssoc related entities. */
  public void removeMainGeoAssocs() throws RemoteException;
  /** Remove the Main GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeMainGeoAssoc(String geoIdTo) throws RemoteException;

  /** Get a collection of Assoc GeoAssoc related entities. */
  public Collection getAssocGeoAssocs() throws RemoteException;
  /** Get the Assoc GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getAssocGeoAssoc(String geoId) throws RemoteException;
  /** Remove Assoc GeoAssoc related entities. */
  public void removeAssocGeoAssocs() throws RemoteException;
  /** Remove the Assoc GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocGeoAssoc(String geoId) throws RemoteException;

}
