
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Inventory Item Variance Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the InventoryItemVariance Entity EJB; acts as a proxy for the Home interface
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
public class InventoryItemVarianceHelper
{

  /** A static variable to cache the Home object for the InventoryItemVariance EJB */
  private static InventoryItemVarianceHome inventoryItemVarianceHome = null;

  /** Initializes the inventoryItemVarianceHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The InventoryItemVarianceHome instance for the default EJB server
   */
  public static InventoryItemVarianceHome getInventoryItemVarianceHome()
  {
    if(inventoryItemVarianceHome == null) //don't want to block here
    {
      synchronized(InventoryItemVarianceHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(inventoryItemVarianceHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.InventoryItemVarianceHome");
            inventoryItemVarianceHome = (InventoryItemVarianceHome)MyNarrow.narrow(homeObject, InventoryItemVarianceHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("inventoryItemVariance home obtained " + inventoryItemVarianceHome);
        }
      }
    }
    return inventoryItemVarianceHome;
  }



  /** Remove the InventoryItemVariance corresponding to the primaryKey specified by fields
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   */
  public static void removeByPrimaryKey(String inventoryItemId, String physicalInventoryId)
  {
    if(inventoryItemId == null || physicalInventoryId == null)
    {
      return;
    }
    InventoryItemVariancePK primaryKey = new InventoryItemVariancePK(inventoryItemId, physicalInventoryId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the InventoryItemVariance corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.inventory.InventoryItemVariancePK primaryKey)
  {
    if(primaryKey == null) return;
    InventoryItemVariance inventoryItemVariance = findByPrimaryKey(primaryKey);
    try
    {
      if(inventoryItemVariance != null)
      {
        inventoryItemVariance.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a InventoryItemVariance by its Primary Key, specified by individual fields
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return       The InventoryItemVariance corresponding to the primaryKey
   */
  public static InventoryItemVariance findByPrimaryKey(String inventoryItemId, String physicalInventoryId)
  {
    if(inventoryItemId == null || physicalInventoryId == null) return null;
    InventoryItemVariancePK primaryKey = new InventoryItemVariancePK(inventoryItemId, physicalInventoryId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a InventoryItemVariance by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The InventoryItemVariance corresponding to the primaryKey
   */
  public static InventoryItemVariance findByPrimaryKey(org.ofbiz.commonapp.product.inventory.InventoryItemVariancePK primaryKey)
  {
    InventoryItemVariance inventoryItemVariance = null;
    Debug.logInfo("InventoryItemVarianceHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      inventoryItemVariance = (InventoryItemVariance)MyNarrow.narrow(getInventoryItemVarianceHome().findByPrimaryKey(primaryKey), InventoryItemVariance.class);
      if(inventoryItemVariance != null)
      {
        inventoryItemVariance = inventoryItemVariance.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemVariance;
  }

  /** Finds all InventoryItemVariance entities
   *@return    Collection containing all InventoryItemVariance entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("InventoryItemVarianceHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getInventoryItemVarianceHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a InventoryItemVariance
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemVariance create(String inventoryItemId, String physicalInventoryId, String varianceReasonId, Double quantity, String comment)
  {
    InventoryItemVariance inventoryItemVariance = null;
    Debug.logInfo("InventoryItemVarianceHelper.create: inventoryItemId, physicalInventoryId: " + inventoryItemId + ", " + physicalInventoryId);
    if(inventoryItemId == null || physicalInventoryId == null) { return null; }

    try { inventoryItemVariance = (InventoryItemVariance)MyNarrow.narrow(getInventoryItemVarianceHome().create(inventoryItemId, physicalInventoryId, varianceReasonId, quantity, comment), InventoryItemVariance.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create inventoryItemVariance with inventoryItemId, physicalInventoryId: " + inventoryItemId + ", " + physicalInventoryId);
      Debug.logError(ce);
      inventoryItemVariance = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemVariance;
  }

  /** Updates the corresponding InventoryItemVariance
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  comment                  Field of the COMMENT column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemVariance update(String inventoryItemId, String physicalInventoryId, String varianceReasonId, Double quantity, String comment) throws java.rmi.RemoteException
  {
    if(inventoryItemId == null || physicalInventoryId == null) { return null; }
    InventoryItemVariance inventoryItemVariance = findByPrimaryKey(inventoryItemId, physicalInventoryId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    InventoryItemVariance inventoryItemVarianceValue = new InventoryItemVarianceValue();

    if(varianceReasonId != null) { inventoryItemVarianceValue.setVarianceReasonId(varianceReasonId); }
    if(quantity != null) { inventoryItemVarianceValue.setQuantity(quantity); }
    if(comment != null) { inventoryItemVarianceValue.setComment(comment); }

    inventoryItemVariance.setValueObject(inventoryItemVarianceValue);
    return inventoryItemVariance;
  }

  /** Removes/deletes the specified  InventoryItemVariance
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   */
  public static void removeByInventoryItemId(String inventoryItemId)
  {
    if(inventoryItemId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByInventoryItemId(inventoryItemId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItemVariance inventoryItemVariance = (InventoryItemVariance) iterator.next();
        Debug.logInfo("Removing inventoryItemVariance with inventoryItemId:" + inventoryItemId);
        inventoryItemVariance.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemVariance records by the following parameters:
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByInventoryItemId(String inventoryItemId)
  {
    Debug.logInfo("findByInventoryItemId: inventoryItemId:" + inventoryItemId);

    Collection collection = null;
    if(inventoryItemId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemVarianceHome().findByInventoryItemId(inventoryItemId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItemVariance
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   */
  public static void removeByPhysicalInventoryId(String physicalInventoryId)
  {
    if(physicalInventoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPhysicalInventoryId(physicalInventoryId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItemVariance inventoryItemVariance = (InventoryItemVariance) iterator.next();
        Debug.logInfo("Removing inventoryItemVariance with physicalInventoryId:" + physicalInventoryId);
        inventoryItemVariance.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemVariance records by the following parameters:
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPhysicalInventoryId(String physicalInventoryId)
  {
    Debug.logInfo("findByPhysicalInventoryId: physicalInventoryId:" + physicalInventoryId);

    Collection collection = null;
    if(physicalInventoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemVarianceHome().findByPhysicalInventoryId(physicalInventoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItemVariance
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   */
  public static void removeByVarianceReasonId(String varianceReasonId)
  {
    if(varianceReasonId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByVarianceReasonId(varianceReasonId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItemVariance inventoryItemVariance = (InventoryItemVariance) iterator.next();
        Debug.logInfo("Removing inventoryItemVariance with varianceReasonId:" + varianceReasonId);
        inventoryItemVariance.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemVariance records by the following parameters:
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByVarianceReasonId(String varianceReasonId)
  {
    Debug.logInfo("findByVarianceReasonId: varianceReasonId:" + varianceReasonId);

    Collection collection = null;
    if(varianceReasonId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemVarianceHome().findByVarianceReasonId(varianceReasonId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
