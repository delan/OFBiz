
package org.ofbiz.commonapp.common.status;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Status Type Entity
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
public class StatusTypeBean implements EntityBean
{
  /** The variable for the STATUS_TYPE_ID column of the STATUS_TYPE table. */
  public String statusTypeId;
  /** The variable for the PARENT_TYPE_ID column of the STATUS_TYPE table. */
  public String parentTypeId;
  /** The variable for the HAS_TABLE column of the STATUS_TYPE table. */
  public String hasTable;
  /** The variable for the DESCRIPTION column of the STATUS_TYPE table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the StatusTypeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key STATUS_TYPE_ID column of the STATUS_TYPE table. */
  public String getStatusTypeId() { return statusTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the STATUS_TYPE table. */
  public String getParentTypeId() { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the STATUS_TYPE table. */
  public void setParentTypeId(String parentTypeId)
  {
    this.parentTypeId = parentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the HAS_TABLE column of the STATUS_TYPE table. */
  public String getHasTable() { return hasTable; }
  /** Set the value of the HAS_TABLE column of the STATUS_TYPE table. */
  public void setHasTable(String hasTable)
  {
    this.hasTable = hasTable;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the STATUS_TYPE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the STATUS_TYPE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the StatusTypeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(StatusType valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getParentTypeId() != null)
      {
        this.parentTypeId = valueObject.getParentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getHasTable() != null)
      {
        this.hasTable = valueObject.getHasTable();
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

  /** Gets the ValueObject attribute of the StatusTypeBean object
   *@return    The ValueObject value
   */
  public StatusType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new StatusTypeValue((StatusType)this.entityContext.getEJBObject(), statusTypeId, parentTypeId, hasTable, description);
    }
    else { return null; }
  }


  /** Get the Parent StatusType entity corresponding to this entity. */
  public StatusType getParentStatusType() { return StatusTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent StatusType entity corresponding to this entity. */
  public void removeParentStatusType() { StatusTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child StatusType related entities. */
  public Collection getChildStatusTypes() { return StatusTypeHelper.findByParentTypeId(statusTypeId); }
  /** Get the Child StatusType keyed by member(s) of this class, and other passed parameters. */
  public StatusType getChildStatusType(String statusTypeId) { return StatusTypeHelper.findByPrimaryKey(statusTypeId); }
  /** Remove Child StatusType related entities. */
  public void removeChildStatusTypes() { StatusTypeHelper.removeByParentTypeId(statusTypeId); }
  /** Remove the Child StatusType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildStatusType(String statusTypeId) { StatusTypeHelper.removeByPrimaryKey(statusTypeId); }

  /** Get a collection of  Status related entities. */
  public Collection getStatuss() { return StatusHelper.findByStatusTypeId(statusTypeId); }
  /** Get the  Status keyed by member(s) of this class, and other passed parameters. */
  public Status getStatus(String statusId) { return StatusHelper.findByPrimaryKey(statusId); }
  /** Remove  Status related entities. */
  public void removeStatuss() { StatusHelper.removeByStatusTypeId(statusTypeId); }
  /** Remove the  Status keyed by member(s) of this class, and other passed parameters. */
  public void removeStatus(String statusId) { StatusHelper.removeByPrimaryKey(statusId); }


  /** Description of the Method
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String statusTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {
    this.statusTypeId = statusTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String statusTypeId) throws CreateException
  {
    return ejbCreate(statusTypeId, null, null, null);
  }

  /** Description of the Method
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String statusTypeId, String parentTypeId, String hasTable, String description) throws CreateException {}

  /** Description of the Method
   *@param  statusTypeId                  Field of the STATUS_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String statusTypeId) throws CreateException
  {
    ejbPostCreate(statusTypeId, null, null, null);
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
