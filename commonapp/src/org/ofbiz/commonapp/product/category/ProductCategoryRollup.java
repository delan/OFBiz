
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

public interface ProductCategoryRollup extends EJBObject
{
  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String getProductCategoryId() throws RemoteException;
  
  /** Get the primary key of the PARENT_PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ROLLUP table. */
  public String getParentProductCategoryId() throws RemoteException;
  

  /** Get the value object of this ProductCategoryRollup class. */
  public ProductCategoryRollup getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductCategoryRollup class. */
  public void setValueObject(ProductCategoryRollup productCategoryRollupValue) throws RemoteException;


  /** Get the Current ProductCategory entity corresponding to this entity. */
  public ProductCategory getCurrentProductCategory() throws RemoteException;
  /** Remove the Current ProductCategory entity corresponding to this entity. */
  public void removeCurrentProductCategory() throws RemoteException;  

  /** Get the Parent ProductCategory entity corresponding to this entity. */
  public ProductCategory getParentProductCategory() throws RemoteException;
  /** Remove the Parent ProductCategory entity corresponding to this entity. */
  public void removeParentProductCategory() throws RemoteException;  

  /** Get a collection of Child ProductCategoryRollup related entities. */
  public Collection getChildProductCategoryRollups() throws RemoteException;
  /** Get the Child ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryRollup getChildProductCategoryRollup(String productCategoryId) throws RemoteException;
  /** Remove Child ProductCategoryRollup related entities. */
  public void removeChildProductCategoryRollups() throws RemoteException;
  /** Remove the Child ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductCategoryRollup(String productCategoryId) throws RemoteException;

  /** Get a collection of Parent ProductCategoryRollup related entities. */
  public Collection getParentProductCategoryRollups() throws RemoteException;
  /** Get the Parent ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryRollup getParentProductCategoryRollup(String parentProductCategoryId) throws RemoteException;
  /** Remove Parent ProductCategoryRollup related entities. */
  public void removeParentProductCategoryRollups() throws RemoteException;
  /** Remove the Parent ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public void removeParentProductCategoryRollup(String parentProductCategoryId) throws RemoteException;

  /** Get a collection of Sibling ProductCategoryRollup related entities. */
  public Collection getSiblingProductCategoryRollups() throws RemoteException;
  /** Get the Sibling ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryRollup getSiblingProductCategoryRollup(String productCategoryId) throws RemoteException;
  /** Remove Sibling ProductCategoryRollup related entities. */
  public void removeSiblingProductCategoryRollups() throws RemoteException;
  /** Remove the Sibling ProductCategoryRollup keyed by member(s) of this class, and other passed parameters. */
  public void removeSiblingProductCategoryRollup(String productCategoryId) throws RemoteException;

}
