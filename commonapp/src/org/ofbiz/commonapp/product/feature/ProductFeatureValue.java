
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.cost.*;
import org.ofbiz.commonapp.product.price.*;

/**
 * <p><b>Title:</b> Product Feature Entity
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
public class ProductFeatureValue implements ProductFeature
{
  /** The variable of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE table. */
  private String productFeatureId;
  /** The variable of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  private String productFeatureTypeId;
  /** The variable of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  private String productFeatureCategoryId;
  /** The variable of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  private String description;
  /** The variable of the UOM_ID column of the PRODUCT_FEATURE table. */
  private String uomId;
  /** The variable of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  private Long numberSpecified;

  private ProductFeature productFeature;

  public ProductFeatureValue()
  {
    this.productFeatureId = null;
    this.productFeatureTypeId = null;
    this.productFeatureCategoryId = null;
    this.description = null;
    this.uomId = null;
    this.numberSpecified = null;

    this.productFeature = null;
  }

  public ProductFeatureValue(ProductFeature productFeature) throws RemoteException
  {
    if(productFeature == null) return;
  
    this.productFeatureId = productFeature.getProductFeatureId();
    this.productFeatureTypeId = productFeature.getProductFeatureTypeId();
    this.productFeatureCategoryId = productFeature.getProductFeatureCategoryId();
    this.description = productFeature.getDescription();
    this.uomId = productFeature.getUomId();
    this.numberSpecified = productFeature.getNumberSpecified();

    this.productFeature = productFeature;
  }

  public ProductFeatureValue(ProductFeature productFeature, String productFeatureId, String productFeatureTypeId, String productFeatureCategoryId, String description, String uomId, Long numberSpecified)
  {
    if(productFeature == null) return;
  
    this.productFeatureId = productFeatureId;
    this.productFeatureTypeId = productFeatureTypeId;
    this.productFeatureCategoryId = productFeatureCategoryId;
    this.description = description;
    this.uomId = uomId;
    this.numberSpecified = numberSpecified;

    this.productFeature = productFeature;
  }


  /** Get the primary key of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureId()  throws RemoteException { return productFeatureId; }

  /** Get the value of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureTypeId() throws RemoteException { return productFeatureTypeId; }
  /** Set the value of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public void setProductFeatureTypeId(String productFeatureTypeId) throws RemoteException
  {
    this.productFeatureTypeId = productFeatureTypeId;
    if(productFeature!=null) productFeature.setProductFeatureTypeId(productFeatureTypeId);
  }

  /** Get the value of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureCategoryId() throws RemoteException { return productFeatureCategoryId; }
  /** Set the value of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public void setProductFeatureCategoryId(String productFeatureCategoryId) throws RemoteException
  {
    this.productFeatureCategoryId = productFeatureCategoryId;
    if(productFeature!=null) productFeature.setProductFeatureCategoryId(productFeatureCategoryId);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productFeature!=null) productFeature.setDescription(description);
  }

  /** Get the value of the UOM_ID column of the PRODUCT_FEATURE table. */
  public String getUomId() throws RemoteException { return uomId; }
  /** Set the value of the UOM_ID column of the PRODUCT_FEATURE table. */
  public void setUomId(String uomId) throws RemoteException
  {
    this.uomId = uomId;
    if(productFeature!=null) productFeature.setUomId(uomId);
  }

  /** Get the value of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public Long getNumberSpecified() throws RemoteException { return numberSpecified; }
  /** Set the value of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public void setNumberSpecified(Long numberSpecified) throws RemoteException
  {
    this.numberSpecified = numberSpecified;
    if(productFeature!=null) productFeature.setNumberSpecified(numberSpecified);
  }

  /** Get the value object of the ProductFeature class. */
  public ProductFeature getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeature class. */
  public void setValueObject(ProductFeature valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeature!=null) productFeature.setValueObject(valueObject);

    if(productFeatureId == null) productFeatureId = valueObject.getProductFeatureId();
    productFeatureTypeId = valueObject.getProductFeatureTypeId();
    productFeatureCategoryId = valueObject.getProductFeatureCategoryId();
    description = valueObject.getDescription();
    uomId = valueObject.getUomId();
    numberSpecified = valueObject.getNumberSpecified();
  }


  /** Get the  ProductFeatureCategory entity corresponding to this entity. */
  public ProductFeatureCategory getProductFeatureCategory() { return ProductFeatureCategoryHelper.findByPrimaryKey(productFeatureCategoryId); }
  /** Remove the  ProductFeatureCategory entity corresponding to this entity. */
  public void removeProductFeatureCategory() { ProductFeatureCategoryHelper.removeByPrimaryKey(productFeatureCategoryId); }

  /** Get the  ProductFeatureType entity corresponding to this entity. */
  public ProductFeatureType getProductFeatureType() { return ProductFeatureTypeHelper.findByPrimaryKey(productFeatureTypeId); }
  /** Remove the  ProductFeatureType entity corresponding to this entity. */
  public void removeProductFeatureType() { ProductFeatureTypeHelper.removeByPrimaryKey(productFeatureTypeId); }

  /** Get a collection of  ProductFeatureAppl related entities. */
  public Collection getProductFeatureAppls() { return ProductFeatureApplHelper.findByProductFeatureId(productFeatureId); }
  /** Get the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureAppl getProductFeatureAppl(String productId) { return ProductFeatureApplHelper.findByPrimaryKey(productId, productFeatureId); }
  /** Remove  ProductFeatureAppl related entities. */
  public void removeProductFeatureAppls() { ProductFeatureApplHelper.removeByProductFeatureId(productFeatureId); }
  /** Remove the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureAppl(String productId) { ProductFeatureApplHelper.removeByPrimaryKey(productId, productFeatureId); }

  /** Get a collection of Main ProductFeatureIactn related entities. */
  public Collection getMainProductFeatureIactns() { return ProductFeatureIactnHelper.findByProductFeatureId(productFeatureId); }
  /** Get the Main ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactn getMainProductFeatureIactn(String productFeatureIdTo) { return ProductFeatureIactnHelper.findByPrimaryKey(productFeatureId, productFeatureIdTo); }
  /** Remove Main ProductFeatureIactn related entities. */
  public void removeMainProductFeatureIactns() { ProductFeatureIactnHelper.removeByProductFeatureId(productFeatureId); }
  /** Remove the Main ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public void removeMainProductFeatureIactn(String productFeatureIdTo) { ProductFeatureIactnHelper.removeByPrimaryKey(productFeatureId, productFeatureIdTo); }

  /** Get a collection of Assoc ProductFeatureIactn related entities. */
  public Collection getAssocProductFeatureIactns() { return ProductFeatureIactnHelper.findByProductFeatureIdTo(productFeatureId); }
  /** Get the Assoc ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactn getAssocProductFeatureIactn(String productFeatureId) { return ProductFeatureIactnHelper.findByPrimaryKey(productFeatureId, productFeatureId); }
  /** Remove Assoc ProductFeatureIactn related entities. */
  public void removeAssocProductFeatureIactns() { ProductFeatureIactnHelper.removeByProductFeatureIdTo(productFeatureId); }
  /** Remove the Assoc ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocProductFeatureIactn(String productFeatureId) { ProductFeatureIactnHelper.removeByPrimaryKey(productFeatureId, productFeatureId); }

  /** Get a collection of  FeatureDataObject related entities. */
  public Collection getFeatureDataObjects() { return FeatureDataObjectHelper.findByProductFeatureId(productFeatureId); }
  /** Get the  FeatureDataObject keyed by member(s) of this class, and other passed parameters. */
  public FeatureDataObject getFeatureDataObject(String dataObjectId) { return FeatureDataObjectHelper.findByPrimaryKey(dataObjectId, productFeatureId); }
  /** Remove  FeatureDataObject related entities. */
  public void removeFeatureDataObjects() { FeatureDataObjectHelper.removeByProductFeatureId(productFeatureId); }
  /** Remove the  FeatureDataObject keyed by member(s) of this class, and other passed parameters. */
  public void removeFeatureDataObject(String dataObjectId) { FeatureDataObjectHelper.removeByPrimaryKey(dataObjectId, productFeatureId); }

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() { return CostComponentHelper.findByProductFeatureId(productFeatureId); }
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) { return CostComponentHelper.findByPrimaryKey(costComponentId); }
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() { CostComponentHelper.removeByProductFeatureId(productFeatureId); }
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) { CostComponentHelper.removeByPrimaryKey(costComponentId); }

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByProductFeatureId(productFeatureId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByProductFeatureId(productFeatureId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeature!=null) return productFeature.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeature!=null) return productFeature.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeature!=null) return productFeature.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeature!=null) return productFeature.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeature!=null) productFeature.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
