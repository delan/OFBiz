
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
 *@created    Tue May 22 16:32:39 MDT 2001
 *@version    1.0
 */

public class PersonBean implements EntityBean
{

  /**
   *  The variable for the USERNAME column of the PERSON table.
   */
  public String username;

  /**
   *  The variable for the PASSWORD column of the PERSON table.
   */
  public String password;

  /**
   *  The variable for the FIRST_NAME column of the PERSON table.
   */
  public String firstName;

  /**
   *  The variable for the MIDDLE_NAME column of the PERSON table.
   */
  public String middleName;

  /**
   *  The variable for the LAST_NAME column of the PERSON table.
   */
  public String lastName;

  /**
   *  The variable for the TITLE column of the PERSON table.
   */
  public String title;

  /**
   *  The variable for the SUFFIX column of the PERSON table.
   */
  public String suffix;

  /**
   *  The variable for the HOME_PHONE column of the PERSON table.
   */
  public String homePhone;

  /**
   *  The variable for the WORK_PHONE column of the PERSON table.
   */
  public String workPhone;

  /**
   *  The variable for the FAX column of the PERSON table.
   */
  public String fax;

  /**
   *  The variable for the EMAIL column of the PERSON table.
   */
  public String email;

  /**
   *  The variable for the HOME_STREET1 column of the PERSON table.
   */
  public String homeStreet1;

  /**
   *  The variable for the HOME_STREET2 column of the PERSON table.
   */
  public String homeStreet2;

  /**
   *  The variable for the HOME_CITY column of the PERSON table.
   */
  public String homeCity;

  /**
   *  The variable for the HOME_COUNTY column of the PERSON table.
   */
  public String homeCounty;

  /**
   *  The variable for the HOME_STATE column of the PERSON table.
   */
  public String homeState;

  /**
   *  The variable for the HOME_COUNTRY column of the PERSON table.
   */
  public String homeCountry;

  /**
   *  The variable for the HOME_POSTAL_CODE column of the PERSON table.
   */
  public String homePostalCode;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the PersonBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key USERNAME column of the PERSON table.
   */
  public String getUsername()
  {
    return username;
  }
  

  
  /**
   *  Get the value of the PASSWORD column of the PERSON table.
   */
  public String getPassword()
  {
    return password;
  }
  /**
   *  Set the value of the PASSWORD column of the PERSON table.
   */
  public void setPassword(String password)
  {
    this.password = password;
  }
  

  
  /**
   *  Get the value of the FIRST_NAME column of the PERSON table.
   */
  public String getFirstName()
  {
    return firstName;
  }
  /**
   *  Set the value of the FIRST_NAME column of the PERSON table.
   */
  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }
  

  
  /**
   *  Get the value of the MIDDLE_NAME column of the PERSON table.
   */
  public String getMiddleName()
  {
    return middleName;
  }
  /**
   *  Set the value of the MIDDLE_NAME column of the PERSON table.
   */
  public void setMiddleName(String middleName)
  {
    this.middleName = middleName;
  }
  

  
  /**
   *  Get the value of the LAST_NAME column of the PERSON table.
   */
  public String getLastName()
  {
    return lastName;
  }
  /**
   *  Set the value of the LAST_NAME column of the PERSON table.
   */
  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }
  

  
  /**
   *  Get the value of the TITLE column of the PERSON table.
   */
  public String getTitle()
  {
    return title;
  }
  /**
   *  Set the value of the TITLE column of the PERSON table.
   */
  public void setTitle(String title)
  {
    this.title = title;
  }
  

  
  /**
   *  Get the value of the SUFFIX column of the PERSON table.
   */
  public String getSuffix()
  {
    return suffix;
  }
  /**
   *  Set the value of the SUFFIX column of the PERSON table.
   */
  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
  }
  

  
  /**
   *  Get the value of the HOME_PHONE column of the PERSON table.
   */
  public String getHomePhone()
  {
    return homePhone;
  }
  /**
   *  Set the value of the HOME_PHONE column of the PERSON table.
   */
  public void setHomePhone(String homePhone)
  {
    this.homePhone = homePhone;
  }
  

  
  /**
   *  Get the value of the WORK_PHONE column of the PERSON table.
   */
  public String getWorkPhone()
  {
    return workPhone;
  }
  /**
   *  Set the value of the WORK_PHONE column of the PERSON table.
   */
  public void setWorkPhone(String workPhone)
  {
    this.workPhone = workPhone;
  }
  

  
  /**
   *  Get the value of the FAX column of the PERSON table.
   */
  public String getFax()
  {
    return fax;
  }
  /**
   *  Set the value of the FAX column of the PERSON table.
   */
  public void setFax(String fax)
  {
    this.fax = fax;
  }
  

  
  /**
   *  Get the value of the EMAIL column of the PERSON table.
   */
  public String getEmail()
  {
    return email;
  }
  /**
   *  Set the value of the EMAIL column of the PERSON table.
   */
  public void setEmail(String email)
  {
    this.email = email;
  }
  

  
  /**
   *  Get the value of the HOME_STREET1 column of the PERSON table.
   */
  public String getHomeStreet1()
  {
    return homeStreet1;
  }
  /**
   *  Set the value of the HOME_STREET1 column of the PERSON table.
   */
  public void setHomeStreet1(String homeStreet1)
  {
    this.homeStreet1 = homeStreet1;
  }
  

  
  /**
   *  Get the value of the HOME_STREET2 column of the PERSON table.
   */
  public String getHomeStreet2()
  {
    return homeStreet2;
  }
  /**
   *  Set the value of the HOME_STREET2 column of the PERSON table.
   */
  public void setHomeStreet2(String homeStreet2)
  {
    this.homeStreet2 = homeStreet2;
  }
  

  
  /**
   *  Get the value of the HOME_CITY column of the PERSON table.
   */
  public String getHomeCity()
  {
    return homeCity;
  }
  /**
   *  Set the value of the HOME_CITY column of the PERSON table.
   */
  public void setHomeCity(String homeCity)
  {
    this.homeCity = homeCity;
  }
  

  
  /**
   *  Get the value of the HOME_COUNTY column of the PERSON table.
   */
  public String getHomeCounty()
  {
    return homeCounty;
  }
  /**
   *  Set the value of the HOME_COUNTY column of the PERSON table.
   */
  public void setHomeCounty(String homeCounty)
  {
    this.homeCounty = homeCounty;
  }
  

  
  /**
   *  Get the value of the HOME_STATE column of the PERSON table.
   */
  public String getHomeState()
  {
    return homeState;
  }
  /**
   *  Set the value of the HOME_STATE column of the PERSON table.
   */
  public void setHomeState(String homeState)
  {
    this.homeState = homeState;
  }
  

  
  /**
   *  Get the value of the HOME_COUNTRY column of the PERSON table.
   */
  public String getHomeCountry()
  {
    return homeCountry;
  }
  /**
   *  Set the value of the HOME_COUNTRY column of the PERSON table.
   */
  public void setHomeCountry(String homeCountry)
  {
    this.homeCountry = homeCountry;
  }
  

  
  /**
   *  Get the value of the HOME_POSTAL_CODE column of the PERSON table.
   */
  public String getHomePostalCode()
  {
    return homePostalCode;
  }
  /**
   *  Set the value of the HOME_POSTAL_CODE column of the PERSON table.
   */
  public void setHomePostalCode(String homePostalCode)
  {
    this.homePostalCode = homePostalCode;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the PersonBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(Person valueObject)
  {

    try
    {

  
      this.password = valueObject.getPassword();
      this.firstName = valueObject.getFirstName();
      this.middleName = valueObject.getMiddleName();
      this.lastName = valueObject.getLastName();
      this.title = valueObject.getTitle();
      this.suffix = valueObject.getSuffix();
      this.homePhone = valueObject.getHomePhone();
      this.workPhone = valueObject.getWorkPhone();
      this.fax = valueObject.getFax();
      this.email = valueObject.getEmail();
      this.homeStreet1 = valueObject.getHomeStreet1();
      this.homeStreet2 = valueObject.getHomeStreet2();
      this.homeCity = valueObject.getHomeCity();
      this.homeCounty = valueObject.getHomeCounty();
      this.homeState = valueObject.getHomeState();
      this.homeCountry = valueObject.getHomeCountry();
      this.homePostalCode = valueObject.getHomePostalCode();
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }

  }

  /**
   *  Gets the ValueObject attribute of the PersonBean object
   *
   *@return    The ValueObject value
   */
  public Person getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PersonValue((Person)this.entityContext.getEJBObject(), username, password, firstName, middleName, lastName, title, suffix, homePhone, workPhone, fax, email, homeStreet1, homeStreet2, homeCity, homeCounty, homeState, homeCountry, homePostalCode);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  password                  Field of the PASSWORD column.
   *@param  firstName                  Field of the FIRST_NAME column.
   *@param  middleName                  Field of the MIDDLE_NAME column.
   *@param  lastName                  Field of the LAST_NAME column.
   *@param  title                  Field of the TITLE column.
   *@param  suffix                  Field of the SUFFIX column.
   *@param  homePhone                  Field of the HOME_PHONE column.
   *@param  workPhone                  Field of the WORK_PHONE column.
   *@param  fax                  Field of the FAX column.
   *@param  email                  Field of the EMAIL column.
   *@param  homeStreet1                  Field of the HOME_STREET1 column.
   *@param  homeStreet2                  Field of the HOME_STREET2 column.
   *@param  homeCity                  Field of the HOME_CITY column.
   *@param  homeCounty                  Field of the HOME_COUNTY column.
   *@param  homeState                  Field of the HOME_STATE column.
   *@param  homeCountry                  Field of the HOME_COUNTRY column.
   *@param  homePostalCode                  Field of the HOME_POSTAL_CODE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String username, String password, String firstName, String middleName, String lastName, String title, String suffix, String homePhone, String workPhone, String fax, String email, String homeStreet1, String homeStreet2, String homeCity, String homeCounty, String homeState, String homeCountry, String homePostalCode) throws CreateException
  {

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
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String username) throws CreateException
  {
    return ejbCreate(username, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  password                  Field of the PASSWORD column.
   *@param  firstName                  Field of the FIRST_NAME column.
   *@param  middleName                  Field of the MIDDLE_NAME column.
   *@param  lastName                  Field of the LAST_NAME column.
   *@param  title                  Field of the TITLE column.
   *@param  suffix                  Field of the SUFFIX column.
   *@param  homePhone                  Field of the HOME_PHONE column.
   *@param  workPhone                  Field of the WORK_PHONE column.
   *@param  fax                  Field of the FAX column.
   *@param  email                  Field of the EMAIL column.
   *@param  homeStreet1                  Field of the HOME_STREET1 column.
   *@param  homeStreet2                  Field of the HOME_STREET2 column.
   *@param  homeCity                  Field of the HOME_CITY column.
   *@param  homeCounty                  Field of the HOME_COUNTY column.
   *@param  homeState                  Field of the HOME_STATE column.
   *@param  homeCountry                  Field of the HOME_COUNTRY column.
   *@param  homePostalCode                  Field of the HOME_POSTAL_CODE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String username, String password, String firstName, String middleName, String lastName, String title, String suffix, String homePhone, String workPhone, String fax, String email, String homeStreet1, String homeStreet2, String homeCity, String homeCounty, String homeState, String homeCountry, String homePostalCode) throws CreateException
  {
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String username) throws CreateException
  {
    ejbPostCreate(username, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  /**
   *  Called when the entity bean is removed.
   *
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException
  {
  }

  /**
   *  Called when the entity bean is activated.
   */
  public void ejbActivate()
  {
  }

  /**
   *  Called when the entity bean is passivated.
   */
  public void ejbPassivate()
  {
  }

  /**
   *  Called when the entity bean is loaded.
   */
  public void ejbLoad()
  {
  }

  /**
   *  Called when the entity bean is stored.
   */
  public void ejbStore()
  {
  }

  /**
   *  Unsets the EntityContext, ie sets it to null.
   */
  public void unsetEntityContext()
  {
    entityContext = null;
  }
}
