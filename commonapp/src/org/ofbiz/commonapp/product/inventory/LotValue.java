
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Lot Entity
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */
public class LotValue implements Lot
{
  /** The variable of the LOT_ID column of the LOT table. */
  private String lotId;
  /** The variable of the CREATION_DATE column of the LOT table. */
  private java.util.Date creationDate;
  /** The variable of the QUANTITY column of the LOT table. */
  private Double quantity;
  /** The variable of the EXPIRATION_DATE column of the LOT table. */
  private java.util.Date expirationDate;

  private Lot lot;

  public LotValue()
  {
    this.lotId = null;
    this.creationDate = null;
    this.quantity = null;
    this.expirationDate = null;

    this.lot = null;
  }

  public LotValue(Lot lot) throws RemoteException
  {
    if(lot == null) return;
  
    this.lotId = lot.getLotId();
    this.creationDate = lot.getCreationDate();
    this.quantity = lot.getQuantity();
    this.expirationDate = lot.getExpirationDate();

    this.lot = lot;
  }

  public LotValue(Lot lot, String lotId, java.util.Date creationDate, Double quantity, java.util.Date expirationDate)
  {
    if(lot == null) return;
  
    this.lotId = lotId;
    this.creationDate = creationDate;
    this.quantity = quantity;
    this.expirationDate = expirationDate;

    this.lot = lot;
  }


  /** Get the primary key of the LOT_ID column of the LOT table. */
  public String getLotId()  throws RemoteException { return lotId; }

  /** Get the value of the CREATION_DATE column of the LOT table. */
  public java.util.Date getCreationDate() throws RemoteException { return creationDate; }
  /** Set the value of the CREATION_DATE column of the LOT table. */
  public void setCreationDate(java.util.Date creationDate) throws RemoteException
  {
    this.creationDate = creationDate;
    if(lot!=null) lot.setCreationDate(creationDate);
  }

  /** Get the value of the QUANTITY column of the LOT table. */
  public Double getQuantity() throws RemoteException { return quantity; }
  /** Set the value of the QUANTITY column of the LOT table. */
  public void setQuantity(Double quantity) throws RemoteException
  {
    this.quantity = quantity;
    if(lot!=null) lot.setQuantity(quantity);
  }

  /** Get the value of the EXPIRATION_DATE column of the LOT table. */
  public java.util.Date getExpirationDate() throws RemoteException { return expirationDate; }
  /** Set the value of the EXPIRATION_DATE column of the LOT table. */
  public void setExpirationDate(java.util.Date expirationDate) throws RemoteException
  {
    this.expirationDate = expirationDate;
    if(lot!=null) lot.setExpirationDate(expirationDate);
  }

  /** Get the value object of the Lot class. */
  public Lot getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Lot class. */
  public void setValueObject(Lot valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(lot!=null) lot.setValueObject(valueObject);

    if(lotId == null) lotId = valueObject.getLotId();
    creationDate = valueObject.getCreationDate();
    quantity = valueObject.getQuantity();
    expirationDate = valueObject.getExpirationDate();
  }


  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByLotId(lotId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByLotId(lotId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(lot!=null) return lot.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(lot!=null) return lot.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(lot!=null) return lot.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(lot!=null) return lot.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(lot!=null) lot.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
