
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
public class InventoryItemVarianceBean implements EntityBean
{
  /** The variable for the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String inventoryItemId;
  /** The variable for the PHYSICAL_INVENTORY_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String physicalInventoryId;
  /** The variable for the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String varianceReasonId;
  /** The variable for the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public Double quantity;
  /** The variable for the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public String comment;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the InventoryItemVarianceBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key INVENTORY_ITEM_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getInventoryItemId() { return inventoryItemId; }

  /** Get the primary key PHYSICAL_INVENTORY_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getPhysicalInventoryId() { return physicalInventoryId; }

  /** Get the value of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public String getVarianceReasonId() { return varianceReasonId; }
  /** Set the value of the VARIANCE_REASON_ID column of the INVENTORY_ITEM_VARIANCE table. */
  public void setVarianceReasonId(String varianceReasonId)
  {
    this.varianceReasonId = varianceReasonId;
    ejbIsModified = true;
  }

  /** Get the value of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public Double getQuantity() { return quantity; }
  /** Set the value of the QUANTITY column of the INVENTORY_ITEM_VARIANCE table. */
  public void setQuantity(Double quantity)
  {
    this.quantity = quantity;
    ejbIsModified = true;
  }

  /** Get the value of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public String getComment() { return comment; }
  /** Set the value of the COMMENT column of the INVENTORY_ITEM_VARIANCE table. */
  public void setComment(String comment)
  {
    this.comment = comment;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the InventoryItemVarianceBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(InventoryItemVariance valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getVarianceReasonId() != null)
      {
        this.varianceReasonId = valueObject.getVarianceReasonId();
        ejbIsModified = true;
      }
      if(valueObject.getQuantity() != null)
      {
        this.quantity = valueObject.getQuantity();
        ejbIsModified = true;
      }
      if(valueObject.getComment() != null)
      {
        this.comment = valueObject.getComment();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the InventoryItemVarianceBean object
   *@return    The ValueObject value
   */
  public InventoryItemVariance getValueObject()
  {
    if(this.entityContext != null)
    {
      return new InventoryItemVarianceValue((InventoryItemVariance)this.entityContext.getEJBObject(), inventoryItemId, physicalInventoryId, varianceReasonId, quantity, comment);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  comment                  Field of the COMMENT column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.inventory.InventoryItemVariancePK ejbCreate(String inventoryItemId, String physicalInventoryId, String varianceReasonId, Double quantity, String comment) throws CreateException
  {
    this.inventoryItemId = inventoryItemId;
    this.physicalInventoryId = physicalInventoryId;
    this.varianceReasonId = varianceReasonId;
    this.quantity = quantity;
    this.comment = comment;
    return null;
  }

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.inventory.InventoryItemVariancePK ejbCreate(String inventoryItemId, String physicalInventoryId) throws CreateException
  {
    return ejbCreate(inventoryItemId, physicalInventoryId, null, null, null);
  }

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  comment                  Field of the COMMENT column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemId, String physicalInventoryId, String varianceReasonId, Double quantity, String comment) throws CreateException {}

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemId, String physicalInventoryId) throws CreateException
  {
    ejbPostCreate(inventoryItemId, physicalInventoryId, null, null, null);
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
