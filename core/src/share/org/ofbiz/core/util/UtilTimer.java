/*
 * $Id$
 * $Log$
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
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
public class UtilTimer {
    long startTime;
    long lastMessageTime;
    String lastMessage = null;
    boolean log = false;
    
    /** Default constructor. Starts the timer.
     */
    public UtilTimer() {
        startTime = (new Date()).getTime();
        lastMessageTime = startTime;
        lastMessage = "Begin";
    }
    
    /** Creates a string with information including the passed message, the last passed message and the time since the last call, and the time since the beginning
     * @param message A message to put into the timer String
     * @return A String with the timing information, the timer String
     */
    public String timerString(String message) {
        String retString =  "[[" + message + "- total:" + secondsSinceStart() + 
                            ",since last(" + ((lastMessage.length() > 20) ? (lastMessage.substring(0, 17) + "...") : lastMessage) + "):" + 
                            secondsSinceLast() + "]]";
        lastMessageTime = (new Date()).getTime();
        lastMessage = message;
        if (log) Debug.logTiming(retString);
        return retString;
    }
    
    /** Returns the number of seconds since the timer started
     * @return The number of seconds since the timer started
     */
    public double secondsSinceStart() {
        return ((double)timeSinceStart())/1000.0;
    }
    
    /** Returns the number of seconds since the last time timerString was called
     * @return The number of seconds since the last time timerString was called
     */
    public double secondsSinceLast() {
        return ((double)timeSinceLast())/1000.0;
    }
    
    /** Returns the number of milliseconds since the timer started
     * @return The number of milliseconds since the timer started
     */
    public long timeSinceStart() {
        long currentTime = (new Date()).getTime();
        return currentTime - startTime;
    }
    
    /** Returns the number of milliseconds since the last time timerString was called
     * @return The number of milliseconds since the last time timerString was called
     */
    public long timeSinceLast() {
        long currentTime = (new Date()).getTime();
        return currentTime - lastMessageTime;
    }
    
    /** Sets the value of the log member, denoting whether log output is off or not
     * @param log The new value of log
     */
    public void setLog(boolean log) {
        this.log = log;
    }
    
    /** Gets the value of the log member, denoting whether log output is off or not
     * @return The value of log
     */
    public boolean getLog() {
        return log;
    }
}
