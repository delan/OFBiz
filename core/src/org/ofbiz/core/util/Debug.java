/*
 * $Id$
 * $Log$
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

import java.io.*;
import java.util.Date;

/**
 * <p><b>Title:</b> Debug.java
 * <p><b>Description:</b> Debugging Methods.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on July 1, 2001, 5:03 PM
 */
public final class Debug {
    
    public static void print(String msg) {
        long timeStamp = System.currentTimeMillis();
        System.out.println(timeStamp + " : " + msg);
    }
    
    public static void println(String msg) {
        print(msg);
    }
    
    public static void print(Exception e, String msg) {
        print((Throwable)e, msg);
    }
    
    public static void print(Exception e) {
        print(e, null);
    }
    
    public static void print(Throwable t, String msg) {
        if (msg != null )
            print(msg);
        print("Received throwable with Message: "+t.getMessage());
        t.printStackTrace();
    }
    
    public static void print(Throwable t) {
        print(t, null);
    }
    
    public static void log(Throwable t, String msg) {
        print(t,msg);
    }
    
    public static void log(Exception e, String msg) {
        print(e,msg);
    }
    
    public static void log(Throwable t) {
        print(t);
    }
    
    public static void log(Exception e ) {
        print(e);
    }

    public static void logInfo(Exception e ) {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) {
        print(e);
      }
    }

    public static void logWarning(Exception e ) {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true")) {
        print(e);
      }
    }

    public static void logError(Exception e ) {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true")) {
        print(e);
      }
    }
    
    public static void logError(Exception e, String msg) {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true")) {
            print(e,msg);
        }
    }
    
    public static void logError(Throwable t, String msg) {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true")) {
            print(t,msg);
        }
    }

    public static void log(String msg) {
        print(msg);
    }
    
    public static void logInfo(String msg) {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) {
        print(msg);
      }
    }
    
    public static void logWarning(String msg) {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true")) {
        print(msg);
      }
    }
    
    public static void logError(String msg) {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true")) {
        print(msg);
      }
    }
}
