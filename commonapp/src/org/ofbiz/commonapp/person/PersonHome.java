
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Person Component - Person Entity
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
 *@created    Tue May 22 16:32:08 MDT 2001
 *@version    1.0
 */

public interface PersonHome extends EJBHome
{

  public Person create(String username, String password, String firstName, String middleName, String lastName, String title, String suffix, String homePhone, String workPhone, String fax, String email, String homeStreet1, String homeStreet2, String homeCity, String homeCounty, String homeState, String homeCountry, String homePostalCode) throws RemoteException, CreateException;
  public Person create(String username) throws RemoteException, CreateException;
  public Person findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds Persons by the following fields:
   *

   *@param  firstName                  Field for the FIRST_NAME column.
   *@return      Collection containing the found Persons
   */
  public Collection findByFirstName(String firstName) throws RemoteException, FinderException;

  /**
   *  Finds Persons by the following fields:
   *

   *@param  lastName                  Field for the LAST_NAME column.
   *@return      Collection containing the found Persons
   */
  public Collection findByLastName(String lastName) throws RemoteException, FinderException;

  /**
   *  Finds Persons by the following fields:
   *

   *@param  firstName                  Field for the FIRST_NAME column.
   *@param  lastName                  Field for the LAST_NAME column.
   *@return      Collection containing the found Persons
   */
  public Collection findByFirstNameAndLastName(String firstName, String lastName) throws RemoteException, FinderException;

  /**
   *  Finds Persons by the following fields:
   *

   *@param  homePhone                  Field for the HOME_PHONE column.
   *@return      Collection containing the found Persons
   */
  public Collection findByHomePhone(String homePhone) throws RemoteException, FinderException;

  /**
   *  Finds Persons by the following fields:
   *

   *@param  email                  Field for the EMAIL column.
   *@return      Collection containing the found Persons
   */
  public Collection findByEmail(String email) throws RemoteException, FinderException;

}
