/*
 * $Id: LoopSubContentTransform.java,v 1.1 2003/12/21 03:39:11 byersa Exp $
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
import freemarker.template.TransformControl;

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
import org.ofbiz.content.content.ContentServicesComplex;
import org.ofbiz.content.webapp.ftl.LoopWriter;

/**
 * LoopSubContentTransform - Freemarker Transform for URLs (links)
 *
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class LoopSubContentTransform implements TemplateTransformModel {
   
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

    public static boolean prepCtx(GenericDelegator delegator, Map ctx, String contentId, 
                                  String mimeTypeIdTemp, String subDataResourceTypeIdTemp ) {

        //Debug.logInfo("in LoopSubContent, prepCtx, ctx :" + ctx, "");
        List lst = (List)ctx.get("entityList");
        //Debug.logInfo("in LoopSubContent, prepCtx, lst :" + lst, "");
        Integer idx = (Integer)ctx.get("entityIndex");
        Debug.logInfo("in LoopSubContent, prepCtx, idx :" + idx, "");
        int i = idx.intValue();
        Debug.logInfo("in LoopSubContent, prepCtx, i :" + i, "");
        if (i >= lst.size()) {
            return false;
        }
        GenericValue subContentDataResourceView = (GenericValue)lst.get(i);
        ctx.put("subContentDataResourceView", subContentDataResourceView);
        GenericValue electronicText = null;
        try {
            electronicText = subContentDataResourceView.getRelatedOne("ElectronicText");
        } catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        ctx.put("textData", electronicText.get("textData"));
        ctx.put("entityIndex", new Integer(i + 1));
        Debug.logInfo("in LoopSubContent, subContentDataResourceView contentId/drDataResourceId:" 
                      + subContentDataResourceView.get("contentId") 
                      + " / " + subContentDataResourceView.get("drDataResourceId") , "");

        String dataResourceIdTemp =  (String)subContentDataResourceView.get("drDataResourceId");
              Debug.logInfo("in LoopSubContent(0), dataResourceIdTemp ." + dataResourceIdTemp ,"");
        String subContentIdSubTemp = (String)subContentDataResourceView.get("contentId");
              Debug.logInfo("in LoopSubContent(0), subContentIdSubTemp ." + subContentIdSubTemp ,"");
              Debug.logInfo("in LoopSubContent(0), mimeTypeIdTemp." + mimeTypeIdTemp,"");
        if (UtilValidate.isEmpty(subDataResourceTypeIdTemp)) {
            subDataResourceTypeIdTemp = (String)subContentDataResourceView.get("drDataResourceTypeId");
        }
        if (UtilValidate.isEmpty(mimeTypeIdTemp)) {
            mimeTypeIdTemp = (String)subContentDataResourceView.get("mimeTypeId");
                if (UtilValidate.isEmpty(mimeTypeIdTemp) && UtilValidate.isNotEmpty(contentId)) { // will need these below
                    try {
                        GenericValue parentContent = delegator.findByPrimaryKey("Content",
                                           UtilMisc.toMap("contentId", contentId));
                        if (parentContent != null) {
                            mimeTypeIdTemp = (String)parentContent.get("mimeTypeId");
                            ctx.put("parentContent", parentContent);
              Debug.logInfo("in LoopSubContent, parentContentId: " + parentContent.get("contentId"),"");
                        }
                    } catch (GenericEntityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            
        }
              Debug.logInfo("in LoopSubContent(2), mimeTypeIdTemp." + mimeTypeIdTemp,"");
              Debug.logInfo("in LoopSubContent, subContentId/Sub." + subContentIdSubTemp,"");
            ctx.put("subContentId", subContentIdSubTemp);
            ctx.put("drDataResourceId", dataResourceIdTemp);
            ctx.put("subContentDataResourceView", subContentDataResourceView);
            ctx.put("mimeTypeId", mimeTypeIdTemp);
            ctx.put("dataResourceId", dataResourceIdTemp);
            ctx.put("subContentIdSub", subContentIdSubTemp);
        return true;
    }

    public Writer getWriter(final Writer out, Map args) {                      
        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
        final String editTemplate = getArg(args, "editTemplate", ctx);
        Debug.logInfo("in LoopSubContent, editTemplate:"+editTemplate,"");
        final String wrapTemplateId = getArg(args, "wrapTemplateId", ctx);
        final String mapKey = getArg(args, "mapKey", ctx);
        Debug.logInfo("in LoopSubContent, mapKey:"+mapKey,"");
        final String templateContentId = getArg(args, "templateContentId", ctx);
        Debug.logInfo("in LoopSubContent, templateContentId:"+templateContentId,"");
        final String subDataResourceTypeId = getArg(args, "subDataResourceTypeId", ctx);
        final String contentId = getArg(args, "contentId", ctx);
        final String rootDir = getArg(args, "rootDir", ctx);
        final String webSiteId = getArg(args, "webSiteId", ctx);
        final String https = getArg(args, "https", ctx);
        Debug.logInfo("in LoopSubContent, contentId:"+contentId,"");
        final String viewSize = getArg(args, "viewSize", ctx);
        final String viewIndex = getArg(args, "viewIndex", ctx);
        final String listSize = getArg(args, "listSize", ctx);
        final String highIndex = getArg(args, "highIndex", ctx);
        final String lowIndex = getArg(args, "lowIndex", ctx);
        final String queryString = getArg(args, "queryString", ctx);
        final Locale locale = (Locale)FreeMarkerWorker.getWrappedObject("locale", env);
        final String mimeTypeId = getArg(args, "mimeTypeId", ctx);
        final LocalDispatcher dispatcher = 
                       (LocalDispatcher)FreeMarkerWorker.getWrappedObject("dispatcher", env);
        final GenericDelegator delegator = 
                       (GenericDelegator)FreeMarkerWorker.getWrappedObject("delegator", env);
        final GenericValue userLogin = 
                       (GenericValue)FreeMarkerWorker.getWrappedObject("userLogin", env);

        ctx.put("mapKey", mapKey);
        ctx.put("contentId", contentId);
        ctx.put("templateContentId", templateContentId);
        ctx.put("locale", locale);

        //ctx.put("userLogin", userLogin);
        List assocTypes = UtilMisc.toList("SUB_CONTENT");
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        GenericValue subContentDataResourceView = null;
        Map results = ContentServicesComplex.getAssocAndContentAndDataResourceMethod(
                          delegator, contentId, mapKey, "From", fromDate, null,
                          null, null, assocTypes, null);
        List entityList = (List)results.get("entityList");
        ctx.put("entityList", entityList);
        final Map templateContext = ctx;

        
        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                StringBuffer ctxBuf = (StringBuffer)templateContext.get("buf");
                ctxBuf.append(cbuf, off, len);
                  ////Debug.logInfo("in LoopSubContent, buf:"+buf.toString(),"");
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {  
                templateContext.put("buf", new StringBuffer());
                templateContext.put("entityIndex", new Integer(0));
        Debug.logInfo("in LoopSubContent, onStart","");
                boolean inProgress = prepCtx(delegator, templateContext, contentId, mimeTypeId, 
                                              subDataResourceTypeId);
        Debug.logInfo("in LoopSubContent, onStart, inProgress:"+inProgress,"");
                if (inProgress) 
                    return TransformControl.EVALUATE_BODY;
                else
                    return TransformControl.SKIP_BODY;
            }

            public int afterBody() throws TemplateModelException, IOException {  
                Integer idx = (Integer)templateContext.get("entityIndex");
                Debug.logInfo("in LoopSubContent, prepCtx, idx :" + idx, "");
                int i = idx.intValue();
                Debug.logInfo("in LoopSubContent, afterBody, i:"+i,"");
                Debug.logInfo("in LoopSubContent, afterBody, templateContext.entityIndex:"+templateContext.get("entityIndex"),"");
                boolean inProgress = prepCtx(delegator, templateContext, contentId, mimeTypeId, 
                                         subDataResourceTypeId);
        Debug.logInfo("in LoopSubContent, afterBody, inProgress:"+inProgress + " inProgress:" + inProgress,"");
                out.write(templateContext.get("buf").toString());
                templateContext.put("buf", new StringBuffer());
                if (inProgress) 
                    return TransformControl.REPEAT_EVALUATION;
                else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {  


                String wrappedFTL = buf.toString();
                  //Debug.logInfo("in LoopSubContent, wrappedFTL:"+wrappedFTL,"");
                    if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                        templateContext.put("wrappedFTL", wrappedFTL);
                  Debug.logInfo("in LoopSubContent, rootDir:"+rootDir,"");
                        templateContext.put("webSiteId", webSiteId);
                        templateContext.put("https", https);
                        templateContext.put("rootDir", rootDir);
                        TemplateHashModel oldRoot = env.getDataModel();
                        SimpleHash templateRoot = FreeMarkerWorker.buildNewRoot(oldRoot);
                        templateRoot.put("viewSize", viewSize);
                        templateRoot.put("viewIndex", viewIndex);
                        templateRoot.put("listSize", listSize);
                        templateRoot.put("highIndex", highIndex);
                        templateRoot.put("lowIndex", lowIndex);
                        templateRoot.put("queryString", queryString);
                        templateRoot.put("wrapDataResourceTypeId", subDataResourceTypeId);
                        Debug.logInfo("in LoopSubContent, wrapDataResourceTypeId:" + subDataResourceTypeId,"");
                        templateRoot.put("wrapContentIdTo", contentId);
                        Debug.logInfo("in LoopSubContent, wrapContentIdTo:" + contentId,"");
                        templateRoot.put("wrapMimeTypeId", mimeTypeId);
                        Debug.logInfo("in LoopSubContent, wrapMimeTypeId:" + mimeTypeId,"");
                        templateRoot.put("wrapMapKey", mapKey);
                        //Debug.logInfo("in LoopSubContent, wrapMapKey:" + mapKey,"");
                        templateRoot.put("context", templateContext);
                        Debug.logInfo("in LoopSubContent, calling renderContentAsText, wrapTemplateId:"+wrapTemplateId,"");
                        ContentWorker.renderContentAsText(delegator, wrapTemplateId, out,
                            templateRoot, null, locale, mimeTypeId);
        Debug.logInfo("in LoopSubContent, after renderContentAsText","");
        Map ctx = (Map)FreeMarkerWorker.getWrappedObject("context", env);
        Debug.logInfo("in LoopSubContent, contentId:" + ctx.get("contentId"),"");
                templateContext.put("contentId", contentId);
                templateContext.put("locale", locale);
                templateContext.put("mapKey", null);
                templateContext.put("subContentId", null);
                templateContext.put("templateContentId", null);
                templateContext.put("subDataResourceTypeId", null);
                templateContext.put("mimeTypeId", null);
        Debug.logInfo("in LoopSubContent, after.","");
        Debug.logInfo("in LoopSubContent, mapKey:" + mapKey,"");
        Debug.logInfo("in LoopSubContent, subDataResourceTypeId:" + subDataResourceTypeId,"");
        Debug.logInfo("in LoopSubContent, contentId:" + contentId,"");
        Debug.logInfo("in LoopSubContent, mimeTypeId:" + mimeTypeId,"");
        Debug.logInfo("in LoopSubContent, locale:" + locale,"");
        Debug.logInfo("in LoopSubContent, contentId2." + ctx.get("contentId"),"");

                    }
            }
        };
    }
}
