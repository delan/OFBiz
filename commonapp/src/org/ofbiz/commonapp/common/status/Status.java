
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

public interface Status extends EJBObject
{
  /** Get the primary key of the STATUS_ID column of the STATUS table. */
  public String getStatusId() throws RemoteException;
  
  /** Get the value of the STATUS_TYPE_ID column of the STATUS table. */
  public String getStatusTypeId() throws RemoteException;
  /** Set the value of the STATUS_TYPE_ID column of the STATUS table. */
  public void setStatusTypeId(String statusTypeId) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the STATUS table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the STATUS table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this Status class. */
  public Status getValueObject() throws RemoteException;
  /** Set the values in the value object of this Status class. */
  public void setValueObject(Status statusValue) throws RemoteException;


  /** Get the  StatusType entity corresponding to this entity. */
  public StatusType getStatusType() throws RemoteException;
  /** Remove the  StatusType entity corresponding to this entity. */
  public void removeStatusType() throws RemoteException;  

}
