
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.party.party.*;

/**
 * <p><b>Title:</b> Login Account History Entity
 * <p><b>Description:</b> None
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
public class LoginAccountHistoryBean implements EntityBean
{
  /** The variable for the USER_LOGIN_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String userLoginId;
  /** The variable for the USER_LOGIN_SEQ_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String userLoginSeqId;
  /** The variable for the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date fromDate;
  /** The variable for the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date thruDate;
  /** The variable for the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String partyId;
  /** The variable for the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String userId;
  /** The variable for the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public String password;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the LoginAccountHistoryBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key USER_LOGIN_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserLoginId() { return userLoginId; }

  /** Get the primary key USER_LOGIN_SEQ_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserLoginSeqId() { return userLoginSeqId; }

  /** Get the value of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date getFromDate() { return fromDate; }
  /** Set the value of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
    ejbIsModified = true;
  }

  /** Get the value of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date getThruDate() { return thruDate; }
  /** Set the value of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
    ejbIsModified = true;
  }

  /** Get the value of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getPartyId() { return partyId; }
  /** Set the value of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setPartyId(String partyId)
  {
    this.partyId = partyId;
    ejbIsModified = true;
  }

  /** Get the value of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserId() { return userId; }
  /** Set the value of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setUserId(String userId)
  {
    this.userId = userId;
    ejbIsModified = true;
  }

  /** Get the value of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getPassword() { return password; }
  /** Set the value of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setPassword(String password)
  {
    this.password = password;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the LoginAccountHistoryBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(LoginAccountHistory valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getFromDate() != null)
      {
        this.fromDate = valueObject.getFromDate();
        ejbIsModified = true;
      }
      if(valueObject.getThruDate() != null)
      {
        this.thruDate = valueObject.getThruDate();
        ejbIsModified = true;
      }
      if(valueObject.getPartyId() != null)
      {
        this.partyId = valueObject.getPartyId();
        ejbIsModified = true;
      }
      if(valueObject.getUserId() != null)
      {
        this.userId = valueObject.getUserId();
        ejbIsModified = true;
      }
      if(valueObject.getPassword() != null)
      {
        this.password = valueObject.getPassword();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the LoginAccountHistoryBean object
   *@return    The ValueObject value
   */
  public LoginAccountHistory getValueObject()
  {
    if(this.entityContext != null)
    {
      return new LoginAccountHistoryValue((LoginAccountHistory)this.entityContext.getEJBObject(), userLoginId, userLoginSeqId, fromDate, thruDate, partyId, userId, password);
    }
    else { return null; }
  }


  /** Get the  UserLogin entity corresponding to this entity. */
  public UserLogin getUserLogin() { return UserLoginHelper.findByPrimaryKey(userLoginId); }
  /** Remove the  UserLogin entity corresponding to this entity. */
  public void removeUserLogin() { UserLoginHelper.removeByPrimaryKey(userLoginId); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }


  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  userId                  Field of the USER_ID column.
   *@param  password                  Field of the PASSWORD column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.security.login.LoginAccountHistoryPK ejbCreate(String userLoginId, String userLoginSeqId, java.util.Date fromDate, java.util.Date thruDate, String partyId, String userId, String password) throws CreateException
  {
    this.userLoginId = userLoginId;
    this.userLoginSeqId = userLoginSeqId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.partyId = partyId;
    this.userId = userId;
    this.password = password;
    return null;
  }

  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.security.login.LoginAccountHistoryPK ejbCreate(String userLoginId, String userLoginSeqId) throws CreateException
  {
    return ejbCreate(userLoginId, userLoginSeqId, null, null, null, null, null);
  }

  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  userId                  Field of the USER_ID column.
   *@param  password                  Field of the PASSWORD column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String userLoginId, String userLoginSeqId, java.util.Date fromDate, java.util.Date thruDate, String partyId, String userId, String password) throws CreateException {}

  /** Description of the Method
   *@param  userLoginId                  Field of the USER_LOGIN_ID column.
   *@param  userLoginSeqId                  Field of the USER_LOGIN_SEQ_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String userLoginId, String userLoginSeqId) throws CreateException
  {
    ejbPostCreate(userLoginId, userLoginSeqId, null, null, null, null, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
