/*
 * $Id: TraverseSubContentTransform.java,v 1.3 2004/04/11 08:28:16 jonesde Exp $
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
import java.util.ArrayList;
import java.util.HashMap;
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
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;

/**
 * TraverseSubContentTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.3 $
 * @since 3.0
 */
public class TraverseSubContentTransform implements TemplateTransformModel {

    public static final String module = TraverseSubContentTransform.class.getName();
    public static final String [] saveKeyNames = {"contentId", "subContentId", "mimeType", "subContentDataResourceView", "wrapTemplateId", "templateContentId", "pickWhen", "followWhen", "returnAfterPickWhen", "returnBeforePickWhen", "indent"};
    public static final String [] removeKeyNames = {"templateContentId", "subDataResourceTypeId", "mapKey", "wrappedFTL", "nodeTrail"};

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
        //FreeMarkerWorker.convertContext(templateCtx);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)before save", templateCtx, 0),module);
        final Map savedValues = FreeMarkerWorker.saveValues(templateCtx, saveKeyNames);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after save", templateCtx, 0),module);
        if (Debug.verboseOn()) Debug.logVerbose("args:" + args,module);
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after overrride", templateCtx, 0),module);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
/*
        final String editTemplate = getArg(args, "editTemplate", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, editTemplate:" + editTemplate, module);
        final String wrapTemplateId = getArg(args, "wrapTemplateId", ctx);
        //final String mapKey = getArg(args, "mapKey", ctx);
        //if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, mapKey:" + mapKey, module);
        final String templateContentId = getArg(args, "templateContentId", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, templateContentId:" + templateContentId, module);
        final String subDataResourceTypeId = getArg(args, "subDataResourceTypeId", ctx);
        final String contentId = getArg(args, "contentId", ctx);
        final String subContentId = getArg(args, "subContentId", ctx);
        final String rootDir = getArg(args, "rootDir", ctx);
        final String webSiteId = getArg(args, "webSiteId", ctx);
        final String https = getArg(args, "https", ctx);
        if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, contentId:" + contentId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, subContentId:" + subContentId, module);
        final String viewSize = getArg(args, "viewSize", ctx);
        final String viewIndex = getArg(args, "viewIndex", ctx);
        final String listSize = getArg(args, "listSize", ctx);
        final String highIndex = getArg(args, "highIndex", ctx);
        final String lowIndex = getArg(args, "lowIndex", ctx);
        final String queryString = getArg(args, "queryString", ctx);
        final Locale locale = (Locale) FreeMarkerWorker.getWrappedObject("locale", env);
        final String mimeTypeId = getArg(args, "mimeTypeId", ctx);
*/
        //final LocalDispatcher dispatcher = (LocalDispatcher) FreeMarkerWorker.getWrappedObject("dispatcher", env);
        //final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);
        GenericValue view = (GenericValue) FreeMarkerWorker.getWrappedObject("subContentDataResourceView", env);
        final Integer indent = (templateCtx.get("indent") == null) ? new Integer(0) : (Integer)templateCtx.get("indent");
       
        String contentId = (String)templateCtx.get("contentId");
        String subContentId = (String)templateCtx.get("subContentId");
        if (view == null) {
            String thisContentId = subContentId;
            if (UtilValidate.isEmpty(thisContentId)) 
                thisContentId = contentId;

            if (UtilValidate.isNotEmpty(thisContentId)) {
            
                try {
                    view = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", thisContentId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Error getting sub-content", module);
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        final GenericValue subContentDataResourceView = view;

        final Map traverseContext = new HashMap();
        traverseContext.put("delegator", delegator);
        Map whenMap = new HashMap();
        whenMap.put("followWhen", (String)templateCtx.get( "followWhen"));
        whenMap.put("pickWhen", (String)templateCtx.get( "pickWhen"));
        whenMap.put("returnBeforePickWhen", (String)templateCtx.get( "returnBeforePickWhen"));
        whenMap.put("returnAfterPickWhen", (String)templateCtx.get( "returnAfterPickWhen"));
        traverseContext.put("whenMap", whenMap);
        String fromDateStr = (String)templateCtx.get( "fromDateStr");
        String thruDateStr = (String)templateCtx.get( "thruDateStr");
        Timestamp fromDate = null;
        if (fromDateStr != null && fromDateStr.length() > 0) {
            fromDate = UtilDateTime.toTimestamp(fromDateStr);
        }
        traverseContext.put("fromDate", fromDate);
        Timestamp thruDate = null;
        if (thruDateStr != null && thruDateStr.length() > 0) {
            thruDate = UtilDateTime.toTimestamp(thruDateStr);
        }
        traverseContext.put("thruDate", thruDate);
        String startContentAssocTypeId = (String)templateCtx.get( "contentAssocTypeId");
        if (startContentAssocTypeId != null)
            startContentAssocTypeId = "SUB_CONTENT";
        traverseContext.put("contentAssocTypeId", startContentAssocTypeId);
        String direction = (String)templateCtx.get( "direction");
        if (UtilValidate.isEmpty(direction)) 
            direction = "From";
        traverseContext.put("direction", direction);


        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                //StringBuffer ctxBuf = (StringBuffer) templateContext.get("buf");
                //ctxBuf.append(cbuf, off, len);
                buf.append(cbuf, off, len);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                //templateContext.put("buf", new StringBuffer());
                List nodeTrail = new ArrayList();
                traverseContext.put("nodeTrail", nodeTrail);
                GenericValue content = null;
/*
                if (UtilValidate.isNotEmpty(contentId)) {
                    try {
                        content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
                    } catch(GenericEntityException e){
                        // TODO: Not sure what to put here.
                        throw new RuntimeException(e.getMessage());
                    }
                }
*/
                Map rootNode = FreeMarkerWorker.makeNode(subContentDataResourceView);
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, onStart, rootNode:" + rootNode, module);
                FreeMarkerWorker.traceNodeTrail("1",nodeTrail);
                ContentWorker.selectKids(rootNode, traverseContext);
                FreeMarkerWorker.traceNodeTrail("2",nodeTrail);
                nodeTrail.add(rootNode);
                boolean isPick = checkWhen(subContentDataResourceView, (String)traverseContext.get("contentAssocTypeId"));
                rootNode.put("isPick", new Boolean(isPick));
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, onStart, isPick(1):" + isPick, module);
                if (!isPick) {
                FreeMarkerWorker.traceNodeTrail("3",nodeTrail);
                    isPick = ContentWorker.traverseSubContent(traverseContext);
                FreeMarkerWorker.traceNodeTrail("4",nodeTrail);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, onStart, isPick(2):" + isPick, module);
                }
                if (isPick) {
                    populateContext(traverseContext, templateCtx);
                FreeMarkerWorker.traceNodeTrail("5",nodeTrail);
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws TemplateModelException, IOException {
                //out.write(buf.toString());
                //buf.setLength(0);
                //templateContext.put("buf", new StringBuffer());
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, buf(w):" + buf.toString(), module);
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, pickWhen(w):" + templateCtx.get("pickWhen"), module);
                List nodeTrail = (List)traverseContext.get("nodeTrail");
                FreeMarkerWorker.traceNodeTrail("6",nodeTrail);
                boolean inProgress = ContentWorker.traverseSubContent(traverseContext);
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, inProgress:"+inProgress,module);
                FreeMarkerWorker.traceNodeTrail("7",nodeTrail);
                if (inProgress) {
                    populateContext(traverseContext, templateCtx);
                FreeMarkerWorker.traceNodeTrail("8",nodeTrail);
                    return TransformControl.REPEAT_EVALUATION;
                } else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {

                String wrappedFTL = buf.toString();
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContent, wrappedFTL:"+wrappedFTL,module);
                String encloseWrappedText = (String)templateCtx.get("encloseWrappedText");
                if (UtilValidate.isEmpty(encloseWrappedText) || encloseWrappedText.equalsIgnoreCase("false")) {

                    out.write(wrappedFTL);
                    wrappedFTL = null; // So it won't get written again below.
                }
                String wrapTemplateId = (String)templateCtx.get("wrapTemplateId");
                if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrappedFTL(0):" + wrappedFTL, module);
                    templateCtx.put("wrappedFTL", wrappedFTL);
                    
                    Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                    
/*
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, rootDir:" + rootDir, module);
                    templateRoot.put("viewSize", viewSize);
                    templateRoot.put("viewIndex", viewIndex);
                    templateRoot.put("listSize", listSize);
                    templateRoot.put("highIndex", highIndex);
                    templateRoot.put("lowIndex", lowIndex);
                    templateRoot.put("queryString", queryString);
                    templateRoot.put("wrapDataResourceTypeId", subDataResourceTypeId);
                    templateRoot.put("wrapContentIdTo", contentId);
                    templateRoot.put("wrapMimeTypeId", mimeTypeId);
                    //templateRoot.put("wrapMapKey", mapKey);
                    
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapDataResourceTypeId:" + subDataResourceTypeId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapContentIdTo:" + contentId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapMimeTypeId:" + mimeTypeId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapMapKey:" + mapKey,module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, calling renderContentAsText, wrapTemplateId:" + wrapTemplateId, module);
*/
                    templateRoot.put("context", templateCtx);
                    String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                    Locale locale = (Locale) templateCtx.get("locale");
                    if (locale == null)
                        locale = Locale.getDefault();
                    try {
                        ContentWorker.renderContentAsText(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, after renderContentAsText", module);
/*
                    Map resultsCtx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, contentId:" + resultsCtx.get("contentId"), module);
                    templateContext.put("contentId", contentId);
                    templateContext.put("locale", locale);
                    templateContext.put("mapKey", null);
                    templateContext.put("subContentId", null);
                    templateContext.put("templateContentId", null);
                    templateContext.put("subDataResourceTypeId", null);
                    templateContext.put("mimeTypeId", null);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, after.", module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, mapKey:" + mapKey, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, subDataResourceTypeId:" + subDataResourceTypeId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, contentId:" + contentId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, mimeTypeId:" + mimeTypeId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, locale:" + locale, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, contentId2." + resultsCtx.get("contentId"), module);
*/


                } else {
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrappedFTL(1):" + wrappedFTL, module);
                    if (UtilValidate.isNotEmpty(wrappedFTL))
                        out.write(wrappedFTL);
                }
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)before remove", templateCtx, 0),module);
                    FreeMarkerWorker.removeValues(templateCtx, removeKeyNames);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after remove", templateCtx, 0),module);
                    FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                    if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after reload", templateCtx, 0),module);
            }

            private boolean checkWhen (GenericValue thisContent, String contentAssocTypeId) {
        
                boolean isPick = false;
                Map assocContext = new HashMap();
                if (UtilValidate.isEmpty(contentAssocTypeId))
                    contentAssocTypeId = "";
                assocContext.put("contentAssocTypeId", contentAssocTypeId);
                //assocContext.put("contentTypeId", assocValue.get("contentTypeId") );
                String assocRelation = null;
                String thisDirection = (String)templateCtx.get("direction");
                String thisContentId = (String)templateCtx.get("thisContentId");
                String relatedDirection = null;
                if (thisDirection != null && thisDirection.equalsIgnoreCase("From")) {
                    assocContext.put("contentIdFrom", thisContentId);
                    assocRelation = "FromContent";
                    relatedDirection = "From";
                } else {
                    assocContext.put("contentIdTo", thisContentId);
                    assocRelation = "ToContent";
                    relatedDirection = "To";
                }
                assocContext.put("content", thisContent);
                List purposes = ContentWorker.getPurposes(thisContent);
                assocContext.put("purposes", purposes);
                List contentTypeAncestry = new ArrayList();
                String contentTypeId = (String)thisContent.get("contentTypeId");
                try {
                    ContentWorker.getContentTypeAncestry(delegator, contentTypeId, contentTypeAncestry);
                } catch(GenericEntityException e) {
                    if (Debug.verboseOn()) Debug.logVerbose("Error getting contentTypeAncestry:" + e.getMessage(),null);
                    return false;
                }
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, checkWhen, contentTypeAncestry(1):" + contentTypeAncestry, module);
                assocContext.put("typeAncestry", contentTypeAncestry);
                Map whenMap = (Map)traverseContext.get("whenMap");
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, checkWhen, whenMap(1):" + whenMap, module);
                String pickWhen = (String)whenMap.get("pickWhen");
                if (Debug.verboseOn()) Debug.logVerbose("pickWhen(checkWhen):" + pickWhen,null);
                if (Debug.verboseOn()) Debug.logVerbose("assocContext(checkWhen):" + assocContext,null);
                List nodeTrail = (List)traverseContext.get("nodeTrail");
                int indentSz = indent.intValue() + nodeTrail.size();
                assocContext.put("indentObj", new Integer(indentSz));
                isPick = ContentWorker.checkWhen(assocContext, (String)whenMap.get("pickWhen"));
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, checkWhen, isPick(1):" + isPick, module);
                return isPick;
           }


            public void populateContext(Map traverseContext, Map templateContext) {

                List nodeTrail = (List)traverseContext.get("nodeTrail");
                int sz = nodeTrail.size();
                Map node = (Map)nodeTrail.get(sz - 1);
                GenericValue content = (GenericValue)node.get("value");
                String contentId = (String)node.get("contentId");
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, populateContext, contentId(1):" + contentId, module);
                String subContentId = (String)node.get("subContentId");
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, populateContext, subContentId(1):" + subContentId, module);
                templateContext.put("subContentId", contentId);
                templateContext.put("subContentDataResourceView", null);
                int indentSz = indent.intValue() + nodeTrail.size();
                templateContext.put("indent", new Integer(indentSz));
                if (sz >= 2) {
                    Map parentNode = (Map)nodeTrail.get(sz - 2);
                    GenericValue parentContent = (GenericValue)parentNode.get("value");
                    String parentContentId = (String)parentNode.get("contentId");
                    templateContext.put("parentContentId", parentContentId);
                    templateContext.put("parentContent", parentContent);
                    templateContext.put("nodeTrail", nodeTrail);
                }
                return;
            }

        };
    }
}
