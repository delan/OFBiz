
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


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
public class LotBean implements EntityBean
{
  /** The variable for the LOT_ID column of the LOT table. */
  public String lotId;
  /** The variable for the CREATION_DATE column of the LOT table. */
  public java.util.Date creationDate;
  /** The variable for the QUANTITY column of the LOT table. */
  public Double quantity;
  /** The variable for the EXPIRATION_DATE column of the LOT table. */
  public java.util.Date expirationDate;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the LotBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key LOT_ID column of the LOT table. */
  public String getLotId() { return lotId; }

  /** Get the value of the CREATION_DATE column of the LOT table. */
  public java.util.Date getCreationDate() { return creationDate; }
  /** Set the value of the CREATION_DATE column of the LOT table. */
  public void setCreationDate(java.util.Date creationDate)
  {
    this.creationDate = creationDate;
    ejbIsModified = true;
  }

  /** Get the value of the QUANTITY column of the LOT table. */
  public Double getQuantity() { return quantity; }
  /** Set the value of the QUANTITY column of the LOT table. */
  public void setQuantity(Double quantity)
  {
    this.quantity = quantity;
    ejbIsModified = true;
  }

  /** Get the value of the EXPIRATION_DATE column of the LOT table. */
  public java.util.Date getExpirationDate() { return expirationDate; }
  /** Set the value of the EXPIRATION_DATE column of the LOT table. */
  public void setExpirationDate(java.util.Date expirationDate)
  {
    this.expirationDate = expirationDate;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the LotBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Lot valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getCreationDate() != null)
      {
        this.creationDate = valueObject.getCreationDate();
        ejbIsModified = true;
      }
      if(valueObject.getQuantity() != null)
      {
        this.quantity = valueObject.getQuantity();
        ejbIsModified = true;
      }
      if(valueObject.getExpirationDate() != null)
      {
        this.expirationDate = valueObject.getExpirationDate();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the LotBean object
   *@return    The ValueObject value
   */
  public Lot getValueObject()
  {
    if(this.entityContext != null)
    {
      return new LotValue((Lot)this.entityContext.getEJBObject(), lotId, creationDate, quantity, expirationDate);
    }
    else { return null; }
  }


  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByLotId(lotId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByLotId(lotId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }


  /** Description of the Method
   *@param  lotId                  Field of the LOT_ID column.
   *@param  creationDate                  Field of the CREATION_DATE column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  expirationDate                  Field of the EXPIRATION_DATE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String lotId, java.util.Date creationDate, Double quantity, java.util.Date expirationDate) throws CreateException
  {
    this.lotId = lotId;
    this.creationDate = creationDate;
    this.quantity = quantity;
    this.expirationDate = expirationDate;
    return null;
  }

  /** Description of the Method
   *@param  lotId                  Field of the LOT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String lotId) throws CreateException
  {
    return ejbCreate(lotId, null, null, null);
  }

  /** Description of the Method
   *@param  lotId                  Field of the LOT_ID column.
   *@param  creationDate                  Field of the CREATION_DATE column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  expirationDate                  Field of the EXPIRATION_DATE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String lotId, java.util.Date creationDate, Double quantity, java.util.Date expirationDate) throws CreateException {}

  /** Description of the Method
   *@param  lotId                  Field of the LOT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String lotId) throws CreateException
  {
    ejbPostCreate(lotId, null, null, null);
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
