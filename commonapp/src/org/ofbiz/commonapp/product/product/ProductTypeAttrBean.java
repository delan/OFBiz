
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Type Attribute Entity
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
 *@created    Fri Jul 27 01:18:25 MDT 2001
 *@version    1.0
 */
public class ProductTypeAttrBean implements EntityBean
{
  /** The variable for the PRODUCT_TYPE_ID column of the PRODUCT_TYPE_ATTR table. */
  public String productTypeId;
  /** The variable for the NAME column of the PRODUCT_TYPE_ATTR table. */
  public String name;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductTypeAttrBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_TYPE_ID column of the PRODUCT_TYPE_ATTR table. */
  public String getProductTypeId() { return productTypeId; }

  /** Get the primary key NAME column of the PRODUCT_TYPE_ATTR table. */
  public String getName() { return name; }

  /** Sets the values from ValueObject attribute of the ProductTypeAttrBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductTypeAttr valueObject)
  {
  }

  /** Gets the ValueObject attribute of the ProductTypeAttrBean object
   *@return    The ValueObject value
   */
  public ProductTypeAttr getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductTypeAttrValue((ProductTypeAttr)this.entityContext.getEJBObject(), productTypeId, name);
    }
    else { return null; }
  }


  /** Get the  ProductType entity corresponding to this entity. */
  public ProductType getProductType() { return ProductTypeHelper.findByPrimaryKey(productTypeId); }
  /** Remove the  ProductType entity corresponding to this entity. */
  public void removeProductType() { ProductTypeHelper.removeByPrimaryKey(productTypeId); }

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() { return ProductAttributeHelper.findByName(name); }
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String productId) { return ProductAttributeHelper.findByPrimaryKey(productId, name); }
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() { ProductAttributeHelper.removeByName(name); }
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String productId) { ProductAttributeHelper.removeByPrimaryKey(productId, name); }

  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() { return ProductClassHelper.findByProductTypeId(productTypeId); }
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productId) { return ProductClassHelper.findByPrimaryKey(productId, productTypeId); }
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() { ProductClassHelper.removeByProductTypeId(productTypeId); }
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productId) { ProductClassHelper.removeByPrimaryKey(productId, productTypeId); }


  /** Description of the Method
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.ProductTypeAttrPK ejbCreate(String productTypeId, String name) throws CreateException
  {
    this.productTypeId = productTypeId;
    this.name = name;
    return null;
  }

  /** Description of the Method
   *@param  productTypeId                  Field of the PRODUCT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productTypeId, String name) throws CreateException {}

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
