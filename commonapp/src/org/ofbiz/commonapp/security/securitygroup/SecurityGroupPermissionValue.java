
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
 *@created    Fri Jul 27 01:18:35 MDT 2001
 *@version    1.0
 */
public class SecurityGroupPermissionValue implements SecurityGroupPermission
{
  /** The variable of the GROUP_ID column of the SECURITY_GROUP_PERMISSION table. */
  private String groupId;
  /** The variable of the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table. */
  private String permissionId;

  private SecurityGroupPermission securityGroupPermission;

  public SecurityGroupPermissionValue()
  {
    this.groupId = null;
    this.permissionId = null;

    this.securityGroupPermission = null;
  }

  public SecurityGroupPermissionValue(SecurityGroupPermission securityGroupPermission) throws RemoteException
  {
    if(securityGroupPermission == null) return;
  
    this.groupId = securityGroupPermission.getGroupId();
    this.permissionId = securityGroupPermission.getPermissionId();

    this.securityGroupPermission = securityGroupPermission;
  }

  public SecurityGroupPermissionValue(SecurityGroupPermission securityGroupPermission, String groupId, String permissionId)
  {
    if(securityGroupPermission == null) return;
  
    this.groupId = groupId;
    this.permissionId = permissionId;

    this.securityGroupPermission = securityGroupPermission;
  }


  /** Get the primary key of the GROUP_ID column of the SECURITY_GROUP_PERMISSION table. */
  public String getGroupId()  throws RemoteException { return groupId; }

  /** Get the primary key of the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table. */
  public String getPermissionId()  throws RemoteException { return permissionId; }

  /** Get the value object of the SecurityGroupPermission class. */
  public SecurityGroupPermission getValueObject() throws RemoteException { return this; }
  /** Set the value object of the SecurityGroupPermission class. */
  public void setValueObject(SecurityGroupPermission valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(securityGroupPermission!=null) securityGroupPermission.setValueObject(valueObject);

    if(groupId == null) groupId = valueObject.getGroupId();
    if(permissionId == null) permissionId = valueObject.getPermissionId();
  }


  /** Get the  SecurityGroup entity corresponding to this entity. */
  public SecurityGroup getSecurityGroup() { return SecurityGroupHelper.findByPrimaryKey(groupId); }
  /** Remove the  SecurityGroup entity corresponding to this entity. */
  public void removeSecurityGroup() { SecurityGroupHelper.removeByPrimaryKey(groupId); }

  /** Get the  SecurityPermission entity corresponding to this entity. */
  public SecurityPermission getSecurityPermission() { return SecurityPermissionHelper.findByPrimaryKey(permissionId); }
  /** Remove the  SecurityPermission entity corresponding to this entity. */
  public void removeSecurityPermission() { SecurityPermissionHelper.removeByPrimaryKey(permissionId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(securityGroupPermission!=null) return securityGroupPermission.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(securityGroupPermission!=null) return securityGroupPermission.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(securityGroupPermission!=null) return securityGroupPermission.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(securityGroupPermission!=null) return securityGroupPermission.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(securityGroupPermission!=null) securityGroupPermission.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
