
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class CostComponentTypeAttrValue implements CostComponentTypeAttr
{
  /** The variable of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE_ATTR table. */
  private String costComponentTypeId;
  /** The variable of the NAME column of the COST_COMPONENT_TYPE_ATTR table. */
  private String name;

  private CostComponentTypeAttr costComponentTypeAttr;

  public CostComponentTypeAttrValue()
  {
    this.costComponentTypeId = null;
    this.name = null;

    this.costComponentTypeAttr = null;
  }

  public CostComponentTypeAttrValue(CostComponentTypeAttr costComponentTypeAttr) throws RemoteException
  {
    if(costComponentTypeAttr == null) return;
  
    this.costComponentTypeId = costComponentTypeAttr.getCostComponentTypeId();
    this.name = costComponentTypeAttr.getName();

    this.costComponentTypeAttr = costComponentTypeAttr;
  }

  public CostComponentTypeAttrValue(CostComponentTypeAttr costComponentTypeAttr, String costComponentTypeId, String name)
  {
    if(costComponentTypeAttr == null) return;
  
    this.costComponentTypeId = costComponentTypeId;
    this.name = name;

    this.costComponentTypeAttr = costComponentTypeAttr;
  }


  /** Get the primary key of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE_ATTR table. */
  public String getCostComponentTypeId()  throws RemoteException { return costComponentTypeId; }

  /** Get the primary key of the NAME column of the COST_COMPONENT_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the CostComponentTypeAttr class. */
  public CostComponentTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the CostComponentTypeAttr class. */
  public void setValueObject(CostComponentTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(costComponentTypeAttr!=null) costComponentTypeAttr.setValueObject(valueObject);

    if(costComponentTypeId == null) costComponentTypeId = valueObject.getCostComponentTypeId();
    if(name == null) name = valueObject.getName();
  }


  /** Get the  CostComponentType entity corresponding to this entity. */
  public CostComponentType getCostComponentType() { return CostComponentTypeHelper.findByPrimaryKey(costComponentTypeId); }
  /** Remove the  CostComponentType entity corresponding to this entity. */
  public void removeCostComponentType() { CostComponentTypeHelper.removeByPrimaryKey(costComponentTypeId); }

  /** Get a collection of  CostComponentAttribute related entities. */
  public Collection getCostComponentAttributes() { return CostComponentAttributeHelper.findByName(name); }
  /** Get the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public CostComponentAttribute getCostComponentAttribute(String costComponentId) { return CostComponentAttributeHelper.findByPrimaryKey(costComponentId, name); }
  /** Remove  CostComponentAttribute related entities. */
  public void removeCostComponentAttributes() { CostComponentAttributeHelper.removeByName(name); }
  /** Remove the  CostComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentAttribute(String costComponentId) { CostComponentAttributeHelper.removeByPrimaryKey(costComponentId, name); }

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() { return CostComponentHelper.findByCostComponentTypeId(costComponentTypeId); }
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) { return CostComponentHelper.findByPrimaryKey(costComponentId); }
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() { CostComponentHelper.removeByCostComponentTypeId(costComponentTypeId); }
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) { CostComponentHelper.removeByPrimaryKey(costComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(costComponentTypeAttr!=null) return costComponentTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(costComponentTypeAttr!=null) return costComponentTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(costComponentTypeAttr!=null) return costComponentTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(costComponentTypeAttr!=null) return costComponentTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(costComponentTypeAttr!=null) costComponentTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
