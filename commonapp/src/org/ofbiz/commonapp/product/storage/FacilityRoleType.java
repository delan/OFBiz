
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Facility Role Type Entity
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

public interface FacilityRoleType extends EJBObject
{
  /** Get the primary key of the FACILITY_ROLE_TYPE_ID column of the FACILITY_ROLE_TYPE table. */
  public String getFacilityRoleTypeId() throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the FACILITY_ROLE_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the FACILITY_ROLE_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this FacilityRoleType class. */
  public FacilityRoleType getValueObject() throws RemoteException;
  /** Set the values in the value object of this FacilityRoleType class. */
  public void setValueObject(FacilityRoleType facilityRoleTypeValue) throws RemoteException;


  /** Get a collection of  PartyFacility related entities. */
  public Collection getPartyFacilitys() throws RemoteException;
  /** Get the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public PartyFacility getPartyFacility(String partyId, String facilityId) throws RemoteException;
  /** Remove  PartyFacility related entities. */
  public void removePartyFacilitys() throws RemoteException;
  /** Remove the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public void removePartyFacility(String partyId, String facilityId) throws RemoteException;

}
