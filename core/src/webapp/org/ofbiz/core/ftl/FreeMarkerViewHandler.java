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
package org.ofbiz.core.ftl;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.view.*;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.WrappingTemplateModel;

/**
 * FreemarkerViewHandler - Freemarker Template Engine View Handler
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class FreeMarkerViewHandler implements ViewHandler {
    
    public static final String module = FreeMarkerViewHandler.class.getName();
    
    protected ServletContext servletContext = null;
    protected Configuration config = null;

    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
        config = Configuration.getDefaultConfiguration();
        //nice thought, but doesn't do auto reloading with this: config.setServletContextForTemplateLoading(context, "/");
        try {
            config.setDirectoryForTemplateLoading(new File(context.getRealPath("/")));
        } catch (java.io.IOException e) {
            throw new ViewHandlerException("Could not create file for webapp root path", e);
        }
        config.setObjectWrapper(BeansWrapper.getDefaultInstance());
        WrappingTemplateModel.setDefaultObjectWrapper(BeansWrapper.getDefaultInstance());
    }    
    
    public void render(String name, String page, String info, String contentType, String encoding, 
            HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {                
        if (page == null || page.length() == 0) 
            throw new ViewHandlerException("Invalid template source");
        
        // make the root context (data model) for freemarker
        SimpleHash root = new SimpleHash(BeansWrapper.getDefaultInstance());                         
        prepOfbizRoot(root, request, response);
                       
        // get the template
        Template template = null;
        try {
            template = config.getTemplate(page, request.getLocale());
        } catch (IOException e) {
            throw new ViewHandlerException("Cannot open template file: " + page, e);
        }
        template.setObjectWrapper(BeansWrapper.getDefaultInstance());
        
        // process the template & flush the output
        try {
            template.process(root, response.getWriter(), BeansWrapper.getDefaultInstance());
            response.flushBuffer();
        } catch (TemplateException te) {
            throw new ViewHandlerException("Problems processing Freemarker template", te);
        } catch (IOException ie) {
            throw new ViewHandlerException("Problems writing to output stream", ie);
        }       
    }
    
    public static void prepOfbizRoot(SimpleHash root, HttpServletRequest request, HttpServletResponse response) {
        ServletContext servletContext = (ServletContext) request.getAttribute("servletContext");
        HttpSession session = request.getSession();
        
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        
        root.put("Static", wrapper.getStaticModels());

        try {
            // add in the OFBiz objects
            root.put("delegator", wrapper.wrap(request.getAttribute("delegator")));
            root.put("dispatcher", wrapper.wrap(request.getAttribute("dispatcher")));
            root.put("security", wrapper.wrap(request.getAttribute("security")));
            root.put("userLogin", wrapper.wrap(session.getAttribute("userLogin")));

            // add the response object (for transforms) to the context as a BeanModel
            root.put("response", wrapper.wrap(response));
            
            // add the application object (for transforms) to the context as a BeanModel
            root.put("application", wrapper.wrap(servletContext));
            
            // add the servlet context -- this has been deprecated, and now requires servlet, do we really need it?
            //root.put("applicationAttributes", new ServletContextHashModel(servletContext, BeansWrapper.getDefaultInstance()));                       
                                 
            // add the session object (for transforms) to the context as a BeanModel
            root.put("session", wrapper.wrap(session));

            // add the session
            root.put("sessionAttributes", new HttpSessionHashModel(session, wrapper));

            // add the request object (for transforms) to the context as a BeanModel
            root.put("request", wrapper.wrap(request));

            // add the request
            root.put("requestAttributes", new HttpRequestHashModel(request, wrapper));

            // add the request parameters -- this now uses a Map from UtilHttp
            Map requestParameters = UtilHttp.getParameterMap(request);
            root.put("requestParameters", wrapper.wrap(requestParameters));

        } catch (freemarker.template.TemplateModelException e) {
            Debug.logError(e, "Error creating template model in OFBiz FreeMarker preparation");
        }
        
        // add the OFBiz transforms/methods
        root.put("ofbizUrl", new OfbizUrlTransform());
        root.put("ofbizContentUrl", new OfbizContentTransform());
        root.put("setRequestAttribute", new SetRequestAttributeMethod());
    }
}
