/*
 * $Id: ProductionRunServices.java,v 1.1 2003/11/27 15:38:03 holivier Exp $
 *
 * Copyright (c) 2001, 2002, 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.manufacturing.jobshopmgt;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;

/**
 * Services for Production Run maintenance
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class ProductionRunServices {

    public static final String module = ProductionRunServices.class.getName();
    public static final String resource = "ManufacturingUiLabels";

    /**
     * Deletes a ProductionRun.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map deleteProductionRun(DispatchContext ctx, Map context) {

        /*
         * pretty serious operation, would delete:
         * - Party
         * - PartyRole
         * - PartyRelationship: from and to
         * - PartyDataObject
         * - Person or PartyGroup
         * - PartyContactMech, but not ContactMech itself
         * - PartyContactMechPurpose
         * - Order?
         *
         * We may want to not allow this, but rather have some sort of delete flag for it if it's REALLY that big of a deal...
         */
        return ServiceUtil.returnError("Cannot delete party, operation not yet implemented");
    }

    /**
     * Creates a Production Run.
     *  - check if routing - product link exist
     *  - check if product have a Bill Of Material
     *  - chedk if routing have routingTask
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters, productId routingId, quantity, startDate.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map createProductionRun(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();        
       	Security security = ctx.getSecurity();
        Timestamp now = UtilDateTime.nowTimestamp();
		List toBeStored = new LinkedList();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
       	GenericValue userLogin = (GenericValue) context.get("userLogin");
/* TODO: security management  
		if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
			return partyId;
		}
*/
        String productId = (String) context.get("productId");
		String workEffortId = (String) context.get("routingId");
		String statusId = "ROU_PROD_TEMPLATE";
		GenericValue workEffortProduct = null, routing = null;
		List productBom = null, routingTask = null ;

		try {
			workEffortProduct = delegator.findByPrimaryKey("WorkEffortGoodStandard", UtilMisc.toMap("productId", productId,"workEffortId",workEffortId));
			routing = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId));
// TODO: sort by seq and filter by date
			productBom = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId,"productAssocTypeId","MANUF_COMPONENT"));
// TODO: sort by seq and filter by date
			routingTask = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdFrom", productId,"workEffortAssocTypeId","ROUTING_COMPONENT"));
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
		}
		if (workEffortProduct == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductRoutingNotExist", locale));
		if (productBom == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductHasNoBom", locale));
		if (routingTask == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingHasNoRoutingTask", locale));

		Map serviceContext = UtilMisc.toMap("workEffortTypeId", "ROUTING", "workEffortName", routing.getString("workEffortName"),"currentStatusId","ROU_ACTIVE", "userLogin", userLogin);
		Map resultService = null;
		try {
			resultService = dispatcher.runSync("createWorkEffort", serviceContext);
		} catch (GenericServiceException e) {
			Debug.logError(e, "Problem calling the createWorkEffort service", module);
		}

		msgResult.add(resultService.get("workEffortId"));

        // message example
//		in properties files there is: cart.add_category_defaults=Added ${totalQuantity} items to the cart.
//		Map messageMap = UtilMisc.toMap("totalQuantity", UtilFormatOut.formatQuantity(totalQuantity) );
//	   request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource, "cart.add_category_defaults",
//									 messageMap, locale ));
//       if (partyId != null && Character.isDigit(partyId.charAt(0))) {
//            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "party.id_is_digit", locale));
//        }

        // partyId might be empty, so check it and get next seq party id if empty
/*        if (partyId == null || partyId.length() == 0) {
           Long newId = delegator.getNextSeqId("Party");

            if (newId == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "party.id_generation_failure", locale));
            } else {
                partyId = newId.toString();
            }
        

            // create a party if one doesn't already exist
            Map newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON", "createdDate", now, "lastModifiedDate", now);
            if (userLogin != null) {
                newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            }
            party = delegator.makeValue("Party", newPartyMap);
            toBeStored.add(party);
        }

        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (person != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.create.person_exists", locale));
        }

        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
        person.setNonPKFields(context);
        toBeStored.add(person);

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.create.db_error", new Object[] { e.getMessage() }, locale));
        } 
        person.setNonPKFields(context);
            person.store();
            if (Debug.verboseOn()) Debug.logVerbose("List: " + c, module);
            if (Debug.infoOn()) Debug.logInfo("PartyFromEmail number found: " + c.size(), module);
            exprs.add(new EntityExpr("infoString", true, EntityOperator.LIKE, "%" + email.toUpperCase() + "%", true));
            List c = EntityUtil.filterByDate(delegator.findByAnd("PartyAndContactMech", exprs, UtilMisc.toList("infoString")), true);
*/
		
        result.put("msgResult", msgResult);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Updates a Person.
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_UPDATE permission.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
/*    public static Map updatePerson(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PARTYMGR", "_UPDATE");

        if (result.size() > 0)
            return result;

        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.update.read_failure", new Object[] { e.getMessage() }, locale));
        }

        if (person == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.update.not_found", locale));
        }

        person.setNonPKFields(context);

        try {
            person.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "person.update.success", locale));
        return result;
    }
*/
}
