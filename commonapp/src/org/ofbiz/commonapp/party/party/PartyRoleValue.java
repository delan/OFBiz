
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Party Role Entity
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
public class PartyRoleValue implements PartyRole
{
  /** The variable of the PARTY_ID column of the PARTY_ROLE table. */
  private String partyId;
  /** The variable of the ROLE_TYPE_ID column of the PARTY_ROLE table. */
  private String roleTypeId;
  /** The variable of the PARTY_ROLE_ID column of the PARTY_ROLE table. */
  private String partyRoleId;

  private PartyRole partyRole;

  public PartyRoleValue()
  {
    this.partyId = null;
    this.roleTypeId = null;
    this.partyRoleId = null;

    this.partyRole = null;
  }

  public PartyRoleValue(PartyRole partyRole) throws RemoteException
  {
    if(partyRole == null) return;
  
    this.partyId = partyRole.getPartyId();
    this.roleTypeId = partyRole.getRoleTypeId();
    this.partyRoleId = partyRole.getPartyRoleId();

    this.partyRole = partyRole;
  }

  public PartyRoleValue(PartyRole partyRole, String partyId, String roleTypeId, String partyRoleId)
  {
    if(partyRole == null) return;
  
    this.partyId = partyId;
    this.roleTypeId = roleTypeId;
    this.partyRoleId = partyRoleId;

    this.partyRole = partyRole;
  }


  /** Get the primary key of the PARTY_ID column of the PARTY_ROLE table. */
  public String getPartyId()  throws RemoteException { return partyId; }

  /** Get the primary key of the ROLE_TYPE_ID column of the PARTY_ROLE table. */
  public String getRoleTypeId()  throws RemoteException { return roleTypeId; }

  /** Get the value of the PARTY_ROLE_ID column of the PARTY_ROLE table. */
  public String getPartyRoleId() throws RemoteException { return partyRoleId; }
  /** Set the value of the PARTY_ROLE_ID column of the PARTY_ROLE table. */
  public void setPartyRoleId(String partyRoleId) throws RemoteException
  {
    this.partyRoleId = partyRoleId;
    if(partyRole!=null) partyRole.setPartyRoleId(partyRoleId);
  }

  /** Get the value object of the PartyRole class. */
  public PartyRole getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyRole class. */
  public void setValueObject(PartyRole valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyRole!=null) partyRole.setValueObject(valueObject);

    if(partyId == null) partyId = valueObject.getPartyId();
    if(roleTypeId == null) roleTypeId = valueObject.getRoleTypeId();
    partyRoleId = valueObject.getPartyRoleId();
  }


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  RoleType entity corresponding to this entity. */
  public RoleType getRoleType() { return RoleTypeHelper.findByPrimaryKey(roleTypeId); }
  /** Remove the  RoleType entity corresponding to this entity. */
  public void removeRoleType() { RoleTypeHelper.removeByPrimaryKey(roleTypeId); }

  /** Get a collection of  RoleTypeAttr related entities. */
  public Collection getRoleTypeAttrs() { return RoleTypeAttrHelper.findByRoleTypeId(roleTypeId); }
  /** Get the  RoleTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public RoleTypeAttr getRoleTypeAttr(String name) { return RoleTypeAttrHelper.findByPrimaryKey(roleTypeId, name); }
  /** Remove  RoleTypeAttr related entities. */
  public void removeRoleTypeAttrs() { RoleTypeAttrHelper.removeByRoleTypeId(roleTypeId); }
  /** Remove the  RoleTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeRoleTypeAttr(String name) { RoleTypeAttrHelper.removeByPrimaryKey(roleTypeId, name); }

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() { return PartyAttributeHelper.findByPartyId(partyId); }
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String name) { return PartyAttributeHelper.findByPrimaryKey(partyId, name); }
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() { PartyAttributeHelper.removeByPartyId(partyId); }
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String name) { PartyAttributeHelper.removeByPrimaryKey(partyId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyRole!=null) return partyRole.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyRole!=null) return partyRole.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyRole!=null) return partyRole.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyRole!=null) return partyRole.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyRole!=null) partyRole.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
