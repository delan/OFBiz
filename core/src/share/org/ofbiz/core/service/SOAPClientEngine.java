/*
 * $Id$
 */

package org.ofbiz.core.service;

import java.net.*;
import java.util.*;
import org.apache.axis.client.*;
import org.apache.axis.encoding.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Generic Service SOAP Interface
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
 *@created    December 7, 2001
 *@version    1.0
 */
public final class SOAPClientEngine extends GenericAsyncEngine {
    
    /** Creates new SOAPClientEngine */
    public SOAPClientEngine(ServiceDispatcher dispatcher) {
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
        if ( result == null )
            throw new GenericServiceException("Service did not return expected result");
        if ( !(result instanceof Map) ) {
            Map newResult = new HashMap();
            newResult.put("result",result);
            return newResult;
        }             
        return (Map) result;
    }
    
    // Invoke the remote SOAP service
    private Object serviceInvoker(ModelService modelService, Map context) throws GenericServiceException {
        if ( modelService.location == null || modelService.invoke == null )
            throw new GenericServiceException("Cannot locate service to invoke");
        
        Service service = null;
        Call call = null;
        
        try {
            service = new Service();
            call = (Call) service.createCall();
        }
        catch ( javax.xml.rpc.JAXRPCException e ) {
            throw new GenericServiceException("RPC service error",e);
        }
        
        URL endPoint = null;
        try {
            endPoint = new URL(modelService.location);
        }
        catch ( MalformedURLException e ) {
            throw new GenericServiceException("Location not a valid URL",e);
        }
        
        Object[] params = new Object[context.size()];
        Debug.logInfo("[SOAPClientEngine.invoke] : Parameter length - " + params.length);
        
        call.setTargetEndpointAddress(endPoint);
        call.setOperationName(modelService.invoke);      
        if ( !modelService.nameSpace.equals("") )
            call.setProperty(Call.NAMESPACE, modelService.nameSpace);
               
        Iterator i = context.keySet().iterator();        
        while ( i.hasNext() ) {                    
            Object key = i.next();
            Object value = context.get(key);
            ModelParam p = (ModelParam) modelService.contextInfo.get(key);
            Debug.logInfo("[SOAPClientEngine.invoke} : Parameter : " + p.name + " ("+p.mode+") - " + p.order);
            if ( p.order > -1 )
                params[p.order] = value;
        }
                      
        Object result = null;
        try {
            Debug.logInfo("[SOAPClientEngine.invoke] : Sending Call To SOAP Server");
            result = call.invoke(params);
        }
        catch ( java.rmi.RemoteException e ) {
            throw new GenericServiceException("RPC error",e);
        }                    
        return result;        
    }
                
}
