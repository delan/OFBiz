
package org.ofbiz.commonapp.product.cost;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


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
public class CostComponentAttributeBean implements EntityBean
{
  /** The variable for the COST_COMPONENT_ID column of the COST_COMPONENT_ATTRIBUTE table. */
  public String costComponentId;
  /** The variable for the NAME column of the COST_COMPONENT_ATTRIBUTE table. */
  public String name;
  /** The variable for the VALUE column of the COST_COMPONENT_ATTRIBUTE table. */
  public String value;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the CostComponentAttributeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key COST_COMPONENT_ID column of the COST_COMPONENT_ATTRIBUTE table. */
  public String getCostComponentId() { return costComponentId; }

  /** Get the primary key NAME column of the COST_COMPONENT_ATTRIBUTE table. */
  public String getName() { return name; }

  /** Get the value of the VALUE column of the COST_COMPONENT_ATTRIBUTE table. */
  public String getValue() { return value; }
  /** Set the value of the VALUE column of the COST_COMPONENT_ATTRIBUTE table. */
  public void setValue(String value)
  {
    this.value = value;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the CostComponentAttributeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(CostComponentAttribute valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getValue() != null)
      {
        this.value = valueObject.getValue();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the CostComponentAttributeBean object
   *@return    The ValueObject value
   */
  public CostComponentAttribute getValueObject()
  {
    if(this.entityContext != null)
    {
      return new CostComponentAttributeValue((CostComponentAttribute)this.entityContext.getEJBObject(), costComponentId, name, value);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.cost.CostComponentAttributePK ejbCreate(String costComponentId, String name, String value) throws CreateException
  {
    this.costComponentId = costComponentId;
    this.name = name;
    this.value = value;
    return null;
  }

  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.cost.CostComponentAttributePK ejbCreate(String costComponentId, String name) throws CreateException
  {
    return ejbCreate(costComponentId, name, null);
  }

  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String costComponentId, String name, String value) throws CreateException {}

  /** Description of the Method
   *@param  costComponentId                  Field of the COST_COMPONENT_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String costComponentId, String name) throws CreateException
  {
    ejbPostCreate(costComponentId, name, null);
  }

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
