/*
 * $Id$
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;

import org.w3c.dom.Element;


/**
 * EntityPermissionChecker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
 * @since      3.1
 * 
 * Services for granting operation permissions on Content entities in a data-driven manner.
 */
public class EntityPermissionChecker {

    public static final String module = EntityPermissionChecker.class.getName();

    protected FlexibleStringExpander entityIdExdr;
    protected FlexibleStringExpander entityNameExdr;
    protected List targetOperationList;
    
    public EntityPermissionChecker(Element element) {
        this.entityNameExdr = new FlexibleStringExpander(element.getAttribute("entity-name"));
        this.entityIdExdr = new FlexibleStringExpander(element.getAttribute("entity-id"));
        String targetOperationString = new String(element.getAttribute("target-operation"));
        if (UtilValidate.isNotEmpty(targetOperationString)) {
            List operationsFromString = StringUtil.split(targetOperationString, "|");
            if (targetOperationList == null) {
                targetOperationList = new ArrayList();
            }
            targetOperationList.addAll(operationsFromString);
        }

        return;
    }

    public boolean runPermissionCheck(Map context) {
    	
    	boolean passed = false;
    	String idString = entityIdExdr.expandString(context);
    	List entityIdList = null;
        if (UtilValidate.isNotEmpty(idString)) {
            entityIdList = StringUtil.split(idString, "|");
        } else {
        	entityIdList = new ArrayList();
        }
    	String entityName = entityNameExdr.expandString(context);
        HttpServletRequest request = (HttpServletRequest)context.get("request");
        GenericValue userLogin = null;
        String userLoginId = null; 
        GenericDelegator delegator = null;
        if (request != null) {
            HttpSession session = request.getSession();
            userLogin = (GenericValue)session.getAttribute("userLogin");
            if (userLogin != null)
            	userLoginId = userLogin.getString("userLoginId");
           delegator = (GenericDelegator)request.getAttribute("delegator");
        }
    	try {
    		passed = ContentPermissionServices.checkPermissionMethod(delegator, userLogin, targetOperationList, entityName, entityIdList, null, null, null);
    	} catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
    	}
        return passed;
    }
    
    public static boolean checkPermission(Map context, List targetOperationList, String entityName, List entityIdList) throws GenericEntityException {

    	boolean passed = false;
        HttpServletRequest request = (HttpServletRequest)context.get("request");
        GenericValue userLogin = null;
        String userLoginId = null; 
        GenericDelegator delegator = null;
        if (request != null) {
            HttpSession session = request.getSession();
            userLogin = (GenericValue)session.getAttribute("userLogin");
            if (userLogin != null)
            	userLoginId = userLogin.getString("userLoginId");
           delegator = (GenericDelegator)request.getAttribute("delegator");
        }
 
        String lcEntityName = entityName.toLowerCase();

        boolean hasRoleOperation = false;
        if (!(targetOperationList == null) && userLoginId != null) {
        	hasRoleOperation = checkHasRoleOperations(userLoginId, targetOperationList, delegator);
        }
        if( hasRoleOperation ) {
        	return true;
        }
        ModelEntity modelEntity = delegator.getModelEntity(entityName);
        boolean hasStatusField = false;
        if (modelEntity.getField("statusId") != null)
        	hasStatusField = true;
  
        boolean hasPrivilegeField = false;
        if (modelEntity.getField("privilegeEnumId") != null)
        	hasPrivilegeField = true;
  
        List operationEntities = null;
        ModelEntity modelOperationEntity = delegator.getModelEntity(entityName + "PurposeOperation");
        if (modelOperationEntity == null) {
            modelOperationEntity = delegator.getModelEntity(entityName + "Operation");        	
        }
        
        if (modelOperationEntity == null) {
        	Debug.logError("No operation entity found for " + entityName, module);
        	throw new RuntimeException("No operation entity found for " + entityName);
        }
        
        boolean hasPurposeOp = false;
        if (modelOperationEntity.getField(lcEntityName + "PurposeTypeId") != null)
        	hasPurposeOp = true;
        boolean hasStatusOp = false;
        if (modelOperationEntity.getField("statusId") != null)
        	hasStatusOp = true;
        boolean hasPrivilegeOp = false;
        if (modelOperationEntity.getField("privilegeEnumId") != null)
        	hasPrivilegeOp = true;
        
        // Get all the condition operations that could apply, rather than having to go thru 
        // entire table each time.
        List condList = new ArrayList();
        Iterator iterType = targetOperationList.iterator();
        while (iterType.hasNext()) {
        	String op = (String)iterType.next();
        	condList.add(new EntityExpr(lcEntityName + "OperationId", EntityOperator.EQUALS, op));
        }
        EntityCondition opCond = new EntityConditionList(condList, EntityOperator.OR);
        List targetOperationEntityList = delegator.findByConditionCache(modelOperationEntity.getEntityName(), opCond, null, null);
        
        // check permission for each id in passed list until success.
        // Note that "quickCheck" id come first in the list
        // Check with no roles or purposes on the chance that the permission fields contain _NA_ s.
        String pkFieldName = getPkFieldName(entityName, modelEntity);
        List alreadyCheckedIds = new ArrayList();
        Map purposes = new HashMap();
        Map roles = new HashMap();
        Map entities = new HashMap();
        Iterator iter = entityIdList.iterator();
        List purposeList = null;
        List roleList = null;
        while (iter.hasNext()) {
        	String entityId = (String)iter.next();
           	GenericValue entity = delegator.findByPrimaryKeyCache(entityName,UtilMisc.toMap(pkFieldName, entityId));
        	String statusId = null;
        	if (hasStatusOp && hasStatusField) {
        		statusId = entity.getString("statusId");
    		}
        	String privilegeEnumId = null;
        	if (hasPrivilegeOp && hasPrivilegeField) {
        		privilegeEnumId = entity.getString("privilegeEnumId");
    		}
           	
        	passed = hasMatch(entityName, targetOperationEntityList, roleList, hasPurposeOp, purposeList, hasStatusOp, statusId, hasPrivilegeOp, privilegeEnumId);
        	if (passed)
        		break;
        	entities.put(entityId, entity);
       }
        
        if (passed)
        	return true;
        
        if (hasPurposeOp) {
            // Check with just purposes next.
            iter = entityIdList.iterator();
	        while (iter.hasNext()) {
	        	String entityId = (String)iter.next();
	           	GenericValue entity = (GenericValue)entities.get(entityId);
	            purposeList = getRelatedPurposes(entity, null);
	        	String statusId = null;
	        	if (hasStatusOp && hasStatusField) {
	        		statusId = entity.getString("statusId");
	    		}
	        	String privilegeEnumId = null;
	        	if (hasPrivilegeOp && hasPrivilegeField) {
	        		privilegeEnumId = entity.getString("privilegeEnumId");
	    		}
	           	
	        	passed = hasMatch(entityName, targetOperationEntityList, roleList, hasPurposeOp, purposeList, hasStatusOp, statusId, hasPrivilegeOp, privilegeEnumId);
   	        	if (passed)
	        		break;
	            purposes.put(entityId, purposeList);
	        }
        }
        
        if (passed)
        	return true;
        
        if (userLogin == null)
            return false;

        // Check with roles.
        iter = entityIdList.iterator();
        while (iter.hasNext()) {
        	String entityId = (String)iter.next();
           	GenericValue entity = (GenericValue)entities.get(entityId);
            purposeList = (List)purposes.get(entityId);
            roleList = getUserRoles(entity, userLogin, delegator);

        	String statusId = null;
        	if (hasStatusOp && hasStatusField) {
        		statusId = entity.getString("statusId");
    		}
        	String privilegeEnumId = null;
        	if (hasPrivilegeOp && hasPrivilegeField) {
        		privilegeEnumId = entity.getString("privilegeEnumId");
    		}
           	
        	passed = hasMatch(entityName, targetOperationEntityList, roleList, hasPurposeOp, purposeList, hasStatusOp, statusId, hasPrivilegeOp, privilegeEnumId);
        	if (passed)
        		break;
            roles.put(entityId, roleList);
        }
        
        if (passed)
        	return true;
        
        // Follow ownedEntityIds
        if (modelEntity.getField("owned" + entityName + "Id") != null) {
	        iter = entityIdList.iterator();
	        while (iter.hasNext()) {
	        	String entityId = (String)iter.next();
	           	GenericValue entity = (GenericValue)entities.get(entityId);
	           	String ownedEntityId = entity.getString("owned" + entityName + "Id");
	           	GenericValue ownedEntity = delegator.findByPrimaryKeyCache(entityName,UtilMisc.toMap(pkFieldName, ownedEntityId));
	           	while (ownedEntity != null) {
		           	if (!alreadyCheckedIds.contains(ownedEntityId)) {
			            purposeList = (List)purposes.get(entityId);
			            purposeList = getRelatedPurposes(ownedEntity, purposeList);
			            roleList = getUserRoles(ownedEntity, userLogin, delegator);
			
			        	String statusId = null;
			        	if (hasStatusOp && hasStatusField) {
			        		statusId = entity.getString("statusId");
			    		}
			        	String privilegeEnumId = null;
			        	if (hasPrivilegeOp && hasPrivilegeField) {
			        		privilegeEnumId = entity.getString("privilegeEnumId");
			    		}
			           	
			        	passed = hasMatch(entityName, targetOperationEntityList, roleList, hasPurposeOp, purposeList, hasStatusOp, statusId, hasPrivilegeOp, privilegeEnumId);
			        	if (passed)
			        		break;
			            alreadyCheckedIds.add(ownedEntityId);
			            purposes.put(ownedEntityId, purposeList);
			            //roles.put(ownedEntityId, roleList);
			           	ownedEntityId = ownedEntity.getString("owned" + entityName + "Id");
			           	ownedEntity = delegator.findByPrimaryKeyCache(entityName,UtilMisc.toMap(pkFieldName, ownedEntityId));
		           	}
	           	}
	           	if (passed)
	           		break;
	        }
        }
        
        
        // Check parents
        iter = entityIdList.iterator();
        while (iter.hasNext()) {
        	String entityId = (String)iter.next();
           	GenericValue entity = (GenericValue)entities.get(entityId);
            purposeList = (List)purposes.get(entityId);
            roleList = getUserRoles(entity, userLogin, delegator);

        	String statusId = null;
        	if (hasStatusOp && hasStatusField) {
        		statusId = entity.getString("statusId");
    		}
        	String privilegeEnumId = null;
        	if (hasPrivilegeOp && hasPrivilegeField) {
        		privilegeEnumId = entity.getString("privilegeEnumId");
    		}
           	
        	passed = hasMatch(entityName, targetOperationEntityList, roleList, hasPurposeOp, purposeList, hasStatusOp, statusId, hasPrivilegeOp, privilegeEnumId);
        	if (passed)
        		break;
            alreadyCheckedIds.add(entityId);
        }
        
        return passed;
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
    
    public static String getPkFieldName(String entityName, ModelEntity modelEntity) {
        List pkFieldNames = modelEntity.getPkFieldNames();
        String idFieldName = null;
        if (pkFieldNames.size() > 0) {
        	idFieldName = (String)pkFieldNames.get(0);
        }
        return idFieldName;
    }
    public static boolean hasMatch(String entityName, List targetOperations, List roles, boolean hasPurposeOp, List purposes, boolean hasStatusOp, String targStatusId, boolean hasPrivilegeOp, String targPrivilegeEnumId) {
        boolean isMatch = false;
        int targPrivilegeSeq = 0;
//        if (UtilValidate.isNotEmpty(targPrivilegeEnumId) && !targPrivilegeEnumId.equals("_NA_") && !targPrivilegeEnumId.equals("_00_") ) {
            // need to do a lookup here to find the seq value of targPrivilegeEnumId.
            // The lookup could be a static store or it could be done on Enumeration entity.
//        }
        String lcEntityName = entityName.toLowerCase();
        Iterator targetOpsIter = targetOperations.iterator();
        while (targetOpsIter.hasNext() ) {
            GenericValue targetOp = (GenericValue)targetOpsIter.next();
            String testRoleTypeId = (String)targetOp.get("roleTypeId");
            String testContentPurposeTypeId = null;
            if (hasPurposeOp)
                testContentPurposeTypeId = (String)targetOp.get(lcEntityName + "PurposeTypeId");
            String testStatusId = null;
            if (hasStatusOp)
                testStatusId = (String)targetOp.get("statusId");
            String testPrivilegeEnumId = null;
            if (hasPrivilegeOp)
                testPrivilegeEnumId = (String)targetOp.get("privilegeEnumId");
            int testPrivilegeSeq = 0;

            boolean purposesCond = ( !hasPurposeOp || (purposes != null && purposes.contains(testContentPurposeTypeId) ) || testContentPurposeTypeId.equals("_NA_") ); 
            boolean statusCond = ( !hasStatusOp || testStatusId.equals("_NA_") || (targStatusId != null && targStatusId.equals(testStatusId) ) ); 
            boolean privilegeCond = ( !hasPrivilegeOp || testPrivilegeEnumId.equals("_NA_") || testPrivilegeSeq <= targPrivilegeSeq || testPrivilegeEnumId.equals(targPrivilegeEnumId) ); 
            boolean roleCond = ( testRoleTypeId.equals("_NA_") || (roles != null && roles.contains(testRoleTypeId) ) );

 
            if (purposesCond && statusCond && privilegeCond && roleCond) {
                
                    isMatch = true;
                    break;
            }
        }
        return isMatch;
    }
    
    /**
     * getRelatedPurposes
     */
    public static List getRelatedPurposes(GenericValue entity, List passedPurposes) {

        if(entity == null) return passedPurposes;

        List purposeIds = null;
        if (purposeIds == null) {
            purposeIds = new ArrayList( );
        } else {
            purposeIds = new ArrayList( passedPurposes );
        }

        String entityName = entity.getEntityName();
        String lcEntityName = entityName.toLowerCase();

        List purposes = null;
        try {
            purposes = entity.getRelatedCache(entityName + "Purpose");
        } catch (GenericEntityException e) {
            Debug.logError(e, "No associated purposes found. ", module);
        }

        Iterator purposesIter = purposes.iterator();
        while (purposesIter.hasNext() ) {
            GenericValue val = (GenericValue)purposesIter.next();
            purposeIds.add(val.get(lcEntityName + "PurposeTypeId"));
        }
        

        return purposeIds;
    }
 

    /**
     * getUserRoles
     * Queries for the ContentRoles associated with a Content entity
     * and returns the ones that match the user.
     * Follows group parties to see if the user is a member.
     */
    public static List getUserRoles(GenericValue entity, GenericValue userLogin, GenericDelegator delegator) throws GenericEntityException {

    	String entityName = entity.getEntityName();
    	List roles = new ArrayList();
        if(entity == null) return roles;
            // TODO: Need to use ContentManagementWorker.getAuthorContent first

 
        roles.remove("OWNER"); // always test with the owner of the current content
        if ( entity.get("createdByUserLogin") != null && userLogin != null) {
            String userLoginId = (String)userLogin.get("userLoginId");
            String userLoginIdCB = (String)entity.get("createdByUserLogin");
            //if (Debug.infoOn()) Debug.logInfo("userLoginId:" + userLoginId + ": userLoginIdCB:" + userLoginIdCB + ":", null);
            if (userLoginIdCB.equals(userLoginId)) {
                roles.add("OWNER");
                //if (Debug.infoOn()) Debug.logInfo("in getUserRoles, passedRoles(0):" + passedRoles, null);
            }
        }
        
        String partyId = (String)userLogin.get("partyId");
        List relatedRoles = null;
        List tmpRelatedRoles = entity.getRelatedCache(entityName + "Role");
        relatedRoles = EntityUtil.filterByDate(tmpRelatedRoles);
        if(relatedRoles != null ) {
            Iterator rolesIter = relatedRoles.iterator();
            while (rolesIter.hasNext() ) {
                GenericValue contentRole = (GenericValue)rolesIter.next();
                String roleTypeId = (String)contentRole.get("roleTypeId");
                String targPartyId = (String)contentRole.get("partyId");
                if (targPartyId.equals(partyId)) {
                    if (!roles.contains(roleTypeId))
                        roles.add(roleTypeId);
                    if (roleTypeId.equals("AUTHOR") && !roles.contains("OWNER"))
                        roles.add("OWNER");
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
    
}
