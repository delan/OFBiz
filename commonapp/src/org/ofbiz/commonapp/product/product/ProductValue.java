
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.category.*;
import org.ofbiz.commonapp.party.party.*;
import org.ofbiz.commonapp.common.uom.*;
import org.ofbiz.commonapp.product.feature.*;
import org.ofbiz.commonapp.product.cost.*;
import org.ofbiz.commonapp.product.price.*;
import org.ofbiz.commonapp.product.inventory.*;
import org.ofbiz.commonapp.product.supplier.*;

/**
 * <p><b>Title:</b> Product Entity
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */
public class ProductValue implements Product
{
  /** The variable of the PRODUCT_ID column of the PRODUCT table. */
  private String productId;
  /** The variable of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  private String primaryProductCategoryId;
  /** The variable of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  private String manufacturerPartyId;
  /** The variable of the UOM_ID column of the PRODUCT table. */
  private String uomId;
  /** The variable of the QUANTITY_INCLUDED column of the PRODUCT table. */
  private Double quantityIncluded;
  /** The variable of the INTRODUCTION_DATE column of the PRODUCT table. */
  private java.util.Date introductionDate;
  /** The variable of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  private java.util.Date salesDiscontinuationDate;
  /** The variable of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  private java.util.Date supportDiscontinuationDate;
  /** The variable of the NAME column of the PRODUCT table. */
  private String name;
  /** The variable of the COMMENT column of the PRODUCT table. */
  private String comment;
  /** The variable of the DESCRIPTION column of the PRODUCT table. */
  private String description;
  /** The variable of the LONG_DESCRIPTION column of the PRODUCT table. */
  private String longDescription;
  /** The variable of the SMALL_IMAGE_URL column of the PRODUCT table. */
  private String smallImageUrl;
  /** The variable of the LARGE_IMAGE_URL column of the PRODUCT table. */
  private String largeImageUrl;
  /** The variable of the DEFAULT_PRICE column of the PRODUCT table. */
  private Double defaultPrice;

  private Product product;

  public ProductValue()
  {
    this.productId = null;
    this.primaryProductCategoryId = null;
    this.manufacturerPartyId = null;
    this.uomId = null;
    this.quantityIncluded = null;
    this.introductionDate = null;
    this.salesDiscontinuationDate = null;
    this.supportDiscontinuationDate = null;
    this.name = null;
    this.comment = null;
    this.description = null;
    this.longDescription = null;
    this.smallImageUrl = null;
    this.largeImageUrl = null;
    this.defaultPrice = null;

    this.product = null;
  }

  public ProductValue(Product product) throws RemoteException
  {
    if(product == null) return;
  
    this.productId = product.getProductId();
    this.primaryProductCategoryId = product.getPrimaryProductCategoryId();
    this.manufacturerPartyId = product.getManufacturerPartyId();
    this.uomId = product.getUomId();
    this.quantityIncluded = product.getQuantityIncluded();
    this.introductionDate = product.getIntroductionDate();
    this.salesDiscontinuationDate = product.getSalesDiscontinuationDate();
    this.supportDiscontinuationDate = product.getSupportDiscontinuationDate();
    this.name = product.getName();
    this.comment = product.getComment();
    this.description = product.getDescription();
    this.longDescription = product.getLongDescription();
    this.smallImageUrl = product.getSmallImageUrl();
    this.largeImageUrl = product.getLargeImageUrl();
    this.defaultPrice = product.getDefaultPrice();

    this.product = product;
  }

  public ProductValue(Product product, String productId, String primaryProductCategoryId, String manufacturerPartyId, String uomId, Double quantityIncluded, java.util.Date introductionDate, java.util.Date salesDiscontinuationDate, java.util.Date supportDiscontinuationDate, String name, String comment, String description, String longDescription, String smallImageUrl, String largeImageUrl, Double defaultPrice)
  {
    if(product == null) return;
  
    this.productId = productId;
    this.primaryProductCategoryId = primaryProductCategoryId;
    this.manufacturerPartyId = manufacturerPartyId;
    this.uomId = uomId;
    this.quantityIncluded = quantityIncluded;
    this.introductionDate = introductionDate;
    this.salesDiscontinuationDate = salesDiscontinuationDate;
    this.supportDiscontinuationDate = supportDiscontinuationDate;
    this.name = name;
    this.comment = comment;
    this.description = description;
    this.longDescription = longDescription;
    this.smallImageUrl = smallImageUrl;
    this.largeImageUrl = largeImageUrl;
    this.defaultPrice = defaultPrice;

    this.product = product;
  }


  /** Get the primary key of the PRODUCT_ID column of the PRODUCT table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the value of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public String getPrimaryProductCategoryId() throws RemoteException { return primaryProductCategoryId; }
  /** Set the value of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public void setPrimaryProductCategoryId(String primaryProductCategoryId) throws RemoteException
  {
    this.primaryProductCategoryId = primaryProductCategoryId;
    if(product!=null) product.setPrimaryProductCategoryId(primaryProductCategoryId);
  }

  /** Get the value of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public String getManufacturerPartyId() throws RemoteException { return manufacturerPartyId; }
  /** Set the value of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public void setManufacturerPartyId(String manufacturerPartyId) throws RemoteException
  {
    this.manufacturerPartyId = manufacturerPartyId;
    if(product!=null) product.setManufacturerPartyId(manufacturerPartyId);
  }

  /** Get the value of the UOM_ID column of the PRODUCT table. */
  public String getUomId() throws RemoteException { return uomId; }
  /** Set the value of the UOM_ID column of the PRODUCT table. */
  public void setUomId(String uomId) throws RemoteException
  {
    this.uomId = uomId;
    if(product!=null) product.setUomId(uomId);
  }

  /** Get the value of the QUANTITY_INCLUDED column of the PRODUCT table. */
  public Double getQuantityIncluded() throws RemoteException { return quantityIncluded; }
  /** Set the value of the QUANTITY_INCLUDED column of the PRODUCT table. */
  public void setQuantityIncluded(Double quantityIncluded) throws RemoteException
  {
    this.quantityIncluded = quantityIncluded;
    if(product!=null) product.setQuantityIncluded(quantityIncluded);
  }

  /** Get the value of the INTRODUCTION_DATE column of the PRODUCT table. */
  public java.util.Date getIntroductionDate() throws RemoteException { return introductionDate; }
  /** Set the value of the INTRODUCTION_DATE column of the PRODUCT table. */
  public void setIntroductionDate(java.util.Date introductionDate) throws RemoteException
  {
    this.introductionDate = introductionDate;
    if(product!=null) product.setIntroductionDate(introductionDate);
  }

  /** Get the value of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date getSalesDiscontinuationDate() throws RemoteException { return salesDiscontinuationDate; }
  /** Set the value of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public void setSalesDiscontinuationDate(java.util.Date salesDiscontinuationDate) throws RemoteException
  {
    this.salesDiscontinuationDate = salesDiscontinuationDate;
    if(product!=null) product.setSalesDiscontinuationDate(salesDiscontinuationDate);
  }

  /** Get the value of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date getSupportDiscontinuationDate() throws RemoteException { return supportDiscontinuationDate; }
  /** Set the value of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public void setSupportDiscontinuationDate(java.util.Date supportDiscontinuationDate) throws RemoteException
  {
    this.supportDiscontinuationDate = supportDiscontinuationDate;
    if(product!=null) product.setSupportDiscontinuationDate(supportDiscontinuationDate);
  }

  /** Get the value of the NAME column of the PRODUCT table. */
  public String getName() throws RemoteException { return name; }
  /** Set the value of the NAME column of the PRODUCT table. */
  public void setName(String name) throws RemoteException
  {
    this.name = name;
    if(product!=null) product.setName(name);
  }

  /** Get the value of the COMMENT column of the PRODUCT table. */
  public String getComment() throws RemoteException { return comment; }
  /** Set the value of the COMMENT column of the PRODUCT table. */
  public void setComment(String comment) throws RemoteException
  {
    this.comment = comment;
    if(product!=null) product.setComment(comment);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(product!=null) product.setDescription(description);
  }

  /** Get the value of the LONG_DESCRIPTION column of the PRODUCT table. */
  public String getLongDescription() throws RemoteException { return longDescription; }
  /** Set the value of the LONG_DESCRIPTION column of the PRODUCT table. */
  public void setLongDescription(String longDescription) throws RemoteException
  {
    this.longDescription = longDescription;
    if(product!=null) product.setLongDescription(longDescription);
  }

  /** Get the value of the SMALL_IMAGE_URL column of the PRODUCT table. */
  public String getSmallImageUrl() throws RemoteException { return smallImageUrl; }
  /** Set the value of the SMALL_IMAGE_URL column of the PRODUCT table. */
  public void setSmallImageUrl(String smallImageUrl) throws RemoteException
  {
    this.smallImageUrl = smallImageUrl;
    if(product!=null) product.setSmallImageUrl(smallImageUrl);
  }

  /** Get the value of the LARGE_IMAGE_URL column of the PRODUCT table. */
  public String getLargeImageUrl() throws RemoteException { return largeImageUrl; }
  /** Set the value of the LARGE_IMAGE_URL column of the PRODUCT table. */
  public void setLargeImageUrl(String largeImageUrl) throws RemoteException
  {
    this.largeImageUrl = largeImageUrl;
    if(product!=null) product.setLargeImageUrl(largeImageUrl);
  }

  /** Get the value of the DEFAULT_PRICE column of the PRODUCT table. */
  public Double getDefaultPrice() throws RemoteException { return defaultPrice; }
  /** Set the value of the DEFAULT_PRICE column of the PRODUCT table. */
  public void setDefaultPrice(Double defaultPrice) throws RemoteException
  {
    this.defaultPrice = defaultPrice;
    if(product!=null) product.setDefaultPrice(defaultPrice);
  }

  /** Get the value object of the Product class. */
  public Product getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Product class. */
  public void setValueObject(Product valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(product!=null) product.setValueObject(valueObject);

    if(productId == null) productId = valueObject.getProductId();
    primaryProductCategoryId = valueObject.getPrimaryProductCategoryId();
    manufacturerPartyId = valueObject.getManufacturerPartyId();
    uomId = valueObject.getUomId();
    quantityIncluded = valueObject.getQuantityIncluded();
    introductionDate = valueObject.getIntroductionDate();
    salesDiscontinuationDate = valueObject.getSalesDiscontinuationDate();
    supportDiscontinuationDate = valueObject.getSupportDiscontinuationDate();
    name = valueObject.getName();
    comment = valueObject.getComment();
    description = valueObject.getDescription();
    longDescription = valueObject.getLongDescription();
    smallImageUrl = valueObject.getSmallImageUrl();
    largeImageUrl = valueObject.getLargeImageUrl();
    defaultPrice = valueObject.getDefaultPrice();
  }


  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() { return ProductClassHelper.findByProductId(productId); }
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productTypeId) { return ProductClassHelper.findByPrimaryKey(productId, productTypeId); }
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() { ProductClassHelper.removeByProductId(productId); }
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productTypeId) { ProductClassHelper.removeByPrimaryKey(productId, productTypeId); }

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() { return ProductAttributeHelper.findByProductId(productId); }
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String name) { return ProductAttributeHelper.findByPrimaryKey(productId, name); }
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() { ProductAttributeHelper.removeByProductId(productId); }
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String name) { ProductAttributeHelper.removeByPrimaryKey(productId, name); }

  /** Get the Primary ProductCategory entity corresponding to this entity. */
  public ProductCategory getPrimaryProductCategory() { return ProductCategoryHelper.findByPrimaryKey(primaryProductCategoryId); }
  /** Remove the Primary ProductCategory entity corresponding to this entity. */
  public void removePrimaryProductCategory() { ProductCategoryHelper.removeByPrimaryKey(primaryProductCategoryId); }

  /** Get a collection of  ProductCategoryMember related entities. */
  public Collection getProductCategoryMembers() { return ProductCategoryMemberHelper.findByProductId(productId); }
  /** Get the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryMember getProductCategoryMember(String productCategoryId) { return ProductCategoryMemberHelper.findByPrimaryKey(productCategoryId, productId); }
  /** Remove  ProductCategoryMember related entities. */
  public void removeProductCategoryMembers() { ProductCategoryMemberHelper.removeByProductId(productId); }
  /** Remove the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryMember(String productCategoryId) { ProductCategoryMemberHelper.removeByPrimaryKey(productCategoryId, productId); }

  /** Get the Manufacturer Party entity corresponding to this entity. */
  public Party getManufacturerParty() { return PartyHelper.findByPrimaryKey(manufacturerPartyId); }
  /** Remove the Manufacturer Party entity corresponding to this entity. */
  public void removeManufacturerParty() { PartyHelper.removeByPrimaryKey(manufacturerPartyId); }

  /** Get the Quantity Uom entity corresponding to this entity. */
  public Uom getQuantityUom() { return UomHelper.findByPrimaryKey(uomId); }
  /** Remove the Quantity Uom entity corresponding to this entity. */
  public void removeQuantityUom() { UomHelper.removeByPrimaryKey(uomId); }

  /** Get a collection of  GoodIdentification related entities. */
  public Collection getGoodIdentifications() { return GoodIdentificationHelper.findByProductId(productId); }
  /** Get the  GoodIdentification keyed by member(s) of this class, and other passed parameters. */
  public GoodIdentification getGoodIdentification(String goodIdentificationTypeId) { return GoodIdentificationHelper.findByPrimaryKey(goodIdentificationTypeId, productId); }
  /** Remove  GoodIdentification related entities. */
  public void removeGoodIdentifications() { GoodIdentificationHelper.removeByProductId(productId); }
  /** Remove the  GoodIdentification keyed by member(s) of this class, and other passed parameters. */
  public void removeGoodIdentification(String goodIdentificationTypeId) { GoodIdentificationHelper.removeByPrimaryKey(goodIdentificationTypeId, productId); }

  /** Get a collection of  ProductDataObject related entities. */
  public Collection getProductDataObjects() { return ProductDataObjectHelper.findByProductId(productId); }
  /** Get the  ProductDataObject keyed by member(s) of this class, and other passed parameters. */
  public ProductDataObject getProductDataObject(String dataObjectId) { return ProductDataObjectHelper.findByPrimaryKey(dataObjectId, productId); }
  /** Remove  ProductDataObject related entities. */
  public void removeProductDataObjects() { ProductDataObjectHelper.removeByProductId(productId); }
  /** Remove the  ProductDataObject keyed by member(s) of this class, and other passed parameters. */
  public void removeProductDataObject(String dataObjectId) { ProductDataObjectHelper.removeByPrimaryKey(dataObjectId, productId); }

  /** Get a collection of Main ProductAssoc related entities. */
  public Collection getMainProductAssocs() { return ProductAssocHelper.findByProductId(productId); }
  /** Get the Main ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public ProductAssoc getMainProductAssoc(String productIdTo, String productAssocTypeId) { return ProductAssocHelper.findByPrimaryKey(productId, productIdTo, productAssocTypeId); }
  /** Remove Main ProductAssoc related entities. */
  public void removeMainProductAssocs() { ProductAssocHelper.removeByProductId(productId); }
  /** Remove the Main ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeMainProductAssoc(String productIdTo, String productAssocTypeId) { ProductAssocHelper.removeByPrimaryKey(productId, productIdTo, productAssocTypeId); }

  /** Get a collection of Assoc ProductAssoc related entities. */
  public Collection getAssocProductAssocs() { return ProductAssocHelper.findByProductIdTo(productId); }
  /** Get the Assoc ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public ProductAssoc getAssocProductAssoc(String productId, String productAssocTypeId) { return ProductAssocHelper.findByPrimaryKey(productId, productId, productAssocTypeId); }
  /** Remove Assoc ProductAssoc related entities. */
  public void removeAssocProductAssocs() { ProductAssocHelper.removeByProductIdTo(productId); }
  /** Remove the Assoc ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocProductAssoc(String productId, String productAssocTypeId) { ProductAssocHelper.removeByPrimaryKey(productId, productId, productAssocTypeId); }

  /** Get a collection of  ProductFeatureAppl related entities. */
  public Collection getProductFeatureAppls() { return ProductFeatureApplHelper.findByProductId(productId); }
  /** Get the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureAppl getProductFeatureAppl(String productFeatureId) { return ProductFeatureApplHelper.findByPrimaryKey(productId, productFeatureId); }
  /** Remove  ProductFeatureAppl related entities. */
  public void removeProductFeatureAppls() { ProductFeatureApplHelper.removeByProductId(productId); }
  /** Remove the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureAppl(String productFeatureId) { ProductFeatureApplHelper.removeByPrimaryKey(productId, productFeatureId); }

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() { return CostComponentHelper.findByProductId(productId); }
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) { return CostComponentHelper.findByPrimaryKey(costComponentId); }
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() { CostComponentHelper.removeByProductId(productId); }
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) { CostComponentHelper.removeByPrimaryKey(costComponentId); }

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByProductId(productId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByProductId(productId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByProductId(productId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByProductId(productId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get a collection of  SupplierProduct related entities. */
  public Collection getSupplierProducts() { return SupplierProductHelper.findByProductId(productId); }
  /** Get the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public SupplierProduct getSupplierProduct(String partyId) { return SupplierProductHelper.findByPrimaryKey(productId, partyId); }
  /** Remove  SupplierProduct related entities. */
  public void removeSupplierProducts() { SupplierProductHelper.removeByProductId(productId); }
  /** Remove the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public void removeSupplierProduct(String partyId) { SupplierProductHelper.removeByPrimaryKey(productId, partyId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(product!=null) return product.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(product!=null) return product.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(product!=null) return product.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(product!=null) return product.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(product!=null) product.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
