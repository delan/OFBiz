
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

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

public interface LoginAccountHistoryHome extends EJBHome
{

  public LoginAccountHistory create(String userLoginId, String userLoginSeqId, java.util.Date fromDate, java.util.Date thruDate, String partyId, String userId, String password) throws RemoteException, CreateException;
  public LoginAccountHistory create(String userLoginId, String userLoginSeqId) throws RemoteException, CreateException;
  public LoginAccountHistory findByPrimaryKey(org.ofbiz.commonapp.security.login.LoginAccountHistoryPK primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds LoginAccountHistorys by the following fields:
   *

   *@param  userLoginId                  Field for the USER_LOGIN_ID column.
   *@return      Collection containing the found LoginAccountHistorys
   */
  public Collection findByUserLoginId(String userLoginId) throws RemoteException, FinderException;

  /**
   *  Finds LoginAccountHistorys by the following fields:
   *

   *@param  userId                  Field for the USER_ID column.
   *@return      Collection containing the found LoginAccountHistorys
   */
  public Collection findByUserId(String userId) throws RemoteException, FinderException;

  /**
   *  Finds LoginAccountHistorys by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@return      Collection containing the found LoginAccountHistorys
   */
  public Collection findByPartyId(String partyId) throws RemoteException, FinderException;

}
