
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Good Identification Entity
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

public interface GoodIdentification extends EJBObject
{
  /** Get the primary key of the GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION table. */
  public String getGoodIdentificationTypeId() throws RemoteException;
  
  /** Get the primary key of the PRODUCT_ID column of the GOOD_IDENTIFICATION table. */
  public String getProductId() throws RemoteException;
  
  /** Get the value of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public String getIdValue() throws RemoteException;
  /** Set the value of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public void setIdValue(String idValue) throws RemoteException;
  

  /** Get the value object of this GoodIdentification class. */
  public GoodIdentification getValueObject() throws RemoteException;
  /** Set the values in the value object of this GoodIdentification class. */
  public void setValueObject(GoodIdentification goodIdentificationValue) throws RemoteException;


  /** Get the  GoodIdentificationType entity corresponding to this entity. */
  public GoodIdentificationType getGoodIdentificationType() throws RemoteException;
  /** Remove the  GoodIdentificationType entity corresponding to this entity. */
  public void removeGoodIdentificationType() throws RemoteException;  

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() throws RemoteException;
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() throws RemoteException;  

}
