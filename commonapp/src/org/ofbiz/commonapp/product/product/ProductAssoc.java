
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

public interface ProductAssoc extends EJBObject
{
  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_ASSOC table. */
  public String getProductId() throws RemoteException;
  
  /** Get the primary key of the PRODUCT_ID_TO column of the PRODUCT_ASSOC table. */
  public String getProductIdTo() throws RemoteException;
  
  /** Get the primary key of the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC table. */
  public String getProductAssocTypeId() throws RemoteException;
  
  /** Get the value of the FROM_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date getFromDate() throws RemoteException;
  /** Set the value of the FROM_DATE column of the PRODUCT_ASSOC table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException;
  
  /** Get the value of the THRU_DATE column of the PRODUCT_ASSOC table. */
  public java.util.Date getThruDate() throws RemoteException;
  /** Set the value of the THRU_DATE column of the PRODUCT_ASSOC table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException;
  
  /** Get the value of the REASON column of the PRODUCT_ASSOC table. */
  public String getReason() throws RemoteException;
  /** Set the value of the REASON column of the PRODUCT_ASSOC table. */
  public void setReason(String reason) throws RemoteException;
  
  /** Get the value of the QUANTITY column of the PRODUCT_ASSOC table. */
  public Double getQuantity() throws RemoteException;
  /** Set the value of the QUANTITY column of the PRODUCT_ASSOC table. */
  public void setQuantity(Double quantity) throws RemoteException;
  
  /** Get the value of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public String getInstruction() throws RemoteException;
  /** Set the value of the INSTRUCTION column of the PRODUCT_ASSOC table. */
  public void setInstruction(String instruction) throws RemoteException;
  

  /** Get the value object of this ProductAssoc class. */
  public ProductAssoc getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductAssoc class. */
  public void setValueObject(ProductAssoc productAssocValue) throws RemoteException;


  /** Get the  ProductAssocType entity corresponding to this entity. */
  public ProductAssocType getProductAssocType() throws RemoteException;
  /** Remove the  ProductAssocType entity corresponding to this entity. */
  public void removeProductAssocType() throws RemoteException;  

  /** Get the Main Product entity corresponding to this entity. */
  public Product getMainProduct() throws RemoteException;
  /** Remove the Main Product entity corresponding to this entity. */
  public void removeMainProduct() throws RemoteException;  

  /** Get the Assoc Product entity corresponding to this entity. */
  public Product getAssocProduct() throws RemoteException;
  /** Remove the Assoc Product entity corresponding to this entity. */
  public void removeAssocProduct() throws RemoteException;  

}
