
package org.ofbiz.commonapp.person;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Person Component - Person Attribute Entity
 * <p><b>Description:</b> The Helper class from the PersonAttribute Entity EJB; acts as a proxy for the Home interface
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
 *@created    Wed May 23 12:49:43 MDT 2001
 *@version    1.0
 */

public class PersonAttributeHelper
{

  /**
   *  A static variable to cache the Home object for the PersonAttribute EJB
   */
  public static PersonAttributeHome personAttributeHome = null;

  /**
   *  Initializes the personAttributeHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(personAttributeHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.person.PersonAttributeHome");
        personAttributeHome = (PersonAttributeHome)MyNarrow.narrow(homeObject, PersonAttributeHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("personAttribute home obtained " + personAttributeHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String username, String name)
  {
    if(username == null || name == null)
    {
      return;
    }
    PersonAttributePK primaryKey = new PersonAttributePK(username, name);
    removeByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.person.PersonAttributePK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    PersonAttribute personAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(personAttribute != null)
      {
        personAttribute.remove();
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

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   *@return       Description of the Returned Value
   */
  public static PersonAttribute findByPrimaryKey(String username, String name)
  {
    if(username == null || name == null)
    {
      return null;
    }
    PersonAttributePK primaryKey = new PersonAttributePK(username, name);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The PersonAttribute of primaryKey
   */
  public static PersonAttribute findByPrimaryKey(org.ofbiz.commonapp.person.PersonAttributePK primaryKey)
  {
    PersonAttribute personAttribute = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      personAttribute = (PersonAttribute)MyNarrow.narrow(personAttributeHome.findByPrimaryKey(primaryKey), PersonAttribute.class);
      if(personAttribute != null)
      {
        personAttribute = personAttribute.getValueObject();

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
    return personAttribute;
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
      System.out.println("PersonAttributeHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(personAttributeHome.findAll(), Collection.class);
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
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static PersonAttribute create(String username, String name, String value)
  {
    PersonAttribute personAttribute = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonAttributeHelper.create: username, name: " + username + ", " + name);
    }
    if(username == null || name == null)
    {
      return null;
    }
    init();

    try
    {
      personAttribute = (PersonAttribute)MyNarrow.narrow(personAttributeHome.create(username, name, value), PersonAttribute.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create personAttribute with username, name: " + username + ", " + name);
        ce.printStackTrace();
      }
      personAttribute = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return personAttribute;
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static PersonAttribute update(String username, String name, String value) throws java.rmi.RemoteException
  {
    if(username == null || name == null)
    {
      return null;
    }
    PersonAttribute personAttribute = findByPrimaryKey(username, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PersonAttribute personAttributeValue = new PersonAttributeValue();

    //When doing a setValueObject, everything gets copied over.  So, for all
    //null fieldeters, we will just let it default to the original value.
    personAttributeValue.setValueObject(personAttribute);


  
  
  
    if(value != null)
    {
      personAttributeValue.setValue(value);
    }

    personAttribute.setValueObject(personAttributeValue);
    return personAttribute;
  }


  
  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   */
  public static void removeByUsername(String username)
  {
    if(username == null)
    {
      return;
    }
    Iterator iterator = findByUsernameIterator(username);

    while(iterator.hasNext())
    {
      try
      {
        PersonAttribute personAttribute = (PersonAttribute) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personAttribute with username:" + username);
        }
        personAttribute.remove();
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

   *@param  username                  Field of the USERNAME column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByUsernameIterator(String username)
  {
    Collection collection = findByUsername(username);
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
   *  Finds PersonAttribute records by the following fieldters:
   *

   *@param  username                  Field of the USERNAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUsername(String username)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByUsername: username:" + username);
    }

    Collection collection = null;
    if(username == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personAttributeHome.findByUsername(username), Collection.class);
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

   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null)
    {
      return;
    }
    Iterator iterator = findByNameIterator(name);

    while(iterator.hasNext())
    {
      try
      {
        PersonAttribute personAttribute = (PersonAttribute) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personAttribute with name:" + name);
        }
        personAttribute.remove();
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

   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByNameIterator(String name)
  {
    Collection collection = findByName(name);
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
   *  Finds PersonAttribute records by the following fieldters:
   *

   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByName: name:" + name);
    }

    Collection collection = null;
    if(name == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personAttributeHome.findByName(name), Collection.class);
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
