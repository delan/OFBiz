
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Role Type Attribute Entity
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
 *@created    Fri Jul 27 01:18:24 MDT 2001
 *@version    1.0
 */

public interface RoleTypeAttr extends EJBObject
{
  /** Get the primary key of the ROLE_TYPE_ID column of the ROLE_TYPE_ATTR table. */
  public String getRoleTypeId() throws RemoteException;
  
  /** Get the primary key of the NAME column of the ROLE_TYPE_ATTR table. */
  public String getName() throws RemoteException;
  

  /** Get the value object of this RoleTypeAttr class. */
  public RoleTypeAttr getValueObject() throws RemoteException;
  /** Set the values in the value object of this RoleTypeAttr class. */
  public void setValueObject(RoleTypeAttr roleTypeAttrValue) throws RemoteException;


  /** Get the  RoleType entity corresponding to this entity. */
  public RoleType getRoleType() throws RemoteException;
  /** Remove the  RoleType entity corresponding to this entity. */
  public void removeRoleType() throws RemoteException;  

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() throws RemoteException;
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String partyId) throws RemoteException;
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() throws RemoteException;
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String partyId) throws RemoteException;

  /** Get a collection of  PartyRole related entities. */
  public Collection getPartyRoles() throws RemoteException;
  /** Get the  PartyRole keyed by member(s) of this class, and other passed parameters. */
  public PartyRole getPartyRole(String partyId) throws RemoteException;
  /** Remove  PartyRole related entities. */
  public void removePartyRoles() throws RemoteException;
  /** Remove the  PartyRole keyed by member(s) of this class, and other passed parameters. */
  public void removePartyRole(String partyId) throws RemoteException;

}
