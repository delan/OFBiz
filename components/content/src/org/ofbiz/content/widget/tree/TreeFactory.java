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
package org.ofbiz.content.widget.tree;

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
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Widget Library - Tree factory class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.2
 */
public class TreeFactory {
    
    public static final String module = TreeFactory.class.getName();

    public static final UtilCache treeLocationCache = new UtilCache("widget.tree.locationResource", 0, 0, false);
    public static final UtilCache treeWebappCache = new UtilCache("widget.tree.webappResource", 0, 0, false);
    
    public static ModelTree getTreeFromLocation(String resourceName, String treeName, GenericDelegator delegator, LocalDispatcher dispatcher) 
            throws IOException, SAXException, ParserConfigurationException {
        Map modelTreeMap = (Map) treeLocationCache.get(resourceName);
        if (modelTreeMap == null) {
            synchronized (TreeFactory.class) {
                modelTreeMap = (Map) treeLocationCache.get(resourceName);
                if (modelTreeMap == null) {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    if (loader == null) {
                        loader = TreeFactory.class.getClassLoader();
                    }
                    
                    URL treeFileUrl = null;
                    treeFileUrl = FlexibleLocation.resolveLocation(resourceName); //, loader);
                    Document treeFileDoc = UtilXml.readXmlDocument(treeFileUrl, true);
                    modelTreeMap = readTreeDocument(treeFileDoc, delegator, dispatcher);
                    treeLocationCache.put(resourceName, modelTreeMap);
                }
            }
        }
        
        ModelTree modelTree = (ModelTree) modelTreeMap.get(treeName);
        if (modelTree == null) {
            throw new IllegalArgumentException("Could not find tree with name [" + treeName + "] in class resource [" + resourceName + "]");
        }
        return modelTree;
    }
    
    public static ModelTree getTreeFromWebappContext(String resourceName, String treeName, HttpServletRequest request) 
            throws IOException, SAXException, ParserConfigurationException {
        String webappName = UtilHttp.getApplicationName(request);
        String cacheKey = webappName + "::" + resourceName;
        
        
        Map modelTreeMap = (Map) treeWebappCache.get(cacheKey);
        if (modelTreeMap == null) {
            synchronized (TreeFactory.class) {
                modelTreeMap = (Map) treeWebappCache.get(cacheKey);
                if (modelTreeMap == null) {
                    ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
                    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
                    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
                    
                    URL treeFileUrl = servletContext.getResource(resourceName);
                    Document treeFileDoc = UtilXml.readXmlDocument(treeFileUrl, true);
                    modelTreeMap = readTreeDocument(treeFileDoc, delegator, dispatcher);
                    treeWebappCache.put(cacheKey, modelTreeMap);
                }
            }
        }
        
        ModelTree modelTree = (ModelTree) modelTreeMap.get(treeName);
        if (modelTree == null) {
            throw new IllegalArgumentException("Could not find tree with name [" + treeName + "] in webapp resource [" + resourceName + "] in the webapp [" + webappName + "]");
        }
        return modelTree;
    }
    
    public static Map readTreeDocument(Document treeFileDoc, GenericDelegator delegator, LocalDispatcher dispatcher) {
        Map modelTreeMap = new HashMap();
        if (treeFileDoc != null) {
            // read document and construct ModelTree for each tree element
            Element rootElement = treeFileDoc.getDocumentElement();
            List treeElements = UtilXml.childElementList(rootElement, "tree");
            Iterator treeElementIter = treeElements.iterator();
            while (treeElementIter.hasNext()) {
                Element treeElement = (Element) treeElementIter.next();
                ModelTree modelTree = new ModelTree(treeElement, delegator, dispatcher);
                modelTreeMap.put(modelTree.getName(), modelTree);
            }
        }
        return modelTreeMap;
    }
}
