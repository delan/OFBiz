
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

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
public class PartyTypeBean implements EntityBean
{

  /**
   *  The variable for the PARTY_TYPE_ID column of the PARTY_TYPE table.
   */
  public String partyTypeId;

  /**
   *  The variable for the PARENT_TYPE_ID column of the PARTY_TYPE table.
   */
  public String parentTypeId;

  /**
   *  The variable for the HAS_TABLE column of the PARTY_TYPE table.
   */
  public String hasTable;

  /**
   *  The variable for the DESCRIPTION column of the PARTY_TYPE table.
   */
  public String description;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the PartyTypeBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key PARTY_TYPE_ID column of the PARTY_TYPE table.
   */
  public String getPartyTypeId()
  {
    return partyTypeId;
  }
  

  
  /**
   *  Get the value of the PARENT_TYPE_ID column of the PARTY_TYPE table.
   */
  public String getParentTypeId()
  {
    return parentTypeId;
  }
  /**
   *  Set the value of the PARENT_TYPE_ID column of the PARTY_TYPE table.
   */
  public void setParentTypeId(String parentTypeId)
  {
    this.parentTypeId = parentTypeId;
  }
  

  
  /**
   *  Get the value of the HAS_TABLE column of the PARTY_TYPE table.
   */
  public String getHasTable()
  {
    return hasTable;
  }
  /**
   *  Set the value of the HAS_TABLE column of the PARTY_TYPE table.
   */
  public void setHasTable(String hasTable)
  {
    this.hasTable = hasTable;
  }
  

  
  /**
   *  Get the value of the DESCRIPTION column of the PARTY_TYPE table.
   */
  public String getDescription()
  {
    return description;
  }
  /**
   *  Set the value of the DESCRIPTION column of the PARTY_TYPE table.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the PartyTypeBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(PartyType valueObject)
  {

    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
    
      if(valueObject.getParentTypeId() != null)
        this.parentTypeId = valueObject.getParentTypeId();
      if(valueObject.getHasTable() != null)
        this.hasTable = valueObject.getHasTable();
      if(valueObject.getDescription() != null)
        this.description = valueObject.getDescription();
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }

  }

  /**
   *  Gets the ValueObject attribute of the PartyTypeBean object
   *
   *@return    The ValueObject value
   */
  public PartyType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PartyTypeValue((PartyType)this.entityContext.getEJBObject(), partyTypeId, parentTypeId, hasTable, description);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String partyTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {

    this.partyTypeId = partyTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String partyTypeId) throws CreateException
  {
    return ejbCreate(partyTypeId, null, null, null);
  }

  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {
  }

  /**
   *  Description of the Method
   *

   *@param  partyTypeId                  Field of the PARTY_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String partyTypeId) throws CreateException
  {
    ejbPostCreate(partyTypeId, null, null, null);
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
