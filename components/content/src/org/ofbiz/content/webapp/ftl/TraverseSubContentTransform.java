/*
 * $Id: TraverseSubContentTransform.java,v 1.1 2003/12/30 05:27:36 byersa Exp $
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
import java.util.HashMap;
import java.util.ArrayList;
import java.io.StringWriter;

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
import freemarker.template.TemplateModelException;

/**
 * TraverseSubContentTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.1 $
 * @since 3.0
 */
public class TraverseSubContentTransform implements TemplateTransformModel {

    public static final String module = TraverseSubContentTransform.class.getName();

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
        //final LocalDispatcher dispatcher = (LocalDispatcher) FreeMarkerWorker.getWrappedObject("dispatcher", env);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        //final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);
        GenericValue view = (GenericValue) FreeMarkerWorker.getWrappedObject("subContentDataResourceView", env);
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
        whenMap.put("followWhen", getArg(args, "followWhen", ctx));
        whenMap.put("pickWhen", getArg(args, "pickWhen", ctx));
        whenMap.put("returnBeforePickWhen", getArg(args, "returnBeforePickWhen", ctx));
        whenMap.put("returnAfterPickWhen", getArg(args, "returnAfterPickWhen", ctx));
        traverseContext.put("whenMap", whenMap);
        String fromDateStr = getArg(args, "fromDateStr", ctx);
        String thruDateStr = getArg(args, "thruDateStr", ctx);
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
        String startContentAssocTypeId = getArg(args, "contentAssocTypeId", ctx);
        if (startContentAssocTypeId != null)
            startContentAssocTypeId = "SUB_CONTENT";
        traverseContext.put("contentAssocTypeId", startContentAssocTypeId);
        String direction = getArg(args, "direction", ctx);
        if (UtilValidate.isEmpty(direction)) 
            direction = "From";
        traverseContext.put("direction", direction);

        ctx.put("contentId", contentId);
        ctx.put("templateContentId", templateContentId);
        ctx.put("locale", locale);

        final Map templateContext = ctx;

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                //StringBuffer ctxBuf = (StringBuffer) templateContext.get("buf");
                //ctxBuf.append(cbuf, off, len);
                buf.append(cbuf, off, len);
                if (Debug.verboseOn()) Debug.logVerbose("in EditRenderSubContent, buf:" + buf.toString(), module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                templateContext.put("buf", new StringBuffer());
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
                Map rootNode = ContentWorker.makeNode(subContentDataResourceView);
                ContentWorker.selectKids(rootNode, traverseContext);
                nodeTrail.add(rootNode);
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, onStart, rootNode:" + rootNode, module);
                boolean isPick = checkWhen(subContentDataResourceView, (String)traverseContext.get("contentAssocTypeId"));
                rootNode.put("isPick", new Boolean(isPick));
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, onStart, isPick(1):" + isPick, module);
                if (!isPick) {
                    isPick = ContentWorker.traverseSubContent(traverseContext);
                    if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, onStart, isPick(2):" + isPick, module);
                }
                if (isPick) {
                    populateContext(traverseContext, templateContext);
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws TemplateModelException, IOException {
                out.write(buf.toString());
                buf.setLength(0);
                //templateContext.put("buf", new StringBuffer());
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, afterBody, traverseContext(2):" + traverseContext, module);
                boolean inProgress = ContentWorker.traverseSubContent(traverseContext);
                if (inProgress) {
                    populateContext(traverseContext, templateContext);
                    return TransformControl.REPEAT_EVALUATION;
                } else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {

                //String wrappedFTL = buf.toString();
                //if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, wrappedFTL:"+wrappedFTL,module);
                if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                    //templateContext.put("wrappedFTL", wrappedFTL);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, rootDir:" + rootDir, module);
                    templateContext.put("webSiteId", webSiteId);
                    templateContext.put("https", https);
                    templateContext.put("rootDir", rootDir);
                    
                    Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                    
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
                    templateRoot.put("context", templateContext);
                    
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapDataResourceTypeId:" + subDataResourceTypeId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapContentIdTo:" + contentId, module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapMimeTypeId:" + mimeTypeId, module);
                    //if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, wrapMapKey:" + mapKey,module);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, calling renderContentAsText, wrapTemplateId:" + wrapTemplateId, module);
                    try {
                        ContentWorker.renderContentAsText(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContent, after renderContentAsText", module);
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
                }
            }

            private boolean checkWhen (GenericValue thisContent, String contentAssocTypeId) {
        
                boolean isPick = false;
                Map assocContext = new HashMap();
                if (UtilValidate.isEmpty(contentAssocTypeId))
                    contentAssocTypeId = "";
                assocContext.put("contentAssocTypeId", contentAssocTypeId);
                //assocContext.put("contentTypeId", assocValue.get("contentTypeId") );
                String assocRelation = null;
                String thisDirection = (String)templateContext.get("direction");
                String relatedDirection = null;
                if (thisDirection != null && thisDirection.equalsIgnoreCase("From")) {
                    assocContext.put("contentIdFrom", contentId);
                    assocRelation = "FromContent";
                    relatedDirection = "From";
                } else {
                    assocContext.put("contentIdTo", contentId);
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
                    if (Debug.infoOn()) Debug.logInfo("Error getting contentTypeAncestry:" + e.getMessage(),null);
                    return false;
                }
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, checkWhen, contentTypeAncestry(1):" + contentTypeAncestry, module);
                assocContext.put("typeAncestry", contentTypeAncestry);
                Map whenMap = (Map)traverseContext.get("whenMap");
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, checkWhen, whenMap(1):" + whenMap, module);
                String pickWhen = (String)whenMap.get("pickWhen");
                if (Debug.infoOn()) Debug.logInfo("pickWhen(checkWhen):" + pickWhen,null);
                if (Debug.infoOn()) Debug.logInfo("assocContext(checkWhen):" + assocContext,null);
                isPick = ContentWorker.checkWhen(assocContext, (String)whenMap.get("pickWhen"));
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, checkWhen, isPick(1):" + isPick, module);
                return isPick;
           }


            public void populateContext(Map traverseContext, Map templateContext) {

                List nodeTrail = (List)traverseContext.get("nodeTrail");
                int sz = nodeTrail.size();
                Map node = (Map)nodeTrail.get(sz - 1);
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, populateContext, node(1):" + node, module);
                GenericValue content = (GenericValue)node.get("value");
                String contentId = (String)node.get("contentId");
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, populateContext, contentId(1):" + contentId, module);
                String subContentId = (String)node.get("subContentId");
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, populateContext, subContentId(1):" + subContentId, module);
                templateContext.put("subContentId", contentId);
/*
                StringWriter sw = new StringWriter();
                Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                //if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, populateContext, templateRoot(1):" + templateRoot, module);
                templateRoot.put("context", templateContext);
                try {
                    ContentWorker.renderContentAsText(delegator, contentId, sw, templateRoot, null, locale, mimeTypeId);
                } catch(GeneralException e) {
                    if (Debug.infoOn()) Debug.logInfo("Error in renderContentAsText:" + e.getMessage(), "");
                    return;
                } catch(IOException e2) {
                    if (Debug.infoOn()) Debug.logInfo("Error in renderContentAsText:" + e2.getMessage(), "");
                    return;
                }
                String textData = sw.toString();
                if (Debug.infoOn()) Debug.logInfo("in TraverseSubContent, populateContext, textData(1):" + textData, module);
                templateContext.put("textData", textData);
*/
                return;
            }

        };
    }
}
