
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Attribute Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyAttribute Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */
public class PartyAttributeHelper
{

  /** A static variable to cache the Home object for the PartyAttribute EJB */
  private static PartyAttributeHome partyAttributeHome = null;

  /** Initializes the partyAttributeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyAttributeHome instance for the default EJB server
   */
  public static PartyAttributeHome getPartyAttributeHome()
  {
    if(partyAttributeHome == null) //don't want to block here
    {
      synchronized(PartyAttributeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyAttributeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyAttributeHome");
            partyAttributeHome = (PartyAttributeHome)MyNarrow.narrow(homeObject, PartyAttributeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyAttribute home obtained " + partyAttributeHome);
        }
      }
    }
    return partyAttributeHome;
  }



  /** Remove the PartyAttribute corresponding to the primaryKey specified by fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   */
  public static void removeByPrimaryKey(String partyId, String name)
  {
    if(partyId == null || name == null)
    {
      return;
    }
    PartyAttributePK primaryKey = new PartyAttributePK(partyId, name);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the PartyAttribute corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.party.party.PartyAttributePK primaryKey)
  {
    if(primaryKey == null) return;
    PartyAttribute partyAttribute = findByPrimaryKey(primaryKey);
    try
    {
      if(partyAttribute != null)
      {
        partyAttribute.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PartyAttribute by its Primary Key, specified by individual fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The PartyAttribute corresponding to the primaryKey
   */
  public static PartyAttribute findByPrimaryKey(String partyId, String name)
  {
    if(partyId == null || name == null) return null;
    PartyAttributePK primaryKey = new PartyAttributePK(partyId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PartyAttribute by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyAttribute corresponding to the primaryKey
   */
  public static PartyAttribute findByPrimaryKey(org.ofbiz.commonapp.party.party.PartyAttributePK primaryKey)
  {
    PartyAttribute partyAttribute = null;
    Debug.logInfo("PartyAttributeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyAttribute = (PartyAttribute)MyNarrow.narrow(getPartyAttributeHome().findByPrimaryKey(primaryKey), PartyAttribute.class);
      if(partyAttribute != null)
      {
        partyAttribute = partyAttribute.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyAttribute;
  }

  /** Finds all PartyAttribute entities
   *@return    Collection containing all PartyAttribute entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyAttributeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyAttributeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyAttribute
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static PartyAttribute create(String partyId, String name, String value)
  {
    PartyAttribute partyAttribute = null;
    Debug.logInfo("PartyAttributeHelper.create: partyId, name: " + partyId + ", " + name);
    if(partyId == null || name == null) { return null; }

    try { partyAttribute = (PartyAttribute)MyNarrow.narrow(getPartyAttributeHome().create(partyId, name, value), PartyAttribute.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyAttribute with partyId, name: " + partyId + ", " + name);
      Debug.logError(ce);
      partyAttribute = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyAttribute;
  }

  /** Updates the corresponding PartyAttribute
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                Description of the Returned Value
   */
  public static PartyAttribute update(String partyId, String name, String value) throws java.rmi.RemoteException
  {
    if(partyId == null || name == null) { return null; }
    PartyAttribute partyAttribute = findByPrimaryKey(partyId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyAttribute partyAttributeValue = new PartyAttributeValue();

    if(value != null) { partyAttributeValue.setValue(value); }

    partyAttribute.setValueObject(partyAttributeValue);
    return partyAttribute;
  }

  /** Removes/deletes the specified  PartyAttribute
   *@param  partyId                  Field of the PARTY_ID column.
   */
  public static void removeByPartyId(String partyId)
  {
    if(partyId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyId(partyId));

    while(iterator.hasNext())
    {
      try
      {
        PartyAttribute partyAttribute = (PartyAttribute) iterator.next();
        Debug.logInfo("Removing partyAttribute with partyId:" + partyId);
        partyAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyAttribute records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyAttributeHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyAttribute
   *@param  name                  Field of the NAME column.
   */
  public static void removeByName(String name)
  {
    if(name == null) return;
    Iterator iterator = UtilMisc.toIterator(findByName(name));

    while(iterator.hasNext())
    {
      try
      {
        PartyAttribute partyAttribute = (PartyAttribute) iterator.next();
        Debug.logInfo("Removing partyAttribute with name:" + name);
        partyAttribute.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyAttribute records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyAttributeHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
