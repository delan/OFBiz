/*
 * $Id: RenderSubContentTransform.java,v 1.3 2003/12/21 03:40:50 byersa Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.ContentManagementWorker;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.*;
import org.ofbiz.service.*;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.Template;
import freemarker.template.Configuration;

import org.jpublish.Page;
/**
 * RenderSubContentTransform - Freemarker Transform for Content rendering
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 *
 * This transform cannot be called recursively (at this time).
 */
public class RenderSubContentTransform implements TemplateTransformModel {
    
   /**
    * Does a conditional search to return a value for a parameter with the passed name.
    * Looks first to see if it was passed as an argument to the transform.
    * Secondly, it looks to see if it is passed as a parameter in the template context object.
    * <p>
    * Note that this is different from the getArg method of EditRenderDataResourceTransform,
    * which checks the request object instead of the template context object.
    */
    public static String getArg(Map args, String key, Environment env ) {
        return FreeMarkerWorker.getArg(args, key, env);
    }

    public static String getArg(Map args, String key, Map ctx ) {
        return FreeMarkerWorker.getArg(args, key, ctx);
    }

   

    public Writer getWriter(final Writer out, Map args) {                      

        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
        if (ctx == null) 
            ctx = new HashMap();
        final String mapKey = getArg(args, "mapKey", ctx);
        final String subContentId = getArg(args, "subContentId", ctx);
        final String subDataResourceTypeId = getArg(args, "subDataResourceTypeId", ctx);
        final String contentId = getArg(args, "contentId", ctx);
        final String mimeTypeId = getArg(args, "mimeTypeId", ctx);
        final Locale locale = (Locale)FreeMarkerWorker.getWrappedObject("locale", env);
        final HttpServletRequest request = 
                       (HttpServletRequest)FreeMarkerWorker.getWrappedObject("request", env);
        final GenericDelegator delegator = 
                       (GenericDelegator)FreeMarkerWorker.getWrappedObject("delegator", env);
        final GenericValue userLogin = 
                       (GenericValue)FreeMarkerWorker.getWrappedObject("userLogin", env);
        GenericValue subContentDataResourceViewTemp = 
                       (GenericValue)FreeMarkerWorker.getWrappedObject("subContentDataResourceView", env);
        if (subContentDataResourceViewTemp == null) {
            List assocTypes = UtilMisc.toList("SUB_CONTENT");
            Timestamp fromDate = UtilDateTime.nowTimestamp();
            try {
                subContentDataResourceViewTemp = ContentWorker.getSubContent( delegator,
                         contentId, mapKey, subContentId, userLogin, assocTypes, fromDate);
            } catch(IOException e) {
               throw new RuntimeException(e.getMessage());
            }
        }

        final GenericValue subContentDataResourceView = subContentDataResourceViewTemp;  

        Debug.logInfo("in RenderSubContent, start.","");
        Debug.logInfo("in RenderSubContent, mapKey:" + mapKey,"");
        Debug.logInfo("in RenderSubContent, subContentId:" + subContentId,"");
        Debug.logInfo("in RenderSubContent, subDataResourceTypeId:" + subDataResourceTypeId,"");
        Debug.logInfo("in RenderSubContent, contentId:" + contentId,"");
        Debug.logInfo("in RenderSubContent, mimeTypeId:" + mimeTypeId,"");
        Debug.logInfo("in RenderSubContent, locale:" + locale,"");

        final Map templateContext = ctx;
        
        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {  
                try {                              
        Debug.logInfo("in RenderSubContent, close:","");
                    renderSubContent();
                } catch (IOException e) {
                    throw new IOException(e.getMessage());
                }
            }


        public void renderSubContent() throws IOException {
         
            TemplateHashModel dataRoot = env.getDataModel();
        

                Timestamp fromDate = UtilDateTime.nowTimestamp();
                ServletContext servletContext 
                    = (ServletContext)request.getSession().getServletContext();
                String rootDir = servletContext.getRealPath("/");
                String webSiteId = (String)servletContext.getAttribute("webSiteId");
                String https = (String)servletContext.getAttribute("https");
                  Debug.logInfo("in RenderSubContent, rootDir:"+rootDir,"");
                templateContext.put("webSiteId", webSiteId);
                templateContext.put("https", https);
                templateContext.put("rootDir", rootDir);
                TemplateHashModel oldRoot = env.getDataModel();
                SimpleHash templateRoot = FreeMarkerWorker.buildNewRoot(oldRoot);
                templateRoot.put("context", templateContext);
                  Debug.logInfo("in RenderSubContent, calling renderSubCasT:"
                           + " contentId:" + contentId
                           + " mapKey:" + mapKey
                           + " subContentId:" + subContentId
                     ,"");
                if (subContentDataResourceView != null) {
                  Debug.logInfo("in RenderSubContent, "
                           + " subContentDataResourceView:" + subContentDataResourceView.get("contentId")
                           + " / " + subContentDataResourceView.get("drDataResourceId")
                     ,"");
                }
                Map results = ContentWorker.renderSubContentAsText( delegator,
                                  contentId, out, mapKey, subContentId, subContentDataResourceView,
                                  templateRoot, locale, mimeTypeId, userLogin, fromDate);
    
        Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
        Debug.logInfo("in RenderSubContent, contentId." + ctx.get("contentId"),"");
                templateContext.put("mapKey", null);
                templateContext.put("subContentId", null);
                templateContext.put("subDataResourceTypeId", null);
                templateContext.put("contentId", contentId);
                templateContext.put("mimeTypeId", null);
                templateContext.put("locale", locale);
        Debug.logInfo("in RenderSubContent, after.","");
        Debug.logInfo("in RenderSubContent, mapKey:" + mapKey,"");
        Debug.logInfo("in RenderSubContent, subContentId:" + subContentId,"");
        Debug.logInfo("in RenderSubContent, subDataResourceTypeId:" + subDataResourceTypeId,"");
        Debug.logInfo("in RenderSubContent, contentId:" + contentId,"");
        Debug.logInfo("in RenderSubContent, mimeTypeId:" + mimeTypeId,"");
        Debug.logInfo("in RenderSubContent, locale:" + locale,"");
        Debug.logInfo("in RenderSubContent, contentId2." + ctx.get("contentId"),"");
            return;
            }
        };
    }

}
