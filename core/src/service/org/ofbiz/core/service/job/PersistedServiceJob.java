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
import java.sql.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Entity Service Job - Store => Schedule => Run
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class PersistedServiceJob extends GenericServiceJob {

    public static final String module = PersistedServiceJob.class.getName();
        
    private transient GenericDelegator delegator = null;
    private Timestamp storedDate = null;

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
    }

    /**
     * @see org.ofbiz.core.service.job.GenericServiceJob#init()
     */
    protected void init() {
        GenericValue job = getJob();
        RecurrenceInfo recurrence = getRecurrence();

        try {
            GenericValue newJob = new GenericValue(job);
            job.set("startDateTime", UtilDateTime.nowTimestamp());
            job.store();
            if (recurrence != null) {
                recurrence.incrementCurrentCount();
                long next = recurrence.next();
                if (Debug.verboseOn()) Debug.logVerbose("Next runtime returned: " + next, module);
                
                if (next > runtime) {
                    newJob.set("runTime", new java.sql.Timestamp(next));
                    delegator.create(newJob);
                    if (Debug.verboseOn()) Debug.logVerbose("Created next job entry: " + newJob, module);
                }
            }
        } catch (GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (Debug.verboseOn()) Debug.logVerbose(this.toString() + " -- Next runtime: " + runtime, module);
    }

    /**
     * @see org.ofbiz.core.service.job.GenericServiceJob#finish()
     */
    protected void finish() {
        GenericValue job = getJob();
        job.set("finishDateTime", UtilDateTime.nowTimestamp());
        try {
            job.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update the datasource");
        }
    }

    /**
     * @see org.ofbiz.core.service.job.GenericServiceJob#getServiceName()
     */
    protected String getServiceName() {
        GenericValue jobObj = getJob();
        if (jobObj == null || jobObj.get("serviceName") == null)
            return null;
        return jobObj.getString("serviceName");
    }

    /**
     * @see org.ofbiz.core.service.job.GenericServiceJob#getContext()
     */
    protected Map getContext() {
        Map context = null;
        try {
            GenericValue jobObj = getJob();
            GenericValue contextObj = jobObj.getRelatedOne("RuntimeData");

            if (contextObj != null)
                context = (Map) XmlSerializer.deserialize(contextObj.getString("runtimeInfo"), delegator);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        } catch (SerializeException e) {
            Debug.logError(e, module);
        } catch (ParserConfigurationException e) {
            Debug.logError(e, module);
        } catch (SAXException e) {
            Debug.logError(e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
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
