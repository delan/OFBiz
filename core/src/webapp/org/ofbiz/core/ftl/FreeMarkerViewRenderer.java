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
package org.ofbiz.core.ftl;

import java.io.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.jpublish.SiteContext;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.ofbiz.core.util.*;

/**
 * JPublish View Renderer For Freemarker Template Engine
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class FreeMarkerViewRenderer implements ViewRenderer {
        
    public static final String module = FreeMarkerViewRenderer.class.getName();        
        
    private SiteContext siteContext = null;
    protected freemarker.template.Configuration config = null;

    /**
     * @see org.jpublish.view.ViewRenderer#setSiteContext(org.jpublish.SiteContext)
     */
    public void setSiteContext(SiteContext siteContext) {
        this.siteContext = siteContext;                
    }

    /**
     * @see org.jpublish.view.ViewRenderer#init()
     */
    public void init() throws Exception {                       
    }

    /**
     * @see org.jpublish.view.ViewRenderer#render(org.jpublish.JPublishContext, java.io.Reader, java.io.Writer)
     */
    public void render(JPublishContext context, String path, Reader reader, Writer writer) throws IOException, ViewRenderException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        ServletContext servletContext = context.getApplication();
        Page page = context.getPage();
        
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        WrappingTemplateModel.setDefaultObjectWrapper(wrapper);        
        if (config == null) {
            synchronized(this) {
                if (config == null) {
                    config = freemarker.template.Configuration.getDefaultConfiguration();
                    config.setDirectoryForTemplateLoading(siteContext.getContextRoot());
                    config.setObjectWrapper(wrapper);
                    //Debug.logInfo("Set directory for template (includes) loading: " + siteContext.getContextRoot(), module);
                }
            }        
        }
        
        Map contextMap = new HashMap();
        SimpleHash root = new SimpleHash(wrapper);          
        try {              
            Object[] keys = context.getKeys(); 
            for (int i = 0; i < keys.length; i++) {
                String key = (String) keys[i];
                contextMap.put(key, context.get(key));
                root.put(key, wrapper.wrap(context.get(key)));
                //Debug.logInfo("Key: " + key + " Value: " + context.get(key), module);
            }
            root.put("context", wrapper.wrap(contextMap));
            FreeMarkerViewHandler.prepOfbizRoot(root, request, response);
            Template template = new Template(path, reader, config);
            template.setLocale(page.getLocale());
            template.process(root, writer, wrapper);
        } catch (IOException ie) {
            throw ie;
        } catch (Exception e) {
            throw new ViewRenderException(e);            
        }                                       
    }

    /**
     * @see org.jpublish.view.ViewRenderer#render(org.jpublish.JPublishContext, java.io.InputStream, java.io.OutputStream)
     */
    public void render(JPublishContext context, String path, InputStream in, OutputStream out) throws IOException, ViewRenderException {
        render(context, path, new InputStreamReader(in), new OutputStreamWriter(out));              
    }

    /**
     * @see org.jpublish.view.ViewRenderer#loadConfiguration(com.anthonyeden.lib.config.Configuration)
     */
    public void loadConfiguration(Configuration config) throws ConfigurationException {               
    }        


}
