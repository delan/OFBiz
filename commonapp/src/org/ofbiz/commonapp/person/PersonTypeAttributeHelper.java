
package org.ofbiz.commonapp.person;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Person Component - Person Type Attribute Entity
 * <p><b>Description:</b> The Helper class from the PersonTypeAttribute Entity EJB; acts as a proxy for the Home interface
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
 *@created    Wed May 23 12:52:35 MDT 2001
 *@version    1.0
 */

public class PersonTypeAttributeHelper
{

  /**
   *  A static variable to cache the Home object for the PersonTypeAttribute EJB
   */
  public static PersonTypeAttributeHome personTypeAttributeHome = null;

  /**
   *  Initializes the personTypeAttributeHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(personTypeAttributeHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.person.PersonTypeAttributeHome");
        personTypeAttributeHome = (PersonTypeAttributeHome)MyNarrow.narrow(homeObject, PersonTypeAttributeHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("personTypeAttribute home obtained " + personTypeAttributeHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String typeId, String name)
  {
    if(typeId == null || name == null)
    {
      return;
    }
    PersonTypeAttributePK primaryKey = new PersonTypeAttributePK(typeId, name);
    removeByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.person.PersonTypeAttributePK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    PersonTypeAttribute personTypeAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(personTypeAttribute != null)
      {
        personTypeAttribute.remove();
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

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       Description of the Returned Value
   */
  public static PersonTypeAttribute findByPrimaryKey(String typeId, String name)
  {
    if(typeId == null || name == null)
    {
      return null;
    }
    PersonTypeAttributePK primaryKey = new PersonTypeAttributePK(typeId, name);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The PersonTypeAttribute of primaryKey
   */
  public static PersonTypeAttribute findByPrimaryKey(org.ofbiz.commonapp.person.PersonTypeAttributePK primaryKey)
  {
    PersonTypeAttribute personTypeAttribute = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonTypeAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      personTypeAttribute = (PersonTypeAttribute)MyNarrow.narrow(personTypeAttributeHome.findByPrimaryKey(primaryKey), PersonTypeAttribute.class);
      if(personTypeAttribute != null)
      {
        personTypeAttribute = personTypeAttribute.getValueObject();

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
    return personTypeAttribute;
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
      System.out.println("PersonTypeAttributeHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(personTypeAttributeHome.findAll(), Collection.class);
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

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PersonTypeAttribute create(String typeId, String name)
  {
    PersonTypeAttribute personTypeAttribute = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonTypeAttributeHelper.create: typeId, name: " + typeId + ", " + name);
    }
    if(typeId == null || name == null)
    {
      return null;
    }
    init();

    try
    {
      personTypeAttribute = (PersonTypeAttribute)MyNarrow.narrow(personTypeAttributeHome.create(typeId, name), PersonTypeAttribute.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create personTypeAttribute with typeId, name: " + typeId + ", " + name);
        ce.printStackTrace();
      }
      personTypeAttribute = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return personTypeAttribute;
  }

  /**
   *  Description of the Method
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PersonTypeAttribute update(String typeId, String name) throws java.rmi.RemoteException
  {
    if(typeId == null || name == null)
    {
      return null;
    }
    PersonTypeAttribute personTypeAttribute = findByPrimaryKey(typeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PersonTypeAttribute personTypeAttributeValue = new PersonTypeAttributeValue();

    //When doing a setValueObject, everything gets copied over.  So, for all
    //null fieldeters, we will just let it default to the original value.
    personTypeAttributeValue.setValueObject(personTypeAttribute);


  
  

    personTypeAttribute.setValueObject(personTypeAttributeValue);
    return personTypeAttribute;
  }


  
  /**
   *  Description of the Method
   *

   *@param  typeId                  Field of the TYPE_ID column.
   */
  public static void removeByTypeId(String typeId)
  {
    if(typeId == null)
    {
      return;
    }
    Iterator iterator = findByTypeIdIterator(typeId);

    while(iterator.hasNext())
    {
      try
      {
        PersonTypeAttribute personTypeAttribute = (PersonTypeAttribute) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personTypeAttribute with typeId:" + typeId);
        }
        personTypeAttribute.remove();
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

   *@param  typeId                  Field of the TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByTypeIdIterator(String typeId)
  {
    Collection collection = findByTypeId(typeId);
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
   *  Finds PersonTypeAttribute records by the following fieldters:
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByTypeId(String typeId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByTypeId: typeId:" + typeId);
    }

    Collection collection = null;
    if(typeId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(personTypeAttributeHome.findByTypeId(typeId), Collection.class);
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
        PersonTypeAttribute personTypeAttribute = (PersonTypeAttribute) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing personTypeAttribute with name:" + name);
        }
        personTypeAttribute.remove();
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
   *  Finds PersonTypeAttribute records by the following fieldters:
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
      collection = (Collection) MyNarrow.narrow(personTypeAttributeHome.findByName(name), Collection.class);
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
