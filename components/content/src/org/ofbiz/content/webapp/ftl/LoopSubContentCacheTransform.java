/*
 * $Id: LoopSubContentCacheTransform.java,v 1.1 2004/01/07 19:30:11 byersa Exp $
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
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

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
import org.ofbiz.minilang.MiniLangException;

import freemarker.template.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import freemarker.template.TemplateModelException;

/**
 * LoopSubContentCacheTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.1 $
 * @since 3.0
 */
public class LoopSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = LoopSubContentCacheTransform.class.getName();

    public static final String [] saveKeyNames = {"contentId", "subContentId", "mimeTypeId", "subContentDataResourceView", "wrapTemplateId", "contentTemplateId", "globalNodeTrail"};
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
        //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, prepCtx, lst :" + lst, module);
        Integer idx = (Integer) ctx.get("entityIndex");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, prepCtx, idx :" + idx, module);
        if (idx == null)
            idx = new Integer(0);
        int i = idx.intValue();
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, prepCtx, i :" + i, module);
        if (i >= lst.size()) {
            return false;
        }
        GenericValue view = (GenericValue) lst.get(i);
        GenericValue electronicText = null;
        try {
            electronicText = view.getRelatedOne("ElectronicText");
        } catch (GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, subContentDataResourceView contentId/drDataResourceId:" + view.get("contentId")  + " / " + view.get("drDataResourceId"), module);

        String dataResourceId = (String) view.get("drDataResourceId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(0), dataResourceId ." + dataResourceId, module);
        String subContentIdSub = (String) view.get("contentId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(0), subContentIdSub ." + subContentIdSub, module);
        // This order is taken so that the dataResourceType can be overridden in the transform arguments.
        String subDataResourceTypeId = (String)ctx.get("subDataResourceTypeId");
        if (UtilValidate.isEmpty(subDataResourceTypeId)) {
            subDataResourceTypeId = (String) view.get("drDataResourceTypeId");
            // TODO: If this value is still empty then it is probably necessary to get a value from
            // the parent context. But it will already have one and it is the same context that is
            // being passed.
        }
        // This order is taken so that the mimeType can be overridden in the transform arguments.
        String mimeTypeId = (String)ctx.get("mimeTypeId");
        if (UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) view.get("mimeTypeId");
            String parentContentId = (String)ctx.get("contentId");
            if (UtilValidate.isEmpty(mimeTypeId) && UtilValidate.isNotEmpty(parentContentId)) { // will need these below
                try {
                    GenericValue parentContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", parentContentId));
                    if (parentContent != null) {
                        mimeTypeId = (String) parentContent.get("mimeTypeId");
                        ctx.put("parentContent", parentContent);
                        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, parentContentId: " + parentContent.get("contentId"), module);
                    }
                } catch (GenericEntityException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

        }
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(2), mimeTypeId." + mimeTypeId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, subContentId/Sub." + subContentIdSub, module);

        // This is what the FM template will see.
        List globalNodeTrail = new ArrayList((List)ctx.get("globalNodeTrail"));
        globalNodeTrail.add(FreeMarkerWorker.makeNode(view));
        int indentSz = globalNodeTrail.size();
        ctx.put("indent", new Integer(indentSz));
        ctx.put("globalNodeTrail", globalNodeTrail);
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
        final HttpServletRequest request = (HttpServletRequest) FreeMarkerWorker.getWrappedObject("request", env);
        FreeMarkerWorker.getSiteParameters(request, templateCtx);
        //templateCtx.put("buf", buf);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)before save", templateCtx, 0),module);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after overrride", templateCtx, 0),module);
        final Map savedValues = FreeMarkerWorker.saveValues(templateCtx, saveKeyNames);
        if (Debug.verboseOn()) Debug.logVerbose("(L-0)savedValues: " + savedValues,module);
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

        String fromDateStr = (String)templateCtx.get("fromDateStr");
        Timestamp fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            fromDate = UtilDateTime.toTimestamp(fromDateStr);
        }
        if (fromDate == null)
            fromDate = UtilDateTime.nowTimestamp();
        GenericValue view = null;
       
        String contentId = (String)templateCtx.get("contentId");
        String subContentId = (String)templateCtx.get("subContentId");
        String thisContentId = (String)templateCtx.get("contentId");
        if (UtilValidate.isEmpty(thisContentId)) {
            thisContentId = (String)templateCtx.get("subContentId");
        }
        List trail = (List)templateCtx.get("globalNodeTrail");
        List passedGlobalNodeTrail = null;
        if (trail != null) 
            passedGlobalNodeTrail = new ArrayList(trail);
        else
            passedGlobalNodeTrail = new ArrayList();
        if (UtilValidate.isNotEmpty(subContentId) || UtilValidate.isNotEmpty(contentId) ) {
            if (UtilValidate.isEmpty(thisContentId)) 
                thisContentId = subContentId;

            if (UtilValidate.isNotEmpty(thisContentId)) {
            
                try {
                    view = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", thisContentId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error getting sub-content", module);
                    throw new RuntimeException(e.getMessage());
                }
                passedGlobalNodeTrail.add(FreeMarkerWorker.makeNode(view));
            }
        }
        templateCtx.put("globalNodeTrail", passedGlobalNodeTrail);
        templateCtx.put("contentId", null);
        templateCtx.put("subContentId", null);

        String thisMapKey = (String)templateCtx.get("mapKey");
        Map results = null;
        try {
            results = ContentServicesComplex.getAssocAndContentAndDataResourceCacheMethod(delegator, thisContentId, thisMapKey, "From", fromDate, null, assocTypes, null);
        } catch(MiniLangException e2) {
            throw new RuntimeException(e2.getMessage());
        } catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        List entityList = (List) results.get("entityList");
        templateCtx.put("entityList", entityList);

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                //StringBuffer ctxBuf = (StringBuffer) templateCtx.get("buf");
                //ctxBuf.append(cbuf, off, len);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, buf:"+buf.toString(),module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                templateCtx.put("entityIndex", new Integer(0));
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, onStart", module);
                boolean inProgress = prepCtx(delegator, templateCtx);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, onStart, inProgress:" + inProgress, module);
                if (inProgress) {
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws TemplateModelException, IOException {
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, afterBody, start", module);
                Integer idx = (Integer) templateCtx.get("entityIndex");
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, prepCtx, idx :" + idx, module);
                int i = idx.intValue();
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, afterBody, i:" + i, module);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, afterBody, templateCtx.entityIndex:" + templateCtx.get("entityIndex"), module);
                if (Debug.verboseOn()) Debug.logVerbose("buf:" + buf.toString(),module);
                boolean inProgress = prepCtx(delegator, templateCtx);
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, afterBody, inProgress:" + inProgress + " inProgress:" + inProgress, module);
                //out.write(buf.toString());
                //buf.setLength(0);
                if (inProgress)
                    return TransformControl.REPEAT_EVALUATION;
                else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {

                String wrappedFTL = buf.toString();
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrappedFTL:"+wrappedFTL,module);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)before remove", templateCtx, 0),module);
                    FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after remove", templateCtx, 0),module);
        if (Debug.verboseOn()) Debug.logVerbose("(L-1)savedValues: " + savedValues,module);
                    FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)after reload", templateCtx, 0),module);
                String encloseWrappedText = (String)templateCtx.get("encloseWrappedText");
                if (UtilValidate.isEmpty(encloseWrappedText) || encloseWrappedText.equalsIgnoreCase("false")) {
                    out.write(wrappedFTL);
                    wrappedFTL = ""; // So it won't get written again below.
                }
                String wrapTemplateId = (String)templateCtx.get("wrapTemplateId");
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrapTemplateId:" + wrapTemplateId, module);
                if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                    templateCtx.put("wrappedFTL", wrappedFTL);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, rootDir:" + rootDir, module);
                    
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
                    
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrapDataResourceTypeId:" + subDataResourceTypeId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrapContentIdTo:" + contentId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrapMimeTypeId:" + mimeTypeId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrapMapKey:" + mapKey,module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, calling renderContentAsText, wrapTemplateId:" + wrapTemplateId, module);

                    Locale locale = (Locale) templateCtx.get("locale");
                    if (locale == null)
                        locale = Locale.getDefault();
                    String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                    try {
                        ContentWorker.renderContentAsTextCache(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                } else {
                    if (UtilValidate.isNotEmpty(wrappedFTL))
                        out.write(wrappedFTL);
                }
            }
        };
    }
}
