/*
 * $Id$
 */

package org.ofbiz.core.workflow;

import java.util.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.workflow.impl.*;

/**
 * <p><b>Title:</b> WorkflowEngine
 * <p><b>Description:</b> Workflow Service Engine
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
 *@created    November 16, 2001
 *@version    1.0
 */
public class WorkflowEngine implements GenericEngine {
    
    protected ServiceDispatcher dispatcher;
    protected String loader;
    
    /** Creates new WorkflowEngine */
    public WorkflowEngine(ServiceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.loader = null;
    }
    
    /** Set the name of the local dispatcher
     * @param loader name of the local dispatcher
     */
    public void setLoader(String loader) {
        this.loader = loader;
    }
    
    /** Run the service synchronously and return the result
     * @param context Map of name, value pairs composing the context
     * @return Map of name, value pairs composing the result
     */
    public Map runSync(ModelService modelService, Map context) throws GenericServiceException {
        GenericResultWaiter waiter = new GenericResultWaiter();
        runAsync(modelService,context,null);
        return new HashMap();
    }
    
    /** Run the service synchronously and IGNORE the result
     * @param context Map of name, value pairs composing the context
     */
    public void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException {
        runAsync(modelService,context,null);
    }
    
    /** Run the service asynchronously and IGNORE the result
     * @param context Map of name, value pairs composing the context
     */
    public void runAsync(ModelService modelService, Map context) throws GenericServiceException {
        runAsync(modelService,context,null);
    }
    
    /** Run the service asynchronously, passing an instance of GenericRequester that will receive the result
     * @param context Map of name, value pairs composing the context
     * @param requester Object implementing GenericRequester interface which will receive the result
     */
    public void runAsync(ModelService modelService, Map context, GenericRequester requester) throws GenericServiceException {
        // validate the context
        if ( !ModelService.validate(modelService.contextInfo,context) )
            throw new GenericServiceException("Context does not match expected requirements");
        
        // Build the requester
        WfRequester req = null;
        try {
            req = WfFactory.newWfRequester();        
        }
        catch ( WfException e ) {
            throw new GenericServiceException(e.getMessage(),e);
        }
        
        // Build the process manager
        WfProcessMgr mgr = null;
        try {
            mgr = WfFactory.newWfProcessMgr(dispatcher.getDelegator(),modelService.name);
        }
        catch ( WfException e ) {
            throw new GenericServiceException(e.getMessage(),e);
        }
        
        // Create the process
        WfProcess process = null;
        try {
            process = mgr.createProcess(req);
        }        
        catch ( NotEnabled ne ) {
            throw new GenericServiceException(ne.getMessage(),ne);
        }
        catch ( InvalidRequester ir ) {
            throw new GenericServiceException(ir.getMessage(),ir);
        }
        catch ( RequesterRequired rr ) {
            throw new GenericServiceException(rr.getMessage(),rr);
        }
        catch ( WfException wfe ) {
            throw new GenericServiceException(wfe.getMessage(),wfe);
        }
        
        // Set the service dispatcher for the workflow
        try {
            process.setDispatcher(dispatcher,loader);
        }
        catch ( WfException e ) {
            throw new GenericServiceException(e.getMessage(),e);
        }
    
        // Register the process
        try {
            req.registerProcess(process,context,requester);
        }
        catch ( WfException wfe ) {
            throw new GenericServiceException(wfe.getMessage(),wfe);
        }
    }
}
