
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class SupplierProductValue implements SupplierProduct
{
  /** The variable of the PRODUCT_ID column of the SUPPLIER_PRODUCT table. */
  private String productId;
  /** The variable of the PARTY_ID column of the SUPPLIER_PRODUCT table. */
  private String partyId;
  /** The variable of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  private java.util.Date availableFromDate;
  /** The variable of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  private java.util.Date availableThruDate;
  /** The variable of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  private String supplierPrefOrderId;
  /** The variable of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  private String supplierRatingTypeId;
  /** The variable of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  private java.util.Date standardLeadTime;
  /** The variable of the COMMENT column of the SUPPLIER_PRODUCT table. */
  private String comment;

  private SupplierProduct supplierProduct;

  public SupplierProductValue()
  {
    this.productId = null;
    this.partyId = null;
    this.availableFromDate = null;
    this.availableThruDate = null;
    this.supplierPrefOrderId = null;
    this.supplierRatingTypeId = null;
    this.standardLeadTime = null;
    this.comment = null;

    this.supplierProduct = null;
  }

  public SupplierProductValue(SupplierProduct supplierProduct) throws RemoteException
  {
    if(supplierProduct == null) return;
  
    this.productId = supplierProduct.getProductId();
    this.partyId = supplierProduct.getPartyId();
    this.availableFromDate = supplierProduct.getAvailableFromDate();
    this.availableThruDate = supplierProduct.getAvailableThruDate();
    this.supplierPrefOrderId = supplierProduct.getSupplierPrefOrderId();
    this.supplierRatingTypeId = supplierProduct.getSupplierRatingTypeId();
    this.standardLeadTime = supplierProduct.getStandardLeadTime();
    this.comment = supplierProduct.getComment();

    this.supplierProduct = supplierProduct;
  }

  public SupplierProductValue(SupplierProduct supplierProduct, String productId, String partyId, java.util.Date availableFromDate, java.util.Date availableThruDate, String supplierPrefOrderId, String supplierRatingTypeId, java.util.Date standardLeadTime, String comment)
  {
    if(supplierProduct == null) return;
  
    this.productId = productId;
    this.partyId = partyId;
    this.availableFromDate = availableFromDate;
    this.availableThruDate = availableThruDate;
    this.supplierPrefOrderId = supplierPrefOrderId;
    this.supplierRatingTypeId = supplierRatingTypeId;
    this.standardLeadTime = standardLeadTime;
    this.comment = comment;

    this.supplierProduct = supplierProduct;
  }


  /** Get the primary key of the PRODUCT_ID column of the SUPPLIER_PRODUCT table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the primary key of the PARTY_ID column of the SUPPLIER_PRODUCT table. */
  public String getPartyId()  throws RemoteException { return partyId; }

  /** Get the value of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getAvailableFromDate() throws RemoteException { return availableFromDate; }
  /** Set the value of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public void setAvailableFromDate(java.util.Date availableFromDate) throws RemoteException
  {
    this.availableFromDate = availableFromDate;
    if(supplierProduct!=null) supplierProduct.setAvailableFromDate(availableFromDate);
  }

  /** Get the value of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getAvailableThruDate() throws RemoteException { return availableThruDate; }
  /** Set the value of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public void setAvailableThruDate(java.util.Date availableThruDate) throws RemoteException
  {
    this.availableThruDate = availableThruDate;
    if(supplierProduct!=null) supplierProduct.setAvailableThruDate(availableThruDate);
  }

  /** Get the value of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public String getSupplierPrefOrderId() throws RemoteException { return supplierPrefOrderId; }
  /** Set the value of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public void setSupplierPrefOrderId(String supplierPrefOrderId) throws RemoteException
  {
    this.supplierPrefOrderId = supplierPrefOrderId;
    if(supplierProduct!=null) supplierProduct.setSupplierPrefOrderId(supplierPrefOrderId);
  }

  /** Get the value of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public String getSupplierRatingTypeId() throws RemoteException { return supplierRatingTypeId; }
  /** Set the value of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public void setSupplierRatingTypeId(String supplierRatingTypeId) throws RemoteException
  {
    this.supplierRatingTypeId = supplierRatingTypeId;
    if(supplierProduct!=null) supplierProduct.setSupplierRatingTypeId(supplierRatingTypeId);
  }

  /** Get the value of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getStandardLeadTime() throws RemoteException { return standardLeadTime; }
  /** Set the value of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public void setStandardLeadTime(java.util.Date standardLeadTime) throws RemoteException
  {
    this.standardLeadTime = standardLeadTime;
    if(supplierProduct!=null) supplierProduct.setStandardLeadTime(standardLeadTime);
  }

  /** Get the value of the COMMENT column of the SUPPLIER_PRODUCT table. */
  public String getComment() throws RemoteException { return comment; }
  /** Set the value of the COMMENT column of the SUPPLIER_PRODUCT table. */
  public void setComment(String comment) throws RemoteException
  {
    this.comment = comment;
    if(supplierProduct!=null) supplierProduct.setComment(comment);
  }

  /** Get the value object of the SupplierProduct class. */
  public SupplierProduct getValueObject() throws RemoteException { return this; }
  /** Set the value object of the SupplierProduct class. */
  public void setValueObject(SupplierProduct valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(supplierProduct!=null) supplierProduct.setValueObject(valueObject);

    if(productId == null) productId = valueObject.getProductId();
    if(partyId == null) partyId = valueObject.getPartyId();
    availableFromDate = valueObject.getAvailableFromDate();
    availableThruDate = valueObject.getAvailableThruDate();
    supplierPrefOrderId = valueObject.getSupplierPrefOrderId();
    supplierRatingTypeId = valueObject.getSupplierRatingTypeId();
    standardLeadTime = valueObject.getStandardLeadTime();
    comment = valueObject.getComment();
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  SupplierPrefOrder entity corresponding to this entity. */
  public SupplierPrefOrder getSupplierPrefOrder() { return SupplierPrefOrderHelper.findByPrimaryKey(supplierPrefOrderId); }
  /** Remove the  SupplierPrefOrder entity corresponding to this entity. */
  public void removeSupplierPrefOrder() { SupplierPrefOrderHelper.removeByPrimaryKey(supplierPrefOrderId); }

  /** Get the  SupplierRatingType entity corresponding to this entity. */
  public SupplierRatingType getSupplierRatingType() { return SupplierRatingTypeHelper.findByPrimaryKey(supplierRatingTypeId); }
  /** Remove the  SupplierRatingType entity corresponding to this entity. */
  public void removeSupplierRatingType() { SupplierRatingTypeHelper.removeByPrimaryKey(supplierRatingTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(supplierProduct!=null) return supplierProduct.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(supplierProduct!=null) return supplierProduct.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(supplierProduct!=null) return supplierProduct.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(supplierProduct!=null) return supplierProduct.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(supplierProduct!=null) supplierProduct.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
