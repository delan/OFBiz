
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

public interface Container extends EJBObject
{
  /** Get the primary key of the CONTAINER_ID column of the CONTAINER table. */
  public String getContainerId() throws RemoteException;
  
  /** Get the value of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public String getContainerTypeId() throws RemoteException;
  /** Set the value of the CONTAINER_TYPE_ID column of the CONTAINER table. */
  public void setContainerTypeId(String containerTypeId) throws RemoteException;
  
  /** Get the value of the FACILITY_ID column of the CONTAINER table. */
  public String getFacilityId() throws RemoteException;
  /** Set the value of the FACILITY_ID column of the CONTAINER table. */
  public void setFacilityId(String facilityId) throws RemoteException;
  

  /** Get the value object of this Container class. */
  public Container getValueObject() throws RemoteException;
  /** Set the values in the value object of this Container class. */
  public void setValueObject(Container containerValue) throws RemoteException;


  /** Get the  ContainerType entity corresponding to this entity. */
  public ContainerType getContainerType() throws RemoteException;
  /** Remove the  ContainerType entity corresponding to this entity. */
  public void removeContainerType() throws RemoteException;  

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() throws RemoteException;
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) throws RemoteException;
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() throws RemoteException;
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) throws RemoteException;

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() throws RemoteException;
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() throws RemoteException;  

}
