
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class SecurityPermissionValue implements SecurityPermission
{
  /** The variable of the PERMISSION_ID column of the SECURITY_PERMISSION table. */
  private String permissionId;
  /** The variable of the DESCRIPTION column of the SECURITY_PERMISSION table. */
  private String description;

  private SecurityPermission securityPermission;

  public SecurityPermissionValue()
  {
    this.permissionId = null;
    this.description = null;

    this.securityPermission = null;
  }

  public SecurityPermissionValue(SecurityPermission securityPermission) throws RemoteException
  {
    if(securityPermission == null) return;
  
    this.permissionId = securityPermission.getPermissionId();
    this.description = securityPermission.getDescription();

    this.securityPermission = securityPermission;
  }

  public SecurityPermissionValue(SecurityPermission securityPermission, String permissionId, String description)
  {
    if(securityPermission == null) return;
  
    this.permissionId = permissionId;
    this.description = description;

    this.securityPermission = securityPermission;
  }


  /** Get the primary key of the PERMISSION_ID column of the SECURITY_PERMISSION table. */
  public String getPermissionId()  throws RemoteException { return permissionId; }

  /** Get the value of the DESCRIPTION column of the SECURITY_PERMISSION table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the SECURITY_PERMISSION table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(securityPermission!=null) securityPermission.setDescription(description);
  }

  /** Get the value object of the SecurityPermission class. */
  public SecurityPermission getValueObject() throws RemoteException { return this; }
  /** Set the value object of the SecurityPermission class. */
  public void setValueObject(SecurityPermission valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(securityPermission!=null) securityPermission.setValueObject(valueObject);

    if(permissionId == null) permissionId = valueObject.getPermissionId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  SecurityGroupPermission related entities. */
  public Collection getSecurityGroupPermissions() { return SecurityGroupPermissionHelper.findByPermissionId(permissionId); }
  /** Get the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public SecurityGroupPermission getSecurityGroupPermission(String groupId) { return SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId); }
  /** Remove  SecurityGroupPermission related entities. */
  public void removeSecurityGroupPermissions() { SecurityGroupPermissionHelper.removeByPermissionId(permissionId); }
  /** Remove the  SecurityGroupPermission keyed by member(s) of this class, and other passed parameters. */
  public void removeSecurityGroupPermission(String groupId) { SecurityGroupPermissionHelper.removeByPrimaryKey(groupId, permissionId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(securityPermission!=null) return securityPermission.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(securityPermission!=null) return securityPermission.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(securityPermission!=null) return securityPermission.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(securityPermission!=null) return securityPermission.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(securityPermission!=null) securityPermission.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
