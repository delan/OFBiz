/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.service.job;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;

import org.ofbiz.base.util.Assert;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.calendar.RecurrenceInfoException;
import org.ofbiz.service.config.ServiceConfigUtil;

import com.ibm.icu.util.Calendar;

/**
 * Job manager. The job manager queues jobs. It contains a <code>JobPoller</code> and a
 * <code>Delegator</code>. Client code can queue a job to be run immediately by calling the
 * {@link #runJob(Job)} method, or schedule a job to be run later by calling the
 * {@link #schedule(String, String, String, Map, long, int, int, int, long, int)} method.
 * Scheduled jobs are persisted in the JobSandbox entity.
 */
public final class JobManager {

    public static final String module = JobManager.class.getName();
    public static final String instanceId = UtilProperties.getPropertyValue("general.properties", "unique.instanceId", "ofbiz0");
    private static final ConcurrentHashMap<String, JobManager> registeredManagers = new ConcurrentHashMap<String, JobManager>();
    private static boolean isShutDown = false;

    private static void assertIsRunning() {
        if (isShutDown) {
            throw new IllegalStateException("OFBiz shutting down");
        }
    }

    /**
     * Returns a <code>JobManager</code> instance.
     * @param delegator
     * @param enablePoller Enables polling of the JobSandbox entity.
     * @throws IllegalStateException if the Job Manager is shut down.
     */
    public static JobManager getInstance(Delegator delegator, boolean enablePoller) {
        assertIsRunning();
        Assert.notNull("delegator", delegator);
        JobManager jm = JobManager.registeredManagers.get(delegator.getDelegatorName());
        if (jm == null) {
            jm = new JobManager(delegator);
            JobManager.registeredManagers.putIfAbsent(delegator.getDelegatorName(), jm);
            jm = JobManager.registeredManagers.get(delegator.getDelegatorName());
            if (enablePoller) {
                jm.enablePoller();
            }
        }
        return jm;
    }

    /**
     * Shuts down all job managers. This method is called when OFBiz shuts down.
     */
    public static void shutDown() {
        isShutDown = true;
        for (JobManager jm : registeredManagers.values()) {
            jm.shutdown();
        }
    }

    private final Delegator delegator;
    private final JobPoller jp;
    private boolean pollerEnabled = false;

    private JobManager(Delegator delegator) {
        this.delegator = delegator;
        jp = new JobPoller(this);
    }

    private synchronized void enablePoller() {
        if (!pollerEnabled) {
            pollerEnabled = true;
            reloadCrashedJobs();
            jp.enable();
        }
    }

    /** Returns the Delegator. */
    public Delegator getDelegator() {
        return this.delegator;
    }

    /** Returns the LocalDispatcher. */
    public LocalDispatcher getDispatcher() {
        LocalDispatcher thisDispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
        return thisDispatcher;
    }

    /**
     * Get a List of each threads current state.
     * 
     * @return List containing a Map of each thread's state.
     */
    public Map<String, Object> getPoolState() {
        return jp.getPoolState();
    }

    /**
     * Scans the JobSandbox entity and returns a list of jobs that are due to run.
     * Returns an empty list if there are no jobs due to run.
     * This method is called by the {@link JobPoller} polling thread.
     */
    protected List<Job> poll(int limit) {
        assertIsRunning();
        // The rest of this method logs exceptions and does not throw them.
        // The idea is to keep the JobPoller working even when a database
        // connection is not available (possible on a saturated server).
        List<Job> poll = new ArrayList<Job>(limit);
        DispatchContext dctx = getDispatcher().getDispatchContext();
        if (dctx == null) {
            Debug.logWarning("Unable to locate DispatchContext object; not running job!", module);
            return poll;
        }
        // basic query
        List<EntityExpr> expressions = UtilMisc.toList(EntityCondition.makeCondition("runTime", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
                EntityCondition.makeCondition("startDateTime", EntityOperator.EQUALS, null),
                EntityCondition.makeCondition("cancelDateTime", EntityOperator.EQUALS, null),
                EntityCondition.makeCondition("runByInstanceId", EntityOperator.EQUALS, null));
        // limit to just defined pools
        List<String> pools = ServiceConfigUtil.getRunPools();
        List<EntityExpr> poolsExpr = UtilMisc.toList(EntityCondition.makeCondition("poolId", EntityOperator.EQUALS, null));
        if (pools != null) {
            for (String poolName : pools) {
                poolsExpr.add(EntityCondition.makeCondition("poolId", EntityOperator.EQUALS, poolName));
            }
        }
        // make the conditions
        EntityCondition baseCondition = EntityCondition.makeCondition(expressions);
        EntityCondition poolCondition = EntityCondition.makeCondition(poolsExpr, EntityOperator.OR);
        EntityCondition mainCondition = EntityCondition.makeCondition(UtilMisc.toList(baseCondition, poolCondition));
        EntityListIterator jobsIterator = null;
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
            if (!beganTransaction) {
                Debug.logWarning("Unable to poll JobSandbox for jobs; transaction was not started by this process", module);
                return poll;
            }
            jobsIterator = delegator.find("JobSandbox", mainCondition, null, null, UtilMisc.toList("runTime"), null);
            GenericValue jobValue = jobsIterator.next();
            while (jobValue != null) {
                // Claim ownership of this value. Using storeByCondition to avoid a race condition.
                List<EntityExpr> updateExpression = UtilMisc.toList(EntityCondition.makeCondition("jobId", EntityOperator.EQUALS, jobValue.get("jobId")), EntityCondition.makeCondition("runByInstanceId", EntityOperator.EQUALS, null));
                int rowsUpdated = delegator.storeByCondition("JobSandbox", UtilMisc.toMap("runByInstanceId", instanceId), EntityCondition.makeCondition(updateExpression));
                if (rowsUpdated == 1) {
                    poll.add(new PersistedServiceJob(dctx, jobValue, null));
                    if (poll.size() == limit) {
                        break;
                    }
                }
                jobValue = jobsIterator.next();
            }
        } catch (Throwable t) {
            poll.clear();
            String errMsg =  "Exception thrown while polling JobSandbox: ";
            Debug.logWarning(t, errMsg, module);
            try {
                TransactionUtil.rollback(beganTransaction, errMsg + t.getMessage(), t);
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Exception thrown while rolling back transaction: ", module);
            }
        } finally {
            if (jobsIterator != null) {
                try {
                    jobsIterator.close();
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                Debug.logWarning(e, "Transaction error trying to commit when polling and updating the JobSandbox: ", module);
            }
        }
        if (poll.isEmpty()) {
            // No jobs to run, see if there are any jobs to purge
            int daysToKeep = ServiceConfigUtil.getPurgeJobDays();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -daysToKeep);
            Timestamp purgeTime = new Timestamp(cal.getTimeInMillis());
            List<EntityExpr> finExp = UtilMisc.toList(EntityCondition.makeCondition("finishDateTime", EntityOperator.NOT_EQUAL, null), EntityCondition.makeCondition("finishDateTime", EntityOperator.LESS_THAN, purgeTime));
            List<EntityExpr> canExp = UtilMisc.toList(EntityCondition.makeCondition("cancelDateTime", EntityOperator.NOT_EQUAL, null), EntityCondition.makeCondition("cancelDateTime", EntityOperator.LESS_THAN, purgeTime));
            EntityCondition doneCond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(canExp), EntityCondition.makeCondition(finExp)), EntityOperator.OR);
            mainCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("runByInstanceId", instanceId), doneCond));
            beganTransaction = false;
            try {
                beganTransaction = TransactionUtil.begin();
                if (!beganTransaction) {
                    Debug.logWarning("Unable to poll JobSandbox for jobs; transaction was not started by this process", module);
                    return poll;
                }
                jobsIterator = delegator.find("JobSandbox", mainCondition, null, null, UtilMisc.toList("jobId"), null);
                GenericValue jobValue = jobsIterator.next();
                while (jobValue != null) {
                    poll.add(new PurgeJob(jobValue));
                    if (poll.size() == limit) {
                        break;
                    }
                    jobValue = jobsIterator.next();
                }
            } catch (Throwable t) {
                poll.clear();
                String errMsg =  "Exception thrown while polling JobSandbox: ";
                Debug.logWarning(t, errMsg, module);
                try {
                    TransactionUtil.rollback(beganTransaction, errMsg + t.getMessage(), t);
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "Exception thrown while rolling back transaction: ", module);
                }
            } finally {
                if (jobsIterator != null) {
                    try {
                        jobsIterator.close();
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e, module);
                    }
                }
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException e) {
                    Debug.logWarning(e, "Transaction error trying to commit when polling the JobSandbox: ", module);
                }
            }
        }
        return poll;
    }

    private void reloadCrashedJobs() {
        List<GenericValue> crashed = null;
        List<EntityExpr> statusExprList = UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_PENDING"),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_QUEUED"),
                EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SERVICE_RUNNING"));
        EntityCondition statusCondition = EntityCondition.makeCondition(statusExprList, EntityOperator.OR);
        EntityCondition mainCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("runByInstanceId", instanceId), statusCondition));
        try {
            crashed = delegator.findList("JobSandbox", mainCondition, null, UtilMisc.toList("startDateTime"), null, false);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Unable to load crashed jobs", module);
        }
        if (UtilValidate.isNotEmpty(crashed)) {
            int rescheduled = 0;
            Timestamp now = UtilDateTime.nowTimestamp();
            for (GenericValue job : crashed) {
                try {
                    Debug.logInfo("Scheduling Job : " + job, module);
                    String pJobId = job.getString("parentJobId");
                    if (pJobId == null) {
                        pJobId = job.getString("jobId");
                    }
                    GenericValue newJob = GenericValue.create(job);
                    newJob.set("statusId", "SERVICE_PENDING");
                    newJob.set("runTime", now);
                    newJob.set("previousJobId", job.getString("jobId"));
                    newJob.set("parentJobId", pJobId);
                    newJob.set("startDateTime", null);
                    newJob.set("runByInstanceId", null);
                    delegator.createSetNextSeqId(newJob);
                    // set the cancel time on the old job to the same as the re-schedule time
                    job.set("statusId", "SERVICE_CRASHED");
                    job.set("cancelDateTime", now);
                    delegator.store(job);
                    rescheduled++;
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            }
            if (Debug.infoOn())
                Debug.logInfo("-- " + rescheduled + " jobs re-scheduled", module);
        } else {
            if (Debug.infoOn())
                Debug.logInfo("No crashed jobs to re-schedule", module);
        }
    }

    /** Queues a Job to run now.
     * @throws IllegalStateException if the Job Manager is shut down.
     * @throws RejectedExecutionException if the poller is stopped.
     */
    public void runJob(Job job) throws JobManagerException {
        assertIsRunning();
        if (job.isValid()) {
            jp.queueNow(job);
        }
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param serviceName
     *            The name of the service to invoke
     *@param context
     *            The context for the service
     *@param startTime
     *            The time in milliseconds the service should run
     *@param frequency
     *            The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval
     *            The interval of the frequency recurrence
     *@param count
     *            The number of times to repeat
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count) throws JobManagerException {
        schedule(serviceName, context, startTime, frequency, interval, count, 0);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param serviceName
     *            The name of the service to invoke
     *@param context
     *            The context for the service
     *@param startTime
     *            The time in milliseconds the service should run
     *@param frequency
     *            The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval
     *            The interval of the frequency recurrence
     *@param count
     *            The number of times to repeat
     *@param endTime
     *            The time in milliseconds the service should expire
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count, long endTime) throws JobManagerException {
        schedule(null, serviceName, context, startTime, frequency, interval, count, endTime);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param serviceName
     *            The name of the service to invoke
     *@param context
     *            The context for the service
     *@param startTime
     *            The time in milliseconds the service should run
     *@param frequency
     *            The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval
     *            The interval of the frequency recurrence
     *@param endTime
     *            The time in milliseconds the service should expire
     */
    public void schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, long endTime) throws JobManagerException {
        schedule(serviceName, context, startTime, frequency, interval, -1, endTime);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param poolName
     *            The name of the pool to run the service from
     *@param serviceName
     *            The name of the service to invoke
     *@param context
     *            The context for the service
     *@param startTime
     *            The time in milliseconds the service should run
     *@param frequency
     *            The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval
     *            The interval of the frequency recurrence
     *@param count
     *            The number of times to repeat
     *@param endTime
     *            The time in milliseconds the service should expire
     */
    public void schedule(String poolName, String serviceName, Map<String, ? extends Object> context, long startTime, int frequency,
            int interval, int count, long endTime) throws JobManagerException {
        schedule(null, null, serviceName, context, startTime, frequency, interval, count, endTime, -1);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param poolName
     *            The name of the pool to run the service from
     *@param serviceName
     *            The name of the service to invoke
     *@param dataId
     *            The persisted context (RuntimeData.runtimeDataId)
     *@param startTime
     *            The time in milliseconds the service should run
     */
    public void schedule(String poolName, String serviceName, String dataId, long startTime) throws JobManagerException {
        schedule(null, poolName, serviceName, dataId, startTime, -1, 0, 1, 0, -1);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param jobName
     *            The name of the job
     *@param poolName
     *            The name of the pool to run the service from
     *@param serviceName
     *            The name of the service to invoke
     *@param context
     *            The context for the service
     *@param startTime
     *            The time in milliseconds the service should run
     *@param frequency
     *            The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval
     *            The interval of the frequency recurrence
     *@param count
     *            The number of times to repeat
     *@param endTime
     *            The time in milliseconds the service should expire
     *@param maxRetry
     *            The max number of retries on failure (-1 for no max)
     */
    public void schedule(String jobName, String poolName, String serviceName, Map<String, ? extends Object> context, long startTime,
            int frequency, int interval, int count, long endTime, int maxRetry) throws JobManagerException {
        // persist the context
        String dataId = null;
        try {
            GenericValue runtimeData = delegator.makeValue("RuntimeData");
            runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
            runtimeData = delegator.createSetNextSeqId(runtimeData);
            dataId = runtimeData.getString("runtimeDataId");
        } catch (GenericEntityException ee) {
            throw new JobManagerException(ee.getMessage(), ee);
        } catch (SerializeException se) {
            throw new JobManagerException(se.getMessage(), se);
        } catch (IOException ioe) {
            throw new JobManagerException(ioe.getMessage(), ioe);
        }
        // schedule the job
        schedule(jobName, poolName, serviceName, dataId, startTime, frequency, interval, count, endTime, maxRetry);
    }

    /**
     * Schedule a job to start at a specific time with specific recurrence info
     * 
     * @param jobName
     *            The name of the job
     *@param poolName
     *            The name of the pool to run the service from
     *@param serviceName
     *            The name of the service to invoke
     *@param dataId
     *            The persisted context (RuntimeData.runtimeDataId)
     *@param startTime
     *            The time in milliseconds the service should run
     *@param frequency
     *            The frequency of the recurrence (HOURLY,DAILY,MONTHLY,etc)
     *@param interval
     *            The interval of the frequency recurrence
     *@param count
     *            The number of times to repeat
     *@param endTime
     *            The time in milliseconds the service should expire
     *@param maxRetry
     *            The max number of retries on failure (-1 for no max)
     * @throws IllegalStateException if the Job Manager is shut down.
     */
    public void schedule(String jobName, String poolName, String serviceName, String dataId, long startTime, int frequency, int interval,
            int count, long endTime, int maxRetry) throws JobManagerException {
        assertIsRunning();
        // create the recurrence
        String infoId = null;
        if (frequency > -1 && count != 0) {
            try {
                RecurrenceInfo info = RecurrenceInfo.makeInfo(delegator, startTime, frequency, interval, count);
                infoId = info.primaryKey();
            } catch (RecurrenceInfoException e) {
                throw new JobManagerException(e.getMessage(), e);
            }
        }
        // set the persisted fields
        if (UtilValidate.isEmpty(jobName)) {
            jobName = Long.toString((new Date().getTime()));
        }
        Map<String, Object> jFields = UtilMisc.<String, Object> toMap("jobName", jobName, "runTime", new java.sql.Timestamp(startTime),
                "serviceName", serviceName, "statusId", "SERVICE_PENDING", "recurrenceInfoId", infoId, "runtimeDataId", dataId);
        // set the pool ID
        if (UtilValidate.isNotEmpty(poolName)) {
            jFields.put("poolId", poolName);
        } else {
            jFields.put("poolId", ServiceConfigUtil.getSendPool());
        }
        // set the loader name
        jFields.put("loaderName", delegator.getDelegatorName());
        // set the max retry
        jFields.put("maxRetry", Long.valueOf(maxRetry));
        jFields.put("currentRetryCount", new Long(0));
        // create the value and store
        GenericValue jobV;
        try {
            jobV = delegator.makeValue("JobSandbox", jFields);
            delegator.createSetNextSeqId(jobV);
        } catch (GenericEntityException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    /** Close out the scheduler thread. */
    public void shutdown() {
        Debug.logInfo("Stopping the JobManager...", module);
        registeredManagers.remove(delegator.getDelegatorName(), this);
        jp.stop();
        Debug.logInfo("JobManager stopped.", module);
    }

}
