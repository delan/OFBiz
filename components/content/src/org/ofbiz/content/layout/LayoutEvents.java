package org.ofbiz.content.layout;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.minilang.MiniLangException;
import org.ofbiz.minilang.SimpleMapProcessor;
import org.ofbiz.content.ContentManagementWorker;

import org.apache.commons.fileupload.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LayoutEvents Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 *
 * 
 */
public class LayoutEvents {

    public static final String module = LayoutEvents.class.getName();


    public static String createLayoutImage(HttpServletRequest request, HttpServletResponse response) {

        try {
            GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
            HttpSession session = request.getSession();
            Map uploadResults = LayoutWorker.uploadImageAndParameters(request, "imageData");
            //Debug.logInfo("in createLayoutImage(java), uploadResults:" + uploadResults, "");
            Map formInput = (Map)uploadResults.get("formInput");
            Map context = new HashMap();
            ByteWrapper byteWrap = (ByteWrapper)uploadResults.get("imageData");
            if (byteWrap == null) {
                request.setAttribute("_ERROR_MESSAGE_", "Image data is null.");
                return "error";
            }
        //Debug.logInfo("in createLayoutImage, byteWrap(0):" + byteWrap, module);
            String imageFileName = (String)uploadResults.get("imageFileName");
            //Debug.logInfo("in createLayoutImage(java), context:" + context, "");

            List errorMessages = new ArrayList();
            Locale loc = null;
            try {
                SimpleMapProcessor.runSimpleMapProcessor(
                      "org/ofbiz/content/ContentManagementMapProcessors.xml", "contentIn",
                      formInput, context, errorMessages, loc);
                SimpleMapProcessor.runSimpleMapProcessor(
                      "org/ofbiz/content/ContentManagementMapProcessors.xml", "dataResourceIn",
                      formInput, context, errorMessages, loc);
                SimpleMapProcessor.runSimpleMapProcessor(
                      "org/ofbiz/content/ContentManagementMapProcessors.xml", "contentAssocIn",
                      formInput, context, errorMessages, loc);
            } catch(MiniLangException e) {
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
            context.put("dataResourceName", context.get("contentName"));
            context.put("userLogin", session.getAttribute("userLogin"));
            context.put("dataResourceTypeId", "IMAGE_OBJECT");
            context.put("contentAssocTypeId", "SUB_CONTENT");
            context.put("contentTypeId", "IMAGE");
            context.put("contentIdTo", formInput.get("contentIdTo"));
            context.put("textData", formInput.get("textData"));
            String contentPurposeTypeId = (String)formInput.get("contentPurposeTypeId");
            if (UtilValidate.isNotEmpty(contentPurposeTypeId)){
                context.put("contentPurposeTypeId", UtilMisc.toList(contentPurposeTypeId));
            }
    
            Map result = dispatcher.runSync("persistContentAndAssoc", context);
        //Debug.logInfo("in createLayoutImage, result:" + result, module);
    
            String dataResourceId = (String)result.get("dataResourceId");
            Map context2 = new HashMap();
            context2.put("activeContentId", result.get("contentId"));
            //context2.put("dataResourceId", dataResourceId);
            context2.put("contentAssocTypeId", result.get("contentAssocTypeId"));
            context2.put("fromDate", result.get("fromDate"));
    
            request.setAttribute("contentId", result.get("contentId"));
            request.setAttribute("drDataResourceId", dataResourceId);
            request.setAttribute("currentEntityName", "SubContentDataResourceId");
    
            context2.put("contentIdTo", formInput.get("contentIdTo"));
            context2.put("mapKey", formInput.get("mapKey"));
    
        //Debug.logInfo("in createLayoutImage, context2:" + context2, module);
            Map result2 = dispatcher.runSync("deactivateAssocs", context2);

            GenericValue dataResource = delegator.findByPrimaryKey("DataResource",
                          UtilMisc.toMap("dataResourceId", dataResourceId));
        //Debug.logInfo("in createLayoutImage, dataResource:" + dataResource, module);
            // Use objectInfo field to store the name of the file, since there is no
            // place in ImageDataResource for it.
            if (dataResource != null) {
                dataResource.set("objectInfo", imageFileName);
                dataResource.store();
            }

            // See if this needs to be a create or an update procedure
            GenericValue imageDataResource = delegator.findByPrimaryKey("ImageDataResource",
                          UtilMisc.toMap("dataResourceId", dataResourceId));
        //Debug.logInfo("in createLayoutImage, imageDataResource(0):" + imageDataResource, module);
            if (imageDataResource == null) {
                imageDataResource = delegator.makeValue("ImageDataResource",
                          UtilMisc.toMap("dataResourceId", dataResourceId));
                imageDataResource.set("imageData", byteWrap.getBytes());
                imageDataResource.create();
            } else {
                imageDataResource.set("imageData", byteWrap.getBytes());
                imageDataResource.store();
            }
        } catch (GenericEntityException e3) {
            request.setAttribute("_ERROR_MESSAGE_", e3.getMessage());
            return "error";
        } catch( GenericServiceException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    public static String updateLayoutImage(HttpServletRequest request, HttpServletResponse response) {

        try {
            GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
            LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
            HttpSession session = request.getSession();
            Map uploadResults = LayoutWorker.uploadImageAndParameters(request, "imageData");
            Map context = (Map)uploadResults.get("formInput");
            ByteWrapper byteWrap = (ByteWrapper)uploadResults.get("imageData");
            if (byteWrap == null) {
                request.setAttribute("_ERROR_MESSAGE_", "Image data is null.");
                return "error";
            }
            String imageFileName = (String)uploadResults.get("imageFileName");
            Debug.logInfo("in createLayoutImage(java), context:" + context, "");
            context.put("userLogin", session.getAttribute("userLogin"));
            context.put("dataResourceTypeId", "IMAGE_OBJECT");
            context.put("contentAssocTypeId", "SUB_CONTENT");
            context.put("contentTypeId", "IMAGE");
            context.put("mimeType", context.get("drMimeType"));
            context.put("drMimeType", null);
            context.put("objectInfo", context.get("drobjectInfo"));
            context.put("drObjectInfo", null);
            context.put("drDataResourceTypeId", null);
    
            String dataResourceId = (String)context.get("drDataResourceId");
            Debug.logInfo("in createLayoutImage(java), dataResourceId:" + dataResourceId, "");

            GenericValue dataResource = delegator.findByPrimaryKey("DataResource",
                          UtilMisc.toMap("dataResourceId", dataResourceId));
            Debug.logInfo("in createLayoutImage(java), dataResource:" + dataResource, "");
            // Use objectInfo field to store the name of the file, since there is no
            // place in ImageDataResource for it.
            Debug.logInfo("in createLayoutImage(java), imageFileName:" + imageFileName, "");
            if (dataResource != null) {
                //dataResource.set("objectInfo", imageFileName);
                dataResource.setNonPKFields(context);
                dataResource.store();
            }

            // See if this needs to be a create or an update procedure
            GenericValue imageDataResource = delegator.findByPrimaryKey("ImageDataResource",
                          UtilMisc.toMap("dataResourceId", dataResourceId));
            if (imageDataResource == null) {
                imageDataResource = delegator.makeValue("ImageDataResource",
                          UtilMisc.toMap("dataResourceId", dataResourceId));
                imageDataResource.set("imageData", byteWrap.getBytes());
                imageDataResource.create();
            } else {
                imageDataResource.set("imageData", byteWrap.getBytes());
                imageDataResource.store();
            }
        } catch (GenericEntityException e3) {
            request.setAttribute("_ERROR_MESSAGE_", e3.getMessage());
            return "error";
        }
        return "success";
    }

    public static String replaceSubContent(HttpServletRequest request, HttpServletResponse response) {

        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Map context = new HashMap();
        Map paramMap = UtilHttp.getParameterMap(request);
        //Debug.logInfo("in replaceSubContent, paramMap:" + paramMap, module);
        String dataResourceId = (String)paramMap.get("dataResourceId");
        if (UtilValidate.isEmpty(dataResourceId)) {
            request.setAttribute("_ERROR_MESSAGE_", "DataResourceId is null.");
            return "error";
        }
        String contentIdTo = (String)paramMap.get("contentIdTo");
        if (UtilValidate.isEmpty(contentIdTo)) {
            request.setAttribute("_ERROR_MESSAGE_", "contentIdTo is null.");
            return "error";
        }
        String mapKey = (String)paramMap.get("mapKey");

        context.put("dataResourceId", dataResourceId);
        String contentId = (String)paramMap.get("contentId");
        context.put("userLogin", session.getAttribute("userLogin"));

/*
        // If contentId is missing
        if (UtilValidate.isEmpty(contentId)) {
            // Look for an existing associated Content
            try {
                List lst = delegator.findByAnd(
                                     "DataResourceContentView ", 
                                     UtilMisc.toMap("dataResourceId", dataResourceId)); 
                if (lst.size() > 0) {
                    GenericValue dataResourceContentView  = (GenericValue)lst.get(0);
                    contentId = (String)dataResourceContentView.get("coContentId");
                }
            } catch( GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
            // Else, create and associate a Content
        } 
*/
        if (UtilValidate.isNotEmpty(contentId)) {
            context.put("contentId", contentId);
            context.put("contentIdTo", contentIdTo);
            context.put("mapKey", mapKey);
            context.put("contentAssocTypeId", "SUB_CONTENT");
    
            try {
                Map result = dispatcher.runSync("persistContentAndAssoc", context);
        //Debug.logInfo("in replaceSubContent, result:" + result, module);
                request.setAttribute("contentId", contentIdTo);
                Map context2 = new HashMap();
                context2.put("activeContentId", contentId);
                //context2.put("dataResourceId", dataResourceId);
                context2.put("contentAssocTypeId", "SUB_CONTENT");
                context2.put("fromDate", result.get("fromDate"));
        
                request.setAttribute("drDataResourceId", null);
                request.setAttribute("currentEntityName", "ContentDataResourceView");
        
                context2.put("contentIdTo", contentIdTo);
                context2.put("mapKey", mapKey);
        
                //Debug.logInfo("in replaceSubContent, context2:" + context2, module);
                Map result2 = dispatcher.runSync("deactivateAssocs", context2);
            } catch( GenericServiceException e) {
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
        }

        return "success";
    }

    public static String cloneLayout(HttpServletRequest request, HttpServletResponse response) {

        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        String contentId = request.getParameter("contentId");
        GenericValue content = null;
        GenericValue newContent = null;
        List entityList = null;
        String newId = null;
        String newDataResourceId = null;
        try {
            content = delegator.findByPrimaryKey("Content", 
                       UtilMisc.toMap("contentId", contentId));
            newContent = delegator.makeValue("Content", content);
            String oldName = (String)content.get("contentName");
            newId = delegator.getNextSeqId("Content").toString();
            newContent.set("contentId", newId);
            newDataResourceId = (String)newContent.get("dataResourceId");
            newContent.set("contentName", "Copy - " + oldName);
            newContent.create();
            //Debug.logInfo("in cloneLayout, newContent:" + newContent, "");
        } catch(GenericEntityException e) {
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
        }
        Map serviceIn = new HashMap();
        Map results = null;
        serviceIn.put("fromDate", UtilDateTime.nowTimestamp());
        serviceIn.put("contentId", contentId);
        serviceIn.put("userLogin", session.getAttribute("userLogin"));
        serviceIn.put("direction", "From");
        serviceIn.put("thruDate", null);
        serviceIn.put("assocTypes", UtilMisc.toList("SUB_CONTENT"));
        try {
            results = dispatcher.runSync("getAssocAndContentAndDataResource", serviceIn);
            entityList = (List)results.get("entityList");
            if (entityList == null || entityList.size() == 0) {
                request.setAttribute("_ERROR_MESSAGE_", "No subContent found");
                return "error";
            }
        } catch(GenericServiceException e) {
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
        }
        
        serviceIn = new HashMap();
        serviceIn.put("userLogin", session.getAttribute("userLogin"));

        // Can't count on records being unique
        Map beenThere = new HashMap();
        for (int i=0; i<entityList.size(); i++) {
            GenericValue view = (GenericValue)entityList.get(i);
            List errorMessages = new ArrayList();
            Locale loc = null;
            try {
                SimpleMapProcessor.runSimpleMapProcessor(
                      "org/ofbiz/content/ContentManagementMapProcessors.xml", "contentAssocIn",
                      view, serviceIn, errorMessages, loc);
            } catch(MiniLangException e) {
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
            String contentIdFrom = (String)view.get("contentId");
            String mapKey = (String)view.get("caMapKey");
            Timestamp fromDate = (Timestamp)view.get("caFromDate");
            Timestamp thruDate = (Timestamp)view.get("caThruDate");
            Debug.logInfo("in cloneLayout, contentIdFrom:" + contentIdFrom 
                      + " fromDate:" + fromDate
                      + " thruDate:" + thruDate
                      + " mapKey:" + mapKey
                      , "");
            if (beenThere.get(contentIdFrom) == null) {
                serviceIn.put("contentIdFrom", contentIdFrom);
                serviceIn.put("contentIdTo", newId);
                serviceIn.put("fromDate", UtilDateTime.nowTimestamp());
                serviceIn.put("thruDate", null);
                try {
                    results = dispatcher.runSync("persistContentAndAssoc", serviceIn);
                } catch(GenericServiceException e) {
                    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                    return "error";
                }
                beenThere.put(contentIdFrom, view);
            }
         
        }

        GenericValue view = delegator.makeValue("ContentDataResourceView", null);
        view.set("contentId", newId);
        view.set("drDataResourceId", newDataResourceId);
            //Debug.logInfo("in cloneLayout, view:" + view, "");
        ContentManagementWorker.setCurrentEntityMap(request, view); 
        return "success";
    }
}
