
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ProductClassValue implements ProductClass
{
  /** The variable of the PRODUCT_ID column of the PRODUCT_CLASS table. */
  private String productId;
  /** The variable of the PRODUCT_TYPE_ID column of the PRODUCT_CLASS table. */
  private String productTypeId;
  /** The variable of the FROM_DATE column of the PRODUCT_CLASS table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PRODUCT_CLASS table. */
  private java.util.Date thruDate;

  private ProductClass productClass;

  public ProductClassValue()
  {
    this.productId = null;
    this.productTypeId = null;
    this.fromDate = null;
    this.thruDate = null;

    this.productClass = null;
  }

  public ProductClassValue(ProductClass productClass) throws RemoteException
  {
    if(productClass == null) return;
  
    this.productId = productClass.getProductId();
    this.productTypeId = productClass.getProductTypeId();
    this.fromDate = productClass.getFromDate();
    this.thruDate = productClass.getThruDate();

    this.productClass = productClass;
  }

  public ProductClassValue(ProductClass productClass, String productId, String productTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    if(productClass == null) return;
  
    this.productId = productId;
    this.productTypeId = productTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;

    this.productClass = productClass;
  }


  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_CLASS table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the primary key of the PRODUCT_TYPE_ID column of the PRODUCT_CLASS table. */
  public String getProductTypeId()  throws RemoteException { return productTypeId; }

  /** Get the value of the FROM_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_CLASS table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(productClass!=null) productClass.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_CLASS table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_CLASS table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(productClass!=null) productClass.setThruDate(thruDate);
  }

  /** Get the value object of the ProductClass class. */
  public ProductClass getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductClass class. */
  public void setValueObject(ProductClass valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productClass!=null) productClass.setValueObject(valueObject);

    if(productId == null) productId = valueObject.getProductId();
    if(productTypeId == null) productTypeId = valueObject.getProductTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  ProductType entity corresponding to this entity. */
  public ProductType getProductType() { return ProductTypeHelper.findByPrimaryKey(productTypeId); }
  /** Remove the  ProductType entity corresponding to this entity. */
  public void removeProductType() { ProductTypeHelper.removeByPrimaryKey(productTypeId); }

  /** Get a collection of  ProductTypeAttr related entities. */
  public Collection getProductTypeAttrs() { return ProductTypeAttrHelper.findByProductTypeId(productTypeId); }
  /** Get the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductTypeAttr getProductTypeAttr(String name) { return ProductTypeAttrHelper.findByPrimaryKey(productTypeId, name); }
  /** Remove  ProductTypeAttr related entities. */
  public void removeProductTypeAttrs() { ProductTypeAttrHelper.removeByProductTypeId(productTypeId); }
  /** Remove the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductTypeAttr(String name) { ProductTypeAttrHelper.removeByPrimaryKey(productTypeId, name); }

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() { return ProductAttributeHelper.findByProductId(productId); }
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String name) { return ProductAttributeHelper.findByPrimaryKey(productId, name); }
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() { ProductAttributeHelper.removeByProductId(productId); }
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String name) { ProductAttributeHelper.removeByPrimaryKey(productId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productClass!=null) return productClass.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productClass!=null) return productClass.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productClass!=null) return productClass.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productClass!=null) return productClass.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productClass!=null) productClass.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
