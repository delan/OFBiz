
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.party.party.*;
import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.product.feature.*;
import org.ofbiz.commonapp.product.category.*;
import org.ofbiz.commonapp.common.uom.*;
import org.ofbiz.commonapp.common.geo.*;

/**
 * <p><b>Title:</b> Price Component Entity
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
 *@created    Fri Jul 27 01:18:29 MDT 2001
 *@version    1.0
 */
public class PriceComponentBean implements EntityBean
{
  /** The variable for the PRICE_COMPONENT_ID column of the PRICE_COMPONENT table. */
  public String priceComponentId;
  /** The variable for the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public String priceComponentTypeId;
  /** The variable for the PARTY_ID column of the PRICE_COMPONENT table. */
  public String partyId;
  /** The variable for the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public String partyTypeId;
  /** The variable for the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public String productId;
  /** The variable for the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public String productFeatureId;
  /** The variable for the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public String productCategoryId;
  /** The variable for the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public String agreementId;
  /** The variable for the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public String agreementItemSeqId;
  /** The variable for the UOM_ID column of the PRICE_COMPONENT table. */
  public String uomId;
  /** The variable for the GEO_ID column of the PRICE_COMPONENT table. */
  public String geoId;
  /** The variable for the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public String saleTypeId;
  /** The variable for the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public String orderValueBreakId;
  /** The variable for the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public String quantityBreakId;
  /** The variable for the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public String utilizationUomId;
  /** The variable for the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public Double utilizationQuantity;
  /** The variable for the FROM_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date fromDate;
  /** The variable for the THRU_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date thruDate;
  /** The variable for the PRICE column of the PRICE_COMPONENT table. */
  public Double price;
  /** The variable for the PERCENT column of the PRICE_COMPONENT table. */
  public Double percent;
  /** The variable for the COMMENT column of the PRICE_COMPONENT table. */
  public String comment;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the PriceComponentBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRICE_COMPONENT_ID column of the PRICE_COMPONENT table. */
  public String getPriceComponentId() { return priceComponentId; }

  /** Get the value of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getPriceComponentTypeId() { return priceComponentTypeId; }
  /** Set the value of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setPriceComponentTypeId(String priceComponentTypeId)
  {
    this.priceComponentTypeId = priceComponentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_ID column of the PRICE_COMPONENT table. */
  public String getPartyId() { return partyId; }
  /** Set the value of the PARTY_ID column of the PRICE_COMPONENT table. */
  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getPartyTypeId() { return partyTypeId; }
  /** Set the value of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setPartyTypeId(String partyTypeId)
  {
    this.partyTypeId = partyTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public String getProductId() { return productId; }
  /** Set the value of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public void setProductId(String productId)
  {
    this.productId = productId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public String getProductFeatureId() { return productFeatureId; }
  /** Set the value of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public void setProductFeatureId(String productFeatureId)
  {
    this.productFeatureId = productFeatureId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public String getProductCategoryId() { return productCategoryId; }
  /** Set the value of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public void setProductCategoryId(String productCategoryId)
  {
    this.productCategoryId = productCategoryId;
    ejbIsModified = true;
  }

  /** Get the value of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public String getAgreementId() { return agreementId; }
  /** Set the value of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public void setAgreementId(String agreementId)
  {
    this.agreementId = agreementId;
    ejbIsModified = true;
  }

  /** Get the value of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public String getAgreementItemSeqId() { return agreementItemSeqId; }
  /** Set the value of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public void setAgreementItemSeqId(String agreementItemSeqId)
  {
    this.agreementItemSeqId = agreementItemSeqId;
    ejbIsModified = true;
  }

  /** Get the value of the UOM_ID column of the PRICE_COMPONENT table. */
  public String getUomId() { return uomId; }
  /** Set the value of the UOM_ID column of the PRICE_COMPONENT table. */
  public void setUomId(String uomId)
  {
    this.uomId = uomId;
    ejbIsModified = true;
  }

  /** Get the value of the GEO_ID column of the PRICE_COMPONENT table. */
  public String getGeoId() { return geoId; }
  /** Set the value of the GEO_ID column of the PRICE_COMPONENT table. */
  public void setGeoId(String geoId)
  {
    this.geoId = geoId;
    ejbIsModified = true;
  }

  /** Get the value of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getSaleTypeId() { return saleTypeId; }
  /** Set the value of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setSaleTypeId(String saleTypeId)
  {
    this.saleTypeId = saleTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public String getOrderValueBreakId() { return orderValueBreakId; }
  /** Set the value of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public void setOrderValueBreakId(String orderValueBreakId)
  {
    this.orderValueBreakId = orderValueBreakId;
    ejbIsModified = true;
  }

  /** Get the value of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public String getQuantityBreakId() { return quantityBreakId; }
  /** Set the value of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public void setQuantityBreakId(String quantityBreakId)
  {
    this.quantityBreakId = quantityBreakId;
    ejbIsModified = true;
  }

  /** Get the value of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public String getUtilizationUomId() { return utilizationUomId; }
  /** Set the value of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public void setUtilizationUomId(String utilizationUomId)
  {
    this.utilizationUomId = utilizationUomId;
    ejbIsModified = true;
  }

  /** Get the value of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public Double getUtilizationQuantity() { return utilizationQuantity; }
  /** Set the value of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public void setUtilizationQuantity(Double utilizationQuantity)
  {
    this.utilizationQuantity = utilizationQuantity;
    ejbIsModified = true;
  }

  /** Get the value of the FROM_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date getFromDate() { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRICE_COMPONENT table. */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date getThruDate() { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRICE_COMPONENT table. */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
    ejbIsModified = true;
  }

  /** Get the value of the PRICE column of the PRICE_COMPONENT table. */
  public Double getPrice() { return price; }
  /** Set the value of the PRICE column of the PRICE_COMPONENT table. */
  public void setPrice(Double price)
  {
    this.price = price;
    ejbIsModified = true;
  }

  /** Get the value of the PERCENT column of the PRICE_COMPONENT table. */
  public Double getPercent() { return percent; }
  /** Set the value of the PERCENT column of the PRICE_COMPONENT table. */
  public void setPercent(Double percent)
  {
    this.percent = percent;
    ejbIsModified = true;
  }

  /** Get the value of the COMMENT column of the PRICE_COMPONENT table. */
  public String getComment() { return comment; }
  /** Set the value of the COMMENT column of the PRICE_COMPONENT table. */
  public void setComment(String comment)
  {
    this.comment = comment;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the PriceComponentBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(PriceComponent valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getPriceComponentTypeId() != null)
      {
        this.priceComponentTypeId = valueObject.getPriceComponentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getPartyId() != null)
      {
        this.partyId = valueObject.getPartyId();
        ejbIsModified = true;
      }
      if(valueObject.getPartyTypeId() != null)
      {
        this.partyTypeId = valueObject.getPartyTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getProductId() != null)
      {
        this.productId = valueObject.getProductId();
        ejbIsModified = true;
      }
      if(valueObject.getProductFeatureId() != null)
      {
        this.productFeatureId = valueObject.getProductFeatureId();
        ejbIsModified = true;
      }
      if(valueObject.getProductCategoryId() != null)
      {
        this.productCategoryId = valueObject.getProductCategoryId();
        ejbIsModified = true;
      }
      if(valueObject.getAgreementId() != null)
      {
        this.agreementId = valueObject.getAgreementId();
        ejbIsModified = true;
      }
      if(valueObject.getAgreementItemSeqId() != null)
      {
        this.agreementItemSeqId = valueObject.getAgreementItemSeqId();
        ejbIsModified = true;
      }
      if(valueObject.getUomId() != null)
      {
        this.uomId = valueObject.getUomId();
        ejbIsModified = true;
      }
      if(valueObject.getGeoId() != null)
      {
        this.geoId = valueObject.getGeoId();
        ejbIsModified = true;
      }
      if(valueObject.getSaleTypeId() != null)
      {
        this.saleTypeId = valueObject.getSaleTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getOrderValueBreakId() != null)
      {
        this.orderValueBreakId = valueObject.getOrderValueBreakId();
        ejbIsModified = true;
      }
      if(valueObject.getQuantityBreakId() != null)
      {
        this.quantityBreakId = valueObject.getQuantityBreakId();
        ejbIsModified = true;
      }
      if(valueObject.getUtilizationUomId() != null)
      {
        this.utilizationUomId = valueObject.getUtilizationUomId();
        ejbIsModified = true;
      }
      if(valueObject.getUtilizationQuantity() != null)
      {
        this.utilizationQuantity = valueObject.getUtilizationQuantity();
        ejbIsModified = true;
      }
      if(valueObject.getFromDate() != null)
      {
        this.fromDate = valueObject.getFromDate();
        ejbIsModified = true;
      }
      if(valueObject.getThruDate() != null)
      {
        this.thruDate = valueObject.getThruDate();
        ejbIsModified = true;
      }
      if(valueObject.getPrice() != null)
      {
        this.price = valueObject.getPrice();
        ejbIsModified = true;
      }
      if(valueObject.getPercent() != null)
      {
        this.percent = valueObject.getPercent();
        ejbIsModified = true;
      }
      if(valueObject.getComment() != null)
      {
        this.comment = valueObject.getComment();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the PriceComponentBean object
   *@return    The ValueObject value
   */
  public PriceComponent getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PriceComponentValue((PriceComponent)this.entityContext.getEJBObject(), priceComponentId, priceComponentTypeId, partyId, partyTypeId, productId, productFeatureId, productCategoryId, agreementId, agreementItemSeqId, uomId, geoId, saleTypeId, orderValueBreakId, quantityBreakId, utilizationUomId, utilizationQuantity, fromDate, thruDate, price, percent, comment);
    }
    else { return null; }
  }


  /** Get the  PriceComponentType entity corresponding to this entity. */
  public PriceComponentType getPriceComponentType() { return PriceComponentTypeHelper.findByPrimaryKey(priceComponentTypeId); }
  /** Remove the  PriceComponentType entity corresponding to this entity. */
  public void removePriceComponentType() { PriceComponentTypeHelper.removeByPrimaryKey(priceComponentTypeId); }

  /** Get a collection of  PriceComponentTypeAttr related entities. */
  public Collection getPriceComponentTypeAttrs() { return PriceComponentTypeAttrHelper.findByPriceComponentTypeId(priceComponentTypeId); }
  /** Get the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentTypeAttr getPriceComponentTypeAttr(String name) { return PriceComponentTypeAttrHelper.findByPrimaryKey(priceComponentTypeId, name); }
  /** Remove  PriceComponentTypeAttr related entities. */
  public void removePriceComponentTypeAttrs() { PriceComponentTypeAttrHelper.removeByPriceComponentTypeId(priceComponentTypeId); }
  /** Remove the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentTypeAttr(String name) { PriceComponentTypeAttrHelper.removeByPrimaryKey(priceComponentTypeId, name); }

  /** Get a collection of  PriceComponentAttribute related entities. */
  public Collection getPriceComponentAttributes() { return PriceComponentAttributeHelper.findByPriceComponentId(priceComponentId); }
  /** Get the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentAttribute getPriceComponentAttribute(String name) { return PriceComponentAttributeHelper.findByPrimaryKey(priceComponentId, name); }
  /** Remove  PriceComponentAttribute related entities. */
  public void removePriceComponentAttributes() { PriceComponentAttributeHelper.removeByPriceComponentId(priceComponentId); }
  /** Remove the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentAttribute(String name) { PriceComponentAttributeHelper.removeByPrimaryKey(priceComponentId, name); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  PartyType entity corresponding to this entity. */
  public PartyType getPartyType() { return PartyTypeHelper.findByPrimaryKey(partyTypeId); }
  /** Remove the  PartyType entity corresponding to this entity. */
  public void removePartyType() { PartyTypeHelper.removeByPrimaryKey(partyTypeId); }

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }

  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }

  /** Get the  Uom entity corresponding to this entity. */
  public Uom getUom() { return UomHelper.findByPrimaryKey(uomId); }
  /** Remove the  Uom entity corresponding to this entity. */
  public void removeUom() { UomHelper.removeByPrimaryKey(uomId); }

  /** Get the  Geo entity corresponding to this entity. */
  public Geo getGeo() { return GeoHelper.findByPrimaryKey(geoId); }
  /** Remove the  Geo entity corresponding to this entity. */
  public void removeGeo() { GeoHelper.removeByPrimaryKey(geoId); }

  /** Get the  SaleType entity corresponding to this entity. */
  public SaleType getSaleType() { return SaleTypeHelper.findByPrimaryKey(saleTypeId); }
  /** Remove the  SaleType entity corresponding to this entity. */
  public void removeSaleType() { SaleTypeHelper.removeByPrimaryKey(saleTypeId); }

  /** Get the  OrderValueBreak entity corresponding to this entity. */
  public OrderValueBreak getOrderValueBreak() { return OrderValueBreakHelper.findByPrimaryKey(orderValueBreakId); }
  /** Remove the  OrderValueBreak entity corresponding to this entity. */
  public void removeOrderValueBreak() { OrderValueBreakHelper.removeByPrimaryKey(orderValueBreakId); }

  /** Get the  QuantityBreak entity corresponding to this entity. */
  public QuantityBreak getQuantityBreak() { return QuantityBreakHelper.findByPrimaryKey(quantityBreakId); }
  /** Remove the  QuantityBreak entity corresponding to this entity. */
  public void removeQuantityBreak() { QuantityBreakHelper.removeByPrimaryKey(quantityBreakId); }

  /** Get the Utilization Uom entity corresponding to this entity. */
  public Uom getUtilizationUom() { return UomHelper.findByPrimaryKey(utilizationUomId); }
  /** Remove the Utilization Uom entity corresponding to this entity. */
  public void removeUtilizationUom() { UomHelper.removeByPrimaryKey(utilizationUomId); }


  /** Description of the Method
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field of the AGREEMENT_ITEM_SEQ_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  utilizationUomId                  Field of the UTILIZATION_UOM_ID column.
   *@param  utilizationQuantity                  Field of the UTILIZATION_QUANTITY column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  price                  Field of the PRICE column.
   *@param  percent                  Field of the PERCENT column.
   *@param  comment                  Field of the COMMENT column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String priceComponentId, String priceComponentTypeId, String partyId, String partyTypeId, String productId, String productFeatureId, String productCategoryId, String agreementId, String agreementItemSeqId, String uomId, String geoId, String saleTypeId, String orderValueBreakId, String quantityBreakId, String utilizationUomId, Double utilizationQuantity, java.util.Date fromDate, java.util.Date thruDate, Double price, Double percent, String comment) throws CreateException
  {
    this.priceComponentId = priceComponentId;
    this.priceComponentTypeId = priceComponentTypeId;
    this.partyId = partyId;
    this.partyTypeId = partyTypeId;
    this.productId = productId;
    this.productFeatureId = productFeatureId;
    this.productCategoryId = productCategoryId;
    this.agreementId = agreementId;
    this.agreementItemSeqId = agreementItemSeqId;
    this.uomId = uomId;
    this.geoId = geoId;
    this.saleTypeId = saleTypeId;
    this.orderValueBreakId = orderValueBreakId;
    this.quantityBreakId = quantityBreakId;
    this.utilizationUomId = utilizationUomId;
    this.utilizationQuantity = utilizationQuantity;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.price = price;
    this.percent = percent;
    this.comment = comment;
    return null;
  }

  /** Description of the Method
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String priceComponentId) throws CreateException
  {
    return ejbCreate(priceComponentId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  agreementId                  Field of the AGREEMENT_ID column.
   *@param  agreementItemSeqId                  Field of the AGREEMENT_ITEM_SEQ_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  saleTypeId                  Field of the SALE_TYPE_ID column.
   *@param  orderValueBreakId                  Field of the ORDER_VALUE_BREAK_ID column.
   *@param  quantityBreakId                  Field of the QUANTITY_BREAK_ID column.
   *@param  utilizationUomId                  Field of the UTILIZATION_UOM_ID column.
   *@param  utilizationQuantity                  Field of the UTILIZATION_QUANTITY column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  price                  Field of the PRICE column.
   *@param  percent                  Field of the PERCENT column.
   *@param  comment                  Field of the COMMENT column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String priceComponentId, String priceComponentTypeId, String partyId, String partyTypeId, String productId, String productFeatureId, String productCategoryId, String agreementId, String agreementItemSeqId, String uomId, String geoId, String saleTypeId, String orderValueBreakId, String quantityBreakId, String utilizationUomId, Double utilizationQuantity, java.util.Date fromDate, java.util.Date thruDate, Double price, Double percent, String comment) throws CreateException {}

  /** Description of the Method
   *@param  priceComponentId                  Field of the PRICE_COMPONENT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String priceComponentId) throws CreateException
  {
    ejbPostCreate(priceComponentId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
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
