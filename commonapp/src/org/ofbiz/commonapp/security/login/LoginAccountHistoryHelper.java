
package org.ofbiz.commonapp.security.login;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Login Account History Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the LoginAccountHistory Entity EJB; acts as a proxy for the Home interface
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
 *@created    Wed Jul 18 12:02:50 MDT 2001
 *@version    1.0
 */
public class LoginAccountHistoryHelper
{

  /** A static variable to cache the Home object for the LoginAccountHistory EJB */
  private static LoginAccountHistoryHome loginAccountHistoryHome = null;

  /** Initializes the loginAccountHistoryHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The LoginAccountHistoryHome instance for the default EJB server
   */
  public static LoginAccountHistoryHome getLoginAccountHistoryHome()
  {
    if(loginAccountHistoryHome == null) //don't want to block here
    {
      synchronized(LoginAccountHistoryHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(loginAccountHistoryHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.login.LoginAccountHistoryHome");
            loginAccountHistoryHome = (LoginAccountHistoryHome)MyNarrow.narrow(homeObject, LoginAccountHistoryHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("loginAccountHistory home obtained " + loginAccountHistoryHome);
        }
      }
    }
    return loginAccountHistoryHome;
  }



  /** Remove the LoginAccountHistory corresponding to the primaryKey specified by fields
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   */
  public static void removeByPrimaryKey(String userLoginId, String userLoginSeqId)
  {
    if(userLoginId == null || userLoginSeqId == null)
    {
      return;
    }
    LoginAccountHistoryPK primaryKey = new LoginAccountHistoryPK(userLoginId, userLoginSeqId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the LoginAccountHistory corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.login.LoginAccountHistoryPK primaryKey)
  {
    if(primaryKey == null) return;
    LoginAccountHistory loginAccountHistory = findByPrimaryKey(primaryKey);
    try
    {
      if(loginAccountHistory != null)
      {
        loginAccountHistory.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a LoginAccountHistory by its Primary Key, specified by individual fields
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@return       The LoginAccountHistory corresponding to the primaryKey
   */
  public static LoginAccountHistory findByPrimaryKey(String userLoginId, String userLoginSeqId)
  {
    if(userLoginId == null || userLoginSeqId == null) return null;
    LoginAccountHistoryPK primaryKey = new LoginAccountHistoryPK(userLoginId, userLoginSeqId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a LoginAccountHistory by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The LoginAccountHistory corresponding to the primaryKey
   */
  public static LoginAccountHistory findByPrimaryKey(org.ofbiz.commonapp.security.login.LoginAccountHistoryPK primaryKey)
  {
    LoginAccountHistory loginAccountHistory = null;
    Debug.logInfo("LoginAccountHistoryHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      loginAccountHistory = (LoginAccountHistory)MyNarrow.narrow(getLoginAccountHistoryHome().findByPrimaryKey(primaryKey), LoginAccountHistory.class);
      if(loginAccountHistory != null)
      {
        loginAccountHistory = loginAccountHistory.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return loginAccountHistory;
  }

  /** Finds all LoginAccountHistory entities, returning an Iterator
   *@return    Iterator containing all LoginAccountHistory entities
   */
  public static Iterator findAllIterator()
  {
    Collection collection = findAll();
    if(collection != null) return collection.iterator();
    else return null;
  }

  /** Finds all LoginAccountHistory entities
   *@return    Collection containing all LoginAccountHistory entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("LoginAccountHistoryHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getLoginAccountHistoryHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a LoginAccountHistory
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  userId                  Field of the USER_ID column.
   *@param  password                  Field of the PASSWORD column.
   *@return                Description of the Returned Value
   */
  public static LoginAccountHistory create(String userLoginId, String userLoginSeqId, java.util.Date fromDate, java.util.Date thruDate, String partyId, String userId, String password)
  {
    LoginAccountHistory loginAccountHistory = null;
    Debug.logInfo("LoginAccountHistoryHelper.create: userLoginId, userLoginSeqId: " + userLoginId + ", " + userLoginSeqId);
    if(userLoginId == null || userLoginSeqId == null) { return null; }

    try { loginAccountHistory = (LoginAccountHistory)MyNarrow.narrow(getLoginAccountHistoryHome().create(userLoginId, userLoginSeqId, fromDate, thruDate, partyId, userId, password), LoginAccountHistory.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create loginAccountHistory with userLoginId, userLoginSeqId: " + userLoginId + ", " + userLoginSeqId);
      Debug.logError(ce);
      loginAccountHistory = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return loginAccountHistory;
  }

  /** Updates the corresponding LoginAccountHistory
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  userId                  Field of the USER_ID column.
   *@param  password                  Field of the PASSWORD column.
   *@return                Description of the Returned Value
   */
  public static LoginAccountHistory update(String userLoginId, String userLoginSeqId, java.util.Date fromDate, java.util.Date thruDate, String partyId, String userId, String password) throws java.rmi.RemoteException
  {
    if(userLoginId == null || userLoginSeqId == null) { return null; }
    LoginAccountHistory loginAccountHistory = findByPrimaryKey(userLoginId, userLoginSeqId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    LoginAccountHistory loginAccountHistoryValue = new LoginAccountHistoryValue();

    if(fromDate != null) { loginAccountHistoryValue.setFromDate(fromDate); }
    if(thruDate != null) { loginAccountHistoryValue.setThruDate(thruDate); }
    if(partyId != null) { loginAccountHistoryValue.setPartyId(partyId); }
    if(userId != null) { loginAccountHistoryValue.setUserId(userId); }
    if(password != null) { loginAccountHistoryValue.setPassword(password); }

    loginAccountHistory.setValueObject(loginAccountHistoryValue);
    return loginAccountHistory;
  }

  /** Removes/deletes the specified  LoginAccountHistory
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   */
  public static void removeByUserLoginId(String userLoginId)
  {
    if(userLoginId == null) return;
    Iterator iterator = findByUserLoginIdIterator(userLoginId);

    while(iterator.hasNext())
    {
      try
      {
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory) iterator.next();
        Debug.logInfo("Removing loginAccountHistory with userLoginId:" + userLoginId);
        loginAccountHistory.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUserLoginIdIterator(String userLoginId)
  {
    Collection collection = findByUserLoginId(userLoginId);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds LoginAccountHistory records by the following parameters:
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUserLoginId(String userLoginId)
  {
    Debug.logInfo("findByUserLoginId: userLoginId:" + userLoginId);

    Collection collection = null;
    if(userLoginId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getLoginAccountHistoryHome().findByUserLoginId(userLoginId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  LoginAccountHistory
   *@param  userId                  Field of the USER_ID column.
   */
  public static void removeByUserId(String userId)
  {
    if(userId == null) return;
    Iterator iterator = findByUserIdIterator(userId);

    while(iterator.hasNext())
    {
      try
      {
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory) iterator.next();
        Debug.logInfo("Removing loginAccountHistory with userId:" + userId);
        loginAccountHistory.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  userId                  Field of the USER_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUserIdIterator(String userId)
  {
    Collection collection = findByUserId(userId);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds LoginAccountHistory records by the following parameters:
   *@param  userId                  Field of the USER_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUserId(String userId)
  {
    Debug.logInfo("findByUserId: userId:" + userId);

    Collection collection = null;
    if(userId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getLoginAccountHistoryHome().findByUserId(userId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  LoginAccountHistory
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
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory) iterator.next();
        Debug.logInfo("Removing loginAccountHistory with partyId:" + partyId);
        loginAccountHistory.remove();
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

  /** Finds LoginAccountHistory records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getLoginAccountHistoryHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
