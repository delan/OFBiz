/*
 * $Id: RenderSubContentCacheTransform.java,v 1.7 2004/04/01 21:55:46 byersa Exp $
 * 
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *  
 */
package org.ofbiz.content.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.minilang.MiniLangException;


import freemarker.template.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TemplateModelException;
//import com.clarkware.profiler.Profiler;
/**
 * RenderSubContentCacheTransform - Freemarker Transform for Content rendering
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.7 $
 * @since 3.0
 * 
 * This transform cannot be called recursively (at this time).
 */
public class RenderSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = RenderSubContentCacheTransform.class.getName();
    public static final String [] upSaveKeyNames = {"globalNodeTrail"};
    public static final String [] saveKeyNames = {"contentId", "subContentId", "subDataResourceTypeId", "mimeTypeId", "whenMap", "locale",  "wrapTemplateId", "encloseWrapText", "nullThruDatesOnly", "globalNodeTrail"};
    
    /**
     * Does a conditional search to return a value for a parameter with the passed name. Looks first to see if it was passed as an argument to the transform.
     * Secondly, it looks to see if it is passed as a parameter in the template context object.
     * <p>
     * Note that this is different from the getArg method of EditRenderDataResourceTransform, which checks the request object instead of the template context
     * object.
     */
    public static String getArg(Map args, String key, Environment env) {
        return FreeMarkerWorker.getArg(args, key, env);
    }

    public static String getArg(Map args, String key, Map ctx) {
        return FreeMarkerWorker.getArg(args, key, ctx);
    }

    public Writer getWriter(final Writer out, Map args) {
        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        final Map templateCtx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        final HttpServletRequest request = (HttpServletRequest) FreeMarkerWorker.getWrappedObject("request", env);
        FreeMarkerWorker.getSiteParameters(request, templateCtx);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(R)before save", templateCtx, 0),module);
        final Map savedValuesUp = new HashMap();
        FreeMarkerWorker.saveContextValues(templateCtx, upSaveKeyNames, savedValuesUp);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(R)after overrride", templateCtx, 0),module);
        final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);
        List trail = (List)templateCtx.get("globalNodeTrail");
        //if (Debug.infoOn()) Debug.logInfo("in Render(0), globalNodeTrail ." + trail , module);
        String contentAssocPredicateId = (String)templateCtx.get("contentAssocPredicateId");
        String strNullThruDatesOnly = (String)templateCtx.get("nullThruDatesOnly");
        Boolean nullThruDatesOnly = (strNullThruDatesOnly != null && strNullThruDatesOnly.equalsIgnoreCase("true")) ? new Boolean(true) :new Boolean(false);
        GenericValue val = null;
        try {
            val = FreeMarkerWorker.getCurrentContent(delegator, trail, userLogin, templateCtx, nullThruDatesOnly, contentAssocPredicateId);
        } catch(GeneralException e) {
            throw new RuntimeException("Error getting current content. " + e.toString());
        }
        final GenericValue view = val;
        if (Debug.verboseOn()) Debug.logVerbose("renderEditSubContentCache, view:" + view, "");

        String dataResourceId = null;
        try {
            dataResourceId = (String) view.get("drDataResourceId");
        } catch (Exception e) {
            dataResourceId = (String) view.get("dataResourceId");
        }
        if (Debug.verboseOn()) Debug.logVerbose("in renderSubContentCache(0), dataResourceId ." + dataResourceId, module);
        String subContentIdSub = (String) view.get("contentId");
        if (Debug.verboseOn()) Debug.logVerbose("in renderSubContentCache(0), subContentIdSub ." + subContentIdSub, module);
        // This order is taken so that the dataResourceType can be overridden in the transform arguments.
        String subDataResourceTypeId = (String)templateCtx.get("subDataResourceTypeId");
        if (UtilValidate.isEmpty(subDataResourceTypeId)) {
            try {
                subDataResourceTypeId = (String) view.get("drDataResourceTypeId");
            } catch (Exception e) {
                // view may be "Content"
            }
            // TODO: If this value is still empty then it is probably necessary to get a value from
            // the parent context. But it will already have one and it is the same context that is
            // being passed.
        }
        String mimeTypeId = FreeMarkerWorker.getMimeTypeId(delegator, view, templateCtx);
        templateCtx.put("drDataResourceId", dataResourceId);
        templateCtx.put("mimeTypeId", mimeTypeId);
        templateCtx.put("dataResourceId", dataResourceId);
        templateCtx.put("subContentIdSub", subContentIdSub);
        templateCtx.put("subDataResourceTypeId", subDataResourceTypeId);

        //final Map savedValues = new HashMap();
        //FreeMarkerWorker.saveContextValues(templateCtx, saveKeyNames, savedValues);

        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {
                 //if (Debug.infoOn()) Debug.logInfo("in Render(1), globalNodeTrail ." + templateCtx.get("globalNodeTrail") , module);
                try {
                    if (Debug.verboseOn()) Debug.logVerbose("in RenderSubContent, close:", module);
                    renderSubContent();
                FreeMarkerWorker.reloadValues(templateCtx, savedValuesUp);
                 //if (Debug.infoOn()) Debug.logInfo("in Render(2), globalNodeTrail ." + templateCtx.get("globalNodeTrail") , module);
                } catch (IOException e) {
                    throw new IOException(e.getMessage());
                }
            }

            public void renderSubContent() throws IOException {
                //TemplateHashModel dataRoot = env.getDataModel();
                Timestamp fromDate = UtilDateTime.nowTimestamp();
                List passedGlobalNodeTrail = (List)templateCtx.get("globalNodeTrail");
                 //if (Debug.infoOn()) Debug.logInfo("in Render(3), passedGlobalNodeTrail ." + passedGlobalNodeTrail , module);
                GenericValue thisView = null;
                if (passedGlobalNodeTrail.size() > 0) {
                    thisView = (GenericValue)((Map)passedGlobalNodeTrail.get(passedGlobalNodeTrail.size() - 1)).get("value");
                }
                if (thisView != null && Debug.verboseOn()) Debug.logVerbose("in RenderSubContent, thisView:" + thisView.get("contentId"), module);
                ServletContext servletContext = request.getSession().getServletContext();
                String rootDir = servletContext.getRealPath("/");
                String webSiteId = (String) servletContext.getAttribute("webSiteId");
                String https = (String) servletContext.getAttribute("https");
                //if (Debug.infoOn()) Debug.logInfo("in RenderSubContent, rootDir:" + rootDir, module);
                templateCtx.put("webSiteId", webSiteId);
                templateCtx.put("https", https);
                templateCtx.put("rootDir", rootDir);


                    //Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                    Map templateRootTemplate = (Map)templateCtx.get("templateRootTemplate");
                    Map templateRoot = null;
                    if (templateRootTemplate == null) {
                        Map templateRootTmp = FreeMarkerWorker.createEnvironmentMap(env);
                        templateRoot = new HashMap(templateRootTmp);
                        //templateCtx.put("templateRootTemplate", templateRootTmp);
                    } else {
                        templateRoot = new HashMap(templateRootTemplate);
                    }
                templateRoot.put("context", templateCtx);
        if (Debug.verboseOn()) {
            Debug.logVerbose("in RenderSubContent, templateCtx.keySet()" + templateCtx.keySet(), "");
            Set kySet = templateCtx.keySet();
            Iterator it = kySet.iterator();
            while (it.hasNext()) {
                Object ky = it.next();
            Debug.logVerbose("in RednerSubContent, ky:" + ky, "");
                Object val = templateCtx.get(ky);
                    Debug.logVerbose("in RednerSubContent, val:" + val, "");
            }
        }

                String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                Locale locale = (Locale) templateCtx.get("locale");
                if (locale == null)
                    locale = Locale.getDefault();
                try {
                    ContentWorker.renderContentAsTextCache(delegator, null, out, templateRoot, thisView, locale, mimeTypeId);
                } catch (GeneralException e) {
                    Debug.logError(e, "Error rendering content", module);
                    throw new IOException("Error rendering thisView:" + thisView + " msg:" + e.toString());
                }
                //if (Debug.infoOn()) Debug.logInfo("in Render(4), globalNodeTrail ." + templateCtx.get("globalNodeTrail") , module);
                return;
            }
        };
        
    }

}
