/*
 * $Id: JobManager.java,v 1.8 2003/12/05 21:02:46 ajzeneski Exp $
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.calendar.RecurrenceInfoException;
import org.ofbiz.service.config.ServiceConfigUtil;

/**
 * JobManager
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.8 $
 * @since      2.0
 */
public class JobManager {

    public static final String module = JobManager.class.getName();
    public static final String dispatcherName = "JobDispatcher";

    protected GenericDelegator delegator;    
    protected JobPoller jp;

    /** Creates a new JobManager object. */
    public JobManager(GenericDelegator delegator) {                
        this.delegator = delegator;        
        jp = new JobPoller(this);
    }

    /** Queues a Job to run now. */
    public void runJob(Job job) throws JobManagerException {
        if (job.isValid())
            jp.queueNow(job);
    }

    /** Returns the ServiceDispatcher. */
    public LocalDispatcher getDispatcher() {
        LocalDispatcher thisDispatcher = null;
        try {
            thisDispatcher = GenericDispatcher.getLocalDispatcher(dispatcherName, delegator);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);                     
        }        
        return thisDispatcher;
    }

    /** Returns the GenericDelegator. */
    public GenericDelegator getDelegator() {
        return this.delegator;
    }

    public synchronized Iterator poll() {
        List poll = new ArrayList();
        Collection jobEnt = null;

        // sort the results by time
        List order = UtilMisc.toList("runTime");

        // basic query
        List expressions = UtilMisc.toList(new EntityExpr("runTime", EntityOperator.LESS_THAN_EQUAL_TO,
                UtilDateTime.nowTimestamp()), new EntityExpr("startDateTime", EntityOperator.EQUALS, null),
                new EntityExpr("cancelDateTime", EntityOperator.EQUALS, null));

        // limit to just defined pools
        List pools = ServiceConfigUtil.getRunPools();
        List poolsExpr = UtilMisc.toList(new EntityExpr("poolId", EntityOperator.EQUALS, null));
        if (pools != null) {
            Iterator poolsIter = pools.iterator();
            while (poolsIter.hasNext()) {
                String poolName = (String) poolsIter.next();
                poolsExpr.add(new EntityExpr("poolId", EntityOperator.EQUALS, poolName));
            }
        }

        // make the conditions
        EntityCondition baseCondition = new EntityConditionList(expressions, EntityOperator.AND);
        EntityCondition poolCondition = new EntityConditionList(poolsExpr, EntityOperator.OR);
        EntityCondition mainCondition = new EntityConditionList(UtilMisc.toList(baseCondition, poolCondition), EntityOperator.AND);

        // we will loop until we have no more to do
        boolean pollDone = false;

        while (!pollDone) {
            boolean beganTransaction;
            try {
                beganTransaction = TransactionUtil.begin();
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Unable to start transaction; not polling for jobs", module);
                return null;
            }
            if (!beganTransaction) {
                Debug.logError("Unable to poll for jobs; transaction was not started by this process", module);
                return null;
            }

            try {
                jobEnt = delegator.findByCondition("JobSandbox", mainCondition, null, order);
            } catch (GenericEntityException ee) {
                Debug.logError(ee, "Cannot load jobs from datasource.", module);
            } catch (Exception e) {
                Debug.logError(e, "Unknown error.", module);
            }

            if (jobEnt != null && jobEnt.size() > 0) {
                Iterator i = jobEnt.iterator();

                while (i.hasNext()) {
                    GenericValue v = (GenericValue) i.next();
                    DispatchContext dctx = getDispatcher().getDispatchContext();

                    if (dctx == null) {
                        Debug.logError("Unable to locate DispatchContext object; not running job!", module);
                        continue;
                    }
                    Job job = new PersistedServiceJob(dctx, v, null); // todo fix the requester
                    poll.add(job);
                }
            } else {
                pollDone = true;
            }

            // finished this run; commit the transaction
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                Debug.logError(e, module);
            }

        }
        return poll.iterator();
    }

    /** 
     * Schedule a job to start at a specific time with specific recurrence info
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count) throws JobManagerException {
        schedule(serviceName, context, startTime, frequency, interval, count, 0);
    }
    
    /** 
     * Schedule a job to start at a specific time with specific recurrence info
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param endTime The time in milliseconds the service should expire
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, long endTime) throws JobManagerException {
        schedule(serviceName, context, startTime, frequency, interval, -1, endTime);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     *@param endTime The time in milliseconds the service should expire
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count, long endTime) throws JobManagerException {
        schedule(null, serviceName, context, startTime, frequency, interval, count, endTime);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     *@param poolName The name of the pool to run the service from
     *@param serviceName The name of the service to invoke
     *@param context The context for the service
     *@param startTime The time in milliseconds the service should run
     *@param frequency The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     *@param endTime The time in milliseconds the service should expire
     */
    public void schedule(String poolName, String serviceName, Map context, long startTime, int frequency, int interval, int count, long endTime) throws JobManagerException {
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
                "serviceName", serviceName, "recurrenceInfoId", infoId, "runtimeDataId", dataId);

        // set the pool ID
        if (poolName != null && poolName.length() > 0) {
            jFields.put("poolId", poolName);
        } else {
            jFields.put("poolId", ServiceConfigUtil.getSendPool());
        }

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
            Debug.logInfo("JobManager: Stopped Scheduler Thread.", module);
        }
    }

}
