
package org.ofbiz.commonapp.security.securitygroup;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Security Group Entity
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
 *@created    Wed May 23 02:35:37 MDT 2001
 *@version    1.0
 */

public class SecurityGroupValue implements SecurityGroup
{

  /**
   *  The variable of the GROUP_ID column of the SECURITY_GROUP table.
   */
  private String groupId;

  /**
   *  The variable of the DESCRIPTION column of the SECURITY_GROUP table.
   */
  private String description;


  private SecurityGroup securityGroup;

  public SecurityGroupValue()
  {

    this.groupId = null;
    this.description = null;

    this.securityGroup = null;
  }

  public SecurityGroupValue(SecurityGroup securityGroup) throws RemoteException
  {
    if(securityGroup == null) return;


    this.groupId = securityGroup.getGroupId();
    this.description = securityGroup.getDescription();

    this.securityGroup = securityGroup;
  }

  public SecurityGroupValue(SecurityGroup securityGroup, String groupId, String description)
  {
    if(securityGroup == null) return;


    this.groupId = groupId;
    this.description = description;

    this.securityGroup = securityGroup;
  }


  /**
   *  Get the primary key of the GROUP_ID column of the SECURITY_GROUP table.
   */
  public String getGroupId()  throws RemoteException
  {
    return groupId;
  }
  
  /**
   *  Get the value of the DESCRIPTION column of the SECURITY_GROUP table.
   */
  public String getDescription() throws RemoteException
  {
    return description;
  }
  /**
   *  Set the value of the DESCRIPTION column of the SECURITY_GROUP table.
   */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(securityGroup!=null) securityGroup.setDescription(description);
  }
  

  /**
   *  Get the value object of the SecurityGroup class.
   */
  public SecurityGroup getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the SecurityGroup class.
   */
  public void setValueObject(SecurityGroup valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(securityGroup!=null) securityGroup.setValueObject(valueObject);

    if(groupId == null) groupId = valueObject.getGroupId();
  
  
    description = valueObject.getDescription();
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(securityGroup!=null) return securityGroup.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(securityGroup!=null) return securityGroup.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(securityGroup!=null) return securityGroup.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(securityGroup!=null) return securityGroup.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(securityGroup!=null) securityGroup.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
