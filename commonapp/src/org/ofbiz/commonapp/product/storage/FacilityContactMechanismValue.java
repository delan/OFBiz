
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Facility Contact Mechanism Entity
 * <p><b>Description:</b> Data Type Of: Contact Mechanism
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
public class FacilityContactMechanismValue implements FacilityContactMechanism
{
  /** The variable of the FACILITY_ID column of the FACILITY_CONTACT_MECHANISM table. */
  private String facilityId;
  /** The variable of the CONTACT_MECHANISM_ID column of the FACILITY_CONTACT_MECHANISM table. */
  private String contactMechanismId;

  private FacilityContactMechanism facilityContactMechanism;

  public FacilityContactMechanismValue()
  {
    this.facilityId = null;
    this.contactMechanismId = null;

    this.facilityContactMechanism = null;
  }

  public FacilityContactMechanismValue(FacilityContactMechanism facilityContactMechanism) throws RemoteException
  {
    if(facilityContactMechanism == null) return;
  
    this.facilityId = facilityContactMechanism.getFacilityId();
    this.contactMechanismId = facilityContactMechanism.getContactMechanismId();

    this.facilityContactMechanism = facilityContactMechanism;
  }

  public FacilityContactMechanismValue(FacilityContactMechanism facilityContactMechanism, String facilityId, String contactMechanismId)
  {
    if(facilityContactMechanism == null) return;
  
    this.facilityId = facilityId;
    this.contactMechanismId = contactMechanismId;

    this.facilityContactMechanism = facilityContactMechanism;
  }


  /** Get the primary key of the FACILITY_ID column of the FACILITY_CONTACT_MECHANISM table. */
  public String getFacilityId()  throws RemoteException { return facilityId; }

  /** Get the primary key of the CONTACT_MECHANISM_ID column of the FACILITY_CONTACT_MECHANISM table. */
  public String getContactMechanismId()  throws RemoteException { return contactMechanismId; }

  /** Get the value object of the FacilityContactMechanism class. */
  public FacilityContactMechanism getValueObject() throws RemoteException { return this; }
  /** Set the value object of the FacilityContactMechanism class. */
  public void setValueObject(FacilityContactMechanism valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(facilityContactMechanism!=null) facilityContactMechanism.setValueObject(valueObject);

    if(facilityId == null) facilityId = valueObject.getFacilityId();
    if(contactMechanismId == null) contactMechanismId = valueObject.getContactMechanismId();
  }


  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(facilityContactMechanism!=null) return facilityContactMechanism.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(facilityContactMechanism!=null) return facilityContactMechanism.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(facilityContactMechanism!=null) return facilityContactMechanism.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(facilityContactMechanism!=null) return facilityContactMechanism.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(facilityContactMechanism!=null) facilityContactMechanism.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
