/*
 * $Id: LoopSubContentCacheTransform.java,v 1.6 2004/04/11 08:28:15 jonesde Exp $
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
import org.ofbiz.content.content.ContentServicesComplex;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;

import freemarker.template.Environment;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;

/**
 * LoopSubContentCacheTransform - Freemarker Transform for URLs (links)
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.6 $
 * @since 3.0
 */
public class LoopSubContentCacheTransform implements TemplateTransformModel {

    public static final String module = LoopSubContentCacheTransform.class.getName();

    public static final String [] upSaveKeyNames = {"globalNodeTrail"};
    public static final String [] saveKeyNames = {"contentId", "subContentId", "entityList", "entityIndex", "subDataResourceTypeId", "mimeTypeId", "whenMap", "locale",  "entityList", "viewSize", "viewIndex", "highIndex", "lowIndex", "listSize", "wrapTemplateId", "encloseWrapText", "nullThruDatesOnly"};

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
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, prepCtx, entityIndex :" + entityIndex, module);
        if (entityIndex >= lst.size()) {
            return false;
        }
        GenericValue view = (GenericValue) lst.get(entityIndex);
        GenericValue electronicText = null;
        try {
            electronicText = view.getRelatedOne("ElectronicText");
        } catch (GenericEntityException e) {
            throw new GeneralException(e.getMessage());
        }
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, subContentDataResourceView contentId/drDataResourceId:" + view.get("contentId")  + " / " + view.get("drDataResourceId"), module);

        String dataResourceId = (String) view.get("drDataResourceId");
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(0), dataResourceId ." + dataResourceId, module);
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
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache(2), mimeTypeId." + mimeTypeId, module);
        if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, subContentId/Sub." + subContentIdSub, module);

        // This is what the FM template will see.
        List passedGlobalNodeTrail = (List)ctx.get("passedGlobalNodeTrail");
        //if (Debug.infoOn()) Debug.logInfo("passedGlobalNodeTrail(prepCtx):" + passedGlobalNodeTrail, "");
        List trail = null;
        if (passedGlobalNodeTrail != null) 
            trail = new ArrayList(passedGlobalNodeTrail);
        else
            trail = new ArrayList();
                //if (Debug.infoOn()) Debug.logInfo("prepCtx, trail(1):" + trail, "");
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
     
            trail.add(trailNode);
            int indentSz = trail.size();
            ctx.put("indent", new Integer(indentSz));
                String csvTrail = FreeMarkerWorker.nodeTrailToCsv(trail);
                //if (Debug.infoOn()) Debug.logInfo("in Loop, csvTrail:"+csvTrail,module);
            ctx.put("globalNodeTrail", trail);
                //if (Debug.infoOn()) Debug.logInfo("prepCtx, trail(2):" + trail, "");
                //if (Debug.infoOn()) Debug.logInfo("prepCtx, globalNodeTrail csv:" + FreeMarkerWorker.nodeTrailToCsv((List)trail), "");
            if (electronicText != null)
                ctx.put("textData", electronicText.get("textData"));
            else
                ctx.put("textData", null);
            ctx.put("subDataResourceTypeId", subDataResourceTypeId);
            ctx.put("mimeTypeId", mimeTypeId);
            ctx.put("subContentId", subContentIdSub);
//            ctx.put("drDataResourceId", dataResourceId);
//            ctx.put("dataResourceId", dataResourceId);
//            ctx.put("subContentIdSub", subContentIdSub);
            if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)end of prepCtx", ctx, 0),module);
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
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, getNext...", module);
                boolean inProgress = false;
 
                while (!inProgress && entityIndex < listSize) {
                    try {
                        inProgress = prepCtx(delegator, templateCtx);
                    } catch(GeneralException e) {
                        throw new IOException(e.getMessage());
                    }
                    entityIndex++;
                    templateCtx.put("entityIndex", new Integer(entityIndex));
                    if (inProgress) {
                        outputIndex++;
                        if (outputIndex >= lowIndex) {
                            break;
                        }
                    }
                }
                templateCtx.put("outputIndex", new Integer(outputIndex));
                return inProgress;
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
        if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)before save", templateCtx, 0),module);
        final Map savedValuesUp = new HashMap();
        FreeMarkerWorker.saveContextValues(templateCtx, upSaveKeyNames, savedValuesUp);
        final Map savedValues = new HashMap();
        // if (Debug.verboseOn()) Debug.logVerbose("(L-0)savedValues: " + savedValues,module);
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
        List trail = (List)templateCtx.get("globalNodeTrail");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), trail ." + trail, module);
        String strNullThruDatesOnly = (String)templateCtx.get("nullThruDatesOnly");
        Boolean nullThruDatesOnly = (strNullThruDatesOnly != null && strNullThruDatesOnly.equalsIgnoreCase("true")) ? new Boolean(true) :new Boolean(false);
        GenericValue val = null;
        try {
            val = FreeMarkerWorker.getCurrentContent(delegator, trail, userLogin, templateCtx, nullThruDatesOnly, null);
        } catch(GeneralException e) {
            throw new RuntimeException("Error getting current content. " + e.toString());
        }
        final GenericValue view = val;

        if (view == null) {
            throw new RuntimeException("No content found.");
        }
        templateCtx.put("contentId", null);
        templateCtx.put("subContentId", null);

        trail = (List)templateCtx.get("globalNodeTrail");
        List passedGlobalNodeTrail = null;
        if (trail != null && trail.size() > 0) 
            passedGlobalNodeTrail = new ArrayList(trail);
        else
            passedGlobalNodeTrail = new ArrayList();
        templateCtx.put("passedGlobalNodeTrail", passedGlobalNodeTrail);
        String contentId = (String)view.get("contentId");
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), passedGlobalNodeTrail ." + passedGlobalNodeTrail, module);
        //if (Debug.infoOn()) Debug.logInfo("in LoopSubContentCache(0), contentId ." + contentId, module);
        final String contentIdTo = contentId;

        String thisMapKey = (String)templateCtx.get("mapKey");
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
        if (Debug.verboseOn()) Debug.logVerbose("viewSize:" + viewSize, "");
        String viewIndexStr = (String)templateCtx.get("viewIndex");
        if (Debug.verboseOn()) Debug.logVerbose("viewIndexStr:" + viewIndexStr, "");
        if (UtilValidate.isEmpty(viewIndexStr))
            viewIndexStr = "0";
        int viewIndex = Integer.parseInt(viewIndexStr); 
        if (Debug.verboseOn()) Debug.logVerbose("viewIndex:" + viewIndex, "");
        int lowIndex = viewIndex * viewSize;
        if (Debug.verboseOn()) Debug.logVerbose("lowIndex:" + lowIndex, "");
        int listSize = longList.size();
        if (Debug.verboseOn()) Debug.logVerbose("listSize:" + listSize, "");
        int highIndex = (viewIndex + 1) * viewSize;
        if (highIndex > listSize)
            highIndex = listSize;
        if (Debug.verboseOn()) Debug.logVerbose("highIndex:" + highIndex, "");
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
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, buf:"+buf.toString(),module);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public int onStart() throws TemplateModelException, IOException {
                int viewIndex = ((Integer)templateCtx.get("viewIndex")).intValue(); 
                int viewSize = ((Integer)templateCtx.get("viewSize")).intValue(); 
                int listSize = ((Integer)templateCtx.get("listSize")).intValue(); 
                if (Debug.verboseOn()) Debug.logVerbose("listSize:" + listSize, "");
                int lowIndex = viewIndex * viewSize;
                if (Debug.verboseOn()) Debug.logVerbose("lowIndex:" + lowIndex, "");
                int highIndex = (viewIndex + 1) * viewSize;
                if (highIndex > listSize)
                    highIndex = listSize;
                if (Debug.verboseOn()) Debug.logVerbose("highIndex:" + highIndex, "");
                int outputIndex = lowIndex;
                templateCtx.put("lowIndex", new Integer(lowIndex));
                templateCtx.put("highIndex", new Integer(highIndex));
                templateCtx.put("outputIndex", new Integer(outputIndex));
                templateCtx.put("entityIndex", new Integer(0));
                boolean inProgress = false;
                if (outputIndex < highIndex) {
                    inProgress = getNextMatchingEntity(templateCtx, delegator);
                }
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, onStart, inProgress:" + inProgress, module);
                if (inProgress) {
                    FreeMarkerWorker.saveContextValues(templateCtx, saveKeyNames, savedValues);
                    return TransformControl.EVALUATE_BODY;
                } else {
                    return TransformControl.SKIP_BODY;
                }
            }

            public int afterBody() throws TemplateModelException, IOException {

                FreeMarkerWorker.reloadValues(templateCtx, savedValues);
                int outputIndex = ((Integer)templateCtx.get("outputIndex")).intValue(); 
                int highIndex = ((Integer)templateCtx.get("highIndex")).intValue(); 
                boolean inProgress = false;
                if (outputIndex < highIndex) {
                    inProgress = getNextMatchingEntity(templateCtx, delegator);
                }

                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, afterBody, inProgress:" + inProgress + " inProgress:" + inProgress, module);
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
/*
                if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrappedContent:"+wrappedContent,module);
                String wrapTemplateId = (String)templateCtx.get("wrapTemplateId");
                    if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, wrapTemplateId:" + wrapTemplateId, module);
                String editTemplate = (String)templateCtx.get("editTemplate");
                if (editTemplate != null && editTemplate.equalsIgnoreCase("true")
                    && UtilValidate.isNotEmpty(wrapTemplateId)) {
                    templateCtx.put("wrappedContent", wrappedContent);
                    templateCtx.put("contentIdTo", contentIdTo);
                    //if (Debug.verboseOn()) Debug.logVerbose("in LoopSubContentCache, rootDir:" + rootDir, module);
                    
                    //Map templateRoot = FreeMarkerWorker.createEnvironmentMap(env);
                    Map templateRoot = null;
                    Map templateRootTemplate = (Map)templateCtx.get("templateRootTemplate");
                    if (templateRootTemplate == null) {
                        Map templateRootTmp = FreeMarkerWorker.createEnvironmentMap(env);
                        templateRoot = new HashMap(templateRootTmp);
                        templateCtx.put("templateRootTemplate", templateRootTmp);
                    } else {
                        templateRoot = new HashMap(templateRootTemplate);
                    }
                    
                    templateRoot.put("context", templateCtx);
        if (Debug.verboseOn()) {
            Debug.logVerbose("in LoopSubContent, templateCtx.keySet()" + templateCtx.keySet(), "");
            Set kySet = templateCtx.keySet();
            Iterator it = kySet.iterator();
            while (it.hasNext()) {
                Object ky = it.next();
            Debug.logVerbose("in LoopContentAsText, ky:" + ky, "");
                Object val = templateCtx.get(ky);
                    Debug.logVerbose("in LoopContentAsText, val:" + val, "");
            }
        }
                    
                    Locale locale = (Locale) templateCtx.get("locale");
                    if (locale == null)
                        locale = Locale.getDefault();
                    String mimeTypeId = (String) templateCtx.get("mimeTypeId");
                if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)close middle", templateCtx, 0),module);
                    try {
                        ContentWorker.renderContentAsTextCache(delegator, wrapTemplateId, out, templateRoot, null, locale, mimeTypeId);
                    } catch (GeneralException e) {
                        Debug.logError(e, "Error rendering content", module);
                        throw new IOException("Error rendering content" + e.toString());
                    }
                if (Debug.verboseOn()) Debug.logVerbose(FreeMarkerWorker.logMap("(L)close middle(2)", templateCtx, 0),module);
                } else {
                    if (UtilValidate.isNotEmpty(wrappedContent))
                        out.write(wrappedContent);
                }
*/
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
