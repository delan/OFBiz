
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Item Variance Accounting Transaction Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the ItemVarianceAcctgTrans Entity EJB; acts as a proxy for the Home interface
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
public class ItemVarianceAcctgTransHelper
{

  /** A static variable to cache the Home object for the ItemVarianceAcctgTrans EJB */
  private static ItemVarianceAcctgTransHome itemVarianceAcctgTransHome = null;

  /** Initializes the itemVarianceAcctgTransHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The ItemVarianceAcctgTransHome instance for the default EJB server
   */
  public static ItemVarianceAcctgTransHome getItemVarianceAcctgTransHome()
  {
    if(itemVarianceAcctgTransHome == null) //don't want to block here
    {
      synchronized(ItemVarianceAcctgTransHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(itemVarianceAcctgTransHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.ItemVarianceAcctgTransHome");
            itemVarianceAcctgTransHome = (ItemVarianceAcctgTransHome)MyNarrow.narrow(homeObject, ItemVarianceAcctgTransHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("itemVarianceAcctgTrans home obtained " + itemVarianceAcctgTransHome);
        }
      }
    }
    return itemVarianceAcctgTransHome;
  }




  /** Remove the ItemVarianceAcctgTrans corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    ItemVarianceAcctgTrans itemVarianceAcctgTrans = findByPrimaryKey(primaryKey);
    try
    {
      if(itemVarianceAcctgTrans != null)
      {
        itemVarianceAcctgTrans.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a ItemVarianceAcctgTrans by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The ItemVarianceAcctgTrans corresponding to the primaryKey
   */
  public static ItemVarianceAcctgTrans findByPrimaryKey(java.lang.String primaryKey)
  {
    ItemVarianceAcctgTrans itemVarianceAcctgTrans = null;
    Debug.logInfo("ItemVarianceAcctgTransHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      itemVarianceAcctgTrans = (ItemVarianceAcctgTrans)MyNarrow.narrow(getItemVarianceAcctgTransHome().findByPrimaryKey(primaryKey), ItemVarianceAcctgTrans.class);
      if(itemVarianceAcctgTrans != null)
      {
        itemVarianceAcctgTrans = itemVarianceAcctgTrans.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return itemVarianceAcctgTrans;
  }

  /** Finds all ItemVarianceAcctgTrans entities
   *@return    Collection containing all ItemVarianceAcctgTrans entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("ItemVarianceAcctgTransHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getItemVarianceAcctgTransHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a ItemVarianceAcctgTrans
   *@param  acctgTransId                  Field of the ACCTG_TRANS_ID column.
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return                Description of the Returned Value
   */
  public static ItemVarianceAcctgTrans create(String acctgTransId, String inventoryItemId, String physicalInventoryId)
  {
    ItemVarianceAcctgTrans itemVarianceAcctgTrans = null;
    Debug.logInfo("ItemVarianceAcctgTransHelper.create: acctgTransId: " + acctgTransId);
    if(acctgTransId == null) { return null; }

    try { itemVarianceAcctgTrans = (ItemVarianceAcctgTrans)MyNarrow.narrow(getItemVarianceAcctgTransHome().create(acctgTransId, inventoryItemId, physicalInventoryId), ItemVarianceAcctgTrans.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create itemVarianceAcctgTrans with acctgTransId: " + acctgTransId);
      Debug.logError(ce);
      itemVarianceAcctgTrans = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return itemVarianceAcctgTrans;
  }

  /** Updates the corresponding ItemVarianceAcctgTrans
   *@param  acctgTransId                  Field of the ACCTG_TRANS_ID column.
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return                Description of the Returned Value
   */
  public static ItemVarianceAcctgTrans update(String acctgTransId, String inventoryItemId, String physicalInventoryId) throws java.rmi.RemoteException
  {
    if(acctgTransId == null) { return null; }
    ItemVarianceAcctgTrans itemVarianceAcctgTrans = findByPrimaryKey(acctgTransId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    ItemVarianceAcctgTrans itemVarianceAcctgTransValue = new ItemVarianceAcctgTransValue();

    if(inventoryItemId != null) { itemVarianceAcctgTransValue.setInventoryItemId(inventoryItemId); }
    if(physicalInventoryId != null) { itemVarianceAcctgTransValue.setPhysicalInventoryId(physicalInventoryId); }

    itemVarianceAcctgTrans.setValueObject(itemVarianceAcctgTransValue);
    return itemVarianceAcctgTrans;
  }

  /** Removes/deletes the specified  ItemVarianceAcctgTrans
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
        ItemVarianceAcctgTrans itemVarianceAcctgTrans = (ItemVarianceAcctgTrans) iterator.next();
        Debug.logInfo("Removing itemVarianceAcctgTrans with inventoryItemId:" + inventoryItemId);
        itemVarianceAcctgTrans.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ItemVarianceAcctgTrans records by the following parameters:
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByInventoryItemId(String inventoryItemId)
  {
    Debug.logInfo("findByInventoryItemId: inventoryItemId:" + inventoryItemId);

    Collection collection = null;
    if(inventoryItemId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getItemVarianceAcctgTransHome().findByInventoryItemId(inventoryItemId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ItemVarianceAcctgTrans
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
        ItemVarianceAcctgTrans itemVarianceAcctgTrans = (ItemVarianceAcctgTrans) iterator.next();
        Debug.logInfo("Removing itemVarianceAcctgTrans with physicalInventoryId:" + physicalInventoryId);
        itemVarianceAcctgTrans.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ItemVarianceAcctgTrans records by the following parameters:
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPhysicalInventoryId(String physicalInventoryId)
  {
    Debug.logInfo("findByPhysicalInventoryId: physicalInventoryId:" + physicalInventoryId);

    Collection collection = null;
    if(physicalInventoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getItemVarianceAcctgTransHome().findByPhysicalInventoryId(physicalInventoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  ItemVarianceAcctgTrans
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   */
  public static void removeByInventoryItemIdAndPhysicalInventoryId(String inventoryItemId, String physicalInventoryId)
  {
    if(inventoryItemId == null || physicalInventoryId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByInventoryItemIdAndPhysicalInventoryId(inventoryItemId, physicalInventoryId));

    while(iterator.hasNext())
    {
      try
      {
        ItemVarianceAcctgTrans itemVarianceAcctgTrans = (ItemVarianceAcctgTrans) iterator.next();
        Debug.logInfo("Removing itemVarianceAcctgTrans with inventoryItemId, physicalInventoryId:" + inventoryItemId + ", " + physicalInventoryId);
        itemVarianceAcctgTrans.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds ItemVarianceAcctgTrans records by the following parameters:
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@param  physicalInventoryId                  Field of the PHYSICAL_INVENTORY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByInventoryItemIdAndPhysicalInventoryId(String inventoryItemId, String physicalInventoryId)
  {
    Debug.logInfo("findByInventoryItemIdAndPhysicalInventoryId: inventoryItemId, physicalInventoryId:" + inventoryItemId + ", " + physicalInventoryId);

    Collection collection = null;
    if(inventoryItemId == null || physicalInventoryId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getItemVarianceAcctgTransHome().findByInventoryItemIdAndPhysicalInventoryId(inventoryItemId, physicalInventoryId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
