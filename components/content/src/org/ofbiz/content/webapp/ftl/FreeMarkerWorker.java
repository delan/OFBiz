/*
 * $Id: FreeMarkerWorker.java,v 1.25 2004/06/02 17:50:09 byersa Exp $
 *
 * Copyright (c) 2002-2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.content.webapp.ftl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.barcode.BarcodeTransform;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.minilang.MiniLangException;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
//import com.clarkware.profiler.Profiler;


/**
 * FreemarkerViewHandler - Freemarker Template Engine Util
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.25 $
 * @since      3.0
 */
public class FreeMarkerWorker {
    
    public static final String module = FreeMarkerWorker.class.getName();
    
    public static OfbizUrlTransform ofbizUrl = new OfbizUrlTransform();
    public static OfbizContentTransform ofbizContentUrl = new OfbizContentTransform();
    public static OfbizCurrencyTransform ofbizCurrency = new OfbizCurrencyTransform();
    public static SetRequestAttributeMethod setRequestAttribute = new SetRequestAttributeMethod();

    public static BarcodeTransform barcodeTransform = new BarcodeTransform();

    public static EditRenderSubContentTransform  editRenderSubContent = new EditRenderSubContentTransform();
    public static RenderSubContentTransform  renderSubContent = new RenderSubContentTransform();
    public static RenderWrappedTextTransform  renderWrappedText = new RenderWrappedTextTransform();
    public static LoopSubContentTransform  loopSubContent = new LoopSubContentTransform();
    public static TraverseSubContentTransform  traverseSubContent = new TraverseSubContentTransform();
    
    public static EditRenderSubContentCacheTransform  editRenderSubContentCache = new EditRenderSubContentCacheTransform();
    public static RenderSubContentCacheTransform  renderSubContentCache = new RenderSubContentCacheTransform();
    public static LoopSubContentCacheTransform  loopSubContentCache = new LoopSubContentCacheTransform();
    public static TraverseSubContentCacheTransform  traverseSubContentCache = new TraverseSubContentCacheTransform();
    public static CheckPermissionTransform  checkPermission = new CheckPermissionTransform();
    public static InjectNodeTrailCsvTransform  injectNodeTrailCsv = new InjectNodeTrailCsvTransform();
    public static WrapSubContentCacheTransform  wrapSubContentCache = new WrapSubContentCacheTransform();
    public static MenuWrapTransform  menuWrap = new MenuWrapTransform();

    public static Map cachedTemplates = new HashMap();
    
    public static void addAllOfbizTransforms(Map context) {
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        context.put("Static", staticModels);

        context.put("ofbizUrl", ofbizUrl);
        context.put("ofbizContentUrl", ofbizContentUrl);
        context.put("ofbizCurrency", ofbizCurrency);
        context.put("setRequestAttribute", setRequestAttribute);
        context.put("barcode", barcodeTransform);

        context.put("editRenderSubContent", editRenderSubContent);
        context.put("renderSubContent", renderSubContent);
        context.put("loopSubContent", loopSubContent);
        context.put("traverseSubContent", traverseSubContent);
        context.put("renderWrappedText", renderWrappedText);
        context.put("editRenderSubContentCache", editRenderSubContentCache);
        context.put("renderSubContentCache", renderSubContentCache);
        context.put("loopSubContentCache", loopSubContentCache);
        context.put("traverseSubContentCache", traverseSubContentCache);
        context.put("wrapSubContentCache", wrapSubContentCache);
        context.put("checkPermission", checkPermission);
        context.put("injectNodeTrailCsv", injectNodeTrailCsv);
        context.put("menuWrap", menuWrap);
    }
    
    public static Configuration makeDefaultOfbizConfig() throws TemplateException {
        Configuration config = Configuration.getDefaultConfiguration();            
        config.setObjectWrapper(BeansWrapper.getDefaultInstance());
        config.setSetting("datetime_format", "yyyy-MM-dd HH:mm:ss.SSS");
        return config;
    }
    
    public static String getArg(Map args, String key, Environment env) {
        Map templateContext = (Map) FreeMarkerWorker.getWrappedObject("context", env);
        return getArg(args, key, templateContext);
    }

    public static String getArg(Map args, String key, Map templateContext) {
        //SimpleScalar s = null;
        Object o = null;
        String returnVal = null;
        o = args.get(key);
        returnVal = (String) unwrap(o);
        if (returnVal == null) {
            try {
                if (templateContext != null) {
                    returnVal = (String) templateContext.get(key);
                }
            } catch (ClassCastException e2) {
                //return null;
            }
        }
        return returnVal;
    }

    public static Object getArgObject(Map args, String key, Map templateContext) {
        //SimpleScalar s = null;
        Object o = null;
        Object returnVal = null;
        o = args.get(key);
        returnVal = unwrap(o);
        if (returnVal == null) {
            try {
                if (templateContext != null) {
                    returnVal = templateContext.get(key);
                }
            } catch (ClassCastException e2) {
                //return null;
            }
        }
        return returnVal;
    }


   /**
    * Gets BeanModel from FreeMarker context and returns the object that it wraps.
    * @param varName the name of the variable in the FreeMarker context.
    * @param env the FreeMarker Environment
    */
    public static Object getWrappedObject(String varName, Environment env) {
        Object obj = null;
        try {
            obj = env.getVariable(varName);
            if (obj != null) {
                if (obj instanceof BeanModel) {
                    BeanModel bean = (BeanModel) obj;
                    obj = bean.getWrappedObject();
                }
            }
        } catch (TemplateModelException e) {
            Debug.logInfo(e.getMessage(), module);
        }
        return obj;
    }

   /**
    * Gets BeanModel from FreeMarker context and returns the object that it wraps.
    * @param varName the name of the variable in the FreeMarker context.
    * @param env the FreeMarker Environment
    */
    public static BeanModel getBeanModel(String varName, Environment env) {
        BeanModel bean = null;
        try {
            bean = (BeanModel) env.getVariable(varName);
        } catch (TemplateModelException e) {
            Debug.logInfo(e.getMessage(), module);
        }
        return bean;
    }

   /*
    * Tries to find the mime type of the associated content and parent content.
    *
    * @param delegator 
    * @param view SubContentDataResourceView
    * @param parentContent Content entity
    * @param contentId part of primary key of view. To be used if view is null.
    * @param dataResourceId part of primary key of view. To be used if view is null.
    * @param parentContentId primary key of parent content. To be used if parentContent is null;
    */
    public static String determineMimeType(GenericDelegator delegator, GenericValue view,
                             GenericValue parentContent, String contentId, String dataResourceId,
                             String parentContentId) throws GenericEntityException {
        String mimeTypeId = null;

        if (view != null) {
            mimeTypeId = (String)view.get("mimeTypeId");
            String drMimeTypeId = (String)view.get("drMimeTypeId");
            if (UtilValidate.isNotEmpty(drMimeTypeId)) {
                mimeTypeId = drMimeTypeId;
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (UtilValidate.isNotEmpty(contentId) && UtilValidate.isNotEmpty(dataResourceId)) {
                view = delegator.findByPrimaryKey("SubContentDataResourceView",
                        UtilMisc.toMap("contentId", contentId, "drDataResourceId", dataResourceId));
                if (view != null) {
                    mimeTypeId = (String)view.get("mimeTypeId");
                    String drMimeTypeId = (String)view.get("drMimeTypeId");
                    if (UtilValidate.isNotEmpty(drMimeTypeId)) {
                        mimeTypeId = drMimeTypeId;
                    }
                }
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (parentContent != null) {
                mimeTypeId = (String)parentContent.get("mimeTypeId");
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            if (UtilValidate.isNotEmpty(parentContentId)) {
                parentContent = delegator.findByPrimaryKey("Content",
                        UtilMisc.toMap("contentId", contentId));
                if (parentContent != null) {
                    mimeTypeId = (String)parentContent.get("mimeTypeId");
                }
            }
        }

        return mimeTypeId;
    }

    public static Object get(SimpleHash args, String key) {
        Object returnObj = null;
        Object o = null;
        try {
            o = args.get(key);
        } catch(TemplateModelException e) {
            Debug.logVerbose(e.getMessage(), module);
            return returnObj;
        }

        returnObj = unwrap(o);

        if (returnObj == null) {
            Object ctxObj = null;
            try {
                ctxObj = args.get("context");
            } catch(TemplateModelException e) {
                Debug.logInfo(e.getMessage(), module);
                return returnObj;
            }
            Map ctx = null;
            if (ctxObj instanceof BeanModel) {
                ctx = (Map)((BeanModel)ctxObj).getWrappedObject();
            returnObj = ctx.get(key);
            }
            /*
            try {
                Map templateContext = (Map)FreeMarkerWorker.getWrappedObject("context", env);
                if (templateContext != null) {
                    returnObj = (String)templateContext.get(key);
                }
            } catch(ClassCastException e2) {
                //return null;
            }
            */
        }
        return returnObj;
    }

    public static Object unwrap(Object o) {
        Object returnObj = null;

        if (o instanceof SimpleScalar) {
            returnObj = o.toString();
        } else if (o instanceof BeanModel) {
            returnObj = ((BeanModel)o).getWrappedObject();
        }
    
        return returnObj;
    }

    public static void checkForLoop(String path, Map ctx) throws IOException {
        List templateList = (List)ctx.get("templateList");
        if (templateList == null) {
            templateList = new ArrayList();
        } else {
            if (templateList.contains(path)) {
                throw new IOException(path + " has already been visited.");
            }
        }
        templateList.add(path);
        ctx.put("templateList", templateList);
        return;
    }

    public static Map createEnvironmentMap(Environment env) {
        Map templateRoot = new HashMap();
        Set varNames = null;
        try {
            varNames = env.getKnownVariableNames();
        } catch (TemplateModelException e1) {
            Debug.logError(e1, "Error getting FreeMarker variable names, will not put pass current context on to sub-content", module);
        }
        if (varNames != null) {
            Iterator varNameIter = varNames.iterator();
            while (varNameIter.hasNext()) {
                String varName = (String) varNameIter.next();
                //freemarker.ext.beans.StringModel varObj = (freemarker.ext.beans.StringModel ) varNameIter.next();
                //Object varObj =  varNameIter.next();
                //String varName = varObj.toString();
//Debug.logInfo("cEM: varObj:" + varObj + " varName:" + varName,"");
                templateRoot.put(varName, FreeMarkerWorker.getWrappedObject(varName, env));
            }
        }
        return templateRoot;
    }
    
    public static void renderTemplate(String templateIdString, String template, Map context, Writer outWriter) throws TemplateException, IOException {
        //if (Debug.infoOn()) Debug.logInfo("template:" + template.toString(), "");        
        Reader templateReader = new StringReader(template);
        renderTemplate(templateIdString, templateReader, context, outWriter);
    }
    
    public static void renderTemplate(String templateIdString, Reader templateReader, Map context, Writer outWriter) throws TemplateException, IOException {
        if (context == null) {
            context = new HashMap();
        }
        
        Configuration config = makeDefaultOfbizConfig();            
        Template template = new Template(templateIdString, templateReader, config);            
        // add the OFBiz transforms/methods
        addAllOfbizTransforms(context);
        
        cachedTemplates.put(templateIdString, template);
        // process the template with the given data and write
        // the email body to the String buffer
        template.process(context, outWriter);
    }
 
    public static Template getTemplateCached(String dataResourceId) {

        Template t = (Template)cachedTemplates.get("DataResource:" + dataResourceId);
        return t;
    }

    public static void renderTemplateCached( Template template, Map context, Writer outWriter) throws TemplateException, IOException {
        template.process(context, outWriter);
    }
    

    public static void traceNodeTrail(String lbl, List nodeTrail) {

/*
        if (!Debug.verboseOn()) {
            return;
        }
        if (nodeTrail == null) {
            return;
        }
        String s = "";
        int sz = nodeTrail.size();
        s = "nTsz:" + sz;
        if (sz > 0) {
            Map cN = (Map)nodeTrail.get(sz - 1);
            if (cN != null) {
                String cid = (String)cN.get("contentId");
                s += " cN[" + cid + "]";
                List kids = (List)cN.get("kids");
                int kSz = (kids == null) ? 0 : kids.size();
                s += " kSz:" + kSz;
                Boolean isPick = (Boolean)cN.get("isPick");
                s += " isPick:" + isPick;
                Boolean isFollow = (Boolean)cN.get("isFollow");
                s += " isFollow:" + isFollow;
                Boolean isReturnAfterPick = (Boolean)cN.get("isReturnAfterPick");
                s += " isReturnAfterPick:" + isReturnAfterPick;
            }
        }
*/
        return;
    }

    public static String logMap(String lbl, Map map, int indent) {
   
        String sep = ":";
        String eol = "\n";
        String spc = "";
        for (int i=0; i<indent; i++) 
            spc += "  ";
        String s = (lbl != null) ? lbl : "";
        s += "=" + indent + "==>" + eol;
        Set keySet = map.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            if ("request response session".indexOf(key) < 0) {
                Object obj = map.get(key);
                s += spc + key + sep;;
                if (obj instanceof GenericValue) {
                    GenericValue gv = (GenericValue)obj;
                    GenericPK pk = gv.getPrimaryKey();
                    s += logMap("GMAP[" + key + " name:" + pk.getEntityName()+ "]", pk, indent + 1);
                } else if (obj instanceof List) {
                    s += logList("LIST[" + ((List)obj).size() + "]", (List)obj, indent + 1);
                } else if (obj instanceof Map) {
                    s += logMap("MAP[" + key + "]", (Map)obj, indent + 1);
                } else if (obj != null) {
                    s += obj + sep + obj.getClass() + eol;
                } else {
                    s += eol;
                }
            }
        }
        return s + eol + eol;
    }

    public static String logList(String lbl, List lst, int indent) {
   
        String sep = ":";
        String eol = "\n";
        String spc = "";
        if (lst == null)
            return "";
        int sz = lst.size();
        for (int i=0; i<indent; i++) 
            spc += "  ";
        String s = (lbl != null) ? lbl : "";
        s += "=" + indent + "==> sz:" + sz + eol;
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
                s += spc;
                if (obj instanceof GenericValue) {
                    GenericValue gv = (GenericValue)obj;
                    GenericPK pk = gv.getPrimaryKey();
                    s += logMap("MAP[name:" + pk.getEntityName() + "]", pk, indent + 1);
                } else if (obj instanceof List) {
                    s += logList("LIST[" + ((List)obj).size() + "]", (List)obj, indent + 1);
                } else if (obj instanceof Map) {
                    s += logMap("MAP[]", (Map)obj, indent + 1);
                } else if (obj != null) {
                    s += obj + sep + obj.getClass() + eol;
                } else {
                    s += eol;
                }
        }
        return s + eol + eol;
    }

    public static void saveContextValues(Map context, String [] saveKeyNames, Map saveMap ) {
        //Map saveMap = new HashMap();
        for (int i=0; i<saveKeyNames.length; i++) {
            String key = (String)saveKeyNames[i];
            Object o = context.get(key);
            if (o instanceof Map)
                o = new HashMap((Map)o);
            else if (o instanceof List)
                o = new ArrayList((List)o);
            saveMap.put(key, o);
        }
        return ;
    }

    public static Map saveValues(Map context, String [] saveKeyNames) {
        Map saveMap = new HashMap();
        for (int i=0; i<saveKeyNames.length; i++) {
            String key = (String)saveKeyNames[i];
            Object o = context.get(key);
            if (o instanceof Map)
                o = new HashMap((Map)o);
            else if (o instanceof List)
                o = new ArrayList((List)o);
            saveMap.put(key, o);
        }
        return saveMap;
    }


    public static void reloadValues(Map context, Map saveValues ) {
        Set keySet = saveValues.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object o = saveValues.get(key);
            if (o instanceof Map) {
                Map map = new HashMap();
                map.putAll((Map)o);
                context.put(key, map);
            } else if (o instanceof List) {
                List list = new ArrayList();
                list.addAll((List)o);
                context.put(key, list);
            } else {
                context.put(key, o);
            }
        }
        return;
    }

    public static void removeValues(Map context, String [] removeKeyNames ) {
        for (int i=0; i<removeKeyNames.length; i++) {
            String key = (String)removeKeyNames[i];
            context.remove(key);
        }
        return;
    }

    public static void overrideWithArgs(Map ctx, Map args) {
        Set keySet = args.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object obj = args.get(key);
            if (obj != null) {
                Object unwrappedObj = unwrap(obj);
                if (unwrappedObj == null)
                    unwrappedObj = obj;
                ctx.put(key, unwrappedObj.toString());
            } else {
                ctx.put(key, null);
            }
        }
        return;
    }

    public static void convertContext(Map ctx) {
        Set keySet = ctx.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object obj = ctx.get(key);
            if (obj != null) {
                Object unwrappedObj = unwrap(obj);
                if (unwrappedObj != null) {
                    ctx.put(key, unwrappedObj);
                }
            }
        }
        return;
    }

    public static void getSiteParameters(HttpServletRequest request, Map ctx) {

        if (request == null) 
            return;
        ServletContext servletContext = request.getSession().getServletContext();
        String rootDir = (String)ctx.get("rootDir");
        String webSiteId = (String)ctx.get("webSiteId");
        String https = (String)ctx.get("https");
        if (UtilValidate.isEmpty(rootDir)) {
            rootDir = servletContext.getRealPath("/");
            ctx.put("webSiteId", webSiteId);
        }
        if (UtilValidate.isEmpty(webSiteId)) {
            webSiteId = (String) servletContext.getAttribute("webSiteId");
            ctx.put("https", https);
        }
        if (UtilValidate.isEmpty(https)) {
            https = (String) servletContext.getAttribute("https");
            ctx.put("rootDir", rootDir);
        }
        return;
    }

    public static Map makeNode(GenericValue thisContent) {
        Map thisNode = null;
        if (thisContent == null) 
            return thisNode;

        thisNode = new HashMap();
        thisNode.put("value", thisContent);
        String contentId = (String)thisContent.get("contentId");
        thisNode.put("contentId", contentId);
        thisNode.put("contentTypeId", thisContent.get("contentTypeId"));
        thisNode.put("isReturnBeforePick", new Boolean(false));
        thisNode.put("isReturnAfterPick", new Boolean(false));
        thisNode.put("isPick", new Boolean(true));
        thisNode.put("isFollow", new Boolean(true));
        try {
            thisNode.put("contentAssocTypeId", thisContent.get("caContentAssocTypeId"));
            thisNode.put("mapKey", thisContent.get("caMapKey"));
            thisNode.put("fromDate", thisContent.get("caFromDate"));
            thisNode.put("contentAssocTypeId", thisContent.get("caContentAssocTypeId"));
        } catch(Exception e) {
            // This ignores the case when thisContent does not have ContentAssoc values
        }
        return thisNode;
    }


    public static String nodeTrailToCsv(List nodeTrail) {
        
        if (nodeTrail == null)
            return "";
        StringBuffer csv = new StringBuffer();
        Iterator it = nodeTrail.iterator();
        while (it.hasNext()) {
            if (csv.length() > 0)
                csv.append(",");
            Map node = (Map)it.next();
            if (node == null)
                break;

            String contentId = (String)node.get("contentId");
            csv.append(contentId);
        }
        return csv.toString();
    }

    public static List csvToList(String csv, GenericDelegator delegator) {
        
        ArrayList outList = new ArrayList();
        List contentIdList = StringUtil.split(csv, ",");
        GenericValue content = null;
        String contentId = null;
        String contentName = null;
        ArrayList values = null;
        Iterator it = contentIdList.iterator();
        while (it.hasNext()) {
            contentId = (String)it.next();
            try {
                content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", contentId));
            } catch(GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
                return new ArrayList();
            }
            contentName = (String)content.get("contentName");
            values = new ArrayList();
            values.add(contentId);
            values.add(contentName);
            outList.add(values);    
        }
        return outList;
    }

    public static List csvToContentList(String csv, GenericDelegator delegator) {

        List trail = new ArrayList();
        if (csv == null)
            return trail;

        ArrayList outList = new ArrayList();
        List contentIdList = StringUtil.split(csv, ",");
        GenericValue content = null;
        String contentId = null;
        Iterator it = contentIdList.iterator();
        while (it.hasNext()) {
            contentId = (String)it.next();
            try {
                content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", contentId));
            } catch(GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
                return new ArrayList();
            }
            trail.add(content);
        }
        return trail;
    }

    public static List csvToTrail(String csv, GenericDelegator delegator) {

        ArrayList trail = new ArrayList();
        if (csv == null)
            return trail;

        List contentList = csvToContentList(csv, delegator);
        GenericValue content = null;
        Iterator it = contentList.iterator();
        while (it.hasNext()) {
            content = (GenericValue)it.next();
            Map node = makeNode(content);
            trail.add(node);
        }
        return trail;
    }

    public static GenericValue getCurrentContent( GenericDelegator delegator, List trail,  GenericValue userLogin, Map ctx, Boolean nullThruDatesOnly, String contentAssocPredicateId)  throws GeneralException {

        String contentId = (String)ctx.get("contentId");
        String subContentId = (String)ctx.get("subContentId");
        String mapKey = (String)ctx.get("mapKey");
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        List assocTypes = null;
        List passedGlobalNodeTrail = null;
        GenericValue currentContent = null;
        String viewContentId = null;
        if (trail != null && trail.size() > 0) 
            passedGlobalNodeTrail = new ArrayList(trail);
        else
            passedGlobalNodeTrail = new ArrayList();
        //if (Debug.infoOn()) Debug.logInfo("in getCurrentContent, passedGlobalNodeTrail(3):" + passedGlobalNodeTrail , module);
        int sz = passedGlobalNodeTrail.size();
        if (sz > 0) {
            Map nd = (Map)passedGlobalNodeTrail.get(sz - 1);
            if (nd != null)
                currentContent = (GenericValue)nd.get("value");
            if (currentContent != null) 
                viewContentId = (String)currentContent.get("contentId");
        }

        //if (Debug.infoOn()) Debug.logInfo("in getCurrentContent, currentContent(3):" + currentContent , module);
        if (Debug.infoOn()) Debug.logInfo("getCurrentContent, contentId:" + contentId, "");
        if (Debug.infoOn()) Debug.logInfo("getCurrentContent, subContentId:" + subContentId, "");
        if (Debug.infoOn()) Debug.logInfo("getCurrentContent, viewContentId:" + viewContentId, "");
        if (UtilValidate.isNotEmpty(subContentId)) {
            ctx.put("subContentId", subContentId);
            ctx.put("contentId", null);
            if (viewContentId != null && viewContentId.equals(subContentId) ) {
                return currentContent;
            }
        } else {
            ctx.put("contentId", contentId);
            ctx.put("subContentId", null);
            if (viewContentId != null && viewContentId.equals(contentId) ) {
                return currentContent;
            }
        }
        //if (Debug.infoOn()) Debug.logInfo("getCurrentContent(2), contentId:" + contentId + " viewContentId:" + viewContentId + " subContentId:" + subContentId, "");
        if (UtilValidate.isNotEmpty(contentId) || UtilValidate.isNotEmpty(subContentId)) {
            
                try {
                    currentContent = ContentWorker.getSubContentCache(delegator, contentId, mapKey, subContentId, userLogin, assocTypes, fromDate, nullThruDatesOnly, contentAssocPredicateId);
                    Map node = FreeMarkerWorker.makeNode(currentContent);
                    passedGlobalNodeTrail.add(node);
                } catch (GenericEntityException e) {
                    throw new GeneralException(e.getMessage());
                } catch (MiniLangException e2) {
                    throw new GeneralException(e2.getMessage());
                }
        }
        ctx.put("globalNodeTrail", passedGlobalNodeTrail);
        ctx.put("indent", new Integer(sz));
        //if (Debug.infoOn()) Debug.logInfo("getCurrentContent, currentContent:" + currentContent, "");
        return currentContent;
    }

    public static String getMimeTypeId(GenericDelegator delegator, GenericValue view, Map ctx) {
        // This order is taken so that the mimeType can be overridden in the transform arguments.
        String mimeTypeId = (String)ctx.get("mimeTypeId");
        if (UtilValidate.isEmpty(mimeTypeId) && view != null) {
            mimeTypeId = (String) view.get("mimeTypeId");
            String parentContentId = (String)ctx.get("contentId");
            if (UtilValidate.isEmpty(mimeTypeId) && UtilValidate.isNotEmpty(parentContentId)) { // will need these below
                try {
                    GenericValue parentContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", parentContentId));
                    if (parentContent != null) {
                        mimeTypeId = (String) parentContent.get("mimeTypeId");
                        ctx.put("parentContent", parentContent);
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e.getMessage(), module);
                    //throw new GeneralException(e.getMessage());
                }
            }

        }
        return mimeTypeId;
    }
}
