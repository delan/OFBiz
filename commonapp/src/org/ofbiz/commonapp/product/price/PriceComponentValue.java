
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class PriceComponentValue implements PriceComponent
{
  /** The variable of the PRICE_COMPONENT_ID column of the PRICE_COMPONENT table. */
  private String priceComponentId;
  /** The variable of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  private String priceComponentTypeId;
  /** The variable of the PARTY_ID column of the PRICE_COMPONENT table. */
  private String partyId;
  /** The variable of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  private String partyTypeId;
  /** The variable of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  private String productId;
  /** The variable of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  private String productFeatureId;
  /** The variable of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  private String productCategoryId;
  /** The variable of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  private String agreementId;
  /** The variable of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  private String agreementItemSeqId;
  /** The variable of the UOM_ID column of the PRICE_COMPONENT table. */
  private String uomId;
  /** The variable of the GEO_ID column of the PRICE_COMPONENT table. */
  private String geoId;
  /** The variable of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  private String saleTypeId;
  /** The variable of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  private String orderValueBreakId;
  /** The variable of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  private String quantityBreakId;
  /** The variable of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  private String utilizationUomId;
  /** The variable of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  private Double utilizationQuantity;
  /** The variable of the FROM_DATE column of the PRICE_COMPONENT table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PRICE_COMPONENT table. */
  private java.util.Date thruDate;
  /** The variable of the PRICE column of the PRICE_COMPONENT table. */
  private Double price;
  /** The variable of the PERCENT column of the PRICE_COMPONENT table. */
  private Double percent;
  /** The variable of the COMMENT column of the PRICE_COMPONENT table. */
  private String comment;

  private PriceComponent priceComponent;

  public PriceComponentValue()
  {
    this.priceComponentId = null;
    this.priceComponentTypeId = null;
    this.partyId = null;
    this.partyTypeId = null;
    this.productId = null;
    this.productFeatureId = null;
    this.productCategoryId = null;
    this.agreementId = null;
    this.agreementItemSeqId = null;
    this.uomId = null;
    this.geoId = null;
    this.saleTypeId = null;
    this.orderValueBreakId = null;
    this.quantityBreakId = null;
    this.utilizationUomId = null;
    this.utilizationQuantity = null;
    this.fromDate = null;
    this.thruDate = null;
    this.price = null;
    this.percent = null;
    this.comment = null;

    this.priceComponent = null;
  }

  public PriceComponentValue(PriceComponent priceComponent) throws RemoteException
  {
    if(priceComponent == null) return;
  
    this.priceComponentId = priceComponent.getPriceComponentId();
    this.priceComponentTypeId = priceComponent.getPriceComponentTypeId();
    this.partyId = priceComponent.getPartyId();
    this.partyTypeId = priceComponent.getPartyTypeId();
    this.productId = priceComponent.getProductId();
    this.productFeatureId = priceComponent.getProductFeatureId();
    this.productCategoryId = priceComponent.getProductCategoryId();
    this.agreementId = priceComponent.getAgreementId();
    this.agreementItemSeqId = priceComponent.getAgreementItemSeqId();
    this.uomId = priceComponent.getUomId();
    this.geoId = priceComponent.getGeoId();
    this.saleTypeId = priceComponent.getSaleTypeId();
    this.orderValueBreakId = priceComponent.getOrderValueBreakId();
    this.quantityBreakId = priceComponent.getQuantityBreakId();
    this.utilizationUomId = priceComponent.getUtilizationUomId();
    this.utilizationQuantity = priceComponent.getUtilizationQuantity();
    this.fromDate = priceComponent.getFromDate();
    this.thruDate = priceComponent.getThruDate();
    this.price = priceComponent.getPrice();
    this.percent = priceComponent.getPercent();
    this.comment = priceComponent.getComment();

    this.priceComponent = priceComponent;
  }

  public PriceComponentValue(PriceComponent priceComponent, String priceComponentId, String priceComponentTypeId, String partyId, String partyTypeId, String productId, String productFeatureId, String productCategoryId, String agreementId, String agreementItemSeqId, String uomId, String geoId, String saleTypeId, String orderValueBreakId, String quantityBreakId, String utilizationUomId, Double utilizationQuantity, java.util.Date fromDate, java.util.Date thruDate, Double price, Double percent, String comment)
  {
    if(priceComponent == null) return;
  
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

    this.priceComponent = priceComponent;
  }


  /** Get the primary key of the PRICE_COMPONENT_ID column of the PRICE_COMPONENT table. */
  public String getPriceComponentId()  throws RemoteException { return priceComponentId; }

  /** Get the value of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getPriceComponentTypeId() throws RemoteException { return priceComponentTypeId; }
  /** Set the value of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setPriceComponentTypeId(String priceComponentTypeId) throws RemoteException
  {
    this.priceComponentTypeId = priceComponentTypeId;
    if(priceComponent!=null) priceComponent.setPriceComponentTypeId(priceComponentTypeId);
  }

  /** Get the value of the PARTY_ID column of the PRICE_COMPONENT table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the PRICE_COMPONENT table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(priceComponent!=null) priceComponent.setPartyId(partyId);
  }

  /** Get the value of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getPartyTypeId() throws RemoteException { return partyTypeId; }
  /** Set the value of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setPartyTypeId(String partyTypeId) throws RemoteException
  {
    this.partyTypeId = partyTypeId;
    if(priceComponent!=null) priceComponent.setPartyTypeId(partyTypeId);
  }

  /** Get the value of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public String getProductId() throws RemoteException { return productId; }
  /** Set the value of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public void setProductId(String productId) throws RemoteException
  {
    this.productId = productId;
    if(priceComponent!=null) priceComponent.setProductId(productId);
  }

  /** Get the value of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public String getProductFeatureId() throws RemoteException { return productFeatureId; }
  /** Set the value of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public void setProductFeatureId(String productFeatureId) throws RemoteException
  {
    this.productFeatureId = productFeatureId;
    if(priceComponent!=null) priceComponent.setProductFeatureId(productFeatureId);
  }

  /** Get the value of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public String getProductCategoryId() throws RemoteException { return productCategoryId; }
  /** Set the value of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public void setProductCategoryId(String productCategoryId) throws RemoteException
  {
    this.productCategoryId = productCategoryId;
    if(priceComponent!=null) priceComponent.setProductCategoryId(productCategoryId);
  }

  /** Get the value of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public String getAgreementId() throws RemoteException { return agreementId; }
  /** Set the value of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public void setAgreementId(String agreementId) throws RemoteException
  {
    this.agreementId = agreementId;
    if(priceComponent!=null) priceComponent.setAgreementId(agreementId);
  }

  /** Get the value of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public String getAgreementItemSeqId() throws RemoteException { return agreementItemSeqId; }
  /** Set the value of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public void setAgreementItemSeqId(String agreementItemSeqId) throws RemoteException
  {
    this.agreementItemSeqId = agreementItemSeqId;
    if(priceComponent!=null) priceComponent.setAgreementItemSeqId(agreementItemSeqId);
  }

  /** Get the value of the UOM_ID column of the PRICE_COMPONENT table. */
  public String getUomId() throws RemoteException { return uomId; }
  /** Set the value of the UOM_ID column of the PRICE_COMPONENT table. */
  public void setUomId(String uomId) throws RemoteException
  {
    this.uomId = uomId;
    if(priceComponent!=null) priceComponent.setUomId(uomId);
  }

  /** Get the value of the GEO_ID column of the PRICE_COMPONENT table. */
  public String getGeoId() throws RemoteException { return geoId; }
  /** Set the value of the GEO_ID column of the PRICE_COMPONENT table. */
  public void setGeoId(String geoId) throws RemoteException
  {
    this.geoId = geoId;
    if(priceComponent!=null) priceComponent.setGeoId(geoId);
  }

  /** Get the value of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getSaleTypeId() throws RemoteException { return saleTypeId; }
  /** Set the value of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setSaleTypeId(String saleTypeId) throws RemoteException
  {
    this.saleTypeId = saleTypeId;
    if(priceComponent!=null) priceComponent.setSaleTypeId(saleTypeId);
  }

  /** Get the value of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public String getOrderValueBreakId() throws RemoteException { return orderValueBreakId; }
  /** Set the value of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public void setOrderValueBreakId(String orderValueBreakId) throws RemoteException
  {
    this.orderValueBreakId = orderValueBreakId;
    if(priceComponent!=null) priceComponent.setOrderValueBreakId(orderValueBreakId);
  }

  /** Get the value of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public String getQuantityBreakId() throws RemoteException { return quantityBreakId; }
  /** Set the value of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public void setQuantityBreakId(String quantityBreakId) throws RemoteException
  {
    this.quantityBreakId = quantityBreakId;
    if(priceComponent!=null) priceComponent.setQuantityBreakId(quantityBreakId);
  }

  /** Get the value of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public String getUtilizationUomId() throws RemoteException { return utilizationUomId; }
  /** Set the value of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public void setUtilizationUomId(String utilizationUomId) throws RemoteException
  {
    this.utilizationUomId = utilizationUomId;
    if(priceComponent!=null) priceComponent.setUtilizationUomId(utilizationUomId);
  }

  /** Get the value of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public Double getUtilizationQuantity() throws RemoteException { return utilizationQuantity; }
  /** Set the value of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public void setUtilizationQuantity(Double utilizationQuantity) throws RemoteException
  {
    this.utilizationQuantity = utilizationQuantity;
    if(priceComponent!=null) priceComponent.setUtilizationQuantity(utilizationQuantity);
  }

  /** Get the value of the FROM_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRICE_COMPONENT table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(priceComponent!=null) priceComponent.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRICE_COMPONENT table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(priceComponent!=null) priceComponent.setThruDate(thruDate);
  }

  /** Get the value of the PRICE column of the PRICE_COMPONENT table. */
  public Double getPrice() throws RemoteException { return price; }
  /** Set the value of the PRICE column of the PRICE_COMPONENT table. */
  public void setPrice(Double price) throws RemoteException
  {
    this.price = price;
    if(priceComponent!=null) priceComponent.setPrice(price);
  }

  /** Get the value of the PERCENT column of the PRICE_COMPONENT table. */
  public Double getPercent() throws RemoteException { return percent; }
  /** Set the value of the PERCENT column of the PRICE_COMPONENT table. */
  public void setPercent(Double percent) throws RemoteException
  {
    this.percent = percent;
    if(priceComponent!=null) priceComponent.setPercent(percent);
  }

  /** Get the value of the COMMENT column of the PRICE_COMPONENT table. */
  public String getComment() throws RemoteException { return comment; }
  /** Set the value of the COMMENT column of the PRICE_COMPONENT table. */
  public void setComment(String comment) throws RemoteException
  {
    this.comment = comment;
    if(priceComponent!=null) priceComponent.setComment(comment);
  }

  /** Get the value object of the PriceComponent class. */
  public PriceComponent getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PriceComponent class. */
  public void setValueObject(PriceComponent valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(priceComponent!=null) priceComponent.setValueObject(valueObject);

    if(priceComponentId == null) priceComponentId = valueObject.getPriceComponentId();
    priceComponentTypeId = valueObject.getPriceComponentTypeId();
    partyId = valueObject.getPartyId();
    partyTypeId = valueObject.getPartyTypeId();
    productId = valueObject.getProductId();
    productFeatureId = valueObject.getProductFeatureId();
    productCategoryId = valueObject.getProductCategoryId();
    agreementId = valueObject.getAgreementId();
    agreementItemSeqId = valueObject.getAgreementItemSeqId();
    uomId = valueObject.getUomId();
    geoId = valueObject.getGeoId();
    saleTypeId = valueObject.getSaleTypeId();
    orderValueBreakId = valueObject.getOrderValueBreakId();
    quantityBreakId = valueObject.getQuantityBreakId();
    utilizationUomId = valueObject.getUtilizationUomId();
    utilizationQuantity = valueObject.getUtilizationQuantity();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
    price = valueObject.getPrice();
    percent = valueObject.getPercent();
    comment = valueObject.getComment();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(priceComponent!=null) return priceComponent.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(priceComponent!=null) return priceComponent.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(priceComponent!=null) return priceComponent.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(priceComponent!=null) return priceComponent.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(priceComponent!=null) priceComponent.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
