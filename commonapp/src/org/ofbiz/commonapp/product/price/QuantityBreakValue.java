
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class QuantityBreakValue implements QuantityBreak
{
  /** The variable of the QUANTITY_BREAK_ID column of the QUANTITY_BREAK table. */
  private String quantityBreakId;
  /** The variable of the FROM_QUANTITY column of the QUANTITY_BREAK table. */
  private Double fromQuantity;
  /** The variable of the THRU_QUANTITY column of the QUANTITY_BREAK table. */
  private Double thruQuantity;

  private QuantityBreak quantityBreak;

  public QuantityBreakValue()
  {
    this.quantityBreakId = null;
    this.fromQuantity = null;
    this.thruQuantity = null;

    this.quantityBreak = null;
  }

  public QuantityBreakValue(QuantityBreak quantityBreak) throws RemoteException
  {
    if(quantityBreak == null) return;
  
    this.quantityBreakId = quantityBreak.getQuantityBreakId();
    this.fromQuantity = quantityBreak.getFromQuantity();
    this.thruQuantity = quantityBreak.getThruQuantity();

    this.quantityBreak = quantityBreak;
  }

  public QuantityBreakValue(QuantityBreak quantityBreak, String quantityBreakId, Double fromQuantity, Double thruQuantity)
  {
    if(quantityBreak == null) return;
  
    this.quantityBreakId = quantityBreakId;
    this.fromQuantity = fromQuantity;
    this.thruQuantity = thruQuantity;

    this.quantityBreak = quantityBreak;
  }


  /** Get the primary key of the QUANTITY_BREAK_ID column of the QUANTITY_BREAK table. */
  public String getQuantityBreakId()  throws RemoteException { return quantityBreakId; }

  /** Get the value of the FROM_QUANTITY column of the QUANTITY_BREAK table. */
  public Double getFromQuantity() throws RemoteException { return fromQuantity; }
  /** Set the value of the FROM_QUANTITY column of the QUANTITY_BREAK table. */
  public void setFromQuantity(Double fromQuantity) throws RemoteException
  {
    this.fromQuantity = fromQuantity;
    if(quantityBreak!=null) quantityBreak.setFromQuantity(fromQuantity);
  }

  /** Get the value of the THRU_QUANTITY column of the QUANTITY_BREAK table. */
  public Double getThruQuantity() throws RemoteException { return thruQuantity; }
  /** Set the value of the THRU_QUANTITY column of the QUANTITY_BREAK table. */
  public void setThruQuantity(Double thruQuantity) throws RemoteException
  {
    this.thruQuantity = thruQuantity;
    if(quantityBreak!=null) quantityBreak.setThruQuantity(thruQuantity);
  }

  /** Get the value object of the QuantityBreak class. */
  public QuantityBreak getValueObject() throws RemoteException { return this; }
  /** Set the value object of the QuantityBreak class. */
  public void setValueObject(QuantityBreak valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(quantityBreak!=null) quantityBreak.setValueObject(valueObject);

    if(quantityBreakId == null) quantityBreakId = valueObject.getQuantityBreakId();
    fromQuantity = valueObject.getFromQuantity();
    thruQuantity = valueObject.getThruQuantity();
  }


  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByQuantityBreakId(quantityBreakId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByQuantityBreakId(quantityBreakId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(quantityBreak!=null) return quantityBreak.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(quantityBreak!=null) return quantityBreak.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(quantityBreak!=null) return quantityBreak.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(quantityBreak!=null) return quantityBreak.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(quantityBreak!=null) quantityBreak.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
