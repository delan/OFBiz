
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.product.*;

/**
 * <p><b>Title:</b> Product Feature Applicability Entity
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
 *@created    Fri Jul 27 01:18:28 MDT 2001
 *@version    1.0
 */
public class ProductFeatureApplValue implements ProductFeatureAppl
{
  /** The variable of the PRODUCT_ID column of the PRODUCT_FEATURE_APPL table. */
  private String productId;
  /** The variable of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_APPL table. */
  private String productFeatureId;
  /** The variable of the PRODUCT_FEATURE_APPL_TYPE_ID column of the PRODUCT_FEATURE_APPL table. */
  private String productFeatureApplTypeId;
  /** The variable of the FROM_DATE column of the PRODUCT_FEATURE_APPL table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PRODUCT_FEATURE_APPL table. */
  private java.util.Date thruDate;

  private ProductFeatureAppl productFeatureAppl;

  public ProductFeatureApplValue()
  {
    this.productId = null;
    this.productFeatureId = null;
    this.productFeatureApplTypeId = null;
    this.fromDate = null;
    this.thruDate = null;

    this.productFeatureAppl = null;
  }

  public ProductFeatureApplValue(ProductFeatureAppl productFeatureAppl) throws RemoteException
  {
    if(productFeatureAppl == null) return;
  
    this.productId = productFeatureAppl.getProductId();
    this.productFeatureId = productFeatureAppl.getProductFeatureId();
    this.productFeatureApplTypeId = productFeatureAppl.getProductFeatureApplTypeId();
    this.fromDate = productFeatureAppl.getFromDate();
    this.thruDate = productFeatureAppl.getThruDate();

    this.productFeatureAppl = productFeatureAppl;
  }

  public ProductFeatureApplValue(ProductFeatureAppl productFeatureAppl, String productId, String productFeatureId, String productFeatureApplTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    if(productFeatureAppl == null) return;
  
    this.productId = productId;
    this.productFeatureId = productFeatureId;
    this.productFeatureApplTypeId = productFeatureApplTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;

    this.productFeatureAppl = productFeatureAppl;
  }


  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_FEATURE_APPL table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the primary key of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE_APPL table. */
  public String getProductFeatureId()  throws RemoteException { return productFeatureId; }

  /** Get the value of the PRODUCT_FEATURE_APPL_TYPE_ID column of the PRODUCT_FEATURE_APPL table. */
  public String getProductFeatureApplTypeId() throws RemoteException { return productFeatureApplTypeId; }
  /** Set the value of the PRODUCT_FEATURE_APPL_TYPE_ID column of the PRODUCT_FEATURE_APPL table. */
  public void setProductFeatureApplTypeId(String productFeatureApplTypeId) throws RemoteException
  {
    this.productFeatureApplTypeId = productFeatureApplTypeId;
    if(productFeatureAppl!=null) productFeatureAppl.setProductFeatureApplTypeId(productFeatureApplTypeId);
  }

  /** Get the value of the FROM_DATE column of the PRODUCT_FEATURE_APPL table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_FEATURE_APPL table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(productFeatureAppl!=null) productFeatureAppl.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_FEATURE_APPL table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_FEATURE_APPL table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(productFeatureAppl!=null) productFeatureAppl.setThruDate(thruDate);
  }

  /** Get the value object of the ProductFeatureAppl class. */
  public ProductFeatureAppl getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeatureAppl class. */
  public void setValueObject(ProductFeatureAppl valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeatureAppl!=null) productFeatureAppl.setValueObject(valueObject);

    if(productId == null) productId = valueObject.getProductId();
    if(productFeatureId == null) productFeatureId = valueObject.getProductFeatureId();
    productFeatureApplTypeId = valueObject.getProductFeatureApplTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
  }


  /** Get the  ProductFeatureApplType entity corresponding to this entity. */
  public ProductFeatureApplType getProductFeatureApplType() { return ProductFeatureApplTypeHelper.findByPrimaryKey(productFeatureApplTypeId); }
  /** Remove the  ProductFeatureApplType entity corresponding to this entity. */
  public void removeProductFeatureApplType() { ProductFeatureApplTypeHelper.removeByPrimaryKey(productFeatureApplTypeId); }

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeatureAppl!=null) return productFeatureAppl.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeatureAppl!=null) return productFeatureAppl.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeatureAppl!=null) return productFeatureAppl.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeatureAppl!=null) return productFeatureAppl.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeatureAppl!=null) productFeatureAppl.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
