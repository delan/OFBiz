
package org.ofbiz.commonapp.security.person;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Person Security Group Entity
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
 *@created    Wed May 23 02:34:41 MDT 2001
 *@version    1.0
 */
public class PersonSecurityGroupValue implements PersonSecurityGroup
{

  /**
   *  The variable of the USERNAME column of the PERSON_SECURITY_GROUP table.
   */
  private String username;

  /**
   *  The variable of the GROUP_ID column of the PERSON_SECURITY_GROUP table.
   */
  private String groupId;


  private PersonSecurityGroup personSecurityGroup;

  public PersonSecurityGroupValue()
  {

    this.username = null;
    this.groupId = null;

    this.personSecurityGroup = null;
  }

  public PersonSecurityGroupValue(PersonSecurityGroup personSecurityGroup) throws RemoteException
  {
    if(personSecurityGroup == null) return;


    this.username = personSecurityGroup.getUsername();
    this.groupId = personSecurityGroup.getGroupId();

    this.personSecurityGroup = personSecurityGroup;
  }

  public PersonSecurityGroupValue(PersonSecurityGroup personSecurityGroup, String username, String groupId)
  {
    if(personSecurityGroup == null) return;


    this.username = username;
    this.groupId = groupId;

    this.personSecurityGroup = personSecurityGroup;
  }


  /**
   *  Get the primary key of the USERNAME column of the PERSON_SECURITY_GROUP table.
   */
  public String getUsername()  throws RemoteException
  {
    return username;
  }
  
  /**
   *  Get the primary key of the GROUP_ID column of the PERSON_SECURITY_GROUP table.
   */
  public String getGroupId()  throws RemoteException
  {
    return groupId;
  }
  

  /**
   *  Get the value object of the PersonSecurityGroup class.
   */
  public PersonSecurityGroup getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the PersonSecurityGroup class.
   */
  public void setValueObject(PersonSecurityGroup valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(personSecurityGroup!=null) personSecurityGroup.setValueObject(valueObject);

    if(username == null) username = valueObject.getUsername();
  
  
    if(groupId == null) groupId = valueObject.getGroupId();
  
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(personSecurityGroup!=null) return personSecurityGroup.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(personSecurityGroup!=null) return personSecurityGroup.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(personSecurityGroup!=null) return personSecurityGroup.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(personSecurityGroup!=null) return personSecurityGroup.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(personSecurityGroup!=null) personSecurityGroup.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
