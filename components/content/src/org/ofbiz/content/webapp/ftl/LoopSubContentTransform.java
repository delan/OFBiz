/*
 * $Id: LoopSubContentTransform.java,v 1.6 2004/04/11 08:28:15 jonesde Exp $
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
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;

/**
 * LoopSubContentTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.6 $
 * @since 3.0
 */
public class LoopSubContentTransform implements TemplateTransformModel {

    public static final String module = LoopSubContentTransform.class.getName();

    public static final String [] saveKeyNames = {"contentId", "subContentId", "mimeType", "subContentDataResourceView", "wrapTemplateId", "contentTemplateId"};
    public static final String [] removeKeyNames = {"wrapTemplateId", "entityList", "entityIndex", "textData", "dataResourceId","drDataResourceId", "subContentIdSub", "parentContent", "wrappedFTL"};

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

    public static boolean prepCtx(GenericDelegator delegator, Map ctx) {

        //String contentId = (String)ctx.get("contentId");
        //String mimeTypeId = (String)ctx.get("mimeTypeId");
        List lst = (List) ctx.get("entityList");
        //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, lst :" + lst, module);
        Integer idx = (Integer) ctx.get("entityIndex");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, idx :" + idx, module);
        if (idx == null)
            idx = new Integer(0);
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
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, subContentDataResourceView contentId/drDataResourceId:" + subContentDataResourceView.get("contentId")  + " / " + subContentDataResourceView.get("drDataResourceId"), module);

        String dataResourceId = (String) subContentDataResourceView.get("drDataResourceId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(0), dataResourceId ." + dataResourceId, module);
        String subContentIdSub = (String) subContentDataResourceView.get("contentId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(0), subContentIdSub ." + subContentIdSub, module);
        // This order is taken so that the dataResourceType can be overridden in the transform arguments.
        String subDataResourceTypeId = (String)ctx.get("subDataResourceTypeId");
        if (UtilValidate.isEmpty(subDataResourceTypeId)) {
            subDataResourceTypeId = (String) subContentDataResourceView.get("drDataResourceTypeId");
            // TODO: If this value is still empty then it is probably necessary to get a value from
            // the parent context. But it will already have one and it is the same context that is
            // being passed.
        }
        // This order is taken so that the mimeType can be overridden in the transform arguments.
        String mimeTypeId = (String)ctx.get("mimeTypeId");
        if (UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) subContentDataResourceView.get("mimeTypeId");
            String parentContentId = (String)ctx.get("contentId");
            if (UtilValidate.isEmpty(mimeTypeId) && UtilValidate.isNotEmpty(parentContentId)) { // will need these below
                try {
                    GenericValue parentContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", parentContentId));
                    if (parentContent != null) {
                        mimeTypeId = (String) parentContent.get("mimeTypeId");
                        ctx.put("parentContent", parentContent);
                        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, parentContentId: " + parentContent.get("contentId"), module);
                    }
                } catch (GenericEntityException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

        }
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent(2), mimeTypeId." + mimeTypeId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, subContentId/Sub." + subContentIdSub, module);

        // This is what the FM template will see.
        ctx.put("subContentDataResourceView", subContentDataResourceView);
        if (electronicText != null)
            ctx.put("textData", electronicText.get("textData"));
        else
            ctx.put("textData", null);
        ctx.put("entityIndex", new Integer(i + 1));
        ctx.put("subContentId", subContentIdSub);
        ctx.put("drDataResourceId", dataResourceId);
        ctx.put("mimeTypeId", mimeTypeId);
        ctx.put("dataResourceId", dataResourceId);
        ctx.put("subContentIdSub", subContentIdSub);
        ctx.put("subDataResourceTypeId", subDataResourceTypeId);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)end of prepCtx", ctx, 0),module);
        return true;
    }

    public Writer getWriter(final Writer out, Map args) {
        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        final Map templateCtx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        //FreeMarkerWorker.convertContext(templateCtx);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        //templateCtx.put("buf", buf);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)before save", templateCtx, 0),module);
        final Map savedValues = FreeMarkerWorker.saveValues(templateCtx, saveKeyNames);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after overrride", templateCtx, 0),module);
        String contentAssocTypeId = (String)templateCtx.get("contentAssocTypeId");
        if (UtilValidate.isEmpty(contentAssocTypeId)) {
            contentAssocTypeId = "SUB_CONTENT";
            templateCtx.put("contentAssocTypeId ", contentAssocTypeId );
        }
        List assocTypes = UtilMisc.toList(contentAssocTypeId);
        templateCtx.put("assocTypes", assocTypes);
        Locale locale = (Locale) templateCtx.get("locale");
        if (locale == null) {
            locale = Locale.getDefault();
            templateCtx.put("locale", locale);
        }

/*
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
        String contentAssocTypeId = getArg(args, "contentAssocTypeId", ctx);
        if (UtilValidate.isEmpty(contentAssocTypeId))
            contentAssocTypeId = "SUB_CONTENT";
        List assocTypes = UtilMisc.toList(contentAssocTypeId);
        ctx.put("assocTypes", assocTypes);
*/
        String fromDateStr = (String)templateCtx.get("fromDateStr");
        Timestamp fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            fromDate = UtilDateTime.toTimestamp(fromDateStr);
        }
        if (fromDate == null)
            fromDate = UtilDateTime.nowTimestamp();
        String thisContentId = (String)templateCtx.get("contentId");
        if (UtilValidate.isEmpty(thisContentId)) {
            thisContentId = (String)templateCtx.get("subContentId");
        }
        String thisMapKey = (String)templateCtx.get("mapKey");
        //GenericValue subContentDataResourceView = null;
        Map results =
            ContentServicesComplex.getAssocAndContentAndDataResourceMethod(delegator, thisContentId, thisMapKey, "From", fromDate, null, null, null, assocTypes, null);
        List entityList = (List) results.get("entityList");
        templateCtx.put("entityList", entityList);

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                //StringBuffer ctxBuf = (StringBuffer) templateCtx.get("buf");
                //ctxBuf.append(cbuf, off, len);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, buf:"+buf.toString(),module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                templateCtx.put("entityIndex", new Integer(0));
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, onStart", module);
                boolean inProgress = prepCtx(delegator, templateCtx);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, onStart, inProgress:" + inProgress, module);
                if (inProgress) {
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws TemplateModelException, IOException {
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, start", module);
                Integer idx = (Integer) templateCtx.get("entityIndex");
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, prepCtx, idx :" + idx, module);
                int i = idx.intValue();
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, i:" + i, module);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, templateCtx.entityIndex:" + templateCtx.get("entityIndex"), module);
                if (Debug.verboseOn()) Debug.logVerbose("buf:" + buf.toString(),module);
                boolean inProgress = prepCtx(delegator, templateCtx);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, afterBody, inProgress:" + inProgress + " inProgress:" + inProgress, module);
                //out.write(buf.toString());
                //buf.setLength(0);
                if (inProgress)
                    return TransformControl.REPEAT_EVALUATION;
                else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {

                String wrappedFTL = buf.toString();
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrappedFTL:"+wrappedFTL,module);
                String encloseWrappedText = (String)templateCtx.get("encloseWrappedText");
                if (UtilValidate.isEmpty(encloseWrappedText) || encloseWrappedText.equalsIgnoreCase("false")) {
                    out.write(wrappedFTL);
                    wrappedFTL = ""; // So it won't get written again below.
                }
                String wrapTemplateId = (String)templateCtx.get("wrapTemplateId");
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapTemplateId:" + wrapTemplateId, module);
                if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                    templateCtx.put("wrappedFTL", wrappedFTL);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, rootDir:" + rootDir, module);
                    
                    Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                    
/*
                    templateRoot.put("viewSize", viewSize);
                    templateRoot.put("viewIndex", viewIndex);
                    templateRoot.put("listSize", listSize);
                    templateRoot.put("highIndex", highIndex);
                    templateRoot.put("lowIndex", lowIndex);
                    templateRoot.put("queryString", queryString);
                    templateRoot.put("wrapMapKey", mapKey);
*/
                    templateRoot.put("wrapDataResourceTypeId", templateCtx.get("subDataResourceTypeId"));
                    templateRoot.put("wrapContentIdTo", templateCtx.get("contentId"));
                    templateRoot.put("wrapMimeTypeId", templateCtx.get("mimeTypeId"));
                    templateRoot.put("context", templateCtx);
                    
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapDataResourceTypeId:" + subDataResourceTypeId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapContentIdTo:" + contentId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapMimeTypeId:" + mimeTypeId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrapMapKey:" + mapKey,module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, calling renderContentAsText, wrapTemplateId:" + wrapTemplateId, module);

                    Locale locale = (Locale) templateCtx.get("locale");
                    if (locale == null)
                        locale = Locale.getDefault();
                    String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                    try {
                        ContentWorker.renderContentAsText(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                } else {
                    if (UtilValidate.isNotEmpty(wrappedFTL))
                        out.write(wrappedFTL);
                }
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)before remove", templateCtx, 0),module);
                    FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after remove", templateCtx, 0),module);
                    FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after reload", templateCtx, 0),module);
            }
        };
    }
}
