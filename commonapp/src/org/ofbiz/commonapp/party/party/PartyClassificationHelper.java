
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Classification Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PartyClassification Entity EJB; acts as a proxy for the Home interface
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
public class PartyClassificationHelper
{

  /** A static variable to cache the Home object for the PartyClassification EJB */
  private static PartyClassificationHome partyClassificationHome = null;

  /** Initializes the partyClassificationHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyClassificationHome instance for the default EJB server
   */
  public static PartyClassificationHome getPartyClassificationHome()
  {
    if(partyClassificationHome == null) //don't want to block here
    {
      synchronized(PartyClassificationHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyClassificationHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyClassificationHome");
            partyClassificationHome = (PartyClassificationHome)MyNarrow.narrow(homeObject, PartyClassificationHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("partyClassification home obtained " + partyClassificationHome);
        }
      }
    }
    return partyClassificationHome;
  }



  /** Remove the PartyClassification corresponding to the primaryKey specified by fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   */
  public static void removeByPrimaryKey(String partyId, String partyTypeId)
  {
    if(partyId == null || partyTypeId == null)
    {
      return;
    }
    PartyClassificationPK primaryKey = new PartyClassificationPK(partyId, partyTypeId);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the PartyClassification corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.party.party.PartyClassificationPK primaryKey)
  {
    if(primaryKey == null) return;
    PartyClassification partyClassification = findByPrimaryKey(primaryKey);
    try
    {
      if(partyClassification != null)
      {
        partyClassification.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a PartyClassification by its Primary Key, specified by individual fields
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return       The PartyClassification corresponding to the primaryKey
   */
  public static PartyClassification findByPrimaryKey(String partyId, String partyTypeId)
  {
    if(partyId == null || partyTypeId == null) return null;
    PartyClassificationPK primaryKey = new PartyClassificationPK(partyId, partyTypeId);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a PartyClassification by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PartyClassification corresponding to the primaryKey
   */
  public static PartyClassification findByPrimaryKey(org.ofbiz.commonapp.party.party.PartyClassificationPK primaryKey)
  {
    PartyClassification partyClassification = null;
    Debug.logInfo("PartyClassificationHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      partyClassification = (PartyClassification)MyNarrow.narrow(getPartyClassificationHome().findByPrimaryKey(primaryKey), PartyClassification.class);
      if(partyClassification != null)
      {
        partyClassification = partyClassification.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return partyClassification;
  }

  /** Finds all PartyClassification entities
   *@return    Collection containing all PartyClassification entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyClassificationHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyClassificationHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PartyClassification
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static PartyClassification create(String partyId, String partyTypeId, String partyClassificationTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    PartyClassification partyClassification = null;
    Debug.logInfo("PartyClassificationHelper.create: partyId, partyTypeId: " + partyId + ", " + partyTypeId);
    if(partyId == null || partyTypeId == null) { return null; }

    try { partyClassification = (PartyClassification)MyNarrow.narrow(getPartyClassificationHome().create(partyId, partyTypeId, partyClassificationTypeId, fromDate, thruDate), PartyClassification.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create partyClassification with partyId, partyTypeId: " + partyId + ", " + partyTypeId);
      Debug.logError(ce);
      partyClassification = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return partyClassification;
  }

  /** Updates the corresponding PartyClassification
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static PartyClassification update(String partyId, String partyTypeId, String partyClassificationTypeId, java.util.Date fromDate, java.util.Date thruDate) throws java.rmi.RemoteException
  {
    if(partyId == null || partyTypeId == null) { return null; }
    PartyClassification partyClassification = findByPrimaryKey(partyId, partyTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PartyClassification partyClassificationValue = new PartyClassificationValue();

    if(partyClassificationTypeId != null) { partyClassificationValue.setPartyClassificationTypeId(partyClassificationTypeId); }
    if(fromDate != null) { partyClassificationValue.setFromDate(fromDate); }
    if(thruDate != null) { partyClassificationValue.setThruDate(thruDate); }

    partyClassification.setValueObject(partyClassificationValue);
    return partyClassification;
  }

  /** Removes/deletes the specified  PartyClassification
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
        PartyClassification partyClassification = (PartyClassification) iterator.next();
        Debug.logInfo("Removing partyClassification with partyId:" + partyId);
        partyClassification.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyClassification records by the following parameters:
   *@param  partyId                  Field of the PARTY_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyId(String partyId)
  {
    Debug.logInfo("findByPartyId: partyId:" + partyId);

    Collection collection = null;
    if(partyId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyClassificationHome().findByPartyId(partyId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyClassification
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
        PartyClassification partyClassification = (PartyClassification) iterator.next();
        Debug.logInfo("Removing partyClassification with partyTypeId:" + partyTypeId);
        partyClassification.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyClassification records by the following parameters:
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyTypeId(String partyTypeId)
  {
    Debug.logInfo("findByPartyTypeId: partyTypeId:" + partyTypeId);

    Collection collection = null;
    if(partyTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyClassificationHome().findByPartyTypeId(partyTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  PartyClassification
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   */
  public static void removeByPartyClassificationTypeId(String partyClassificationTypeId)
  {
    if(partyClassificationTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPartyClassificationTypeId(partyClassificationTypeId));

    while(iterator.hasNext())
    {
      try
      {
        PartyClassification partyClassification = (PartyClassification) iterator.next();
        Debug.logInfo("Removing partyClassification with partyClassificationTypeId:" + partyClassificationTypeId);
        partyClassification.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds PartyClassification records by the following parameters:
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPartyClassificationTypeId(String partyClassificationTypeId)
  {
    Debug.logInfo("findByPartyClassificationTypeId: partyClassificationTypeId:" + partyClassificationTypeId);

    Collection collection = null;
    if(partyClassificationTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getPartyClassificationHome().findByPartyClassificationTypeId(partyClassificationTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
