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
import java.lang.reflect.*;

import org.ofbiz.core.util.*;

/**
 * Standard Java Static Method Service Engine
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 2, 2001
 *@version    1.0
 */
public final class StandardJavaEngine extends GenericAsyncEngine {

    public static final String module = StandardJavaEngine.class.getName();

    /** Creates new StandardJavaEngine */
    public StandardJavaEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Run the service synchronously and IGNORE the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @throws GenericServiceException
     */
    public void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException {
        Map result = runSync(modelService, context);
    }

    /**
     * Run the service synchronously and return the result.
     * @param modelService Service model object.
     * @param context Map of name, value pairs composing the context.
     * @return Map of name, value pairs composing the result.
     * @throws GenericServiceException
     */
    public Map runSync(ModelService modelService, Map context) throws GenericServiceException {
        Object result = serviceInvoker(modelService, context);
        if (result == null || !(result instanceof Map))
            throw new GenericServiceException("Service did not return expected result");
        return (Map) result;
    }

    // Invoke the static java method service.
    private Object serviceInvoker(ModelService modelService, Map context) throws GenericServiceException {
        // static java service methods should be: public Map methodName(DispatchContext dctx, Map context)
        DispatchContext dctx = dispatcher.getLocalContext(loader);
        if (modelService == null)
            Debug.logError("ERROR: Null Model Service.", module);
        if (dctx == null)
            Debug.logError("ERROR: Null DispatchContext.", module);
        if (context == null)
            Debug.logError("ERROR: Null Service Context.", module);

        Class[] paramTypes = new Class[]{DispatchContext.class, Map.class};
        Object[] params = new Object[]{dctx, context};
        Object result = null;

        // check the package and method names
        if (modelService.location == null || modelService.invoke == null)
            throw new GenericServiceException("Cannot locate service to invoke (location or invoke name missing)");

        // get the classloader to use
        ClassLoader cl = null;
        if (loader == null)
            cl = this.getClass().getClassLoader();
        else
            cl = dispatcher.getLocalContext(loader).getClassLoader();

        try {
            Class c = cl.loadClass(modelService.location);
            Debug.logVerbose("Loaded class: " + c, module);
            Method m = c.getMethod(modelService.invoke, paramTypes);
            Debug.logVerbose("Created Method: " + m, module);
            result = m.invoke(null, params);
            Debug.logVerbose("Invoked Method -- Result: " + result, module);
        } catch (ClassNotFoundException cnfe) {
            throw new GenericServiceException("Cannot find service location", cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new GenericServiceException("Service method does not exist", nsme);
        } catch (SecurityException se) {
            throw new GenericServiceException("Access denied", se);
        } catch (IllegalAccessException iae) {
            throw new GenericServiceException("Method not accessible", iae);
        } catch (IllegalArgumentException iarge) {
            throw new GenericServiceException("Invalid parameter match", iarge);
        } catch (InvocationTargetException ite) {
            throw new GenericServiceException("Service threw an unexpected exception",
                                              ite);
        } catch (NullPointerException npe) {
            throw new GenericServiceException("Specified object is null", npe);
        } catch (ExceptionInInitializerError eie) {
            throw new GenericServiceException("Initialization failed", eie);
        }

        return result;
    }
}

