package org.ofbiz.content;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

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
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * ContentManagementServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.17 $
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
                    Map thisResult = DataServices.createDataResourceMethod(dctx, context);
                    String errorResult = (String)thisResult.get(ModelService.RESPONSE_MESSAGE);
                    if (errorResult != null && errorResult.equals(ModelService.RESPOND_ERROR)) {
                            return ServiceUtil.returnError((String)thisResult.get(ModelService.ERROR_MESSAGE));
                    }
                    dataResourceId = (String)thisResult.get("dataResourceId");
                    if (Debug.infoOn()) Debug.logInfo("in persist... dataResourceId(0):" + dataResourceId, null);
                    dataResource = (GenericValue)thisResult.get("dataResource");
                    if (dataResourceTypeId.indexOf("_FILE_BIN") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        context.put("dataResource", dataResource);
                        try {
                            thisResult = DataServices.createBinaryFileMethod(dctx, context);
                        } catch(GenericServiceException e) {
                            return ServiceUtil.returnError(e.getMessage());
                        }
                    } else if (dataResourceTypeId.indexOf("_FILE") >=0) {
                        dataResource = (GenericValue)thisResult.get("dataResource");
                        context.put("dataResource", dataResource);
                        try {
                            thisResult = DataServices.createFileMethod(dctx, context);
                        } catch(GenericServiceException e) {
                            return ServiceUtil.returnError(e.getMessage());
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
                        if (UtilValidate.isNotEmpty(textData)) {
                            context.put("dataResourceId", dataResourceId);
                            thisResult = DataServices.createElectronicTextMethod(dctx, context);
                        } else {
                            return ServiceUtil.returnError("'textData' empty when trying to create database text.");
                        }
                    }
                } else {
                    Map thisResult = DataServices.updateDataResourceMethod(dctx, context);
                    if (Debug.infoOn()) Debug.logInfo("in persist... thisResult.permissionStatus(0):" + thisResult.get("permissionStatus"), null);
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
            if (Debug.infoOn()) Debug.logInfo("CREATING contentASSOC contentAssocTypeId:" +  contentAssocTypeId, null);
        if (contentAssocTypeId != null && contentAssocTypeId.length() > 0 ) {
            if (Debug.infoOn()) Debug.logInfo("in persistContentAndAssoc, deactivateExistin:" +  deactivateExisting, null);
            context.put("deactivateExisting", deactivateExisting);
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
       context.remove("skipPermissionCheck");
       context.put("contentId", origContentId);
       context.put("dataResourceId", origDataResourceId);
       context.remove("dataResource");
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
                      addRoleToUser(delegator, dispatcher, serviceContext);
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
                      newContext.put("userLogin", userLogin);
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

  public static void addRoleToUser(GenericDelegator delegator, LocalDispatcher dispatcher, Map serviceContext) throws GenericServiceException {
    String partyId = (String)serviceContext.get("partyId");
    Map findMap = UtilMisc.toMap("partyId", partyId);
    try {
        List userLoginList = delegator.findByAnd("UserLogin", findMap);
        Iterator iter = userLoginList.iterator();
        while (iter.hasNext()) {
            GenericValue partyUserLogin = (GenericValue)iter.next();
            String partyUserLoginId = partyUserLogin.getString("userLoginId");
            serviceContext.put("contentId", partyUserLoginId); // author contentId
            dispatcher.runSync("createContentRole", serviceContext);
        }
    } catch(GenericEntityException e) {
        Debug.logError(e, "No action, except returning, taken.", module);
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
}
