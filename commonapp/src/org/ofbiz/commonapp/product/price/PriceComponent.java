
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

public interface PriceComponent extends EJBObject
{
  /** Get the primary key of the PRICE_COMPONENT_ID column of the PRICE_COMPONENT table. */
  public String getPriceComponentId() throws RemoteException;
  
  /** Get the value of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getPriceComponentTypeId() throws RemoteException;
  /** Set the value of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setPriceComponentTypeId(String priceComponentTypeId) throws RemoteException;
  
  /** Get the value of the PARTY_ID column of the PRICE_COMPONENT table. */
  public String getPartyId() throws RemoteException;
  /** Set the value of the PARTY_ID column of the PRICE_COMPONENT table. */
  public void setPartyId(String partyId) throws RemoteException;
  
  /** Get the value of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getPartyTypeId() throws RemoteException;
  /** Set the value of the PARTY_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setPartyTypeId(String partyTypeId) throws RemoteException;
  
  /** Get the value of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public String getProductId() throws RemoteException;
  /** Set the value of the PRODUCT_ID column of the PRICE_COMPONENT table. */
  public void setProductId(String productId) throws RemoteException;
  
  /** Get the value of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public String getProductFeatureId() throws RemoteException;
  /** Set the value of the PRODUCT_FEATURE_ID column of the PRICE_COMPONENT table. */
  public void setProductFeatureId(String productFeatureId) throws RemoteException;
  
  /** Get the value of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public String getProductCategoryId() throws RemoteException;
  /** Set the value of the PRODUCT_CATEGORY_ID column of the PRICE_COMPONENT table. */
  public void setProductCategoryId(String productCategoryId) throws RemoteException;
  
  /** Get the value of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public String getAgreementId() throws RemoteException;
  /** Set the value of the AGREEMENT_ID column of the PRICE_COMPONENT table. */
  public void setAgreementId(String agreementId) throws RemoteException;
  
  /** Get the value of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public String getAgreementItemSeqId() throws RemoteException;
  /** Set the value of the AGREEMENT_ITEM_SEQ_ID column of the PRICE_COMPONENT table. */
  public void setAgreementItemSeqId(String agreementItemSeqId) throws RemoteException;
  
  /** Get the value of the UOM_ID column of the PRICE_COMPONENT table. */
  public String getUomId() throws RemoteException;
  /** Set the value of the UOM_ID column of the PRICE_COMPONENT table. */
  public void setUomId(String uomId) throws RemoteException;
  
  /** Get the value of the GEO_ID column of the PRICE_COMPONENT table. */
  public String getGeoId() throws RemoteException;
  /** Set the value of the GEO_ID column of the PRICE_COMPONENT table. */
  public void setGeoId(String geoId) throws RemoteException;
  
  /** Get the value of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public String getSaleTypeId() throws RemoteException;
  /** Set the value of the SALE_TYPE_ID column of the PRICE_COMPONENT table. */
  public void setSaleTypeId(String saleTypeId) throws RemoteException;
  
  /** Get the value of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public String getOrderValueBreakId() throws RemoteException;
  /** Set the value of the ORDER_VALUE_BREAK_ID column of the PRICE_COMPONENT table. */
  public void setOrderValueBreakId(String orderValueBreakId) throws RemoteException;
  
  /** Get the value of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public String getQuantityBreakId() throws RemoteException;
  /** Set the value of the QUANTITY_BREAK_ID column of the PRICE_COMPONENT table. */
  public void setQuantityBreakId(String quantityBreakId) throws RemoteException;
  
  /** Get the value of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public String getUtilizationUomId() throws RemoteException;
  /** Set the value of the UTILIZATION_UOM_ID column of the PRICE_COMPONENT table. */
  public void setUtilizationUomId(String utilizationUomId) throws RemoteException;
  
  /** Get the value of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public Double getUtilizationQuantity() throws RemoteException;
  /** Set the value of the UTILIZATION_QUANTITY column of the PRICE_COMPONENT table. */
  public void setUtilizationQuantity(Double utilizationQuantity) throws RemoteException;
  
  /** Get the value of the FROM_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date getFromDate() throws RemoteException;
  /** Set the value of the FROM_DATE column of the PRICE_COMPONENT table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException;
  
  /** Get the value of the THRU_DATE column of the PRICE_COMPONENT table. */
  public java.util.Date getThruDate() throws RemoteException;
  /** Set the value of the THRU_DATE column of the PRICE_COMPONENT table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException;
  
  /** Get the value of the PRICE column of the PRICE_COMPONENT table. */
  public Double getPrice() throws RemoteException;
  /** Set the value of the PRICE column of the PRICE_COMPONENT table. */
  public void setPrice(Double price) throws RemoteException;
  
  /** Get the value of the PERCENT column of the PRICE_COMPONENT table. */
  public Double getPercent() throws RemoteException;
  /** Set the value of the PERCENT column of the PRICE_COMPONENT table. */
  public void setPercent(Double percent) throws RemoteException;
  
  /** Get the value of the COMMENT column of the PRICE_COMPONENT table. */
  public String getComment() throws RemoteException;
  /** Set the value of the COMMENT column of the PRICE_COMPONENT table. */
  public void setComment(String comment) throws RemoteException;
  

  /** Get the value object of this PriceComponent class. */
  public PriceComponent getValueObject() throws RemoteException;
  /** Set the values in the value object of this PriceComponent class. */
  public void setValueObject(PriceComponent priceComponentValue) throws RemoteException;


  /** Get the  PriceComponentType entity corresponding to this entity. */
  public PriceComponentType getPriceComponentType() throws RemoteException;
  /** Remove the  PriceComponentType entity corresponding to this entity. */
  public void removePriceComponentType() throws RemoteException;  

  /** Get a collection of  PriceComponentTypeAttr related entities. */
  public Collection getPriceComponentTypeAttrs() throws RemoteException;
  /** Get the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentTypeAttr getPriceComponentTypeAttr(String name) throws RemoteException;
  /** Remove  PriceComponentTypeAttr related entities. */
  public void removePriceComponentTypeAttrs() throws RemoteException;
  /** Remove the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentTypeAttr(String name) throws RemoteException;

  /** Get a collection of  PriceComponentAttribute related entities. */
  public Collection getPriceComponentAttributes() throws RemoteException;
  /** Get the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentAttribute getPriceComponentAttribute(String name) throws RemoteException;
  /** Remove  PriceComponentAttribute related entities. */
  public void removePriceComponentAttributes() throws RemoteException;
  /** Remove the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentAttribute(String name) throws RemoteException;

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get the  PartyType entity corresponding to this entity. */
  public PartyType getPartyType() throws RemoteException;
  /** Remove the  PartyType entity corresponding to this entity. */
  public void removePartyType() throws RemoteException;  

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() throws RemoteException;
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() throws RemoteException;  

  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() throws RemoteException;
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() throws RemoteException;  

  /** Get the  Uom entity corresponding to this entity. */
  public Uom getUom() throws RemoteException;
  /** Remove the  Uom entity corresponding to this entity. */
  public void removeUom() throws RemoteException;  

  /** Get the  Geo entity corresponding to this entity. */
  public Geo getGeo() throws RemoteException;
  /** Remove the  Geo entity corresponding to this entity. */
  public void removeGeo() throws RemoteException;  

  /** Get the  SaleType entity corresponding to this entity. */
  public SaleType getSaleType() throws RemoteException;
  /** Remove the  SaleType entity corresponding to this entity. */
  public void removeSaleType() throws RemoteException;  

  /** Get the  OrderValueBreak entity corresponding to this entity. */
  public OrderValueBreak getOrderValueBreak() throws RemoteException;
  /** Remove the  OrderValueBreak entity corresponding to this entity. */
  public void removeOrderValueBreak() throws RemoteException;  

  /** Get the  QuantityBreak entity corresponding to this entity. */
  public QuantityBreak getQuantityBreak() throws RemoteException;
  /** Remove the  QuantityBreak entity corresponding to this entity. */
  public void removeQuantityBreak() throws RemoteException;  

  /** Get the Utilization Uom entity corresponding to this entity. */
  public Uom getUtilizationUom() throws RemoteException;
  /** Remove the Utilization Uom entity corresponding to this entity. */
  public void removeUtilizationUom() throws RemoteException;  

}
