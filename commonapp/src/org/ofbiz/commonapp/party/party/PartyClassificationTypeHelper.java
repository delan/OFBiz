
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Classification Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyClassificationType Entity EJB; acts as a proxy for the Home interface
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
public class PartyClassificationTypeHelper
{

  /** A static variable to cache the Home object for the PartyClassificationType EJB */
  private static PartyClassificationTypeHome partyClassificationTypeHome = null;

  /** Initializes the partyClassificationTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyClassificationTypeHome instance for the default EJB server
   */
  public static PartyClassificationTypeHome getPartyClassificationTypeHome()
  {
    if(partyClassificationTypeHome == null) //don't want to block here
    {
      synchronized(PartyClassificationTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyClassificationTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyClassificationTypeHome");
            partyClassificationTypeHome = (PartyClassificationTypeHome)MyNarrow.narrow(homeObject, PartyClassificationTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyClassificationType home obtained " + partyClassificationTypeHome);
        }
      }
    }
    return partyClassificationTypeHome;
  }




  /** Remove the PartyClassificationType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    PartyClassificationType partyClassificationType = findByPrimaryKey(primaryKey);
    try
    {
      if(partyClassificationType != null)
      {
        partyClassificationType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a PartyClassificationType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyClassificationType corresponding to the primaryKey
   */
  public static PartyClassificationType findByPrimaryKey(java.lang.String primaryKey)
  {
    PartyClassificationType partyClassificationType = null;
    Debug.logInfo("PartyClassificationTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyClassificationType = (PartyClassificationType)MyNarrow.narrow(getPartyClassificationTypeHome().findByPrimaryKey(primaryKey), PartyClassificationType.class);
      if(partyClassificationType != null)
      {
        partyClassificationType = partyClassificationType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyClassificationType;
  }

  /** Finds all PartyClassificationType entities
   *@return    Collection containing all PartyClassificationType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyClassificationTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyClassificationTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyClassificationType
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PartyClassificationType create(String partyClassificationTypeId, String parentTypeId, String hasTable, String description)
  {
    PartyClassificationType partyClassificationType = null;
    Debug.logInfo("PartyClassificationTypeHelper.create: partyClassificationTypeId: " + partyClassificationTypeId);
    if(partyClassificationTypeId == null) { return null; }

    try { partyClassificationType = (PartyClassificationType)MyNarrow.narrow(getPartyClassificationTypeHome().create(partyClassificationTypeId, parentTypeId, hasTable, description), PartyClassificationType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyClassificationType with partyClassificationTypeId: " + partyClassificationTypeId);
      Debug.logError(ce);
      partyClassificationType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyClassificationType;
  }

  /** Updates the corresponding PartyClassificationType
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PartyClassificationType update(String partyClassificationTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(partyClassificationTypeId == null) { return null; }
    PartyClassificationType partyClassificationType = findByPrimaryKey(partyClassificationTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyClassificationType partyClassificationTypeValue = new PartyClassificationTypeValue();

    if(parentTypeId != null) { partyClassificationTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { partyClassificationTypeValue.setHasTable(hasTable); }
    if(description != null) { partyClassificationTypeValue.setDescription(description); }

    partyClassificationType.setValueObject(partyClassificationTypeValue);
    return partyClassificationType;
  }

  /** Removes/deletes the specified  PartyClassificationType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentTypeId(parentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PartyClassificationType partyClassificationType = (PartyClassificationType) iterator.next();
        Debug.logInfo("Removing partyClassificationType with parentTypeId:" + parentTypeId);
        partyClassificationType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyClassificationType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyClassificationTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyClassificationType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = UtilMisc.toIterator(findByHasTable(hasTable));

    while(iterator.hasNext())
    {
      try
      {
        PartyClassificationType partyClassificationType = (PartyClassificationType) iterator.next();
        Debug.logInfo("Removing partyClassificationType with hasTable:" + hasTable);
        partyClassificationType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyClassificationType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyClassificationTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
