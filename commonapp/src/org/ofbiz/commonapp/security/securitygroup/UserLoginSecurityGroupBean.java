
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
 *@created    Sun Jul 08 01:14:09 MDT 2001
 *@version    1.0
 */
public class UserLoginSecurityGroupBean implements EntityBean
{
  /** The variable for the USER_LOGIN_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String userLoginId;
  /** The variable for the GROUP_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String groupId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the UserLoginSecurityGroupBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key USER_LOGIN_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String getUserLoginId() { return userLoginId; }

  /** Get the primary key GROUP_ID column of the USER_LOGIN_SECURITY_GROUP table. */
  public String getGroupId() { return groupId; }

  /** Sets the values from ValueObject attribute of the UserLoginSecurityGroupBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(UserLoginSecurityGroup valueObject)
  {
  }

  /** Gets the ValueObject attribute of the UserLoginSecurityGroupBean object
   *@return    The ValueObject value
   */
  public UserLoginSecurityGroup getValueObject()
  {
    if(this.entityContext != null)
    {
      return new UserLoginSecurityGroupValue((UserLoginSecurityGroup)this.entityContext.getEJBObject(), userLoginId, groupId);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupPK ejbCreate(String userLoginId, String groupId) throws CreateException
  {
    this.userLoginId = userLoginId;
    this.groupId = groupId;
    return null;
  }

  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String userLoginId, String groupId) throws CreateException {}

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
