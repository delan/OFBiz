
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

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
 *@created    Tue Jul 03 01:11:45 MDT 2001
 *@version    1.0
 */

public interface PartyAttribute extends EJBObject
{

  
  /**
   *  Get the primary key of the PARTY_ID column of the PARTY_ATTRIBUTE table.
   */
  public String getPartyId() throws RemoteException;
  

  
  /**
   *  Get the primary key of the NAME column of the PARTY_ATTRIBUTE table.
   */
  public String getName() throws RemoteException;
  

  
  /**
   *  Get the value of the VALUE column of the PARTY_ATTRIBUTE table.
   */
  public String getValue() throws RemoteException;
  /**
   *  Set the value of the VALUE column of the PARTY_ATTRIBUTE table.
   */
  public void setValue(String value) throws RemoteException;
  


  /**
   *  Get the value object of this PartyAttribute class.
   */
  public PartyAttribute getValueObject() throws RemoteException;
  /**
   *  Set the values in the value object of this PartyAttribute class.
   */
  public void setValueObject(PartyAttribute partyAttributeValue) throws RemoteException;
}
