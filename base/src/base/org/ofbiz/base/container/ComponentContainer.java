/*
 * $Id: ComponentContainer.java,v 1.2 2003/08/15 22:05:59 ajzeneski Exp $
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

import java.io.*;
import java.util.*;

import org.ofbiz.base.component.*;
import org.ofbiz.base.start.*;
import org.ofbiz.base.util.*;

/**
 * ComponentContainer - StartupContainer implementation for Components
 * 
 * Example ofbiz-container.xml configuration:
 * <pre>
 *   <container name="component-container" class="org.ofbiz.base.component.ComponentContainer">
 *     <property name="[component-name]" value="[path-to-component-configuration]"/>
 *   </container>
 * </pre>
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.2 $
 * @since      2.2
 */
public class ComponentContainer implements Container {
    
    public static final String module = ComponentContainer.class.getName();
    
    protected List loadedComponents = null;
    protected Classpath classPath = null;

    /**
     * @see org.ofbiz.core.start.StartupContainer#start(java.lang.String)
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
        
        // get the components
        ContainerConfig.Container cc = ContainerConfig.getContainer("component-container", configFileLocation);        
        Iterator i = cc.properties.values().iterator();
        while (i.hasNext()) {
            ContainerConfig.Container.Property prop = (ContainerConfig.Container.Property) i.next();                        
            ComponentConfig config = null;
            try {
                config = ComponentConfig.getComponentConfig(prop.name, prop.value); 
            } catch (ComponentException e) {
                Debug.logError("Cannot load component : " + prop.name + " @ " + prop.value + " : " + e.getMessage(), module);                
            }
            if (config == null) {
                Debug.logError("Cannot load component : " + prop.name + " @ " + prop.value, module);
            } else {
                loadComponent(config);
            }                                       
        }
        
        // set the new classload on the current thread
        ClassLoader cl = classPath.getClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        
        return true;
    }
    
    private void loadComponent(ComponentConfig config) throws ContainerException {
        List classpathInfos = config.getClasspathInfos();
        String configRoot = config.getRootLocation();
        // set the root to have a trailing slash
        if (!configRoot.endsWith("/")) {
            configRoot = configRoot + "/";
        }
        if (classpathInfos != null) {
            Iterator cpi = classpathInfos.iterator();
            while (cpi.hasNext()) {
                ComponentConfig.ClasspathInfo cp = (ComponentConfig.ClasspathInfo) cpi.next();
                String location = cp.location;
                // set the location to not have a leading slash
                if (location.startsWith("/")) {
                    location = location.substring(1);
                }
                if ("dir".equals(cp.type)) {
                    classPath.addComponent(configRoot + location);
                } else if ("jar".equals(cp.type)) {
                    if (location.endsWith("/*")) {
                        // load the entire directory
                        String dirLoc = location.substring(0, location.length() - 1);
                        File path = new File(dirLoc);
                        if (path.isDirectory()) {
                            File files[] = path.listFiles();
                            for (int i = 0; i < files.length; i++) {
                                String file = files[i].getName();
                                if (file.endsWith(".jar") || file.endsWith(".zip")) {
                                    classPath.addComponent(files[i]);
                                }
                            }
                        } else {
                            Debug.logInfo("Location '" + configRoot + dirLoc + "' is not a directory; not loaded", module);
                        }
                    } else {
                        // load a single file
                        classPath.addComponent(configRoot + location);
                    }
                } else {
                    Debug.logError("Classpath type '" + cp.type + "' is not supported; '" + location + "' not loaded", module);                    
                }
            }
        }                
    }

    /**
     * @see org.ofbiz.core.start.StartupContainer#stop()
     */
    public void stop() throws ContainerException {        
    }    
}