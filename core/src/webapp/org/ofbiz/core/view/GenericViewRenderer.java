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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.jpublish.Repository;
import org.jpublish.SiteContext;
import org.jpublish.page.PageInstance;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.ftl.FreeMarkerViewRenderer;
import org.ofbiz.core.util.Debug;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.XMLConfiguration;
import com.anthonyeden.lib.util.IOUtilities;

/**
 * Generic JPublish View Renderer - This is in testing; for use in wrapping other renderers
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class GenericViewRenderer implements ViewRenderer {
        
    public static final String module = GenericViewRenderer.class.getName();  
    public static final String DEFAULT_RENDERER = "freemarker";
    public Map renderers = null;      
        
    protected SiteContext siteContext = null;

    /**
     * @see org.jpublish.view.ViewRenderer#setSiteContext(org.jpublish.SiteContext)
     */
    public void setSiteContext(SiteContext siteContext) {
        this.siteContext = siteContext;
        this.renderers = new HashMap();
        try {
            loadCustom();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }                
    }

    /* (non-Javadoc)
     * @see org.jpublish.view.ViewRenderer#init()
     */
    public void init() throws Exception {                         
    }

    /**
     * @see org.jpublish.view.ViewRenderer#render(org.jpublish.JPublishContext, java.io.Reader, java.io.Writer)
     */
    public void render(JPublishContext context, String path, Reader in, Writer out) throws IOException, ViewRenderException {
        HttpSession session = context.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
        if (Debug.verboseOn()) Debug.logVerbose("Getting renderer for: " + path, module);
        // locate the page renderer from page.getProperty("page-renderer");        
        ViewRenderer renderer = getPathRenderer(path);        
        
        Debug.logVerbose("Checking security", module);
        // do some user/security checking for page edits -- this test just checks for admin
        if (userLogin != null && userLogin.getString("userLoginId").equals("admin")) {    
            out.write("<a href='/content/control/editContent?filePath=" + path + "'>*</a>");
        }
        
        Debug.logVerbose("Calling render");
        // call the renderer to render the rest of the page.
        renderer.render(context, path, in, out);                   
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
    
    private ViewRenderer getPathRenderer(String path) throws ViewRenderException {
        String rendererName = DEFAULT_RENDERER;
        try {        
            PageInstance page = siteContext.getPageManager().getPage(path.substring(path.lastIndexOf(":")));
            rendererName = page.getProperty("page-renderer");
        } catch (Exception e) {}
        //Debug.logInfo("Using renderer: " + rendererName, module);
        if (rendererName == null) {
            return (ViewRenderer) renderers.get(DEFAULT_RENDERER);
        } else {
            return (ViewRenderer) renderers.get(rendererName);
        }       
    }
    
    private void loadCustom() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream in = new FileInputStream(siteContext.getConfigurationFile());
        Configuration configuration = new XMLConfiguration(in);
        
        Iterator renderElements = configuration.getChildren("page-renderer").iterator();
        while (renderElements.hasNext()) {
            Configuration viewRendererConfiguration = (Configuration) renderElements.next();
            String renderName = viewRendererConfiguration.getAttribute("name");
            String className = viewRendererConfiguration.getAttribute("classname");
            ViewRenderer renderer = (ViewRenderer) cl.loadClass(className).newInstance();
            renderer.setSiteContext(siteContext);
            renderer.loadConfiguration(viewRendererConfiguration);
            renderer.init();
            Debug.logInfo("Added renderer [" + renderName + "] - [" + className + "]", module);
            renderers.put(renderName, renderer);           
        }                   
    }       
}