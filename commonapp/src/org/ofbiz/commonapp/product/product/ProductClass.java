
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

public interface ProductClass extends EJBObject
{
  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_CLASS table. */
  public String getProductId() throws RemoteException;
  
  /** Get the primary key of the PRODUCT_TYPE_ID column of the PRODUCT_CLASS table. */
  public String getProductTypeId() throws RemoteException;
  
  /** Get the value of the FROM_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date getFromDate() throws RemoteException;
  /** Set the value of the FROM_DATE column of the PRODUCT_CLASS table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException;
  
  /** Get the value of the THRU_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date getThruDate() throws RemoteException;
  /** Set the value of the THRU_DATE column of the PRODUCT_CLASS table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException;
  

  /** Get the value object of this ProductClass class. */
  public ProductClass getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductClass class. */
  public void setValueObject(ProductClass productClassValue) throws RemoteException;


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

  /** Get the  ProductType entity corresponding to this entity. */
  public ProductType getProductType() throws RemoteException;
  /** Remove the  ProductType entity corresponding to this entity. */
  public void removeProductType() throws RemoteException;  

  /** Get a collection of  ProductTypeAttr related entities. */
  public Collection getProductTypeAttrs() throws RemoteException;
  /** Get the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductTypeAttr getProductTypeAttr(String name) throws RemoteException;
  /** Remove  ProductTypeAttr related entities. */
  public void removeProductTypeAttrs() throws RemoteException;
  /** Remove the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductTypeAttr(String name) throws RemoteException;

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() throws RemoteException;
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String name) throws RemoteException;
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() throws RemoteException;
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String name) throws RemoteException;

}
