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
package org.ofbiz.core.service.engine;

import java.io.*;
import java.util.*;

import javax.transaction.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.serialize.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Generic Asynchronous Engine
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public abstract class GenericAsyncEngine implements GenericEngine {

    protected ServiceDispatcher dispatcher;
    
    protected GenericAsyncEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;       
    }

    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runSync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map)
     */
    public abstract Map runSync(String localName, ModelService modelService, Map context) throws GenericServiceException;
    
    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runSyncIgnore(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map)
     */
    public abstract void runSyncIgnore(String localName, ModelService modelService, Map context) throws GenericServiceException;

    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        runAsync(localName, modelService, context, null, persist);
    }
    
    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map, org.ofbiz.core.service.GenericRequester, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, GenericRequester requester, boolean persist)
        throws GenericServiceException {

        DispatchContext dctx = dispatcher.getLocalContext(localName);
        Job job = null;

        if (persist) {
            // suspend the current transaction
            TransactionManager tm = TransactionFactory.getTransactionManager();

            if (tm == null)
                throw new GenericServiceException("Cannot get the transaction manager; cannot run persisted services.");

            Transaction parentTrans = null;

            try {
                parentTrans = tm.suspend();
            } catch (SystemException se) {
                Debug.logError(se, "Cannot suspend transaction: " + se.getMessage());
            }

            String exceptionMessage = "Cannot begin asynchronous service.";
            GenericValue jobV = null;

            // Build the value object(s).
            try {
                List toBeStored = new LinkedList();

                // Create the runtime data
                String dataId = dispatcher.getDelegator().getNextSeqId("RuntimeData").toString();

                GenericValue runtimeData = dispatcher.getDelegator().makeValue("RuntimeData",
                        UtilMisc.toMap("runtimeDataId", dataId));

                runtimeData.set("runtimeInfo", XmlSerializer.serialize(context));
                toBeStored.add(runtimeData);

                // Create the job info
                String jobName = new String(new Long((new Date().getTime())).toString());
                Map jFields = UtilMisc.toMap("jobName", jobName, "runTime", UtilDateTime.nowTimestamp(),
                        "serviceName", modelService.name, "loaderName", localName,
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
        } catch (JobManagerException jse) {
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

