
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

public interface PartyFacility extends EJBObject
{
  /** Get the primary key of the PARTY_ID column of the PARTY_FACILITY table. */
  public String getPartyId() throws RemoteException;
  
  /** Get the primary key of the FACILITY_ID column of the PARTY_FACILITY table. */
  public String getFacilityId() throws RemoteException;
  
  /** Get the value of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public String getFacilityRoleTypeId() throws RemoteException;
  /** Set the value of the FACILITY_ROLE_TYPE_ID column of the PARTY_FACILITY table. */
  public void setFacilityRoleTypeId(String facilityRoleTypeId) throws RemoteException;
  

  /** Get the value object of this PartyFacility class. */
  public PartyFacility getValueObject() throws RemoteException;
  /** Set the values in the value object of this PartyFacility class. */
  public void setValueObject(PartyFacility partyFacilityValue) throws RemoteException;


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() throws RemoteException;
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() throws RemoteException;  

  /** Get the  FacilityRoleType entity corresponding to this entity. */
  public FacilityRoleType getFacilityRoleType() throws RemoteException;
  /** Remove the  FacilityRoleType entity corresponding to this entity. */
  public void removeFacilityRoleType() throws RemoteException;  

}
