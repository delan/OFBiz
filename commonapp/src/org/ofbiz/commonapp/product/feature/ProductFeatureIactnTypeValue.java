
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Feature Interaction Type Entity
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
public class ProductFeatureIactnTypeValue implements ProductFeatureIactnType
{
  /** The variable of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  private String productFeatureIactnTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  private String description;

  private ProductFeatureIactnType productFeatureIactnType;

  public ProductFeatureIactnTypeValue()
  {
    this.productFeatureIactnTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.productFeatureIactnType = null;
  }

  public ProductFeatureIactnTypeValue(ProductFeatureIactnType productFeatureIactnType) throws RemoteException
  {
    if(productFeatureIactnType == null) return;
  
    this.productFeatureIactnTypeId = productFeatureIactnType.getProductFeatureIactnTypeId();
    this.parentTypeId = productFeatureIactnType.getParentTypeId();
    this.hasTable = productFeatureIactnType.getHasTable();
    this.description = productFeatureIactnType.getDescription();

    this.productFeatureIactnType = productFeatureIactnType;
  }

  public ProductFeatureIactnTypeValue(ProductFeatureIactnType productFeatureIactnType, String productFeatureIactnTypeId, String parentTypeId, String hasTable, String description)
  {
    if(productFeatureIactnType == null) return;
  
    this.productFeatureIactnTypeId = productFeatureIactnTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.productFeatureIactnType = productFeatureIactnType;
  }


  /** Get the primary key of the PRODUCT_FEATURE_IACTN_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getProductFeatureIactnTypeId()  throws RemoteException { return productFeatureIactnTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(productFeatureIactnType!=null) productFeatureIactnType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(productFeatureIactnType!=null) productFeatureIactnType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE_IACTN_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productFeatureIactnType!=null) productFeatureIactnType.setDescription(description);
  }

  /** Get the value object of the ProductFeatureIactnType class. */
  public ProductFeatureIactnType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeatureIactnType class. */
  public void setValueObject(ProductFeatureIactnType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeatureIactnType!=null) productFeatureIactnType.setValueObject(valueObject);

    if(productFeatureIactnTypeId == null) productFeatureIactnTypeId = valueObject.getProductFeatureIactnTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent ProductFeatureIactnType entity corresponding to this entity. */
  public ProductFeatureIactnType getParentProductFeatureIactnType() { return ProductFeatureIactnTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductFeatureIactnType entity corresponding to this entity. */
  public void removeParentProductFeatureIactnType() { ProductFeatureIactnTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductFeatureIactnType related entities. */
  public Collection getChildProductFeatureIactnTypes() { return ProductFeatureIactnTypeHelper.findByParentTypeId(productFeatureIactnTypeId); }
  /** Get the Child ProductFeatureIactnType keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactnType getChildProductFeatureIactnType(String productFeatureIactnTypeId) { return ProductFeatureIactnTypeHelper.findByPrimaryKey(productFeatureIactnTypeId); }
  /** Remove Child ProductFeatureIactnType related entities. */
  public void removeChildProductFeatureIactnTypes() { ProductFeatureIactnTypeHelper.removeByParentTypeId(productFeatureIactnTypeId); }
  /** Remove the Child ProductFeatureIactnType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductFeatureIactnType(String productFeatureIactnTypeId) { ProductFeatureIactnTypeHelper.removeByPrimaryKey(productFeatureIactnTypeId); }

  /** Get a collection of  ProductFeatureIactn related entities. */
  public Collection getProductFeatureIactns() { return ProductFeatureIactnHelper.findByProductFeatureIactnTypeId(productFeatureIactnTypeId); }
  /** Get the  ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactn getProductFeatureIactn(String productFeatureId, String productFeatureIdTo) { return ProductFeatureIactnHelper.findByPrimaryKey(productFeatureId, productFeatureIdTo); }
  /** Remove  ProductFeatureIactn related entities. */
  public void removeProductFeatureIactns() { ProductFeatureIactnHelper.removeByProductFeatureIactnTypeId(productFeatureIactnTypeId); }
  /** Remove the  ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureIactn(String productFeatureId, String productFeatureIdTo) { ProductFeatureIactnHelper.removeByPrimaryKey(productFeatureId, productFeatureIdTo); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeatureIactnType!=null) return productFeatureIactnType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeatureIactnType!=null) return productFeatureIactnType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeatureIactnType!=null) return productFeatureIactnType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeatureIactnType!=null) return productFeatureIactnType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeatureIactnType!=null) productFeatureIactnType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
