
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Variance Reason Entity
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
 *@created    Fri Jul 27 01:18:31 MDT 2001
 *@version    1.0
 */
public class VarianceReasonBean implements EntityBean
{
  /** The variable for the VARIANCE_REASON_ID column of the VARIANCE_REASON table. */
  public String varianceReasonId;
  /** The variable for the DESCRIPTION column of the VARIANCE_REASON table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the VarianceReasonBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key VARIANCE_REASON_ID column of the VARIANCE_REASON table. */
  public String getVarianceReasonId() { return varianceReasonId; }

  /** Get the value of the DESCRIPTION column of the VARIANCE_REASON table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the VARIANCE_REASON table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the VarianceReasonBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(VarianceReason valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the VarianceReasonBean object
   *@return    The ValueObject value
   */
  public VarianceReason getValueObject()
  {
    if(this.entityContext != null)
    {
      return new VarianceReasonValue((VarianceReason)this.entityContext.getEJBObject(), varianceReasonId, description);
    }
    else { return null; }
  }


  /** Get a collection of  InventoryItemVariance related entities. */
  public Collection getInventoryItemVariances() { return InventoryItemVarianceHelper.findByVarianceReasonId(varianceReasonId); }
  /** Get the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemVariance getInventoryItemVariance(String inventoryItemId, String physicalInventoryId) { return InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId); }
  /** Remove  InventoryItemVariance related entities. */
  public void removeInventoryItemVariances() { InventoryItemVarianceHelper.removeByVarianceReasonId(varianceReasonId); }
  /** Remove the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemVariance(String inventoryItemId, String physicalInventoryId) { InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId); }


  /** Description of the Method
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String varianceReasonId, String description) throws CreateException
  {
    this.varianceReasonId = varianceReasonId;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String varianceReasonId) throws CreateException
  {
    return ejbCreate(varianceReasonId, null);
  }

  /** Description of the Method
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String varianceReasonId, String description) throws CreateException {}

  /** Description of the Method
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String varianceReasonId) throws CreateException
  {
    ejbPostCreate(varianceReasonId, null);
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
