/*
 * $Id$
 * $Log$
 * Revision 1.2  2001/12/19 06:43:05  jonesde
 * Added method to get the debug PrintWriter and PrintStream
 *
 * Revision 1.1  2001/09/28 22:56:44  jonesde
 * Big update for fromDate PK use, organization stuff
 *
 * Revision 1.3  2001/08/25 17:29:11  azeneski
 * Started migrating Debug.log to Debug.logInfo and Debug.logError
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

import java.io.*;
import java.util.Date;
import java.text.DateFormat;

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
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version 1.0
 * Created on July 1, 2001, 5:03 PM
 */
public final class Debug {
    static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    
    public static final int ALWAYS = 0;
    public static final int VERBOSE = 1;
    public static final int TIMING = 2;
    public static final int INFO = 3;
    public static final int WARNING = 4;
    public static final int ERROR = 5;
    
    public static final String[] levels = {"Always", "Verbose", "Timing", "Info", "Warning", "Error"};
    public static final String[] levelProps = {"", "print.verbose", "print.timing", "print.info", "print.warning", "print.error"};
    
    protected static PrintStream printStream = System.out;
    protected static PrintWriter printWriter = new PrintWriter(printStream);
    
    public static PrintStream getPrintStream() {
        return printStream;
    }
    public static void setPrintStream(PrintStream printStream) {
        Debug.printStream = printStream;
        Debug.printWriter = new PrintWriter(printStream);
    }
    
    public static PrintWriter getPrintWriter() {
        return printWriter;
    }
    
    public static void log(Throwable t, String msg, int level, String module) {
        if(level == Debug.ALWAYS || UtilProperties.propertyValueEqualsIgnoreCase("debug", levelProps[level], "true")) {
            StringBuffer prefixBuf = new StringBuffer();
            prefixBuf.append(dateFormat.format(new java.util.Date()));
            prefixBuf.append(" [OFBiz");
            if (module != null) {
                prefixBuf.append(":");
                prefixBuf.append(module);
            }
            prefixBuf.append(":");
            prefixBuf.append(levels[level]);
            prefixBuf.append("] ");
            if (msg != null) {
                getPrintWriter().print(prefixBuf.toString());
                getPrintWriter().println(msg);
            }
            if (t != null) {
                getPrintWriter().print(prefixBuf.toString());
                getPrintWriter().println("Received throwable:");
                t.printStackTrace(getPrintWriter());
            }
        }
    }
    
    public static void log(String msg) { log(null, msg, Debug.ALWAYS, null); }
    public static void log(String msg, String module) { log(null, msg, Debug.ALWAYS, module); }
    public static void log(Throwable t) { log(t, null, Debug.ALWAYS, null); }
    public static void log(Throwable t, String msg) { log(t, msg, Debug.ALWAYS, null); }
    public static void log(Throwable t, String msg, String module) { log(t, msg, Debug.ALWAYS, module); }
    
    public static void logVerbose(String msg) { log(null, msg, Debug.VERBOSE, null); }
    public static void logVerbose(String msg, String module) { log(null, msg, Debug.VERBOSE, module); }
    public static void logVerbose(Throwable t) { log(t, null, Debug.VERBOSE, null); }
    public static void logVerbose(Throwable t, String msg) { log(t, msg, Debug.VERBOSE, null); }
    public static void logVerbose(Throwable t, String msg, String module) { log(t, msg, Debug.VERBOSE, module); }
    
    public static void logTiming(String msg) { log(null, msg, Debug.TIMING, null); }
    public static void logTiming(String msg, String module) { log(null, msg, Debug.TIMING, module); }
    public static void logTiming(Throwable t) { log(t, null, Debug.TIMING, null); }
    public static void logTiming(Throwable t, String msg) { log(t, msg, Debug.TIMING, null); }
    public static void logTiming(Throwable t, String msg, String module) { log(t, msg, Debug.TIMING, module); }

    public static void logInfo(String msg) { log(null, msg, Debug.INFO, null); }
    public static void logInfo(String msg, String module) { log(null, msg, Debug.INFO, module); }
    public static void logInfo(Throwable t) { log(t, null, Debug.INFO, null); }
    public static void logInfo(Throwable t, String msg) { log(t, msg, Debug.INFO, null); }
    public static void logInfo(Throwable t, String msg, String module) { log(t, msg, Debug.INFO, module); }

    public static void logWarning(String msg) { log(null, msg, Debug.WARNING, null); }
    public static void logWarning(String msg, String module) { log(null, msg, Debug.WARNING, module); }
    public static void logWarning(Throwable t) { log(t, null, Debug.WARNING, null); }
    public static void logWarning(Throwable t, String msg) { log(t, msg, Debug.WARNING, null); }
    public static void logWarning(Throwable t, String msg, String module) { log(t, msg, Debug.WARNING, module); }

    public static void logError(String msg) { log(null, msg, Debug.ERROR, null); }
    public static void logError(String msg, String module) { log(null, msg, Debug.ERROR, module); }
    public static void logError(Throwable t) { log(t, null, Debug.ERROR, null); }
    public static void logError(Throwable t, String msg) { log(t, msg, Debug.ERROR, null); }
    public static void logError(Throwable t, String msg, String module) { log(t, msg, Debug.ERROR, module); }
}
