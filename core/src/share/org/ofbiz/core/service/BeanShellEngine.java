/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.io.*;
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    November 28, 2001
 *@version    1.0
 */
public final class BeanShellEngine extends GenericAsyncEngine {
            
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
        Object result = serviceInvoker(modelService,context);
        if ( result == null || !(result instanceof Map))
            throw new GenericServiceException("Service did not return expected result");
        return (Map) result;
    }
    
    // Invoke the BeanShell script service.
    private Object serviceInvoker(ModelService modelService, Map context) throws GenericServiceException {
       if ( modelService.location == null || modelService.invoke == null )
            throw new GenericServiceException("Cannot locate service to invoke");
        
        // Get the classloader to use
        ClassLoader cl = null;
        if ( loader == null )
            cl = this.getClass().getClassLoader();
        else
            cl = dispatcher.getLocalContext(loader).getClassLoader();
        
        Interpreter bsh = null;
        try {
            Class c = cl.loadClass("bsh.Interpreter");
            bsh = (Interpreter) c.newInstance();
        }
        catch ( ClassNotFoundException e ) {
            throw new GenericServiceException("Cannot load the BeanShell Interpreter");
        }
        catch ( IllegalAccessException e ) {
            throw new GenericServiceException("BeanShell Interpreter class not accessible");
        }
        catch ( InstantiationException e ) {
            throw new GenericServiceException("Cannot instantiate the BeanShell Interpreter");
        }
        
        Map result = null;                
        try {
            bsh.set("context",context);             // set the context for the BSH script
            bsh.set("result",new HashMap());     // set the result for the script
            bsh.source(modelService.location);
            result = (Map) bsh.get("result");
        }
        catch ( FileNotFoundException e ) {
            throw new GenericServiceException("Cannot locate the BeanShell script");
        }
        catch ( IOException e ) {
            throw new GenericServiceException(e.getMessage(),e);
        }
        catch ( EvalError e ) {
            throw new GenericServiceException("BeanShell script threw an exception",e);
        }
        catch ( ClassCastException e ) {
            throw new GenericServiceException("BeanShell script did not return a proper response");
        }
                
        return result;
    }
    
}



