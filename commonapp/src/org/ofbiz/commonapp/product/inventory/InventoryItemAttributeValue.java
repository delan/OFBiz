
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class InventoryItemAttributeValue implements InventoryItemAttribute
{
  /** The variable of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_ATTRIBUTE table. */
  private String inventoryItemId;
  /** The variable of the NAME column of the INVENTORY_ITEM_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  private String value;

  private InventoryItemAttribute inventoryItemAttribute;

  public InventoryItemAttributeValue()
  {
    this.inventoryItemId = null;
    this.name = null;
    this.value = null;

    this.inventoryItemAttribute = null;
  }

  public InventoryItemAttributeValue(InventoryItemAttribute inventoryItemAttribute) throws RemoteException
  {
    if(inventoryItemAttribute == null) return;
  
    this.inventoryItemId = inventoryItemAttribute.getInventoryItemId();
    this.name = inventoryItemAttribute.getName();
    this.value = inventoryItemAttribute.getValue();

    this.inventoryItemAttribute = inventoryItemAttribute;
  }

  public InventoryItemAttributeValue(InventoryItemAttribute inventoryItemAttribute, String inventoryItemId, String name, String value)
  {
    if(inventoryItemAttribute == null) return;
  
    this.inventoryItemId = inventoryItemId;
    this.name = name;
    this.value = value;

    this.inventoryItemAttribute = inventoryItemAttribute;
  }


  /** Get the primary key of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getInventoryItemId()  throws RemoteException { return inventoryItemId; }

  /** Get the primary key of the NAME column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the INVENTORY_ITEM_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(inventoryItemAttribute!=null) inventoryItemAttribute.setValue(value);
  }

  /** Get the value object of the InventoryItemAttribute class. */
  public InventoryItemAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the InventoryItemAttribute class. */
  public void setValueObject(InventoryItemAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(inventoryItemAttribute!=null) inventoryItemAttribute.setValueObject(valueObject);

    if(inventoryItemId == null) inventoryItemId = valueObject.getInventoryItemId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(inventoryItemAttribute!=null) return inventoryItemAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(inventoryItemAttribute!=null) return inventoryItemAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(inventoryItemAttribute!=null) return inventoryItemAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(inventoryItemAttribute!=null) return inventoryItemAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(inventoryItemAttribute!=null) inventoryItemAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
