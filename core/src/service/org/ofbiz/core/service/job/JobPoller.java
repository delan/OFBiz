/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.config.*;
import org.ofbiz.core.util.*;

/**
 * JobPoller - Polls for persisted jobs to run.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     <a href="mailto:">Magnus Rosenquist</a>
 * @version    $Revision$
 * @since      2.0
 */
public class JobPoller implements Runnable {

    public static final String module = JobPoller.class.getName();

    public static final int MIN_THREADS = 1;
    public static final int MAX_THREADS = 15;
    public static final int MAX_JOBS = 3;
    public static final int POLL_WAIT = 20000;
    public static final long MAX_TTL = 18000000;

    protected boolean isRunning = false;    
    protected boolean threadStopped = false;
    
    protected Thread thread = null;
    protected LinkedList pool = null;
    protected LinkedList run = null;
    protected JobManager jm= null;

    /**
     * Creates a new JobScheduler
     * @param jm JobManager associated with this scheduler
     */
    public JobPoller(JobManager jm) {
        this.jm = jm;
        this.run = new LinkedList();       
        
        // create the thread pool
        this.pool = createThreadPool();

        // start the thread only if polling is enabled
        if (pollEnabled()) {
                        
            // create the poller thread
            thread = new Thread(this, this.toString());
            thread.setDaemon(false);
            
            // start the poller
            this.isRunning = true;
            thread.start();
        }
    }
    
    protected JobPoller() {}

    public synchronized void run() {
        if (Debug.infoOn()) Debug.logInfo("JobPoller: (" + thread.getName() + ") Thread Running...", module);
        while (isRunning) {
            try {
                // grab a list of jobs to run.
                Iterator poll = jm.poll();

                while (poll.hasNext()) {
                    Job job = (Job) poll.next();

                    if (job.isValid())
                        queueNow(job);
                }
                wait(pollWaitTime());
            } catch (InterruptedException e) {
                Debug.logError(e, module);
                stop();
            }
        }
        threadStopped = true;
        if (Debug.infoOn()) Debug.logInfo("JobPoller: (" + thread.getName() + ") Thread ending...", module);
    }

    /**
     * Returns the JobManager
     */
    public JobManager getManager() {
        return jm;
    }

    /**
     * Stops the JobPoller
     */
    public void stop() {
        isRunning = false;
        Debug.logInfo("JobPoller: Shutting down...", module);
        while (!threadStopped) {
            Debug.logInfo("Waiting for thread to stop.");
            try {
                Thread.sleep(1000);
            } catch(Exception e) {
                Debug.logError(e, "Error putting job poller thread to sleep");
            }
        }
        destroyThreadPool();
    }


    /**
     * Stops all threads in the threadPool and clears
     * the pool as final step.
     */
    private void destroyThreadPool() {
        Debug.logInfo("Destroying thread pool...", module);
        Iterator it = pool.iterator();
        while (it.hasNext()) {
            JobInvoker ji = (JobInvoker) it.next();
            ji.stop();
        }
        pool.clear();
    }

    /**
     * Returns the next job to run
     */
    public synchronized Job next() {
        if (run.size() > 0)
            return (Job) run.removeFirst();
        return null;
    }

    /**
     * Adds a job to the RUN queue
     */
    public synchronized void queueNow(Job job) {
        run.add(job);
        if (Debug.verboseOn()) Debug.logVerbose("New run queue size: " + run.size(), module);
        if (run.size() > pool.size() && pool.size() < maxThreads()) {
            int calcSize = (run.size() / jobsPerThread()) - (pool.size());
            int addSize = calcSize > maxThreads() ? maxThreads() : calcSize;

            for (int i = 0; i < addSize; i++) {
                JobInvoker iv = new JobInvoker(this, invokerWaitTime());
                pool.add(iv);
            }
        }
    }

    /**
     * Removes a thread from the pool.
     * @param JobInvoker The invoker to remove.
     */
    public synchronized void removeThread(JobInvoker invoker) {
        pool.remove(invoker);
        invoker.stop();
        if (pool.size() < minThreads()) {
            for (int i = 0; i < minThreads() - pool.size(); i++) {
                JobInvoker iv = new JobInvoker(this, invokerWaitTime());
                pool.add(iv);
            }
        }
    }

    // Creates the invoker pool
    private LinkedList createThreadPool() {
        LinkedList threadPool = new LinkedList();

        while (threadPool.size() < minThreads()) {
            JobInvoker iv = new JobInvoker(this, invokerWaitTime());
            threadPool.add(iv);
        }

        return threadPool;
    }

    private int maxThreads() {
        int max = MAX_THREADS;

        try {
            max = Integer.parseInt(ServiceConfigUtil.getElementAttr("thread-pool", "max-threads"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.xml file [" + nfe.toString() + "]. Using defaults.", module);
        }
        return max;
    }

    private int minThreads() {
        int min = MIN_THREADS;

        try {
            min = Integer.parseInt(ServiceConfigUtil.getElementAttr("thread-pool", "min-threads"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.xml file [" + nfe.toString() + "]. Using defaults.", module);
        }
        return min;
    }

    private int jobsPerThread() {
        int jobs = MAX_JOBS;

        try {
            jobs = Integer.parseInt(ServiceConfigUtil.getElementAttr("thread-pool", "jobs"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.xml file [" + nfe.toString() + "]. Using defaults.", module);
        }
        return jobs;
    }

    private int invokerWaitTime() {
        int wait = JobInvoker.WAIT_TIME;

        try {
            wait = Integer.parseInt(ServiceConfigUtil.getElementAttr("thread-pool", "wait-millis"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.xml file [" + nfe.toString() + "]. Using defaults.", module);
        }
        return wait;
    }

    private int pollWaitTime() {
        int poll = POLL_WAIT;

        try {
            poll = Integer.parseInt(ServiceConfigUtil.getElementAttr("thread-pool", "poll-db-millis"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.xml file [" + nfe.toString() + "]. Using defaults.", module);
        }
        return poll;
    }

    private boolean pollEnabled() {
        String enabled = ServiceConfigUtil.getElementAttr("thread-pool", "poll-enabled");

        if (enabled.equalsIgnoreCase("false"))
            return false;
        return true;
    }
}

