
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Party Attribute Entity
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
 *@created    Wed Jul 18 12:02:47 MDT 2001
 *@version    1.0
 */
public class PartyAttributeValue implements PartyAttribute
{
  /** The variable of the PARTY_ID column of the PARTY_ATTRIBUTE table. */
  private String partyId;
  /** The variable of the NAME column of the PARTY_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the PARTY_ATTRIBUTE table. */
  private String value;

  private PartyAttribute partyAttribute;

  public PartyAttributeValue()
  {
    this.partyId = null;
    this.name = null;
    this.value = null;

    this.partyAttribute = null;
  }

  public PartyAttributeValue(PartyAttribute partyAttribute) throws RemoteException
  {
    if(partyAttribute == null) return;
  
    this.partyId = partyAttribute.getPartyId();
    this.name = partyAttribute.getName();
    this.value = partyAttribute.getValue();

    this.partyAttribute = partyAttribute;
  }

  public PartyAttributeValue(PartyAttribute partyAttribute, String partyId, String name, String value)
  {
    if(partyAttribute == null) return;
  
    this.partyId = partyId;
    this.name = name;
    this.value = value;

    this.partyAttribute = partyAttribute;
  }


  /** Get the primary key of the PARTY_ID column of the PARTY_ATTRIBUTE table. */
  public String getPartyId()  throws RemoteException { return partyId; }

  /** Get the primary key of the NAME column of the PARTY_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the PARTY_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the PARTY_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(partyAttribute!=null) partyAttribute.setValue(value);
  }

  /** Get the value object of the PartyAttribute class. */
  public PartyAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyAttribute class. */
  public void setValueObject(PartyAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyAttribute!=null) partyAttribute.setValueObject(valueObject);

    if(partyId == null) partyId = valueObject.getPartyId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
  }


  /** Get the  Party entity corresponding to this entity. */
  public Party getParty() { return PartyHelper.findByPrimaryKey(partyId); }
  /** Remove the  Party entity corresponding to this entity. */
  public void removeParty() { PartyHelper.removeByPrimaryKey(partyId); }

  /** Get a collection of  PartyTypeAttr related entities. */
  public Collection getPartyTypeAttrs() { return PartyTypeAttrHelper.findByName(name); }
  /** Get the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PartyTypeAttr getPartyTypeAttr(String partyTypeId) { return PartyTypeAttrHelper.findByPrimaryKey(partyTypeId, name); }
  /** Remove  PartyTypeAttr related entities. */
  public void removePartyTypeAttrs() { PartyTypeAttrHelper.removeByName(name); }
  /** Remove the  PartyTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePartyTypeAttr(String partyTypeId) { PartyTypeAttrHelper.removeByPrimaryKey(partyTypeId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyAttribute!=null) return partyAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyAttribute!=null) return partyAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyAttribute!=null) return partyAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyAttribute!=null) return partyAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyAttribute!=null) partyAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
