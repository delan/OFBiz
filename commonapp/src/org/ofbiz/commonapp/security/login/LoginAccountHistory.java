
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
 *@created    Tue Jul 17 02:08:31 MDT 2001
 *@version    1.0
 */

public interface LoginAccountHistory extends EJBObject
{
  /** Get the primary key of the USER_LOGIN_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserLoginId() throws RemoteException;
  
  /** Get the primary key of the USER_LOGIN_SEQ_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserLoginSeqId() throws RemoteException;
  
  /** Get the value of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date getFromDate() throws RemoteException;
  /** Set the value of the FROM_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException;
  
  /** Get the value of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public java.util.Date getThruDate() throws RemoteException;
  /** Set the value of the THRU_DATE column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException;
  
  /** Get the value of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getPartyId() throws RemoteException;
  /** Set the value of the PARTY_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setPartyId(String partyId) throws RemoteException;
  
  /** Get the value of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getUserId() throws RemoteException;
  /** Set the value of the USER_ID column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setUserId(String userId) throws RemoteException;
  
  /** Get the value of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public String getPassword() throws RemoteException;
  /** Set the value of the PASSWORD column of the LOGIN_ACCOUNT_HISTORY table. */
  public void setPassword(String password) throws RemoteException;
  

  /** Get the value object of this LoginAccountHistory class. */
  public LoginAccountHistory getValueObject() throws RemoteException;
  /** Set the values in the value object of this LoginAccountHistory class. */
  public void setValueObject(LoginAccountHistory loginAccountHistoryValue) throws RemoteException;


  /** Get the  UserLogin entity corresponding to this entity. */
  public UserLogin getUserLogin() throws RemoteException;
  /** Remove the  UserLogin entity corresponding to this entity. */
  public void removeUserLogin() throws RemoteException;  

  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

}
