
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Type Entity
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

public interface ProductType extends EJBObject
{
  /** Get the primary key of the PRODUCT_TYPE_ID column of the PRODUCT_TYPE table. */
  public String getProductTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PRODUCT_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PRODUCT_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ProductType class. */
  public ProductType getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductType class. */
  public void setValueObject(ProductType productTypeValue) throws RemoteException;


  /** Get the Parent ProductType entity corresponding to this entity. */
  public ProductType getParentProductType() throws RemoteException;
  /** Remove the Parent ProductType entity corresponding to this entity. */
  public void removeParentProductType() throws RemoteException;  

  /** Get a collection of Child ProductType related entities. */
  public Collection getChildProductTypes() throws RemoteException;
  /** Get the Child ProductType keyed by member(s) of this class, and other passed parameters. */
  public ProductType getChildProductType(String productTypeId) throws RemoteException;
  /** Remove Child ProductType related entities. */
  public void removeChildProductTypes() throws RemoteException;
  /** Remove the Child ProductType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductType(String productTypeId) throws RemoteException;

  /** Get a collection of  ProductTypeAttr related entities. */
  public Collection getProductTypeAttrs() throws RemoteException;
  /** Get the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductTypeAttr getProductTypeAttr(String name) throws RemoteException;
  /** Remove  ProductTypeAttr related entities. */
  public void removeProductTypeAttrs() throws RemoteException;
  /** Remove the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductTypeAttr(String name) throws RemoteException;

  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() throws RemoteException;
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productId) throws RemoteException;
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() throws RemoteException;
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productId) throws RemoteException;

}
