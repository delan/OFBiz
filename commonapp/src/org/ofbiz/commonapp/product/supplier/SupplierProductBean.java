
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
public class SupplierProductBean implements EntityBean
{
  /** The variable for the PRODUCT_ID column of the SUPPLIER_PRODUCT table. */
  public String productId;
  /** The variable for the PARTY_ID column of the SUPPLIER_PRODUCT table. */
  public String partyId;
  /** The variable for the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date availableFromDate;
  /** The variable for the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date availableThruDate;
  /** The variable for the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public String supplierPrefOrderId;
  /** The variable for the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public String supplierRatingTypeId;
  /** The variable for the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public java.util.Date standardLeadTime;
  /** The variable for the COMMENT column of the SUPPLIER_PRODUCT table. */
  public String comment;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the SupplierProductBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_ID column of the SUPPLIER_PRODUCT table. */
  public String getProductId() { return productId; }

  /** Get the primary key PARTY_ID column of the SUPPLIER_PRODUCT table. */
  public String getPartyId() { return partyId; }

  /** Get the value of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getAvailableFromDate() { return availableFromDate; }
  /** Set the value of the AVAILABLE_FROM_DATE column of the SUPPLIER_PRODUCT table. */
  public void setAvailableFromDate(java.util.Date availableFromDate)
  {
    this.availableFromDate = availableFromDate;
    ejbIsModified = true;
  }

  /** Get the value of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getAvailableThruDate() { return availableThruDate; }
  /** Set the value of the AVAILABLE_THRU_DATE column of the SUPPLIER_PRODUCT table. */
  public void setAvailableThruDate(java.util.Date availableThruDate)
  {
    this.availableThruDate = availableThruDate;
    ejbIsModified = true;
  }

  /** Get the value of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public String getSupplierPrefOrderId() { return supplierPrefOrderId; }
  /** Set the value of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PRODUCT table. */
  public void setSupplierPrefOrderId(String supplierPrefOrderId)
  {
    this.supplierPrefOrderId = supplierPrefOrderId;
    ejbIsModified = true;
  }

  /** Get the value of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public String getSupplierRatingTypeId() { return supplierRatingTypeId; }
  /** Set the value of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_PRODUCT table. */
  public void setSupplierRatingTypeId(String supplierRatingTypeId)
  {
    this.supplierRatingTypeId = supplierRatingTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public java.util.Date getStandardLeadTime() { return standardLeadTime; }
  /** Set the value of the STANDARD_LEAD_TIME column of the SUPPLIER_PRODUCT table. */
  public void setStandardLeadTime(java.util.Date standardLeadTime)
  {
    this.standardLeadTime = standardLeadTime;
    ejbIsModified = true;
  }

  /** Get the value of the COMMENT column of the SUPPLIER_PRODUCT table. */
  public String getComment() { return comment; }
  /** Set the value of the COMMENT column of the SUPPLIER_PRODUCT table. */
  public void setComment(String comment)
  {
    this.comment = comment;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the SupplierProductBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(SupplierProduct valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getAvailableFromDate() != null)
      {
        this.availableFromDate = valueObject.getAvailableFromDate();
        ejbIsModified = true;
      }
      if(valueObject.getAvailableThruDate() != null)
      {
        this.availableThruDate = valueObject.getAvailableThruDate();
        ejbIsModified = true;
      }
      if(valueObject.getSupplierPrefOrderId() != null)
      {
        this.supplierPrefOrderId = valueObject.getSupplierPrefOrderId();
        ejbIsModified = true;
      }
      if(valueObject.getSupplierRatingTypeId() != null)
      {
        this.supplierRatingTypeId = valueObject.getSupplierRatingTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getStandardLeadTime() != null)
      {
        this.standardLeadTime = valueObject.getStandardLeadTime();
        ejbIsModified = true;
      }
      if(valueObject.getComment() != null)
      {
        this.comment = valueObject.getComment();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the SupplierProductBean object
   *@return    The ValueObject value
   */
  public SupplierProduct getValueObject()
  {
    if(this.entityContext != null)
    {
      return new SupplierProductValue((SupplierProduct)this.entityContext.getEJBObject(), productId, partyId, availableFromDate, availableThruDate, supplierPrefOrderId, supplierRatingTypeId, standardLeadTime, comment);
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

  /** Get the  SupplierPrefOrder entity corresponding to this entity. */
  public SupplierPrefOrder getSupplierPrefOrder() { return SupplierPrefOrderHelper.findByPrimaryKey(supplierPrefOrderId); }
  /** Remove the  SupplierPrefOrder entity corresponding to this entity. */
  public void removeSupplierPrefOrder() { SupplierPrefOrderHelper.removeByPrimaryKey(supplierPrefOrderId); }

  /** Get the  SupplierRatingType entity corresponding to this entity. */
  public SupplierRatingType getSupplierRatingType() { return SupplierRatingTypeHelper.findByPrimaryKey(supplierRatingTypeId); }
  /** Remove the  SupplierRatingType entity corresponding to this entity. */
  public void removeSupplierRatingType() { SupplierRatingTypeHelper.removeByPrimaryKey(supplierRatingTypeId); }


  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  availableFromDate                  Field of the AVAILABLE_FROM_DATE column.
   *@param  availableThruDate                  Field of the AVAILABLE_THRU_DATE column.
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@param  standardLeadTime                  Field of the STANDARD_LEAD_TIME column.
   *@param  comment                  Field of the COMMENT column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.supplier.SupplierProductPK ejbCreate(String productId, String partyId, java.util.Date availableFromDate, java.util.Date availableThruDate, String supplierPrefOrderId, String supplierRatingTypeId, java.util.Date standardLeadTime, String comment) throws CreateException
  {
    this.productId = productId;
    this.partyId = partyId;
    this.availableFromDate = availableFromDate;
    this.availableThruDate = availableThruDate;
    this.supplierPrefOrderId = supplierPrefOrderId;
    this.supplierRatingTypeId = supplierRatingTypeId;
    this.standardLeadTime = standardLeadTime;
    this.comment = comment;
    return null;
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.supplier.SupplierProductPK ejbCreate(String productId, String partyId) throws CreateException
  {
    return ejbCreate(productId, partyId, null, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  availableFromDate                  Field of the AVAILABLE_FROM_DATE column.
   *@param  availableThruDate                  Field of the AVAILABLE_THRU_DATE column.
   *@param  supplierPrefOrderId                  Field of the SUPPLIER_PREF_ORDER_ID column.
   *@param  supplierRatingTypeId                  Field of the SUPPLIER_RATING_TYPE_ID column.
   *@param  standardLeadTime                  Field of the STANDARD_LEAD_TIME column.
   *@param  comment                  Field of the COMMENT column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String partyId, java.util.Date availableFromDate, java.util.Date availableThruDate, String supplierPrefOrderId, String supplierRatingTypeId, java.util.Date standardLeadTime, String comment) throws CreateException {}

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String partyId) throws CreateException
  {
    ejbPostCreate(productId, partyId, null, null, null, null, null, null);
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
