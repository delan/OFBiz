/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/08/30 20:55:40  jonesde
 * Added a simple function to only return pre and post text if the base string is not empty.
 *
 * Revision 1.1  2001/07/16 14:45:48  azeneski
 * Added the missing 'core' directory into the module.
 *
 * Revision 1.1  2001/07/15 16:36:18  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.util;

import javax.servlet.http.*;
import java.text.*;

/**
 * <p><b>Title:</b> General output formatting functions - mainly for helping in JSPs
 * <p><b>Description:</b> Utility functions for formatting output
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
 *@created    May 21, 2001
 *@version    1.0
 */
public class UtilFormatOut
{
  //------------------- price format handlers -------------------
  static DecimalFormat priceDecimalFormat = new DecimalFormat("#,##0.00");
  /** Formats a Double representing a price into a string
   * @param price The price Double to be formatted
   * @return A String with the formatted price
   */  
  public static String formatPrice(Double price)
  {
    if(price == null) return "";
    return formatPrice(price.doubleValue());
  }
  /** Formats a double representing a price into a string
   * @param price The price double to be formatted
   * @return A String with the formatted price
   */  
  public static String formatPrice(double price)
  {
    return priceDecimalFormat.format(price);
  }

  //------------------- quantity format handlers -------------------
  static DecimalFormat quantityDecimalFormat = new DecimalFormat("#,##0.###");
  /** Formats an Long representing a quantity into a string
   * @param quantity The quantity Long to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(Long quantity)
  {
    if(quantity == null) return "";
    else return formatQuantity(quantity.doubleValue());
  }
  /** Formats an int representing a quantity into a string
   * @param quantity The quantity long to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(long quantity)
  {
    return formatQuantity((double)quantity);
  }
  /** Formats an Integer representing a quantity into a string
   * @param quantity The quantity Integer to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(Integer quantity)
  {
    if(quantity == null) return "";
    else return formatQuantity(quantity.doubleValue());
  }
  /** Formats an int representing a quantity into a string
   * @param quantity The quantity int to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(int quantity)
  {
    return formatQuantity((double)quantity);
  }
  /** Formats a Float representing a quantity into a string
   * @param quantity The quantity Float to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(Float quantity)
  {
    if(quantity == null) return "";
    else return formatQuantity(quantity.doubleValue());
  }
  /** Formats a float representing a quantity into a string
   * @param quantity The quantity float to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(float quantity)
  {
    return formatQuantity((double)quantity);
  }
  /** Formats an Double representing a quantity into a string
   * @param quantity The quantity Double to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(Double quantity)
  {
    if(quantity == null) return "";
    else return formatQuantity(quantity.doubleValue());
  }
  /** Formats an double representing a quantity into a string
   * @param quantity The quantity double to be formatted
   * @return A String with the formatted quantity
   */  
  public static String formatQuantity(double quantity)
  {
    return quantityDecimalFormat.format(quantity);
  }

  //------------------- null string handlers -------------------
  /** Checks to see if the passed string is null, if it is returns an empty but non-null string.
   * @param string1 The passed String
   * @return The passed String if not null, otherwise an empty non-null String
   */  
  public static String checkNull(String string1)
  {
    if(string1 != null) return string1;
    else return "";
  }

  /** Returns the first passed String if not null, otherwise the second if not null, otherwise an empty but non-null String.
   * @param string1 The first passed String
   * @param string2 The second passed String
   * @return The first passed String if not null, otherwise the second if not null, otherwise an empty but non-null String
   */  
  public static String checkNull(String string1, String string2)
  {
    if(string1 != null) return string1;
    else if(string2 != null) return string2;
    else return "";
  }

  /** Returns the first passed String if not null, otherwise the second if not null, otherwise the third if not null, otherwise an empty but non-null String.
   * @param string1 The first passed String
   * @param string2 The second passed String
   * @param string3 The third passed String
   * @return The first passed String if not null, otherwise the second if not null, otherwise the third if not null, otherwise an empty but non-null String
   */  
  public static String checkNull(String string1, String string2, String string3)
  {
    if(string1 != null) return string1;
    else if(string2 != null) return string2;
    else if(string3 != null) return string3;
    else return "";
  }
  
  /** Returns <code>pre + base + post</code> if base String is not null or empty, otherwise an empty but non-null String.
   * @param base The base String
   * @param pre The pre String
   * @param post The post String
   * @return <code>pre + base + post</code> if base String is not null or empty, otherwise an empty but non-null String.
   */  
  public static String ifNotEmpty(String base, String pre, String post)
  {
    if(base != null && base.length() > 0) return pre + base + post;
    else return "";
  }
  
  //------------------- web encode handlers -------------------
  /** Encodes an HTTP URL query String, replacing characters used for other things in HTTP URL query strings, but not touching the separator characters '?', '=', and '&'
   * @param query The plain query String
   * @return The encoded String
   */  
  public static String encodeQuery(String query)
  {
    String retString;
    retString = replaceString(query, "%", "%25");
    retString = replaceString(retString, " ", "%20");
    return retString;
  }
  
  /** Encodes a single HTTP URL query value, replacing characters used for other things in HTTP URL query strings
   * @param query The plain query value String
   * @return The encoded String
   */  
  public static String encodeQueryValue(String query)
  {
    String retString;
    retString = replaceString(query, "%", "%25");
    retString = replaceString(retString, " ", "%20");
    retString = replaceString(retString, "&", "%26");
    retString = replaceString(retString, "?", "%3F");
    retString = replaceString(retString, "=", "%3D");
    return retString;
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

  /** Decodes a single query value from an HTTP URL parameter, replacing %ASCII values with characters
   * @param query The encoded query value String
   * @return The plain, decoded String
   */  
  public static String decodeQueryValue(String query)
  {
    String retString;
    retString = replaceString(query, "%25", "%");
    retString = replaceString(retString, "%20", " ");
    retString = replaceString(retString, "%26", "&");
    retString = replaceString(retString, "%3F", "?");
    retString = replaceString(retString, "%3D", "=");
    return retString;
  }
}