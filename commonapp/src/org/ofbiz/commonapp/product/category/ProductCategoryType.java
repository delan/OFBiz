
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Category Type Entity
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

public interface ProductCategoryType extends EJBObject
{
  /** Get the primary key of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String getProductCategoryTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ProductCategoryType class. */
  public ProductCategoryType getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductCategoryType class. */
  public void setValueObject(ProductCategoryType productCategoryTypeValue) throws RemoteException;


  /** Get the Parent ProductCategoryType entity corresponding to this entity. */
  public ProductCategoryType getParentProductCategoryType() throws RemoteException;
  /** Remove the Parent ProductCategoryType entity corresponding to this entity. */
  public void removeParentProductCategoryType() throws RemoteException;  

  /** Get a collection of Child ProductCategoryType related entities. */
  public Collection getChildProductCategoryTypes() throws RemoteException;
  /** Get the Child ProductCategoryType keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryType getChildProductCategoryType(String productCategoryTypeId) throws RemoteException;
  /** Remove Child ProductCategoryType related entities. */
  public void removeChildProductCategoryTypes() throws RemoteException;
  /** Remove the Child ProductCategoryType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductCategoryType(String productCategoryTypeId) throws RemoteException;

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() throws RemoteException;
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String name) throws RemoteException;
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() throws RemoteException;
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String name) throws RemoteException;

  /** Get a collection of  ProductCategoryClass related entities. */
  public Collection getProductCategoryClasss() throws RemoteException;
  /** Get the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryClass getProductCategoryClass(String productCategoryId) throws RemoteException;
  /** Remove  ProductCategoryClass related entities. */
  public void removeProductCategoryClasss() throws RemoteException;
  /** Remove the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryClass(String productCategoryId) throws RemoteException;

}
