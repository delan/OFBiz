
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Classification Entity
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
public class ProductClassBean implements EntityBean
{
  /** The variable for the PRODUCT_ID column of the PRODUCT_CLASS table. */
  public String productId;
  /** The variable for the PRODUCT_TYPE_ID column of the PRODUCT_CLASS table. */
  public String productTypeId;
  /** The variable for the FROM_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date fromDate;
  /** The variable for the THRU_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date thruDate;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductClassBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_ID column of the PRODUCT_CLASS table. */
  public String getProductId() { return productId; }

  /** Get the primary key PRODUCT_TYPE_ID column of the PRODUCT_CLASS table. */
  public String getProductTypeId() { return productTypeId; }

  /** Get the value of the FROM_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date getFromDate() { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_CLASS table. */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date getThruDate() { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_CLASS table. */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductClassBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductClass valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
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
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductClassBean object
   *@return    The ValueObject value
   */
  public ProductClass getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductClassValue((ProductClass)this.entityContext.getEJBObject(), productId, productTypeId, fromDate, thruDate);
    }
    else { return null; }
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  ProductType entity corresponding to this entity. */
  public ProductType getProductType() { return ProductTypeHelper.findByPrimaryKey(productTypeId); }
  /** Remove the  ProductType entity corresponding to this entity. */
  public void removeProductType() { ProductTypeHelper.removeByPrimaryKey(productTypeId); }

  /** Get a collection of  ProductTypeAttr related entities. */
  public Collection getProductTypeAttrs() { return ProductTypeAttrHelper.findByProductTypeId(productTypeId); }
  /** Get the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductTypeAttr getProductTypeAttr(String name) { return ProductTypeAttrHelper.findByPrimaryKey(productTypeId, name); }
  /** Remove  ProductTypeAttr related entities. */
  public void removeProductTypeAttrs() { ProductTypeAttrHelper.removeByProductTypeId(productTypeId); }
  /** Remove the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductTypeAttr(String name) { ProductTypeAttrHelper.removeByPrimaryKey(productTypeId, name); }

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() { return ProductAttributeHelper.findByProductId(productId); }
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String name) { return ProductAttributeHelper.findByPrimaryKey(productId, name); }
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() { ProductAttributeHelper.removeByProductId(productId); }
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String name) { ProductAttributeHelper.removeByPrimaryKey(productId, name); }


  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.ProductClassPK ejbCreate(String productId, String productTypeId, java.util.Date fromDate, java.util.Date thruDate) throws CreateException
  {
    this.productId = productId;
    this.productTypeId = productTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    return null;
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.ProductClassPK ejbCreate(String productId, String productTypeId) throws CreateException
  {
    return ejbCreate(productId, productTypeId, null, null);
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String productTypeId, java.util.Date fromDate, java.util.Date thruDate) throws CreateException {}

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String productTypeId) throws CreateException
  {
    ejbPostCreate(productId, productTypeId, null, null);
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
