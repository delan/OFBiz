/*
 * $Id: ContentPermissionServices.java,v 1.20 2004/06/16 18:54:44 byersa Exp $
 *
 * Copyright (c) 2001-2003 The Open For Business Project - www.ofbiz.org
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
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * ContentPermissionServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.20 $
 * @since      2.2
 * 
 * Services for granting operation permissions on Content entities in a data-driven manner.
 */
public class ContentPermissionServices {

    public static final String module = ContentPermissionServices.class.getName();


    public ContentPermissionServices() {}

    /**
     * checkContentPermission
     *
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     *
     * This service goes thru a series of test to determine if the user has
     * authority to performed anyone of the passed in target operations.
     *
     * It expects a Content entity in "currentContent" 
     * It expects a list of contentOperationIds in "targetOperationList" rather
     * than a scalar because it is thought that sometimes more than one operation
     * would fit the situation.
     * Similarly, it expects a list of contentPurposeTypeIds in "contentPurposeList".
     * Again, normally there will just be one, but it is possible that a Content 
     * entity could have multiple purposes associated with it.
     * The userLogin GenericValue is also required.
     * A list of roleTypeIds is also possible.
     *
     * The basic sequence of testing events is:
     * First the ContentPurposeOperation table is checked to see if there are any 
     * entries with matching purposes (and operations) with no roleTypeId (ie. _NA_).
     * This is done because it would be the most common scenario and is quick to check.
     *
     * Secondly, the CONTENTMGR permission is checked.
     *
     * Thirdly, the ContentPurposeOperation table is rechecked to see if there are 
     * any conditions with roleTypeIds that match associated ContentRoles tied to the
     * user. 
     * If a Party of "PARTY_GROUP" type is found, the PartyRelationship table is checked
     * to see if the current user is linked to that group.
     *
     * If no match is found to this point and the current Content entity has a value for
     * ownerContentId, then the last step is recusively applied, using the ContentRoles
     * associated with the ownerContent entity.
     */

    public static Map checkContentPermission(DispatchContext dctx, Map context) {

        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        String statusId = (String) context.get("statusId");
        String privilegeEnumId = (String) context.get("privilegeEnumId");
        GenericValue content = (GenericValue) context.get("currentContent"); 
        GenericValue userLogin = (GenericValue) context.get("userLogin"); 

        // I realized, belatedly, that I wanted to be able to pass parameters in as
        // strings so this service could be used in an action event directly,
        // so I had to write this code to handle both list and strings
        List passedPurposes = (List) context.get("contentPurposeList"); 
        String contentPurposeString = (String) context.get("contentPurposeString"); 
        //Debug.logInfo("contentPurposeString(b):" + contentPurposeString, "");
        if (UtilValidate.isNotEmpty(contentPurposeString)) {
            List purposesFromString = StringUtil.split(contentPurposeString, "|");
            if (passedPurposes == null) {
                passedPurposes = new ArrayList();
            }
            passedPurposes.addAll(purposesFromString);
        }
        //Debug.logInfo("passedPurposes(b):" + passedPurposes, "");
        List targetOperations = (List) context.get("targetOperationList"); 
        String targetOperationString = (String) context.get("targetOperationString"); 
        //Debug.logInfo("targetOperationString(b):" + targetOperationString, "");
        if (UtilValidate.isNotEmpty(targetOperationString)) {
            List operationsFromString = StringUtil.split(targetOperationString, "|");
            if (targetOperations == null) {
                targetOperations = new ArrayList();
            }
            targetOperations.addAll(operationsFromString);
        }
        //Debug.logInfo("targetOperations(b):" + targetOperations, "");
        List passedRoles = (List) context.get("roleTypeList"); 
        if (passedRoles == null) passedRoles = new ArrayList();
        // If the current user created the content, then add "_OWNER_" as one of
        //   the contentRoles that is in effect.
        String entityAction = (String) context.get("entityOperation");
        if (entityAction == null) entityAction = "_ADMIN";

        String quickCheckContentId = (String) context.get("quickCheckContentId");
        Map results = checkPermission( content, statusId, userLogin, passedPurposes, targetOperations, passedRoles, delegator, security, entityAction, privilegeEnumId, quickCheckContentId);
                //Debug.logInfo("results(b):" + results, "");
                PermissionRecorder r = (PermissionRecorder)results.get("permissionRecorder");
                //Debug.logInfo("recorder(b):" + r, "");
/*
        String permissionStatus = null;
        String quickCheckContentId = (String) context.get("quickCheckContentId");
        if (UtilValidate.isNotEmpty(quickCheckContentId)) {
            GenericValue quickCheckContent = null;
            try {
                quickCheckContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", quickCheckContentId); 
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError("Error getting quickCheck content: " + e.toString());
            }
             results = checkPermission( quickCheckContent, statusId, userLogin, passedPurposes, targetOperations, passedRoles, delegator, security, entityAction, privilegeEnumId);
             permissionStatus = (String)results.get("permissionStatus");
        }

        if (results == null || permissionStatus == null || !permissionStatus.equals("granted")) {
            results = checkPermission( content, statusId, userLogin, passedPurposes, targetOperations, passedRoles, delegator, security, entityAction, privilegeEnumId);
                //Debug.logInfo("results(b):" + results, "");
                PermissionRecorder r = (PermissionRecorder)results.get("permissionRecorder");
                //Debug.logInfo("recorder(b):" + r, "");
        }
*/
        return results;
    }

    public static Map checkPermission(GenericValue content, String statusId, GenericValue userLogin, List passedPurposes, List targetOperations, List passedRoles, GenericDelegator delegator , Security security, String entityAction
        ) {
             String privilegeEnumId = null;
             return checkPermission( content, statusId,
                                      userLogin, passedPurposes,
                                      targetOperations, passedRoles,
                                      delegator, security, entityAction, privilegeEnumId, null);
        }

    public static Map checkPermission(GenericValue content, String statusId,
                                      GenericValue userLogin, List passedPurposes,
                                      List targetOperations, List passedRoles,
                                      GenericDelegator delegator ,
                                      Security security, String entityAction,
                                      String privilegeEnumId, String quickCheckContentId
        ) {
             List statusList = null;
             if (statusId != null) {
                 statusList = StringUtil.split(statusId, "|");
             }
             return checkPermission( content, statusList,
                                      userLogin, passedPurposes,
                                      targetOperations, passedRoles,
                                      delegator, security, entityAction, privilegeEnumId, quickCheckContentId);
        }

    public static Map checkPermission(GenericValue content, List statusList,
                                      GenericValue userLogin, List passedPurposes,
                                      List targetOperations, List passedRoles,
                                      GenericDelegator delegator ,
                                      Security security, String entityAction,
                                      String privilegeEnumId
        ) {
             return checkPermission( content, statusList,
                                      userLogin, passedPurposes,
                                      targetOperations, passedRoles,
                                      delegator, security, entityAction, privilegeEnumId, null);
     }


    public static Map checkPermission(GenericValue content, List statusList,
                                      GenericValue userLogin, List passedPurposes,
                                      List targetOperations, List passedRoles,
                                      GenericDelegator delegator ,
                                      Security security, String entityAction,
                                      String privilegeEnumId, String quickCheckContentId
        ) {

        //Debug.logInfo("passedPurposes(c):" + passedPurposes, "");
        //Debug.logInfo("targetOperations(c):" + targetOperations, "");
        Map result = new HashMap();
        PermissionRecorder recorder = new PermissionRecorder();
                //Debug.logInfo("recorder(a):" + recorder, "");
        result.put("permissionRecorder", recorder);

        List allowedHasRoleOperationList = null;
        boolean hasRoleOperation = false;
        String userLoginId = null; 
        if (userLogin != null) {
            userLoginId = userLogin.getString("userLoginId");
        }
        if (!(targetOperations == null) && userLoginId != null) {
            hasRoleOperation = checkHasRoleOperations(userLoginId, targetOperations, delegator);
        }
        //if (Debug.infoOn()) Debug.logInfo("hasRoleOperation:" + hasRoleOperation, module);
        if( hasRoleOperation ) {
            result.put("permissionStatus", "granted");
            return result;
        }


        if (content != null) {
            String statusId = (String)content.get("statusId");
            if (UtilValidate.isNotEmpty(statusId)) {
                if (statusList == null)
                    statusList = new ArrayList();
                statusList.add(statusId);
            }
            if (UtilValidate.isEmpty(privilegeEnumId))
                privilegeEnumId = (String)content.get("privilegeEnumId");
        }
        if (passedRoles == null) passedRoles = new ArrayList();
        if (UtilValidate.isEmpty(privilegeEnumId))
            privilegeEnumId = "_00_"; // minimum privilege. any request passes

        if (recorder.isOn()) recorder.setUserLogin(userLogin);

        if (targetOperations == null || targetOperations.size() == 0) {
            //Debug.logWarning("No targetOperations.", module);
        }
	List roleIds = null;
        String permissionStatus = null;
        result.put("roleTypeList", passedRoles);
        //if (Debug.infoOn()) Debug.logInfo("in permissionCheck, passedRoles(1):" + passedRoles, null);

        // Get the ContentPurposeOperation table and save the result to be reused.
        List purposeOperations = null;
        try {
            purposeOperations = delegator.findAllCache("ContentPurposeOperation");
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Error in retrieving ContentPurposeOperations. " + e.getMessage());
        }

        List purposeIds = null;
        // Do check before bothering to get related purposes.
        String contentId = null;
        if (content != null)
            contentId = content.getString("contentId");
        //if (content != null && Debug.infoOn()) Debug.logInfo("in checkPermission, contentId(1):" + content.get("contentId"), null);
        boolean isMatch = publicMatches(purposeOperations, targetOperations, purposeIds, passedRoles, statusList, privilegeEnumId, recorder, contentId);
        
        if( isMatch ) {
            result.put("permissionStatus", "granted");
            return result;
        }


        // Combine any passed purposes with those linked to the Content entity
        // Note that purposeIds is a list of contentPurposeTypeIds, not GenericValues
        purposeIds = getRelatedPurposes(content, passedPurposes );
        //if (Debug.infoOn()) Debug.logInfo("purposeIds:" + purposeIds, null);
        if (purposeIds == null || purposeIds.size() == 0) {
            //Debug.logWarning("No purposeIds.", module);
        }

        // Do check of non-RoleType conditions
        //if (Debug.infoOn()) Debug.logInfo("in publicMatches, contentId(3):" + contentId, null);
        isMatch = publicMatches(purposeOperations, targetOperations, purposeIds, passedRoles, statusList, privilegeEnumId, recorder, contentId);
        
        if( isMatch ) {
            result.put("permissionStatus", "granted");
            return result;
        }

        // Do entity permission check. This will pass users with administrative permissions.
        if (userLogin != null ) {
            isMatch = security.hasEntityPermission("CONTENTMGR", entityAction, userLogin);
            recorder.setEntityPermCheckResult(isMatch);
        }

        if( isMatch ) {
            result.put("permissionStatus", "granted");
            return result;
        }


        if (content == null || content.isEmpty() ) {
            //if (Debug.infoOn()) Debug.logInfo("content is null:" + content, null);
            return result;
        }

        //if (Debug.infoOn()) Debug.logInfo("userLogin:" + userLogin, null);
        if (userLogin != null ) {

/*
            // Get all roles associated with this Content and the user,
            // including groups.
            roleIds = getUserRoles(content, userLogin, passedRoles, delegator);
        //if (Debug.infoOn()) Debug.logInfo("in permissionCheck, roleIds(0):" + roleIds, null);
		//if (passedRoles == null) {
                    //passedRoles = roleIds;
                //} else {
                    //passedRoles.addAll(roleIds);
                //}
                result.put("roleTypeList", roleIds);
*/

            Map thisResult = null;
            if (UtilValidate.isNotEmpty(quickCheckContentId)) {
                GenericValue quickCheckContent = null;
                try {
                    quickCheckContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", quickCheckContentId)); 
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError("Error getting quickCheck content: " + e.toString());
                }
                thisResult = checkPermissionWithRoles(quickCheckContent, purposeIds, passedRoles, targetOperations, purposeOperations, userLogin, delegator, statusList, privilegeEnumId, recorder );
                result.put("roleTypeList", thisResult.get("roleTypeList"));
                result.put("permissionStatus", thisResult.get("permissionStatus"));
                permissionStatus = (String)thisResult.get("permissionStatus");
            }
    
            if (permissionStatus == null || !permissionStatus.equals("granted")) {
                // This is a recursive query that looks for any "owner" content in the 
                // ancestoral path that might have ContentRole associations that
                // make a ContentPurposeOperation condition match.
                thisResult = checkPermissionWithRoles(content, purposeIds, passedRoles, targetOperations, purposeOperations, userLogin, delegator, statusList, privilegeEnumId, recorder );
                result.put("roleTypeList", thisResult.get("roleTypeList"));
                result.put("permissionStatus", thisResult.get("permissionStatus"));
            }
        }
                    //Debug.logInfo("result(a):" + result, "");
                    PermissionRecorder r = (PermissionRecorder)result.get("permissionRecorder");
                    //Debug.logInfo("recorder(a):" + r, "");
        return result;

    }

    /**
     * checkContentPermissionWithRoles
     *
     *@param content The content GenericValue to be checked
     *@param passedPurposes The list of contentPurposeTypeIds to be used in the test 
     *@param passedRoles The list of roleTypeIds to be used in the test 
     *@param targetOperatons The list of contentOperationIds that must be matched
     *@param purposeOperations The list of contentPurposeOperation GenericValues that will
     *                           be used to find matches
     *@param userLogin
     *@param delegator 
     *@return boolean True if a match is found, else false.
     *
     */
    public static Map checkPermissionWithRoles( GenericValue content, List passedPurposes, 
                                           List passedRoles, 
                                           List targetOperations, List purposeOperations,
                                           GenericValue userLogin, GenericDelegator delegator, 
                                           List statusList, String privilegeEnumId, PermissionRecorder recorder){ 

        String permissionStatus = null;
        Map result = new HashMap();
        List roleIds = getUserRoles(content, userLogin, passedRoles, delegator);
        result.put("roleTypeList", roleIds);
        result.put("permissionStatus", permissionStatus);
        String contentId = null;
        if (content != null)
            contentId = content.getString("contentId");
        //if (Debug.infoOn()) Debug.logInfo("in publicMatches, contentId(2):" + contentId, null);
        boolean isMatch = publicMatches(purposeOperations, targetOperations, passedPurposes, roleIds, statusList, privilegeEnumId, recorder, contentId);
        if (isMatch) {
            result.put("permissionStatus", "granted");
            return result;
        }

        // recursively try if the "owner" Content has ContentRoles that allow a match
        String ownerContentId = (String)content.get("ownerContentId");
        //if (Debug.infoOn()) Debug.logInfo("ownerContentId:" + ownerContentId, null);
        if (UtilValidate.isNotEmpty(ownerContentId)) {
            GenericValue ownerContent = null;
            try {
                ownerContent = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", ownerContentId) );
            } catch (GenericEntityException e) {
                Debug.logError(e, "Owner content not found. ", module);
            }
            if (ownerContent != null) {
                // Already been checked with old roles, so send only new roles to checkPermission.
                Map result2 = checkPermissionWithRoles(ownerContent, passedPurposes, roleIds, 
                             targetOperations, purposeOperations, userLogin,  delegator, statusList, privilegeEnumId, recorder );
                result.put("roleTypeList", result2.get("roleTypeList"));
                result.put("permissionStatus", result2.get("permissionStatus"));
            }
        }
        return result;

    }



    /**
     * getUserRoles
     * Queries for the ContentRoles associated with a Content entity
     * and returns the ones that match the user.
     * Follows group parties to see if the user is a member.
     */
    public static List getUserRoles(GenericValue content, GenericValue userLogin, 
                                    List passedRoles, GenericDelegator delegator) {

        if(content == null) return passedRoles;
            // TODO: Need to use ContentManagementWorker.getAuthorContent first

        ArrayList roles = null;
        if (passedRoles == null) {
            roles = new ArrayList( );
        } else {
            roles = new ArrayList( passedRoles );
        }

        roles.remove("_OWNER_"); // always test with the owner of the current content
        if ( content.get("createdByUserLogin") != null && userLogin != null) {
            String userLoginId = (String)userLogin.get("userLoginId");
            String userLoginIdCB = (String)content.get("createdByUserLogin");
            //if (Debug.infoOn()) Debug.logInfo("userLoginId:" + userLoginId + ": userLoginIdCB:" + userLoginIdCB + ":", null);
            if (userLoginIdCB.equals(userLoginId)) {
                roles.add("_OWNER_");
                //if (Debug.infoOn()) Debug.logInfo("in getUserRoles, passedRoles(0):" + passedRoles, null);
            }
        }
        
        String partyId = (String)userLogin.get("partyId");
	List relatedRoles = null;
        try {
            List tmpRelatedRoles = content.getRelatedCache("ContentRole");
            relatedRoles = EntityUtil.filterByDate(tmpRelatedRoles);
        } catch (GenericEntityException e) {
            Debug.logError(e, "No related roles found. ", module);
        }
        if(relatedRoles != null ) {
            Iterator rolesIter = relatedRoles.iterator();
            while (rolesIter.hasNext() ) {
                GenericValue contentRole = (GenericValue)rolesIter.next();
                String roleTypeId = (String)contentRole.get("roleTypeId");
                String targPartyId = (String)contentRole.get("partyId");
                if (targPartyId.equals(partyId)) {
                    if (!roles.contains(roleTypeId))
                        roles.add(roleTypeId);
                } else { // Party may be of "PARTY_GROUP" type, in which case the userLogin may still possess this role
                    GenericValue party = null;
                    String partyTypeId = null;
                    try {
                        party = contentRole.getRelatedOne("Party");
                        partyTypeId = (String)party.get("partyTypeId");
                        if ( partyTypeId != null && partyTypeId.equals("PARTY_GROUP") ) {
                           HashMap map = new HashMap();
                         
                           // At some point from/thru date will need to be added
                           map.put("partyIdFrom", partyId);
                           map.put("partyIdTo", targPartyId);
                           if ( isGroupMember( map, delegator ) ) {
                               if (!roles.contains(roleTypeId))
                                   roles.add(roleTypeId);
                           }
                        }
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Error in finding related party. " + e.getMessage(), module);
                    }
                }
            }
        }
        return roles;
    }



    /**
     * publicMatches
     * Takes all the criteria and performs a check to see if there is a match.
     */
    public static boolean publicMatches(List purposeOperations, List targetOperations, List purposes, List roles, List targStatusList, String targPrivilegeEnumId, PermissionRecorder recorder, String contentId) {
        boolean isMatch = false;
        //if (Debug.infoOn()) Debug.logInfo("in publicMatches, contentId(1):" + contentId, null);
        if (recorder.isOn()) recorder.startMatchGroup(targetOperations, purposes, roles, targStatusList, targPrivilegeEnumId, contentId);
        int targPrivilegeSeq = 0;
//        if (UtilValidate.isNotEmpty(targPrivilegeEnumId) && !targPrivilegeEnumId.equals("_NA_") && !targPrivilegeEnumId.equals("_00_") ) {
            // need to do a lookup here to find the seq value of targPrivilegeEnumId.
            // The lookup could be a static store or it could be done on Enumeration entity.
//        }
        boolean permissionDebug = false;
        Iterator purposeOpsIter = purposeOperations.iterator();
        while (purposeOpsIter.hasNext() ) {
            GenericValue purposeOp = (GenericValue)purposeOpsIter.next();
            String testRoleTypeId = (String)purposeOp.get("roleTypeId");
            String testContentPurposeTypeId = (String)purposeOp.get("contentPurposeTypeId");
            String testContentOperationId = (String)purposeOp.get("contentOperationId");
            String testStatusId = (String)purposeOp.get("statusId");
            String testPrivilegeEnumId = (String)purposeOp.get("privilegeEnumId");
            int testPrivilegeSeq = 0;

            boolean targetOpCond = ((targetOperations != null && targetOperations.contains(testContentOperationId)) || (testContentOperationId != null && testContentOperationId.equals("_NA_")));
            boolean purposesCond = ( (purposes != null && purposes.contains(testContentPurposeTypeId) ) || testContentPurposeTypeId.equals("_NA_") ); 
            boolean statusCond = ( testStatusId.equals("_NA_") || (targStatusList != null && targStatusList.contains(testStatusId) ) ); 
            boolean privilegeCond = ( testPrivilegeEnumId.equals("_NA_") || testPrivilegeSeq <= targPrivilegeSeq || testPrivilegeEnumId.equals(targPrivilegeEnumId) ); 
            boolean roleCond = ( testRoleTypeId.equals("_NA_") || (roles != null && roles.contains(testRoleTypeId) ) );

            if (recorder.isOn()) {
                recorder.record(purposeOp, targetOpCond, purposesCond, statusCond, privilegeCond, roleCond);
            }
 
            if (targetOpCond && purposesCond && statusCond && privilegeCond && roleCond) {
                
                    isMatch = true;
                    break;
            }
        }
        return isMatch;
    }



    /**
     * Tests to see if the user belongs to a group
     */
    public static boolean isGroupMember( Map partyRelationshipValues, GenericDelegator delegator ) {
        boolean isMember = false;
        String partyIdFrom = (String)partyRelationshipValues.get("partyIdFrom") ;
        String partyIdTo = (String)partyRelationshipValues.get("partyIdTo") ;
        String roleTypeIdFrom = "PERMISSION_GROUP_MBR";
        String roleTypeIdTo = "PERMISSION_GROUP";
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        Timestamp thruDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), 1);

        if (partyRelationshipValues.get("roleTypeIdFrom") != null ) {
            roleTypeIdFrom = (String)partyRelationshipValues.get("roleTypeIdFrom") ;
        }
        if (partyRelationshipValues.get("roleTypeIdTo") != null ) {
            roleTypeIdTo = (String)partyRelationshipValues.get("roleTypeIdTo") ;
        }
        if (partyRelationshipValues.get("fromDate") != null ) {
            fromDate = (Timestamp)partyRelationshipValues.get("fromDate") ;
        }
        if (partyRelationshipValues.get("thruDate") != null ) {
            thruDate = (Timestamp)partyRelationshipValues.get("thruDate") ;
        }

        EntityExpr partyFromExpr = new EntityExpr("partyIdFrom", EntityOperator.EQUALS, partyIdFrom);
        EntityExpr partyToExpr = new EntityExpr("partyIdTo", EntityOperator.EQUALS, partyIdTo);
       
        EntityExpr relationExpr = new EntityExpr("partyRelationshipTypeId", EntityOperator.EQUALS,
                                                       "CONTENT_PERMISSION");
        //EntityExpr roleTypeIdFromExpr = new EntityExpr("roleTypeIdFrom", EntityOperator.EQUALS, "CONTENT_PERMISSION_GROUP_MEMBER");
        //EntityExpr roleTypeIdToExpr = new EntityExpr("roleTypeIdTo", EntityOperator.EQUALS, "CONTENT_PERMISSION_GROUP");
        EntityExpr fromExpr = new EntityExpr("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,
                                                       fromDate);
        EntityCondition thruCond = new EntityConditionList(
                        UtilMisc.toList(
                            new EntityExpr("thruDate", EntityOperator.EQUALS, null),
                            new EntityExpr("thruDate", EntityOperator.GREATER_THAN, thruDate) ),
                        EntityOperator.OR);

        // This method is simplified to make it work, these conditions need to be added back in.
        //List joinList = UtilMisc.toList(fromExpr, thruCond, partyFromExpr, partyToExpr, relationExpr);
        List joinList = UtilMisc.toList( partyFromExpr, partyToExpr);
        EntityCondition condition = new EntityConditionList(joinList, EntityOperator.AND);

        List partyRelationships = null;
        try {
            partyRelationships = delegator.findByCondition("PartyRelationship", condition, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem finding PartyRelationships. ", module);
            return false;
        }
        if (partyRelationships.size() > 0) {
           isMember = true;
        }

        return isMember;
    }

    /**
     * getRelatedPurposes
     */
    public static List getRelatedPurposes(GenericValue content, List passedPurposes) {

        if(content == null) return passedPurposes;

        List purposeIds = null;
        if (passedPurposes == null) {
            purposeIds = new ArrayList( );
        } else {
            purposeIds = new ArrayList( passedPurposes );
        }


        if (content == null || content.get("contentId") == null ) {
            return purposeIds;
        }

        List purposes = null;
        try {
            purposes = content.getRelatedCache("ContentPurpose");
        } catch (GenericEntityException e) {
            Debug.logError(e, "No associated purposes found. ", module);
        }

        Iterator purposesIter = purposes.iterator();
        while (purposesIter.hasNext() ) {
            GenericValue val = (GenericValue)purposesIter.next();
            purposeIds.add(val.get("contentPurposeTypeId"));
        }
        

        return purposeIds;
    }



    public static Map checkAssocPermission(DispatchContext dctx, Map context) {

        Map results = new HashMap();
        Security security = dctx.getSecurity();
        GenericDelegator delegator = dctx.getDelegator();
        String contentIdFrom = (String) context.get("contentIdFrom");
        String contentIdTo = (String) context.get("contentIdTo");
        String statusId = (String) context.get("statusId");
        String privilegeEnumId = (String) context.get("privilegeEnumId");
        GenericValue content = (GenericValue) context.get("currentContent"); 
        GenericValue userLogin = (GenericValue) context.get("userLogin"); 
        List purposeList = (List) context.get("contentPurposeList"); 
        //if (Debug.infoOn()) Debug.logInfo("in checkAssocPerm, purposeList:" + purposeList, "");
        List targetOperations = (List) context.get("targetOperationList"); 
        List roleList = (List) context.get("roleTypeList"); 
        if (roleList == null) roleList = new ArrayList();
        String entityAction = (String) context.get("entityOperation");
        if (entityAction == null) entityAction = "_ADMIN";
	List roleIds = null;

        GenericValue contentTo = null;
        GenericValue contentFrom = null;
        try {
                contentTo = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentIdTo) );
                contentFrom = delegator.findByPrimaryKey("Content", 
                                                 UtilMisc.toMap("contentId", contentIdFrom) );
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Error in retrieving content To or From. " + e.getMessage());
        }
        if (contentTo == null || contentFrom == null) {
            return ServiceUtil.returnError("contentTo[" + contentTo + "]/From[" + contentFrom + "] is null. ");
        }
        String creatorLoginTo = (String)contentTo.get("createdByUserLogin");
        String creatorLoginFrom = (String)contentFrom.get("createdByUserLogin");
        if(creatorLoginTo != null && creatorLoginFrom != null 
           && creatorLoginTo.equals(creatorLoginFrom) ) {
            roleList.add("_OWNER_");
        }
    
        Map resultsMap = checkPermission( null, statusId, userLogin, purposeList, targetOperations, roleList, delegator, security, entityAction, privilegeEnumId, null);
        boolean isMatch = false;
        String permissionStatus = (String)resultsMap.get("permissionStatus");
        if(permissionStatus != null && permissionStatus.equals("granted") ) isMatch = true;

        boolean isMatchTo = false;
        boolean isMatchFrom = false;
        if(!isMatch){
            roleList = (List)resultsMap.get("roleTypeList");
            resultsMap = checkPermission( contentTo, statusId, userLogin, purposeList, targetOperations, roleList, delegator, security, entityAction, privilegeEnumId, null);
            permissionStatus = (String)resultsMap.get("permissionStatus");
            if(permissionStatus != null && permissionStatus.equals("granted") ) isMatchTo = true;
            results.putAll(resultsMap);

            resultsMap = checkPermission( contentFrom, statusId, userLogin, purposeList, targetOperations, roleList, delegator, security, entityAction, privilegeEnumId, null);
            permissionStatus = (String)resultsMap.get("permissionStatus");
            if(permissionStatus != null && permissionStatus.equals("granted") ) isMatchFrom = true;
            results.put("roleTypeList", resultsMap.get("roleTypeList"));
            results.put("permissionRecorderTo", results.get("permissionRecorder"));
            results.putAll(resultsMap);

            if(isMatchTo && isMatchFrom) isMatch = true;
        } else {
            results.putAll(resultsMap);
        }

        String permStatus = null;
        if( isMatch ) permStatus = "granted";
        results.put("permissionStatus", permStatus);
        return results;
    }

    public static boolean checkHasRoleOperations(String userLoginId,  List targetOperations, GenericDelegator delegator) {

        //if (Debug.infoOn()) Debug.logInfo("targetOperations:" + targetOperations, module);
        //if (Debug.infoOn()) Debug.logInfo("userLoginId:" + userLoginId, module);
        if (targetOperations == null)
            return false;

        if (userLoginId != null && targetOperations.contains("HAS_USER_ROLE"))
            return true;

        boolean hasRoleOperation = false;
        Iterator targOpIter = targetOperations.iterator();
        boolean hasNeed = false;
        List newHasRoleList = new ArrayList();
        while (targOpIter.hasNext()) {
            String roleOp = (String)targOpIter.next();
            int idx1 = roleOp.indexOf("HAS_");
            if (idx1 == 0) {
                String roleOp1 = roleOp.substring(4); // lop off "HAS_"
                int idx2 = roleOp1.indexOf("_ROLE");
                if (idx2 == (roleOp1.length() - 5)) {
                    String roleOp2 = roleOp1.substring(0, roleOp1.indexOf("_ROLE") - 1); // lop off "_ROLE"
                    //if (Debug.infoOn()) Debug.logInfo("roleOp2:" + roleOp2, module);
                    newHasRoleList.add(roleOp2);
                    hasNeed = true;
                }
            }
        }

        if (hasNeed) {
            GenericValue uLogin = null;
            try {
                uLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
                String partyId = uLogin.getString("partyId");
                if (UtilValidate.isNotEmpty(partyId)) {
                    List partyRoleList = delegator.findByAndCache("PartyRole", UtilMisc.toMap("partyId", partyId));
                    Iterator partyRoleIter = partyRoleList.iterator();
                    while (partyRoleIter.hasNext()) {
                        GenericValue partyRole = (GenericValue)partyRoleIter.next();
                        String roleTypeId = partyRole.getString("roleTypeId");
                        targOpIter = newHasRoleList.iterator();
                        while (targOpIter.hasNext()) {
                            String thisRole = (String)targOpIter.next();
                            if (roleTypeId.indexOf(thisRole) >= 0) {
                                hasRoleOperation = true;
                                break;
                            }
                        }
                        if (hasRoleOperation)
                            break;
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return hasRoleOperation;
            }
        }
        return hasRoleOperation;
    }
}
