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

package org.ofbiz.core.view;

import java.util.*;

import org.ofbiz.core.control.*;
import org.ofbiz.core.util.*;

/**
 * ViewFactory - View Handler Factory
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    Feb 26, 2002
 *@version    1.0
 */
public class ViewFactory {

    protected static Map handlers = new HashMap();
    public static final String module = ViewFactory.class.getName();

    public static ViewHandler getViewHandler(RequestHandler rh, String type) throws ViewHandlerException {
        if (handlers.size() == 0) {
            try {
                ViewHandler h = (ViewHandler) ObjectType.getInstance("org.ofbiz.core.view.JspViewHandler");
                h.init(rh.getServletContext());
                handlers.put("default", h);
            } catch (Exception e) {
                Debug.logError(e,"[viewFactory.getDefault]: Cannot load default handler.", module);
            }
        }
        ViewHandler handler = (ViewHandler) handlers.get(type);
        if (handler == null) {
            synchronized (ViewHandler.class) {
                handler = (ViewHandler) handlers.get(type);
                if (handler == null) {
                    String handlerClass = rh.getRequestManager().getHandlerClass(type, RequestManager.VIEW_HANDLER_KEY);
                    if (handlerClass == null)
                        throw new ViewHandlerException("Unknown handler");
                    try {
                        handler = (ViewHandler) ObjectType.getInstance(handlerClass);
                        handler.init(rh.getServletContext());
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
            if (handler == null)
                throw new ViewHandlerException("Invalid handler");
        }
        return handler;
    }
}



