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

package org.ofbiz.core.service.scheduler;

import java.util.*;

import org.ofbiz.core.util.*;

/**
 * JobScheduler
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @created    November 15, 2001
 * @version    1.0
 */
public class JobScheduler implements Runnable {

    public static final String module = JobScheduler.class.getName();
    public static final int MIN_THREADS = 1;
    public static final int MAX_THREADS = 15;
    public static final int THREAD_INCR = 3;
    public static final int MAX_USAGE = 50;
    public static final long MAX_TTL = 18000;


    protected JobManager jm;
    protected Thread thread;
    protected long sleep;
    protected boolean isRunning;
    protected SortedSet queue;
    protected List pool;
    protected int totalThreads;

    /**
     * Creates a new JobScheduler
     * @param jm JobManager associated with this scheduler
     */
    public JobScheduler(JobManager jm) {
        this.jm = jm;
        this.queue = new TreeSet();
        this.sleep = -1;
        this.isRunning = true;
        this.totalThreads = 0;
        this.pool = createThreadPool();

        // start the thread
        thread = new Thread(this, this.toString());
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Returns true if another job is scheduled for the same time
     * @param job Job to test against
     */
    public synchronized boolean containsJob(Job job) {
        return queue.contains(job);
    }

    /**
     * Queues a job
     * @param job The job to queue
     * @throws JobSchedulerException
     */
    public synchronized void queueJob(Job job) throws JobSchedulerException {
        if (job.getRuntime() != -1) {
            if (!containsJob(job)) {
                queue.add(job);
                updateDelay(((Job) queue.first()).getRuntime());
            } else {
                throw new JobSchedulerException("Job conflicts with existing job.");
            }
        }
    }

    /**
     * Clears the job queue
     */
    public synchronized void clearJobs() {
        this.sleep = -1;
        queue = new TreeSet();
    }

    /**
     * Removes a specific job from the queue
     * @param job The job to remove
     * @throws JobSchedulerException
     */
    public synchronized void removeJob(Job job) throws JobSchedulerException {
        if (queue.contains(job)) {
            queue.remove(job);
            updateDelay(((Job) queue.first()).getRuntime());
        } else {
            throw new JobSchedulerException("Job not in queue.");
        }
    }

    private synchronized void updateDelay(long sleep) {
        this.sleep = sleep;
        notify();
    }

    public synchronized void run() {
        Debug.logInfo("JobScheduler: (" + thread.getName() + ") Thread Running...", module);
        while (isRunning) {
            try {
                if (sleep <= 0)
                    wait();
                else {
                    long timeout = sleep - System.currentTimeMillis();
                    if (timeout > 0)
                        wait(timeout);
                }
                if (isRunning && sleep >= 0 && (sleep - System.currentTimeMillis() < 1000)) {
                    sleep = -1;
                    invokeJob();
                }
            } catch (InterruptedException e) {
                Debug.logError(e, module);
                stop();
            }
        }
        Debug.logInfo("JobScheduler: (" + thread.getName() + ") Thread ending...", module);
    }

    // Spawns the invoker thread.
    private synchronized void invokeJob() {
        if (queue.isEmpty()) {
            Debug.logWarning("Job queue is empty.", module);
            return;
        }

        Debug.logVerbose("Getting first job from the queue.", module);
        long timeout = new Date().getTime() + (120*1000);

        Job firstJob = (Job) queue.first();
        queue.remove(firstJob);

        // Get a thread from the pool and invoke the service
        JobInvoker invoker = getInvoker();
        while (invoker == null) {
            long now = new Date().getTime();
            if (now > timeout)
                throw new RuntimeException("Timeout waiting for invoker thread.");
            invoker = getInvoker();
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // invoke the job
        invoker.invoke(this, firstJob);

        // Re-schedule the job if it repeats.
        firstJob.rescheduleJob();
        if (firstJob.getRuntime() > 0) {
            boolean queued = false;
            while (!queued) {
                try {
                    queueJob(firstJob);
                    queued = true;
                } catch (JobSchedulerException e) {
                    firstJob.adjustSeqNum();
                }
            }
        }

        // If the queue is not empty, check the status of the next job.
        if (!queue.isEmpty()) {
            Job nextJob = (Job) queue.first();
            long nextDelayTime = nextJob.getRuntime();
            // invoke the next job if it is less then a second away.
            if (nextDelayTime - System.currentTimeMillis() < 1000)
                invokeJob();
            else
                updateDelay(nextDelayTime);
        }
    }

    /**
     * Stops the JobScheduler
     */
    public synchronized void stop() {
        isRunning = false;
        Debug.logInfo("JobScheduler: Shutting down...", module);
        notify();
    }

    /**
     * Returns an Iterator of the job queue
     */
    public synchronized Iterator iterator() {
        return queue.iterator();
    }

    /**
     * Returns the JobManager
     */
    public JobManager getManager() {
        return jm;
    }

    // Creates the invoker pool
    private List createThreadPool() {
        putInvoker(getInvoker());
        Debug.logInfo("JobScheduler: Created invoker thread pool (" + pool.size() + ")", module);
        return pool;
    }

    /**
     * Gets a JobInvoker thread from the thread pool.
     * @return An invoker thread.
     */
    public JobInvoker getInvoker() {
        int min = MIN_THREADS;
        int max = MAX_THREADS;
        try {
            min = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.min"));
            max = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.max"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.properties file. Using defaults.", module);
        }
        boolean added = true;
        while (totalThreads < min && added) {
            JobInvoker iv = new JobInvoker();
            added = putInvoker(iv);
            totalThreads++;
        }
        if (pool.size() < min && totalThreads < max) {
            for (int i = 0; i < THREAD_INCR; i++) {
                JobInvoker iv = new JobInvoker();
                putInvoker(iv);
            }
        }

        if (pool.size() == 0 && totalThreads == max)
            return null;

        return (JobInvoker) pool.remove(0);
    }

    /**
     * Puts a JobInvoker thread back into the pool.
     * @return Returns 'true' if the invoker was added back to the pool.
     */
    public boolean putInvoker(JobInvoker invoker) {
        // default values
        int max = MAX_THREADS;
        int maxUse = MAX_USAGE;
        long maxTime = MAX_TTL;

        // get values from properties file
        try {
            max = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.max"));
            maxUse = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.utl"));
            maxTime = Long.parseLong(UtilProperties.getPropertyValue("servicesengine", "pool.thread.ttl"));
        } catch (NumberFormatException nfe) {
            Debug.logError("Problems reading values from serviceengine.properties file. Using defaults.", module);
        }

        if (pool == null)
            pool = new ArrayList();

        long now = new Date().getTime();
        long then = invoker.getTime();
        long diff = now - then;
        long invokerTime = diff / 1000;

        Debug.logVerbose("Invoker Ct: " + invoker.getUsage() + " Time: " + invokerTime + " Now: " + now + " Then: " + then + " Diff: " + diff, module);
        if (max > totalThreads && (maxUse <= 0 || maxUse > invoker.getUsage()) && (maxTime <= 0 || maxTime > invokerTime)) {
            pool.add(invoker);
            return true;
        } else {
            totalThreads--;
            return false;
        }
    }
}

