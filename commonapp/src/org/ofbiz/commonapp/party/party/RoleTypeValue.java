
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Role Type Entity
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
public class RoleTypeValue implements RoleType
{
  /** The variable of the ROLE_TYPE_ID column of the ROLE_TYPE table. */
  private String roleTypeId;
  /** The variable of the PARENT_TYPE_ID column of the ROLE_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the ROLE_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the ROLE_TYPE table. */
  private String description;

  private RoleType roleType;

  public RoleTypeValue()
  {
    this.roleTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.roleType = null;
  }

  public RoleTypeValue(RoleType roleType) throws RemoteException
  {
    if(roleType == null) return;
  
    this.roleTypeId = roleType.getRoleTypeId();
    this.parentTypeId = roleType.getParentTypeId();
    this.hasTable = roleType.getHasTable();
    this.description = roleType.getDescription();

    this.roleType = roleType;
  }

  public RoleTypeValue(RoleType roleType, String roleTypeId, String parentTypeId, String hasTable, String description)
  {
    if(roleType == null) return;
  
    this.roleTypeId = roleTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.roleType = roleType;
  }


  /** Get the primary key of the ROLE_TYPE_ID column of the ROLE_TYPE table. */
  public String getRoleTypeId()  throws RemoteException { return roleTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the ROLE_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the ROLE_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(roleType!=null) roleType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the ROLE_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the ROLE_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(roleType!=null) roleType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the ROLE_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the ROLE_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(roleType!=null) roleType.setDescription(description);
  }

  /** Get the value object of the RoleType class. */
  public RoleType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the RoleType class. */
  public void setValueObject(RoleType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(roleType!=null) roleType.setValueObject(valueObject);

    if(roleTypeId == null) roleTypeId = valueObject.getRoleTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent RoleType entity corresponding to this entity. */
  public RoleType getParentRoleType() { return RoleTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent RoleType entity corresponding to this entity. */
  public void removeParentRoleType() { RoleTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child RoleType related entities. */
  public Collection getChildRoleTypes() { return RoleTypeHelper.findByParentTypeId(roleTypeId); }
  /** Get the Child RoleType keyed by member(s) of this class, and other passed parameters. */
  public RoleType getChildRoleType(String roleTypeId) { return RoleTypeHelper.findByPrimaryKey(roleTypeId); }
  /** Remove Child RoleType related entities. */
  public void removeChildRoleTypes() { RoleTypeHelper.removeByParentTypeId(roleTypeId); }
  /** Remove the Child RoleType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildRoleType(String roleTypeId) { RoleTypeHelper.removeByPrimaryKey(roleTypeId); }

  /** Get a collection of  RoleTypeAttr related entities. */
  public Collection getRoleTypeAttrs() { return RoleTypeAttrHelper.findByRoleTypeId(roleTypeId); }
  /** Get the  RoleTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public RoleTypeAttr getRoleTypeAttr(String name) { return RoleTypeAttrHelper.findByPrimaryKey(roleTypeId, name); }
  /** Remove  RoleTypeAttr related entities. */
  public void removeRoleTypeAttrs() { RoleTypeAttrHelper.removeByRoleTypeId(roleTypeId); }
  /** Remove the  RoleTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeRoleTypeAttr(String name) { RoleTypeAttrHelper.removeByPrimaryKey(roleTypeId, name); }

  /** Get a collection of  PartyRole related entities. */
  public Collection getPartyRoles() { return PartyRoleHelper.findByRoleTypeId(roleTypeId); }
  /** Get the  PartyRole keyed by member(s) of this class, and other passed parameters. */
  public PartyRole getPartyRole(String partyId) { return PartyRoleHelper.findByPrimaryKey(partyId, roleTypeId); }
  /** Remove  PartyRole related entities. */
  public void removePartyRoles() { PartyRoleHelper.removeByRoleTypeId(roleTypeId); }
  /** Remove the  PartyRole keyed by member(s) of this class, and other passed parameters. */
  public void removePartyRole(String partyId) { PartyRoleHelper.removeByPrimaryKey(partyId, roleTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(roleType!=null) return roleType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(roleType!=null) return roleType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(roleType!=null) return roleType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(roleType!=null) return roleType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(roleType!=null) roleType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
