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
package org.ofbiz.core.view;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.core.view.ViewHandler;
import org.ofbiz.core.view.ViewHandlerException;

import com.wspublisher.servlet.WspEngine;


public class WspViewHandler implements ViewHandler {
    
    protected WspEngine wspEngine;
    
    protected WspEngine getWspEngine() {
        return wspEngine;
    }
    protected void setWspEngine(WspEngine inEngine) {
        wspEngine = inEngine;
    }
    
    /**
     * @see org.ofbiz.core.view.ViewHandler#init(ServletContext)
     */
    public void init(ServletContext context) throws ViewHandlerException {
        wspEngine = new WspEngine(context);
    }
    
    /**
     * @see org.ofbiz.core.view.ViewHandler#render(String, String, String, String, String, HttpServletRequest, HttpServletResponse)
     */
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response)
            throws ViewHandlerException {
        try {
            getWspEngine().render(page, request, response, true, true);
        } catch (Exception e) {
            throw new ViewHandlerException("Could not render with wsp view named " + name, e);
        }
    }
}
