
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.inventory.*;

/**
 * <p><b>Title:</b> Container Entity
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
public class ContainerBean implements EntityBean
{
  /** The variable for the CONTAINER_ID column of the CONTAINER table. */
  public String containerId;
  /** The variable for the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public String containerTypeId;
  /** The variable for the FACILITY_ID column of the CONTAINER table. */
  public String facilityId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ContainerBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key CONTAINER_ID column of the CONTAINER table. */
  public String getContainerId() { return containerId; }

  /** Get the value of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public String getContainerTypeId() { return containerTypeId; }
  /** Set the value of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public void setContainerTypeId(String containerTypeId)
  {
    this.containerTypeId = containerTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the FACILITY_ID column of the CONTAINER table. */
  public String getFacilityId() { return facilityId; }
  /** Set the value of the FACILITY_ID column of the CONTAINER table. */
  public void setFacilityId(String facilityId)
  {
    this.facilityId = facilityId;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ContainerBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Container valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getContainerTypeId() != null)
      {
        this.containerTypeId = valueObject.getContainerTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getFacilityId() != null)
      {
        this.facilityId = valueObject.getFacilityId();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ContainerBean object
   *@return    The ValueObject value
   */
  public Container getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ContainerValue((Container)this.entityContext.getEJBObject(), containerId, containerTypeId, facilityId);
    }
    else { return null; }
  }


  /** Get the  ContainerType entity corresponding to this entity. */
  public ContainerType getContainerType() { return ContainerTypeHelper.findByPrimaryKey(containerTypeId); }
  /** Remove the  ContainerType entity corresponding to this entity. */
  public void removeContainerType() { ContainerTypeHelper.removeByPrimaryKey(containerTypeId); }

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByContainerId(containerId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByContainerId(containerId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }


  /** Description of the Method
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String containerId, String containerTypeId, String facilityId) throws CreateException
  {
    this.containerId = containerId;
    this.containerTypeId = containerTypeId;
    this.facilityId = facilityId;
    return null;
  }

  /** Description of the Method
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String containerId) throws CreateException
  {
    return ejbCreate(containerId, null, null);
  }

  /** Description of the Method
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@param  containerTypeId                  Field of the CONTAINER_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String containerId, String containerTypeId, String facilityId) throws CreateException {}

  /** Description of the Method
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String containerId) throws CreateException
  {
    ejbPostCreate(containerId, null, null);
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
