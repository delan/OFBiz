/*
 * $Id: LoopSubContentCacheTransform.java,v 1.14 2004/04/30 23:08:26 ajzeneski Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.content.content.ContentServicesComplex;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;

/**
 * LoopSubContentCacheTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.14 $
 * @since 3.0
 */
public class LoopSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = LoopSubContentCacheTransform.class.getName();

    public static final String [] upSaveKeyNames = {"globalNodeTrail"};
    public static final String [] saveKeyNames = {"contentId", "subContentId", "entityList", "entityIndex", "subDataResourceTypeId", "mimeTypeId", "whenMap", "locale",  "entityList", "viewSize", "viewIndex", "highIndex", "lowIndex", "listSize", "wrapTemplateId", "encloseWrapText", "nullThruDatesOnly", "globalNodeTrail"};

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

    public static boolean prepCtx(GenericDelegator delegator, Map ctx) throws GeneralException {

        //String contentId = (String)ctx.get("contentId");
        //String mimeTypeId = (String)ctx.get("mimeTypeId");
        List lst = (List) ctx.get("entityList");
        int entityIndex = ((Integer)ctx.get("entityIndex")).intValue();
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache, prepCtx, entityIndex :" + entityIndex, module);
        if (entityIndex >= lst.size()) {
            return false;
        }
        GenericValue view = (GenericValue) lst.get(entityIndex);
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache, subContentDataResourceView contentId/drDataResourceId:" + view.get("contentId")  + " / " + view.get("drDataResourceId") + " entityIndex:" + entityIndex, module);

        String dataResourceId = (String) view.get("drDataResourceId");
        String subContentIdSub = (String) view.get("contentId");
        //String contentAssocTypeId = (String) view.get("caContentAssocTypeId");
        //String mapKey = (String) view.get("caMapKey");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), subContentIdSub ." + subContentIdSub, module);
        // This order is taken so that the dataResourceType can be overridden in the transform arguments.
        String subDataResourceTypeId = (String)ctx.get("subDataResourceTypeId");
        if (UtilValidate.isEmpty(subDataResourceTypeId)) {
            subDataResourceTypeId = (String) view.get("drDataResourceTypeId");
            // TODO: If this value is still empty then it is probably necessary to get a value from
            // the parent context. But it will already have one and it is the same context that is
            // being passed.
        }

        String mimeTypeId = FreeMarkerWorker.getMimeTypeId(delegator, view, ctx);


        Map trailNode = FreeMarkerWorker.makeNode(view);
        Map whenMap = (Map)ctx.get("whenMap");
        Locale locale = (Locale) ctx.get("locale");
        if (locale == null)
            locale = Locale.getDefault();
        GenericValue assocContent = null;
        ContentWorker.checkConditions(delegator, trailNode, assocContent, whenMap);
        Boolean isReturnBeforeObj = (Boolean)trailNode.get("isReturnBefore");
        Boolean isReturnAfterObj = (Boolean)trailNode.get("isReturnAfter");
        Boolean isPickObj = (Boolean)trailNode.get("isPick");
        Boolean isFollowObj = (Boolean)trailNode.get("isFollow");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache, isReturnBeforeObj" + isReturnBeforeObj + " isPickObj:" + isPickObj + " isFollowObj:" + isFollowObj + " isReturnAfterObj:" + isReturnAfterObj, module);
        if ( (isReturnBeforeObj == null || !isReturnBeforeObj.booleanValue())
           && ( (isPickObj != null && isPickObj.booleanValue())
              ||  (isFollowObj != null && isFollowObj.booleanValue()))
        ) {
     
            List globalNodeTrail = (List)ctx.get("globalNodeTrail");
            globalNodeTrail.add(trailNode);
            ctx.put("globalNodeTrail", globalNodeTrail);
            String csvTrail = FreeMarkerWorker.nodeTrailToCsv(globalNodeTrail);
            ctx.put("nodeTrailCsv", csvTrail);
            //if (Debug.infoOn()) Debug.logInfo("prepCtx, csvTrail(2):" + csvTrail, "");
            int indentSz = globalNodeTrail.size();
            ctx.put("indent", new Integer(indentSz));
                //if (Debug.infoOn()) Debug.logInfo("prepCtx, trail(2):" + trail, "");
                //if (Debug.infoOn()) Debug.logInfo("prepCtx, globalNodeTrail csv:" + FreeMarkerWorker.nodeTrailToCsv((List)trail), "");
//            GenericValue electronicText = null;
//            try {
//                electronicText = view.getRelatedOneCache("ElectronicText");
//            } catch (GenericEntityException e) {
//                throw new GeneralException(e.getMessage());
//            }
//            if (electronicText != null)
//                ctx.put("textData", electronicText.get("textData"));
//            else
//                ctx.put("textData", null);
            ctx.put("subDataResourceTypeId", subDataResourceTypeId);
            ctx.put("mimeTypeId", mimeTypeId);
            ctx.put("subContentId", subContentIdSub);
            ctx.put("content", view);
//            ctx.put("drDataResourceId", dataResourceId);
//            ctx.put("dataResourceId", dataResourceId);
//            ctx.put("subContentIdSub", subContentIdSub);
            return true;
        } else {
            return false;
        }
    }

    public static boolean getNextMatchingEntity(Map templateCtx, GenericDelegator delegator) throws IOException {

                int lowIndex = ((Integer)templateCtx.get("lowIndex")).intValue(); 
                int entityIndex = ((Integer)templateCtx.get("entityIndex")).intValue(); 
                int outputIndex = ((Integer)templateCtx.get("outputIndex")).intValue(); 
                int listSize = ((Integer)templateCtx.get("listSize")).intValue(); 
                boolean matchFound = false;
 
                while (!matchFound && entityIndex < listSize) {
                    try {
                        matchFound = prepCtx(delegator, templateCtx);
                    } catch(GeneralException e) {
                        throw new IOException(e.getMessage());
                    }
                    entityIndex++;
                    templateCtx.put("entityIndex", new Integer(entityIndex));
                    if (matchFound) {
                        outputIndex++;
                        if (outputIndex >= lowIndex) {
                            break;
                        } else {
                            matchFound = false;
                        }
                    }
                }
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache, getNextMatchingEntity, outputIndex :" + outputIndex, module);
                templateCtx.put("outputIndex", new Integer(outputIndex));
                return matchFound;
    }

    public Writer getWriter(final Writer out, Map args) {
        //Profiler.begin("Loop");
        final StringBuffer buf = new StringBuffer();
        final Environment env = Environment.getCurrentEnvironment();
        final Map templateCtx = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        //FreeMarkerWorker.convertContext(templateCtx);
        final GenericDelegator delegator = (GenericDelegator) FreeMarkerWorker.getWrappedObject("delegator", env);
        final HttpServletRequest request = (HttpServletRequest) FreeMarkerWorker.getWrappedObject("request", env);
        FreeMarkerWorker.getSiteParameters(request, templateCtx);
        //templateCtx.put("buf", buf);
        final Map savedValuesUp = new HashMap();
        FreeMarkerWorker.saveContextValues(templateCtx, upSaveKeyNames, savedValuesUp);
        final Map savedValues = new HashMap();
        FreeMarkerWorker.overrideWithArgs(templateCtx, args);
        String contentAssocTypeId = (String)templateCtx.get("contentAssocTypeId");
        //if (UtilValidate.isEmpty(contentAssocTypeId)) {
            //throw new RuntimeException("contentAssocTypeId is empty");
        //}
        List assocTypes = StringUtil.split(contentAssocTypeId, "|");

        String contentPurposeTypeId = (String)templateCtx.get("contentPurposeTypeId");
        List purposeTypes = StringUtil.split(contentPurposeTypeId, "|");
        templateCtx.put("purposeTypes", purposeTypes);
        Locale locale = (Locale) templateCtx.get("locale");
        if (locale == null) {
            locale = Locale.getDefault();
            templateCtx.put("locale", locale);
        }

        Map whenMap = new HashMap();
        whenMap.put("followWhen", (String)templateCtx.get( "followWhen"));
        whenMap.put("pickWhen", (String)templateCtx.get( "pickWhen"));
        whenMap.put("returnBeforePickWhen", (String)templateCtx.get( "returnBeforePickWhen"));
        whenMap.put("returnAfterPickWhen", (String)templateCtx.get( "returnAfterPickWhen"));
        templateCtx.put("whenMap", whenMap);

        String fromDateStr = (String)templateCtx.get("fromDateStr");
        Timestamp fromDate = null;
        if (UtilValidate.isNotEmpty(fromDateStr)) {
            fromDate = UtilDateTime.toTimestamp(fromDateStr);
        }
        if (fromDate == null)
            fromDate = UtilDateTime.nowTimestamp();
       
        final GenericValue userLogin = (GenericValue) FreeMarkerWorker.getWrappedObject("userLogin", env);
        List globalNodeTrail = (List)templateCtx.get("globalNodeTrail");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), nodeTrailCsv ." + FreeMarkerWorker.nodeTrailToCsv(globalNodeTrail), module);
        String strNullThruDatesOnly = (String)templateCtx.get("nullThruDatesOnly");
        Boolean nullThruDatesOnly = (strNullThruDatesOnly != null && strNullThruDatesOnly.equalsIgnoreCase("true")) ? new Boolean(true) :new Boolean(false);
        GenericValue val = null;
        try {
            val = FreeMarkerWorker.getCurrentContent(delegator, globalNodeTrail, userLogin, templateCtx, nullThruDatesOnly, null);
        } catch(GeneralException e) {
            throw new RuntimeException("Error getting current content. " + e.toString());
        }
        final GenericValue view = val;

        if (view == null) {
            throw new RuntimeException("No content found.");
        }
        templateCtx.put("contentId", null);
        templateCtx.put("subContentId", null);

        String contentId = (String)view.get("contentId");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), contentId ." + contentId, module);
        final String contentIdTo = contentId;

        String thisMapKey = (String)templateCtx.get("mapKey");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), thisMapKey ." + thisMapKey, module);
        Map results = null;
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), assocTypes ." + assocTypes, module);
        String contentAssocPredicateId = (String)templateCtx.get("contentAssocPredicateId");
        try {
            results = ContentServicesComplex.getAssocAndContentAndDataResourceCacheMethod(delegator, contentId, thisMapKey, "From", fromDate, null, assocTypes, null, new Boolean(true), contentAssocPredicateId);
        } catch(MiniLangException e2) {
            throw new RuntimeException(e2.getMessage());
        } catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        List longList = (List) results.get("entityList");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), longList ." + longList.size(), module);
        String viewSizeStr = (String)templateCtx.get("viewSize");
        if (UtilValidate.isEmpty(viewSizeStr))
            viewSizeStr = "10";
        int viewSize = Integer.parseInt(viewSizeStr); 
        String viewIndexStr = (String)templateCtx.get("viewIndex");
        //if (Debug.infoOn()) Debug.logInfo("viewIndexStr:" + viewIndexStr, "");
        if (UtilValidate.isEmpty(viewIndexStr))
            viewIndexStr = "0";
        int viewIndex = Integer.parseInt(viewIndexStr); 
        int lowIndex = viewIndex * viewSize;
        int listSize = longList.size();
        int highIndex = (viewIndex + 1) * viewSize;
        if (highIndex > listSize)
            highIndex = listSize;
        Iterator it = longList.iterator();
        //List entityList = longList.subList(lowIndex, highIndex);
        List entityList = longList;
        templateCtx.put("entityList", entityList);
        templateCtx.put("viewIndex", new Integer(viewIndex));
        templateCtx.put("viewSize", new Integer(viewSize));
        templateCtx.put("lowIndex", new Integer(lowIndex));
        templateCtx.put("highIndex", new Integer(highIndex));
        templateCtx.put("listSize", new Integer(listSize));

        return new LoopWriter(out) {

            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
                //StringBuffer ctxBuf = (StringBuffer) templateCtx.get("buf");
                //ctxBuf.append(cbuf, off, len);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                List globalNodeTrail = (List)templateCtx.get("globalNodeTrail");
                String trailCsv = FreeMarkerWorker.nodeTrailToCsv(globalNodeTrail);
                //if (Debug.infoOn()) Debug.logInfo("in Loop, onStart, trailCsv:" + trailCsv, "");
                int viewIndex = ((Integer)templateCtx.get("viewIndex")).intValue(); 
                int viewSize = ((Integer)templateCtx.get("viewSize")).intValue(); 
                int listSize = ((Integer)templateCtx.get("listSize")).intValue(); 
                int lowIndex = viewIndex * viewSize;
                int highIndex = (viewIndex + 1) * viewSize;
                if (highIndex > listSize)
                    highIndex = listSize;
                int outputIndex = 0;
                templateCtx.put("lowIndex", new Integer(lowIndex));
                templateCtx.put("highIndex", new Integer(highIndex));
                templateCtx.put("outputIndex", new Integer(outputIndex));
                templateCtx.put("entityIndex", new Integer(0));
                boolean inProgress = false;
                if (outputIndex < highIndex) {
                    inProgress = getNextMatchingEntity(templateCtx, delegator);
                }
                FreeMarkerWorker.saveContextValues(templateCtx, saveKeyNames, savedValues);
                if (inProgress) {
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws TemplateModelException, IOException {

                FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                List list = (List)templateCtx.get("globalNodeTrail");
                List subList = list.subList(0, list.size() - 1 );
                templateCtx.put("globalNodeTrail", subList);
                
                int outputIndex = ((Integer)templateCtx.get("outputIndex")).intValue(); 
                int highIndex = ((Integer)templateCtx.get("highIndex")).intValue(); 
                boolean inProgress = false;
                if (outputIndex < highIndex) {
                    inProgress = getNextMatchingEntity(templateCtx, delegator);
                }

                FreeMarkerWorker.saveContextValues(templateCtx, saveKeyNames, savedValues);
                if (inProgress)
                    return TransformControl.REPEAT_EVALUATION;
                else
                    return TransformControl.END_EVALUATION;
            }

            public void close() throws IOException {

                FreeMarkerWorker.reloadValues(templateCtx, savedValuesUp);
                String wrappedContent = buf.toString();
                out.write(wrappedContent);
                //if (Debug.infoOn()) Debug.logInfo("in LoopSubContent, wrappedContent:" + wrappedContent, module);
        //try {
        //Profiler.end("Loop");
        //FileOutputStream fw = new FileOutputStream(new File("/usr/local/agi/ofbiz/hot-deploy/sfmp/misc/profile.data"));
        //Profiler.print(fw);
        //fw.close();
        //} catch (IOException e) {
           //Debug.logError("[PROFILER] " + e.getMessage(),"");
        //}
            }
        };
    }
}
