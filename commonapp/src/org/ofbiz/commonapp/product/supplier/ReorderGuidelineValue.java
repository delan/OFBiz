
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.party.party.*;
import org.ofbiz.commonapp.product.storage.*;
import org.ofbiz.commonapp.common.geo.*;

/**
 * <p><b>Title:</b> Reorder Guideline Entity
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */
public class ReorderGuidelineValue implements ReorderGuideline
{
  /** The variable of the REORDER_GUIDELINE_ID column of the REORDER_GUIDELINE table. */
  private String reorderGuidelineId;
  /** The variable of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  private String productId;
  /** The variable of the PARTY_ID column of the REORDER_GUIDELINE table. */
  private String partyId;
  /** The variable of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  private String roleTypeId;
  /** The variable of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  private String facilityId;
  /** The variable of the GEO_ID column of the REORDER_GUIDELINE table. */
  private String geoId;
  /** The variable of the FROM_DATE column of the REORDER_GUIDELINE table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the REORDER_GUIDELINE table. */
  private java.util.Date thruDate;
  /** The variable of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  private Double reorderQuantity;
  /** The variable of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  private Double reorderLevel;

  private ReorderGuideline reorderGuideline;

  public ReorderGuidelineValue()
  {
    this.reorderGuidelineId = null;
    this.productId = null;
    this.partyId = null;
    this.roleTypeId = null;
    this.facilityId = null;
    this.geoId = null;
    this.fromDate = null;
    this.thruDate = null;
    this.reorderQuantity = null;
    this.reorderLevel = null;

    this.reorderGuideline = null;
  }

  public ReorderGuidelineValue(ReorderGuideline reorderGuideline) throws RemoteException
  {
    if(reorderGuideline == null) return;
  
    this.reorderGuidelineId = reorderGuideline.getReorderGuidelineId();
    this.productId = reorderGuideline.getProductId();
    this.partyId = reorderGuideline.getPartyId();
    this.roleTypeId = reorderGuideline.getRoleTypeId();
    this.facilityId = reorderGuideline.getFacilityId();
    this.geoId = reorderGuideline.getGeoId();
    this.fromDate = reorderGuideline.getFromDate();
    this.thruDate = reorderGuideline.getThruDate();
    this.reorderQuantity = reorderGuideline.getReorderQuantity();
    this.reorderLevel = reorderGuideline.getReorderLevel();

    this.reorderGuideline = reorderGuideline;
  }

  public ReorderGuidelineValue(ReorderGuideline reorderGuideline, String reorderGuidelineId, String productId, String partyId, String roleTypeId, String facilityId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double reorderQuantity, Double reorderLevel)
  {
    if(reorderGuideline == null) return;
  
    this.reorderGuidelineId = reorderGuidelineId;
    this.productId = productId;
    this.partyId = partyId;
    this.roleTypeId = roleTypeId;
    this.facilityId = facilityId;
    this.geoId = geoId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.reorderQuantity = reorderQuantity;
    this.reorderLevel = reorderLevel;

    this.reorderGuideline = reorderGuideline;
  }


  /** Get the primary key of the REORDER_GUIDELINE_ID column of the REORDER_GUIDELINE table. */
  public String getReorderGuidelineId()  throws RemoteException { return reorderGuidelineId; }

  /** Get the value of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public String getProductId() throws RemoteException { return productId; }
  /** Set the value of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public void setProductId(String productId) throws RemoteException
  {
    this.productId = productId;
    if(reorderGuideline!=null) reorderGuideline.setProductId(productId);
  }

  /** Get the value of the PARTY_ID column of the REORDER_GUIDELINE table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the REORDER_GUIDELINE table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(reorderGuideline!=null) reorderGuideline.setPartyId(partyId);
  }

  /** Get the value of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public String getRoleTypeId() throws RemoteException { return roleTypeId; }
  /** Set the value of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public void setRoleTypeId(String roleTypeId) throws RemoteException
  {
    this.roleTypeId = roleTypeId;
    if(reorderGuideline!=null) reorderGuideline.setRoleTypeId(roleTypeId);
  }

  /** Get the value of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public String getFacilityId() throws RemoteException { return facilityId; }
  /** Set the value of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public void setFacilityId(String facilityId) throws RemoteException
  {
    this.facilityId = facilityId;
    if(reorderGuideline!=null) reorderGuideline.setFacilityId(facilityId);
  }

  /** Get the value of the GEO_ID column of the REORDER_GUIDELINE table. */
  public String getGeoId() throws RemoteException { return geoId; }
  /** Set the value of the GEO_ID column of the REORDER_GUIDELINE table. */
  public void setGeoId(String geoId) throws RemoteException
  {
    this.geoId = geoId;
    if(reorderGuideline!=null) reorderGuideline.setGeoId(geoId);
  }

  /** Get the value of the FROM_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the REORDER_GUIDELINE table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(reorderGuideline!=null) reorderGuideline.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the REORDER_GUIDELINE table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(reorderGuideline!=null) reorderGuideline.setThruDate(thruDate);
  }

  /** Get the value of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public Double getReorderQuantity() throws RemoteException { return reorderQuantity; }
  /** Set the value of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public void setReorderQuantity(Double reorderQuantity) throws RemoteException
  {
    this.reorderQuantity = reorderQuantity;
    if(reorderGuideline!=null) reorderGuideline.setReorderQuantity(reorderQuantity);
  }

  /** Get the value of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public Double getReorderLevel() throws RemoteException { return reorderLevel; }
  /** Set the value of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public void setReorderLevel(Double reorderLevel) throws RemoteException
  {
    this.reorderLevel = reorderLevel;
    if(reorderGuideline!=null) reorderGuideline.setReorderLevel(reorderLevel);
  }

  /** Get the value object of the ReorderGuideline class. */
  public ReorderGuideline getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ReorderGuideline class. */
  public void setValueObject(ReorderGuideline valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(reorderGuideline!=null) reorderGuideline.setValueObject(valueObject);

    if(reorderGuidelineId == null) reorderGuidelineId = valueObject.getReorderGuidelineId();
    productId = valueObject.getProductId();
    partyId = valueObject.getPartyId();
    roleTypeId = valueObject.getRoleTypeId();
    facilityId = valueObject.getFacilityId();
    geoId = valueObject.getGeoId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
    reorderQuantity = valueObject.getReorderQuantity();
    reorderLevel = valueObject.getReorderLevel();
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }

  /** Get the  Geo entity corresponding to this entity. */
  public Geo getGeo() { return GeoHelper.findByPrimaryKey(geoId); }
  /** Remove the  Geo entity corresponding to this entity. */
  public void removeGeo() { GeoHelper.removeByPrimaryKey(geoId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(reorderGuideline!=null) return reorderGuideline.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(reorderGuideline!=null) return reorderGuideline.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(reorderGuideline!=null) return reorderGuideline.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(reorderGuideline!=null) return reorderGuideline.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(reorderGuideline!=null) reorderGuideline.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
