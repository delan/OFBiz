
package org.ofbiz.commonapp.security.login;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */

public interface UserLoginHome extends EJBHome
{

  public UserLogin create(String userLoginId, String partyId, String contactMechanismId, String currentUserId, String currentPassword) throws RemoteException, CreateException;
  public UserLogin create(String userLoginId) throws RemoteException, CreateException;
  public UserLogin findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds UserLogins by the following fields:
   *

   *@param  partyId                  Field for the PARTY_ID column.
   *@return      Collection containing the found UserLogins
   */
  public Collection findByPartyId(String partyId) throws RemoteException, FinderException;

  /**
   *  Finds UserLogins by the following fields:
   *

   *@param  contactMechanismId                  Field for the CONTACT_MECHANISM_ID column.
   *@return      Collection containing the found UserLogins
   */
  public Collection findByContactMechanismId(String contactMechanismId) throws RemoteException, FinderException;

  /**
   *  Finds UserLogins by the following fields:
   *

   *@param  currentUserId                  Field for the CURRENT_USER_ID column.
   *@return      Collection containing the found UserLogins
   */
  public Collection findByCurrentUserId(String currentUserId) throws RemoteException, FinderException;

}
