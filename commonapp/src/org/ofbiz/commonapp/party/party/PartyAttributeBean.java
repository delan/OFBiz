
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
public class PartyAttributeBean implements EntityBean
{

  /**
   *  The variable for the PARTY_ID column of the PARTY_ATTRIBUTE table.
   */
  public String partyId;

  /**
   *  The variable for the NAME column of the PARTY_ATTRIBUTE table.
   */
  public String name;

  /**
   *  The variable for the VALUE column of the PARTY_ATTRIBUTE table.
   */
  public String value;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the PartyAttributeBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key PARTY_ID column of the PARTY_ATTRIBUTE table.
   */
  public String getPartyId()
  {
    return partyId;
  }
  

  
  /**
   *  Get the primary key NAME column of the PARTY_ATTRIBUTE table.
   */
  public String getName()
  {
    return name;
  }
  

  
  /**
   *  Get the value of the VALUE column of the PARTY_ATTRIBUTE table.
   */
  public String getValue()
  {
    return value;
  }
  /**
   *  Set the value of the VALUE column of the PARTY_ATTRIBUTE table.
   */
  public void setValue(String value)
  {
    this.value = value;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the PartyAttributeBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(PartyAttribute valueObject)
  {

    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
    
      if(valueObject.getValue() != null)
        this.value = valueObject.getValue();
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }

  }

  /**
   *  Gets the ValueObject attribute of the PartyAttributeBean object
   *
   *@return    The ValueObject value
   */
  public PartyAttribute getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PartyAttributeValue((PartyAttribute)this.entityContext.getEJBObject(), partyId, name, value);
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
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.PartyAttributePK ejbCreate(String partyId, String name, String value) throws CreateException
  {

    this.partyId = partyId;
    this.name = name;
    this.value = value;
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.party.party.PartyAttributePK ejbCreate(String partyId, String name) throws CreateException
  {
    return ejbCreate(partyId, name, null);
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String name, String value) throws CreateException
  {
  }

  /**
   *  Description of the Method
   *

   *@param  partyId                  Field of the PARTY_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyId, String name) throws CreateException
  {
    ejbPostCreate(partyId, name, null);
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
