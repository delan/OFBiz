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
import org.ofbiz.core.service.eca.*;
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
    protected JmsListenerFactory jlf;

    public ServiceDispatcher(GenericDelegator delegator) {
        Debug.logInfo("[ServiceDispatcher] : Creating new instance.", module);
        ECAUtil.readConfig();
        this.delegator = delegator;
        this.localContext = new HashMap();
        if (delegator != null) {

            try {
                this.security = SecurityFactory.getInstance(delegator);
            } catch (SecurityConfigurationException e) {
                Debug.logError(e, "[ServiceDispatcher.init] : No instance of security imeplemtation found.", module);
            }
            this.jm = new JobManager(this, this.delegator);
            this.jlf = new JmsListenerFactory(this);
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
     * @param context the context of the local dispatcher
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
     * @param context the context of the local dispatcher
     */
    public void register(String name, DispatchContext context) {
        if (Debug.infoOn()) Debug.logInfo("[ServiceDispatcher.register] : Registered dispatcher: " + context.getName(), module);
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
    public Map runSync(String localName, ModelService service, Map context) throws GenericServiceException {
        // start the transaction
        boolean beganTrans = false;

        if (service.useTransaction) {
            try {
                beganTrans = TransactionUtil.begin();
            } catch (GenericTransactionException te) {
                throw new GenericServiceException("Cannot start the transaction.", te.getNested());
            }
        }

        try {
            // get eventMap once for all calls for speed, don't do event calls if it is null
            Map eventMap = ECAUtil.getServiceEventMap(service.name);

            // pre-auth ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "auth", (DispatchContext) localContext.get(localName), context, null, false);

            context = checkAuth(localName, context, service);
            Object userLogin = context.get("userLogin");

            if (service.auth && userLogin == null)
                throw new ServiceAuthException("User authorization is required for this service");

            // setup the engine
            GenericEngine engine = getGenericEngine(service.engineName);

            engine.setLoader(localName);

            // pre-validate ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "in-validate", (DispatchContext) localContext.get(localName), context, null, false);

            // validate the context
            if (service.validate) {
                try {
                    service.validate(context, ModelService.IN_PARAM);
                } catch (ServiceValidationException e) {
                    throw new GenericServiceException("Context (in runSync) does not match expected requirements: ", e);
                }
            }

            // pre-invoke ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "invoke", (DispatchContext) localContext.get(localName), context, null, false);

            if (Debug.verboseOn()) {
                Debug.logVerbose("[ServiceDispatcher.runSync] : invoking service [" + service.location +
                    "/" + service.invoke + "] (" + service.engineName + ")", module);
            }

            // ===== invoke the service =====
            Map result = engine.runSync(service, context);

            // if anything but the error response, is not an error
            boolean isError = ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE));

            // create a new context with the results to pass to ECA services; necessary because caller may reuse this context
            Map ecaContext = new HashMap(context);

            // copy all results: don't worry parameters that aren't allowed won't be passed to the ECA services
            ecaContext.putAll(result);

            // validate the result
            if (service.validate) {
                if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "out-validate", (DispatchContext) localContext.get(localName), ecaContext, result, isError);
                try {
                    service.validate(result, ModelService.OUT_PARAM);
                } catch (ServiceValidationException e) {
                    throw new GenericServiceException("Result (in runSync) does not match expected requirements: ", e);
                }
            }

            // pre-commit ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "commit", (DispatchContext) localContext.get(localName), ecaContext, result, isError);

            // commit the transaction
            try {
                TransactionUtil.commit(beganTrans);
            } catch (GenericTransactionException te) {
                Debug.logError(te, "Cannot commit transaction", module);
            }

            // pre-return ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "return", (DispatchContext) localContext.get(localName), ecaContext, result, isError);

            return result;
        } catch (GenericServiceException e) {
            try {
                TransactionUtil.rollback(beganTrans);
            } catch (GenericTransactionException te) {
                Debug.logError(te, "Cannot rollback transaction", module);
            }
            throw e;
        }
    }

    /**
     * Run the service synchronously and IGNORE the result.
     * @param localName Name of the context to use.
     * @param service Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws ServiceAuthException
     * @throws GenericServiceException
     */
    public void runSyncIgnore(String localName, ModelService service, Map context) throws GenericServiceException {
        // start the transaction
        boolean beganTrans = false;

        if (service.useTransaction) {
            try {
                beganTrans = TransactionUtil.begin();
            } catch (GenericTransactionException te) {
                throw new GenericServiceException("Cannot start the transaction.", te.getNested());
            }
        }

        try {
            // get eventMap once for all calls for speed, don't do event calls if it is null
            Map eventMap = ECAUtil.getServiceEventMap(service.name);

            // pre-auth ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "auth", (DispatchContext) localContext.get(localName), context, null, false);

            context = checkAuth(localName, context, service);
            Object userLogin = context.get("userLogin");

            if (service.auth && userLogin == null)
                throw new ServiceAuthException("User authorization is required for this service");

            // setup the engine
            GenericEngine engine = getGenericEngine(service.engineName);

            engine.setLoader(localName);

            // pre-validate ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "in-validate", (DispatchContext) localContext.get(localName), context, null, false);

            // validate the context
            if (service.validate) {
                try {
                    service.validate(context, ModelService.IN_PARAM);
                } catch (ServiceValidationException e) {
                    throw new GenericServiceException("Context (in runSync) does not match expected requirements: ", e);
                }
            }

            // pre-invoke ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "invoke", (DispatchContext) localContext.get(localName), context, null, false);

            if (Debug.verboseOn()) {
                Debug.logVerbose("[ServiceDispatcher.runSyncIgnore] : invoking service [" + service.location + "/" + service.invoke +
                    "] (" + service.engineName + ")", module);
            }

            engine.runSyncIgnore(service, context);

            // pre-commit ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "commit", (DispatchContext) localContext.get(localName), context, null, false);

            // commit the transaction
            try {
                TransactionUtil.commit(beganTrans);
            } catch (GenericTransactionException te) {
                Debug.logError(te, "Cannot commit transaction", module);
            }

            // pre-return ECA
            if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "return", (DispatchContext) localContext.get(localName), context, null, false);
        } catch (GenericServiceException e) {
            try {
                TransactionUtil.rollback(beganTrans);
            } catch (GenericTransactionException te) {
                Debug.logError(te, "Cannot rollback transaction", module);
            }
            throw e;
        }
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
    public void runAsync(String localName, ModelService service, Map context, GenericRequester requester, boolean persist) throws GenericServiceException {
        // get eventMap once for all calls for speed, don't do event calls if it is null
        Map eventMap = ECAUtil.getServiceEventMap(service.name);

        // pre-auth ECA
        if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "auth", (DispatchContext) localContext.get(localName), context, null, false);

        context = checkAuth(localName, context, service);
        Object userLogin = context.get("userLogin");

        if (service.auth && userLogin == null)
            throw new ServiceAuthException("User authorization is required for this service");

        // setup the engine
        GenericEngine engine = getGenericEngine(service.engineName);

        engine.setLoader(localName);

        // pre-validate ECA
        if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "in-validate", (DispatchContext) localContext.get(localName), context, null, false);

        // validate the context
        if (service.validate) {
            try {
                service.validate(context, ModelService.IN_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Context (in runAsync) does not match expected requirements: ", e);
            }
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("[ServiceDispatcher.runAsync] : invoking service [" + service.location + "/" + service.invoke +
                "] (" + service.engineName + ")", module);
        }

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
    public void runAsync(String localName, ModelService service, Map context, boolean persist) throws GenericServiceException {
        // get eventMap once for all calls for speed, don't do event calls if it is null
        Map eventMap = ECAUtil.getServiceEventMap(service.name);

        // pre-auth ECA
        if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "auth", (DispatchContext) localContext.get(localName), context, null, false);

        context = checkAuth(localName, context, service);
        Object userLogin = context.get("userLogin");

        if (service.auth && userLogin == null)
            throw new ServiceAuthException("User authorization is required for this service");

        // setup the engine
        GenericEngine engine = getGenericEngine(service.engineName);

        engine.setLoader(localName);

        // pre-validate ECA
        if (eventMap != null) ECAUtil.evalConditions(service.name, eventMap, "in-validate", (DispatchContext) localContext.get(localName), context, null, false);

        // validate the context
        if (service.validate) {
            try {
                service.validate(context, ModelService.IN_PARAM);
            } catch (ServiceValidationException e) {
                throw new GenericServiceException("Context (in runSync) does not match expected requirements: ", e);
            }
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("[ServiceDispatcher.runAsync] : invoking service [" + service.location + "/" + service.invoke +
                "] (" + service.engineName + ")", module);
        }

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
     * Gets the JmsListenerFactory which holds the message listeners.
     * @return JmsListenerFactory
     */
    public JmsListenerFactory getJMSListenerFactory() {
        return this.jlf;
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
     * @param name of the context to find.
     */
    public DispatchContext getLocalContext(String name) {
        if (localContext.containsKey(name))
            return (DispatchContext) localContext.get(name);
        return null;
    }

    /**
     * Test if this dispatcher instance contains the local context.
     * @param name of the local context
     * @return true if the local context is found in this dispatcher.
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
            // manually calling the auth service, don't continue...
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
            // if a userLogin object is there, make sure the given username/password exists in our local database
            GenericValue userLogin = (GenericValue) context.get("userLogin");

            if (userLogin != null) {
                GenericValue newUserLogin = getLoginObject(service, localName, userLogin.getString("userLoginId"), userLogin.getString("currentPassword"));

                if (newUserLogin == null) {
                    // uh oh, couldn't validate that one...
                    // we'll have to remove it from the incoming context which will cause an auth error later if auth is required
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
}
