/*
 * $Id: SharkServiceEngine.java,v 1.2 2004/07/01 15:27:15 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.service;

import java.util.Map;

import org.ofbiz.service.ModelService;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.GenericRequester;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.engine.AbstractEngine;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.shark.requester.PersistentRequester;
import org.ofbiz.shark.requester.RequesterFactory;

import org.enhydra.shark.api.client.wfservice.AdminInterface;
import org.enhydra.shark.api.client.wfservice.ExecutionAdministration;
import org.enhydra.shark.api.client.wfservice.NotConnected;
import org.enhydra.shark.api.client.wfservice.ConnectFailed;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.client.wfmodel.*;

/**
 * Shark Service Engine
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class SharkServiceEngine extends AbstractEngine {

    public static final String module = SharkServiceEngine.class.getName();

    public SharkServiceEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Run the service synchronously and return the result.
     *
     * @param localName    Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context      Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws org.ofbiz.service.GenericServiceException
     *
     */
    public Map runSync(String localName, ModelService modelService, Map context) throws GenericServiceException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Run the service synchronously and IGNORE the result.
     *
     * @param localName    Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context      Map of name, value pairs composing the context.
     * @throws org.ofbiz.service.GenericServiceException
     *
     */
    public void runSyncIgnore(String localName, ModelService modelService, Map context) throws GenericServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     *
     * @param localName    Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context      Map of name, value pairs composing the context.
     * @param requester    Object implementing GenericRequester interface which will receive the result.
     * @param persist      True for store/run; False for run.
     * @throws org.ofbiz.service.GenericServiceException
     *
     */
    public void runAsync(String localName, ModelService modelService, Map context, GenericRequester requester, boolean persist) throws GenericServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Run the service asynchronously and IGNORE the result.
     *
     * @param localName    Name of the LocalDispatcher.
     * @param modelService Service model object.
     * @param context      Map of name, value pairs composing the context.
     * @param persist      True for store/run; False for run.
     * @throws org.ofbiz.service.GenericServiceException
     *
     */
    public void runAsync(String localName, ModelService modelService, Map context, boolean persist) throws GenericServiceException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void run(ModelService model, Map context) {
        AdminInterface admin = SharkContainer.getAdminInterface();
        ExecutionAdministration exec = admin.getExecutionAdministration();
        try {
            exec.connect("admin", "ofbiz", null, null);
        } catch (BaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ConnectFailed connectFailed) {
            connectFailed.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        PersistentRequester req = RequesterFactory.getNewRequester("org.ofbiz.shark.requester.LoggingRequester");

        WfProcessMgr mgr = null;
        try {
            mgr = exec.getProcessMgr(this.getLocation(model), model.name);
        } catch (BaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        WfProcess proc = null;
        try {
            proc = mgr.create_process(req);
        } catch (BaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotEnabled notEnabled) {
            notEnabled.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidRequester invalidRequester) {
            invalidRequester.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RequesterRequired requesterRequired) {
            requesterRequired.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            proc.set_process_context(context);
        } catch (BaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidData invalidData) {
            invalidData.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UpdateNotAllowed updateNotAllowed) {
            updateNotAllowed.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            proc.start();
        } catch (BaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CannotStart cannotStart) {
            cannotStart.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (AlreadyRunning alreadyRunning) {
            alreadyRunning.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
