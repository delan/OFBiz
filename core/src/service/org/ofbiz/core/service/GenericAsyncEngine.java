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

import javax.transaction.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.util.*;

/**
 * Generic Asynchronous Engine
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 2, 2001
 *@version    1.0
 */
public abstract class GenericAsyncEngine implements GenericEngine {

    protected ServiceDispatcher dispatcher;
    protected String loader;

    public GenericAsyncEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.loader = null;
    }

    /**
     * Set the name of the local dispatcher.
     * @param loader name of the local dispatcher.
     */
    public void setLoader(String loader) {
        this.loader = loader;
    }

    /**
     * Run the service synchronously and return the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws GenericServiceException
     */
    public abstract Map runSync(ModelService modelService, Map context) throws GenericServiceException;

    /**
     * Run the service synchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public abstract void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException;

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        runAsync(modelService, context, null, persist);
    }

    /**
     * Run the service asynchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(ModelService modelService, Map context, GenericRequester requester, boolean persist)
            throws GenericServiceException {

        DispatchContext dctx = dispatcher.getLocalContext(loader);
        Job job = null;

        if (persist) {
            TransactionManager tm = TransactionFactory.getTransactionManager();
            if (tm == null)
                throw new GenericServiceException("Cannot get the transaction manager; cannot run persisted services.");

            Transaction parentTrans = null;
            try {
                tm.suspend();
            } catch (SystemException se) {
                Debug.logError(se, "Cannot suspend transaction: " + se.getMessage());
            }

            String exceptionMessage = "Cannot begin asynchronous service.";
            GenericValue jobV = null;

            // Build the value object(s).
            try {
                Collection toBeStored = new LinkedList();

                // Create the runtime data
                String dataId = dispatcher.getDelegator().getNextSeqId("RuntimeData").toString();

                GenericValue runtimeData = dispatcher.getDelegator().makeValue("RuntimeData",
                        UtilMisc.toMap("runtimeDataId", dataId));
                runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
                toBeStored.add(runtimeData);

                // Create the job info
                String jobName = new String(new Long((new Date().getTime())).toString());
                Map jFields = UtilMisc.toMap("jobName", jobName, "serviceName", modelService.name, "loaderName", loader,
                        "runtimeDataId", dataId);
                jobV = dispatcher.getDelegator().makeValue("JobSandbox", jFields);
                toBeStored.add(jobV);
                dispatcher.getDelegator().storeAll(toBeStored);

            } catch (GenericEntityException e) {
                resumeTransaction(tm, parentTrans);
                throw new GenericServiceException(exceptionMessage, e);
            } catch (SerializeException e) {
                resumeTransaction(tm, parentTrans);
                throw new GenericServiceException(exceptionMessage, e);
            } catch (FileNotFoundException e) {
                resumeTransaction(tm, parentTrans);
                throw new GenericServiceException(exceptionMessage, e);
            } catch (IOException e) {
                resumeTransaction(tm, parentTrans);
                throw new GenericServiceException(exceptionMessage, e);
            }

            resumeTransaction(tm, parentTrans);

            if (jobV == null)
                throw new GenericServiceException("Problems creating job.");
            job = new PersistedServiceJob(dctx, jobV, requester);
        } else {
            String name = new Long(new Date().getTime()).toString();
            job = new GenericServiceJob(dctx, name, modelService.name, context, requester);
        }

        // Schedule the job.
        try {
            dispatcher.getJobManager().runJob(job);
        } catch (JobSchedulerException jse) {
            throw new GenericServiceException("Cannot run job.", jse);
        }
    }

    private void resumeTransaction(TransactionManager tm, Transaction trans) throws GenericServiceException {
        if (trans == null)
            return;
        try {
            tm.resume(trans);
        } catch (InvalidTransactionException ite) {
            throw new GenericServiceException("Cannot resume transaction", ite);
        } catch (SystemException se) {
            throw new GenericServiceException("Unexpected transaction error", se);
        }
    }
}


