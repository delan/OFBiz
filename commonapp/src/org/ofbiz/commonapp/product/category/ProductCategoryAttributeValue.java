
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
 *@created    Fri Jul 27 01:18:27 MDT 2001
 *@version    1.0
 */
public class ProductCategoryAttributeValue implements ProductCategoryAttribute
{
  /** The variable of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  private String productCategoryId;
  /** The variable of the NAME column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  private String value;

  private ProductCategoryAttribute productCategoryAttribute;

  public ProductCategoryAttributeValue()
  {
    this.productCategoryId = null;
    this.name = null;
    this.value = null;

    this.productCategoryAttribute = null;
  }

  public ProductCategoryAttributeValue(ProductCategoryAttribute productCategoryAttribute) throws RemoteException
  {
    if(productCategoryAttribute == null) return;
  
    this.productCategoryId = productCategoryAttribute.getProductCategoryId();
    this.name = productCategoryAttribute.getName();
    this.value = productCategoryAttribute.getValue();

    this.productCategoryAttribute = productCategoryAttribute;
  }

  public ProductCategoryAttributeValue(ProductCategoryAttribute productCategoryAttribute, String productCategoryId, String name, String value)
  {
    if(productCategoryAttribute == null) return;
  
    this.productCategoryId = productCategoryId;
    this.name = name;
    this.value = value;

    this.productCategoryAttribute = productCategoryAttribute;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getProductCategoryId()  throws RemoteException { return productCategoryId; }

  /** Get the primary key of the NAME column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the PRODUCT_CATEGORY_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(productCategoryAttribute!=null) productCategoryAttribute.setValue(value);
  }

  /** Get the value object of the ProductCategoryAttribute class. */
  public ProductCategoryAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategoryAttribute class. */
  public void setValueObject(ProductCategoryAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategoryAttribute!=null) productCategoryAttribute.setValueObject(valueObject);

    if(productCategoryId == null) productCategoryId = valueObject.getProductCategoryId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
  }


  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() { return ProductCategoryTypeAttrHelper.findByName(name); }
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String productCategoryTypeId) { return ProductCategoryTypeAttrHelper.findByPrimaryKey(productCategoryTypeId, name); }
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() { ProductCategoryTypeAttrHelper.removeByName(name); }
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String productCategoryTypeId) { ProductCategoryTypeAttrHelper.removeByPrimaryKey(productCategoryTypeId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategoryAttribute!=null) return productCategoryAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategoryAttribute!=null) return productCategoryAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategoryAttribute!=null) return productCategoryAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategoryAttribute!=null) return productCategoryAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategoryAttribute!=null) productCategoryAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
