
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.product.*;

/**
 * <p><b>Title:</b> Product Category Member Entity
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
public class ProductCategoryMemberValue implements ProductCategoryMember
{
  /** The variable of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_MEMBER table. */
  private String productCategoryId;
  /** The variable of the PRODUCT_ID column of the PRODUCT_CATEGORY_MEMBER table. */
  private String productId;
  /** The variable of the FROM_DATE column of the PRODUCT_CATEGORY_MEMBER table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PRODUCT_CATEGORY_MEMBER table. */
  private java.util.Date thruDate;
  /** The variable of the PRIMARY_FLAG column of the PRODUCT_CATEGORY_MEMBER table. */
  private String primaryFlag;
  /** The variable of the COMMENT column of the PRODUCT_CATEGORY_MEMBER table. */
  private String comment;

  private ProductCategoryMember productCategoryMember;

  public ProductCategoryMemberValue()
  {
    this.productCategoryId = null;
    this.productId = null;
    this.fromDate = null;
    this.thruDate = null;
    this.primaryFlag = null;
    this.comment = null;

    this.productCategoryMember = null;
  }

  public ProductCategoryMemberValue(ProductCategoryMember productCategoryMember) throws RemoteException
  {
    if(productCategoryMember == null) return;
  
    this.productCategoryId = productCategoryMember.getProductCategoryId();
    this.productId = productCategoryMember.getProductId();
    this.fromDate = productCategoryMember.getFromDate();
    this.thruDate = productCategoryMember.getThruDate();
    this.primaryFlag = productCategoryMember.getPrimaryFlag();
    this.comment = productCategoryMember.getComment();

    this.productCategoryMember = productCategoryMember;
  }

  public ProductCategoryMemberValue(ProductCategoryMember productCategoryMember, String productCategoryId, String productId, java.util.Date fromDate, java.util.Date thruDate, String primaryFlag, String comment)
  {
    if(productCategoryMember == null) return;
  
    this.productCategoryId = productCategoryId;
    this.productId = productId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.primaryFlag = primaryFlag;
    this.comment = comment;

    this.productCategoryMember = productCategoryMember;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_MEMBER table. */
  public String getProductCategoryId()  throws RemoteException { return productCategoryId; }

  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_CATEGORY_MEMBER table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the value of the FROM_DATE column of the PRODUCT_CATEGORY_MEMBER table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_CATEGORY_MEMBER table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(productCategoryMember!=null) productCategoryMember.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_CATEGORY_MEMBER table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_CATEGORY_MEMBER table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(productCategoryMember!=null) productCategoryMember.setThruDate(thruDate);
  }

  /** Get the value of the PRIMARY_FLAG column of the PRODUCT_CATEGORY_MEMBER table. */
  public String getPrimaryFlag() throws RemoteException { return primaryFlag; }
  /** Set the value of the PRIMARY_FLAG column of the PRODUCT_CATEGORY_MEMBER table. */
  public void setPrimaryFlag(String primaryFlag) throws RemoteException
  {
    this.primaryFlag = primaryFlag;
    if(productCategoryMember!=null) productCategoryMember.setPrimaryFlag(primaryFlag);
  }

  /** Get the value of the COMMENT column of the PRODUCT_CATEGORY_MEMBER table. */
  public String getComment() throws RemoteException { return comment; }
  /** Set the value of the COMMENT column of the PRODUCT_CATEGORY_MEMBER table. */
  public void setComment(String comment) throws RemoteException
  {
    this.comment = comment;
    if(productCategoryMember!=null) productCategoryMember.setComment(comment);
  }

  /** Get the value object of the ProductCategoryMember class. */
  public ProductCategoryMember getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategoryMember class. */
  public void setValueObject(ProductCategoryMember valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategoryMember!=null) productCategoryMember.setValueObject(valueObject);

    if(productCategoryId == null) productCategoryId = valueObject.getProductCategoryId();
    if(productId == null) productId = valueObject.getProductId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
    primaryFlag = valueObject.getPrimaryFlag();
    comment = valueObject.getComment();
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategoryMember!=null) return productCategoryMember.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategoryMember!=null) return productCategoryMember.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategoryMember!=null) return productCategoryMember.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategoryMember!=null) return productCategoryMember.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategoryMember!=null) productCategoryMember.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
