/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/07/27 07:53:48  jonesde
 * Added misc utils class UtilMisc. Currently just has a toIterator routine.
 *
 *
 */

package org.ofbiz.core.util;

import java.util.*;

/**
 * <p><b>Title:</b> UtilMisc
 * <p><b>Description:</b> Misc Utility Functions
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
 *@created    July 26, 2001
 *@version    1.0
 */
public class UtilMisc
{
  /** Get an iterator from a collection, returning null if collection is null
   * @param col The collection to be turned in to an iterator
   * @return The resulting Iterator
   */
  public static Iterator toIterator(Collection col)
  {
    if(col == null) return null;
    else return col.iterator();
  }

  /** Create a map from passed nameX, valueX parameters
   * @return The resulting Map
   */  
  public static Map toMap(String name1, Object value1)
  {
    Map fields = new HashMap();
    fields.put(name1, value1);
    return fields;
  }

  /** Create a map from passed nameX, valueX parameters
   * @return The resulting Map
   */  
  public static Map toMap(String name1, Object value1, String name2, Object value2)
  {
    Map fields = new HashMap();
    fields.put(name1, value1);
    fields.put(name2, value2);
    return fields;
  }

  /** Create a map from passed nameX, valueX parameters
   * @return The resulting Map
   */  
  public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3)
  {
    Map fields = new HashMap();
    fields.put(name1, value1);
    fields.put(name2, value2);
    fields.put(name3, value3);
    return fields;
  }

  /** Create a map from passed nameX, valueX parameters
   * @return The resulting Map
   */  
  public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4)
  {
    Map fields = new HashMap();
    fields.put(name1, value1);
    fields.put(name2, value2);
    fields.put(name3, value3);
    fields.put(name4, value4);
    return fields;
  }

  /** Create a map from passed nameX, valueX parameters
   * @return The resulting Map
   */  
  public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4, String name5, Object value5)
  {
    Map fields = new HashMap();
    fields.put(name1, value1);
    fields.put(name2, value2);
    fields.put(name3, value3);
    fields.put(name4, value4);
    fields.put(name5, value5);
    return fields;
  }

  /** Create a map from passed nameX, valueX parameters
   * @return The resulting Map
   */  
  public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4, String name5, Object value5, String name6, Object value6)
  {
    Map fields = new HashMap();
    fields.put(name1, value1);
    fields.put(name2, value2);
    fields.put(name3, value3);
    fields.put(name4, value4);
    fields.put(name5, value5);
    fields.put(name6, value6);
    return fields;
  }
}
