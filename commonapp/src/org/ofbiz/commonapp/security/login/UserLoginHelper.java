
package org.ofbiz.commonapp.security.login;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> User Login Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the UserLogin Entity EJB; acts as a proxy for the Home interface
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
 *@created    Wed Jul 18 12:02:49 MDT 2001
 *@version    1.0
 */
public class UserLoginHelper
{

  /** A static variable to cache the Home object for the UserLogin EJB */
  private static UserLoginHome userLoginHome = null;

  /** Initializes the userLoginHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The UserLoginHome instance for the default EJB server
   */
  public static UserLoginHome getUserLoginHome()
  {
    if(userLoginHome == null) //don't want to block here
    {
      synchronized(UserLoginHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(userLoginHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.login.UserLoginHome");
            userLoginHome = (UserLoginHome)MyNarrow.narrow(homeObject, UserLoginHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("userLogin home obtained " + userLoginHome);
        }
      }
    }
    return userLoginHome;
  }




  /** Remove the UserLogin corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    UserLogin userLogin = findByPrimaryKey(primaryKey);
    try
    {
      if(userLogin != null)
      {
        userLogin.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a UserLogin by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The UserLogin corresponding to the primaryKey
   */
  public static UserLogin findByPrimaryKey(java.lang.String primaryKey)
  {
    UserLogin userLogin = null;
    Debug.logInfo("UserLoginHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      userLogin = (UserLogin)MyNarrow.narrow(getUserLoginHome().findByPrimaryKey(primaryKey), UserLogin.class);
      if(userLogin != null)
      {
        userLogin = userLogin.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return userLogin;
  }

  /** Finds all UserLogin entities, returning an Iterator
   *@return    Iterator containing all UserLogin entities
   */
  public static Iterator findAllIterator()
  {
    Collection collection = findAll();
    if(collection != null) return collection.iterator();
    else return null;
  }

  /** Finds all UserLogin entities
   *@return    Collection containing all UserLogin entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("UserLoginHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getUserLoginHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a UserLogin
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@param  currentUserId                  Field of the CURRENT_USER_ID column.
   *@param  currentPassword                  Field of the CURRENT_PASSWORD column.
   *@return                Description of the Returned Value
   */
  public static UserLogin create(String userLoginId, String partyId, String contactMechanismId, String currentUserId, String currentPassword)
  {
    UserLogin userLogin = null;
    Debug.logInfo("UserLoginHelper.create: userLoginId: " + userLoginId);
    if(userLoginId == null) { return null; }

    try { userLogin = (UserLogin)MyNarrow.narrow(getUserLoginHome().create(userLoginId, partyId, contactMechanismId, currentUserId, currentPassword), UserLogin.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create userLogin with userLoginId: " + userLoginId);
      Debug.logError(ce);
      userLogin = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return userLogin;
  }

  /** Updates the corresponding UserLogin
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@param  currentUserId                  Field of the CURRENT_USER_ID column.
   *@param  currentPassword                  Field of the CURRENT_PASSWORD column.
   *@return                Description of the Returned Value
   */
  public static UserLogin update(String userLoginId, String partyId, String contactMechanismId, String currentUserId, String currentPassword) throws java.rmi.RemoteException
  {
    if(userLoginId == null) { return null; }
    UserLogin userLogin = findByPrimaryKey(userLoginId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    UserLogin userLoginValue = new UserLoginValue();

    if(partyId != null) { userLoginValue.setPartyId(partyId); }
    if(contactMechanismId != null) { userLoginValue.setContactMechanismId(contactMechanismId); }
    if(currentUserId != null) { userLoginValue.setCurrentUserId(currentUserId); }
    if(currentPassword != null) { userLoginValue.setCurrentPassword(currentPassword); }

    userLogin.setValueObject(userLoginValue);
    return userLogin;
  }

  /** Removes/deletes the specified  UserLogin
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null) return;
    Iterator iterator = findByPartyIdIterator(partyId);

    while(iterator.hasNext())
    {
      try
      {
        UserLogin userLogin = (UserLogin) iterator.next();
        Debug.logInfo("Removing userLogin with partyId:" + partyId);
        userLogin.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByPartyIdIterator(String partyId)
  {
    Collection collection = findByPartyId(partyId);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds UserLogin records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUserLoginHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  UserLogin
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   */
  public static void removeByContactMechanismId(String contactMechanismId)
  {
    if(contactMechanismId == null) return;
    Iterator iterator = findByContactMechanismIdIterator(contactMechanismId);

    while(iterator.hasNext())
    {
      try
      {
        UserLogin userLogin = (UserLogin) iterator.next();
        Debug.logInfo("Removing userLogin with contactMechanismId:" + contactMechanismId);
        userLogin.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByContactMechanismIdIterator(String contactMechanismId)
  {
    Collection collection = findByContactMechanismId(contactMechanismId);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds UserLogin records by the following parameters:
   *@param  contactMechanismId                  Field of the CONTACT_MECHANISM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByContactMechanismId(String contactMechanismId)
  {
    Debug.logInfo("findByContactMechanismId: contactMechanismId:" + contactMechanismId);

    Collection collection = null;
    if(contactMechanismId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUserLoginHome().findByContactMechanismId(contactMechanismId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  UserLogin
   *@param  currentUserId                  Field of the CURRENT_USER_ID column.
   */
  public static void removeByCurrentUserId(String currentUserId)
  {
    if(currentUserId == null) return;
    Iterator iterator = findByCurrentUserIdIterator(currentUserId);

    while(iterator.hasNext())
    {
      try
      {
        UserLogin userLogin = (UserLogin) iterator.next();
        Debug.logInfo("Removing userLogin with currentUserId:" + currentUserId);
        userLogin.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  currentUserId                  Field of the CURRENT_USER_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByCurrentUserIdIterator(String currentUserId)
  {
    Collection collection = findByCurrentUserId(currentUserId);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds UserLogin records by the following parameters:
   *@param  currentUserId                  Field of the CURRENT_USER_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByCurrentUserId(String currentUserId)
  {
    Debug.logInfo("findByCurrentUserId: currentUserId:" + currentUserId);

    Collection collection = null;
    if(currentUserId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUserLoginHome().findByCurrentUserId(currentUserId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
