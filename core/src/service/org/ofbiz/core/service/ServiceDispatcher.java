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

import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.job.*;
import org.ofbiz.core.service.jms.*;
import org.ofbiz.core.service.config.*;
import org.ofbiz.core.service.engine.*;
import org.ofbiz.core.security.*;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Global Service Dispatcher
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 7, 2001
 *@version    1.0
 */
public class ServiceDispatcher {

    public static final String module = ServiceDispatcher.class.getName();

    protected static Map dispatchers = new HashMap();
    protected GenericDelegator delegator;
    protected Security security;
    protected Map localContext;
    protected JobManager jm;
    protected List jmsListeners;

    public ServiceDispatcher(GenericDelegator delegator) {
        Debug.logInfo("[ServiceDispatcher] : Creating new instance.", module);
        this.delegator = delegator;
        this.localContext = new HashMap();
        this.jm = new JobManager(this, this.delegator);
        if (delegator != null) {
            this.security = new Security(delegator);
            jmsListeners = loadListeners(this);
        }
    }

    /**
     * Returns a pre-registered instance of the ServiceDispatcher associated with this delegator.
     * @param delegator the local delegator
     * @return A reference to this global ServiceDispatcher
     */
    public static ServiceDispatcher getInstance(String name, GenericDelegator delegator) {
        ServiceDispatcher sd = getInstance(null, null, delegator);
        if (!sd.containsContext(name))
            return null;
        return sd;
    }

    /**
     * Returns an instance of the ServiceDispatcher associated with this delegator and registers the loader.
     * @param name the local dispatcher
     * @param loader classloader of the local dispatcher
     * @param delegator the local delegator
     * @return A reference to this global ServiceDispatcher
     */
    public static ServiceDispatcher getInstance(String name, DispatchContext context, GenericDelegator delegator) {
        ServiceDispatcher sd = null;
        sd = (ServiceDispatcher) dispatchers.get(delegator.getDelegatorName());
        if (sd == null) {
            synchronized (ServiceDispatcher.class) {
                if (Debug.verboseOn()) Debug.logVerbose("[ServiceDispatcher.getInstance] : No instance found (" + delegator.getDelegatorName() + ").", module);
                sd = (ServiceDispatcher) dispatchers.get(delegator.getDelegatorName());
                if (sd == null) {
                    sd = new ServiceDispatcher(delegator);
                    dispatchers.put(delegator.getDelegatorName(), sd);
                }
            }
        }
        if (name != null && context != null)
            sd.register(name, context);
        return sd;
    }

    /**
     * Registers the loader with this ServiceDispatcher
     * @param name the local dispatcher
     * @param loader the classloader of the local dispatcher
     */
    public void register(String name, DispatchContext context) {
        if (Debug.infoOn()) Debug.logInfo("[ServiceDispatcher.register] : Registered dispatcher: " +
                      context.getName(), module);
        this.localContext.put(name, context);
    }

    /**
     * Run the service synchronously and return the result.
     * @param localName Name of the context to use.
     * @param service Service model object.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws ServiceAuthException
     * @throws GenericServiceException
     */
    public Map runSync(String localName, ModelService service, Map context)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context, service);
        Object userLogin = context.get("userLogin");
        if (service.auth && userLogin == null)
            throw new ServiceAuthException("User authorization is required for this service");
        GenericEngine engine = getGenericEngine(service.engineName);
        engine.setLoader(localName);

        if (Debug.verboseOn()) Debug.logVerbose("[ServiceDispatcher.runSync] : invoking service [" + service.location + "/" + service.invoke +
                "] (" + service.engineName + ")", module);

        // validate the context
        if (service.validate) {
            try {
                service.validate(context, ModelService.IN_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Context (in runSync) does not match expected requirements: ", e);
            }
        }
        Map result = engine.runSync(service, context);
        // validate the result
        if (service.validate) {
            try {
                service.validate(result, ModelService.OUT_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Result (in runSync) does not match expected requirements: ", e);
            }
        }

        return result;
    }

    /**
     * Run the service synchronously and IGNORE the result.
     * @param localName Name of the context to use.
     * @param service Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws ServiceAuthException
     * @throws GenericServiceException
     */
    public void runSyncIgnore(String localName, ModelService service, Map context)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context, service);
        Object userLogin = context.get("userLogin");
        if (service.auth && userLogin == null)
            throw new ServiceAuthException("User authorization is required for this service");
        GenericEngine engine = getGenericEngine(service.engineName);
        engine.setLoader(localName);

        // validate the context
        if (service.validate) {
            try {
                service.validate(context, ModelService.IN_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Context (in runSync) does not match expected requirements: ", e);
            }
        }

        if (Debug.verboseOn()) Debug.logVerbose("[ServiceDispatcher.runSyncIgnore] : invoking service [" + service.location + "/" + service.invoke +
                "] (" + service.engineName + ")", module);

        engine.runSyncIgnore(service, context);
    }

    /**
     * Run the service asynchronously, passing an instance of GenericRequester that will receive the result.
     * @param localName Name of the context to use.
     * @param service Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param requester Object implementing GenericRequester interface which will receive the result.
     * @param persist True for store/run; False for run.
     * @throws ServiceAuthException
     * @throws GenericServiceException
     */
    public void runAsync(String localName, ModelService service, Map context, GenericRequester requester, boolean persist)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context, service);
        Object userLogin = context.get("userLogin");
        if (service.auth && userLogin == null)
            throw new ServiceAuthException("User authorization is required for this service");
        GenericEngine engine = getGenericEngine(service.engineName);
        engine.setLoader(localName);

        // validate the context
        if (service.validate) {
            try {
                service.validate(context, ModelService.IN_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Context (in runAsync) does not match expected requirements: ", e);
            }
        }

        if (Debug.verboseOn()) Debug.logVerbose("[ServiceDispatcher.runAsync] : invoking service [" + service.location + "/" + service.invoke +
                "] (" + service.engineName + ")", module);

        engine.runAsync(service, context, requester, persist);
    }

    /**
     * Run the service asynchronously and IGNORE the result.
     * @param localName Name of the context to use.
     * @param service Service model object.
     * @param context Map of name, value pairs composing the context.
     * @param persist True for store/run; False for run.
     * @throws ServiceAuthException
     * @throws GenericServiceException
     */
    public void runAsync(String localName, ModelService service, Map context, boolean persist)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context, service);
        Object userLogin = context.get("userLogin");
        if (service.auth && userLogin == null)
            throw new ServiceAuthException("User authorization is required for this service");
        GenericEngine engine = getGenericEngine(service.engineName);
        engine.setLoader(localName);

        // validate the context
        if (service.validate) {
            try {
                service.validate(context, ModelService.IN_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Context (in runAsync) does not match expected requirements: ", e);
            }
        }

        if (Debug.verboseOn()) Debug.logVerbose("[ServiceDispatcher.runAsync] : invoking service [" + service.location + "/" + service.invoke +
                "] (" + service.engineName + ")", module);

        engine.runAsync(service, context, persist);
    }

    /**
     * Gets the GenericEngine instance that corresponds to the given name
     * @param engineName Name of the engine
     * @return GenericEngine instance that corresponds to the engineName
     */
    public GenericEngine getGenericEngine(String engineName) throws GenericServiceException {
        return GenericEngineFactory.getGenericEngine(engineName, this);
    }

    /**
     * Gets the JobManager associated with this dispatcher
     * @return JobManager that is associated with this dispatcher
     */
    public JobManager getJobManager() {
        return this.jm;
    }

    /**
     * Gets the GenericDelegator associated with this dispatcher
     * @return GenericDelegator associated with this dispatcher
     */
    public GenericDelegator getDelegator() {
        return this.delegator;
    }

    /**
     * Gets the Security object associated with this dispatcher
     * @return Security object associated with this dispatcher
     */
    public Security getSecurity() {
        return this.security;
    }

    /**
     * Gets the local dispatcher from a name
     * @param String name of the loader to find.
     */
    public DispatchContext getLocalContext(String name) {
        if (localContext.containsKey(name))
            return (DispatchContext) localContext.get(name);
        return null;
    }

    /**
     * Test if this dispatcher instance contains the local context.
     * @param String name of the local context
     * @returns True if the local context is found in this dispatcher.
     */
    public boolean containsContext(String name) {
        return localContext.containsKey(name);
    }

    // checks if parameters were passed for authentication
    private Map checkAuth(String localName, Map context, ModelService origService) throws GenericServiceException {
        String service = ServiceConfigUtil.getElementAttr("authorization", "service-name");
        if (service == null) {
            throw new GenericServiceException("No Authentication Service Defined");
        }
        if (service.equals(origService.name)) {
            //manually calling the auth service, don't continue...
            return context;
        }

        if (context.containsKey("login.username")) {
            // check for a username/password, if there log the user in and make the userLogin object
            String username = (String) context.get("login.username");
            if (context.containsKey("login.password")) {
                String password = (String) context.get("login.password");
                context.put("userLogin", getLoginObject(service, localName, username, password));
                context.remove("login.password");
            } else {
                context.put("userLogin", getLoginObject(service, localName, username, null));
            }
            context.remove("login.username");
        } else {
            //if a userLogin object is there, make sure the given username/password exists in our local database
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            if (userLogin != null) {
                GenericValue newUserLogin = getLoginObject(service, localName, userLogin.getString("userLoginId"), userLogin.getString("currentPassword"));
                if (newUserLogin == null) {
                    //uh oh, couldn't validate that one...
                    //we'll have to remove it from the incoming context which will cause an auth error later if auth is required
                    context.remove("userLogin");
                }
            }
        }
        return context;
    }

    // gets a value object from name/password pair
    private GenericValue getLoginObject(String service, String localName, String username, String password)
            throws GenericServiceException {
        Map context = UtilMisc.toMap("login.username", username, "login.password", password, "isServiceAuth", new Boolean(true));
        if (Debug.verboseOn()) Debug.logVerbose("[ServiceDispathcer.authenticate] : Invoking UserLogin Service", module);

        // Manually invoke the service
        DispatchContext dctx = getLocalContext(localName);
        ModelService model = dctx.getModelService(service);
        GenericEngine engine = getGenericEngine(model.engineName);
        engine.setLoader(localName);
        Map result = engine.runSync(model, context);

        GenericValue value = (GenericValue) result.get("userLogin");
        return value;
    }

    // Load the JMS listeners
    private List loadListeners(ServiceDispatcher dispatcher) {
        List listeners = new ArrayList();
        try {
            Element rootElement = ServiceConfigUtil.getXmlRootElement();
            NodeList nodeList = rootElement.getElementsByTagName("jms-service");
            if (Debug.infoOn()) Debug.logInfo("[ServiceDispatcher] : Loading JMS Listeners.", module);
            for (int i = 0; i < nodeList.getLength(); i++) {
                try {
                    Element element = (Element) nodeList.item(i);
                    Object o = JMSListenerFactory.getMessageListener(element, dispatcher);
                    if (o != null)
                        listeners.add(o);
                } catch (GenericServiceException gse) {
                    Debug.logError(gse, "Cannot load message listener for position [" + i + "].", module);
                } catch (Exception e) {
                    Debug.logError(e, "Uncaught exception.", module);
                }
            }
        } catch (org.ofbiz.core.config.GenericConfigException gce) {
            Debug.logError(gce, "Cannot get serviceengine.xml root element.", module);
        } catch (Exception e) {
            Debug.logError(e, "Uncaught exception.", module);
        }
        return listeners;
    }

}
