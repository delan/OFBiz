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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.core.start.StartupException;
import org.ofbiz.core.start.StartupLoader;

/**
 * ContainerLoader - StartupLoader for the container
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision$
 * @since      2.2
 */
public class ContainerLoader implements StartupLoader {
    
    public static final String module = ContainerLoader.class.getName();
    public static final String OFBIZ_CONFIG = "ofbiz.xml";
    
    protected List containers = new LinkedList();    

    /**
     * @see org.ofbiz.core.start.StartupLoader#load(java.lang.String)
     */
    public void load(String args[]) throws StartupException {        
        // get the master ofbiz configuration file
        String configFileLocation = null;
        if (args[1] != null) {
            configFileLocation = args[1];
        } else {
            configFileLocation = System.getProperty("ofbiz.home") + OFBIZ_CONFIG;   
        }
        
        ContainerConfig.ComponentContainer componentContainer = null;
        ContainerConfig.WebContainer webContainer = null;
        try { 
            componentContainer = ContainerConfig.getComponentContainer(configFileLocation);
            webContainer = ContainerConfig.getWebContainer(configFileLocation);
        } catch (ContainerException e) {
            throw new StartupException(e);                        
        }
        
        if (componentContainer == null || componentContainer.containerClass == null) {
            throw new StartupException("Cannot locate component containter to load");
        }
        if (webContainer == null || webContainer.containerClass == null) {
            throw new StartupException("Cannot locate web container to load");
        }

        // load and cache the containers so we can stop then when needed
        containers.add(loadContainer(componentContainer.containerClass, configFileLocation));
        containers.add(loadContainer(webContainer.containerClass, configFileLocation));                
    }

    /**
     * @see org.ofbiz.core.start.StartupLoader#unload()
     */
    public void unload() throws StartupException {
        Iterator i = containers.iterator();
        while (i.hasNext()) {
            Container container = (Container) i.next();
            try {
                container.stop();
            } catch (ContainerException e) {
                throw new StartupException(e);                
            }
        }              
    }

    private Container loadContainer(String classname, String configFileLocation) throws StartupException {
        // load the component container class
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        Class componentClass = null;
        try {
            componentClass = loader.loadClass(classname);
        } catch (ClassNotFoundException e) {
            throw new StartupException("Cannot locate container class", e);            
        }
        if (componentClass == null) {
            throw new StartupException("Component container class not loaded");
        }
        
        Container componentObj = null;
        try {
            componentObj = (Container) componentClass.newInstance();
        } catch (InstantiationException e) {
            throw new StartupException(e);            
        } catch (IllegalAccessException e) {
            throw new StartupException(e);            
        } catch (ClassCastException e) {
            throw new StartupException(e);
        }
        
        if (componentObj == null) {
            throw new StartupException("Unable to create instance of component container");
        }
        
        try {
            componentObj.start(configFileLocation);
        } catch (ContainerException e) {
            throw new StartupException(e);
        }  
        
        return componentObj;
    }    
}
