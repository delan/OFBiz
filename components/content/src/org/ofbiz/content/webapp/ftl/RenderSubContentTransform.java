/*
 * $Id: RenderSubContentTransform.java,v 1.1 2003/12/05 21:37:16 byersa Exp $
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
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.*;
import org.ofbiz.service.*;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Environment;
import freemarker.template.SimpleScalar;
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
 * @version    $Revision: 1.1 $
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
            SimpleScalar s = (SimpleScalar)args.get(key);
            String returnVal = (s == null) ? null : s.toString();
            if (returnVal == null) {
                Map templateContext = (Map)FreeMarkerWorker.getWrappedObject("context", env);
                if (templateContext != null) {
                    returnVal = (String)templateContext.get(key);
                }
            }
            return returnVal;
    }

   

    public Writer getWriter(final Writer out, Map args) {                      

        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        final String mapKey = getArg(args, "mapKey", env);
        final String subContentId = getArg(args, "subContentId", env);
        final String subDataResourceTypeId = getArg(args, "subDataResourceTypeId", env);
        final String contentId = getArg(args, "contentId", env);
        final String mimeTypeId = getArg(args, "mimeTypeId", env);
        final Locale locale = (Locale)FreeMarkerWorker.getWrappedObject("locale", env);
        final LocalDispatcher dispatcher = 
                       (LocalDispatcher)FreeMarkerWorker.getWrappedObject("dispatcher", env);
        final GenericValue userLogin = 
                       (GenericValue)FreeMarkerWorker.getWrappedObject("userLogin", env);

        /*
        Debug.logInfo("in RenderSubContent, start.","");
        Debug.logInfo("in RenderSubContent, mapKey:" + mapKey,"");
        Debug.logInfo("in RenderSubContent, subContentId:" + subContentId,"");
        Debug.logInfo("in RenderSubContent, subDataResourceTypeId:" + subDataResourceTypeId,"");
        Debug.logInfo("in RenderSubContent, contentId:" + contentId,"");
        Debug.logInfo("in RenderSubContent, mimeTypeId:" + mimeTypeId,"");
        Debug.logInfo("in RenderSubContent, locale:" + locale,"");
        Debug.logInfo("in RenderSubContent, dispatcher:" + dispatcher,"");
        */
        
        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {  
                try {                              
                    renderSubContent();
                } catch (IOException e) {
                    throw new IOException(e.getMessage());
                }
            }


        public void renderSubContent() throws IOException {
         
            TemplateHashModel dataRoot = env.getDataModel();
                Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
                //Debug.logInfo("in RenderSubContent, ctx:" + ctx,"");
                if (ctx == null) 
                    ctx = new HashMap();
                final Map templateContext = ctx;
        
                GenericValue subContentDataResourceView 
                       = (GenericValue)templateContext.get("subContentDataResourceView");
                //Debug.logInfo("in RenderSubContent, subContentDataResourceView:" + subContentDataResourceView,"");
    
                Map serviceIn = new HashMap();
                serviceIn.put("contentId", contentId);
                serviceIn.put("mapKey", mapKey);
                serviceIn.put("subContentId", subContentId);
                serviceIn.put("mimeTypeId", mimeTypeId);
                serviceIn.put("locale", locale);
                serviceIn.put("templateContext", ctx);
                serviceIn.put("outWriter", out);
                serviceIn.put("subContentDataResourceView", subContentDataResourceView);
                try {
                    Map results = dispatcher.runSync("renderSubContentAsText", serviceIn);
                    subContentDataResourceView = (GenericValue)results.get("view");
                    //Debug.logInfo("in EditRenderSubContent, subContentDataResourceView:" + subContentDataResourceView, "");
                } catch(GenericServiceException e) {
                    throw new RuntimeException(e.getMessage());
                }
                /* to use this call, dispatcher and delegator must be passed in templateContext
                Timestamp fromDate = UtilDateTime.nowTimestamp();
                Map results = ContentWorker.renderSubContentAsText(dispatcher, delegator,
                                  contentId, out, mapKey, subContentId, subContentDataResourceView,
                                  ctx, locale, mimeTypeId, userLogin, fromDate);
                */
    
            return;
            }
        };
    }

/*
    public void checkForLoop(String path) throws IOException {
        List templateList = (List)request.getAttribute("templateList");
            //Debug.logInfo("in checkForLoop, templateList:" +templateList, "");
            //Debug.logInfo("in checkForLoop, templatePath:" +path, "");
        if (templateList == null) {
            templateList = new ArrayList();
        } else {
            if (templateList.contains(path)) {
                throw new IOException(path + " has already been visited.");
            }
        }
        templateList.add(path);
        request.setAttribute("templateList", templateList);
        return;
    }
*/

}
