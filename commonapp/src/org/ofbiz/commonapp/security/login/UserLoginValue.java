
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class UserLoginValue implements UserLogin
{
  /** The variable of the USER_LOGIN_ID column of the USER_LOGIN table. */
  private String userLoginId;
  /** The variable of the PARTY_ID column of the USER_LOGIN table. */
  private String partyId;
  /** The variable of the CONTACT_MECHANISM_ID column of the USER_LOGIN table. */
  private String contactMechanismId;
  /** The variable of the CURRENT_USER_ID column of the USER_LOGIN table. */
  private String currentUserId;
  /** The variable of the CURRENT_PASSWORD column of the USER_LOGIN table. */
  private String currentPassword;

  private UserLogin userLogin;

  public UserLoginValue()
  {
    this.userLoginId = null;
    this.partyId = null;
    this.contactMechanismId = null;
    this.currentUserId = null;
    this.currentPassword = null;

    this.userLogin = null;
  }

  public UserLoginValue(UserLogin userLogin) throws RemoteException
  {
    if(userLogin == null) return;
  
    this.userLoginId = userLogin.getUserLoginId();
    this.partyId = userLogin.getPartyId();
    this.contactMechanismId = userLogin.getContactMechanismId();
    this.currentUserId = userLogin.getCurrentUserId();
    this.currentPassword = userLogin.getCurrentPassword();

    this.userLogin = userLogin;
  }

  public UserLoginValue(UserLogin userLogin, String userLoginId, String partyId, String contactMechanismId, String currentUserId, String currentPassword)
  {
    if(userLogin == null) return;
  
    this.userLoginId = userLoginId;
    this.partyId = partyId;
    this.contactMechanismId = contactMechanismId;
    this.currentUserId = currentUserId;
    this.currentPassword = currentPassword;

    this.userLogin = userLogin;
  }


  /** Get the primary key of the USER_LOGIN_ID column of the USER_LOGIN table. */
  public String getUserLoginId()  throws RemoteException { return userLoginId; }

  /** Get the value of the PARTY_ID column of the USER_LOGIN table. */
  public String getPartyId() throws RemoteException { return partyId; }
  /** Set the value of the PARTY_ID column of the USER_LOGIN table. */
  public void setPartyId(String partyId) throws RemoteException
  {
    this.partyId = partyId;
    if(userLogin!=null) userLogin.setPartyId(partyId);
  }

  /** Get the value of the CONTACT_MECHANISM_ID column of the USER_LOGIN table. */
  public String getContactMechanismId() throws RemoteException { return contactMechanismId; }
  /** Set the value of the CONTACT_MECHANISM_ID column of the USER_LOGIN table. */
  public void setContactMechanismId(String contactMechanismId) throws RemoteException
  {
    this.contactMechanismId = contactMechanismId;
    if(userLogin!=null) userLogin.setContactMechanismId(contactMechanismId);
  }

  /** Get the value of the CURRENT_USER_ID column of the USER_LOGIN table. */
  public String getCurrentUserId() throws RemoteException { return currentUserId; }
  /** Set the value of the CURRENT_USER_ID column of the USER_LOGIN table. */
  public void setCurrentUserId(String currentUserId) throws RemoteException
  {
    this.currentUserId = currentUserId;
    if(userLogin!=null) userLogin.setCurrentUserId(currentUserId);
  }

  /** Get the value of the CURRENT_PASSWORD column of the USER_LOGIN table. */
  public String getCurrentPassword() throws RemoteException { return currentPassword; }
  /** Set the value of the CURRENT_PASSWORD column of the USER_LOGIN table. */
  public void setCurrentPassword(String currentPassword) throws RemoteException
  {
    this.currentPassword = currentPassword;
    if(userLogin!=null) userLogin.setCurrentPassword(currentPassword);
  }

  /** Get the value object of the UserLogin class. */
  public UserLogin getValueObject() throws RemoteException { return this; }
  /** Set the value object of the UserLogin class. */
  public void setValueObject(UserLogin valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(userLogin!=null) userLogin.setValueObject(valueObject);

    if(userLoginId == null) userLoginId = valueObject.getUserLoginId();
    partyId = valueObject.getPartyId();
    contactMechanismId = valueObject.getContactMechanismId();
    currentUserId = valueObject.getCurrentUserId();
    currentPassword = valueObject.getCurrentPassword();
  }


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get a collection of  UserLoginSecurityGroup related entities. */
  public Collection getUserLoginSecurityGroups() { return UserLoginSecurityGroupHelper.findByUserLoginId(userLoginId); }
  /** Get the  UserLoginSecurityGroup keyed by member(s) of this class, and other passed parameters. */
  public UserLoginSecurityGroup getUserLoginSecurityGroup(String groupId) { return UserLoginSecurityGroupHelper.findByPrimaryKey(userLoginId, groupId); }
  /** Remove  UserLoginSecurityGroup related entities. */
  public void removeUserLoginSecurityGroups() { UserLoginSecurityGroupHelper.removeByUserLoginId(userLoginId); }
  /** Remove the  UserLoginSecurityGroup keyed by member(s) of this class, and other passed parameters. */
  public void removeUserLoginSecurityGroup(String groupId) { UserLoginSecurityGroupHelper.removeByPrimaryKey(userLoginId, groupId); }

  /** Get a collection of  LoginAccountHistory related entities. */
  public Collection getLoginAccountHistorys() { return LoginAccountHistoryHelper.findByUserLoginId(userLoginId); }
  /** Get the  LoginAccountHistory keyed by member(s) of this class, and other passed parameters. */
  public LoginAccountHistory getLoginAccountHistory(String userLoginSeqId) { return LoginAccountHistoryHelper.findByPrimaryKey(userLoginId, userLoginSeqId); }
  /** Remove  LoginAccountHistory related entities. */
  public void removeLoginAccountHistorys() { LoginAccountHistoryHelper.removeByUserLoginId(userLoginId); }
  /** Remove the  LoginAccountHistory keyed by member(s) of this class, and other passed parameters. */
  public void removeLoginAccountHistory(String userLoginSeqId) { LoginAccountHistoryHelper.removeByPrimaryKey(userLoginId, userLoginSeqId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(userLogin!=null) return userLogin.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(userLogin!=null) return userLogin.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(userLogin!=null) return userLogin.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(userLogin!=null) return userLogin.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(userLogin!=null) userLogin.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
