/*
 * $Id: ComponentContainer.java,v 1.11 2003/08/20 23:46:27 ajzeneski Exp $
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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.component.ComponentLoaderConfig;
import org.ofbiz.base.start.Classpath;
import org.ofbiz.base.util.Debug;

/**
 * ComponentContainer - StartupContainer implementation for Components
 * 
 * Example ofbiz-container.xml configuration:
 * <pre>
 *   <container name="component-container" class="org.ofbiz.base.component.ComponentContainer"/>
 * </pre>
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.11 $
 * @since      3.0
 */
public class ComponentContainer implements Container {
    
    public static final String module = ComponentContainer.class.getName();   
    
    protected List loadedComponents = null;
    protected Classpath classPath = null;   

    /**
     * @see org.ofbiz.base.start.StartupContainer#start(java.lang.String)
     */
    public boolean start(String configFileLocation) throws ContainerException {
        if (classPath == null) {
            classPath = new Classpath(System.getProperty("java.class.path"));    
        } 
        if (loadedComponents == null) {
            loadedComponents = new LinkedList();
        } else {
            throw new ContainerException("Components already loaded, cannot start");
        }
        
                                      
        // get the config for this container
        ContainerConfig.Container cc = ContainerConfig.getContainer("component-container", configFileLocation);
        
        // check for an override loader config
        String loaderConfig = null;
        if (cc.getProperty("loader-config") != null) {
            loaderConfig = cc.getProperty("loader-config").value;
        }
                
        // get the components to load
        List components = null;
        try {            
            components = ComponentLoaderConfig.getComponentsToLoad(loaderConfig);
        } catch (ComponentException e) {
            throw new ContainerException(e);            
        }
                       
        // load each component
        if (components != null) {
            Iterator ci = components.iterator();
            while (ci.hasNext()) {
                ComponentLoaderConfig.ComponentDef def = (ComponentLoaderConfig.ComponentDef) ci.next();                
                if (def.type == ComponentLoaderConfig.SINGLE_COMPONENT) {
                    ComponentConfig config = null;
                    try {
                        config = ComponentConfig.getComponentConfig(def.name, def.location);
                    } catch (ComponentException e) {
                        Debug.logError("Cannot load component : " + def.name + " @ " + def.location + " : " + e.getMessage(), module);    
                    }
                    if (config == null) {
                        Debug.logError("Cannot load component : " + def.name + " @ " + def.location, module);   
                    } else {
                        loadComponent(config);
                    }                   
                } else if (def.type == ComponentLoaderConfig.COMPONENT_DIRECTORY) {
                    loadComponentDirectory(def.location);    
                }                                
            }
        }

        // set the new classloader on the current thread
        System.setProperty("java.class.path", classPath.toString());
        ClassLoader cl = classPath.getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);        
        
        return true;
    }
    
    private void loadComponentDirectory(String directoryName) throws ContainerException {
        Debug.logInfo("Loading component directory [" + directoryName + "]", module);
        File parentPath = new File(directoryName);
        if (!parentPath.exists() || !parentPath.isDirectory()) {
            Debug.logError("Auto-Load Component directory not found : " + directoryName, module);
        } else {
            String subs[] = parentPath.list();
            for (int i = 0; i < subs.length; i++) {
                try {
                    File componentPath = new File(parentPath.getCanonicalPath() + "/" + subs[i]);
                    if (componentPath.isDirectory() && !subs[i].equals("CVS")) {                        
                        // make sure we have a component configuraton file
                        String componentLocation = componentPath.getCanonicalPath();
                        File configFile = new File(componentLocation + "/ofbiz-component.xml");                        
                        if (configFile.exists()) {
                            ComponentConfig config = null;
                            try {
                                config = ComponentConfig.getComponentConfig(componentPath.getName(), componentLocation);
                            } catch (ComponentException e) {
                                Debug.logError("Cannot load component : " + componentPath.getName() + " @ " + componentLocation + " : " + e.getMessage(), module);    
                            }
                            if (config == null) {
                                Debug.logError("Cannot load component : " + componentPath.getName() + " @ " + componentLocation, module);    
                            } else {
                                loadComponent(config);                                
                            }                          
                        }
                    }
                } catch (IOException ioe) {
                    Debug.logError(ioe, module);
                }
            }            
        }                           
    }
    
    private void loadComponent(ComponentConfig config) throws ContainerException {
        List classpathInfos = config.getClasspathInfos();
        String configRoot = config.getRootLocation();
        configRoot = configRoot.replace('\\', '/');
        // set the root to have a trailing slash
        if (!configRoot.endsWith("/")) {
            configRoot = configRoot + "/";
        }
        if (classpathInfos != null) {
            Iterator cpi = classpathInfos.iterator();
            while (cpi.hasNext()) {
                ComponentConfig.ClasspathInfo cp = (ComponentConfig.ClasspathInfo) cpi.next();
                String location = cp.location.replace('\\', '/');
                // set the location to not have a leading slash
                if (location.startsWith("/")) {
                    location = location.substring(1);
                }
                if ("dir".equals(cp.type)) {                    
                    classPath.addComponent(configRoot + location);
                } else if ("jar".equals(cp.type)) {
                    String dirLoc = location;
                    if (dirLoc.endsWith("/*")) {
                        // strip off the slash splat                        
                        dirLoc = location.substring(0, location.length() - 2);
                    }                    
                    File path = new File(configRoot + dirLoc);
                    if (path.exists()) {
                        if (path.isDirectory()) {
                            // load all .jar and .zip files in this directory
                            File files[] = path.listFiles();
                            for (int i = 0; i < files.length; i++) {
                                String file = files[i].getName();
                                if (file.endsWith(".jar") || file.endsWith(".zip")) {                                    
                                    classPath.addComponent(files[i]);
                                }
                            }
                        } else {
                            // add a single file                                                       
                            classPath.addComponent(configRoot + location);    
                        }
                    } else {                                               
                        Debug.logWarning("Location '" + configRoot + dirLoc + "' does not exist", module);
                    }
                } else {
                    Debug.logError("Classpath type '" + cp.type + "' is not supported; '" + location + "' not loaded", module);                    
                }
            }
        }                
    }
    
    /**
     * @see org.ofbiz.base.start.StartupContainer#stop()
     */
    public void stop() throws ContainerException {        
    }    
}