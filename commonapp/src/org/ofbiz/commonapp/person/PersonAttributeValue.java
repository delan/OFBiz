
package org.ofbiz.commonapp.person;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

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
 *@created    Wed May 23 12:49:36 MDT 2001
 *@version    1.0
 */

public class PersonAttributeValue implements PersonAttribute
{

  /**
   *  The variable of the USERNAME column of the PERSON_ATTRIBUTE table.
   */
  private String username;

  /**
   *  The variable of the NAME column of the PERSON_ATTRIBUTE table.
   */
  private String name;

  /**
   *  The variable of the VALUE column of the PERSON_ATTRIBUTE table.
   */
  private String value;


  private PersonAttribute personAttribute;

  public PersonAttributeValue()
  {

    this.username = null;
    this.name = null;
    this.value = null;

    this.personAttribute = null;
  }

  public PersonAttributeValue(PersonAttribute personAttribute) throws RemoteException
  {
    if(personAttribute == null) return;


    this.username = personAttribute.getUsername();
    this.name = personAttribute.getName();
    this.value = personAttribute.getValue();

    this.personAttribute = personAttribute;
  }

  public PersonAttributeValue(PersonAttribute personAttribute, String username, String name, String value)
  {
    if(personAttribute == null) return;


    this.username = username;
    this.name = name;
    this.value = value;

    this.personAttribute = personAttribute;
  }


  /**
   *  Get the primary key of the USERNAME column of the PERSON_ATTRIBUTE table.
   */
  public String getUsername()  throws RemoteException
  {
    return username;
  }
  
  /**
   *  Get the primary key of the NAME column of the PERSON_ATTRIBUTE table.
   */
  public String getName()  throws RemoteException
  {
    return name;
  }
  
  /**
   *  Get the value of the VALUE column of the PERSON_ATTRIBUTE table.
   */
  public String getValue() throws RemoteException
  {
    return value;
  }
  /**
   *  Set the value of the VALUE column of the PERSON_ATTRIBUTE table.
   */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(personAttribute!=null) personAttribute.setValue(value);
  }
  

  /**
   *  Get the value object of the PersonAttribute class.
   */
  public PersonAttribute getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the PersonAttribute class.
   */
  public void setValueObject(PersonAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(personAttribute!=null) personAttribute.setValueObject(valueObject);

    if(username == null) username = valueObject.getUsername();
  
  
    if(name == null) name = valueObject.getName();
  
  
    value = valueObject.getValue();
  
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(personAttribute!=null) return personAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(personAttribute!=null) return personAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(personAttribute!=null) return personAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(personAttribute!=null) return personAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(personAttribute!=null) personAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
