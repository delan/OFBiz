
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Association Entity
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
 *@created    Fri Jul 27 01:18:25 MDT 2001
 *@version    1.0
 */
public class ProductAssocBean implements EntityBean
{
  /** The variable for the PRODUCT_ID column of the PRODUCT_ASSOC table. */
  public String productId;
  /** The variable for the PRODUCT_ID_TO column of the PRODUCT_ASSOC table. */
  public String productIdTo;
  /** The variable for the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC table. */
  public String productAssocTypeId;
  /** The variable for the FROM_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date fromDate;
  /** The variable for the THRU_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date thruDate;
  /** The variable for the REASON column of the PRODUCT_ASSOC table. */
  public String reason;
  /** The variable for the QUANTITY column of the PRODUCT_ASSOC table. */
  public Double quantity;
  /** The variable for the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public String instruction;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductAssocBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_ID column of the PRODUCT_ASSOC table. */
  public String getProductId() { return productId; }

  /** Get the primary key PRODUCT_ID_TO column of the PRODUCT_ASSOC table. */
  public String getProductIdTo() { return productIdTo; }

  /** Get the primary key PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC table. */
  public String getProductAssocTypeId() { return productAssocTypeId; }

  /** Get the value of the FROM_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date getFromDate() { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_ASSOC table. */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date getThruDate() { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_ASSOC table. */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
    ejbIsModified = true;
  }

  /** Get the value of the REASON column of the PRODUCT_ASSOC table. */
  public String getReason() { return reason; }
  /** Set the value of the REASON column of the PRODUCT_ASSOC table. */
  public void setReason(String reason)
  {
    this.reason = reason;
    ejbIsModified = true;
  }

  /** Get the value of the QUANTITY column of the PRODUCT_ASSOC table. */
  public Double getQuantity() { return quantity; }
  /** Set the value of the QUANTITY column of the PRODUCT_ASSOC table. */
  public void setQuantity(Double quantity)
  {
    this.quantity = quantity;
    ejbIsModified = true;
  }

  /** Get the value of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public String getInstruction() { return instruction; }
  /** Set the value of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public void setInstruction(String instruction)
  {
    this.instruction = instruction;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductAssocBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductAssoc valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
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
      if(valueObject.getReason() != null)
      {
        this.reason = valueObject.getReason();
        ejbIsModified = true;
      }
      if(valueObject.getQuantity() != null)
      {
        this.quantity = valueObject.getQuantity();
        ejbIsModified = true;
      }
      if(valueObject.getInstruction() != null)
      {
        this.instruction = valueObject.getInstruction();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductAssocBean object
   *@return    The ValueObject value
   */
  public ProductAssoc getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductAssocValue((ProductAssoc)this.entityContext.getEJBObject(), productId, productIdTo, productAssocTypeId, fromDate, thruDate, reason, quantity, instruction);
    }
    else { return null; }
  }


  /** Get the  ProductAssocType entity corresponding to this entity. */
  public ProductAssocType getProductAssocType() { return ProductAssocTypeHelper.findByPrimaryKey(productAssocTypeId); }
  /** Remove the  ProductAssocType entity corresponding to this entity. */
  public void removeProductAssocType() { ProductAssocTypeHelper.removeByPrimaryKey(productAssocTypeId); }

  /** Get the Main Product entity corresponding to this entity. */
  public Product getMainProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the Main Product entity corresponding to this entity. */
  public void removeMainProduct() { ProductHelper.removeByPrimaryKey(productId); }

  /** Get the Assoc Product entity corresponding to this entity. */
  public Product getAssocProduct() { return ProductHelper.findByPrimaryKey(productIdTo); }
  /** Remove the Assoc Product entity corresponding to this entity. */
  public void removeAssocProduct() { ProductHelper.removeByPrimaryKey(productIdTo); }


  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reason                  Field of the REASON column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  instruction                  Field of the INSTRUCTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.ProductAssocPK ejbCreate(String productId, String productIdTo, String productAssocTypeId, java.util.Date fromDate, java.util.Date thruDate, String reason, Double quantity, String instruction) throws CreateException
  {
    this.productId = productId;
    this.productIdTo = productIdTo;
    this.productAssocTypeId = productAssocTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.reason = reason;
    this.quantity = quantity;
    this.instruction = instruction;
    return null;
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.product.ProductAssocPK ejbCreate(String productId, String productIdTo, String productAssocTypeId) throws CreateException
  {
    return ejbCreate(productId, productIdTo, productAssocTypeId, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  reason                  Field of the REASON column.
   *@param  quantity                  Field of the QUANTITY column.
   *@param  instruction                  Field of the INSTRUCTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String productIdTo, String productAssocTypeId, java.util.Date fromDate, java.util.Date thruDate, String reason, Double quantity, String instruction) throws CreateException {}

  /** Description of the Method
   *@param  productId                  Field of the PRODUCT_ID column.
   *@param  productIdTo                  Field of the PRODUCT_ID_TO column.
   *@param  productAssocTypeId                  Field of the PRODUCT_ASSOC_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productId, String productIdTo, String productAssocTypeId) throws CreateException
  {
    ejbPostCreate(productId, productIdTo, productAssocTypeId, null, null, null, null, null);
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
