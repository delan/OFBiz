
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class PartyFacilityValue implements PartyFacility
{
  /** The variable of the PARTY_ID column of the PARTY_FACILITY table. */
  private String partyId;
  /** The variable of the FACILITY_ID column of the PARTY_FACILITY table. */
  private String facilityId;
  /** The variable of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  private String facilityRoleTypeId;

  private PartyFacility partyFacility;

  public PartyFacilityValue()
  {
    this.partyId = null;
    this.facilityId = null;
    this.facilityRoleTypeId = null;

    this.partyFacility = null;
  }

  public PartyFacilityValue(PartyFacility partyFacility) throws RemoteException
  {
    if(partyFacility == null) return;
  
    this.partyId = partyFacility.getPartyId();
    this.facilityId = partyFacility.getFacilityId();
    this.facilityRoleTypeId = partyFacility.getFacilityRoleTypeId();

    this.partyFacility = partyFacility;
  }

  public PartyFacilityValue(PartyFacility partyFacility, String partyId, String facilityId, String facilityRoleTypeId)
  {
    if(partyFacility == null) return;
  
    this.partyId = partyId;
    this.facilityId = facilityId;
    this.facilityRoleTypeId = facilityRoleTypeId;

    this.partyFacility = partyFacility;
  }


  /** Get the primary key of the PARTY_ID column of the PARTY_FACILITY table. */
  public String getPartyId()  throws RemoteException { return partyId; }

  /** Get the primary key of the FACILITY_ID column of the PARTY_FACILITY table. */
  public String getFacilityId()  throws RemoteException { return facilityId; }

  /** Get the value of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public String getFacilityRoleTypeId() throws RemoteException { return facilityRoleTypeId; }
  /** Set the value of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public void setFacilityRoleTypeId(String facilityRoleTypeId) throws RemoteException
  {
    this.facilityRoleTypeId = facilityRoleTypeId;
    if(partyFacility!=null) partyFacility.setFacilityRoleTypeId(facilityRoleTypeId);
  }

  /** Get the value object of the PartyFacility class. */
  public PartyFacility getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyFacility class. */
  public void setValueObject(PartyFacility valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyFacility!=null) partyFacility.setValueObject(valueObject);

    if(partyId == null) partyId = valueObject.getPartyId();
    if(facilityId == null) facilityId = valueObject.getFacilityId();
    facilityRoleTypeId = valueObject.getFacilityRoleTypeId();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyFacility!=null) return partyFacility.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyFacility!=null) return partyFacility.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyFacility!=null) return partyFacility.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyFacility!=null) return partyFacility.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyFacility!=null) partyFacility.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
