
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */
public class PartyTypeAttrHelper
{

  /** A static variable to cache the Home object for the PartyTypeAttr EJB */
  private static PartyTypeAttrHome partyTypeAttrHome = null;

  /** Initializes the partyTypeAttrHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyTypeAttrHome instance for the default EJB server
   */
  public static PartyTypeAttrHome getPartyTypeAttrHome()
  {
    if(partyTypeAttrHome == null) //don't want to block here
    {
      synchronized(PartyTypeAttrHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyTypeAttrHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyTypeAttrHome");
            partyTypeAttrHome = (PartyTypeAttrHome)MyNarrow.narrow(homeObject, PartyTypeAttrHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyTypeAttr home obtained " + partyTypeAttrHome);
        }
      }
    }
    return partyTypeAttrHome;
  }



  /** Remove the PartyTypeAttr corresponding to the primaryKey specified by fields
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

  /** Remove the PartyTypeAttr corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.party.party.PartyTypeAttrPK primaryKey)
  {
    if(primaryKey == null) return;
    PartyTypeAttr partyTypeAttr = findByPrimaryKey(primaryKey);
    try
    {
      if(partyTypeAttr != null)
      {
        partyTypeAttr.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PartyTypeAttr by its Primary Key, specified by individual fields
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return       The PartyTypeAttr corresponding to the primaryKey
   */
  public static PartyTypeAttr findByPrimaryKey(String partyTypeId, String name)
  {
    if(partyTypeId == null || name == null) return null;
    PartyTypeAttrPK primaryKey = new PartyTypeAttrPK(partyTypeId, name);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PartyTypeAttr by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyTypeAttr corresponding to the primaryKey
   */
  public static PartyTypeAttr findByPrimaryKey(org.ofbiz.commonapp.party.party.PartyTypeAttrPK primaryKey)
  {
    PartyTypeAttr partyTypeAttr = null;
    Debug.logInfo("PartyTypeAttrHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyTypeAttr = (PartyTypeAttr)MyNarrow.narrow(getPartyTypeAttrHome().findByPrimaryKey(primaryKey), PartyTypeAttr.class);
      if(partyTypeAttr != null)
      {
        partyTypeAttr = partyTypeAttr.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyTypeAttr;
  }

  /** Finds all PartyTypeAttr entities
   *@return    Collection containing all PartyTypeAttr entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyTypeAttrHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyTypeAttrHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyTypeAttr
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PartyTypeAttr create(String partyTypeId, String name)
  {
    PartyTypeAttr partyTypeAttr = null;
    Debug.logInfo("PartyTypeAttrHelper.create: partyTypeId, name: " + partyTypeId + ", " + name);
    if(partyTypeId == null || name == null) { return null; }

    try { partyTypeAttr = (PartyTypeAttr)MyNarrow.narrow(getPartyTypeAttrHome().create(partyTypeId, name), PartyTypeAttr.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyTypeAttr with partyTypeId, name: " + partyTypeId + ", " + name);
      Debug.logError(ce);
      partyTypeAttr = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyTypeAttr;
  }

  /** Updates the corresponding PartyTypeAttr
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                Description of the Returned Value
   */
  public static PartyTypeAttr update(String partyTypeId, String name) throws java.rmi.RemoteException
  {
    if(partyTypeId == null || name == null) { return null; }
    PartyTypeAttr partyTypeAttr = findByPrimaryKey(partyTypeId, name);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyTypeAttr partyTypeAttrValue = new PartyTypeAttrValue();


    partyTypeAttr.setValueObject(partyTypeAttrValue);
    return partyTypeAttr;
  }

  /** Removes/deletes the specified  PartyTypeAttr
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   */
  public static void removeByPartyTypeId(String partyTypeId)
  {
    if(partyTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyTypeId(partyTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PartyTypeAttr partyTypeAttr = (PartyTypeAttr) iterator.next();
        Debug.logInfo("Removing partyTypeAttr with partyTypeId:" + partyTypeId);
        partyTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyTypeAttr records by the following parameters:
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyTypeId(String partyTypeId)
  {
    Debug.logInfo("findByPartyTypeId: partyTypeId:" + partyTypeId);

    Collection collection = null;
    if(partyTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyTypeAttrHome().findByPartyTypeId(partyTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyTypeAttr
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
        PartyTypeAttr partyTypeAttr = (PartyTypeAttr) iterator.next();
        Debug.logInfo("Removing partyTypeAttr with name:" + name);
        partyTypeAttr.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyTypeAttr records by the following parameters:
   *@param  name                  Field of the NAME column.
   *@return      Description of the Returned Value
   */
  public static Collection findByName(String name)
  {
    Debug.logInfo("findByName: name:" + name);

    Collection collection = null;
    if(name == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyTypeAttrHome().findByName(name), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
