/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.util.*;

/**
 * Generic Service Job - A generic async-service Job.
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @created    March 3, 2002
 * @version    1.2
 */
public class GenericServiceJob extends AbstractJob {

    public static final String module = GenericServiceJob.class.getName();

    protected transient GenericRequester requester;
    protected transient DispatchContext dctx;
    private boolean trans;
    private String service;
    private Map context;

    public GenericServiceJob(DispatchContext dctx, String jobName, String service, Map context, GenericRequester req) {
        this(dctx, jobName, service, context, req, true);
    }

    public GenericServiceJob(DispatchContext dctx, String jobName, String service, Map context, GenericRequester req, boolean trans) {
        super(jobName);
        this.dctx = dctx;
        this.service = service;
        this.context = context;
        this.requester = req;
        runtime = new Date().getTime();
    }

    protected GenericServiceJob(String jobName) {
        super(jobName);
        this.dctx = null;
        this.requester = null;
        this.service = null;
        this.context = null;
    }

    /**
     * Invokes the service.
     */
    public void exec() {
        init();
        boolean begunTransaction = false;
        if (trans) {
            try {
                begunTransaction = TransactionUtil.begin();
            } catch (GenericTransactionException te) {
                Debug.logError(te, module);
            }
        }

        try {
            // get the dispatcher and invoke the service via runSync -- will run all ECAs
            LocalDispatcher dispatcher = dctx.getDispatcher();
            Map result = dispatcher.runSync(getServiceName(), getContext());
            if (requester != null)
                requester.receiveResult(result);

            // call the finish method
            finish();

            // commit the transaction if we started it.
            if (trans && begunTransaction) {
                try {
                    TransactionUtil.commit();
                } catch (GenericTransactionException te) {
                    throw new GenericServiceException("Cannot commit transaction.", te.getNested());
                }
            }
        } catch (Exception e) {
            Debug.logError(e, "Service invocation error: " + e.getMessage(), module);
            e.printStackTrace();
            if (trans && begunTransaction) {
                try {
                    TransactionUtil.rollback();
                } catch (GenericTransactionException te) {
                    Debug.logError(te, "Cannot rollback transaction", module);
                }
            }
        }
    }

    /**
     * Method is called prior to running the service.
     */
    protected void init() {
        if (Debug.verboseOn()) Debug.logVerbose("Async-Service initializing.", module);
    }

    /**
     * Method is called after the service has finished.
     */
    protected void finish() {
        if (Debug.verboseOn()) Debug.logVerbose("Async-Service finished.", module);
        runtime = 0;
    }

    /**
     * Gets the context for the service invocation.
     * @return Map of name value pairs making up the service context.
     */
    protected Map getContext() {
        return context;
    }

    /**
     * Gets the name of the service as defined in the definition file.
     * @return The name of the service to be invoked.
     */
    protected String getServiceName() {
        return service;
    }
}
