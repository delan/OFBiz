/*
 * $Id: GenericAsyncEngine.java,v 1.1 2003/08/17 05:12:39 ajzeneski Exp $
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
package org.ofbiz.service.engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.transaction.TransactionFactory;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericRequester;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.job.GenericServiceJob;
import org.ofbiz.service.job.Job;
import org.ofbiz.service.job.JobManagerException;
import org.ofbiz.service.job.PersistedServiceJob;

/**
 * Generic Asynchronous Engine
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      2.0
 */
public abstract class GenericAsyncEngine implements GenericEngine {
    
    public static final String module = GenericAsyncEngine.class.getName();

    protected ServiceDispatcher dispatcher = null;
    
    protected GenericAsyncEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;       
    }

    /**
     * @see org.ofbiz.service.engine.GenericEngine#runSync(java.lang.String, org.ofbiz.service.ModelService, java.util.Map)
     */
    public abstract Map runSync(String localName, ModelService modelService, Map context) throws GenericServiceException;
    
    /**
     * @see org.ofbiz.service.engine.GenericEngine#runSyncIgnore(java.lang.String, org.ofbiz.service.ModelService, java.util.Map)
     */
    public abstract void runSyncIgnore(String localName, ModelService modelService, Map context) throws GenericServiceException;

    /**
     * @see org.ofbiz.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.service.ModelService, java.util.Map, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        runAsync(localName, modelService, context, null, persist);
    }
    
    /**
     * @see org.ofbiz.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.service.ModelService, java.util.Map, org.ofbiz.service.GenericRequester, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, GenericRequester requester, boolean persist)
        throws GenericServiceException {

        DispatchContext dctx = dispatcher.getLocalContext(localName);
        Job job = null;

        if (persist) {
            // check for a delegator
            if (dispatcher.getDelegator() == null)
                throw new GenericServiceException("No reference to delegator; cannot run persisted services.");
                
            // suspend the current transaction
            TransactionManager tm = TransactionFactory.getTransactionManager();

            if (tm == null)
                throw new GenericServiceException("Cannot get the transaction manager; cannot run persisted services.");

            Transaction parentTrans = null;

            try {
                parentTrans = tm.suspend();
            } catch (SystemException se) {
                Debug.logError(se, "Cannot suspend transaction: " + se.getMessage(), module);
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

