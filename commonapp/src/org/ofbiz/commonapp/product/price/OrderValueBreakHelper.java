
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Order Value Break Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the OrderValueBreak Entity EJB; acts as a proxy for the Home interface
 *
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
public class OrderValueBreakHelper
{

  /** A static variable to cache the Home object for the OrderValueBreak EJB */
  private static OrderValueBreakHome orderValueBreakHome = null;

  /** Initializes the orderValueBreakHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The OrderValueBreakHome instance for the default EJB server
   */
  public static OrderValueBreakHome getOrderValueBreakHome()
  {
    if(orderValueBreakHome == null) //don't want to block here
    {
      synchronized(OrderValueBreakHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(orderValueBreakHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.OrderValueBreakHome");
            orderValueBreakHome = (OrderValueBreakHome)MyNarrow.narrow(homeObject, OrderValueBreakHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("orderValueBreak home obtained " + orderValueBreakHome);
        }
      }
    }
    return orderValueBreakHome;
  }




  /** Remove the OrderValueBreak corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    OrderValueBreak orderValueBreak = findByPrimaryKey(primaryKey);
    try
    {
      if(orderValueBreak != null)
      {
        orderValueBreak.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a OrderValueBreak by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The OrderValueBreak corresponding to the primaryKey
   */
  public static OrderValueBreak findByPrimaryKey(java.lang.String primaryKey)
  {
    OrderValueBreak orderValueBreak = null;
    Debug.logInfo("OrderValueBreakHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      orderValueBreak = (OrderValueBreak)MyNarrow.narrow(getOrderValueBreakHome().findByPrimaryKey(primaryKey), OrderValueBreak.class);
      if(orderValueBreak != null)
      {
        orderValueBreak = orderValueBreak.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return orderValueBreak;
  }

  /** Finds all OrderValueBreak entities
   *@return    Collection containing all OrderValueBreak entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("OrderValueBreakHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getOrderValueBreakHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a OrderValueBreak
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@param  fromAmount                  Field of the FROM_AMOUNT column.
   *@param  thruAmount                  Field of the THRU_AMOUNT column.
   *@return                Description of the Returned Value
   */
  public static OrderValueBreak create(String orderValueBreakId, Double fromAmount, Double thruAmount)
  {
    OrderValueBreak orderValueBreak = null;
    Debug.logInfo("OrderValueBreakHelper.create: orderValueBreakId: " + orderValueBreakId);
    if(orderValueBreakId == null) { return null; }

    try { orderValueBreak = (OrderValueBreak)MyNarrow.narrow(getOrderValueBreakHome().create(orderValueBreakId, fromAmount, thruAmount), OrderValueBreak.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create orderValueBreak with orderValueBreakId: " + orderValueBreakId);
      Debug.logError(ce);
      orderValueBreak = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return orderValueBreak;
  }

  /** Updates the corresponding OrderValueBreak
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@param  fromAmount                  Field of the FROM_AMOUNT column.
   *@param  thruAmount                  Field of the THRU_AMOUNT column.
   *@return                Description of the Returned Value
   */
  public static OrderValueBreak update(String orderValueBreakId, Double fromAmount, Double thruAmount) throws java.rmi.RemoteException
  {
    if(orderValueBreakId == null) { return null; }
    OrderValueBreak orderValueBreak = findByPrimaryKey(orderValueBreakId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    OrderValueBreak orderValueBreakValue = new OrderValueBreakValue();

    if(fromAmount != null) { orderValueBreakValue.setFromAmount(fromAmount); }
    if(thruAmount != null) { orderValueBreakValue.setThruAmount(thruAmount); }

    orderValueBreak.setValueObject(orderValueBreakValue);
    return orderValueBreak;
  }


}
