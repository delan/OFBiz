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
 * @version    $Revision: 1.9 $
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

        //Debug.logVerbose("in getSubContent(svc), contentId:" + contentId, "");
        //Debug.logVerbose("in getSubContent(svc), subContentId:" + subContentId, "");
        //Debug.logVerbose("in getSubContent(svc), mapKey:" + mapKey, "");
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
    Debug.logVerbose("in addMostRecentEntity(svc2), request:" +request, "");
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

        //Debug.logVerbose("CREATING CONTENTANDASSOC:" + context, null);
        HashMap result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map permContext = new HashMap();

        List contentPurposeList = (List)context.get("contentPurposeList");
        if (Debug.infoOn()) Debug.logInfo("in persist... contentPurposeList(0):" + contentPurposeList, null);
        GenericValue content = delegator.makeValue("Content", null);
        content.setPKFields(context);
        content.setNonPKFields(context);
        String contentId = (String)content.get("contentId");
        String contentTypeId = (String)content.get("contentTypeId");
        String origContentId = (String)content.get("contentId");
        String origDataResourceId = (String)content.get("dataResourceId");
        String origContentTypeId = (String)content.get("contentTypeId");


        GenericValue dataResource = delegator.makeValue("DataResource", null);
        dataResource.setPKFields(context);
        dataResource.setNonPKFields(context);
        String dataResourceId = (String)dataResource.get("dataResourceId");
        String dataResourceTypeId = (String)dataResource.get("dataResourceTypeId");

        GenericValue electronicText = delegator.makeValue("ElectronicText", null);
        electronicText.setPKFields(context);
        electronicText.setNonPKFields(context);
        String textData = (String)electronicText.get("textData");

        ByteWrapper byteWrapper = (ByteWrapper)context.get("imageData");

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

        boolean dataResourceExists = true;
        if (UtilValidate.isNotEmpty(dataResourceTypeId) ) {
                if (UtilValidate.isEmpty(dataResourceId)) {
                    dataResourceExists = false;
                    context.put("skipPermissionCheck", "granted"); // TODO: a temp hack because I don't want to bother with DataResource permissions at this time.
                    Map thisResult = DataServices.createDataResourceMethod(dctx, context);
                    dataResourceId = (String)thisResult.get("dataResourceId");
                    dataResource = (GenericValue)thisResult.get("dataResource");
                    if (dataResourceTypeId.indexOf("_FILE") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        context.put("dataResource", dataResource);
                        try {
                            thisResult = DataServices.createFileMethod(dctx, context);
                        } catch(GenericServiceException e) {
                            Debug.logVerbose("in persistContentAndAssoc. " + e.getMessage(),"");
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    } else if (dataResourceTypeId.equals("IMAGE_OBJECT")) {
                        if (byteWrapper != null) {
                            context.put("dataResourceId", dataResourceId);
                            thisResult = DataServices.createImageMethod(dctx, context);
                        } else {
                            return ServiceUtil.returnError("'byteWrapper' empty when trying to create database image.");
                        }
                    } else if (dataResourceTypeId.equals("SHORT_TEXT")) {
                    } else {
                        if (UtilValidate.isNotEmpty(textData)) {
                            context.put("dataResourceId", dataResourceId);
                            thisResult = DataServices.createElectronicTextMethod(dctx, context);
                        } else {
                            return ServiceUtil.returnError("'textData' empty when trying to create database text.");
                        }
                    }
                //Debug.logVerbose("dataResourceId(create):" + dataResourceId, null);
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
                    } else if (dataResourceTypeId.equals("IMAGE_OBJECT")) {
                        thisResult = DataServices.updateImageMethod(dctx, context);
                    } else if (dataResourceTypeId.equals("SHORT_TEXT")) {
                    } else {
                        thisResult = DataServices.updateElectronicTextMethod(dctx, context);
                    }
                }
                result.put("dataResourceId", dataResourceId);
                context.put("dataResourceId", dataResourceId);
        }

        // Do update and create permission checks on Content if warranted.

        boolean contentExists = true;
        if (UtilValidate.isNotEmpty(contentTypeId) ) {
            if (UtilValidate.isEmpty(contentId)) 
                contentExists = false;
            else {
                try {
                    GenericValue val = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
                    if (val == null)
                        contentExists = false;
                } catch(GenericEntityException e) {
                    return ServiceUtil.returnError(e.getMessage());
                }
            }
            //List targetOperations = new ArrayList();
            //context.put("targetOperations", targetOperations);
            if (contentExists) {
                //targetOperations.add("CONTENT_UPDATE");
                Map thisResult = ContentServices.updateContentMethod(dctx, context);
                boolean isError = ModelService.RESPOND_ERROR.equals(thisResult.get(ModelService.RESPONSE_MESSAGE));
                if (isError) 
                    return ServiceUtil.returnError( (String)thisResult.get(ModelService.ERROR_MESSAGE));
            } else {
                //targetOperations.add("CONTENT_CREATE");
                Map thisResult = ContentServices.createContentMethod(dctx, context);
                boolean isError = ModelService.RESPOND_ERROR.equals(thisResult.get(ModelService.RESPONSE_MESSAGE));
                if (isError) 
                    return ServiceUtil.returnError( (String)thisResult.get(ModelService.ERROR_MESSAGE));

                contentId = (String)thisResult.get("contentId");
            }
            result.put("contentId", contentId);
            context.put("contentId", contentId);

        

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
            Debug.logVerbose("CREATING contentASSOC contentAssocTypeId:" +  contentAssocTypeId, null);
        if (contentAssocTypeId != null && contentAssocTypeId.length() > 0 ) {
            Debug.logVerbose("CREATING contentASSOC context:" +  context, null);
            Map thisResult = null;
            try {
                thisResult = ContentServices.createContentAssocMethod(dctx, context);
            } catch (GenericEntityException e) {
                throw new GenericServiceException(e.getMessage());
            } catch (Exception e2) {
                throw new GenericServiceException(e2.getMessage());
            }
            boolean isError = ModelService.RESPOND_ERROR.equals(thisResult.get(ModelService.RESPONSE_MESSAGE));
            if (isError) 
                return ServiceUtil.returnError( (String)thisResult.get(ModelService.ERROR_MESSAGE));

            result.put("contentIdTo", thisResult.get("contentIdTo"));
            result.put("contentIdFrom", thisResult.get("contentIdFrom"));
            result.put("contentAssocTypeId", thisResult.get("contentAssocTypeId"));
            result.put("fromDate", thisResult.get("fromDate"));
       }
            //Debug.logVerbose("return from CREATING CONTENTASSOC result:" +  result, null);
       context.remove("skipPermissionCheck");
       context.put("contentId", origContentId);
       context.put("dataResourceId", origDataResourceId);
       context.remove("dataResource");
       return result;
    }
}
