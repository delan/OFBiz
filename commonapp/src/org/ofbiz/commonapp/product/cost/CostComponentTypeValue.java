
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Cost Component Type Entity
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
public class CostComponentTypeValue implements CostComponentType
{
  /** The variable of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE table. */
  private String costComponentTypeId;
  /** The variable of the PARENT_TYPE_ID column of the COST_COMPONENT_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the COST_COMPONENT_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the COST_COMPONENT_TYPE table. */
  private String description;

  private CostComponentType costComponentType;

  public CostComponentTypeValue()
  {
    this.costComponentTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.costComponentType = null;
  }

  public CostComponentTypeValue(CostComponentType costComponentType) throws RemoteException
  {
    if(costComponentType == null) return;
  
    this.costComponentTypeId = costComponentType.getCostComponentTypeId();
    this.parentTypeId = costComponentType.getParentTypeId();
    this.hasTable = costComponentType.getHasTable();
    this.description = costComponentType.getDescription();

    this.costComponentType = costComponentType;
  }

  public CostComponentTypeValue(CostComponentType costComponentType, String costComponentTypeId, String parentTypeId, String hasTable, String description)
  {
    if(costComponentType == null) return;
  
    this.costComponentTypeId = costComponentTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.costComponentType = costComponentType;
  }


  /** Get the primary key of the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE table. */
  public String getCostComponentTypeId()  throws RemoteException { return costComponentTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the COST_COMPONENT_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the COST_COMPONENT_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(costComponentType!=null) costComponentType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the COST_COMPONENT_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the COST_COMPONENT_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(costComponentType!=null) costComponentType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the COST_COMPONENT_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the COST_COMPONENT_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(costComponentType!=null) costComponentType.setDescription(description);
  }

  /** Get the value object of the CostComponentType class. */
  public CostComponentType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the CostComponentType class. */
  public void setValueObject(CostComponentType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(costComponentType!=null) costComponentType.setValueObject(valueObject);

    if(costComponentTypeId == null) costComponentTypeId = valueObject.getCostComponentTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent CostComponentType entity corresponding to this entity. */
  public CostComponentType getParentCostComponentType() { return CostComponentTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent CostComponentType entity corresponding to this entity. */
  public void removeParentCostComponentType() { CostComponentTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child CostComponentType related entities. */
  public Collection getChildCostComponentTypes() { return CostComponentTypeHelper.findByParentTypeId(costComponentTypeId); }
  /** Get the Child CostComponentType keyed by member(s) of this class, and other passed parameters. */
  public CostComponentType getChildCostComponentType(String costComponentTypeId) { return CostComponentTypeHelper.findByPrimaryKey(costComponentTypeId); }
  /** Remove Child CostComponentType related entities. */
  public void removeChildCostComponentTypes() { CostComponentTypeHelper.removeByParentTypeId(costComponentTypeId); }
  /** Remove the Child CostComponentType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildCostComponentType(String costComponentTypeId) { CostComponentTypeHelper.removeByPrimaryKey(costComponentTypeId); }

  /** Get a collection of  CostComponentTypeAttr related entities. */
  public Collection getCostComponentTypeAttrs() { return CostComponentTypeAttrHelper.findByCostComponentTypeId(costComponentTypeId); }
  /** Get the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public CostComponentTypeAttr getCostComponentTypeAttr(String name) { return CostComponentTypeAttrHelper.findByPrimaryKey(costComponentTypeId, name); }
  /** Remove  CostComponentTypeAttr related entities. */
  public void removeCostComponentTypeAttrs() { CostComponentTypeAttrHelper.removeByCostComponentTypeId(costComponentTypeId); }
  /** Remove the  CostComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponentTypeAttr(String name) { CostComponentTypeAttrHelper.removeByPrimaryKey(costComponentTypeId, name); }

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() { return CostComponentHelper.findByCostComponentTypeId(costComponentTypeId); }
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) { return CostComponentHelper.findByPrimaryKey(costComponentId); }
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() { CostComponentHelper.removeByCostComponentTypeId(costComponentTypeId); }
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) { CostComponentHelper.removeByPrimaryKey(costComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(costComponentType!=null) return costComponentType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(costComponentType!=null) return costComponentType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(costComponentType!=null) return costComponentType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(costComponentType!=null) return costComponentType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(costComponentType!=null) costComponentType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
