
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Inventory Item Variance Entity
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

public interface InventoryItemVariance extends EJBObject
{
  /** Get the primary key of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getInventoryItemId() throws RemoteException;
  
  /** Get the primary key of the PHYSICAL_INVENTORY_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getPhysicalInventoryId() throws RemoteException;
  
  /** Get the value of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getVarianceReasonId() throws RemoteException;
  /** Set the value of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public void setVarianceReasonId(String varianceReasonId) throws RemoteException;
  
  /** Get the value of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public Double getQuantity() throws RemoteException;
  /** Set the value of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public void setQuantity(Double quantity) throws RemoteException;
  
  /** Get the value of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public String getComment() throws RemoteException;
  /** Set the value of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public void setComment(String comment) throws RemoteException;
  

  /** Get the value object of this InventoryItemVariance class. */
  public InventoryItemVariance getValueObject() throws RemoteException;
  /** Set the values in the value object of this InventoryItemVariance class. */
  public void setValueObject(InventoryItemVariance inventoryItemVarianceValue) throws RemoteException;


  /** Get the  VarianceReason entity corresponding to this entity. */
  public VarianceReason getVarianceReason() throws RemoteException;
  /** Remove the  VarianceReason entity corresponding to this entity. */
  public void removeVarianceReason() throws RemoteException;  

  /** Get the  PhysicalInventory entity corresponding to this entity. */
  public PhysicalInventory getPhysicalInventory() throws RemoteException;
  /** Remove the  PhysicalInventory entity corresponding to this entity. */
  public void removePhysicalInventory() throws RemoteException;  

  /** Get the  InventoryItem entity corresponding to this entity. */
  public InventoryItem getInventoryItem() throws RemoteException;
  /** Remove the  InventoryItem entity corresponding to this entity. */
  public void removeInventoryItem() throws RemoteException;  

  /** Get a collection of  ItemVarianceAcctgTrans related entities. */
  public Collection getItemVarianceAcctgTranss() throws RemoteException;
  /** Get the  ItemVarianceAcctgTrans keyed by member(s) of this class, and other passed parameters. */
  public ItemVarianceAcctgTrans getItemVarianceAcctgTrans(String acctgTransId) throws RemoteException;
  /** Remove  ItemVarianceAcctgTrans related entities. */
  public void removeItemVarianceAcctgTranss() throws RemoteException;
  /** Remove the  ItemVarianceAcctgTrans keyed by member(s) of this class, and other passed parameters. */
  public void removeItemVarianceAcctgTrans(String acctgTransId) throws RemoteException;

}
