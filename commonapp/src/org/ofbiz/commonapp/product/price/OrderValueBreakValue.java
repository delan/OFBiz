
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Order Value Break Entity
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
public class OrderValueBreakValue implements OrderValueBreak
{
  /** The variable of the ORDER_VALUE_BREAK_ID column of the ORDER_VALUE_BREAK table. */
  private String orderValueBreakId;
  /** The variable of the FROM_AMOUNT column of the ORDER_VALUE_BREAK table. */
  private Double fromAmount;
  /** The variable of the THRU_AMOUNT column of the ORDER_VALUE_BREAK table. */
  private Double thruAmount;

  private OrderValueBreak orderValueBreak;

  public OrderValueBreakValue()
  {
    this.orderValueBreakId = null;
    this.fromAmount = null;
    this.thruAmount = null;

    this.orderValueBreak = null;
  }

  public OrderValueBreakValue(OrderValueBreak orderValueBreak) throws RemoteException
  {
    if(orderValueBreak == null) return;
  
    this.orderValueBreakId = orderValueBreak.getOrderValueBreakId();
    this.fromAmount = orderValueBreak.getFromAmount();
    this.thruAmount = orderValueBreak.getThruAmount();

    this.orderValueBreak = orderValueBreak;
  }

  public OrderValueBreakValue(OrderValueBreak orderValueBreak, String orderValueBreakId, Double fromAmount, Double thruAmount)
  {
    if(orderValueBreak == null) return;
  
    this.orderValueBreakId = orderValueBreakId;
    this.fromAmount = fromAmount;
    this.thruAmount = thruAmount;

    this.orderValueBreak = orderValueBreak;
  }


  /** Get the primary key of the ORDER_VALUE_BREAK_ID column of the ORDER_VALUE_BREAK table. */
  public String getOrderValueBreakId()  throws RemoteException { return orderValueBreakId; }

  /** Get the value of the FROM_AMOUNT column of the ORDER_VALUE_BREAK table. */
  public Double getFromAmount() throws RemoteException { return fromAmount; }
  /** Set the value of the FROM_AMOUNT column of the ORDER_VALUE_BREAK table. */
  public void setFromAmount(Double fromAmount) throws RemoteException
  {
    this.fromAmount = fromAmount;
    if(orderValueBreak!=null) orderValueBreak.setFromAmount(fromAmount);
  }

  /** Get the value of the THRU_AMOUNT column of the ORDER_VALUE_BREAK table. */
  public Double getThruAmount() throws RemoteException { return thruAmount; }
  /** Set the value of the THRU_AMOUNT column of the ORDER_VALUE_BREAK table. */
  public void setThruAmount(Double thruAmount) throws RemoteException
  {
    this.thruAmount = thruAmount;
    if(orderValueBreak!=null) orderValueBreak.setThruAmount(thruAmount);
  }

  /** Get the value object of the OrderValueBreak class. */
  public OrderValueBreak getValueObject() throws RemoteException { return this; }
  /** Set the value object of the OrderValueBreak class. */
  public void setValueObject(OrderValueBreak valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(orderValueBreak!=null) orderValueBreak.setValueObject(valueObject);

    if(orderValueBreakId == null) orderValueBreakId = valueObject.getOrderValueBreakId();
    fromAmount = valueObject.getFromAmount();
    thruAmount = valueObject.getThruAmount();
  }


  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByOrderValueBreakId(orderValueBreakId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByOrderValueBreakId(orderValueBreakId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(orderValueBreak!=null) return orderValueBreak.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(orderValueBreak!=null) return orderValueBreak.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(orderValueBreak!=null) return orderValueBreak.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(orderValueBreak!=null) return orderValueBreak.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(orderValueBreak!=null) orderValueBreak.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
