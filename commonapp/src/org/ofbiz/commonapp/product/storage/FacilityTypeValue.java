
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class FacilityTypeValue implements FacilityType
{
  /** The variable of the FACILITY_TYPE_ID column of the FACILITY_TYPE table. */
  private String facilityTypeId;
  /** The variable of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the FACILITY_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the FACILITY_TYPE table. */
  private String description;

  private FacilityType facilityType;

  public FacilityTypeValue()
  {
    this.facilityTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.facilityType = null;
  }

  public FacilityTypeValue(FacilityType facilityType) throws RemoteException
  {
    if(facilityType == null) return;
  
    this.facilityTypeId = facilityType.getFacilityTypeId();
    this.parentTypeId = facilityType.getParentTypeId();
    this.hasTable = facilityType.getHasTable();
    this.description = facilityType.getDescription();

    this.facilityType = facilityType;
  }

  public FacilityTypeValue(FacilityType facilityType, String facilityTypeId, String parentTypeId, String hasTable, String description)
  {
    if(facilityType == null) return;
  
    this.facilityTypeId = facilityTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.facilityType = facilityType;
  }


  /** Get the primary key of the FACILITY_TYPE_ID column of the FACILITY_TYPE table. */
  public String getFacilityTypeId()  throws RemoteException { return facilityTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(facilityType!=null) facilityType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the FACILITY_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the FACILITY_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(facilityType!=null) facilityType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the FACILITY_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the FACILITY_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(facilityType!=null) facilityType.setDescription(description);
  }

  /** Get the value object of the FacilityType class. */
  public FacilityType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the FacilityType class. */
  public void setValueObject(FacilityType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(facilityType!=null) facilityType.setValueObject(valueObject);

    if(facilityTypeId == null) facilityTypeId = valueObject.getFacilityTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent FacilityType entity corresponding to this entity. */
  public FacilityType getParentFacilityType() { return FacilityTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent FacilityType entity corresponding to this entity. */
  public void removeParentFacilityType() { FacilityTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child FacilityType related entities. */
  public Collection getChildFacilityTypes() { return FacilityTypeHelper.findByParentTypeId(facilityTypeId); }
  /** Get the Child FacilityType keyed by member(s) of this class, and other passed parameters. */
  public FacilityType getChildFacilityType(String facilityTypeId) { return FacilityTypeHelper.findByPrimaryKey(facilityTypeId); }
  /** Remove Child FacilityType related entities. */
  public void removeChildFacilityTypes() { FacilityTypeHelper.removeByParentTypeId(facilityTypeId); }
  /** Remove the Child FacilityType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildFacilityType(String facilityTypeId) { FacilityTypeHelper.removeByPrimaryKey(facilityTypeId); }

  /** Get a collection of  FacilityTypeAttr related entities. */
  public Collection getFacilityTypeAttrs() { return FacilityTypeAttrHelper.findByFacilityTypeId(facilityTypeId); }
  /** Get the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public FacilityTypeAttr getFacilityTypeAttr(String name) { return FacilityTypeAttrHelper.findByPrimaryKey(facilityTypeId, name); }
  /** Remove  FacilityTypeAttr related entities. */
  public void removeFacilityTypeAttrs() { FacilityTypeAttrHelper.removeByFacilityTypeId(facilityTypeId); }
  /** Remove the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityTypeAttr(String name) { FacilityTypeAttrHelper.removeByPrimaryKey(facilityTypeId, name); }

  /** Get a collection of  Facility related entities. */
  public Collection getFacilitys() { return FacilityHelper.findByFacilityTypeId(facilityTypeId); }
  /** Get the  Facility keyed by member(s) of this class, and other passed parameters. */
  public Facility getFacility(String facilityId) { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove  Facility related entities. */
  public void removeFacilitys() { FacilityHelper.removeByFacilityTypeId(facilityTypeId); }
  /** Remove the  Facility keyed by member(s) of this class, and other passed parameters. */
  public void removeFacility(String facilityId) { FacilityHelper.removeByPrimaryKey(facilityId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(facilityType!=null) return facilityType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(facilityType!=null) return facilityType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(facilityType!=null) return facilityType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(facilityType!=null) return facilityType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(facilityType!=null) facilityType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
