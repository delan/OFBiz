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
import org.jpublish.util.JPublishContextMap;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.log.LogManager;
import com.anthonyeden.lib.log.Logger;
import com.wspublisher.WSPException;
import com.wspublisher.generators.AbstractGenerator;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

import org.ofbiz.core.util.Debug;


/**
 * This generator uses FreeMarker to process a template file, which (hopefully)
 * uses the content on the given page.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.1
 */
public class FreeMarkerGenerator extends AbstractGenerator {
    public static final String NAME = "ofbiz-freemarker";
    private static Logger log = LogManager.getLogger(FreeMarkerGenerator.class.getName());
    
    public FreeMarkerGenerator() {
        super(NAME);
        //System.out.println("=========== Initializing OFBiz FreeMarkerGenerator ===========");
    }
    
    /*
     * @see Generator#generate(Page, JPublishContext)
     */
    public void generate(JPublishContext inContext, Configuration inConfig, OutputStream inOutput) throws Throwable {
        Writer out = new OutputStreamWriter(inOutput);
        Page page = (Page) inContext.get("page");

        //System.out.println("=========== Running generate ===========");
        
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
            //should cache this I guess
            /*String template = inConfig.getChildValue("template");
             */
            Template parsedTemplate = new Template(page.getContentFile().getAbsolutePath(), new FileReader(page.getContentFile()));
            Writer writer = new OutputStreamWriter(inOutput ,"UTF-8");
            
            try {
                SimpleHash root = new SimpleHash();

                HttpServletRequest request = null;
                HttpServletResponse response = null;
                ServletContext application = null;
                
                //TODO: should make the config file tell us what TemplateModel to create
                Map map = new JPublishContextMap(inContext);
                for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    Object value = entry.getValue();
                    if ("request".equals(key)) {
                        request = (HttpServletRequest) value;
                    } else if ("request".equals(key)) {
                        response = (HttpServletResponse) value;
                    } else if ("application".equals(key)) {
                        application = (ServletContext) value;
                    } else {
                        root.put(key, BeansWrapper.getDefaultInstance().wrap(value));
                        //System.out.println("==== Adding to the freemarker root " + key + ":" + inContext.get(key));
                    }
                }
                
                FreeMarkerViewHandler.prepOfbizRoot(root, request, response, application);
                
                /*
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(in);

                TemplateModel document = new DOMNodeModel(doc);
                root.put("document", document);
                 */
                parsedTemplate.process(root, writer);
                writer.flush();
            } finally {
                writer.close();
            }
            
            
            //parsedTemplate = getConfiguration().getTemplate(templateFile.getName(), templateEncoding);
        } catch (IOException ioe) {
            throw new WSPException(ioe);
        }
        
    }
    
    protected  freemarker.template.Configuration getConfiguration() throws IOException {
        freemarker.template.Configuration config = freemarker.template.Configuration.getDefaultConfiguration();
        config.setDirectoryForTemplateLoading(getSiteContext().getRealPageRoot());
        return config;
    }
}
