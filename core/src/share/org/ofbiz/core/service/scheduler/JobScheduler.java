/*
 * $Id$ 
 */

package org.ofbiz.core.service.scheduler;

import java.util.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> JobScheduler.java
 * <p><b>Description:</b> Thread For Job Scheduling.
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 15, 2001
 *@version    1.0
 */
public class JobScheduler implements Runnable {
    
    protected JobManager jm;
    protected Thread thread;
    protected long sleep;
    protected boolean isRunning;
    protected SortedSet queue;
    
    /** Creates a new <code>JobScheduler</code>. */
    public JobScheduler( JobManager jm) {
        this.jm = jm;
        this.queue = new TreeSet();
        this.sleep = -1;
        this.isRunning = true;
        
        // start the thread
        thread = new Thread(this, this.toString());
        thread.setDaemon(false);
        thread.start();
    }
    
    /** Returns true if another job is scheduled for the same time. */
    public synchronized boolean containsJob(Job job) {
        return queue.contains(job);
    }
    
    /** Queues a job. */
    public synchronized void queueJob(Job job) throws JobSchedulerException {
        if ( job.getRuntime() != -1 ) {
            if ( !containsJob(job) ) {
                queue.add(job);
                updateDelay( ((Job) queue.first()).getRuntime());
            }
            else {
                throw new JobSchedulerException("Job conflicts with existing job.");
            }
        }
    }
    
    /** Clears the jobs from the queue. */
    public synchronized void clearJobs() {
        this.sleep = -1;
        queue = new TreeSet();
    }
    
    /** Remove a job from the queue. */
    public synchronized void removeJob(Job job)  throws JobSchedulerException {
        if (queue.contains(job)) {
            queue.remove(job);
            updateDelay( ((Job) queue.first()).getRuntime());
        }
        else {
            throw new JobSchedulerException("Job not in queue.");
        }
    }
    
    private synchronized void updateDelay(long sleep) {
        this.sleep = sleep;
        notify();
    }
    
    public synchronized void run() {
        Debug.logInfo("JobScheduler: (" + thread.getName() + ") Thread Running...");
        while( isRunning ) {
            try {
                if ( sleep <= 0 )
                    wait();
                else {
                    long timeout = sleep - System.currentTimeMillis();
                    if ( timeout > 0 )
                        wait(timeout);
                }
                if ( isRunning && sleep >= 0 && (sleep - System.currentTimeMillis() < 1000) ) {
                    sleep = -1;
                    invokeJob();
                }
            }
            catch(InterruptedException e) {
                Debug.log(e);
                stop();
            }
        }
        Debug.logInfo("JobScheduler: (" + thread.getName() + ") Thread ending...");
    }
    
    /** Spawns the invoker thread. */
    private synchronized void invokeJob() {
        if (queue.isEmpty())
            return;
        Job firstJob = (Job) queue.first();
        queue.remove(firstJob);
        
        // Get a new thread and invoke the service.
        new JobInvoker(firstJob,jm.getDispatcher());
        
        // Re-schedule the job if it repeats.
        firstJob.rescheduleJob();        
        if ( firstJob.getRuntime() > 0 ) {
            boolean queued = false;
            while (!queued) {
                try {
                    queueJob(firstJob);
                    queued = true;
                }
                catch ( JobSchedulerException e ) {
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
    
    public synchronized void stop() {
        isRunning = false;
        Debug.logInfo("JobScheduler: Shutting down...");
        notify();
    }
    
    /** Retuns an Iterator of this scheduler's job queue. */
    public synchronized Iterator iterator() {
        return queue.iterator();
    }
}

