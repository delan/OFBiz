/*
 * $Id: ServiceXaWrapper.java,v 1.8 2004/07/27 18:12:42 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.service;

import java.util.Map;

import javax.transaction.*;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.transaction.GenericXaResource;
import org.ofbiz.entity.transaction.TransactionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;

/**
 * ServiceXaWrapper - XA Resource wrapper for running services on commit() or rollback()
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.8 $
 * @since      3.0
 */
public class ServiceXaWrapper extends GenericXaResource {

    public static final String module = ServiceXaWrapper.class.getName();

    protected DispatchContext dctx = null;
    protected String rollbackService = null;
    protected String commitService = null;
    protected Map rollbackContext = null;
    protected Map commitContext = null;
    protected boolean rollbackAsync = true;
    protected boolean rollbackAsyncPersist = true;
    protected boolean commitAsync = false;
    protected boolean commitAsyncPersist = false;

    protected ServiceXaWrapper() {}
    public ServiceXaWrapper(DispatchContext dctx) {
        this.dctx = dctx;
    }

    /**
     * Sets the service to run on rollback()
     * @param serviceName Name of service to run
     * @param context Context to use when running
     */
    public void setCommitService(String serviceName, Map context) {
        this.setCommitService(serviceName, context, commitAsync, commitAsyncPersist);
    }

    /**
     * Sets the service to run on rollback()
     * @param serviceName Name of service to run
     * @param context Context to use when running
     * @param async override default async behavior
     */
    public void setCommitService(String serviceName, Map context, boolean async, boolean persist) {
        this.commitService = serviceName;
        this.commitContext = context;
        this.commitAsync = async;
        this.commitAsyncPersist = persist;
    }

    /**
     * @return The name of the service to run on rollback()
     */
    public String getCommitService() {
        return this.commitService;
    }

    /**
     * @return The context used when running the rollback() service
     */
    public Map getCommitContext() {
        return this.commitContext;
    }

    /**
     * Sets the service to run on rollback()
     * @param serviceName Name of service to run
     * @param context Context to use when running
     */
    public void setRollbackService(String serviceName, Map context) {
        this.setRollbackService(serviceName, context, rollbackAsync, rollbackAsyncPersist);
    }

    /**
     * Sets the service to run on rollback()
     * @param serviceName Name of service to run
     * @param context Context to use when running
     * @param async override default async behavior
     */
    public void setRollbackService(String serviceName, Map context, boolean async, boolean persist) {
        this.rollbackService = serviceName;
        this.rollbackContext = context;
        this.rollbackAsync = async;
        this.rollbackAsyncPersist = persist;
    }

    /**
     * @return The name of the service to run on rollback()
     */
    public String getRollbackService() {
        return this.rollbackService;
    }

    /**
     * @return The context used when running the rollback() service
     */
    public Map getRollbackContext() {
        return this.rollbackContext;
    }

    public void enlist() throws XAException {
        super.enlist();
        Debug.log("Enlisted in transaction : " + this.toString(), module);
    }

    // -- XAResource Methods
    /**
     * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid xid, boolean onePhase)
     */
    public void commit(Xid xid, boolean onePhase) throws XAException {
        Debug.log("ServiceXaWrapper#commit() : " + onePhase + " / " + xid.toString(), module);
        // the commit listener
        if (this.active) {
            Debug.logWarning("commit() called without end()", module);
        }
        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }

        if (this.commitService != null) {
            // suspend this transaction
            TransactionManager tm = TransactionFactory.getTransactionManager();
            Transaction parentTransaction = null;
            try {
                parentTransaction = tm.suspend();
            } catch (SystemException e) {
                throw new XAException(XAException.XA_RBOTHER);
            }

            // invoke the service
            boolean serviceError = false;
            try {
                ModelService model = dctx.getModelService(this.commitService);
                Map thisContext = this.commitContext;
                if (model.validate) {
                    thisContext = model.makeValid(this.commitContext, ModelService.IN_PARAM);
                }
                if (this.commitAsync) {
                    Debug.log("[Commit] Invoking [" + this.commitService + "] via runAsync", module);
                    dctx.getDispatcher().runAsync(this.commitService, thisContext, this.commitAsyncPersist);
                } else {
                    Debug.log("[Commit] Invoking [" + this.commitService + "] via runSyncIgnore", module);
                    dctx.getDispatcher().runSyncIgnore(this.commitService, thisContext);
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling commit service : " + this.commitService + " / " + this.commitContext, module);
                // async calls are assumed to not effect this TX
                if (!this.commitAsync) {
                    serviceError = true; // don't throw the exception until we resume the transaction
                }
            }

            // resume the transaction
            if (parentTransaction != null) {
                try {
                    tm.resume(parentTransaction);
                } catch (InvalidTransactionException e) {
                    Debug.logError(e, module);
                    throw new XAException(XAException.XA_RBOTHER);
                } catch (IllegalStateException e) {
                    Debug.logError(e, module);
                    throw new XAException(XAException.XA_RBOTHER);
                } catch (SystemException e) {
                    Debug.logError(e, module);
                    throw new XAException(XAException.XA_RBOTHER);
                }
            }

            // now throw the exception
            if (serviceError) {
                throw new XAException(XAException.XA_RBOTHER);
            }
        } else {
            Debug.log("No commit service defined; nothing to do", module);
        }

        this.xid = null;
        this.active = false;
    }

    /**
     * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid xid)
     */
    public void rollback(Xid xid) throws XAException {
        Debug.log("ServiceXaWrapper#rollback() : " + xid.toString(), module);
        // the rollback listener
        if (this.active) {
            Debug.logWarning("rollback() called without end()", module);
        }
        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }

        if (this.rollbackService != null) {
            int currentTxStatus = Status.STATUS_UNKNOWN;
            try {
                currentTxStatus = TransactionUtil.getStatus();
            } catch (GenericTransactionException e) {
                Debug.logError(e, module);
            }

            TransactionManager tm = TransactionFactory.getTransactionManager();
            Transaction parentTransaction = null;
            if (currentTxStatus != Status.STATUS_NO_TRANSACTION) {
                try {
                    parentTransaction = tm.suspend();
                } catch (SystemException e) {
                    Debug.logError(e, module);
                }
            }

            // invoke the service
            try {
                ModelService model = dctx.getModelService(this.rollbackService);
                Map thisContext = this.rollbackContext;
                if (model.validate) {
                    thisContext = model.makeValid(this.rollbackContext, ModelService.IN_PARAM);
                }
                if (this.rollbackAsync) {
                    Debug.log("[Rollback] Invoking [" + this.rollbackService + "] via runAsync", module);
                    dctx.getDispatcher().runAsync(this.rollbackService, thisContext, this.rollbackAsyncPersist);
                } else {
                    Debug.log("[Rollback] Invoking [" + this.rollbackService + "] via runSyncIgnore", module);
                    dctx.getDispatcher().runSyncIgnore(this.rollbackService, thisContext);
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling async service : " + this.rollbackService + " / " + this.rollbackContext, module);
            }

            if (parentTransaction != null) {
                try {
                    tm.resume(parentTransaction);
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
        } else {
            Debug.log("No rollback service defined; nothing to do", module);
        }

        this.xid = null;
        this.active = false;
    }

    public int prepare(Xid xid) throws XAException {
        // overriding to log two phase commits
        Debug.log("ServiceXaWrapper#prepare() : " + xid.toString(), module);
        int rtn = XA_OK;
        try {
            rtn = super.prepare(xid);
        } catch (XAException e) {
            Debug.logError(e, module);
            throw e;
        }
        Debug.log("ServiceXaWrapper#prepare() : " + rtn + " / " + (rtn == XA_OK) , module);
        return rtn;
    }
}
