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

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import org.jpublish.view.freemarker.JPublishTemplateLoader;

import org.ofbiz.core.util.*;

/**
 * JPublish View Renderer For Freemarker Template Engine
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class FreeMarkerViewRenderer extends org.jpublish.view.freemarker.FreeMarkerViewRenderer {
        
    public static final String module = FreeMarkerViewRenderer.class.getName();        

    public void init() throws Exception{
        super.init();
        fmConfig.setCacheSoftReference(false);
    }
    
    protected Object createViewContext(JPublishContext context, String path) throws ViewRenderException {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        WrappingTemplateModel.setDefaultObjectWrapper(wrapper);                    
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
         } catch (Exception e) {
             throw new ViewRenderException(e);            
         }          
         return root;         
    }
}
