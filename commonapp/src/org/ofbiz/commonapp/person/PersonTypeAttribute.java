
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
 *@created    Wed May 23 12:51:59 MDT 2001
 *@version    1.0
 */

public interface PersonTypeAttribute extends EJBObject
{

  
  /**
   *  Get the primary key of the TYPE_ID column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String getTypeId() throws RemoteException;
  

  
  /**
   *  Get the primary key of the NAME column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String getName() throws RemoteException;
  


  /**
   *  Get the value object of this PersonTypeAttribute class.
   */
  public PersonTypeAttribute getValueObject() throws RemoteException;
  /**
   *  Set the values in the value object of this PersonTypeAttribute class.
   */
  public void setValueObject(PersonTypeAttribute personTypeAttributeValue) throws RemoteException;
}
