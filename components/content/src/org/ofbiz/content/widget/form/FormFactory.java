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
package org.ofbiz.content.widget.form;

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
 * Widget Library - Form factory class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.2
 */
public class FormFactory {
    
    public static final String module = FormFactory.class.getName();

    public static final UtilCache formLocationCache = new UtilCache("widget.form.locationResource", 0, 0, false);
    public static final UtilCache formWebappCache = new UtilCache("widget.form.webappResource", 0, 0, false);
    
    public static ModelForm getFormFromLocation(String resourceName, String formName, GenericDelegator delegator, LocalDispatcher dispatcher) 
            throws IOException, SAXException, ParserConfigurationException {
        Map modelFormMap = (Map) formLocationCache.get(resourceName);
        if (modelFormMap == null) {
            synchronized (FormFactory.class) {
                modelFormMap = (Map) formLocationCache.get(resourceName);
                if (modelFormMap == null) {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    if (loader == null) {
                        loader = FormFactory.class.getClassLoader();
                    }
                    
                    URL formFileUrl = null;
                    formFileUrl = FlexibleLocation.resolveLocation(resourceName); //, loader);
                    Document formFileDoc = UtilXml.readXmlDocument(formFileUrl, true);
                    modelFormMap = readFormDocument(formFileDoc, delegator, dispatcher);
                    formLocationCache.put(resourceName, modelFormMap);
                }
            }
        }
        
        ModelForm modelForm = (ModelForm) modelFormMap.get(formName);
        if (modelForm == null) {
            throw new IllegalArgumentException("Could not find form with name [" + formName + "] in class resource [" + resourceName + "]");
        }
        return modelForm;
    }
    
    public static ModelForm getFormFromWebappContext(String resourceName, String formName, HttpServletRequest request) 
            throws IOException, SAXException, ParserConfigurationException {
        String webappName = UtilHttp.getApplicationName(request);
        String cacheKey = webappName + "::" + resourceName;
        
        
        Map modelFormMap = (Map) formWebappCache.get(cacheKey);
        if (modelFormMap == null) {
            synchronized (FormFactory.class) {
                modelFormMap = (Map) formWebappCache.get(cacheKey);
                if (modelFormMap == null) {
                    ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
                    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
                    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
                    
                    URL formFileUrl = servletContext.getResource(resourceName);
                    Document formFileDoc = UtilXml.readXmlDocument(formFileUrl, true);
                    modelFormMap = readFormDocument(formFileDoc, delegator, dispatcher);
                    formWebappCache.put(cacheKey, modelFormMap);
                }
            }
        }
        
        ModelForm modelForm = (ModelForm) modelFormMap.get(formName);
        if (modelForm == null) {
            throw new IllegalArgumentException("Could not find form with name [" + formName + "] in webapp resource [" + resourceName + "] in the webapp [" + webappName + "]");
        }
        return modelForm;
    }
    
    public static Map readFormDocument(Document formFileDoc, GenericDelegator delegator, LocalDispatcher dispatcher) {
        Map modelFormMap = new HashMap();
        if (formFileDoc != null) {
            // read document and construct ModelForm for each form element
            Element rootElement = formFileDoc.getDocumentElement();
            List formElements = UtilXml.childElementList(rootElement, "form");
            Iterator formElementIter = formElements.iterator();
            while (formElementIter.hasNext()) {
                Element formElement = (Element) formElementIter.next();
                ModelForm modelForm = new ModelForm(formElement, delegator, dispatcher);
                modelFormMap.put(modelForm.getName(), modelForm);
            }
        }
        return modelFormMap;
    }
}
