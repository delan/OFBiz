
package org.ofbiz.commonapp.person;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Person Component - Person Entity
 * <p><b>Description:</b> The Helper class from the Person Entity EJB; acts as a proxy for the Home interface
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
 *@created    Tue May 22 17:28:59 MDT 2001
 *@version    1.0
 */

public class PersonHelper
{

  /**
   *  A static variable to cache the Home object for the Person EJB
   */
  public static PersonHome personHome = null;

  /**
   *  Initializes the personHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(personHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.person.PersonHome");
        personHome = (PersonHome)MyNarrow.narrow(homeObject, PersonHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("person home obtained " + personHome);
      }
    }
  }




  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    Person person = findByPrimaryKey(primaryKey);
    try
    {
      if(person != null)
      {
        person.remove();
      }
    }
    catch(Exception e)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        e.printStackTrace();
      }
    }


  }



  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The Person of primaryKey
   */
  public static Person findByPrimaryKey(java.lang.String primaryKey)
  {
    Person person = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      person = (Person)MyNarrow.narrow(personHome.findByPrimaryKey(primaryKey), Person.class);
      if(person != null)
      {
        person = person.getValueObject();

      }
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return person;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public static Iterator findAllIterator()
  {
    Collection collection = findAll();
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public static Collection findAll()
  {
    Collection collection = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(personHome.findAll(), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return collection;
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
   *@return                Description of the Returned Value
   */
  public static Person create(String username, String password, String firstName, String middleName, String lastName, String title, String suffix, String homePhone, String workPhone, String fax, String email, String homeStreet1, String homeStreet2, String homeCity, String homeCounty, String homeState, String homeCountry, String homePostalCode)
  {
    Person person = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonHelper.create: username: " + username);
    }
    if(username == null)
    {
      return null;
    }
    init();

    try
    {
      person = (Person)MyNarrow.narrow(personHome.create(username, password, firstName, middleName, lastName, title, suffix, homePhone, workPhone, fax, email, homeStreet1, homeStreet2, homeCity, homeCounty, homeState, homeCountry, homePostalCode), Person.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create person with username: " + username);
        ce.printStackTrace();
      }
      person = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return person;
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
   *@return                Description of the Returned Value
   */
  public static Person update(String username, String password, String firstName, String middleName, String lastName, String title, String suffix, String homePhone, String workPhone, String fax, String email, String homeStreet1, String homeStreet2, String homeCity, String homeCounty, String homeState, String homeCountry, String homePostalCode) throws java.rmi.RemoteException
  {
    if(username == null)
    {
      return null;
    }
    Person person = findByPrimaryKey(username);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Person personValue = new PersonValue();

    //When doing a setValueObject, everything gets copied over.  So, for all
    //null fieldeters, we will just let it default to the original value.
    personValue.setValueObject(person);


  
  
    if(password != null)
    {
      personValue.setPassword(password);
    }
  
    if(firstName != null)
    {
      personValue.setFirstName(firstName);
    }
  
    if(middleName != null)
    {
      personValue.setMiddleName(middleName);
    }
  
    if(lastName != null)
    {
      personValue.setLastName(lastName);
    }
  
    if(title != null)
    {
      personValue.setTitle(title);
    }
  
    if(suffix != null)
    {
      personValue.setSuffix(suffix);
    }
  
    if(homePhone != null)
    {
      personValue.setHomePhone(homePhone);
    }
  
    if(workPhone != null)
    {
      personValue.setWorkPhone(workPhone);
    }
  
    if(fax != null)
    {
      personValue.setFax(fax);
    }
  
    if(email != null)
    {
      personValue.setEmail(email);
    }
  
    if(homeStreet1 != null)
    {
      personValue.setHomeStreet1(homeStreet1);
    }
  
    if(homeStreet2 != null)
    {
      personValue.setHomeStreet2(homeStreet2);
    }
  
    if(homeCity != null)
    {
      personValue.setHomeCity(homeCity);
    }
  
    if(homeCounty != null)
    {
      personValue.setHomeCounty(homeCounty);
    }
  
    if(homeState != null)
    {
      personValue.setHomeState(homeState);
    }
  
    if(homeCountry != null)
    {
      personValue.setHomeCountry(homeCountry);
    }
  
    if(homePostalCode != null)
    {
      personValue.setHomePostalCode(homePostalCode);
    }

    person.setValueObject(personValue);
    return person;
  }


  
  /**
   *  Description of the Method
   *

   *@param  firstName                  Field of the FIRST_NAME column.
   */
  public static void removeByFirstName(String firstName)
  {
    if(firstName == null)
    {
      return;
    }
    Iterator iterator = findByFirstNameIterator(firstName);

    while(iterator.hasNext())
    {
      try
      {
        Person person = (Person) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing person with firstName:" + firstName);
        }
        person.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  firstName                  Field of the FIRST_NAME column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByFirstNameIterator(String firstName)
  {
    Collection collection = findByFirstName(firstName);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds Person records by the following fieldters:
   *

   *@param  firstName                  Field of the FIRST_NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFirstName(String firstName)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByFirstName: firstName:" + firstName);
    }

    Collection collection = null;
    if(firstName == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personHome.findByFirstName(firstName), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }

  
  /**
   *  Description of the Method
   *

   *@param  lastName                  Field of the LAST_NAME column.
   */
  public static void removeByLastName(String lastName)
  {
    if(lastName == null)
    {
      return;
    }
    Iterator iterator = findByLastNameIterator(lastName);

    while(iterator.hasNext())
    {
      try
      {
        Person person = (Person) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing person with lastName:" + lastName);
        }
        person.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  lastName                  Field of the LAST_NAME column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByLastNameIterator(String lastName)
  {
    Collection collection = findByLastName(lastName);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds Person records by the following fieldters:
   *

   *@param  lastName                  Field of the LAST_NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByLastName(String lastName)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByLastName: lastName:" + lastName);
    }

    Collection collection = null;
    if(lastName == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personHome.findByLastName(lastName), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }

  
  /**
   *  Description of the Method
   *

   *@param  firstName                  Field of the FIRST_NAME column.
   *@param  lastName                  Field of the LAST_NAME column.
   */
  public static void removeByFirstNameAndLastName(String firstName, String lastName)
  {
    if(firstName == null || lastName == null)
    {
      return;
    }
    Iterator iterator = findByFirstNameAndLastNameIterator(firstName, lastName);

    while(iterator.hasNext())
    {
      try
      {
        Person person = (Person) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing person with firstName, lastName:" + firstName + ", " + lastName);
        }
        person.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  firstName                  Field of the FIRST_NAME column.
   *@param  lastName                  Field of the LAST_NAME column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByFirstNameAndLastNameIterator(String firstName, String lastName)
  {
    Collection collection = findByFirstNameAndLastName(firstName, lastName);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds Person records by the following fieldters:
   *

   *@param  firstName                  Field of the FIRST_NAME column.
   *@param  lastName                  Field of the LAST_NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByFirstNameAndLastName(String firstName, String lastName)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByFirstNameAndLastName: firstName, lastName:" + firstName + ", " + lastName);
    }

    Collection collection = null;
    if(firstName == null || lastName == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personHome.findByFirstNameAndLastName(firstName, lastName), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }

  
  /**
   *  Description of the Method
   *

   *@param  homePhone                  Field of the HOME_PHONE column.
   */
  public static void removeByHomePhone(String homePhone)
  {
    if(homePhone == null)
    {
      return;
    }
    Iterator iterator = findByHomePhoneIterator(homePhone);

    while(iterator.hasNext())
    {
      try
      {
        Person person = (Person) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing person with homePhone:" + homePhone);
        }
        person.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  homePhone                  Field of the HOME_PHONE column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByHomePhoneIterator(String homePhone)
  {
    Collection collection = findByHomePhone(homePhone);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds Person records by the following fieldters:
   *

   *@param  homePhone                  Field of the HOME_PHONE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHomePhone(String homePhone)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByHomePhone: homePhone:" + homePhone);
    }

    Collection collection = null;
    if(homePhone == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personHome.findByHomePhone(homePhone), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }

  
  /**
   *  Description of the Method
   *

   *@param  email                  Field of the EMAIL column.
   */
  public static void removeByEmail(String email)
  {
    if(email == null)
    {
      return;
    }
    Iterator iterator = findByEmailIterator(email);

    while(iterator.hasNext())
    {
      try
      {
        Person person = (Person) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing person with email:" + email);
        }
        person.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *

   *@param  email                  Field of the EMAIL column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByEmailIterator(String email)
  {
    Collection collection = findByEmail(email);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds Person records by the following fieldters:
   *

   *@param  email                  Field of the EMAIL column.
   *@return      Description of the Returned Value
   */
  public static Collection findByEmail(String email)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByEmail: email:" + email);
    }

    Collection collection = null;
    if(email == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personHome.findByEmail(email), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }



}
