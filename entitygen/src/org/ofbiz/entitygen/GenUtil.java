package org.ofbiz.entitygen;

/**
 * <p><b>Title:</b> Entity Generator
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
 *@created    May 15, 2001
 *@version    1.0
 */

public class GenUtil
{
    /** Changes the first letter of the passed String to upper case.
     * @param string The passed String
     * @return A String with an upper case first letter
     */    
  public static String upperFirstChar(String string)
  {
    if(string == null) return null;
    if(string.length() <= 1) return string.toLowerCase();
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }
  /** Changes the first letter of the passed String to lower case.
   *
   * @param string The passed String
   * @return A String with a lower case first letter
   */  
  public static String lowerFirstChar(String string)
  {
    if(string == null) return null;
    if(string.length() <= 1) return string.toLowerCase();
    return string.substring(0, 1).toLowerCase() + string.substring(1);
  }
  /** Converts a database name to a Java class name.
   * The naming conventions used to allow for this are as follows: a database name (table or column) is in all capital letters, and the words are separated by an underscore (for example: NEAT_ENTITY_NAME or RANDOM_FIELD_NAME); a Java name (ejb or field) is in all lower case letters, except the letter at the beginning of each word (for example: NeatEntityName or RandomFieldName). The convention of using a capital letter at the beginning of a class name in Java, or a lower-case letter for the beginning of a variable name in Java is also used along with the Java name convention above.
   * @param columnName The database name
   * @return The Java class name
   */  
  public static String dbNameToClassName(String columnName)
  {
    return upperFirstChar(dbNameToVarName(columnName));
  }
  /** Converts a database name to a Java variable name.
   * The naming conventions used to allow for this are as follows: a database name (table or column) is in all capital letters, and the words are separated by an underscore (for example: NEAT_ENTITY_NAME or RANDOM_FIELD_NAME); a Java name (ejb or field) is in all lower case letters, except the letter at the beginning of each word (for example: NeatEntityName or RandomFieldName). The convention of using a capital letter at the beginning of a class name in Java, or a lower-case letter for the beginning of a variable name in Java is also used along with the Java name convention above.
   * @param columnName The database name
   * @return The Java variable name
   */  
  public static String dbNameToVarName(String columnName)
  {
    String fieldName = null;
    int end = columnName.indexOf("_");
    int start = 0;
    if(end > 0)
    {
      fieldName = columnName.substring(start, end).toLowerCase();
      start = end + 1;
      end = columnName.indexOf("_", start);
      while(end > 0)
      {
        fieldName = fieldName + upperFirstChar(columnName.substring(start, end).toLowerCase());
        start = end + 1;
        end = columnName.indexOf("_", start);
      }
      fieldName = fieldName + upperFirstChar(columnName.substring(start).toLowerCase());
    }
    else
    {
      fieldName = columnName.toLowerCase();
    }
    return fieldName;
  }
}
