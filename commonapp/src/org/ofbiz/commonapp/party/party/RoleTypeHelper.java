
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Role Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the RoleType Entity EJB; acts as a proxy for the Home interface
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
public class RoleTypeHelper
{

  /** A static variable to cache the Home object for the RoleType EJB */
  private static RoleTypeHome roleTypeHome = null;

  /** Initializes the roleTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The RoleTypeHome instance for the default EJB server
   */
  public static RoleTypeHome getRoleTypeHome()
  {
    if(roleTypeHome == null) //don't want to block here
    {
      synchronized(RoleTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(roleTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.RoleTypeHome");
            roleTypeHome = (RoleTypeHome)MyNarrow.narrow(homeObject, RoleTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("roleType home obtained " + roleTypeHome);
        }
      }
    }
    return roleTypeHome;
  }




  /** Remove the RoleType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    RoleType roleType = findByPrimaryKey(primaryKey);
    try
    {
      if(roleType != null)
      {
        roleType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a RoleType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The RoleType corresponding to the primaryKey
   */
  public static RoleType findByPrimaryKey(java.lang.String primaryKey)
  {
    RoleType roleType = null;
    Debug.logInfo("RoleTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      roleType = (RoleType)MyNarrow.narrow(getRoleTypeHome().findByPrimaryKey(primaryKey), RoleType.class);
      if(roleType != null)
      {
        roleType = roleType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return roleType;
  }

  /** Finds all RoleType entities
   *@return    Collection containing all RoleType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("RoleTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getRoleTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a RoleType
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static RoleType create(String roleTypeId, String parentTypeId, String hasTable, String description)
  {
    RoleType roleType = null;
    Debug.logInfo("RoleTypeHelper.create: roleTypeId: " + roleTypeId);
    if(roleTypeId == null) { return null; }

    try { roleType = (RoleType)MyNarrow.narrow(getRoleTypeHome().create(roleTypeId, parentTypeId, hasTable, description), RoleType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create roleType with roleTypeId: " + roleTypeId);
      Debug.logError(ce);
      roleType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return roleType;
  }

  /** Updates the corresponding RoleType
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static RoleType update(String roleTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(roleTypeId == null) { return null; }
    RoleType roleType = findByPrimaryKey(roleTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    RoleType roleTypeValue = new RoleTypeValue();

    if(parentTypeId != null) { roleTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { roleTypeValue.setHasTable(hasTable); }
    if(description != null) { roleTypeValue.setDescription(description); }

    roleType.setValueObject(roleTypeValue);
    return roleType;
  }

  /** Removes/deletes the specified  RoleType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentTypeId(parentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        RoleType roleType = (RoleType) iterator.next();
        Debug.logInfo("Removing roleType with parentTypeId:" + parentTypeId);
        roleType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds RoleType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getRoleTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  RoleType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = UtilMisc.toIterator(findByHasTable(hasTable));

    while(iterator.hasNext())
    {
      try
      {
        RoleType roleType = (RoleType) iterator.next();
        Debug.logInfo("Removing roleType with hasTable:" + hasTable);
        roleType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds RoleType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getRoleTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
