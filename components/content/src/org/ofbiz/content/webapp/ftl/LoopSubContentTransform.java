/*
 * $Id: LoopSubContentTransform.java,v 1.2 2003/12/21 11:53:05 jonesde Exp $
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentServicesComplex;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import freemarker.template.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;

/**
 * LoopSubContentTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.2 $
 * @since 3.0
 */
public class LoopSubContentTransform implements TemplateTransformModel {

    public static final String module = LoopSubContentTransform.class.getName();

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

    public static boolean prepCtx(GenericDelegator delegator, Map ctx, String contentId, String mimeTypeIdTemp, String subDataResourceTypeIdTemp) {

        //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, ctx :" + ctx, module);
        List lst = (List) ctx.get("entityList");
        //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, lst :" + lst, module);
        Integer idx = (Integer) ctx.get("entityIndex");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, idx :" + idx, module);
        int i = idx.intValue();
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, i :" + i, module);
        if (i >= lst.size()) {
            return false;
        }
        GenericValue subContentDataResourceView = (GenericValue) lst.get(i);
        ctx.put("subContentDataResourceView", subContentDataResourceView);
        GenericValue electronicText = null;
        try {
            electronicText = subContentDataResourceView.getRelatedOne("ElectronicText");
        } catch (GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        ctx.put("textData", electronicText.get("textData"));
        ctx.put("entityIndex", new Integer(i + 1));
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, subContentDataResourceView contentId/drDataResourceId:" + subContentDataResourceView.get("contentId")  + " / " + subContentDataResourceView.get("drDataResourceId"), module);

        String dataResourceIdTemp = (String) subContentDataResourceView.get("drDataResourceId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(0), dataResourceIdTemp ." + dataResourceIdTemp, module);
        String subContentIdSubTemp = (String) subContentDataResourceView.get("contentId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(0), subContentIdSubTemp ." + subContentIdSubTemp, module);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(0), mimeTypeIdTemp." + mimeTypeIdTemp, module);
        if (UtilValidate.isEmpty(subDataResourceTypeIdTemp)) {
            subDataResourceTypeIdTemp = (String) subContentDataResourceView.get("drDataResourceTypeId");
        }
        if (UtilValidate.isEmpty(mimeTypeIdTemp)) {
            mimeTypeIdTemp = (String) subContentDataResourceView.get("mimeTypeId");
            if (UtilValidate.isEmpty(mimeTypeIdTemp) && UtilValidate.isNotEmpty(contentId)) { // will need these below
                try {
                    GenericValue parentContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
                    if (parentContent != null) {
                        mimeTypeIdTemp = (String) parentContent.get("mimeTypeId");
                        ctx.put("parentContent", parentContent);
                        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, parentContentId: " + parentContent.get("contentId"), module);
                    }
                } catch (GenericEntityException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

        }
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(2), mimeTypeIdTemp." + mimeTypeIdTemp, module);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, subContentId/Sub." + subContentIdSubTemp, module);
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
        Map ctx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        final String editTemplate = getArg(args, "editTemplate", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, editTemplate:" + editTemplate, module);
        final String wrapTemplateId = getArg(args, "wrapTemplateId", ctx);
        final String mapKey = getArg(args, "mapKey", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, mapKey:" + mapKey, module);
        final String templateContentId = getArg(args, "templateContentId", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, templateContentId:" + templateContentId, module);
        final String subDataResourceTypeId = getArg(args, "subDataResourceTypeId", ctx);
        final String contentId = getArg(args, "contentId", ctx);
        final String rootDir = getArg(args, "rootDir", ctx);
        final String webSiteId = getArg(args, "webSiteId", ctx);
        final String https = getArg(args, "https", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, contentId:" + contentId, module);
        final String viewSize = getArg(args, "viewSize", ctx);
        final String viewIndex = getArg(args, "viewIndex", ctx);
        final String listSize = getArg(args, "listSize", ctx);
        final String highIndex = getArg(args, "highIndex", ctx);
        final String lowIndex = getArg(args, "lowIndex", ctx);
        final String queryString = getArg(args, "queryString", ctx);
        final Locale locale = (Locale) FreeMarkerWorker.getWrappedObject("locale", env);
        final String mimeTypeId = getArg(args, "mimeTypeId", ctx);
        //final LocalDispatcher dispatcher = (LocalDispatcher) FreeMarkerWorker.getWrappedObject("dispatcher", env);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        //final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);

        ctx.put("mapKey", mapKey);
        ctx.put("contentId", contentId);
        ctx.put("templateContentId", templateContentId);
        ctx.put("locale", locale);

        //ctx.put("userLogin", userLogin);
        List assocTypes = UtilMisc.toList("SUB_CONTENT");
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        //GenericValue subContentDataResourceView = null;
        Map results =
            ContentServicesComplex.getAssocAndContentAndDataResourceMethod(delegator, contentId, mapKey, "From", fromDate, null, null, null, assocTypes, null);
        List entityList = (List) results.get("entityList");
        ctx.put("entityList", entityList);
        final Map templateContext = ctx;

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                StringBuffer ctxBuf = (StringBuffer) templateContext.get("buf");
                ctxBuf.append(cbuf, off, len);
                ////if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, buf:"+buf.toString(),module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws IOException {
                templateContext.put("buf", new StringBuffer());
                templateContext.put("entityIndex", new Integer(0));
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, onStart", module);
                boolean inProgress = prepCtx(delegator, templateContext, contentId, mimeTypeId, subDataResourceTypeId);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, onStart, inProgress:" + inProgress, module);
                if (inProgress) {
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws IOException {
                Integer idx = (Integer) templateContext.get("entityIndex");
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, idx :" + idx, module);
                int i = idx.intValue();
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, i:" + i, module);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, templateContext.entityIndex:" + templateContext.get("entityIndex"), module);
                boolean inProgress = prepCtx(delegator, templateContext, contentId, mimeTypeId, subDataResourceTypeId);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, inProgress:" + inProgress + " inProgress:" + inProgress, module);
                out.write(templateContext.get("buf").toString());
                templateContext.put("buf", new StringBuffer());
                if (inProgress)
                    return TransformControl.REPEAT_EVALUATION;
                else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {

                String wrappedFTL = buf.toString();
                //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrappedFTL:"+wrappedFTL,module);
                if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                    templateContext.put("wrappedFTL", wrappedFTL);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, rootDir:" + rootDir, module);
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
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapDataResourceTypeId:" + subDataResourceTypeId, module);
                    templateRoot.put("wrapContentIdTo", contentId);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapContentIdTo:" + contentId, module);
                    templateRoot.put("wrapMimeTypeId", mimeTypeId);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapMimeTypeId:" + mimeTypeId, module);
                    templateRoot.put("wrapMapKey", mapKey);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapMapKey:" + mapKey,module);
                    templateRoot.put("context", templateContext);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, calling renderContentAsText, wrapTemplateId:" + wrapTemplateId, module);
                    try {
                        ContentWorker.renderContentAsText(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, after renderContentAsText", module);
                    Map resultsCtx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, contentId:" + resultsCtx.get("contentId"), module);
                    templateContext.put("contentId", contentId);
                    templateContext.put("locale", locale);
                    templateContext.put("mapKey", null);
                    templateContext.put("subContentId", null);
                    templateContext.put("templateContentId", null);
                    templateContext.put("subDataResourceTypeId", null);
                    templateContext.put("mimeTypeId", null);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, after.", module);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, mapKey:" + mapKey, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, subDataResourceTypeId:" + subDataResourceTypeId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, contentId:" + contentId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, mimeTypeId:" + mimeTypeId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, locale:" + locale, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, contentId2." + resultsCtx.get("contentId"), module);
                }
            }
        };
    }
}
