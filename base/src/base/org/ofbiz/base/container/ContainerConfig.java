/*
 * $Id: ContainerConfig.java,v 1.5 2003/09/02 02:17:15 ajzeneski Exp $
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
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.OrderedMap;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * ContainerConfig - Container configuration for ofbiz.xml
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.5 $
 * @since      3.0
 */
public class ContainerConfig {
    
    public static final String module = ContainerConfig.class.getName();
    
    protected static Map containers = new OrderedMap();    
    
    public static Container getContainer(String containerName, String configFile) throws ContainerException {
        Container container = (Container) containers.get(containerName);
        if (container == null) {            
            synchronized (ContainerConfig.class) {
                container = (Container) containers.get(containerName);
                if (container == null) {
                    if (configFile == null) {
                        throw new ContainerException("Container config file cannot be null");
                    }
                    new ContainerConfig(configFile);
                    container = (Container) containers.get(containerName);
                }                
            }
            if (container == null) {
                throw new ContainerException("No container found with the name : " + containerName);
            }            
        }
        return container;
    }
    
    public static Collection getContainers(String configFile) throws ContainerException {
        if (containers.size() == 0) {
            synchronized (ContainerConfig.class) {                
                if (containers.size() == 0) {
                    if (configFile == null) {
                        throw new ContainerException("Container config file cannot be null");
                    }
                    new ContainerConfig(configFile);                    
                }                
            }
            if (containers.size() == 0) {
                throw new ContainerException("No contaners loaded; problem with configuration");
            }            
        }
        return containers.values();
    }
            
    protected ContainerConfig() {}
    
    protected ContainerConfig(String configFileLocation) throws ContainerException {        
        // load the config file
        URL xmlUrl = UtilURL.fromFilename(configFileLocation);
        if (xmlUrl == null) {
            throw new ContainerException("Could not find " + configFileLocation + " master OFBiz container configuration");
        }
        
        // read the document
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
        
        // root element
        Element root = containerDocument.getDocumentElement();        
          
        // containers
        Iterator elementIter = UtilXml.childElementList(root, "container").iterator();
        while (elementIter.hasNext()) {
            Element curElement = (Element) elementIter.next();
            Container container = new Container(curElement);
            containers.put(container.name, container);    
        }                          
    }
    
    public static class Container {
        public String name;
        public String className;
        public Map properties;
        
        public Container(Element element) {
            this.name = element.getAttribute("name");
            this.className = element.getAttribute("class");
            
            properties = new OrderedMap();
            Iterator elementIter = UtilXml.childElementList(element, "property").iterator();
            while (elementIter.hasNext()) {
                Element curElement = (Element) elementIter.next();
                Property property = new Property(curElement);
                properties.put(property.name, property);
            }                       
        }
        
        public Property getProperty(String name) {
            return (Property) properties.get(name);
        }
        
        public static class Property {
            public String name;
            public String value;
            public Map properties;
            
            public Property(Element element) {
                this.name = element.getAttribute("name");
                this.value = element.getAttribute("value");
                
                properties = new OrderedMap();
                Iterator elementIter = UtilXml.childElementList(element, "property").iterator();
                while (elementIter.hasNext()) {
                    Element curElement = (Element) elementIter.next();
                    Property property = new Property(curElement);
                    properties.put(property.name, property);                    
                }                    
            }
            
            public Property getProperty(String name) {
                return (Property) properties.get(name);
            }
        }
    }                        
}