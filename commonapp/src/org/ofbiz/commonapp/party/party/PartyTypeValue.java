
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class PartyTypeValue implements PartyType
{
  /** The variable of the PARTY_TYPE_ID column of the PARTY_TYPE table. */
  private String partyTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PARTY_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PARTY_TYPE table. */
  private String description;

  private PartyType partyType;

  public PartyTypeValue()
  {
    this.partyTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.partyType = null;
  }

  public PartyTypeValue(PartyType partyType) throws RemoteException
  {
    if(partyType == null) return;
  
    this.partyTypeId = partyType.getPartyTypeId();
    this.parentTypeId = partyType.getParentTypeId();
    this.hasTable = partyType.getHasTable();
    this.description = partyType.getDescription();

    this.partyType = partyType;
  }

  public PartyTypeValue(PartyType partyType, String partyTypeId, String parentTypeId, String hasTable, String description)
  {
    if(partyType == null) return;
  
    this.partyTypeId = partyTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.partyType = partyType;
  }


  /** Get the primary key of the PARTY_TYPE_ID column of the PARTY_TYPE table. */
  public String getPartyTypeId()  throws RemoteException { return partyTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PARTY_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(partyType!=null) partyType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PARTY_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PARTY_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(partyType!=null) partyType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PARTY_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PARTY_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(partyType!=null) partyType.setDescription(description);
  }

  /** Get the value object of the PartyType class. */
  public PartyType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyType class. */
  public void setValueObject(PartyType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyType!=null) partyType.setValueObject(valueObject);

    if(partyTypeId == null) partyTypeId = valueObject.getPartyTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyType!=null) return partyType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyType!=null) return partyType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyType!=null) return partyType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyType!=null) return partyType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyType!=null) partyType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
