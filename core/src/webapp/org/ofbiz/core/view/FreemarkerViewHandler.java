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

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.util.*;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * FreemarkerViewHandler - Freemarker Template Engine View Handler
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class FreemarkerViewHandler implements ViewHandler {
    
    public static final String module = FreemarkerViewHandler.class.getName();
    
    protected ServletContext servletContext = null;
    protected Configuration config = null;

    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
        config = Configuration.getDefaultConfiguration();
        try {        
            config.setDirectoryForTemplateLoading(new File(context.getRealPath("/")));
        } catch (IOException e) {
            Debug.logError(e, "Problems creating file for root path.", module);
        }
    }    
    
    public void render(String name, String page, String info, String contentType, String encoding, 
            HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {                
        if (page == null || page.length() == 0) 
            throw new ViewHandlerException("Invalid template source");
        
        // make the root context (data model) for freemarker            
        Map root = new HashMap();
        
        // add in the OFBiz objects
        BeanModel delegatorModel = new BeanModel(request.getAttribute("delegator"), BeansWrapper.getDefaultInstance());
        root.put("delegator", delegatorModel);
        
        BeanModel dispatcherModel = new BeanModel(request.getAttribute("dispatcher"), BeansWrapper.getDefaultInstance());
        root.put("dispatcher", dispatcherModel);
        
        BeanModel securityModel = new BeanModel(request.getAttribute("security"), BeansWrapper.getDefaultInstance());
        root.put("security", securityModel);
        
        BeanModel userLoginModel = new BeanModel(request.getAttribute("userLogin"), BeansWrapper.getDefaultInstance());
        root.put("userLogin", userLoginModel);    
                
        // add the servlet context    
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servletContext, ObjectWrapper.SIMPLE_WRAPPER);     
        root.put("application", servletContextModel);

        // add the session
        HttpSession session = request.getSession();
        HttpSessionHashModel sessionModel = new HttpSessionHashModel(session, ObjectWrapper.SIMPLE_WRAPPER);           
        root.put("session", sessionModel);

        // add the request
        HttpRequestHashModel requestModel = new HttpRequestHashModel(request, ObjectWrapper.SIMPLE_WRAPPER);            
        root.put("request", requestModel);

        // add the request parameters                        
        HttpRequestParametersHashModel requestParametersModel = new HttpRequestParametersHashModel(request);
        root.put("requestParameters", requestParametersModel);
                  
        // get the template
        Template template = null;
        try {
            template = config.getTemplate(page, request.getLocale());
        } catch (IOException e) {
            throw new ViewHandlerException("Cannot open template file: " + page, e);
        }
        
        // process the template & flush the output
        try {
            template.process(root, response.getWriter());
            response.flushBuffer();
        } catch (TemplateException te) {
            throw new ViewHandlerException("Problems processing Freemarker template", te);
        } catch (IOException ie) {
            throw new ViewHandlerException("Problems writing to output stream", ie);
        }       
    }
}
