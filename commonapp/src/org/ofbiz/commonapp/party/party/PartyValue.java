
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Party Entity
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
 *@created    Tue Jul 03 01:11:41 MDT 2001
 *@version    1.0
 */
public class PartyValue implements Party
{

  /**
   *  The variable of the PARTY_ID column of the PARTY table.
   */
  private String partyId;


  private Party party;

  public PartyValue()
  {

    this.partyId = null;

    this.party = null;
  }

  public PartyValue(Party party) throws RemoteException
  {
    if(party == null) return;


    this.partyId = party.getPartyId();

    this.party = party;
  }

  public PartyValue(Party party, String partyId)
  {
    if(party == null) return;


    this.partyId = partyId;

    this.party = party;
  }


  /**
   *  Get the primary key of the PARTY_ID column of the PARTY table.
   */
  public String getPartyId()  throws RemoteException
  {
    return partyId;
  }
  

  /**
   *  Get the value object of the Party class.
   */
  public Party getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the Party class.
   */
  public void setValueObject(Party valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(party!=null) party.setValueObject(valueObject);

    if(partyId == null) partyId = valueObject.getPartyId();
  
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(party!=null) return party.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(party!=null) return party.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(party!=null) return party.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(party!=null) return party.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(party!=null) party.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
