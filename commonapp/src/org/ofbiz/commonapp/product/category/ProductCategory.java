
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.product.price.*;
import org.ofbiz.commonapp.product.supplier.*;

/**
 * <p><b>Title:</b> Product Category Entity
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

public interface ProductCategory extends EJBObject
{
  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY table. */
  public String getProductCategoryId() throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_CATEGORY table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this ProductCategory class. */
  public ProductCategory getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductCategory class. */
  public void setValueObject(ProductCategory productCategoryValue) throws RemoteException;


  /** Get a collection of  ProductCategoryClass related entities. */
  public Collection getProductCategoryClasss() throws RemoteException;
  /** Get the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryClass getProductCategoryClass(String productCategoryTypeId) throws RemoteException;
  /** Remove  ProductCategoryClass related entities. */
  public void removeProductCategoryClasss() throws RemoteException;
  /** Remove the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryClass(String productCategoryTypeId) throws RemoteException;

  /** Get a collection of  ProductCategoryAttribute related entities. */
  public Collection getProductCategoryAttributes() throws RemoteException;
  /** Get the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryAttribute getProductCategoryAttribute(String name) throws RemoteException;
  /** Remove  ProductCategoryAttribute related entities. */
  public void removeProductCategoryAttributes() throws RemoteException;
  /** Remove the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryAttribute(String name) throws RemoteException;

  /** Get a collection of  ProductCategoryMember related entities. */
  public Collection getProductCategoryMembers() throws RemoteException;
  /** Get the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryMember getProductCategoryMember(String productId) throws RemoteException;
  /** Remove  ProductCategoryMember related entities. */
  public void removeProductCategoryMembers() throws RemoteException;
  /** Remove the  ProductCategoryMember keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryMember(String productId) throws RemoteException;

  /** Get a collection of  Product related entities. */
  public Collection getProducts() throws RemoteException;
  /** Get the  Product keyed by member(s) of this class, and other passed parameters. */
  public Product getProduct(String productId) throws RemoteException;
  /** Remove  Product related entities. */
  public void removeProducts() throws RemoteException;
  /** Remove the  Product keyed by member(s) of this class, and other passed parameters. */
  public void removeProduct(String productId) throws RemoteException;

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() throws RemoteException;
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() throws RemoteException;
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) throws RemoteException;

  /** Get a collection of  MarketInterest related entities. */
  public Collection getMarketInterests() throws RemoteException;
  /** Get the  MarketInterest keyed by member(s) of this class, and other passed parameters. */
  public MarketInterest getMarketInterest(String partyTypeId) throws RemoteException;
  /** Remove  MarketInterest related entities. */
  public void removeMarketInterests() throws RemoteException;
  /** Remove the  MarketInterest keyed by member(s) of this class, and other passed parameters. */
  public void removeMarketInterest(String partyTypeId) throws RemoteException;

}
