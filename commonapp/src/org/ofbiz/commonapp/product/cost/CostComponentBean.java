
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.product.feature.*;
import org.ofbiz.commonapp.party.party.*;
import org.ofbiz.commonapp.common.geo.*;

/**
 * <p><b>Title:</b> Cost Component Entity
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
 *@created    Fri Jul 27 01:18:29 MDT 2001
 *@version    1.0
 */
public class CostComponentBean implements EntityBean
{
  /** The variable for the COST_COMPONENT_ID column of the COST_COMPONENT table. */
  public String costComponentId;
  /** The variable for the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public String costComponentTypeId;
  /** The variable for the PRODUCT_ID column of the COST_COMPONENT table. */
  public String productId;
  /** The variable for the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public String productFeatureId;
  /** The variable for the PARTY_ID column of the COST_COMPONENT table. */
  public String partyId;
  /** The variable for the GEO_ID column of the COST_COMPONENT table. */
  public String geoId;
  /** The variable for the FROM_DATE column of the COST_COMPONENT table. */
  public java.util.Date fromDate;
  /** The variable for the THRU_DATE column of the COST_COMPONENT table. */
  public java.util.Date thruDate;
  /** The variable for the COST column of the COST_COMPONENT table. */
  public Double cost;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the CostComponentBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key COST_COMPONENT_ID column of the COST_COMPONENT table. */
  public String getCostComponentId() { return costComponentId; }

  /** Get the value of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public String getCostComponentTypeId() { return costComponentTypeId; }
  /** Set the value of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public void setCostComponentTypeId(String costComponentTypeId)
  {
    this.costComponentTypeId = costComponentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_ID column of the COST_COMPONENT table. */
  public String getProductId() { return productId; }
  /** Set the value of the PRODUCT_ID column of the COST_COMPONENT table. */
  public void setProductId(String productId)
  {
    this.productId = productId;
    ejbIsModified = true;
  }

  /** Get the value of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public String getProductFeatureId() { return productFeatureId; }
  /** Set the value of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public void setProductFeatureId(String productFeatureId)
  {
    this.productFeatureId = productFeatureId;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_ID column of the COST_COMPONENT table. */
  public String getPartyId() { return partyId; }
  /** Set the value of the PARTY_ID column of the COST_COMPONENT table. */
  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
    ejbIsModified = true;
  }

  /** Get the value of the GEO_ID column of the COST_COMPONENT table. */
  public String getGeoId() { return geoId; }
  /** Set the value of the GEO_ID column of the COST_COMPONENT table. */
  public void setGeoId(String geoId)
  {
    this.geoId = geoId;
    ejbIsModified = true;
  }

  /** Get the value of the FROM_DATE column of the COST_COMPONENT table. */
  public java.util.Date getFromDate() { return fromDate; }
  /** Set the value of the FROM_DATE column of the COST_COMPONENT table. */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_DATE column of the COST_COMPONENT table. */
  public java.util.Date getThruDate() { return thruDate; }
  /** Set the value of the THRU_DATE column of the COST_COMPONENT table. */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
    ejbIsModified = true;
  }

  /** Get the value of the COST column of the COST_COMPONENT table. */
  public Double getCost() { return cost; }
  /** Set the value of the COST column of the COST_COMPONENT table. */
  public void setCost(Double cost)
  {
    this.cost = cost;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the CostComponentBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(CostComponent valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getCostComponentTypeId() != null)
      {
        this.costComponentTypeId = valueObject.getCostComponentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getProductId() != null)
      {
        this.productId = valueObject.getProductId();
        ejbIsModified = true;
      }
      if(valueObject.getProductFeatureId() != null)
      {
        this.productFeatureId = valueObject.getProductFeatureId();
        ejbIsModified = true;
      }
      if(valueObject.getPartyId() != null)
      {
        this.partyId = valueObject.getPartyId();
        ejbIsModified = true;
      }
      if(valueObject.getGeoId() != null)
      {
        this.geoId = valueObject.getGeoId();
        ejbIsModified = true;
      }
      if(valueObject.getFromDate() != null)
      {
        this.fromDate = valueObject.getFromDate();
        ejbIsModified = true;
      }
      if(valueObject.getThruDate() != null)
      {
        this.thruDate = valueObject.getThruDate();
        ejbIsModified = true;
      }
      if(valueObject.getCost() != null)
      {
        this.cost = valueObject.getCost();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the CostComponentBean object
   *@return    The ValueObject value
   */
  public CostComponent getValueObject()
  {
    if(this.entityContext != null)
    {
      return new CostComponentValue((CostComponent)this.entityContext.getEJBObject(), costComponentId, costComponentTypeId, productId, productFeatureId, partyId, geoId, fromDate, thruDate, cost);
    }
    else { return null; }
  }


  /** Get the  CostComponentType entity corresponding to this entity. */
  public CostComponentType getCostComponentType() { return CostComponentTypeHelper.findByPrimaryKey(costComponentTypeId); }
  /** Remove the  CostComponentType entity corresponding to this entity. */
  public void removeCostComponentType() { CostComponentTypeHelper.removeByPrimaryKey(costComponentTypeId); }

  /** Get a collection of  CostComponentTypeAttr related entities. */
  public Collection getCostComponentTypeAttrs() { return CostComponentTypeAttrHelper.findByCostComponentTypeId(costComponentTypeId); }
  /** Get the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public CostComponentTypeAttr getCostComponentTypeAttr(String name) { return CostComponentTypeAttrHelper.findByPrimaryKey(costComponentTypeId, name); }
  /** Remove  CostComponentTypeAttr related entities. */
  public void removeCostComponentTypeAttrs() { CostComponentTypeAttrHelper.removeByCostComponentTypeId(costComponentTypeId); }
  /** Remove the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentTypeAttr(String name) { CostComponentTypeAttrHelper.removeByPrimaryKey(costComponentTypeId, name); }

  /** Get a collection of  CostComponentAttribute related entities. */
  public Collection getCostComponentAttributes() { return CostComponentAttributeHelper.findByCostComponentId(costComponentId); }
  /** Get the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public CostComponentAttribute getCostComponentAttribute(String name) { return CostComponentAttributeHelper.findByPrimaryKey(costComponentId, name); }
  /** Remove  CostComponentAttribute related entities. */
  public void removeCostComponentAttributes() { CostComponentAttributeHelper.removeByCostComponentId(costComponentId); }
  /** Remove the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentAttribute(String name) { CostComponentAttributeHelper.removeByPrimaryKey(costComponentId, name); }

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  Geo entity corresponding to this entity. */
  public Geo getGeo() { return GeoHelper.findByPrimaryKey(geoId); }
  /** Remove the  Geo entity corresponding to this entity. */
  public void removeGeo() { GeoHelper.removeByPrimaryKey(geoId); }


  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  cost                  Field of the COST column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String costComponentId, String costComponentTypeId, String productId, String productFeatureId, String partyId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double cost) throws CreateException
  {
    this.costComponentId = costComponentId;
    this.costComponentTypeId = costComponentTypeId;
    this.productId = productId;
    this.productFeatureId = productFeatureId;
    this.partyId = partyId;
    this.geoId = geoId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.cost = cost;
    return null;
  }

  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String costComponentId) throws CreateException
  {
    return ejbCreate(costComponentId, null, null, null, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productFeatureId                  Field of the PRODUCT_FEATURE_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  cost                  Field of the COST column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String costComponentId, String costComponentTypeId, String productId, String productFeatureId, String partyId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double cost) throws CreateException {}

  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String costComponentId) throws CreateException
  {
    ejbPostCreate(costComponentId, null, null, null, null, null, null, null, null);
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
