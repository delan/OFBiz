/*
 * $Id: ProductionRunServices.java,v 1.5 2004/05/09 21:13:58 holivier Exp $
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

import org.ofbiz.manufacturing.techdata.TechDataServices;

/**
 * Services for Production Run maintenance
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Revision: 1.5 $
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
         * pretty serious operation, status depending would delete:
         * - task
         * - goods
         * - inventoryItem
         * - ...
         *
         * The analyse is plan but I don't know when ;-) ...
         */
        return ServiceUtil.returnError("Cannot delete productionRun , operation not yet implemented");
    }

    /**
     * Creates a Production Run.
     *  <li> check if routing - product link exist
     *  <li> check if product have a Bill Of Material
     *  <li> check if routing have routingTask
     *  <li> create the workEffort for ProductionRun
     *  <li> create the WorkEffortGoodStandard for link between ProductionRun and the product it will produce
     *  <li> for each valid routingTask of the routing create a workeffort-task
     *  <li> for the first routingTask, create for all the valid productIdTo with no associateRoutingTask  a WorkEffortGoodStandard
     *  <li> for each valid routingTask of the routing and valid productIdTo associate with this RoutingTask create a WorkEffortGoodStandard
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters, productId, routingId, pRQuantity, startDate, workEffortName, description
     * @return Map with the result of the service, the output parameters.
     */
    public static Map createProductionRun(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();        
       	Security security = ctx.getSecurity();
        Timestamp now = UtilDateTime.nowTimestamp();
	    List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
       	GenericValue userLogin = (GenericValue) context.get("userLogin");
        /* TODO: security management  and finishing cleaning (ex copy from PartyServices.java)
		if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
			return partyId;
		}
        */
        String productId = (String) context.get("productId");
		String workEffortId = (String) context.get("routingId");
		Timestamp  startDate =  (Timestamp) context.get("startDate");
		String statusId = "PRUN_CREATED";
		GenericValue routing = null, product = null;
		List workEffortProducts = null, productBoms = null, routingTaskAssocs = null ;

		try {
			workEffortProducts = delegator.findByAnd("WorkEffortGoodStandard", UtilMisc.toMap("productId", productId,"workEffortId",workEffortId));
			routing = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId));
			product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
// TODO: sort by seq and filter by date, pour ne prendre que les composants valide à la date de lancement de l'OF
			productBoms = product.getRelatedByAnd("MainProductAssoc",UtilMisc.toMap("productAssocTypeId","MANUF_COMPONENT"));
// TODO:   filter by date, pour ne prendre que les opérations valide à la date de lancement de l'OF
			routingTaskAssocs = routing.getRelated("FromWorkEffortAssoc",UtilMisc.toMap("workEffortAssocTypeId","ROUTING_COMPONENT"), UtilMisc.toList("sequenceNum","fromDate"));
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
		}
//TODO: test sur quantité vide et choix de la bonne gamme
		if (workEffortProducts == null) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductRoutingNotExist", locale));
		if (productBoms == null || productBoms.size() == 0) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductHasNoBom", locale));
		if (routingTaskAssocs == null || routingTaskAssocs.size()==0) 
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingHasNoRoutingTask", locale));

        // ProductionRun header creation,   
        String workEffortName = (String) context.get("workEffortName");
		String description = (String) context.get("description");
		Double pRQuantity = (Double) context.get("pRQuantity");
        if (workEffortName== null) workEffortName = product.getString("productName") + "-" + routing.getString("workEffortName");
		Map serviceContext = new HashMap();
		serviceContext.put("workEffortTypeId", "PROD_ORDER_HEADER");
		serviceContext.put("workEffortPurposeTypeId","WEPT_PRODUCTION_RUN");
		serviceContext.put("currentStatusId","PRUN_CREATED");
		serviceContext.put("workEffortName", workEffortName);
        serviceContext.put("description",description);
		serviceContext.put("estimatedStartDate",startDate);
		serviceContext.put("quantityToProduce", pRQuantity);
		serviceContext.put("userLogin", userLogin);
		Map resultService = null;
		try {
			resultService = dispatcher.runSync("createWorkEffort", serviceContext);
		} catch (GenericServiceException e) {
			Debug.logError(e, "Problem calling the createWorkEffort service", module);
		}
		String productionRunId = (String) resultService.get("workEffortId");
		if (Debug.infoOn()) Debug.logInfo("ProductioRun created: " + productionRunId, module);
        
        // ProductionRun,  product will be produce creation = WorkEffortGoodStandard for the productId
        serviceContext.clear();
        serviceContext.put("workEffortId",productionRunId);
        serviceContext.put("productId",productId);
        serviceContext.put("statusId","WIP_OUTGOING_FULFIL");
        serviceContext.put("estimatedQuantity",pRQuantity);
        serviceContext.put("fromDate",startDate);
        serviceContext.put("userLogin", userLogin);
        try {
            resultService = dispatcher.runSync("createWorkEffortGoodStandard", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the createWorkEffortGoodStandard service", module);
        }

        // Multi creation (like clone) ProductionRunTask and GoodAssoc
		Iterator  rt = routingTaskAssocs.iterator();
		boolean first = true;
 		while (rt.hasNext()) {
	 		GenericValue routingTaskAssoc = (GenericValue) rt.next();
			if (TechDataServices.routingTaskAssocIsValid(routingTaskAssoc, startDate)) {
				GenericValue routingTask = null;
				try {
				  routingTask = routingTaskAssoc.getRelatedOne("ToWorkEffort");
				} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(),  module);
			    }   
                // Calculate the estimatedCompletionDate 
                long duringTime = (long)  (routingTask.getDouble("estimatedSetupMillis").doubleValue() + (routingTask.getDouble("estimatedMilliSeconds").doubleValue() * pRQuantity.doubleValue()));
                Timestamp endDate = TechDataServices.addForward(TechDataServices.getTechDataCalendar(routingTask),startDate, duringTime);

				serviceContext.clear();
                serviceContext.put("priority", routingTaskAssoc.get("sequenceNum"));
                serviceContext.put("workEffortPurposeTypeId", routingTask.get("workEffortPurposeTypeId"));
                serviceContext.put("workEffortName",routingTask.get("workEffortName"));
                serviceContext.put("description",routingTask.get("description"));
                serviceContext.put("fixedAssetId",routingTask.get("fixedAssetId"));
                serviceContext.put("estimatedSetupMillis",routingTask.get("estimatedSetupMillis"));
                serviceContext.put("estimatedMilliSeconds",routingTask.get("estimatedMilliSeconds"));
				serviceContext.put("workEffortTypeId", "PROD_ORDER_TASK");
				serviceContext.put("currentStatusId","PRUN_CREATED");
				serviceContext.put("workEffortParentId", productionRunId);
				serviceContext.put("estimatedStartDate",startDate);
                serviceContext.put("estimatedCompletionDate",endDate);
				serviceContext.put("quantityToProduce", pRQuantity);
				serviceContext.put("userLogin", userLogin);
				resultService = null;
				try {
					resultService = dispatcher.runSync("createWorkEffort", serviceContext);
				} catch (GenericServiceException e) {
					Debug.logError(e, "Problem calling the createWorkEffort service", module);
				}
				String productionRunTaskId = (String) resultService.get("workEffortId");
				if (Debug.infoOn()) Debug.logInfo("ProductionRunTaskId created: " + productionRunTaskId, module);
				if ( first ) {
					first = false;
					Iterator  pb = productBoms.iterator();
					while (pb.hasNext()) {
						GenericValue productBom = (GenericValue) pb.next();
						if (TechDataServices.productBomIsValid(productBom, startDate)  && 
                        (productBom.getString("routingWorkEffortId") == null || productBom.getString("routingWorkEffortId").equals(productionRunTaskId))) {
							serviceContext.clear();
							serviceContext.put("workEffortId", productionRunTaskId);
							serviceContext.put("productId", productBom.get("productIdTo"));
							serviceContext.put("statusId","WIP_INCOMING_FULFIL");
							serviceContext.put("fromDate",productBom.get("fromDate"));
							double scrapFactor = (productBom.get("scrapFactor") != null)? productBom.getDouble("scrapFactor").doubleValue() : 0;
							serviceContext.put("estimatedQuantity", new Double(Math.floor((productBom.getDouble("quantity").doubleValue() * pRQuantity.doubleValue() / (1-(scrapFactor / 100))) + 0.5)));
							serviceContext.put("userLogin", userLogin);
							resultService = null;
							try {
								resultService = dispatcher.runSync("createWorkEffortGoodStandard", serviceContext);
							} catch (GenericServiceException e) {
								Debug.logError(e, "Problem calling the createWorkEffortGoodStandard service", module);
							}
							if (Debug.infoOn()) Debug.logInfo("ProductLink created for productId: " + productBom.getString("productIdTo"), module);
						}
					}
				}
//              TODO: generate the WorkEffortGoodStandard if there are some BOM at this RoutingTask pour ça il faut une jointure de table BOM et ROU
                startDate = endDate;
	 		}
 		}
        // update the estimatedCompletionDate field for the productionRun
        serviceContext.clear();
        serviceContext.put("workEffortId",productionRunId);
        serviceContext.put("estimatedCompletionDate",startDate);
        serviceContext.put("userLogin", userLogin);
        resultService = null;
        try {
            resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the updateWorkEffort service", module);
        }
      
        result.put("productionRunId", productionRunId);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunCreated",UtilMisc.toMap("productionRunId", productionRunId), locale));
        return result;
    }
    /**
     * Update a Production Run.
     *  <li> update field and after recalculate the entire ProductionRun data (routingTask and productComponent)
     *  <li> create the WorkEffortGoodStandard for link between ProductionRun and the product it will produce
     *  <li> for each valid routingTask of the routing create a workeffort-task
     *  <li> for the first routingTask, create for all the valid productIdTo with no associateRoutingTask  a WorkEffortGoodStandard
     *  <li> for each valid routingTask of the routing and valid productIdTo associate with this RoutingTask create a WorkEffortGoodStandard
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters, productId, routingId, quantity, estimatedStartDate, workEffortName, description
     * @return Map with the result of the service, the output parameters.
     */
    public static Map updateProductionRun(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        /* TODO: security management  and finishing cleaning (ex copy from PartyServices.java)
        if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
            return partyId;
        }
        */
        String productionRunId = (String) context.get("productionRunId");
        if (! UtilValidate.isEmpty(productionRunId)) {
            ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
            if (productionRun.exist()){

                Double quantity = (Double) context.get("quantity");
                if (quantity != null &&  ! quantity.equals(productionRun.getQuantity())) 
                        productionRun.setQuantity(quantity);

                Timestamp  estimatedStartDate =  (Timestamp) context.get("estimatedStartDate");
                if (estimatedStartDate != null && ! estimatedStartDate.equals(productionRun.getEstimatedStartDate())) 
                        productionRun.setEstimatedStartDate(estimatedStartDate);
                
                String  workEffortName = (String) context.get("workEffortName");
                if (workEffortName != null) productionRun.setProductionRunName(workEffortName);
                
                String  description = (String) context.get("description");
                if (description != null) productionRun.setDescription(description);
                
                if (productionRun.store()) return ServiceUtil.returnSuccess();
                else {
                    Debug.logError("productionRun.store() fail for productionRunId ="+productionRunId,module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
                }
            }
            Debug.logError("no productionRun for productionRunId ="+productionRunId,module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
        }       
        Debug.logError("service updateProductionRun call with productionRunId empty",module);
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
    }
    /**
     * check if field for routingTask update are correct and if need recalculated data in Production Run.
     *  Check<ul>
     *  <li> if estimatedStartDate is not before Production Run estimatedStartDate.</ul>
     *  <li> if there is not a another routingTask with the same priority
     *  If priority or estimatedStartDate has changed recalculated data for routingTask after that one.
     * <br> update the productionRun
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters, productId, routingId, priority, estimatedStartDate, estimatedSetupMillis, estimatedMilliSeconds
     * @return Map with the result of the service, the output parameters, estimatedCompletionDate.
     */
    public static Map checkUpdatePrunRoutingTask(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        /* TODO: security management  and finishing cleaning (ex copy from PartyServices.java)
        if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
            return partyId;
        }
        */
        String productionRunId = (String) context.get("productionRunId");
        String routingTaskId = (String) context.get("routingTaskId");
        if (! UtilValidate.isEmpty(productionRunId) && ! UtilValidate.isEmpty(routingTaskId)) {
            ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
            if (productionRun.exist()){

                Timestamp estimatedStartDate = (Timestamp) context.get("estimatedStartDate");
                Timestamp pRestimatedStartDate = productionRun.getEstimatedStartDate();
                if (pRestimatedStartDate.after(estimatedStartDate))
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingTaskStartDateBeforePRun", locale));

                Long priority = (Long) context.get("priority");
                List pRRoutingTasks = productionRun.getProductionRunRoutingTasks();
                boolean first = true;
                for (Iterator iter=pRRoutingTasks.iterator();iter.hasNext();){
                    GenericValue routingTask = (GenericValue) iter.next();
                    if (priority.equals(routingTask.get("priority")) && ! routingTaskId.equals(routingTask.get("workEffortId")))
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingTaskSeqIdAlreadyExist", locale));
                    if (routingTaskId.equals(routingTask.get("workEffortId"))){
                        routingTask.set("estimatedSetupMillis", context.get("estimatedSetupMillis"));
                        routingTask.set("estimatedMilliSeconds", context.get("estimatedMilliSeconds"));
                        if (first){    // for the first routingTask the estimatedStartDate update imply estimatedStartDate productonRun update
                            if (! estimatedStartDate.equals(pRestimatedStartDate)){
                                productionRun.setEstimatedStartDate(estimatedStartDate);
                            }
                        }
                        // the priority has been changed
                        if (! priority.equals(routingTask.get("priority"))){
                            routingTask.set("priority", priority);
                            // update the routingTask List and re-read it to be able to have it sorted with the new value
                            if ( ! productionRun.store()) {
                                Debug.logError("productionRun.store(), in routingTask.priority update, fail for productionRunId ="+productionRunId,module);
                                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
                            }
                            productionRun.clearRoutingTasksList();
                        }    
                    }
                    if (first) first = false;
                }
                productionRun.setEstimatedCompletionDate(productionRun.recalculateEstimatedCompletionDate(priority, estimatedStartDate));                       
                
                if (productionRun.store()) return ServiceUtil.returnSuccess();
                else {
                    Debug.logError("productionRun.store() fail for productionRunId ="+productionRunId,module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
                }
            }
            Debug.logError("no productionRun for productionRunId ="+productionRunId,module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
        }       
        Debug.logError("service updateProductionRun call with productionRunId empty",module);
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotUpdated", locale));
    }
}
