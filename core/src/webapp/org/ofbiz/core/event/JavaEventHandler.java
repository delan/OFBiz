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

package org.ofbiz.core.event;


import java.util.*;
import java.lang.reflect.*;
import javax.servlet.http.*;

import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> JavaEventHandler - Static Method Java Event Handler
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 7, 2001
 *@version    1.0
 */
public class JavaEventHandler implements EventHandler {

    public static final String module = JavaEventHandler.class.getName();

    private Map eventClassMap = new HashMap();

    /** Invoke the web event
     *@param eventPath The path or location of this event
     *@param eventMethod The method to invoke
     *@param request The servlet request object
     *@param response The servlet response object
     *@return String Result code
     *@throws EventHandlerException
     */
    public String invoke(String eventPath, String eventMethod, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        Class eventClass = (Class) this.eventClassMap.get(eventPath);

        if (eventClass == null) {
            synchronized (this) {
                eventClass = (Class) this.eventClassMap.get(eventPath);
                if (eventClass == null) {
                    try {
                        eventClass = Class.forName(eventPath);
                    } catch (ClassNotFoundException e) {
                        Debug.logError(e, "Error loading class with name: " + eventPath + ", will not be able to run event...");
                    }
                    if (eventClass != null) {
                        eventClassMap.put(eventPath, eventClass);
                    }
                }
            }
        }
        if (Debug.verboseOn()) Debug.logVerbose("[Set path/method]: " + eventPath + " / " + eventMethod, module);

        Class[] paramTypes = new Class[] {HttpServletRequest.class, HttpServletResponse.class};

        Debug.logVerbose("*[[Event invocation]]*", module);
        Object[] params = new Object[] {request, response};

        return invoke(eventPath, eventMethod, eventClass, paramTypes, params);
    }

    private String invoke(String eventPath, String eventMethod, Class eventClass, Class[] paramTypes, Object[] params) throws EventHandlerException {
        if (eventClass == null) {
            throw new EventHandlerException("Error invoking event, the class " + eventPath + " was not found");
        }
        if (eventPath == null || eventMethod == null) {
            throw new EventHandlerException("Invalid event method or path; call initialize()");
        }

        Debug.logVerbose("[Processing]: JAVA Event", module);
        try {
            Method m = eventClass.getMethod(eventMethod, paramTypes);
            String eventReturn = (String) m.invoke(null, params);

            if (Debug.verboseOn()) Debug.logVerbose("[Event Return]: " + eventReturn, module);
            return eventReturn;
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable t = e.getTargetException();

            if (t != null) {
                Debug.logError(t, "Problems Processing Event", module);
                throw new EventHandlerException("Problems processing event: " + t.toString(), t);
            } else {
                Debug.logError(e, "Problems Processing Event", module);
                throw new EventHandlerException("Problems processing event: " + e.toString(), e);
            }
        } catch (Exception e) {
            Debug.logError(e, "Problems Processing Event", module);
            throw new EventHandlerException("Problems processing event: " + e.toString(), e);
        }
    }
}
