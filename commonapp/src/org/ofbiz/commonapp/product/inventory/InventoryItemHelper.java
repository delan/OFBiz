
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Inventory Item Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the InventoryItem Entity EJB; acts as a proxy for the Home interface
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
public class InventoryItemHelper
{

  /** A static variable to cache the Home object for the InventoryItem EJB */
  private static InventoryItemHome inventoryItemHome = null;

  /** Initializes the inventoryItemHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The InventoryItemHome instance for the default EJB server
   */
  public static InventoryItemHome getInventoryItemHome()
  {
    if(inventoryItemHome == null) //don't want to block here
    {
      synchronized(InventoryItemHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(inventoryItemHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.InventoryItemHome");
            inventoryItemHome = (InventoryItemHome)MyNarrow.narrow(homeObject, InventoryItemHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("inventoryItem home obtained " + inventoryItemHome);
        }
      }
    }
    return inventoryItemHome;
  }




  /** Remove the InventoryItem corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    InventoryItem inventoryItem = findByPrimaryKey(primaryKey);
    try
    {
      if(inventoryItem != null)
      {
        inventoryItem.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a InventoryItem by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The InventoryItem corresponding to the primaryKey
   */
  public static InventoryItem findByPrimaryKey(java.lang.String primaryKey)
  {
    InventoryItem inventoryItem = null;
    Debug.logInfo("InventoryItemHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      inventoryItem = (InventoryItem)MyNarrow.narrow(getInventoryItemHome().findByPrimaryKey(primaryKey), InventoryItem.class);
      if(inventoryItem != null)
      {
        inventoryItem = inventoryItem.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItem;
  }

  /** Finds all InventoryItem entities
   *@return    Collection containing all InventoryItem entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("InventoryItemHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getInventoryItemHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a InventoryItem
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@param  lotId                  Field of the LOT_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  quantityOnHand                  Field of the QUANTITY_ON_HAND column.
   *@param  serialNumber                  Field of the SERIAL_NUMBER column.
   *@return                Description of the Returned Value
   */
  public static InventoryItem create(String inventoryItemId, String inventoryItemTypeId, String productId, String partyId, String statusTypeId, String facilityId, String containerId, String lotId, String uomId, Double quantityOnHand, String serialNumber)
  {
    InventoryItem inventoryItem = null;
    Debug.logInfo("InventoryItemHelper.create: inventoryItemId: " + inventoryItemId);
    if(inventoryItemId == null) { return null; }

    try { inventoryItem = (InventoryItem)MyNarrow.narrow(getInventoryItemHome().create(inventoryItemId, inventoryItemTypeId, productId, partyId, statusTypeId, facilityId, containerId, lotId, uomId, quantityOnHand, serialNumber), InventoryItem.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create inventoryItem with inventoryItemId: " + inventoryItemId);
      Debug.logError(ce);
      inventoryItem = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItem;
  }

  /** Updates the corresponding InventoryItem
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@param  lotId                  Field of the LOT_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  quantityOnHand                  Field of the QUANTITY_ON_HAND column.
   *@param  serialNumber                  Field of the SERIAL_NUMBER column.
   *@return                Description of the Returned Value
   */
  public static InventoryItem update(String inventoryItemId, String inventoryItemTypeId, String productId, String partyId, String statusTypeId, String facilityId, String containerId, String lotId, String uomId, Double quantityOnHand, String serialNumber) throws java.rmi.RemoteException
  {
    if(inventoryItemId == null) { return null; }
    InventoryItem inventoryItem = findByPrimaryKey(inventoryItemId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    InventoryItem inventoryItemValue = new InventoryItemValue();

    if(inventoryItemTypeId != null) { inventoryItemValue.setInventoryItemTypeId(inventoryItemTypeId); }
    if(productId != null) { inventoryItemValue.setProductId(productId); }
    if(partyId != null) { inventoryItemValue.setPartyId(partyId); }
    if(statusTypeId != null) { inventoryItemValue.setStatusTypeId(statusTypeId); }
    if(facilityId != null) { inventoryItemValue.setFacilityId(facilityId); }
    if(containerId != null) { inventoryItemValue.setContainerId(containerId); }
    if(lotId != null) { inventoryItemValue.setLotId(lotId); }
    if(uomId != null) { inventoryItemValue.setUomId(uomId); }
    if(quantityOnHand != null) { inventoryItemValue.setQuantityOnHand(quantityOnHand); }
    if(serialNumber != null) { inventoryItemValue.setSerialNumber(serialNumber); }

    inventoryItem.setValueObject(inventoryItemValue);
    return inventoryItem;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   */
  public static void removeByInventoryItemTypeId(String inventoryItemTypeId)
  {
    if(inventoryItemTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByInventoryItemTypeId(inventoryItemTypeId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with inventoryItemTypeId:" + inventoryItemTypeId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByInventoryItemTypeId(String inventoryItemTypeId)
  {
    Debug.logInfo("findByInventoryItemTypeId: inventoryItemTypeId:" + inventoryItemTypeId);

    Collection collection = null;
    if(inventoryItemTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByInventoryItemTypeId(inventoryItemTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  productId                  Field of the PRODUCT_ID column.
   */
  public static void removeByProductId(String productId)
  {
    if(productId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByProductId(productId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with productId:" + productId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByProductId(String productId)
  {
    Debug.logInfo("findByProductId: productId:" + productId);

    Collection collection = null;
    if(productId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByProductId(productId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
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
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with partyId:" + partyId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   */
  public static void removeByStatusTypeId(String statusTypeId)
  {
    if(statusTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByStatusTypeId(statusTypeId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with statusTypeId:" + statusTypeId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByStatusTypeId(String statusTypeId)
  {
    Debug.logInfo("findByStatusTypeId: statusTypeId:" + statusTypeId);

    Collection collection = null;
    if(statusTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByStatusTypeId(statusTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  facilityId                  Field of the FACILITY_ID column.
   */
  public static void removeByFacilityId(String facilityId)
  {
    if(facilityId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByFacilityId(facilityId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with facilityId:" + facilityId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFacilityId(String facilityId)
  {
    Debug.logInfo("findByFacilityId: facilityId:" + facilityId);

    Collection collection = null;
    if(facilityId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByFacilityId(facilityId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  containerId                  Field of the CONTAINER_ID column.
   */
  public static void removeByContainerId(String containerId)
  {
    if(containerId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByContainerId(containerId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with containerId:" + containerId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  containerId                  Field of the CONTAINER_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByContainerId(String containerId)
  {
    Debug.logInfo("findByContainerId: containerId:" + containerId);

    Collection collection = null;
    if(containerId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByContainerId(containerId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  lotId                  Field of the LOT_ID column.
   */
  public static void removeByLotId(String lotId)
  {
    if(lotId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByLotId(lotId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with lotId:" + lotId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  lotId                  Field of the LOT_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByLotId(String lotId)
  {
    Debug.logInfo("findByLotId: lotId:" + lotId);

    Collection collection = null;
    if(lotId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByLotId(lotId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItem
   *@param  uomId                  Field of the UOM_ID column.
   */
  public static void removeByUomId(String uomId)
  {
    if(uomId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUomId(uomId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItem inventoryItem = (InventoryItem) iterator.next();
        Debug.logInfo("Removing inventoryItem with uomId:" + uomId);
        inventoryItem.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItem records by the following parameters:
   *@param  uomId                  Field of the UOM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomId(String uomId)
  {
    Debug.logInfo("findByUomId: uomId:" + uomId);

    Collection collection = null;
    if(uomId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemHome().findByUomId(uomId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
