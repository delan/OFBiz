/*
 * $Id: PersistedServiceJob.java,v 1.4 2003/11/05 22:41:55 ajzeneski Exp $
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericRequester;
import org.ofbiz.service.config.ServiceConfigUtil;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.calendar.RecurrenceInfoException;
import org.xml.sax.SAXException;

/**
 * Entity Service Job - Store => Schedule => Run
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision: 1.4 $
 * @since      2.0
 */
public class PersistedServiceJob extends GenericServiceJob {

    public static final String module = PersistedServiceJob.class.getName();
        
    private transient GenericDelegator delegator = null;
    private Timestamp storedDate = null;
    private long nextRecurrence = -1;

    /**
     * Creates a new PersistedServiceJob
     * @param dctx 
     * @param jobValue
     * @param req 
     */
    public PersistedServiceJob(DispatchContext dctx, GenericValue jobValue, GenericRequester req) {
        super(jobValue.getString("jobName"));
        this.delegator = dctx.getDelegator();
        this.requester = req;
        this.dctx = dctx;
        this.storedDate = jobValue.getTimestamp("runTime");
        this.runtime = storedDate.getTime();
        
        // set the start time to now
        jobValue.set("startDateTime", UtilDateTime.nowTimestamp());
        try {
            jobValue.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to set the startDateTime on the current job; not running!");
            runtime = -1;            
        }        
    }

    /**
     * @see org.ofbiz.service.job.GenericServiceJob#init()
     */
    protected void init() {
        super.init();

        // configure any addition recurrences
        GenericValue job = getJob();
        RecurrenceInfo recurrence = getRecurrence();

        try {
            if (recurrence != null) {
                recurrence.incrementCurrentCount();
                long next = recurrence.next();
                createRecurrence(job, next);
            }
        } catch (GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (Debug.verboseOn()) Debug.logVerbose(this.toString() + " -- Next runtime: " + runtime, module);
    }

    private void createRecurrence(GenericValue job, long next) throws GenericEntityException {
        if (Debug.verboseOn()) Debug.logVerbose("Next runtime returned: " + next, module);
        GenericValue newJob = new GenericValue(job);
        if (next > runtime) {
            newJob.set("startDateTime", null);
            newJob.set("runTime", new java.sql.Timestamp(next));
            nextRecurrence = next;
            delegator.create(newJob);
            if (Debug.verboseOn()) Debug.logVerbose("Created next job entry: " + newJob, module);
        }
    }

    /**
     * @see org.ofbiz.service.job.GenericServiceJob#finish()
     */
    protected void finish() {
        super.finish();

        // set the finish date
        GenericValue job = getJob();
        job.set("finishDateTime", UtilDateTime.nowTimestamp());
        try {
            job.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update the datasource", module);
        }
    }

    /**
     * @see org.ofbiz.service.job.GenericServiceJob#failed(Exception)
     */
    protected void failed(Exception e) {
        super.failed(e);

        // if the job has not been re-scheduled; we need to re-schedule and run again
        if (nextRecurrence == -1) {
            // create a recurrence
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, ServiceConfigUtil.getFailedRetryMin());
            long next = cal.getTimeInMillis();
            GenericValue job = getJob();
            try {
                createRecurrence(job, next);
            } catch (GenericEntityException gee) {
                Debug.logError(gee, "ERROR: Unable to re-schedule job to re-run : " + job, module);
            }
        }
    }

    /**
     * @see org.ofbiz.service.job.GenericServiceJob#getServiceName()
     */
    protected String getServiceName() {
        GenericValue jobObj = getJob();
        if (jobObj == null || jobObj.get("serviceName") == null)
            return null;
        return jobObj.getString("serviceName");
    }

    /**
     * @see org.ofbiz.service.job.GenericServiceJob#getContext()
     */
    protected Map getContext() {
        Map context = null;
        try {
            GenericValue jobObj = getJob();
            GenericValue contextObj = jobObj.getRelatedOne("RuntimeData");

            if (contextObj != null) {
                context = (Map) XmlSerializer.deserialize(contextObj.getString("runtimeInfo"), delegator);
            }
            
            if (context == null) {
                context = new HashMap();
            }
            
            // check the runAsUser
            GenericValue runAsUser = jobObj.getRelatedOne("RunAsUserLogin");
            if (runAsUser != null) {
                context.put("userLogin", runAsUser);
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
        if (context == null)
            Debug.logError("Job context is null", module);
        return context;
    }

    // gets the job value object
    private GenericValue getJob() {
        try {
            Map fields = UtilMisc.toMap("jobName", getJobName(), "runTime", storedDate);
            GenericValue jobObj = delegator.findByPrimaryKey("JobSandbox", fields);

            if (jobObj == null)
                Debug.logError("Job came back null from datasource", module);
            return jobObj;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get job definition from entity", module);
            e.printStackTrace();
        }
        return null;
    }

    // gets the recurrence info value object
    private RecurrenceInfo getRecurrence() {
        try {
            GenericValue job = getJob();

            if (job != null) {
                GenericValue ri = job.getRelatedOne("RecurrenceInfo");

                if (ri != null)
                    return new RecurrenceInfo(ri);
                else
                    return null;
            } else {
                return null;
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            Debug.logError(e, "Problem getting RecurrenceInfo entity from JobSandbox", module);
        } catch (RecurrenceInfoException re) {
            re.printStackTrace();
            Debug.logError(re, "Problem creating RecurrenceInfo instance: " + re.getMessage(), module);
        }
        return null;
    }
}
