/*
 * $Id$
 *
 * Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.widget.screen;

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
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Widget Library - Screen factory class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ScreenFactory {
    
    public static final String module = ScreenFactory.class.getName();

    public static final UtilCache screenLocationCache = new UtilCache("widget.screen.locationResource", 0, 0, false);
    public static final UtilCache screenWebappCache = new UtilCache("widget.screen.webappResource", 0, 0, false);

    public static String getResourceNameFromCombined(String combinedName) {
        // split out the name on the last "#"
        int numSignIndex = combinedName.lastIndexOf("#");
        if (numSignIndex == -1) {
            throw new IllegalArgumentException("Error in screen location/name: no \"#\" found to separate the location from the name; correct example: component://product/screen/product/ProductScreens.xml#EditProduct");
        }
        if (numSignIndex + 1 >= combinedName.length()) {
            throw new IllegalArgumentException("Error in screen location/name: the \"#\" was at the end with no screen name after it; correct example: component://product/screen/product/ProductScreens.xml#EditProduct");
        }
        String resourceName = combinedName.substring(0, numSignIndex);
        return resourceName;
    }
    
    public static String getScreenNameFromCombined(String combinedName) {
        // split out the name on the last "#"
        int numSignIndex = combinedName.lastIndexOf("#");
        if (numSignIndex == -1) {
            throw new IllegalArgumentException("Error in screen location/name: no \"#\" found to separate the location from the name; correct example: component://product/screen/product/ProductScreens.xml#EditProduct");
        }
        if (numSignIndex + 1 >= combinedName.length()) {
            throw new IllegalArgumentException("Error in screen location/name: the \"#\" was at the end with no screen name after it; correct example: component://product/screen/product/ProductScreens.xml#EditProduct");
        }
        String screenName = combinedName.substring(numSignIndex + 1);
        return screenName;
    }
    
    public static ModelScreen getScreenFromLocation(String combinedName) 
            throws IOException, SAXException, ParserConfigurationException {
        String resourceName = getResourceNameFromCombined(combinedName);
        String screenName = getScreenNameFromCombined(combinedName);
        return getScreenFromLocation(resourceName, screenName);
    }
    
    public static ModelScreen getScreenFromLocation(String resourceName, String screenName) 
            throws IOException, SAXException, ParserConfigurationException {
        Map modelScreenMap = (Map) screenLocationCache.get(resourceName);
        if (modelScreenMap == null) {
            synchronized (ScreenFactory.class) {
                modelScreenMap = (Map) screenLocationCache.get(resourceName);
                if (modelScreenMap == null) {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    if (loader == null) {
                        loader = ScreenFactory.class.getClassLoader();
                    }
                    
                    URL screenFileUrl = null;
                    screenFileUrl = FlexibleLocation.resolveLocation(resourceName, loader);
                    if (screenFileUrl == null) {
                        throw new IllegalArgumentException("Could not resolve location to URL: " + resourceName);
                    }
                    Document screenFileDoc = UtilXml.readXmlDocument(screenFileUrl, true);
                    modelScreenMap = readScreenDocument(screenFileDoc);
                    Debug.logInfo("Got " + modelScreenMap.size() + " screen definitions from the location: " + screenFileUrl.toExternalForm(), module);
                    screenLocationCache.put(resourceName, modelScreenMap);
                }
            }
        }
        
        ModelScreen modelScreen = (ModelScreen) modelScreenMap.get(screenName);
        if (modelScreen == null) {
            throw new IllegalArgumentException("Could not find screen with name [" + screenName + "] in class resource [" + resourceName + "]");
        }
        return modelScreen;
    }
    
    public static ModelScreen getScreenFromWebappContext(String resourceName, String screenName, HttpServletRequest request) 
            throws IOException, SAXException, ParserConfigurationException {
        String webappName = UtilHttp.getApplicationName(request);
        String cacheKey = webappName + "::" + resourceName;
        
        
        Map modelScreenMap = (Map) screenWebappCache.get(cacheKey);
        if (modelScreenMap == null) {
            synchronized (ScreenFactory.class) {
                modelScreenMap = (Map) screenWebappCache.get(cacheKey);
                if (modelScreenMap == null) {
                    ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
                    
                    URL screenFileUrl = servletContext.getResource(resourceName);
                    Document screenFileDoc = UtilXml.readXmlDocument(screenFileUrl, true);
                    modelScreenMap = readScreenDocument(screenFileDoc);
                    screenWebappCache.put(cacheKey, modelScreenMap);
                }
            }
        }
        
        ModelScreen modelScreen = (ModelScreen) modelScreenMap.get(screenName);
        if (modelScreen == null) {
            throw new IllegalArgumentException("Could not find screen with name [" + screenName + "] in webapp resource [" + resourceName + "] in the webapp [" + webappName + "]");
        }
        return modelScreen;
    }
    
    public static Map readScreenDocument(Document screenFileDoc) {
        Map modelScreenMap = new HashMap();
        if (screenFileDoc != null) {
            // read document and construct ModelScreen for each screen element
            Element rootElement = screenFileDoc.getDocumentElement();
            List screenElements = UtilXml.childElementList(rootElement, "screen");
            Iterator screenElementIter = screenElements.iterator();
            while (screenElementIter.hasNext()) {
                Element screenElement = (Element) screenElementIter.next();
                ModelScreen modelScreen = new ModelScreen(screenElement, modelScreenMap);
                //Debug.logInfo("Read Screen with name: " + modelScreen.getName(), module);
                modelScreenMap.put(modelScreen.getName(), modelScreen);
            }
        }
        return modelScreenMap;
    }
}
