
package org.ofbiz.commonapp.product.price;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Sale Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the SaleType Entity EJB; acts as a proxy for the Home interface
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
public class SaleTypeHelper
{

  /** A static variable to cache the Home object for the SaleType EJB */
  private static SaleTypeHome saleTypeHome = null;

  /** Initializes the saleTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SaleTypeHome instance for the default EJB server
   */
  public static SaleTypeHome getSaleTypeHome()
  {
    if(saleTypeHome == null) //don't want to block here
    {
      synchronized(SaleTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(saleTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.price.SaleTypeHome");
            saleTypeHome = (SaleTypeHome)MyNarrow.narrow(homeObject, SaleTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("saleType home obtained " + saleTypeHome);
        }
      }
    }
    return saleTypeHome;
  }




  /** Remove the SaleType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    SaleType saleType = findByPrimaryKey(primaryKey);
    try
    {
      if(saleType != null)
      {
        saleType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a SaleType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SaleType corresponding to the primaryKey
   */
  public static SaleType findByPrimaryKey(java.lang.String primaryKey)
  {
    SaleType saleType = null;
    Debug.logInfo("SaleTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      saleType = (SaleType)MyNarrow.narrow(getSaleTypeHome().findByPrimaryKey(primaryKey), SaleType.class);
      if(saleType != null)
      {
        saleType = saleType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return saleType;
  }

  /** Finds all SaleType entities
   *@return    Collection containing all SaleType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SaleTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSaleTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SaleType
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SaleType create(String saleTypeId, String description)
  {
    SaleType saleType = null;
    Debug.logInfo("SaleTypeHelper.create: saleTypeId: " + saleTypeId);
    if(saleTypeId == null) { return null; }

    try { saleType = (SaleType)MyNarrow.narrow(getSaleTypeHome().create(saleTypeId, description), SaleType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create saleType with saleTypeId: " + saleTypeId);
      Debug.logError(ce);
      saleType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return saleType;
  }

  /** Updates the corresponding SaleType
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SaleType update(String saleTypeId, String description) throws java.rmi.RemoteException
  {
    if(saleTypeId == null) { return null; }
    SaleType saleType = findByPrimaryKey(saleTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SaleType saleTypeValue = new SaleTypeValue();

    if(description != null) { saleTypeValue.setDescription(description); }

    saleType.setValueObject(saleTypeValue);
    return saleType;
  }


}
