/*
 * $Id: ContainerLoader.java,v 1.8 2004/03/30 22:35:09 ajzeneski Exp $
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.start.StartupException;
import org.ofbiz.base.start.StartupLoader;
import org.ofbiz.base.start.Start;
import org.ofbiz.base.util.Debug;

/**
 * ContainerLoader - StartupLoader for the container
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.8 $
 * @since      3.0
 */
public class ContainerLoader implements StartupLoader {
    
    public static final String module = ContainerLoader.class.getName();
    public static final String CONTAINER_CONFIG = "ofbiz-containers.xml";
    
    protected List loadedContainers = new LinkedList();    

    /**
     * @see org.ofbiz.base.start.StartupLoader#load(Start.Config, String[])
     */
    public void load(Start.Config config, String args[]) throws StartupException {
        Debug.logInfo("[Startup] Loading ContainerLoader...", module);
              
        // get the master container configuration file
        String configFileLocation = config.containerConfig;
        
        Collection containers = null;
        try {
            containers = ContainerConfig.getContainers(configFileLocation);
        } catch (ContainerException e) {            
            throw new StartupException(e);
        }

        if (containers != null) {
            Iterator i = containers.iterator();
            while (i.hasNext()) {
                ContainerConfig.Container containerCfg = (ContainerConfig.Container) i.next();                
                loadedContainers.add(loadContainer(containerCfg.className, configFileLocation));
            }
        }                                    
    }

    /**
     * @see org.ofbiz.base.start.StartupLoader#unload()
     */
    public void unload() throws StartupException {
        Debug.logInfo("Shutting down containers", module);
        // shutting down in reverse order
        for (int i = loadedContainers.size(); i > 0; i--) {
            Container container = (Container) loadedContainers.get(i-1);
            try {
                container.stop();
            } catch (ContainerException e) {
                Debug.logError(e, module);
            }
        }
    }

    private Container loadContainer(String classname, String configFileLocation) throws StartupException {
        // load the component container class
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            Debug.logWarning("Unable to get context classloader; using system", module);
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
