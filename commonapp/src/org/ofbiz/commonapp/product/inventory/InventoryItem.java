
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.party.party.*;
import org.ofbiz.commonapp.common.status.*;
import org.ofbiz.commonapp.product.storage.*;
import org.ofbiz.commonapp.common.uom.*;

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

public interface InventoryItem extends EJBObject
{
  /** Get the primary key of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM table. */
  public String getInventoryItemId() throws RemoteException;
  
  /** Get the value of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public String getInventoryItemTypeId() throws RemoteException;
  /** Set the value of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public void setInventoryItemTypeId(String inventoryItemTypeId) throws RemoteException;
  
  /** Get the value of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public String getProductId() throws RemoteException;
  /** Set the value of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public void setProductId(String productId) throws RemoteException;
  
  /** Get the value of the PARTY_ID column of the INVENTORY_ITEM table. */
  public String getPartyId() throws RemoteException;
  /** Set the value of the PARTY_ID column of the INVENTORY_ITEM table. */
  public void setPartyId(String partyId) throws RemoteException;
  
  /** Get the value of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public String getStatusTypeId() throws RemoteException;
  /** Set the value of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public void setStatusTypeId(String statusTypeId) throws RemoteException;
  
  /** Get the value of the FACILITY_ID column of the INVENTORY_ITEM table. */
  public String getFacilityId() throws RemoteException;
  /** Set the value of the FACILITY_ID column of the INVENTORY_ITEM table. */
  public void setFacilityId(String facilityId) throws RemoteException;
  
  /** Get the value of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public String getContainerId() throws RemoteException;
  /** Set the value of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public void setContainerId(String containerId) throws RemoteException;
  
  /** Get the value of the LOT_ID column of the INVENTORY_ITEM table. */
  public String getLotId() throws RemoteException;
  /** Set the value of the LOT_ID column of the INVENTORY_ITEM table. */
  public void setLotId(String lotId) throws RemoteException;
  
  /** Get the value of the UOM_ID column of the INVENTORY_ITEM table. */
  public String getUomId() throws RemoteException;
  /** Set the value of the UOM_ID column of the INVENTORY_ITEM table. */
  public void setUomId(String uomId) throws RemoteException;
  
  /** Get the value of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public Double getQuantityOnHand() throws RemoteException;
  /** Set the value of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public void setQuantityOnHand(Double quantityOnHand) throws RemoteException;
  
  /** Get the value of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public String getSerialNumber() throws RemoteException;
  /** Set the value of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public void setSerialNumber(String serialNumber) throws RemoteException;
  

  /** Get the value object of this InventoryItem class. */
  public InventoryItem getValueObject() throws RemoteException;
  /** Set the values in the value object of this InventoryItem class. */
  public void setValueObject(InventoryItem inventoryItemValue) throws RemoteException;


  /** Get the  InventoryItemType entity corresponding to this entity. */
  public InventoryItemType getInventoryItemType() throws RemoteException;
  /** Remove the  InventoryItemType entity corresponding to this entity. */
  public void removeInventoryItemType() throws RemoteException;  

  /** Get a collection of  InventoryItemTypeAttr related entities. */
  public Collection getInventoryItemTypeAttrs() throws RemoteException;
  /** Get the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemTypeAttr getInventoryItemTypeAttr(String name) throws RemoteException;
  /** Remove  InventoryItemTypeAttr related entities. */
  public void removeInventoryItemTypeAttrs() throws RemoteException;
  /** Remove the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemTypeAttr(String name) throws RemoteException;

  /** Get a collection of  InventoryItemAttribute related entities. */
  public Collection getInventoryItemAttributes() throws RemoteException;
  /** Get the  InventoryItemAttribute keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemAttribute getInventoryItemAttribute(String name) throws RemoteException;
  /** Remove  InventoryItemAttribute related entities. */
  public void removeInventoryItemAttributes() throws RemoteException;
  /** Remove the  InventoryItemAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemAttribute(String name) throws RemoteException;

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get the  StatusType entity corresponding to this entity. */
  public StatusType getStatusType() throws RemoteException;
  /** Remove the  StatusType entity corresponding to this entity. */
  public void removeStatusType() throws RemoteException;  

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() throws RemoteException;
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() throws RemoteException;  

  /** Get the  Container entity corresponding to this entity. */
  public Container getContainer() throws RemoteException;
  /** Remove the  Container entity corresponding to this entity. */
  public void removeContainer() throws RemoteException;  

  /** Get the  Lot entity corresponding to this entity. */
  public Lot getLot() throws RemoteException;
  /** Remove the  Lot entity corresponding to this entity. */
  public void removeLot() throws RemoteException;  

  /** Get the  Uom entity corresponding to this entity. */
  public Uom getUom() throws RemoteException;
  /** Remove the  Uom entity corresponding to this entity. */
  public void removeUom() throws RemoteException;  

  /** Get a collection of  InventoryItemVariance related entities. */
  public Collection getInventoryItemVariances() throws RemoteException;
  /** Get the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemVariance getInventoryItemVariance(String physicalInventoryId) throws RemoteException;
  /** Remove  InventoryItemVariance related entities. */
  public void removeInventoryItemVariances() throws RemoteException;
  /** Remove the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemVariance(String physicalInventoryId) throws RemoteException;

}
