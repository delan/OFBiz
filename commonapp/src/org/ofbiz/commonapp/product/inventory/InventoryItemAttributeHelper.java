
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Inventory Item Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the InventoryItemAttribute Entity EJB; acts as a proxy for the Home interface
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
public class InventoryItemAttributeHelper
{

  /** A static variable to cache the Home object for the InventoryItemAttribute EJB */
  private static InventoryItemAttributeHome inventoryItemAttributeHome = null;

  /** Initializes the inventoryItemAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The InventoryItemAttributeHome instance for the default EJB server
   */
  public static InventoryItemAttributeHome getInventoryItemAttributeHome()
  {
    if(inventoryItemAttributeHome == null) //don't want to block here
    {
      synchronized(InventoryItemAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(inventoryItemAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.InventoryItemAttributeHome");
            inventoryItemAttributeHome = (InventoryItemAttributeHome)MyNarrow.narrow(homeObject, InventoryItemAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("inventoryItemAttribute home obtained " + inventoryItemAttributeHome);
        }
      }
    }
    return inventoryItemAttributeHome;
  }



  /** Remove the InventoryItemAttribute corresponding to the primaryKey specified by fields
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String inventoryItemId, String name)
  {
    if(inventoryItemId == null || name == null)
    {
      return;
    }
    InventoryItemAttributePK primaryKey = new InventoryItemAttributePK(inventoryItemId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the InventoryItemAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.product.inventory.InventoryItemAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    InventoryItemAttribute inventoryItemAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(inventoryItemAttribute != null)
      {
        inventoryItemAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a InventoryItemAttribute by its Primary Key, specified by individual fields
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The InventoryItemAttribute corresponding to the primaryKey
   */
  public static InventoryItemAttribute findByPrimaryKey(String inventoryItemId, String name)
  {
    if(inventoryItemId == null || name == null) return null;
    InventoryItemAttributePK primaryKey = new InventoryItemAttributePK(inventoryItemId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a InventoryItemAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The InventoryItemAttribute corresponding to the primaryKey
   */
  public static InventoryItemAttribute findByPrimaryKey(org.ofbiz.commonapp.product.inventory.InventoryItemAttributePK primaryKey)
  {
    InventoryItemAttribute inventoryItemAttribute = null;
    Debug.logInfo("InventoryItemAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      inventoryItemAttribute = (InventoryItemAttribute)MyNarrow.narrow(getInventoryItemAttributeHome().findByPrimaryKey(primaryKey), InventoryItemAttribute.class);
      if(inventoryItemAttribute != null)
      {
        inventoryItemAttribute = inventoryItemAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemAttribute;
  }

  /** Finds all InventoryItemAttribute entities
   *@return    Collection containing all InventoryItemAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("InventoryItemAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getInventoryItemAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a InventoryItemAttribute
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemAttribute create(String inventoryItemId, String name, String value)
  {
    InventoryItemAttribute inventoryItemAttribute = null;
    Debug.logInfo("InventoryItemAttributeHelper.create: inventoryItemId, name: " + inventoryItemId + ", " + name);
    if(inventoryItemId == null || name == null) { return null; }

    try { inventoryItemAttribute = (InventoryItemAttribute)MyNarrow.narrow(getInventoryItemAttributeHome().create(inventoryItemId, name, value), InventoryItemAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create inventoryItemAttribute with inventoryItemId, name: " + inventoryItemId + ", " + name);
      Debug.logError(ce);
      inventoryItemAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return inventoryItemAttribute;
  }

  /** Updates the corresponding InventoryItemAttribute
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static InventoryItemAttribute update(String inventoryItemId, String name, String value) throws java.rmi.RemoteException
  {
    if(inventoryItemId == null || name == null) { return null; }
    InventoryItemAttribute inventoryItemAttribute = findByPrimaryKey(inventoryItemId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    InventoryItemAttribute inventoryItemAttributeValue = new InventoryItemAttributeValue();

    if(value != null) { inventoryItemAttributeValue.setValue(value); }

    inventoryItemAttribute.setValueObject(inventoryItemAttributeValue);
    return inventoryItemAttribute;
  }

  /** Removes/deletes the specified  InventoryItemAttribute
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
        InventoryItemAttribute inventoryItemAttribute = (InventoryItemAttribute) iterator.next();
        Debug.logInfo("Removing inventoryItemAttribute with inventoryItemId:" + inventoryItemId);
        inventoryItemAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemAttribute records by the following parameters:
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByInventoryItemId(String inventoryItemId)
  {
    Debug.logInfo("findByInventoryItemId: inventoryItemId:" + inventoryItemId);

    Collection collection = null;
    if(inventoryItemId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemAttributeHome().findByInventoryItemId(inventoryItemId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  InventoryItemAttribute
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
        InventoryItemAttribute inventoryItemAttribute = (InventoryItemAttribute) iterator.next();
        Debug.logInfo("Removing inventoryItemAttribute with name:" + name);
        inventoryItemAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds InventoryItemAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getInventoryItemAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
