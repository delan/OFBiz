/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.scheduler.*;

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
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 8, 2001
 */
public class LocalDispatcher {
    
    protected DispatchContext ctx;
    protected ServiceDispatcher dispatcher;
    protected String name;
    
    public LocalDispatcher(String name, String root, GenericDelegator delegator, Collection readerURLs) {
        this(name,root,delegator,readerURLs,null);
    }
    
    public LocalDispatcher(String name, String root, GenericDelegator delegator, Collection readerURLs, ClassLoader loader) {
        if ( loader == null ) {
            try {
                loader = Thread.currentThread().getContextClassLoader();
            }
            catch ( SecurityException e ) {
                loader = this.getClass().getClassLoader();
            }
        }
        ctx = new DispatchContext(name,root,readerURLs,loader,this);
        init(name, delegator, ctx);        
    }
    
    public LocalDispatcher(String name, GenericDelegator delegator, DispatchContext ctx) {
        init(name, delegator, ctx);
    }
    
    private void init(String name, GenericDelegator delegator, DispatchContext ctx) {
        this.name = name;
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
