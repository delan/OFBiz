
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Facility Type Entity
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
public class FacilityTypeBean implements EntityBean
{
  /** The variable for the FACILITY_TYPE_ID column of the FACILITY_TYPE table. */
  public String facilityTypeId;
  /** The variable for the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public String parentTypeId;
  /** The variable for the HAS_TABLE column of the FACILITY_TYPE table. */
  public String hasTable;
  /** The variable for the DESCRIPTION column of the FACILITY_TYPE table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the FacilityTypeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key FACILITY_TYPE_ID column of the FACILITY_TYPE table. */
  public String getFacilityTypeId() { return facilityTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public String getParentTypeId() { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the FACILITY_TYPE table. */
  public void setParentTypeId(String parentTypeId)
  {
    this.parentTypeId = parentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the HAS_TABLE column of the FACILITY_TYPE table. */
  public String getHasTable() { return hasTable; }
  /** Set the value of the HAS_TABLE column of the FACILITY_TYPE table. */
  public void setHasTable(String hasTable)
  {
    this.hasTable = hasTable;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the FACILITY_TYPE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the FACILITY_TYPE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the FacilityTypeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(FacilityType valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getParentTypeId() != null)
      {
        this.parentTypeId = valueObject.getParentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getHasTable() != null)
      {
        this.hasTable = valueObject.getHasTable();
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

  /** Gets the ValueObject attribute of the FacilityTypeBean object
   *@return    The ValueObject value
   */
  public FacilityType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new FacilityTypeValue((FacilityType)this.entityContext.getEJBObject(), facilityTypeId, parentTypeId, hasTable, description);
    }
    else { return null; }
  }


  /** Get the Parent FacilityType entity corresponding to this entity. */
  public FacilityType getParentFacilityType() { return FacilityTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent FacilityType entity corresponding to this entity. */
  public void removeParentFacilityType() { FacilityTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child FacilityType related entities. */
  public Collection getChildFacilityTypes() { return FacilityTypeHelper.findByParentTypeId(facilityTypeId); }
  /** Get the Child FacilityType keyed by member(s) of this class, and other passed parameters. */
  public FacilityType getChildFacilityType(String facilityTypeId) { return FacilityTypeHelper.findByPrimaryKey(facilityTypeId); }
  /** Remove Child FacilityType related entities. */
  public void removeChildFacilityTypes() { FacilityTypeHelper.removeByParentTypeId(facilityTypeId); }
  /** Remove the Child FacilityType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildFacilityType(String facilityTypeId) { FacilityTypeHelper.removeByPrimaryKey(facilityTypeId); }

  /** Get a collection of  FacilityTypeAttr related entities. */
  public Collection getFacilityTypeAttrs() { return FacilityTypeAttrHelper.findByFacilityTypeId(facilityTypeId); }
  /** Get the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public FacilityTypeAttr getFacilityTypeAttr(String name) { return FacilityTypeAttrHelper.findByPrimaryKey(facilityTypeId, name); }
  /** Remove  FacilityTypeAttr related entities. */
  public void removeFacilityTypeAttrs() { FacilityTypeAttrHelper.removeByFacilityTypeId(facilityTypeId); }
  /** Remove the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityTypeAttr(String name) { FacilityTypeAttrHelper.removeByPrimaryKey(facilityTypeId, name); }

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
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String facilityTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {
    this.facilityTypeId = facilityTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String facilityTypeId) throws CreateException
  {
    return ejbCreate(facilityTypeId, null, null, null);
  }

  /** Description of the Method
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String facilityTypeId, String parentTypeId, String hasTable, String description) throws CreateException {}

  /** Description of the Method
   *@param  facilityTypeId                  Field of the FACILITY_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String facilityTypeId) throws CreateException
  {
    ejbPostCreate(facilityTypeId, null, null, null);
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
