
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

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
public class ProductFeatureBean implements EntityBean
{
  /** The variable for the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE table. */
  public String productFeatureId;
  /** The variable for the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public String productFeatureTypeId;
  /** The variable for the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public String productFeatureCategoryId;
  /** The variable for the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public String description;
  /** The variable for the UOM_ID column of the PRODUCT_FEATURE table. */
  public String uomId;
  /** The variable for the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public Long numberSpecified;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductFeatureBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureId() { return productFeatureId; }

  /** Get the value of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureTypeId() { return productFeatureTypeId; }
  /** Set the value of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public void setProductFeatureTypeId(String productFeatureTypeId)
  {
    this.productFeatureTypeId = productFeatureTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureCategoryId() { return productFeatureCategoryId; }
  /** Set the value of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public void setProductFeatureCategoryId(String productFeatureCategoryId)
  {
    this.productFeatureCategoryId = productFeatureCategoryId;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Get the value of the UOM_ID column of the PRODUCT_FEATURE table. */
  public String getUomId() { return uomId; }
  /** Set the value of the UOM_ID column of the PRODUCT_FEATURE table. */
  public void setUomId(String uomId)
  {
    this.uomId = uomId;
    ejbIsModified = true;
  }

  /** Get the value of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public Long getNumberSpecified() { return numberSpecified; }
  /** Set the value of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public void setNumberSpecified(Long numberSpecified)
  {
    this.numberSpecified = numberSpecified;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductFeatureBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductFeature valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getProductFeatureTypeId() != null)
      {
        this.productFeatureTypeId = valueObject.getProductFeatureTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getProductFeatureCategoryId() != null)
      {
        this.productFeatureCategoryId = valueObject.getProductFeatureCategoryId();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
      if(valueObject.getUomId() != null)
      {
        this.uomId = valueObject.getUomId();
        ejbIsModified = true;
      }
      if(valueObject.getNumberSpecified() != null)
      {
        this.numberSpecified = valueObject.getNumberSpecified();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductFeatureBean object
   *@return    The ValueObject value
   */
  public ProductFeature getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductFeatureValue((ProductFeature)this.entityContext.getEJBObject(), productFeatureId, productFeatureTypeId, productFeatureCategoryId, description, uomId, numberSpecified);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  numberSpecified                  Field of the NUMBER_SPECIFIED column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productFeatureId, String productFeatureTypeId, String productFeatureCategoryId, String description, String uomId, Long numberSpecified) throws CreateException
  {
    this.productFeatureId = productFeatureId;
    this.productFeatureTypeId = productFeatureTypeId;
    this.productFeatureCategoryId = productFeatureCategoryId;
    this.description = description;
    this.uomId = uomId;
    this.numberSpecified = numberSpecified;
    return null;
  }

  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productFeatureId) throws CreateException
  {
    return ejbCreate(productFeatureId, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  productFeatureTypeId                  Field of the PRODUCT_FEATURE_TYPE_ID column.
   *@param  productFeatureCategoryId                  Field of the PRODUCT_FEATURE_CATEGORY_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@param  uomId                  Field of the UOM_ID column.
   *@param  numberSpecified                  Field of the NUMBER_SPECIFIED column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productFeatureId, String productFeatureTypeId, String productFeatureCategoryId, String description, String uomId, Long numberSpecified) throws CreateException {}

  /** Description of the Method
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productFeatureId) throws CreateException
  {
    ejbPostCreate(productFeatureId, null, null, null, null, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
