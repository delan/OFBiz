
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class LoginAccountHistoryValue implements LoginAccountHistory
{
  /** The variable of the USER_LOGIN_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  private String userLoginId;
  /** The variable of the USER_LOGIN_SEQ_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  private String userLoginSeqId;
  /** The variable of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  private java.util.Date thruDate;
  /** The variable of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  private String partyId;
  /** The variable of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  private String userId;
  /** The variable of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  private String password;

  private LoginAccountHistory loginAccountHistory;

  public LoginAccountHistoryValue()
  {
    this.userLoginId = null;
    this.userLoginSeqId = null;
    this.fromDate = null;
    this.thruDate = null;
    this.partyId = null;
    this.userId = null;
    this.password = null;

    this.loginAccountHistory = null;
  }

  public LoginAccountHistoryValue(LoginAccountHistory loginAccountHistory) throws RemoteException
  {
    if(loginAccountHistory == null) return;
  
    this.userLoginId = loginAccountHistory.getUserLoginId();
    this.userLoginSeqId = loginAccountHistory.getUserLoginSeqId();
    this.fromDate = loginAccountHistory.getFromDate();
    this.thruDate = loginAccountHistory.getThruDate();
    this.partyId = loginAccountHistory.getPartyId();
    this.userId = loginAccountHistory.getUserId();
    this.password = loginAccountHistory.getPassword();

    this.loginAccountHistory = loginAccountHistory;
  }

  public LoginAccountHistoryValue(LoginAccountHistory loginAccountHistory, String userLoginId, String userLoginSeqId, java.util.Date fromDate, java.util.Date thruDate, String partyId, String userId, String password)
  {
    if(loginAccountHistory == null) return;
  
    this.userLoginId = userLoginId;
    this.userLoginSeqId = userLoginSeqId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    this.partyId = partyId;
    this.userId = userId;
    this.password = password;

    this.loginAccountHistory = loginAccountHistory;
  }


  /** Get the primary key of the USER_LOGIN_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserLoginId()  throws RemoteException { return userLoginId; }

  /** Get the primary key of the USER_LOGIN_SEQ_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserLoginSeqId()  throws RemoteException { return userLoginSeqId; }

  /** Get the value of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(loginAccountHistory!=null) loginAccountHistory.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(loginAccountHistory!=null) loginAccountHistory.setThruDate(thruDate);
  }

  /** Get the value of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(loginAccountHistory!=null) loginAccountHistory.setPartyId(partyId);
  }

  /** Get the value of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserId() throws RemoteException { return userId; }
  /** Set the value of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setUserId(String userId) throws RemoteException
  {
    this.userId = userId;
    if(loginAccountHistory!=null) loginAccountHistory.setUserId(userId);
  }

  /** Get the value of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getPassword() throws RemoteException { return password; }
  /** Set the value of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setPassword(String password) throws RemoteException
  {
    this.password = password;
    if(loginAccountHistory!=null) loginAccountHistory.setPassword(password);
  }

  /** Get the value object of the LoginAccountHistory class. */
  public LoginAccountHistory getValueObject() throws RemoteException { return this; }
  /** Set the value object of the LoginAccountHistory class. */
  public void setValueObject(LoginAccountHistory valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(loginAccountHistory!=null) loginAccountHistory.setValueObject(valueObject);

    if(userLoginId == null) userLoginId = valueObject.getUserLoginId();
    if(userLoginSeqId == null) userLoginSeqId = valueObject.getUserLoginSeqId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
    partyId = valueObject.getPartyId();
    userId = valueObject.getUserId();
    password = valueObject.getPassword();
  }


  /** Get the  UserLogin entity corresponding to this entity. */
  public UserLogin getUserLogin() { return UserLoginHelper.findByPrimaryKey(userLoginId); }
  /** Remove the  UserLogin entity corresponding to this entity. */
  public void removeUserLogin() { UserLoginHelper.removeByPrimaryKey(userLoginId); }

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(loginAccountHistory!=null) return loginAccountHistory.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(loginAccountHistory!=null) return loginAccountHistory.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(loginAccountHistory!=null) return loginAccountHistory.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(loginAccountHistory!=null) return loginAccountHistory.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(loginAccountHistory!=null) loginAccountHistory.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
