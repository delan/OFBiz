/*
 * $Id$
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.core.control.JPublishWrapper;
import org.ofbiz.core.util.GeneralException;

/**
 * Handles JPublish type view rendering
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class JPublishViewHandler implements ViewHandler {

    public static final String module = JPublishViewHandler.class.getName();
    
    protected ServletContext servletContext = null;
    protected JPublishWrapper wrapper = null;

    /**
     * @see org.ofbiz.core.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
        this.wrapper = (JPublishWrapper) context.getAttribute("jpublishWrapper");
        if (wrapper == null)
            throw new ViewHandlerException("JPublishWrapper not found in ServletContext");
    }

    /**
     * @see org.ofbiz.core.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        try {
            // Jetty throws IllegalStateException when doing response.getOutputStream()
            // this is only used for static content, why would we use JP for static content?
            Writer writer = response.getWriter();           
            wrapper.render(page, request, response, writer, null, true);
            writer.close();
        } catch (IOException e) {
            throw new ViewHandlerException("Problems with the response writer/output stream", e);
        } catch (GeneralException e) {
            throw new ViewHandlerException("Cannot render page", e);
        }
        
    }
}

