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
package org.ofbiz.core.component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import org.ofbiz.core.util.*;

/**
 * ComponentConfig - Component configuration class for ofbiz-container.xml
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.2
 */
public class ComponentConfig {
    
    public static final String module = ComponentConfig.class.getName();
    public static final String OFBIZ_COMPONENT_XML_FILENAME = "ofbiz-component.xml";

    // this is not a UtilCache because reloading may cause problems
    protected static Map componentConfigs = new OrderedMap();

    public static ComponentConfig getComponentConfig(String globalName) throws ComponentException {
        return getComponentConfig(globalName, null);
    }
    
    public static ComponentConfig getComponentConfig(String globalName, String rootLocation) throws ComponentException {
        ComponentConfig componentConfig = (ComponentConfig) componentConfigs.get(globalName);
        if (componentConfig == null) {
            if (rootLocation != null) {
                synchronized (ComponentConfig.class) {
                    componentConfig = (ComponentConfig) componentConfigs.get(globalName);
                    if (componentConfig == null) {
                        componentConfig = new ComponentConfig(globalName, rootLocation);
                        componentConfigs.put(globalName, componentConfig);                        
                    }
                }
            } else {
                throw new ComponentException("No component found named : " + globalName);
            }
        }
        return componentConfig;
    }
        
    public static Collection getAllComponents() {
        return componentConfigs.values();
        
    }
    
    public static List getAllClasspathInfos() {
        List classpaths = new LinkedList();
        Iterator i = getAllComponents().iterator();
        while (i.hasNext()) {
            ComponentConfig cc = (ComponentConfig) i.next();
            classpaths.addAll(cc.getClasspathInfos());
        }
        return classpaths;
    } 
    
    public static List getAllEntityResourceInfos() {
        List entityInfos = new LinkedList();
        Iterator i = getAllComponents().iterator();
        while (i.hasNext()) {
            ComponentConfig cc = (ComponentConfig) i.next();
            entityInfos.addAll(cc.getEntityResourceInfos());
        }
        return entityInfos;
    }
    
    public static List getAllServiceResourceInfos() {
        List serviceInfos = new LinkedList();
        Iterator i = getAllComponents().iterator();
        while (i.hasNext()) {
            ComponentConfig cc = (ComponentConfig) i.next();
            serviceInfos.addAll(cc.getServiceResourceInfos());
        }
        return serviceInfos;        
        
    }
    
    public static List getAllWebappResourceInfos() {
        List webappInfos = new LinkedList();
        Iterator i = getAllComponents().iterator();
        while (i.hasNext()) {
            ComponentConfig cc = (ComponentConfig) i.next();
            webappInfos.addAll(cc.getWebappInfos());
        }
        return webappInfos;        
        
    }    

    // ========== component info fields ==========
    protected String globalName = null;
    protected String rootLocation = null;    
    protected String componentName = null;
    
    protected Map resourceLoaderInfos = new HashMap();
    protected List classpathInfos = new LinkedList();
    protected List entityResourceInfos = new LinkedList();
    protected List serviceResourceInfos = new LinkedList();
    protected List webappInfos = new LinkedList();

    protected ComponentConfig() {}
    
    protected ComponentConfig(String globalName, String rootLocation) throws ComponentException {
        this.globalName = globalName;
        this.rootLocation = rootLocation;
        
        File rootLocationDir = new File(rootLocation);
        if (rootLocationDir == null) {
            throw new ComponentException("The given component root location is does not exist: " + rootLocation);
        }
        if (!rootLocationDir.isDirectory()) {
            throw new ComponentException("The given component root location is not a directory: " + rootLocation);
        }
        
        String xmlFilename = rootLocation + "/" + OFBIZ_COMPONENT_XML_FILENAME;
        URL xmlUrl = UtilURL.fromFilename(xmlFilename);
        if (xmlUrl == null) {
            throw new ComponentException("Could not find the " + OFBIZ_COMPONENT_XML_FILENAME + " configuration file in the component root location: " + rootLocation);
        }
        
        Document ofbizComponentDocument = null;
        try {
            ofbizComponentDocument = UtilXml.readXmlDocument(xmlUrl, true);
        } catch (SAXException e) {
            throw new ComponentException("Error reading the component config file: " + xmlUrl, e);
        } catch (ParserConfigurationException e) {
            throw new ComponentException("Error reading the component config file: " + xmlUrl, e);
        } catch (IOException e) {
            throw new ComponentException("Error reading the component config file: " + xmlUrl, e);
        }
        
        Element ofbizComponentElement = ofbizComponentDocument.getDocumentElement();
        this.componentName = ofbizComponentElement.getAttribute("name");        
        Iterator elementIter = null;
        
        // resource-loader - resourceLoaderInfos
        elementIter = UtilXml.childElementList(ofbizComponentElement, "resource-loader").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            ResourceLoaderInfo resourceLoaderInfo = new ResourceLoaderInfo(curElement);
            this.resourceLoaderInfos.put(resourceLoaderInfo.name, resourceLoaderInfo);
        }
        
        // classpath - classpathInfos
        elementIter = UtilXml.childElementList(ofbizComponentElement, "classpath").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            ClasspathInfo classpathInfo = new ClasspathInfo(curElement);
            this.classpathInfos.add(classpathInfo);
        }
        
        // entity-resource - entityResourceInfos
        elementIter = UtilXml.childElementList(ofbizComponentElement, "entity-resource").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            EntityResourceInfo entityResourceInfo = new EntityResourceInfo(curElement);
            this.entityResourceInfos.add(entityResourceInfo);
        }
        
        // service-resource - serviceResourceInfos
        elementIter = UtilXml.childElementList(ofbizComponentElement, "service-resource").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            ServiceResourceInfo serviceResourceInfo = new ServiceResourceInfo(curElement);
            this.serviceResourceInfos.add(serviceResourceInfo);
        }
        
        // webapp - webappInfos
        elementIter = UtilXml.childElementList(ofbizComponentElement, "webapp").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            WebappInfo webappInfo = new WebappInfo(curElement);
            this.webappInfos.add(webappInfo);
        }
        
        Debug.logInfo("Loaded component : " + globalName + " [" + rootLocation + "]", module);
    }
    
    public boolean isFileResource(ResourceInfo resourceInfo) throws ComponentException {
        ResourceLoaderInfo resourceLoaderInfo = (ResourceLoaderInfo) resourceLoaderInfos.get(resourceInfo.loader);
        if (resourceLoaderInfo == null) {
            throw new ComponentException("Could not find resource-loader named: " + resourceInfo.loader);
        }
        return "file".equals(resourceLoaderInfo.type) || "component".equals(resourceLoaderInfo.type);
    }
       
    public List getClasspathInfos() {
        return classpathInfos;
    }
   
    public String getComponentName() {
        return componentName;
    }
    
    public List getEntityResourceInfos() {
        return entityResourceInfos;
    }
    
    public String getGlobalName() {
        return globalName;
    }
    
    public Map getResourceLoaderInfos() {
        return resourceLoaderInfos;
    }
    
    public String getRootLocation() {
        return rootLocation;
    }
    
    public List getServiceResourceInfos() {
        return serviceResourceInfos;
    }
   
    public List getWebappInfos() {
        return webappInfos;
    }    
    

    public static class ResourceLoaderInfo {
        public String name;
        public String type;
        public String prependEnv;
        public String prefix;

        public ResourceLoaderInfo(Element element) {
            this.name = element.getAttribute("name");
            this.type = element.getAttribute("type");
            this.prependEnv = element.getAttribute("prepend-env");
            this.prefix = element.getAttribute("prefix");
        }
    }
    
    public static class ResourceInfo {
        public String loader;
        public String location;

        public ResourceInfo(Element element) {
            this.loader = element.getAttribute("loader");
            this.location = element.getAttribute("location");
        }
    }

    public static class ClasspathInfo extends ResourceInfo {
        public String type;

        public ClasspathInfo(Element element) {
            super(element);
            this.type = element.getAttribute("type");
        }
    }

    public static class EntityResourceInfo extends ResourceInfo {
        public String type;
        public String readerName;

        public EntityResourceInfo(Element element) {
            super(element);
            this.type = element.getAttribute("type");
            this.readerName = element.getAttribute("reader-name");
        }
    }

    public static class ServiceResourceInfo extends ResourceInfo {
        public String type;

        public ServiceResourceInfo(Element element) {
            super(element);
            this.type = element.getAttribute("type");
        }
    }

    public static class WebappInfo extends ResourceInfo {
        public String name;
        public String title;
        public String server;
        public String mountPoint;

        public WebappInfo(Element element) {
            super(element);
            this.name = element.getAttribute("name");
            this.title = element.getAttribute("title");
            this.server = element.getAttribute("server");
            this.mountPoint = element.getAttribute("mount-point");
            
            // default title is name w/ upper-cased first letter
            if (UtilValidate.isEmpty(this.title)) {                
                this.title = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();               
            }
            
            // default mount point is name if none specified
            if (UtilValidate.isEmpty(this.mountPoint)) {
                this.mountPoint = this.name;
            }
            
            // check the mount point and make sure it is properly formatted
            if (!this.mountPoint.startsWith("/")) {
                this.mountPoint = "/" + this.mountPoint;
            }
            if (!this.mountPoint.endsWith("/*")) {
                if (!this.mountPoint.endsWith("/")) {
                    this.mountPoint = this.mountPoint + "/";
                }
                this.mountPoint = this.mountPoint + "*";   
            }
        }               
    }
}
