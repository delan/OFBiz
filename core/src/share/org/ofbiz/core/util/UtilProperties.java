/*
 * $Id$
 * $Log$
 * Revision 1.3  2001/10/13 22:37:37  jonesde
 * Added UtilURL and changed FlexibleProperties and UtilProperties to be URL-centric
 *
 * Revision 1.2  2001/10/11 02:34:29  azeneski
 * Added new method to get numbers from properties file.
 *
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.4  2001/09/27 06:45:15  jonesde
 * Added FlexibleProperties and updated UtilProperties to use it.
 *
 * Revision 1.3  2001/09/23 01:35:29  jonesde
 * Improved exception handling when a bundle is not found.
 *
 * Revision 1.2  2001/07/18 22:22:53  jonesde
 * A few small changes to use the Debug class for logging instead of straight
 * System.out. Also added conditional logging for info, warning, and error messages
 * which are controlled through the debug.properties file.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.1  2001/07/15 16:36:18  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.util;

import java.util.*;
import java.net.*;

/**
 * <p><b>Title:</b> Generic Property Accessor - with Cache
 * <p><b>Description:</b> Utilities for working with properties files
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
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    May 22, 2001
 *@version    1.0
 */
public class UtilProperties {
  /** An instance of the generic cache for storing the ResourceBundle corresponding to each properties file
   */
  static public UtilCache resCache = new UtilCache("UtilPropertiesCache");
  
  /** Compares the specified property to the compareString, returns true if they are the same, false otherwise
   * @param resource The name of the resource - if the properties file is 'webevent.properties', the resource name is 'webevent'
   * @param name The name of the property in the properties file
   * @param compareString The String to compare the property value to
   * @return True if the strings are the same, false otherwise
   */
  public static boolean propertyValueEquals(String resource, String name, String compareString) {
    String value = getPropertyValue(resource, name);
    if(value == null) return false;
    return value.equals(compareString);
  }
  
  /** Compares Ignoring Case the specified property to the compareString, returns true if they are the same, false otherwise
   * @param resource The name of the resource - if the properties file is 'webevent.properties', the resource name is 'webevent'
   * @param name The name of the property in the properties file
   * @param compareString The String to compare the property value to
   * @return True if the strings are the same, false otherwise
   */
  public static boolean propertyValueEqualsIgnoreCase(String resource, String name, String compareString) {
    String value = getPropertyValue(resource, name);
    if(value == null) return false;
    return value.equalsIgnoreCase(compareString);
  }
  
  /** Returns the value of the specified property name from the specified resource/properties file.
   * If the specified property name or properties file is not found, the defaultValue is returned.
   * @param resource The name of the resource - if the properties file is 'webevent.properties', the resource name is 'webevent'
   * @param name The name of the property in the properties file
   * @param defaultValue The value to return if the property is not found
   * @return The value of the property in the properties file, or if not found then the defaultValue
   */
  public static String getPropertyValue(String resource, String name, String defaultValue) {
    String value = getPropertyValue(resource, name);
    if(value == null || value.length() <= 0) return defaultValue;
    else return value;
  }
  
  public static double getPropertyNumber(String resource, String name) {
    String str = getPropertyValue(resource,name);
    double strValue = 0.00000;
    try { strValue = Double.parseDouble(str); }
    catch ( NumberFormatException nfe ) {}
    return strValue;
  }

  /** Returns the value of the specified property name from the specified resource/properties file
   * @param resource The name of the resource - can be a file, class, or URL
   * @param name The name of the property in the properties file
   * @return The value of the property in the properties file
   */
  public static String getPropertyValue(String resource, String name) {
    URL url = UtilURL.fromResource(resource);
    return getPropertyValue(url, name);
  }

//========= URL Based Methods ==========
  
  /** Compares the specified property to the compareString, returns true if they are the same, false otherwise
   * @param url URL object specifying the location of the resource
   * @param name The name of the property in the properties file
   * @param compareString The String to compare the property value to
   * @return True if the strings are the same, false otherwise
   */
  public static boolean propertyValueEquals(URL url, String name, String compareString) {
    String value = getPropertyValue(url, name);
    if(value == null) return false;
    return value.equals(compareString);
  }
  
  /** Compares Ignoring Case the specified property to the compareString, returns true if they are the same, false otherwise
   * @param url URL object specifying the location of the resource
   * @param name The name of the property in the properties file
   * @param compareString The String to compare the property value to
   * @return True if the strings are the same, false otherwise
   */
  public static boolean propertyValueEqualsIgnoreCase(URL url, String name, String compareString) {
    String value = getPropertyValue(url, name);
    if(value == null) return false;
    return value.equalsIgnoreCase(compareString);
  }
  
  /** Returns the value of the specified property name from the specified resource/properties file.
   * If the specified property name or properties file is not found, the defaultValue is returned.
   * @param url URL object specifying the location of the resource
   * @param name The name of the property in the properties file
   * @param defaultValue The value to return if the property is not found
   * @return The value of the property in the properties file, or if not found then the defaultValue
   */
  public static String getPropertyValue(URL url, String name, String defaultValue) {
    String value = getPropertyValue(url, name);
    if(value == null || value.length() <= 0) return defaultValue;
    else return value;
  }
  
  public static double getPropertyNumber(URL url, String name) {
    String str = getPropertyValue(url,name);
    double strValue = 0.00000;
    try { strValue = Double.parseDouble(str); }
    catch ( NumberFormatException nfe ) {}
    return strValue;
  }

  /** Returns the value of the specified property name from the specified resource/properties file
   * @param url URL object specifying the location of the resource
   * @param name The name of the property in the properties file
   * @return The value of the property in the properties file
   */
  public static String getPropertyValue(URL url, String name) {
    if(url == null) return "";
    if(name == null || name.length() <= 0) return "";
    FlexibleProperties properties = (FlexibleProperties)resCache.get(url);
    if(properties == null) {
      try {
        properties = FlexibleProperties.makeFlexibleProperties(url);
        resCache.put(url, properties);
      }
      catch(MissingResourceException e) { Debug.log(e.getMessage()); }
    }
    if(properties == null) {
      Debug.log("[UtilProperties.getPropertyValue] could not find resource: " + url);
      return null;
    }

    String value = null;
    try {
        value = properties.getProperty(name);
    } catch(Exception e) { Debug.log(e.getMessage()); }
    return value==null ? "" : value;
  }


  /** Returns the value of a split property name from the specified resource/properties file
   * Rather than specifying the property name the value of a name.X property is specified which
   * will correspond to a value.X property whose value will be returned. X is a number from 1 to 
   * whatever and all values are checked until a name.X for a certain X is not found.
   * @param url URL object specifying the location of the resource
   * @param name The name of the split property in the properties file
   * @return The value of the split property from the properties file
   */
  public static String getSplitPropertyValue(URL url, String name) {
    if (url == null) return "";
    if (name == null || name.length() <= 0) return "";

    FlexibleProperties properties = (FlexibleProperties)resCache.get(url);
    if (properties == null) {
      try {
        properties = FlexibleProperties.makeFlexibleProperties(url);
        resCache.put(url, properties);
      } catch (MissingResourceException e) { Debug.log(e.getMessage()); }
    }
    if (properties == null) {
      Debug.log("[UtilProperties.getPropertyValue] could not find resource: " + url);
      return null;
    }

    String value = null;
    try {
        int curIdx = 1;
        String curName = null;
        while ((curName = properties.getProperty("name." + curIdx)) != null) {
            if (name.equals(curName)) {
                value = properties.getProperty("value." + curIdx);
                break;
            }
            curIdx++;
        }
    } catch (Exception e) {
        Debug.log(e.getMessage());
    }
    return value==null ? "" : value;
  }
}
