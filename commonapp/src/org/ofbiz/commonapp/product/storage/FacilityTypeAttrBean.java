
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Facility Type Attribute Entity
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
public class FacilityTypeAttrBean implements EntityBean
{
  /** The variable for the FACILITY_TYPE_ID column of the FACILITY_TYPE_ATTR table. */
  public String facilityTypeId;
  /** The variable for the NAME column of the FACILITY_TYPE_ATTR table. */
  public String name;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the FacilityTypeAttrBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key FACILITY_TYPE_ID column of the FACILITY_TYPE_ATTR table. */
  public String getFacilityTypeId() { return facilityTypeId; }

  /** Get the primary key NAME column of the FACILITY_TYPE_ATTR table. */
  public String getName() { return name; }

  /** Sets the values from ValueObject attribute of the FacilityTypeAttrBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(FacilityTypeAttr valueObject)
  {
  }

  /** Gets the ValueObject attribute of the FacilityTypeAttrBean object
   *@return    The ValueObject value
   */
  public FacilityTypeAttr getValueObject()
  {
    if(this.entityContext != null)
    {
      return new FacilityTypeAttrValue((FacilityTypeAttr)this.entityContext.getEJBObject(), facilityTypeId, name);
    }
    else { return null; }
  }


  /** Get the  FacilityType entity corresponding to this entity. */
  public FacilityType getFacilityType() { return FacilityTypeHelper.findByPrimaryKey(facilityTypeId); }
  /** Remove the  FacilityType entity corresponding to this entity. */
  public void removeFacilityType() { FacilityTypeHelper.removeByPrimaryKey(facilityTypeId); }

  /** Get a collection of  FacilityAttribute related entities. */
  public Collection getFacilityAttributes() { return FacilityAttributeHelper.findByName(name); }
  /** Get the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public FacilityAttribute getFacilityAttribute(String facilityId) { return FacilityAttributeHelper.findByPrimaryKey(facilityId, name); }
  /** Remove  FacilityAttribute related entities. */
  public void removeFacilityAttributes() { FacilityAttributeHelper.removeByName(name); }
  /** Remove the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityAttribute(String facilityId) { FacilityAttributeHelper.removeByPrimaryKey(facilityId, name); }

  /** Get a collection of  Facility related entities. */
  public Collection getFacilitys() { return FacilityHelper.findByFacilityTypeId(facilityTypeId); }
  /** Get the  Facility keyed by member(s) of this class, and other passed parameters. */
  public Facility getFacility(String facilityId) { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove  Facility related entities. */
  public void removeFacilitys() { FacilityHelper.removeByFacilityTypeId(facilityTypeId); }
  /** Remove the  Facility keyed by member(s) of this class, and other passed parameters. */
  public void removeFacility(String facilityId) { FacilityHelper.removeByPrimaryKey(facilityId); }


  /** Description of the Method
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.storage.FacilityTypeAttrPK ejbCreate(String facilityTypeId, String name) throws CreateException
  {
    this.facilityTypeId = facilityTypeId;
    this.name = name;
    return null;
  }

  /** Description of the Method
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String facilityTypeId, String name) throws CreateException {}

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
