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

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.config.GenericConfigException;
import org.ofbiz.core.config.MainResourceHandler;
import org.ofbiz.core.config.ResourceHandler;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.service.config.ServiceConfigUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilCache;
import org.ofbiz.core.util.UtilXml;
import org.w3c.dom.Element;

/**
 * Dispatcher Context
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class DispatchContext implements Serializable {

    public static final String module = DispatchContext.class.getName();

    protected static final String GLOBAL_KEY = "global.services";
    protected static UtilCache modelService = new UtilCache("service.ModelServices", 0, 0);

    protected transient LocalDispatcher dispatcher;
    protected ClassLoader loader;
    protected Collection readers;
    protected Map attributes;
    protected String name;

    /** 
     * Creates new DispatchContext
     * @param readers a collection of reader URLs
     * @param loader the classloader to use for dispatched services
     */
    public DispatchContext(String name, Collection readers, ClassLoader loader, LocalDispatcher dispatcher) {
        this.name = name;
        this.readers = readers;
        this.loader = loader;
        this.dispatcher = dispatcher;
        this.attributes = new HashMap();                
    }

    public void loadReaders() {
        Map localService = addReaders(readers);
        if (localService != null) {
            modelService.put(name, localService);
        }
        
        Map globalService = addGlobal();
        if (globalService != null) {
            modelService.put(GLOBAL_KEY, globalService);
        }
    }

    /** 
     * Returns the service attribute for the given name, or null if there is no attribute by that name.
     * @param name a String specifying the name of the attribute
     * @return an Object conatining the value of the attribute, or null if there is no attribute by that name.
     */
    public Object getAttribute(String name) {
        if (attributes.containsKey(name))
            return attributes.get(name);
        return null;
    }

    /** 
     * Binds an object to a given attribute name in this context.
     * @param name a String specifying the name of the attribute
     * @param object an Object representing the attribute to be bound.
     */
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    /** 
     * Gets the classloader of this context
     * @return ClassLoader of the context
     */
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    /** 
     * Gets the collection of readers associated with this context
     * @return Collection of reader URLs
     */
    public Collection getReaders() {
        return readers;
    }

    /** 
     * Gets the name of the local dispatcher
     * @return String name of the LocalDispatcher object
     */
    public String getName() {
        return name;
    }
    
    /**
     * Uses an existing map of name value pairs and extracts the keys which are used in serviceName
     * Note: This goes not guarantee the context will be 100% valid, there may be missing fields 
     * @param serviceName The name of the service to obtain parameters for
     * @param mode The mode to use for building the new map (i.e. can be IN or OUT) 
     * @param context The initial set of values to pull from     
     * @return Map contains any valid values
     * @throws GenericServiceException
     */
    public Map makeValidContext(String serviceName, String mode, Map context) throws GenericServiceException {        
        ModelService model = this.getModelService(serviceName);
        return makeValidContext(model, mode, context);
        
    }
    
    /**
     * Uses an existing map of name value pairs and extracts the keys which are used in serviceName
     * Note: This goes not guarantee the context will be 100% valid, there may be missing fields 
     * @param model The ModelService object of the service to obtain parameters for
     * @param mode The mode to use for building the new map (i.e. can be IN or OUT)
     * @param context The initial set of values to pull from     
     * @return Map contains any valid values
     * @throws GenericServiceException
     */
    public Map makeValidContext(ModelService model, String mode, Map context) throws GenericServiceException {
        Map newContext = null;
        
        int modeInt = 0;
        if (mode.equalsIgnoreCase("in")) {
            modeInt = 1;
        } else if (mode.equalsIgnoreCase("out")) {
            modeInt = 2;
        }        
                        
        if (model == null) {
            throw new GenericServiceException("Model service is null! Should never happen.");        
        } else {
            switch (modeInt) {
                case 2:
                    newContext = model.makeValid(context, ModelService.OUT_PARAM, true);
                    break;
                case 1:
                    newContext = model.makeValid(context, ModelService.IN_PARAM, true);
                    break;
                default:
                    throw new GenericServiceException("Invalid mode, should be either IN or OUT");
            }
            return newContext;
        }        
    }

    /** 
     * Gets the ModelService instance that corresponds to given the name
     * @param serviceName Name of the service
     * @return GenericServiceModel that corresponds to the serviceName
     */
    public ModelService getModelService(String serviceName) throws GenericServiceException {
        ModelService retVal = getLocalModelService(serviceName);
        if (retVal == null) {
            retVal = getGlobalModelService(serviceName);
        }
        
        if (retVal == null) {
            throw new GenericServiceException("Cannot locate service by name (" + serviceName + ")");
        }
        
        return retVal;                
    }
    
    private ModelService getLocalModelService(String serviceName) throws GenericServiceException {
        Map serviceMap = (Map) modelService.get(name);
        if (serviceMap == null) {
            synchronized (this) {
                serviceMap = (Map) modelService.get(name);
                if (serviceMap == null) {
                    serviceMap = addReaders(readers);
                    if (serviceMap != null)
                        modelService.put(name, serviceMap);
                }
            }
        }
        
        ModelService retVal = null;
        if (serviceMap != null) {
            retVal = (ModelService) serviceMap.get(serviceName); 
            if (retVal != null && !retVal.inheritedParameters()) {
                retVal.interfaceUpdate(this);     
            }
        }
        
        return retVal;
    }

    private ModelService getGlobalModelService(String serviceName) throws GenericServiceException {
        Map serviceMap = (Map) modelService.get(GLOBAL_KEY);
        if (serviceMap == null) {
            synchronized (this) {
                serviceMap = (Map) modelService.get(GLOBAL_KEY);
                if (serviceMap == null) {
                    serviceMap = addGlobal();
                    if (serviceMap != null)
                        modelService.put(GLOBAL_KEY, serviceMap);
                }
            }
        }

        ModelService retVal = null;
        if (serviceMap != null) {
            retVal = (ModelService) serviceMap.get(serviceName);
            if (retVal != null && !retVal.inheritedParameters()) {
                retVal.interfaceUpdate(this);
            }
        }
        
        return retVal;
    }

    /** 
     * Gets the LocalDispatcher used with this context
     * @return LocalDispatcher that was used to create this context
     */
    public LocalDispatcher getDispatcher() {
        return this.dispatcher;
    }

    /** 
     * Sets the LocalDispatcher used with this context
     * @param dispatcher The LocalDispatcher to re-assign to this context
     */
    public void setDispatcher(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /** 
     * Gets the GenericDelegator associated with this context/dispatcher
     * @return GenericDelegator associated with this context
     */
    public GenericDelegator getDelegator() {
        return dispatcher.getDelegator();
    }

    /** 
     * Gets the Security object associated with this dispatcher
     * @return Security object associated with this dispatcher
     */
    public Security getSecurity() {
        return dispatcher.getSecurity();
    }

    private Map addReaders(Collection readerURLs) {
        Map serviceMap = new HashMap();

        if (readerURLs == null)
            return null;
        Iterator urlIter = readerURLs.iterator();

        while (urlIter.hasNext()) {
            URL readerURL = (URL) urlIter.next();

            serviceMap.putAll(addReader(readerURL));
        }
        return serviceMap;
    }

    private Map addReader(URL readerURL) {
        if (readerURL == null) {
            Debug.logError("Cannot add reader with a null reader URL", module);
            return null;
        }

        ModelServiceReader reader = ModelServiceReader.getModelServiceReader(readerURL, this);

        if (reader == null) {
            Debug.logError("Could not load the reader for the reader URL " + readerURL, module);
            return null;
        }

        Map serviceMap = reader.getModelServices();

        return serviceMap;
    }

    private Map addReader(ResourceHandler handler) {
        ModelServiceReader reader = ModelServiceReader.getModelServiceReader(handler, this);

        if (reader == null) {
            Debug.logError("Could not load the reader for " + handler, module);
            return null;
        }

        Map serviceMap = reader.getModelServices();

        return serviceMap;
    }

    private Map addGlobal() {
        Map globalMap = new HashMap();

        Element rootElement = null;

        try {
            rootElement = ServiceConfigUtil.getXmlRootElement();
        } catch (GenericConfigException e) {
            Debug.logError(e, "Error getting Service Engine XML root element", module);
            return null;
        }

        List globalServicesElements = UtilXml.childElementList(rootElement, "global-services");
        Iterator gseIter = globalServicesElements.iterator();

        while (gseIter.hasNext()) {
            Element globalServicesElement = (Element) gseIter.next();
            ResourceHandler handler = new MainResourceHandler(
                    ServiceConfigUtil.SERVICE_ENGINE_XML_FILENAME, globalServicesElement);

            Map servicesMap = addReader(handler);

            if (servicesMap != null) {
                globalMap.putAll(servicesMap);
            }
        }

        return globalMap;
    }
}

