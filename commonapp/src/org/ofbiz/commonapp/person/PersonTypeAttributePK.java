
package org.ofbiz.commonapp.person;

import java.io.*;

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
 *@created    Mon May 28 21:35:13 MDT 2001
 *@version    1.0
 */
public class PersonTypeAttributePK implements Serializable
{


  /**
   *  The variable of the TYPE_ID column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String typeId;

  /**
   *  The variable of the NAME column of the PERSON_TYPE_ATTRIBUTE table.
   */
  public String name;


  /**
   *  Constructor for the PersonTypeAttributePK object
   */
  public PersonTypeAttributePK()
  {
  }

  /**
   *  Constructor for the PersonTypeAttributePK object
   *

   *@param  typeId                  Field of the TYPE_ID column.
   *@param  name                  Field of the NAME column.
   */
  public PersonTypeAttributePK(String typeId, String name)
  {

    this.typeId = typeId;
    this.name = name;
  }

  /**
   *  Determines the equality of two PersonTypeAttributePK objects, overrides the default equals
   *
   *@param  obj  The object (PersonTypeAttributePK) to compare this two
   *@return      boolean stating if the two objects are equal
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      PersonTypeAttributePK that = (PersonTypeAttributePK)obj;
      return

            this.typeId.equals(that.typeId) &&
            this.name.equals(that.name) &&
            true; //This "true" is a dummy thing to take care of the last &&, just for laziness sake.
    }
    return false;
  }

  /**
   *  Creates a hashCode for the combined primary keys, using the default String hashCode, overrides the default hashCode
   *
   *@return    Hashcode corresponding to this primary key
   */
  public int hashCode()
  {
    return (typeId + "::" + name).hashCode();
  }

  /**
   *  Creates a String for the combined primary keys, overrides the default toString
   *
   *@return    String corresponding to this primary key
   */
  public String toString()
  {
    return typeId + "::" + name;
  }
}
