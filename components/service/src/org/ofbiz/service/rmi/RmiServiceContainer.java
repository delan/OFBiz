/*
 * $Id: RmiServiceContainer.java,v 1.1 2003/12/02 06:39:32 ajzeneski Exp $
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

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.service.*;
import org.ofbiz.entity.GenericDelegator;

import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

/**
 * RMI Service Engine Container / Dispatcher
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class RmiServiceContainer implements Container {

    public static final String module = RmiServiceContainer.class.getName();

    protected RemoteDispatcherImpl remote = null;
    protected String name = null;

    // Container methods

    public boolean start(String configFile) throws ContainerException {
        // get the container config
        ContainerConfig.Container cfg = ContainerConfig.getContainer("rmi-dispatcher", configFile);
        ContainerConfig.Container.Property lookupNameProp = cfg.getProperty("lookup-name");
        ContainerConfig.Container.Property delegatorProp = cfg.getProperty("delegator-name");
        ContainerConfig.Container.Property clientProp = cfg.getProperty("client-factory");
        ContainerConfig.Container.Property serverProp = cfg.getProperty("server-factory");

        // check the required lookup-name property
        if (lookupNameProp == null || lookupNameProp.value == null || lookupNameProp.value.length() == 0) {
            throw new ContainerException("Invalid lookup-name defined in container configuration");
        } else {
            this.name = lookupNameProp.value;
        }

        // check the required delegator-name property
        if (delegatorProp == null || delegatorProp.value == null || delegatorProp.value.length() == 0) {
            throw new ContainerException("Invalid delegator-name defined in container configuration");
        }

        // setup the factories
        RMIClientSocketFactory csf = null;
        RMIServerSocketFactory ssf = null;

        // get the classloader
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // load the factories
        if (clientProp != null && clientProp.value != null && clientProp.value.length() > 0) {
            try {
                Class c = loader.loadClass(clientProp.value);
                csf = (RMIClientSocketFactory) c.newInstance();
            } catch (Exception e) {
                throw new ContainerException(e);
            }
        }
        if (serverProp != null && serverProp.value != null && serverProp.value.length() > 0) {
            try {
                Class c = loader.loadClass(serverProp.value);
                ssf = (RMIServerSocketFactory) c.newInstance();
            } catch (Exception e) {
                throw new ContainerException(e);
            }
        }

        // get the delegator for this container
        GenericDelegator delegator = GenericDelegator.getGenericDelegator(delegatorProp.value);

        // create the LocalDispatcher
        LocalDispatcher dispatcher = new GenericDispatcher(name, delegator);

        // create the RemoteDispatcher
        try {
            remote = new RemoteDispatcherImpl(dispatcher, csf, ssf);
        } catch (RemoteException e) {
            throw new ContainerException("Unable to start the RMI dispatcher", e);
        }

        // bind this object in JNDI
        try {
            Naming.rebind(name, remote);
        } catch (RemoteException e) {
            throw new ContainerException("Unable to bind object", e);
        } catch (java.net.MalformedURLException e) {
            throw new ContainerException("Problem binding JNDI name", e);
        }

        return true;
    }

    public void stop() throws ContainerException {
        remote.deregister();
    }
}
