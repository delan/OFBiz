
package org.ofbiz.commonapp.party.party;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Party Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Party Entity EJB; acts as a proxy for the Home interface
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
public class PartyHelper
{

  /** A static variable to cache the Home object for the Party EJB */
  private static PartyHome partyHome = null;

  /** Initializes the partyHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PartyHome instance for the default EJB server
   */
  public static PartyHome getPartyHome()
  {
    if(partyHome == null) //don't want to block here
    {
      synchronized(PartyHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(partyHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.party.party.PartyHome");
            partyHome = (PartyHome)MyNarrow.narrow(homeObject, PartyHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("party home obtained " + partyHome);
        }
      }
    }
    return partyHome;
  }




  /** Remove the Party corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Party party = findByPrimaryKey(primaryKey);
    try
    {
      if(party != null)
      {
        party.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Party by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Party corresponding to the primaryKey
   */
  public static Party findByPrimaryKey(java.lang.String primaryKey)
  {
    Party party = null;
    Debug.logInfo("PartyHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      party = (Party)MyNarrow.narrow(getPartyHome().findByPrimaryKey(primaryKey), Party.class);
      if(party != null)
      {
        party = party.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return party;
  }

  /** Finds all Party entities
   *@return    Collection containing all Party entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PartyHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPartyHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Party
   *@param  partyId                  Field of the PARTY_ID column.
   *@return                Description of the Returned Value
   */
  public static Party create(String partyId)
  {
    Party party = null;
    Debug.logInfo("PartyHelper.create: partyId: " + partyId);
    if(partyId == null) { return null; }

    try { party = (Party)MyNarrow.narrow(getPartyHome().create(partyId), Party.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create party with partyId: " + partyId);
      Debug.logError(ce);
      party = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return party;
  }

  /** Updates the corresponding Party
   *@param  partyId                  Field of the PARTY_ID column.
   *@return                Description of the Returned Value
   */
  public static Party update(String partyId) throws java.rmi.RemoteException
  {
    if(partyId == null) { return null; }
    Party party = findByPrimaryKey(partyId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Party partyValue = new PartyValue();


    party.setValueObject(partyValue);
    return party;
  }


}
