
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
 *@created    Fri Jul 27 01:18:28 MDT 2001
 *@version    1.0
 */

public interface CostComponent extends EJBObject
{
  /** Get the primary key of the COST_COMPONENT_ID column of the COST_COMPONENT table. */
  public String getCostComponentId() throws RemoteException;
  
  /** Get the value of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public String getCostComponentTypeId() throws RemoteException;
  /** Set the value of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT table. */
  public void setCostComponentTypeId(String costComponentTypeId) throws RemoteException;
  
  /** Get the value of the PRODUCT_ID column of the COST_COMPONENT table. */
  public String getProductId() throws RemoteException;
  /** Set the value of the PRODUCT_ID column of the COST_COMPONENT table. */
  public void setProductId(String productId) throws RemoteException;
  
  /** Get the value of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public String getProductFeatureId() throws RemoteException;
  /** Set the value of the PRODUCT_FEATURE_ID column of the COST_COMPONENT table. */
  public void setProductFeatureId(String productFeatureId) throws RemoteException;
  
  /** Get the value of the PARTY_ID column of the COST_COMPONENT table. */
  public String getPartyId() throws RemoteException;
  /** Set the value of the PARTY_ID column of the COST_COMPONENT table. */
  public void setPartyId(String partyId) throws RemoteException;
  
  /** Get the value of the GEO_ID column of the COST_COMPONENT table. */
  public String getGeoId() throws RemoteException;
  /** Set the value of the GEO_ID column of the COST_COMPONENT table. */
  public void setGeoId(String geoId) throws RemoteException;
  
  /** Get the value of the FROM_DATE column of the COST_COMPONENT table. */
  public java.util.Date getFromDate() throws RemoteException;
  /** Set the value of the FROM_DATE column of the COST_COMPONENT table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException;
  
  /** Get the value of the THRU_DATE column of the COST_COMPONENT table. */
  public java.util.Date getThruDate() throws RemoteException;
  /** Set the value of the THRU_DATE column of the COST_COMPONENT table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException;
  
  /** Get the value of the COST column of the COST_COMPONENT table. */
  public Double getCost() throws RemoteException;
  /** Set the value of the COST column of the COST_COMPONENT table. */
  public void setCost(Double cost) throws RemoteException;
  

  /** Get the value object of this CostComponent class. */
  public CostComponent getValueObject() throws RemoteException;
  /** Set the values in the value object of this CostComponent class. */
  public void setValueObject(CostComponent costComponentValue) throws RemoteException;


  /** Get the  CostComponentType entity corresponding to this entity. */
  public CostComponentType getCostComponentType() throws RemoteException;
  /** Remove the  CostComponentType entity corresponding to this entity. */
  public void removeCostComponentType() throws RemoteException;  

  /** Get a collection of  CostComponentTypeAttr related entities. */
  public Collection getCostComponentTypeAttrs() throws RemoteException;
  /** Get the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public CostComponentTypeAttr getCostComponentTypeAttr(String name) throws RemoteException;
  /** Remove  CostComponentTypeAttr related entities. */
  public void removeCostComponentTypeAttrs() throws RemoteException;
  /** Remove the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentTypeAttr(String name) throws RemoteException;

  /** Get a collection of  CostComponentAttribute related entities. */
  public Collection getCostComponentAttributes() throws RemoteException;
  /** Get the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public CostComponentAttribute getCostComponentAttribute(String name) throws RemoteException;
  /** Remove  CostComponentAttribute related entities. */
  public void removeCostComponentAttributes() throws RemoteException;
  /** Remove the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentAttribute(String name) throws RemoteException;

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() throws RemoteException;
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() throws RemoteException;  

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get the  Geo entity corresponding to this entity. */
  public Geo getGeo() throws RemoteException;
  /** Remove the  Geo entity corresponding to this entity. */
  public void removeGeo() throws RemoteException;  

}
