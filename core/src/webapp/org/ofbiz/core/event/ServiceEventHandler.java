/*
 * $Id$
 */

package org.ofbiz.core.event;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ServiceEventHandler.java
 * <p><b>Description:</b> Service Event Handler
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
public class ServiceEventHandler implements EventHandler {
    
    private String serviceName = null;
    
    /** Initialize the required parameters
     *@param eventPath The path or location of this event
     *@param eventMethod The method to invoke
     */
    public void initialize(String eventPath, String eventMethod) {
        this.serviceName = eventMethod;
    }
    
    /** Invoke the web event
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        LocalDispatcher dispatcher = (LocalDispatcher) context.getAttribute("dispatcher");
        
        if ( serviceName == null )
            throw new EventHandlerException("Service name (eventMethod) cannot be null");
        
        // get the service model to generate context
        ModelService model = null;
        try {
            model = dispatcher.getDispatchContext().getModelService(serviceName);
        }
        catch ( GenericServiceException e ) {
            throw new EventHandlerException("Problems getting the service model",e);
        }
        
        if ( model == null )
            throw new EventHandlerException("Problems getting the service model");
        
        // we have a service and the model; build the context
        Map serviceContext = new HashMap();
        Iterator ci = model.contextInfo.keySet().iterator();
        while ( ci.hasNext() ) {
            String name = (String) ci.next();
            String value = request.getParameter(name);
            if ( value != null )
                serviceContext.put(name,value);
        }
        
        // invoke the service
        Map result = null;
        try {
            result = dispatcher.runSync(serviceName,serviceContext);
        }
        catch ( GenericServiceException e ) {
            throw new EventHandlerException("Service invocation error",e);
        }
        
        String responseString = null;
        if ( result == null || !result.containsKey("response") )
            responseString = "success";
        else
            responseString = (String) result.get("response");
        
        if ( result.containsKey("errorMessage") ) {
            // TODO check if this is a string or a list, if list create a long string
            request.setAttribute(SiteDefs.ERROR_MESSAGE,result.get("errorMessage"));
        }
        
        // set the results in the request
        Iterator ri = result.keySet().iterator();
        while( ri.hasNext() ) {
            String resultKey = (String) ri.next();
            Object resultValue = result.get((Object)resultKey);
            if ( !resultKey.equals("response") && !resultKey.equals("errorMessage") )
                request.setAttribute(resultKey,resultValue);
        }
        
        return responseString;
        
    }
}
