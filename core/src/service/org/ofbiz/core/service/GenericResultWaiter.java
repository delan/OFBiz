/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0 
 */
public class GenericResultWaiter implements GenericRequester {

    public static final String module = GenericResultWaiter.class.getName();
    
    /** Status code for a running service */
    public static final int SERVICE_RUNNING = -1;
    /** Status code for a failed service */
    public static final int SERVICE_FAILED = 0;
    /** Status code for a successful service */
    public static final int SERVICE_FINISHED = 1;
    
    private boolean completed = false;
    private int status = -1;
    private Map result = null;
    private Exception exception = null;

    /**
     * @see org.ofbiz.core.service.GenericRequester#receiveResult(java.util.Map)
     */
    public synchronized void receiveResult(Map result) {
        this.result = result;
        completed = true;
        status = SERVICE_FINISHED;
        notify();
        if (Debug.verboseOn()) 
            Debug.logVerbose("Received Result (" + completed + ") -- " + result, module);
    }
    
    /**
     * @see org.ofbiz.core.service.GenericRequester#receiveException(java.lang.Exception)
     */
    public synchronized void receiveException(Exception exception) {
        this.exception = exception;
        completed = true;
        status = SERVICE_FAILED;
        notify();              
    }
    
    /**
     * Returns the status of the service.
     * @return int Status code
     */
    public synchronized int status() {
        return this.status;
    }
    
    /**
     * If the service has completed return true     * @return boolean     */
    public synchronized boolean isCompleted() {
        return completed;
    }
    
    /**
     * Returns the exception which was thrown or null if none
     * @return Exception
     */
    public synchronized Exception getException() {
        if (!isCompleted())
            throw new java.lang.IllegalStateException("Cannot return exception, synchronous call has not completed.");
        return this.exception;
    }    

    /**
     * Gets the results of the service or null if none     * @return Map     */
    public synchronized Map getResult() {
        if (!isCompleted())
            throw new java.lang.IllegalStateException("Cannot return result, asynchronous call has not completed.");
        return result;
    }

    /**
     * Waits for the service to complete     * @return Map     */
    public synchronized Map waitForResult() {
        return this.waitForResult(10);
    }

    /**
     * Waits for the service to complete, check the status ever n milliseconds     * @param milliseconds     * @return Map     */
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

