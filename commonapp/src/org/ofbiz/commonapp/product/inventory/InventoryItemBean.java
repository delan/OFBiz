
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
public class InventoryItemBean implements EntityBean
{
  /** The variable for the INVENTORY_ITEM_ID column of the INVENTORY_ITEM table. */
  public String inventoryItemId;
  /** The variable for the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public String inventoryItemTypeId;
  /** The variable for the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public String productId;
  /** The variable for the PARTY_ID column of the INVENTORY_ITEM table. */
  public String partyId;
  /** The variable for the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public String statusTypeId;
  /** The variable for the FACILITY_ID column of the INVENTORY_ITEM table. */
  public String facilityId;
  /** The variable for the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public String containerId;
  /** The variable for the LOT_ID column of the INVENTORY_ITEM table. */
  public String lotId;
  /** The variable for the UOM_ID column of the INVENTORY_ITEM table. */
  public String uomId;
  /** The variable for the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public Double quantityOnHand;
  /** The variable for the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public String serialNumber;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the InventoryItemBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key INVENTORY_ITEM_ID column of the INVENTORY_ITEM table. */
  public String getInventoryItemId() { return inventoryItemId; }

  /** Get the value of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public String getInventoryItemTypeId() { return inventoryItemTypeId; }
  /** Set the value of the INVENTORY_ITEM_TYPE_ID column of the INVENTORY_ITEM table. */
  public void setInventoryItemTypeId(String inventoryItemTypeId)
  {
    this.inventoryItemTypeId = inventoryItemTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public String getProductId() { return productId; }
  /** Set the value of the PRODUCT_ID column of the INVENTORY_ITEM table. */
  public void setProductId(String productId)
  {
    this.productId = productId;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_ID column of the INVENTORY_ITEM table. */
  public String getPartyId() { return partyId; }
  /** Set the value of the PARTY_ID column of the INVENTORY_ITEM table. */
  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
    ejbIsModified = true;
  }

  /** Get the value of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public String getStatusTypeId() { return statusTypeId; }
  /** Set the value of the STATUS_TYPE_ID column of the INVENTORY_ITEM table. */
  public void setStatusTypeId(String statusTypeId)
  {
    this.statusTypeId = statusTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the FACILITY_ID column of the INVENTORY_ITEM table. */
  public String getFacilityId() { return facilityId; }
  /** Set the value of the FACILITY_ID column of the INVENTORY_ITEM table. */
  public void setFacilityId(String facilityId)
  {
    this.facilityId = facilityId;
    ejbIsModified = true;
  }

  /** Get the value of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public String getContainerId() { return containerId; }
  /** Set the value of the CONTAINER_ID column of the INVENTORY_ITEM table. */
  public void setContainerId(String containerId)
  {
    this.containerId = containerId;
    ejbIsModified = true;
  }

  /** Get the value of the LOT_ID column of the INVENTORY_ITEM table. */
  public String getLotId() { return lotId; }
  /** Set the value of the LOT_ID column of the INVENTORY_ITEM table. */
  public void setLotId(String lotId)
  {
    this.lotId = lotId;
    ejbIsModified = true;
  }

  /** Get the value of the UOM_ID column of the INVENTORY_ITEM table. */
  public String getUomId() { return uomId; }
  /** Set the value of the UOM_ID column of the INVENTORY_ITEM table. */
  public void setUomId(String uomId)
  {
    this.uomId = uomId;
    ejbIsModified = true;
  }

  /** Get the value of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public Double getQuantityOnHand() { return quantityOnHand; }
  /** Set the value of the QUANTITY_ON_HAND column of the INVENTORY_ITEM table. */
  public void setQuantityOnHand(Double quantityOnHand)
  {
    this.quantityOnHand = quantityOnHand;
    ejbIsModified = true;
  }

  /** Get the value of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public String getSerialNumber() { return serialNumber; }
  /** Set the value of the SERIAL_NUMBER column of the INVENTORY_ITEM table. */
  public void setSerialNumber(String serialNumber)
  {
    this.serialNumber = serialNumber;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the InventoryItemBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(InventoryItem valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getInventoryItemTypeId() != null)
      {
        this.inventoryItemTypeId = valueObject.getInventoryItemTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getProductId() != null)
      {
        this.productId = valueObject.getProductId();
        ejbIsModified = true;
      }
      if(valueObject.getPartyId() != null)
      {
        this.partyId = valueObject.getPartyId();
        ejbIsModified = true;
      }
      if(valueObject.getStatusTypeId() != null)
      {
        this.statusTypeId = valueObject.getStatusTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getFacilityId() != null)
      {
        this.facilityId = valueObject.getFacilityId();
        ejbIsModified = true;
      }
      if(valueObject.getContainerId() != null)
      {
        this.containerId = valueObject.getContainerId();
        ejbIsModified = true;
      }
      if(valueObject.getLotId() != null)
      {
        this.lotId = valueObject.getLotId();
        ejbIsModified = true;
      }
      if(valueObject.getUomId() != null)
      {
        this.uomId = valueObject.getUomId();
        ejbIsModified = true;
      }
      if(valueObject.getQuantityOnHand() != null)
      {
        this.quantityOnHand = valueObject.getQuantityOnHand();
        ejbIsModified = true;
      }
      if(valueObject.getSerialNumber() != null)
      {
        this.serialNumber = valueObject.getSerialNumber();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the InventoryItemBean object
   *@return    The ValueObject value
   */
  public InventoryItem getValueObject()
  {
    if(this.entityContext != null)
    {
      return new InventoryItemValue((InventoryItem)this.entityContext.getEJBObject(), inventoryItemId, inventoryItemTypeId, productId, partyId, statusTypeId, facilityId, containerId, lotId, uomId, quantityOnHand, serialNumber);
    }
    else { return null; }
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


  /** Description of the Method
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
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String inventoryItemId, String inventoryItemTypeId, String productId, String partyId, String statusTypeId, String facilityId, String containerId, String lotId, String uomId, Double quantityOnHand, String serialNumber) throws CreateException
  {
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
    return null;
  }

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String inventoryItemId) throws CreateException
  {
    return ejbCreate(inventoryItemId, null, null, null, null, null, null, null, null, null, null);
  }

  /** Description of the Method
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
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemId, String inventoryItemTypeId, String productId, String partyId, String statusTypeId, String facilityId, String containerId, String lotId, String uomId, Double quantityOnHand, String serialNumber) throws CreateException {}

  /** Description of the Method
   *@param  inventoryItemId                  Field of the INVENTORY_ITEM_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String inventoryItemId) throws CreateException
  {
    ejbPostCreate(inventoryItemId, null, null, null, null, null, null, null, null, null, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
