/*
 * $Id$
 */

package org.ofbiz.core.event;

import java.util.*;
import java.lang.reflect.*;
import javax.servlet.http.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> JavaEventHandler.java
 * <p><b>Description:</b> Java Event Handler
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
public class JavaEventHandler implements EventHandler {
                
    private String eventPath = null;
    private String eventMethod = null;
    
    /** Initialize the required parameters
     *@param eventPath The path or location of this event
     *@param eventMethod The method to invoke
     */
    public void initialize(String eventPath, String eventMethod) {
        this.eventPath = eventPath;
        this.eventMethod = eventMethod;
    }
    
    /** Invoke the web event
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        Class[] paramTypes = new Class[] {HttpServletRequest.class, HttpServletResponse.class};
        Object[] params = new Object[] {request,response};
        return invoke(paramTypes, params);
    }
    
    private String invoke(Class[] paramTypes, Object[] params) throws EventHandlerException {
        String eventReturnString = null;
        if ( eventPath == null || eventMethod == null )
            throw new EventHandlerException("Invalid event method or path; call initialize()");
        
        Debug.logInfo("[EventHandler] : Processing JAVA event.");
        try {
            Class c = Class.forName(eventPath);
            Method m = c.getMethod(eventMethod,paramTypes);
            eventReturnString = (String) m.invoke(null,params);
            Debug.logInfo("[EventHandler] : Returned -  " + eventReturnString);
        }
        catch ( Exception e ) {
            Debug.logError(e,"[EventHandler] : Problems Processing Event.");
            throw new EventHandlerException("Problems processing event: " + e.getMessage());
        }
        
        return eventReturnString;
    }
}
