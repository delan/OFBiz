
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class ContainerValue implements Container
{
  /** The variable of the CONTAINER_ID column of the CONTAINER table. */
  private String containerId;
  /** The variable of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  private String containerTypeId;
  /** The variable of the FACILITY_ID column of the CONTAINER table. */
  private String facilityId;

  private Container container;

  public ContainerValue()
  {
    this.containerId = null;
    this.containerTypeId = null;
    this.facilityId = null;

    this.container = null;
  }

  public ContainerValue(Container container) throws RemoteException
  {
    if(container == null) return;
  
    this.containerId = container.getContainerId();
    this.containerTypeId = container.getContainerTypeId();
    this.facilityId = container.getFacilityId();

    this.container = container;
  }

  public ContainerValue(Container container, String containerId, String containerTypeId, String facilityId)
  {
    if(container == null) return;
  
    this.containerId = containerId;
    this.containerTypeId = containerTypeId;
    this.facilityId = facilityId;

    this.container = container;
  }


  /** Get the primary key of the CONTAINER_ID column of the CONTAINER table. */
  public String getContainerId()  throws RemoteException { return containerId; }

  /** Get the value of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public String getContainerTypeId() throws RemoteException { return containerTypeId; }
  /** Set the value of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public void setContainerTypeId(String containerTypeId) throws RemoteException
  {
    this.containerTypeId = containerTypeId;
    if(container!=null) container.setContainerTypeId(containerTypeId);
  }

  /** Get the value of the FACILITY_ID column of the CONTAINER table. */
  public String getFacilityId() throws RemoteException { return facilityId; }
  /** Set the value of the FACILITY_ID column of the CONTAINER table. */
  public void setFacilityId(String facilityId) throws RemoteException
  {
    this.facilityId = facilityId;
    if(container!=null) container.setFacilityId(facilityId);
  }

  /** Get the value object of the Container class. */
  public Container getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Container class. */
  public void setValueObject(Container valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(container!=null) container.setValueObject(valueObject);

    if(containerId == null) containerId = valueObject.getContainerId();
    containerTypeId = valueObject.getContainerTypeId();
    facilityId = valueObject.getFacilityId();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(container!=null) return container.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(container!=null) return container.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(container!=null) return container.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(container!=null) return container.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(container!=null) container.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
