
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

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
public class ProductCategoryBean implements EntityBean
{
  /** The variable for the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY table. */
  public String productCategoryId;
  /** The variable for the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductCategoryBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY table. */
  public String getProductCategoryId() { return productCategoryId; }

  /** Get the value of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductCategoryBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductCategory valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductCategoryBean object
   *@return    The ValueObject value
   */
  public ProductCategory getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductCategoryValue((ProductCategory)this.entityContext.getEJBObject(), productCategoryId, description);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productCategoryId, String description) throws CreateException
  {
    this.productCategoryId = productCategoryId;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productCategoryId) throws CreateException
  {
    return ejbCreate(productCategoryId, null);
  }

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryId, String description) throws CreateException {}

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryId) throws CreateException
  {
    ejbPostCreate(productCategoryId, null);
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
