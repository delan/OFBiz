/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 * Misc. utility method for dealing with the ofbiz-component.xml file
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Revision$
 * @since      2.2
 */
public class ComponentConfig {
    
    public static final String module = ComponentConfig.class.getName();
    public static final String OFBIZ_COMPONENT_XML_FILENAME = "ofbiz-component.xml";

    // this is not a UtilCache because reloading may cause problems
    public static Map componentConfigs = new HashMap();

    public static ComponentConfig getComponentConfig(String globalName, String rootLocation) throws ComponentException {
        ComponentConfig componentConfig = (ComponentConfig) componentConfigs.get(globalName);
        if (componentConfig == null) {
            synchronized (ComponentConfig.class) {
                componentConfig = (ComponentConfig) componentConfigs.get(globalName);
                if (componentConfig == null) {
                    componentConfig = new ComponentConfig(globalName, rootLocation);
                    componentConfigs.put(globalName, componentConfig);
                }
            }
        }
        return componentConfig;
    }

    // ========== component info fields ==========
    public String globalName;
    public String rootLocation;
    
    public String componentName;
    
    public Map resourceLoaderInfos = new HashMap();
    public List classpathInfos = new LinkedList();
    public List entityResourceInfos = new LinkedList();
    public List serviceResourceInfos = new LinkedList();
    public List webappInfos = new LinkedList();

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
            throw new ComponentException("Could not find the " + OFBIZ_COMPONENT_XML_FILENAME + " configuration file  in the component root location: " + rootLocation);
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
        
        Iterator elementIter;
        
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
    }
    
    public boolean isFileResource(ResourceInfo resourceInfo) throws ComponentException {
        ResourceLoaderInfo resourceLoaderInfo = (ResourceLoaderInfo) resourceLoaderInfos.get(resourceInfo.loader);
        if (resourceLoaderInfo == null) {
            throw new ComponentException("Could not find resource-loader named: " + resourceInfo.loader);
        }
        return "file".equals(resourceLoaderInfo.type) || "component".equals(resourceLoaderInfo.type);
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
        }
        /**
         * @return
         */
        public String getMountPoint() {
            if (UtilValidate.isNotEmpty(mountPoint)) {
                return mountPoint;
            } else if (UtilValidate.isNotEmpty(name)) {
                return "/" + name;
            } else {
                return null;
            }
        }

        /**
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * @return
         */
        public String getServer() {
            return server;
        }

        /**
         * @return
         */
        public String getTitle() {
            if (UtilValidate.isNotEmpty(title)) {
                return title;
            } else if (UtilValidate.isNotEmpty(name)) {
                return Character.toUpperCase(name.charAt(0)) + name.substring(1);
            } else {
                return null;
            }
        }
    }
}
