/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.net.*;
import java.util.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.scheduler.*;

/**
 * <p><b>Title:</b> Generic Engine Interface
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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    Oct 20 2001
 *@version    1.0
 */
public class ServiceDispatcher {

    protected Map modelServices;
    protected GenericDelegator delegator;
    protected JobManager jm;
    
    ServiceDispatcher(GenericDelegator delegator, Collection readerURLs) {
        this.addReaders(readerURLs);
        this.delegator = delegator;
        jm = new JobManager(this,null);
    }
    
    public void addReaders(Collection readerURLs) {
        if(readerURLs == null) return;
        Iterator urlIter = readerURLs.iterator();
        while(urlIter.hasNext()) {
            URL readerURL = (URL)urlIter.next();
            this.addReader(readerURL);
        }
    }
    
    public void addReader(URL readerURL) {
        ModelServiceReader reader = ModelServiceReader.getModelServiceReader(readerURL);
        modelServices.putAll(reader.getModelServices());
    }
    
    /** Run the service synchronously and return the result
     *@param context Map of name, value pairs composing the context
     *@return Map of name, value pairs composing the result
     */
    public Map runSync(String serviceName, Map context) throws GenericServiceException {
        return null;
    }
    
    /** Run the service synchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException {
    }
    
    /** Run the service asynchronously, passing an instance of GenericRequester that will receive the result
     *@param context Map of name, value pairs composing the context
     *@param requester Object implementing GenericRequester interface which will receive the result
     */
    public void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException {
    }
    
    /** Run the service asynchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runAsync(String serviceName, Map context) throws GenericServiceException {
    }
    
    /** Run the service asynchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        this.runAsync(serviceName, context, waiter);
        return waiter;
    }
    
    /** Gets the GenericServiceModel instance that corresponds to given the name
     *@param serviceName Name of the service
     *@return GenericServiceModel that corresponds to the serviceName
     */
    public ModelService getModelService(String serviceName) {
        return (ModelService)modelServices.get(serviceName);
    }
    
    /** Gets the JobManager associated with this dispatcher
     *@return JobManager that is associated with this dispatcher
     */
    public JobManager getJobManager() {
        return this.jm;
    }            
    
    /** Gets the GenericEntityDelegator associated with this dispatcher
     *@return GenericEntityDelegator associated with this dispatcher
     */
    public GenericDelegator getDelegator() {
        return this.delegator;
    }
}
