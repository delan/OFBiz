/*
 * $Id: TraverseSubContentCacheTransform.java,v 1.3 2004/01/11 06:21:41 byersa Exp $
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

import freemarker.template.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import freemarker.template.TemplateModelException;

/**
 * TraverseSubContentCacheTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.3 $
 * @since 3.0
 */
public class TraverseSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = TraverseSubContentCacheTransform.class.getName();
    public static final String [] saveKeyNames = {"contentId", "subContentId", "mimeTypeId", "subContentDataResourceView", "wrapTemplateId", "templateContentId", "pickWhen", "followWhen", "returnAfterPickWhen", "returnBeforePickWhen", "globalNodeTrail", "entityList", "viewSize", "viewIndex", "highIndex", "lowIndex", "listSize", "thisViewIndex", "thisViewSize"};
    public static final String [] removeKeyNames = {"templateContentId", "subDataResourceTypeId", "mapKey", "wrappedFTL", "nodeTrail", "thisViewIndex", "thisViewSize"};

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
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(T)after overrride", templateCtx, 0),module);
        if (Debug.verboseOn()) Debug.logVerbose("args:" + args,module);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        final HttpServletRequest request = (HttpServletRequest) FreeMarkerWorker.getWrappedObject("request", env);
        FreeMarkerWorker.getSiteParameters(request, templateCtx);
        List trail = (List)templateCtx.get("globalNodeTrail");
        if (trail != null) 
            trail = new ArrayList(trail);
        else
            trail = new ArrayList();
        final List passedGlobalNodeTrail = trail;
        GenericValue view = null;
        if (passedGlobalNodeTrail.size() > 0) {
            view = (GenericValue)((Map)passedGlobalNodeTrail.get(passedGlobalNodeTrail.size() - 1)).get("value");
        }
       
        String contentId = (String)templateCtx.get("contentId");
        String subContentId = (String)templateCtx.get("subContentId");
        if (UtilValidate.isNotEmpty(subContentId) || UtilValidate.isNotEmpty(contentId) ) {
            String thisContentId = subContentId;
            if (UtilValidate.isEmpty(thisContentId)) 
                thisContentId = contentId;

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
        templateCtx.put("contentId", null);
        templateCtx.put("subContentId", null);

        final Map traverseContext = new HashMap();
        String vwIdx = (String)templateCtx.get("viewIndex");
        if (UtilValidate.isEmpty(vwIdx))
            vwIdx = "0";
        final int viewIndex = Integer.parseInt(vwIdx);
        templateCtx.put("viewIndex", new Integer(viewIndex).toString());
        String vwSz = (String)templateCtx.get("viewSize");
        if (UtilValidate.isEmpty(vwSz))
            vwSz = "10";
        final int viewSize = Integer.parseInt(vwSz);
        templateCtx.put("viewSize", new Integer(viewSize).toString());
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
                templateCtx.put("thisViewIndex", new Integer(0));
                templateCtx.put("thisViewSize", new Integer(0));
                templateCtx.put("lowIndex", "-1");
                templateCtx.put("highIndex", "0");
                List nodeTrail = null;
                GenericValue subContentDataResourceView = null;
                if (passedGlobalNodeTrail.size() > 0) {
                    int sz = passedGlobalNodeTrail.size() ;
                    nodeTrail = new ArrayList();
                    //nodeTrail = passedGlobalNodeTrail.subList(sz - 1, sz);
                    subContentDataResourceView = (GenericValue)((Map)passedGlobalNodeTrail.get(sz - 1)).get("value");
                } else {
                    // This really can't happen. Should probably throw exception
                    nodeTrail = new ArrayList();
                }
                traverseContext.put("nodeTrail", nodeTrail);
                GenericValue content = null;
                Map rootNode = FreeMarkerWorker.makeNode(subContentDataResourceView);
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, onStart, rootNode:" + rootNode, module);
                FreeMarkerWorker.traceNodeTrail("1",nodeTrail);
                ContentWorker.selectKids(rootNode, traverseContext);
                FreeMarkerWorker.traceNodeTrail("2",nodeTrail);
                nodeTrail.add(rootNode);
                boolean isPick = checkWhen(subContentDataResourceView, (String)traverseContext.get("contentAssocTypeId"));
                rootNode.put("isPick", new Boolean(isPick));
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, onStart, isPick(1):" + isPick, module);
                if (!isPick) {
                FreeMarkerWorker.traceNodeTrail("3",nodeTrail);
                    isPick = ContentWorker.traverseSubContent(traverseContext);
                FreeMarkerWorker.traceNodeTrail("4",nodeTrail);
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, onStart, isPick(2):" + isPick, module);
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
                //templateCtx.put("buf", new StringBuffer());
                Integer thisViewIndexInteger = (Integer)templateCtx.get("thisViewIndex");
                int thisViewIndex = thisViewIndexInteger.intValue();
                Integer thisViewSizeInteger = (Integer)templateCtx.get("thisViewSize");
                int thisViewSize = thisViewSizeInteger.intValue();
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, pickWhen(w):" + templateCtx.get("pickWhen"), module);
                List nodeTrail = (List)traverseContext.get("nodeTrail");
                FreeMarkerWorker.traceNodeTrail("afterBody",nodeTrail);
                int sz = nodeTrail.size();
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, sz(w):" + sz, module);
                Object highIndexObj = templateCtx.get("highIndex");
                int highIndex = 0;
                if (highIndexObj != null) {
                    highIndex = Integer.parseInt((String)highIndexObj);
                }
                highIndex++;
                templateCtx.put("highIndex", new Integer(highIndex).toString());
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, highIndex(w):" + highIndex, module);
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, viewSize(w):" + viewSize, module);
                if (sz == 2) {
                    if (thisViewIndex == viewIndex) {
                        templateCtx.put("lowIndex", new Integer(highIndex - 1).toString());
                    }
                    Object lowIndexObj = templateCtx.get("lowIndex");
                    if (lowIndexObj != null) {
                        int lowIndex = Integer.parseInt((String)lowIndexObj);
                        if ((highIndex - lowIndex) >= viewSize) {
                             return TransformControl.END_EVALUATION;
                        }
                    }
                    templateCtx.put("thisViewIndex", new Integer(thisViewIndex + 1));
                    templateCtx.put("viewIndex", new Integer(thisViewIndex).toString());
                }
                FreeMarkerWorker.traceNodeTrail("6",nodeTrail);
                boolean inProgress = ContentWorker.traverseSubContent(traverseContext);
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, inProgress:"+inProgress,module);
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
                String wrapTemplateId = (String)templateCtx.get("wrapTemplateId");
                if (UtilValidate.isEmpty(encloseWrappedText) || encloseWrappedText.equalsIgnoreCase("false")) {

                    out.write(wrappedFTL);
                    wrappedFTL = null; // So it won't get written again below.
                }
                if (UtilValidate.isNotEmpty(wrapTemplateId)) {
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, wrappedFTL(0):" + wrappedFTL, module);
                    templateCtx.put("wrappedFTL", wrappedFTL);
                    
                    Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                    
                    templateRoot.put("context", templateCtx);
                    String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                    Locale locale = (Locale) templateCtx.get("locale");
                    if (locale == null)
                        locale = Locale.getDefault();
                    try {
                        ContentWorker.renderContentAsTextCache(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, after renderContentAsText", module);


                } else {
                    if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, wrappedFTL(1):" + wrappedFTL, module);
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
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, checkWhen, contentTypeAncestry(1):" + contentTypeAncestry, module);
                assocContext.put("typeAncestry", contentTypeAncestry);
                Map whenMap = (Map)traverseContext.get("whenMap");
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, checkWhen, whenMap(1):" + whenMap, module);
                String pickWhen = (String)whenMap.get("pickWhen");
                if (Debug.verboseOn()) Debug.logVerbose("pickWhen(checkWhen):" + pickWhen,null);
                if (Debug.verboseOn()) Debug.logVerbose("assocContext(checkWhen):" + assocContext,null);
                List nodeTrail = (List)traverseContext.get("nodeTrail");
                //int indentSz = indent.intValue() + nodeTrail.size();
                //assocContext.put("indentObj", new Integer(indentSz));
                isPick = ContentWorker.checkWhen(assocContext, (String)whenMap.get("pickWhen"));
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, checkWhen, isPick(1):" + isPick, module);
                return isPick;
           }


            public void populateContext(Map traverseContext, Map templateContext) {

                List nodeTrail = (List)traverseContext.get("nodeTrail");
                int sz = nodeTrail.size();
                Map node = (Map)nodeTrail.get(sz - 1);
                GenericValue content = (GenericValue)node.get("value");
                String contentId = (String)node.get("contentId");
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, populateContext, contentId(1):" + contentId, module);
                String subContentId = (String)node.get("subContentId");
                if (Debug.verboseOn()) Debug.logVerbose("in TraverseSubContentCache, populateContext, subContentId(1):" + subContentId, module);
                templateContext.put("subContentDataResourceView", null);
                List globalNodeTrail = new ArrayList(passedGlobalNodeTrail);
                globalNodeTrail.addAll(nodeTrail);
                int indentSz = globalNodeTrail.size();
                templateContext.put("indent", new Integer(indentSz));
                templateContext.put("globalNodeTrail", globalNodeTrail);
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
