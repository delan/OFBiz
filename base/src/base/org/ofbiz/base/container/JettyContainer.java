/*
 * $Id: JettyContainer.java,v 1.4 2003/08/17 01:44:14 ajzeneski Exp $
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
package org.ofbiz.base.container;

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
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;

/**
 * JettyContainer - Container implementation for Jetty
 * This container depends on the ComponentContainer as well.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.4 $
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

        // get the container
        ContainerConfig.Container cc = ContainerConfig.getContainer("component-container", configFile);
        ContainerConfig.Container jc = ContainerConfig.getContainer("jetty-container", configFile);
                        
        // create the servers
        Iterator sci = jc.properties.values().iterator();
        while (sci.hasNext()) {
            ContainerConfig.Container.Property prop = (ContainerConfig.Container.Property) sci.next();
            servers.put(prop.name, createServer(prop));                                   
        }
        
        // load the applications
        Iterator components = cc.properties.values().iterator();
        while (components.hasNext()) {
            ContainerConfig.Container.Property prop = (ContainerConfig.Container.Property) components.next();
            ComponentConfig component = null;
            try {
                component = ComponentConfig.getComponentConfig(prop.name, prop.value);
            } catch (ComponentException e) {
                Debug.logError("Unable to load component application [" + prop.name + "] component not found", module);                               
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
                        Debug.logError(e, "Problem mounting application [" + appInfo.name + " / " + appInfo.location + "]", module);                        
                    }
                }                    
            }                        
        }                
    }
    
    private Server createServer(ContainerConfig.Container.Property serverConfig) throws ContainerException {
        Server server = new Server();
        
        // configure the listeners
        Iterator listeners = serverConfig.properties.values().iterator();
        while (listeners.hasNext()) {
            ContainerConfig.Container.Property listenerProps = 
                    (ContainerConfig.Container.Property) listeners.next();
            
            Debug.logInfo(listenerProps.name + " = " + listenerProps.value, module);
            if ("default".equals(listenerProps.getProperty("type").value)) {
                SocketListener listener = new SocketListener();
                setListenerOptions(listener, listenerProps);
                if (listenerProps.getProperty("low-resource-persist-time") != null) {
                    int value = 0;
                    try {
                        value = Integer.parseInt(listenerProps.getProperty("low-resource-persist-time").value);
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    if (value > 0) {
                        listener.setLowResourcePersistTimeMs(value);
                    }
                }                
                server.addListener(listener);                                               
            } else if ("sun-jsse".equals(listenerProps.getProperty("type").value)) {
                SunJsseListener listener = new SunJsseListener();
                setListenerOptions(listener, listenerProps);
                if (listenerProps.getProperty("keystore") != null) {
                    listener.setKeystore(listenerProps.getProperty("keystore").value);    
                }
                if (listenerProps.getProperty("password") != null) {
                    listener.setKeystore(listenerProps.getProperty("password").value);    
                }                
                if (listenerProps.getProperty("key-password") != null) {
                    listener.setKeystore(listenerProps.getProperty("key-password").value);    
                }
                if (listenerProps.getProperty("low-resource-persist-time") != null) {
                    int value = 0;
                    try {
                        value = Integer.parseInt(listenerProps.getProperty("low-resource-persist-time").value);
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    if (value > 0) {
                        listener.setLowResourcePersistTimeMs(value);
                    }
                }                                               
                server.addListener(listener);
            } else if ("ibm-jsse".equals(listenerProps.getProperty("type").value)) {
                throw new ContainerException("Listener not supported yet [" + listenerProps.getProperty("type").value + "]");
            } else if ("nio".equals(listenerProps.getProperty("type").value)) {
                throw new ContainerException("Listener not supported yet [" + listenerProps.getProperty("type").value + "]");
            } else if ("ajp13".equals(listenerProps.getProperty("type").value)) {
                AJP13Listener listener = new AJP13Listener();
                setListenerOptions(listener, listenerProps);
                server.addListener(listener);                
            }                       
        }
        return server;
    }  
    
    private void setListenerOptions(ThreadedServer listener, ContainerConfig.Container.Property listenerProps) throws ContainerException {
        if (listenerProps.getProperty("host") != null) {
            try {
                listener.setHost(listenerProps.getProperty("host").value);
            } catch (UnknownHostException e) {
                throw new ContainerException(e);                       
            }
        } else {
            try {
                listener.setHost("0.0.0.0");
            } catch (UnknownHostException e) {
                throw new ContainerException(e);
            }          
        }
        
        if (listenerProps.getProperty("port") != null) {
            int value = 8080;
            try {
                value = Integer.parseInt(listenerProps.getProperty("port").value);
            } catch (NumberFormatException e) {
                value = 8080;
            }
            if (value == 0) value = 8080;
            
            listener.setPort(value);
        } else {
            listener.setPort(8080);
        }
        
        if (listenerProps.getProperty("min-threads") != null) {
            int value = 0;
            try {
                value = Integer.parseInt(listenerProps.getProperty("min-threads").value);
            } catch (NumberFormatException e) {
                value = 0;
            }
            if (value > 0) {
                listener.setMinThreads(value);
            }
        }
        
        if (listenerProps.getProperty("max-threads") != null) {
            int value = 0;
            try {
                value = Integer.parseInt(listenerProps.getProperty("max-threads").value);
            } catch (NumberFormatException e) {
                value = 0;
            }
            if (value > 0) {
                listener.setMaxThreads(value);
            }
        }
        
        if (listenerProps.getProperty("max-idle-time") != null) {
            int value = 0;
            try {
                value = Integer.parseInt(listenerProps.getProperty("max-idle-time").value);
            } catch (NumberFormatException e) {
                value = 0;
            }
            if (value > 0) {
                listener.setMaxIdleTimeMs(value);
            }
        }                                   
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
       
    public void start() throws Exception {
        _started=true;
    }
       
    public void stop() {    
        _started=false;
    }
   
    public boolean isStarted() {    
        return _started;
    }
       
    public void log(String tag, Object msg, Frame frame, long time) {    
        String method = frame.getMethod();
        int lb = method.indexOf('(');
        int ld = (lb > 0) ? method.lastIndexOf('.', lb) : method.lastIndexOf('.');
        if (ld < 0) ld = lb;
        String class_name = (ld > 0) ? method.substring(0,ld) : method;
        
        Logger log = Logger.getLogger(class_name);

        Priority priority = Priority.INFO;

        if (Log.DEBUG.equals(tag)) {
            priority = Priority.DEBUG;
        } else if (Log.WARN.equals(tag) || Log.ASSERT.equals(tag)) {
            priority = Priority.ERROR;
        } else if (Log.FAIL.equals(tag)) {
            priority = Priority.FATAL;
        }
        
        if (!log.isEnabledFor(priority)) {
            return;
        }

        log.log("jetty.log4jSink", priority, "" + msg, null);
    }
    
    public synchronized void log(String s) {
        Logger.getRootLogger().log("jetty.log4jSink", Priority.INFO, s, null);
    }    
}
