
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Cost Component Attribute Entity
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
public class CostComponentAttributeValue implements CostComponentAttribute
{
  /** The variable of the COST_COMPONENT_ID column of the COST_COMPONENT_ATTRIBUTE table. */
  private String costComponentId;
  /** The variable of the NAME column of the COST_COMPONENT_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the COST_COMPONENT_ATTRIBUTE table. */
  private String value;

  private CostComponentAttribute costComponentAttribute;

  public CostComponentAttributeValue()
  {
    this.costComponentId = null;
    this.name = null;
    this.value = null;

    this.costComponentAttribute = null;
  }

  public CostComponentAttributeValue(CostComponentAttribute costComponentAttribute) throws RemoteException
  {
    if(costComponentAttribute == null) return;
  
    this.costComponentId = costComponentAttribute.getCostComponentId();
    this.name = costComponentAttribute.getName();
    this.value = costComponentAttribute.getValue();

    this.costComponentAttribute = costComponentAttribute;
  }

  public CostComponentAttributeValue(CostComponentAttribute costComponentAttribute, String costComponentId, String name, String value)
  {
    if(costComponentAttribute == null) return;
  
    this.costComponentId = costComponentId;
    this.name = name;
    this.value = value;

    this.costComponentAttribute = costComponentAttribute;
  }


  /** Get the primary key of the COST_COMPONENT_ID column of the COST_COMPONENT_ATTRIBUTE table. */
  public String getCostComponentId()  throws RemoteException { return costComponentId; }

  /** Get the primary key of the NAME column of the COST_COMPONENT_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the COST_COMPONENT_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the COST_COMPONENT_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(costComponentAttribute!=null) costComponentAttribute.setValue(value);
  }

  /** Get the value object of the CostComponentAttribute class. */
  public CostComponentAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the CostComponentAttribute class. */
  public void setValueObject(CostComponentAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(costComponentAttribute!=null) costComponentAttribute.setValueObject(valueObject);

    if(costComponentId == null) costComponentId = valueObject.getCostComponentId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
  }


  /** Get the  CostComponent entity corresponding to this entity. */
  public CostComponent getCostComponent() { return CostComponentHelper.findByPrimaryKey(costComponentId); }
  /** Remove the  CostComponent entity corresponding to this entity. */
  public void removeCostComponent() { CostComponentHelper.removeByPrimaryKey(costComponentId); }

  /** Get a collection of  CostComponentTypeAttr related entities. */
  public Collection getCostComponentTypeAttrs() { return CostComponentTypeAttrHelper.findByName(name); }
  /** Get the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public CostComponentTypeAttr getCostComponentTypeAttr(String costComponentTypeId) { return CostComponentTypeAttrHelper.findByPrimaryKey(costComponentTypeId, name); }
  /** Remove  CostComponentTypeAttr related entities. */
  public void removeCostComponentTypeAttrs() { CostComponentTypeAttrHelper.removeByName(name); }
  /** Remove the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentTypeAttr(String costComponentTypeId) { CostComponentTypeAttrHelper.removeByPrimaryKey(costComponentTypeId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(costComponentAttribute!=null) return costComponentAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(costComponentAttribute!=null) return costComponentAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(costComponentAttribute!=null) return costComponentAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(costComponentAttribute!=null) return costComponentAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(costComponentAttribute!=null) costComponentAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
