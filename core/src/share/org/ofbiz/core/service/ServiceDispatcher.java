/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.scheduler.*;
import org.ofbiz.core.security.*;

/**
 * <p><b>Title:</b> Global Service Dispatcher
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
 *@created    November 7, 2001
 *@version    1.0
 */
public class ServiceDispatcher {

    protected static Map dispatchers = new HashMap();
    protected GenericDelegator delegator;
    protected Security security;
    protected Map localContext;
    protected JobManager jm;

    public ServiceDispatcher(GenericDelegator delegator) {
        Debug.logInfo("[ServiceDispatcher] : Creating new instance.");
        this.delegator = delegator;
        this.localContext = new HashMap();
        this.jm = new JobManager(this, this.delegator);
        if (delegator != null)
            this.security = new Security(delegator);
    }

    /** Returns a pre-registered instance of the ServiceDispatcher associated with this delegator.
     *@param delegator the local delegator
     *@return A reference to this global ServiceDispatcher
     */
    public static ServiceDispatcher getInstance(String name, GenericDelegator delegator) {
        ServiceDispatcher sd = getInstance(null, null, delegator);
        if (!sd.containsContext(name))
            return null;
        return sd;
    }

    /** Returns an instance of the ServiceDispatcher associated with this delegator and registers the loader.
     *@param name the local dispatcher
     *@param loader classloader of the local dispatcher
     *@param delegator the local delegator
     *@return A reference to this global ServiceDispatcher
     */
    public static ServiceDispatcher getInstance(String name, DispatchContext context, GenericDelegator delegator) {
        ServiceDispatcher sd = null;
        sd = (ServiceDispatcher) dispatchers.get(delegator);
        if (sd == null) {
            synchronized (ServiceDispatcher.class) {
                sd = (ServiceDispatcher) dispatchers.get(delegator);
                if (sd == null) {
                    sd = new ServiceDispatcher(delegator);
                    dispatchers.put(delegator, sd);
                }
            }
        }
        if (name != null && context != null)
            sd.register(name, context);
        return sd;
    }

    /** Registers the loader with this ServiceDispatcher
     *@param name the local dispatcher
     *@param loader the classloader of the local dispatcher
     */
    public void register(String name, DispatchContext context) {
        Debug.logInfo("[ServiceDispatcher.register] : Registered dispatcher: " +
                      context.getName());
        this.localContext.put(name, context);
    }

    /** Run the service synchronously and return the result
     *@param context Map of name, value pairs composing the context
     *@return Map of name, value pairs composing the result
     */
    public Map runSync(String localName, ModelService service, Map context)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context);
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

    /** Run the service synchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runSyncIgnore(String localName, ModelService service, Map context)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context);
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
        engine.runSyncIgnore(service, context);
    }

    /** Run the service asynchronously, passing an instance of GenericRequester that will receive the result
     *@param context Map of name, value pairs composing the context
     *@param requester Object implementing GenericRequester interface which will receive the result
     */
    public void runAsync(String localName, ModelService service, Map context, GenericRequester requester)
            throws ServiceAuthException,
            GenericServiceException {
        context = checkAuth(localName, context);
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
        engine.runAsync(service, context, requester);
    }

    /** Run the service asynchronously and IGNORE the result
     *@param context Map of name, value pairs composing the context
     */
    public void runAsync(String localName, ModelService service, Map context)
            throws ServiceAuthException, GenericServiceException {
        context = checkAuth(localName, context);
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
        engine.runAsync(service, context);
    }

    /** Gets the GenericEngine instance that corresponds to the given name
     *@param engineName Name of the engine
     *@return GenericEngine instance that corresponds to the engineName
     */
    public GenericEngine getGenericEngine(String engineName) throws GenericServiceException {
        GenericEngine engine =
                GenericEngineFactory.getGenericEngine(engineName, this);
        return engine;
    }

    /** Gets the JobManager associated with this dispatcher
     *@return JobManager that is associated with this dispatcher
     */
    public JobManager getJobManager() {
        return this.jm;
    }

    /** Gets the GenericDelegator associated with this dispatcher
     *@return GenericDelegator associated with this dispatcher
     */
    public GenericDelegator getDelegator() {
        return this.delegator;
    }

    /** Gets the Security object associated with this dispatcher
     *@return Security object associated with this dispatcher
     */
    public Security getSecurity() {
        return this.security;
    }

    /** Gets the local dispatcher from a name
     *@param String name of the loader to find.
     */
    public DispatchContext getLocalContext(String name) {
        if (localContext.containsKey(name))
            return (DispatchContext) localContext.get(name);
        return null;
    }

    /** Test if this dispatcher instance contains the local context.
     *@param String name of the local context
     *@returns True if the local context is found in this dispatcher.
     */
    public boolean containsContext(String name) {
        return localContext.containsKey(name);
    }

    // checks if parameters were passed for authentication
    private Map checkAuth(String localName, Map context) throws GenericServiceException {
        // check for a username/password
        if (context.containsKey("login.username")) {
            String username = (String) context.get("login.username");
            if (context.containsKey("login.password")) {
                String password = (String) context.get("login.password");
                context.put("userLogin",
                            getLoginObject(localName, username, password));
                context.remove("login.password");
            } else
                context.put("userLogin", getLoginObject(localName, username, null));
            context.remove("login.username");
        }
        return context;
    }

    // gets a value object from name/password pair
    private GenericValue getLoginObject(String localName, String username, String password)
            throws GenericServiceException {
        String service = UtilProperties.getPropertyValue("servicesengine", "auth.service");
        Map context = UtilMisc.toMap("login.username", username, "login.password",
                                     password);

        if (service == null)
            throw new GenericServiceException("No Authentication Service Defined");

        Debug.logVerbose("[ServiceDispathcer.authenticate] : Invoking UserLogin Service");

        // Manually invoke the service
        DispatchContext dctx = getLocalContext(localName);
        ModelService model = dctx.getModelService(service);
        GenericEngine engine = getGenericEngine(model.engineName);
        engine.setLoader(localName);
        Map result = engine.runSync(model, context);

        GenericValue value = null;
        if (result.containsKey("userLogin") && result.get("userLogin") != null)
            value = (GenericValue) result.get("userLogin");

        return value;
    }
}


