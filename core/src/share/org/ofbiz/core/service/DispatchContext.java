/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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

import java.net.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.security.*;

/**
 * <p><b>Title:</b> Dispatcher Context
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 8, 2001
 *@version    1.0
 */
public class DispatchContext {

    protected static final String GLOBAL_KEY = "global.services";
    protected static UtilCache modelService = new UtilCache("ModelServices", 0, 0);

    protected String name;
    protected Map attributes;
    protected Collection readers;
    protected ClassLoader loader;
    protected LocalDispatcher dispatcher;

    /** Creates new DispatchContext
     *@param readers a collection of reader URLs
     *@param loader the classloader to use for dispatched services
     */
    public DispatchContext(String name, Collection readers, ClassLoader loader, LocalDispatcher dispatcher) {
        this.name = name;
        this.readers = readers;
        this.loader = loader;
        this.dispatcher = dispatcher;
        this.attributes = new HashMap();
        Map localService = addReaders(readers);
        if (localService != null)
            modelService.put(name, localService);
        Map globalService = addGlobal();
        if (globalService != null)
            modelService.put(GLOBAL_KEY, globalService);
    }

    /** Returns the service attribute for the given name, or null if there is no attribute by that name.
     *@param name a String specifying the name of the attribute
     *@return an Object conatining the value of the attribute, or null if there is no attribute by that name.
     */
    public Object getAttribute(String name) {
        if (attributes.containsKey(name))
            return attributes.get(name);
        return null;
    }

    /** Binds an object to a given attribute name in this context.
     *@param name a String specifying the name of the attribute
     *@param object an Object representing the attribute to be bound.
     */
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    /** Gets the classloader of this context
     *@return ClassLoader of the context
     */
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    /** Gets the collection of readers associated with this context
     *@return Collection of reader URLs
     */
    public Collection getReaders() {
        return readers;
    }

    /** Gets the name of the local dispatcher
     *@return String name of the LocalDispatcher object
     */
    public String getName() {
        return name;
    }

    /** Gets the GenericServiceModel instance that corresponds to given the name
     *@param serviceName Name of the service
     *@return GenericServiceModel that corresponds to the serviceName
     */
    public ModelService getModelService(String serviceName) throws GenericServiceException {
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
        if (serviceMap != null)
            retVal = (ModelService) serviceMap.get(serviceName);
        if (retVal == null)
            retVal = getGlobalModelService(serviceName);
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

        ModelService retVal = (ModelService) serviceMap.get(serviceName);
        if (retVal == null)
            throw new GenericServiceException("Cannot locate service by name");
        return retVal;
    }

    /** Gets the LocalDispatcher used to create this context
     *@return LocalDispatcher that was used to create this context
     */
    public LocalDispatcher getDispatcher() {
        return this.dispatcher;
    }

    /** Sets the LocalDispatcher used with this context
     *@param dispatcher The LocalDispatcher to re-assign to this context
     */
    public void setDispatcher(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /** Gets the GenericDelegator associated with this context/dispatcher
     *@return GenericDelegator associated with this context
     */
    public GenericDelegator getDelegator() {
        return dispatcher.getDelegator();
    }

    /** Gets the Security object associated with this dispatcher
     *@return Security object associated with this dispatcher
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
        if (readerURL == null)
            return null;
        ModelServiceReader reader =
                ModelServiceReader.getModelServiceReader(readerURL);
        if (reader == null)
            return null;
        Map serviceMap = reader.getModelServices();
        if (serviceMap == null)
            return null;
        else
            return serviceMap;
    }

    private Map addGlobal() {
        Map globalMap = new HashMap();
        String path = UtilProperties.getPropertyValue("servicesengine", "global.paths");
        Debug.logVerbose("[addGlobal] paths: " + path);
        if (path == null)
            return null;
        List paths = StringUtil.split(path, ";");
        if (paths == null || paths.size() == 0)
            return null;
        Iterator i = paths.iterator();
        while (i.hasNext()) {
            URL readerURL = UtilURL.fromFilename((String) i.next());
            if (readerURL != null)
                globalMap.putAll(addReader(readerURL));
            else
                Debug.logInfo("[DispatchContext.addGlobal] : URL returned a 'null' service map");
        }
        return globalMap;
    }
}

