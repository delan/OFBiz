
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

/**
 * <p><b>Title:</b> Person Component - Person Attribute Entity
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
 *@created    Wed May 23 12:49:15 MDT 2001
 *@version    1.0
 */

public class PersonAttributeBean implements EntityBean
{

  /**
   *  The variable for the USERNAME column of the PERSON_ATTRIBUTE table.
   */
  public String username;

  /**
   *  The variable for the NAME column of the PERSON_ATTRIBUTE table.
   */
  public String name;

  /**
   *  The variable for the VALUE column of the PERSON_ATTRIBUTE table.
   */
  public String value;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the PersonAttributeBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key USERNAME column of the PERSON_ATTRIBUTE table.
   */
  public String getUsername()
  {
    return username;
  }
  

  
  /**
   *  Get the primary key NAME column of the PERSON_ATTRIBUTE table.
   */
  public String getName()
  {
    return name;
  }
  

  
  /**
   *  Get the value of the VALUE column of the PERSON_ATTRIBUTE table.
   */
  public String getValue()
  {
    return value;
  }
  /**
   *  Set the value of the VALUE column of the PERSON_ATTRIBUTE table.
   */
  public void setValue(String value)
  {
    this.value = value;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the PersonAttributeBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(PersonAttribute valueObject)
  {

    try
    {

  
  
      this.value = valueObject.getValue();
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }

  }

  /**
   *  Gets the ValueObject attribute of the PersonAttributeBean object
   *
   *@return    The ValueObject value
   */
  public PersonAttribute getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PersonAttributeValue((PersonAttribute)this.entityContext.getEJBObject(), username, name, value);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.person.PersonAttributePK ejbCreate(String username, String name, String value) throws CreateException
  {

    this.username = username;
    this.name = name;
    this.value = value;
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.person.PersonAttributePK ejbCreate(String username, String name) throws CreateException
  {
    return ejbCreate(username, name, null);
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   *@param  value                  Field of the VALUE column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String username, String name, String value) throws CreateException
  {
  }

  /**
   *  Description of the Method
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String username, String name) throws CreateException
  {
    ejbPostCreate(username, name, null);
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
