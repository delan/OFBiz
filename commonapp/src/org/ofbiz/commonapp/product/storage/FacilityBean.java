
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

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
public class FacilityBean implements EntityBean
{
  /** The variable for the FACILITY_ID column of the FACILITY table. */
  public String facilityId;
  /** The variable for the FACILITY_TYPE_ID column of the FACILITY table. */
  public String facilityTypeId;
  /** The variable for the FACILITY_NAME column of the FACILITY table. */
  public String facilityName;
  /** The variable for the SQUARE_FOOTAGE column of the FACILITY table. */
  public Long squareFootage;
  /** The variable for the DESCRIPTION column of the FACILITY table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the FacilityBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key FACILITY_ID column of the FACILITY table. */
  public String getFacilityId() { return facilityId; }

  /** Get the value of the FACILITY_TYPE_ID column of the FACILITY table. */
  public String getFacilityTypeId() { return facilityTypeId; }
  /** Set the value of the FACILITY_TYPE_ID column of the FACILITY table. */
  public void setFacilityTypeId(String facilityTypeId)
  {
    this.facilityTypeId = facilityTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the FACILITY_NAME column of the FACILITY table. */
  public String getFacilityName() { return facilityName; }
  /** Set the value of the FACILITY_NAME column of the FACILITY table. */
  public void setFacilityName(String facilityName)
  {
    this.facilityName = facilityName;
    ejbIsModified = true;
  }

  /** Get the value of the SQUARE_FOOTAGE column of the FACILITY table. */
  public Long getSquareFootage() { return squareFootage; }
  /** Set the value of the SQUARE_FOOTAGE column of the FACILITY table. */
  public void setSquareFootage(Long squareFootage)
  {
    this.squareFootage = squareFootage;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the FACILITY table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the FACILITY table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the FacilityBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Facility valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getFacilityTypeId() != null)
      {
        this.facilityTypeId = valueObject.getFacilityTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getFacilityName() != null)
      {
        this.facilityName = valueObject.getFacilityName();
        ejbIsModified = true;
      }
      if(valueObject.getSquareFootage() != null)
      {
        this.squareFootage = valueObject.getSquareFootage();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the FacilityBean object
   *@return    The ValueObject value
   */
  public Facility getValueObject()
  {
    if(this.entityContext != null)
    {
      return new FacilityValue((Facility)this.entityContext.getEJBObject(), facilityId, facilityTypeId, facilityName, squareFootage, description);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  facilityName                  Field of the FACILITY_NAME column.
   *@param  squareFootage                  Field of the SQUARE_FOOTAGE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String facilityId, String facilityTypeId, String facilityName, Long squareFootage, String description) throws CreateException
  {
    this.facilityId = facilityId;
    this.facilityTypeId = facilityTypeId;
    this.facilityName = facilityName;
    this.squareFootage = squareFootage;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String facilityId) throws CreateException
  {
    return ejbCreate(facilityId, null, null, null, null);
  }

  /** Description of the Method
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  facilityName                  Field of the FACILITY_NAME column.
   *@param  squareFootage                  Field of the SQUARE_FOOTAGE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String facilityId, String facilityTypeId, String facilityName, Long squareFootage, String description) throws CreateException {}

  /** Description of the Method
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String facilityId) throws CreateException
  {
    ejbPostCreate(facilityId, null, null, null, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
