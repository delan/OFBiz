
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ItemVarianceAcctgTransValue implements ItemVarianceAcctgTrans
{
  /** The variable of the ACCTG_TRANS_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  private String acctgTransId;
  /** The variable of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  private String inventoryItemId;
  /** The variable of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  private String physicalInventoryId;

  private ItemVarianceAcctgTrans itemVarianceAcctgTrans;

  public ItemVarianceAcctgTransValue()
  {
    this.acctgTransId = null;
    this.inventoryItemId = null;
    this.physicalInventoryId = null;

    this.itemVarianceAcctgTrans = null;
  }

  public ItemVarianceAcctgTransValue(ItemVarianceAcctgTrans itemVarianceAcctgTrans) throws RemoteException
  {
    if(itemVarianceAcctgTrans == null) return;
  
    this.acctgTransId = itemVarianceAcctgTrans.getAcctgTransId();
    this.inventoryItemId = itemVarianceAcctgTrans.getInventoryItemId();
    this.physicalInventoryId = itemVarianceAcctgTrans.getPhysicalInventoryId();

    this.itemVarianceAcctgTrans = itemVarianceAcctgTrans;
  }

  public ItemVarianceAcctgTransValue(ItemVarianceAcctgTrans itemVarianceAcctgTrans, String acctgTransId, String inventoryItemId, String physicalInventoryId)
  {
    if(itemVarianceAcctgTrans == null) return;
  
    this.acctgTransId = acctgTransId;
    this.inventoryItemId = inventoryItemId;
    this.physicalInventoryId = physicalInventoryId;

    this.itemVarianceAcctgTrans = itemVarianceAcctgTrans;
  }


  /** Get the primary key of the ACCTG_TRANS_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getAcctgTransId()  throws RemoteException { return acctgTransId; }

  /** Get the value of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getInventoryItemId() throws RemoteException { return inventoryItemId; }
  /** Set the value of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public void setInventoryItemId(String inventoryItemId) throws RemoteException
  {
    this.inventoryItemId = inventoryItemId;
    if(itemVarianceAcctgTrans!=null) itemVarianceAcctgTrans.setInventoryItemId(inventoryItemId);
  }

  /** Get the value of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getPhysicalInventoryId() throws RemoteException { return physicalInventoryId; }
  /** Set the value of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public void setPhysicalInventoryId(String physicalInventoryId) throws RemoteException
  {
    this.physicalInventoryId = physicalInventoryId;
    if(itemVarianceAcctgTrans!=null) itemVarianceAcctgTrans.setPhysicalInventoryId(physicalInventoryId);
  }

  /** Get the value object of the ItemVarianceAcctgTrans class. */
  public ItemVarianceAcctgTrans getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ItemVarianceAcctgTrans class. */
  public void setValueObject(ItemVarianceAcctgTrans valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(itemVarianceAcctgTrans!=null) itemVarianceAcctgTrans.setValueObject(valueObject);

    if(acctgTransId == null) acctgTransId = valueObject.getAcctgTransId();
    inventoryItemId = valueObject.getInventoryItemId();
    physicalInventoryId = valueObject.getPhysicalInventoryId();
  }


  /** Get the  PhysicalInventory entity corresponding to this entity. */
  public PhysicalInventory getPhysicalInventory() { return PhysicalInventoryHelper.findByPrimaryKey(physicalInventoryId); }
  /** Remove the  PhysicalInventory entity corresponding to this entity. */
  public void removePhysicalInventory() { PhysicalInventoryHelper.removeByPrimaryKey(physicalInventoryId); }

  /** Get the  InventoryItem entity corresponding to this entity. */
  public InventoryItem getInventoryItem() { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove the  InventoryItem entity corresponding to this entity. */
  public void removeInventoryItem() { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get the  InventoryItemVariance entity corresponding to this entity. */
  public InventoryItemVariance getInventoryItemVariance() { return InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId); }
  /** Remove the  InventoryItemVariance entity corresponding to this entity. */
  public void removeInventoryItemVariance() { InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(itemVarianceAcctgTrans!=null) return itemVarianceAcctgTrans.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(itemVarianceAcctgTrans!=null) return itemVarianceAcctgTrans.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(itemVarianceAcctgTrans!=null) return itemVarianceAcctgTrans.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(itemVarianceAcctgTrans!=null) return itemVarianceAcctgTrans.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(itemVarianceAcctgTrans!=null) itemVarianceAcctgTrans.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
