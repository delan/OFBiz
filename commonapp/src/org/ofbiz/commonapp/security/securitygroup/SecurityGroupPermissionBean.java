
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Security Component - Security Group Permission Entity
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
 *@created    Wed Jul 18 12:02:52 MDT 2001
 *@version    1.0
 */
public class SecurityGroupPermissionBean implements EntityBean
{
  /** The variable for the GROUP_ID column of the SECURITY_GROUP_PERMISSION table. */
  public String groupId;
  /** The variable for the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table. */
  public String permissionId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the SecurityGroupPermissionBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key GROUP_ID column of the SECURITY_GROUP_PERMISSION table. */
  public String getGroupId() { return groupId; }

  /** Get the primary key PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table. */
  public String getPermissionId() { return permissionId; }

  /** Sets the values from ValueObject attribute of the SecurityGroupPermissionBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(SecurityGroupPermission valueObject)
  {
  }

  /** Gets the ValueObject attribute of the SecurityGroupPermissionBean object
   *@return    The ValueObject value
   */
  public SecurityGroupPermission getValueObject()
  {
    if(this.entityContext != null)
    {
      return new SecurityGroupPermissionValue((SecurityGroupPermission)this.entityContext.getEJBObject(), groupId, permissionId);
    }
    else { return null; }
  }


  /** Get the  SecurityGroup entity corresponding to this entity. */
  public SecurityGroup getSecurityGroup() { return SecurityGroupHelper.findByPrimaryKey(groupId); }
  /** Remove the  SecurityGroup entity corresponding to this entity. */
  public void removeSecurityGroup() { SecurityGroupHelper.removeByPrimaryKey(groupId); }

  /** Get the  SecurityPermission entity corresponding to this entity. */
  public SecurityPermission getSecurityPermission() { return SecurityPermissionHelper.findByPrimaryKey(permissionId); }
  /** Remove the  SecurityPermission entity corresponding to this entity. */
  public void removeSecurityPermission() { SecurityPermissionHelper.removeByPrimaryKey(permissionId); }


  /** Description of the Method
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionPK ejbCreate(String groupId, String permissionId) throws CreateException
  {
    this.groupId = groupId;
    this.permissionId = permissionId;
    return null;
  }

  /** Description of the Method
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String groupId, String permissionId) throws CreateException {}

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
