package org.ofbiz.commonapp.common;

import java.util.*;
import java.lang.*;

/**
 * <p><b>Title:</b> Date handling utilities
 * <p><b>Description:</b> Utility class for handling java.util.Date and related information
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
public class UtilDateTime
{
  /** Return a Timestamp for right now
   * @return Timestamp for right now
   */  
  public static java.sql.Timestamp nowTimestamp()
  {
    java.util.Date nowDate = new java.util.Date();
    return new java.sql.Timestamp(nowDate.getTime());
  }

  /** Return a Date for right now
   * @return Date for right now
   */  
  public static java.util.Date nowDate()
  {
    return new java.util.Date();
  }

  /** Converts a date and time String into a Timestamp
   * @param dateTime A combined data and time string in the format "MM/DD/YYYY HH:MM:SS", the seconds are optional
   * @return The corresponding Timestamp
   */  
  public static java.sql.Timestamp toTimestamp(String dateTime)
  {    
    //dateTime must have one space between the date and time...
    String date = dateTime.substring(0, dateTime.indexOf(" "));
    String time = dateTime.substring(dateTime.indexOf(" ")+1);
    return toTimestamp(date, time);
  }
  
  /** Converts a date String and a time String into a Timestamp
   * @param date The date String: MM/DD/YYYY
   * @param time The time String: either HH:MM or HH:MM:SS
   * @return A Timestamp made from the date and time Strings
   */  
  public static java.sql.Timestamp toTimestamp(String date, String time)
  {
    if(date == null || time == null) return null;
    String month;
    String day;
    String year;
    String hour;
    String minute;
    String second;

    int dateSlash1 = date.indexOf("/");
    int dateSlash2 = date.lastIndexOf("/");
    if(dateSlash1 <= 0 || dateSlash1 == dateSlash2) return null;
    int timeColon1 = time.indexOf(":");
    int timeColon2 = time.lastIndexOf(":");
    if(timeColon1 <= 0) return null;
    month = date.substring(0, dateSlash1);
    day = date.substring(dateSlash1 + 1, dateSlash2);
    year = date.substring(dateSlash2 + 1);
    hour = time.substring(0, timeColon1);

    if(timeColon1 == timeColon2)
    {
      minute = time.substring(timeColon1 + 1);
      second = "0";
    }
    else
    {
      minute = time.substring(timeColon1 + 1, timeColon2);
      second = time.substring(timeColon2 + 1);
    }

    return toTimestamp(month, day, year, hour, minute, second);
  }

  /** Makes a Timestamp from separate Strings for month, day, year, hour, minute, and second.
   * @param monthStr The month String
   * @param dayStr The day String
   * @param yearStr The year String
   * @param hourStr The hour String
   * @param minuteStr The minute String
   * @param secondStr The second String
   * @return A Timestamp made from separate Strings for month, day, year, hour, minute, and second.
   */  
  public static java.sql.Timestamp toTimestamp(String monthStr, String dayStr, String yearStr, String hourStr, String minuteStr, String secondStr)
  {
    int month, day, year, hour, minute, second;
    try
    {
      month = Integer.parseInt(monthStr);
      day = Integer.parseInt(dayStr);
      year = Integer.parseInt(yearStr);
      hour = Integer.parseInt(hourStr);
      minute = Integer.parseInt(minuteStr);
      second = Integer.parseInt(secondStr);
    }
    catch(Exception e)
    {
      return null;
    }
    return toTimestamp(month, day, year, hour, minute, second);
  }

  /** Makes a Timestamp from separate ints for month, day, year, hour, minute, and second.
   * @param month The month int
   * @param day The day int
   * @param year The year int
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   * @return A Timestamp made from separate ints for month, day, year, hour, minute, and second.
   */  
  public static java.sql.Timestamp toTimestamp(int month, int day, int year, int hour, int minute, int second)
  {
    Calendar calendar = Calendar.getInstance();
    try { calendar.set(year, month - 1, day, hour, minute, second); }
    catch(Exception e) { return null; }
    return new java.sql.Timestamp(calendar.getTime().getTime());
  }

  /** Converts a date and time String into a Date
   * @param dateTime A combined data and time string in the format "MM/DD/YYYY HH:MM:SS", the seconds are optional
   * @return The corresponding Date
   */  
  public static java.util.Date toDate(String dateTime)
  {    
    //dateTime must have one space between the date and time...
    String date = dateTime.substring(0, dateTime.indexOf(" "));
    String time = dateTime.substring(dateTime.indexOf(" ")+1);
    return toDate(date, time);
  }
  
  /** Converts a date String and a time String into a Date
   * @param date The date String: MM/DD/YYYY
   * @param time The time String: either HH:MM or HH:MM:SS
   * @return A Date made from the date and time Strings
   */  
  public static java.util.Date toDate(String date, String time)
  {
    if(date == null || time == null) return null;
    String month;
    String day;
    String year;
    String hour;
    String minute;
    String second;

    int dateSlash1 = date.indexOf("/");
    int dateSlash2 = date.lastIndexOf("/");
    if(dateSlash1 <= 0 || dateSlash1 == dateSlash2) return null;
    int timeColon1 = time.indexOf(":");
    int timeColon2 = time.lastIndexOf(":");
    if(timeColon1 <= 0) return null;
    month = date.substring(0, dateSlash1);
    day = date.substring(dateSlash1 + 1, dateSlash2);
    year = date.substring(dateSlash2 + 1);
    hour = time.substring(0, timeColon1);

    if(timeColon1 == timeColon2)
    {
      minute = time.substring(timeColon1 + 1);
      second = "0";
    }
    else
    {
      minute = time.substring(timeColon1 + 1, timeColon2);
      second = time.substring(timeColon2 + 1);
    }

    return toDate(month, day, year, hour, minute, second);
  }

  /** Makes a Date from separate Strings for month, day, year, hour, minute, and second.
   * @param monthStr The month String
   * @param dayStr The day String
   * @param yearStr The year String
   * @param hourStr The hour String
   * @param minuteStr The minute String
   * @param secondStr The second String
   * @return A Date made from separate Strings for month, day, year, hour, minute, and second.
   */  
  public static java.util.Date toDate(String monthStr, String dayStr, String yearStr, String hourStr, String minuteStr, String secondStr)
  {
    int month, day, year, hour, minute, second;
    try
    {
      month = Integer.parseInt(monthStr);
      day = Integer.parseInt(dayStr);
      year = Integer.parseInt(yearStr);
      hour = Integer.parseInt(hourStr);
      minute = Integer.parseInt(minuteStr);
      second = Integer.parseInt(secondStr);
    }
    catch(Exception e)
    {
      return null;
    }
    return toDate(month, day, year, hour, minute, second);
  }

  /** Makes a Date from separate ints for month, day, year, hour, minute, and second.
   * @param month The month int
   * @param day The day int
   * @param year The year int
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   * @return A Date made from separate ints for month, day, year, hour, minute, and second.
   */  
  public static java.util.Date toDate(int month, int day, int year, int hour, int minute, int second)
  {
    Calendar calendar = Calendar.getInstance();
    try { calendar.set(year, month - 1, day, hour, minute, second); }
    catch(Exception e) { return null; }
    return new java.util.Date(calendar.getTime().getTime());
  }

  /** Makes a date String in the format MM/DD/YYYY from a Date
   * @param date The Date
   * @return A date String in the format MM/DD/YYYY
   */  
  public static String toDateString(java.util.Date date)
  {
    if(date == null) return null;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int year = calendar.get(Calendar.YEAR);
    String monthStr;
    String dayStr;
    String yearStr;
    if(month < 10) { monthStr = "0" + month; }
    else { monthStr = "" + month; }
    if(day < 10) { dayStr = "0" + day; }
    else { dayStr = "" + day; }
    yearStr = "" + year;
    return monthStr + "/" + dayStr + "/" + yearStr;
  }

  /** Makes a time String in the format HH:MM:SS from a Date. If the seconds are 0, then the output is in HH:MM.
   * @param date The Date
   * @return A time String in the format HH:MM:SS or HH:MM
   */  
  public static String toTimeString(java.util.Date date)
  {
    if(date == null) return null;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return(toTimeString(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)));
  }

  /** Makes a time String in the format HH:MM:SS from a separate ints for hour, minute, and second. If the seconds are 0, then the output is in HH:MM.
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   * @return A time String in the format HH:MM:SS or HH:MM
   */  
  public static String toTimeString(int hour, int minute, int second)
  {
    String hourStr;
    String minuteStr;
    String secondStr;
    if(hour < 10) { hourStr = "0" + hour; }
    else { hourStr = "" + hour; }
    if(minute < 10) { minuteStr = "0" + minute; }
    else { minuteStr = "" + minute; }
    if(second < 10) { secondStr = "0" + second; }
    else { secondStr = "" + second; }
    if(second == 0) return hourStr + ":" + minuteStr;
    else return hourStr + ":" + minuteStr + ":" + secondStr;
  }

  /** Makes a combined data and time string in the format "MM/DD/YYYY HH:MM:SS" from a Date. If the seconds are 0 they are left off.
   * @param date The Date
   * @return A combined data and time string in the format "MM/DD/YYYY HH:MM:SS" where the seconds are left off if they are 0.
   */  
  public static String toDateTimeString(java.util.Date date)
  {
    if(date == null) return "";
    String dateString = toDateString(date);
    String timeString = toTimeString(date);
    if(dateString != null && timeString != null) return dateString + " " + timeString;
    else return "";
  }
}
