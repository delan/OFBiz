/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.scheduler.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Asynchronous Engine
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@created    November 2, 2001
 *@version    1.0
 */
public abstract class GenericAsyncEngine implements GenericEngine {
    
    protected ServiceDispatcher dispatcher;
    
    /** Creates new GenericAsyncEngine */
    public GenericAsyncEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    /** Run the service synchronously and return the result
     *@param context Map of name, value pairs composing the context
     *@return Map of name, value pairs composing the result
     */
    public abstract Map runSync(ModelService modelService, Map context) throws GenericServiceException;
    
    /** Run the service synchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public abstract void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException;
    
    /** Run the service asynchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runAsync(ModelService modelService, Map context) throws GenericServiceException {
        runAsync(modelService, context, null);
    }
    
    /** Run the service asynchronously, passing an instance of GenericRequester that will receive the result
     *@param context Map of name, value pairs composing the context
     *@param requester Object implementing GenericRequester interface which will receive the result
     */
    public void runAsync(ModelService modelService, Map context, GenericRequester requester) throws GenericServiceException {
        GenericValue job = null;
        try {
            job = createScheduledJob(null,modelService.name);
        }
        catch ( GenericEntityException e ) {
            throw new GenericServiceException("Cannot begin asynchronous service.",e);
        }
        scheduleJob(job,context);
        if ( requester != null ) {
            // implement the requester.
        }
    }
    
    // Private method to add a new scheduled Job.
    private void scheduleJob(GenericValue job, Map context) throws GenericServiceException {
        try {
            dispatcher.getJobManager().addJob(job,context);
        }
        catch ( JobSchedulerException e ) {
            throw new GenericServiceException("Cannot create Job.",e);
        }
    }
    
    // Private Methods to create a GenericValue object for a Job.
    
    private GenericValue createScheduledJob(String jobName, String service) throws GenericEntityException {
        return createScheduledJob(jobName,service,null,null,null,null,null,null);
    }
    
    private GenericValue createScheduledJob(String jobName, String service, Date startDate) throws GenericEntityException {
        return createScheduledJob(jobName,service,startDate,null,null,null,null,null);
    }
    
    private GenericValue createScheduledJob(String jobName, String service, Date startDate, Date endDate) throws GenericEntityException {
        return createScheduledJob(jobName,service,startDate,endDate,null,null,null,null);
    }
         
    private GenericValue createScheduledJob(String jobName, String serviceName, Date startDate, Date endDate,
    String exceptions, String exceptionRule, String recurrences, String recurrenceRule) throws GenericEntityException {
        
        // Make create a name if null.
        if ( jobName == null )
            jobName = new String(new Long(System.currentTimeMillis()).toString());
        
        // Get a sequence ID for the RecurrenceInfo entity.
        String recurrenceId = dispatcher.getDelegator().getNextSeqId("RecurrenceInfo").toString();
        
        // Make the RecurrenceInfo field mapping.
        Map rFields = new HashMap();
        rFields.put("recurrenceInfoId",recurrenceId);
        rFields.put("startDateTime",startDate);
        rFields.put("endDateTime",endDate);
        rFields.put("exceptionDateTimes",exceptions);
        rFields.put("exceptionRule",exceptionRule);
        rFields.put("recurrenceDateTimes",recurrences);
        rFields.put("recurrenceRule",recurrenceRule);
        
        // Make the JobSandbox field mapping
        Map jFields = UtilMisc.toMap("jobName",jobName,"serviceName",serviceName,"recurrenceInfoId",recurrenceId);
        
        // Create the value objects.
        GenericValue job = dispatcher.getDelegator().makeValue("OrderHeader", jFields);
        GenericValue rule = dispatcher.getDelegator().makeValue("RecurrenceInfo", rFields);
        job.preStoreOther(rule);
        dispatcher.getDelegator().create(job);
        return job;
    }
    
}
