
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Facility Type Entity
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

public interface FacilityType extends EJBObject
{
  /** Get the primary key of the FACILITY_TYPE_ID column of the FACILITY_TYPE table. */
  public String getFacilityTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the FACILITY_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the FACILITY_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the FACILITY_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the FACILITY_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this FacilityType class. */
  public FacilityType getValueObject() throws RemoteException;
  /** Set the values in the value object of this FacilityType class. */
  public void setValueObject(FacilityType facilityTypeValue) throws RemoteException;


  /** Get the Parent FacilityType entity corresponding to this entity. */
  public FacilityType getParentFacilityType() throws RemoteException;
  /** Remove the Parent FacilityType entity corresponding to this entity. */
  public void removeParentFacilityType() throws RemoteException;  

  /** Get a collection of Child FacilityType related entities. */
  public Collection getChildFacilityTypes() throws RemoteException;
  /** Get the Child FacilityType keyed by member(s) of this class, and other passed parameters. */
  public FacilityType getChildFacilityType(String facilityTypeId) throws RemoteException;
  /** Remove Child FacilityType related entities. */
  public void removeChildFacilityTypes() throws RemoteException;
  /** Remove the Child FacilityType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildFacilityType(String facilityTypeId) throws RemoteException;

  /** Get a collection of  FacilityTypeAttr related entities. */
  public Collection getFacilityTypeAttrs() throws RemoteException;
  /** Get the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public FacilityTypeAttr getFacilityTypeAttr(String name) throws RemoteException;
  /** Remove  FacilityTypeAttr related entities. */
  public void removeFacilityTypeAttrs() throws RemoteException;
  /** Remove the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityTypeAttr(String name) throws RemoteException;

  /** Get a collection of  Facility related entities. */
  public Collection getFacilitys() throws RemoteException;
  /** Get the  Facility keyed by member(s) of this class, and other passed parameters. */
  public Facility getFacility(String facilityId) throws RemoteException;
  /** Remove  Facility related entities. */
  public void removeFacilitys() throws RemoteException;
  /** Remove the  Facility keyed by member(s) of this class, and other passed parameters. */
  public void removeFacility(String facilityId) throws RemoteException;

}
