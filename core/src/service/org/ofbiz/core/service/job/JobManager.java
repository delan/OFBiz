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

import java.io.*;
import java.util.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * JobManager
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class JobManager {

    public static final String module = JobManager.class.getName();

    protected GenericDelegator delegator;
    protected ServiceDispatcher dispatcher;
    protected JobPoller jp;

    /** Creates a new JobManager object. */
    public JobManager(ServiceDispatcher dispatcher, GenericDelegator delegator) {
        this.dispatcher = dispatcher;
        this.delegator = delegator;
        jp = new JobPoller(this);
    }

    /** Queues a Job to run now. */
    public void runJob(Job job) throws JobManagerException {
        if (job.isValid())
            jp.queueNow(job);
    }

    /** Returns the ServiceDispatcher. */
    public ServiceDispatcher getDispatcher() {
        return this.dispatcher;
    }

    /** Returns the GenericDelegator. */
    public GenericDelegator getDelegator() {
        return this.delegator;
    }

    public synchronized Iterator poll() {
        List poll = new ArrayList();
        Collection jobEnt = null;
        List order = UtilMisc.toList("runTime");
        List expressions = UtilMisc.toList(new EntityExpr("runTime", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()),
                new EntityExpr("startDateTime", EntityOperator.EQUALS, null));

        try {
            jobEnt = delegator.findByAnd("JobSandbox", expressions, order);
        } catch (GenericEntityException ee) {
            Debug.logError(ee, "Cannot load jobs from datasource.", module);
        } catch (Exception e) {
            Debug.logError(e, "Unknown error.", module);
            e.printStackTrace();
        }
        if (jobEnt != null && jobEnt.size() > 0) {
            Iterator i = jobEnt.iterator();

            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                String loader = v.getString("loaderName");
                DispatchContext dctx = dispatcher.getLocalContext(loader);

                if (dctx == null) {
                    Debug.logWarning("Job (" + v.getString("jobName") + " scheduled to run at " +
                        v.getTimestamp("runTime") + " has an invalid service loader.", module);
                    continue;
                }
                Job job = new PersistedServiceJob(dctx, v, null); // todo fix the requester

                poll.add(job);
            }
        }
        return poll.iterator();
    }

    /** 
     * Schedule a job to start at a specific time with specific recurrence info
     *@param loader The name of the local dispatcher to use
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     */
    public void schedule(String loader, String serviceName, Map context, long startTime,
            int frequency, int interval, int count) throws JobManagerException {
        schedule(loader, serviceName, context, startTime, frequency, interval, count, 0);
    }
    
    /** 
     * Schedule a job to start at a specific time with specific recurrence info
     *@param loader The name of the local dispatcher to use
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param endTime The time in milliseconds the service should expire
     */
    public void schedule(String loader, String serviceName, Map context, long startTime,
            int frequency, int interval, long endTime) throws JobManagerException {
        schedule(loader, serviceName, context, startTime, frequency, interval, -1, endTime);
    }    
        
    /** 
     * Schedule a job to start at a specific time with specific recurrence info
     *@param loader The name of the local dispatcher to use
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     *@param endTime The time in milliseconds the service should expire
     */
    public void schedule(String loader, String serviceName, Map context, long startTime,
            int frequency, int interval, int count, long endTime) throws JobManagerException {
        String dataId = null;
        String infoId = null;
        String jobName = new String(new Long((new Date().getTime())).toString());
        
        if (delegator == null) {
            Debug.logWarning("No delegator referenced; cannot schedule job.", module);
            return;
        }

        try {
            dataId = delegator.getNextSeqId("RuntimeData").toString();
            GenericValue runtimeData = delegator.makeValue("RuntimeData", UtilMisc.toMap("runtimeDataId", dataId));

            runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
            delegator.create(runtimeData);
        } catch (GenericEntityException ee) {
            throw new JobManagerException(ee.getMessage(), ee);
        } catch (SerializeException se) {
            throw new JobManagerException(se.getMessage(), se);
        } catch (IOException ioe) {
            throw new JobManagerException(ioe.getMessage(), ioe);
        }
        try {
            RecurrenceInfo info = RecurrenceInfo.makeInfo(delegator, startTime, frequency, interval, count);

            infoId = info.primaryKey();
        } catch (RecurrenceInfoException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
        Map jFields = UtilMisc.toMap("jobName", jobName, "runTime", new java.sql.Timestamp(startTime),
                "serviceName", serviceName, "loaderName", loader,
                "recurrenceInfoId", infoId, "runtimeDataId", dataId);

        GenericValue jobV = null;

        try {
            jobV = delegator.makeValue("JobSandbox", jFields);
            delegator.create(jobV);
        } catch (GenericEntityException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    /** Close out the scheduler thread. */
    public void finalize() {
        if (jp != null) {
            jp.stop();
            jp = null;
            Debug.logInfo("JobManager: Stopped Scheduler Thread.");
        }
    }

}
