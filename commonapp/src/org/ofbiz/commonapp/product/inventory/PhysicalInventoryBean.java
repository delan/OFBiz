
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


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
public class PhysicalInventoryBean implements EntityBean
{
  /** The variable for the PHYSICAL_INVENTORY_ID column of the PHYSICAL_INVENTORY table. */
  public String physicalInventoryId;
  /** The variable for the DATE column of the PHYSICAL_INVENTORY table. */
  public java.util.Date date;
  /** The variable for the PARTY_ID column of the PHYSICAL_INVENTORY table. */
  public String partyId;
  /** The variable for the COMMENT column of the PHYSICAL_INVENTORY table. */
  public String comment;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the PhysicalInventoryBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PHYSICAL_INVENTORY_ID column of the PHYSICAL_INVENTORY table. */
  public String getPhysicalInventoryId() { return physicalInventoryId; }

  /** Get the value of the DATE column of the PHYSICAL_INVENTORY table. */
  public java.util.Date getDate() { return date; }
  /** Set the value of the DATE column of the PHYSICAL_INVENTORY table. */
  public void setDate(java.util.Date date)
  {
    this.date = date;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_ID column of the PHYSICAL_INVENTORY table. */
  public String getPartyId() { return partyId; }
  /** Set the value of the PARTY_ID column of the PHYSICAL_INVENTORY table. */
  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
    ejbIsModified = true;
  }

  /** Get the value of the COMMENT column of the PHYSICAL_INVENTORY table. */
  public String getComment() { return comment; }
  /** Set the value of the COMMENT column of the PHYSICAL_INVENTORY table. */
  public void setComment(String comment)
  {
    this.comment = comment;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the PhysicalInventoryBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(PhysicalInventory valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getDate() != null)
      {
        this.date = valueObject.getDate();
        ejbIsModified = true;
      }
      if(valueObject.getPartyId() != null)
      {
        this.partyId = valueObject.getPartyId();
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

  /** Gets the ValueObject attribute of the PhysicalInventoryBean object
   *@return    The ValueObject value
   */
  public PhysicalInventory getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PhysicalInventoryValue((PhysicalInventory)this.entityContext.getEJBObject(), physicalInventoryId, date, partyId, comment);
    }
    else { return null; }
  }


  /** Get a collection of  InventoryItemVariance related entities. */
  public Collection getInventoryItemVariances() { return InventoryItemVarianceHelper.findByPhysicalInventoryId(physicalInventoryId); }
  /** Get the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemVariance getInventoryItemVariance(String inventoryItemId) { return InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId); }
  /** Remove  InventoryItemVariance related entities. */
  public void removeInventoryItemVariances() { InventoryItemVarianceHelper.removeByPhysicalInventoryId(physicalInventoryId); }
  /** Remove the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemVariance(String inventoryItemId) { InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId); }


  /** Description of the Method
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  date                  Field of the DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  comment                  Field of the COMMENT column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String physicalInventoryId, java.util.Date date, String partyId, String comment) throws CreateException
  {
    this.physicalInventoryId = physicalInventoryId;
    this.date = date;
    this.partyId = partyId;
    this.comment = comment;
    return null;
  }

  /** Description of the Method
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String physicalInventoryId) throws CreateException
  {
    return ejbCreate(physicalInventoryId, null, null, null);
  }

  /** Description of the Method
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  date                  Field of the DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  comment                  Field of the COMMENT column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String physicalInventoryId, java.util.Date date, String partyId, String comment) throws CreateException {}

  /** Description of the Method
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String physicalInventoryId) throws CreateException
  {
    ejbPostCreate(physicalInventoryId, null, null, null);
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
