
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Security Permission Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the SecurityPermission Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jun 29 12:50:47 MDT 2001
 *@version    1.0
 */
public class SecurityPermissionHelper
{

  /**
   *  A static variable to cache the Home object for the SecurityPermission EJB
   */
  public static SecurityPermissionHome securityPermissionHome = null;

  /**
   *  Initializes the securityPermissionHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(securityPermissionHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.SecurityPermissionHome");
        securityPermissionHome = (SecurityPermissionHome)MyNarrow.narrow(homeObject, SecurityPermissionHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("securityPermission home obtained " + securityPermissionHome);
      }
    }
  }




  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    SecurityPermission securityPermission = findByPrimaryKey(primaryKey);
    try
    {
      if(securityPermission != null)
      {
        securityPermission.remove();
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
   *@param  primaryKey  The primary key to find by.
   *@return             The SecurityPermission of primaryKey
   */
  public static SecurityPermission findByPrimaryKey(java.lang.String primaryKey)
  {
    SecurityPermission securityPermission = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("SecurityPermissionHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      securityPermission = (SecurityPermission)MyNarrow.narrow(securityPermissionHome.findByPrimaryKey(primaryKey), SecurityPermission.class);
      if(securityPermission != null)
      {
        securityPermission = securityPermission.getValueObject();

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
    return securityPermission;
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
      System.out.println("SecurityPermissionHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(securityPermissionHome.findAll(), Collection.class);
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
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityPermission create(String permissionId, String description)
  {
    SecurityPermission securityPermission = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("SecurityPermissionHelper.create: permissionId: " + permissionId);
    }
    if(permissionId == null)
    {
      return null;
    }
    init();

    try
    {
      securityPermission = (SecurityPermission)MyNarrow.narrow(securityPermissionHome.create(permissionId, description), SecurityPermission.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create securityPermission with permissionId: " + permissionId);
        ce.printStackTrace();
      }
      securityPermission = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return securityPermission;
  }

  /**
   *  Description of the Method
   *

   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityPermission update(String permissionId, String description) throws java.rmi.RemoteException
  {
    if(permissionId == null)
    {
      return null;
    }
    SecurityPermission securityPermission = findByPrimaryKey(permissionId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SecurityPermission securityPermissionValue = new SecurityPermissionValue();


  
  
    if(description != null)
    {
      securityPermissionValue.setDescription(description);
    }

    securityPermission.setValueObject(securityPermissionValue);
    return securityPermission;
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
        SecurityPermission securityPermission = (SecurityPermission) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing securityPermission with permissionId:" + permissionId);
        }
        securityPermission.remove();
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
   *  Finds SecurityPermission records by the following fieldters:
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
      collection = (Collection) MyNarrow.narrow(securityPermissionHome.findByPermissionId(permissionId), Collection.class);
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
