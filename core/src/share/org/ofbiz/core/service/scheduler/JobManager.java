/*
 * $Id$ 
 */

package org.ofbiz.core.service.scheduler;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> JobManager.java
 * <p><b>Description:</b> Manages scheduled events/jobs.
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
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on July 17, 2001
 */

public class JobManager {
    
    protected JobScheduler js;
    protected GenericDelegator delegator;
    protected ServiceDispatcher dispatcher;
    
    /** Creates a new JobManager object. */
    public JobManager(ServiceDispatcher dispatcher, GenericDelegator delegator) {
        this.dispatcher = dispatcher;
        this.delegator = delegator;
        js = new JobScheduler(this);
        loadJobs();
    }
    
    /** Loads / Re-Loads Job Definitions from the Job Entity. */
    public void loadJobs() {        
        // Get all scheduled jobs from the database.
        Collection jobList = null;
        Debug.logInfo("[JobScheduler.loadJobs] : loading jobs...");
        try {
            jobList = delegator.findAll("JobSandbox");
        }
        catch ( NullPointerException npe ) {
            Debug.logError(npe,"[JobManager.loadJobs] : null pointer from delegator");
        }
        catch ( GenericEntityException e ) {
            Debug.logError(e,"[JobManager.loadJobs] : Cannot get JobSandbox entities");            
        }        
        if ( jobList != null ) {            
            Iterator i = jobList.iterator();
            while ( i.hasNext() ) {
                // Add the job.
                try {
                    // Map context = getContextMethod()?
                    Job thisJob = addJob((GenericValue)i.next(),null); // fix the context
                }
                catch ( JobSchedulerException e ) {
                    Debug.logError(e,"[JobManager.loadJobs] : Cannot add job to queue.");
                }
            }
        }        
    }
    
    /** Create a Job object and add to the queue. */
    public synchronized Job addJob(GenericValue value, Map context) throws JobSchedulerException {
        return addJob(value,context,null);
    }
    
    /** Create a Job object and add to the queue. */
    public synchronized Job addJob(GenericValue value, Map context, GenericRequester req) throws JobSchedulerException {
        Job job = new Job(value,context,req);
        
        // Queue the job with the scheduler.
        // This can be modified to queue several schedulers.
        boolean queued = false;
        while (!queued) {
            if ( !js.containsJob(job) ) {
                js.queueJob(job);        
                queued = true;
            }
            else {
                job.adjustSeqNum();
            }
        }
        return job;
    }
        
    /** Removes all jobs and stops the scheduler. */
    public synchronized void clearJobs() {
        if ( js != null ) 
            js.clearJobs();                    
    }
        
    /** Returns a list of Jobs. */
    public synchronized List getJobList() {
        LinkedList jobList = new LinkedList();
        Iterator iterator = js.iterator();
        while (iterator.hasNext())
            jobList.add(iterator.next());
        return jobList;
    }
                     
    /** Close out the scheduler thread. */
    public void finalize() {
        if (js != null) {
            js.stop();
            js = null;
            Debug.logInfo("JobManager: Stopped Scheduler Thread.");
        }
    }
    
    /** Returns the ServiceDispatcher. */
    public ServiceDispatcher getDispatcher() {
        return this.dispatcher;
    }
}
