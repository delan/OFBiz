/*
 * $Id: EditRenderSubContentTransform.java,v 1.5 2003/12/21 03:40:50 byersa Exp $
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
import java.io.StringReader;
import java.util.*;
import java.sql.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.Environment;

import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TemplateHashModel;

import org.jpublish.SiteContext;
import org.jpublish.Page;
import org.jpublish.JPublishContext;
import org.jpublish.Template;
import org.jpublish.TemplateMergeException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.content.webapp.view.JPublishWrapper;
import org.ofbiz.content.content.ContentWorker;

/**
 * EditRenderSubContentTransform - Freemarker Transform for URLs (links)
 *
 * This is an interactive FreeMarker tranform that allows the user to modify
 * the contents that are placed within it.
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.5 $
 * @since      3.0
 */
public class EditRenderSubContentTransform implements TemplateTransformModel {
   
   /**
    * A wrapper for the FreeMarkerWorker version.
    */
    public static Object getWrappedObject(String varName, Environment env) {
        return FreeMarkerWorker.getWrappedObject(varName, env);
    }

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
        final String editTemplate = getArg(args, "editTemplate", ctx);
        Debug.logInfo("in EditRenderSubContent, editTemplate:"+editTemplate,"");
        final String wrapTemplateId = getArg(args, "wrapTemplateId", ctx);
        Debug.logInfo("in EditRenderSubContent, wrapTemplateId:"+wrapTemplateId,"");
        final String mapKey = getArg(args, "mapKey", ctx);
        Debug.logInfo("in EditRenderSubContent, mapKey:"+mapKey,"");
        final String templateContentId = getArg(args, "templateContentId", ctx);
        Debug.logInfo("in EditRenderSubContent, templateContentId:"+templateContentId,"");
        final String subContentId = getArg(args, "subContentId", ctx);
        Debug.logInfo("in EditRenderSubContent, subContentId:"+subContentId,"");
        String subDataResourceTypeIdTemp = getArg(args, "subDataResourceTypeId", ctx);
        final String contentId = getArg(args, "contentId", ctx);
        Debug.logInfo("in EditRenderSubContent, contentId:"+contentId,"");
        final Locale locale = (Locale)FreeMarkerWorker.getWrappedObject("locale", env);
        String mimeTypeIdTemp = getArg(args, "mimeTypeId", ctx);
        final String rootDir = getArg(args, "rootDir", ctx);
        final String webSiteId = getArg(args, "webSiteId", ctx);
        final String https = getArg(args, "https", ctx);
        final LocalDispatcher dispatcher = 
                       (LocalDispatcher)FreeMarkerWorker.getWrappedObject("dispatcher", env);
        final GenericDelegator delegator = 
                       (GenericDelegator)FreeMarkerWorker.getWrappedObject("delegator", env);
        final GenericValue userLogin = 
                       (GenericValue)FreeMarkerWorker.getWrappedObject("userLogin", env);
        GenericValue subContentDataResourceViewTemp = 
                       (GenericValue)FreeMarkerWorker.getWrappedObject("subContentDataResourceView", env);
        //final HttpServletRequest request = (HttpServletRequest)FreeMarkerWorker.getWrappedObject("request", env);

        ctx.put("mapKey", mapKey);
        ctx.put("subDataResourceTypeIdTemp", subDataResourceTypeIdTemp);
        ctx.put("contentId", contentId);
        ctx.put("templateContentId", templateContentId);
        ctx.put("locale", locale);

        // This transform does not need information about the subContent until the
        // close action, but any embedded RenderDataResourceTransformation will need it
        // and since it cannot be passed back up from that transform, the subContent view
        // is gotten here and made available to underlying transforms to save overall
        // processing time.
        GenericValue parentContent = null;
        //ctx.put("userLogin", userLogin);
        List assocTypes = UtilMisc.toList("SUB_CONTENT");
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        if (subContentDataResourceViewTemp == null) {
            try {
                subContentDataResourceViewTemp = ContentWorker.getSubContent( delegator,
                                 contentId, mapKey, subContentId, userLogin, assocTypes, fromDate);
            } catch(IOException e) {
               throw new RuntimeException(e.getMessage());
            }
        }

        final GenericValue subContentDataResourceView = subContentDataResourceViewTemp;  

        String dataResourceIdTemp = null;
        String subContentIdSubTemp = null;
        if (subContentDataResourceView != null 
            && subContentDataResourceView.get("contentId") != null) {

            Debug.logInfo("in EditRenderSubContent, subContentDataResourceView contentId/drDataResourceId:" 
                      + subContentDataResourceView.get("contentId") 
                      + " / " + subContentDataResourceView.get("drDataResourceId") , "");

            dataResourceIdTemp =  (String)subContentDataResourceView.get("drDataResourceId");
              Debug.logInfo("in EditRenderSubContent(0), dataResourceIdTemp ." + dataResourceIdTemp ,"");
            subContentIdSubTemp = (String)subContentDataResourceView.get("contentId");
              Debug.logInfo("in EditRenderSubContent(0), subContentIdSubTemp ." + subContentIdSubTemp ,"");
              Debug.logInfo("in EditRenderSubContent(0), mimeTypeIdTemp." + mimeTypeIdTemp,"");
            if (UtilValidate.isEmpty(subDataResourceTypeIdTemp)) {
                subDataResourceTypeIdTemp = (String)subContentDataResourceView.get("drDataResourceTypeId");
            }
            if (UtilValidate.isEmpty(mimeTypeIdTemp)) {
                mimeTypeIdTemp = (String)subContentDataResourceView.get("mimeTypeId");
                if (UtilValidate.isEmpty(mimeTypeIdTemp) && UtilValidate.isNotEmpty(contentId)) { // will need these below
                    try {
                        parentContent = delegator.findByPrimaryKey("Content",
                                           UtilMisc.toMap("contentId", contentId));
                        if (parentContent != null) {
                            mimeTypeIdTemp = (String)parentContent.get("mimeTypeId");
              Debug.logInfo("in EditRenderSubContent, parentContentId: " + parentContent.get("contentId"),"");
                        }
                    } catch (GenericEntityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            
            }
              Debug.logInfo("in EditRenderSubContent(2), mimeTypeIdTemp." + mimeTypeIdTemp,"");
              Debug.logInfo("in EditRenderSubContent, subContentId/Sub." + subContentIdSubTemp,"");
            ctx.put("subContentId", subContentIdSubTemp);
            ctx.put("drDataResourceId", dataResourceIdTemp);
            ctx.put("subContentDataResourceView", subContentDataResourceView);
            ctx.put("mimeTypeId", mimeTypeIdTemp);
            //request.setAttribute("drDataResourceId", subContentDataResourceView.get("drDataResourceId"));
        } else {
            ctx.put("subContentId", null);
            ctx.put("drDataResourceId", null);
            ctx.put("subContentDataResourceView", null);
            ctx.put("mimeTypeId", null);
            //request.setAttribute("drDataResourceId", null);
        }
 
        final String dataResourceId= dataResourceIdTemp;
        final String subContentIdSub= subContentIdSubTemp;
        final GenericValue finalSubContentView = subContentDataResourceView;
        final GenericValue content = parentContent;
        final Map templateContext = ctx;
        //Debug.logInfo("in EditRenderSubContent, templateContext:"+templateContext,"");
        final String mimeTypeId = mimeTypeIdTemp;
        final String subDataResourceTypeId = subDataResourceTypeIdTemp;
        
        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                  Debug.logInfo("in EditRenderSubContent, buf:"+buf.toString(),"");
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {  


                String wrappedFTL = buf.toString();
                  Debug.logInfo("in EditRenderSubContent, wrappedFTL:"+wrappedFTL,"");
                if (editTemplate != null && editTemplate.equalsIgnoreCase("true")) {
                    if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                        templateContext.put("wrappedFTL", wrappedFTL);
                        //ServletContext servletContext = (ServletContext)request.getSession().getServletContext();
                        //String rootDir = servletContext.getRealPath("/");
                  Debug.logInfo("in EditRenderSubContent, rootDir:"+rootDir,"");
                        templateContext.put("webSiteId", webSiteId);
                        templateContext.put("https", https);
                        templateContext.put("rootDir", rootDir);
                        TemplateHashModel oldRoot = env.getDataModel();
                        SimpleHash templateRoot = FreeMarkerWorker.buildNewRoot(oldRoot);
                        templateRoot.put("wrapDataResourceId", dataResourceId);
        Debug.logInfo("in EditRenderSubContent, wrapDataResourceId:" + dataResourceId,"");
                        templateRoot.put("wrapDataResourceTypeId", subDataResourceTypeId);
        Debug.logInfo("in EditRenderSubContent, wrapDataResourceTypeId:" + subDataResourceTypeId,"");
                        templateRoot.put("wrapContentIdTo", contentId);
        Debug.logInfo("in EditRenderSubContent, wrapContentIdTo:" + contentId,"");
                        templateRoot.put("wrapSubContentId", subContentIdSub);
        Debug.logInfo("in EditRenderSubContent, wrapSubContentId:" + subContentIdSub,"");
                        templateRoot.put("wrapMimeTypeId", mimeTypeId);
        Debug.logInfo("in EditRenderSubContent, wrapMimeTypeId:" + mimeTypeId,"");
                        templateRoot.put("wrapMapKey", mapKey);
        //Debug.logInfo("in EditRenderSubContent, wrapMapKey:" + mapKey,"");
                        templateRoot.put("context", templateContext);
        Debug.logInfo("in EditRenderSubContent, calling renderContentAsText, wrapTemplateId:"+wrapTemplateId,"");
                        ContentWorker.renderContentAsText(delegator, wrapTemplateId, out,
                            templateRoot, null, locale, mimeTypeId);
        Debug.logInfo("in EditRenderSubContent, after renderContentAsText","");
        Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
        Debug.logInfo("in EditRenderSubContent, contentId:" + ctx.get("contentId"),"");
                templateContext.put("contentId", contentId);
                templateContext.put("locale", locale);
                templateContext.put("mapKey", null);
                templateContext.put("subContentId", null);
                templateContext.put("templateContentId", null);
                templateContext.put("subDataResourceTypeId", null);
                templateContext.put("mimeTypeId", null);
        Debug.logInfo("in EditRenderSubContent, after.","");
        Debug.logInfo("in EditRenderSubContent, mapKey:" + mapKey,"");
        Debug.logInfo("in EditRenderSubContent, subContentId:" + subContentId,"");
        Debug.logInfo("in EditRenderSubContent, subDataResourceTypeId:" + subDataResourceTypeId,"");
        Debug.logInfo("in EditRenderSubContent, contentId:" + contentId,"");
        Debug.logInfo("in EditRenderSubContent, mimeTypeId:" + mimeTypeId,"");
        Debug.logInfo("in EditRenderSubContent, locale:" + locale,"");
        Debug.logInfo("in EditRenderSubContent, contentId2." + ctx.get("contentId"),"");

                    }
                } else {
                    out.write(wrappedFTL);
                }
            }
        };
    }
}
