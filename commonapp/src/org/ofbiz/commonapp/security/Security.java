package org.ofbiz.commonapp.security;

import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;

import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.person.*;
import org.ofbiz.commonapp.security.person.*;
import org.ofbiz.commonapp.security.securitygroup.*;

/**
 * <p><b>Title:</b> Security handler
 * <p><b>Description:</b> Security class - contains methods to check security permissions.
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
 *@created    May 21, 2001
 *@version    1.0
 */
public class Security
{
  /** Checks to see if the currently logged in person has the passed permission.
   * @param permission Name of the permission to check.
   * @param session The current HTTP session, contains the logged in person as an attribute.
   * @throws RemoteException Standard RMI Remote Exception
   * @return Returns true if the currently logged in person has the specified permission, otherwise returns false.
   */  
  public static boolean hasPermission(String permission, HttpSession session) throws java.rmi.RemoteException
  {
    Person person = (Person)session.getAttribute("PERSON");
    if(person == null) return false;

    //--For some strange reason the finder here is not working, just have to fix it later
    //Collection collection = PersonSecurityGroupHelper.findByUsernameAndPermissionId(person.getUsername(), permission);
    //if(collection != null && collection.size() > 0) return true;

    //Slow, but working method
    Iterator iterator = PersonSecurityGroupHelper.findByUsernameIterator(person.getUsername());
    PersonSecurityGroup personSecurityGroup = null;
    SecurityGroupPermission securityGroupPermission = null;

    while(iterator.hasNext())
    {
      personSecurityGroup = (PersonSecurityGroup)iterator.next();
      securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(personSecurityGroup.getGroupId(), permission);
      if(securityGroupPermission != null) return true;
    }

    return false;
 }

 /** Like hasPermission above, except it has functionality specific to Entity permissions. Checks the entity for the specified action, as well as for "_ADMIN" to allow for simplified general administration permission.
  * @param entity The name of the Entity corresponding to the desired permission.
  * @param action The action on the Entity corresponding to the desired permission.
  * @param session The current HTTP session, contains the logged in person as an attribute.
  * @throws RemoteException Standard RMI Remote Exception
  * @return Returns true if the currently logged in person has the specified permission, otherwise returns false.
  */ 
  public static boolean hasEntityPermission(String entity, String action, HttpSession session) throws java.rmi.RemoteException
  {
    Person person = (Person)session.getAttribute("PERSON");
    if(person == null) return false;

    System.out.println("hasEntityPermission: entity=" + entity + ", action=" + action);
    //--For some strange reason the finder here is not working, just have to fix it later
    //Collection collection = PersonSecurityGroupHelper.findByUsernameAndPermissionId(person.getUsername(), entity + "_ADMIN");
    //if(collection != null && collection.size() > 0) return true;
    //collection = PersonSecurityGroupHelper.findByUsernameAndPermissionId(person.getUsername(), entity + action);
    //if(collection != null && collection.size() > 0) return true;
    
    //Slow, but working method
    Iterator iterator = PersonSecurityGroupHelper.findByUsernameIterator(person.getUsername());
    PersonSecurityGroup personSecurityGroup = null;
    SecurityGroupPermission securityGroupPermission = null;

    while(iterator.hasNext())
    {
      personSecurityGroup = (PersonSecurityGroup)iterator.next();
      
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        System.out.println("hasEntityPermission: personSecurityGroup=" + personSecurityGroup.toString());

      securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(personSecurityGroup.getGroupId(), entity + "_ADMIN");
      if(securityGroupPermission != null) return true;
      securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(personSecurityGroup.getGroupId(), entity + action);
      if(securityGroupPermission != null) return true;
    }

    return false;
  }
}
