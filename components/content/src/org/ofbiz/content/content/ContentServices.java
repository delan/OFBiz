/*
 * $Id: ContentServices.java,v 1.15 2004/01/07 19:30:11 byersa Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.content;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.webapp.ftl.FreeMarkerWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * ContentServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.15 $
 * @since 2.2
 * 
 *  
 */
public class ContentServices {

    public static final String module = ContentServices.class.getName();

    /**
     * findRelatedContent Finds the related
     */
    public static Map findRelatedContent(DispatchContext dctx, Map context) {
        Map results = new HashMap();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue currentContent = (GenericValue) context.get("currentContent");
        String fromDate = (String) context.get("fromDate");
        String thruDate = (String) context.get("thruDate");
        String toFrom = (String) context.get("toFrom");
        if (toFrom == null) {
            toFrom = "TO";
        } else {
            toFrom = toFrom.toUpperCase();
        }
        List assocTypes = (List) context.get("contentAssocTypeList");
        List targetOperations = (List) context.get("targetOperationList");
        List contentList = null;
        List contentTypes = (List) context.get("contentTypeList");
        try {
            contentList = ContentWorker.getAssociatedContent(currentContent, toFrom, assocTypes, contentTypes, fromDate, thruDate);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Error getting associated content: " + e.toString());
        }

        if (targetOperations == null || targetOperations.isEmpty()) {
            results.put("contentList", contentList);
            return results;
        }

        Map serviceInMap = new HashMap();
        serviceInMap.put("userLogin", context.get("userLogin"));
        serviceInMap.put("targetOperationList", targetOperations);
        serviceInMap.put("entityOperation", context.get("entityOperation"));
        List permittedList = new ArrayList();
        Iterator it = contentList.iterator();
        Map permResults = null;
        while (it.hasNext()) {
            GenericValue content = (GenericValue) it.next();
            serviceInMap.put("currentContent", content);
            try {
                permResults = dispatcher.runSync("checkContentPermission", serviceInMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem checking permissions", "ContentServices");
                return ServiceUtil.returnError("Problem checking permissions");
            }
            String permissionStatus = (String) permResults.get("permissionStatus");
            if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
                permittedList.add(content);
            }

        }

        results.put("contentList", permittedList);

        return results;

    }

    /**
     * This is a generic service for traversing a Content tree, typical of a blog response tree. It calls the ContentWorker.traverse method.
     */
    public static Map traverseContent(DispatchContext dctx, Map context) {
        HashMap results = new HashMap();

        GenericDelegator delegator = dctx.getDelegator();

        String contentId = (String) context.get("contentId");
        String direction = (String) context.get("direction");
        if (direction != null && direction.equalsIgnoreCase("From")) {
            direction = "From";
        } else {
            direction = "To";
        }
        //Debug.logVerbose("contentId(start):" + contentId, null);
        if (contentId == null)
            contentId = "PUBLISH_ROOT";
        GenericValue content = null;
        try {
            content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
        } catch (GenericEntityException e) {
            System.out.println("Entity Error:" + e.getMessage());
            return ServiceUtil.returnError("Error in retrieving Content. " + e.getMessage());
        }
        //Debug.logVerbose("content(start):" + content, null);
        String fromDateStr = (String) context.get("fromDateStr");
        String thruDateStr = (String) context.get("thruDateStr");
        Timestamp fromDate = null;
        if (fromDateStr != null && fromDateStr.length() > 0) {
            fromDate = UtilDateTime.toTimestamp(fromDateStr);
        }
        Timestamp thruDate = null;
        if (thruDateStr != null && thruDateStr.length() > 0) {
            thruDate = UtilDateTime.toTimestamp(thruDateStr);
        }
        Map whenMap = new HashMap();
        whenMap.put("followWhen", context.get("followWhen"));
        whenMap.put("pickWhen", context.get("pickWhen"));
        whenMap.put("returnBeforePickWhen", context.get("returnBeforePickWhen"));
        whenMap.put("returnAfterPickWhen", context.get("returnAfterPickWhen"));
        String startContentAssocTypeId = (String) context.get("contentAssocTypeId");
        if (startContentAssocTypeId != null)
            startContentAssocTypeId = "PUBLISH";
        Map nodeMap = new HashMap();
        List pickList = new ArrayList();
        //Debug.logVerbose("whenMap(start):" + whenMap,null);
        ContentWorker.traverse(delegator, content, fromDate, thruDate, whenMap, 0, nodeMap, startContentAssocTypeId, pickList, direction);
        //Debug.logVerbose("After travers",null);

        results.put("nodeMap", nodeMap);
        results.put("pickList", pickList);
        return results;
    }

    /**
     * Create a Content service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map createContent(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = createContentMethod(dctx, context);
        return result;
    }

    /**
     * Create a Content method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map createContentMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String contentId = (String) context.get("contentId");
        //String contentTypeId = (String) context.get("contentTypeId");

        if (contentId == null)
            contentId = delegator.getNextSeqId("Content").toString();
        GenericValue content = delegator.makeValue("Content", UtilMisc.toMap("contentId", contentId));
        content.setNonPKFields(context);
        context.put("currentContent", content);
        String permissionStatus = ContentWorker.callContentPermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = (String) userLogin.get("userLoginId");
            String createdByUserLogin = userLoginId;
            String lastModifiedByUserLogin = userLoginId;
            Timestamp createdDate = UtilDateTime.nowTimestamp();
            Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

            content.put("createdByUserLogin", createdByUserLogin);
            content.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
            content.put("createdDate", createdDate);
            content.put("lastModifiedDate", lastModifiedDate);
            //Debug.logVerbose("in createContent, content:" + content, "");
            try {
                content.create();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
            result.put("contentId", contentId);
        }
        context.remove("currentContent");
        return result;
    }

    /**
     * Create a ContentAssoc service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map createContentAssoc(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = null;
        try {
            result = createContentAssocMethod(dctx, context);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericEntityException e2) {
            return ServiceUtil.returnError(e2.getMessage());
        } catch (Exception e3) {
            return ServiceUtil.returnError(e3.getMessage());
        }
        return result;
    }

    /**
     * Create a ContentAssoc method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map createContentAssocMethod(DispatchContext dctx, Map context) throws GenericServiceException, GenericEntityException {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        // This section guesses how contentId should be used (From or To) if
        // only a contentIdFrom o contentIdTo is passed in
        String contentIdFrom = (String) context.get("contentIdFrom");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentId = (String) context.get("contentId");
        Debug.logVerbose("CREATING CONTENTASSOC contentIdFrom(1):" + contentIdFrom, null);
        Debug.logVerbose("CREATING CONTENTASSOC contentIdTo(1):" + contentIdTo, null);
        Debug.logVerbose("CREATING CONTENTASSOC contentId:" + contentId, null);
        int contentIdCount = 0;
        if (UtilValidate.isNotEmpty(contentIdFrom))
            contentIdCount++;
        if (UtilValidate.isNotEmpty(contentIdTo))
            contentIdCount++;
        if (UtilValidate.isNotEmpty(contentId))
            contentIdCount++;
        if (contentIdCount < 2) {
            Debug.logError("Not 2 out of ContentId/To/From.", "ContentServices");
            return ServiceUtil.returnError("Not 2 out of ContentId/To/From");
        }
        if (UtilValidate.isNotEmpty(contentIdFrom)) {
            if (UtilValidate.isEmpty(contentIdTo))
                contentIdTo = contentId;
        }
        if (UtilValidate.isNotEmpty(contentIdTo)) {
            if (UtilValidate.isEmpty(contentIdFrom))
                contentIdFrom = contentId;
        }
        Debug.logVerbose("CREATING CONTENTASSOC contentIdFrom(2):" + contentIdFrom, null);
        Debug.logVerbose("CREATING CONTENTASSOC contentIdTo(2):" + contentIdTo, null);

        GenericValue contentAssoc = delegator.makeValue("ContentAssoc", new HashMap());
        contentAssoc.put("contentId", contentIdFrom);
        contentAssoc.put("contentIdTo", contentIdTo);
        contentAssoc.put("contentAssocTypeId", context.get("contentAssocTypeId"));
        contentAssoc.put("contentAssocPredicateId", context.get("contentAssocPredicateIdFrom"));
        contentAssoc.put("dataSourceId", context.get("dataSourceId"));
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        if (fromDate == null) {
            contentAssoc.put("fromDate", UtilDateTime.nowTimestamp());
        } else {
            contentAssoc.set("fromDate", fromDate);
        }
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        if (thruDate == null) {
            contentAssoc.put("thruDate", null);
        } else {
            contentAssoc.set("thruDate", thruDate);
        }
        contentAssoc.set("sequenceNum", context.get("sequenceNum"));
        contentAssoc.put("mapKey", context.get("mapKey"));
        String upperCoordinateStr = (String) context.get("upperCoordinate");
        if (UtilValidate.isEmpty(upperCoordinateStr)) {
            contentAssoc.put("upperCoordinate", null);
        } else {
            contentAssoc.setString("upperCoordinate", upperCoordinateStr);
        }
        String leftCoordinateStr = (String) context.get("leftCoordinate");
        if (UtilValidate.isEmpty(leftCoordinateStr)) {
            contentAssoc.put("leftCoordinate", null);
        } else {
            contentAssoc.setString("leftCoordinate", leftCoordinateStr);
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");
        String createdByUserLogin = userLoginId;
        String lastModifiedByUserLogin = userLoginId;
        Timestamp createdDate = UtilDateTime.nowTimestamp();
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
        contentAssoc.put("createdByUserLogin", createdByUserLogin);
        contentAssoc.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
        contentAssoc.put("createdDate", createdDate);
        contentAssoc.put("lastModifiedDate", lastModifiedDate);

        String permissionStatus = null;
        Map serviceInMap = new HashMap();
        serviceInMap.put("userLogin", context.get("userLogin"));
        List targetOperations = new ArrayList();
        targetOperations.add("ASSOC_CONTENT");
        serviceInMap.put("targetOperationList", targetOperations);
        serviceInMap.put("contentPurposeList", context.get("contentPurposeList"));
        serviceInMap.put("entityOperation", context.get("entityOperation"));
        serviceInMap.put("contentIdTo", contentIdTo);
        serviceInMap.put("contentIdFrom", contentIdFrom);
        Map permResults = null;
        permResults = dispatcher.runSync("checkAssocPermission", serviceInMap);
        permissionStatus = (String) permResults.get("permissionStatus");

        //Debug.logVerbose("CREATING CONTENTASSOC:" + contentAssoc, null);
        if (permissionStatus != null && permissionStatus.equals("granted")) {
            contentAssoc.create();
        }
        result.put("contentIdTo", contentIdTo);
        result.put("contentIdFrom", contentIdFrom);
        result.put("fromDate", contentAssoc.get("fromDate"));
        result.put("contentAssocTypeId", contentAssoc.get("contentAssocTypeId"));
        return result;
    }

    /**
     * A service wrapper for the updateContentMethod method. Forces permissions to be checked.
     */
    public static Map updateContent(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = updateContentMethod(dctx, context);
        return result;
    }

    /**
     * Update a Content method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty of calling a service.
     */
    public static Map updateContentMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue dataResource = null;
        //Locale locale = (Locale) context.get("locale");
        String permissionStatus = ContentWorker.callContentPermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = (String) userLogin.get("userLoginId");
            String lastModifiedByUserLogin = userLoginId;
            Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

            // If textData exists, then create Content and return contentId
            String contentId = (String) context.get("contentId");
            try {
                dataResource = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                return ServiceUtil.returnError("dataResource.update.read_failure" + e.getMessage());
            }

            dataResource.setNonPKFields(context);
            dataResource.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
            dataResource.put("lastModifiedDate", lastModifiedDate);
            try {
                dataResource.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Update a ContentAssoc service. The work is done in a separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map updateContentAssoc(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_UPDATE");
        List targetOperations = new ArrayList();
        targetOperations.add("UPDATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = updateContentAssocMethod(dctx, context);
        return result;
    }

    /**
     * Update a ContentAssoc method. The work is done in this separate method so that complex services that need this functionality do not need to incur the
     * reflection performance penalty.
     */
    public static Map updateContentAssocMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        // This section guesses how contentId should be used (From or To) if
        // only a contentIdFrom o contentIdTo is passed in
        String contentIdFrom = (String) context.get("contentId");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentId = (String) context.get("contentId");
        String contentAssocTypeId = (String) context.get("contentAssocTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");

        GenericValue contentAssoc = null;
        try {
            contentAssoc = delegator.findByPrimaryKey("ContentAssoc", UtilMisc.toMap("contentId", contentId, "contentIdTo", contentIdTo, "contentAssocTypeId", contentAssocTypeId, "fromDate", fromDate));
        } catch (GenericEntityException e) {
            System.out.println("Entity Error:" + e.getMessage());
            return ServiceUtil.returnError("Error in retrieving Content. " + e.getMessage());
        }
        contentAssoc.put("contentAssocPredicateId", context.get("contentAssocPredicateIdFrom"));
        contentAssoc.put("dataSourceId", context.get("dataSourceId"));
        String thruDateStr = (String) context.get("thruDate");
        if (!UtilValidate.isEmpty(thruDateStr)) {
            contentAssoc.setString("thruDate", (String) context.get("thruDate"));
        }
        contentAssoc.set("sequenceNum", context.get("sequenceNum"));
        contentAssoc.put("mapKey", context.get("mapKey"));
        String upperCoordinateStr = (String) context.get("upperCoordinate");
        if (UtilValidate.isEmpty(upperCoordinateStr)) {
            contentAssoc.put("upperCoordinate", null);
        } else {
            contentAssoc.setString("upperCoordinate", upperCoordinateStr);
        }
        String leftCoordinateStr = (String) context.get("leftCoordinate");
        if (UtilValidate.isEmpty(leftCoordinateStr)) {
            contentAssoc.put("leftCoordinate", null);
        } else {
            contentAssoc.setString("leftCoordinate", leftCoordinateStr);
        }

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");
        String createdByUserLogin = userLoginId;
        String lastModifiedByUserLogin = userLoginId;
        Timestamp createdDate = UtilDateTime.nowTimestamp();
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
        contentAssoc.put("createdByUserLogin", createdByUserLogin);
        contentAssoc.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
        contentAssoc.put("createdDate", createdDate);
        contentAssoc.put("lastModifiedDate", lastModifiedDate);

        String permissionStatus = null;
        Map serviceInMap = new HashMap();
        serviceInMap.put("userLogin", context.get("userLogin"));
        List targetOperations = new ArrayList();
        targetOperations.add("ASSOC_CONTENT");
        serviceInMap.put("targetOperationList", targetOperations);
        serviceInMap.put("contentPurposeList", context.get("contentPurposeList"));
        serviceInMap.put("entityOperation", context.get("entityOperation"));
        serviceInMap.put("contentIdTo", contentIdTo);
        serviceInMap.put("contentIdFrom", contentIdFrom);
        Map permResults = null;
        try {
            permResults = dispatcher.runSync("checkAssocPermission", serviceInMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem checking permissions", "ContentServices");
            return ServiceUtil.returnError("Problem checking association permissions");
        }
        permissionStatus = (String) permResults.get("permissionStatus");

        if (permissionStatus != null && permissionStatus.equals("granted")) {
            try {
                contentAssoc.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Deactivates any active ContentAssoc (except the current one) that is associated with the passed in template/layout contentId and mapKey.
     */
    public static Map deactivateAssocs(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String contentIdTo = (String) context.get("contentIdTo");
        String mapKey = (String) context.get("mapKey");
        String contentAssocTypeId = (String) context.get("contentAssocTypeId");
        String activeContentId = (String) context.get("activeContentId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Map results = new HashMap();
        try {
            GenericValue activeAssoc =
                delegator.findByPrimaryKey("ContentAssoc", UtilMisc.toMap("contentId", activeContentId, "contentIdTo", contentIdTo, "fromDate", fromDate, "contentAssocTypeId", contentAssocTypeId));
            if (activeAssoc == null) {
                return ServiceUtil.returnError("No association found for contentId=" + activeContentId + " and contentIdTo=" + contentIdTo
                        + " and contentAssocTypeId=" + contentAssocTypeId + " and fromDate=" + fromDate);
            }
            String sequenceNum = (String) activeAssoc.get("sequenceNum");
            List exprList = new ArrayList();
            exprList.add(new EntityExpr("mapKey", EntityOperator.EQUALS, mapKey));
            if (sequenceNum != null) {
                exprList.add(new EntityExpr("sequenceNum", EntityOperator.EQUALS, sequenceNum));
            }
            exprList.add(new EntityExpr("mapKey", EntityOperator.EQUALS, mapKey));
            exprList.add(new EntityExpr("thruDate", EntityOperator.EQUALS, null));
            exprList.add(new EntityExpr("contentIdTo", EntityOperator.EQUALS, contentIdTo));
            exprList.add(new EntityExpr("contentAssocTypeId", EntityOperator.EQUALS, contentAssocTypeId));
            exprList.add(new EntityExpr("contentId", EntityOperator.NOT_EQUAL, activeContentId));
            EntityConditionList assocExprList = new EntityConditionList(exprList, EntityOperator.AND);
            List relatedAssocs = delegator.findByCondition("ContentAssoc", assocExprList, new ArrayList(), UtilMisc.toList("fromDate"));
            Iterator it = relatedAssocs.iterator();
            while (it.hasNext()) {
                GenericValue val = (GenericValue) it.next();
                val.set("thruDate", fromDate);
                val.store();
            }
            results.put("deactivatedList", relatedAssocs);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        return results;
    }

    /**
     * Get and render subcontent associated with template id and mapkey. If subContentId is supplied, that content will be rendered without searching for other
     * matching content.
     */
    public static Map renderSubContentAsText(DispatchContext dctx, Map context) {
        Map results = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map templateContext = (Map) context.get("templateContext");
        String contentId = (String) context.get("contentId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (templateContext != null && UtilValidate.isEmpty(contentId)) {
            contentId = (String) templateContext.get("contentId");
        }
        String mapKey = (String) context.get("mapKey");
        if (templateContext != null && UtilValidate.isEmpty(mapKey)) {
            mapKey = (String) templateContext.get("mapKey");
        }
        String subContentId = (String) context.get("subContentId");
        if (templateContext != null && UtilValidate.isEmpty(subContentId)) {
            subContentId = (String) templateContext.get("subContentId");
        }
        String mimeTypeId = (String) context.get("mimeTypeId");
        if (templateContext != null && UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) templateContext.get("mimeTypeId");
        }
        Locale locale = (Locale) context.get("locale");
        if (templateContext != null && locale == null) {
            locale = (Locale) templateContext.get("locale");
        }
        GenericValue subContentDataResourceView = (GenericValue) context.get("subContentDataResourceView");
        if (subContentDataResourceView != null && subContentDataResourceView == null) {
            subContentDataResourceView = (GenericValue) templateContext.get("subContentDataResourceView");
        }
        Writer out = (Writer) context.get("outWriter");

        //Debug.logVerbose("in renderSubContent(svc), contentId:" + contentId, "");
        //Debug.logVerbose("in renderSubContent(svc), subContentId:" + subContentId, "");
        //Debug.logVerbose("in renderSubContent(svc), mapKey:" + mapKey, "");
        if (templateContext == null) {
            templateContext = new HashMap();
        }

        try {
            results = ContentWorker.renderSubContentAsText(delegator, contentId, out, mapKey, subContentId, subContentDataResourceView, templateContext, locale, mimeTypeId, userLogin, fromDate);
        } catch (GeneralException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        } catch (IOException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        }
        return results;

    }

    /**
     * Get and render subcontent associated with template id and mapkey. If subContentId is supplied, that content will be rendered without searching for other
     * matching content.
     */
    public static Map renderContentAsText(DispatchContext dctx, Map context) {
        Map results = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        Writer out = (Writer) context.get("outWriter");
        Map templateContext = (Map) context.get("templateContext");
        //GenericValue userLogin = (GenericValue)context.get("userLogin");
        String contentId = (String) context.get("contentId");
        if (templateContext != null && UtilValidate.isEmpty(contentId)) {
            contentId = (String) templateContext.get("contentId");
        }
        String mimeTypeId = (String) context.get("mimeTypeId");
        if (templateContext != null && UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) templateContext.get("mimeTypeId");
        }
        Locale locale = (Locale) context.get("locale");
        if (templateContext != null && locale == null) {
            locale = (Locale) templateContext.get("locale");
        }

        if (templateContext == null) {
            templateContext = new HashMap();
        }

        GenericValue view = null;
        try {
             results = ContentWorker.renderContentAsText(delegator, contentId, out, templateContext, view, locale, mimeTypeId);
        } catch (GeneralException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        } catch (IOException e) {
            Debug.logError(e, "Error rendering sub-content text", module);
            return ServiceUtil.returnError(e.toString());
        }
        return results;
    }
}
