/*
 * $Id: DebugLoggingManager.java,v 1.1 2004/04/22 15:41:04 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.shark.logging;

import org.ofbiz.base.util.Debug;

import org.enhydra.shark.api.internal.logging.LoggingManager;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.RootException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class DebugLoggingManager implements LoggingManager {

    public static final String module = DebugLoggingManager.class.getName();
    private CallbackUtilities cus = null;

    public void configure(CallbackUtilities cus) throws RootException {
        this.cus = cus;
    }

    public void error(String msg) throws RootException {
        Debug.logError(msg, module);
    }

    public void error(String msg, RootException ex) throws RootException {
        Debug.logError(ex, msg, module);
    }

    public void error(String channel, String msg) throws RootException {
        Debug.logError(msg, channel);
    }

    public void error(String channel, String msg, RootException ex) throws RootException {
        Debug.logError(ex, msg, channel);
    }

    public void warn(String msg) throws RootException {
        Debug.logWarning(msg, module);
    }

    public void warn(String msg, RootException ex) throws RootException {
        Debug.logWarning(ex, msg, module);
    }

    public void warn(String channel, String msg) throws RootException {
        Debug.logWarning(msg, channel);
    }

    public void warn(String channel, String msg, RootException ex) throws RootException {
        Debug.logWarning(ex, msg, channel);
    }

    public void info(String msg) throws RootException {
        Debug.logInfo(msg, module);
    }

    public void info(String msg, RootException ex) throws RootException {
        Debug.logInfo(ex, msg, module);
    }

    public void info(String channel, String msg) throws RootException {
        Debug.logInfo(msg, channel);
    }

    public void info(String channel, String msg, RootException ex) throws RootException {
        Debug.logInfo(ex, msg, channel);
    }

    public void debug(String msg) throws RootException {
        Debug.logVerbose(msg, module);
    }

    public void debug(String msg, RootException ex) throws RootException {
        Debug.logVerbose(ex, msg, module);
    }

    public void debug(String channel, String msg) throws RootException {
        Debug.logVerbose(msg, channel);
    }

    public void debug(String channel, String msg, RootException ex) throws RootException {
        Debug.logVerbose(ex, msg, channel);
    }
}
