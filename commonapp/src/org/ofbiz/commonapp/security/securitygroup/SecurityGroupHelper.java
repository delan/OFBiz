
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class SecurityGroupHelper
{

  /** A static variable to cache the Home object for the SecurityGroup EJB */
  private static SecurityGroupHome securityGroupHome = null;

  /** Initializes the securityGroupHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SecurityGroupHome instance for the default EJB server
   */
  public static SecurityGroupHome getSecurityGroupHome()
  {
    if(securityGroupHome == null) //don't want to block here
    {
      synchronized(SecurityGroupHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(securityGroupHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.SecurityGroupHome");
            securityGroupHome = (SecurityGroupHome)MyNarrow.narrow(homeObject, SecurityGroupHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("securityGroup home obtained " + securityGroupHome);
        }
      }
    }
    return securityGroupHome;
  }




  /** Remove the SecurityGroup corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    SecurityGroup securityGroup = findByPrimaryKey(primaryKey);
    try
    {
      if(securityGroup != null)
      {
        securityGroup.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a SecurityGroup by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SecurityGroup corresponding to the primaryKey
   */
  public static SecurityGroup findByPrimaryKey(java.lang.String primaryKey)
  {
    SecurityGroup securityGroup = null;
    Debug.logInfo("SecurityGroupHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      securityGroup = (SecurityGroup)MyNarrow.narrow(getSecurityGroupHome().findByPrimaryKey(primaryKey), SecurityGroup.class);
      if(securityGroup != null)
      {
        securityGroup = securityGroup.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return securityGroup;
  }

  /** Finds all SecurityGroup entities
   *@return    Collection containing all SecurityGroup entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SecurityGroupHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSecurityGroupHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SecurityGroup
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroup create(String groupId, String description)
  {
    SecurityGroup securityGroup = null;
    Debug.logInfo("SecurityGroupHelper.create: groupId: " + groupId);
    if(groupId == null) { return null; }

    try { securityGroup = (SecurityGroup)MyNarrow.narrow(getSecurityGroupHome().create(groupId, description), SecurityGroup.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create securityGroup with groupId: " + groupId);
      Debug.logError(ce);
      securityGroup = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return securityGroup;
  }

  /** Updates the corresponding SecurityGroup
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroup update(String groupId, String description) throws java.rmi.RemoteException
  {
    if(groupId == null) { return null; }
    SecurityGroup securityGroup = findByPrimaryKey(groupId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SecurityGroup securityGroupValue = new SecurityGroupValue();

    if(description != null) { securityGroupValue.setDescription(description); }

    securityGroup.setValueObject(securityGroupValue);
    return securityGroup;
  }


}
