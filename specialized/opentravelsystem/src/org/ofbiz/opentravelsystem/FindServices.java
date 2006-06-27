/*
 * $Id: FindServices.java 6206 2005-11-29 05:33:53Z jonesde $
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.opentravelsystem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.base.util.Debug;

/**
 * FindServices Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev: 6206 $
 * @since      2.2
 */
public class FindServices {

    public static final String module = FindServices.class.getName();


    /**
     * prepareFind
     *
     * This is a generic method that expects entity data affixed with special suffixes
     * to indicate their purpose in formulating an SQL query statement.
     */
    public static Map prepareFind(DispatchContext dctx, Map context) {
        String entityName = (String) context.get("entityName");
        String orderBy = (String) context.get("orderBy");
        Map inputFields = (Map) context.get("inputFields"); // Input
        String noConditionFind = (String) context.get("noConditionFind");
        if (UtilValidate.isEmpty(noConditionFind)) {
            // try finding in inputFields Map
            noConditionFind = (String) inputFields.get("noConditionFind");
        }
        String filterByDate = (String) context.get("filterByDate");
        if (UtilValidate.isEmpty(filterByDate)) {
            // try finding in inputFields Map
            filterByDate = (String) inputFields.get("filterByDate");
        }

        // parameters run thru UtilHttp.getParameterMap
        Map queryStringMap = new HashMap();
        Map origValueMap = new HashMap();
        HashMap normalizedFields = org.ofbiz.common.FindServices.prepareField(inputFields, queryStringMap, origValueMap);

        // Now use only the values that correspond to entity fields to build
        //   an EntityConditionList
        GenericDelegator delegator = dctx.getDelegator();

        GenericValue entityValue = delegator.makeValue(entityName, new HashMap());

        ModelEntity modelEntity = entityValue.getModelEntity();
        List keys = modelEntity.getAllFieldNames();
        ArrayList tmpList = org.ofbiz.common.FindServices.createCondition(keys, normalizedFields, queryStringMap, origValueMap);

        if (!UtilValidate.isEmpty(filterByDate) && "Y".equals(filterByDate)) {
            EntityCondition filterByDateCondition = EntityUtil.getFilterByDateExpr();
            tmpList.add(filterByDateCondition);
        }
        
        // get the organizationPartyId, if defined limit the search result depending on the entity
        GenericValue userLogin = (GenericValue) inputFields.get("userLogin");
        String organizationPartyId =  null;
        if (userLogin != null)   
            organizationPartyId =  (String)  userLogin.getString("partyId");
        /*
         * Only insert extra find string with key prefix if the logged on uersId was found in the partyPreeference table.
         * and also only insert extra find string if another selection is made or the noConditionFind was set to true
         */
//        Debug.logInfo("=====orgPartyId:" + organizationPartyId + " list empty?:" + UtilValidate.isEmpty(tmpList) + " nocondfind:" +  noConditionFind + "- entity: " + entityName,module);
        if (organizationPartyId != null && (UtilValidate.isNotEmpty(tmpList) || (noConditionFind != null && noConditionFind.equals("Y")))) {
        	GenericValue partyAcctgPreference = null;
        	try{
        		partyAcctgPreference = delegator.findByPrimaryKeyCache("PartyAcctgPreference",UtilMisc.toMap("partyId",organizationPartyId));
        	} catch (GenericEntityException ee) {
                return ServiceUtil.returnError("Error getting partyAcctgPreference: " + ee.getMessage());
            }
        	
        	EntityCondition extraCond = null;
        	if (partyAcctgPreference != null) {
        		String companyPrefix = partyAcctgPreference.getString("invoiceIdPrefix").concat("%");
        		if (entityName.equals("Product")) {
        			extraCond = new EntityExpr("productId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else if (entityName.equals("OrderHeader")) {
        			extraCond = new EntityExpr("orderId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else if (entityName.equals("ProductCategory")) {
        			extraCond = new EntityExpr("productCategoryId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else if (entityName.equals("FixedAsset")) {
        			extraCond = new EntityExpr("fixedAssetId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else if (entityName.equals("Invoice")) {
        			extraCond = new EntityExpr("invoiceId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else if (entityName.equals("Payment")) {
        			extraCond = new EntityExpr("paymentId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else if (entityName.equals("Party") 
        				|| entityName.equals("PartyRelationshipAndPartyDetail")
        				|| entityName.equals("Party")
        				|| entityName.equals("PartyGroup")
        				|| entityName.equals("Person")
        				|| entityName.equals("UserLogin")
        				|| entityName.equals("PartyAndUserLoginAndPerson")
        				|| entityName.equals("PartyNameView"))  {
        			extraCond = new EntityExpr("partyId", EntityOperator.LIKE, companyPrefix); 
        		}
                else if (entityName.equals("ElectronicText") || entityName.equals("DataResource"))  {
                    extraCond = new EntityExpr("dataResourceId", EntityOperator.LIKE, companyPrefix); 
                }
                else if (entityName.equals("ContentAssocViewFrom") || entityName.equals("ContentAssocOptViewFrom"))  {
                    extraCond = new EntityExpr("contentId", EntityOperator.LIKE, companyPrefix); 
                }
                else if (entityName.equals("ContentAssocViewTo") || entityName.equals("ContentAssocOptViewTo"))  {
                    extraCond = new EntityExpr("contentId", EntityOperator.LIKE, companyPrefix); 
                }
                else if (entityName.equals("Content"))  {
                    extraCond = new EntityExpr("contentId", EntityOperator.LIKE, companyPrefix); 
                }
        		else if (entityName.equals("ContactList")
        				|| entityName.equals("ContactListParty")
        				|| entityName.equals("ContactListPartyStatus")
        				|| entityName.equals("ContactListPartyAndStatus"))  {
        			extraCond = new EntityExpr("contactListId", EntityOperator.LIKE, companyPrefix); 
        		}
        		else {
                	Debug.logInfo("OrganizationPartyId found but no limitation on entity: " + entityName,module);
        		}
        	}
        	// add extra condition if not empty
        	if (extraCond != null) {
        		tmpList.add(extraCond);
        	}
        }
        
        EntityConditionList exprList = null;
        if (tmpList.size() > 0) {
            exprList = new EntityConditionList(tmpList, (EntityJoinOperator) EntityOperator.AND);
        }
        
        List orderByList = null;
        if (UtilValidate.isNotEmpty(orderBy)) {
            orderByList = StringUtil.split(orderBy,"|");
        }

        Map results = ServiceUtil.returnSuccess();
        Map reducedQueryStringMap = org.ofbiz.common.FindServices.buildReducedQueryString(inputFields, entityName, delegator);
        reducedQueryStringMap.put("noConditionFind", noConditionFind);
        reducedQueryStringMap.put("filterByDate", filterByDate);
        String queryString = UtilHttp.urlEncodeArgs(reducedQueryStringMap);
        results.put("queryString", queryString);
        results.put("queryStringMap", reducedQueryStringMap);
        
        results.put("orderByList", orderByList);
        results.put("entityConditionList", exprList);
        return results;
    }
    /**
     * Returns the first generic item of the service 'performFind'
     * Same parameters as performFind service but returns a single GenericValue
     * 
     * @param dctx
     * @param context
     * @return
     */
    public static Map performFindItem(DispatchContext dctx, Map context) {
        Map result = org.ofbiz.common.FindServices.performFind(dctx,context);
        
        List list = null;
        GenericValue item= null;
        try{
            EntityListIterator it = (EntityListIterator) result.get("listIt");
            list = it.getPartialList(1, 1); // list starts at '1'
            if (list != null && list.size() > 0 ) {
                item = (GenericValue) list.get(0);
            }
            it.close();
        } catch (Exception e) {
            Debug.logInfo("Problem getting list Item" + e,module);
        }
        
        result.put("item",item);
        result.remove("listIt");
        return result;
    }
}
