
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


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
public class PartyRoleBean implements EntityBean
{
  /** The variable for the PARTY_ID column of the PARTY_ROLE table. */
  public String partyId;
  /** The variable for the ROLE_TYPE_ID column of the PARTY_ROLE table. */
  public String roleTypeId;
  /** The variable for the PARTY_ROLE_ID column of the PARTY_ROLE table. */
  public String partyRoleId;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the PartyRoleBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PARTY_ID column of the PARTY_ROLE table. */
  public String getPartyId() { return partyId; }

  /** Get the primary key ROLE_TYPE_ID column of the PARTY_ROLE table. */
  public String getRoleTypeId() { return roleTypeId; }

  /** Get the value of the PARTY_ROLE_ID column of the PARTY_ROLE table. */
  public String getPartyRoleId() { return partyRoleId; }
  /** Set the value of the PARTY_ROLE_ID column of the PARTY_ROLE table. */
  public void setPartyRoleId(String partyRoleId)
  {
    this.partyRoleId = partyRoleId;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the PartyRoleBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(PartyRole valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getPartyRoleId() != null)
      {
        this.partyRoleId = valueObject.getPartyRoleId();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the PartyRoleBean object
   *@return    The ValueObject value
   */
  public PartyRole getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PartyRoleValue((PartyRole)this.entityContext.getEJBObject(), partyId, roleTypeId, partyRoleId);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  partyRoleId                  Field of the PARTY_ROLE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.PartyRolePK ejbCreate(String partyId, String roleTypeId, String partyRoleId) throws CreateException
  {
    this.partyId = partyId;
    this.roleTypeId = roleTypeId;
    this.partyRoleId = partyRoleId;
    return null;
  }

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.PartyRolePK ejbCreate(String partyId, String roleTypeId) throws CreateException
  {
    return ejbCreate(partyId, roleTypeId, null);
  }

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  partyRoleId                  Field of the PARTY_ROLE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String roleTypeId, String partyRoleId) throws CreateException {}

  /** Description of the Method
   *@param  partyId                  Field of the PARTY_ID column.
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String roleTypeId) throws CreateException
  {
    ejbPostCreate(partyId, roleTypeId, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
