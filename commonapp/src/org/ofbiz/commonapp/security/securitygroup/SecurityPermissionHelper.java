
package org.ofbiz.commonapp.security.securitygroup;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class SecurityPermissionHelper
{

  /** A static variable to cache the Home object for the SecurityPermission EJB */
  private static SecurityPermissionHome securityPermissionHome = null;

  /** Initializes the securityPermissionHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The SecurityPermissionHome instance for the default EJB server
   */
  public static SecurityPermissionHome getSecurityPermissionHome()
  {
    if(securityPermissionHome == null) //don't want to block here
    {
      synchronized(SecurityPermissionHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(securityPermissionHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.securitygroup.SecurityPermissionHome");
            securityPermissionHome = (SecurityPermissionHome)MyNarrow.narrow(homeObject, SecurityPermissionHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("securityPermission home obtained " + securityPermissionHome);
        }
      }
    }
    return securityPermissionHome;
  }




  /** Remove the SecurityPermission corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    SecurityPermission securityPermission = findByPrimaryKey(primaryKey);
    try
    {
      if(securityPermission != null)
      {
        securityPermission.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a SecurityPermission by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The SecurityPermission corresponding to the primaryKey
   */
  public static SecurityPermission findByPrimaryKey(java.lang.String primaryKey)
  {
    SecurityPermission securityPermission = null;
    Debug.logInfo("SecurityPermissionHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      securityPermission = (SecurityPermission)MyNarrow.narrow(getSecurityPermissionHome().findByPrimaryKey(primaryKey), SecurityPermission.class);
      if(securityPermission != null)
      {
        securityPermission = securityPermission.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return securityPermission;
  }

  /** Finds all SecurityPermission entities
   *@return    Collection containing all SecurityPermission entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("SecurityPermissionHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getSecurityPermissionHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a SecurityPermission
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityPermission create(String permissionId, String description)
  {
    SecurityPermission securityPermission = null;
    Debug.logInfo("SecurityPermissionHelper.create: permissionId: " + permissionId);
    if(permissionId == null) { return null; }

    try { securityPermission = (SecurityPermission)MyNarrow.narrow(getSecurityPermissionHome().create(permissionId, description), SecurityPermission.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create securityPermission with permissionId: " + permissionId);
      Debug.logError(ce);
      securityPermission = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return securityPermission;
  }

  /** Updates the corresponding SecurityPermission
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static SecurityPermission update(String permissionId, String description) throws java.rmi.RemoteException
  {
    if(permissionId == null) { return null; }
    SecurityPermission securityPermission = findByPrimaryKey(permissionId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    SecurityPermission securityPermissionValue = new SecurityPermissionValue();

    if(description != null) { securityPermissionValue.setDescription(description); }

    securityPermission.setValueObject(securityPermissionValue);
    return securityPermission;
  }

  /** Removes/deletes the specified  SecurityPermission
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
        SecurityPermission securityPermission = (SecurityPermission) iterator.next();
        Debug.logInfo("Removing securityPermission with permissionId:" + permissionId);
        securityPermission.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds SecurityPermission records by the following parameters:
   *@param  permissionId                  Field of the PERMISSION_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPermissionId(String permissionId)
  {
    Debug.logInfo("findByPermissionId: permissionId:" + permissionId);

    Collection collection = null;
    if(permissionId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getSecurityPermissionHome().findByPermissionId(permissionId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
