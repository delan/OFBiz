/*
 * $Id: GenericAbstractDispatcher.java,v 1.2 2003/08/28 19:34:57 ajzeneski Exp $
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
package org.ofbiz.service;

import java.util.Date;
import java.util.Map;

import org.ofbiz.service.calendar.RecurrenceRule;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.security.Security;
import org.ofbiz.service.jms.JmsListenerFactory;
import org.ofbiz.service.job.JobManager;
import org.ofbiz.service.job.JobManagerException;
import org.ofbiz.base.util.Debug;

/**
 * Generic Services Local Dispatcher
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision: 1.2 $
 * @since      2.0
 */
public abstract class GenericAbstractDispatcher implements LocalDispatcher {

    public static final String module = GenericAbstractDispatcher.class.getName();

    protected DispatchContext ctx = null;
    protected ServiceDispatcher dispatcher = null;
    protected String name = null;

    public GenericAbstractDispatcher() {}
    
    // abstract methods
       
    /**
     * @see org.ofbiz.service.LocalDispatcher#runSync(java.lang.String, java.util.Map)
     */
    public abstract Map runSync(String serviceName, Map context) throws GenericServiceException;
    
    /**
     * @see org.ofbiz.service.LocalDispatcher#runSyncIgnore(java.lang.String, java.util.Map)
     */
    public abstract void runSyncIgnore(String serviceName, Map context) throws GenericServiceException;

    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.service.GenericRequester, boolean)
     */
    public abstract void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist) throws GenericServiceException;
   
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.service.GenericRequester)
     */
    public abstract void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException;
    
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, boolean)
     */
    public abstract void runAsync(String serviceName, Map context, boolean persist) throws GenericServiceException;
   
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map)
     */
    public abstract void runAsync(String serviceName, Map context) throws GenericServiceException;
  
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsyncWait(java.lang.String, java.util.Map, boolean)
     */
    public abstract GenericResultWaiter runAsyncWait(String serviceName, Map context, boolean persist) throws GenericServiceException;
 
    /**
     * @see org.ofbiz.service.LocalDispatcher#runAsyncWait(java.lang.String, java.util.Map)
     */
    public abstract GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException;
  
    // generic methods
    
    /**
     * @see org.ofbiz.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long, int, int, int, long)
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count, long endTime) throws GenericServiceException {            
        try {
            getJobManager().schedule(serviceName, context, startTime, frequency, interval, count, endTime);
                
            if (Debug.verboseOn()) {
                Debug.logVerbose("[LocalDispatcher.schedule] : Current time: " + (new Date()).getTime(), module);
                Debug.logVerbose("[LocalDispatcher.schedule] : Runtime: " + startTime, module);
                Debug.logVerbose("[LocalDispatcher.schedule] : Frequency: " + frequency, module);
                Debug.logVerbose("[LocalDispatcher.schedule] : Interval: " + interval, module);
                Debug.logVerbose("[LocalDispatcher.schedule] : Count: " + count, module);
                Debug.logVerbose("[LocalDispatcher.schedule] : EndTime: " + endTime, module);
            }
            
        } catch (JobManagerException e) {
            throw new GenericServiceException(e.getMessage(), e);
        }
    }
      
    /**
     * @see org.ofbiz.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long, int, int, int)
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count) throws GenericServiceException {            
        schedule(serviceName, context, startTime, frequency, interval, count, 0);
    }  
      
    /**
     * @see org.ofbiz.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long, int, int, long)
     */
    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, long endTime) throws GenericServiceException {            
        schedule(serviceName, context, startTime, frequency, interval, -1, endTime);
    }      

    /**
     * @see org.ofbiz.service.LocalDispatcher#schedule(java.lang.String, java.util.Map, long)
     */
    public void schedule(String serviceName, Map context, long startTime) throws GenericServiceException {
        schedule(serviceName, context, startTime, RecurrenceRule.DAILY, 1, 1);
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#getJobManager()
     */
    public JobManager getJobManager() {
        return dispatcher.getJobManager();
    }
   
    /**
     * @see org.ofbiz.service.LocalDispatcher#getJMSListeneFactory()
     */
    public JmsListenerFactory getJMSListeneFactory() {
        return dispatcher.getJMSListenerFactory();
    }
  
    /**
     * @see org.ofbiz.service.LocalDispatcher#getDelegator()
     */
    public GenericDelegator getDelegator() {
        return dispatcher.getDelegator();
    }
  
    /**
     * @see org.ofbiz.service.LocalDispatcher#getSecurity()
     */
    public Security getSecurity() {
        return dispatcher.getSecurity();
    }
  
    /**
     * @see org.ofbiz.service.LocalDispatcher#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see org.ofbiz.service.LocalDispatcher#getDispatchContext()
     */
    public DispatchContext getDispatchContext() {
        return ctx;
    }
    
    /**
     * @see org.ofbiz.service.LocalDispatcher#deregister()
     */
    public void deregister() {
        dispatcher.deregister(this);
    }

}

