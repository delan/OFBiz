
package org.ofbiz.commonapp.product.supplier;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Preference Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the SupplierPrefOrder Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class SupplierPrefOrderHelper
{

  /** A static variable to cache the Home object for the SupplierPrefOrder EJB */
  private static SupplierPrefOrderHome supplierPrefOrderHome = null;

  /** Initializes the supplierPrefOrderHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SupplierPrefOrderHome instance for the default EJB server
   */
  public static SupplierPrefOrderHome getSupplierPrefOrderHome()
  {
    if(supplierPrefOrderHome == null) //don't want to block here
    {
      synchronized(SupplierPrefOrderHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(supplierPrefOrderHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.supplier.SupplierPrefOrderHome");
            supplierPrefOrderHome = (SupplierPrefOrderHome)MyNarrow.narrow(homeObject, SupplierPrefOrderHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("supplierPrefOrder home obtained " + supplierPrefOrderHome);
        }
      }
    }
    return supplierPrefOrderHome;
  }




  /** Remove the SupplierPrefOrder corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    SupplierPrefOrder supplierPrefOrder = findByPrimaryKey(primaryKey);
    try
    {
      if(supplierPrefOrder != null)
      {
        supplierPrefOrder.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a SupplierPrefOrder by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SupplierPrefOrder corresponding to the primaryKey
   */
  public static SupplierPrefOrder findByPrimaryKey(java.lang.String primaryKey)
  {
    SupplierPrefOrder supplierPrefOrder = null;
    Debug.logInfo("SupplierPrefOrderHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      supplierPrefOrder = (SupplierPrefOrder)MyNarrow.narrow(getSupplierPrefOrderHome().findByPrimaryKey(primaryKey), SupplierPrefOrder.class);
      if(supplierPrefOrder != null)
      {
        supplierPrefOrder = supplierPrefOrder.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return supplierPrefOrder;
  }

  /** Finds all SupplierPrefOrder entities
   *@return    Collection containing all SupplierPrefOrder entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SupplierPrefOrderHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSupplierPrefOrderHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SupplierPrefOrder
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SupplierPrefOrder create(String supplierPrefOrderId, String description)
  {
    SupplierPrefOrder supplierPrefOrder = null;
    Debug.logInfo("SupplierPrefOrderHelper.create: supplierPrefOrderId: " + supplierPrefOrderId);
    if(supplierPrefOrderId == null) { return null; }

    try { supplierPrefOrder = (SupplierPrefOrder)MyNarrow.narrow(getSupplierPrefOrderHome().create(supplierPrefOrderId, description), SupplierPrefOrder.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create supplierPrefOrder with supplierPrefOrderId: " + supplierPrefOrderId);
      Debug.logError(ce);
      supplierPrefOrder = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return supplierPrefOrder;
  }

  /** Updates the corresponding SupplierPrefOrder
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SupplierPrefOrder update(String supplierPrefOrderId, String description) throws java.rmi.RemoteException
  {
    if(supplierPrefOrderId == null) { return null; }
    SupplierPrefOrder supplierPrefOrder = findByPrimaryKey(supplierPrefOrderId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SupplierPrefOrder supplierPrefOrderValue = new SupplierPrefOrderValue();

    if(description != null) { supplierPrefOrderValue.setDescription(description); }

    supplierPrefOrder.setValueObject(supplierPrefOrderValue);
    return supplierPrefOrder;
  }


}
