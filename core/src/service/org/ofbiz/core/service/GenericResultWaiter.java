/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.service;


import java.util.*;

import org.ofbiz.core.util.*;


/**
 * Generic Result Waiter Class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    October 20, 2001
 *@version    1.0
 */
public class GenericResultWaiter implements GenericRequester {

    public static final String module = GenericResultWaiter.class.getName();

    boolean completed = false;
    Map result = null;

    /** Receive the result of an asynchronous service call
     * @param result Map of name, value pairs composing the result
     */
    public synchronized void receiveResult(Map result) {
        this.result = result;
        completed = true;
        notify();
        if (Debug.verboseOn()) Debug.logVerbose("Received Result (" + completed + ") -- " + result, module);
    }

    public synchronized boolean isCompleted() {
        return completed;
    }

    public synchronized Map getResult() {
        if (!isCompleted())
            throw new java.lang.IllegalStateException("Cannot return result, asynchronous call has not completed.");
        return result;
    }

    public synchronized Map waitForResult() {
        return this.waitForResult(10);
    }

    public synchronized Map waitForResult(long milliseconds) {
        if (Debug.verboseOn()) Debug.logVerbose("Waiting for results...", module);
        while (!isCompleted()) {
            try {
                this.wait(milliseconds);
                if (Debug.verboseOn()) Debug.logVerbose("Waiting...", module);
            } catch (java.lang.InterruptedException e) {
                Debug.logError(e);
            }
        }
        return this.getResult();
    }
}

