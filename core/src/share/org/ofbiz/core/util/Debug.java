/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.util;

import java.io.*;
import java.util.*;
import java.text.DateFormat;

import org.apache.log4j.*;

/**
 * Configurable Debug logging wrapper class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    July 1, 2001
 */
public final class Debug {

    public static final boolean useLog4J = true;
    static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    public static final int ALWAYS = 0;
    public static final int VERBOSE = 1;
    public static final int TIMING = 2;
    public static final int INFO = 3;
    public static final int IMPORTANT = 4;
    public static final int WARNING = 5;
    public static final int ERROR = 6;
    public static final int FATAL = 7;

    public static final String[] levels = {"Always", "Verbose", "Timing", "Info", "Important", "Warning", "Error", "Fatal"};
    public static final String[] levelProps = {"", "print.verbose", "print.timing", "print.info", "print.important", "print.warning", "print.error", "print.fatal"};
    public static final Priority[] levelObjs = {Priority.INFO, Priority.DEBUG, Priority.DEBUG, Priority.INFO, Priority.INFO, Priority.WARN, Priority.ERROR, Priority.FATAL};

    protected static PrintStream printStream = System.out;
    protected static PrintWriter printWriter = new PrintWriter(printStream);

    protected static boolean levelOnCache[] = new boolean[8];
    protected static final boolean useLevelOnCache = true;
    
    static {
        //initialize Log4J
        PropertyConfigurator.configure(FlexibleProperties.makeFlexibleProperties(UtilURL.fromResource("debug")));
        
        //initialize levelOnCache
        for (int i=0; i<8; i++) {
            levelOnCache[i] = (i == Debug.ALWAYS || UtilProperties.propertyValueEqualsIgnoreCase("debug", levelProps[i], "true"));
        }
    }

    static Category root = Category.getRoot();

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

    public static Category getLogger(String module) {
        if (module != null && module.length() > 0) {
            return Category.getInstance(module);
        } else {
            return root;
        }
    }

    public static void log(int level, Throwable t, String msg, String module) {
        log(level, t, msg, module, "org.ofbiz.core.util.Debug");
    }

    public static void log(int level, Throwable t, String msg, String module, String callingClass) {
        if (isOn(level)) {
            if (useLog4J) {
                Category logger = getLogger(module);
                logger.log(callingClass, levelObjs[level], msg, t);
            } else {
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
    }

    public static boolean isOn(int level) {
        if (useLevelOnCache) {
            return levelOnCache[level];
        } else {
            return (level == Debug.ALWAYS || UtilProperties.propertyValueEqualsIgnoreCase("debug", levelProps[level], "true"));
        }
    }
    public static void log(String msg) {
        log(Debug.ALWAYS, null, msg, null);
    }
    public static void log(String msg, String module) {
        log(Debug.ALWAYS, null, msg, module);
    }
    public static void log(Throwable t) {
        log(Debug.ALWAYS, t, null, null);
    }
    public static void log(Throwable t, String msg) {
        log(Debug.ALWAYS, t, msg, null);
    }
    public static void log(Throwable t, String msg, String module) {
        log(Debug.ALWAYS, t, msg, module);
    }

    public static boolean verboseOn() {
        return isOn(Debug.VERBOSE);
    }
    public static void logVerbose(String msg) {
        log(Debug.VERBOSE, null, msg, null);
    }
    public static void logVerbose(String msg, String module) {
        log(Debug.VERBOSE, null, msg, module);
    }
    public static void logVerbose(Throwable t) {
        log(Debug.VERBOSE, t, null, null);
    }
    public static void logVerbose(Throwable t, String msg) {
        log(Debug.VERBOSE, t, msg, null);
    }
    public static void logVerbose(Throwable t, String msg, String module) {
        log(Debug.VERBOSE, t, msg, module);
    }

    public static boolean timingOn() {
        return isOn(Debug.TIMING);
    }
    public static void logTiming(String msg) {
        log(Debug.TIMING, null, msg, null);
    }
    public static void logTiming(String msg, String module) {
        log(Debug.TIMING, null, msg, module);
    }
    public static void logTiming(Throwable t) {
        log(Debug.TIMING, t, null, null);
    }
    public static void logTiming(Throwable t, String msg) {
        log(Debug.TIMING, t, msg, null);
    }
    public static void logTiming(Throwable t, String msg, String module) {
        log(Debug.TIMING, t, msg, module);
    }

    public static boolean infoOn() {
        return isOn(Debug.INFO);
    }
    public static void logInfo(String msg) {
        log(Debug.INFO, null, msg, null);
    }
    public static void logInfo(String msg, String module) {
        log(Debug.INFO, null, msg, module);
    }
    public static void logInfo(Throwable t) {
        log(Debug.INFO, t, null, null);
    }
    public static void logInfo(Throwable t, String msg) {
        log(Debug.INFO, t, msg, null);
    }
    public static void logInfo(Throwable t, String msg, String module) {
        log(Debug.INFO, t, msg, module);
    }

    public static boolean importantOn() {
        return isOn(Debug.IMPORTANT);
    }
    public static void logImportant(String msg) {
        log(Debug.IMPORTANT, null, msg, null);
    }
    public static void logImportant(String msg, String module) {
        log(Debug.IMPORTANT, null, msg, module);
    }
    public static void logImportant(Throwable t) {
        log(Debug.IMPORTANT, t, null, null);
    }
    public static void logImportant(Throwable t, String msg) {
        log(Debug.IMPORTANT, t, msg, null);
    }
    public static void logImportant(Throwable t, String msg, String module) {
        log(Debug.IMPORTANT, t, msg, module);
    }

    public static boolean warningOn() {
        return isOn(Debug.WARNING);
    }
    public static void logWarning(String msg) {
        log(Debug.WARNING, null, msg, null);
    }
    public static void logWarning(String msg, String module) {
        log(Debug.WARNING, null, msg, module);
    }
    public static void logWarning(Throwable t) {
        log(Debug.WARNING, t, null, null);
    }
    public static void logWarning(Throwable t, String msg) {
        log(Debug.WARNING, t, msg, null);
    }
    public static void logWarning(Throwable t, String msg, String module) {
        log(Debug.WARNING, t, msg, module);
    }

    public static boolean errorOn() {
        return isOn(Debug.ERROR);
    }
    public static void logError(String msg) {
        log(Debug.ERROR, null, msg, null);
    }
    public static void logError(String msg, String module) {
        log(Debug.ERROR, null, msg, module);
    }
    public static void logError(Throwable t) {
        log(Debug.ERROR, t, null, null);
    }
    public static void logError(Throwable t, String msg) {
        log(Debug.ERROR, t, msg, null);
    }
    public static void logError(Throwable t, String msg, String module) {
        log(Debug.ERROR, t, msg, module);
    }

    public static boolean fatalOn() {
        return isOn(Debug.FATAL);
    }
    public static void logFatal(String msg) {
        log(Debug.FATAL, null, msg, null);
    }
    public static void logFatal(String msg, String module) {
        log(Debug.FATAL, null, msg, module);
    }
    public static void logFatal(Throwable t) {
        log(Debug.FATAL, t, null, null);
    }
    public static void logFatal(Throwable t, String msg) {
        log(Debug.FATAL, t, msg, null);
    }
    public static void logFatal(Throwable t, String msg, String module) {
        log(Debug.FATAL, t, msg, module);
    }

    public static void set(int level, boolean on) {
        if (!useLevelOnCache)
            return;
        levelOnCache[level] = on;
    }
}
