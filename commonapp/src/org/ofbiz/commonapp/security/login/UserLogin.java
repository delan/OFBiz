
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

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
 *@created    Fri Jun 29 12:50:44 MDT 2001
 *@version    1.0
 */

public interface UserLogin extends EJBObject
{

  
  /**
   *  Get the primary key of the USER_LOGIN_ID column of the USER_LOGIN table.
   */
  public String getUserLoginId() throws RemoteException;
  

  
  /**
   *  Get the value of the PARTY_ID column of the USER_LOGIN table.
   */
  public String getPartyId() throws RemoteException;
  /**
   *  Set the value of the PARTY_ID column of the USER_LOGIN table.
   */
  public void setPartyId(String partyId) throws RemoteException;
  

  
  /**
   *  Get the value of the CONTACT_MECHANISM_ID column of the USER_LOGIN table.
   */
  public String getContactMechanismId() throws RemoteException;
  /**
   *  Set the value of the CONTACT_MECHANISM_ID column of the USER_LOGIN table.
   */
  public void setContactMechanismId(String contactMechanismId) throws RemoteException;
  

  
  /**
   *  Get the value of the CURRENT_USER_ID column of the USER_LOGIN table.
   */
  public String getCurrentUserId() throws RemoteException;
  /**
   *  Set the value of the CURRENT_USER_ID column of the USER_LOGIN table.
   */
  public void setCurrentUserId(String currentUserId) throws RemoteException;
  

  
  /**
   *  Get the value of the CURRENT_PASSWORD column of the USER_LOGIN table.
   */
  public String getCurrentPassword() throws RemoteException;
  /**
   *  Set the value of the CURRENT_PASSWORD column of the USER_LOGIN table.
   */
  public void setCurrentPassword(String currentPassword) throws RemoteException;
  


  /**
   *  Get the value object of this UserLogin class.
   */
  public UserLogin getValueObject() throws RemoteException;
  /**
   *  Set the values in the value object of this UserLogin class.
   */
  public void setValueObject(UserLogin userLoginValue) throws RemoteException;
}
