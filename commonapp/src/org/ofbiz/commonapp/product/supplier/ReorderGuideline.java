
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

public interface ReorderGuideline extends EJBObject
{
  /** Get the primary key of the REORDER_GUIDELINE_ID column of the REORDER_GUIDELINE table. */
  public String getReorderGuidelineId() throws RemoteException;
  
  /** Get the value of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public String getProductId() throws RemoteException;
  /** Set the value of the PRODUCT_ID column of the REORDER_GUIDELINE table. */
  public void setProductId(String productId) throws RemoteException;
  
  /** Get the value of the PARTY_ID column of the REORDER_GUIDELINE table. */
  public String getPartyId() throws RemoteException;
  /** Set the value of the PARTY_ID column of the REORDER_GUIDELINE table. */
  public void setPartyId(String partyId) throws RemoteException;
  
  /** Get the value of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public String getRoleTypeId() throws RemoteException;
  /** Set the value of the ROLE_TYPE_ID column of the REORDER_GUIDELINE table. */
  public void setRoleTypeId(String roleTypeId) throws RemoteException;
  
  /** Get the value of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public String getFacilityId() throws RemoteException;
  /** Set the value of the FACILITY_ID column of the REORDER_GUIDELINE table. */
  public void setFacilityId(String facilityId) throws RemoteException;
  
  /** Get the value of the GEO_ID column of the REORDER_GUIDELINE table. */
  public String getGeoId() throws RemoteException;
  /** Set the value of the GEO_ID column of the REORDER_GUIDELINE table. */
  public void setGeoId(String geoId) throws RemoteException;
  
  /** Get the value of the FROM_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date getFromDate() throws RemoteException;
  /** Set the value of the FROM_DATE column of the REORDER_GUIDELINE table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException;
  
  /** Get the value of the THRU_DATE column of the REORDER_GUIDELINE table. */
  public java.util.Date getThruDate() throws RemoteException;
  /** Set the value of the THRU_DATE column of the REORDER_GUIDELINE table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException;
  
  /** Get the value of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public Double getReorderQuantity() throws RemoteException;
  /** Set the value of the REORDER_QUANTITY column of the REORDER_GUIDELINE table. */
  public void setReorderQuantity(Double reorderQuantity) throws RemoteException;
  
  /** Get the value of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public Double getReorderLevel() throws RemoteException;
  /** Set the value of the REORDER_LEVEL column of the REORDER_GUIDELINE table. */
  public void setReorderLevel(Double reorderLevel) throws RemoteException;
  

  /** Get the value object of this ReorderGuideline class. */
  public ReorderGuideline getValueObject() throws RemoteException;
  /** Set the values in the value object of this ReorderGuideline class. */
  public void setValueObject(ReorderGuideline reorderGuidelineValue) throws RemoteException;


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() throws RemoteException;
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() throws RemoteException;  

  /** Get the  Geo entity corresponding to this entity. */
  public Geo getGeo() throws RemoteException;
  /** Remove the  Geo entity corresponding to this entity. */
  public void removeGeo() throws RemoteException;  

}
