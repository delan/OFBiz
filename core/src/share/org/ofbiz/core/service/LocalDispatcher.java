/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;

import org.ofbiz.core.calendar.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Services Local Dispatcher
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
 *@created    November 8, 2001
 *@version    1.0
 */
public class LocalDispatcher {

    protected DispatchContext ctx;
    protected ServiceDispatcher dispatcher;
    protected String name;

    public LocalDispatcher(String name, String root, String rootExt, GenericDelegator delegator,
                           Collection readerURLs) {
        this(name, root, rootExt, delegator, readerURLs, null);
    }

    public LocalDispatcher(String name, String root, String rootExt, GenericDelegator delegator,
                           Collection readerURLs, ClassLoader loader) {
        if (loader == null) {
            try {
                loader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException e) {
                loader = this.getClass().getClassLoader();
            }
        }
        DispatchContext dc =
                new DispatchContext(name, root, rootExt, readerURLs, loader, null);
        init(name, delegator, dc);
    }

    public LocalDispatcher(String name, GenericDelegator delegator,
                           DispatchContext ctx) {
        init(name, delegator, ctx);
    }

    private void init(String name, GenericDelegator delegator, DispatchContext ctx) {
        this.name = name;
        this.ctx = ctx;
        ctx.setDispatcher(this);
        dispatcher = ServiceDispatcher.getInstance(name, ctx, delegator);
        Debug.logInfo("[LocalDispatcher] : Created Dispatcher for: " + name);
    }

    /** Run the service synchronously and return the result
     *@param context Map of name, value pairs composing the context
     *@return Map of name, value pairs composing the result
     */
    public Map runSync(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        return dispatcher.runSync(this.name, service, context);
    }

    /** Run the service synchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runSyncIgnore(this.name, service, context);
    }

    /** Run the service asynchronously, passing an instance of GenericRequester that will receive the result
     *@param context Map of name, value pairs composing the context
     *@param requester Object implementing GenericRequester interface which will receive the result
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runAsync(this.name, service, context, requester);
    }

    /** Run the service asynchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runAsync(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runAsync(this.name, service, context);
    }

    /** Run the service asynchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        this.runAsync(serviceName, context, waiter);
        return waiter;
    }

    /** Schedule a service to run asynchronously at a specific start time
     *@param serviceName Name of the service to invoke
     *@param context The name/value pairs composing the context
     *@param startTime The time to run this service
     *@param frequency The frequency of the recurrence (RecurrenceRule.DAILY, etc)
     *@param interval The interval of the frequency recurrence
     *@param count The number of times to repeat
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count)
            throws GenericServiceException {
        try {
            getJobManager().schedule(getName(), serviceName, context, startTime,
                                     frequency, interval, count);
            Debug.logVerbose("[LocalDispatcher.schedule] : Current time: " +
                             (new Date()).getTime());
            Debug.logVerbose("[LocalDispatcher.schedule] : Runtime: " + startTime);
            Debug.logVerbose("[LocalDispatcher.schedule] : Frequency: " + frequency);
            Debug.logVerbose("[LocalDispatcher.schedule] : Interval: " + interval);
            Debug.logVerbose("[LocalDispatcher.schedule] : Count: " + count);
        } catch (JobSchedulerException e) {
            throw new GenericServiceException(e.getMessage(), e);
        }
    }

    /** Schedule a service to run asynchronously at a specific start time
     *@param serviceName Name of the service to invoke
     *@param context The name/value pairs composing the context
     *@param startTime The time to run this service
     */
    public void schedule(String serviceName, Map context, long startTime) throws GenericServiceException {
        schedule(serviceName, context, startTime, RecurrenceRule.DAILY, 1, 1);
    }

    /** Gets the JobManager associated with this dispatcher
     *@return JobManager that is associated with this dispatcher
     */
    public JobManager getJobManager() {
        return dispatcher.getJobManager();
    }

    /** Gets the GenericEntityDelegator associated with this dispatcher
     *@return GenericEntityDelegator associated with this dispatcher
     */
    public GenericDelegator getDelegator() {
        return dispatcher.getDelegator();
    }

    /** Gets the Security object associated with this dispatcher
     *@return Security object associated with this dispatcher
     */
    public Security getSecurity() {
        return dispatcher.getSecurity();
    }

    /** Returns the Name of this local dispatcher
     *@return String representing the name of this local dispatcher
     */
    public String getName() {
        return this.name;
    }

    /** Returns the DispatchContext created by this dispatcher
     *@return DispatchContext created by this dispatcher
     */
    public DispatchContext getDispatchContext() {
        return ctx;
    }
}

