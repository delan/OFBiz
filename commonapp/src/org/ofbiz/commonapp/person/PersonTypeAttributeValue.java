
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

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
 *@created    Wed May 23 12:52:28 MDT 2001
 *@version    1.0
 */

public class PersonTypeAttributeValue implements PersonTypeAttribute
{

  /**
   *  The variable of the TYPE_ID column of the PERSON_TYPE_ATTRIBUTE table.
   */
  private String typeId;

  /**
   *  The variable of the NAME column of the PERSON_TYPE_ATTRIBUTE table.
   */
  private String name;


  private PersonTypeAttribute personTypeAttribute;

  public PersonTypeAttributeValue()
  {

    this.typeId = null;
    this.name = null;

    this.personTypeAttribute = null;
  }

  public PersonTypeAttributeValue(PersonTypeAttribute personTypeAttribute) throws RemoteException
  {
    if(personTypeAttribute == null) return;


    this.typeId = personTypeAttribute.getTypeId();
    this.name = personTypeAttribute.getName();

    this.personTypeAttribute = personTypeAttribute;
  }

  public PersonTypeAttributeValue(PersonTypeAttribute personTypeAttribute, String typeId, String name)
  {
    if(personTypeAttribute == null) return;


    this.typeId = typeId;
    this.name = name;

    this.personTypeAttribute = personTypeAttribute;
  }


  /**
   *  Get the primary key of the TYPE_ID column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String getTypeId()  throws RemoteException
  {
    return typeId;
  }
  
  /**
   *  Get the primary key of the NAME column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String getName()  throws RemoteException
  {
    return name;
  }
  

  /**
   *  Get the value object of the PersonTypeAttribute class.
   */
  public PersonTypeAttribute getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the PersonTypeAttribute class.
   */
  public void setValueObject(PersonTypeAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(personTypeAttribute!=null) personTypeAttribute.setValueObject(valueObject);

    if(typeId == null) typeId = valueObject.getTypeId();
  
  
    if(name == null) name = valueObject.getName();
  
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(personTypeAttribute!=null) return personTypeAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(personTypeAttribute!=null) return personTypeAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(personTypeAttribute!=null) return personTypeAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(personTypeAttribute!=null) return personTypeAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(personTypeAttribute!=null) personTypeAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
