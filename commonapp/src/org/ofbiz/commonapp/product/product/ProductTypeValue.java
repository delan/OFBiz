
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Type Entity
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
public class ProductTypeValue implements ProductType
{
  /** The variable of the PRODUCT_TYPE_ID column of the PRODUCT_TYPE table. */
  private String productTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRODUCT_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRODUCT_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRODUCT_TYPE table. */
  private String description;

  private ProductType productType;

  public ProductTypeValue()
  {
    this.productTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.productType = null;
  }

  public ProductTypeValue(ProductType productType) throws RemoteException
  {
    if(productType == null) return;
  
    this.productTypeId = productType.getProductTypeId();
    this.parentTypeId = productType.getParentTypeId();
    this.hasTable = productType.getHasTable();
    this.description = productType.getDescription();

    this.productType = productType;
  }

  public ProductTypeValue(ProductType productType, String productTypeId, String parentTypeId, String hasTable, String description)
  {
    if(productType == null) return;
  
    this.productTypeId = productTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.productType = productType;
  }


  /** Get the primary key of the PRODUCT_TYPE_ID column of the PRODUCT_TYPE table. */
  public String getProductTypeId()  throws RemoteException { return productTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(productType!=null) productType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(productType!=null) productType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productType!=null) productType.setDescription(description);
  }

  /** Get the value object of the ProductType class. */
  public ProductType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductType class. */
  public void setValueObject(ProductType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productType!=null) productType.setValueObject(valueObject);

    if(productTypeId == null) productTypeId = valueObject.getProductTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent ProductType entity corresponding to this entity. */
  public ProductType getParentProductType() { return ProductTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductType entity corresponding to this entity. */
  public void removeParentProductType() { ProductTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductType related entities. */
  public Collection getChildProductTypes() { return ProductTypeHelper.findByParentTypeId(productTypeId); }
  /** Get the Child ProductType keyed by member(s) of this class, and other passed parameters. */
  public ProductType getChildProductType(String productTypeId) { return ProductTypeHelper.findByPrimaryKey(productTypeId); }
  /** Remove Child ProductType related entities. */
  public void removeChildProductTypes() { ProductTypeHelper.removeByParentTypeId(productTypeId); }
  /** Remove the Child ProductType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductType(String productTypeId) { ProductTypeHelper.removeByPrimaryKey(productTypeId); }

  /** Get a collection of  ProductTypeAttr related entities. */
  public Collection getProductTypeAttrs() { return ProductTypeAttrHelper.findByProductTypeId(productTypeId); }
  /** Get the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductTypeAttr getProductTypeAttr(String name) { return ProductTypeAttrHelper.findByPrimaryKey(productTypeId, name); }
  /** Remove  ProductTypeAttr related entities. */
  public void removeProductTypeAttrs() { ProductTypeAttrHelper.removeByProductTypeId(productTypeId); }
  /** Remove the  ProductTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductTypeAttr(String name) { ProductTypeAttrHelper.removeByPrimaryKey(productTypeId, name); }

  /** Get a collection of  ProductClass related entities. */
  public Collection getProductClasss() { return ProductClassHelper.findByProductTypeId(productTypeId); }
  /** Get the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public ProductClass getProductClass(String productId) { return ProductClassHelper.findByPrimaryKey(productId, productTypeId); }
  /** Remove  ProductClass related entities. */
  public void removeProductClasss() { ProductClassHelper.removeByProductTypeId(productTypeId); }
  /** Remove the  ProductClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductClass(String productId) { ProductClassHelper.removeByPrimaryKey(productId, productTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productType!=null) return productType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productType!=null) return productType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productType!=null) return productType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productType!=null) return productType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productType!=null) productType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
