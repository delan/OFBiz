/*
 * $Id: FreeMarkerWorker.java,v 1.1 2003/12/05 21:37:59 byersa Exp $
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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.content.webapp.view.ViewHandler;
import org.ofbiz.content.webapp.view.ViewHandlerException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.Environment;

import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;


/**
 * FreemarkerViewHandler - Freemarker Template Engine Util
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class FreeMarkerWorker {
    
    public static final String module = FreeMarkerWorker.class.getName();
    

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

   /**
    * Gets BeanModel from FreeMarker context and returns the object that it wraps.
    * @param varName the name of the variable in the FreeMarker context.
    * @param env the FreeMarker Environment
    */
    public static Object getWrappedObject(String varName, Environment env) {
            BeanModel bean = null;
            try {
                bean = (BeanModel) env.getVariable(varName);
            } catch(TemplateModelException e) {
                Debug.logInfo(e.getMessage(), module);
            }
            Object obj = null;
            if (bean != null) {                    
                    obj = bean.getWrappedObject();
            } 
            return obj;
    }

   /**
    * Tries to find the mime type of the associated content and parent content.
    *
    * @param delegator 
    * @param view SubContentDataResourceView
    * @param parentContent Content entity
    * @param contentId part of primary key of view. To be used if view is null.
    * @param dataResourceId part of primary key of view. To be used if view is null.
    * @param parentContentId primary key of parent content. To be used if parentContent is null;
    */
    public static String determineMimeType(GenericDelegator delegator, GenericValue view,
                             GenericValue parentContent, String contentId, String dataResourceId,
                             String parentContentId) throws GenericEntityException {
        String mimeTypeId = null;

        if (view != null) {
            mimeTypeId = (String)view.get("mimeTypeId");
            if (UtilValidate.isEmpty(mimeTypeId)) {
                mimeTypeId = (String)view.get("drMimeTypeId");
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (UtilValidate.isNotEmpty(contentId) && UtilValidate.isNotEmpty(dataResourceId)) {
                view = delegator.findByPrimaryKey("SubContentDataResourceView",
                        UtilMisc.toMap("contentId", contentId, "drDataResourceId", dataResourceId));
                if (view != null) {
                    mimeTypeId = (String)view.get("mimeTypeId");
                    if (UtilValidate.isEmpty(mimeTypeId)) {
                        mimeTypeId = (String)view.get("drMimeTypeId");
                    }
                }
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (parentContent != null) {
                mimeTypeId = (String)parentContent.get("mimeTypeId");
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (UtilValidate.isNotEmpty(parentContentId)) {
                parentContent = delegator.findByPrimaryKey("Content",
                        UtilMisc.toMap("contentId", contentId));
                if (parentContent != null) {
                    mimeTypeId = (String)parentContent.get("mimeTypeId");
                }
            }
        }

        return mimeTypeId;
    }
}
