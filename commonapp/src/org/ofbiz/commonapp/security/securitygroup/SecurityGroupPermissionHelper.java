
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Security Group Permission Entity
 * <p><b>Description:</b> Defines a permission available to a security group
 * <p>The Helper class from the SecurityGroupPermission Entity EJB; acts as a proxy for the Home interface
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
 *@created    Sun Jul 08 01:14:08 MDT 2001
 *@version    1.0
 */
public class SecurityGroupPermissionHelper
{

  /**
   *  A static variable to cache the Home object for the SecurityGroupPermission EJB
   */
  public static SecurityGroupPermissionHome securityGroupPermissionHome = null;

  /**
   *  Initializes the securityGroupPermissionHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(securityGroupPermissionHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionHome");
        securityGroupPermissionHome = (SecurityGroupPermissionHome)MyNarrow.narrow(homeObject, SecurityGroupPermissionHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("securityGroupPermission home obtained " + securityGroupPermissionHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   */
  public static void removeByPrimaryKey(String groupId, String permissionId)
  {
    if(groupId == null || permissionId == null)
    {
      return;
    }
    SecurityGroupPermissionPK primaryKey = new SecurityGroupPermissionPK(groupId, permissionId);
    removeByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionPK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    SecurityGroupPermission securityGroupPermission = findByPrimaryKey(primaryKey);
    try
    {
      if(securityGroupPermission != null)
      {
        securityGroupPermission.remove();
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

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return       Description of the Returned Value
   */
  public static SecurityGroupPermission findByPrimaryKey(String groupId, String permissionId)
  {
    if(groupId == null || permissionId == null)
    {
      return null;
    }
    SecurityGroupPermissionPK primaryKey = new SecurityGroupPermissionPK(groupId, permissionId);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The SecurityGroupPermission of primaryKey
   */
  public static SecurityGroupPermission findByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionPK primaryKey)
  {
    SecurityGroupPermission securityGroupPermission = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("SecurityGroupPermissionHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      securityGroupPermission = (SecurityGroupPermission)MyNarrow.narrow(securityGroupPermissionHome.findByPrimaryKey(primaryKey), SecurityGroupPermission.class);
      if(securityGroupPermission != null)
      {
        securityGroupPermission = securityGroupPermission.getValueObject();

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
    return securityGroupPermission;
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
      System.out.println("SecurityGroupPermissionHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(securityGroupPermissionHome.findAll(), Collection.class);
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
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroupPermission create(String groupId, String permissionId)
  {
    SecurityGroupPermission securityGroupPermission = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("SecurityGroupPermissionHelper.create: groupId, permissionId: " + groupId + ", " + permissionId);
    }
    if(groupId == null || permissionId == null)
    {
      return null;
    }
    init();

    try
    {
      securityGroupPermission = (SecurityGroupPermission)MyNarrow.narrow(securityGroupPermissionHome.create(groupId, permissionId), SecurityGroupPermission.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create securityGroupPermission with groupId, permissionId: " + groupId + ", " + permissionId);
        ce.printStackTrace();
      }
      securityGroupPermission = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return securityGroupPermission;
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroupPermission update(String groupId, String permissionId) throws java.rmi.RemoteException
  {
    if(groupId == null || permissionId == null)
    {
      return null;
    }
    SecurityGroupPermission securityGroupPermission = findByPrimaryKey(groupId, permissionId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SecurityGroupPermission securityGroupPermissionValue = new SecurityGroupPermissionValue();


  
  

    securityGroupPermission.setValueObject(securityGroupPermissionValue);
    return securityGroupPermission;
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
        SecurityGroupPermission securityGroupPermission = (SecurityGroupPermission) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing securityGroupPermission with groupId:" + groupId);
        }
        securityGroupPermission.remove();
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
   *  Finds SecurityGroupPermission records by the following fieldters:
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
      collection = (Collection) MyNarrow.narrow(securityGroupPermissionHome.findByGroupId(groupId), Collection.class);
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

   *@param  permissionId                  Field of the PERMISSION_ID column.
   */
  public static void removeByPermissionId(String permissionId)
  {
    if(permissionId == null)
    {
      return;
    }
    Iterator iterator = findByPermissionIdIterator(permissionId);

    while(iterator.hasNext())
    {
      try
      {
        SecurityGroupPermission securityGroupPermission = (SecurityGroupPermission) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing securityGroupPermission with permissionId:" + permissionId);
        }
        securityGroupPermission.remove();
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

   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByPermissionIdIterator(String permissionId)
  {
    Collection collection = findByPermissionId(permissionId);
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
   *  Finds SecurityGroupPermission records by the following fieldters:
   *

   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPermissionId(String permissionId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByPermissionId: permissionId:" + permissionId);
    }

    Collection collection = null;
    if(permissionId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(securityGroupPermissionHome.findByPermissionId(permissionId), Collection.class);
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
