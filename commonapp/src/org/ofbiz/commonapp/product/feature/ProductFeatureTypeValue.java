
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Feature Type Entity
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
public class ProductFeatureTypeValue implements ProductFeatureType
{
  /** The variable of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE_TYPE table. */
  private String productFeatureTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRODUCT_FEATURE_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRODUCT_FEATURE_TYPE table. */
  private String description;

  private ProductFeatureType productFeatureType;

  public ProductFeatureTypeValue()
  {
    this.productFeatureTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.productFeatureType = null;
  }

  public ProductFeatureTypeValue(ProductFeatureType productFeatureType) throws RemoteException
  {
    if(productFeatureType == null) return;
  
    this.productFeatureTypeId = productFeatureType.getProductFeatureTypeId();
    this.parentTypeId = productFeatureType.getParentTypeId();
    this.hasTable = productFeatureType.getHasTable();
    this.description = productFeatureType.getDescription();

    this.productFeatureType = productFeatureType;
  }

  public ProductFeatureTypeValue(ProductFeatureType productFeatureType, String productFeatureTypeId, String parentTypeId, String hasTable, String description)
  {
    if(productFeatureType == null) return;
  
    this.productFeatureTypeId = productFeatureTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.productFeatureType = productFeatureType;
  }


  /** Get the primary key of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE_TYPE table. */
  public String getProductFeatureTypeId()  throws RemoteException { return productFeatureTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_FEATURE_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(productFeatureType!=null) productFeatureType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_FEATURE_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_FEATURE_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(productFeatureType!=null) productFeatureType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productFeatureType!=null) productFeatureType.setDescription(description);
  }

  /** Get the value object of the ProductFeatureType class. */
  public ProductFeatureType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeatureType class. */
  public void setValueObject(ProductFeatureType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeatureType!=null) productFeatureType.setValueObject(valueObject);

    if(productFeatureTypeId == null) productFeatureTypeId = valueObject.getProductFeatureTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent ProductFeatureType entity corresponding to this entity. */
  public ProductFeatureType getParentProductFeatureType() { return ProductFeatureTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductFeatureType entity corresponding to this entity. */
  public void removeParentProductFeatureType() { ProductFeatureTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductFeatureType related entities. */
  public Collection getChildProductFeatureTypes() { return ProductFeatureTypeHelper.findByParentTypeId(productFeatureTypeId); }
  /** Get the Child ProductFeatureType keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureType getChildProductFeatureType(String productFeatureTypeId) { return ProductFeatureTypeHelper.findByPrimaryKey(productFeatureTypeId); }
  /** Remove Child ProductFeatureType related entities. */
  public void removeChildProductFeatureTypes() { ProductFeatureTypeHelper.removeByParentTypeId(productFeatureTypeId); }
  /** Remove the Child ProductFeatureType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductFeatureType(String productFeatureTypeId) { ProductFeatureTypeHelper.removeByPrimaryKey(productFeatureTypeId); }

  /** Get a collection of  ProductFeature related entities. */
  public Collection getProductFeatures() { return ProductFeatureHelper.findByProductFeatureTypeId(productFeatureTypeId); }
  /** Get the  ProductFeature keyed by member(s) of this class, and other passed parameters. */
  public ProductFeature getProductFeature(String productFeatureId) { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove  ProductFeature related entities. */
  public void removeProductFeatures() { ProductFeatureHelper.removeByProductFeatureTypeId(productFeatureTypeId); }
  /** Remove the  ProductFeature keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeature(String productFeatureId) { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeatureType!=null) return productFeatureType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeatureType!=null) return productFeatureType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeatureType!=null) return productFeatureType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeatureType!=null) return productFeatureType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeatureType!=null) productFeatureType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
