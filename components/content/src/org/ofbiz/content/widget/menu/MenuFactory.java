/*
 * $Id: MenuFactory.java,v 1.3 2004/07/16 18:53:24 byersa Exp $
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
 */
package org.ofbiz.content.widget.menu;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Widget Library - Menu factory class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      2.2
 */
public class MenuFactory {
    
    public static final String module = MenuFactory.class.getName();

    public static final UtilCache menuClassCache = new UtilCache("widget.menu.classResource", 0, 0, false);
    public static final UtilCache menuWebappCache = new UtilCache("widget.menu.webappResource", 0, 0, false);
    public static final UtilCache menuLocationCache = new UtilCache("widget.menu.locationResource", 0, 0, false);
    
    public static ModelMenu getMenuFromClass(String resourceName, String menuName, GenericDelegator delegator, LocalDispatcher dispatcher) 
            throws IOException, SAXException, ParserConfigurationException {
        Map modelMenuMap = (Map) menuClassCache.get(resourceName);
        if (modelMenuMap == null) {
            synchronized (MenuFactory.class) {
                modelMenuMap = (Map) menuClassCache.get(resourceName);
                if (modelMenuMap == null) {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    if (loader == null) {
                        loader = MenuFactory.class.getClassLoader();
                    }
                    
                    URL menuFileUrl = loader.getResource(resourceName);
                    Document menuFileDoc = UtilXml.readXmlDocument(menuFileUrl, true);
                    modelMenuMap = readMenuDocument(menuFileDoc, delegator, dispatcher);
                    menuClassCache.put(resourceName, modelMenuMap);
                }
            }
        }
        
        ModelMenu modelMenu = (ModelMenu) modelMenuMap.get(menuName);
        if (modelMenu == null) {
            throw new IllegalArgumentException("Could not find menu with name [" + menuName + "] in class resource [" + resourceName + "]");
        }
        return modelMenu;
    }
    
    public static ModelMenu getMenuFromWebappContext(String resourceName, String menuName, HttpServletRequest request) 
            throws IOException, SAXException, ParserConfigurationException {
        String webappName = UtilHttp.getApplicationName(request);
        String cacheKey = webappName + "::" + resourceName;
        
        
        Map modelMenuMap = (Map) menuWebappCache.get(cacheKey);
        if (modelMenuMap == null) {
            synchronized (MenuFactory.class) {
                modelMenuMap = (Map) menuWebappCache.get(cacheKey);
                if (modelMenuMap == null) {
                    ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
                    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
                    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
                    
                    URL menuFileUrl = servletContext.getResource(resourceName);
                    Document menuFileDoc = UtilXml.readXmlDocument(menuFileUrl, true);
                    modelMenuMap = readMenuDocument(menuFileDoc, delegator, dispatcher);
                    menuWebappCache.put(cacheKey, modelMenuMap);
                }
            }
        }
        
        ModelMenu modelMenu = (ModelMenu) modelMenuMap.get(menuName);
        if (modelMenu == null) {
            throw new IllegalArgumentException("Could not find menu with name [" + menuName + "] in webapp resource [" + resourceName + "] in the webapp [" + webappName + "]");
        }
        return modelMenu;
    }
    
    public static Map readMenuDocument(Document menuFileDoc, GenericDelegator delegator, LocalDispatcher dispatcher) {
        Map modelMenuMap = new HashMap();
        if (menuFileDoc != null) {
            // read document and construct ModelMenu for each menu element
            Element rootElement = menuFileDoc.getDocumentElement();
            List menuElements = UtilXml.childElementList(rootElement, "menu");
            Iterator menuElementIter = menuElements.iterator();
            while (menuElementIter.hasNext()) {
                Element menuElement = (Element) menuElementIter.next();
                ModelMenu modelMenu = new ModelMenu(menuElement, delegator, dispatcher);
                modelMenuMap.put(modelMenu.getName(), modelMenu);
            }
        }
        return modelMenuMap;
    }

    public static ModelMenu getMenuFromLocation(String resourceName, String menuName, GenericDelegator delegator, LocalDispatcher dispatcher) 
            throws IOException, SAXException, ParserConfigurationException {
        Map modelMenuMap = (Map) menuLocationCache.get(resourceName);
        if (modelMenuMap == null) {
            synchronized (MenuFactory.class) {
                modelMenuMap = (Map) menuLocationCache.get(resourceName);
                if (modelMenuMap == null) {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    if (loader == null) {
                        loader = MenuFactory.class.getClassLoader();
                    }
                    
                    URL menuFileUrl = null;
                    menuFileUrl = FlexibleLocation.resolveLocation(resourceName); //, loader);
                    Document menuFileDoc = UtilXml.readXmlDocument(menuFileUrl, true);
                    modelMenuMap = readMenuDocument(menuFileDoc, delegator, dispatcher);
                    menuLocationCache.put(resourceName, modelMenuMap);
                }
            }
        }
        
        ModelMenu modelMenu = (ModelMenu) modelMenuMap.get(menuName);
        if (modelMenu == null) {
            throw new IllegalArgumentException("Could not find menu with name [" + menuName + "] in class resource [" + resourceName + "]");
        }
        return modelMenu;
    }
    
}
