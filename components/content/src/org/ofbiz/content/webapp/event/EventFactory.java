/*
 * $Id$
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.event;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.ObjectType;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.control.RequestManager;

/**
 * EventFactory - Event Handler Factory
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class EventFactory {

    protected RequestHandler requestHandler = null;
    protected RequestManager requestManager = null;
    protected ServletContext context = null;
    protected Map handlers = null;

    public EventFactory(RequestHandler requestHandler) {
        handlers = new HashMap();
        this.requestHandler = requestHandler;
        this.requestManager = requestHandler.getRequestManager();
        this.context = requestHandler.getServletContext();
    }

    public EventHandler getEventHandler(String type) throws EventHandlerException {                    
        // attempt to get a pre-loaded handler
        EventHandler handler = (EventHandler) handlers.get(type);

        if (handler == null) {
            synchronized (EventHandler.class) {
                handler = (EventHandler) handlers.get(type);
                if (handler == null) {
                    String handlerClass = requestManager.getHandlerClass(type, RequestManager.EVENT_HANDLER_KEY);
                    if (handlerClass == null)
                        throw new EventHandlerException("Unknown handler");

                    try {
                        handler = (EventHandler) ObjectType.getInstance(handlerClass);
                        handlers.put(type, handler);
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
        }
        return handler;
    }

    public static String runRequestEvent(HttpServletRequest request, HttpServletResponse response, String requestUri)
            throws EventHandlerException {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        RequestHandler handler = (RequestHandler) application.getAttribute("_REQUEST_HANDLER_");
        RequestManager rm = handler.getRequestManager();
        String eventType = rm.getEventType(requestUri);
        String eventPath = rm.getEventPath(requestUri);
        String eventMethod = rm.getEventMethod(requestUri);
        return handler.runEvent(request, response, eventType, eventPath, eventMethod);        
    }
}
