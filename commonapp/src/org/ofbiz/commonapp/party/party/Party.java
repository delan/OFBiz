
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.security.login.*;

/**
 * <p><b>Title:</b> Party Entity
 * <p><b>Description:</b> None
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
 *@created    Tue Jul 17 02:08:15 MDT 2001
 *@version    1.0
 */

public interface Party extends EJBObject
{
  /** Get the primary key of the PARTY_ID column of the PARTY table. */
  public String getPartyId() throws RemoteException;
  

  /** Get the value object of this Party class. */
  public Party getValueObject() throws RemoteException;
  /** Set the values in the value object of this Party class. */
  public void setValueObject(Party partyValue) throws RemoteException;


  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() throws RemoteException;
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyTypeId) throws RemoteException;
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() throws RemoteException;
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyTypeId) throws RemoteException;

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() throws RemoteException;
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String name) throws RemoteException;
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() throws RemoteException;
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String name) throws RemoteException;

  /** Get a collection of  UserLogin related entities. */
  public Collection getUserLogins() throws RemoteException;
  /** Get the  UserLogin keyed by member(s) of this class, and other passed parameters. */
  public UserLogin getUserLogin(String userLoginId) throws RemoteException;
  /** Remove  UserLogin related entities. */
  public void removeUserLogins() throws RemoteException;
  /** Remove the  UserLogin keyed by member(s) of this class, and other passed parameters. */
  public void removeUserLogin(String userLoginId) throws RemoteException;

}
