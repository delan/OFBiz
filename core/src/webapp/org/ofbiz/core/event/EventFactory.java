/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.control.*;
import org.ofbiz.core.util.*;

/**
 * EventFactory - Event Handler Factory
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class EventFactory {

    protected static Map handlers = new HashMap();

    public static EventHandler getEventHandler(RequestHandler rh, String type) throws EventHandlerException {
        // get the context specific handlers
        ServletContext context = rh.getServletContext();
        Map contextHandlers = (Map) handlers.get(context.getInitParameter("webSiteId"));
        if (contextHandlers == null)
            contextHandlers = new HashMap();
        
        // attempt to get a pre-loaded handler
        EventHandler handler = (EventHandler) contextHandlers.get(type);

        if (handler == null) {
            synchronized (EventHandler.class) {
                handler = (EventHandler) contextHandlers.get(type);
                if (handler == null) {
                    String handlerClass = rh.getRequestManager().getHandlerClass(type, RequestManager.EVENT_HANDLER_KEY);
                    if (handlerClass == null)
                        throw new EventHandlerException("Unknown handler");
                        
                    try {
                        handler = (EventHandler) ObjectType.getInstance(handlerClass);
                        contextHandlers.put(type, handler);
                    } catch (ClassNotFoundException cnf) {
                        throw new EventHandlerException("Cannot load handler class", cnf);
                    } catch (InstantiationException ie) {
                        throw new EventHandlerException("Cannot get instance of the handler", ie);
                    } catch (IllegalAccessException iae) {
                        throw new EventHandlerException(iae.getMessage(), iae);
                    }
                }
            }
            if (handler == null)
                throw new EventHandlerException("Invalid handler");
            
            // lets store the updates in the master map
            handlers.put(context.getInitParameter("webSiteId"), contextHandlers);
        }
        return handler;
    }
    
    public static String runRequestEvent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws EventHandlerException {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        RequestHandler handler = (RequestHandler) application.getAttribute(SiteDefs.REQUEST_HANDLER);
        RequestManager rm = handler.getRequestManager();
        String eventType = rm.getEventType(requestUri);
        String eventPath = rm.getEventPath(requestUri);
        String eventMethod = rm.getEventMethod(requestUri);
        if (eventType != null && eventPath != null && eventMethod != null) {
            EventHandler eh = EventFactory.getEventHandler(handler, eventType);
            return eh.invoke(eventPath, eventMethod, request, response);
        } else {
            throw new EventHandlerException("Invocation error; cannot locate event details.");
        }
    }        
}

