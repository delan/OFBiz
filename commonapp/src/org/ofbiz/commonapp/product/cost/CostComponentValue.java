
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class CostComponentValue implements CostComponent
{
  /** The variable of the COST_COMPONENT_ID column of the COST_COMPONENT table. */
  private String costComponentId;
  /** The variable of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  private String costComponentTypeId;
  /** The variable of the PRODUCT_ID column of the COST_COMPONENT table. */
  private String productId;
  /** The variable of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  private String productFeatureId;
  /** The variable of the PARTY_ID column of the COST_COMPONENT table. */
  private String partyId;
  /** The variable of the GEO_ID column of the COST_COMPONENT table. */
  private String geoId;
  /** The variable of the FROM_DATE column of the COST_COMPONENT table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the COST_COMPONENT table. */
  private java.util.Date thruDate;
  /** The variable of the COST column of the COST_COMPONENT table. */
  private Double cost;

  private CostComponent costComponent;

  public CostComponentValue()
  {
    this.costComponentId = null;
    this.costComponentTypeId = null;
    this.productId = null;
    this.productFeatureId = null;
    this.partyId = null;
    this.geoId = null;
    this.fromDate = null;
    this.thruDate = null;
    this.cost = null;

    this.costComponent = null;
  }

  public CostComponentValue(CostComponent costComponent) throws RemoteException
  {
    if(costComponent == null) return;
  
    this.costComponentId = costComponent.getCostComponentId();
    this.costComponentTypeId = costComponent.getCostComponentTypeId();
    this.productId = costComponent.getProductId();
    this.productFeatureId = costComponent.getProductFeatureId();
    this.partyId = costComponent.getPartyId();
    this.geoId = costComponent.getGeoId();
    this.fromDate = costComponent.getFromDate();
    this.thruDate = costComponent.getThruDate();
    this.cost = costComponent.getCost();

    this.costComponent = costComponent;
  }

  public CostComponentValue(CostComponent costComponent, String costComponentId, String costComponentTypeId, String productId, String productFeatureId, String partyId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double cost)
  {
    if(costComponent == null) return;
  
    this.costComponentId = costComponentId;
    this.costComponentTypeId = costComponentTypeId;
    this.productId = productId;
    this.productFeatureId = productFeatureId;
    this.partyId = partyId;
    this.geoId = geoId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.cost = cost;

    this.costComponent = costComponent;
  }


  /** Get the primary key of the COST_COMPONENT_ID column of the COST_COMPONENT table. */
  public String getCostComponentId()  throws RemoteException { return costComponentId; }

  /** Get the value of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public String getCostComponentTypeId() throws RemoteException { return costComponentTypeId; }
  /** Set the value of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public void setCostComponentTypeId(String costComponentTypeId) throws RemoteException
  {
    this.costComponentTypeId = costComponentTypeId;
    if(costComponent!=null) costComponent.setCostComponentTypeId(costComponentTypeId);
  }

  /** Get the value of the PRODUCT_ID column of the COST_COMPONENT table. */
  public String getProductId() throws RemoteException { return productId; }
  /** Set the value of the PRODUCT_ID column of the COST_COMPONENT table. */
  public void setProductId(String productId) throws RemoteException
  {
    this.productId = productId;
    if(costComponent!=null) costComponent.setProductId(productId);
  }

  /** Get the value of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public String getProductFeatureId() throws RemoteException { return productFeatureId; }
  /** Set the value of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public void setProductFeatureId(String productFeatureId) throws RemoteException
  {
    this.productFeatureId = productFeatureId;
    if(costComponent!=null) costComponent.setProductFeatureId(productFeatureId);
  }

  /** Get the value of the PARTY_ID column of the COST_COMPONENT table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the COST_COMPONENT table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(costComponent!=null) costComponent.setPartyId(partyId);
  }

  /** Get the value of the GEO_ID column of the COST_COMPONENT table. */
  public String getGeoId() throws RemoteException { return geoId; }
  /** Set the value of the GEO_ID column of the COST_COMPONENT table. */
  public void setGeoId(String geoId) throws RemoteException
  {
    this.geoId = geoId;
    if(costComponent!=null) costComponent.setGeoId(geoId);
  }

  /** Get the value of the FROM_DATE column of the COST_COMPONENT table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the COST_COMPONENT table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(costComponent!=null) costComponent.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the COST_COMPONENT table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the COST_COMPONENT table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(costComponent!=null) costComponent.setThruDate(thruDate);
  }

  /** Get the value of the COST column of the COST_COMPONENT table. */
  public Double getCost() throws RemoteException { return cost; }
  /** Set the value of the COST column of the COST_COMPONENT table. */
  public void setCost(Double cost) throws RemoteException
  {
    this.cost = cost;
    if(costComponent!=null) costComponent.setCost(cost);
  }

  /** Get the value object of the CostComponent class. */
  public CostComponent getValueObject() throws RemoteException { return this; }
  /** Set the value object of the CostComponent class. */
  public void setValueObject(CostComponent valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(costComponent!=null) costComponent.setValueObject(valueObject);

    if(costComponentId == null) costComponentId = valueObject.getCostComponentId();
    costComponentTypeId = valueObject.getCostComponentTypeId();
    productId = valueObject.getProductId();
    productFeatureId = valueObject.getProductFeatureId();
    partyId = valueObject.getPartyId();
    geoId = valueObject.getGeoId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
    cost = valueObject.getCost();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(costComponent!=null) return costComponent.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(costComponent!=null) return costComponent.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(costComponent!=null) return costComponent.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(costComponent!=null) return costComponent.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(costComponent!=null) costComponent.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
