
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Party Type Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyTypeAttr Entity EJB; acts as a proxy for the Home interface
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
 *@created    Sun Jul 08 01:14:04 MDT 2001
 *@version    1.0
 */
public class PartyTypeAttrHelper
{

  /**
   *  A static variable to cache the Home object for the PartyTypeAttr EJB
   */
  public static PartyTypeAttrHome partyTypeAttrHome = null;

  /**
   *  Initializes the partyTypeAttrHome, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(partyTypeAttrHome == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyTypeAttrHome");
        partyTypeAttrHome = (PartyTypeAttrHome)MyNarrow.narrow(homeObject, PartyTypeAttrHome.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("partyTypeAttr home obtained " + partyTypeAttrHome);
      }
    }
  }



  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String partyTypeId, String name)
  {
    if(partyTypeId == null || name == null)
    {
      return;
    }
    PartyTypeAttrPK primaryKey = new PartyTypeAttrPK(partyTypeId, name);
    removeByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.party.party.PartyTypeAttrPK primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    PartyTypeAttr partyTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(partyTypeAttr != null)
      {
        partyTypeAttr.remove();
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

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       Description of the Returned Value
   */
  public static PartyTypeAttr findByPrimaryKey(String partyTypeId, String name)
  {
    if(partyTypeId == null || name == null)
    {
      return null;
    }
    PartyTypeAttrPK primaryKey = new PartyTypeAttrPK(partyTypeId, name);
    return findByPrimaryKey(primaryKey);
  }


  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyTypeAttr of primaryKey
   */
  public static PartyTypeAttr findByPrimaryKey(org.ofbiz.commonapp.party.party.PartyTypeAttrPK primaryKey)
  {
    PartyTypeAttr partyTypeAttr = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PartyTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }

    init();

    try
    {
      partyTypeAttr = (PartyTypeAttr)MyNarrow.narrow(partyTypeAttrHome.findByPrimaryKey(primaryKey), PartyTypeAttr.class);
      if(partyTypeAttr != null)
      {
        partyTypeAttr = partyTypeAttr.getValueObject();

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
    return partyTypeAttr;
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
      System.out.println("PartyTypeAttrHelper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(partyTypeAttrHome.findAll(), Collection.class);
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

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PartyTypeAttr create(String partyTypeId, String name)
  {
    PartyTypeAttr partyTypeAttr = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("PartyTypeAttrHelper.create: partyTypeId, name: " + partyTypeId + ", " + name);
    }
    if(partyTypeId == null || name == null)
    {
      return null;
    }
    init();

    try
    {
      partyTypeAttr = (PartyTypeAttr)MyNarrow.narrow(partyTypeAttrHome.create(partyTypeId, name), PartyTypeAttr.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create partyTypeAttr with partyTypeId, name: " + partyTypeId + ", " + name);
        ce.printStackTrace();
      }
      partyTypeAttr = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return partyTypeAttr;
  }

  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PartyTypeAttr update(String partyTypeId, String name) throws java.rmi.RemoteException
  {
    if(partyTypeId == null || name == null)
    {
      return null;
    }
    PartyTypeAttr partyTypeAttr = findByPrimaryKey(partyTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyTypeAttr partyTypeAttrValue = new PartyTypeAttrValue();


  
  

    partyTypeAttr.setValueObject(partyTypeAttrValue);
    return partyTypeAttr;
  }


  
  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   */
  public static void removeByPartyTypeId(String partyTypeId)
  {
    if(partyTypeId == null)
    {
      return;
    }
    Iterator iterator = findByPartyTypeIdIterator(partyTypeId);

    while(iterator.hasNext())
    {
      try
      {
        PartyTypeAttr partyTypeAttr = (PartyTypeAttr) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing partyTypeAttr with partyTypeId:" + partyTypeId);
        }
        partyTypeAttr.remove();
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

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByPartyTypeIdIterator(String partyTypeId)
  {
    Collection collection = findByPartyTypeId(partyTypeId);
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
   *  Finds PartyTypeAttr records by the following fieldters:
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyTypeId(String partyTypeId)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findByPartyTypeId: partyTypeId:" + partyTypeId);
    }

    Collection collection = null;
    if(partyTypeId == null)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(partyTypeAttrHome.findByPartyTypeId(partyTypeId), Collection.class);
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
        PartyTypeAttr partyTypeAttr = (PartyTypeAttr) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing partyTypeAttr with name:" + name);
        }
        partyTypeAttr.remove();
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
   *  Finds PartyTypeAttr records by the following fieldters:
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
      collection = (Collection) MyNarrow.narrow(partyTypeAttrHome.findByName(name), Collection.class);
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
