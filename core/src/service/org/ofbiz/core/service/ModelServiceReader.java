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
package org.ofbiz.core.service;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.core.config.GenericConfigException;
import org.ofbiz.core.config.ResourceHandler;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.OrderedMap;
import org.ofbiz.core.util.UtilCache;
import org.ofbiz.core.util.UtilTimer;
import org.ofbiz.core.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Generic Service - Service Definition Reader
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */

public class ModelServiceReader {

    public static final String module = ModelServiceReader.class.getName();

    protected static UtilCache readersUrl = new UtilCache("service.ModelServiceReader.ByURL", 0, 0);
    protected static UtilCache readersLoader = new UtilCache("service.ModelServiceReader.ByResourceLoader", 0, 0);

    /** is either from a URL or from a ResourceLoader (through the ResourceHandler) */
    protected boolean isFromURL;
    protected URL readerURL = null;
    protected ResourceHandler handler = null;
    protected Map modelServices = null;
    protected DispatchContext dctx = null;

    public static ModelServiceReader getModelServiceReader(URL readerURL, DispatchContext dctx) {
        ModelServiceReader reader = null;

        // if ( readersUrl.containsKey(readerURL) ) <-- this is unnecessary as it will return null below if not found
        reader = (ModelServiceReader) readersUrl.get(readerURL);
        if (reader == null) { // don't want to block here
            synchronized (ModelServiceReader.class) {
                // must check if null again as one of the blocked threads can still enter
                reader = (ModelServiceReader) readersUrl.get(readerURL);
                if (reader == null) {
                    // if (Debug.infoOn()) Debug.logInfo("[Creating reader]: " + readerURL.toExternalForm(), module);
                    reader = new ModelServiceReader(readerURL, dctx);
                    readersUrl.put(readerURL, reader);
                }
            }
        }
        return reader;
    }

    public static ModelServiceReader getModelServiceReader(ResourceHandler handler, DispatchContext dctx) {
        ModelServiceReader reader = null;

        reader = (ModelServiceReader) readersLoader.get(handler);
        if (reader == null) { // don't want to block here
            synchronized (ModelServiceReader.class) {
                // must check if null again as one of the blocked threads can still enter
                reader = (ModelServiceReader) readersLoader.get(handler);
                if (reader == null) {
                    // if (Debug.infoOn()) Debug.logInfo("[Creating reader]: " + handler, module);
                    reader = new ModelServiceReader(handler, dctx);
                    readersLoader.put(handler, reader);
                }
            }
        }
        return reader;
    }

    protected ModelServiceReader(URL readerURL, DispatchContext dctx) {
        this.isFromURL = true;
        this.readerURL = readerURL;
        this.handler = null;
        this.dctx = dctx;
        // preload models...
        getModelServices();
    }

    protected ModelServiceReader(ResourceHandler handler, DispatchContext dctx) {
        this.isFromURL = false;
        this.readerURL = null;
        this.handler = handler;
        this.dctx = dctx;
        // preload models...
        getModelServices();
    }

    public Map getModelServices() {
        if (modelServices == null) { // don't want to block here
            synchronized (ModelServiceReader.class) {
                // must check if null again as one of the blocked threads can still enter
                if (modelServices == null) { // now it's safe
                    modelServices = new HashMap();

                    UtilTimer utilTimer = new UtilTimer();

                    Document document = null;

                    if (this.isFromURL) {
                        // utilTimer.timerString("Before getDocument in file " + readerURL);
                        document = getDocument(readerURL);

                        if (document == null) {
                            modelServices = null;
                            return null;
                        }
                    } else {
                        // utilTimer.timerString("Before getDocument in " + handler);
                        try {
                            document = handler.getDocument();
                        } catch (GenericConfigException e) {
                            Debug.logError(e, "Error getting XML document from resource");
                            return null;
                        }
                    }

                    if (this.isFromURL) {// utilTimer.timerString("Before getDocumentElement in file " + readerURL);
                    } else {// utilTimer.timerString("Before getDocumentElement in " + handler);
                    }
                    Element docElement = document.getDocumentElement();

                    if (docElement == null) {
                        modelServices = null;
                        return null;
                    }
                    docElement.normalize();
                    Node curChild = docElement.getFirstChild();

                    int i = 0;

                    if (curChild != null) {
                        if (this.isFromURL) {
                            utilTimer.timerString("Before start of service loop in file " + readerURL);
                        } else {
                            utilTimer.timerString("Before start of service loop in " + handler);
                        }
                        int servicesLoaded = 0;

                        do {
                            if (curChild.getNodeType() == Node.ELEMENT_NODE && "service".equals(curChild.getNodeName())) {
                                i++;
                                Element curService = (Element) curChild;
                                String serviceName = UtilXml.checkEmpty(curService.getAttribute("name"));

                                // check to see if service with same name has already been read
                                if (modelServices.containsKey(serviceName)) {
                                    Debug.logWarning("WARNING: Service " + serviceName + " is defined more than once, " +
                                        "most recent will over-write previous definition(s)", module);
                                }

                                // utilTimer.timerString("  After serviceName -- " + i + " --");
                                ModelService service = createModelService(curService);

                                // utilTimer.timerString("  After createModelService -- " + i + " --");
                                if (service != null) {
                                    modelServices.put(serviceName, service);
                                    // utilTimer.timerString("  After modelServices.put -- " + i + " --");
                                    /*
                                    int reqIn = service.getParameterNames(ModelService.IN_PARAM, false).size();
                                    int optIn = service.getParameterNames(ModelService.IN_PARAM, true).size() - reqIn;
                                    int reqOut = service.getParameterNames(ModelService.OUT_PARAM, false).size();
                                    int optOut = service.getParameterNames(ModelService.OUT_PARAM, true).size() - reqOut;

                                    if (Debug.verboseOn()) {
                                        String msg = "-- getModelService: # " + i + " Loaded service: " + serviceName +
                                            " (IN) " + reqIn + "/" + optIn + " (OUT) " + reqOut + "/" + optOut;

                                        Debug.logVerbose(msg, module);                                        
                                    }
                                    */
                                } else {
                                    Debug.logWarning(
                                        "-- -- SERVICE ERROR:getModelService: Could not create service for serviceName: " +
                                        serviceName, module);
                                }

                            }
                        } while ((curChild = curChild.getNextSibling()) != null);
                    } else {
                        Debug.logWarning("No child nodes found.", module);
                    }
                    if (this.isFromURL) {
                        utilTimer.timerString("Finished file " + readerURL + " - Total Services: " + i + " FINISHED");
                        Debug.logImportant("Loaded " + i + " Service definitions from " + readerURL);
                    } else {
                        utilTimer.timerString("Finished document in " + handler + " - Total Services: " + i + " FINISHED");
                        Debug.logImportant("Loaded " + i + " Service definitions from " + handler.getLocation() + " in loader " + handler.getLoaderName());
                    }
                }
            }
        }
        return modelServices;
    }

    /** 
     * Gets an Service object based on a definition from the specified XML Service descriptor file.
     * @param serviceName The serviceName of the Service definition to use.
     * @return An Service object describing the specified service of the specified descriptor file.
     */
    public ModelService getModelService(String serviceName) {
        Map ec = getModelServices();

        if (ec != null)
            return (ModelService) ec.get(serviceName);
        else
            return null;
    }

    /** 
     * Creates a Iterator with the serviceName of each Service defined in the specified XML Service Descriptor file.
     * @return A Iterator of serviceName Strings
     */
    public Iterator getServiceNamesIterator() {
        Collection collection = getServiceNames();

        if (collection != null) {
            return collection.iterator();
        } else {
            return null;
        }
    }

    /** 
     * Creates a Collection with the serviceName of each Service defined in the specified XML Service Descriptor file.
     * @return A Collection of serviceName Strings
     */
    public Collection getServiceNames() {
        Map ec = getModelServices();

        return ec.keySet();
    }

    protected ModelService createModelService(Element serviceElement) {
        ModelService service = new ModelService();

        service.name = UtilXml.checkEmpty(serviceElement.getAttribute("name"));
        service.engineName = UtilXml.checkEmpty(serviceElement.getAttribute("engine"));
        service.location = UtilXml.checkEmpty(serviceElement.getAttribute("location"));
        service.invoke = UtilXml.checkEmpty(serviceElement.getAttribute("invoke"));  
        service.defaultEntityName = UtilXml.checkEmpty(serviceElement.getAttribute("default-entity-name"));
        
        // these default to true; if anything but true, make false    
        service.auth = "true".equalsIgnoreCase(serviceElement.getAttribute("auth"));
        service.export = "true".equalsIgnoreCase(serviceElement.getAttribute("export"));
        
        // this defaults to true; if anything but false, make it true
        service.validate = !"false".equalsIgnoreCase(serviceElement.getAttribute("validate"));
        service.useTransaction = !"false".equalsIgnoreCase(serviceElement.getAttribute("use-transaction"));
        service.requireNewTransaction = !"false".equalsIgnoreCase(serviceElement.getAttribute("require-new-transaction"));
        
        // get the timeout and convert to int       
        String timeoutStr = UtilXml.checkEmpty(serviceElement.getAttribute("transactionTimeout"));
        if (timeoutStr == null || timeoutStr.length() == 0) {
            timeoutStr = "0";
        }
        int timeout = 0;
        try {
            timeout = Integer.parseInt(timeoutStr);
        } catch (NumberFormatException e) {
            Debug.logWarning(e, "Setting timeout to 0 (default)", module);
            timeout = 0;            
        }
        service.transactionTimeout = timeout;
                       
        service.description = getCDATADef(serviceElement, "description");
        service.nameSpace = getCDATADef(serviceElement, "namespace");  
              
        service.contextInfo = new HashMap();
        this.createImplDefs(serviceElement, service);
        this.createAutoAttrDefs(serviceElement, service);
        this.createAttrDefs(serviceElement, service);
        this.createOverrideDefs(serviceElement, service);
               
        return service;
    }

    protected String getCDATADef(Element baseElement, String tagName) {
        String value = "";
        NodeList nl = baseElement.getElementsByTagName(tagName);

        // if there are more then one decriptions we will use only the first one
        if (nl.getLength() > 0) {
            Node n = nl.item(0);
            NodeList childNodes = n.getChildNodes();

            if (childNodes.getLength() > 0) {
                Node cdata = childNodes.item(0);

                value = UtilXml.checkEmpty(cdata.getNodeValue());
            }
        }
        return value;
    }
    
    protected void createImplDefs(Element baseElement, ModelService service) {
        List implElements = UtilXml.childElementList(baseElement, "implements");
        Iterator implIter = implElements.iterator();
                
        while (implIter.hasNext()) {
            Element implement = (Element) implIter.next();
            String serviceName = UtilXml.checkEmpty(implement.getAttribute("service"));
            if (serviceName.length() > 0)
                service.implServices.add(serviceName);
        }
    }
    
    protected void createAutoAttrDefs(Element baseElement, ModelService service) {
        List autoElement = UtilXml.childElementList(baseElement, "auto-attributes");
        Iterator autoIter = autoElement.iterator();
        
        while (autoIter.hasNext()) {
            Element element = (Element) autoIter.next();            
            createAutoAttrDef(element, service);
        }
    }
    
    protected void createAutoAttrDef(Element autoElement, ModelService service) {
        // get the entity name; first from the auto-attributes then from the service def                 
        String entityName = UtilXml.checkEmpty(autoElement.getAttribute("entity-name"));        
        if (entityName == null || entityName.length() == 0) {
            entityName = service.defaultEntityName;
            if (entityName == null || entityName.length() == 0) {
                Debug.logWarning("Auto-Attribute does not specify an entity-name; not default-entity on service definition");
            }
        }
        
        // get the include type 'pk|nonpk|all'
        String includeType = UtilXml.checkEmpty(autoElement.getAttribute("include"));
        boolean includePk = "pk".equals(includeType) || "all".equals(includeType);
        boolean includeNonPk = "nonpk".equals(includeType) || "all".equals(includeType);
        
        // need a delegator for this
        GenericDelegator delegator = dctx.getDelegator();
        if (delegator == null) {
            Debug.logWarning("Cannot use auto-attribute fields with a null delegator");
        }
        
        if (delegator != null && entityName != null) {
            Map modelParamMap = new OrderedMap();
            try {            
                ModelEntity entity = delegator.getModelEntity(entityName);
                if (entity == null) {
                    throw new IllegalArgumentException("Could not find entity with name [" + entityName + "]");
                }
                Iterator fieldsIter = entity.getFieldsIterator();
                if (fieldsIter != null) {            
                    while (fieldsIter.hasNext()) {
                        ModelField field = (ModelField) fieldsIter.next();
                        if ((field.getIsPk() && includePk) || (!field.getIsPk() && includeNonPk)) {                        
                            ModelFieldType fieldType = delegator.getEntityFieldType(entity, field.getType());
                            
                            ModelParam param = new ModelParam();
                            param.entityName = entityName;
                            param.fieldName = field.getName();
                            param.name = field.getName();
                            param.type = fieldType.getJavaType();
                            param.mode = UtilXml.checkEmpty(autoElement.getAttribute("mode"));
                            param.optional = "true".equalsIgnoreCase(autoElement.getAttribute("optional")); // default to true
                            param.formDisplay = !"false".equalsIgnoreCase(autoElement.getAttribute("form-display")); // default to false                        
                            modelParamMap.put(field.getName(), param);
                        }
                    }
                    
                    // get the excludes list; and remove those from the map
                    List excludes = UtilXml.childElementList(autoElement, "exclude");
                    if (excludes != null) {                    
                        Iterator excludesIter = excludes.iterator();
                        while (excludesIter.hasNext()) {
                            Element exclude = (Element) excludesIter.next();
                            modelParamMap.remove(UtilXml.checkEmpty(exclude.getAttribute("field-name")));
                        }
                    }
                    
                    // now add in all the remaining params
                    Set keySet = modelParamMap.keySet();
                    Iterator setIter = keySet.iterator();
                    while (setIter.hasNext()) {
                        ModelParam thisParam = (ModelParam) modelParamMap.get(setIter.next()); 
                        //Debug.logInfo("Adding Param to " + service.name + ": " + thisParam.name + " [" + thisParam.mode + "] " + thisParam.type + " (" + thisParam.optional + ")", module);                       
                        service.addParam(thisParam);
                    }                    
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem loading auto-attribute [" + entityName + "]", module);
            }
        }
    }
            
    protected void createAttrDefs(Element baseElement, ModelService service) {
        // Add in the defined attributes (override the above defaults if specified)
        List paramElements = UtilXml.childElementList(baseElement, "attribute");
        Iterator paramIter = paramElements.iterator();

        while (paramIter.hasNext()) {
            Element attribute = (Element) paramIter.next();
            ModelParam param = new ModelParam();

            param.name = UtilXml.checkEmpty(attribute.getAttribute("name"));
            param.type = UtilXml.checkEmpty(attribute.getAttribute("type"));
            param.mode = UtilXml.checkEmpty(attribute.getAttribute("mode"));
            param.entityName = UtilXml.checkEmpty(attribute.getAttribute("entity-name"));
            param.fieldName = UtilXml.checkEmpty(attribute.getAttribute("field-name"));
            param.stringMapPrefix = UtilXml.checkEmpty(attribute.getAttribute("string-map-prefix"));
            param.formLabel = UtilXml.checkEmpty(attribute.getAttribute("form-label"));            
            param.optional = "true".equalsIgnoreCase(attribute.getAttribute("optional")); // default to true
            param.formDisplay = !"false".equalsIgnoreCase(attribute.getAttribute("form-display")); // default to false
            
            // set the entity name to the default if not specified
            if (param.entityName.length() == 0) {
                param.entityName = service.defaultEntityName;
            }   
            
            // set the field-name to the name if entity name is specified but no field-name
            if (param.fieldName.length() == 0 && param.entityName.length() > 0) {
                param.fieldName = param.name;         
            }
            
            service.addParam(param);
        }

        // Add the default optional parameters
        ModelParam def = null;

        // responseMessage
        def = new ModelParam();
        def.name = ModelService.RESPONSE_MESSAGE;
        def.type = "String";
        def.mode = "OUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
        // errorMessage
        def = new ModelParam();
        def.name = ModelService.ERROR_MESSAGE;
        def.type = "String";
        def.mode = "OUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
        // errorMessageList
        def = new ModelParam();
        def.name = ModelService.ERROR_MESSAGE_LIST;
        def.type = "java.util.List";
        def.mode = "OUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
        // successMessage
        def = new ModelParam();
        def.name = ModelService.SUCCESS_MESSAGE;
        def.type = "String";
        def.mode = "OUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
        // successMessageList
        def = new ModelParam();
        def.name = ModelService.SUCCESS_MESSAGE_LIST;
        def.type = "java.util.List";
        def.mode = "OUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
        // userLogin
        def = new ModelParam();
        def.name = "userLogin";
        def.type = "org.ofbiz.core.entity.GenericValue";
        def.mode = "INOUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
        // Locale
        def = new ModelParam();
        def.name = "locale";
        def.type = "java.util.Locale";
        def.mode = "INOUT";
        def.optional = true;
        def.internal = true;
        service.addParam(def);
    }
    
    protected void createOverrideDefs(Element baseElement, ModelService service) {
        List paramElements = UtilXml.childElementList(baseElement, "override");
        Iterator paramIter = paramElements.iterator();

        while (paramIter.hasNext()) {
            Element attribute = (Element) paramIter.next();
            String name = UtilXml.checkEmpty(attribute.getAttribute("name"));
            ModelParam param = service.getParam(name);
            boolean directToParams = true;
            if (param == null) {
                if (service.implServices.size() > 0 && !service.inheritedParameters) {                
                    // create a temp def to place in the ModelService
                    // this will get read when we read implemented services 
                    directToParams = false;               
                    param = new ModelParam();
                    param.name = name;
                } else {
                    Debug.logWarning("No parameter found for override parameter named: " + name, module);
                }
            }             
            
            if (param != null) {                                                        
                // set only modified values
                if (attribute.getAttribute("type") != null && attribute.getAttribute("type").length() > 0) {                
                    param.name = UtilXml.checkEmpty(attribute.getAttribute("type"));
                }
                if (attribute.getAttribute("mode") != null && attribute.getAttribute("mode").length() > 0) {                            
                    param.mode = UtilXml.checkEmpty(attribute.getAttribute("mode"));
                }
                if (attribute.getAttribute("entity-name") != null && attribute.getAttribute("entity-name").length() > 0) {
                   param.entityName = UtilXml.checkEmpty(attribute.getAttribute("entity-name"));
                }
                if (attribute.getAttribute("field-name") != null && attribute.getAttribute("field-name").length() > 0) {
                    param.fieldName = UtilXml.checkEmpty(attribute.getAttribute("field-name"));
                }
                if (attribute.getAttribute("form-label") != null && attribute.getAttribute("form-label").length() > 0) {                
                    param.formLabel = UtilXml.checkEmpty(attribute.getAttribute("form-label"));
                }
                if (attribute.getAttribute("optional") != null && attribute.getAttribute("optional").length() > 0) {                            
                    param.optional = "true".equalsIgnoreCase(attribute.getAttribute("optional")); // default to true
                    param.overrideOptional = true;
                }
                if (attribute.getAttribute("form-display") != null && attribute.getAttribute("form-display").length() > 0) {                
                    param.formDisplay = !"false".equalsIgnoreCase(attribute.getAttribute("form-display")); // default to false
                    param.overrideFormDisplay = true;
                }
                
                if (directToParams) {
                    service.addParam(param);
                } else {                  
                    service.overrideParameters.add(param);                    
                }
            }                                                                                      
        }        
    }

    protected Document getDocument(URL url) {
        if (url == null)
            return null;
        Document document = null;

        try {
            document = UtilXml.readXmlDocument(url, true);
        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;

            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return document;
    }
}
