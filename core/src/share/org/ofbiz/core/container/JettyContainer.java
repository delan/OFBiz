/*
 * $Id$
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
package org.ofbiz.core.container;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mortbay.http.*;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.*;
import org.mortbay.util.MultiException;

import org.ofbiz.core.util.*;
import org.ofbiz.core.component.ComponentConfig;
import org.ofbiz.core.component.ComponentException;
import org.ofbiz.core.start.StartupException;
import org.ofbiz.core.start.StartupContainer;

/**
 * JettyContainer - StartupContainer implementation for Jetty
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision$
 * @since      2.2
 */
public class JettyContainer implements StartupContainer {
    
    public static final String module = JettyContainer.class.getName();    
    private Map servers = new HashMap();
    
    private void init(String configFile) throws StartupException {
        ContainerConfig.ComponentContainer componentContainer = null;
        ContainerConfig.WebContainer webContainer = null;
        try { 
            componentContainer = ContainerConfig.getComponentContainer(configFile);
            webContainer = ContainerConfig.getWebContainer(configFile);
        } catch (ContainerException e) {
            throw new StartupException(e);
        }
        
        // create the servers
        Iterator sci = webContainer.servers.iterator();
        while (sci.hasNext()) {
            ContainerConfig.WebContainer.Server sc = (ContainerConfig.WebContainer.Server) sci.next();
            servers.put(sc.name, createServer(sc));                           
        }
        
        // load the applications
        Iterator components = componentContainer.components.iterator();
        while (components.hasNext()) {
            ContainerConfig.ComponentContainer.Component comp = (ContainerConfig.ComponentContainer.Component) components.next();
            ComponentConfig component = null;
            try {
                component = ComponentConfig.getComponentConfig(comp.name, comp.config);
            } catch (ComponentException e) {
                throw new StartupException(e);                
            }
            Iterator appInfos = component.getWebappInfos().iterator();
            while (appInfos.hasNext()) {
                ComponentConfig.WebappInfo appInfo = (ComponentConfig.WebappInfo) appInfos.next();
                Server server = (Server) servers.get(appInfo.server);
                if (server == null) {
                    Debug.logWarning("Server with name [" + appInfo.server + "] not found; not mounting [" + appInfo.name + "]", module);
                } else {
                    try {
                        String location = appInfo.location;
                        if (!location.endsWith("/")) {
                            location = location + "/";
                        }
                        server.addWebApplication(appInfo.mountPoint, location);
                    } catch (IOException e) {                        
                        throw new StartupException(e);
                    }
                }                    
            }                        
        }                
    }
    
    private Server createServer(ContainerConfig.WebContainer.Server serverConfig) throws StartupException {
        Server server = new Server();
        
        // configure the listeners
        Iterator listeners = serverConfig.listeners.iterator();
        while (listeners.hasNext()) {
            ContainerConfig.WebContainer.Server.Listener listenerConf = 
                    (ContainerConfig.WebContainer.Server.Listener) listeners.next();
            
            if ("default".equals(listenerConf.type)) {
                SocketListener listener = new SocketListener();
                if (listenerConf.host != null && listenerConf.host.length() > 0) {
                    try {
                        listener.setHost(listenerConf.host);
                    } catch (UnknownHostException e) {
                        throw new StartupException(e);                       
                    }
                }
                listener.setPort(listenerConf.port);
                listener.setMinThreads(listenerConf.minThreads);
                listener.setMaxThreads(listenerConf.maxThreads);
                listener.setMaxIdleTimeMs(listenerConf.maxIdleTime);
                server.addListener(listener);                                               
            } else if ("sun-jsse".equals(listenerConf.type)) {
                throw new StartupException("Listener not supported yet [" + listenerConf.type + "]");
            } else if ("ibm-jsse".equals(listenerConf.type)) {
                throw new StartupException("Listener not supported yet [" + listenerConf.type + "]");
            } else if ("nio".equals(listenerConf.type)) {
                throw new StartupException("Listener not supported yet [" + listenerConf.type + "]");
            } else if ("ajp13".equals(listenerConf.type)) {
                throw new StartupException("Listener not supported yet [" + listenerConf.type + "]");
            }                       
        }
        return server;
    }    
    
    /**
     * @see org.ofbiz.core.start.StartupContainer#start(java.lang.String)
     */
    public boolean start(String configFile) throws StartupException {        
        // start the server(s)
        this.init(configFile);
        if (servers != null) {
            Iterator i = servers.values().iterator();
            while (i.hasNext()) {
                Server server = (Server) i.next();
                try {
                    server.start();
                } catch (MultiException e) {                    
                    throw new StartupException(e);
                }
            }
        }                                
        return true;
    }
        
    /**
     * @see org.ofbiz.core.start.StartupContainer#stop()
     */
    public void stop() throws StartupException {
        if (servers != null) {
            Iterator i = servers.values().iterator();
            while(i.hasNext()) {
                Server server = (Server) i.next();
                try {
                    server.stop();
                } catch (InterruptedException e) {
                    Debug.logWarning(e, module);                    
                }
            }
        }
    }              
}
