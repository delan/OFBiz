
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
 *@created    Tue Jul 17 02:08:35 MDT 2001
 *@version    1.0
 */
public class UserLoginSecurityGroupHelper
{

  /**
   *  A static variable to cache the Home object for the UserLoginSecurityGroup EJB
   */
  public static UserLoginSecurityGroupHome userLoginSecurityGroupHome = null;

  /**
   *  Initializes the userLoginSecurityGroupHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(userLoginSecurityGroupHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupHome");
        userLoginSecurityGroupHome = (UserLoginSecurityGroupHome)MyNarrow.narrow(homeObject, UserLoginSecurityGroupHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("userLoginSecurityGroup home obtained " + userLoginSecurityGroupHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

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


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupPK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    UserLoginSecurityGroup userLoginSecurityGroup = findByPrimaryKey(primaryKey);
    try
    {
      if(userLoginSecurityGroup != null)
      {
        userLoginSecurityGroup.remove();
      }
    }
    catch(Exception e)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        e.printStackTrace();
      }
    }


  }


  /**
   *  Description of the Method
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return       Description of the Returned Value
   */
  public static UserLoginSecurityGroup findByPrimaryKey(String userLoginId, String groupId)
  {
    if(userLoginId == null || groupId == null)
    {
      return null;
    }
    UserLoginSecurityGroupPK primaryKey = new UserLoginSecurityGroupPK(userLoginId, groupId);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The UserLoginSecurityGroup of primaryKey
   */
  public static UserLoginSecurityGroup findByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.UserLoginSecurityGroupPK primaryKey)
  {
    UserLoginSecurityGroup userLoginSecurityGroup = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("UserLoginSecurityGroupHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      userLoginSecurityGroup = (UserLoginSecurityGroup)MyNarrow.narrow(userLoginSecurityGroupHome.findByPrimaryKey(primaryKey), UserLoginSecurityGroup.class);
      if(userLoginSecurityGroup != null)
      {
        userLoginSecurityGroup = userLoginSecurityGroup.getValueObject();

      }
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return userLoginSecurityGroup;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public static Iterator findAllIterator()
  {
    Collection collection = findAll();
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public static Collection findAll()
  {
    Collection collection = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("UserLoginSecurityGroupHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(userLoginSecurityGroupHome.findAll(), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return collection;
  }

  /**
   *  Description of the Method
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                Description of the Returned Value
   */
  public static UserLoginSecurityGroup create(String userLoginId, String groupId)
  {
    UserLoginSecurityGroup userLoginSecurityGroup = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("UserLoginSecurityGroupHelper.create: userLoginId, groupId: " + userLoginId + ", " + groupId);
    }
    if(userLoginId == null || groupId == null)
    {
      return null;
    }
    init();

    try
    {
      userLoginSecurityGroup = (UserLoginSecurityGroup)MyNarrow.narrow(userLoginSecurityGroupHome.create(userLoginId, groupId), UserLoginSecurityGroup.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create userLoginSecurityGroup with userLoginId, groupId: " + userLoginId + ", " + groupId);
        ce.printStackTrace();
      }
      userLoginSecurityGroup = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return userLoginSecurityGroup;
  }

  /**
   *  Description of the Method
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                Description of the Returned Value
   */
  public static UserLoginSecurityGroup update(String userLoginId, String groupId) throws java.rmi.RemoteException
  {
    if(userLoginId == null || groupId == null)
    {
      return null;
    }
    UserLoginSecurityGroup userLoginSecurityGroup = findByPrimaryKey(userLoginId, groupId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    UserLoginSecurityGroup userLoginSecurityGroupValue = new UserLoginSecurityGroupValue();


  
  

    userLoginSecurityGroup.setValueObject(userLoginSecurityGroupValue);
    return userLoginSecurityGroup;
  }


  
  /**
   *  Description of the Method
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   */
  public static void removeByUserLoginId(String userLoginId)
  {
    if(userLoginId == null)
    {
      return;
    }
    Iterator iterator = findByUserLoginIdIterator(userLoginId);

    while(iterator.hasNext())
    {
      try
      {
        UserLoginSecurityGroup userLoginSecurityGroup = (UserLoginSecurityGroup) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing userLoginSecurityGroup with userLoginId:" + userLoginId);
        }
        userLoginSecurityGroup.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUserLoginIdIterator(String userLoginId)
  {
    Collection collection = findByUserLoginId(userLoginId);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds UserLoginSecurityGroup records by the following fieldters:
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUserLoginId(String userLoginId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByUserLoginId: userLoginId:" + userLoginId);
    }

    Collection collection = null;
    if(userLoginId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(userLoginSecurityGroupHome.findByUserLoginId(userLoginId), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }

  
  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   */
  public static void removeByGroupId(String groupId)
  {
    if(groupId == null)
    {
      return;
    }
    Iterator iterator = findByGroupIdIterator(groupId);

    while(iterator.hasNext())
    {
      try
      {
        UserLoginSecurityGroup userLoginSecurityGroup = (UserLoginSecurityGroup) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing userLoginSecurityGroup with groupId:" + groupId);
        }
        userLoginSecurityGroup.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByGroupIdIterator(String groupId)
  {
    Collection collection = findByGroupId(groupId);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds UserLoginSecurityGroup records by the following fieldters:
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGroupId(String groupId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByGroupId: groupId:" + groupId);
    }

    Collection collection = null;
    if(groupId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(userLoginSecurityGroupHome.findByGroupId(groupId), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }



}
