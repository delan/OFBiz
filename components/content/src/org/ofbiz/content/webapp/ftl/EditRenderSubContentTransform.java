/*
 * $Id: EditRenderSubContentTransform.java,v 1.8 2003/12/30 05:35:50 byersa Exp $
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

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import freemarker.template.Environment;
import freemarker.template.TemplateTransformModel;

/**
 * EditRenderSubContentTransform - Freemarker Transform for URLs (links)
 * 
 * This is an interactive FreeMarker tranform that allows the user to modify the contents that are placed within it.
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.8 $
 * @since 3.0
 */
public class EditRenderSubContentTransform implements TemplateTransformModel {

    public static final String module = EditRenderSubContentTransform.class.getName();
    
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
        Map ctx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        final String editTemplate = getArg(args, "editTemplate", ctx);
        final String wrapTemplateId = getArg(args, "wrapTemplateId", ctx);
        final String mapKey = getArg(args, "mapKey", ctx);
        final String templateContentId = getArg(args, "templateContentId", ctx);
        final String subContentId = getArg(args, "subContentId", ctx);
        String subDataResourceTypeIdTemp = getArg(args, "subDataResourceTypeId", ctx);
        final String contentId = getArg(args, "contentId", ctx);

        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, editTemplate:" + editTemplate, module);
        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, wrapTemplateId:" + wrapTemplateId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, mapKey:" + mapKey, module);
        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, templateContentId:" + templateContentId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, subContentId:" + subContentId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, contentId:" + contentId, module);
        
        final Locale locale = (Locale) FreeMarkerWorker.getWrappedObject("locale", env);
        String mimeTypeIdTemp = getArg(args, "mimeTypeId", ctx);
        final String rootDir = getArg(args, "rootDir", ctx);
        final String webSiteId = getArg(args, "webSiteId", ctx);
        final String https = getArg(args, "https", ctx);
        //final LocalDispatcher dispatcher = (LocalDispatcher) FreeMarkerWorker.getWrappedObject("dispatcher", env);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);
        GenericValue subContentDataResourceViewTemp = (GenericValue) FreeMarkerWorker.getWrappedObject("subContentDataResourceView", env);
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
                subContentDataResourceViewTemp = ContentWorker.getSubContent(delegator, contentId, mapKey, subContentId, userLogin, assocTypes, fromDate);
            } catch (IOException e) {
                Debug.logError(e, "Error getting sub-content", module);
                throw new RuntimeException(e.getMessage());
            }
        }

        final GenericValue subContentDataResourceView = subContentDataResourceViewTemp;

        String dataResourceIdTemp = null;
        String subContentIdSubTemp = null;
        if (subContentDataResourceView != null && subContentDataResourceView.get("contentId") != null) {

            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, subContentDataResourceView contentId/drDataResourceId:"
                    + subContentDataResourceView.get("contentId") + " / "
                    + subContentDataResourceView.get("drDataResourceId"), module);

            dataResourceIdTemp = (String) subContentDataResourceView.get("drDataResourceId");
            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent(0), dataResourceIdTemp ." + dataResourceIdTemp, module);
            subContentIdSubTemp = (String) subContentDataResourceView.get("contentId");
            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent(0), subContentIdSubTemp ." + subContentIdSubTemp, module);
            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent(0), mimeTypeIdTemp." + mimeTypeIdTemp, module);
            if (UtilValidate.isEmpty(subDataResourceTypeIdTemp)) {
                subDataResourceTypeIdTemp = (String) subContentDataResourceView.get("drDataResourceTypeId");
            }
            if (UtilValidate.isEmpty(mimeTypeIdTemp)) {
                mimeTypeIdTemp = (String) subContentDataResourceView.get("mimeTypeId");
                if (UtilValidate.isEmpty(mimeTypeIdTemp) && UtilValidate.isNotEmpty(contentId)) { // will need these below
                    try {
                        parentContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
                        if (parentContent != null) {
                            mimeTypeIdTemp = (String) parentContent.get("mimeTypeId");
                            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, parentContentId: " + parentContent.get("contentId"), module);
                        }
                    } catch (GenericEntityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }

            }
            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent(2), mimeTypeIdTemp." + mimeTypeIdTemp, module);
            if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, subContentId/Sub." + subContentIdSubTemp, module);
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

        final String dataResourceId = dataResourceIdTemp;
        final String subContentIdSub = subContentIdSubTemp;
        //final GenericValue finalSubContentView = subContentDataResourceView;
        //final GenericValue content = parentContent;
        final Map templateContext = ctx;
        //if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, templateContext:"+templateContext,module);
        final String mimeTypeId = mimeTypeIdTemp;
        final String subDataResourceTypeId = subDataResourceTypeIdTemp;

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
                if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, wrappedFTL:" + wrappedFTL, module);
                if (editTemplate != null && editTemplate.equalsIgnoreCase("true")) {
                    if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                        templateContext.put("wrappedFTL", wrappedFTL);
                        //ServletContext servletContext = (ServletContext)request.getSession().getServletContext();
                        //String rootDir = servletContext.getRealPath("/");
                        if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, rootDir:" + rootDir, module);
                        templateContext.put("webSiteId", webSiteId);
                        templateContext.put("https", https);
                        templateContext.put("rootDir", rootDir);
                        
                        Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                        
                        templateRoot.put("wrapDataResourceId", dataResourceId);
                        templateRoot.put("wrapDataResourceTypeId", subDataResourceTypeId);
                        templateRoot.put("wrapContentIdTo", contentId);
                        templateRoot.put("wrapSubContentId", subContentIdSub);
                        templateRoot.put("wrapMimeTypeId", mimeTypeId);
                        templateRoot.put("wrapMapKey", mapKey);
                        templateRoot.put("context", templateContext);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, wrapDataResourceId:" + dataResourceId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, wrapDataResourceTypeId:" + subDataResourceTypeId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, wrapContentIdTo:" + contentId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, wrapSubContentId:" + subContentIdSub, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, wrapMimeTypeId:" + mimeTypeId, module);
                        //if (Debug.verboseOn()) Debug.logVerbose("in ERSC, wrapMapKey:" + mapKey,module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, calling renderContentAsText, wrapTemplateId:" + wrapTemplateId, module);
                        
                        try {
                            ContentWorker.renderContentAsText(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                        } catch (IOException e) {
                            Debug.logError(e, "Error rendering content" + e.getMessage(), module);
                            throw new IOException("Error rendering content" + e.toString());
                        } catch (GeneralException e2) {
                            Debug.logError(e2, "Error rendering content" + e2.getMessage(), module);
                            throw new IOException("Error rendering content" + e2.toString());
                        }
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, after renderContentAsText", module);
                        
                        Map ctx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, contentId:" + ctx.get("contentId"), module);
                        templateContext.put("contentId", contentId);
                        templateContext.put("locale", locale);
                        templateContext.put("mapKey", null);
                        templateContext.put("subContentId", null);
                        templateContext.put("templateContentId", null);
                        templateContext.put("subDataResourceTypeId", null);
                        templateContext.put("mimeTypeId", null);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, after.", module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, mapKey:" + mapKey, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, subContentId:" + subContentId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, subDataResourceTypeId:" + subDataResourceTypeId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, contentId:" + contentId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, mimeTypeId:" + mimeTypeId, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, locale:" + locale, module);
                        if (Debug.verboseOn()) Debug.logVerbose("in ERSC, contentId2." + ctx.get("contentId"), module);
                    }
                } else {
                    out.write(wrappedFTL);
                }
            }
        };
    }
}
