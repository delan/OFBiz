
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

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
public class ProductBean implements EntityBean
{
  /** The variable for the PRODUCT_ID column of the PRODUCT table. */
  public String productId;
  /** The variable for the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public String primaryProductCategoryId;
  /** The variable for the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public String manufacturerPartyId;
  /** The variable for the UOM_ID column of the PRODUCT table. */
  public String uomId;
  /** The variable for the QUANTITY_INCLUDED column of the PRODUCT table. */
  public Double quantityIncluded;
  /** The variable for the INTRODUCTION_DATE column of the PRODUCT table. */
  public java.util.Date introductionDate;
  /** The variable for the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date salesDiscontinuationDate;
  /** The variable for the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date supportDiscontinuationDate;
  /** The variable for the NAME column of the PRODUCT table. */
  public String name;
  /** The variable for the COMMENT column of the PRODUCT table. */
  public String comment;
  /** The variable for the DESCRIPTION column of the PRODUCT table. */
  public String description;
  /** The variable for the LONG_DESCRIPTION column of the PRODUCT table. */
  public String longDescription;
  /** The variable for the SMALL_IMAGE_URL column of the PRODUCT table. */
  public String smallImageUrl;
  /** The variable for the LARGE_IMAGE_URL column of the PRODUCT table. */
  public String largeImageUrl;
  /** The variable for the DEFAULT_PRICE column of the PRODUCT table. */
  public Double defaultPrice;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_ID column of the PRODUCT table. */
  public String getProductId() { return productId; }

  /** Get the value of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public String getPrimaryProductCategoryId() { return primaryProductCategoryId; }
  /** Set the value of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public void setPrimaryProductCategoryId(String primaryProductCategoryId)
  {
    this.primaryProductCategoryId = primaryProductCategoryId;
    ejbIsModified = true;
  }

  /** Get the value of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public String getManufacturerPartyId() { return manufacturerPartyId; }
  /** Set the value of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public void setManufacturerPartyId(String manufacturerPartyId)
  {
    this.manufacturerPartyId = manufacturerPartyId;
    ejbIsModified = true;
  }

  /** Get the value of the UOM_ID column of the PRODUCT table. */
  public String getUomId() { return uomId; }
  /** Set the value of the UOM_ID column of the PRODUCT table. */
  public void setUomId(String uomId)
  {
    this.uomId = uomId;
    ejbIsModified = true;
  }

  /** Get the value of the QUANTITY_INCLUDED column of the PRODUCT table. */
  public Double getQuantityIncluded() { return quantityIncluded; }
  /** Set the value of the QUANTITY_INCLUDED column of the PRODUCT table. */
  public void setQuantityIncluded(Double quantityIncluded)
  {
    this.quantityIncluded = quantityIncluded;
    ejbIsModified = true;
  }

  /** Get the value of the INTRODUCTION_DATE column of the PRODUCT table. */
  public java.util.Date getIntroductionDate() { return introductionDate; }
  /** Set the value of the INTRODUCTION_DATE column of the PRODUCT table. */
  public void setIntroductionDate(java.util.Date introductionDate)
  {
    this.introductionDate = introductionDate;
    ejbIsModified = true;
  }

  /** Get the value of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date getSalesDiscontinuationDate() { return salesDiscontinuationDate; }
  /** Set the value of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public void setSalesDiscontinuationDate(java.util.Date salesDiscontinuationDate)
  {
    this.salesDiscontinuationDate = salesDiscontinuationDate;
    ejbIsModified = true;
  }

  /** Get the value of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date getSupportDiscontinuationDate() { return supportDiscontinuationDate; }
  /** Set the value of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public void setSupportDiscontinuationDate(java.util.Date supportDiscontinuationDate)
  {
    this.supportDiscontinuationDate = supportDiscontinuationDate;
    ejbIsModified = true;
  }

  /** Get the value of the NAME column of the PRODUCT table. */
  public String getName() { return name; }
  /** Set the value of the NAME column of the PRODUCT table. */
  public void setName(String name)
  {
    this.name = name;
    ejbIsModified = true;
  }

  /** Get the value of the COMMENT column of the PRODUCT table. */
  public String getComment() { return comment; }
  /** Set the value of the COMMENT column of the PRODUCT table. */
  public void setComment(String comment)
  {
    this.comment = comment;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Get the value of the LONG_DESCRIPTION column of the PRODUCT table. */
  public String getLongDescription() { return longDescription; }
  /** Set the value of the LONG_DESCRIPTION column of the PRODUCT table. */
  public void setLongDescription(String longDescription)
  {
    this.longDescription = longDescription;
    ejbIsModified = true;
  }

  /** Get the value of the SMALL_IMAGE_URL column of the PRODUCT table. */
  public String getSmallImageUrl() { return smallImageUrl; }
  /** Set the value of the SMALL_IMAGE_URL column of the PRODUCT table. */
  public void setSmallImageUrl(String smallImageUrl)
  {
    this.smallImageUrl = smallImageUrl;
    ejbIsModified = true;
  }

  /** Get the value of the LARGE_IMAGE_URL column of the PRODUCT table. */
  public String getLargeImageUrl() { return largeImageUrl; }
  /** Set the value of the LARGE_IMAGE_URL column of the PRODUCT table. */
  public void setLargeImageUrl(String largeImageUrl)
  {
    this.largeImageUrl = largeImageUrl;
    ejbIsModified = true;
  }

  /** Get the value of the DEFAULT_PRICE column of the PRODUCT table. */
  public Double getDefaultPrice() { return defaultPrice; }
  /** Set the value of the DEFAULT_PRICE column of the PRODUCT table. */
  public void setDefaultPrice(Double defaultPrice)
  {
    this.defaultPrice = defaultPrice;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Product valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getPrimaryProductCategoryId() != null)
      {
        this.primaryProductCategoryId = valueObject.getPrimaryProductCategoryId();
        ejbIsModified = true;
      }
      if(valueObject.getManufacturerPartyId() != null)
      {
        this.manufacturerPartyId = valueObject.getManufacturerPartyId();
        ejbIsModified = true;
      }
      if(valueObject.getUomId() != null)
      {
        this.uomId = valueObject.getUomId();
        ejbIsModified = true;
      }
      if(valueObject.getQuantityIncluded() != null)
      {
        this.quantityIncluded = valueObject.getQuantityIncluded();
        ejbIsModified = true;
      }
      if(valueObject.getIntroductionDate() != null)
      {
        this.introductionDate = valueObject.getIntroductionDate();
        ejbIsModified = true;
      }
      if(valueObject.getSalesDiscontinuationDate() != null)
      {
        this.salesDiscontinuationDate = valueObject.getSalesDiscontinuationDate();
        ejbIsModified = true;
      }
      if(valueObject.getSupportDiscontinuationDate() != null)
      {
        this.supportDiscontinuationDate = valueObject.getSupportDiscontinuationDate();
        ejbIsModified = true;
      }
      if(valueObject.getName() != null)
      {
        this.name = valueObject.getName();
        ejbIsModified = true;
      }
      if(valueObject.getComment() != null)
      {
        this.comment = valueObject.getComment();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
      if(valueObject.getLongDescription() != null)
      {
        this.longDescription = valueObject.getLongDescription();
        ejbIsModified = true;
      }
      if(valueObject.getSmallImageUrl() != null)
      {
        this.smallImageUrl = valueObject.getSmallImageUrl();
        ejbIsModified = true;
      }
      if(valueObject.getLargeImageUrl() != null)
      {
        this.largeImageUrl = valueObject.getLargeImageUrl();
        ejbIsModified = true;
      }
      if(valueObject.getDefaultPrice() != null)
      {
        this.defaultPrice = valueObject.getDefaultPrice();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductBean object
   *@return    The ValueObject value
   */
  public Product getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductValue((Product)this.entityContext.getEJBObject(), productId, primaryProductCategoryId, manufacturerPartyId, uomId, quantityIncluded, introductionDate, salesDiscontinuationDate, supportDiscontinuationDate, name, comment, description, longDescription, smallImageUrl, largeImageUrl, defaultPrice);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  primaryProductCategoryId                  Field of the PRIMARY_PRODUCT_CATEGORY_ID column.
   *@param  manufacturerPartyId                  Field of the MANUFACTURER_PARTY_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  quantityIncluded                  Field of the QUANTITY_INCLUDED column.
   *@param  introductionDate                  Field of the INTRODUCTION_DATE column.
   *@param  salesDiscontinuationDate                  Field of the SALES_DISCONTINUATION_DATE column.
   *@param  supportDiscontinuationDate                  Field of the SUPPORT_DISCONTINUATION_DATE column.
   *@param  name                  Field of the NAME column.
   *@param  comment                  Field of the COMMENT column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  longDescription                  Field of the LONG_DESCRIPTION column.
   *@param  smallImageUrl                  Field of the SMALL_IMAGE_URL column.
   *@param  largeImageUrl                  Field of the LARGE_IMAGE_URL column.
   *@param  defaultPrice                  Field of the DEFAULT_PRICE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productId, String primaryProductCategoryId, String manufacturerPartyId, String uomId, Double quantityIncluded, java.util.Date introductionDate, java.util.Date salesDiscontinuationDate, java.util.Date supportDiscontinuationDate, String name, String comment, String description, String longDescription, String smallImageUrl, String largeImageUrl, Double defaultPrice) throws CreateException
  {
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
    return null;
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productId) throws CreateException
  {
    return ejbCreate(productId, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  primaryProductCategoryId                  Field of the PRIMARY_PRODUCT_CATEGORY_ID column.
   *@param  manufacturerPartyId                  Field of the MANUFACTURER_PARTY_ID column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  quantityIncluded                  Field of the QUANTITY_INCLUDED column.
   *@param  introductionDate                  Field of the INTRODUCTION_DATE column.
   *@param  salesDiscontinuationDate                  Field of the SALES_DISCONTINUATION_DATE column.
   *@param  supportDiscontinuationDate                  Field of the SUPPORT_DISCONTINUATION_DATE column.
   *@param  name                  Field of the NAME column.
   *@param  comment                  Field of the COMMENT column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  longDescription                  Field of the LONG_DESCRIPTION column.
   *@param  smallImageUrl                  Field of the SMALL_IMAGE_URL column.
   *@param  largeImageUrl                  Field of the LARGE_IMAGE_URL column.
   *@param  defaultPrice                  Field of the DEFAULT_PRICE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String primaryProductCategoryId, String manufacturerPartyId, String uomId, Double quantityIncluded, java.util.Date introductionDate, java.util.Date salesDiscontinuationDate, java.util.Date supportDiscontinuationDate, String name, String comment, String description, String longDescription, String smallImageUrl, String largeImageUrl, Double defaultPrice) throws CreateException {}

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId) throws CreateException
  {
    ejbPostCreate(productId, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
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
