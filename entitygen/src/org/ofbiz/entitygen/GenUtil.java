package org.ofbiz.entitygen;

import net.matuschek.http.*;
import java.io.*;

/**
 * <p><b>Title:</b> Entity Generator - General Generator Utilities
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

  /** Converts a package name to a path by replacing all '.' characters with the File.separatorChar character. Is therefore platform independent.
   * @param The package name.
   * @return The path name corresponding to the specified package name.
   */
  public static String packageToPath(String packageName)
  {
    //just replace all of the '.' characters with the folder separater character
    return packageName.replace('.', File.separatorChar);
  }
  
  /** Retrieves a web page, and returns the content of the page in a String.
   * @param url The URL of the page to get.
   * @param params Any parameters (optional) to pass to the page.
   * @return The content of the page in a String
   */
  public static String getCodeFromUrl(java.net.URL url, String params)
  {
    try
    {
      HttpTool httpTool = new HttpTool();
      HttpDoc httpDoc = httpTool.retrieveDocument(url, HttpConstants.GET, params);
      return new String(httpDoc.getContent());
    }
    catch(Exception e) {e.printStackTrace();}
    return null;
  }
  
  /** Writes the passed String to the specified fileName in the filePath directory.
   * @param filePath The path or folder name where the file will be written
   * @param fileName The name of the file to create
   * @param content The content to write to the file
   * @return Returns <code>true</code> if file write succeeds, <code>false</code> otherwise
   */
  public static boolean writeFile(String filePath, String fileName, String content)
  {
    //create directory(ies) if do(es) not exist...
    File testDir = new File(filePath);
    if(!testDir.exists())
    {
      if(testDir.mkdirs()) System.out.println("Created directory: " + filePath);
      else 
      {
        System.out.println("Directory creation failed for: " + filePath);
        return false;
      }
    }

    try
    {
      File newFile = new File(filePath, fileName);
      FileWriter newFileWriter = new FileWriter(newFile);
      newFileWriter.write(content);
      newFileWriter.close();
      return true;
    }
    catch(Exception e) {e.printStackTrace();}
    return false;
  }

  /** Replaces all occurances of oldString in mainString with newString
   * @param mainString The original string
   * @param oldString The string to replace
   * @param newString The string to insert in place of the old
   * @return mainString with all occurances of oldString replaced by newString
   */  
  public static String replaceString(String mainString, String oldString, String newString)
  {
    String retString = new String(mainString);
    int loc=0;
    int i=retString.indexOf(oldString,loc);
    while(i >= 0)
    {
      StringBuffer querySb = new StringBuffer(retString);
      querySb.replace(i, i+oldString.length(), newString);
      retString = querySb.toString();

      loc=i+newString.length();
      i=retString.indexOf(oldString,loc);
    }
    return retString;
  }
}
