
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Quantity Break Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the QuantityBreak Entity EJB; acts as a proxy for the Home interface
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
public class QuantityBreakHelper
{

  /** A static variable to cache the Home object for the QuantityBreak EJB */
  private static QuantityBreakHome quantityBreakHome = null;

  /** Initializes the quantityBreakHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The QuantityBreakHome instance for the default EJB server
   */
  public static QuantityBreakHome getQuantityBreakHome()
  {
    if(quantityBreakHome == null) //don't want to block here
    {
      synchronized(QuantityBreakHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(quantityBreakHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.QuantityBreakHome");
            quantityBreakHome = (QuantityBreakHome)MyNarrow.narrow(homeObject, QuantityBreakHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("quantityBreak home obtained " + quantityBreakHome);
        }
      }
    }
    return quantityBreakHome;
  }




  /** Remove the QuantityBreak corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    QuantityBreak quantityBreak = findByPrimaryKey(primaryKey);
    try
    {
      if(quantityBreak != null)
      {
        quantityBreak.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a QuantityBreak by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The QuantityBreak corresponding to the primaryKey
   */
  public static QuantityBreak findByPrimaryKey(java.lang.String primaryKey)
  {
    QuantityBreak quantityBreak = null;
    Debug.logInfo("QuantityBreakHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      quantityBreak = (QuantityBreak)MyNarrow.narrow(getQuantityBreakHome().findByPrimaryKey(primaryKey), QuantityBreak.class);
      if(quantityBreak != null)
      {
        quantityBreak = quantityBreak.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return quantityBreak;
  }

  /** Finds all QuantityBreak entities
   *@return    Collection containing all QuantityBreak entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("QuantityBreakHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getQuantityBreakHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a QuantityBreak
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  fromQuantity                  Field of the FROM_QUANTITY column.
   *@param  thruQuantity                  Field of the THRU_QUANTITY column.
   *@return                Description of the Returned Value
   */
  public static QuantityBreak create(String quantityBreakId, Double fromQuantity, Double thruQuantity)
  {
    QuantityBreak quantityBreak = null;
    Debug.logInfo("QuantityBreakHelper.create: quantityBreakId: " + quantityBreakId);
    if(quantityBreakId == null) { return null; }

    try { quantityBreak = (QuantityBreak)MyNarrow.narrow(getQuantityBreakHome().create(quantityBreakId, fromQuantity, thruQuantity), QuantityBreak.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create quantityBreak with quantityBreakId: " + quantityBreakId);
      Debug.logError(ce);
      quantityBreak = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return quantityBreak;
  }

  /** Updates the corresponding QuantityBreak
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  fromQuantity                  Field of the FROM_QUANTITY column.
   *@param  thruQuantity                  Field of the THRU_QUANTITY column.
   *@return                Description of the Returned Value
   */
  public static QuantityBreak update(String quantityBreakId, Double fromQuantity, Double thruQuantity) throws java.rmi.RemoteException
  {
    if(quantityBreakId == null) { return null; }
    QuantityBreak quantityBreak = findByPrimaryKey(quantityBreakId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    QuantityBreak quantityBreakValue = new QuantityBreakValue();

    if(fromQuantity != null) { quantityBreakValue.setFromQuantity(fromQuantity); }
    if(thruQuantity != null) { quantityBreakValue.setThruQuantity(thruQuantity); }

    quantityBreak.setValueObject(quantityBreakValue);
    return quantityBreak;
  }


}
