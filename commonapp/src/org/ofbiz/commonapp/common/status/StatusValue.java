
package org.ofbiz.commonapp.common.status;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class StatusValue implements Status
{
  /** The variable of the STATUS_ID column of the STATUS table. */
  private String statusId;
  /** The variable of the STATUS_TYPE_ID column of the STATUS table. */
  private String statusTypeId;
  /** The variable of the DESCRIPTION column of the STATUS table. */
  private String description;

  private Status status;

  public StatusValue()
  {
    this.statusId = null;
    this.statusTypeId = null;
    this.description = null;

    this.status = null;
  }

  public StatusValue(Status status) throws RemoteException
  {
    if(status == null) return;
  
    this.statusId = status.getStatusId();
    this.statusTypeId = status.getStatusTypeId();
    this.description = status.getDescription();

    this.status = status;
  }

  public StatusValue(Status status, String statusId, String statusTypeId, String description)
  {
    if(status == null) return;
  
    this.statusId = statusId;
    this.statusTypeId = statusTypeId;
    this.description = description;

    this.status = status;
  }


  /** Get the primary key of the STATUS_ID column of the STATUS table. */
  public String getStatusId()  throws RemoteException { return statusId; }

  /** Get the value of the STATUS_TYPE_ID column of the STATUS table. */
  public String getStatusTypeId() throws RemoteException { return statusTypeId; }
  /** Set the value of the STATUS_TYPE_ID column of the STATUS table. */
  public void setStatusTypeId(String statusTypeId) throws RemoteException
  {
    this.statusTypeId = statusTypeId;
    if(status!=null) status.setStatusTypeId(statusTypeId);
  }

  /** Get the value of the DESCRIPTION column of the STATUS table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the STATUS table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(status!=null) status.setDescription(description);
  }

  /** Get the value object of the Status class. */
  public Status getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Status class. */
  public void setValueObject(Status valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(status!=null) status.setValueObject(valueObject);

    if(statusId == null) statusId = valueObject.getStatusId();
    statusTypeId = valueObject.getStatusTypeId();
    description = valueObject.getDescription();
  }


  /** Get the  StatusType entity corresponding to this entity. */
  public StatusType getStatusType() { return StatusTypeHelper.findByPrimaryKey(statusTypeId); }
  /** Remove the  StatusType entity corresponding to this entity. */
  public void removeStatusType() { StatusTypeHelper.removeByPrimaryKey(statusTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(status!=null) return status.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(status!=null) return status.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(status!=null) return status.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(status!=null) return status.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(status!=null) status.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
