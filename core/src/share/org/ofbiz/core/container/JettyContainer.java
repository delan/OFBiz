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

import org.mortbay.http.*;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.*;
import org.mortbay.util.MultiException;

import org.ofbiz.core.util.*;
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
    
    private Server server = null;
    
    /**
     * @see org.ofbiz.core.start.StartupContainer#start(java.lang.String)
     */
    public boolean start(String configFile) throws StartupException {
        server = new Server();
        SocketListener listener = new SocketListener();
        listener.setPort(8080);
        listener.setMinThreads(5);
        listener.setMaxThreads(25);
        server.addListener(listener);
        
        try {
            server.addWebApplication("webtools", "./webtools/webapp/");
        } catch (IOException e) {
            throw new StartupException(e);
        }
        
        try {
            server.start();
        } catch (MultiException e) {
            throw new StartupException(e);            
        }
        
        try {
            server.addWebApplication("ecommerce", "./ecommerce/webapp/");
        } catch (IOException e) {
            throw new StartupException(e);
        }
        
        try {
            Method shutdownHook = java.lang.Runtime.class.getMethod("addShutdownHook",new Class[] {java.lang.Thread.class});
            Thread hook = new Thread() {
                public void run() {                
                    setName("OFBiz_Shutdown_Hook");
                    Debug.log("OFBiz Shutdown Hook Executing...");
                    try {
                        server.stop();
                    } catch (Exception e) {
                        Debug.logError(e, module);
                    }
                     
                    // Try to avoid JVM crash
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Debug.logWarning(e, module);
                    }
                }
            };
            
            shutdownHook.invoke(Runtime.getRuntime(), new Object[]{hook});
        } catch(Exception e) {                            
            Debug.log("VM Does not support shutdown hook", module);
        }        
                
        return true;
    }

}
