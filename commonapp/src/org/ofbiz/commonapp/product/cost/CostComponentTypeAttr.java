
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Cost Component Type Attribute Entity
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

public interface CostComponentTypeAttr extends EJBObject
{
  /** Get the primary key of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE_ATTR table. */
  public String getCostComponentTypeId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the COST_COMPONENT_TYPE_ATTR table. */
  public String getName() throws RemoteException;
  

  /** Get the value object of this CostComponentTypeAttr class. */
  public CostComponentTypeAttr getValueObject() throws RemoteException;
  /** Set the values in the value object of this CostComponentTypeAttr class. */
  public void setValueObject(CostComponentTypeAttr costComponentTypeAttrValue) throws RemoteException;


  /** Get the  CostComponentType entity corresponding to this entity. */
  public CostComponentType getCostComponentType() throws RemoteException;
  /** Remove the  CostComponentType entity corresponding to this entity. */
  public void removeCostComponentType() throws RemoteException;  

  /** Get a collection of  CostComponentAttribute related entities. */
  public Collection getCostComponentAttributes() throws RemoteException;
  /** Get the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public CostComponentAttribute getCostComponentAttribute(String costComponentId) throws RemoteException;
  /** Remove  CostComponentAttribute related entities. */
  public void removeCostComponentAttributes() throws RemoteException;
  /** Remove the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentAttribute(String costComponentId) throws RemoteException;

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() throws RemoteException;
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) throws RemoteException;
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() throws RemoteException;
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) throws RemoteException;

}
