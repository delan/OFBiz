
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class InventoryItemTypeAttrValue implements InventoryItemTypeAttr
{
  /** The variable of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE_ATTR table. */
  private String inventoryItemTypeId;
  /** The variable of the NAME column of the INVENTORY_ITEM_TYPE_ATTR table. */
  private String name;

  private InventoryItemTypeAttr inventoryItemTypeAttr;

  public InventoryItemTypeAttrValue()
  {
    this.inventoryItemTypeId = null;
    this.name = null;

    this.inventoryItemTypeAttr = null;
  }

  public InventoryItemTypeAttrValue(InventoryItemTypeAttr inventoryItemTypeAttr) throws RemoteException
  {
    if(inventoryItemTypeAttr == null) return;
  
    this.inventoryItemTypeId = inventoryItemTypeAttr.getInventoryItemTypeId();
    this.name = inventoryItemTypeAttr.getName();

    this.inventoryItemTypeAttr = inventoryItemTypeAttr;
  }

  public InventoryItemTypeAttrValue(InventoryItemTypeAttr inventoryItemTypeAttr, String inventoryItemTypeId, String name)
  {
    if(inventoryItemTypeAttr == null) return;
  
    this.inventoryItemTypeId = inventoryItemTypeId;
    this.name = name;

    this.inventoryItemTypeAttr = inventoryItemTypeAttr;
  }


  /** Get the primary key of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM_TYPE_ATTR table. */
  public String getInventoryItemTypeId()  throws RemoteException { return inventoryItemTypeId; }

  /** Get the primary key of the NAME column of the INVENTORY_ITEM_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the InventoryItemTypeAttr class. */
  public InventoryItemTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the InventoryItemTypeAttr class. */
  public void setValueObject(InventoryItemTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(inventoryItemTypeAttr!=null) inventoryItemTypeAttr.setValueObject(valueObject);

    if(inventoryItemTypeId == null) inventoryItemTypeId = valueObject.getInventoryItemTypeId();
    if(name == null) name = valueObject.getName();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(inventoryItemTypeAttr!=null) return inventoryItemTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(inventoryItemTypeAttr!=null) return inventoryItemTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(inventoryItemTypeAttr!=null) return inventoryItemTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(inventoryItemTypeAttr!=null) return inventoryItemTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(inventoryItemTypeAttr!=null) inventoryItemTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
