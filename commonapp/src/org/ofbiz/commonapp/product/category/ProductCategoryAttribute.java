
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

public interface ProductCategoryAttribute extends EJBObject
{
  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getProductCategoryId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getName() throws RemoteException;
  
  /** Get the value of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getValue() throws RemoteException;
  /** Set the value of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException;
  

  /** Get the value object of this ProductCategoryAttribute class. */
  public ProductCategoryAttribute getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductCategoryAttribute class. */
  public void setValueObject(ProductCategoryAttribute productCategoryAttributeValue) throws RemoteException;


  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() throws RemoteException;
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() throws RemoteException;  

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() throws RemoteException;
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String productCategoryTypeId) throws RemoteException;
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() throws RemoteException;
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String productCategoryTypeId) throws RemoteException;

}
