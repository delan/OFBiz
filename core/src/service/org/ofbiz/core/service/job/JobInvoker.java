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
package org.ofbiz.core.service.job;

import java.util.*;

import org.ofbiz.core.service.config.*;
import org.ofbiz.core.util.*;

/**
 * JobInvoker
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class JobInvoker implements Runnable {

    public static final String module = JobInvoker.class.getName();
    public static final long THREAD_TTL = 18000000;
    public static final int WAIT_TIME = 750;

    private JobPoller jp = null;
    private Thread thread = null;
    private Date created = null;
    private int count = 0;
    private int wait = 0;
    private boolean run = false;

    public JobInvoker(JobPoller jp) {
        this(jp, WAIT_TIME);
    }

    public JobInvoker(JobPoller jp, int wait) {
        this.created = new Date();
        this.run = true;
        this.count = 0;
        this.jp = jp;
        this.wait = wait;

        // get a new thread
        thread = new Thread(this);
        thread.setDaemon(false);
        if (Debug.verboseOn()) Debug.logVerbose("JobInoker: Starting Invoker Thread -- " + thread.getName(), module);
        thread.start();
    }
    
    protected JobInvoker() {}

    /**
     * Tells the thread to stop after the next job.
     */
    public void stop() {
        run = false;        
    }

    /**
     * Wakes up this thread.
     */
    public void wakeUp() {
        notifyAll();
    }

    /**
     * Gets the number of times this thread was used.
     * @return The number of times used.
     */
    public int getUsage() {
        return count;
    }

    /**
     * Gets the time when this thread was created.
     * @returns Time in milliseconds when this was created.
     */
    public long getTime() {
        return created.getTime();
    }

    public synchronized void run() {
        while (run) {
            Job job = jp.next();

            if (job == null) {
                try {
                    wait(wait);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    stop();
                }
            } else {
                if (Debug.verboseOn()) Debug.logVerbose("Invoker: " + thread.getName() + " executing job -- " + job.getJobName(), module);
                job.exec();
                if (Debug.verboseOn()) Debug.logVerbose("Invoker: " + thread.getName() + " finished executing job -- " + job.getJobName(), module);
                count++;
                if (Debug.verboseOn()) Debug.logVerbose("Invoker: " + thread.getName() + " (" + count + ") total.", module);
            }
            long diff = (new Date().getTime() - this.getTime());

            if (getTTL() > 0 && diff > getTTL())
                jp.removeThread(this);
        }
        if (Debug.verboseOn()) Debug.logVerbose("Invoker: " + thread.getName() + " dead -- " + UtilDateTime.nowTimestamp(), module);
    }

    private long getTTL() {
        long ttl = THREAD_TTL;

        try {
            ttl = Long.parseLong(ServiceConfigUtil.getElementAttr("thread-pool", "ttl"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.xml file [" + nfe.toString() + "]. Using defaults.", module);
        }
        return ttl;
    }
}
