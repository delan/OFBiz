/*
 * $Id: FreeMarkerWorker.java,v 1.5 2003/12/23 13:50:40 jonesde Exp $
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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;


/**
 * FreemarkerViewHandler - Freemarker Template Engine Util
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.5 $
 * @since      3.0
 */
public class FreeMarkerWorker {
    
    public static final String module = FreeMarkerWorker.class.getName();
    
    public static OfbizUrlTransform ofbizUrl = new OfbizUrlTransform();
    public static OfbizContentTransform ofbizContentUrl = new OfbizContentTransform();
    public static OfbizCurrencyTransform ofbizCurrency = new OfbizCurrencyTransform();
    public static SetRequestAttributeMethod setRequestAttribute = new SetRequestAttributeMethod();
    public static EditRenderSubContentTransform  editRenderSubContent = new EditRenderSubContentTransform();
    public static RenderSubContentTransform  renderSubContent = new RenderSubContentTransform();
    public static RenderWrappedTextTransform  renderWrappedText = new RenderWrappedTextTransform();
    public static LoopSubContentTransform  loopSubContent = new LoopSubContentTransform();
    
    public static void addAllOfbizTransforms(Map context) {
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        context.put("Static", staticModels);

        context.put("ofbizUrl", ofbizUrl);
        context.put("ofbizContentUrl", ofbizContentUrl);
        context.put("ofbizCurrency", ofbizCurrency);
        context.put("setRequestAttribute", setRequestAttribute);
        context.put("editRenderSubContent", editRenderSubContent);
        context.put("renderSubContent", renderSubContent);
        context.put("loopSubContent", loopSubContent);
        context.put("renderWrappedText", renderWrappedText);
    }
    
    public static Configuration makeDefaultOfbizConfig() throws TemplateException {
        Configuration config = Configuration.getDefaultConfiguration();            
        config.setObjectWrapper(BeansWrapper.getDefaultInstance());
        config.setSetting("datetime_format", "yyyy-MM-dd HH:mm:ss.SSS");
        return config;
    }
    
    public static String getArg(Map args, String key, Environment env) {
        Map templateContext = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        return getArg(args, key, templateContext);
    }

    public static String getArg(Map args, String key, Map templateContext) {
        //SimpleScalar s = null;
        Object o = null;
        String returnVal = null;
        o = args.get(key);
        returnVal = (String) unwrap(o);
        if (returnVal == null) {
            try {
                if (templateContext != null) {
                    returnVal = (String) templateContext.get(key);
                }
            } catch (ClassCastException e2) {
                //return null;
            }
        }
        return returnVal;
    }


   /**
    * Gets BeanModel from FreeMarker context and returns the object that it wraps.
    * @param varName the name of the variable in the FreeMarker context.
    * @param env the FreeMarker Environment
    */
    public static Object getWrappedObject(String varName, Environment env) {
        Object obj = null;
        try {
            obj = env.getVariable(varName);
            if (obj != null) {
                if (obj instanceof BeanModel) {
                    BeanModel bean = (BeanModel) obj;
                    obj = bean.getWrappedObject();
                }
            }
        } catch (TemplateModelException e) {
            Debug.logInfo(e.getMessage(), module);
        }
        return obj;
    }

   /**
    * Gets BeanModel from FreeMarker context and returns the object that it wraps.
    * @param varName the name of the variable in the FreeMarker context.
    * @param env the FreeMarker Environment
    */
    public static BeanModel getBeanModel(String varName, Environment env) {
        BeanModel bean = null;
        try {
            bean = (BeanModel) env.getVariable(varName);
        } catch (TemplateModelException e) {
            Debug.logInfo(e.getMessage(), module);
        }
        return bean;
    }

   /*
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
       Debug.logInfo("in determineMimeType,  mimeType:" + mimeTypeId,"");
            String drMimeTypeId = (String)view.get("drMimeTypeId");
       Debug.logInfo("in determineMimeType,  drMimeType:" + drMimeTypeId,"");
            if (UtilValidate.isNotEmpty(drMimeTypeId)) {
                mimeTypeId = drMimeTypeId;
            }
       Debug.logInfo("in determineMimeType, view: " + view.get("contentId") + " / " + view.get("drDataResourceId")  + " mimeType:" + view.get("drMimeTypeId"),"");
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (UtilValidate.isNotEmpty(contentId) && UtilValidate.isNotEmpty(dataResourceId)) {
                view = delegator.findByPrimaryKey("SubContentDataResourceView",
                        UtilMisc.toMap("contentId", contentId, "drDataResourceId", dataResourceId));
                if (view != null) {
                    mimeTypeId = (String)view.get("mimeTypeId");
               Debug.logInfo("in determineMimeType,  mimeType:" + mimeTypeId,"");
                    String drMimeTypeId = (String)view.get("drMimeTypeId");
               Debug.logInfo("in determineMimeType,  drMimeType:" + drMimeTypeId,"");
                    if (UtilValidate.isNotEmpty(drMimeTypeId)) {
                        mimeTypeId = drMimeTypeId;
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

    public static Object get(SimpleHash args, String key) {
        Object returnObj = null;
        Object o = null;
        try {
            o = args.get(key);
            //Debug.logInfo("in FM.get, o:" + o + " key:" + key, module);
            if (o != null) {
                 //Debug.logInfo("in FM.get, o class:" + o.getClass(), module);
            }
        } catch(TemplateModelException e) {
            Debug.logInfo(e.getMessage(), module);
            return returnObj;
        }

        returnObj = unwrap(o);

        if (returnObj == null) {
            Object ctxObj = null;
            try {
                ctxObj = args.get("context");
                //Debug.logInfo("in FM.get, ctxObj:" + ctxObj, module);
            } catch(TemplateModelException e) {
                Debug.logInfo(e.getMessage(), module);
                return returnObj;
            }
            Map ctx = null;
            if (ctxObj instanceof BeanModel) {
                ctx = (Map)((BeanModel)ctxObj).getWrappedObject();
                //Debug.logInfo("in FM.get, ctx:" + ctx, module);
            returnObj = ctx.get(key);
            //Debug.logInfo("in FM.get..., returnObj:" + returnObj + " key:" + key, module);
            }
            /*
            try {
                Map templateContext = (Map)FreeMarkerWorker.getWrappedObject("context", env);
                if (templateContext != null) {
                    returnObj = (String)templateContext.get(key);
                }
            } catch(ClassCastException e2) {
                //return null;
            }
            */
        }
        return returnObj;
    }

    public static Object unwrap(Object o) {
        Object returnObj = null;

        if (o instanceof SimpleScalar) {
            returnObj = o.toString();
            //Debug.logInfo("in FM.get, SimpleScalar:" + returnObj, module);
        } else if (o instanceof BeanModel) {
            returnObj = ((BeanModel)o).getWrappedObject();
            //Debug.logInfo("in FM.get, BeanModel:" + returnObj, module);
        }
    
        return returnObj;
    }

    public static void checkForLoop(String path, Map ctx) throws IOException {
        List templateList = (List)ctx.get("templateList");
            //Debug.logInfo("in checkForLoop, templateList:" +templateList, "");
            //Debug.logInfo("in checkForLoop, templatePath:" +path, "");
        if (templateList == null) {
            templateList = new ArrayList();
        } else {
            if (templateList.contains(path)) {
                Debug.logInfo("in checkForLoop, " +path + " has been visited.", "");
                throw new IOException(path + " has already been visited.");
            }
        }
        templateList.add(path);
        ctx.put("templateList", templateList);
        return;
    }

    public static Map createEnvironmentMap(Environment env) {
        Map templateRoot = new HashMap();
        Set varNames = null;
        try {
            varNames = env.getKnownVariableNames();
        } catch (TemplateModelException e1) {
            Debug.logError(e1, "Error getting FreeMarker variable names, will not put pass current context on to sub-content", module);
        }
        if (varNames != null) {
            Iterator varNameIter = varNames.iterator();
            while (varNameIter.hasNext()) {
                String varName = (String) varNameIter.next();
                templateRoot.put(varName, FreeMarkerWorker.getWrappedObject(varName, env));
            }
        }
        return templateRoot;
    }
    
    public static void renderTemplate(String templateIdString, String template, Map context, Writer outWriter) throws TemplateException, IOException {
        Reader templateReader = new StringReader(template);
        renderTemplate(templateIdString, templateReader, context, outWriter);
    }
    
    public static void renderTemplate(String templateIdString, Reader templateReader, Map context, Writer outWriter) throws TemplateException, IOException {
        if (context == null) {
            context = new HashMap();
        }
        
        Configuration config = makeDefaultOfbizConfig();            
        Template template = new Template(templateIdString, templateReader, config);            
        
        // add the OFBiz transforms/methods
        addAllOfbizTransforms(context);
        
        // process the template with the given data and write
        // the email body to the String buffer
        template.process(context, outWriter);
    }
}
