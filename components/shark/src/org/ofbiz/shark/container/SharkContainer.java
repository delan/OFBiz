/*
 * $Id$
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
package org.ofbiz.shark.container;

import java.util.Properties;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.shark.requester.RequesterFactory;

import org.enhydra.shark.Shark;
import org.enhydra.shark.corba.SharkCORBAServer;
import org.enhydra.shark.api.client.wfservice.AdminInterface;
import org.enhydra.shark.api.client.wfservice.RepositoryMgr;
import org.enhydra.shark.api.client.wfservice.SharkConnection;
import org.enhydra.shark.api.client.wfservice.ExecutionAdministration;
import org.enhydra.shark.api.client.wfservice.ConnectFailed;
import org.enhydra.shark.api.client.wfservice.NotConnected;
import org.enhydra.shark.api.client.wfbase.BaseException;
import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.api.TransactionException;

/**
 * Shark Workflow Engine Container
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class SharkContainer implements Container, Runnable {

    public static final String module = SharkContainer.class.getName();

    private static GenericDelegator delegator = null;
    private static LocalDispatcher dispatcher = null;
    private static GenericValue adminUser = null;
    private static Shark shark = null;

    protected String configFile = null;
    private SharkCORBAServer corbaServer = null;
    private Thread orbThread = null;

    /**
     * @see org.ofbiz.base.container.Container#init(java.lang.String[], java.lang.String)
     */
    public void init(String[] args, String configFile) {
        this.configFile = configFile;
    }
    
    public boolean start() throws ContainerException {
        ContainerConfig.Container cfg = ContainerConfig.getContainer("shark-container", configFile);
        ContainerConfig.Container.Property dispatcherProp = cfg.getProperty("dispatcher-name");
        ContainerConfig.Container.Property delegatorProp = cfg.getProperty("delegator-name");
        ContainerConfig.Container.Property adminProp = cfg.getProperty("admin-user");
        ContainerConfig.Container.Property engineName = cfg.getProperty("engine-name");
        ContainerConfig.Container.Property iiopHost = cfg.getProperty("iiop-host");
        ContainerConfig.Container.Property iiopPort = cfg.getProperty("iiop-port");

        // check the required delegator-name property
        if (delegatorProp == null || delegatorProp.value == null || delegatorProp.value.length() == 0) {
            throw new ContainerException("Invalid delegator-name defined in container configuration");
        }

        // check the required dispatcher-name property
        if (dispatcherProp == null || dispatcherProp.value == null || dispatcherProp.value.length() == 0) {
            throw new ContainerException("Invalid dispatcher-name defined in container configuration");
        }

        // check the required admin-user property
        if (adminProp == null || adminProp.value == null || adminProp.value.length() == 0) {
            throw new ContainerException("Invalid admin-user defined in container configuration");
        }

        // check the required admin-user property
        if (engineName == null || engineName.value == null || engineName.value.length() == 0) {
            throw new ContainerException("Invalid engine-name defined in container configuration");
        }

        // get the delegator and dispatcher objects
        SharkContainer.delegator = GenericDelegator.getGenericDelegator(delegatorProp.value);
        try {
            SharkContainer.dispatcher = GenericDispatcher.getLocalDispatcher(dispatcherProp.value, SharkContainer.delegator);
        } catch (GenericServiceException e) {
            throw new ContainerException(e);
        }

        // get the admin user
        try {
            SharkContainer.adminUser = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", adminProp.value));
        } catch (GenericEntityException e) {
            throw new ContainerException(e);
        }

        // make sure the admin user exists
        if (SharkContainer.adminUser == null) {
            Debug.logWarning("Invalid admi-user; UserLogin not found not starting Shark!", module);
            return false;
        }

        // set the Shark configuration
        Properties props = UtilProperties.getProperties("shark.properties");
        Shark.configure(props);

        SharkContainer.shark = Shark.getInstance();
        Debug.logInfo("Started Shark workflow service", module);

        // create the CORBA server and bind to iiop
        if (iiopHost != null && iiopHost.value != null && iiopHost.value.length() > 0) {
            if (iiopPort != null && iiopPort.value != null && iiopPort.value.length() > 0) {
                try {
                    corbaServer = new SharkCORBAServer(engineName.value, iiopHost.value, iiopPort.value, shark);
                    orbThread = new Thread(this);
                    orbThread.setDaemon(false);
                    orbThread.setName(this.getClass().getName());
                    orbThread.start();                    
                    Debug.logInfo("Started Shark CORBA service", module);
                } catch (IllegalArgumentException e) {
                    throw new ContainerException(e);
                } catch (GeneralRuntimeException e) {
                    throw new ContainerException(e);
                }
            }
        }

        // restore the persisted requesters
        int restored = RequesterFactory.restoreRequesters(adminUser);
        Debug.logInfo("Restored persisted requesters [" + restored + "]", module);

        // re-eval current assignments
        ExecutionAdministration exAdmin = SharkContainer.getAdminInterface().getExecutionAdministration();
        try {
            exAdmin.connect(adminUser.getString("userLoginId"), adminUser.getString("currentPassword"), null, null);
            exAdmin.reevaluateAssignments();
            exAdmin.disconnect();
        } catch (ConnectFailed e) {
            throw new ContainerException(e);
        } catch (NotConnected e) {
            throw new ContainerException(e);
        } catch (BaseException e) {
            throw new ContainerException(e);
        }

        return true;
    }

    public void run() {
        try {
            corbaServer.startCORBAServer();
        } catch (BaseException e) {
            throw new GeneralRuntimeException(e);
        }
    }

    public void stop() throws ContainerException {
        // shut down the dispatcher
        if (dispatcher != null) {
            dispatcher.deregister();
        }

        // shutdown the corba server
        if (corbaServer != null) {
            corbaServer.shutdownORB();
        }
        Debug.logInfo("stop Shark", module);
    }

    // static helper methods
    public static GenericDelegator getDelegator() {
        return SharkContainer.delegator;
    }

    public static LocalDispatcher getDispatcher() {
        return SharkContainer.dispatcher;
    }

    public static AdminInterface getAdminInterface() {
        return shark.getAdminInterface();
    }

    public static RepositoryMgr getRepositoryMgr() {
        return shark.getRepositoryManager();
    }

    public static SharkConnection getSharkConntection() {
        return shark.getSharkConnection();
    }

    public static SharkTransaction getTransaction() throws TransactionException {
        return shark.createTransaction();
    }
}