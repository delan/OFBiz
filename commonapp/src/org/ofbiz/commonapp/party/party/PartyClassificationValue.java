
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Party Classification Entity
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
 *@created    Wed Jul 18 12:02:44 MDT 2001
 *@version    1.0
 */
public class PartyClassificationValue implements PartyClassification
{
  /** The variable of the PARTY_ID column of the PARTY_CLASSIFICATION table. */
  private String partyId;
  /** The variable of the PARTY_TYPE_ID column of the PARTY_CLASSIFICATION table. */
  private String partyTypeId;
  /** The variable of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION table. */
  private String partyClassificationTypeId;
  /** The variable of the FROM_DATE column of the PARTY_CLASSIFICATION table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the PARTY_CLASSIFICATION table. */
  private java.util.Date thruDate;

  private PartyClassification partyClassification;

  public PartyClassificationValue()
  {
    this.partyId = null;
    this.partyTypeId = null;
    this.partyClassificationTypeId = null;
    this.fromDate = null;
    this.thruDate = null;

    this.partyClassification = null;
  }

  public PartyClassificationValue(PartyClassification partyClassification) throws RemoteException
  {
    if(partyClassification == null) return;
  
    this.partyId = partyClassification.getPartyId();
    this.partyTypeId = partyClassification.getPartyTypeId();
    this.partyClassificationTypeId = partyClassification.getPartyClassificationTypeId();
    this.fromDate = partyClassification.getFromDate();
    this.thruDate = partyClassification.getThruDate();

    this.partyClassification = partyClassification;
  }

  public PartyClassificationValue(PartyClassification partyClassification, String partyId, String partyTypeId, String partyClassificationTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    if(partyClassification == null) return;
  
    this.partyId = partyId;
    this.partyTypeId = partyTypeId;
    this.partyClassificationTypeId = partyClassificationTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;

    this.partyClassification = partyClassification;
  }


  /** Get the primary key of the PARTY_ID column of the PARTY_CLASSIFICATION table. */
  public String getPartyId()  throws RemoteException { return partyId; }

  /** Get the primary key of the PARTY_TYPE_ID column of the PARTY_CLASSIFICATION table. */
  public String getPartyTypeId()  throws RemoteException { return partyTypeId; }

  /** Get the value of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION table. */
  public String getPartyClassificationTypeId() throws RemoteException { return partyClassificationTypeId; }
  /** Set the value of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION table. */
  public void setPartyClassificationTypeId(String partyClassificationTypeId) throws RemoteException
  {
    this.partyClassificationTypeId = partyClassificationTypeId;
    if(partyClassification!=null) partyClassification.setPartyClassificationTypeId(partyClassificationTypeId);
  }

  /** Get the value of the FROM_DATE column of the PARTY_CLASSIFICATION table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the PARTY_CLASSIFICATION table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(partyClassification!=null) partyClassification.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the PARTY_CLASSIFICATION table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the PARTY_CLASSIFICATION table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(partyClassification!=null) partyClassification.setThruDate(thruDate);
  }

  /** Get the value object of the PartyClassification class. */
  public PartyClassification getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyClassification class. */
  public void setValueObject(PartyClassification valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyClassification!=null) partyClassification.setValueObject(valueObject);

    if(partyId == null) partyId = valueObject.getPartyId();
    if(partyTypeId == null) partyTypeId = valueObject.getPartyTypeId();
    partyClassificationTypeId = valueObject.getPartyClassificationTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
  }


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get the  PartyType entity corresponding to this entity. */
  public PartyType getPartyType() { return PartyTypeHelper.findByPrimaryKey(partyTypeId); }
  /** Remove the  PartyType entity corresponding to this entity. */
  public void removePartyType() { PartyTypeHelper.removeByPrimaryKey(partyTypeId); }

  /** Get the  PartyClassificationType entity corresponding to this entity. */
  public PartyClassificationType getPartyClassificationType() { return PartyClassificationTypeHelper.findByPrimaryKey(partyClassificationTypeId); }
  /** Remove the  PartyClassificationType entity corresponding to this entity. */
  public void removePartyClassificationType() { PartyClassificationTypeHelper.removeByPrimaryKey(partyClassificationTypeId); }

  /** Get a collection of  PartyTypeAttr related entities. */
  public Collection getPartyTypeAttrs() { return PartyTypeAttrHelper.findByPartyTypeId(partyTypeId); }
  /** Get the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PartyTypeAttr getPartyTypeAttr(String name) { return PartyTypeAttrHelper.findByPrimaryKey(partyTypeId, name); }
  /** Remove  PartyTypeAttr related entities. */
  public void removePartyTypeAttrs() { PartyTypeAttrHelper.removeByPartyTypeId(partyTypeId); }
  /** Remove the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePartyTypeAttr(String name) { PartyTypeAttrHelper.removeByPrimaryKey(partyTypeId, name); }

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() { return PartyAttributeHelper.findByPartyId(partyId); }
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String name) { return PartyAttributeHelper.findByPrimaryKey(partyId, name); }
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() { PartyAttributeHelper.removeByPartyId(partyId); }
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String name) { PartyAttributeHelper.removeByPrimaryKey(partyId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyClassification!=null) return partyClassification.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyClassification!=null) return partyClassification.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyClassification!=null) return partyClassification.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyClassification!=null) return partyClassification.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyClassification!=null) partyClassification.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
