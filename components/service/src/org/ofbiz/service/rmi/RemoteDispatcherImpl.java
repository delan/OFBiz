/*
 * $Id: RemoteDispatcherImpl.java,v 1.1 2003/12/02 06:39:32 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.service.rmi;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.GenericRequester;
import org.ofbiz.service.GenericResultWaiter;
import org.ofbiz.service.LocalDispatcher;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Generic Services Remote Dispatcher Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class RemoteDispatcherImpl extends UnicastRemoteObject implements RemoteDispatcher {

    public static final String module = RemoteDispatcherImpl.class.getName();

    protected LocalDispatcher dispatcher = null;

    RemoteDispatcherImpl(LocalDispatcher dispatcher, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(0, csf, ssf);
        this.dispatcher = dispatcher;
    }

    // RemoteDispatcher methods

    public Map runSync(String serviceName, Map context) throws GenericServiceException, RemoteException {
        return dispatcher.runSync(serviceName, context);
    }

    public void runSyncIgnore(String serviceName, Map context) throws GenericServiceException, RemoteException {
        dispatcher.runSyncIgnore(serviceName, context);
    }

    public void runAsync(String serviceName, Map context, GenericRequester requester, boolean persist) throws GenericServiceException, RemoteException {
        dispatcher.runAsync(serviceName, context, requester, persist);
    }

    public void runAsync(String serviceName, Map context, GenericRequester requester) throws GenericServiceException, RemoteException {
        dispatcher.runAsync(serviceName, context, requester);
    }

    public void runAsync(String serviceName, Map context, boolean persist) throws GenericServiceException, RemoteException {
        dispatcher.runAsync(serviceName, context, persist);
    }

    public void runAsync(String serviceName, Map context) throws GenericServiceException, RemoteException {
        dispatcher.runAsync(serviceName, context);
    }

    public GenericResultWaiter runAsyncWait(String serviceName, Map context, boolean persist) throws GenericServiceException, RemoteException {
        return dispatcher.runAsyncWait(serviceName, context, persist);
    }

    public GenericResultWaiter runAsyncWait(String serviceName, Map context) throws GenericServiceException, RemoteException {
        return dispatcher.runAsyncWait(serviceName, context);
    }

    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count, long endTime) throws GenericServiceException, RemoteException {
        dispatcher.schedule(serviceName, context, startTime, frequency, interval, count, endTime);
    }

    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, int count) throws GenericServiceException, RemoteException {
        dispatcher.schedule(serviceName, context, startTime, frequency, interval, count);
    }

    public void schedule(String serviceName, Map context, long startTime, int frequency, int interval, long endTime) throws GenericServiceException, RemoteException {
        dispatcher.schedule(serviceName, context, startTime, frequency, interval, endTime);
    }

    public void schedule(String serviceName, Map context, long startTime) throws GenericServiceException, RemoteException {
        dispatcher.schedule(serviceName, context, startTime);
    }

    public void deregister() {
        dispatcher.deregister();
    }

}
