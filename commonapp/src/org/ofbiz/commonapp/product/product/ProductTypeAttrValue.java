
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ProductTypeAttrValue implements ProductTypeAttr
{
  /** The variable of the PRODUCT_TYPE_ID column of the PRODUCT_TYPE_ATTR table. */
  private String productTypeId;
  /** The variable of the NAME column of the PRODUCT_TYPE_ATTR table. */
  private String name;

  private ProductTypeAttr productTypeAttr;

  public ProductTypeAttrValue()
  {
    this.productTypeId = null;
    this.name = null;

    this.productTypeAttr = null;
  }

  public ProductTypeAttrValue(ProductTypeAttr productTypeAttr) throws RemoteException
  {
    if(productTypeAttr == null) return;
  
    this.productTypeId = productTypeAttr.getProductTypeId();
    this.name = productTypeAttr.getName();

    this.productTypeAttr = productTypeAttr;
  }

  public ProductTypeAttrValue(ProductTypeAttr productTypeAttr, String productTypeId, String name)
  {
    if(productTypeAttr == null) return;
  
    this.productTypeId = productTypeId;
    this.name = name;

    this.productTypeAttr = productTypeAttr;
  }


  /** Get the primary key of the PRODUCT_TYPE_ID column of the PRODUCT_TYPE_ATTR table. */
  public String getProductTypeId()  throws RemoteException { return productTypeId; }

  /** Get the primary key of the NAME column of the PRODUCT_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the ProductTypeAttr class. */
  public ProductTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductTypeAttr class. */
  public void setValueObject(ProductTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productTypeAttr!=null) productTypeAttr.setValueObject(valueObject);

    if(productTypeId == null) productTypeId = valueObject.getProductTypeId();
    if(name == null) name = valueObject.getName();
  }


  /** Get the  ProductType entity corresponding to this entity. */
  public ProductType getProductType() { return ProductTypeHelper.findByPrimaryKey(productTypeId); }
  /** Remove the  ProductType entity corresponding to this entity. */
  public void removeProductType() { ProductTypeHelper.removeByPrimaryKey(productTypeId); }

  /** Get a collection of  ProductAttribute related entities. */
  public Collection getProductAttributes() { return ProductAttributeHelper.findByName(name); }
  /** Get the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductAttribute getProductAttribute(String productId) { return ProductAttributeHelper.findByPrimaryKey(productId, name); }
  /** Remove  ProductAttribute related entities. */
  public void removeProductAttributes() { ProductAttributeHelper.removeByName(name); }
  /** Remove the  ProductAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAttribute(String productId) { ProductAttributeHelper.removeByPrimaryKey(productId, name); }

  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() { return ProductClassHelper.findByProductTypeId(productTypeId); }
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productId) { return ProductClassHelper.findByPrimaryKey(productId, productTypeId); }
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() { ProductClassHelper.removeByProductTypeId(productTypeId); }
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productId) { ProductClassHelper.removeByPrimaryKey(productId, productTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productTypeAttr!=null) return productTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productTypeAttr!=null) return productTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productTypeAttr!=null) return productTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productTypeAttr!=null) return productTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productTypeAttr!=null) productTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
