
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Price Component Type Attribute Entity
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
 *@created    Fri Jul 27 01:18:30 MDT 2001
 *@version    1.0
 */

public interface PriceComponentTypeAttr extends EJBObject
{
  /** Get the primary key of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String getPriceComponentTypeId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String getName() throws RemoteException;
  

  /** Get the value object of this PriceComponentTypeAttr class. */
  public PriceComponentTypeAttr getValueObject() throws RemoteException;
  /** Set the values in the value object of this PriceComponentTypeAttr class. */
  public void setValueObject(PriceComponentTypeAttr priceComponentTypeAttrValue) throws RemoteException;


  /** Get the  PriceComponentType entity corresponding to this entity. */
  public PriceComponentType getPriceComponentType() throws RemoteException;
  /** Remove the  PriceComponentType entity corresponding to this entity. */
  public void removePriceComponentType() throws RemoteException;  

  /** Get a collection of  PriceComponentAttribute related entities. */
  public Collection getPriceComponentAttributes() throws RemoteException;
  /** Get the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentAttribute getPriceComponentAttribute(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponentAttribute related entities. */
  public void removePriceComponentAttributes() throws RemoteException;
  /** Remove the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentAttribute(String priceComponentId) throws RemoteException;

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() throws RemoteException;
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() throws RemoteException;
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) throws RemoteException;

}
