/*
 * $Id: ContentPermissionServices.java,v 1.2 2003/09/14 05:36:47 jonesde Exp $
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
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * ContentPermissionServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.2 $
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
        String contentId = (String) context.get("contentId");
        GenericValue content = (GenericValue) context.get("currentContent"); 
        GenericValue userLogin = (GenericValue) context.get("userLogin"); 
        List passedPurposes = (List) context.get("contentPurposeList"); 
        List targetOperations = (List) context.get("targetOperationList"); 
        List passedRoles = (List) context.get("roleTypeList"); 
        if (passedRoles == null) passedRoles = new ArrayList();

        // Get the ContentPurposeOperation table and save the results to be reused.
        List purposeOperations = null;
        try {
            purposeOperations = delegator.findAllCache("ContentPurposeOperation");
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Error in retrieving ContentPurposeOperations. " + e.getMessage());
        }

        if (content == null || content.isEmpty()) {
            return ServiceUtil.returnError("Content not found.");
        }


        // Combine any passed purposes with those linked to the Content entity
        // Note that purposeIds is a list of contentPurposeTypeIds, not GenericValues
        List purposeIds = getRelatedPurposes(content, passedPurposes );

        // Do check of non-RoleType conditions
        boolean isMatch = publicMatches(purposeOperations, targetOperations, content, purposeIds, null);
        
        if (!isMatch && userLogin == null) {
            Debug.logInfo("No unauthorized permissions found. ", module);
            return ServiceUtil.returnError("No unauthorized permissions found. ");
        }

        if (!isMatch) {
            isMatch = security.hasEntityPermission("CONTENTMGR", "_CREATE", userLogin);
        }

        if (!isMatch) {

            // Get all roles associated with this Content and the user,
            // including groups.
            List roleIds = getUserRoles(content, userLogin, passedRoles, delegator);

            // This is a recursive query that looks for any "owner" content in the 
            // ancestoral path that might have ContentRole associations that
            // make a ContentPurposeOperation condition match.
            isMatch = checkPermissionWithRoles(content, purposeIds, roleIds, 
                             targetOperations, purposeOperations, userLogin, delegator );
        }

        Map results = new HashMap();
        String permissionStatus = null;
        if( isMatch ) permissionStatus = "granted";
        results.put("permissionStatus", permissionStatus);
        return results;
    }

    /**
     * checkContentPermission
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
    public static boolean checkPermissionWithRoles( GenericValue content, List passedPurposes, 
                                           List passedRoles, 
                                           List targetOperations, List purposeOperations,
                                           GenericValue userLogin, GenericDelegator delegator){ 

        boolean isMatch = publicMatches(purposeOperations, targetOperations, content,  
                                        passedPurposes, passedRoles);
        if (isMatch) return isMatch;

        // recursively try if the "owner" Content has ContentRoles that allow a match
        String ownerContentId = (String)content.get("ownerContentId");
        if (ownerContentId != null && ownerContentId.length() > 0 ) {
            GenericValue ownerContent = null;
            try {
                ownerContent = delegator.findByPrimaryKeyCache("Content", 
                                                 UtilMisc.toMap("contentId", ownerContentId) );
            } catch (GenericEntityException e) {
                Debug.logError(e, "Owner content not found. ", module);
            }
            if (ownerContent != null) {
                List roleIds = getUserRoles(ownerContent, userLogin, null, delegator);
                isMatch = checkPermissionWithRoles(ownerContent, passedPurposes, roleIds, 
                             targetOperations, purposeOperations, userLogin,  delegator );
            }
        }
        return isMatch;

    }

    /**
     * publicMatches
     * Takes all the criteria and performs a check to see if there is a match.
     */
    public static boolean publicMatches(List purposeOperations, List targetOperations, GenericValue content, 
                   List purposes, List roles) {
        boolean isMatch = false;
        Iterator purposeOpsIter = purposeOperations.iterator();
        while (purposeOpsIter.hasNext() ) {
            GenericValue purposeOp = (GenericValue)purposeOpsIter.next();
            String roleTypeId = (String)purposeOp.get("roleTypeId");
            String contentPurposeTypeId = (String)purposeOp.get("contentPurposeTypeId");
            String contentOperationId = (String)purposeOp.get("contentOperationId");
 
            if ( targetOperations.contains(contentOperationId)           
                 && (purposes.contains(contentPurposeTypeId) 
                     || contentPurposeTypeId.equals("_NA_") ) ) {
                if ( roleTypeId == null 
                    || roleTypeId.equals("_NA_") 
                    || (roles != null && roles.contains(roleTypeId) ) ){
                
                    isMatch = true;
                    break;
                }
            }
        }
        return isMatch;
    }

    /**
     * getUserRoles
     * Queries for the ContentRoles associated with a Content entity
     * and returns the ones that match the user.
     * Follows group parties to see if the user is a member.
     */
    public static List getUserRoles(GenericValue content, GenericValue userLogin, 
                                    List passedRoles, GenericDelegator delegator) {
        ArrayList roles = null;
        if (passedRoles == null) {
            roles = new ArrayList( );
        } else {
            roles = new ArrayList( passedRoles );
        }
        String partyId = (String)userLogin.get("partyId");
	List relatedRoles = null;
        try {
            relatedRoles = content.getRelatedCache("ContentRole");
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
                    roles.add(roleTypeId);
                } else {
                    GenericValue party = null;
                    String partyTypeId = null;
                    try {
                        party = contentRole.getRelatedOneCache("Party");
                        partyTypeId = (String)party.get("partyTypeId");
                        if ( partyTypeId != null && partyTypeId.equals("PARTY_GROUP") ) {
                           HashMap map = new HashMap();
                         
                           // At some point from/thru date will need to be added
                           map.put("partyIdFrom", partyId);
                           map.put("partyIdTo", targPartyId);
                           if ( isGroupMember( map, delegator ) ) {
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
     * isGroupMember
     * Tests to see if the user belongs to a group
     */
    public static boolean isGroupMember( Map partyRelationshipValues, GenericDelegator delegator ) {
        boolean isMember = false;
        String partyIdFrom = (String)partyRelationshipValues.get("partyIdFrom") ;
        String partyIdTo = (String)partyRelationshipValues.get("partyIdTo") ;
        String roleTypeIdFrom = "CONTENT_PERMISSION_GROUP_MEMBER";
        String roleTypeIdTo = "CONTENT_PERMISSION_GROUP";
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


}
