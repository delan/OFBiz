/*
 * $Id: ComponentLoaderConfig.java,v 1.1 2003/08/15 23:08:22 ajzeneski Exp $
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
package org.ofbiz.base.component;

import java.util.*;
import java.io.IOException;
import java.net.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.ofbiz.base.util.*;

/**
 * ComponentLoaderConfig - Component Loader configuration
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      2.2
 */
public class ComponentLoaderConfig {
    
    public static final String module = ComponentLoaderConfig.class.getName();    
    public static final String COMPONENT_LOAD_XML_FILENAME = "component-load.xml";
    
    public static final int SINGLE_COMPONENT = 0;
    public static final int COMPONENT_DIRECTORY = 1;
    
    protected static List componentsToLoad = null;
    
    protected ComponentLoaderConfig() throws ComponentException {
        ComponentLoaderConfig.componentsToLoad = new LinkedList();
        
        URL xmlUrl = UtilURL.fromResource(COMPONENT_LOAD_XML_FILENAME);
        
        Document document = null;
        try {
            document = UtilXml.readXmlDocument(xmlUrl, true);
        } catch (SAXException e) {
            throw new ComponentException("Error reading the component config file: " + xmlUrl, e);
        } catch (ParserConfigurationException e) {
            throw new ComponentException("Error reading the component config file: " + xmlUrl, e);
        } catch (IOException e) {
            throw new ComponentException("Error reading the component config file: " + xmlUrl, e);
        }
        
        Element root = document.getDocumentElement();
        List toLoad = UtilXml.childElementList(root, null);
        if (toLoad != null && toLoad.size() > 0) {
            Iterator i = toLoad.iterator();
            while (i.hasNext()) {
                Element element = (Element) i.next();
                componentsToLoad.add(new ComponentDef(element));                
            }
        }        
        
    }
    
    public static List getComponentsToLoad() throws ComponentException {
        if (componentsToLoad == null) {
            synchronized (ComponentLoaderConfig.class) {
                if (componentsToLoad ==  null) {
                    new ComponentLoaderConfig();
                }                
            }
        }
        return componentsToLoad;
    }
        
    public static class ComponentDef {
        public String name;
        public String location;
        public int type;
        
        public ComponentDef(Element element) {
            if ("load-component".equals(element.getLocalName())) {
                name = element.getAttribute("component-name");
                location = element.getAttribute("component-location");
                type = SINGLE_COMPONENT;
            } else if ("load-components".equals(element.getLocalName())) {
                name = null;
                location = element.getAttribute("parent-directory");
                type = COMPONENT_DIRECTORY;
            }
        }        
    }
}
