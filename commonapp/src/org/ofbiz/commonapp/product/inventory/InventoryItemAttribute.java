
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Inventory Item Attribute Entity
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
 *@created    Fri Jul 27 01:18:31 MDT 2001
 *@version    1.0
 */

public interface InventoryItemAttribute extends EJBObject
{
  /** Get the primary key of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getInventoryItemId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getName() throws RemoteException;
  
  /** Get the value of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getValue() throws RemoteException;
  /** Set the value of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException;
  

  /** Get the value object of this InventoryItemAttribute class. */
  public InventoryItemAttribute getValueObject() throws RemoteException;
  /** Set the values in the value object of this InventoryItemAttribute class. */
  public void setValueObject(InventoryItemAttribute inventoryItemAttributeValue) throws RemoteException;


  /** Get the  InventoryItem entity corresponding to this entity. */
  public InventoryItem getInventoryItem() throws RemoteException;
  /** Remove the  InventoryItem entity corresponding to this entity. */
  public void removeInventoryItem() throws RemoteException;  

  /** Get a collection of  InventoryItemTypeAttr related entities. */
  public Collection getInventoryItemTypeAttrs() throws RemoteException;
  /** Get the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemTypeAttr getInventoryItemTypeAttr(String inventoryItemTypeId) throws RemoteException;
  /** Remove  InventoryItemTypeAttr related entities. */
  public void removeInventoryItemTypeAttrs() throws RemoteException;
  /** Remove the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemTypeAttr(String inventoryItemTypeId) throws RemoteException;

}
