
package org.ofbiz.commonapp.person;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Person Component - Person Type Entity
 * <p><b>Description:</b> The Helper class from the PersonType Entity EJB; acts as a proxy for the Home interface
 *
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
 *@created    Wed May 23 12:51:41 MDT 2001
 *@version    1.0
 */

public class PersonTypeHelper
{

  /**
   *  A static variable to cache the Home object for the PersonType EJB
   */
  public static PersonTypeHome personTypeHome = null;

  /**
   *  Initializes the personTypeHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(personTypeHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.person.PersonTypeHome");
        personTypeHome = (PersonTypeHome)MyNarrow.narrow(homeObject, PersonTypeHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("personType home obtained " + personTypeHome);
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
    PersonType personType = findByPrimaryKey(primaryKey);
    try
    {
      if(personType != null)
      {
        personType.remove();
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
   *@return             The PersonType of primaryKey
   */
  public static PersonType findByPrimaryKey(java.lang.String primaryKey)
  {
    PersonType personType = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonTypeHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      personType = (PersonType)MyNarrow.narrow(personTypeHome.findByPrimaryKey(primaryKey), PersonType.class);
      if(personType != null)
      {
        personType = personType.getValueObject();

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
    return personType;
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
      System.out.println("PersonTypeHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(personTypeHome.findAll(), Collection.class);
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
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PersonType create(String typeId, String description)
  {
    PersonType personType = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PersonTypeHelper.create: typeId: " + typeId);
    }
    if(typeId == null)
    {
      return null;
    }
    init();

    try
    {
      personType = (PersonType)MyNarrow.narrow(personTypeHome.create(typeId, description), PersonType.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create personType with typeId: " + typeId);
        ce.printStackTrace();
      }
      personType = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return personType;
  }

  /**
   *  Description of the Method
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PersonType update(String typeId, String description) throws java.rmi.RemoteException
  {
    if(typeId == null)
    {
      return null;
    }
    PersonType personType = findByPrimaryKey(typeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PersonType personTypeValue = new PersonTypeValue();

    //When doing a setValueObject, everything gets copied over.  So, for all
    //null fieldeters, we will just let it default to the original value.
    personTypeValue.setValueObject(personType);


  
  
    if(description != null)
    {
      personTypeValue.setDescription(description);
    }

    personType.setValueObject(personTypeValue);
    return personType;
  }




}
