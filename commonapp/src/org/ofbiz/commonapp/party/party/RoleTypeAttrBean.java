
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
public class RoleTypeAttrBean implements EntityBean
{
  /** The variable for the ROLE_TYPE_ID column of the ROLE_TYPE_ATTR table. */
  public String roleTypeId;
  /** The variable for the NAME column of the ROLE_TYPE_ATTR table. */
  public String name;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the RoleTypeAttrBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key ROLE_TYPE_ID column of the ROLE_TYPE_ATTR table. */
  public String getRoleTypeId() { return roleTypeId; }

  /** Get the primary key NAME column of the ROLE_TYPE_ATTR table. */
  public String getName() { return name; }

  /** Sets the values from ValueObject attribute of the RoleTypeAttrBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(RoleTypeAttr valueObject)
  {
  }

  /** Gets the ValueObject attribute of the RoleTypeAttrBean object
   *@return    The ValueObject value
   */
  public RoleTypeAttr getValueObject()
  {
    if(this.entityContext != null)
    {
      return new RoleTypeAttrValue((RoleTypeAttr)this.entityContext.getEJBObject(), roleTypeId, name);
    }
    else { return null; }
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


  /** Description of the Method
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.RoleTypeAttrPK ejbCreate(String roleTypeId, String name) throws CreateException
  {
    this.roleTypeId = roleTypeId;
    this.name = name;
    return null;
  }

  /** Description of the Method
   *@param  roleTypeId                  Field of the ROLE_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String roleTypeId, String name) throws CreateException {}

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
