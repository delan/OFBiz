/*
 * $Id$
 *
 * Copyright 2003-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.widget.screen;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilJ2eeCompat;
import org.ofbiz.webapp.view.ViewHandler;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.xml.sax.SAXException;

/**
 * Handles view rendering for the Screen Widget
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @since      3.1
 */
public class ScreenWidgetViewHandler implements ViewHandler {

    public static final String module = ScreenWidgetViewHandler.class.getName();
    
    protected ServletContext servletContext = null;
    protected HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();

    /**
     * @see org.ofbiz.webapp.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }
    
    /**
     * @see org.ofbiz.webapp.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Writer writer = null;
        try {
            // use UtilJ2eeCompat to get this setup properly
            boolean useOutputStreamNotWriter = false;
            if (this.servletContext != null) {
                useOutputStreamNotWriter = UtilJ2eeCompat.useOutputStreamNotWriter(this.servletContext);
            }
            if (useOutputStreamNotWriter) {
                ServletOutputStream ros = response.getOutputStream();
                writer = new OutputStreamWriter(ros, "UTF-8");
            } else {
                writer = response.getWriter();
            }

            ScreenRenderer screens = new ScreenRenderer(writer, null, htmlScreenRenderer);
            screens.populateContextForRequest(request, response, servletContext);
            screens.render(page);
        } catch (IOException e) {
            throw new ViewHandlerException("Error in the response writer/output stream: " + e.toString(), e);
        } catch (SAXException e) {
            throw new ViewHandlerException("XML Error rendering page: " + e.toString(), e);
        } catch (ParserConfigurationException e) {
            throw new ViewHandlerException("XML Error rendering page: " + e.toString(), e);
        } catch (GeneralException e) {
            throw new ViewHandlerException("Lower level error rendering page: " + e.toString(), e);
        } 
    }
}
