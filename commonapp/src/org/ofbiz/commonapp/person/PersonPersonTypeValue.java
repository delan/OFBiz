
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

/**
 * <p><b>Title:</b> Person Component - Person Person Type Entity
 * <p><b>Description:</b> Maps a Person to a Person Type; necessary so a person can be of multiple types.
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
 *@created    Wed May 23 12:54:24 MDT 2001
 *@version    1.0
 */

public class PersonPersonTypeValue implements PersonPersonType
{

  /**
   *  The variable of the USERNAME column of the PERSON_PERSON_TYPE table.
   */
  private String username;

  /**
   *  The variable of the TYPE_ID column of the PERSON_PERSON_TYPE table.
   */
  private String typeId;


  private PersonPersonType personPersonType;

  public PersonPersonTypeValue()
  {

    this.username = null;
    this.typeId = null;

    this.personPersonType = null;
  }

  public PersonPersonTypeValue(PersonPersonType personPersonType) throws RemoteException
  {
    if(personPersonType == null) return;


    this.username = personPersonType.getUsername();
    this.typeId = personPersonType.getTypeId();

    this.personPersonType = personPersonType;
  }

  public PersonPersonTypeValue(PersonPersonType personPersonType, String username, String typeId)
  {
    if(personPersonType == null) return;


    this.username = username;
    this.typeId = typeId;

    this.personPersonType = personPersonType;
  }


  /**
   *  Get the primary key of the USERNAME column of the PERSON_PERSON_TYPE table.
   */
  public String getUsername()  throws RemoteException
  {
    return username;
  }
  
  /**
   *  Get the primary key of the TYPE_ID column of the PERSON_PERSON_TYPE table.
   */
  public String getTypeId()  throws RemoteException
  {
    return typeId;
  }
  

  /**
   *  Get the value object of the PersonPersonType class.
   */
  public PersonPersonType getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the PersonPersonType class.
   */
  public void setValueObject(PersonPersonType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(personPersonType!=null) personPersonType.setValueObject(valueObject);

    if(username == null) username = valueObject.getUsername();
  
  
    if(typeId == null) typeId = valueObject.getTypeId();
  
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(personPersonType!=null) return personPersonType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(personPersonType!=null) return personPersonType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(personPersonType!=null) return personPersonType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(personPersonType!=null) return personPersonType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(personPersonType!=null) personPersonType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
