
package org.ofbiz.commonapp.common.status;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class StatusTypeValue implements StatusType
{
  /** The variable of the STATUS_TYPE_ID column of the STATUS_TYPE table. */
  private String statusTypeId;
  /** The variable of the PARENT_TYPE_ID column of the STATUS_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the STATUS_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the STATUS_TYPE table. */
  private String description;

  private StatusType statusType;

  public StatusTypeValue()
  {
    this.statusTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.statusType = null;
  }

  public StatusTypeValue(StatusType statusType) throws RemoteException
  {
    if(statusType == null) return;
  
    this.statusTypeId = statusType.getStatusTypeId();
    this.parentTypeId = statusType.getParentTypeId();
    this.hasTable = statusType.getHasTable();
    this.description = statusType.getDescription();

    this.statusType = statusType;
  }

  public StatusTypeValue(StatusType statusType, String statusTypeId, String parentTypeId, String hasTable, String description)
  {
    if(statusType == null) return;
  
    this.statusTypeId = statusTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.statusType = statusType;
  }


  /** Get the primary key of the STATUS_TYPE_ID column of the STATUS_TYPE table. */
  public String getStatusTypeId()  throws RemoteException { return statusTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the STATUS_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the STATUS_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(statusType!=null) statusType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the STATUS_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the STATUS_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(statusType!=null) statusType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the STATUS_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the STATUS_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(statusType!=null) statusType.setDescription(description);
  }

  /** Get the value object of the StatusType class. */
  public StatusType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the StatusType class. */
  public void setValueObject(StatusType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(statusType!=null) statusType.setValueObject(valueObject);

    if(statusTypeId == null) statusTypeId = valueObject.getStatusTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(statusType!=null) return statusType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(statusType!=null) return statusType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(statusType!=null) return statusType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(statusType!=null) return statusType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(statusType!=null) statusType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
