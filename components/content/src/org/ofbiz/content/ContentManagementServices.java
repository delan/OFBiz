/*
 * $Id$
 *
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentServices;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;


/**
 * ContentManagementServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
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

        try {
            view = ContentWorker.getSubContentCache( delegator, contentId, mapKey, subContentId, userLogin, assocTypes, fromDate, new Boolean(false), null);
            content = ContentWorker.getContentFromView(view);
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        results.put("view", view);
        results.put("content", content);
   

        return results;

    }

    /**
     * getContent
     * This service calls a same-named method in ContentWorker to do the work.
     */
    public static Map getContent(DispatchContext dctx, Map context) {

        Map results = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        String contentId = (String) context.get("contentId"); 
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        GenericValue view = null;

        try {
            view = ContentWorker.getContentCache( delegator, contentId);
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        results.put("view", view);

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
        String suffix = (String) context.get("suffix"); 
        GenericValue val = (GenericValue)context.get("pk");
        GenericPK pk = val.getPrimaryKey();
        HttpSession session = (HttpSession)context.get("session");

        ContentManagementWorker.mruAdd(session, pk);
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

        HashMap result = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map permContext = new HashMap();
        String mapKey = (String) context.get("mapKey"); 
        String deactivateExisting = (String) context.get("deactivateExisting"); 
        if (UtilValidate.isEmpty(deactivateExisting)) {
            if (UtilValidate.isEmpty(mapKey)) 
                deactivateExisting = "false";
            else 
                deactivateExisting = "true";
        }
        if (Debug.infoOn()) Debug.logInfo("in persist... mapKey(0):" + mapKey, null);

        List contentPurposeList = (List)context.get("contentPurposeList");
        //if (Debug.infoOn()) Debug.logInfo("in persist... contentPurposeList(0):" + contentPurposeList, null);
        if (Debug.infoOn()) Debug.logInfo("in persist... textData(0):" + context.get("textData"), null);

        GenericValue content = delegator.makeValue("Content", null);
        content.setPKFields(context);
        content.setNonPKFields(context);
        String contentId = (String)content.get("contentId");
        String contentTypeId = (String)content.get("contentTypeId");
        String origContentId = (String)content.get("contentId");
        String origDataResourceId = (String)content.get("dataResourceId");
        String origContentTypeId = (String)content.get("contentTypeId");
        if (Debug.infoOn()) Debug.logInfo("in persist... contentId(0):" + contentId, null);


        GenericValue dataResource = delegator.makeValue("DataResource", null);
        dataResource.setPKFields(context);
        dataResource.setNonPKFields(context);
        dataResource.setAllFields(context, false, "dr", null);
        context.putAll(dataResource);
        String dataResourceId = (String)dataResource.get("dataResourceId");
        String dataResourceTypeId = (String)dataResource.get("dataResourceTypeId");
        if (Debug.infoOn()) Debug.logInfo("in persist... dataResourceId(0):" + dataResourceId, null);

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

        boolean dataResourceExists = true;
        if (Debug.infoOn()) Debug.logInfo("in persist... dataResourceTypeId(0):" + dataResourceTypeId, null);
        if (UtilValidate.isNotEmpty(dataResourceTypeId) ) {
                context.put("skipPermissionCheck", "granted"); // TODO: a temp hack because I don't want to bother with DataResource permissions at this time.
                if (UtilValidate.isEmpty(dataResourceId)) {
                    dataResourceExists = false;
                } else {
                	try {
                    	GenericValue val = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
                    	if (val == null)
                        	dataResourceExists = false;
                	} catch(GenericEntityException e) {
                    	return ServiceUtil.returnError(e.getMessage());
                	}
            	}
                if (!dataResourceExists) {
                    Map thisResult = DataServices.createDataResourceMethod(dctx, context);
                    String errorMsg = ServiceUtil.getErrorMessage(thisResult);
                    if (UtilValidate.isNotEmpty(errorMsg)) {
                            return ServiceUtil.returnError(errorMsg);
                    }
                    dataResourceId = (String)thisResult.get("dataResourceId");
                    if (Debug.infoOn()) Debug.logInfo("in persist... dataResourceId(0):" + dataResourceId, null);
                    dataResource = (GenericValue)thisResult.get("dataResource");
                    if ( dataResourceTypeId.indexOf("_FILE") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        context.put("dataResource", dataResource);
                        ByteWrapper byteWrapper = (ByteWrapper)context.get("imageData");
                        if (byteWrapper != null) 
                            context.put("binData", byteWrapper);
                        thisResult = DataServices.createFileMethod(dctx, context);
                        errorMsg = ServiceUtil.getErrorMessage(thisResult);
                        if (UtilValidate.isNotEmpty(errorMsg)) {
                            return ServiceUtil.returnError(errorMsg);
                        }
                    } else if (dataResourceTypeId.equals("IMAGE_OBJECT")) {
                        ByteWrapper byteWrapper = (ByteWrapper)context.get("imageData");
                        if (byteWrapper != null) {
                            context.put("dataResourceId", dataResourceId);
                            thisResult = DataServices.createImageMethod(dctx, context);
                        } else {
                            return ServiceUtil.returnError("'byteWrapper' empty when trying to create database image.");
                        }
                    } else if (dataResourceTypeId.equals("SHORT_TEXT")) {
                    } else {
                        // assume ELECTRONIC_TEXT
                        if (UtilValidate.isNotEmpty(textData)) {
                            context.put("dataResourceId", dataResourceId);
                            thisResult = DataServices.createElectronicTextMethod(dctx, context);
                            errorMsg = ServiceUtil.getErrorMessage(thisResult);
                        	if (UtilValidate.isNotEmpty(errorMsg)) {
                            	return ServiceUtil.returnError(errorMsg);
                        	}
                        }
                    }
                } else {
                    Map newDrContext = new HashMap();
                    newDrContext.putAll(dataResource);
                    newDrContext.put("userLogin", userLogin);
                    newDrContext.put("skipPermissionCheck", context.get("skipPermissionCheck"));
                    Map thisResult = dispatcher.runSync("updateDataResource", newDrContext);
                    String errMsg = ServiceUtil.getErrorMessage(thisResult);
        			if (UtilValidate.isNotEmpty(errMsg)) {
            			return ServiceUtil.returnError(errMsg);
        			}
                    //Map thisResult = DataServices.updateDataResourceMethod(dctx, context);
                    if (Debug.infoOn()) Debug.logInfo("in persist... thisResult.permissionStatus(0):" + thisResult.get("permissionStatus"), null);
                        //thisResult = DataServices.updateElectronicTextMethod(dctx, context);
                    if (dataResourceTypeId.indexOf("_FILE") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        ByteWrapper byteWrapper = (ByteWrapper)context.get("imageData");
                        if (byteWrapper != null) 
                            context.put("binData", byteWrapper);
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
                        Map newContext = new HashMap();
                        newContext.put("dataResourceId", dataResourceId);
                        newContext.put("textData", textData);
                        newContext.put("userLogin", userLogin);
                        newContext.put("skipPermissionCheck", context.get("skipPermissionCheck"));
                        thisResult = dispatcher.runSync("updateElectronicText", newContext);
                    }
                    errMsg = ServiceUtil.getErrorMessage(thisResult);
        			if (UtilValidate.isNotEmpty(errMsg)) {
            			Debug.logError(errMsg, module);
            			return ServiceUtil.returnError(errMsg);
        			}
                }

                result.put("dataResourceId", dataResourceId);
                context.put("dataResourceId", dataResourceId);
                context.put("drDataResourceId", dataResourceId);
        }
        // Do update and create permission checks on Content if warranted.

        context.put("skipPermissionCheck", null);  // Force check here
        boolean contentExists = true;
            if (Debug.infoOn()) Debug.logInfo("in persist... contentTypeId" +  contentTypeId + " dataResourceTypeId:" + dataResourceTypeId + " contentId:" + contentId + " dataResourceId:" + dataResourceId, null);
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
            context.putAll(content);
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
            context.put("caContentId", contentId);

        

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

        } else if (UtilValidate.isNotEmpty(dataResourceTypeId) && UtilValidate.isNotEmpty(contentId)) {
            if (UtilValidate.isNotEmpty(dataResourceId)) {
                context.put("dataResourceId", dataResourceId);
                if (Debug.infoOn()) Debug.logInfo("in persist... context:" + context, module);
                Map r = ContentServices.updateContentMethod(dctx, context);
                boolean isError = ModelService.RESPOND_ERROR.equals(r.get(ModelService.RESPONSE_MESSAGE));
                if (isError) 
                    return ServiceUtil.returnError( (String)r.get(ModelService.ERROR_MESSAGE));
            }
        }

        // If parentContentIdTo or parentContentIdFrom exists, create association with newly created content
        String contentAssocTypeId = (String)context.get("contentAssocTypeId");
        if (UtilValidate.isEmpty(contentAssocTypeId)) 
            contentAssocTypeId = (String)context.get("caContentAssocTypeId");

        if (Debug.infoOn()) Debug.logInfo("CREATING contentASSOC contentAssocTypeId:" +  contentAssocTypeId, null);
        if (contentAssocTypeId != null && contentAssocTypeId.length() > 0 ) {
            if (Debug.infoOn()) Debug.logInfo("in persistContentAndAssoc, deactivateExistin:" +  deactivateExisting, null);
            context.put("deactivateExisting", deactivateExisting);
            Map thisResult = null;
            try {
                GenericValue contentAssoc = delegator.makeValue("ContentAssoc", null);
                GenericValue contentAssocPK = delegator.makeValue("ContentAssoc", null);
                contentAssoc.setAllFields(context, false, "ca", null);
                contentAssocPK.setAllFields(context, false, "ca", new Boolean(true));
                context.putAll(contentAssoc);
                GenericValue contentAssocExisting = null;
                if (contentAssocPK.isPrimaryKey())
                    contentAssocExisting = delegator.findByPrimaryKeyCache("ContentAssoc", contentAssocPK);
                if (contentAssocExisting == null)
                    thisResult = ContentServices.createContentAssocMethod(dctx, context);
                else
                    thisResult = ContentServices.updateContentAssocMethod(dctx, context);
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
            result.put("contentId", thisResult.get("contentIdFrom"));
            result.put("contentAssocTypeId", thisResult.get("contentAssocTypeId"));
            result.put("fromDate", thisResult.get("fromDate"));
            
            result.put("caContentIdTo", thisResult.get("contentIdTo"));
            result.put("caContentAssocTypeId", thisResult.get("contentAssocTypeId"));
            result.put("caFromDate", thisResult.get("fromDate"));
       }
       context.remove("skipPermissionCheck");
       context.put("contentId", origContentId);
       context.put("dataResourceId", origDataResourceId);
       context.remove("dataResource");
       Debug.logInfo("result:" + result, module);
       return result;
    }

    /**
    Service for update publish sites with a ContentRole that will tie them to the passed 
    in party. 
   */
  public static Map updateSiteRoles(DispatchContext dctx, Map context) {

      LocalDispatcher dispatcher = dctx.getDispatcher();
      GenericDelegator delegator = dctx.getDelegator();
      GenericValue userLogin = (GenericValue)context.get("userLogin");
      String userLoginPartyId = userLogin.getString("partyId");
      Map results = new HashMap();
      // siteContentId will equal "ADMIN_MASTER", "AGINC_MASTER", etc.
      // Remember that this service is called in the "multi" mode,
      // with a new siteContentId each time.
      // siteContentId could also have been name deptContentId, since this same
      // service is used for updating department roles, too.
      String siteContentId = (String)context.get("contentId");
      String partyId = (String)context.get("partyId");

      if (UtilValidate.isEmpty(siteContentId) || UtilValidate.isEmpty(partyId))
          return results;

      //Debug.logInfo("updateSiteRoles, context(0):" + context, module);

      List siteRoles = null;
      try {
          siteRoles = delegator.findByAndCache("RoleType", UtilMisc.toMap("parentTypeId", "BLOG"));
      } catch(GenericEntityException e) {
          return ServiceUtil.returnError( e.getMessage());
      }
        
      Iterator siteRoleIter = siteRoles.iterator();
      while (siteRoleIter.hasNext()) {
          Map serviceContext = new HashMap();
          serviceContext.put("partyId", partyId);
          serviceContext.put("contentId", siteContentId);
          serviceContext.put("userLogin", userLogin);
          Debug.logInfo("updateSiteRoles, serviceContext(0):" + serviceContext, module);
            GenericValue roleType = (GenericValue)siteRoleIter.next();
          String siteRole = (String)roleType.get("roleTypeId"); // BLOG_EDITOR, BLOG_ADMIN, etc.
          String cappedSiteRole = ModelUtil.dbNameToVarName(siteRole);
          if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, cappediteRole(1):" + cappedSiteRole, module);

          String siteRoleVal = (String)context.get(cappedSiteRole);
          if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, siteRoleVal(1):" + siteRoleVal, module);
          if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, context(1):" + context, module);
          Object fromDate = context.get(cappedSiteRole + "FromDate");
          if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, fromDate(1):" + fromDate, module);
          serviceContext.put("roleTypeId", siteRole);
          if (siteRoleVal != null && siteRoleVal.equalsIgnoreCase("Y")) {
                  // for now, will assume that any error is due to duplicates - ignore
                  //return ServiceUtil.returnError(e.getMessage());
              if (fromDate == null ) {
                  try {
                      Map newContext = new HashMap();
                      newContext.put("contentId", serviceContext.get("contentId"));
                      newContext.put("partyId", serviceContext.get("partyId"));
                      newContext.put("roleTypeId", serviceContext.get("roleTypeId"));
                      newContext.put("userLogin", userLogin);
                      Map permResults = dispatcher.runSync("deactivateAllContentRoles", newContext);
                      serviceContext.put("fromDate", UtilDateTime.nowTimestamp());
                      if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, serviceContext(1):" + serviceContext, module);
                      permResults = dispatcher.runSync("createContentRole", serviceContext);
                      String errMsg = ServiceUtil.getErrorMessage(permResults);
                      if (UtilValidate.isNotEmpty(errMsg)) 
                        return ServiceUtil.returnError(errMsg);
                      //addRoleToUser(delegator, dispatcher, serviceContext);
                  } catch (GenericServiceException e) {
                      Debug.logError(e, e.getMessage(), module);
                      return ServiceUtil.returnError( e.getMessage());
                  } catch (Exception e2) {
                      Debug.logError(e2, e2.getMessage(), module);
                      return ServiceUtil.returnError( e2.getMessage());
                  }
              }
          } else {
              if (fromDate != null ) {
                      // for now, will assume that any error is due to non-existence - ignore
                      //return ServiceUtil.returnError(e.getMessage());
                  try {
Debug.logInfo("updateSiteRoles, serviceContext(2):" + serviceContext, module);
                      //Timestamp thruDate = UtilDateTime.nowTimestamp();
                      //serviceContext.put("thruDate", thruDate);
                      //serviceContext.put("fromDate", fromDate);
                      Map newContext = new HashMap();
                      newContext.put("contentId", serviceContext.get("contentId"));
                      newContext.put("partyId", serviceContext.get("partyId"));
                      newContext.put("roleTypeId", serviceContext.get("roleTypeId"));
                      newContext.put("userLogin", userLogin);
                      Map permResults = dispatcher.runSync("deactivateAllContentRoles", newContext);
                      String errMsg = ServiceUtil.getErrorMessage(permResults);
                      if (UtilValidate.isNotEmpty(errMsg)) 
                        return ServiceUtil.returnError(errMsg);
                  } catch (GenericServiceException e) {
                      Debug.logError(e, e.getMessage(), module);
                      return ServiceUtil.returnError( e.getMessage());
                  } catch (Exception e2) {
                      Debug.logError(e2, e2.getMessage(), module);
                      return ServiceUtil.returnError( e2.getMessage());
                  }
              }
          }
      }
      return results;
  }

  public static void addRoleToUser(GenericDelegator delegator, LocalDispatcher dispatcher, Map serviceContext) throws GenericServiceException, GenericEntityException {
    String partyId = (String)serviceContext.get("partyId");
    Map findMap = UtilMisc.toMap("partyId", partyId);
        List userLoginList = delegator.findByAnd("UserLogin", findMap);
        Iterator iter = userLoginList.iterator();
        while (iter.hasNext()) {
            GenericValue partyUserLogin = (GenericValue)iter.next();
            String partyUserLoginId = partyUserLogin.getString("userLoginId");
            serviceContext.put("contentId", partyUserLoginId); // author contentId
            dispatcher.runSync("createContentRole", serviceContext);
        }
}

  public static Map updateSiteRolesDyn(DispatchContext dctx, Map context) {

      LocalDispatcher dispatcher = dctx.getDispatcher();
      GenericDelegator delegator = dctx.getDelegator();
      Map results = new HashMap();
      Map serviceContext = new HashMap();
      // siteContentId will equal "ADMIN_MASTER", "AGINC_MASTER", etc.
      // Remember that this service is called in the "multi" mode,
      // with a new siteContentId each time.
      // siteContentId could also have been name deptContentId, since this same
      // service is used for updating department roles, too.
      String siteContentId = (String)context.get("contentId");
      String partyId = (String)context.get("partyId");
      serviceContext.put("partyId", partyId);
      serviceContext.put("contentId", siteContentId);
      //Debug.logInfo("updateSiteRoles, serviceContext(0):" + serviceContext, module);
      //Debug.logInfo("updateSiteRoles, context(0):" + context, module);

      List siteRoles = null;
      try {
            siteRoles = delegator.findByAndCache("RoleType", UtilMisc.toMap("parentTypeId", "BLOG"));
      } catch(GenericEntityException e) {
          return ServiceUtil.returnError( e.getMessage());
      }
      Iterator siteRoleIter = siteRoles.iterator();
      while (siteRoleIter.hasNext()) {
            GenericValue roleType = (GenericValue)siteRoleIter.next();
          String siteRole = (String)roleType.get("roleTypeId"); // BLOG_EDITOR, BLOG_ADMIN, etc.
          String cappedSiteRole = ModelUtil.dbNameToVarName(siteRole);
          //if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, cappediteRole(1):" + cappedSiteRole, module);

          String siteRoleVal = (String)context.get(cappedSiteRole);
          Object fromDate = context.get(cappedSiteRole + "FromDate");
          serviceContext.put("roleTypeId", siteRole);
          if (siteRoleVal != null && siteRoleVal.equalsIgnoreCase("Y")) {
                  // for now, will assume that any error is due to duplicates - ignore
                  //return ServiceUtil.returnError(e.getMessage());
              if (fromDate == null ) {
                  try {
                      serviceContext.put("fromDate", UtilDateTime.nowTimestamp());
                      if (Debug.infoOn()) Debug.logInfo("updateSiteRoles, serviceContext(1):" + serviceContext, module);
                      addRoleToUser(delegator, dispatcher, serviceContext);
                      Map permResults = dispatcher.runSync("createContentRole", serviceContext);
                  } catch (GenericServiceException e) {
                      Debug.logError(e, e.getMessage(), module);
                  } catch (Exception e2) {
                      Debug.logError(e2, e2.getMessage(), module);
                  }
              }
          } else {
              if (fromDate != null ) {
                      // for now, will assume that any error is due to non-existence - ignore
                      //return ServiceUtil.returnError(e.getMessage());
                  try {
Debug.logInfo("updateSiteRoles, serviceContext(2):" + serviceContext, module);
                      //Timestamp thruDate = UtilDateTime.nowTimestamp();
                      //serviceContext.put("thruDate", thruDate);
                      //serviceContext.put("fromDate", fromDate);
                      Map newContext = new HashMap();
                      newContext.put("contentId", serviceContext.get("contentId"));
                      newContext.put("partyId", serviceContext.get("partyId"));
                      newContext.put("roleTypeId", serviceContext.get("roleTypeId"));
                      Map permResults = dispatcher.runSync("deactivateAllContentRoles", newContext);
                  } catch (GenericServiceException e) {
                      Debug.logError(e, e.getMessage(), module);
                  } catch (Exception e2) {
                      Debug.logError(e2, e2.getMessage(), module);
                  }
              }
          }
      }
      return results;
  }

    public static Map updateOrRemove(DispatchContext dctx, Map context) {

        Map results = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String entityName = (String)context.get("entityName");
        String action = (String)context.get("action");
        String pkFieldCount = (String)context.get("pkFieldCount");
        Map pkFields = new HashMap();
        int fieldCount = Integer.parseInt(pkFieldCount);
        for (int i=0; i<fieldCount; i++) {
            String fieldName = (String)context.get("fieldName" + i);
            String fieldValue = (String)context.get("fieldValue" + i);
            if (UtilValidate.isEmpty(fieldValue)) {
                // It may be the case that the last row in a form is "empty" waiting for
                // someone to enter a value, in which case we do not want to throw an
                // error, we just want to ignore it.
                return results;
            }
            pkFields.put(fieldName, fieldValue);
        }
        boolean doLink = (action != null && action.equalsIgnoreCase("Y")) ? true : false;
        if (Debug.infoOn()) Debug.logInfo("in updateOrRemove, context:" + context, module);
        try {
            GenericValue entityValuePK = delegator.makeValue(entityName, pkFields);
            if (Debug.infoOn()) Debug.logInfo("in updateOrRemove, entityValuePK:" + entityValuePK, module);
            GenericValue entityValueExisting = delegator.findByPrimaryKeyCache(entityName, entityValuePK);
            if (Debug.infoOn()) Debug.logInfo("in updateOrRemove, entityValueExisting:" + entityValueExisting, module);
            if (entityValueExisting == null) {
                if (doLink) {
                    entityValuePK.create();
                    if (Debug.infoOn()) Debug.logInfo("in updateOrRemove, entityValuePK: CREATED", module);
                }
            } else {
                if (!doLink) {
                    entityValueExisting.remove();
                    if (Debug.infoOn()) Debug.logInfo("in updateOrRemove, entityValueExisting: REMOVED", module);
                }
            }
            
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        return results; 
    }
    
    public static Map resequence(DispatchContext dctx, Map context) throws GenericServiceException{

        HashMap result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        String contentIdTo = (String)context.get("contentIdTo");
        Integer seqInc = (Integer)context.get("seqInc");
        if (seqInc == null)
            seqInc = new Integer(100);
        int seqIncrement = seqInc.intValue();
        List typeList = (List)context.get("typeList");
        if (typeList == null)
            typeList = UtilMisc.toList("PUBLISH_LINK", "SUB_CONTENT");
        List condList = new ArrayList();
        Iterator iterType = typeList.iterator();
        while (iterType.hasNext()) {
            String type = (String)iterType.next();
            condList.add(new EntityExpr("contentAssocTypeId", EntityOperator.EQUALS, type));
        }
        
        EntityCondition conditionType = new EntityConditionList(condList, EntityOperator.OR);
        EntityCondition conditionMain = new EntityConditionList(UtilMisc.toList( new EntityExpr("contentIdTo", EntityOperator.EQUALS, contentIdTo), conditionType), EntityOperator.AND);
         try {
             List listAll = delegator.findByConditionCache("ContentAssoc", conditionMain, null, UtilMisc.toList("sequenceNum", "fromDate", "createdDate"));
             List listFiltered = EntityUtil.filterByDate(listAll);
             Iterator iter = listFiltered.iterator();
             int seqNum = seqIncrement;
             while (iter.hasNext()) {
                 GenericValue contentAssoc = (GenericValue)iter.next();
                 contentAssoc.put("sequenceNum", new Integer(seqNum));
                 contentAssoc.store();
                 seqNum += seqIncrement;
             }
        } catch(GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());             
         }
         
       
        return result;
    }
    
    public static Map changeLeafToNode(DispatchContext dctx, Map context) throws GenericServiceException{

        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String contentId = (String)context.get("contentId");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String userLoginId = userLogin.getString("userLoginId");
        int seqNum = 9999;
        try {
            GenericValue content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
            if (content == null) {
                Debug.logError("content was null", module);
                return ServiceUtil.returnError("content was null");
            }
            String dataResourceId = content.getString("dataResourceId");
            content.set("dataResourceId", null);
            content.set("lastModifiedDate", UtilDateTime.nowTimestamp());
            content.set("lastModifiedByUserLogin", userLoginId);
            content.store();
            
            if (UtilValidate.isNotEmpty(dataResourceId)) {
            	// add previous DataResource as part of new subcontent
            	GenericValue contentClone = (GenericValue)content.clone();
            	contentClone.set("dataResourceId", dataResourceId);
            	content.set("lastModifiedDate", UtilDateTime.nowTimestamp());
            	content.set("lastModifiedByUserLogin", userLoginId);
            	content.set("createdDate", UtilDateTime.nowTimestamp());
            	content.set("createdByUserLogin", userLoginId);
	            
            	contentClone.set("contentId", null);
            	ModelService modelService = dctx.getModelService("persistContentAndAssoc");
            	Map serviceIn = modelService.makeValid(contentClone, "IN");
            	serviceIn.put("userLogin", userLogin);
            	serviceIn.put("caContentIdTo", contentId);
            	serviceIn.put("caContentAssocTypeId", "SUB_CONTENT");
            	try {
                	Map thisResult = dispatcher.runSync("persistContentAndAssoc", serviceIn);
            	} catch(ServiceAuthException e) {
                	return ServiceUtil.returnError(e.getMessage());             
            	}
            	
            	List typeList = UtilMisc.toList("SUB_CONTENT");
            	int leafCount = ContentManagementWorker.updateStatsTopDown(delegator, contentId, typeList);
            }
            
        } catch(GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());             
        }
         
       
        return result;
    }
    
    public static Map updateLeafCount(DispatchContext dctx, Map context) throws GenericServiceException{

        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        List typeList = (List)context.get("typeList");
        if (typeList == null)
            typeList = UtilMisc.toList("PUBLISH_LINK", "SUB_CONTENT");
        String startContentId = (String)context.get("contentId");
        try {
            int leafCount = ContentManagementWorker.updateStatsTopDown(delegator, startContentId, typeList);
            result.put("leafCount", new Integer(leafCount));
        } catch(GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());             
        }
        return result;
    }
    
/*
    public static Map updateLeafChange(DispatchContext dctx, Map context) throws GenericServiceException{

        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        List typeList = (List)context.get("typeList");
        if (typeList == null)
            typeList = UtilMisc.toList("PUBLISH_LINK", "SUB_CONTENT");
        String contentId = (String)context.get("contentId");
        
        try {
            GenericValue thisContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
            if (thisContent == null)
                throw new RuntimeException("No entity found for id=" + contentId);
            
            String thisContentId = thisContent.getString("contentId");
            Long leafCount = (Long)thisContent.get("nodeLeafCount");
            int subLeafCount = (leafCount == null) ? 1 : leafCount.intValue();
            String mode = (String)context.get("mode");
            if (mode != null && mode.equalsIgnoreCase("remove")) {
                subLeafCount *= -1;
            } else {
                // TODO: ??? what is this supposed to do:
                //subLeafCount = subLeafCount;
            }
            
           List condList = new ArrayList();
           Iterator iterType = typeList.iterator();
           while (iterType.hasNext()) {
               String type = (String)iterType.next();
               condList.add(new EntityExpr("contentAssocTypeId", EntityOperator.EQUALS, type));
           }
           
           EntityCondition conditionType = new EntityConditionList(condList, EntityOperator.OR);
           EntityCondition conditionMain = new EntityConditionList(UtilMisc.toList( new EntityExpr("contentId", EntityOperator.EQUALS, thisContentId), conditionType), EntityOperator.AND);
            List listAll = delegator.findByConditionCache("ContentAssoc", conditionMain, null, null);
            List listFiltered = EntityUtil.filterByDate(listAll);
            Iterator iter = listFiltered.iterator();
            while (iter.hasNext()) {
                GenericValue contentAssoc = (GenericValue)iter.next();
                String subContentId = contentAssoc.getString("contentId");
                GenericValue contentTo = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", subContentId));
                Integer childBranchCount = (Integer)contentTo.get("childBranchCount");
                int branchCount = (childBranchCount == null) ? 1 : childBranchCount.intValue();
                if (mode != null && mode.equalsIgnoreCase("remove"))
                    branchCount += -1;
                else
                    branchCount += 1;
                // For the level just above only, update the branch count
                contentTo.put("childBranchCount", new Integer(branchCount));
                
                // Start the updating of leaf counts above
                ContentManagementWorker.updateStatsBottomUp(delegator, subContentId, typeList, subLeafCount);
            }
        
        
        } catch(GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());             
        }
        return result;
    }
    */
    
    /**
     * This service changes the contentTypeId of the current content and its children depending on the pageMode.
     * if pageMode == "outline" then if the contentTypeId of children is not "OUTLINE_NODE" or "PAGE_NODE" 
     * (it could be DOCUMENT or SUBPAGE_NODE) then it will get changed to PAGE_NODE.`
     * if pageMode == "page" then if the contentTypeId of children is not "PAGE_NODE" or "SUBPAGE_NODE" 
     * (it could be DOCUMENT or OUTLINE_NODE) then it will get changed to SUBPAGE_NODE.`
     * @param delegator
     * @param contentId
     * @param pageMode
     */
    public static Map updatePageType(DispatchContext dctx, Map context) throws GenericServiceException{
        
        GenericDelegator delegator = dctx.getDelegator();
    	Map results = new HashMap();
    	String pageMode = (String)context.get("pageMode");
    	String contentId = (String)context.get("contentId");
        String contentTypeId = "PAGE_NODE";
        if (pageMode != null && pageMode.toLowerCase().indexOf("outline") >= 0)
        	contentTypeId = "OUTLINE_NODE";
        GenericValue thisContent = null;
        try {
            thisContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
            if (thisContent == null)
                ServiceUtil.returnError("No entity found for id=" + contentId);
            thisContent.set("contentTypeId", contentTypeId);
            thisContent.store();
            List kids = ContentWorker.getAssociatedContent(thisContent, "from", UtilMisc.toList("SUB_CONTENT"), null, null, null);
            Iterator iter = kids.iterator();
            while (iter.hasNext()) {
            	GenericValue kidContent = (GenericValue)iter.next();
                if (contentTypeId.equals("OUTLINE_NODE")) {
                	updateOutlineNodeChildren(kidContent);
                } else {
                	updatePageNodeChildren(kidContent);
                }
            }
        } catch(GenericEntityException e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
            
        return results;
    }
    
    public static void updatePageNodeChildren(GenericValue content) throws GenericEntityException {
        
    	String contentTypeId = content.getString("contentTypeId");
    	String newContentTypeId = "SUBPAGE_NODE";
//        if (contentTypeId == null || contentTypeId.equals("DOCUMENT")) {
//            newContentTypeId = "SUBPAGE_NODE";
//        } else if (contentTypeId.equals("OUTLINE_NODE")) {
//            newContentTypeId = "PAGE_NODE";
//        }
            
        content.put("contentTypeId", newContentTypeId);
        content.store();
        
        //if (contentTypeId == null || contentTypeId.equals("OUTLINE_DOCUMENT") || contentTypeId.equals("DOCUMENT")) {
            List kids = ContentWorker.getAssociatedContent(content, "from", UtilMisc.toList("SUB_CONTENT"), null, null, null);
            Iterator iter = kids.iterator();
            while (iter.hasNext()) {
            	GenericValue kidContent = (GenericValue)iter.next();
            	updatePageNodeChildren(kidContent);
            }
        //}
        return;
    }

    public static void updateOutlineNodeChildren(GenericValue content) throws GenericEntityException {
    	
    	String contentTypeId = content.getString("contentTypeId");
    	String newContentTypeId = contentTypeId;
    	String dataResourceId = content.getString("dataResourceId");
    	Long branchCount = (Long)content.get("childBranchCount");
        if (contentTypeId == null || contentTypeId.equals("DOCUMENT")) {
        	if (UtilValidate.isEmpty(dataResourceId) || (branchCount != null && branchCount.intValue() > 0))
        		newContentTypeId = "OUTLINE_NODE";
       		else
        		newContentTypeId = "PAGE_NODE";
        } else if (contentTypeId.equals("SUBPAGE_NODE")) {
            newContentTypeId = "PAGE_NODE";
        }
            
        content.put("contentTypeId", newContentTypeId);
        content.store();
        
        if (contentTypeId == null || contentTypeId.equals("DOCUMENT") || contentTypeId.equals("OUTLINE_NODE")) {
        //if (contentTypeId == null || contentTypeId.equals("DOCUMENT")) {
            List kids = ContentWorker.getAssociatedContent(content, "from", UtilMisc.toList("SUB_CONTENT"), null, null, null);
            Iterator iter = kids.iterator();
            while (iter.hasNext()) {
            	GenericValue kidContent = (GenericValue)iter.next();
            	updateOutlineNodeChildren(kidContent);
            }
        }
        return;
    }

}
