
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

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
 *@created    Tue Jul 03 01:11:44 MDT 2001
 *@version    1.0
 */
public class PartyTypeValue implements PartyType
{

  /**
   *  The variable of the PARTY_TYPE_ID column of the PARTY_TYPE table.
   */
  private String partyTypeId;

  /**
   *  The variable of the PARENT_TYPE_ID column of the PARTY_TYPE table.
   */
  private String parentTypeId;

  /**
   *  The variable of the HAS_TABLE column of the PARTY_TYPE table.
   */
  private String hasTable;

  /**
   *  The variable of the DESCRIPTION column of the PARTY_TYPE table.
   */
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


  /**
   *  Get the primary key of the PARTY_TYPE_ID column of the PARTY_TYPE table.
   */
  public String getPartyTypeId()  throws RemoteException
  {
    return partyTypeId;
  }
  
  /**
   *  Get the value of the PARENT_TYPE_ID column of the PARTY_TYPE table.
   */
  public String getParentTypeId() throws RemoteException
  {
    return parentTypeId;
  }
  /**
   *  Set the value of the PARENT_TYPE_ID column of the PARTY_TYPE table.
   */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(partyType!=null) partyType.setParentTypeId(parentTypeId);
  }
  
  /**
   *  Get the value of the HAS_TABLE column of the PARTY_TYPE table.
   */
  public String getHasTable() throws RemoteException
  {
    return hasTable;
  }
  /**
   *  Set the value of the HAS_TABLE column of the PARTY_TYPE table.
   */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(partyType!=null) partyType.setHasTable(hasTable);
  }
  
  /**
   *  Get the value of the DESCRIPTION column of the PARTY_TYPE table.
   */
  public String getDescription() throws RemoteException
  {
    return description;
  }
  /**
   *  Set the value of the DESCRIPTION column of the PARTY_TYPE table.
   */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(partyType!=null) partyType.setDescription(description);
  }
  

  /**
   *  Get the value object of the PartyType class.
   */
  public PartyType getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the PartyType class.
   */
  public void setValueObject(PartyType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(partyType!=null) partyType.setValueObject(valueObject);

    if(partyTypeId == null) partyTypeId = valueObject.getPartyTypeId();
  
  
    parentTypeId = valueObject.getParentTypeId();
  
    hasTable = valueObject.getHasTable();
  
    description = valueObject.getDescription();
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(partyType!=null) return partyType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(partyType!=null) return partyType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(partyType!=null) return partyType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(partyType!=null) return partyType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(partyType!=null) partyType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
