
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

public interface ProductTypeAttr extends EJBObject
{
  /** Get the primary key of the PRODUCT_TYPE_ID column of the PRODUCT_TYPE_ATTR table. */
  public String getProductTypeId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the PRODUCT_TYPE_ATTR table. */
  public String getName() throws RemoteException;
  

  /** Get the value object of this ProductTypeAttr class. */
  public ProductTypeAttr getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductTypeAttr class. */
  public void setValueObject(ProductTypeAttr productTypeAttrValue) throws RemoteException;


  /** Get the  ProductType entity corresponding to this entity. */
  public ProductType getProductType() throws RemoteException;
  /** Remove the  ProductType entity corresponding to this entity. */
  public void removeProductType() throws RemoteException;  

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() throws RemoteException;
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String productId) throws RemoteException;
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() throws RemoteException;
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String productId) throws RemoteException;

  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() throws RemoteException;
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productId) throws RemoteException;
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() throws RemoteException;
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productId) throws RemoteException;

}
