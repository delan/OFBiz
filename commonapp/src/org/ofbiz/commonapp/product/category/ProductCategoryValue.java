
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.product.price.*;
import org.ofbiz.commonapp.product.supplier.*;

/**
 * <p><b>Title:</b> Product Category Entity
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
 *@created    Fri Jul 27 01:18:26 MDT 2001
 *@version    1.0
 */
public class ProductCategoryValue implements ProductCategory
{
  /** The variable of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY table. */
  private String productCategoryId;
  /** The variable of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  private String description;

  private ProductCategory productCategory;

  public ProductCategoryValue()
  {
    this.productCategoryId = null;
    this.description = null;

    this.productCategory = null;
  }

  public ProductCategoryValue(ProductCategory productCategory) throws RemoteException
  {
    if(productCategory == null) return;
  
    this.productCategoryId = productCategory.getProductCategoryId();
    this.description = productCategory.getDescription();

    this.productCategory = productCategory;
  }

  public ProductCategoryValue(ProductCategory productCategory, String productCategoryId, String description)
  {
    if(productCategory == null) return;
  
    this.productCategoryId = productCategoryId;
    this.description = description;

    this.productCategory = productCategory;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY table. */
  public String getProductCategoryId()  throws RemoteException { return productCategoryId; }

  /** Get the value of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productCategory!=null) productCategory.setDescription(description);
  }

  /** Get the value object of the ProductCategory class. */
  public ProductCategory getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategory class. */
  public void setValueObject(ProductCategory valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategory!=null) productCategory.setValueObject(valueObject);

    if(productCategoryId == null) productCategoryId = valueObject.getProductCategoryId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  ProductCategoryClass related entities. */
  public Collection getProductCategoryClasss() { return ProductCategoryClassHelper.findByProductCategoryId(productCategoryId); }
  /** Get the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryClass getProductCategoryClass(String productCategoryTypeId) { return ProductCategoryClassHelper.findByPrimaryKey(productCategoryId, productCategoryTypeId); }
  /** Remove  ProductCategoryClass related entities. */
  public void removeProductCategoryClasss() { ProductCategoryClassHelper.removeByProductCategoryId(productCategoryId); }
  /** Remove the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryClass(String productCategoryTypeId) { ProductCategoryClassHelper.removeByPrimaryKey(productCategoryId, productCategoryTypeId); }

  /** Get a collection of  ProductCategoryAttribute related entities. */
  public Collection getProductCategoryAttributes() { return ProductCategoryAttributeHelper.findByProductCategoryId(productCategoryId); }
  /** Get the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryAttribute getProductCategoryAttribute(String name) { return ProductCategoryAttributeHelper.findByPrimaryKey(productCategoryId, name); }
  /** Remove  ProductCategoryAttribute related entities. */
  public void removeProductCategoryAttributes() { ProductCategoryAttributeHelper.removeByProductCategoryId(productCategoryId); }
  /** Remove the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryAttribute(String name) { ProductCategoryAttributeHelper.removeByPrimaryKey(productCategoryId, name); }

  /** Get a collection of  ProductCategoryMember related entities. */
  public Collection getProductCategoryMembers() { return ProductCategoryMemberHelper.findByProductCategoryId(productCategoryId); }
  /** Get the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryMember getProductCategoryMember(String productId) { return ProductCategoryMemberHelper.findByPrimaryKey(productCategoryId, productId); }
  /** Remove  ProductCategoryMember related entities. */
  public void removeProductCategoryMembers() { ProductCategoryMemberHelper.removeByProductCategoryId(productCategoryId); }
  /** Remove the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryMember(String productId) { ProductCategoryMemberHelper.removeByPrimaryKey(productCategoryId, productId); }

  /** Get a collection of  Product related entities. */
  public Collection getProducts() { return ProductHelper.findByPrimaryProductCategoryId(productCategoryId); }
  /** Get the  Product keyed by member(s) of this class, and other passed parameters. */
  public Product getProduct(String productId) { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove  Product related entities. */
  public void removeProducts() { ProductHelper.removeByPrimaryProductCategoryId(productCategoryId); }
  /** Remove the  Product keyed by member(s) of this class, and other passed parameters. */
  public void removeProduct(String productId) { ProductHelper.removeByPrimaryKey(productId); }

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByProductCategoryId(productCategoryId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByProductCategoryId(productCategoryId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }

  /** Get a collection of  MarketInterest related entities. */
  public Collection getMarketInterests() { return MarketInterestHelper.findByProductCategoryId(productCategoryId); }
  /** Get the  MarketInterest keyed by member(s) of this class, and other passed parameters. */
  public MarketInterest getMarketInterest(String partyTypeId) { return MarketInterestHelper.findByPrimaryKey(productCategoryId, partyTypeId); }
  /** Remove  MarketInterest related entities. */
  public void removeMarketInterests() { MarketInterestHelper.removeByProductCategoryId(productCategoryId); }
  /** Remove the  MarketInterest keyed by member(s) of this class, and other passed parameters. */
  public void removeMarketInterest(String partyTypeId) { MarketInterestHelper.removeByPrimaryKey(productCategoryId, partyTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategory!=null) return productCategory.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategory!=null) return productCategory.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategory!=null) return productCategory.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategory!=null) return productCategory.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategory!=null) productCategory.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
