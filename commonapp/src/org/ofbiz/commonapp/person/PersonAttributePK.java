
package org.ofbiz.commonapp.person;

import java.io.*;

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
 *@created    Wed May 23 12:49:28 MDT 2001
 *@version    1.0
 */

public class PersonAttributePK implements Serializable
{


  /**
   *  The variable of the USERNAME column of the PERSON_ATTRIBUTE table.
   */
  public String username;

  /**
   *  The variable of the NAME column of the PERSON_ATTRIBUTE table.
   */
  public String name;


  /**
   *  Constructor for the PersonAttributePK object
   */
  public PersonAttributePK()
  {
  }

  /**
   *  Constructor for the PersonAttributePK object
   *

   *@param  username                  Field of the USERNAME column.
   *@param  name                  Field of the NAME column.
   */
  public PersonAttributePK(String username, String name)
  {

    this.username = username;
    this.name = name;
  }

  /**
   *  Description of the Method
   *
   *@param  obj  Description of Field
   *@return      Description of the Returned Value
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      PersonAttributePK that = (PersonAttributePK)obj;
      return

            this.username.equals(that.username) &&
            this.name.equals(that.name) &&
            true; //This "true" is a dummy thing to take care of the last &&, just for laziness sake.
    }
    return false;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public int hashCode()
  {
    return (username + name).hashCode();
  }
}
