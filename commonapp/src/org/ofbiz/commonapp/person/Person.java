
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

/**
 * <p><b>Title:</b> Person Component - Person Entity
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author David E. Jones
 * @created Tue May 22 16:31:43 MDT 2001
 * @version 1.0
 */

public interface Person extends EJBObject
{

  
  /**
   *  Get the primary key of the USERNAME column of the PERSON table.
   */
  public String getUsername() throws RemoteException;
  

  
  /**
   *  Get the value of the PASSWORD column of the PERSON table.
   */
  public String getPassword() throws RemoteException;
  /**
   *  Set the value of the PASSWORD column of the PERSON table.
   */
  public void setPassword(String password) throws RemoteException;
  

  
  /**
   *  Get the value of the FIRST_NAME column of the PERSON table.
   */
  public String getFirstName() throws RemoteException;
  /**
   *  Set the value of the FIRST_NAME column of the PERSON table.
   */
  public void setFirstName(String firstName) throws RemoteException;
  

  
  /**
   *  Get the value of the MIDDLE_NAME column of the PERSON table.
   */
  public String getMiddleName() throws RemoteException;
  /**
   *  Set the value of the MIDDLE_NAME column of the PERSON table.
   */
  public void setMiddleName(String middleName) throws RemoteException;
  

  
  /**
   *  Get the value of the LAST_NAME column of the PERSON table.
   */
  public String getLastName() throws RemoteException;
  /**
   *  Set the value of the LAST_NAME column of the PERSON table.
   */
  public void setLastName(String lastName) throws RemoteException;
  

  
  /**
   *  Get the value of the TITLE column of the PERSON table.
   */
  public String getTitle() throws RemoteException;
  /**
   *  Set the value of the TITLE column of the PERSON table.
   */
  public void setTitle(String title) throws RemoteException;
  

  
  /**
   *  Get the value of the SUFFIX column of the PERSON table.
   */
  public String getSuffix() throws RemoteException;
  /**
   *  Set the value of the SUFFIX column of the PERSON table.
   */
  public void setSuffix(String suffix) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_PHONE column of the PERSON table.
   */
  public String getHomePhone() throws RemoteException;
  /**
   *  Set the value of the HOME_PHONE column of the PERSON table.
   */
  public void setHomePhone(String homePhone) throws RemoteException;
  

  
  /**
   *  Get the value of the WORK_PHONE column of the PERSON table.
   */
  public String getWorkPhone() throws RemoteException;
  /**
   *  Set the value of the WORK_PHONE column of the PERSON table.
   */
  public void setWorkPhone(String workPhone) throws RemoteException;
  

  
  /**
   *  Get the value of the FAX column of the PERSON table.
   */
  public String getFax() throws RemoteException;
  /**
   *  Set the value of the FAX column of the PERSON table.
   */
  public void setFax(String fax) throws RemoteException;
  

  
  /**
   *  Get the value of the EMAIL column of the PERSON table.
   */
  public String getEmail() throws RemoteException;
  /**
   *  Set the value of the EMAIL column of the PERSON table.
   */
  public void setEmail(String email) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_STREET1 column of the PERSON table.
   */
  public String getHomeStreet1() throws RemoteException;
  /**
   *  Set the value of the HOME_STREET1 column of the PERSON table.
   */
  public void setHomeStreet1(String homeStreet1) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_STREET2 column of the PERSON table.
   */
  public String getHomeStreet2() throws RemoteException;
  /**
   *  Set the value of the HOME_STREET2 column of the PERSON table.
   */
  public void setHomeStreet2(String homeStreet2) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_CITY column of the PERSON table.
   */
  public String getHomeCity() throws RemoteException;
  /**
   *  Set the value of the HOME_CITY column of the PERSON table.
   */
  public void setHomeCity(String homeCity) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_COUNTY column of the PERSON table.
   */
  public String getHomeCounty() throws RemoteException;
  /**
   *  Set the value of the HOME_COUNTY column of the PERSON table.
   */
  public void setHomeCounty(String homeCounty) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_STATE column of the PERSON table.
   */
  public String getHomeState() throws RemoteException;
  /**
   *  Set the value of the HOME_STATE column of the PERSON table.
   */
  public void setHomeState(String homeState) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_COUNTRY column of the PERSON table.
   */
  public String getHomeCountry() throws RemoteException;
  /**
   *  Set the value of the HOME_COUNTRY column of the PERSON table.
   */
  public void setHomeCountry(String homeCountry) throws RemoteException;
  

  
  /**
   *  Get the value of the HOME_POSTAL_CODE column of the PERSON table.
   */
  public String getHomePostalCode() throws RemoteException;
  /**
   *  Set the value of the HOME_POSTAL_CODE column of the PERSON table.
   */
  public void setHomePostalCode(String homePostalCode) throws RemoteException;
  


  /**
   *  Get the value object of this Person class.
   */
  public Person getValueObject() throws RemoteException;
  /**
   *  Set the values in the value object of this Person class.
   */
  public void setValueObject(Person personValue) throws RemoteException;
}
