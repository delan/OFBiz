
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Quantity Break Entity
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
public class QuantityBreakBean implements EntityBean
{
  /** The variable for the QUANTITY_BREAK_ID column of the QUANTITY_BREAK table. */
  public String quantityBreakId;
  /** The variable for the FROM_QUANTITY column of the QUANTITY_BREAK table. */
  public Double fromQuantity;
  /** The variable for the THRU_QUANTITY column of the QUANTITY_BREAK table. */
  public Double thruQuantity;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the QuantityBreakBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key QUANTITY_BREAK_ID column of the QUANTITY_BREAK table. */
  public String getQuantityBreakId() { return quantityBreakId; }

  /** Get the value of the FROM_QUANTITY column of the QUANTITY_BREAK table. */
  public Double getFromQuantity() { return fromQuantity; }
  /** Set the value of the FROM_QUANTITY column of the QUANTITY_BREAK table. */
  public void setFromQuantity(Double fromQuantity)
  {
    this.fromQuantity = fromQuantity;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_QUANTITY column of the QUANTITY_BREAK table. */
  public Double getThruQuantity() { return thruQuantity; }
  /** Set the value of the THRU_QUANTITY column of the QUANTITY_BREAK table. */
  public void setThruQuantity(Double thruQuantity)
  {
    this.thruQuantity = thruQuantity;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the QuantityBreakBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(QuantityBreak valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getFromQuantity() != null)
      {
        this.fromQuantity = valueObject.getFromQuantity();
        ejbIsModified = true;
      }
      if(valueObject.getThruQuantity() != null)
      {
        this.thruQuantity = valueObject.getThruQuantity();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the QuantityBreakBean object
   *@return    The ValueObject value
   */
  public QuantityBreak getValueObject()
  {
    if(this.entityContext != null)
    {
      return new QuantityBreakValue((QuantityBreak)this.entityContext.getEJBObject(), quantityBreakId, fromQuantity, thruQuantity);
    }
    else { return null; }
  }


  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByQuantityBreakId(quantityBreakId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByQuantityBreakId(quantityBreakId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  /** Description of the Method
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  fromQuantity                  Field of the FROM_QUANTITY column.
   *@param  thruQuantity                  Field of the THRU_QUANTITY column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String quantityBreakId, Double fromQuantity, Double thruQuantity) throws CreateException
  {
    this.quantityBreakId = quantityBreakId;
    this.fromQuantity = fromQuantity;
    this.thruQuantity = thruQuantity;
    return null;
  }

  /** Description of the Method
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String quantityBreakId) throws CreateException
  {
    return ejbCreate(quantityBreakId, null, null);
  }

  /** Description of the Method
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  fromQuantity                  Field of the FROM_QUANTITY column.
   *@param  thruQuantity                  Field of the THRU_QUANTITY column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String quantityBreakId, Double fromQuantity, Double thruQuantity) throws CreateException {}

  /** Description of the Method
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String quantityBreakId) throws CreateException
  {
    ejbPostCreate(quantityBreakId, null, null);
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
