
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.party.party.*;

/**
 * <p><b>Title:</b> Party Facility Entity
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */
public class PartyFacilityBean implements EntityBean
{
  /** The variable for the PARTY_ID column of the PARTY_FACILITY table. */
  public String partyId;
  /** The variable for the FACILITY_ID column of the PARTY_FACILITY table. */
  public String facilityId;
  /** The variable for the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public String facilityRoleTypeId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the PartyFacilityBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PARTY_ID column of the PARTY_FACILITY table. */
  public String getPartyId() { return partyId; }

  /** Get the primary key FACILITY_ID column of the PARTY_FACILITY table. */
  public String getFacilityId() { return facilityId; }

  /** Get the value of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public String getFacilityRoleTypeId() { return facilityRoleTypeId; }
  /** Set the value of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public void setFacilityRoleTypeId(String facilityRoleTypeId)
  {
    this.facilityRoleTypeId = facilityRoleTypeId;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the PartyFacilityBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(PartyFacility valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getFacilityRoleTypeId() != null)
      {
        this.facilityRoleTypeId = valueObject.getFacilityRoleTypeId();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the PartyFacilityBean object
   *@return    The ValueObject value
   */
  public PartyFacility getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PartyFacilityValue((PartyFacility)this.entityContext.getEJBObject(), partyId, facilityId, facilityRoleTypeId);
    }
    else { return null; }
  }


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }

  /** Get the  FacilityRoleType entity corresponding to this entity. */
  public FacilityRoleType getFacilityRoleType() { return FacilityRoleTypeHelper.findByPrimaryKey(facilityRoleTypeId); }
  /** Remove the  FacilityRoleType entity corresponding to this entity. */
  public void removeFacilityRoleType() { FacilityRoleTypeHelper.removeByPrimaryKey(facilityRoleTypeId); }


  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.storage.PartyFacilityPK ejbCreate(String partyId, String facilityId, String facilityRoleTypeId) throws CreateException
  {
    this.partyId = partyId;
    this.facilityId = facilityId;
    this.facilityRoleTypeId = facilityRoleTypeId;
    return null;
  }

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.storage.PartyFacilityPK ejbCreate(String partyId, String facilityId) throws CreateException
  {
    return ejbCreate(partyId, facilityId, null);
  }

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  facilityRoleTypeId                  Field of the FACILITY_ROLE_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String facilityId, String facilityRoleTypeId) throws CreateException {}

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String facilityId) throws CreateException
  {
    ejbPostCreate(partyId, facilityId, null);
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
