
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Price Component Attribute Entity
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
 *@created    Fri Jul 27 01:18:29 MDT 2001
 *@version    1.0
 */

public interface PriceComponentAttribute extends EJBObject
{
  /** Get the primary key of the PRICE_COMPONENT_ID column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public String getPriceComponentId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public String getName() throws RemoteException;
  
  /** Get the value of the VALUE column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public String getValue() throws RemoteException;
  /** Set the value of the VALUE column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException;
  

  /** Get the value object of this PriceComponentAttribute class. */
  public PriceComponentAttribute getValueObject() throws RemoteException;
  /** Set the values in the value object of this PriceComponentAttribute class. */
  public void setValueObject(PriceComponentAttribute priceComponentAttributeValue) throws RemoteException;


  /** Get the  PriceComponent entity corresponding to this entity. */
  public PriceComponent getPriceComponent() throws RemoteException;
  /** Remove the  PriceComponent entity corresponding to this entity. */
  public void removePriceComponent() throws RemoteException;  

  /** Get a collection of  PriceComponentTypeAttr related entities. */
  public Collection getPriceComponentTypeAttrs() throws RemoteException;
  /** Get the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentTypeAttr getPriceComponentTypeAttr(String priceComponentTypeId) throws RemoteException;
  /** Remove  PriceComponentTypeAttr related entities. */
  public void removePriceComponentTypeAttrs() throws RemoteException;
  /** Remove the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentTypeAttr(String priceComponentTypeId) throws RemoteException;

}
