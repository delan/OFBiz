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
import org.ofbiz.core.util.*;

/**
 * Generic Services Local Dispatcher
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class GenericDispatcher extends GenericAbstractDispatcher {

    public static final String module = GenericDispatcher.class.getName();
  
    public GenericDispatcher() {}
    
    public GenericDispatcher(String name, GenericDelegator delegator) {
        this(name, delegator, null);
    }

    public GenericDispatcher(String name, GenericDelegator delegator, ClassLoader loader) {
        if (loader == null) {
            try {
                loader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException e) {
                loader = this.getClass().getClassLoader();
            }
        }
        DispatchContext dc = new DispatchContext(name, null, loader, null);
        init(name, delegator, dc);
    }

    public GenericDispatcher(DispatchContext ctx, GenericDelegator delegator) {
        init(ctx.getName(), delegator, ctx);
    }
    
    public GenericDispatcher(DispatchContext ctx, ServiceDispatcher dispatcher) {       
        this.dispatcher = dispatcher;
        this.ctx = ctx;
        this.name = ctx.getName();
                
        ctx.setDispatcher(this);
        dispatcher.register(name, ctx);
    }

    protected void init(String name, GenericDelegator delegator, DispatchContext ctx) {
        if (name == null && name.length() == 0)
            throw new IllegalArgumentException("The name of a LocalDispatcher cannot be a null or empty String");
            
        this.name = name;
        this.ctx = ctx;
        this.dispatcher = ServiceDispatcher.getInstance(name, ctx, delegator);
        
        ctx.setDispatcher(this);
        if (Debug.infoOn()) Debug.logInfo("[LocalDispatcher] : Created Dispatcher for: " + name, module);
    }
   
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runSync(java.lang.String, java.util.Map)
     */
    public Map runSync(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        return dispatcher.runSync(this.name, service, context);
    }
    
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runSyncIgnore(java.lang.String, java.util.Map)
     */
    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runSyncIgnore(this.name, service, context);
    }
    
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.core.service.GenericRequester, boolean)
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runAsync(this.name, service, context, requester, persist);
    }
   
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, org.ofbiz.core.service.GenericRequester)
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException {
        runAsync(serviceName, context, requester, true);
    }
    
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map, boolean)
     */
    public void runAsync(String serviceName, Map context, boolean persist) throws GenericServiceException {
        ModelService service = ctx.getModelService(serviceName);
        dispatcher.runAsync(this.name, service, context, persist);
    }
   
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runAsync(java.lang.String, java.util.Map)
     */
    public void runAsync(String serviceName, Map context) throws GenericServiceException {
        runAsync(serviceName, context, true);
    }
  
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runAsyncWait(java.lang.String, java.util.Map, boolean)
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context, boolean persist) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        this.runAsync(serviceName, context, waiter, persist);
        return waiter;
    }
 
    /**
     * @see org.ofbiz.core.service.LocalDispatcher#runAsyncWait(java.lang.String, java.util.Map)
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException {
        return runAsyncWait(serviceName, context, true);
    }  
}

