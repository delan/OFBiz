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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.mortbay.http.SocketListener;
import org.mortbay.http.SunJsseListener;
import org.mortbay.http.ajp.AJP13Listener;
import org.mortbay.jetty.Server;
import org.mortbay.util.Frame;
import org.mortbay.util.Log;
import org.mortbay.util.LogSink;
import org.mortbay.util.MultiException;
import org.mortbay.util.ThreadedServer;
import org.ofbiz.core.component.ComponentConfig;
import org.ofbiz.core.component.ComponentException;
import org.ofbiz.core.util.Debug;

/**
 * JettyContainer - Container implementation for Jetty
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision$
 * @since      2.2
 */
public class JettyContainer implements Container {
    
    public static final String module = JettyContainer.class.getName();    
    private Map servers = new HashMap();
    
    private void init(String configFile) throws ContainerException {
        // configure jetty logging
        Log log = Log.instance();
        log.disableLog();
        log.add(new Log4jSink());

        // get the containers
        ContainerConfig.ComponentContainer componentContainer = null;
        ContainerConfig.WebContainer webContainer = null;
        try { 
            componentContainer = ContainerConfig.getComponentContainer(configFile);
            webContainer = ContainerConfig.getWebContainer(configFile);
        } catch (ContainerException e) {
            throw new ContainerException(e);
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
                throw new ContainerException(e);                
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
                        throw new ContainerException(e);
                    }
                }                    
            }                        
        }                
    }
    
    private Server createServer(ContainerConfig.WebContainer.Server serverConfig) throws ContainerException {
        Server server = new Server();
        
        // configure the listeners
        Iterator listeners = serverConfig.listeners.iterator();
        while (listeners.hasNext()) {
            ContainerConfig.WebContainer.Server.Listener listenerConf = 
                    (ContainerConfig.WebContainer.Server.Listener) listeners.next();
            
            if ("default".equals(listenerConf.type)) {
                SocketListener listener = new SocketListener();
                setListenerOptions(listener, listenerConf);
                server.addListener(listener);                                               
            } else if ("sun-jsse".equals(listenerConf.type)) {
                SunJsseListener listener = new SunJsseListener();
                setListenerOptions(listener, listenerConf);
                listener.setKeystore(listenerConf.keystore);
                listener.setPassword(listenerConf.password);
                listener.setKeyPassword(listenerConf.keyPassword);
                server.addListener(listener);
            } else if ("ibm-jsse".equals(listenerConf.type)) {
                throw new ContainerException("Listener not supported yet [" + listenerConf.type + "]");
            } else if ("nio".equals(listenerConf.type)) {
                throw new ContainerException("Listener not supported yet [" + listenerConf.type + "]");
            } else if ("ajp13".equals(listenerConf.type)) {
                AJP13Listener listener = new AJP13Listener();
                setListenerOptions(listener, listenerConf);
                server.addListener(listener);                
            }                       
        }
        return server;
    }  
    
    private void setListenerOptions(ThreadedServer listener, ContainerConfig.WebContainer.Server.Listener listenerConf) throws ContainerException {
        if (listenerConf.host != null && listenerConf.host.length() > 0) {
            try {
                listener.setHost(listenerConf.host);
            } catch (UnknownHostException e) {
                throw new ContainerException(e);                       
            }
        }
        listener.setPort(listenerConf.port);
        listener.setMinThreads(listenerConf.minThreads);
        listener.setMaxThreads(listenerConf.maxThreads);
        listener.setMaxIdleTimeMs(listenerConf.maxIdleTime);            
    }  
    
    /**
     * @see org.ofbiz.core.start.StartupContainer#start(java.lang.String)
     */
    public boolean start(String configFile) throws ContainerException {        
        // start the server(s)
        this.init(configFile);
        if (servers != null) {
            Iterator i = servers.values().iterator();
            while (i.hasNext()) {
                Server server = (Server) i.next();
                try {
                    server.start();
                } catch (MultiException e) {                    
                    throw new ContainerException(e);
                }
            }
        }                                
        return true;
    }
        
    /**
     * @see org.ofbiz.core.start.StartupContainer#stop()
     */
    public void stop() throws ContainerException {
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

// taken from JettyPlus
class Log4jSink implements LogSink {

    private String _options;
    private transient boolean _started;
        
    public void setOptions(String filename) {
        _options=filename;
    }
       
    public String getOptions() {
        return _options;
    }
       
    public  void start() throws Exception {
        _started=true;
    }
       
    public  void stop() {    
        _started=false;
    }
   
    public boolean isStarted() {    
        return _started;
    }
       
    public  void log(String tag, Object msg, Frame frame, long time) {    
        String method = frame.getMethod();
        int lb = method.indexOf('(');
        int ld = (lb > 0) ? method.lastIndexOf('.', lb) : method.lastIndexOf('.');
        if (ld < 0) ld = lb;
        String class_name = (ld > 0) ? method.substring(0,ld) : method;
        
        Logger log = Logger.getLogger(class_name);

        Priority priority = Priority.INFO;

        if (Log.DEBUG.equals(tag)) {
            priority=Priority.DEBUG;
        } else if (Log.WARN.equals(tag) || Log.ASSERT.equals(tag)) {
            priority=Priority.ERROR;
        } else if (Log.FAIL.equals(tag)) {
            priority=Priority.FATAL;
        }
        
        if (!log.isEnabledFor(priority)) {
            return;
        }

        log.log("org.mortbay.util.Log4jSink", priority, "" + msg, null);
    }
    
    public  synchronized void log(String s) {
        Logger.getRootLogger().log("jetty.log4jSink", Priority.INFO, s, null);
    }
    
}
