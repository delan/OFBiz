
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Category Classification Entity
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
public class ProductCategoryClassValue implements ProductCategoryClass
{
  /** The variable of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_CLASS table. */
  private String productCategoryId;
  /** The variable of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_CLASS table. */
  private String productCategoryTypeId;
  /** The variable of the FROM_DATE column of the PRODUCT_CATEGORY_CLASS table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PRODUCT_CATEGORY_CLASS table. */
  private java.util.Date thruDate;

  private ProductCategoryClass productCategoryClass;

  public ProductCategoryClassValue()
  {
    this.productCategoryId = null;
    this.productCategoryTypeId = null;
    this.fromDate = null;
    this.thruDate = null;

    this.productCategoryClass = null;
  }

  public ProductCategoryClassValue(ProductCategoryClass productCategoryClass) throws RemoteException
  {
    if(productCategoryClass == null) return;
  
    this.productCategoryId = productCategoryClass.getProductCategoryId();
    this.productCategoryTypeId = productCategoryClass.getProductCategoryTypeId();
    this.fromDate = productCategoryClass.getFromDate();
    this.thruDate = productCategoryClass.getThruDate();

    this.productCategoryClass = productCategoryClass;
  }

  public ProductCategoryClassValue(ProductCategoryClass productCategoryClass, String productCategoryId, String productCategoryTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    if(productCategoryClass == null) return;
  
    this.productCategoryId = productCategoryId;
    this.productCategoryTypeId = productCategoryTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;

    this.productCategoryClass = productCategoryClass;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_CLASS table. */
  public String getProductCategoryId()  throws RemoteException { return productCategoryId; }

  /** Get the primary key of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_CLASS table. */
  public String getProductCategoryTypeId()  throws RemoteException { return productCategoryTypeId; }

  /** Get the value of the FROM_DATE column of the PRODUCT_CATEGORY_CLASS table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_CATEGORY_CLASS table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(productCategoryClass!=null) productCategoryClass.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_CATEGORY_CLASS table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_CATEGORY_CLASS table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(productCategoryClass!=null) productCategoryClass.setThruDate(thruDate);
  }

  /** Get the value object of the ProductCategoryClass class. */
  public ProductCategoryClass getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategoryClass class. */
  public void setValueObject(ProductCategoryClass valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategoryClass!=null) productCategoryClass.setValueObject(valueObject);

    if(productCategoryId == null) productCategoryId = valueObject.getProductCategoryId();
    if(productCategoryTypeId == null) productCategoryTypeId = valueObject.getProductCategoryTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
  }


  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }

  /** Get the  ProductCategoryType entity corresponding to this entity. */
  public ProductCategoryType getProductCategoryType() { return ProductCategoryTypeHelper.findByPrimaryKey(productCategoryTypeId); }
  /** Remove the  ProductCategoryType entity corresponding to this entity. */
  public void removeProductCategoryType() { ProductCategoryTypeHelper.removeByPrimaryKey(productCategoryTypeId); }

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() { return ProductCategoryTypeAttrHelper.findByProductCategoryTypeId(productCategoryTypeId); }
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String name) { return ProductCategoryTypeAttrHelper.findByPrimaryKey(productCategoryTypeId, name); }
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() { ProductCategoryTypeAttrHelper.removeByProductCategoryTypeId(productCategoryTypeId); }
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String name) { ProductCategoryTypeAttrHelper.removeByPrimaryKey(productCategoryTypeId, name); }

  /** Get a collection of  ProductCategoryAttribute related entities. */
  public Collection getProductCategoryAttributes() { return ProductCategoryAttributeHelper.findByProductCategoryId(productCategoryId); }
  /** Get the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryAttribute getProductCategoryAttribute(String name) { return ProductCategoryAttributeHelper.findByPrimaryKey(productCategoryId, name); }
  /** Remove  ProductCategoryAttribute related entities. */
  public void removeProductCategoryAttributes() { ProductCategoryAttributeHelper.removeByProductCategoryId(productCategoryId); }
  /** Remove the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryAttribute(String name) { ProductCategoryAttributeHelper.removeByPrimaryKey(productCategoryId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategoryClass!=null) return productCategoryClass.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategoryClass!=null) return productCategoryClass.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategoryClass!=null) return productCategoryClass.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategoryClass!=null) return productCategoryClass.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategoryClass!=null) productCategoryClass.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
