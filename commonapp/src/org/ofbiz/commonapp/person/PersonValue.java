package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

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
 *@created    Tue May 22 16:32:53 MDT 2001
 *@version    1.0
 */

public class PersonValue implements Person
{

  /**
   *  The variable of the USERNAME column of the PERSON table.
   */
  private String username;

  /**
   *  The variable of the PASSWORD column of the PERSON table.
   */
  private String password;

  /**
   *  The variable of the FIRST_NAME column of the PERSON table.
   */
  private String firstName;

  /**
   *  The variable of the MIDDLE_NAME column of the PERSON table.
   */
  private String middleName;

  /**
   *  The variable of the LAST_NAME column of the PERSON table.
   */
  private String lastName;

  /**
   *  The variable of the TITLE column of the PERSON table.
   */
  private String title;

  /**
   *  The variable of the SUFFIX column of the PERSON table.
   */
  private String suffix;

  /**
   *  The variable of the HOME_PHONE column of the PERSON table.
   */
  private String homePhone;

  /**
   *  The variable of the WORK_PHONE column of the PERSON table.
   */
  private String workPhone;

  /**
   *  The variable of the FAX column of the PERSON table.
   */
  private String fax;

  /**
   *  The variable of the EMAIL column of the PERSON table.
   */
  private String email;

  /**
   *  The variable of the HOME_STREET1 column of the PERSON table.
   */
  private String homeStreet1;

  /**
   *  The variable of the HOME_STREET2 column of the PERSON table.
   */
  private String homeStreet2;

  /**
   *  The variable of the HOME_CITY column of the PERSON table.
   */
  private String homeCity;

  /**
   *  The variable of the HOME_COUNTY column of the PERSON table.
   */
  private String homeCounty;

  /**
   *  The variable of the HOME_STATE column of the PERSON table.
   */
  private String homeState;

  /**
   *  The variable of the HOME_COUNTRY column of the PERSON table.
   */
  private String homeCountry;

  /**
   *  The variable of the HOME_POSTAL_CODE column of the PERSON table.
   */
  private String homePostalCode;


  private Person person;

  public PersonValue()
  {

    this.username = null;
    this.password = null;
    this.firstName = null;
    this.middleName = null;
    this.lastName = null;
    this.title = null;
    this.suffix = null;
    this.homePhone = null;
    this.workPhone = null;
    this.fax = null;
    this.email = null;
    this.homeStreet1 = null;
    this.homeStreet2 = null;
    this.homeCity = null;
    this.homeCounty = null;
    this.homeState = null;
    this.homeCountry = null;
    this.homePostalCode = null;

    this.person = null;
  }

  public PersonValue(Person person) throws RemoteException
  {
    if(person == null) return;


    this.username = person.getUsername();
    this.password = person.getPassword();
    this.firstName = person.getFirstName();
    this.middleName = person.getMiddleName();
    this.lastName = person.getLastName();
    this.title = person.getTitle();
    this.suffix = person.getSuffix();
    this.homePhone = person.getHomePhone();
    this.workPhone = person.getWorkPhone();
    this.fax = person.getFax();
    this.email = person.getEmail();
    this.homeStreet1 = person.getHomeStreet1();
    this.homeStreet2 = person.getHomeStreet2();
    this.homeCity = person.getHomeCity();
    this.homeCounty = person.getHomeCounty();
    this.homeState = person.getHomeState();
    this.homeCountry = person.getHomeCountry();
    this.homePostalCode = person.getHomePostalCode();

    this.person = person;
  }

  public PersonValue(Person person, String username, String password, String firstName, String middleName, String lastName, String title, String suffix, String homePhone, String workPhone, String fax, String email, String homeStreet1, String homeStreet2, String homeCity, String homeCounty, String homeState, String homeCountry, String homePostalCode)
  {
    if(person == null) return;


    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.title = title;
    this.suffix = suffix;
    this.homePhone = homePhone;
    this.workPhone = workPhone;
    this.fax = fax;
    this.email = email;
    this.homeStreet1 = homeStreet1;
    this.homeStreet2 = homeStreet2;
    this.homeCity = homeCity;
    this.homeCounty = homeCounty;
    this.homeState = homeState;
    this.homeCountry = homeCountry;
    this.homePostalCode = homePostalCode;

    this.person = person;
  }


  /**
   *  Get the primary key of the USERNAME column of the PERSON table.
   */
  public String getUsername()  throws RemoteException
  {
    return username;
  }
  
  /**
   *  Get the value of the PASSWORD column of the PERSON table.
   */
  public String getPassword() throws RemoteException
  {
    return password;
  }
  /**
   *  Set the value of the PASSWORD column of the PERSON table.
   */
  public void setPassword(String password) throws RemoteException
  {
    this.password = password;
    if(person!=null) person.setPassword(password);
  }
  
  /**
   *  Get the value of the FIRST_NAME column of the PERSON table.
   */
  public String getFirstName() throws RemoteException
  {
    return firstName;
  }
  /**
   *  Set the value of the FIRST_NAME column of the PERSON table.
   */
  public void setFirstName(String firstName) throws RemoteException
  {
    this.firstName = firstName;
    if(person!=null) person.setFirstName(firstName);
  }
  
  /**
   *  Get the value of the MIDDLE_NAME column of the PERSON table.
   */
  public String getMiddleName() throws RemoteException
  {
    return middleName;
  }
  /**
   *  Set the value of the MIDDLE_NAME column of the PERSON table.
   */
  public void setMiddleName(String middleName) throws RemoteException
  {
    this.middleName = middleName;
    if(person!=null) person.setMiddleName(middleName);
  }
  
  /**
   *  Get the value of the LAST_NAME column of the PERSON table.
   */
  public String getLastName() throws RemoteException
  {
    return lastName;
  }
  /**
   *  Set the value of the LAST_NAME column of the PERSON table.
   */
  public void setLastName(String lastName) throws RemoteException
  {
    this.lastName = lastName;
    if(person!=null) person.setLastName(lastName);
  }
  
  /**
   *  Get the value of the TITLE column of the PERSON table.
   */
  public String getTitle() throws RemoteException
  {
    return title;
  }
  /**
   *  Set the value of the TITLE column of the PERSON table.
   */
  public void setTitle(String title) throws RemoteException
  {
    this.title = title;
    if(person!=null) person.setTitle(title);
  }
  
  /**
   *  Get the value of the SUFFIX column of the PERSON table.
   */
  public String getSuffix() throws RemoteException
  {
    return suffix;
  }
  /**
   *  Set the value of the SUFFIX column of the PERSON table.
   */
  public void setSuffix(String suffix) throws RemoteException
  {
    this.suffix = suffix;
    if(person!=null) person.setSuffix(suffix);
  }
  
  /**
   *  Get the value of the HOME_PHONE column of the PERSON table.
   */
  public String getHomePhone() throws RemoteException
  {
    return homePhone;
  }
  /**
   *  Set the value of the HOME_PHONE column of the PERSON table.
   */
  public void setHomePhone(String homePhone) throws RemoteException
  {
    this.homePhone = homePhone;
    if(person!=null) person.setHomePhone(homePhone);
  }
  
  /**
   *  Get the value of the WORK_PHONE column of the PERSON table.
   */
  public String getWorkPhone() throws RemoteException
  {
    return workPhone;
  }
  /**
   *  Set the value of the WORK_PHONE column of the PERSON table.
   */
  public void setWorkPhone(String workPhone) throws RemoteException
  {
    this.workPhone = workPhone;
    if(person!=null) person.setWorkPhone(workPhone);
  }
  
  /**
   *  Get the value of the FAX column of the PERSON table.
   */
  public String getFax() throws RemoteException
  {
    return fax;
  }
  /**
   *  Set the value of the FAX column of the PERSON table.
   */
  public void setFax(String fax) throws RemoteException
  {
    this.fax = fax;
    if(person!=null) person.setFax(fax);
  }
  
  /**
   *  Get the value of the EMAIL column of the PERSON table.
   */
  public String getEmail() throws RemoteException
  {
    return email;
  }
  /**
   *  Set the value of the EMAIL column of the PERSON table.
   */
  public void setEmail(String email) throws RemoteException
  {
    this.email = email;
    if(person!=null) person.setEmail(email);
  }
  
  /**
   *  Get the value of the HOME_STREET1 column of the PERSON table.
   */
  public String getHomeStreet1() throws RemoteException
  {
    return homeStreet1;
  }
  /**
   *  Set the value of the HOME_STREET1 column of the PERSON table.
   */
  public void setHomeStreet1(String homeStreet1) throws RemoteException
  {
    this.homeStreet1 = homeStreet1;
    if(person!=null) person.setHomeStreet1(homeStreet1);
  }
  
  /**
   *  Get the value of the HOME_STREET2 column of the PERSON table.
   */
  public String getHomeStreet2() throws RemoteException
  {
    return homeStreet2;
  }
  /**
   *  Set the value of the HOME_STREET2 column of the PERSON table.
   */
  public void setHomeStreet2(String homeStreet2) throws RemoteException
  {
    this.homeStreet2 = homeStreet2;
    if(person!=null) person.setHomeStreet2(homeStreet2);
  }
  
  /**
   *  Get the value of the HOME_CITY column of the PERSON table.
   */
  public String getHomeCity() throws RemoteException
  {
    return homeCity;
  }
  /**
   *  Set the value of the HOME_CITY column of the PERSON table.
   */
  public void setHomeCity(String homeCity) throws RemoteException
  {
    this.homeCity = homeCity;
    if(person!=null) person.setHomeCity(homeCity);
  }
  
  /**
   *  Get the value of the HOME_COUNTY column of the PERSON table.
   */
  public String getHomeCounty() throws RemoteException
  {
    return homeCounty;
  }
  /**
   *  Set the value of the HOME_COUNTY column of the PERSON table.
   */
  public void setHomeCounty(String homeCounty) throws RemoteException
  {
    this.homeCounty = homeCounty;
    if(person!=null) person.setHomeCounty(homeCounty);
  }
  
  /**
   *  Get the value of the HOME_STATE column of the PERSON table.
   */
  public String getHomeState() throws RemoteException
  {
    return homeState;
  }
  /**
   *  Set the value of the HOME_STATE column of the PERSON table.
   */
  public void setHomeState(String homeState) throws RemoteException
  {
    this.homeState = homeState;
    if(person!=null) person.setHomeState(homeState);
  }
  
  /**
   *  Get the value of the HOME_COUNTRY column of the PERSON table.
   */
  public String getHomeCountry() throws RemoteException
  {
    return homeCountry;
  }
  /**
   *  Set the value of the HOME_COUNTRY column of the PERSON table.
   */
  public void setHomeCountry(String homeCountry) throws RemoteException
  {
    this.homeCountry = homeCountry;
    if(person!=null) person.setHomeCountry(homeCountry);
  }
  
  /**
   *  Get the value of the HOME_POSTAL_CODE column of the PERSON table.
   */
  public String getHomePostalCode() throws RemoteException
  {
    return homePostalCode;
  }
  /**
   *  Set the value of the HOME_POSTAL_CODE column of the PERSON table.
   */
  public void setHomePostalCode(String homePostalCode) throws RemoteException
  {
    this.homePostalCode = homePostalCode;
    if(person!=null) person.setHomePostalCode(homePostalCode);
  }
  

  /**
   *  Get the value object of the Person class.
   */
  public Person getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the Person class.
   */
  public void setValueObject(Person valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(person!=null) person.setValueObject(valueObject);

    if(username == null) username = valueObject.getUsername();
  
  
    password = valueObject.getPassword();
  
    firstName = valueObject.getFirstName();
  
    middleName = valueObject.getMiddleName();
  
    lastName = valueObject.getLastName();
  
    title = valueObject.getTitle();
  
    suffix = valueObject.getSuffix();
  
    homePhone = valueObject.getHomePhone();
  
    workPhone = valueObject.getWorkPhone();
  
    fax = valueObject.getFax();
  
    email = valueObject.getEmail();
  
    homeStreet1 = valueObject.getHomeStreet1();
  
    homeStreet2 = valueObject.getHomeStreet2();
  
    homeCity = valueObject.getHomeCity();
  
    homeCounty = valueObject.getHomeCounty();
  
    homeState = valueObject.getHomeState();
  
    homeCountry = valueObject.getHomeCountry();
  
    homePostalCode = valueObject.getHomePostalCode();
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(person!=null) return person.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(person!=null) return person.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(person!=null) return person.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(person!=null) return person.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(person!=null) person.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
