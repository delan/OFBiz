
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Physical Inventory Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PhysicalInventory Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:31 MDT 2001
 *@version    1.0
 */
public class PhysicalInventoryHelper
{

  /** A static variable to cache the Home object for the PhysicalInventory EJB */
  private static PhysicalInventoryHome physicalInventoryHome = null;

  /** Initializes the physicalInventoryHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PhysicalInventoryHome instance for the default EJB server
   */
  public static PhysicalInventoryHome getPhysicalInventoryHome()
  {
    if(physicalInventoryHome == null) //don't want to block here
    {
      synchronized(PhysicalInventoryHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(physicalInventoryHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.PhysicalInventoryHome");
            physicalInventoryHome = (PhysicalInventoryHome)MyNarrow.narrow(homeObject, PhysicalInventoryHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("physicalInventory home obtained " + physicalInventoryHome);
        }
      }
    }
    return physicalInventoryHome;
  }




  /** Remove the PhysicalInventory corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    PhysicalInventory physicalInventory = findByPrimaryKey(primaryKey);
    try
    {
      if(physicalInventory != null)
      {
        physicalInventory.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a PhysicalInventory by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PhysicalInventory corresponding to the primaryKey
   */
  public static PhysicalInventory findByPrimaryKey(java.lang.String primaryKey)
  {
    PhysicalInventory physicalInventory = null;
    Debug.logInfo("PhysicalInventoryHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      physicalInventory = (PhysicalInventory)MyNarrow.narrow(getPhysicalInventoryHome().findByPrimaryKey(primaryKey), PhysicalInventory.class);
      if(physicalInventory != null)
      {
        physicalInventory = physicalInventory.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return physicalInventory;
  }

  /** Finds all PhysicalInventory entities
   *@return    Collection containing all PhysicalInventory entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PhysicalInventoryHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPhysicalInventoryHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PhysicalInventory
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  date                  Field of the DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static PhysicalInventory create(String physicalInventoryId, java.util.Date date, String partyId, String comment)
  {
    PhysicalInventory physicalInventory = null;
    Debug.logInfo("PhysicalInventoryHelper.create: physicalInventoryId: " + physicalInventoryId);
    if(physicalInventoryId == null) { return null; }

    try { physicalInventory = (PhysicalInventory)MyNarrow.narrow(getPhysicalInventoryHome().create(physicalInventoryId, date, partyId, comment), PhysicalInventory.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create physicalInventory with physicalInventoryId: " + physicalInventoryId);
      Debug.logError(ce);
      physicalInventory = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return physicalInventory;
  }

  /** Updates the corresponding PhysicalInventory
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  date                  Field of the DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static PhysicalInventory update(String physicalInventoryId, java.util.Date date, String partyId, String comment) throws java.rmi.RemoteException
  {
    if(physicalInventoryId == null) { return null; }
    PhysicalInventory physicalInventory = findByPrimaryKey(physicalInventoryId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PhysicalInventory physicalInventoryValue = new PhysicalInventoryValue();

    if(date != null) { physicalInventoryValue.setDate(date); }
    if(partyId != null) { physicalInventoryValue.setPartyId(partyId); }
    if(comment != null) { physicalInventoryValue.setComment(comment); }

    physicalInventory.setValueObject(physicalInventoryValue);
    return physicalInventory;
  }

  /** Removes/deletes the specified  PhysicalInventory
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyId(partyId));

    while(iterator.hasNext())
    {
      try
      {
        PhysicalInventory physicalInventory = (PhysicalInventory) iterator.next();
        Debug.logInfo("Removing physicalInventory with partyId:" + partyId);
        physicalInventory.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PhysicalInventory records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPhysicalInventoryHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PhysicalInventory
   *@param  date                  Field of the DATE column.
   */
  public static void removeByDate(java.util.Date date)
  {
    if(date == null) return;
    Iterator iterator = UtilMisc.toIterator(findByDate(date));

    while(iterator.hasNext())
    {
      try
      {
        PhysicalInventory physicalInventory = (PhysicalInventory) iterator.next();
        Debug.logInfo("Removing physicalInventory with date:" + date);
        physicalInventory.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PhysicalInventory records by the following parameters:
   *@param  date                  Field of the DATE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByDate(java.util.Date date)
  {
    Debug.logInfo("findByDate: date:" + date);

    Collection collection = null;
    if(date == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPhysicalInventoryHome().findByDate(date), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
