
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Lot Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Lot Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */
public class LotHelper
{

  /** A static variable to cache the Home object for the Lot EJB */
  private static LotHome lotHome = null;

  /** Initializes the lotHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The LotHome instance for the default EJB server
   */
  public static LotHome getLotHome()
  {
    if(lotHome == null) //don't want to block here
    {
      synchronized(LotHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(lotHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.LotHome");
            lotHome = (LotHome)MyNarrow.narrow(homeObject, LotHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("lot home obtained " + lotHome);
        }
      }
    }
    return lotHome;
  }




  /** Remove the Lot corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Lot lot = findByPrimaryKey(primaryKey);
    try
    {
      if(lot != null)
      {
        lot.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Lot by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Lot corresponding to the primaryKey
   */
  public static Lot findByPrimaryKey(java.lang.String primaryKey)
  {
    Lot lot = null;
    Debug.logInfo("LotHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      lot = (Lot)MyNarrow.narrow(getLotHome().findByPrimaryKey(primaryKey), Lot.class);
      if(lot != null)
      {
        lot = lot.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return lot;
  }

  /** Finds all Lot entities
   *@return    Collection containing all Lot entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("LotHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getLotHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Lot
   *@param  lotId                  Field of the LOT_ID column.
   *@param  creationDate                  Field of the CREATION_DATE column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  expirationDate                  Field of the EXPIRATION_DATE column.
   *@return                Description of the Returned Value
   */
  public static Lot create(String lotId, java.util.Date creationDate, Double quantity, java.util.Date expirationDate)
  {
    Lot lot = null;
    Debug.logInfo("LotHelper.create: lotId: " + lotId);
    if(lotId == null) { return null; }

    try { lot = (Lot)MyNarrow.narrow(getLotHome().create(lotId, creationDate, quantity, expirationDate), Lot.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create lot with lotId: " + lotId);
      Debug.logError(ce);
      lot = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return lot;
  }

  /** Updates the corresponding Lot
   *@param  lotId                  Field of the LOT_ID column.
   *@param  creationDate                  Field of the CREATION_DATE column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  expirationDate                  Field of the EXPIRATION_DATE column.
   *@return                Description of the Returned Value
   */
  public static Lot update(String lotId, java.util.Date creationDate, Double quantity, java.util.Date expirationDate) throws java.rmi.RemoteException
  {
    if(lotId == null) { return null; }
    Lot lot = findByPrimaryKey(lotId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Lot lotValue = new LotValue();

    if(creationDate != null) { lotValue.setCreationDate(creationDate); }
    if(quantity != null) { lotValue.setQuantity(quantity); }
    if(expirationDate != null) { lotValue.setExpirationDate(expirationDate); }

    lot.setValueObject(lotValue);
    return lot;
  }


}
