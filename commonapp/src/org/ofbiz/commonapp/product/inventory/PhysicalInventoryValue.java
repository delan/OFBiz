
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Physical Inventory Entity
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
public class PhysicalInventoryValue implements PhysicalInventory
{
  /** The variable of the PHYSICAL_INVENTORY_ID column of the PHYSICAL_INVENTORY table. */
  private String physicalInventoryId;
  /** The variable of the DATE column of the PHYSICAL_INVENTORY table. */
  private java.util.Date date;
  /** The variable of the PARTY_ID column of the PHYSICAL_INVENTORY table. */
  private String partyId;
  /** The variable of the COMMENT column of the PHYSICAL_INVENTORY table. */
  private String comment;

  private PhysicalInventory physicalInventory;

  public PhysicalInventoryValue()
  {
    this.physicalInventoryId = null;
    this.date = null;
    this.partyId = null;
    this.comment = null;

    this.physicalInventory = null;
  }

  public PhysicalInventoryValue(PhysicalInventory physicalInventory) throws RemoteException
  {
    if(physicalInventory == null) return;
  
    this.physicalInventoryId = physicalInventory.getPhysicalInventoryId();
    this.date = physicalInventory.getDate();
    this.partyId = physicalInventory.getPartyId();
    this.comment = physicalInventory.getComment();

    this.physicalInventory = physicalInventory;
  }

  public PhysicalInventoryValue(PhysicalInventory physicalInventory, String physicalInventoryId, java.util.Date date, String partyId, String comment)
  {
    if(physicalInventory == null) return;
  
    this.physicalInventoryId = physicalInventoryId;
    this.date = date;
    this.partyId = partyId;
    this.comment = comment;

    this.physicalInventory = physicalInventory;
  }


  /** Get the primary key of the PHYSICAL_INVENTORY_ID column of the PHYSICAL_INVENTORY table. */
  public String getPhysicalInventoryId()  throws RemoteException { return physicalInventoryId; }

  /** Get the value of the DATE column of the PHYSICAL_INVENTORY table. */
  public java.util.Date getDate() throws RemoteException { return date; }
  /** Set the value of the DATE column of the PHYSICAL_INVENTORY table. */
  public void setDate(java.util.Date date) throws RemoteException
  {
    this.date = date;
    if(physicalInventory!=null) physicalInventory.setDate(date);
  }

  /** Get the value of the PARTY_ID column of the PHYSICAL_INVENTORY table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the PHYSICAL_INVENTORY table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(physicalInventory!=null) physicalInventory.setPartyId(partyId);
  }

  /** Get the value of the COMMENT column of the PHYSICAL_INVENTORY table. */
  public String getComment() throws RemoteException { return comment; }
  /** Set the value of the COMMENT column of the PHYSICAL_INVENTORY table. */
  public void setComment(String comment) throws RemoteException
  {
    this.comment = comment;
    if(physicalInventory!=null) physicalInventory.setComment(comment);
  }

  /** Get the value object of the PhysicalInventory class. */
  public PhysicalInventory getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PhysicalInventory class. */
  public void setValueObject(PhysicalInventory valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(physicalInventory!=null) physicalInventory.setValueObject(valueObject);

    if(physicalInventoryId == null) physicalInventoryId = valueObject.getPhysicalInventoryId();
    date = valueObject.getDate();
    partyId = valueObject.getPartyId();
    comment = valueObject.getComment();
  }


  /** Get a collection of  InventoryItemVariance related entities. */
  public Collection getInventoryItemVariances() { return InventoryItemVarianceHelper.findByPhysicalInventoryId(physicalInventoryId); }
  /** Get the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemVariance getInventoryItemVariance(String inventoryItemId) { return InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId); }
  /** Remove  InventoryItemVariance related entities. */
  public void removeInventoryItemVariances() { InventoryItemVarianceHelper.removeByPhysicalInventoryId(physicalInventoryId); }
  /** Remove the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemVariance(String inventoryItemId) { InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(physicalInventory!=null) return physicalInventory.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(physicalInventory!=null) return physicalInventory.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(physicalInventory!=null) return physicalInventory.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(physicalInventory!=null) return physicalInventory.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(physicalInventory!=null) physicalInventory.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
