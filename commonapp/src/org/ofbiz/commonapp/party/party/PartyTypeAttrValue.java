
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Party Type Attribute Entity
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
public class PartyTypeAttrValue implements PartyTypeAttr
{
  /** The variable of the PARTY_TYPE_ID column of the PARTY_TYPE_ATTR table. */
  private String partyTypeId;
  /** The variable of the NAME column of the PARTY_TYPE_ATTR table. */
  private String name;

  private PartyTypeAttr partyTypeAttr;

  public PartyTypeAttrValue()
  {
    this.partyTypeId = null;
    this.name = null;

    this.partyTypeAttr = null;
  }

  public PartyTypeAttrValue(PartyTypeAttr partyTypeAttr) throws RemoteException
  {
    if(partyTypeAttr == null) return;
  
    this.partyTypeId = partyTypeAttr.getPartyTypeId();
    this.name = partyTypeAttr.getName();

    this.partyTypeAttr = partyTypeAttr;
  }

  public PartyTypeAttrValue(PartyTypeAttr partyTypeAttr, String partyTypeId, String name)
  {
    if(partyTypeAttr == null) return;
  
    this.partyTypeId = partyTypeId;
    this.name = name;

    this.partyTypeAttr = partyTypeAttr;
  }


  /** Get the primary key of the PARTY_TYPE_ID column of the PARTY_TYPE_ATTR table. */
  public String getPartyTypeId()  throws RemoteException { return partyTypeId; }

  /** Get the primary key of the NAME column of the PARTY_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the PartyTypeAttr class. */
  public PartyTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PartyTypeAttr class. */
  public void setValueObject(PartyTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyTypeAttr!=null) partyTypeAttr.setValueObject(valueObject);

    if(partyTypeId == null) partyTypeId = valueObject.getPartyTypeId();
    if(name == null) name = valueObject.getName();
  }


  /** Get the  PartyType entity corresponding to this entity. */
  public PartyType getPartyType() { return PartyTypeHelper.findByPrimaryKey(partyTypeId); }
  /** Remove the  PartyType entity corresponding to this entity. */
  public void removePartyType() { PartyTypeHelper.removeByPrimaryKey(partyTypeId); }

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() { return PartyAttributeHelper.findByName(name); }
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String partyId) { return PartyAttributeHelper.findByPrimaryKey(partyId, name); }
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() { PartyAttributeHelper.removeByName(name); }
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String partyId) { PartyAttributeHelper.removeByPrimaryKey(partyId, name); }

  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() { return PartyClassificationHelper.findByPartyTypeId(partyTypeId); }
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyId) { return PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId); }
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() { PartyClassificationHelper.removeByPartyTypeId(partyTypeId); }
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyId) { PartyClassificationHelper.removeByPrimaryKey(partyId, partyTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyTypeAttr!=null) return partyTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyTypeAttr!=null) return partyTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyTypeAttr!=null) return partyTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyTypeAttr!=null) return partyTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyTypeAttr!=null) partyTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
