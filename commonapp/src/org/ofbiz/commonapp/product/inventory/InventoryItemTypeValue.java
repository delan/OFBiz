
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class InventoryItemTypeValue implements InventoryItemType
{
  /** The variable of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  private String inventoryItemTypeId;
  /** The variable of the PARENT_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the INVENTORY_ITEM_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the INVENTORY_ITEM_TYPE table. */
  private String description;

  private InventoryItemType inventoryItemType;

  public InventoryItemTypeValue()
  {
    this.inventoryItemTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.inventoryItemType = null;
  }

  public InventoryItemTypeValue(InventoryItemType inventoryItemType) throws RemoteException
  {
    if(inventoryItemType == null) return;
  
    this.inventoryItemTypeId = inventoryItemType.getInventoryItemTypeId();
    this.parentTypeId = inventoryItemType.getParentTypeId();
    this.hasTable = inventoryItemType.getHasTable();
    this.description = inventoryItemType.getDescription();

    this.inventoryItemType = inventoryItemType;
  }

  public InventoryItemTypeValue(InventoryItemType inventoryItemType, String inventoryItemTypeId, String parentTypeId, String hasTable, String description)
  {
    if(inventoryItemType == null) return;
  
    this.inventoryItemTypeId = inventoryItemTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.inventoryItemType = inventoryItemType;
  }


  /** Get the primary key of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  public String getInventoryItemTypeId()  throws RemoteException { return inventoryItemTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the INVENTORY_ITEM_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(inventoryItemType!=null) inventoryItemType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the INVENTORY_ITEM_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the INVENTORY_ITEM_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(inventoryItemType!=null) inventoryItemType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the INVENTORY_ITEM_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the INVENTORY_ITEM_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(inventoryItemType!=null) inventoryItemType.setDescription(description);
  }

  /** Get the value object of the InventoryItemType class. */
  public InventoryItemType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the InventoryItemType class. */
  public void setValueObject(InventoryItemType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(inventoryItemType!=null) inventoryItemType.setValueObject(valueObject);

    if(inventoryItemTypeId == null) inventoryItemTypeId = valueObject.getInventoryItemTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent InventoryItemType entity corresponding to this entity. */
  public InventoryItemType getParentInventoryItemType() { return InventoryItemTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent InventoryItemType entity corresponding to this entity. */
  public void removeParentInventoryItemType() { InventoryItemTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child InventoryItemType related entities. */
  public Collection getChildInventoryItemTypes() { return InventoryItemTypeHelper.findByParentTypeId(inventoryItemTypeId); }
  /** Get the Child InventoryItemType keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemType getChildInventoryItemType(String inventoryItemTypeId) { return InventoryItemTypeHelper.findByPrimaryKey(inventoryItemTypeId); }
  /** Remove Child InventoryItemType related entities. */
  public void removeChildInventoryItemTypes() { InventoryItemTypeHelper.removeByParentTypeId(inventoryItemTypeId); }
  /** Remove the Child InventoryItemType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildInventoryItemType(String inventoryItemTypeId) { InventoryItemTypeHelper.removeByPrimaryKey(inventoryItemTypeId); }

  /** Get a collection of  InventoryItemTypeAttr related entities. */
  public Collection getInventoryItemTypeAttrs() { return InventoryItemTypeAttrHelper.findByInventoryItemTypeId(inventoryItemTypeId); }
  /** Get the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemTypeAttr getInventoryItemTypeAttr(String name) { return InventoryItemTypeAttrHelper.findByPrimaryKey(inventoryItemTypeId, name); }
  /** Remove  InventoryItemTypeAttr related entities. */
  public void removeInventoryItemTypeAttrs() { InventoryItemTypeAttrHelper.removeByInventoryItemTypeId(inventoryItemTypeId); }
  /** Remove the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemTypeAttr(String name) { InventoryItemTypeAttrHelper.removeByPrimaryKey(inventoryItemTypeId, name); }

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByInventoryItemTypeId(inventoryItemTypeId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByInventoryItemTypeId(inventoryItemTypeId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(inventoryItemType!=null) return inventoryItemType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(inventoryItemType!=null) return inventoryItemType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(inventoryItemType!=null) return inventoryItemType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(inventoryItemType!=null) return inventoryItemType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(inventoryItemType!=null) inventoryItemType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
