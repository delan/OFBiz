/*
 * $Id: JobInvoker.java,v 1.4 2004/01/24 19:37:53 ajzeneski Exp $
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
package org.ofbiz.service.job;

import java.util.Date;

import org.ofbiz.service.config.ServiceConfigUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

/**
 * JobInvoker
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.4 $
 * @since      2.0
 */
public class JobInvoker implements Runnable {

    public static final String module = JobInvoker.class.getName();
    public static final long THREAD_TTL = 18000000;
    public static final int WAIT_TIME = 750;

    private JobPoller jp = null;
    private Thread thread = null;
    private Date created = null;
    private String name = null;
    private int count = 0;
    private int wait = 0;
    private boolean run = false;

    private Job currentJob = null;
    private int statusCode = 0;
    private long jobStart = 0;

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
        this.thread = new Thread(this);
        this.thread.setDaemon(false);
        this.name = this.thread.getName();

        if (Debug.verboseOn()) Debug.logVerbose("JobInoker: Starting Invoker Thread -- " + thread.getName(), module);
        this.thread.start();
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
     * @return Time in milliseconds when this was created.
     */
    public long getTime() {
        return created.getTime();
    }

    /**
     * Gets the name of this JobInvoker.
     * @return Name of the invoker.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the status code for this thread (0 = sleeping, 1 = running job)
     * @return 0 for sleeping or 1 when running a job.
     */
    public int getCurrentStatus() {
        return this.statusCode;
    }

    /**
     * Gets the total time the current job has been running or 0 when sleeping.
     * @return Total time the curent job has been running.
     */
    public long getCurrentRuntime() {
        if (this.jobStart > 0) {
            long now = System.currentTimeMillis();
            return now - this.jobStart;
        } else {
            return 0;
        }
    }

    /**
     * Get the current running job's name.
     * @return String name of the current running job.
     */
    public String getJobName() {
        if (this.statusCode == 1) {
            if (this.currentJob != null) {
                return this.currentJob.getJobName();
            } else {
                return "WARNING: Invalid Job!";
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the service being run.
     * @return The name of the service being run.
     */
    public String getServiceName() {
        String serviceName = null;
        if (this.statusCode == 1) {
            if (this.currentJob != null) {
                if (this.currentJob instanceof GenericServiceJob) {
                    GenericServiceJob gsj = (GenericServiceJob) this.currentJob;
                    serviceName = gsj.getServiceName();
                }
            }
        }
        return serviceName;
    }

    /**
     * Kill this invoker thread.s
     */
    public void kill() {
        this.stop();
        this.statusCode = -1;
        this.thread.interrupt();
        this.thread = null;
    }

    public synchronized void run() {
        while (run) {
            Job job = jp.next();

            if (job == null) {
                try {
                    wait(wait);
                } catch (InterruptedException ie) {
                    Debug.logError(ie, "JobInvoker.run() : InterruptedException", module);
                    stop();
                }
            } else {
                // setup the current job settings
                this.currentJob = job;
                this.statusCode = 1;
                this.jobStart = System.currentTimeMillis();

                // execute the job
                if (Debug.verboseOn()) Debug.logVerbose("Invoker: " + thread.getName() + " executing job -- " + job.getJobName(), module);
                job.exec();
                if (Debug.verboseOn()) Debug.logVerbose("Invoker: " + thread.getName() + " finished executing job -- " + job.getJobName(), module);

                // clear the current job settings
                this.currentJob = null;
                this.statusCode = 0;
                this.jobStart = 0;

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
