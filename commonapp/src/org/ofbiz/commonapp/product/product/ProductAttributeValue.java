
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Attribute Entity
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
public class ProductAttributeValue implements ProductAttribute
{
  /** The variable of the PRODUCT_ID column of the PRODUCT_ATTRIBUTE table. */
  private String productId;
  /** The variable of the NAME column of the PRODUCT_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the PRODUCT_ATTRIBUTE table. */
  private String value;

  private ProductAttribute productAttribute;

  public ProductAttributeValue()
  {
    this.productId = null;
    this.name = null;
    this.value = null;

    this.productAttribute = null;
  }

  public ProductAttributeValue(ProductAttribute productAttribute) throws RemoteException
  {
    if(productAttribute == null) return;
  
    this.productId = productAttribute.getProductId();
    this.name = productAttribute.getName();
    this.value = productAttribute.getValue();

    this.productAttribute = productAttribute;
  }

  public ProductAttributeValue(ProductAttribute productAttribute, String productId, String name, String value)
  {
    if(productAttribute == null) return;
  
    this.productId = productId;
    this.name = name;
    this.value = value;

    this.productAttribute = productAttribute;
  }


  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_ATTRIBUTE table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the primary key of the NAME column of the PRODUCT_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the PRODUCT_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the PRODUCT_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(productAttribute!=null) productAttribute.setValue(value);
  }

  /** Get the value object of the ProductAttribute class. */
  public ProductAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductAttribute class. */
  public void setValueObject(ProductAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productAttribute!=null) productAttribute.setValueObject(valueObject);

    if(productId == null) productId = valueObject.getProductId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get a collection of  ProductTypeAttr related entities. */
  public Collection getProductTypeAttrs() { return ProductTypeAttrHelper.findByName(name); }
  /** Get the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductTypeAttr getProductTypeAttr(String productTypeId) { return ProductTypeAttrHelper.findByPrimaryKey(productTypeId, name); }
  /** Remove  ProductTypeAttr related entities. */
  public void removeProductTypeAttrs() { ProductTypeAttrHelper.removeByName(name); }
  /** Remove the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductTypeAttr(String productTypeId) { ProductTypeAttrHelper.removeByPrimaryKey(productTypeId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productAttribute!=null) return productAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productAttribute!=null) return productAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productAttribute!=null) return productAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productAttribute!=null) return productAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productAttribute!=null) productAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
