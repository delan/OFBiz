/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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


package org.ofbiz.core.service;

import java.io.*;
import java.util.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.scheduler.*;
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
    protected String loader;

    /** Creates new GenericAsyncEngine */
    public GenericAsyncEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.loader = null;
    }

    /** Set the name of the local dispatcher
     *@param loader of the local dispatcher
     */
    public void setLoader(String loader) {
        this.loader = loader;
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
    public void runAsync(ModelService modelService, Map context, GenericRequester requester)
            throws GenericServiceException {
        String exceptionMessage = "Cannot begin asynchronous service.";
        GenericValue job = null;

        // Build the value object(s).
        try {
            Collection toBeStored = new LinkedList();

            // Create the recurrence info
            String ruleId = dispatcher.getDelegator().getNextSeqId(
                    "RecurrenceRule").toString();
            GenericValue rule = dispatcher.getDelegator().makeValue("RecurrenceRule",
                                                                    UtilMisc.toMap("recurrenceRuleId", ruleId));
            rule.set("frequency", "DAILY");
            rule.set("intervalNumber", new Long(1));
            rule.set("countNumber", new Long(1));
            toBeStored.add(rule);

            String infoId = dispatcher.getDelegator().getNextSeqId(
                    "RecurrenceInfo").toString();
            GenericValue info = dispatcher.getDelegator().makeValue("RecurrenceInfo",
                                                                    UtilMisc.toMap("recurrenceInfoId", infoId));
            info.set("recurrenceRuleId", ruleId);
            info.set("startDateTime", new java.sql.Timestamp(RecurrenceUtil.now()));
            toBeStored.add(info);

            // Create the runtime data
            String dataId = dispatcher.getDelegator().getNextSeqId(
                    "RuntimeData").toString();
            GenericValue runtimeData =
                    dispatcher.getDelegator().makeValue("RuntimeData",
                                                        UtilMisc.toMap("runtimeDataId", dataId));
            runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
            toBeStored.add(runtimeData);

            // Create the job info
            String jobName = new String(new Long((new Date().getTime())).toString());
            Map jFields = UtilMisc.toMap("jobName", jobName, "serviceName",
                                         modelService.name, "loaderName", loader, "recurrenceInfoId",
                                         infoId, "runtimeDataId", dataId);
            job = dispatcher.getDelegator().makeValue("JobSandbox", jFields);
            toBeStored.add(job);

            dispatcher.getDelegator().storeAll(toBeStored);
        } catch (GenericEntityException e) {
            throw new GenericServiceException(exceptionMessage, e);
        } catch (SerializeException e) {
            throw new GenericServiceException(exceptionMessage, e);
        } catch (FileNotFoundException e) {
            throw new GenericServiceException(exceptionMessage, e);
        } catch (IOException e) {
            throw new GenericServiceException(exceptionMessage, e);
        }

        if (job == null)
            throw new GenericServiceException("Problems creating job.");

        // Schedule the job.
        try {
            dispatcher.getJobManager().addJob(job, context, requester);
        } catch (JobSchedulerException jse) {
            throw new GenericServiceException("Cannot schedule job.", jse);
        }

        // Implement the requester.
        if (requester != null) {
        }
    }
}


