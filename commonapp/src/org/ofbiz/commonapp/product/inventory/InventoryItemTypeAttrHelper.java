
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Inventory Item Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the InventoryItemTypeAttr Entity EJB; acts as a proxy for the Home interface
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
public class InventoryItemTypeAttrHelper
{

  /** A static variable to cache the Home object for the InventoryItemTypeAttr EJB */
  private static InventoryItemTypeAttrHome inventoryItemTypeAttrHome = null;

  /** Initializes the inventoryItemTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The InventoryItemTypeAttrHome instance for the default EJB server
   */
  public static InventoryItemTypeAttrHome getInventoryItemTypeAttrHome()
  {
    if(inventoryItemTypeAttrHome == null) //don't want to block here
    {
      synchronized(InventoryItemTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(inventoryItemTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.InventoryItemTypeAttrHome");
            inventoryItemTypeAttrHome = (InventoryItemTypeAttrHome)MyNarrow.narrow(homeObject, InventoryItemTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("inventoryItemTypeAttr home obtained " + inventoryItemTypeAttrHome);
        }
      }
    }
    return inventoryItemTypeAttrHome;
  }



  /** Remove the InventoryItemTypeAttr corresponding to the primaryKey specified by fields
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String inventoryItemTypeId, String name)
  {
    if(inventoryItemTypeId == null || name == null)
    {
      return;
    }
    InventoryItemTypeAttrPK primaryKey = new InventoryItemTypeAttrPK(inventoryItemTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the InventoryItemTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.inventory.InventoryItemTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    InventoryItemTypeAttr inventoryItemTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(inventoryItemTypeAttr != null)
      {
        inventoryItemTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a InventoryItemTypeAttr by its Primary Key, specified by individual fields
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The InventoryItemTypeAttr corresponding to the primaryKey
   */
  public static InventoryItemTypeAttr findByPrimaryKey(String inventoryItemTypeId, String name)
  {
    if(inventoryItemTypeId == null || name == null) return null;
    InventoryItemTypeAttrPK primaryKey = new InventoryItemTypeAttrPK(inventoryItemTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a InventoryItemTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The InventoryItemTypeAttr corresponding to the primaryKey
   */
  public static InventoryItemTypeAttr findByPrimaryKey(org.ofbiz.commonapp.product.inventory.InventoryItemTypeAttrPK primaryKey)
  {
    InventoryItemTypeAttr inventoryItemTypeAttr = null;
    Debug.logInfo("InventoryItemTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      inventoryItemTypeAttr = (InventoryItemTypeAttr)MyNarrow.narrow(getInventoryItemTypeAttrHome().findByPrimaryKey(primaryKey), InventoryItemTypeAttr.class);
      if(inventoryItemTypeAttr != null)
      {
        inventoryItemTypeAttr = inventoryItemTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemTypeAttr;
  }

  /** Finds all InventoryItemTypeAttr entities
   *@return    Collection containing all InventoryItemTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("InventoryItemTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getInventoryItemTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a InventoryItemTypeAttr
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemTypeAttr create(String inventoryItemTypeId, String name)
  {
    InventoryItemTypeAttr inventoryItemTypeAttr = null;
    Debug.logInfo("InventoryItemTypeAttrHelper.create: inventoryItemTypeId, name: " + inventoryItemTypeId + ", " + name);
    if(inventoryItemTypeId == null || name == null) { return null; }

    try { inventoryItemTypeAttr = (InventoryItemTypeAttr)MyNarrow.narrow(getInventoryItemTypeAttrHome().create(inventoryItemTypeId, name), InventoryItemTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create inventoryItemTypeAttr with inventoryItemTypeId, name: " + inventoryItemTypeId + ", " + name);
      Debug.logError(ce);
      inventoryItemTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemTypeAttr;
  }

  /** Updates the corresponding InventoryItemTypeAttr
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemTypeAttr update(String inventoryItemTypeId, String name) throws java.rmi.RemoteException
  {
    if(inventoryItemTypeId == null || name == null) { return null; }
    InventoryItemTypeAttr inventoryItemTypeAttr = findByPrimaryKey(inventoryItemTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    InventoryItemTypeAttr inventoryItemTypeAttrValue = new InventoryItemTypeAttrValue();


    inventoryItemTypeAttr.setValueObject(inventoryItemTypeAttrValue);
    return inventoryItemTypeAttr;
  }

  /** Removes/deletes the specified  InventoryItemTypeAttr
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
        InventoryItemTypeAttr inventoryItemTypeAttr = (InventoryItemTypeAttr) iterator.next();
        Debug.logInfo("Removing inventoryItemTypeAttr with inventoryItemTypeId:" + inventoryItemTypeId);
        inventoryItemTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemTypeAttr records by the following parameters:
   *@param  inventoryItemTypeId                  Field of the INVENTORY_ITEM_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByInventoryItemTypeId(String inventoryItemTypeId)
  {
    Debug.logInfo("findByInventoryItemTypeId: inventoryItemTypeId:" + inventoryItemTypeId);

    Collection collection = null;
    if(inventoryItemTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemTypeAttrHome().findByInventoryItemTypeId(inventoryItemTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItemTypeAttr
   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null) return;
    Iterator iterator = UtilMisc.toIterator(findByName(name));

    while(iterator.hasNext())
    {
      try
      {
        InventoryItemTypeAttr inventoryItemTypeAttr = (InventoryItemTypeAttr) iterator.next();
        Debug.logInfo("Removing inventoryItemTypeAttr with name:" + name);
        inventoryItemTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
