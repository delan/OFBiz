
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Feature Applicability Type Entity
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
public class ProductFeatureApplTypeValue implements ProductFeatureApplType
{
  /** The variable of the PRODUCT_FEATURE_APPL_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  private String productFeatureApplTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRODUCT_FEATURE_APPL_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRODUCT_FEATURE_APPL_TYPE table. */
  private String description;

  private ProductFeatureApplType productFeatureApplType;

  public ProductFeatureApplTypeValue()
  {
    this.productFeatureApplTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.productFeatureApplType = null;
  }

  public ProductFeatureApplTypeValue(ProductFeatureApplType productFeatureApplType) throws RemoteException
  {
    if(productFeatureApplType == null) return;
  
    this.productFeatureApplTypeId = productFeatureApplType.getProductFeatureApplTypeId();
    this.parentTypeId = productFeatureApplType.getParentTypeId();
    this.hasTable = productFeatureApplType.getHasTable();
    this.description = productFeatureApplType.getDescription();

    this.productFeatureApplType = productFeatureApplType;
  }

  public ProductFeatureApplTypeValue(ProductFeatureApplType productFeatureApplType, String productFeatureApplTypeId, String parentTypeId, String hasTable, String description)
  {
    if(productFeatureApplType == null) return;
  
    this.productFeatureApplTypeId = productFeatureApplTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.productFeatureApplType = productFeatureApplType;
  }


  /** Get the primary key of the PRODUCT_FEATURE_APPL_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getProductFeatureApplTypeId()  throws RemoteException { return productFeatureApplTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(productFeatureApplType!=null) productFeatureApplType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(productFeatureApplType!=null) productFeatureApplType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE_APPL_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productFeatureApplType!=null) productFeatureApplType.setDescription(description);
  }

  /** Get the value object of the ProductFeatureApplType class. */
  public ProductFeatureApplType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeatureApplType class. */
  public void setValueObject(ProductFeatureApplType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeatureApplType!=null) productFeatureApplType.setValueObject(valueObject);

    if(productFeatureApplTypeId == null) productFeatureApplTypeId = valueObject.getProductFeatureApplTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent ProductFeatureApplType entity corresponding to this entity. */
  public ProductFeatureApplType getParentProductFeatureApplType() { return ProductFeatureApplTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductFeatureApplType entity corresponding to this entity. */
  public void removeParentProductFeatureApplType() { ProductFeatureApplTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductFeatureApplType related entities. */
  public Collection getChildProductFeatureApplTypes() { return ProductFeatureApplTypeHelper.findByParentTypeId(productFeatureApplTypeId); }
  /** Get the Child ProductFeatureApplType keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureApplType getChildProductFeatureApplType(String productFeatureApplTypeId) { return ProductFeatureApplTypeHelper.findByPrimaryKey(productFeatureApplTypeId); }
  /** Remove Child ProductFeatureApplType related entities. */
  public void removeChildProductFeatureApplTypes() { ProductFeatureApplTypeHelper.removeByParentTypeId(productFeatureApplTypeId); }
  /** Remove the Child ProductFeatureApplType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductFeatureApplType(String productFeatureApplTypeId) { ProductFeatureApplTypeHelper.removeByPrimaryKey(productFeatureApplTypeId); }

  /** Get a collection of  ProductFeatureAppl related entities. */
  public Collection getProductFeatureAppls() { return ProductFeatureApplHelper.findByProductFeatureApplTypeId(productFeatureApplTypeId); }
  /** Get the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureAppl getProductFeatureAppl(String productId, String productFeatureId) { return ProductFeatureApplHelper.findByPrimaryKey(productId, productFeatureId); }
  /** Remove  ProductFeatureAppl related entities. */
  public void removeProductFeatureAppls() { ProductFeatureApplHelper.removeByProductFeatureApplTypeId(productFeatureApplTypeId); }
  /** Remove the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureAppl(String productId, String productFeatureId) { ProductFeatureApplHelper.removeByPrimaryKey(productId, productFeatureId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeatureApplType!=null) return productFeatureApplType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeatureApplType!=null) return productFeatureApplType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeatureApplType!=null) return productFeatureApplType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeatureApplType!=null) return productFeatureApplType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeatureApplType!=null) productFeatureApplType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
