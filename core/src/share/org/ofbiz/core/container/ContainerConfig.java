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
import java.net.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.ofbiz.core.util.*;

/**
 * ContainerConfig - Container configuration for ofbiz.xml
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.2
 */
public class ContainerConfig {
    
    public static final String module = ContainerConfig.class.getName();
    
    protected List components = new LinkedList();
    protected List servers = new LinkedList();    
    
    protected ContainerConfig() {}
    
    protected ContainerConfig(String configFileLocation) throws ContainerException {
        URL xmlUrl = UtilURL.fromFilename(configFileLocation);
        if (xmlUrl == null) {
            throw new ContainerException("Could not find " + configFileLocation + " master OFBiz container configuration");
        }
        
        Document containerDocument = null;
        try {
            containerDocument = UtilXml.readXmlDocument(xmlUrl, true);
        } catch (SAXException e) {
            throw new ContainerException("Error reading the container config file: " + xmlUrl, e);
        } catch (ParserConfigurationException e) {
            throw new ContainerException("Error reading the container config file: " + xmlUrl, e);
        } catch (IOException e) {
            throw new ContainerException("Error reading the container config file: " + xmlUrl, e);
        } 
        
        Element containers = containerDocument.getDocumentElement();
        Iterator elementIter = null;
          
        // components
        elementIter = UtilXml.childElementList(containers, "component-container").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            ComponentContainer container = new ComponentContainer(curElement);
            this.components.add(container);
        }
        
        // servers
        elementIter = UtilXml.childElementList(containers, "web-container").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            WebContainer container = new WebContainer(curElement);
            this.components.add(container);
        }                            
    }
        
    public static class ComponentContainer {
        public String containerClass;
        public List components;
        
        public ComponentContainer(Element element) {
            this.containerClass = element.getAttribute("class");
            
            Iterator elementIter = UtilXml.childElementList(element, "component").iterator();
            while (elementIter.hasNext()) {
                Element curElement = (Element) elementIter.next();
                Component comp = new Component(curElement);
                this.components.add(comp);
            }            
        }
        
        public static class Component {
            public String name;
            public String config;
            
            public Component(Element element) {
                this.name = element.getAttribute("name");
                this.config = element.getAttribute("config");                
            }
        }
    }
    
    public static class WebContainer {
        public String containerClass;
        public List servers;
    
        public WebContainer(Element element) {
            this.containerClass = element.getAttribute("class");
            this.servers = new LinkedList();
            
            Iterator elementIter = UtilXml.childElementList(element, "server").iterator();
            while (elementIter.hasNext()) {
                Element curElement = (Element) elementIter.next();
                Server server = new Server(curElement);
                this.servers.add(server);
            }            
        }
        
        public static class Server {
            public String name;
            public List listeners; 
            
            public Server(Element element) {
                this.name = element.getAttribute("name");
                this.listeners = new LinkedList();
                
                Iterator elementIter = UtilXml.childElementList(element, "server").iterator();
                while (elementIter.hasNext()) {
                    Element curElement = (Element) elementIter.next();
                    Listener listener = new Listener(curElement);
                    this.listeners.add(listener);    
                }                
            }   
        
            public static class Listener {
                public String type;
                public String host;                
                public String keystore;
                public String password;
                public String keyPassword;
                int minThreads;
                int maxThreads;
                int maxIdleTime;
                int maxReadTime;
                int port;
                boolean requireClientCert;
            
                public Listener(Element element) {
                    this.type = element.getAttribute("type");
                    this.host = element.getAttribute("host");
                    this.keystore = element.getAttribute("keystore");
                    this.password = element.getAttribute("password");
                    this.keyPassword = element.getAttribute("keyPassword");
                    this.requireClientCert = UtilXml.checkBoolean("need-client-cert", false);
                                       
                    String minThreadsStr = element.getAttribute("min-threads");                                                                                
                    try {
                        this.minThreads = Integer.parseInt(minThreadsStr);
                    } catch (NumberFormatException e) {
                        Debug.logWarning(e, module);
                        this.minThreads = 5;
                    }
                    
                    String maxThreadsStr = element.getAttribute("max-threads");
                    try {
                        this.maxThreads = Integer.parseInt(maxThreadsStr);
                    } catch (NumberFormatException e) {
                        Debug.logWarning(e, module);
                        this.maxThreads = 250;
                    }
                    
                    String maxIdleStr = element.getAttribute("max-idle-time");
                    try {
                        this.maxIdleTime = Integer.parseInt(maxIdleStr);
                    } catch (NumberFormatException e) {
                        Debug.logWarning(e, module);
                        this.maxIdleTime = 30000;
                    }
                    
                    String maxReadStr = element.getAttribute("max-read-time");
                    try {
                        this.maxReadTime = Integer.parseInt(maxReadStr);
                    } catch (NumberFormatException e) {
                        Debug.logWarning(e, module);
                        this.maxReadTime = 60000;
                    }
                    
                    String portStr = element.getAttribute("port");
                    try {
                        this.port = Integer.parseInt(portStr);
                    } catch (NumberFormatException e) {
                        Debug.logWarning(e, module);
                        this.port = 8080;
                    }                                   
                }
            }
        }  
    }        
}