/*
 * $Id: ContentServices.java,v 1.5 2003/11/25 06:05:35 jonesde Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * ContentServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.5 $
 * @since      2.2
 *
 *
 */
public class ContentServices {

    public static final String module = ContentServices.class.getName();

    /**
     * findRelatedContent
     * Finds the related
     */
    public static Map findRelatedContent(DispatchContext dctx, Map context) {

        Map results = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue currentContent = (GenericValue) context.get("currentContent");
        String fromDate = (String) context.get("fromDate");
        String thruDate = (String) context.get("thruDate");
        String toFrom = (String) context.get("toFrom");
        if (toFrom == null)
            toFrom = "TO";
        else
            toFrom = toFrom.toUpperCase();
        List assocTypes = (List) context.get("contentAssocTypeList");
        List targetOperations = (List) context.get("targetOperationList");
        List contentList = null;
        try {
            contentList = ContentWorker.getAssociatedContent(currentContent, toFrom, assocTypes,
                    new ArrayList(), fromDate, thruDate);
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
     * This is a generic service for traversing a Content tree,
     * typical of a blog response tree.
     * It calls the ContentWorker.traverse method.
     */
    public static Map traverseContent(DispatchContext dctx, Map context) {
        HashMap results = new HashMap();

        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();

        String contentId = (String) context.get("contentId");
        String direction = (String) context.get("direction");
        if (direction != null && direction.equalsIgnoreCase("From")) {
            direction = "From";
        } else {
            direction = "To";
        }
        Debug.logInfo("contentId(start):" + contentId, null);
        if (contentId == null) contentId = "PUBLISH_ROOT";
        GenericValue content = null;
        try {
            content = delegator.findByPrimaryKey("Content",
                    UtilMisc.toMap("contentId", contentId));
        } catch (GenericEntityException e) {
            System.out.println("Entity Error:" + e.getMessage());
            return ServiceUtil.returnError("Error in retrieving Content. " + e.getMessage());
        }
        Debug.logInfo("content(start):" + content, null);
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
        if (startContentAssocTypeId != null) startContentAssocTypeId = "PUBLISH";
        Map nodeMap = new HashMap();
        List pickList = new ArrayList();
//Debug.logInfo("whenMap(start):" + whenMap,null);
        ContentWorker.traverse(delegator, content, fromDate, thruDate, whenMap, 0, nodeMap,
                startContentAssocTypeId, pickList, direction);
//Debug.logInfo("After travers",null);

        results.put("nodeMap", nodeMap);
        results.put("pickList", pickList);
        return results;
    }

    public static Map createContent(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = createContentMethod(dctx, context);
        return result;
    }

    public static Map createContentMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String contentId = (String) context.get("contentId");
        String contentTypeId = (String) context.get("contentTypeId");

        if (contentId == null) contentId = delegator.getNextSeqId("Content").toString();
        GenericValue content = delegator.makeValue("Content", UtilMisc.toMap("contentId", contentId));
        content.setNonPKFields(context);
        context.put("currentContent", content);
        String permissionStatus = ContentWorker.callContentPermissionCheck(delegator, dispatcher,
                context);
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
            try {
                content.create();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
            result.put("contentId", contentId);
        }
        return result;
    }


    public static Map createAssocContent(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = createContentAssocMethod(dctx, context);
        return result;
    }

    public static Map createContentAssocMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        // This section guesses how contentId should be used (From or To) if
        // only a contentIdFrom o contentIdTo is passed in
        String contentIdFrom = (String) context.get("contentIdFrom");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentId = (String) context.get("contentId");
        Debug.logInfo("CREATING CONTENTASSOC contentIdFrom(1):" + contentIdFrom, null);
        Debug.logInfo("CREATING CONTENTASSOC contentIdTo(1):" + contentIdTo, null);
        Debug.logInfo("CREATING CONTENTASSOC contentId:" + contentId, null);
        int contentIdCount = 0;
        if (UtilValidate.isNotEmpty(contentIdFrom)) contentIdCount++;
        if (UtilValidate.isNotEmpty(contentIdTo)) contentIdCount++;
        if (UtilValidate.isNotEmpty(contentId)) contentIdCount++;
        if (contentIdCount < 2) {
            Debug.logError("Not 2 out of ContentId/To/From.", "ContentServices");
            return ServiceUtil.returnError("Not 2 out of ContentId/To/From");
        }
        if (UtilValidate.isNotEmpty(contentIdFrom)) {
            if (UtilValidate.isEmpty(contentIdTo)) contentIdTo = contentId;
        }
        if (UtilValidate.isNotEmpty(contentIdTo)) {
            if (UtilValidate.isEmpty(contentIdFrom)) contentIdFrom = contentId;
        }

        GenericValue contentAssoc = delegator.makeValue("ContentAssoc", new HashMap());
        contentAssoc.put("contentId", contentIdFrom);
        contentAssoc.put("contentIdTo", contentIdTo);
        contentAssoc.put("contentAssocTypeId", context.get("contentAssocTypeId"));
        contentAssoc.put("contentAssocPredicateId", context.get("contentAssocPredicateIdFrom"));
        contentAssoc.put("dataSourceId", context.get("dataSourceId"));
        String fromDateStr = (String) context.get("fromDate");
        if (UtilValidate.isEmpty(fromDateStr)) {
            contentAssoc.put("fromDate", UtilDateTime.nowTimestamp());
        } else {
            contentAssoc.setString("fromDate", (String) context.get("fromDate"));
        }
        String thruDateStr = (String) context.get("thruDate");
        if (UtilValidate.isEmpty(thruDateStr)) {
            contentAssoc.put("thruDate", null);
        } else {
            contentAssoc.setString("thruDate", (String) context.get("thruDate"));
        }
        String sequenceNumStr = (String) context.get("sequenceNum");
        if (UtilValidate.isEmpty(sequenceNumStr)) {
            contentAssoc.put("sequenceNum", null);
        } else {
            contentAssoc.setString("sequenceNum", sequenceNumStr);
        }
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


        Debug.logInfo("CREATING CONTENTASSOC:" + contentAssoc, null);
        if (permissionStatus != null && permissionStatus.equals("granted")) {
            try {
                contentAssoc.create();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        result.put("contentIdTo", contentIdTo);
        result.put("contentIdFrom", contentIdFrom);
        return result;
    }


    /**
     * A service wrapper for the updateContentMethod method.
     * Forces permissions to be checked.
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

    public static Map updateContentMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue dataResource = null;
        Locale locale = (Locale) context.get("locale");
        String permissionStatus = ContentWorker.callContentPermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = (String) userLogin.get("userLoginId");
            String lastModifiedByUserLogin = userLoginId;
            Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

            // If textData exists, then create Content and return contentId
            String contentId = (String) context.get("contentId");
            try {
                dataResource = delegator.findByPrimaryKey("Content",
                        UtilMisc.toMap("contentId", contentId));
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

    public static Map updateAssocContent(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_UPDATE");
        List targetOperations = new ArrayList();
        targetOperations.add("UPDATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = updateContentAssocMethod(dctx, context);
        return result;
    }

    public static Map updateContentAssocMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        // This section guesses how contentId should be used (From or To) if
        // only a contentIdFrom o contentIdTo is passed in
        String contentIdFrom = (String) context.get("contentId");
        String contentIdTo = (String) context.get("contentIdTo");
        String contentId = (String) context.get("contentId");

        GenericValue contentAssoc = null;
        try {
            contentAssoc = delegator.findByPrimaryKey("ContentAssoc",
                    UtilMisc.toMap("contentId", contentId, "contentIdTo", contentIdTo));
        } catch (GenericEntityException e) {
            System.out.println("Entity Error:" + e.getMessage());
            return ServiceUtil.returnError("Error in retrieving Content. " + e.getMessage());
        }
        contentAssoc.put("contentAssocTypeId", context.get("contentAssocTypeId"));
        contentAssoc.put("contentAssocPredicateId", context.get("contentAssocPredicateIdFrom"));
        contentAssoc.put("dataSourceId", context.get("dataSourceId"));
        String fromDateStr = (String) context.get("fromDate");
        if (UtilValidate.isEmpty(fromDateStr)) {
        } else {
            contentAssoc.setString("fromDate", (String) context.get("fromDate"));
        }
        String thruDateStr = (String) context.get("thruDate");
        if (UtilValidate.isEmpty(thruDateStr)) {
        } else {
            contentAssoc.setString("thruDate", (String) context.get("thruDate"));
        }
        String sequenceNumStr = (String) context.get("sequenceNum");
        if (UtilValidate.isEmpty(sequenceNumStr)) {
            contentAssoc.put("sequenceNum", null);
        } else {
            contentAssoc.setString("sequenceNum", sequenceNumStr);
        }
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
        String updatedByUserLogin = userLoginId;
        String lastModifiedByUserLogin = userLoginId;
        Timestamp updatedDate = UtilDateTime.nowTimestamp();
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
        contentAssoc.put("updatedByUserLogin", updatedByUserLogin);
        contentAssoc.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
        contentAssoc.put("updatedDate", updatedDate);
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

}


