
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Category Attribute Entity
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
public class ProductCategoryAttributeBean implements EntityBean
{
  /** The variable for the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String productCategoryId;
  /** The variable for the NAME column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String name;
  /** The variable for the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String value;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductCategoryAttributeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getProductCategoryId() { return productCategoryId; }

  /** Get the primary key NAME column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getName() { return name; }

  /** Get the value of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getValue() { return value; }
  /** Set the value of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public void setValue(String value)
  {
    this.value = value;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductCategoryAttributeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductCategoryAttribute valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getValue() != null)
      {
        this.value = valueObject.getValue();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductCategoryAttributeBean object
   *@return    The ValueObject value
   */
  public ProductCategoryAttribute getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductCategoryAttributeValue((ProductCategoryAttribute)this.entityContext.getEJBObject(), productCategoryId, name, value);
    }
    else { return null; }
  }


  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() { return ProductCategoryTypeAttrHelper.findByName(name); }
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String productCategoryTypeId) { return ProductCategoryTypeAttrHelper.findByPrimaryKey(productCategoryTypeId, name); }
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() { ProductCategoryTypeAttrHelper.removeByName(name); }
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String productCategoryTypeId) { ProductCategoryTypeAttrHelper.removeByPrimaryKey(productCategoryTypeId, name); }


  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.category.ProductCategoryAttributePK ejbCreate(String productCategoryId, String name, String value) throws CreateException
  {
    this.productCategoryId = productCategoryId;
    this.name = name;
    this.value = value;
    return null;
  }

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.category.ProductCategoryAttributePK ejbCreate(String productCategoryId, String name) throws CreateException
  {
    return ejbCreate(productCategoryId, name, null);
  }

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryId, String name, String value) throws CreateException {}

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryId, String name) throws CreateException
  {
    ejbPostCreate(productCategoryId, name, null);
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
