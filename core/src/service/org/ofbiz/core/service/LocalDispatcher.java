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


import java.util.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.jms.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.util.*;


/**
 * Generic Services Local Dispatcher
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 8, 2001
 *@version    1.0
 */
public class LocalDispatcher {

    public static final String module = LocalDispatcher.class.getName();

    protected DispatchContext ctx;
    protected ServiceDispatcher dispatcher;
    protected String name;

    public LocalDispatcher(DispatchContext ctx, ServiceDispatcher dispatcher) {
        this.name = ctx.getName();
        this.dispatcher = dispatcher;
        this.ctx = ctx;
        ctx.setDispatcher(this);
        dispatcher.register(name, ctx);
    }

    public LocalDispatcher(String name, GenericDelegator delegator, Collection readerURLs) {
        this(name, delegator, readerURLs, null);
    }

    public LocalDispatcher(String name, GenericDelegator delegator, Collection readerURLs, ClassLoader loader) {
        if (loader == null) {
            try {
                loader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException e) {
                loader = this.getClass().getClassLoader();
            }
        }
        DispatchContext dc = new DispatchContext(name, readerURLs, loader, null);

        init(name, delegator, dc);
    }

    public LocalDispatcher(DispatchContext ctx, GenericDelegator delegator) {
        init(ctx.getName(), delegator, ctx);
    }

    private void init(String name, GenericDelegator delegator, DispatchContext ctx) {
        if (name == null && name.length() == 0)
            throw new IllegalArgumentException("The name of a LocalDispatcher cannot be a null or empty String");
        this.name = name;
        this.ctx = ctx;
        this.dispatcher = ServiceDispatcher.getInstance(name, ctx, delegator);
        ctx.setDispatcher(this);
        if (Debug.infoOn()) Debug.logInfo("[LocalDispatcher] : Created Dispatcher for: " + name, module);
    }

    /**
     * Run the service synchronously and return the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws GenericServiceException
     */
    public Map runSync(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);

        return dispatcher.runSync(this.name, service, context);
    }

    /**
     * Run the service synchronously and IGNORE the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);

        dispatcher.runSyncIgnore(this.name, service, context);
    }

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);

        dispatcher.runAsync(this.name, service, context, requester, persist);
    }

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * This method WILL persist the job.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException {
        runAsync(serviceName, context, requester, true);
    }

    /**
     * Run the service asynchronously and IGNORE the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context, boolean persist) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);

        dispatcher.runAsync(this.name, service, context, persist);
    }

    /**
     * Run the service asynchronously and IGNORE the result. This method WILL persist the job.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context) throws GenericServiceException {
        runAsync(serviceName, context, true);
    }

    /**
     * Run the service asynchronously.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @return A new GenericRequester object.
     * @throws GenericServiceException
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context, boolean persist) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();

        this.runAsync(serviceName, context, waiter, persist);
        return waiter;
    }

    /**
     * Run the service asynchronously. This method WILL persist the job.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @return A new GenericRequester object.
     * @throws GenericServiceException
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException {
        return runAsyncWait(serviceName, context, true);
    }

    /**
     * Schedule a service to run asynchronously at a specific start time.
     * @param serviceName Name of the service to invoke.
     * @param context The name/value pairs composing the context.
     * @param startTime The time to run this service.
     * @param frequency The frequency of the recurrence (RecurrenceRule.DAILY, etc).
     * @param interval The interval of the frequency recurrence.
     * @param count The number of times to repeat.
     * @throws GenericServiceException.
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count)
        throws GenericServiceException {
        try {
            getJobManager().schedule(getName(), serviceName, context, startTime,
                frequency, interval, count);
            if (Debug.verboseOn()) Debug.logVerbose("[LocalDispatcher.schedule] : Current time: " + (new Date()).getTime(), module);
            if (Debug.verboseOn()) Debug.logVerbose("[LocalDispatcher.schedule] : Runtime: " + startTime, module);
            if (Debug.verboseOn()) Debug.logVerbose("[LocalDispatcher.schedule] : Frequency: " + frequency, module);
            if (Debug.verboseOn()) Debug.logVerbose("[LocalDispatcher.schedule] : Interval: " + interval, module);
            if (Debug.verboseOn()) Debug.logVerbose("[LocalDispatcher.schedule] : Count: " + count, module);
        } catch (JobManagerException e) {
            throw new GenericServiceException(e.getMessage(), e);
        }
    }

    /**
     * Schedule a service to run asynchronously at a specific start time.
     * @param serviceName Name of the service to invoke.
     * @param context The name/value pairs composing the context.
     * @param startTime The time to run this service.
     * @throws GenericServiceException
     */
    public void schedule(String serviceName, Map context, long startTime) throws GenericServiceException {
        schedule(serviceName, context, startTime, RecurrenceRule.DAILY, 1, 1);
    }

    /**
     * Gets the JobManager associated with this dispatcher
     * @return JobManager that is associated with this dispatcher
     */
    public JobManager getJobManager() {
        return dispatcher.getJobManager();
    }

    /**
     * Gets the JmsListenerFactory which holds the message listeners.
     * @return JmsListenerFactory
     */
    public JmsListenerFactory getJMSListeneFactory() {
        return dispatcher.getJMSListenerFactory();
    }

    /**
     * Gets the GenericEntityDelegator associated with this dispatcher
     * @return GenericEntityDelegator associated with this dispatcher
     */
    public GenericDelegator getDelegator() {
        return dispatcher.getDelegator();
    }

    /**
     * Gets the Security object associated with this dispatcher
     * @return Security object associated with this dispatcher
     */
    public Security getSecurity() {
        return dispatcher.getSecurity();
    }

    /**
     * Returns the Name of this local dispatcher
     * @return String representing the name of this local dispatcher
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the DispatchContext created by this dispatcher
     * @return DispatchContext created by this dispatcher
     */
    public DispatchContext getDispatchContext() {
        return ctx;
    }

}

