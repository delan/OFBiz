
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

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
 *@created    Tue Jul 03 01:11:42 MDT 2001
 *@version    1.0
 */
public class PartyClassificationBean implements EntityBean
{

  /**
   *  The variable for the PARTY_ID column of the PARTY_CLASSIFICATION table.
   */
  public String partyId;

  /**
   *  The variable for the PARTY_TYPE_ID column of the PARTY_CLASSIFICATION table.
   */
  public String partyTypeId;

  /**
   *  The variable for the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION table.
   */
  public String partyClassificationTypeId;

  /**
   *  The variable for the FROM_DATE column of the PARTY_CLASSIFICATION table.
   */
  public java.util.Date fromDate;

  /**
   *  The variable for the THRU_DATE column of the PARTY_CLASSIFICATION table.
   */
  public java.util.Date thruDate;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the PartyClassificationBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key PARTY_ID column of the PARTY_CLASSIFICATION table.
   */
  public String getPartyId()
  {
    return partyId;
  }
  

  
  /**
   *  Get the primary key PARTY_TYPE_ID column of the PARTY_CLASSIFICATION table.
   */
  public String getPartyTypeId()
  {
    return partyTypeId;
  }
  

  
  /**
   *  Get the value of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION table.
   */
  public String getPartyClassificationTypeId()
  {
    return partyClassificationTypeId;
  }
  /**
   *  Set the value of the PARTY_CLASSIFICATION_TYPE_ID column of the PARTY_CLASSIFICATION table.
   */
  public void setPartyClassificationTypeId(String partyClassificationTypeId)
  {
    this.partyClassificationTypeId = partyClassificationTypeId;
  }
  

  
  /**
   *  Get the value of the FROM_DATE column of the PARTY_CLASSIFICATION table.
   */
  public java.util.Date getFromDate()
  {
    return fromDate;
  }
  /**
   *  Set the value of the FROM_DATE column of the PARTY_CLASSIFICATION table.
   */
  public void setFromDate(java.util.Date fromDate)
  {
    this.fromDate = fromDate;
  }
  

  
  /**
   *  Get the value of the THRU_DATE column of the PARTY_CLASSIFICATION table.
   */
  public java.util.Date getThruDate()
  {
    return thruDate;
  }
  /**
   *  Set the value of the THRU_DATE column of the PARTY_CLASSIFICATION table.
   */
  public void setThruDate(java.util.Date thruDate)
  {
    this.thruDate = thruDate;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the PartyClassificationBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(PartyClassification valueObject)
  {

    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
    
      if(valueObject.getPartyClassificationTypeId() != null)
        this.partyClassificationTypeId = valueObject.getPartyClassificationTypeId();
      if(valueObject.getFromDate() != null)
        this.fromDate = valueObject.getFromDate();
      if(valueObject.getThruDate() != null)
        this.thruDate = valueObject.getThruDate();
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }

  }

  /**
   *  Gets the ValueObject attribute of the PartyClassificationBean object
   *
   *@return    The ValueObject value
   */
  public PartyClassification getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PartyClassificationValue((PartyClassification)this.entityContext.getEJBObject(), partyId, partyTypeId, partyClassificationTypeId, fromDate, thruDate);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.PartyClassificationPK ejbCreate(String partyId, String partyTypeId, String partyClassificationTypeId, java.util.Date fromDate, java.util.Date thruDate) throws CreateException
  {

    this.partyId = partyId;
    this.partyTypeId = partyTypeId;
    this.partyClassificationTypeId = partyClassificationTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.PartyClassificationPK ejbCreate(String partyId, String partyTypeId) throws CreateException
  {
    return ejbCreate(partyId, partyTypeId, null, null, null);
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  partyClassificationTypeId                  Field of the PARTY_CLASSIFICATION_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String partyTypeId, String partyClassificationTypeId, java.util.Date fromDate, java.util.Date thruDate) throws CreateException
  {
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String partyTypeId) throws CreateException
  {
    ejbPostCreate(partyId, partyTypeId, null, null, null);
  }

  /**
   *  Called when the entity bean is removed.
   *
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException
  {
  }

  /**
   *  Called when the entity bean is activated.
   */
  public void ejbActivate()
  {
  }

  /**
   *  Called when the entity bean is passivated.
   */
  public void ejbPassivate()
  {
  }

  /**
   *  Called when the entity bean is loaded.
   */
  public void ejbLoad()
  {
  }

  /**
   *  Called when the entity bean is stored.
   */
  public void ejbStore()
  {
  }

  /**
   *  Unsets the EntityContext, ie sets it to null.
   */
  public void unsetEntityContext()
  {
    entityContext = null;
  }
}
