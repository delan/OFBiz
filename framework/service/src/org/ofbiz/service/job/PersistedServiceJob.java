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
import com.ibm.icu.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.calendar.TemporalExpression;
import org.ofbiz.service.calendar.TemporalExpressionWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldMap;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericRequester;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.config.ServiceConfigUtil;
import org.xml.sax.SAXException;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link Job} that is backed by the entity engine. Job data is stored
 * in the JobSandbox entity.
 * <p>When the job is queued, this object "owns" the entity value. Any external changes
 * are ignored except the cancelDateTime field - jobs can be canceled after they are queued.</p>
 */
@SuppressWarnings("serial")
public class PersistedServiceJob extends GenericServiceJob {

    public static final String module = PersistedServiceJob.class.getName();

    private final transient Delegator delegator;
    private long nextRecurrence = -1;
    private final long maxRetry;
    private final long currentRetryCount;
    private final GenericValue jobValue;

    /**
     * Creates a new PersistedServiceJob
     * @param dctx
     * @param jobValue
     * @param req
     */
    public PersistedServiceJob(DispatchContext dctx, GenericValue jobValue, GenericRequester req) {
        super(dctx, jobValue.getString("jobId"), jobValue.getString("jobName"), null, null, req);
        this.delegator = dctx.getDelegator();
        this.jobValue = jobValue;
        Timestamp storedDate = jobValue.getTimestamp("runTime");
        this.runtime = storedDate.getTime();
        this.maxRetry = jobValue.get("maxRetry") != null ? jobValue.getLong("maxRetry").longValue() : -1;
        Long retryCount = jobValue.getLong("currentRetryCount");
        if (retryCount != null) {
            this.currentRetryCount = retryCount.longValue();
        } else {
            // backward compatibility
            this.currentRetryCount = getRetries(this.delegator);
        }
    }

    @Override
    public void queue() throws InvalidJobException {
        super.queue();
        Timestamp cancelTime = jobValue.getTimestamp("cancelDateTime");
        Timestamp startTime = jobValue.getTimestamp("startDateTime");
        if (cancelTime != null || startTime != null) {
            // job not available
            throw new InvalidJobException("Job [" + getJobId() + "] is not available");
        } else {
            // set the start time to now
            jobValue.set("startDateTime", UtilDateTime.nowTimestamp());
            jobValue.set("statusId", "SERVICE_RUNNING");
            try {
                jobValue.store();
            } catch (GenericEntityException e) {
                throw new InvalidJobException("Unable to set the startDateTime and statusId on the current job [" + getJobId() + "]; not running!", e);
            }
        }
    }

    @Override
    protected void init() throws InvalidJobException {
        super.init();
        try {
            // Job might have been canceled after it was placed in the queue.
            jobValue.refresh();
        } catch (GenericEntityException e) {
            throw new InvalidJobException("Unable to refresh JobSandbox value", e);
        }
        if (jobValue.getTimestamp("cancelDateTime") != null) {
            // Job cancelled
            throw new InvalidJobException("Job [" + getJobId() + "] was cancelled");
        }
        String instanceId = UtilProperties.getPropertyValue("general.properties", "unique.instanceId", "ofbiz0");
        if (!instanceId.equals(jobValue.getString("runByInstanceId"))) {
            // This condition isn't possible, but we will leave it here.
            throw new InvalidJobException("Job has been accepted by a different instance!");
        }
        // configure any additional recurrences
        long maxRecurrenceCount = -1;
        long currentRecurrenceCount = 0;
        TemporalExpression expr = null;
        RecurrenceInfo recurrence = JobManager.getRecurrenceInfo(jobValue);
        if (recurrence != null) {
            Debug.logWarning("Persisted Job [" + getJobId() + "] references a RecurrenceInfo, recommend using TemporalExpression instead", module);
            currentRecurrenceCount = recurrence.getCurrentCount();
            expr = RecurrenceInfo.toTemporalExpression(recurrence);
        }
        if (expr == null && UtilValidate.isNotEmpty(jobValue.getString("tempExprId"))) {
            try {
                expr = TemporalExpressionWorker.getTemporalExpression(this.delegator, jobValue.getString("tempExprId"));
            } catch (GenericEntityException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        if (jobValue.get("maxRecurrenceCount") != null) {
            maxRecurrenceCount = jobValue.getLong("maxRecurrenceCount").longValue();
        }
        if (jobValue.get("currentRecurrenceCount") != null) {
            currentRecurrenceCount = jobValue.getLong("currentRecurrenceCount").longValue();
        }
        if (maxRecurrenceCount != -1) {
            currentRecurrenceCount++;
            jobValue.set("currentRecurrenceCount", currentRecurrenceCount);
        }
        try {
            if (expr != null && (maxRecurrenceCount == -1 || currentRecurrenceCount <= maxRecurrenceCount)) {
                if (recurrence != null) {
                    recurrence.incrementCurrentCount();
                }
                Calendar next = expr.next(Calendar.getInstance());
                if (next != null) {
                    createRecurrence(next.getTimeInMillis(), false);
                }
            }
        } catch (GenericEntityException e) {
            throw new InvalidJobException(e);
        }
        if (Debug.infoOn()) Debug.logInfo("Job  [" + getJobName() + "] Id ["  + getJobId() + "] -- Next runtime: " + new Date(nextRecurrence), module);
    }

    private void createRecurrence(long next, boolean isRetryOnFailure) throws GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("Next runtime returned: " + next, module);
        if (next > runtime) {
            String pJobId = jobValue.getString("parentJobId");
            if (pJobId == null) {
                pJobId = jobValue.getString("jobId");
            }
            GenericValue newJob = GenericValue.create(jobValue);
            newJob.remove("jobId");
            newJob.set("previousJobId", jobValue.getString("jobId"));
            newJob.set("parentJobId", pJobId);
            newJob.set("statusId", "SERVICE_PENDING");
            newJob.set("startDateTime", null);
            newJob.set("runByInstanceId", null);
            newJob.set("runTime", new java.sql.Timestamp(next));
            if (isRetryOnFailure) {
                newJob.set("currentRetryCount", new Long(currentRetryCount + 1));
            } else {
                newJob.set("currentRetryCount", new Long(0));
            }
            nextRecurrence = next;
            delegator.createSetNextSeqId(newJob);
            if (Debug.verboseOn()) Debug.logVerbose("Created next job entry: " + newJob, module);
        }
    }

    @Override
    protected void finish(Map<String, Object> result) throws InvalidJobException {
        super.finish(result);
        // set the finish date
        jobValue.set("statusId", "SERVICE_FINISHED");
        jobValue.set("finishDateTime", UtilDateTime.nowTimestamp());
        String jobResult = null;
        if (ServiceUtil.isError(result)) {
            jobResult = StringUtils.substring(ServiceUtil.getErrorMessage(result), 0, 255);
        } else {
            jobResult = StringUtils.substring(ServiceUtil.makeSuccessMessage(result, "", "", "", ""), 0, 255);
        }
        if (UtilValidate.isNotEmpty(jobResult)) {
            jobValue.set("jobResult", jobResult);
        }
        try {
            jobValue.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update the job [" + getJobId() + "] sandbox", module);
        }
    }

    @Override
    protected void failed(Throwable t) throws InvalidJobException {
        super.failed(t);
        // if the job has not been re-scheduled; we need to re-schedule and run again
        if (nextRecurrence == -1) {
            if (this.canRetry()) {
                // create a recurrence
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, ServiceConfigUtil.getFailedRetryMin());
                long next = cal.getTimeInMillis();
                try {
                    createRecurrence(next, true);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to re-schedule job [" + getJobId() + "]: ", module);
                }
                Debug.logInfo("Persisted Job [" + getJobId() + "] Failed Re-Scheduling : " + next, module);
            } else {
                Debug.logWarning("Persisted Job [" + getJobId() + "] Failed - Max Retry Hit; not re-scheduling", module);
            }
        }
        // set the failed status
        jobValue.set("statusId", "SERVICE_FAILED");
        jobValue.set("finishDateTime", UtilDateTime.nowTimestamp());
        jobValue.set("jobResult", StringUtils.substring(t.getMessage(), 0, 255));
        try {
            jobValue.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update the JobSandbox entity", module);
        }
    }

    @Override
    protected String getServiceName() throws InvalidJobException {
        if (jobValue == null || jobValue.get("serviceName") == null) {
            return null;
        }
        return jobValue.getString("serviceName");
    }

    @Override
    protected Map<String, Object> getContext() throws InvalidJobException {
        Map<String, Object> context = null;
        try {
            if (!UtilValidate.isEmpty(jobValue.getString("runtimeDataId"))) {
                GenericValue contextObj = jobValue.getRelatedOne("RuntimeData", false);
                if (contextObj != null) {
                    context = UtilGenerics.checkMap(XmlSerializer.deserialize(contextObj.getString("runtimeInfo"), delegator), String.class, Object.class);
                }
            }
            if (context == null) {
                context = FastMap.newInstance();
            }
            // check the runAsUser
            if (!UtilValidate.isEmpty(jobValue.getString("runAsUser"))) {
                context.put("userLogin", ServiceUtil.getUserLogin(dctx, context, jobValue.getString("runAsUser")));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "PersistedServiceJob.getContext(): Entity Exception", module);
        } catch (SerializeException e) {
            Debug.logError(e, "PersistedServiceJob.getContext(): Serialize Exception", module);
        } catch (ParserConfigurationException e) {
            Debug.logError(e, "PersistedServiceJob.getContext(): Parse Exception", module);
        } catch (SAXException e) {
            Debug.logError(e, "PersistedServiceJob.getContext(): SAXException", module);
        } catch (IOException e) {
            Debug.logError(e, "PersistedServiceJob.getContext(): IOException", module);
        }
        if (context == null) {
            Debug.logError("Job context is null", module);
        }
        return context;
    }

    // returns the number of current retries
    private long getRetries(Delegator delegator) {
        String pJobId = jobValue.getString("parentJobId");
        if (pJobId == null) {
            return 0;
        }
        long count = 0;
        try {
            EntityFieldMap ecl = EntityCondition.makeConditionMap("parentJobId", pJobId, "statusId", "SERVICE_FAILED");
            count = delegator.findCountByCondition("JobSandbox", ecl, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Exception thrown while counting retries: ", module);
        }
        return count + 1; // add one for the parent
    }

    private boolean canRetry() {
        if (maxRetry == -1) {
            return true;
        }
        return currentRetryCount < maxRetry;
    }
}
