/*
 * $Id: RenderSubContentCacheTransform.java,v 1.1 2004/01/07 19:30:11 byersa Exp $
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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

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
/**
 * RenderSubContentCacheTransform - Freemarker Transform for Content rendering
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.1 $
 * @since 3.0
 * 
 * This transform cannot be called recursively (at this time).
 */
public class RenderSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = RenderSubContentCacheTransform.class.getName();
    public static final String [] saveKeyNames = {"contentId", "subContentId", "mimeTypeId", "subContentDataResourceView", "wrapTemplateId", "templateContentId", "pickWhen", "followWhen", "returnAfterPickWhen", "returnBeforePickWhen", "globalNodeTrail"};
    public static final String [] removeKeyNames = {"templateContentId", "subDataResourceTypeId", "mapKey", "wrappedFTL"};
    
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
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)before save", templateCtx, 0),module);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after overrride", templateCtx, 0),module);
        final Map savedValues = FreeMarkerWorker.saveValues(templateCtx, saveKeyNames);
        final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);
        String contentId = (String)templateCtx.get("contentId");
        String subContentId = (String)templateCtx.get("subContentId");
        String mapKey = (String)templateCtx.get("mapKey");
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        List assocTypes = null;
        List trail = (List)templateCtx.get("globalNodeTrail");
        List passedGlobalNodeTrail = null;
        if (trail != null) 
            passedGlobalNodeTrail = new ArrayList(trail);
        else
            passedGlobalNodeTrail = new ArrayList();
        if (UtilValidate.isNotEmpty(subContentId) || UtilValidate.isNotEmpty(contentId) ) {
            
                try {
                    GenericValue view = ContentWorker.getSubContentCache(delegator, contentId, mapKey, subContentId, userLogin, assocTypes, fromDate);
                    passedGlobalNodeTrail.add(FreeMarkerWorker.makeNode(view));
                } catch (GenericEntityException e) {
                    throw new RuntimeException(e.getMessage());
                } catch (MiniLangException e2) {
                    throw new RuntimeException(e2.getMessage());
                } catch (GeneralException e3) {
                    throw new RuntimeException(e3.getMessage());
                }
        }
        templateCtx.put("globalNodeTrail", passedGlobalNodeTrail);
        templateCtx.put("contentId", null);
        templateCtx.put("subContentId", null);
        int sz = passedGlobalNodeTrail.size();
        if (sz == 0)
            throw new RuntimeException("No current subContent found.");
        templateCtx.put("indent", new Integer(sz));
        final GenericValue view = (GenericValue)((Map)passedGlobalNodeTrail.get(sz - 1)).get("value");

        String dataResourceId = null;
        try {
            dataResourceId = (String) view.get("drDataResourceId");
        } catch (Exception e) {
            dataResourceId = (String) view.get("dataResourceId");
        }
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(0), dataResourceId ." + dataResourceId, module);
        String subContentIdSub = (String) view.get("contentId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(0), subContentIdSub ." + subContentIdSub, module);
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
        // This order is taken so that the mimeType can be overridden in the transform arguments.
        String mimeTypeId = (String)templateCtx.get("mimeTypeId");
        if (UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) view.get("mimeTypeId");
            String parentContentId = (String)templateCtx.get("contentId");
            if (UtilValidate.isEmpty(mimeTypeId) && UtilValidate.isNotEmpty(parentContentId)) { // will need these below
                try {
                    GenericValue parentContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", parentContentId));
                    if (parentContent != null) {
                        mimeTypeId = (String) parentContent.get("mimeTypeId");
                        templateCtx.put("parentContent", parentContent);
                        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, parentContentId: " + parentContent.get("contentId"), module);
                    }
                } catch (GenericEntityException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

        }
        templateCtx.put("drDataResourceId", dataResourceId);
        templateCtx.put("mimeTypeId", mimeTypeId);
        templateCtx.put("dataResourceId", dataResourceId);
        templateCtx.put("subContentIdSub", subContentIdSub);
        templateCtx.put("subDataResourceTypeId", subDataResourceTypeId);


        return new Writer(out) {

            public void write(char cbuf[], int off, int len) {
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {
                try {
                    if (Debug.verboseOn()) Debug.logVerbose("in RenderSubContent, close:", module);
                    renderSubContent();
                } catch (IOException e) {
                    throw new IOException(e.getMessage());
                }
            }

            public void renderSubContent() throws IOException {
                //TemplateHashModel dataRoot = env.getDataModel();
                Timestamp fromDate = UtilDateTime.nowTimestamp();
                List passedGlobalNodeTrail = (List)templateCtx.get("globalNodeTrail");
                GenericValue view = null;
                if (passedGlobalNodeTrail.size() > 0) {
                    view = (GenericValue)((Map)passedGlobalNodeTrail.get(passedGlobalNodeTrail.size() - 1)).get("value");
                }

                Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                templateRoot.put("context", templateCtx);
                String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                Locale locale = (Locale) templateCtx.get("locale");
                if (locale == null)
                    locale = Locale.getDefault();
                try {
                        ContentWorker.renderContentAsTextCache(delegator, null, out, templateRoot, view, locale, mimeTypeId);
                } catch (GeneralException e) {
                    Debug.logError(e, "Error rendering content", module);
                    throw new IOException("Error rendering view:" + view + " msg:" + e.toString());
                }

                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)before remove", templateCtx, 0),module);
                FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after remove", templateCtx, 0),module);
                FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after reload", templateCtx, 0),module);
                
                return;
            }
        };
    }

}
