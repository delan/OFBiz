
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.inventory.*;

/**
 * <p><b>Title:</b> Facility Entity
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
public class FacilityValue implements Facility
{
  /** The variable of the FACILITY_ID column of the FACILITY table. */
  private String facilityId;
  /** The variable of the FACILITY_TYPE_ID column of the FACILITY table. */
  private String facilityTypeId;
  /** The variable of the FACILITY_NAME column of the FACILITY table. */
  private String facilityName;
  /** The variable of the SQUARE_FOOTAGE column of the FACILITY table. */
  private Long squareFootage;
  /** The variable of the DESCRIPTION column of the FACILITY table. */
  private String description;

  private Facility facility;

  public FacilityValue()
  {
    this.facilityId = null;
    this.facilityTypeId = null;
    this.facilityName = null;
    this.squareFootage = null;
    this.description = null;

    this.facility = null;
  }

  public FacilityValue(Facility facility) throws RemoteException
  {
    if(facility == null) return;
  
    this.facilityId = facility.getFacilityId();
    this.facilityTypeId = facility.getFacilityTypeId();
    this.facilityName = facility.getFacilityName();
    this.squareFootage = facility.getSquareFootage();
    this.description = facility.getDescription();

    this.facility = facility;
  }

  public FacilityValue(Facility facility, String facilityId, String facilityTypeId, String facilityName, Long squareFootage, String description)
  {
    if(facility == null) return;
  
    this.facilityId = facilityId;
    this.facilityTypeId = facilityTypeId;
    this.facilityName = facilityName;
    this.squareFootage = squareFootage;
    this.description = description;

    this.facility = facility;
  }


  /** Get the primary key of the FACILITY_ID column of the FACILITY table. */
  public String getFacilityId()  throws RemoteException { return facilityId; }

  /** Get the value of the FACILITY_TYPE_ID column of the FACILITY table. */
  public String getFacilityTypeId() throws RemoteException { return facilityTypeId; }
  /** Set the value of the FACILITY_TYPE_ID column of the FACILITY table. */
  public void setFacilityTypeId(String facilityTypeId) throws RemoteException
  {
    this.facilityTypeId = facilityTypeId;
    if(facility!=null) facility.setFacilityTypeId(facilityTypeId);
  }

  /** Get the value of the FACILITY_NAME column of the FACILITY table. */
  public String getFacilityName() throws RemoteException { return facilityName; }
  /** Set the value of the FACILITY_NAME column of the FACILITY table. */
  public void setFacilityName(String facilityName) throws RemoteException
  {
    this.facilityName = facilityName;
    if(facility!=null) facility.setFacilityName(facilityName);
  }

  /** Get the value of the SQUARE_FOOTAGE column of the FACILITY table. */
  public Long getSquareFootage() throws RemoteException { return squareFootage; }
  /** Set the value of the SQUARE_FOOTAGE column of the FACILITY table. */
  public void setSquareFootage(Long squareFootage) throws RemoteException
  {
    this.squareFootage = squareFootage;
    if(facility!=null) facility.setSquareFootage(squareFootage);
  }

  /** Get the value of the DESCRIPTION column of the FACILITY table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the FACILITY table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(facility!=null) facility.setDescription(description);
  }

  /** Get the value object of the Facility class. */
  public Facility getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Facility class. */
  public void setValueObject(Facility valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(facility!=null) facility.setValueObject(valueObject);

    if(facilityId == null) facilityId = valueObject.getFacilityId();
    facilityTypeId = valueObject.getFacilityTypeId();
    facilityName = valueObject.getFacilityName();
    squareFootage = valueObject.getSquareFootage();
    description = valueObject.getDescription();
  }


  /** Get the  FacilityType entity corresponding to this entity. */
  public FacilityType getFacilityType() { return FacilityTypeHelper.findByPrimaryKey(facilityTypeId); }
  /** Remove the  FacilityType entity corresponding to this entity. */
  public void removeFacilityType() { FacilityTypeHelper.removeByPrimaryKey(facilityTypeId); }

  /** Get a collection of  FacilityTypeAttr related entities. */
  public Collection getFacilityTypeAttrs() { return FacilityTypeAttrHelper.findByFacilityTypeId(facilityTypeId); }
  /** Get the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public FacilityTypeAttr getFacilityTypeAttr(String name) { return FacilityTypeAttrHelper.findByPrimaryKey(facilityTypeId, name); }
  /** Remove  FacilityTypeAttr related entities. */
  public void removeFacilityTypeAttrs() { FacilityTypeAttrHelper.removeByFacilityTypeId(facilityTypeId); }
  /** Remove the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityTypeAttr(String name) { FacilityTypeAttrHelper.removeByPrimaryKey(facilityTypeId, name); }

  /** Get a collection of  FacilityAttribute related entities. */
  public Collection getFacilityAttributes() { return FacilityAttributeHelper.findByFacilityId(facilityId); }
  /** Get the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public FacilityAttribute getFacilityAttribute(String name) { return FacilityAttributeHelper.findByPrimaryKey(facilityId, name); }
  /** Remove  FacilityAttribute related entities. */
  public void removeFacilityAttributes() { FacilityAttributeHelper.removeByFacilityId(facilityId); }
  /** Remove the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityAttribute(String name) { FacilityAttributeHelper.removeByPrimaryKey(facilityId, name); }

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByFacilityId(facilityId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByFacilityId(facilityId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get a collection of  Container related entities. */
  public Collection getContainers() { return ContainerHelper.findByFacilityId(facilityId); }
  /** Get the  Container keyed by member(s) of this class, and other passed parameters. */
  public Container getContainer(String containerId) { return ContainerHelper.findByPrimaryKey(containerId); }
  /** Remove  Container related entities. */
  public void removeContainers() { ContainerHelper.removeByFacilityId(facilityId); }
  /** Remove the  Container keyed by member(s) of this class, and other passed parameters. */
  public void removeContainer(String containerId) { ContainerHelper.removeByPrimaryKey(containerId); }

  /** Get a collection of  FacilityContactMechanism related entities. */
  public Collection getFacilityContactMechanisms() { return FacilityContactMechanismHelper.findByFacilityId(facilityId); }
  /** Get the  FacilityContactMechanism keyed by member(s) of this class, and other passed parameters. */
  public FacilityContactMechanism getFacilityContactMechanism(String contactMechanismId) { return FacilityContactMechanismHelper.findByPrimaryKey(facilityId, contactMechanismId); }
  /** Remove  FacilityContactMechanism related entities. */
  public void removeFacilityContactMechanisms() { FacilityContactMechanismHelper.removeByFacilityId(facilityId); }
  /** Remove the  FacilityContactMechanism keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityContactMechanism(String contactMechanismId) { FacilityContactMechanismHelper.removeByPrimaryKey(facilityId, contactMechanismId); }

  /** Get a collection of  PartyFacility related entities. */
  public Collection getPartyFacilitys() { return PartyFacilityHelper.findByFacilityId(facilityId); }
  /** Get the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public PartyFacility getPartyFacility(String partyId) { return PartyFacilityHelper.findByPrimaryKey(partyId, facilityId); }
  /** Remove  PartyFacility related entities. */
  public void removePartyFacilitys() { PartyFacilityHelper.removeByFacilityId(facilityId); }
  /** Remove the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public void removePartyFacility(String partyId) { PartyFacilityHelper.removeByPrimaryKey(partyId, facilityId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(facility!=null) return facility.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(facility!=null) return facility.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(facility!=null) return facility.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(facility!=null) return facility.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(facility!=null) facility.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
