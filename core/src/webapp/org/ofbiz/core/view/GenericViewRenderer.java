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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.http.HttpSession;

import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.jpublish.SiteContext;
import org.jpublish.view.ViewRenderException;
import org.jpublish.view.ViewRenderer;
import org.ofbiz.core.ftl.FreeMarkerViewRenderer;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

/**
 * Generic JPublish View Renderer - This is in testing; for use in wrapping other renderers
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class GenericViewRenderer implements ViewRenderer {
        
    public static final String module = GenericViewRenderer.class.getName();        
        
    protected SiteContext siteContext;

    /**
     * @see org.jpublish.view.ViewRenderer#setSiteContext(org.jpublish.SiteContext)
     */
    public void setSiteContext(SiteContext siteContext) {
        this.siteContext = siteContext;
        
    }

    /* (non-Javadoc)
     * @see org.jpublish.view.ViewRenderer#init()
     */
    public void init() throws Exception {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.jpublish.view.ViewRenderer#render(org.jpublish.JPublishContext, java.io.Reader, java.io.Writer)
     */
    public void render(JPublishContext context, String path, Reader in, Writer out) throws IOException, ViewRenderException {
        HttpSession session = context.getSession();
        Page page = context.getPage();
        // locate the page renderer from page.getProperty("page-renderer");
        // load the renderer class; somehow this needs to be mapped name/class
        FreeMarkerViewRenderer renderer = new FreeMarkerViewRenderer(); // this is a test
        renderer.setSiteContext(siteContext);
        
        // do some user/security checking for page edits       
        //out.write("<b>" + page.getPath()  + "</b>");   // write the page editor links, etc
        
        // call the renderer to render the rest of the page.
        renderer.render(context, path, in, out);                   
    }

    /**
     * @see org.jpublish.view.ViewRenderer#render(org.jpublish.JPublishContext, java.io.InputStream, java.io.OutputStream)
     */
    public void render(JPublishContext context, String path, InputStream in, OutputStream out) throws IOException, ViewRenderException {
        render(context, path, new InputStreamReader(in), new OutputStreamWriter(out));               
    }

    /* (non-Javadoc)
     * @see org.jpublish.view.ViewRenderer#loadConfiguration(com.anthonyeden.lib.config.Configuration)
     */
    public void loadConfiguration(Configuration config) throws ConfigurationException {
        // TODO Auto-generated method stub
        
    }
        
}