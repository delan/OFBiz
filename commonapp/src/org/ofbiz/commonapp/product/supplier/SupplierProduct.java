
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.product.*;
import org.ofbiz.commonapp.party.party.*;

/**
 * <p><b>Title:</b> Supplier Product Entity
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

public interface SupplierProduct extends EJBObject
{
  /** Get the primary key of the PRODUCT_ID column of the SUPPLIER_PRODUCT table. */
  public String getProductId() throws RemoteException;
  
  /** Get the primary key of the PARTY_ID column of the SUPPLIER_PRODUCT table. */
  public String getPartyId() throws RemoteException;
  
  /** Get the value of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getAvailableFromDate() throws RemoteException;
  /** Set the value of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public void setAvailableFromDate(java.util.Date availableFromDate) throws RemoteException;
  
  /** Get the value of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getAvailableThruDate() throws RemoteException;
  /** Set the value of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public void setAvailableThruDate(java.util.Date availableThruDate) throws RemoteException;
  
  /** Get the value of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public String getSupplierPrefOrderId() throws RemoteException;
  /** Set the value of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public void setSupplierPrefOrderId(String supplierPrefOrderId) throws RemoteException;
  
  /** Get the value of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public String getSupplierRatingTypeId() throws RemoteException;
  /** Set the value of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public void setSupplierRatingTypeId(String supplierRatingTypeId) throws RemoteException;
  
  /** Get the value of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getStandardLeadTime() throws RemoteException;
  /** Set the value of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public void setStandardLeadTime(java.util.Date standardLeadTime) throws RemoteException;
  
  /** Get the value of the COMMENT column of the SUPPLIER_PRODUCT table. */
  public String getComment() throws RemoteException;
  /** Set the value of the COMMENT column of the SUPPLIER_PRODUCT table. */
  public void setComment(String comment) throws RemoteException;
  

  /** Get the value object of this SupplierProduct class. */
  public SupplierProduct getValueObject() throws RemoteException;
  /** Set the values in the value object of this SupplierProduct class. */
  public void setValueObject(SupplierProduct supplierProductValue) throws RemoteException;


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get the  SupplierPrefOrder entity corresponding to this entity. */
  public SupplierPrefOrder getSupplierPrefOrder() throws RemoteException;
  /** Remove the  SupplierPrefOrder entity corresponding to this entity. */
  public void removeSupplierPrefOrder() throws RemoteException;  

  /** Get the  SupplierRatingType entity corresponding to this entity. */
  public SupplierRatingType getSupplierRatingType() throws RemoteException;
  /** Remove the  SupplierRatingType entity corresponding to this entity. */
  public void removeSupplierRatingType() throws RemoteException;  

}
