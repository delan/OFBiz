package org.ofbiz.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericPK;
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
import org.ofbiz.content.ContentManagementWorker;
import org.ofbiz.content.content.ContentServices;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.content.content.ContentWorker;

/**
 * ContentManagementServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.5 $
 * @since      3.0
 *
 * 
 */
public class ContentManagementServices {

    public static final String module = ContentManagementServices.class.getName();

    /**
     * getSubContent
     * Finds the related subContent given the template Content and the mapKey.
     * This service calls a same-named method in ContentWorker to do the work.
     */
    public static Map getSubContent(DispatchContext dctx, Map context) {

        Map results = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String contentId = (String) context.get("contentId"); 
        String subContentId = (String) context.get("subContentId"); 
        String mapKey = (String) context.get("mapKey"); 
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Timestamp fromDate = (Timestamp)context.get("fromDate");
        List assocTypes = (List) context.get("assocTypes"); 
        GenericValue content = null;
        GenericValue view = null;

        //Debug.logInfo("in getSubContent(svc), contentId:" + contentId, "");
        //Debug.logInfo("in getSubContent(svc), subContentId:" + subContentId, "");
        //Debug.logInfo("in getSubContent(svc), mapKey:" + mapKey, "");
        try {
            view = ContentWorker.getSubContent( delegator, 
                          contentId, mapKey, subContentId, userLogin, assocTypes, fromDate);
            content = ContentWorker.getContentFromView(view);
        } catch(IOException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        results.put("view", view);
        results.put("content", content);
   

        return results;

    }

    /**
     * addMostRecent
     * A service for adding the most recently used of an entity class to the cache.
     * Entities make it to the most recently used list primarily by being selected for editing,
     * either by being created or being selected from a list.
     */
    public static Map addMostRecent(DispatchContext dctx, Map context) {

        Map results = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        HttpServletRequest request = (HttpServletRequest)context.get("request");  
    Debug.logInfo("in addMostRecentEntity(svc2), request:" +request, "");
        String suffix = (String) context.get("suffix"); 
        GenericValue val = (GenericValue)context.get("pk");
        GenericPK pk = val.getPrimaryKey();
        HttpSession session = (HttpSession)context.get("session");

        ContentManagementWorker.mruAdd(session, pk, suffix);
        return results;

    }


    /**
     * persistContentAndAssoc
     * A combination method that will create or update all or one of the following
     * a Content entity, a ContentAssoc related to the Content and 
     * the ElectronicText that may be associated with the Content.
     * The keys for determining if each entity is created is the presence
     * of the contentTypeId, contentAssocTypeId and dataResourceTypeId.
     */
    public static Map persistContentAndAssoc(DispatchContext dctx, Map context) throws GenericServiceException{

        //Debug.logInfo("CREATING CONTENTANDASSOC:" + context, null);
        HashMap result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map permContext = new HashMap();

        GenericValue content = delegator.makeValue("Content", null);
        content.setPKFields(context);
        content.setNonPKFields(context);
        String contentId = (String)content.get("contentId");
        String contentTypeId = (String)content.get("contentTypeId");
        String origDataResourceId = (String)content.get("dataResourceId");


        GenericValue dataResource = delegator.makeValue("DataResource", null);
        dataResource.setPKFields(context);
        dataResource.setNonPKFields(context);
        String dataResourceId = (String)dataResource.get("dataResourceId");
        String dataResourceTypeId = (String)dataResource.get("dataResourceTypeId");

        GenericValue electronicText = delegator.makeValue("ElectronicText", null);
        electronicText.setPKFields(context);
        electronicText.setNonPKFields(context);
        String textData = (String)electronicText.get("textData");

        // get user info for multiple use
        GenericValue userLogin = (GenericValue) context.get("userLogin"); 
        String userLoginId = (String)userLogin.get("userLoginId");
        String createdByUserLogin = userLoginId;
        String lastModifiedByUserLogin = userLoginId;
        Timestamp createdDate = UtilDateTime.nowTimestamp();
        Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

        // Do update and create permission checks on DataResource if warranted.
        boolean updatePermOK = false;
        boolean createPermOK = false;
        if (UtilValidate.isNotEmpty(dataResourceTypeId) 
           || UtilValidate.isNotEmpty(textData) ) {
            List targetOperations = new ArrayList();
            if (UtilValidate.isNotEmpty(dataResourceId) ) {
                permContext.put("entityOperation", "_UPDATE");
                targetOperations.add("UPDATE_CONTENT");
                updatePermOK = true;
            } else {
                permContext.put("entityOperation", "_CREATE");
                targetOperations.add("CREATE_CONTENT");
                createPermOK = true;
            }
            permContext.put("targetOperationList", targetOperations);
            permContext.put("contentPurposeList", context.get("contentPurposeList"));
            permContext.put("userLogin", userLogin);
            String permissionStatus = ContentWorker.callContentPermissionCheck(delegator,
                                     dispatcher, permContext);

            Debug.logInfo("permissionStatus(update):" + permissionStatus, null);
            if (permissionStatus == null || !permissionStatus.equals("granted") ) {
                return ServiceUtil.returnError("Permission not granted");
            }
            context.put("skipPermissionCheck", "granted");
        }

        boolean dataResourceExists = true;
        if (UtilValidate.isNotEmpty(dataResourceTypeId) ) {
                if (UtilValidate.isEmpty(dataResourceId)) {
                    dataResourceExists = false;
                    Map thisResult = DataServices.createDataResourceMethod(dctx, context);
                    dataResourceId = (String)thisResult.get("dataResourceId");
                    dataResource = (GenericValue)thisResult.get("dataResource");
                    if (dataResourceTypeId.indexOf("_FILE") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        context.put("dataResource", dataResource);
                        try {
                            thisResult = DataServices.createFileMethod(dctx, context);
                        } catch(GenericServiceException e) {
                            Debug.logInfo("in persistContentAndAssoc. " + e.getMessage(),"");
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    } else {
                        if (UtilValidate.isNotEmpty(textData)) {
                            context.put("dataResourceId", dataResourceId);
                            thisResult = DataServices.createElectronicTextMethod(dctx, context);
                        } else {
                            return ServiceUtil.returnError("'textData' empty when trying to create database text.");
                        }
                    }
                //Debug.logInfo("dataResourceId(create):" + dataResourceId, null);
                } else {
                    Map thisResult = DataServices.updateDataResourceMethod(dctx, context);
                    if (dataResourceTypeId.indexOf("_FILE") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        context.put("dataResource", dataResource);
                        try {
                            thisResult = DataServices.updateFileMethod(dctx, context);
                        } catch(GenericServiceException e) {
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    } else {
                        thisResult = DataServices.updateElectronicTextMethod(dctx, context);
                    }
                }
                result.put("dataResourceId", dataResourceId);
                context.put("dataResourceId", dataResourceId);
        }

        // Do update and create permission checks on Content if warranted.
        if (UtilValidate.isNotEmpty(contentTypeId)) {
            String permissionStatus = "granted";
            List targetOperations = new ArrayList();
            permContext.put("targetOperationList", targetOperations);
            permContext.put("contentPurposeList", context.get("contentPurposeList"));
            permContext.put("userLogin", userLogin);
            if (UtilValidate.isNotEmpty(dataResourceId) ) {
                permContext.put("entityOperation", "_UPDATE");
                targetOperations.add("UPDATE_CONTENT");
                if (!updatePermOK) {
                    permissionStatus = ContentWorker.callContentPermissionCheck(delegator,
                                     dispatcher, permContext);
                    updatePermOK = true;
                }
            } else {
                permContext.put("entityOperation", "_CREATE");
                targetOperations.add("CREATE_CONTENT");
                if (!createPermOK) {
                    permissionStatus = ContentWorker.callContentPermissionCheck(delegator,
                                     dispatcher, permContext);
                    createPermOK = true;
                }
            }

            Debug.logInfo("permissionStatus(update):" + permissionStatus, null);
            if (permissionStatus == null || !permissionStatus.equals("granted") ) {
                return ServiceUtil.returnError("Permission not granted");
            }
            context.put("skipPermissionCheck", "granted");
        }

        boolean contentExists = true;
        if (UtilValidate.isNotEmpty(contentTypeId) ) {
            if (UtilValidate.isEmpty(contentId)) {
                contentExists = false;
                Map thisResult = ContentServices.createContentMethod(dctx, context);
                contentId = (String)thisResult.get("contentId");
            //Debug.logInfo("contentId(create):" + contentId, null);
            } else {
                Map thisResult = ContentServices.updateContentMethod(dctx, context);
            //Debug.logInfo("contentId(update):" + contentId, null);
            }
            result.put("contentId", contentId);
            context.put("contentId", contentId);

        

            List contentPurposeList = (List)context.get("contentPurposeList");
            // Add ContentPurposes if this is a create operation
            if (contentId != null && !contentExists) {
                try {
                    if (contentPurposeList != null) {
                        for (int i=0; i < contentPurposeList.size(); i++) {
                            String contentPurposeTypeId = (String)contentPurposeList.get(i);
                            GenericValue contentPurpose = delegator.makeValue("ContentPurpose",
                                   UtilMisc.toMap("contentId", contentId, 
                                                  "contentPurposeTypeId", contentPurposeTypeId) );
                            contentPurpose.create();
                        }
                    }
                } catch(GenericEntityException e) {
                    return ServiceUtil.returnError(e.getMessage());
                }
            }

        }

        // If parentContentIdTo or parentContentIdFrom exists, create association with newly created content
        String contentAssocTypeId = (String)context.get("contentAssocTypeId");
            Debug.logInfo("CREATING contentASSOC contentAssocTypeId:" +  contentAssocTypeId, null);
        if (contentAssocTypeId != null && contentAssocTypeId.length() > 0 ) {
            Debug.logInfo("CREATING contentASSOC context:" +  context, null);
            Map thisResult = null;
            try {
                thisResult = ContentServices.createContentAssocMethod(dctx, context);
            } catch (GenericEntityException e) {
                throw new GenericServiceException(e.getMessage());
            } catch (Exception e2) {
                throw new GenericServiceException(e2.getMessage());
            }
            result.put("contentIdTo", thisResult.get("contentIdTo"));
            result.put("contentIdFrom", thisResult.get("contentIdFrom"));
            result.put("contentAssocTypeId", thisResult.get("contentAssocTypeId"));
            result.put("fromDate", thisResult.get("fromDate"));
       }
            //Debug.logInfo("return from CREATING CONTENTASSOC result:" +  result, null);
       context.remove("skipPermissionCheck");
       context.remove("contentId");
       context.remove("dataResourceId");
       context.remove("dataResource");
       return result;
    }
}
