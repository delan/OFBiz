
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.security.login.*;

/**
 * <p><b>Title:</b> Security Component - User Login Security Group Entity
 * <p><b>Description:</b> Defines a permission available to a security group
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
 *@created    Sun Jul 08 01:14:08 MDT 2001
 *@version    1.0
 */

public interface UserLoginSecurityGroup extends EJBObject
{
  /** Get the primary key of the USER_LOGIN_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String getUserLoginId() throws RemoteException;
  
  /** Get the primary key of the GROUP_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String getGroupId() throws RemoteException;
  

  /** Get the value object of this UserLoginSecurityGroup class. */
  public UserLoginSecurityGroup getValueObject() throws RemoteException;
  /** Set the values in the value object of this UserLoginSecurityGroup class. */
  public void setValueObject(UserLoginSecurityGroup userLoginSecurityGroupValue) throws RemoteException;


  /** Get the  UserLogin entity corresponding to this entity. */
  public UserLogin getUserLogin() throws RemoteException;
  /** Remove the  UserLogin entity corresponding to this entity. */
  public void removeUserLogin() throws RemoteException;  

  /** Get the  SecurityGroup entity corresponding to this entity. */
  public SecurityGroup getSecurityGroup() throws RemoteException;
  /** Remove the  SecurityGroup entity corresponding to this entity. */
  public void removeSecurityGroup() throws RemoteException;  

  /** Get a collection of  SecurityGroupPermission related entities. */
  public Collection getSecurityGroupPermissions() throws RemoteException;
  /** Get the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public SecurityGroupPermission getSecurityGroupPermission(String permissionId) throws RemoteException;
  /** Remove  SecurityGroupPermission related entities. */
  public void removeSecurityGroupPermissions() throws RemoteException;
  /** Remove the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public void removeSecurityGroupPermission(String permissionId) throws RemoteException;

}
