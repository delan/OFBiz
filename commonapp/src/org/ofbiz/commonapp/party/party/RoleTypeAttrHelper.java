
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Role Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the RoleTypeAttr Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */
public class RoleTypeAttrHelper
{

  /** A static variable to cache the Home object for the RoleTypeAttr EJB */
  private static RoleTypeAttrHome roleTypeAttrHome = null;

  /** Initializes the roleTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The RoleTypeAttrHome instance for the default EJB server
   */
  public static RoleTypeAttrHome getRoleTypeAttrHome()
  {
    if(roleTypeAttrHome == null) //don't want to block here
    {
      synchronized(RoleTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(roleTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.RoleTypeAttrHome");
            roleTypeAttrHome = (RoleTypeAttrHome)MyNarrow.narrow(homeObject, RoleTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("roleTypeAttr home obtained " + roleTypeAttrHome);
        }
      }
    }
    return roleTypeAttrHome;
  }



  /** Remove the RoleTypeAttr corresponding to the primaryKey specified by fields
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String roleTypeId, String name)
  {
    if(roleTypeId == null || name == null)
    {
      return;
    }
    RoleTypeAttrPK primaryKey = new RoleTypeAttrPK(roleTypeId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the RoleTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.party.party.RoleTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    RoleTypeAttr roleTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(roleTypeAttr != null)
      {
        roleTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a RoleTypeAttr by its Primary Key, specified by individual fields
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The RoleTypeAttr corresponding to the primaryKey
   */
  public static RoleTypeAttr findByPrimaryKey(String roleTypeId, String name)
  {
    if(roleTypeId == null || name == null) return null;
    RoleTypeAttrPK primaryKey = new RoleTypeAttrPK(roleTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a RoleTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The RoleTypeAttr corresponding to the primaryKey
   */
  public static RoleTypeAttr findByPrimaryKey(org.ofbiz.commonapp.party.party.RoleTypeAttrPK primaryKey)
  {
    RoleTypeAttr roleTypeAttr = null;
    Debug.logInfo("RoleTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      roleTypeAttr = (RoleTypeAttr)MyNarrow.narrow(getRoleTypeAttrHome().findByPrimaryKey(primaryKey), RoleTypeAttr.class);
      if(roleTypeAttr != null)
      {
        roleTypeAttr = roleTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return roleTypeAttr;
  }

  /** Finds all RoleTypeAttr entities
   *@return    Collection containing all RoleTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("RoleTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getRoleTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a RoleTypeAttr
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static RoleTypeAttr create(String roleTypeId, String name)
  {
    RoleTypeAttr roleTypeAttr = null;
    Debug.logInfo("RoleTypeAttrHelper.create: roleTypeId, name: " + roleTypeId + ", " + name);
    if(roleTypeId == null || name == null) { return null; }

    try { roleTypeAttr = (RoleTypeAttr)MyNarrow.narrow(getRoleTypeAttrHome().create(roleTypeId, name), RoleTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create roleTypeAttr with roleTypeId, name: " + roleTypeId + ", " + name);
      Debug.logError(ce);
      roleTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return roleTypeAttr;
  }

  /** Updates the corresponding RoleTypeAttr
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static RoleTypeAttr update(String roleTypeId, String name) throws java.rmi.RemoteException
  {
    if(roleTypeId == null || name == null) { return null; }
    RoleTypeAttr roleTypeAttr = findByPrimaryKey(roleTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    RoleTypeAttr roleTypeAttrValue = new RoleTypeAttrValue();


    roleTypeAttr.setValueObject(roleTypeAttrValue);
    return roleTypeAttr;
  }

  /** Removes/deletes the specified  RoleTypeAttr
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   */
  public static void removeByRoleTypeId(String roleTypeId)
  {
    if(roleTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByRoleTypeId(roleTypeId));

    while(iterator.hasNext())
    {
      try
      {
        RoleTypeAttr roleTypeAttr = (RoleTypeAttr) iterator.next();
        Debug.logInfo("Removing roleTypeAttr with roleTypeId:" + roleTypeId);
        roleTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds RoleTypeAttr records by the following parameters:
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByRoleTypeId(String roleTypeId)
  {
    Debug.logInfo("findByRoleTypeId: roleTypeId:" + roleTypeId);

    Collection collection = null;
    if(roleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getRoleTypeAttrHome().findByRoleTypeId(roleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  RoleTypeAttr
   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null) return;
    Iterator iterator = UtilMisc.toIterator(findByName(name));

    while(iterator.hasNext())
    {
      try
      {
        RoleTypeAttr roleTypeAttr = (RoleTypeAttr) iterator.next();
        Debug.logInfo("Removing roleTypeAttr with name:" + name);
        roleTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds RoleTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getRoleTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
