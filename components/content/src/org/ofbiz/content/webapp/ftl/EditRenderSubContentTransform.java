/*
 * $Id: EditRenderSubContentTransform.java,v 1.2 2003/12/06 08:38:47 byersa Exp $
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
 * @version    $Revision: 1.2 $
 * @since      3.0
 */
public class EditRenderSubContentTransform implements TemplateTransformModel {
   
   /**
    * A wrapper for the FreeMarkerWorker version.
    */
    public static Object getWrappedObject(String varName, Environment env) {
        return FreeMarkerWorker.getWrappedObject(varName, env);
    }

    
   /**
    * Does a conditional search to return a value for a parameter with the passed name.
    * Looks first to see if it was passed as an argument to the transform.
    * Secondly, it looks to see if it is passed as an attribute in the request object.
    * Then it looks to see if it is a parameter in the request object.
    * <p>
    * Note that this is different from the getArg method of RenderDataResourceTransform,
    * which checks the template context object instead of the request object.
    */
    public static String getArg(Map args, String key, Environment env ) {
            SimpleScalar s = (SimpleScalar)args.get(key);
            String returnVal = (s == null) ? null : s.toString();
            if (returnVal == null) {
                HttpServletRequest request = (HttpServletRequest)getWrappedObject("request", env);
                returnVal = (String)request.getAttribute(key);
                if (UtilValidate.isEmpty(returnVal)) {
                    returnVal = (String)request.getParameter(key);
                }
                /*
                if (returnVal == null) {
                    SimpleScalar s2 = (SimpleScalar)args.get(key + "Default");
                    returnVal = (s2 == null) ? null : s2.toString();
                }
                */
            }
            return returnVal;
    }

    public Writer getWriter(final Writer out, Map args) {                      
        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        final String editTemplate = getArg(args, "editTemplate", env);
        final String wrapTemplate = getArg(args, "wrapTemplate", env);
        final String mapKey = getArg(args, "mapKey", env);
        String subContentIdTemp = getArg(args, "subContentId", env);
        final String subDataResourceTypeId = getArg(args, "subDataResourceTypeId", env);
        final String contentId = getArg(args, "contentId", env);
        final HttpServletRequest request = (HttpServletRequest)getWrappedObject("request", env);
        final Locale locale = UtilHttp.getLocale(request);
        final GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        final LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        final Page page = (Page)getWrappedObject("page", env);
        String mimeTypeIdTemp = getArg(args, "mimeTypeId", env);
        //Debug.logInfo("in EditRenderSubContent, mapKey." + mapKey,"");
        //Debug.logInfo("in EditRenderSubContent, subContentId(0)." + subContentIdTemp,"");

        Map ctx = (Map)getWrappedObject("context", env);
        final TemplateHashModel dataRoot = env.getDataModel();
        JPublishContext jpub = null;
        try {
            BeanModel jctx = (BeanModel) env.getVariable("jpublishContext");
            jpub = (JPublishContext)jctx.getWrappedObject();
        } catch(TemplateModelException e) {
           throw new RuntimeException(e.getMessage());
        }

        if (jpub == null) {
            throw new RuntimeException("JPublishContext object in environment cannot be null.");
        }
        jpub.put("mapKey", mapKey);
        jpub.put("subDataResourceTypeId", subDataResourceTypeId);
        jpub.put("contentId", contentId);
        jpub.put("locale", locale);

        ctx.put("mapKey", mapKey);
        ctx.put("subDataResourceTypeId", subDataResourceTypeId);
        ctx.put("contentId", contentId);
        ctx.put("locale", locale);

        // This transform does not need information about the subContent until the
        // close action, but any embedded RenderDataResourceTransformation will need it
        // and since it cannot be passed back up from that transform, the subContent view
        // is gotten here and made available to underlying transforms to save overall
        // processing time.
        GenericValue parentContent = null;
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        ctx.put("userLogin", userLogin);
        List assocTypes = UtilMisc.toList("SUB_CONTENT");
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        GenericValue subContentDataResourceView = null;
        try {
            subContentDataResourceView = ContentWorker.getSubContent(dispatcher, delegator,
                                 contentId, mapKey, subContentIdTemp, userLogin, assocTypes, fromDate);
        } catch(IOException e) {
           throw new RuntimeException(e.getMessage());
        }

        //Debug.logInfo("in EditRenderSubContent, subContentDataResourceView:" + subContentDataResourceView, "");
        if (subContentDataResourceView != null 
            && subContentDataResourceView.get("contentId") != null) {

            String dataResourceId = (String)subContentDataResourceView.get("drDataResourceId");
            subContentIdTemp = (String)subContentDataResourceView.get("contentId");
              //Debug.logInfo("in EditRenderSubContent, mimeTypeIdTemp." + mimeTypeIdTemp,"");
            if (UtilValidate.isEmpty(mimeTypeIdTemp)) {
                if (UtilValidate.isEmpty(wrapTemplate)) { // will need these below
                    try {
                        parentContent = delegator.findByPrimaryKey("Content",
                                           UtilMisc.toMap("contentId", contentId));
                        if (parentContent == null) {
                            throw new RuntimeException("'Content' cannot be null if 'wrapTemplate' is.");
                        }
                    } catch (GenericEntityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
                if (UtilValidate.isEmpty(mimeTypeIdTemp)) { 
                    try {
                        mimeTypeIdTemp = FreeMarkerWorker.determineMimeType(delegator, 
                                     subContentDataResourceView, parentContent, subContentIdTemp,
                                     dataResourceId, contentId );
                    } catch(GenericEntityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            
            }
            jpub.put("subContentId", subContentIdTemp);
            jpub.put("drDataResourceId", dataResourceId);
            jpub.put("subContentDataResourceView", subContentDataResourceView);
            jpub.put("mimeTypeId", mimeTypeIdTemp);
            ctx.put("subContentId", subContentIdTemp);
            ctx.put("drDataResourceId", dataResourceId);
            ctx.put("subContentDataResourceView", subContentDataResourceView);
            ctx.put("mimeTypeId", mimeTypeIdTemp);
            request.setAttribute("drDataResourceId", subContentDataResourceView.get("drDataResourceId"));
        } else {
            jpub.put("subContentId", null);
            jpub.put("drDataResourceId", null);
            jpub.put("subContentDataResourceView", null);
            jpub.put("mimeTypeId", null);
            ctx.put("subContentId", null);
            ctx.put("drDataResourceId", null);
            ctx.put("subContentDataResourceView", null);
            ctx.put("mimeTypeId", null);
            request.setAttribute("drDataResourceId", null);
        }
 
        final JPublishContext jpubContext = jpub;
        final GenericValue finalSubContentView = subContentDataResourceView;
        final GenericValue content = parentContent;
        final String mimeTypeId = mimeTypeIdTemp;
        final String subContentId = subContentIdTemp;
        
        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                  ////Debug.logInfo("in EditRenderSubContent, buf:"+buf.toString(),"");
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {  

                String wrappedFTL = buf.toString();
                String wrapTemplateTemp = wrapTemplate;
                  ////Debug.logInfo("in EditRenderSubContent, wrappedFTL:"+wrappedFTL,"");
                jpubContext.put("wrappedFTL", wrappedFTL);
                Map ctx = (Map)getWrappedObject("context", env);
                ctx.put("wrappedFTL", wrappedFTL);

                if (editTemplate != null && editTemplate.equalsIgnoreCase("true")) {
                  ////Debug.logInfo("in transform, wrapTemplateTemp(0):"+wrapTemplateTemp,"");
                    GenericValue dataResource = null;
                    if (UtilValidate.isEmpty(wrapTemplateTemp)) {
                        if (UtilValidate.isEmpty(contentId)) {
                            throw new IOException("'contentId' cannot be null if 'wrapTemplate' is.");
                        }
                        try {
                            dataResource = content.getRelatedOne("DataResource");
                        } catch (GenericEntityException e) {
                            throw new IOException(e.getMessage());
                        }
                        if (dataResource != null) {
                            wrapTemplateTemp = (String)dataResource.get("objectInfo");
                        }
                    }
                    if (UtilValidate.isNotEmpty(wrapTemplateTemp)) {
                        if (!wrapTemplateTemp.endsWith(".ftl") ) wrapTemplateTemp += ".ftl";
                        SiteContext siteContext = (SiteContext)getWrappedObject("site", env);

                        try {
                            Template template = siteContext.getTemplateManager().getTemplate(wrapTemplateTemp);
                           //////Debug.logInfo("in transform, wrapTemplateTemp(1):"+wrapTemplateTemp,"");
                            //checkForLoop(wrapTemplateTemp);
                            template.merge(jpubContext, page, out);
                        } catch (TemplateMergeException e) {
                            throw new IOException(e.getMessage());
                        } catch (Exception e) {
                            throw new IOException(e.getMessage());
                        }
                    }

                } else {
                    out.write(wrappedFTL);
                }
            }
        };
    }
}
