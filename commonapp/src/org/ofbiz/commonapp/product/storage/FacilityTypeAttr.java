
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Facility Type Attribute Entity
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */

public interface FacilityTypeAttr extends EJBObject
{
  /** Get the primary key of the FACILITY_TYPE_ID column of the FACILITY_TYPE_ATTR table. */
  public String getFacilityTypeId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the FACILITY_TYPE_ATTR table. */
  public String getName() throws RemoteException;
  

  /** Get the value object of this FacilityTypeAttr class. */
  public FacilityTypeAttr getValueObject() throws RemoteException;
  /** Set the values in the value object of this FacilityTypeAttr class. */
  public void setValueObject(FacilityTypeAttr facilityTypeAttrValue) throws RemoteException;


  /** Get the  FacilityType entity corresponding to this entity. */
  public FacilityType getFacilityType() throws RemoteException;
  /** Remove the  FacilityType entity corresponding to this entity. */
  public void removeFacilityType() throws RemoteException;  

  /** Get a collection of  FacilityAttribute related entities. */
  public Collection getFacilityAttributes() throws RemoteException;
  /** Get the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public FacilityAttribute getFacilityAttribute(String facilityId) throws RemoteException;
  /** Remove  FacilityAttribute related entities. */
  public void removeFacilityAttributes() throws RemoteException;
  /** Remove the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityAttribute(String facilityId) throws RemoteException;

  /** Get a collection of  Facility related entities. */
  public Collection getFacilitys() throws RemoteException;
  /** Get the  Facility keyed by member(s) of this class, and other passed parameters. */
  public Facility getFacility(String facilityId) throws RemoteException;
  /** Remove  Facility related entities. */
  public void removeFacilitys() throws RemoteException;
  /** Remove the  Facility keyed by member(s) of this class, and other passed parameters. */
  public void removeFacility(String facilityId) throws RemoteException;

}
