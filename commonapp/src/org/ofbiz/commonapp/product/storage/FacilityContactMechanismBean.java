
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


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
public class FacilityContactMechanismBean implements EntityBean
{
  /** The variable for the FACILITY_ID column of the FACILITY_CONTACT_MECHANISM table. */
  public String facilityId;
  /** The variable for the CONTACT_MECHANISM_ID column of the FACILITY_CONTACT_MECHANISM table. */
  public String contactMechanismId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the FacilityContactMechanismBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key FACILITY_ID column of the FACILITY_CONTACT_MECHANISM table. */
  public String getFacilityId() { return facilityId; }

  /** Get the primary key CONTACT_MECHANISM_ID column of the FACILITY_CONTACT_MECHANISM table. */
  public String getContactMechanismId() { return contactMechanismId; }

  /** Sets the values from ValueObject attribute of the FacilityContactMechanismBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(FacilityContactMechanism valueObject)
  {
  }

  /** Gets the ValueObject attribute of the FacilityContactMechanismBean object
   *@return    The ValueObject value
   */
  public FacilityContactMechanism getValueObject()
  {
    if(this.entityContext != null)
    {
      return new FacilityContactMechanismValue((FacilityContactMechanism)this.entityContext.getEJBObject(), facilityId, contactMechanismId);
    }
    else { return null; }
  }


  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }


  /** Description of the Method
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.storage.FacilityContactMechanismPK ejbCreate(String facilityId, String contactMechanismId) throws CreateException
  {
    this.facilityId = facilityId;
    this.contactMechanismId = contactMechanismId;
    return null;
  }

  /** Description of the Method
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String facilityId, String contactMechanismId) throws CreateException {}

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
