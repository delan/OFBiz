
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Role Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyRole Entity EJB; acts as a proxy for the Home interface
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
public class PartyRoleHelper
{

  /** A static variable to cache the Home object for the PartyRole EJB */
  private static PartyRoleHome partyRoleHome = null;

  /** Initializes the partyRoleHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyRoleHome instance for the default EJB server
   */
  public static PartyRoleHome getPartyRoleHome()
  {
    if(partyRoleHome == null) //don't want to block here
    {
      synchronized(PartyRoleHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyRoleHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyRoleHome");
            partyRoleHome = (PartyRoleHome)MyNarrow.narrow(homeObject, PartyRoleHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyRole home obtained " + partyRoleHome);
        }
      }
    }
    return partyRoleHome;
  }



  /** Remove the PartyRole corresponding to the primaryKey specified by fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   */
  public static void removeByPrimaryKey(String partyId, String roleTypeId)
  {
    if(partyId == null || roleTypeId == null)
    {
      return;
    }
    PartyRolePK primaryKey = new PartyRolePK(partyId, roleTypeId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the PartyRole corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.party.party.PartyRolePK primaryKey)
  {
    if(primaryKey == null) return;
    PartyRole partyRole = findByPrimaryKey(primaryKey);
    try
    {
      if(partyRole != null)
      {
        partyRole.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PartyRole by its Primary Key, specified by individual fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@return       The PartyRole corresponding to the primaryKey
   */
  public static PartyRole findByPrimaryKey(String partyId, String roleTypeId)
  {
    if(partyId == null || roleTypeId == null) return null;
    PartyRolePK primaryKey = new PartyRolePK(partyId, roleTypeId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PartyRole by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyRole corresponding to the primaryKey
   */
  public static PartyRole findByPrimaryKey(org.ofbiz.commonapp.party.party.PartyRolePK primaryKey)
  {
    PartyRole partyRole = null;
    Debug.logInfo("PartyRoleHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyRole = (PartyRole)MyNarrow.narrow(getPartyRoleHome().findByPrimaryKey(primaryKey), PartyRole.class);
      if(partyRole != null)
      {
        partyRole = partyRole.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyRole;
  }

  /** Finds all PartyRole entities
   *@return    Collection containing all PartyRole entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyRoleHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyRoleHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyRole
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  partyRoleId                  Field of the PARTY_ROLE_ID column.
   *@return                Description of the Returned Value
   */
  public static PartyRole create(String partyId, String roleTypeId, String partyRoleId)
  {
    PartyRole partyRole = null;
    Debug.logInfo("PartyRoleHelper.create: partyId, roleTypeId: " + partyId + ", " + roleTypeId);
    if(partyId == null || roleTypeId == null) { return null; }

    try { partyRole = (PartyRole)MyNarrow.narrow(getPartyRoleHome().create(partyId, roleTypeId, partyRoleId), PartyRole.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyRole with partyId, roleTypeId: " + partyId + ", " + roleTypeId);
      Debug.logError(ce);
      partyRole = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyRole;
  }

  /** Updates the corresponding PartyRole
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  partyRoleId                  Field of the PARTY_ROLE_ID column.
   *@return                Description of the Returned Value
   */
  public static PartyRole update(String partyId, String roleTypeId, String partyRoleId) throws java.rmi.RemoteException
  {
    if(partyId == null || roleTypeId == null) { return null; }
    PartyRole partyRole = findByPrimaryKey(partyId, roleTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyRole partyRoleValue = new PartyRoleValue();

    if(partyRoleId != null) { partyRoleValue.setPartyRoleId(partyRoleId); }

    partyRole.setValueObject(partyRoleValue);
    return partyRole;
  }

  /** Removes/deletes the specified  PartyRole
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyId(partyId));

    while(iterator.hasNext())
    {
      try
      {
        PartyRole partyRole = (PartyRole) iterator.next();
        Debug.logInfo("Removing partyRole with partyId:" + partyId);
        partyRole.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyRole records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyRoleHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyRole
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
        PartyRole partyRole = (PartyRole) iterator.next();
        Debug.logInfo("Removing partyRole with roleTypeId:" + roleTypeId);
        partyRole.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyRole records by the following parameters:
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByRoleTypeId(String roleTypeId)
  {
    Debug.logInfo("findByRoleTypeId: roleTypeId:" + roleTypeId);

    Collection collection = null;
    if(roleTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyRoleHome().findByRoleTypeId(roleTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyRole
   *@param  partyRoleId                  Field of the PARTY_ROLE_ID column.
   */
  public static void removeByPartyRoleId(String partyRoleId)
  {
    if(partyRoleId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyRoleId(partyRoleId));

    while(iterator.hasNext())
    {
      try
      {
        PartyRole partyRole = (PartyRole) iterator.next();
        Debug.logInfo("Removing partyRole with partyRoleId:" + partyRoleId);
        partyRole.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyRole records by the following parameters:
   *@param  partyRoleId                  Field of the PARTY_ROLE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyRoleId(String partyRoleId)
  {
    Debug.logInfo("findByPartyRoleId: partyRoleId:" + partyRoleId);

    Collection collection = null;
    if(partyRoleId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyRoleHome().findByPartyRoleId(partyRoleId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
