
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Security Component - Security Permission Entity
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
 *@created    Wed Jul 18 12:02:51 MDT 2001
 *@version    1.0
 */

public interface SecurityPermission extends EJBObject
{
  /** Get the primary key of the PERMISSION_ID column of the SECURITY_PERMISSION table. */
  public String getPermissionId() throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the SECURITY_PERMISSION table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the SECURITY_PERMISSION table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this SecurityPermission class. */
  public SecurityPermission getValueObject() throws RemoteException;
  /** Set the values in the value object of this SecurityPermission class. */
  public void setValueObject(SecurityPermission securityPermissionValue) throws RemoteException;


  /** Get a collection of  SecurityGroupPermission related entities. */
  public Collection getSecurityGroupPermissions() throws RemoteException;
  /** Get the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public SecurityGroupPermission getSecurityGroupPermission(String groupId) throws RemoteException;
  /** Remove  SecurityGroupPermission related entities. */
  public void removeSecurityGroupPermissions() throws RemoteException;
  /** Remove the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public void removeSecurityGroupPermission(String groupId) throws RemoteException;

}
