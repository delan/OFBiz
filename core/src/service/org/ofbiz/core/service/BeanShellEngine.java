/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

import bsh.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> BeanShell Script Service Engine
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
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @created    November 28, 2001
 * @version    1.0
 */
public final class BeanShellEngine extends GenericAsyncEngine {

    public static UtilCache beanShellCache = new UtilCache("BeanShellScripts", 0, 0);

    /** Creates new BeanShellEngine */
    public BeanShellEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /** Run the service synchronously and IGNORE the result
     * @param context Map of name, value pairs composing the context
     */
    public void runSyncIgnore(ModelService modelService, Map context) throws GenericServiceException {
        Map result = runSync(modelService, context);
    }

    /** Run the service synchronously and return the result
     * @param context Map of name, value pairs composing the context
     * @return Map of name, value pairs composing the result
     */
    public Map runSync(ModelService modelService, Map context) throws GenericServiceException {
        Object result = serviceInvoker(modelService, context);
        if (result == null || !(result instanceof Map))
            throw new GenericServiceException("Service did not return expected result");
        return (Map) result;
    }

    // Invoke the BeanShell script service.
    private Object serviceInvoker(ModelService modelService, Map context) throws GenericServiceException {
        if (modelService.location == null || modelService.invoke == null)
            throw new GenericServiceException("Cannot locate service to invoke");

        // Get the classloader to use
        ClassLoader cl = null;
        if (loader == null)
            cl = this.getClass().getClassLoader();
        else
            cl = dispatcher.getLocalContext(loader).getClassLoader();

        // source the script into a string
        String script = (String) beanShellCache.get(loader + "_" + modelService.location);
        if (script == null) {
            synchronized (this) {
                script = (String) beanShellCache.get(loader + "_" + modelService.location);
                if (script == null) {
                    URL scriptUrl = UtilURL.fromResource(modelService.location, cl);
                    if (scriptUrl != null) {
                        try {
                            HttpClient http = new HttpClient(scriptUrl);
                            script = http.get();
                        } catch (HttpClientException e) {
                            throw new GenericServiceException("Cannot read script from resource");
                        }
                    } else {
                        throw new GenericServiceException("Cannot read script, resource [" + modelService.location + "] not found");
                    }
                    if (script == null || script.length() < 2) {
                        throw new GenericServiceException("Null or empty script");
                    }
                    beanShellCache.put(loader + "_" + modelService.location, script);
                }
            }
        }

        Interpreter bsh = new Interpreter();

        Map result = null;
        try {
            bsh.set("dctx", dispatcher.getLocalContext(loader)); // set the dispatch context
            bsh.set("context", context); // set the parameter context used for both IN and OUT
            bsh.eval(script);
            Object bshResult = bsh.get("result");
            if ((bshResult != null) && (bshResult instanceof Map))
                context.putAll((Map)bshResult);
            result = modelService.makeValid(context, ModelService.OUT_PARAM);
        } catch (EvalError e) {
            throw new GenericServiceException("BeanShell script threw an exception", e);
        }
        return result;
    }

}




