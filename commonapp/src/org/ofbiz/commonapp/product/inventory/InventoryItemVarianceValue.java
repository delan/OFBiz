
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class InventoryItemVarianceValue implements InventoryItemVariance
{
  /** The variable of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_VARIANCE table. */
  private String inventoryItemId;
  /** The variable of the PHYSICAL_INVENTORY_ID column of the INVENTORY_ITEM_VARIANCE table. */
  private String physicalInventoryId;
  /** The variable of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  private String varianceReasonId;
  /** The variable of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  private Double quantity;
  /** The variable of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  private String comment;

  private InventoryItemVariance inventoryItemVariance;

  public InventoryItemVarianceValue()
  {
    this.inventoryItemId = null;
    this.physicalInventoryId = null;
    this.varianceReasonId = null;
    this.quantity = null;
    this.comment = null;

    this.inventoryItemVariance = null;
  }

  public InventoryItemVarianceValue(InventoryItemVariance inventoryItemVariance) throws RemoteException
  {
    if(inventoryItemVariance == null) return;
  
    this.inventoryItemId = inventoryItemVariance.getInventoryItemId();
    this.physicalInventoryId = inventoryItemVariance.getPhysicalInventoryId();
    this.varianceReasonId = inventoryItemVariance.getVarianceReasonId();
    this.quantity = inventoryItemVariance.getQuantity();
    this.comment = inventoryItemVariance.getComment();

    this.inventoryItemVariance = inventoryItemVariance;
  }

  public InventoryItemVarianceValue(InventoryItemVariance inventoryItemVariance, String inventoryItemId, String physicalInventoryId, String varianceReasonId, Double quantity, String comment)
  {
    if(inventoryItemVariance == null) return;
  
    this.inventoryItemId = inventoryItemId;
    this.physicalInventoryId = physicalInventoryId;
    this.varianceReasonId = varianceReasonId;
    this.quantity = quantity;
    this.comment = comment;

    this.inventoryItemVariance = inventoryItemVariance;
  }


  /** Get the primary key of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getInventoryItemId()  throws RemoteException { return inventoryItemId; }

  /** Get the primary key of the PHYSICAL_INVENTORY_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getPhysicalInventoryId()  throws RemoteException { return physicalInventoryId; }

  /** Get the value of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getVarianceReasonId() throws RemoteException { return varianceReasonId; }
  /** Set the value of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public void setVarianceReasonId(String varianceReasonId) throws RemoteException
  {
    this.varianceReasonId = varianceReasonId;
    if(inventoryItemVariance!=null) inventoryItemVariance.setVarianceReasonId(varianceReasonId);
  }

  /** Get the value of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public Double getQuantity() throws RemoteException { return quantity; }
  /** Set the value of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public void setQuantity(Double quantity) throws RemoteException
  {
    this.quantity = quantity;
    if(inventoryItemVariance!=null) inventoryItemVariance.setQuantity(quantity);
  }

  /** Get the value of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public String getComment() throws RemoteException { return comment; }
  /** Set the value of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public void setComment(String comment) throws RemoteException
  {
    this.comment = comment;
    if(inventoryItemVariance!=null) inventoryItemVariance.setComment(comment);
  }

  /** Get the value object of the InventoryItemVariance class. */
  public InventoryItemVariance getValueObject() throws RemoteException { return this; }
  /** Set the value object of the InventoryItemVariance class. */
  public void setValueObject(InventoryItemVariance valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(inventoryItemVariance!=null) inventoryItemVariance.setValueObject(valueObject);

    if(inventoryItemId == null) inventoryItemId = valueObject.getInventoryItemId();
    if(physicalInventoryId == null) physicalInventoryId = valueObject.getPhysicalInventoryId();
    varianceReasonId = valueObject.getVarianceReasonId();
    quantity = valueObject.getQuantity();
    comment = valueObject.getComment();
  }


  /** Get the  VarianceReason entity corresponding to this entity. */
  public VarianceReason getVarianceReason() { return VarianceReasonHelper.findByPrimaryKey(varianceReasonId); }
  /** Remove the  VarianceReason entity corresponding to this entity. */
  public void removeVarianceReason() { VarianceReasonHelper.removeByPrimaryKey(varianceReasonId); }

  /** Get the  PhysicalInventory entity corresponding to this entity. */
  public PhysicalInventory getPhysicalInventory() { return PhysicalInventoryHelper.findByPrimaryKey(physicalInventoryId); }
  /** Remove the  PhysicalInventory entity corresponding to this entity. */
  public void removePhysicalInventory() { PhysicalInventoryHelper.removeByPrimaryKey(physicalInventoryId); }

  /** Get the  InventoryItem entity corresponding to this entity. */
  public InventoryItem getInventoryItem() { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove the  InventoryItem entity corresponding to this entity. */
  public void removeInventoryItem() { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get a collection of  ItemVarianceAcctgTrans related entities. */
  public Collection getItemVarianceAcctgTranss() { return ItemVarianceAcctgTransHelper.findByInventoryItemIdAndPhysicalInventoryId(inventoryItemId, physicalInventoryId); }
  /** Get the  ItemVarianceAcctgTrans keyed by member(s) of this class, and other passed parameters. */
  public ItemVarianceAcctgTrans getItemVarianceAcctgTrans(String acctgTransId) { return ItemVarianceAcctgTransHelper.findByPrimaryKey(acctgTransId); }
  /** Remove  ItemVarianceAcctgTrans related entities. */
  public void removeItemVarianceAcctgTranss() { ItemVarianceAcctgTransHelper.removeByInventoryItemIdAndPhysicalInventoryId(inventoryItemId, physicalInventoryId); }
  /** Remove the  ItemVarianceAcctgTrans keyed by member(s) of this class, and other passed parameters. */
  public void removeItemVarianceAcctgTrans(String acctgTransId) { ItemVarianceAcctgTransHelper.removeByPrimaryKey(acctgTransId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(inventoryItemVariance!=null) return inventoryItemVariance.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(inventoryItemVariance!=null) return inventoryItemVariance.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(inventoryItemVariance!=null) return inventoryItemVariance.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(inventoryItemVariance!=null) return inventoryItemVariance.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(inventoryItemVariance!=null) inventoryItemVariance.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
