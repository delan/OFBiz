
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
public class CostComponentTypeAttrBean implements EntityBean
{
  /** The variable for the COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE_ATTR table. */
  public String costComponentTypeId;
  /** The variable for the NAME column of the COST_COMPONENT_TYPE_ATTR table. */
  public String name;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the CostComponentTypeAttrBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key COST_COMPONENT_TYPE_ID column of the COST_COMPONENT_TYPE_ATTR table. */
  public String getCostComponentTypeId() { return costComponentTypeId; }

  /** Get the primary key NAME column of the COST_COMPONENT_TYPE_ATTR table. */
  public String getName() { return name; }

  /** Sets the values from ValueObject attribute of the CostComponentTypeAttrBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(CostComponentTypeAttr valueObject)
  {
  }

  /** Gets the ValueObject attribute of the CostComponentTypeAttrBean object
   *@return    The ValueObject value
   */
  public CostComponentTypeAttr getValueObject()
  {
    if(this.entityContext != null)
    {
      return new CostComponentTypeAttrValue((CostComponentTypeAttr)this.entityContext.getEJBObject(), costComponentTypeId, name);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.cost.CostComponentTypeAttrPK ejbCreate(String costComponentTypeId, String name) throws CreateException
  {
    this.costComponentTypeId = costComponentTypeId;
    this.name = name;
    return null;
  }

  /** Description of the Method
   *@param  costComponentTypeId                  Field of the COST_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String costComponentTypeId, String name) throws CreateException {}

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
