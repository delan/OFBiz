
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Party Type Entity
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
 *@created    Tue Jul 17 02:08:27 MDT 2001
 *@version    1.0
 */

public interface PartyType extends EJBObject
{
  /** Get the primary key of the PARTY_TYPE_ID column of the PARTY_TYPE table. */
  public String getPartyTypeId() throws RemoteException;
  
  /** Get the value of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public String getParentTypeId() throws RemoteException;
  /** Set the value of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException;
  
  /** Get the value of the HAS_TABLE column of the PARTY_TYPE table. */
  public String getHasTable() throws RemoteException;
  /** Set the value of the HAS_TABLE column of the PARTY_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PARTY_TYPE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PARTY_TYPE table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this PartyType class. */
  public PartyType getValueObject() throws RemoteException;
  /** Set the values in the value object of this PartyType class. */
  public void setValueObject(PartyType partyTypeValue) throws RemoteException;


  /** Get the Parent PartyType entity corresponding to this entity. */
  public PartyType getParentPartyType() throws RemoteException;
  /** Remove the Parent PartyType entity corresponding to this entity. */
  public void removeParentPartyType() throws RemoteException;  

  /** Get a collection of Children PartyType related entities. */
  public Collection getChildrenPartyTypes() throws RemoteException;
  /** Get the Children PartyType keyed by member(s) of this class, and other passed parameters. */
  public PartyType getChildrenPartyType(String partyTypeId) throws RemoteException;
  /** Remove Children PartyType related entities. */
  public void removeChildrenPartyTypes() throws RemoteException;
  /** Remove the Children PartyType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildrenPartyType(String partyTypeId) throws RemoteException;

  /** Get a collection of Sibling PartyType related entities. */
  public Collection getSiblingPartyTypes() throws RemoteException;
  /** Get the Sibling PartyType keyed by member(s) of this class, and other passed parameters. */
  public PartyType getSiblingPartyType(String partyTypeId) throws RemoteException;
  /** Remove Sibling PartyType related entities. */
  public void removeSiblingPartyTypes() throws RemoteException;
  /** Remove the Sibling PartyType keyed by member(s) of this class, and other passed parameters. */
  public void removeSiblingPartyType(String partyTypeId) throws RemoteException;

  /** Get a collection of  PartyTypeAttr related entities. */
  public Collection getPartyTypeAttrs() throws RemoteException;
  /** Get the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PartyTypeAttr getPartyTypeAttr(String name) throws RemoteException;
  /** Remove  PartyTypeAttr related entities. */
  public void removePartyTypeAttrs() throws RemoteException;
  /** Remove the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePartyTypeAttr(String name) throws RemoteException;

  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() throws RemoteException;
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyId) throws RemoteException;
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() throws RemoteException;
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyId) throws RemoteException;

}
