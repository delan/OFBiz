
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Inventory Item Entity
 * <p><b>Description:</b> None
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

public interface InventoryItemHome extends EJBHome
{

  public InventoryItem create(String inventoryItemId, String inventoryItemTypeId, String productId, String partyId, String statusTypeId, String facilityId, String containerId, String lotId, String uomId, Double quantityOnHand, String serialNumber) throws RemoteException, CreateException;
  public InventoryItem create(String inventoryItemId) throws RemoteException, CreateException;
  public InventoryItem findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  inventoryItemTypeId                  Field for the INVENTORY_ITEM_TYPE_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByInventoryItemTypeId(String inventoryItemTypeId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  productId                  Field for the PRODUCT_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByProductId(String productId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByPartyId(String partyId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  statusTypeId                  Field for the STATUS_TYPE_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByStatusTypeId(String statusTypeId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  facilityId                  Field for the FACILITY_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByFacilityId(String facilityId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  containerId                  Field for the CONTAINER_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByContainerId(String containerId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  lotId                  Field for the LOT_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByLotId(String lotId) throws RemoteException, FinderException;

  /**
   *  Finds InventoryItems by the following fields:
   *

   *@param  uomId                  Field for the UOM_ID column.
   *@return      Collection containing the found InventoryItems
   */
  public Collection findByUomId(String uomId) throws RemoteException, FinderException;

}
