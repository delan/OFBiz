
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

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
 *@created    Fri Jul 27 01:18:35 MDT 2001
 *@version    1.0
 */
public class SecurityGroupPermissionHelper
{

  /** A static variable to cache the Home object for the SecurityGroupPermission EJB */
  private static SecurityGroupPermissionHome securityGroupPermissionHome = null;

  /** Initializes the securityGroupPermissionHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SecurityGroupPermissionHome instance for the default EJB server
   */
  public static SecurityGroupPermissionHome getSecurityGroupPermissionHome()
  {
    if(securityGroupPermissionHome == null) //don't want to block here
    {
      synchronized(SecurityGroupPermissionHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(securityGroupPermissionHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionHome");
            securityGroupPermissionHome = (SecurityGroupPermissionHome)MyNarrow.narrow(homeObject, SecurityGroupPermissionHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("securityGroupPermission home obtained " + securityGroupPermissionHome);
        }
      }
    }
    return securityGroupPermissionHome;
  }



  /** Remove the SecurityGroupPermission corresponding to the primaryKey specified by fields
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

  /** Remove the SecurityGroupPermission corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionPK primaryKey)
  {
    if(primaryKey == null) return;
    SecurityGroupPermission securityGroupPermission = findByPrimaryKey(primaryKey);
    try
    {
      if(securityGroupPermission != null)
      {
        securityGroupPermission.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a SecurityGroupPermission by its Primary Key, specified by individual fields
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return       The SecurityGroupPermission corresponding to the primaryKey
   */
  public static SecurityGroupPermission findByPrimaryKey(String groupId, String permissionId)
  {
    if(groupId == null || permissionId == null) return null;
    SecurityGroupPermissionPK primaryKey = new SecurityGroupPermissionPK(groupId, permissionId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a SecurityGroupPermission by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SecurityGroupPermission corresponding to the primaryKey
   */
  public static SecurityGroupPermission findByPrimaryKey(org.ofbiz.commonapp.security.securitygroup.SecurityGroupPermissionPK primaryKey)
  {
    SecurityGroupPermission securityGroupPermission = null;
    Debug.logInfo("SecurityGroupPermissionHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      securityGroupPermission = (SecurityGroupPermission)MyNarrow.narrow(getSecurityGroupPermissionHome().findByPrimaryKey(primaryKey), SecurityGroupPermission.class);
      if(securityGroupPermission != null)
      {
        securityGroupPermission = securityGroupPermission.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return securityGroupPermission;
  }

  /** Finds all SecurityGroupPermission entities
   *@return    Collection containing all SecurityGroupPermission entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SecurityGroupPermissionHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSecurityGroupPermissionHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SecurityGroupPermission
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroupPermission create(String groupId, String permissionId)
  {
    SecurityGroupPermission securityGroupPermission = null;
    Debug.logInfo("SecurityGroupPermissionHelper.create: groupId, permissionId: " + groupId + ", " + permissionId);
    if(groupId == null || permissionId == null) { return null; }

    try { securityGroupPermission = (SecurityGroupPermission)MyNarrow.narrow(getSecurityGroupPermissionHome().create(groupId, permissionId), SecurityGroupPermission.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create securityGroupPermission with groupId, permissionId: " + groupId + ", " + permissionId);
      Debug.logError(ce);
      securityGroupPermission = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return securityGroupPermission;
  }

  /** Updates the corresponding SecurityGroupPermission
   *@param  groupId                  Field of the GROUP_ID column.
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return                Description of the Returned Value
   */
  public static SecurityGroupPermission update(String groupId, String permissionId) throws java.rmi.RemoteException
  {
    if(groupId == null || permissionId == null) { return null; }
    SecurityGroupPermission securityGroupPermission = findByPrimaryKey(groupId, permissionId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SecurityGroupPermission securityGroupPermissionValue = new SecurityGroupPermissionValue();


    securityGroupPermission.setValueObject(securityGroupPermissionValue);
    return securityGroupPermission;
  }

  /** Removes/deletes the specified  SecurityGroupPermission
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
        SecurityGroupPermission securityGroupPermission = (SecurityGroupPermission) iterator.next();
        Debug.logInfo("Removing securityGroupPermission with groupId:" + groupId);
        securityGroupPermission.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SecurityGroupPermission records by the following parameters:
   *@param  groupId                  Field of the GROUP_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByGroupId(String groupId)
  {
    Debug.logInfo("findByGroupId: groupId:" + groupId);

    Collection collection = null;
    if(groupId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSecurityGroupPermissionHome().findByGroupId(groupId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  SecurityGroupPermission
   *@param  permissionId                  Field of the PERMISSION_ID column.
   */
  public static void removeByPermissionId(String permissionId)
  {
    if(permissionId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPermissionId(permissionId));

    while(iterator.hasNext())
    {
      try
      {
        SecurityGroupPermission securityGroupPermission = (SecurityGroupPermission) iterator.next();
        Debug.logInfo("Removing securityGroupPermission with permissionId:" + permissionId);
        securityGroupPermission.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SecurityGroupPermission records by the following parameters:
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPermissionId(String permissionId)
  {
    Debug.logInfo("findByPermissionId: permissionId:" + permissionId);

    Collection collection = null;
    if(permissionId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSecurityGroupPermissionHome().findByPermissionId(permissionId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
