
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Category Rollup Entity
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
 *@created    Fri Jul 27 01:18:27 MDT 2001
 *@version    1.0
 */
public class ProductCategoryRollupBean implements EntityBean
{
  /** The variable for the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String productCategoryId;
  /** The variable for the PARENT_PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String parentProductCategoryId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductCategoryRollupBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String getProductCategoryId() { return productCategoryId; }

  /** Get the primary key PARENT_PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String getParentProductCategoryId() { return parentProductCategoryId; }

  /** Sets the values from ValueObject attribute of the ProductCategoryRollupBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductCategoryRollup valueObject)
  {
  }

  /** Gets the ValueObject attribute of the ProductCategoryRollupBean object
   *@return    The ValueObject value
   */
  public ProductCategoryRollup getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductCategoryRollupValue((ProductCategoryRollup)this.entityContext.getEJBObject(), productCategoryId, parentProductCategoryId);
    }
    else { return null; }
  }


  /** Get the Current ProductCategory entity corresponding to this entity. */
  public ProductCategory getCurrentProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the Current ProductCategory entity corresponding to this entity. */
  public void removeCurrentProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }

  /** Get the Parent ProductCategory entity corresponding to this entity. */
  public ProductCategory getParentProductCategory() { return ProductCategoryHelper.findByPrimaryKey(parentProductCategoryId); }
  /** Remove the Parent ProductCategory entity corresponding to this entity. */
  public void removeParentProductCategory() { ProductCategoryHelper.removeByPrimaryKey(parentProductCategoryId); }

  /** Get a collection of Child ProductCategoryRollup related entities. */
  public Collection getChildProductCategoryRollups() { return ProductCategoryRollupHelper.findByParentProductCategoryId(productCategoryId); }
  /** Get the Child ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryRollup getChildProductCategoryRollup(String productCategoryId) { return ProductCategoryRollupHelper.findByPrimaryKey(productCategoryId, productCategoryId); }
  /** Remove Child ProductCategoryRollup related entities. */
  public void removeChildProductCategoryRollups() { ProductCategoryRollupHelper.removeByParentProductCategoryId(productCategoryId); }
  /** Remove the Child ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductCategoryRollup(String productCategoryId) { ProductCategoryRollupHelper.removeByPrimaryKey(productCategoryId, productCategoryId); }

  /** Get a collection of Parent ProductCategoryRollup related entities. */
  public Collection getParentProductCategoryRollups() { return ProductCategoryRollupHelper.findByProductCategoryId(parentProductCategoryId); }
  /** Get the Parent ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryRollup getParentProductCategoryRollup(String parentProductCategoryId) { return ProductCategoryRollupHelper.findByPrimaryKey(parentProductCategoryId, parentProductCategoryId); }
  /** Remove Parent ProductCategoryRollup related entities. */
  public void removeParentProductCategoryRollups() { ProductCategoryRollupHelper.removeByProductCategoryId(parentProductCategoryId); }
  /** Remove the Parent ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public void removeParentProductCategoryRollup(String parentProductCategoryId) { ProductCategoryRollupHelper.removeByPrimaryKey(parentProductCategoryId, parentProductCategoryId); }

  /** Get a collection of Sibling ProductCategoryRollup related entities. */
  public Collection getSiblingProductCategoryRollups() { return ProductCategoryRollupHelper.findByParentProductCategoryId(parentProductCategoryId); }
  /** Get the Sibling ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryRollup getSiblingProductCategoryRollup(String productCategoryId) { return ProductCategoryRollupHelper.findByPrimaryKey(productCategoryId, parentProductCategoryId); }
  /** Remove Sibling ProductCategoryRollup related entities. */
  public void removeSiblingProductCategoryRollups() { ProductCategoryRollupHelper.removeByParentProductCategoryId(parentProductCategoryId); }
  /** Remove the Sibling ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public void removeSiblingProductCategoryRollup(String productCategoryId) { ProductCategoryRollupHelper.removeByPrimaryKey(productCategoryId, parentProductCategoryId); }


  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.category.ProductCategoryRollupPK ejbCreate(String productCategoryId, String parentProductCategoryId) throws CreateException
  {
    this.productCategoryId = productCategoryId;
    this.parentProductCategoryId = parentProductCategoryId;
    return null;
  }

  /** Description of the Method
   *@param  productCategoryId                  Field of the PRODUCT_CATEGORY_ID column.
   *@param  parentProductCategoryId                  Field of the PARENT_PRODUCT_CATEGORY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryId, String parentProductCategoryId) throws CreateException {}

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
