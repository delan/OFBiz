
package org.ofbiz.commonapp.common.uom;

import java.io.*;

/**
 * <p><b>Title:</b> Unit Of Measure Conversion Type Entity
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
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */
public class UomConversionPK implements Serializable
{


  /**
   *  The variable of the UOM_ID column of the UOM_CONVERSION table.
   */
  public String uomId;

  /**
   *  The variable of the UOM_ID_TO column of the UOM_CONVERSION table.
   */
  public String uomIdTo;


  /**
   *  Constructor for the UomConversionPK object
   */
  public UomConversionPK()
  {
  }

  /**
   *  Constructor for the UomConversionPK object
   *

   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   */
  public UomConversionPK(String uomId, String uomIdTo)
  {

    this.uomId = uomId;
    this.uomIdTo = uomIdTo;
  }

  /**
   *  Determines the equality of two UomConversionPK objects, overrides the default equals
   *
   *@param  obj  The object (UomConversionPK) to compare this two
   *@return      boolean stating if the two objects are equal
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      UomConversionPK that = (UomConversionPK)obj;
      return

            this.uomId.equals(that.uomId) &&
            this.uomIdTo.equals(that.uomIdTo) &&
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
    return (uomId + "::" + uomIdTo).hashCode();
  }

  /**
   *  Creates a String for the combined primary keys, overrides the default toString
   *
   *@return    String corresponding to this primary key
   */
  public String toString()
  {
    return uomId + "::" + uomIdTo;
  }
}
