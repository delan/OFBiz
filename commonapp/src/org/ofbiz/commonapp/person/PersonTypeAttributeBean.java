
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

/**
 * <p><b>Title:</b> Person Component - Person Type Attribute Entity
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
 *@created    Wed May 23 14:48:17 MDT 2001
 *@version    1.0
 */

public class PersonTypeAttributeBean implements EntityBean
{

  /**
   *  The variable for the TYPE_ID column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String typeId;

  /**
   *  The variable for the NAME column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String name;


  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the PersonTypeAttributeBean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }



  
  /**
   *  Get the primary key TYPE_ID column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String getTypeId()
  {
    return typeId;
  }
  

  
  /**
   *  Get the primary key NAME column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String getName()
  {
    return name;
  }
  


  /**
   *  Sets the values from ValueObject attribute of the PersonTypeAttributeBean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(PersonTypeAttribute valueObject)
  {

  }

  /**
   *  Gets the ValueObject attribute of the PersonTypeAttributeBean object
   *
   *@return    The ValueObject value
   */
  public PersonTypeAttribute getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PersonTypeAttributeValue((PersonTypeAttribute)this.entityContext.getEJBObject(), typeId, name);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.person.PersonTypeAttributePK ejbCreate(String typeId, String name) throws CreateException
  {

    this.typeId = typeId;
    this.name = name;
    return null;
  }

  /**
   *  Description of the Method
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String typeId, String name) throws CreateException
  {
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
