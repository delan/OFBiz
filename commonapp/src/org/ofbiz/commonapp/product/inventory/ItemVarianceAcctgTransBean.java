
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
public class ItemVarianceAcctgTransBean implements EntityBean
{
  /** The variable for the ACCTG_TRANS_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String acctgTransId;
  /** The variable for the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String inventoryItemId;
  /** The variable for the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String physicalInventoryId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ItemVarianceAcctgTransBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key ACCTG_TRANS_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getAcctgTransId() { return acctgTransId; }

  /** Get the value of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getInventoryItemId() { return inventoryItemId; }
  /** Set the value of the INVENTORY_ITEM_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public void setInventoryItemId(String inventoryItemId)
  {
    this.inventoryItemId = inventoryItemId;
    ejbIsModified = true;
  }

  /** Get the value of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public String getPhysicalInventoryId() { return physicalInventoryId; }
  /** Set the value of the PHYSICAL_INVENTORY_ID column of the ITEM_VARIANCE_ACCTG_TRANS table. */
  public void setPhysicalInventoryId(String physicalInventoryId)
  {
    this.physicalInventoryId = physicalInventoryId;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ItemVarianceAcctgTransBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ItemVarianceAcctgTrans valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getInventoryItemId() != null)
      {
        this.inventoryItemId = valueObject.getInventoryItemId();
        ejbIsModified = true;
      }
      if(valueObject.getPhysicalInventoryId() != null)
      {
        this.physicalInventoryId = valueObject.getPhysicalInventoryId();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ItemVarianceAcctgTransBean object
   *@return    The ValueObject value
   */
  public ItemVarianceAcctgTrans getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ItemVarianceAcctgTransValue((ItemVarianceAcctgTrans)this.entityContext.getEJBObject(), acctgTransId, inventoryItemId, physicalInventoryId);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  acctgTransId                  Field of the ACCTG_TRANS_ID column.
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String acctgTransId, String inventoryItemId, String physicalInventoryId) throws CreateException
  {
    this.acctgTransId = acctgTransId;
    this.inventoryItemId = inventoryItemId;
    this.physicalInventoryId = physicalInventoryId;
    return null;
  }

  /** Description of the Method
   *@param  acctgTransId                  Field of the ACCTG_TRANS_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String acctgTransId) throws CreateException
  {
    return ejbCreate(acctgTransId, null, null);
  }

  /** Description of the Method
   *@param  acctgTransId                  Field of the ACCTG_TRANS_ID column.
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String acctgTransId, String inventoryItemId, String physicalInventoryId) throws CreateException {}

  /** Description of the Method
   *@param  acctgTransId                  Field of the ACCTG_TRANS_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String acctgTransId) throws CreateException
  {
    ejbPostCreate(acctgTransId, null, null);
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
