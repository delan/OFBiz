
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
 *@created    Tue Jul 17 02:08:35 MDT 2001
 *@version    1.0
 */
public class UserLoginSecurityGroupValue implements UserLoginSecurityGroup
{
  /** The variable of the USER_LOGIN_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  private String userLoginId;
  /** The variable of the GROUP_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  private String groupId;

  private UserLoginSecurityGroup userLoginSecurityGroup;

  public UserLoginSecurityGroupValue()
  {
    this.userLoginId = null;
    this.groupId = null;

    this.userLoginSecurityGroup = null;
  }

  public UserLoginSecurityGroupValue(UserLoginSecurityGroup userLoginSecurityGroup) throws RemoteException
  {
    if(userLoginSecurityGroup == null) return;
  
    this.userLoginId = userLoginSecurityGroup.getUserLoginId();
    this.groupId = userLoginSecurityGroup.getGroupId();

    this.userLoginSecurityGroup = userLoginSecurityGroup;
  }

  public UserLoginSecurityGroupValue(UserLoginSecurityGroup userLoginSecurityGroup, String userLoginId, String groupId)
  {
    if(userLoginSecurityGroup == null) return;
  
    this.userLoginId = userLoginId;
    this.groupId = groupId;

    this.userLoginSecurityGroup = userLoginSecurityGroup;
  }


  /** Get the primary key of the USER_LOGIN_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String getUserLoginId()  throws RemoteException { return userLoginId; }

  /** Get the primary key of the GROUP_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String getGroupId()  throws RemoteException { return groupId; }

  /** Get the value object of the UserLoginSecurityGroup class. */
  public UserLoginSecurityGroup getValueObject() throws RemoteException { return this; }
  /** Set the value object of the UserLoginSecurityGroup class. */
  public void setValueObject(UserLoginSecurityGroup valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(userLoginSecurityGroup!=null) userLoginSecurityGroup.setValueObject(valueObject);

    if(userLoginId == null) userLoginId = valueObject.getUserLoginId();
    if(groupId == null) groupId = valueObject.getGroupId();
  }


  /** Get the  UserLogin entity corresponding to this entity. */
  public UserLogin getUserLogin() { return UserLoginHelper.findByPrimaryKey(userLoginId); }
  /** Remove the  UserLogin entity corresponding to this entity. */
  public void removeUserLogin() { UserLoginHelper.removeByPrimaryKey(userLoginId); }

  /** Get the  SecurityGroup entity corresponding to this entity. */
  public SecurityGroup getSecurityGroup() { return SecurityGroupHelper.findByPrimaryKey(groupId); }
  /** Remove the  SecurityGroup entity corresponding to this entity. */
  public void removeSecurityGroup() { SecurityGroupHelper.removeByPrimaryKey(groupId); }

  /** Get a collection of  SecurityGroupPermission related entities. */
  public Collection getSecurityGroupPermissions() { return SecurityGroupPermissionHelper.findByGroupId(groupId); }
  /** Get the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public SecurityGroupPermission getSecurityGroupPermission(String permissionId) { return SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId); }
  /** Remove  SecurityGroupPermission related entities. */
  public void removeSecurityGroupPermissions() { SecurityGroupPermissionHelper.removeByGroupId(groupId); }
  /** Remove the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public void removeSecurityGroupPermission(String permissionId) { SecurityGroupPermissionHelper.removeByPrimaryKey(groupId, permissionId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(userLoginSecurityGroup!=null) return userLoginSecurityGroup.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(userLoginSecurityGroup!=null) return userLoginSecurityGroup.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(userLoginSecurityGroup!=null) return userLoginSecurityGroup.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(userLoginSecurityGroup!=null) return userLoginSecurityGroup.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(userLoginSecurityGroup!=null) userLoginSecurityGroup.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
