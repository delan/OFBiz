
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.party.party.*;
import org.ofbiz.commonapp.security.securitygroup.*;

/**
 * <p><b>Title:</b> User Login Entity
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
 *@created    Wed Jul 18 12:02:49 MDT 2001
 *@version    1.0
 */

public interface UserLogin extends EJBObject
{
  /** Get the primary key of the USER_LOGIN_ID column of the USER_LOGIN table. */
  public String getUserLoginId() throws RemoteException;
  
  /** Get the value of the PARTY_ID column of the USER_LOGIN table. */
  public String getPartyId() throws RemoteException;
  /** Set the value of the PARTY_ID column of the USER_LOGIN table. */
  public void setPartyId(String partyId) throws RemoteException;
  
  /** Get the value of the CONTACT_MECHANISM_ID column of the USER_LOGIN table. */
  public String getContactMechanismId() throws RemoteException;
  /** Set the value of the CONTACT_MECHANISM_ID column of the USER_LOGIN table. */
  public void setContactMechanismId(String contactMechanismId) throws RemoteException;
  
  /** Get the value of the CURRENT_USER_ID column of the USER_LOGIN table. */
  public String getCurrentUserId() throws RemoteException;
  /** Set the value of the CURRENT_USER_ID column of the USER_LOGIN table. */
  public void setCurrentUserId(String currentUserId) throws RemoteException;
  
  /** Get the value of the CURRENT_PASSWORD column of the USER_LOGIN table. */
  public String getCurrentPassword() throws RemoteException;
  /** Set the value of the CURRENT_PASSWORD column of the USER_LOGIN table. */
  public void setCurrentPassword(String currentPassword) throws RemoteException;
  

  /** Get the value object of this UserLogin class. */
  public UserLogin getValueObject() throws RemoteException;
  /** Set the values in the value object of this UserLogin class. */
  public void setValueObject(UserLogin userLoginValue) throws RemoteException;


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() throws RemoteException;
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() throws RemoteException;  

  /** Get a collection of  UserLoginSecurityGroup related entities. */
  public Collection getUserLoginSecurityGroups() throws RemoteException;
  /** Get the  UserLoginSecurityGroup keyed by member(s) of this class, and other passed parameters. */
  public UserLoginSecurityGroup getUserLoginSecurityGroup(String groupId) throws RemoteException;
  /** Remove  UserLoginSecurityGroup related entities. */
  public void removeUserLoginSecurityGroups() throws RemoteException;
  /** Remove the  UserLoginSecurityGroup keyed by member(s) of this class, and other passed parameters. */
  public void removeUserLoginSecurityGroup(String groupId) throws RemoteException;

  /** Get a collection of  LoginAccountHistory related entities. */
  public Collection getLoginAccountHistorys() throws RemoteException;
  /** Get the  LoginAccountHistory keyed by member(s) of this class, and other passed parameters. */
  public LoginAccountHistory getLoginAccountHistory(String userLoginSeqId) throws RemoteException;
  /** Remove  LoginAccountHistory related entities. */
  public void removeLoginAccountHistorys() throws RemoteException;
  /** Remove the  LoginAccountHistory keyed by member(s) of this class, and other passed parameters. */
  public void removeLoginAccountHistory(String userLoginSeqId) throws RemoteException;

}
