
package org.ofbiz.commonapp.security.person;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Person Security Group Entity
 * <p><b>Description:</b> Defines a permission available to a security group
 * <p>The Helper class from the PersonSecurityGroup Entity EJB; acts as a proxy for the Home interface
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
 *@created    Wed May 23 02:34:49 MDT 2001
 *@version    1.0
 */
public class PersonSecurityGroupHelper
{

  /**
   *  A static variable to cache the Home object for the PersonSecurityGroup EJB
   */
  public static PersonSecurityGroupHome personSecurityGroupHome = null;

  /**
   *  Initializes the personSecurityGroupHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(personSecurityGroupHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.person.PersonSecurityGroupHome");
        personSecurityGroupHome = (PersonSecurityGroupHome)MyNarrow.narrow(homeObject, PersonSecurityGroupHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("personSecurityGroup home obtained " + personSecurityGroupHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  groupId                  Field of the GROUP_ID column.
   */
  public static void removeByPrimaryKey(String username, String groupId)
  {
    if(username == null || groupId == null)
    {
      return;
    }
    PersonSecurityGroupPK primaryKey = new PersonSecurityGroupPK(username, groupId);
    removeByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.person.PersonSecurityGroupPK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    PersonSecurityGroup personSecurityGroup = findByPrimaryKey(primaryKey);
    try
    {
      if(personSecurityGroup != null)
      {
        personSecurityGroup.remove();
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

   *@param  username                  Field of the USERNAME column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return       Description of the Returned Value
   */
  public static PersonSecurityGroup findByPrimaryKey(String username, String groupId)
  {
    if(username == null || groupId == null)
    {
      return null;
    }
    PersonSecurityGroupPK primaryKey = new PersonSecurityGroupPK(username, groupId);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The PersonSecurityGroup of primaryKey
   */
  public static PersonSecurityGroup findByPrimaryKey(org.ofbiz.commonapp.security.person.PersonSecurityGroupPK primaryKey)
  {
    PersonSecurityGroup personSecurityGroup = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonSecurityGroupHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      personSecurityGroup = (PersonSecurityGroup)MyNarrow.narrow(personSecurityGroupHome.findByPrimaryKey(primaryKey), PersonSecurityGroup.class);
      if(personSecurityGroup != null)
      {
        personSecurityGroup = personSecurityGroup.getValueObject();

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
    return personSecurityGroup;
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
      System.out.println("PersonSecurityGroupHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(personSecurityGroupHome.findAll(), Collection.class);
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

   *@param  username                  Field of the USERNAME column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                Description of the Returned Value
   */
  public static PersonSecurityGroup create(String username, String groupId)
  {
    PersonSecurityGroup personSecurityGroup = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonSecurityGroupHelper.create: username, groupId: " + username + ", " + groupId);
    }
    if(username == null || groupId == null)
    {
      return null;
    }
    init();

    try
    {
      personSecurityGroup = (PersonSecurityGroup)MyNarrow.narrow(personSecurityGroupHome.create(username, groupId), PersonSecurityGroup.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create personSecurityGroup with username, groupId: " + username + ", " + groupId);
        ce.printStackTrace();
      }
      personSecurityGroup = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return personSecurityGroup;
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  groupId                  Field of the GROUP_ID column.
   *@return                Description of the Returned Value
   */
  public static PersonSecurityGroup update(String username, String groupId) throws java.rmi.RemoteException
  {
    if(username == null || groupId == null)
    {
      return null;
    }
    PersonSecurityGroup personSecurityGroup = findByPrimaryKey(username, groupId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PersonSecurityGroup personSecurityGroupValue = new PersonSecurityGroupValue();

    //When doing a setValueObject, everything gets copied over.  So, for all
    //null fieldeters, we will just let it default to the original value.
    personSecurityGroupValue.setValueObject(personSecurityGroup);


  
  

    personSecurityGroup.setValueObject(personSecurityGroupValue);
    return personSecurityGroup;
  }


  
  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   */
  public static void removeByUsername(String username)
  {
    if(username == null)
    {
      return;
    }
    Iterator iterator = findByUsernameIterator(username);

    while(iterator.hasNext())
    {
      try
      {
        PersonSecurityGroup personSecurityGroup = (PersonSecurityGroup) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personSecurityGroup with username:" + username);
        }
        personSecurityGroup.remove();
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

   *@param  username                  Field of the USERNAME column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUsernameIterator(String username)
  {
    Collection collection = findByUsername(username);
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
   *  Finds PersonSecurityGroup records by the following fieldters:
   *

   *@param  username                  Field of the USERNAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUsername(String username)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByUsername: username:" + username);
    }

    Collection collection = null;
    if(username == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personSecurityGroupHome.findByUsername(username), Collection.class);
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
        PersonSecurityGroup personSecurityGroup = (PersonSecurityGroup) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personSecurityGroup with groupId:" + groupId);
        }
        personSecurityGroup.remove();
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
   *  Finds PersonSecurityGroup records by the following fieldters:
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
      collection = (Collection) MyNarrow.narrow(personSecurityGroupHome.findByGroupId(groupId), Collection.class);
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

   *@param  username                  Field for the USERNAME column.
   *@param  permissionId              Field for the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table.
   */
  public static void removeByUsernameAndPermissionId(String username, String permissionId)
  {
    if(username == null || permissionId == null)
    {
      return;
    }
    Iterator iterator = findByUsernameAndPermissionIdIterator(username, permissionId);

    while(iterator.hasNext())
    {
      try
      {
        PersonSecurityGroup personSecurityGroup = (PersonSecurityGroup) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personSecurityGroup with username, permissionId:" + username + ", " + permissionId);
        }
        personSecurityGroup.remove();
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

   *@param  username                  Field for the USERNAME column.
   *@param  permissionId              Field for the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUsernameAndPermissionIdIterator(String username, String permissionId)
  {
    Collection collection = findByUsernameAndPermissionId(username, permissionId);
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
   *  Finds PersonSecurityGroup records by the following fieldters:
   *

   *@param  username                  Field for the USERNAME column.
   *@param  permissionId              Field for the PERMISSION_ID column of the SECURITY_GROUP_PERMISSION table.
   *@return      Description of the Returned Value
   */
  public static Collection findByUsernameAndPermissionId(String username, String permissionId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByUsernameAndPermissionId: username, permissionId:" + username + ", " + permissionId);
    }

    Collection collection = null;
    if(username == null || permissionId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personSecurityGroupHome.findByUsernameAndPermissionId(username, permissionId), Collection.class);
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
