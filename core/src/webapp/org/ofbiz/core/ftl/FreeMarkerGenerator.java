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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jpublish.JPublishContext;
import org.jpublish.Page;
import org.jpublish.Repository;
import org.jpublish.SiteContext;
import org.jpublish.util.JPublishContextMap;

import com.anthonyeden.lib.config.*;
import com.wspublisher.WSPException;
import com.wspublisher.generators.AbstractGenerator;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;

import org.ofbiz.core.util.Debug;


/**
 * This generator uses FreeMarker to process a template file.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.1
 */
public class FreeMarkerGenerator extends AbstractGenerator {
    public static final String module = FreeMarkerGenerator.class.getName();

    public static final String NAME = "ofbiz-freemarker";
    protected freemarker.template.Configuration config = null;
    
    public FreeMarkerGenerator() {
        super(NAME);
        Debug.logVerbose("=========== Initializing OFBiz FreeMarkerGenerator ===========", module);
    }
    
    /*
     * @see Generator#generate(Page, JPublishContext)
     */
    public void generate(JPublishContext inContext, com.anthonyeden.lib.config.Configuration inConfig, OutputStream inOutput) throws Throwable {
        Writer out = new OutputStreamWriter(inOutput);
        HttpServletRequest request = (HttpServletRequest) inContext.get("request");
        HttpServletResponse response = (HttpServletResponse) inContext.get("response");
        ServletContext servletContext = (ServletContext) inContext.get("application");
        Page page = (Page) inContext.get("page");
        SiteContext site = (SiteContext) inContext.get("site");

        Debug.logVerbose("=========== Running generate ===========", module);

        if (config == null) {
            synchronized(this) {
                if (config == null) {
                    config = freemarker.template.Configuration.getDefaultConfiguration();
                    //nice thought, but doesn't do auto reloading with this: config.setServletContextForTemplateLoading(servletContext, "/");
                    config.setDirectoryForTemplateLoading(site.getRealPageRoot());
                    Debug.logInfo("Site page root: " + site.getRealPageRoot().getAbsolutePath());
                    config.setObjectWrapper(BeansWrapper.getDefaultInstance());
                    WrappingTemplateModel.setDefaultObjectWrapper(BeansWrapper.getDefaultInstance());
                }
            }
        }
        
        //First we let the child generator work
        InputStream in = null;
        if (inConfig != null && inConfig.getChild("generator") != null) {
            //Seems like we should evaluate a filter approach to improve performance here with Velocity
            ByteArrayOutputStream tmpout = new ByteArrayOutputStream();
            subGenerate(inContext, inConfig, tmpout);
            in = new ByteArrayInputStream(tmpout.toByteArray());
        } else {
            Repository notwrapped = page.getRepository();
            in = notwrapped.getInputStream(page.getContentPath());
        }
        
        try {
            Template parsedTemplate = null;
            try {
                //parsedTemplate = config.getTemplate(page.getContentPath(), request.getLocale());
                parsedTemplate = config.getTemplate(page.getContentPath(), request.getLocale());
            } catch (IOException e) {
                throw new WSPException(e);
            }
            parsedTemplate.setObjectWrapper(BeansWrapper.getDefaultInstance());
            
            Writer writer = new OutputStreamWriter(inOutput ,"UTF-8");
            
            try {
                SimpleHash root = new SimpleHash(BeansWrapper.getDefaultInstance());                
                Map map = new JPublishContextMap(inContext);
                root.put("context", new BeanModel(map, BeansWrapper.getDefaultInstance()));
                for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    Object value = entry.getValue();
                    root.put(key, BeansWrapper.getDefaultInstance().wrap(value));
                    //if (Debug.verboseOn()) Debug.logVerbose("==== Adding to the freemarker root " + key + ":" + value, module);
                }
                
                FreeMarkerViewHandler.prepOfbizRoot(root, request, response);
                
                /*
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(in);

                TemplateModel document = new DOMNodeModel(doc);
                root.put("document", document);
                 */
                parsedTemplate.process(root, writer, BeansWrapper.getDefaultInstance());
                writer.flush();
            } finally {
                writer.close();
            }
            
            
            //parsedTemplate = getConfiguration().getTemplate(templateFile.getName(), templateEncoding);
        } catch (IOException ioe) {
            throw new WSPException(ioe);
        }
        
    }
}

/* An example of what might be in the JPublish inContext using WSP
 *
==== Adding to the freemarker root request:org.apache.coyote.tomcat4.CoyoteRequestFacade@9ca1fb
==== Adding to the freemarker root response:org.apache.coyote.tomcat4.CoyoteResponseFacade@14e9851
==== Adding to the freemarker root session:org.apache.catalina.session.StandardSessionFacade@4dd1b
==== Adding to the freemarker root application:org.apache.catalina.core.ApplicationContextFacade@d0f59e
==== Adding to the freemarker root content:/catalog/category.jsp
==== Adding to the freemarker root syslog:com.anthonyeden.lib.log.SystemErrorLogger@e1eae7
==== Adding to the freemarker root page:/templates/main_lr.html
==== Adding to the freemarker root pages:org.jpublish.finder.DefaultPageFinder@e9a7c2
==== Adding to the freemarker root google:com.wspublisher.modules.soapclient.google.GoogleSearchServiceLocator@58213c
==== Adding to the freemarker root out:org.apache.coyote.tomcat4.CoyoteOutputStream@e4e358
==== Adding to the freemarker root site:org.jpublish.SiteContext@1898115
==== Adding to the freemarker root browser:com.wspublisher.web.Browser@1503458
==== Adding to the freemarker root url_util:org.jpublish.util.URLUtilities@229ed4
 *
 */
