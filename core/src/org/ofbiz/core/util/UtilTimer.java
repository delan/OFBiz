/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/07/15 16:36:18  azeneski
 * Initial Import
 *
 */

package org.ofbiz.core.util;

import java.util.*;

/**
 * <p><b>Title:</b> Timer handling utilities
 * <p><b>Description:</b> Utility class for simple reporting of the progress of a process. Steps are labelled, and the time between each label (or message) and the time since the start are reported in each call to timerString.
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
public class UtilTimer
{
  long startTime;
  long lastMessageTime;
  String lastMessage = null;
  boolean quiet = false;

  /** Default constructor. Starts the timer.
   */  
  public UtilTimer()
  {
    startTime = (new Date()).getTime();
    lastMessageTime = startTime;
  }

  /** Creates a string with information including the passed message, the last passed message and the time since the last call, and the time since the beginning
   * @param message A message to put into the timer String
   * @return A String with the timing information, the timer String
   */  
  public String timerString(String message)
  {
    String retString =  "[[" + message + ": seconds since start: " + secondsSinceStart() + ",since last(" + lastMessage + "):" + secondsSinceLast() + "]]";
    lastMessageTime = (new Date()).getTime();
    lastMessage = message;
    if(!quiet && UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println(retString);
    return retString;
  }
  
  /** Returns the number of seconds since the timer started
   * @return The number of seconds since the timer started
   */  
  public double secondsSinceStart()
  {
    return ((double)timeSinceStart())/1000.0;
  }
  
  /** Returns the number of seconds since the last time timerString was called
   * @return The number of seconds since the last time timerString was called
   */  
  public double secondsSinceLast()
  {
    return ((double)timeSinceLast())/1000.0;
  }
  
  /** Returns the number of milliseconds since the timer started
   * @return The number of milliseconds since the timer started
   */  
  public long timeSinceStart()
  {
    long currentTime = (new Date()).getTime();
    return currentTime - startTime;
  }

  /** Returns the number of milliseconds since the last time timerString was called
   * @return The number of milliseconds since the last time timerString was called
   */  
  public long timeSinceLast()
  {
    long currentTime = (new Date()).getTime();
    return currentTime - lastMessageTime;
  }
  
  /** Sets the value of the quiet member, denoting whether log output is off or not
   * @param quiet The new value of quiet
   */  
  public void setQuiet(boolean quiet)
  {
    this.quiet = quiet;
  }
  
  /** Gets the value of the quiet member, denoting whether log output is off or not
   * @return The value of quiet
   */  
  public boolean getQuiet()
  {
    return quiet;
  }
}