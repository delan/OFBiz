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
package org.ofbiz.content.webapp.view;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.control.RequestManager;

/**
 * ViewFactory - View Handler Factory
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ViewFactory {
    
    public static final String module = ViewFactory.class.getName();
        
    protected RequestHandler requestHandler = null;
    protected RequestManager requestManager = null;
    protected ServletContext context = null;
    protected Map handlers = null;
    
    public ViewFactory(RequestHandler requestHandler) {
        handlers = new HashMap();
        this.requestHandler = requestHandler;
        this.requestManager = requestHandler.getRequestManager();
        this.context = requestHandler.getServletContext();
    }
    
    public ViewHandler getViewHandler(String type) throws ViewHandlerException {
        if (type == null || type.length() == 0) {
            type = "default";
        }
                            
        // check if we are new / empty and add the default handler in
        if (handlers.size() == 0) {            
            try {
                ViewHandler h = (ViewHandler) ObjectType.getInstance("org.ofbiz.content.webapp.view.JspViewHandler");
                h.init(context);
                handlers.put("default", h);
            } catch (Exception e) {
                Debug.logError(e, "[viewFactory.getDefault]: Cannot load default handler.", module);
            }
        }
        
        // get the view handler by type from the contextHandlers 
        ViewHandler handler = (ViewHandler) handlers.get(type);

        // if none found lets create it and add it in
        if (handler == null) {
            synchronized (ViewFactory.class) {
                handler = (ViewHandler) handlers.get(type);
                if (handler == null) {
                    String handlerClass = requestManager.getHandlerClass(type, RequestManager.VIEW_HANDLER_KEY);
                    if (handlerClass == null)
                        throw new ViewHandlerException("Unknown handler type: " + type);
                        
                    try {
                        handler = (ViewHandler) ObjectType.getInstance(handlerClass);
                        handler.init(context);
                        handlers.put(type, handler);
                    } catch (ClassNotFoundException cnf) {
                        throw new ViewHandlerException("Cannot load handler class", cnf);
                    } catch (InstantiationException ie) {
                        throw new ViewHandlerException("Cannot get instance of the handler", ie);
                    } catch (IllegalAccessException iae) {
                        throw new ViewHandlerException(iae.getMessage(), iae);
                    }
                }
            }
            if (handler == null) {
                throw new ViewHandlerException("No handler found for type: " + type);
            }                               
        }
        return handler;
    }
}
