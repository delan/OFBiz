/*
 * $Id: EditRenderSubContentCacheTransform.java,v 1.1 2004/01/07 19:30:11 byersa Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.webapp.ftl;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
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
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;

import freemarker.template.Environment;
import freemarker.template.TemplateTransformModel;

/**
 * EditRenderSubContentCacheTransform - Freemarker Transform for URLs (links)
 * 
 * This is an interactive FreeMarker tranform that allows the user to modify the contents that are placed within it.
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.1 $
 * @since 3.0
 */
public class EditRenderSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = EditRenderSubContentCacheTransform.class.getName();
    public static final String [] saveKeyNames = {"contentId", "subContentId", "mimeTypeId", "subContentDataResourceView", "wrapTemplateId", "templateContentId", "pickWhen", "followWhen", "returnAfterPickWhen", "returnBeforePickWhen", "globalNodeTrail"};
    public static final String [] removeKeyNames = {"templateContentId", "subDataResourceTypeId", "mapKey", "wrappedFTL"};
    
    /**
     * A wrapper for the FreeMarkerWorker version.
     */
    public static Object getWrappedObject(String varName, Environment env) {
        return FreeMarkerWorker.getWrappedObject(varName, env);
    }

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
        if (Debug.verboseOn()) Debug.logVerbose("sz:" + sz ,module);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(E)before get", templateCtx, 0),module);
        if (sz == 0)
            throw new RuntimeException("No current subContent found.");
        templateCtx.put("indent", new Integer(sz));
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logList("(E)passedGlobalNodeTrail", passedGlobalNodeTrail, 0),module);
        final GenericValue view = (GenericValue)((Map)passedGlobalNodeTrail.get(sz - 1)).get("value");

        String dataResourceId = null;
        try {
            dataResourceId = (String) view.get("drDataResourceId");
        } catch (Exception e) {
            dataResourceId = (String) view.get("dataResourceId");
        }
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
                buf.append(cbuf, off, len);
                if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, buf:" + buf.toString(), module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {
                String wrappedFTL = buf.toString();
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)before remove", templateCtx, 0),module);
                    FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after remove", templateCtx, 0),module);
                    FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after reload", templateCtx, 0),module);
                if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, wrappedFTL:" + wrappedFTL, module);
                String editTemplate = (String)templateCtx.get("editTemplate");
                if (editTemplate != null && editTemplate.equalsIgnoreCase("true")) {
                    String wrapTemplateId = (String)templateCtx.get("wrapTemplateId");
                    if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                        templateCtx.put("wrappedFTL", wrappedFTL);
                        
                        Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                        
                        /*
                        templateRoot.put("wrapDataResourceId", dataResourceId);
                        templateRoot.put("wrapDataResourceTypeId", subDataResourceTypeId);
                        templateRoot.put("wrapContentIdTo", contentId);
                        templateRoot.put("wrapSubContentId", subContentIdSub);
                        templateRoot.put("wrapMimeTypeId", mimeTypeId);
                        templateRoot.put("wrapMapKey", mapKey);
                        */
                        templateRoot.put("context", templateCtx);
                        
                        String mimeTypeId = (String)templateCtx.get("mimeTypeId");
                        Locale locale = null;
                        try {
                            ContentWorker.renderContentAsTextCache(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                        } catch (IOException e) {
                            Debug.logError(e, "Error rendering content" + e.getMessage(), module);
                            throw new IOException("Error rendering content" + e.toString());
                        } catch (GeneralException e2) {
                            Debug.logError(e2, "Error rendering content" + e2.getMessage(), module);
                            throw new IOException("Error rendering content" + e2.toString());
                        }
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, after renderContentAsText", module);
                        
                    }
                } else {
                    out.write(wrappedFTL);
                }
            }
        };
    }
}
