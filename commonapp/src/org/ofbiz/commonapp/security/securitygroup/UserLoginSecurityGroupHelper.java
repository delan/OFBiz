
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Security Component - User Login Security Group Entity
 * <p><b>Description:</b> Defines a permission available to a security group
 * <p>The Helper class from the UserLoginSecurityGroup Entity EJB; acts as a proxy for the Home interface
 *
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
public class UserLoginSecurityGroupHelper
{

  /** A static variable to cache the Home object for the UserLoginSecurityGroup EJB */
  private static UserLoginSecurityGroupHome userLoginSecurityGroupHome = null;

  /** Initializes the userLoginSecurityGroupHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The UserLoginSecurityGroupHome instance for the default EJB server
   */
  public static UserLoginSecurityGroupHome getUserLoginSecurityGroupHome()
  {
    if(userLoginSecurityGroupHome == null) //don't want to block here
    {
      synchronized(UserLoginSecurityGroupHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(userLoginSecurityGroupHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupHome");
            userLoginSecurityGroupHome = (UserLoginSecurityGroupHome)MyNarrow.narrow(homeObject, UserLoginSecurityGroupHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("userLoginSecurityGroup home obtained " + userLoginSecurityGroupHome);
        }
      }
    }
    return userLoginSecurityGroupHome;
  }



  /** Remove the UserLoginSecurityGroup corresponding to the primaryKey specified by fields
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   */
  public static void removeByPrimaryKey(String userLoginId, String groupId)
  {
    if(userLoginId == null || groupId == null)
    {
      return;
    }
    UserLoginSecurityGroupPK primaryKey = new UserLoginSecurityGroupPK(userLoginId, groupId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the UserLoginSecurityGroup corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupPK primaryKey)
  {
    if(primaryKey == null) return;
    UserLoginSecurityGroup userLoginSecurityGroup = findByPrimaryKey(primaryKey);
    try
    {
      if(userLoginSecurityGroup != null)
      {
        userLoginSecurityGroup.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a UserLoginSecurityGroup by its Primary Key, specified by individual fields
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return       The UserLoginSecurityGroup corresponding to the primaryKey
   */
  public static UserLoginSecurityGroup findByPrimaryKey(String userLoginId, String groupId)
  {
    if(userLoginId == null || groupId == null) return null;
    UserLoginSecurityGroupPK primaryKey = new UserLoginSecurityGroupPK(userLoginId, groupId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a UserLoginSecurityGroup by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The UserLoginSecurityGroup corresponding to the primaryKey
   */
  public static UserLoginSecurityGroup findByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupPK primaryKey)
  {
    UserLoginSecurityGroup userLoginSecurityGroup = null;
    Debug.logInfo("UserLoginSecurityGroupHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      userLoginSecurityGroup = (UserLoginSecurityGroup)MyNarrow.narrow(getUserLoginSecurityGroupHome().findByPrimaryKey(primaryKey), UserLoginSecurityGroup.class);
      if(userLoginSecurityGroup != null)
      {
        userLoginSecurityGroup = userLoginSecurityGroup.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return userLoginSecurityGroup;
  }

  /** Finds all UserLoginSecurityGroup entities
   *@return    Collection containing all UserLoginSecurityGroup entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("UserLoginSecurityGroupHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getUserLoginSecurityGroupHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a UserLoginSecurityGroup
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                Description of the Returned Value
   */
  public static UserLoginSecurityGroup create(String userLoginId, String groupId)
  {
    UserLoginSecurityGroup userLoginSecurityGroup = null;
    Debug.logInfo("UserLoginSecurityGroupHelper.create: userLoginId, groupId: " + userLoginId + ", " + groupId);
    if(userLoginId == null || groupId == null) { return null; }

    try { userLoginSecurityGroup = (UserLoginSecurityGroup)MyNarrow.narrow(getUserLoginSecurityGroupHome().create(userLoginId, groupId), UserLoginSecurityGroup.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create userLoginSecurityGroup with userLoginId, groupId: " + userLoginId + ", " + groupId);
      Debug.logError(ce);
      userLoginSecurityGroup = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return userLoginSecurityGroup;
  }

  /** Updates the corresponding UserLoginSecurityGroup
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                Description of the Returned Value
   */
  public static UserLoginSecurityGroup update(String userLoginId, String groupId) throws java.rmi.RemoteException
  {
    if(userLoginId == null || groupId == null) { return null; }
    UserLoginSecurityGroup userLoginSecurityGroup = findByPrimaryKey(userLoginId, groupId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    UserLoginSecurityGroup userLoginSecurityGroupValue = new UserLoginSecurityGroupValue();


    userLoginSecurityGroup.setValueObject(userLoginSecurityGroupValue);
    return userLoginSecurityGroup;
  }

  /** Removes/deletes the specified  UserLoginSecurityGroup
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   */
  public static void removeByUserLoginId(String userLoginId)
  {
    if(userLoginId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUserLoginId(userLoginId));

    while(iterator.hasNext())
    {
      try
      {
        UserLoginSecurityGroup userLoginSecurityGroup = (UserLoginSecurityGroup) iterator.next();
        Debug.logInfo("Removing userLoginSecurityGroup with userLoginId:" + userLoginId);
        userLoginSecurityGroup.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds UserLoginSecurityGroup records by the following parameters:
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUserLoginId(String userLoginId)
  {
    Debug.logInfo("findByUserLoginId: userLoginId:" + userLoginId);

    Collection collection = null;
    if(userLoginId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUserLoginSecurityGroupHome().findByUserLoginId(userLoginId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  UserLoginSecurityGroup
   *@param  groupId                  Field of the GROUP_ID column.
   */
  public static void removeByGroupId(String groupId)
  {
    if(groupId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByGroupId(groupId));

    while(iterator.hasNext())
    {
      try
      {
        UserLoginSecurityGroup userLoginSecurityGroup = (UserLoginSecurityGroup) iterator.next();
        Debug.logInfo("Removing userLoginSecurityGroup with groupId:" + groupId);
        userLoginSecurityGroup.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds UserLoginSecurityGroup records by the following parameters:
   *@param  groupId                  Field of the GROUP_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGroupId(String groupId)
  {
    Debug.logInfo("findByGroupId: groupId:" + groupId);

    Collection collection = null;
    if(groupId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUserLoginSecurityGroupHome().findByGroupId(groupId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
