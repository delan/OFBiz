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

package org.ofbiz.core.service.scheduler;

import java.util.*;

import org.ofbiz.core.util.*;

/**
 * JobScheduler
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @created    March 3, 2002
 * @version    1.2
 */
public class JobScheduler implements Runnable {

    public static final String module = JobScheduler.class.getName();
    public static final int MIN_THREADS = 1;
    public static final int MAX_THREADS = 15;
    public static final int MAX_JOBS = 3;
    public static final long MAX_TTL = 18000000;

    protected JobManager jm;
    protected boolean isRunning;
    protected Thread thread;
    protected LinkedList pool;
    protected LinkedList run;
    protected TreeSet queue;
    protected long sleep;

    /**
     * Creates a new JobScheduler
     * @param jm JobManager associated with this scheduler
     */
    public JobScheduler(JobManager jm) {
        this.jm = jm;
        this.queue = new TreeSet();
        this.run = new LinkedList();
        this.sleep = -1;
        this.isRunning = true;
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
    public void clearJobs() {
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

    private void updateDelay(long sleep) {
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
        queueNow(firstJob);

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
    public void stop() {
        isRunning = false;
        Debug.logInfo("JobScheduler: Shutting down...", module);
        notify();
    }

    /**
     * Returns an Iterator of the job scheduled queue
     */
    public Iterator iterator() {
        return queue.iterator();
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
        Debug.logVerbose("New run queue size: " + run.size(), module);
        if (run.size() > pool.size() && pool.size() < maxThreads()) {
            int calcSize = (run.size() / jobsPerThread()) - (pool.size());
            int addSize = calcSize > maxThreads() ? maxThreads() : calcSize;
            for (int i = 0; i < addSize; i++) {
                JobInvoker iv = new JobInvoker(this, threadWaitTime());
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
                JobInvoker iv = new JobInvoker(this, threadWaitTime());
                pool.add(iv);
            }
        }
    }

    /**
     * Returns the JobManager
     */
    public JobManager getManager() {
        return jm;
    }

    // Creates the invoker pool
    private LinkedList createThreadPool() {
        LinkedList threadPool = new LinkedList();
        while (threadPool.size() < minThreads()) {
            JobInvoker iv = new JobInvoker(this, threadWaitTime());
            threadPool.add(iv);
        }

        return threadPool;
    }

    private int maxThreads() {
        int max = MAX_THREADS;
        try {
            max = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.max"));
        } catch (NumberFormatException nfe) {
           Debug.logError("Problems reading values from serviceengine.properties file. Using defaults.", module);
        }
        return max;
    }

    private int minThreads() {
        int min = MIN_THREADS;
        try {
            min = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.min"));
        } catch (NumberFormatException nfe) {
           Debug.logError("Problems reading values from serviceengine.properties file. Using defaults.", module);
        }
        return min;
    }

    private int jobsPerThread() {
        int jobs = MAX_JOBS;
        try {
            jobs = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.jobs"));
        } catch (NumberFormatException nfe) {
           Debug.logError("Problems reading values from serviceengine.properties file. Using defaults.", module);
        }
        return jobs;
    }

    private int threadWaitTime() {
        int wait = JobInvoker.WAIT_TIME;
        try {
            wait = Integer.parseInt(UtilProperties.getPropertyValue("servicesengine", "pool.thread.wait"));
        } catch (NumberFormatException nfe) {
           Debug.logError("Problems reading values from serviceengine.properties file. Using defaults.", module);
        }
        return wait;
    }
}

