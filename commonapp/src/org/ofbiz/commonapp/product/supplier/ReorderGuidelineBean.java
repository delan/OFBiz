
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

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
public class ReorderGuidelineBean implements EntityBean
{
  /** The variable for the REORDER_GUIDELINE_ID column of the REORDER_GUIDELINE table. */
  public String reorderGuidelineId;
  /** The variable for the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public String productId;
  /** The variable for the PARTY_ID column of the REORDER_GUIDELINE table. */
  public String partyId;
  /** The variable for the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public String roleTypeId;
  /** The variable for the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public String facilityId;
  /** The variable for the GEO_ID column of the REORDER_GUIDELINE table. */
  public String geoId;
  /** The variable for the FROM_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date fromDate;
  /** The variable for the THRU_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date thruDate;
  /** The variable for the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public Double reorderQuantity;
  /** The variable for the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public Double reorderLevel;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ReorderGuidelineBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key REORDER_GUIDELINE_ID column of the REORDER_GUIDELINE table. */
  public String getReorderGuidelineId() { return reorderGuidelineId; }

  /** Get the value of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public String getProductId() { return productId; }
  /** Set the value of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public void setProductId(String productId)
  {
    this.productId = productId;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_ID column of the REORDER_GUIDELINE table. */
  public String getPartyId() { return partyId; }
  /** Set the value of the PARTY_ID column of the REORDER_GUIDELINE table. */
  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
    ejbIsModified = true;
  }

  /** Get the value of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public String getRoleTypeId() { return roleTypeId; }
  /** Set the value of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public void setRoleTypeId(String roleTypeId)
  {
    this.roleTypeId = roleTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public String getFacilityId() { return facilityId; }
  /** Set the value of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public void setFacilityId(String facilityId)
  {
    this.facilityId = facilityId;
    ejbIsModified = true;
  }

  /** Get the value of the GEO_ID column of the REORDER_GUIDELINE table. */
  public String getGeoId() { return geoId; }
  /** Set the value of the GEO_ID column of the REORDER_GUIDELINE table. */
  public void setGeoId(String geoId)
  {
    this.geoId = geoId;
    ejbIsModified = true;
  }

  /** Get the value of the FROM_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date getFromDate() { return fromDate; }
  /** Set the value of the FROM_DATE column of the REORDER_GUIDELINE table. */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date getThruDate() { return thruDate; }
  /** Set the value of the THRU_DATE column of the REORDER_GUIDELINE table. */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
    ejbIsModified = true;
  }

  /** Get the value of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public Double getReorderQuantity() { return reorderQuantity; }
  /** Set the value of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public void setReorderQuantity(Double reorderQuantity)
  {
    this.reorderQuantity = reorderQuantity;
    ejbIsModified = true;
  }

  /** Get the value of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public Double getReorderLevel() { return reorderLevel; }
  /** Set the value of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public void setReorderLevel(Double reorderLevel)
  {
    this.reorderLevel = reorderLevel;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ReorderGuidelineBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ReorderGuideline valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getProductId() != null)
      {
        this.productId = valueObject.getProductId();
        ejbIsModified = true;
      }
      if(valueObject.getPartyId() != null)
      {
        this.partyId = valueObject.getPartyId();
        ejbIsModified = true;
      }
      if(valueObject.getRoleTypeId() != null)
      {
        this.roleTypeId = valueObject.getRoleTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getFacilityId() != null)
      {
        this.facilityId = valueObject.getFacilityId();
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
      if(valueObject.getReorderQuantity() != null)
      {
        this.reorderQuantity = valueObject.getReorderQuantity();
        ejbIsModified = true;
      }
      if(valueObject.getReorderLevel() != null)
      {
        this.reorderLevel = valueObject.getReorderLevel();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ReorderGuidelineBean object
   *@return    The ValueObject value
   */
  public ReorderGuideline getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ReorderGuidelineValue((ReorderGuideline)this.entityContext.getEJBObject(), reorderGuidelineId, productId, partyId, roleTypeId, facilityId, geoId, fromDate, thruDate, reorderQuantity, reorderLevel);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  reorderGuidelineId                  Field of the REORDER_GUIDELINE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reorderQuantity                  Field of the REORDER_QUANTITY column.
   *@param  reorderLevel                  Field of the REORDER_LEVEL column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String reorderGuidelineId, String productId, String partyId, String roleTypeId, String facilityId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double reorderQuantity, Double reorderLevel) throws CreateException
  {
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
    return null;
  }

  /** Description of the Method
   *@param  reorderGuidelineId                  Field of the REORDER_GUIDELINE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String reorderGuidelineId) throws CreateException
  {
    return ejbCreate(reorderGuidelineId, null, null, null, null, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  reorderGuidelineId                  Field of the REORDER_GUIDELINE_ID column.
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  facilityId                  Field of the FACILITY_ID column.
   *@param  geoId                  Field of the GEO_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reorderQuantity                  Field of the REORDER_QUANTITY column.
   *@param  reorderLevel                  Field of the REORDER_LEVEL column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String reorderGuidelineId, String productId, String partyId, String roleTypeId, String facilityId, String geoId, java.util.Date fromDate, java.util.Date thruDate, Double reorderQuantity, Double reorderLevel) throws CreateException {}

  /** Description of the Method
   *@param  reorderGuidelineId                  Field of the REORDER_GUIDELINE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String reorderGuidelineId) throws CreateException
  {
    ejbPostCreate(reorderGuidelineId, null, null, null, null, null, null, null, null, null);
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
