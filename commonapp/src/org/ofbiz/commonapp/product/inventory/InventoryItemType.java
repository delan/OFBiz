
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Inventory Item Type Entity
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
 *@created    Fri Jul 27 01:18:30 MDT 2001
 *@version    1.0
 */

public interface InventoryItemType extends EJBObject
{
  /** Get the primary key of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  public String getInventoryItemTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the INVENTORY_ITEM_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the INVENTORY_ITEM_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the INVENTORY_ITEM_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the INVENTORY_ITEM_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this InventoryItemType class. */
  public InventoryItemType getValueObject() throws RemoteException;
  /** Set the values in the value object of this InventoryItemType class. */
  public void setValueObject(InventoryItemType inventoryItemTypeValue) throws RemoteException;


  /** Get the Parent InventoryItemType entity corresponding to this entity. */
  public InventoryItemType getParentInventoryItemType() throws RemoteException;
  /** Remove the Parent InventoryItemType entity corresponding to this entity. */
  public void removeParentInventoryItemType() throws RemoteException;  

  /** Get a collection of Child InventoryItemType related entities. */
  public Collection getChildInventoryItemTypes() throws RemoteException;
  /** Get the Child InventoryItemType keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemType getChildInventoryItemType(String inventoryItemTypeId) throws RemoteException;
  /** Remove Child InventoryItemType related entities. */
  public void removeChildInventoryItemTypes() throws RemoteException;
  /** Remove the Child InventoryItemType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildInventoryItemType(String inventoryItemTypeId) throws RemoteException;

  /** Get a collection of  InventoryItemTypeAttr related entities. */
  public Collection getInventoryItemTypeAttrs() throws RemoteException;
  /** Get the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemTypeAttr getInventoryItemTypeAttr(String name) throws RemoteException;
  /** Remove  InventoryItemTypeAttr related entities. */
  public void removeInventoryItemTypeAttrs() throws RemoteException;
  /** Remove the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemTypeAttr(String name) throws RemoteException;

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() throws RemoteException;
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) throws RemoteException;
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() throws RemoteException;
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) throws RemoteException;

}
