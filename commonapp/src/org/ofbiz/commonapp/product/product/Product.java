
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

public interface Product extends EJBObject
{
  /** Get the primary key of the PRODUCT_ID column of the PRODUCT table. */
  public String getProductId() throws RemoteException;
  
  /** Get the value of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public String getPrimaryProductCategoryId() throws RemoteException;
  /** Set the value of the PRIMARY_PRODUCT_CATEGORY_ID column of the PRODUCT table. */
  public void setPrimaryProductCategoryId(String primaryProductCategoryId) throws RemoteException;
  
  /** Get the value of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public String getManufacturerPartyId() throws RemoteException;
  /** Set the value of the MANUFACTURER_PARTY_ID column of the PRODUCT table. */
  public void setManufacturerPartyId(String manufacturerPartyId) throws RemoteException;
  
  /** Get the value of the UOM_ID column of the PRODUCT table. */
  public String getUomId() throws RemoteException;
  /** Set the value of the UOM_ID column of the PRODUCT table. */
  public void setUomId(String uomId) throws RemoteException;
  
  /** Get the value of the QUANTITY_INCLUDED column of the PRODUCT table. */
  public Double getQuantityIncluded() throws RemoteException;
  /** Set the value of the QUANTITY_INCLUDED column of the PRODUCT table. */
  public void setQuantityIncluded(Double quantityIncluded) throws RemoteException;
  
  /** Get the value of the INTRODUCTION_DATE column of the PRODUCT table. */
  public java.util.Date getIntroductionDate() throws RemoteException;
  /** Set the value of the INTRODUCTION_DATE column of the PRODUCT table. */
  public void setIntroductionDate(java.util.Date introductionDate) throws RemoteException;
  
  /** Get the value of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date getSalesDiscontinuationDate() throws RemoteException;
  /** Set the value of the SALES_DISCONTINUATION_DATE column of the PRODUCT table. */
  public void setSalesDiscontinuationDate(java.util.Date salesDiscontinuationDate) throws RemoteException;
  
  /** Get the value of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public java.util.Date getSupportDiscontinuationDate() throws RemoteException;
  /** Set the value of the SUPPORT_DISCONTINUATION_DATE column of the PRODUCT table. */
  public void setSupportDiscontinuationDate(java.util.Date supportDiscontinuationDate) throws RemoteException;
  
  /** Get the value of the NAME column of the PRODUCT table. */
  public String getName() throws RemoteException;
  /** Set the value of the NAME column of the PRODUCT table. */
  public void setName(String name) throws RemoteException;
  
  /** Get the value of the COMMENT column of the PRODUCT table. */
  public String getComment() throws RemoteException;
  /** Set the value of the COMMENT column of the PRODUCT table. */
  public void setComment(String comment) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT table. */
  public void setDescription(String description) throws RemoteException;
  
  /** Get the value of the LONG_DESCRIPTION column of the PRODUCT table. */
  public String getLongDescription() throws RemoteException;
  /** Set the value of the LONG_DESCRIPTION column of the PRODUCT table. */
  public void setLongDescription(String longDescription) throws RemoteException;
  
  /** Get the value of the SMALL_IMAGE_URL column of the PRODUCT table. */
  public String getSmallImageUrl() throws RemoteException;
  /** Set the value of the SMALL_IMAGE_URL column of the PRODUCT table. */
  public void setSmallImageUrl(String smallImageUrl) throws RemoteException;
  
  /** Get the value of the LARGE_IMAGE_URL column of the PRODUCT table. */
  public String getLargeImageUrl() throws RemoteException;
  /** Set the value of the LARGE_IMAGE_URL column of the PRODUCT table. */
  public void setLargeImageUrl(String largeImageUrl) throws RemoteException;
  
  /** Get the value of the DEFAULT_PRICE column of the PRODUCT table. */
  public Double getDefaultPrice() throws RemoteException;
  /** Set the value of the DEFAULT_PRICE column of the PRODUCT table. */
  public void setDefaultPrice(Double defaultPrice) throws RemoteException;
  

  /** Get the value object of this Product class. */
  public Product getValueObject() throws RemoteException;
  /** Set the values in the value object of this Product class. */
  public void setValueObject(Product productValue) throws RemoteException;


  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() throws RemoteException;
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productTypeId) throws RemoteException;
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() throws RemoteException;
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productTypeId) throws RemoteException;

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() throws RemoteException;
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String name) throws RemoteException;
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() throws RemoteException;
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String name) throws RemoteException;

  /** Get the Primary ProductCategory entity corresponding to this entity. */
  public ProductCategory getPrimaryProductCategory() throws RemoteException;
  /** Remove the Primary ProductCategory entity corresponding to this entity. */
  public void removePrimaryProductCategory() throws RemoteException;  

  /** Get a collection of  ProductCategoryMember related entities. */
  public Collection getProductCategoryMembers() throws RemoteException;
  /** Get the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryMember getProductCategoryMember(String productCategoryId) throws RemoteException;
  /** Remove  ProductCategoryMember related entities. */
  public void removeProductCategoryMembers() throws RemoteException;
  /** Remove the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryMember(String productCategoryId) throws RemoteException;

  /** Get the Manufacturer Party entity corresponding to this entity. */
  public Party getManufacturerParty() throws RemoteException;
  /** Remove the Manufacturer Party entity corresponding to this entity. */
  public void removeManufacturerParty() throws RemoteException;  

  /** Get the Quantity Uom entity corresponding to this entity. */
  public Uom getQuantityUom() throws RemoteException;
  /** Remove the Quantity Uom entity corresponding to this entity. */
  public void removeQuantityUom() throws RemoteException;  

  /** Get a collection of  GoodIdentification related entities. */
  public Collection getGoodIdentifications() throws RemoteException;
  /** Get the  GoodIdentification keyed by member(s) of this class, and other passed parameters. */
  public GoodIdentification getGoodIdentification(String goodIdentificationTypeId) throws RemoteException;
  /** Remove  GoodIdentification related entities. */
  public void removeGoodIdentifications() throws RemoteException;
  /** Remove the  GoodIdentification keyed by member(s) of this class, and other passed parameters. */
  public void removeGoodIdentification(String goodIdentificationTypeId) throws RemoteException;

  /** Get a collection of  ProductDataObject related entities. */
  public Collection getProductDataObjects() throws RemoteException;
  /** Get the  ProductDataObject keyed by member(s) of this class, and other passed parameters. */
  public ProductDataObject getProductDataObject(String dataObjectId) throws RemoteException;
  /** Remove  ProductDataObject related entities. */
  public void removeProductDataObjects() throws RemoteException;
  /** Remove the  ProductDataObject keyed by member(s) of this class, and other passed parameters. */
  public void removeProductDataObject(String dataObjectId) throws RemoteException;

  /** Get a collection of Main ProductAssoc related entities. */
  public Collection getMainProductAssocs() throws RemoteException;
  /** Get the Main ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public ProductAssoc getMainProductAssoc(String productIdTo, String productAssocTypeId) throws RemoteException;
  /** Remove Main ProductAssoc related entities. */
  public void removeMainProductAssocs() throws RemoteException;
  /** Remove the Main ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeMainProductAssoc(String productIdTo, String productAssocTypeId) throws RemoteException;

  /** Get a collection of Assoc ProductAssoc related entities. */
  public Collection getAssocProductAssocs() throws RemoteException;
  /** Get the Assoc ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public ProductAssoc getAssocProductAssoc(String productId, String productAssocTypeId) throws RemoteException;
  /** Remove Assoc ProductAssoc related entities. */
  public void removeAssocProductAssocs() throws RemoteException;
  /** Remove the Assoc ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocProductAssoc(String productId, String productAssocTypeId) throws RemoteException;

  /** Get a collection of  ProductFeatureAppl related entities. */
  public Collection getProductFeatureAppls() throws RemoteException;
  /** Get the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureAppl getProductFeatureAppl(String productFeatureId) throws RemoteException;
  /** Remove  ProductFeatureAppl related entities. */
  public void removeProductFeatureAppls() throws RemoteException;
  /** Remove the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureAppl(String productFeatureId) throws RemoteException;

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() throws RemoteException;
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) throws RemoteException;
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() throws RemoteException;
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) throws RemoteException;

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() throws RemoteException;
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() throws RemoteException;
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) throws RemoteException;

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() throws RemoteException;
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) throws RemoteException;
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() throws RemoteException;
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) throws RemoteException;

  /** Get a collection of  SupplierProduct related entities. */
  public Collection getSupplierProducts() throws RemoteException;
  /** Get the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public SupplierProduct getSupplierProduct(String partyId) throws RemoteException;
  /** Remove  SupplierProduct related entities. */
  public void removeSupplierProducts() throws RemoteException;
  /** Remove the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public void removeSupplierProduct(String partyId) throws RemoteException;

}
