
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class RoleTypeAttrValue implements RoleTypeAttr
{
  /** The variable of the ROLE_TYPE_ID column of the ROLE_TYPE_ATTR table. */
  private String roleTypeId;
  /** The variable of the NAME column of the ROLE_TYPE_ATTR table. */
  private String name;

  private RoleTypeAttr roleTypeAttr;

  public RoleTypeAttrValue()
  {
    this.roleTypeId = null;
    this.name = null;

    this.roleTypeAttr = null;
  }

  public RoleTypeAttrValue(RoleTypeAttr roleTypeAttr) throws RemoteException
  {
    if(roleTypeAttr == null) return;
  
    this.roleTypeId = roleTypeAttr.getRoleTypeId();
    this.name = roleTypeAttr.getName();

    this.roleTypeAttr = roleTypeAttr;
  }

  public RoleTypeAttrValue(RoleTypeAttr roleTypeAttr, String roleTypeId, String name)
  {
    if(roleTypeAttr == null) return;
  
    this.roleTypeId = roleTypeId;
    this.name = name;

    this.roleTypeAttr = roleTypeAttr;
  }


  /** Get the primary key of the ROLE_TYPE_ID column of the ROLE_TYPE_ATTR table. */
  public String getRoleTypeId()  throws RemoteException { return roleTypeId; }

  /** Get the primary key of the NAME column of the ROLE_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the RoleTypeAttr class. */
  public RoleTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the RoleTypeAttr class. */
  public void setValueObject(RoleTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(roleTypeAttr!=null) roleTypeAttr.setValueObject(valueObject);

    if(roleTypeId == null) roleTypeId = valueObject.getRoleTypeId();
    if(name == null) name = valueObject.getName();
  }


  /** Get the  RoleType entity corresponding to this entity. */
  public RoleType getRoleType() { return RoleTypeHelper.findByPrimaryKey(roleTypeId); }
  /** Remove the  RoleType entity corresponding to this entity. */
  public void removeRoleType() { RoleTypeHelper.removeByPrimaryKey(roleTypeId); }

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() { return PartyAttributeHelper.findByName(name); }
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String partyId) { return PartyAttributeHelper.findByPrimaryKey(partyId, name); }
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() { PartyAttributeHelper.removeByName(name); }
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String partyId) { PartyAttributeHelper.removeByPrimaryKey(partyId, name); }

  /** Get a collection of  PartyRole related entities. */
  public Collection getPartyRoles() { return PartyRoleHelper.findByRoleTypeId(roleTypeId); }
  /** Get the  PartyRole keyed by member(s) of this class, and other passed parameters. */
  public PartyRole getPartyRole(String partyId) { return PartyRoleHelper.findByPrimaryKey(partyId, roleTypeId); }
  /** Remove  PartyRole related entities. */
  public void removePartyRoles() { PartyRoleHelper.removeByRoleTypeId(roleTypeId); }
  /** Remove the  PartyRole keyed by member(s) of this class, and other passed parameters. */
  public void removePartyRole(String partyId) { PartyRoleHelper.removeByPrimaryKey(partyId, roleTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(roleTypeAttr!=null) return roleTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(roleTypeAttr!=null) return roleTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(roleTypeAttr!=null) return roleTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(roleTypeAttr!=null) return roleTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(roleTypeAttr!=null) roleTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
