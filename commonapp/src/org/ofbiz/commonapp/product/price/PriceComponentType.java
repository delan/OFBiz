
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Price Component Type Entity
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

public interface PriceComponentType extends EJBObject
{
  /** Get the primary key of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  public String getPriceComponentTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PRICE_COMPONENT_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PRICE_COMPONENT_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRICE_COMPONENT_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRICE_COMPONENT_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this PriceComponentType class. */
  public PriceComponentType getValueObject() throws RemoteException;
  /** Set the values in the value object of this PriceComponentType class. */
  public void setValueObject(PriceComponentType priceComponentTypeValue) throws RemoteException;


  /** Get the Parent PriceComponentType entity corresponding to this entity. */
  public PriceComponentType getParentPriceComponentType() throws RemoteException;
  /** Remove the Parent PriceComponentType entity corresponding to this entity. */
  public void removeParentPriceComponentType() throws RemoteException;  

  /** Get a collection of Child PriceComponentType related entities. */
  public Collection getChildPriceComponentTypes() throws RemoteException;
  /** Get the Child PriceComponentType keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentType getChildPriceComponentType(String priceComponentTypeId) throws RemoteException;
  /** Remove Child PriceComponentType related entities. */
  public void removeChildPriceComponentTypes() throws RemoteException;
  /** Remove the Child PriceComponentType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildPriceComponentType(String priceComponentTypeId) throws RemoteException;

  /** Get a collection of  PriceComponentTypeAttr related entities. */
  public Collection getPriceComponentTypeAttrs() throws RemoteException;
  /** Get the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentTypeAttr getPriceComponentTypeAttr(String name) throws RemoteException;
  /** Remove  PriceComponentTypeAttr related entities. */
  public void removePriceComponentTypeAttrs() throws RemoteException;
  /** Remove the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentTypeAttr(String name) throws RemoteException;

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() throws RemoteException;
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() throws RemoteException;
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) throws RemoteException;

}
