
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
public class InventoryItemAttributeBean implements EntityBean
{
  /** The variable for the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String inventoryItemId;
  /** The variable for the NAME column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String name;
  /** The variable for the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String value;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the InventoryItemAttributeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key INVENTORY_ITEM_ID column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getInventoryItemId() { return inventoryItemId; }

  /** Get the primary key NAME column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getName() { return name; }

  /** Get the value of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getValue() { return value; }
  /** Set the value of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public void setValue(String value)
  {
    this.value = value;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the InventoryItemAttributeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(InventoryItemAttribute valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getValue() != null)
      {
        this.value = valueObject.getValue();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the InventoryItemAttributeBean object
   *@return    The ValueObject value
   */
  public InventoryItemAttribute getValueObject()
  {
    if(this.entityContext != null)
    {
      return new InventoryItemAttributeValue((InventoryItemAttribute)this.entityContext.getEJBObject(), inventoryItemId, name, value);
    }
    else { return null; }
  }


  /** Get the  InventoryItem entity corresponding to this entity. */
  public InventoryItem getInventoryItem() { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove the  InventoryItem entity corresponding to this entity. */
  public void removeInventoryItem() { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get a collection of  InventoryItemTypeAttr related entities. */
  public Collection getInventoryItemTypeAttrs() { return InventoryItemTypeAttrHelper.findByName(name); }
  /** Get the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemTypeAttr getInventoryItemTypeAttr(String inventoryItemTypeId) { return InventoryItemTypeAttrHelper.findByPrimaryKey(inventoryItemTypeId, name); }
  /** Remove  InventoryItemTypeAttr related entities. */
  public void removeInventoryItemTypeAttrs() { InventoryItemTypeAttrHelper.removeByName(name); }
  /** Remove the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemTypeAttr(String inventoryItemTypeId) { InventoryItemTypeAttrHelper.removeByPrimaryKey(inventoryItemTypeId, name); }


  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.inventory.InventoryItemAttributePK ejbCreate(String inventoryItemId, String name, String value) throws CreateException
  {
    this.inventoryItemId = inventoryItemId;
    this.name = name;
    this.value = value;
    return null;
  }

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.inventory.InventoryItemAttributePK ejbCreate(String inventoryItemId, String name) throws CreateException
  {
    return ejbCreate(inventoryItemId, name, null);
  }

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemId, String name, String value) throws CreateException {}

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemId, String name) throws CreateException
  {
    ejbPostCreate(inventoryItemId, name, null);
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
