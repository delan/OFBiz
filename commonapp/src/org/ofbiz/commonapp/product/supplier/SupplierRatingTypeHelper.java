
package org.ofbiz.commonapp.product.supplier;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Supplier Rating Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the SupplierRatingType Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */
public class SupplierRatingTypeHelper
{

  /** A static variable to cache the Home object for the SupplierRatingType EJB */
  private static SupplierRatingTypeHome supplierRatingTypeHome = null;

  /** Initializes the supplierRatingTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SupplierRatingTypeHome instance for the default EJB server
   */
  public static SupplierRatingTypeHome getSupplierRatingTypeHome()
  {
    if(supplierRatingTypeHome == null) //don't want to block here
    {
      synchronized(SupplierRatingTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(supplierRatingTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.supplier.SupplierRatingTypeHome");
            supplierRatingTypeHome = (SupplierRatingTypeHome)MyNarrow.narrow(homeObject, SupplierRatingTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("supplierRatingType home obtained " + supplierRatingTypeHome);
        }
      }
    }
    return supplierRatingTypeHome;
  }




  /** Remove the SupplierRatingType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    SupplierRatingType supplierRatingType = findByPrimaryKey(primaryKey);
    try
    {
      if(supplierRatingType != null)
      {
        supplierRatingType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a SupplierRatingType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SupplierRatingType corresponding to the primaryKey
   */
  public static SupplierRatingType findByPrimaryKey(java.lang.String primaryKey)
  {
    SupplierRatingType supplierRatingType = null;
    Debug.logInfo("SupplierRatingTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      supplierRatingType = (SupplierRatingType)MyNarrow.narrow(getSupplierRatingTypeHome().findByPrimaryKey(primaryKey), SupplierRatingType.class);
      if(supplierRatingType != null)
      {
        supplierRatingType = supplierRatingType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return supplierRatingType;
  }

  /** Finds all SupplierRatingType entities
   *@return    Collection containing all SupplierRatingType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SupplierRatingTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSupplierRatingTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SupplierRatingType
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SupplierRatingType create(String supplierRatingTypeId, String description)
  {
    SupplierRatingType supplierRatingType = null;
    Debug.logInfo("SupplierRatingTypeHelper.create: supplierRatingTypeId: " + supplierRatingTypeId);
    if(supplierRatingTypeId == null) { return null; }

    try { supplierRatingType = (SupplierRatingType)MyNarrow.narrow(getSupplierRatingTypeHome().create(supplierRatingTypeId, description), SupplierRatingType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create supplierRatingType with supplierRatingTypeId: " + supplierRatingTypeId);
      Debug.logError(ce);
      supplierRatingType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return supplierRatingType;
  }

  /** Updates the corresponding SupplierRatingType
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SupplierRatingType update(String supplierRatingTypeId, String description) throws java.rmi.RemoteException
  {
    if(supplierRatingTypeId == null) { return null; }
    SupplierRatingType supplierRatingType = findByPrimaryKey(supplierRatingTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SupplierRatingType supplierRatingTypeValue = new SupplierRatingTypeValue();

    if(description != null) { supplierRatingTypeValue.setDescription(description); }

    supplierRatingType.setValueObject(supplierRatingTypeValue);
    return supplierRatingType;
  }


}
