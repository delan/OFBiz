
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Security Component - Security Group Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the SecurityGroup Entity EJB; acts as a proxy for the Home interface
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
 *@created    Tue Jul 03 01:11:49 MDT 2001
 *@version    1.0
 */
public class SecurityGroupHelper
{

  /**
   *  A static variable to cache the Home object for the SecurityGroup EJB
   */
  public static SecurityGroupHome securityGroupHome = null;

  /**
   *  Initializes the securityGroupHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(securityGroupHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.SecurityGroupHome");
        securityGroupHome = (SecurityGroupHome)MyNarrow.narrow(homeObject, SecurityGroupHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("securityGroup home obtained " + securityGroupHome);
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
    SecurityGroup securityGroup = findByPrimaryKey(primaryKey);
    try
    {
      if(securityGroup != null)
      {
        securityGroup.remove();
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
   *@return             The SecurityGroup of primaryKey
   */
  public static SecurityGroup findByPrimaryKey(java.lang.String primaryKey)
  {
    SecurityGroup securityGroup = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("SecurityGroupHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      securityGroup = (SecurityGroup)MyNarrow.narrow(securityGroupHome.findByPrimaryKey(primaryKey), SecurityGroup.class);
      if(securityGroup != null)
      {
        securityGroup = securityGroup.getValueObject();

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
    return securityGroup;
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
      System.out.println("SecurityGroupHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(securityGroupHome.findAll(), Collection.class);
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
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroup create(String groupId, String description)
  {
    SecurityGroup securityGroup = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("SecurityGroupHelper.create: groupId: " + groupId);
    }
    if(groupId == null)
    {
      return null;
    }
    init();

    try
    {
      securityGroup = (SecurityGroup)MyNarrow.narrow(securityGroupHome.create(groupId, description), SecurityGroup.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create securityGroup with groupId: " + groupId);
        ce.printStackTrace();
      }
      securityGroup = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return securityGroup;
  }

  /**
   *  Description of the Method
   *

   *@param  groupId                  Field of the GROUP_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroup update(String groupId, String description) throws java.rmi.RemoteException
  {
    if(groupId == null)
    {
      return null;
    }
    SecurityGroup securityGroup = findByPrimaryKey(groupId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SecurityGroup securityGroupValue = new SecurityGroupValue();


  
  
    if(description != null)
    {
      securityGroupValue.setDescription(description);
    }

    securityGroup.setValueObject(securityGroupValue);
    return securityGroup;
  }




}
