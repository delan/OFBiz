
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
 *@created    Tue Jul 17 02:08:28 MDT 2001
 *@version    1.0
 */
public class PartyTypeBean implements EntityBean
{
  /** The variable for the PARTY_TYPE_ID column of the PARTY_TYPE table. */
  public String partyTypeId;
  /** The variable for the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public String parentTypeId;
  /** The variable for the HAS_TABLE column of the PARTY_TYPE table. */
  public String hasTable;
  /** The variable for the DESCRIPTION column of the PARTY_TYPE table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the PartyTypeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PARTY_TYPE_ID column of the PARTY_TYPE table. */
  public String getPartyTypeId() { return partyTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public String getParentTypeId() { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public void setParentTypeId(String parentTypeId)
  {
    this.parentTypeId = parentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the HAS_TABLE column of the PARTY_TYPE table. */
  public String getHasTable() { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PARTY_TYPE table. */
  public void setHasTable(String hasTable)
  {
    this.hasTable = hasTable;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the PARTY_TYPE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the PARTY_TYPE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the PartyTypeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(PartyType valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getParentTypeId() != null)
      {
        this.parentTypeId = valueObject.getParentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getHasTable() != null)
      {
        this.hasTable = valueObject.getHasTable();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the PartyTypeBean object
   *@return    The ValueObject value
   */
  public PartyType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PartyTypeValue((PartyType)this.entityContext.getEJBObject(), partyTypeId, parentTypeId, hasTable, description);
    }
    else { return null; }
  }


  /** Get the Parent PartyType entity corresponding to this entity. */
  public PartyType getParentPartyType() { return PartyTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent PartyType entity corresponding to this entity. */
  public void removeParentPartyType() { PartyTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Children PartyType related entities. */
  public Collection getChildrenPartyTypes() { return PartyTypeHelper.findByParentTypeId(partyTypeId); }
  /** Get the Children PartyType keyed by member(s) of this class, and other passed parameters. */
  public PartyType getChildrenPartyType(String partyTypeId) { return PartyTypeHelper.findByPrimaryKey(partyTypeId); }
  /** Remove Children PartyType related entities. */
  public void removeChildrenPartyTypes() { PartyTypeHelper.removeByParentTypeId(partyTypeId); }
  /** Remove the Children PartyType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildrenPartyType(String partyTypeId) { PartyTypeHelper.removeByPrimaryKey(partyTypeId); }

  /** Get a collection of Sibling PartyType related entities. */
  public Collection getSiblingPartyTypes() { return PartyTypeHelper.findByParentTypeId(parentTypeId); }
  /** Get the Sibling PartyType keyed by member(s) of this class, and other passed parameters. */
  public PartyType getSiblingPartyType(String partyTypeId) { return PartyTypeHelper.findByPrimaryKey(partyTypeId); }
  /** Remove Sibling PartyType related entities. */
  public void removeSiblingPartyTypes() { PartyTypeHelper.removeByParentTypeId(parentTypeId); }
  /** Remove the Sibling PartyType keyed by member(s) of this class, and other passed parameters. */
  public void removeSiblingPartyType(String partyTypeId) { PartyTypeHelper.removeByPrimaryKey(partyTypeId); }

  /** Get a collection of  PartyTypeAttr related entities. */
  public Collection getPartyTypeAttrs() { return PartyTypeAttrHelper.findByPartyTypeId(partyTypeId); }
  /** Get the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PartyTypeAttr getPartyTypeAttr(String name) { return PartyTypeAttrHelper.findByPrimaryKey(partyTypeId, name); }
  /** Remove  PartyTypeAttr related entities. */
  public void removePartyTypeAttrs() { PartyTypeAttrHelper.removeByPartyTypeId(partyTypeId); }
  /** Remove the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePartyTypeAttr(String name) { PartyTypeAttrHelper.removeByPrimaryKey(partyTypeId, name); }

  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() { return PartyClassificationHelper.findByPartyTypeId(partyTypeId); }
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyId) { return PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId); }
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() { PartyClassificationHelper.removeByPartyTypeId(partyTypeId); }
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyId) { PartyClassificationHelper.removeByPrimaryKey(partyId, partyTypeId); }


  /** Description of the Method
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String partyTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {
    this.partyTypeId = partyTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String partyTypeId) throws CreateException
  {
    return ejbCreate(partyTypeId, null, null, null);
  }

  /** Description of the Method
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyTypeId, String parentTypeId, String hasTable, String description) throws CreateException {}

  /** Description of the Method
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyTypeId) throws CreateException
  {
    ejbPostCreate(partyTypeId, null, null, null);
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
