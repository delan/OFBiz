
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ProductCategoryRollupValue implements ProductCategoryRollup
{
  /** The variable of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  private String productCategoryId;
  /** The variable of the PARENT_PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  private String parentProductCategoryId;

  private ProductCategoryRollup productCategoryRollup;

  public ProductCategoryRollupValue()
  {
    this.productCategoryId = null;
    this.parentProductCategoryId = null;

    this.productCategoryRollup = null;
  }

  public ProductCategoryRollupValue(ProductCategoryRollup productCategoryRollup) throws RemoteException
  {
    if(productCategoryRollup == null) return;
  
    this.productCategoryId = productCategoryRollup.getProductCategoryId();
    this.parentProductCategoryId = productCategoryRollup.getParentProductCategoryId();

    this.productCategoryRollup = productCategoryRollup;
  }

  public ProductCategoryRollupValue(ProductCategoryRollup productCategoryRollup, String productCategoryId, String parentProductCategoryId)
  {
    if(productCategoryRollup == null) return;
  
    this.productCategoryId = productCategoryId;
    this.parentProductCategoryId = parentProductCategoryId;

    this.productCategoryRollup = productCategoryRollup;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String getProductCategoryId()  throws RemoteException { return productCategoryId; }

  /** Get the primary key of the PARENT_PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String getParentProductCategoryId()  throws RemoteException { return parentProductCategoryId; }

  /** Get the value object of the ProductCategoryRollup class. */
  public ProductCategoryRollup getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategoryRollup class. */
  public void setValueObject(ProductCategoryRollup valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategoryRollup!=null) productCategoryRollup.setValueObject(valueObject);

    if(productCategoryId == null) productCategoryId = valueObject.getProductCategoryId();
    if(parentProductCategoryId == null) parentProductCategoryId = valueObject.getParentProductCategoryId();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategoryRollup!=null) return productCategoryRollup.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategoryRollup!=null) return productCategoryRollup.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategoryRollup!=null) return productCategoryRollup.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategoryRollup!=null) return productCategoryRollup.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategoryRollup!=null) productCategoryRollup.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
