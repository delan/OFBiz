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
package org.ofbiz.core.service;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.jms.*;
import org.ofbiz.core.service.job.*;

/**
 * Generic Services Local Dispatcher
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public interface LocalDispatcher {
    
    /**
     * Run the service synchronously and return the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws GenericServiceException
     */
    public Map runSync(String serviceName, Map context) throws GenericServiceException;        

    /**
     * Run the service synchronously and IGNORE the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException;

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist) throws GenericServiceException;

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * This method WILL persist the job.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException;

    /**
     * Run the service asynchronously and IGNORE the result.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context, boolean persist) throws GenericServiceException;

    /**
     * Run the service asynchronously and IGNORE the result. This method WILL persist the job.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runAsync(String serviceName, Map context) throws GenericServiceException;

    /**
     * Run the service asynchronously.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @return A new GenericRequester object.
     * @throws GenericServiceException
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context, boolean persist) throws GenericServiceException;

    /**
     * Run the service asynchronously. This method WILL persist the job.
     * @param serviceName Name of the service to run.
     * @param context Map of name, value pairs composing the context.
     * @return A new GenericRequester object.
     * @throws GenericServiceException
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException;

    /**
     * Schedule a service to run asynchronously at a specific start time.
     * @param serviceName Name of the service to invoke.
     * @param context The name/value pairs composing the context.
     * @param startTime The time to run this service.
     * @param frequency The frequency of the recurrence (RecurrenceRule.DAILY, etc).
     * @param interval The interval of the frequency recurrence.
     * @param count The number of times to repeat.
     * @param endTime The time in milliseconds the service should expire
     * @throws GenericServiceException.
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count, long endTime) throws GenericServiceException;
                
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
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count) throws GenericServiceException;
   
    /**
     * Schedule a service to run asynchronously at a specific start time.
     * @param serviceName Name of the service to invoke.
     * @param context The name/value pairs composing the context.
     * @param startTime The time to run this service.
     * @param frequency The frequency of the recurrence (RecurrenceRule.DAILY, etc).
     * @param interval The interval of the frequency recurrence.
     * @param endTime The time in milliseconds the service should expire
     * @throws GenericServiceException.
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, long endTime) throws GenericServiceException;
             
    /**
     * Schedule a service to run asynchronously at a specific start time.
     * @param serviceName Name of the service to invoke.
     * @param context The name/value pairs composing the context.
     * @param startTime The time to run this service.
     * @throws GenericServiceException
     */
    public void schedule(String serviceName, Map context, long startTime) throws GenericServiceException;
           
    /**
     * Gets the JobManager associated with this dispatcher
     * @return JobManager that is associated with this dispatcher
     */
    public JobManager getJobManager();        

    /**
     * Gets the JmsListenerFactory which holds the message listeners.
     * @return JmsListenerFactory
     */
    public JmsListenerFactory getJMSListeneFactory();            

    /**
     * Gets the GenericEntityDelegator associated with this dispatcher
     * @return GenericEntityDelegator associated with this dispatcher
     */
    public GenericDelegator getDelegator();

    /**
     * Gets the Security object associated with this dispatcher
     * @return Security object associated with this dispatcher
     */
    public Security getSecurity();

    /**
     * Returns the Name of this local dispatcher
     * @return String representing the name of this local dispatcher
     */
    public String getName();            

    /**
     * Returns the DispatchContext created by this dispatcher
     * @return DispatchContext created by this dispatcher
     */
    public DispatchContext getDispatchContext();  
}

