
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Item Variance Accounting Transaction Entity
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

public interface ItemVarianceAcctgTrans extends EJBObject
{
  /** Get the primary key of the ACCTG_TRANS_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getAcctgTransId() throws RemoteException;
  
  /** Get the value of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getInventoryItemId() throws RemoteException;
  /** Set the value of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public void setInventoryItemId(String inventoryItemId) throws RemoteException;
  
  /** Get the value of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getPhysicalInventoryId() throws RemoteException;
  /** Set the value of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public void setPhysicalInventoryId(String physicalInventoryId) throws RemoteException;
  

  /** Get the value object of this ItemVarianceAcctgTrans class. */
  public ItemVarianceAcctgTrans getValueObject() throws RemoteException;
  /** Set the values in the value object of this ItemVarianceAcctgTrans class. */
  public void setValueObject(ItemVarianceAcctgTrans itemVarianceAcctgTransValue) throws RemoteException;


  /** Get the  PhysicalInventory entity corresponding to this entity. */
  public PhysicalInventory getPhysicalInventory() throws RemoteException;
  /** Remove the  PhysicalInventory entity corresponding to this entity. */
  public void removePhysicalInventory() throws RemoteException;  

  /** Get the  InventoryItem entity corresponding to this entity. */
  public InventoryItem getInventoryItem() throws RemoteException;
  /** Remove the  InventoryItem entity corresponding to this entity. */
  public void removeInventoryItem() throws RemoteException;  

  /** Get the  InventoryItemVariance entity corresponding to this entity. */
  public InventoryItemVariance getInventoryItemVariance() throws RemoteException;
  /** Remove the  InventoryItemVariance entity corresponding to this entity. */
  public void removeInventoryItemVariance() throws RemoteException;  

}
