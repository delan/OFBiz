
package org.ofbiz.commonapp.common.status;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Status Entity
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
 *@created    Fri Jul 27 01:18:22 MDT 2001
 *@version    1.0
 */
public class StatusBean implements EntityBean
{
  /** The variable for the STATUS_ID column of the STATUS table. */
  public String statusId;
  /** The variable for the STATUS_TYPE_ID column of the STATUS table. */
  public String statusTypeId;
  /** The variable for the DESCRIPTION column of the STATUS table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the StatusBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key STATUS_ID column of the STATUS table. */
  public String getStatusId() { return statusId; }

  /** Get the value of the STATUS_TYPE_ID column of the STATUS table. */
  public String getStatusTypeId() { return statusTypeId; }
  /** Set the value of the STATUS_TYPE_ID column of the STATUS table. */
  public void setStatusTypeId(String statusTypeId)
  {
    this.statusTypeId = statusTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the STATUS table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the STATUS table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the StatusBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Status valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getStatusTypeId() != null)
      {
        this.statusTypeId = valueObject.getStatusTypeId();
        ejbIsModified = true;
      }
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

  /** Gets the ValueObject attribute of the StatusBean object
   *@return    The ValueObject value
   */
  public Status getValueObject()
  {
    if(this.entityContext != null)
    {
      return new StatusValue((Status)this.entityContext.getEJBObject(), statusId, statusTypeId, description);
    }
    else { return null; }
  }


  /** Get the  StatusType entity corresponding to this entity. */
  public StatusType getStatusType() { return StatusTypeHelper.findByPrimaryKey(statusTypeId); }
  /** Remove the  StatusType entity corresponding to this entity. */
  public void removeStatusType() { StatusTypeHelper.removeByPrimaryKey(statusTypeId); }


  /** Description of the Method
   *@param  statusId                  Field of the STATUS_ID column.
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String statusId, String statusTypeId, String description) throws CreateException
  {
    this.statusId = statusId;
    this.statusTypeId = statusTypeId;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  statusId                  Field of the STATUS_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String statusId) throws CreateException
  {
    return ejbCreate(statusId, null, null);
  }

  /** Description of the Method
   *@param  statusId                  Field of the STATUS_ID column.
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String statusId, String statusTypeId, String description) throws CreateException {}

  /** Description of the Method
   *@param  statusId                  Field of the STATUS_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String statusId) throws CreateException
  {
    ejbPostCreate(statusId, null, null);
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
