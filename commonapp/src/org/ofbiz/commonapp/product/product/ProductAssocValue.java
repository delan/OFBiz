
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class ProductAssocValue implements ProductAssoc
{
  /** The variable of the PRODUCT_ID column of the PRODUCT_ASSOC table. */
  private String productId;
  /** The variable of the PRODUCT_ID_TO column of the PRODUCT_ASSOC table. */
  private String productIdTo;
  /** The variable of the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC table. */
  private String productAssocTypeId;
  /** The variable of the FROM_DATE column of the PRODUCT_ASSOC table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PRODUCT_ASSOC table. */
  private java.util.Date thruDate;
  /** The variable of the REASON column of the PRODUCT_ASSOC table. */
  private String reason;
  /** The variable of the QUANTITY column of the PRODUCT_ASSOC table. */
  private Double quantity;
  /** The variable of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  private String instruction;

  private ProductAssoc productAssoc;

  public ProductAssocValue()
  {
    this.productId = null;
    this.productIdTo = null;
    this.productAssocTypeId = null;
    this.fromDate = null;
    this.thruDate = null;
    this.reason = null;
    this.quantity = null;
    this.instruction = null;

    this.productAssoc = null;
  }

  public ProductAssocValue(ProductAssoc productAssoc) throws RemoteException
  {
    if(productAssoc == null) return;
  
    this.productId = productAssoc.getProductId();
    this.productIdTo = productAssoc.getProductIdTo();
    this.productAssocTypeId = productAssoc.getProductAssocTypeId();
    this.fromDate = productAssoc.getFromDate();
    this.thruDate = productAssoc.getThruDate();
    this.reason = productAssoc.getReason();
    this.quantity = productAssoc.getQuantity();
    this.instruction = productAssoc.getInstruction();

    this.productAssoc = productAssoc;
  }

  public ProductAssocValue(ProductAssoc productAssoc, String productId, String productIdTo, String productAssocTypeId, java.util.Date fromDate, java.util.Date thruDate, String reason, Double quantity, String instruction)
  {
    if(productAssoc == null) return;
  
    this.productId = productId;
    this.productIdTo = productIdTo;
    this.productAssocTypeId = productAssocTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.reason = reason;
    this.quantity = quantity;
    this.instruction = instruction;

    this.productAssoc = productAssoc;
  }


  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_ASSOC table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the primary key of the PRODUCT_ID_TO column of the PRODUCT_ASSOC table. */
  public String getProductIdTo()  throws RemoteException { return productIdTo; }

  /** Get the primary key of the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC table. */
  public String getProductAssocTypeId()  throws RemoteException { return productAssocTypeId; }

  /** Get the value of the FROM_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PRODUCT_ASSOC table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(productAssoc!=null) productAssoc.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PRODUCT_ASSOC table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(productAssoc!=null) productAssoc.setThruDate(thruDate);
  }

  /** Get the value of the REASON column of the PRODUCT_ASSOC table. */
  public String getReason() throws RemoteException { return reason; }
  /** Set the value of the REASON column of the PRODUCT_ASSOC table. */
  public void setReason(String reason) throws RemoteException
  {
    this.reason = reason;
    if(productAssoc!=null) productAssoc.setReason(reason);
  }

  /** Get the value of the QUANTITY column of the PRODUCT_ASSOC table. */
  public Double getQuantity() throws RemoteException { return quantity; }
  /** Set the value of the QUANTITY column of the PRODUCT_ASSOC table. */
  public void setQuantity(Double quantity) throws RemoteException
  {
    this.quantity = quantity;
    if(productAssoc!=null) productAssoc.setQuantity(quantity);
  }

  /** Get the value of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public String getInstruction() throws RemoteException { return instruction; }
  /** Set the value of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public void setInstruction(String instruction) throws RemoteException
  {
    this.instruction = instruction;
    if(productAssoc!=null) productAssoc.setInstruction(instruction);
  }

  /** Get the value object of the ProductAssoc class. */
  public ProductAssoc getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductAssoc class. */
  public void setValueObject(ProductAssoc valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productAssoc!=null) productAssoc.setValueObject(valueObject);

    if(productId == null) productId = valueObject.getProductId();
    if(productIdTo == null) productIdTo = valueObject.getProductIdTo();
    if(productAssocTypeId == null) productAssocTypeId = valueObject.getProductAssocTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
    reason = valueObject.getReason();
    quantity = valueObject.getQuantity();
    instruction = valueObject.getInstruction();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productAssoc!=null) return productAssoc.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productAssoc!=null) return productAssoc.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productAssoc!=null) return productAssoc.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productAssoc!=null) return productAssoc.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productAssoc!=null) productAssoc.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
