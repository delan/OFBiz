
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Party Type Attribute Entity
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
 *@created    Tue Jul 17 02:08:29 MDT 2001
 *@version    1.0
 */

public interface PartyTypeAttr extends EJBObject
{
  /** Get the primary key of the PARTY_TYPE_ID column of the PARTY_TYPE_ATTR table. */
  public String getPartyTypeId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the PARTY_TYPE_ATTR table. */
  public String getName() throws RemoteException;
  

  /** Get the value object of this PartyTypeAttr class. */
  public PartyTypeAttr getValueObject() throws RemoteException;
  /** Set the values in the value object of this PartyTypeAttr class. */
  public void setValueObject(PartyTypeAttr partyTypeAttrValue) throws RemoteException;


  /** Get the  PartyType entity corresponding to this entity. */
  public PartyType getPartyType() throws RemoteException;
  /** Remove the  PartyType entity corresponding to this entity. */
  public void removePartyType() throws RemoteException;  

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() throws RemoteException;
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String partyId) throws RemoteException;
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() throws RemoteException;
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String partyId) throws RemoteException;

  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() throws RemoteException;
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyId) throws RemoteException;
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() throws RemoteException;
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyId) throws RemoteException;

}
