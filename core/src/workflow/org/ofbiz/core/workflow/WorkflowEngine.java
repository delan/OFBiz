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
package org.ofbiz.core.workflow;

import java.util.*;
import javax.transaction.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;

/**
 * WorkflowEngine - Workflow Service Engine
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class WorkflowEngine implements GenericEngine {

    public static final String module = WorkflowEngine.class.getName();

    protected ServiceDispatcher dispatcher;
     
    public WorkflowEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;      
    }
       
    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runSync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map)
     */
    public Map runSync(String localName, ModelService modelService, Map context) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        runAsync(localName, modelService, context, waiter, false);
        return waiter.waitForResult();
    }
   
    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runSyncIgnore(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map)
     */
    public void runSyncIgnore(String localName, ModelService modelService, Map context) throws GenericServiceException {
        runAsync(localName, modelService, context, null, false);
    }
   
    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        runAsync(localName, modelService, context, null, persist);
    }
   
    /**
     * @see org.ofbiz.core.service.engine.GenericEngine#runAsync(java.lang.String, org.ofbiz.core.service.ModelService, java.util.Map, org.ofbiz.core.service.GenericRequester, boolean)
     */
    public void runAsync(String localName, ModelService modelService, Map context, GenericRequester requester, boolean persist) throws GenericServiceException {       
        // Suspend the current transaction
        TransactionManager tm = TransactionFactory.getTransactionManager();
        if (tm == null) {
            throw new GenericServiceException("Cannot get the transaction manager; cannot run persisted services.");
        }

        Transaction parentTrans = null;
        boolean beganTransaction = false;
        try {
            try {
                parentTrans = tm.suspend();                
                beganTransaction = TransactionUtil.begin();
                Debug.logInfo("Suspended transaction; began new: " + beganTransaction, module);
            } catch (SystemException se) {
                Debug.logError(se, "Cannot suspend transaction: " + se.getMessage());
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Cannot begin nested transaction: " + e.getMessage());
            }
            
            // Build the requester
            WfRequester req = null;
            try {
                req = WfFactory.getWfRequester();
            } catch (WfException e) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(e.getMessage(), e);
            }

            // Get the package and process ID::VERSION
            String packageId = this.getSplitPosition(modelService.location, 0);
            String packageVersion = this.getSplitPosition(modelService.location, 1);
            String processId = this.getSplitPosition(modelService.invoke, 0);
            String processVersion = this.getSplitPosition(modelService.invoke, 1);

            // Build the process manager
            WfProcessMgr mgr = null;
            try {
                mgr = WfFactory.getWfProcessMgr(dispatcher.getDelegator(), packageId, packageVersion, processId, processVersion);
            } catch (WfException e) {
                Debug.logError(e, "Process manager error", module);
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(e.getMessage(), e);
            } catch (Exception e) {
                Debug.logError(e, "Un-handled process manager error", module);
                throw new GenericServiceException(e.getMessage(), e);
            }

            // Create the process
            WfProcess process = null;
            try {
                process = mgr.createProcess(req);
            } catch (NotEnabled ne) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(ne.getMessage(), ne);
            } catch (InvalidRequester ir) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(ir.getMessage(), ir);
            } catch (RequesterRequired rr) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(rr.getMessage(), rr);
            } catch (WfException wfe) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(wfe.getMessage(), wfe);
            } catch (Exception e) {
                Debug.logError(e, "Un-handled process exception", module);
                throw new GenericServiceException(e.getMessage(), e);
            }

            // Set the service dispatcher for the workflow
            try {
                process.setServiceLoader(localName);
            } catch (WfException e) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
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
                        try {
                            TransactionUtil.rollback(beganTransaction);
                        } catch (GenericTransactionException gte) {
                            Debug.logError(gte, "Unable to rollback nested exception.");
                        }
                        throw new GenericServiceException("Cannot set ownership of workflow", e);
                    }
                } catch (WfException we) {
                    try {
                        TransactionUtil.rollback(beganTransaction);
                    } catch (GenericTransactionException gte) {
                        Debug.logError(gte, "Unable to rollback nested exception.");
                    }
                    throw new GenericServiceException("Cannot get the workflow process runtime key");
                }
            }
        
            // Grab the locale from the context
            Locale locale = (Locale) context.remove("locale");
        
            // Grab the starting activityId from the context
            String startActivityId = (String) context.remove("startWithActivityId");

            // Register the process and set the workflow owner
            try {
                req.registerProcess(process, context, requester);
                if (userLogin != null) {
                    Map pContext = process.processContext();
                    pContext.put("workflowOwnerId", userLogin.getString("userLoginId"));
                    process.setProcessContext(pContext);
                }
            } catch (WfException wfe) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(wfe.getMessage(), wfe);
            }
        
            // Set the initial locale - (in context)
            if (locale != null) {
                try {
                    Map pContext = process.processContext();
                    pContext.put("initialLocale", locale);
                    process.setProcessContext(pContext);
                } catch (WfException wfe) {
                    try {
                        TransactionUtil.rollback(beganTransaction);
                    } catch (GenericTransactionException gte) {
                        Debug.logError(gte, "Unable to rollback nested exception.");
                    }
                    throw new GenericServiceException(wfe.getMessage(), wfe);
                }
            }
        
            // Use the WorkflowRunner to start the workflow in a new thread                        
            try {
                Job job = new WorkflowRunner(process, requester, startActivityId);
                if (Debug.verboseOn()) Debug.logVerbose("Created WorkflowRunner: " + job, module);
                dispatcher.getJobManager().runJob(job);
            } catch (JobManagerException je) {
                try {
                    TransactionUtil.rollback(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback nested exception.");
                }
                throw new GenericServiceException(je.getMessage(), je);
            }
            
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Cannot commit nested transaction: " + e.getMessage());
            }
        } finally {
            // Resume the parent transaction
            if (parentTrans != null) {
                try {
                    tm.resume(parentTrans);
                    Debug.logInfo("Resumed the parent transaction.", module);
                } catch (InvalidTransactionException ite) {
                    throw new GenericServiceException("Cannot resume transaction", ite);
                } catch (SystemException se) {
                    throw new GenericServiceException("Unexpected transaction error", se);
                }
            }
        }
    }

    private String getSplitPosition(String splitString, int position) {
        if (splitString.indexOf("::") == -1) {
            if (position == 0)
                return splitString;
            if (position == 1)
                return null;
        }
        List splitList = StringUtil.split(splitString, "::");
        return (String) splitList.get(position);
    }
}

/** Workflow Runner class runs inside its own thread using the Scheduler API */
class WorkflowRunner extends AbstractJob {

    GenericRequester requester;
    WfProcess process;
    String startActivityId;

    WorkflowRunner(WfProcess process, GenericRequester requester, String startActivityId) {
        super(process.toString());
        this.process = process;
        this.requester = requester;
        this.startActivityId = startActivityId;
        runtime = new Date().getTime();
    }

    protected void finish() {
        runtime = -1;
    }

    public void exec() {
        try {
            if (startActivityId != null)
                process.start(startActivityId);
            else
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

