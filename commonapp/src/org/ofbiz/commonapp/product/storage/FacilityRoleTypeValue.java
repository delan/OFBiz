
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class FacilityRoleTypeValue implements FacilityRoleType
{
  /** The variable of the FACILITY_ROLE_TYPE_ID column of the FACILITY_ROLE_TYPE table. */
  private String facilityRoleTypeId;
  /** The variable of the DESCRIPTION column of the FACILITY_ROLE_TYPE table. */
  private String description;

  private FacilityRoleType facilityRoleType;

  public FacilityRoleTypeValue()
  {
    this.facilityRoleTypeId = null;
    this.description = null;

    this.facilityRoleType = null;
  }

  public FacilityRoleTypeValue(FacilityRoleType facilityRoleType) throws RemoteException
  {
    if(facilityRoleType == null) return;
  
    this.facilityRoleTypeId = facilityRoleType.getFacilityRoleTypeId();
    this.description = facilityRoleType.getDescription();

    this.facilityRoleType = facilityRoleType;
  }

  public FacilityRoleTypeValue(FacilityRoleType facilityRoleType, String facilityRoleTypeId, String description)
  {
    if(facilityRoleType == null) return;
  
    this.facilityRoleTypeId = facilityRoleTypeId;
    this.description = description;

    this.facilityRoleType = facilityRoleType;
  }


  /** Get the primary key of the FACILITY_ROLE_TYPE_ID column of the FACILITY_ROLE_TYPE table. */
  public String getFacilityRoleTypeId()  throws RemoteException { return facilityRoleTypeId; }

  /** Get the value of the DESCRIPTION column of the FACILITY_ROLE_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the FACILITY_ROLE_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(facilityRoleType!=null) facilityRoleType.setDescription(description);
  }

  /** Get the value object of the FacilityRoleType class. */
  public FacilityRoleType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the FacilityRoleType class. */
  public void setValueObject(FacilityRoleType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(facilityRoleType!=null) facilityRoleType.setValueObject(valueObject);

    if(facilityRoleTypeId == null) facilityRoleTypeId = valueObject.getFacilityRoleTypeId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  PartyFacility related entities. */
  public Collection getPartyFacilitys() { return PartyFacilityHelper.findByFacilityRoleTypeId(facilityRoleTypeId); }
  /** Get the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public PartyFacility getPartyFacility(String partyId, String facilityId) { return PartyFacilityHelper.findByPrimaryKey(partyId, facilityId); }
  /** Remove  PartyFacility related entities. */
  public void removePartyFacilitys() { PartyFacilityHelper.removeByFacilityRoleTypeId(facilityRoleTypeId); }
  /** Remove the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public void removePartyFacility(String partyId, String facilityId) { PartyFacilityHelper.removeByPrimaryKey(partyId, facilityId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(facilityRoleType!=null) return facilityRoleType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(facilityRoleType!=null) return facilityRoleType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(facilityRoleType!=null) return facilityRoleType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(facilityRoleType!=null) return facilityRoleType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(facilityRoleType!=null) facilityRoleType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
