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

package org.ofbiz.core.workflow;

import java.util.*;

import javax.transaction.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.workflow.impl.*;

/**
 * WorkflowEngine - Workflow Service Engine
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 16, 2001
 *@version    1.0
 */
public class WorkflowEngine implements GenericEngine {

    public static final String module = WorkflowEngine.class.getName();

    protected ServiceDispatcher dispatcher;
    protected String loader;

    /** Creates new WorkflowEngine */
    public WorkflowEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.loader = null;
    }

    /** Set the name of the local dispatcher
     * @param loader name of the local dispatcher
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
    public Map runSync(ModelService modelService, Map context) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        runAsync(modelService, context, waiter, false);
        return waiter.waitForResult();
    }

    /**
     * Run the service synchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException {
        runAsync(modelService, context, null, false);
    }

    /**
     * Run the service asynchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run. (Ignored)
     * @throws GenericServiceException
     */
    public void runAsync(ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        runAsync(modelService, context, null, persist);
    }

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run. (Ignored)
     * @throws GenericServiceException
     */
    public void runAsync(ModelService modelService, Map context, GenericRequester requester, boolean persist)
            throws GenericServiceException {
        // Suspend the current transaction
        TransactionManager tm = TransactionFactory.getTransactionManager();
        if (tm == null)
            throw new GenericServiceException("Cannot get the transaction manager; cannot run persisted services.");

        Transaction parentTrans = null;
        try {
            parentTrans = tm.suspend();
            Debug.logVerbose("Suspended transaction.", module);
        } catch (SystemException se) {
            Debug.logError(se, "Cannot suspend transaction: " + se.getMessage());
        }
        // Build the requester
        WfRequester req = null;
        try {
            req = WfFactory.getWfRequester();
        } catch (WfException e) {
            throw new GenericServiceException(e.getMessage(), e);
        }

        // Build the process manager
        WfProcessMgr mgr = null;
        try {
            mgr = WfFactory.getWfProcessMgr(dispatcher.getDelegator(), modelService.location, modelService.invoke);
        } catch (WfException e) {
            throw new GenericServiceException(e.getMessage(), e);
        }

        // Create the process
        WfProcess process = null;
        try {
            process = mgr.createProcess(req);
        } catch (NotEnabled ne) {
            throw new GenericServiceException(ne.getMessage(), ne);
        } catch (InvalidRequester ir) {
            throw new GenericServiceException(ir.getMessage(), ir);
        } catch (RequesterRequired rr) {
            throw new GenericServiceException(rr.getMessage(), rr);
        } catch (WfException wfe) {
            throw new GenericServiceException(wfe.getMessage(), wfe);
        }

        // Set the service dispatcher for the workflow
        try {
            process.setServiceLoader(loader);
        } catch (WfException e) {
            throw new GenericServiceException(e.getMessage(), e);
        }

        // Assign the owner of the process
        GenericValue userLogin = null;
        if (context.containsKey("userLogin")) {
            userLogin = (GenericValue) context.remove("userLogin");
            try {
                Map fields = UtilMisc.toMap("partyId", userLogin.getString("partyId"),
                        "roleTypeId", "WF_OWNER", "workEffortId", process.runtimeKey(),
                        "fromDate", UtilDateTime.nowTimestamp());
                try {
                    GenericValue wepa = dispatcher.getDelegator().makeValue("WorkEffortPartyAssignment", fields);
                    dispatcher.getDelegator().create(wepa);
                } catch (GenericEntityException e) {
                    throw new GenericServiceException("Cannot set ownership of workflow");
                }
            } catch (WfException we) {
                throw new GenericServiceException("Cannot get the workflow process runtime key");
            }
        }

        // Register the process and set the workflow owner
        try {
            req.registerProcess(process, context, requester);
            if (userLogin != null) {
                Map pContext = process.processContext();
                pContext.put("workflowOwnerId", userLogin.getString("userLoginId"));
                process.setProcessContext(pContext);
            }
        } catch (WfException wfe) {
            throw new GenericServiceException(wfe.getMessage(), wfe);
        }

        try {
            Job job = new WorkflowRunner(process, requester);
            Debug.logVerbose("Created WorkflowRunner: " + job, module);
            dispatcher.getJobManager().runJob(job);
        } catch (JobManagerException je) {
            throw new GenericServiceException(je.getMessage(), je);
        }

        // Resume the parent transaction
        if (parentTrans != null) {
            try {
                tm.resume(parentTrans);
                Debug.logVerbose("Resumed the parent transaction.", module);
            } catch (InvalidTransactionException ite) {
                throw new GenericServiceException("Cannot resume transaction", ite);
            } catch (SystemException se) {
                throw new GenericServiceException("Unexpected transaction error", se);
            }
        }
    }
}

class WorkflowRunner extends AbstractJob {

    GenericRequester requester;
    WfProcess process;

    WorkflowRunner(WfProcess process, GenericRequester requester) {
        super(process.toString());
        this.process = process;
        this.requester = requester;
        runtime = new Date().getTime();
    }

    protected void finish() {
        runtime = -1;
    }

    public void exec() {
        try {
            process.start();
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e);
            if (requester != null)
                requester.receiveResult(null);
        }
        finish();
    }
}

