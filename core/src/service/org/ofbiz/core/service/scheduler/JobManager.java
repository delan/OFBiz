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

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * JobManager
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    March 3, 2002
 *@version    1.2
 */
public class JobManager {

    public static final String module = JobManager.class.getName();

    protected JobScheduler js;
    protected GenericDelegator delegator;
    protected ServiceDispatcher dispatcher;

    /** Creates a new JobManager object. */
    public JobManager(ServiceDispatcher dispatcher, GenericDelegator delegator) {
        this.dispatcher = dispatcher;
        this.delegator = delegator;
        js = new JobScheduler(this);
    }

    /**
     * Loads / Re-Loads Job Definitions from the Job Entity.
     * @param loader The dispatch loader name to load jobs for.
     */
    public void loadJobs(String loader) {
        // Get all scheduled jobs for a specific loader from the database.
        Collection jobList = null;
        UtilTimer timer = null;
        if (Debug.timingOn()) {
            timer = new UtilTimer();
            Debug.logTiming(timer.timerString("[JobScheduler.loadJobs] : loading jobs..."), module);
        } else {
            Debug.logInfo("[JobScheduler.loadJobs] : loading jobs...", module);
        }
        try {
            jobList = delegator.findByAnd("JobSandbox", UtilMisc.toMap("loaderName", loader));
        } catch (NullPointerException npe) {
            Debug.logError(npe, "[JobManager.loadJobs] : null pointer from delegator");
        } catch (GenericEntityException e) {
            Debug.logError(e, "[JobManager.loadJobs] : Cannot get JobSandbox entities");
        }
        if (jobList != null) {
            Iterator i = jobList.iterator();
            while (i.hasNext()) {
                // Add the job.
                GenericValue jobValue = (GenericValue) i.next();
                DispatchContext dctx = dispatcher.getLocalContext(loader);
                if (dctx == null) {
                    Debug.logError("Cannot get DispatchContext for loader: " + jobValue.getString("loaderName"), module);
                } else {
                    Job job = new PersistedServiceJob(dctx, jobValue, null);
                    try {
                        scheduleJob(job);
                    } catch (JobSchedulerException e) {
                        Debug.logVerbose(e.getMessage(), module);
                    }
                }
            }
            if (Debug.timingOn()) {
                Debug.logTiming(timer.timerString("[JobScheduler.loadJobs] : Finished"), module);
            } else {
                Debug.logInfo("[JobScheduler.loadJobs] : Finished", module);
            }
        }
    }

    /** Queues a Job to run now. */
    public void runJob(Job job) throws JobSchedulerException {
        if (job.isValid())
            js.queueNow(job);
    }

    /** Create a Job object and add to the queue. */
    public void scheduleJob(GenericValue value) throws JobSchedulerException {
        scheduleJob(value, null);
    }

    /** Create a Job object and add to the queue. */
    public void scheduleJob(GenericValue value, GenericRequester req) throws JobSchedulerException {
        String loader = value.getString("loaderName");
        DispatchContext dctx = dispatcher.getLocalContext(loader);
        if (dctx != null) {
            Job job = new PersistedServiceJob(dctx, value, req);
            scheduleJob(job);
        }
    }

    /** Create a Job object and add to the queue. */
    public void scheduleJob(Job job) throws JobSchedulerException {
        // Queue the job with the scheduler.
        // This can be modified to queue several schedulers.
        Debug.logVerbose("Attempting to schedule job: " + job.getJobName(), module);
        if (job.isValid()) {
            Debug.logVerbose("Scheduling valid job.", module);
            boolean queued = false;
            while (!queued) {
                if (!js.containsJob(job)) {
                    js.queueJob(job);
                    queued = true;
                } else {
                    job.setSequence(job.getSequence() + 1);
                }
            }
        } else {
            throw new JobSchedulerException("Job is not valid or has expired.");
        }
    }

    /** Removes all jobs and stops the scheduler. */
    public void clearJobs() {
        if (js != null)
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

    /** Schedule a job to start at a specific time with specific recurrence info
     *@param loader The name of the local dispatcher to use
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     */
    public void schedule(String loader, String serviceName, Map context, long startTime,
                                      int frequency, int interval, int count) throws JobSchedulerException {
        String dataId = null;
        String infoId = null;
        String jobName = new String(new Long((new Date().getTime())).toString());
        try {
            dataId = delegator.getNextSeqId("RuntimeData").toString();
            GenericValue runtimeData = delegator.makeValue("RuntimeData", UtilMisc.toMap("runtimeDataId", dataId));
            runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
            delegator.create(runtimeData);
        } catch (GenericEntityException ee) {
            throw new JobSchedulerException(ee.getMessage(), ee);
        } catch (SerializeException se) {
            throw new JobSchedulerException(se.getMessage(), se);
        } catch (IOException ioe) {
            throw new JobSchedulerException(ioe.getMessage(), ioe);
        }
        try {
            RecurrenceInfo info = RecurrenceInfo.makeInfo(delegator, startTime, frequency, interval, count);
            infoId = info.primaryKey();
        } catch (RecurrenceInfoException e) {
            throw new JobSchedulerException(e.getMessage(), e);
        }
        Map jFields = UtilMisc.toMap("jobName", jobName, "serviceName", serviceName, "loaderName", loader,
                "recurrenceInfoId", infoId, "runtimeDataId", dataId);
        GenericValue jobV = null;
        try {
            jobV = delegator.makeValue("JobSandbox", jFields);
            delegator.create(jobV);
        } catch (GenericEntityException e) {
            throw new JobSchedulerException(e.getMessage(), e);
        }
        if (jobV != null) {
            Job job = new PersistedServiceJob(dispatcher.getLocalContext(loader), jobV, null);
            scheduleJob(job);
        }
    }
}
