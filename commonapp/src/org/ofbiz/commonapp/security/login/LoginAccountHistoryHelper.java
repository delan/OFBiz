
package org.ofbiz.commonapp.security.login;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

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
 *@created    Tue Jul 03 01:11:48 MDT 2001
 *@version    1.0
 */
public class LoginAccountHistoryHelper
{

  /**
   *  A static variable to cache the Home object for the LoginAccountHistory EJB
   */
  public static LoginAccountHistoryHome loginAccountHistoryHome = null;

  /**
   *  Initializes the loginAccountHistoryHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(loginAccountHistoryHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.security.login.LoginAccountHistoryHome");
        loginAccountHistoryHome = (LoginAccountHistoryHome)MyNarrow.narrow(homeObject, LoginAccountHistoryHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("loginAccountHistory home obtained " + loginAccountHistoryHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

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


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.security.login.LoginAccountHistoryPK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    LoginAccountHistory loginAccountHistory = findByPrimaryKey(primaryKey);
    try
    {
      if(loginAccountHistory != null)
      {
        loginAccountHistory.remove();
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

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@return       Description of the Returned Value
   */
  public static LoginAccountHistory findByPrimaryKey(String userLoginId, String userLoginSeqId)
  {
    if(userLoginId == null || userLoginSeqId == null)
    {
      return null;
    }
    LoginAccountHistoryPK primaryKey = new LoginAccountHistoryPK(userLoginId, userLoginSeqId);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The LoginAccountHistory of primaryKey
   */
  public static LoginAccountHistory findByPrimaryKey(org.ofbiz.commonapp.security.login.LoginAccountHistoryPK primaryKey)
  {
    LoginAccountHistory loginAccountHistory = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("LoginAccountHistoryHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      loginAccountHistory = (LoginAccountHistory)MyNarrow.narrow(loginAccountHistoryHome.findByPrimaryKey(primaryKey), LoginAccountHistory.class);
      if(loginAccountHistory != null)
      {
        loginAccountHistory = loginAccountHistory.getValueObject();

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
    return loginAccountHistory;
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
      System.out.println("LoginAccountHistoryHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(loginAccountHistoryHome.findAll(), Collection.class);
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
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("LoginAccountHistoryHelper.create: userLoginId, userLoginSeqId: " + userLoginId + ", " + userLoginSeqId);
    }
    if(userLoginId == null || userLoginSeqId == null)
    {
      return null;
    }
    init();

    try
    {
      loginAccountHistory = (LoginAccountHistory)MyNarrow.narrow(loginAccountHistoryHome.create(userLoginId, userLoginSeqId, fromDate, thruDate, partyId, userId, password), LoginAccountHistory.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create loginAccountHistory with userLoginId, userLoginSeqId: " + userLoginId + ", " + userLoginSeqId);
        ce.printStackTrace();
      }
      loginAccountHistory = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return loginAccountHistory;
  }

  /**
   *  Description of the Method
   *

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
    if(userLoginId == null || userLoginSeqId == null)
    {
      return null;
    }
    LoginAccountHistory loginAccountHistory = findByPrimaryKey(userLoginId, userLoginSeqId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    LoginAccountHistory loginAccountHistoryValue = new LoginAccountHistoryValue();


  
  
  
    if(fromDate != null)
    {
      loginAccountHistoryValue.setFromDate(fromDate);
    }
  
    if(thruDate != null)
    {
      loginAccountHistoryValue.setThruDate(thruDate);
    }
  
    if(partyId != null)
    {
      loginAccountHistoryValue.setPartyId(partyId);
    }
  
    if(userId != null)
    {
      loginAccountHistoryValue.setUserId(userId);
    }
  
    if(password != null)
    {
      loginAccountHistoryValue.setPassword(password);
    }

    loginAccountHistory.setValueObject(loginAccountHistoryValue);
    return loginAccountHistory;
  }


  
  /**
   *  Description of the Method
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   */
  public static void removeByUserLoginId(String userLoginId)
  {
    if(userLoginId == null)
    {
      return;
    }
    Iterator iterator = findByUserLoginIdIterator(userLoginId);

    while(iterator.hasNext())
    {
      try
      {
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing loginAccountHistory with userLoginId:" + userLoginId);
        }
        loginAccountHistory.remove();
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

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUserLoginIdIterator(String userLoginId)
  {
    Collection collection = findByUserLoginId(userLoginId);
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
   *  Finds LoginAccountHistory records by the following fieldters:
   *

   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUserLoginId(String userLoginId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByUserLoginId: userLoginId:" + userLoginId);
    }

    Collection collection = null;
    if(userLoginId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(loginAccountHistoryHome.findByUserLoginId(userLoginId), Collection.class);
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

   *@param  userId                  Field of the USER_ID column.
   */
  public static void removeByUserId(String userId)
  {
    if(userId == null)
    {
      return;
    }
    Iterator iterator = findByUserIdIterator(userId);

    while(iterator.hasNext())
    {
      try
      {
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing loginAccountHistory with userId:" + userId);
        }
        loginAccountHistory.remove();
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

   *@param  userId                  Field of the USER_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUserIdIterator(String userId)
  {
    Collection collection = findByUserId(userId);
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
   *  Finds LoginAccountHistory records by the following fieldters:
   *

   *@param  userId                  Field of the USER_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUserId(String userId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByUserId: userId:" + userId);
    }

    Collection collection = null;
    if(userId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(loginAccountHistoryHome.findByUserId(userId), Collection.class);
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

   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null)
    {
      return;
    }
    Iterator iterator = findByPartyIdIterator(partyId);

    while(iterator.hasNext())
    {
      try
      {
        LoginAccountHistory loginAccountHistory = (LoginAccountHistory) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing loginAccountHistory with partyId:" + partyId);
        }
        loginAccountHistory.remove();
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

   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByPartyIdIterator(String partyId)
  {
    Collection collection = findByPartyId(partyId);
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
   *  Finds LoginAccountHistory records by the following fieldters:
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByPartyId: partyId:" + partyId);
    }

    Collection collection = null;
    if(partyId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(loginAccountHistoryHome.findByPartyId(partyId), Collection.class);
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
