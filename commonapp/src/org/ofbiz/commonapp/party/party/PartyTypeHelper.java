
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyType Entity EJB; acts as a proxy for the Home interface
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
 *@created    Wed Jul 18 12:02:47 MDT 2001
 *@version    1.0
 */
public class PartyTypeHelper
{

  /** A static variable to cache the Home object for the PartyType EJB */
  private static PartyTypeHome partyTypeHome = null;

  /** Initializes the partyTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyTypeHome instance for the default EJB server
   */
  public static PartyTypeHome getPartyTypeHome()
  {
    if(partyTypeHome == null) //don't want to block here
    {
      synchronized(PartyTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyTypeHome");
            partyTypeHome = (PartyTypeHome)MyNarrow.narrow(homeObject, PartyTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyType home obtained " + partyTypeHome);
        }
      }
    }
    return partyTypeHome;
  }




  /** Remove the PartyType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    PartyType partyType = findByPrimaryKey(primaryKey);
    try
    {
      if(partyType != null)
      {
        partyType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a PartyType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyType corresponding to the primaryKey
   */
  public static PartyType findByPrimaryKey(java.lang.String primaryKey)
  {
    PartyType partyType = null;
    Debug.logInfo("PartyTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyType = (PartyType)MyNarrow.narrow(getPartyTypeHome().findByPrimaryKey(primaryKey), PartyType.class);
      if(partyType != null)
      {
        partyType = partyType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyType;
  }

  /** Finds all PartyType entities, returning an Iterator
   *@return    Iterator containing all PartyType entities
   */
  public static Iterator findAllIterator()
  {
    Collection collection = findAll();
    if(collection != null) return collection.iterator();
    else return null;
  }

  /** Finds all PartyType entities
   *@return    Collection containing all PartyType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyType
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PartyType create(String partyTypeId, String parentTypeId, String hasTable, String description)
  {
    PartyType partyType = null;
    Debug.logInfo("PartyTypeHelper.create: partyTypeId: " + partyTypeId);
    if(partyTypeId == null) { return null; }

    try { partyType = (PartyType)MyNarrow.narrow(getPartyTypeHome().create(partyTypeId, parentTypeId, hasTable, description), PartyType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyType with partyTypeId: " + partyTypeId);
      Debug.logError(ce);
      partyType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyType;
  }

  /** Updates the corresponding PartyType
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PartyType update(String partyTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(partyTypeId == null) { return null; }
    PartyType partyType = findByPrimaryKey(partyTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyType partyTypeValue = new PartyTypeValue();

    if(parentTypeId != null) { partyTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { partyTypeValue.setHasTable(hasTable); }
    if(description != null) { partyTypeValue.setDescription(description); }

    partyType.setValueObject(partyTypeValue);
    return partyType;
  }

  /** Removes/deletes the specified  PartyType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = findByParentTypeIdIterator(parentTypeId);

    while(iterator.hasNext())
    {
      try
      {
        PartyType partyType = (PartyType) iterator.next();
        Debug.logInfo("Removing partyType with parentTypeId:" + parentTypeId);
        partyType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByParentTypeIdIterator(String parentTypeId)
  {
    Collection collection = findByParentTypeId(parentTypeId);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds PartyType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = findByHasTableIterator(hasTable);

    while(iterator.hasNext())
    {
      try
      {
        PartyType partyType = (PartyType) iterator.next();
        Debug.logInfo("Removing partyType with hasTable:" + hasTable);
        partyType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Description of the Method
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Iterator findByHasTableIterator(String hasTable)
  {
    Collection collection = findByHasTable(hasTable);
    if(collection != null) { return collection.iterator(); }
    else { return null; }
  }

  /** Finds PartyType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
