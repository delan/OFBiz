
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class InventoryItemValue implements InventoryItem
{
  /** The variable of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM table. */
  private String inventoryItemId;
  /** The variable of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  private String inventoryItemTypeId;
  /** The variable of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  private String productId;
  /** The variable of the PARTY_ID column of the INVENTORY_ITEM table. */
  private String partyId;
  /** The variable of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  private String statusTypeId;
  /** The variable of the FACILITY_ID column of the INVENTORY_ITEM table. */
  private String facilityId;
  /** The variable of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  private String containerId;
  /** The variable of the LOT_ID column of the INVENTORY_ITEM table. */
  private String lotId;
  /** The variable of the UOM_ID column of the INVENTORY_ITEM table. */
  private String uomId;
  /** The variable of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  private Double quantityOnHand;
  /** The variable of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  private String serialNumber;

  private InventoryItem inventoryItem;

  public InventoryItemValue()
  {
    this.inventoryItemId = null;
    this.inventoryItemTypeId = null;
    this.productId = null;
    this.partyId = null;
    this.statusTypeId = null;
    this.facilityId = null;
    this.containerId = null;
    this.lotId = null;
    this.uomId = null;
    this.quantityOnHand = null;
    this.serialNumber = null;

    this.inventoryItem = null;
  }

  public InventoryItemValue(InventoryItem inventoryItem) throws RemoteException
  {
    if(inventoryItem == null) return;
  
    this.inventoryItemId = inventoryItem.getInventoryItemId();
    this.inventoryItemTypeId = inventoryItem.getInventoryItemTypeId();
    this.productId = inventoryItem.getProductId();
    this.partyId = inventoryItem.getPartyId();
    this.statusTypeId = inventoryItem.getStatusTypeId();
    this.facilityId = inventoryItem.getFacilityId();
    this.containerId = inventoryItem.getContainerId();
    this.lotId = inventoryItem.getLotId();
    this.uomId = inventoryItem.getUomId();
    this.quantityOnHand = inventoryItem.getQuantityOnHand();
    this.serialNumber = inventoryItem.getSerialNumber();

    this.inventoryItem = inventoryItem;
  }

  public InventoryItemValue(InventoryItem inventoryItem, String inventoryItemId, String inventoryItemTypeId, String productId, String partyId, String statusTypeId, String facilityId, String containerId, String lotId, String uomId, Double quantityOnHand, String serialNumber)
  {
    if(inventoryItem == null) return;
  
    this.inventoryItemId = inventoryItemId;
    this.inventoryItemTypeId = inventoryItemTypeId;
    this.productId = productId;
    this.partyId = partyId;
    this.statusTypeId = statusTypeId;
    this.facilityId = facilityId;
    this.containerId = containerId;
    this.lotId = lotId;
    this.uomId = uomId;
    this.quantityOnHand = quantityOnHand;
    this.serialNumber = serialNumber;

    this.inventoryItem = inventoryItem;
  }


  /** Get the primary key of the INVENTORY_ITEM_ID column of the INVENTORY_ITEM table. */
  public String getInventoryItemId()  throws RemoteException { return inventoryItemId; }

  /** Get the value of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public String getInventoryItemTypeId() throws RemoteException { return inventoryItemTypeId; }
  /** Set the value of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public void setInventoryItemTypeId(String inventoryItemTypeId) throws RemoteException
  {
    this.inventoryItemTypeId = inventoryItemTypeId;
    if(inventoryItem!=null) inventoryItem.setInventoryItemTypeId(inventoryItemTypeId);
  }

  /** Get the value of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public String getProductId() throws RemoteException { return productId; }
  /** Set the value of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public void setProductId(String productId) throws RemoteException
  {
    this.productId = productId;
    if(inventoryItem!=null) inventoryItem.setProductId(productId);
  }

  /** Get the value of the PARTY_ID column of the INVENTORY_ITEM table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the INVENTORY_ITEM table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(inventoryItem!=null) inventoryItem.setPartyId(partyId);
  }

  /** Get the value of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public String getStatusTypeId() throws RemoteException { return statusTypeId; }
  /** Set the value of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public void setStatusTypeId(String statusTypeId) throws RemoteException
  {
    this.statusTypeId = statusTypeId;
    if(inventoryItem!=null) inventoryItem.setStatusTypeId(statusTypeId);
  }

  /** Get the value of the FACILITY_ID column of the INVENTORY_ITEM table. */
  public String getFacilityId() throws RemoteException { return facilityId; }
  /** Set the value of the FACILITY_ID column of the INVENTORY_ITEM table. */
  public void setFacilityId(String facilityId) throws RemoteException
  {
    this.facilityId = facilityId;
    if(inventoryItem!=null) inventoryItem.setFacilityId(facilityId);
  }

  /** Get the value of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public String getContainerId() throws RemoteException { return containerId; }
  /** Set the value of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public void setContainerId(String containerId) throws RemoteException
  {
    this.containerId = containerId;
    if(inventoryItem!=null) inventoryItem.setContainerId(containerId);
  }

  /** Get the value of the LOT_ID column of the INVENTORY_ITEM table. */
  public String getLotId() throws RemoteException { return lotId; }
  /** Set the value of the LOT_ID column of the INVENTORY_ITEM table. */
  public void setLotId(String lotId) throws RemoteException
  {
    this.lotId = lotId;
    if(inventoryItem!=null) inventoryItem.setLotId(lotId);
  }

  /** Get the value of the UOM_ID column of the INVENTORY_ITEM table. */
  public String getUomId() throws RemoteException { return uomId; }
  /** Set the value of the UOM_ID column of the INVENTORY_ITEM table. */
  public void setUomId(String uomId) throws RemoteException
  {
    this.uomId = uomId;
    if(inventoryItem!=null) inventoryItem.setUomId(uomId);
  }

  /** Get the value of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public Double getQuantityOnHand() throws RemoteException { return quantityOnHand; }
  /** Set the value of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public void setQuantityOnHand(Double quantityOnHand) throws RemoteException
  {
    this.quantityOnHand = quantityOnHand;
    if(inventoryItem!=null) inventoryItem.setQuantityOnHand(quantityOnHand);
  }

  /** Get the value of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public String getSerialNumber() throws RemoteException { return serialNumber; }
  /** Set the value of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public void setSerialNumber(String serialNumber) throws RemoteException
  {
    this.serialNumber = serialNumber;
    if(inventoryItem!=null) inventoryItem.setSerialNumber(serialNumber);
  }

  /** Get the value object of the InventoryItem class. */
  public InventoryItem getValueObject() throws RemoteException { return this; }
  /** Set the value object of the InventoryItem class. */
  public void setValueObject(InventoryItem valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(inventoryItem!=null) inventoryItem.setValueObject(valueObject);

    if(inventoryItemId == null) inventoryItemId = valueObject.getInventoryItemId();
    inventoryItemTypeId = valueObject.getInventoryItemTypeId();
    productId = valueObject.getProductId();
    partyId = valueObject.getPartyId();
    statusTypeId = valueObject.getStatusTypeId();
    facilityId = valueObject.getFacilityId();
    containerId = valueObject.getContainerId();
    lotId = valueObject.getLotId();
    uomId = valueObject.getUomId();
    quantityOnHand = valueObject.getQuantityOnHand();
    serialNumber = valueObject.getSerialNumber();
  }


  /** Get the  InventoryItemType entity corresponding to this entity. */
  public InventoryItemType getInventoryItemType() { return InventoryItemTypeHelper.findByPrimaryKey(inventoryItemTypeId); }
  /** Remove the  InventoryItemType entity corresponding to this entity. */
  public void removeInventoryItemType() { InventoryItemTypeHelper.removeByPrimaryKey(inventoryItemTypeId); }

  /** Get a collection of  InventoryItemTypeAttr related entities. */
  public Collection getInventoryItemTypeAttrs() { return InventoryItemTypeAttrHelper.findByInventoryItemTypeId(inventoryItemTypeId); }
  /** Get the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemTypeAttr getInventoryItemTypeAttr(String name) { return InventoryItemTypeAttrHelper.findByPrimaryKey(inventoryItemTypeId, name); }
  /** Remove  InventoryItemTypeAttr related entities. */
  public void removeInventoryItemTypeAttrs() { InventoryItemTypeAttrHelper.removeByInventoryItemTypeId(inventoryItemTypeId); }
  /** Remove the  InventoryItemTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemTypeAttr(String name) { InventoryItemTypeAttrHelper.removeByPrimaryKey(inventoryItemTypeId, name); }

  /** Get a collection of  InventoryItemAttribute related entities. */
  public Collection getInventoryItemAttributes() { return InventoryItemAttributeHelper.findByInventoryItemId(inventoryItemId); }
  /** Get the  InventoryItemAttribute keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemAttribute getInventoryItemAttribute(String name) { return InventoryItemAttributeHelper.findByPrimaryKey(inventoryItemId, name); }
  /** Remove  InventoryItemAttribute related entities. */
  public void removeInventoryItemAttributes() { InventoryItemAttributeHelper.removeByInventoryItemId(inventoryItemId); }
  /** Remove the  InventoryItemAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemAttribute(String name) { InventoryItemAttributeHelper.removeByPrimaryKey(inventoryItemId, name); }

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  StatusType entity corresponding to this entity. */
  public StatusType getStatusType() { return StatusTypeHelper.findByPrimaryKey(statusTypeId); }
  /** Remove the  StatusType entity corresponding to this entity. */
  public void removeStatusType() { StatusTypeHelper.removeByPrimaryKey(statusTypeId); }

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }

  /** Get the  Container entity corresponding to this entity. */
  public Container getContainer() { return ContainerHelper.findByPrimaryKey(containerId); }
  /** Remove the  Container entity corresponding to this entity. */
  public void removeContainer() { ContainerHelper.removeByPrimaryKey(containerId); }

  /** Get the  Lot entity corresponding to this entity. */
  public Lot getLot() { return LotHelper.findByPrimaryKey(lotId); }
  /** Remove the  Lot entity corresponding to this entity. */
  public void removeLot() { LotHelper.removeByPrimaryKey(lotId); }

  /** Get the  Uom entity corresponding to this entity. */
  public Uom getUom() { return UomHelper.findByPrimaryKey(uomId); }
  /** Remove the  Uom entity corresponding to this entity. */
  public void removeUom() { UomHelper.removeByPrimaryKey(uomId); }

  /** Get a collection of  InventoryItemVariance related entities. */
  public Collection getInventoryItemVariances() { return InventoryItemVarianceHelper.findByInventoryItemId(inventoryItemId); }
  /** Get the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemVariance getInventoryItemVariance(String physicalInventoryId) { return InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId); }
  /** Remove  InventoryItemVariance related entities. */
  public void removeInventoryItemVariances() { InventoryItemVarianceHelper.removeByInventoryItemId(inventoryItemId); }
  /** Remove the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemVariance(String physicalInventoryId) { InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(inventoryItem!=null) return inventoryItem.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(inventoryItem!=null) return inventoryItem.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(inventoryItem!=null) return inventoryItem.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(inventoryItem!=null) return inventoryItem.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(inventoryItem!=null) inventoryItem.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
