
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Category Type Attribute Entity
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
public class ProductCategoryTypeAttrValue implements ProductCategoryTypeAttr
{
  /** The variable of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE_ATTR table. */
  private String productCategoryTypeId;
  /** The variable of the NAME column of the PRODUCT_CATEGORY_TYPE_ATTR table. */
  private String name;

  private ProductCategoryTypeAttr productCategoryTypeAttr;

  public ProductCategoryTypeAttrValue()
  {
    this.productCategoryTypeId = null;
    this.name = null;

    this.productCategoryTypeAttr = null;
  }

  public ProductCategoryTypeAttrValue(ProductCategoryTypeAttr productCategoryTypeAttr) throws RemoteException
  {
    if(productCategoryTypeAttr == null) return;
  
    this.productCategoryTypeId = productCategoryTypeAttr.getProductCategoryTypeId();
    this.name = productCategoryTypeAttr.getName();

    this.productCategoryTypeAttr = productCategoryTypeAttr;
  }

  public ProductCategoryTypeAttrValue(ProductCategoryTypeAttr productCategoryTypeAttr, String productCategoryTypeId, String name)
  {
    if(productCategoryTypeAttr == null) return;
  
    this.productCategoryTypeId = productCategoryTypeId;
    this.name = name;

    this.productCategoryTypeAttr = productCategoryTypeAttr;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE_ATTR table. */
  public String getProductCategoryTypeId()  throws RemoteException { return productCategoryTypeId; }

  /** Get the primary key of the NAME column of the PRODUCT_CATEGORY_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the ProductCategoryTypeAttr class. */
  public ProductCategoryTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategoryTypeAttr class. */
  public void setValueObject(ProductCategoryTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategoryTypeAttr!=null) productCategoryTypeAttr.setValueObject(valueObject);

    if(productCategoryTypeId == null) productCategoryTypeId = valueObject.getProductCategoryTypeId();
    if(name == null) name = valueObject.getName();
  }


  /** Get the  ProductCategoryType entity corresponding to this entity. */
  public ProductCategoryType getProductCategoryType() { return ProductCategoryTypeHelper.findByPrimaryKey(productCategoryTypeId); }
  /** Remove the  ProductCategoryType entity corresponding to this entity. */
  public void removeProductCategoryType() { ProductCategoryTypeHelper.removeByPrimaryKey(productCategoryTypeId); }

  /** Get a collection of  ProductCategoryAttribute related entities. */
  public Collection getProductCategoryAttributes() { return ProductCategoryAttributeHelper.findByName(name); }
  /** Get the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryAttribute getProductCategoryAttribute(String productCategoryId) { return ProductCategoryAttributeHelper.findByPrimaryKey(productCategoryId, name); }
  /** Remove  ProductCategoryAttribute related entities. */
  public void removeProductCategoryAttributes() { ProductCategoryAttributeHelper.removeByName(name); }
  /** Remove the  ProductCategoryAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryAttribute(String productCategoryId) { ProductCategoryAttributeHelper.removeByPrimaryKey(productCategoryId, name); }

  /** Get a collection of  ProductCategoryClass related entities. */
  public Collection getProductCategoryClasss() { return ProductCategoryClassHelper.findByProductCategoryTypeId(productCategoryTypeId); }
  /** Get the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryClass getProductCategoryClass(String productCategoryId) { return ProductCategoryClassHelper.findByPrimaryKey(productCategoryId, productCategoryTypeId); }
  /** Remove  ProductCategoryClass related entities. */
  public void removeProductCategoryClasss() { ProductCategoryClassHelper.removeByProductCategoryTypeId(productCategoryTypeId); }
  /** Remove the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryClass(String productCategoryId) { ProductCategoryClassHelper.removeByPrimaryKey(productCategoryId, productCategoryTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategoryTypeAttr!=null) return productCategoryTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategoryTypeAttr!=null) return productCategoryTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategoryTypeAttr!=null) return productCategoryTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategoryTypeAttr!=null) return productCategoryTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategoryTypeAttr!=null) productCategoryTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
