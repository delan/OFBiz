
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Party Classification Type Entity
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
 *@created    Wed Jul 18 12:02:45 MDT 2001
 *@version    1.0
 */
public class PartyClassificationTypeValue implements PartyClassificationType
{
  /** The variable of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION_TYPE table. */
  private String partyClassificationTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PARTY_CLASSIFICATION_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PARTY_CLASSIFICATION_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PARTY_CLASSIFICATION_TYPE table. */
  private String description;

  private PartyClassificationType partyClassificationType;

  public PartyClassificationTypeValue()
  {
    this.partyClassificationTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.partyClassificationType = null;
  }

  public PartyClassificationTypeValue(PartyClassificationType partyClassificationType) throws RemoteException
  {
    if(partyClassificationType == null) return;
  
    this.partyClassificationTypeId = partyClassificationType.getPartyClassificationTypeId();
    this.parentTypeId = partyClassificationType.getParentTypeId();
    this.hasTable = partyClassificationType.getHasTable();
    this.description = partyClassificationType.getDescription();

    this.partyClassificationType = partyClassificationType;
  }

  public PartyClassificationTypeValue(PartyClassificationType partyClassificationType, String partyClassificationTypeId, String parentTypeId, String hasTable, String description)
  {
    if(partyClassificationType == null) return;
  
    this.partyClassificationTypeId = partyClassificationTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.partyClassificationType = partyClassificationType;
  }


  /** Get the primary key of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION_TYPE table. */
  public String getPartyClassificationTypeId()  throws RemoteException { return partyClassificationTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PARTY_CLASSIFICATION_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PARTY_CLASSIFICATION_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(partyClassificationType!=null) partyClassificationType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PARTY_CLASSIFICATION_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PARTY_CLASSIFICATION_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(partyClassificationType!=null) partyClassificationType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PARTY_CLASSIFICATION_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PARTY_CLASSIFICATION_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(partyClassificationType!=null) partyClassificationType.setDescription(description);
  }

  /** Get the value object of the PartyClassificationType class. */
  public PartyClassificationType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyClassificationType class. */
  public void setValueObject(PartyClassificationType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyClassificationType!=null) partyClassificationType.setValueObject(valueObject);

    if(partyClassificationTypeId == null) partyClassificationTypeId = valueObject.getPartyClassificationTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the  PartyClassificationType entity corresponding to this entity. */
  public PartyClassificationType getPartyClassificationType() { return PartyClassificationTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the  PartyClassificationType entity corresponding to this entity. */
  public void removePartyClassificationType() { PartyClassificationTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of  PartyClassificationType related entities. */
  public Collection getPartyClassificationTypes() { return PartyClassificationTypeHelper.findByParentTypeId(partyClassificationTypeId); }
  /** Get the  PartyClassificationType keyed by member(s) of this class, and other passed parameters. */
  public PartyClassificationType getPartyClassificationType(String partyClassificationTypeId) { return PartyClassificationTypeHelper.findByPrimaryKey(partyClassificationTypeId); }
  /** Remove  PartyClassificationType related entities. */
  public void removePartyClassificationTypes() { PartyClassificationTypeHelper.removeByParentTypeId(partyClassificationTypeId); }
  /** Remove the  PartyClassificationType keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassificationType(String partyClassificationTypeId) { PartyClassificationTypeHelper.removeByPrimaryKey(partyClassificationTypeId); }

  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() { return PartyClassificationHelper.findByPartyClassificationTypeId(partyClassificationTypeId); }
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyId, String partyTypeId) { return PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId); }
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() { PartyClassificationHelper.removeByPartyClassificationTypeId(partyClassificationTypeId); }
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyId, String partyTypeId) { PartyClassificationHelper.removeByPrimaryKey(partyId, partyTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyClassificationType!=null) return partyClassificationType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyClassificationType!=null) return partyClassificationType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyClassificationType!=null) return partyClassificationType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyClassificationType!=null) return partyClassificationType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyClassificationType!=null) partyClassificationType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
