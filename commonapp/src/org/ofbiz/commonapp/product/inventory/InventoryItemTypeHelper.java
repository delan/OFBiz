
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Inventory Item Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the InventoryItemType Entity EJB; acts as a proxy for the Home interface
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
public class InventoryItemTypeHelper
{

  /** A static variable to cache the Home object for the InventoryItemType EJB */
  private static InventoryItemTypeHome inventoryItemTypeHome = null;

  /** Initializes the inventoryItemTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The InventoryItemTypeHome instance for the default EJB server
   */
  public static InventoryItemTypeHome getInventoryItemTypeHome()
  {
    if(inventoryItemTypeHome == null) //don't want to block here
    {
      synchronized(InventoryItemTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(inventoryItemTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.InventoryItemTypeHome");
            inventoryItemTypeHome = (InventoryItemTypeHome)MyNarrow.narrow(homeObject, InventoryItemTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("inventoryItemType home obtained " + inventoryItemTypeHome);
        }
      }
    }
    return inventoryItemTypeHome;
  }




  /** Remove the InventoryItemType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    InventoryItemType inventoryItemType = findByPrimaryKey(primaryKey);
    try
    {
      if(inventoryItemType != null)
      {
        inventoryItemType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a InventoryItemType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The InventoryItemType corresponding to the primaryKey
   */
  public static InventoryItemType findByPrimaryKey(java.lang.String primaryKey)
  {
    InventoryItemType inventoryItemType = null;
    Debug.logInfo("InventoryItemTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      inventoryItemType = (InventoryItemType)MyNarrow.narrow(getInventoryItemTypeHome().findByPrimaryKey(primaryKey), InventoryItemType.class);
      if(inventoryItemType != null)
      {
        inventoryItemType = inventoryItemType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemType;
  }

  /** Finds all InventoryItemType entities
   *@return    Collection containing all InventoryItemType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("InventoryItemTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getInventoryItemTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a InventoryItemType
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemType create(String inventoryItemTypeId, String parentTypeId, String hasTable, String description)
  {
    InventoryItemType inventoryItemType = null;
    Debug.logInfo("InventoryItemTypeHelper.create: inventoryItemTypeId: " + inventoryItemTypeId);
    if(inventoryItemTypeId == null) { return null; }

    try { inventoryItemType = (InventoryItemType)MyNarrow.narrow(getInventoryItemTypeHome().create(inventoryItemTypeId, parentTypeId, hasTable, description), InventoryItemType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create inventoryItemType with inventoryItemTypeId: " + inventoryItemTypeId);
      Debug.logError(ce);
      inventoryItemType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemType;
  }

  /** Updates the corresponding InventoryItemType
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemType update(String inventoryItemTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(inventoryItemTypeId == null) { return null; }
    InventoryItemType inventoryItemType = findByPrimaryKey(inventoryItemTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    InventoryItemType inventoryItemTypeValue = new InventoryItemTypeValue();

    if(parentTypeId != null) { inventoryItemTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { inventoryItemTypeValue.setHasTable(hasTable); }
    if(description != null) { inventoryItemTypeValue.setDescription(description); }

    inventoryItemType.setValueObject(inventoryItemTypeValue);
    return inventoryItemType;
  }

  /** Removes/deletes the specified  InventoryItemType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentTypeId(parentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItemType inventoryItemType = (InventoryItemType) iterator.next();
        Debug.logInfo("Removing inventoryItemType with parentTypeId:" + parentTypeId);
        inventoryItemType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItemType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = UtilMisc.toIterator(findByHasTable(hasTable));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItemType inventoryItemType = (InventoryItemType) iterator.next();
        Debug.logInfo("Removing inventoryItemType with hasTable:" + hasTable);
        inventoryItemType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
