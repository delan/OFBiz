/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.util.*;
import java.lang.reflect.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Standard Java Static Method Service Engine
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
 *@created    November 2, 2001
 *@version    1.0
 */
public final class StandardJavaEngine extends GenericAsyncEngine {
            
    /** Creates new StandardJavaEngine */
    public StandardJavaEngine(ServiceDispatcher dispatcher) {
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
    
    // Invoke the static java method service.
    private Object serviceInvoker(ModelService modelService, Map context) throws GenericServiceException {
        // static java service methods should be: public Map methodName(DispatchContext dctx, Map context)
        DispatchContext dctx = dispatcher.getLocalContext(loader);
        Class[] paramTypes = new Class[] { DispatchContext.class, Map.class };
        Object[] params = new Object[] { dctx, context };
        Object result = null;
        
        if ( modelService.location == null || modelService.invoke == null )
            throw new GenericServiceException("Cannot locate service to invoke");
        
        // Get the classloader to use
        ClassLoader cl = null;
        if ( loader == null )
            cl = this.getClass().getClassLoader();
        else
            cl = dispatcher.getLocalContext(loader).getClassLoader();
                        
        try {
            Class c = cl.loadClass(modelService.location);
            Method m = c.getMethod(modelService.invoke, paramTypes);
            result = m.invoke(null, params);
        }
        catch ( ClassNotFoundException cnfe ) {
            throw new GenericServiceException("Cannot find service location",cnfe);
        }
        catch ( NoSuchMethodException nsme) {
            throw new GenericServiceException("Service method does not exist",nsme);
        }
        catch ( SecurityException se ) {
            throw new GenericServiceException("Access denied",se);
        }
        catch ( IllegalAccessException iae ) {
            throw new GenericServiceException("Method not accessible",iae);
        }
        catch ( IllegalArgumentException iarge ) {
            throw new GenericServiceException("Invalid parameter match",iarge);
        }
        catch ( InvocationTargetException ite ) {
            throw new GenericServiceException("Service threw an unexpected exception",ite);
        }
        catch ( NullPointerException npe ) {
            throw new GenericServiceException("Specified object is null",npe);
        }
        catch ( ExceptionInInitializerError eie ) {
            throw new GenericServiceException("Initialization failed",eie);
        }
        
        return result;
    }
    
}
