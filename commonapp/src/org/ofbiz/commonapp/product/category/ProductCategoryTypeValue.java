
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Category Type Entity
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
public class ProductCategoryTypeValue implements ProductCategoryType
{
  /** The variable of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  private String productCategoryTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  private String description;

  private ProductCategoryType productCategoryType;

  public ProductCategoryTypeValue()
  {
    this.productCategoryTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.productCategoryType = null;
  }

  public ProductCategoryTypeValue(ProductCategoryType productCategoryType) throws RemoteException
  {
    if(productCategoryType == null) return;
  
    this.productCategoryTypeId = productCategoryType.getProductCategoryTypeId();
    this.parentTypeId = productCategoryType.getParentTypeId();
    this.hasTable = productCategoryType.getHasTable();
    this.description = productCategoryType.getDescription();

    this.productCategoryType = productCategoryType;
  }

  public ProductCategoryTypeValue(ProductCategoryType productCategoryType, String productCategoryTypeId, String parentTypeId, String hasTable, String description)
  {
    if(productCategoryType == null) return;
  
    this.productCategoryTypeId = productCategoryTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.productCategoryType = productCategoryType;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String getProductCategoryTypeId()  throws RemoteException { return productCategoryTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(productCategoryType!=null) productCategoryType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(productCategoryType!=null) productCategoryType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productCategoryType!=null) productCategoryType.setDescription(description);
  }

  /** Get the value object of the ProductCategoryType class. */
  public ProductCategoryType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductCategoryType class. */
  public void setValueObject(ProductCategoryType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productCategoryType!=null) productCategoryType.setValueObject(valueObject);

    if(productCategoryTypeId == null) productCategoryTypeId = valueObject.getProductCategoryTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent ProductCategoryType entity corresponding to this entity. */
  public ProductCategoryType getParentProductCategoryType() { return ProductCategoryTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductCategoryType entity corresponding to this entity. */
  public void removeParentProductCategoryType() { ProductCategoryTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductCategoryType related entities. */
  public Collection getChildProductCategoryTypes() { return ProductCategoryTypeHelper.findByParentTypeId(productCategoryTypeId); }
  /** Get the Child ProductCategoryType keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryType getChildProductCategoryType(String productCategoryTypeId) { return ProductCategoryTypeHelper.findByPrimaryKey(productCategoryTypeId); }
  /** Remove Child ProductCategoryType related entities. */
  public void removeChildProductCategoryTypes() { ProductCategoryTypeHelper.removeByParentTypeId(productCategoryTypeId); }
  /** Remove the Child ProductCategoryType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductCategoryType(String productCategoryTypeId) { ProductCategoryTypeHelper.removeByPrimaryKey(productCategoryTypeId); }

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() { return ProductCategoryTypeAttrHelper.findByProductCategoryTypeId(productCategoryTypeId); }
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String name) { return ProductCategoryTypeAttrHelper.findByPrimaryKey(productCategoryTypeId, name); }
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() { ProductCategoryTypeAttrHelper.removeByProductCategoryTypeId(productCategoryTypeId); }
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String name) { ProductCategoryTypeAttrHelper.removeByPrimaryKey(productCategoryTypeId, name); }

  /** Get a collection of  ProductCategoryClass related entities. */
  public Collection getProductCategoryClasss() { return ProductCategoryClassHelper.findByProductCategoryTypeId(productCategoryTypeId); }
  /** Get the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryClass getProductCategoryClass(String productCategoryId) { return ProductCategoryClassHelper.findByPrimaryKey(productCategoryId, productCategoryTypeId); }
  /** Remove  ProductCategoryClass related entities. */
  public void removeProductCategoryClasss() { ProductCategoryClassHelper.removeByProductCategoryTypeId(productCategoryTypeId); }
  /** Remove the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryClass(String productCategoryId) { ProductCategoryClassHelper.removeByPrimaryKey(productCategoryId, productCategoryTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productCategoryType!=null) return productCategoryType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productCategoryType!=null) return productCategoryType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productCategoryType!=null) return productCategoryType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productCategoryType!=null) return productCategoryType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productCategoryType!=null) productCategoryType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
