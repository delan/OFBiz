/*
 * $Id: ContentWorker.java,v 1.7 2003/12/21 09:22:11 jonesde Exp $
 * 
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.io.IOException;
import java.io.Writer;

import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FlexibleStringExpander;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.minilang.SimpleMapProcessor;
import org.ofbiz.minilang.MiniLangException;

import bsh.EvalError;
import freemarker.template.SimpleHash;

/**
 * ContentWorker Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.7 $
 * @since 2.2
 * 
 *  
 */
public class ContentWorker {

    public static final String module = ContentWorker.class.getName();

    public static GenericValue findAlternateLocaleContent(GenericDelegator delegator, GenericValue view, Locale locale) throws IOException {
        GenericValue contentAssocDataResourceViewFrom = view;
        if (locale == null) {
            return contentAssocDataResourceViewFrom;
        }
        
        String localeStr = locale.toString();
        boolean isTwoLetterLocale = localeStr.length() == 2;

        List alternateViews = null;
        try {
            alternateViews = view.getRelated("ContentAssocDataResourceViewTo", UtilMisc.toMap("caContentAssocTypeId", "ALTERNATE_LOCALE"), UtilMisc.toList("-caFromDate"));
        } catch (GenericEntityException e) {
            throw new IOException(e.getMessage());
        }
        
        Iterator alternateViewIter = alternateViews.iterator();
        while (alternateViewIter.hasNext()) {
            GenericValue thisView = (GenericValue) alternateViewIter.next();
            String currentLocaleString = thisView.getString("localeString");
            if (UtilValidate.isEmpty(currentLocaleString)) {
                continue;
            }
            
            int currentLocaleLength = currentLocaleString.length();
            
            // could be a 2 letter or 5 letter code
            if (isTwoLetterLocale) {
                if (currentLocaleLength == 2) {
                    // if the currentLocaleString is only a two letter code and the current one is a two and it matches, we are done
                    if (localeStr.equals(currentLocaleString)) {
                        contentAssocDataResourceViewFrom = thisView;
                        break;
                    }
                } else if (currentLocaleLength == 5) {
                    // if the currentLocaleString is only a two letter code and the current one is a five, match up but keep going
                    if (localeStr.equals(currentLocaleString.substring(0, 2))) {
                        contentAssocDataResourceViewFrom = thisView;
                    }
                }
            } else {
                if (currentLocaleLength == 2) {
                    // if the currentLocaleString is a five letter code and the current one is a two and it matches, keep going
                    if (localeStr.substring(0, 2).equals(currentLocaleString)) {
                        contentAssocDataResourceViewFrom = thisView;
                    }
                } else if (currentLocaleLength == 5) {
                    // if the currentLocaleString is a five letter code and the current one is a five, if it matches we are done
                    if (localeStr.equals(currentLocaleString)) {
                        contentAssocDataResourceViewFrom = thisView;
                        break;
                    }
                }
            }
        }
        
        return contentAssocDataResourceViewFrom;
    }

    public static void traverse(GenericDelegator delegator, GenericValue content, Timestamp fromDate, Timestamp thruDate, Map whenMap,
            int depthIdx, Map masterNode, String contentAssocTypeId, List pickList, String direction) {
        //if (Debug.infoOn()) Debug.logInfo("contentId(traverse - 0):" + content.get("contentId") + " depth:" + depthIdx,null);
        //if (Debug.infoOn()) Debug.logInfo("masterNode(traverse -0):" + masterNode,null);
        //if (Debug.infoOn()) Debug.logInfo("traverse, fromDate:" + fromDate,null);
        //if (Debug.infoOn()) Debug.logInfo("traverse, thruDate:" + thruDate,null);
        
        //String startContentAssocTypeId = null;
        String contentTypeId = null;
        String contentId = null;
        try {
            if (contentAssocTypeId == null) {
                contentAssocTypeId = "";
            }
            contentId = (String) content.get("contentId");
            contentTypeId = (String) content.get("contentTypeId");
            //if (Debug.infoOn()) Debug.logInfo("contentTypeId(traverse):" + contentTypeId,null);
            List topicList = content.getRelatedByAnd("ToContentAssoc", UtilMisc.toMap("contentAssocTypeId", "TOPIC"));
            List topics = new ArrayList();
            for (int i = 0; i < topicList.size(); i++) {
                GenericValue assoc = (GenericValue) topicList.get(i);
                topics.add(assoc.get("contentId"));
            }
            //if (Debug.infoOn()) Debug.logInfo("topics(traverse):" + topics,null);
            List keywordList = content.getRelatedByAnd("ToContentAssoc", UtilMisc.toMap("contentAssocTypeId", "KEYWORD"));
            List keywords = new ArrayList();
            for (int i = 0; i < keywordList.size(); i++) {
                GenericValue assoc = (GenericValue) keywordList.get(i);
                keywords.add(assoc.get("contentId"));
            }
            //if (Debug.infoOn()) Debug.logInfo("keywords(traverse):" + keywords,null);
            List purposeValueList = content.getRelatedCache("ContentPurpose");
            List purposes = new ArrayList();
            for (int i = 0; i < purposeValueList.size(); i++) {
                GenericValue purposeValue = (GenericValue) purposeValueList.get(i);
                purposes.add(purposeValue.get("contentPurposeTypeId"));
            }
            //if (Debug.infoOn()) Debug.logInfo("purposes(traverse):" + purposes,null);
            List contentTypeAncestry = new ArrayList();
            getContentTypeAncestry(delegator, contentTypeId, contentTypeAncestry);

            Map context = new HashMap();
            context.put("content", content);
            context.put("contentAssocTypeId", contentAssocTypeId);
            //if (Debug.infoOn()) Debug.logInfo("contentAssocTypeId(traverse):" + contentAssocTypeId,null);
            //context.put("related", related);
            context.put("purposes", purposes);
            context.put("topics", topics);
            context.put("keywords", keywords);
            context.put("typeAncestry", contentTypeAncestry);
            //if (Debug.infoOn()) Debug.logInfo("context(traverse):" + context,null);
            boolean isPick = checkWhen(context, (String) whenMap.get("pickWhen"));
            //if (Debug.infoOn()) Debug.logInfo("isPick(traverse):" + isPick,null);
            boolean isReturnBefore = checkReturnWhen(context, (String) whenMap.get("returnBeforePickWhen"));
            //if (Debug.infoOn()) Debug.logInfo("isReturnBefore:" + isReturnBefore,null);
            Map thisNode = null;
            if (isPick || !isReturnBefore) {
                //if (Debug.infoOn()) Debug.logInfo("masterNode(traverse -1):" + masterNode,null);
                thisNode = new HashMap();
                thisNode.put("contentId", contentId);
                thisNode.put("contentTypeId", contentTypeId);
                thisNode.put("contentAssocTypeId", contentAssocTypeId);
                //if (Debug.infoOn()) Debug.logInfo("thisNode(traverse):" + thisNode,null);
                List kids = (List) masterNode.get("kids");
                if (kids == null) {
                    kids = new ArrayList();
                    masterNode.put("kids", kids);
                }
                kids.add(thisNode);
                //if (Debug.infoOn()) Debug.logInfo("masterNode(traverse -2):" + masterNode,null);
            }
            if (isPick) {
                pickList.add(content);
                thisNode.put("value", content);
                //if (Debug.infoOn()) Debug.logInfo("thisNode2(traverse):" + thisNode,null);
                //if (Debug.infoOn()) Debug.logInfo("masterNode(traverse -3):" + masterNode,null);
            }
            boolean isReturnAfter = checkReturnWhen(context, (String) whenMap.get("returnAfterPickWhen"));
            //if (Debug.infoOn()) Debug.logInfo("isReturnAfter:" + isReturnAfter,null);
            if (!isReturnAfter) {

                //if (Debug.infoOn()) Debug.logInfo("traverse, getContentAssocs, contentId:" + contentId,null);
                List relatedAssocs = getContentAssocsWithId(delegator, contentId, fromDate, thruDate, direction, new ArrayList());
                //if (Debug.infoOn()) Debug.logInfo("traverse, relatedAssocs:" + relatedAssocs,null);
                Iterator it = relatedAssocs.iterator();
                Map assocContext = new HashMap();
                assocContext.put("related", relatedAssocs);
                while (it.hasNext()) {
                    GenericValue assocValue = (GenericValue) it.next();
                    if (Debug.infoOn()) Debug.logInfo("assocValue, Id:" + assocValue.get("contentId") + " To:" + assocValue.get("contentIdTo") + " AssocTypeId:" + assocValue.get("contentAssocTypeId"), null);
                    contentAssocTypeId = (String) assocValue.get("contentAssocTypeId");
                    assocContext.put("contentAssocTypeId", contentAssocTypeId);
                    //assocContext.put("contentTypeId", assocValue.get("contentTypeId") );
                    assocContext.put("parentContent", content);
                    String assocRelation = null;
                    // This needs to be the opposite
                    String relatedDirection = null;
                    if (direction != null && direction.equalsIgnoreCase("From")) {
                        assocContext.put("contentIdFrom", assocValue.get("contentId"));
                        assocRelation = "ToContent";
                        relatedDirection = "From";
                    } else {
                        assocContext.put("contentIdTo", assocValue.get("contentId"));
                        assocRelation = "FromContent";
                        relatedDirection = "To";
                    }

                    //if (Debug.infoOn()) Debug.logInfo("assocContext(traverse - 2):" + assocContext,null);
                    boolean isFollow = checkWhen(assocContext, (String) whenMap.get("followWhen"));
                    //if (Debug.infoOn()) Debug.logInfo("isFollow:" + isFollow,null);
                    //if (Debug.infoOn()) Debug.logInfo("assocRelation:" + assocRelation,null);
                    //if (Debug.infoOn()) Debug.logInfo("relatedDirection:" + relatedDirection,null);
                    if (isFollow) {
                        GenericValue thisContent = assocValue.getRelatedOne(assocRelation);
                        //if (Debug.infoOn()) Debug.logInfo("thisContent, id:" + thisContent.get("contentId"),null);
                        traverse(delegator, thisContent, fromDate, thruDate, whenMap, depthIdx + 1, thisNode, contentAssocTypeId, pickList, relatedDirection);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError("Entity Error:" + e.getMessage(), null);
        }
        return;
    }

    public static boolean checkWhen(Map context, String whenStr) {
        //if (Debug.infoOn()) Debug.logInfo("whenStr:" + whenStr,null);
        boolean isWhen = true; //opposite default from checkReturnWhen
        if (whenStr != null && whenStr.length() > 0) {
            FlexibleStringExpander fse = new FlexibleStringExpander(whenStr);
            String newWhen = fse.expandString(context);
            try {
                Boolean isWhenObj = (Boolean) BshUtil.eval(newWhen, context);
                isWhen = isWhenObj.booleanValue();
            } catch (EvalError e) {
                Debug.logError("Error in evaluating :" + whenStr + " : " + e.getMessage(), null);
            }
        }
        return isWhen;
    }

    public static boolean checkReturnWhen(Map context, String whenStr) {
        //if (Debug.infoOn()) Debug.logInfo("checkReturnWhen:" + whenStr,null);
        boolean isWhen = false; //opposite default from checkWhen
        if (whenStr != null && whenStr.length() > 0) {
            FlexibleStringExpander fse = new FlexibleStringExpander(whenStr);
            String newWhen = fse.expandString(context);
            try {
                Boolean isWhenObj = (Boolean) BshUtil.eval(newWhen, context);
                isWhen = isWhenObj.booleanValue();
            } catch (EvalError e) {
                Debug.logError("Error in evaluating :" + whenStr + " : " + e.getMessage(), null);
            }
        }
        return isWhen;
    }

    public static List getAssociatedContent(GenericValue currentContent, String linkDir, List assocTypes, List contentTypes, String fromDate, String thruDate)
        throws GenericEntityException {

        GenericDelegator delegator = currentContent.getDelegator();
        List assocList = getAssociations(currentContent, linkDir, assocTypes, fromDate, thruDate);

        List contentList = new ArrayList();
        String contentIdName = "contentId";
        if (linkDir != null && linkDir.equalsIgnoreCase("TO")) {
            contentIdName.concat("To");
        }
        GenericValue assoc = null;
        GenericValue content = null;
        String contentTypeId = null;
        Iterator assocIt = assocList.iterator();
        while (assocIt.hasNext()) {
            assoc = (GenericValue) assocIt.next();
            String contentId = (String) assoc.get(contentIdName);
            content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
            if (contentTypes != null && contentTypes.size() > 0) {
                contentTypeId = (String) content.get("contentTypeId");
                if (contentTypes.contains(contentTypeId)) {
                    contentList.add(content);
                }
            } else {
                contentList.add(content);
            }

        }
        return contentList;

    }

    public static List getAssociatedContentView(GenericValue currentContent, String linkDir, List assocTypes, List contentTypes, String fromDate, String thruDate) throws GenericEntityException {
        List contentList = new ArrayList();
        List exprListAnd = new ArrayList();

        String origContentId = (String) currentContent.get("contentId");
        String contentIdName = "contentId";
        String contentAssocViewName = "contentAssocView";
        if (linkDir != null && linkDir.equalsIgnoreCase("TO")) {
            contentIdName.concat("To");
            contentAssocViewName.concat("To");
        }
        EntityExpr expr = new EntityExpr(contentIdName, EntityOperator.EQUALS, origContentId);
        exprListAnd.add(expr);

        if (contentTypes.size() > 0) {
            List exprListOr = new ArrayList();
            Iterator it = contentTypes.iterator();
            while (it.hasNext()) {
                String contentType = (String) it.next();
                expr = new EntityExpr("contentTypeId", EntityOperator.EQUALS, contentType);
                exprListOr.add(expr);
            }
            EntityConditionList contentExprList = new EntityConditionList(exprListOr, EntityOperator.OR);
            exprListAnd.add(contentExprList);
        }
        if (assocTypes.size() > 0) {
            List exprListOr = new ArrayList();
            Iterator it = assocTypes.iterator();
            while (it.hasNext()) {
                String assocType = (String) it.next();
                expr = new EntityExpr("contentAssocTypeId", EntityOperator.EQUALS, assocType);
                exprListOr.add(expr);
            }
            EntityConditionList assocExprList = new EntityConditionList(exprListOr, EntityOperator.OR);
            exprListAnd.add(assocExprList);
        }

        if (fromDate != null) {
            Timestamp tsFrom = UtilDateTime.toTimestamp(fromDate);
            expr = new EntityExpr("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, tsFrom);
            exprListAnd.add(expr);
        }

        if (thruDate != null) {
            Timestamp tsThru = UtilDateTime.toTimestamp(thruDate);
            expr = new EntityExpr("thruDate", EntityOperator.LESS_THAN, tsThru);
            exprListAnd.add(expr);
        }
        EntityConditionList contentCondList = new EntityConditionList(exprListAnd, EntityOperator.AND);
        GenericDelegator delegator = currentContent.getDelegator();
        contentList = delegator.findByCondition(contentAssocViewName, contentCondList, null, null);
        return contentList;
    }

    public static List getAssociations(GenericValue currentContent, String linkDir, List assocTypes, String strFromDate, String strThruDate) throws GenericEntityException {
        GenericDelegator delegator = currentContent.getDelegator();
        String origContentId = (String) currentContent.get("contentId");
        Timestamp fromDate = null;
        if (strFromDate != null) {
            fromDate = UtilDateTime.toTimestamp(strFromDate);
        }
        Timestamp thruDate = null;
        if (strThruDate != null) {
            thruDate = UtilDateTime.toTimestamp(strThruDate);
        }
        List assocs = getContentAssocsWithId(delegator, origContentId, fromDate, thruDate, linkDir, assocTypes);
        return assocs;
    }

    public static List getContentAssocsWithId(GenericDelegator delegator, String contentId, Timestamp fromDate, Timestamp thruDate, String direction, List assocTypes) throws GenericEntityException {
        //if (Debug.infoOn()) Debug.logInfo("getContentAssocs, direction:" + direction,null);
        List exprList = new ArrayList();
        EntityExpr joinExpr = null;
        EntityExpr expr = null;
        if (direction != null && direction.equalsIgnoreCase("From")) {
            joinExpr = new EntityExpr("contentIdTo", EntityOperator.EQUALS, contentId);
        } else {
            joinExpr = new EntityExpr("contentId", EntityOperator.EQUALS, contentId);
        }
        exprList.add(joinExpr);
        if (assocTypes != null && assocTypes.size() > 0) {
            List exprListOr = new ArrayList();
            Iterator it = assocTypes.iterator();
            while (it.hasNext()) {
                String assocType = (String) it.next();
                expr = new EntityExpr("contentAssocTypeId", EntityOperator.EQUALS, assocType);
                exprListOr.add(expr);
            }
            EntityConditionList assocExprList = new EntityConditionList(exprListOr, EntityOperator.OR);
            exprList.add(assocExprList);
        }
        if (fromDate != null) {
            EntityExpr fromExpr = new EntityExpr("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
            exprList.add(fromExpr);
        }
        if (thruDate != null) {
            List thruList = new ArrayList();
            //thruDate = UtilDateTime.getDayStart(thruDate, daysLater);

            EntityExpr thruExpr = new EntityExpr("thruDate", EntityOperator.LESS_THAN, thruDate);
            thruList.add(thruExpr);
            EntityExpr thruExpr2 = new EntityExpr("thruDate", EntityOperator.EQUALS, null);
            thruList.add(thruExpr2);
            EntityConditionList thruExprList = new EntityConditionList(thruList, EntityOperator.OR);
            exprList.add(thruExprList);
        } else if (fromDate != null) {
            List thruList = new ArrayList();

            EntityExpr thruExpr = new EntityExpr("thruDate", EntityOperator.GREATER_THAN, fromDate);
            thruList.add(thruExpr);
            EntityExpr thruExpr2 = new EntityExpr("thruDate", EntityOperator.EQUALS, null);
            thruList.add(thruExpr2);
            EntityConditionList thruExprList = new EntityConditionList(thruList, EntityOperator.OR);
            exprList.add(thruExprList);
        }
        EntityConditionList assocExprList = new EntityConditionList(exprList, EntityOperator.AND);
        List relatedAssocs = delegator.findByCondition("ContentAssoc", assocExprList, new ArrayList(), new ArrayList());
        //if (Debug.infoOn()) Debug.logInfo("relatedAssocs:", null);
        for (int i = 0; i < relatedAssocs.size(); i++) {
            GenericValue a = (GenericValue) relatedAssocs.get(i);
            if (Debug.infoOn()) Debug.logInfo(" contentId:" + a.get("contentId") + " To:" + a.get("contentIdTo") + " AssocTypeId:" + a.get("contentAssocTypeId"), null);

        }
        return relatedAssocs;
    }

    public static void getContentTypeAncestry(GenericDelegator delegator, String contentTypeId, List contentTypes) throws GenericEntityException {
        contentTypes.add(contentTypeId);
        GenericValue contentTypeValue = delegator.findByPrimaryKey("ContentType", UtilMisc.toMap("contentTypeId", contentTypeId));
        if (contentTypeValue == null)
            return;
        String parentTypeId = (String) contentTypeValue.get("parentTypeId");
        if (parentTypeId != null) {
            getContentTypeAncestry(delegator, parentTypeId, contentTypes);
        }
        return;
    }

    public static Map pullEntityValues(GenericDelegator delegator, String entityName, Map context) throws GenericEntityException {
        GenericValue entOut = delegator.makeValue(entityName, null);
        entOut.setPKFields(context);
        entOut.setNonPKFields(context);
        return (Map) entOut;
    }

    /**
     * callContentPermissionCheck Formats data for a call to the checkContentPermission service.
     */
    public static String callContentPermissionCheck(GenericDelegator delegator, LocalDispatcher dispatcher, Map context) {
        String permissionStatus = "granted";
        String skipPermissionCheck = (String) context.get("skipPermissionCheck");

        if (skipPermissionCheck == null
            || skipPermissionCheck.length() == 0
            || (!skipPermissionCheck.equalsIgnoreCase("true") && !skipPermissionCheck.equalsIgnoreCase("granted"))) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            Map serviceInMap = new HashMap();
            serviceInMap.put("userLogin", userLogin);
            serviceInMap.put("targetOperationList", context.get("targetOperationList"));
            serviceInMap.put("contentPurposeList", context.get("contentPurposeList"));
            serviceInMap.put("entityOperation", context.get("entityOperation"));

            try {
                Map permResults = dispatcher.runSync("checkContentPermission", serviceInMap);
                permissionStatus = (String) permResults.get("permissionStatus");
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem checking permissions", "ContentServices");
            }
        }
        return permissionStatus;
    }

    public static GenericValue getSubContent(GenericDelegator delegator, String contentId, String mapKey, String subContentId, GenericValue userLogin, List assocTypes, Timestamp fromDate) throws IOException {
        //GenericValue content = null;
        GenericValue view = null;
        try {
            if (subContentId == null) {
                if (contentId == null) {
                    throw new GenericEntityException("contentId and subContentId are null.");
                }
                Map results = null;
                results = ContentServicesComplex.getAssocAndContentAndDataResourceMethod(delegator, contentId, mapKey, "From", fromDate, null, null, null, assocTypes, null);
                List entityList = (List) results.get("entityList");
                if (entityList == null || entityList.size() == 0) {
                    //throw new IOException("No subcontent found.");
                } else {
                    view = (GenericValue) entityList.get(0);
                }
            } else {
                List lst = delegator.findByAnd("ContentDataResourceView", UtilMisc.toMap("contentId", subContentId));
                if (lst == null || lst.size() == 0) {
                    throw new IOException("No subContent found for subContentId=." + subContentId);
                }
                view = (GenericValue) lst.get(0);
            }
        } catch (GenericEntityException e) {
            throw new IOException(e.getMessage());
        }
        return view;
    }

    public static GenericValue getContentFromView(GenericValue view) {
        GenericValue content = null;
        if (view == null) {
            return content;
        }
        GenericDelegator delegator = view.getDelegator();
        content = delegator.makeValue("Content", null);
        content.setPKFields(view);
        content.setNonPKFields(view);
        String dataResourceId = (String) view.get("drDataResourceId");
        content.set("dataResourceId", dataResourceId);
        return content;
    }

    public static Map renderSubContentAsText(GenericDelegator delegator, String contentId, Writer out, String mapKey, String subContentId, GenericValue subContentDataResourceView, 
            SimpleHash templateContext, Locale locale, String mimeTypeId, GenericValue userLogin, Timestamp fromDate) throws IOException {

        //Map context = (Map) FreeMarkerWorker.get(templateContext, "context");
        //if (Debug.infoOn()) Debug.logInfo(" in renderSubContentAsText, mimeTypeId:" + mimeTypeId, module);
        Map results = new HashMap();
        //GenericValue content = null;
        if (subContentDataResourceView == null) {
            subContentDataResourceView = ContentWorker.getSubContent(delegator, contentId, mapKey, subContentId, userLogin, null, fromDate);
        }
        results.put("view", subContentDataResourceView);
        if (subContentDataResourceView == null) {
            //throw new IOException("SubContentDataResourceView is null.");
            if (Debug.infoOn()) Debug.logInfo(" in renderSubContentAsText, SubContentDataResourceView is null", module);
            return results;
        }

        //String dataResourceId = (String) subContentDataResourceView.get("drDataResourceId");
        subContentId = (String) subContentDataResourceView.get("contentId");
        //GenericValue dataResourceContentView = null;

        if (templateContext == null) {
            templateContext = new SimpleHash();
        }

        renderContentAsText(delegator, subContentId, out, templateContext, subContentDataResourceView, locale, mimeTypeId);

        return results;
    }

    public static Map renderContentAsText(GenericDelegator delegator, String contentId, Writer out, SimpleHash templateContext, GenericValue view, Locale locale, String mimeTypeId) throws IOException {
        //Map context = (Map) FreeMarkerWorker.get(templateContext, "context");
        //if (Debug.infoOn()) Debug.logInfo(" in renderContentAsText, mimeTypeId:" + mimeTypeId, module);
        Map results = new HashMap();
        GenericValue content = null;

        if (view == null) {
            if (contentId == null) {
                throw new IOException("ContentId is null");
            }
            try {
                List lst = delegator.findByAnd("SubContentDataResourceView", UtilMisc.toMap("contentId", contentId), UtilMisc.toList("-fromDate"));
                if (lst != null && lst.size() > 0) {
                    view = (GenericValue) lst.get(0);
                } else {
                    throw new IOException("SubContentDataResourceView not found in renderSubContentAsText" + " for contentId=" + contentId);
                }
            } catch (GenericEntityException e) {
                throw new IOException(e.getMessage());
            }
        }
        if (view != null) {
            Map contentMap = new HashMap();
            try {
                SimpleMapProcessor.runSimpleMapProcessor("org/ofbiz/content/ContentManagementMapProcessors.xml", "contentIn", view, contentMap, new ArrayList(), locale);
            } catch (MiniLangException e) {
                throw new IOException(e.getMessage());
            }
            content = delegator.makeValue("Content", contentMap);
        }

        results.put("view", view);
        results.put("content", content);

        if (locale != null) {
            String targetLocaleString = locale.toString();
            String thisLocaleString = (String) view.get("localeString");
            thisLocaleString = (thisLocaleString != null) ? thisLocaleString : "";
            //if (Debug.infoOn()) Debug.logInfo("thisLocaleString" + thisLocaleString, "");
            if (targetLocaleString != null && !targetLocaleString.equalsIgnoreCase(thisLocaleString)) {
                view = findAlternateLocaleContent(delegator, view, locale);
            }
        }

        //String contentTypeId = (String) view.get("contentTypeId");
        String dataResourceId = (String) view.get("drDataResourceId");
        if (templateContext == null) {
            templateContext = new SimpleHash();
        }

        try {
            DataResourceWorker.renderDataResourceAsHtml(delegator, dataResourceId, out, templateContext, view, locale, mimeTypeId);
        } catch (IOException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        return results;
    }
}
