
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Inventory Item Type Attribute Entity
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
public class InventoryItemTypeAttrBean implements EntityBean
{
  /** The variable for the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE_ATTR table. */
  public String inventoryItemTypeId;
  /** The variable for the NAME column of the INVENTORY_ITEM_TYPE_ATTR table. */
  public String name;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the InventoryItemTypeAttrBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE_ATTR table. */
  public String getInventoryItemTypeId() { return inventoryItemTypeId; }

  /** Get the primary key NAME column of the INVENTORY_ITEM_TYPE_ATTR table. */
  public String getName() { return name; }

  /** Sets the values from ValueObject attribute of the InventoryItemTypeAttrBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(InventoryItemTypeAttr valueObject)
  {
  }

  /** Gets the ValueObject attribute of the InventoryItemTypeAttrBean object
   *@return    The ValueObject value
   */
  public InventoryItemTypeAttr getValueObject()
  {
    if(this.entityContext != null)
    {
      return new InventoryItemTypeAttrValue((InventoryItemTypeAttr)this.entityContext.getEJBObject(), inventoryItemTypeId, name);
    }
    else { return null; }
  }


  /** Get the  InventoryItemType entity corresponding to this entity. */
  public InventoryItemType getInventoryItemType() { return InventoryItemTypeHelper.findByPrimaryKey(inventoryItemTypeId); }
  /** Remove the  InventoryItemType entity corresponding to this entity. */
  public void removeInventoryItemType() { InventoryItemTypeHelper.removeByPrimaryKey(inventoryItemTypeId); }

  /** Get a collection of  InventoryItemAttribute related entities. */
  public Collection getInventoryItemAttributes() { return InventoryItemAttributeHelper.findByName(name); }
  /** Get the  InventoryItemAttribute keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemAttribute getInventoryItemAttribute(String inventoryItemId) { return InventoryItemAttributeHelper.findByPrimaryKey(inventoryItemId, name); }
  /** Remove  InventoryItemAttribute related entities. */
  public void removeInventoryItemAttributes() { InventoryItemAttributeHelper.removeByName(name); }
  /** Remove the  InventoryItemAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemAttribute(String inventoryItemId) { InventoryItemAttributeHelper.removeByPrimaryKey(inventoryItemId, name); }

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByInventoryItemTypeId(inventoryItemTypeId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByInventoryItemTypeId(inventoryItemTypeId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }


  /** Description of the Method
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.inventory.InventoryItemTypeAttrPK ejbCreate(String inventoryItemTypeId, String name) throws CreateException
  {
    this.inventoryItemTypeId = inventoryItemTypeId;
    this.name = name;
    return null;
  }

  /** Description of the Method
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemTypeId, String name) throws CreateException {}

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
