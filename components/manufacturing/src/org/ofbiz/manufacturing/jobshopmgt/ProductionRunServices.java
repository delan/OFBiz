/*
 * $Id$
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
import java.util.ArrayList;
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
import org.ofbiz.entity.util.SequenceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;

import org.ofbiz.manufacturing.techdata.TechDataServices;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.config.ProductConfigWrapper.ConfigOption;
/**
 * Services for Production Run maintenance
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Rev$
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
        // Mandatory input fields
        String productId = (String) context.get("productId");
        Timestamp  startDate =  (Timestamp) context.get("startDate");
        Double pRQuantity = (Double) context.get("pRQuantity");
        String facilityId = (String) context.get("facilityId");
        // Optional input fields
        String workEffortId = (String) context.get("routingId");
        String workEffortName = (String) context.get("workEffortName");
        String description = (String) context.get("description");
        
        GenericValue routing = null;
        GenericValue product = null;
        List workEffortProducts = null;
        List productBoms = null;
        List routingTaskAssocs = null;
        try {
            // Find the product
            product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            if (product == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductNotExists", locale));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // -------------------
        // Components
        // -------------------
        // The components are retrieved using the getManufacturingComponents service
        // (that performs a bom breakdown and if needed runs the configurator).
        List components = null;
        Map serviceContext = new HashMap();
        serviceContext.put("productId", productId); // the product that we want to manufacture
        serviceContext.put("quantity", pRQuantity); // the quantity that we want to manufacture
        Map resultService = null;
        try {
            resultService = dispatcher.runSync("getManufacturingComponents", serviceContext);
            components = (List)resultService.get("components"); // a list of objects representing the product's components
            if (workEffortId == null) {
                workEffortId = (String)resultService.get("workEffortId"); // the product routing id
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the getManufacturingComponents service", module);
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // -------------------
        // Routing and routing tasks
        // -------------------
        try {
            // Select the product's routing
            if (workEffortId == null) {
                // TODO: we should select the best product routing based on quantity to manufacture
                workEffortProducts = delegator.findByAnd("WorkEffortGoodStandard", UtilMisc.toMap("productId", productId, "statusId", "ROU_PROD_TEMPLATE"));
                workEffortProducts = EntityUtil.filterByDate(workEffortProducts);
                GenericValue workEffortProduct = EntityUtil.getFirst(workEffortProducts);
                if (workEffortProduct != null) {
                    workEffortId = workEffortProduct.getString("workEffortId");
                }
            }
            if (workEffortId != null) {
                routing = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
            }
            if (routing == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductRoutingNotExist", locale));
            }
            // The routing tasks are loaded
            routingTaskAssocs = routing.getRelated("FromWorkEffortAssoc",UtilMisc.toMap("workEffortAssocTypeId","ROUTING_COMPONENT"), UtilMisc.toList("sequenceNum","fromDate"));
            routingTaskAssocs = EntityUtil.filterByDate(routingTaskAssocs);
            if (routingTaskAssocs == null || routingTaskAssocs.size()==0) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingHasNoRoutingTask", locale));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // ProductionRun header creation,
        if (workEffortName == null) {
            String prdName = UtilValidate.isNotEmpty(product.getString("productName"))? product.getString("productName"): product.getString("productId");
            String wefName = UtilValidate.isNotEmpty(routing.getString("workEffortName"))? routing.getString("workEffortName"): routing.getString("workEffortId");
            workEffortName =  prdName + "-" + wefName;
        }
        
        serviceContext.clear();
        serviceContext.put("workEffortTypeId", "PROD_ORDER_HEADER");
        serviceContext.put("workEffortPurposeTypeId", "WEPT_PRODUCTION_RUN");
        serviceContext.put("currentStatusId", "PRUN_CREATED");
        serviceContext.put("workEffortName", workEffortName);
        serviceContext.put("description",description);
        serviceContext.put("facilityId", facilityId);
        serviceContext.put("estimatedStartDate",startDate);
        serviceContext.put("quantityToProduce", pRQuantity);
        serviceContext.put("userLogin", userLogin);
        try {
            resultService = dispatcher.runSync("createWorkEffort", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the createWorkEffort service", module);
            return ServiceUtil.returnError(e.getMessage());
        }
        String productionRunId = (String) resultService.get("workEffortId");
        if (Debug.infoOn()) {
            Debug.logInfo("ProductionRun created: " + productionRunId, module);
        }
        
        // ProductionRun,  product will be produce creation = WorkEffortGoodStandard for the productId
        serviceContext.clear();
        serviceContext.put("workEffortId", productionRunId);
        serviceContext.put("productId", productId);
        serviceContext.put("statusId", "WIP_OUTGOING_FULFIL");
        serviceContext.put("estimatedQuantity", pRQuantity);
        serviceContext.put("fromDate", startDate);
        serviceContext.put("userLogin", userLogin);
        try {
            resultService = dispatcher.runSync("createWorkEffortGoodStandard", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the createWorkEffortGoodStandard service", module);
            return ServiceUtil.returnError(e.getMessage());
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
                double setupTime = (routingTask.getDouble("estimatedSetupMillis") != null? routingTask.getDouble("estimatedSetupMillis").doubleValue(): 0);
                double taskTime = (routingTask.getDouble("estimatedMilliSeconds") != null? routingTask.getDouble("estimatedMilliSeconds").doubleValue(): 1);
                long duringTime = (long)  (setupTime + (taskTime * pRQuantity.doubleValue()));
                Timestamp endDate = TechDataServices.addForward(TechDataServices.getTechDataCalendar(routingTask),startDate, duringTime);
                
                serviceContext.clear();
                serviceContext.put("priority", routingTaskAssoc.get("sequenceNum"));
                serviceContext.put("workEffortPurposeTypeId", routingTask.get("workEffortPurposeTypeId"));
                serviceContext.put("workEffortName",routingTask.get("workEffortName"));
                serviceContext.put("description",routingTask.get("description"));
                serviceContext.put("fixedAssetId",routingTask.get("fixedAssetId"));
                serviceContext.put("workEffortTypeId", "PROD_ORDER_TASK");
                serviceContext.put("currentStatusId","PRUN_CREATED");
                serviceContext.put("workEffortParentId", productionRunId);
                serviceContext.put("facilityId", facilityId);
                serviceContext.put("estimatedStartDate",startDate);
                serviceContext.put("estimatedCompletionDate",endDate);
                serviceContext.put("estimatedSetupMillis", routingTask.get("estimatedSetupMillis"));
                serviceContext.put("estimatedMilliSeconds", routingTask.get("estimatedMilliSeconds"));
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
                // Now we iterate thru the components returned by the getManufacturingComponents service
                // TODO: if in the BOM a routingWorkEffortId is specified, but the task is not in the routing
                //       the component is not added to the production run.
                Iterator  pb = components.iterator();
                while (pb.hasNext()) {
                    // The components variable contains a list of BOMNodes:
                    // each node represents a product (component).
                    org.ofbiz.manufacturing.bom.BOMNode node = (org.ofbiz.manufacturing.bom.BOMNode) pb.next();
                    GenericValue productBom = node.getProductAssoc();
                    if ((productBom.getString("routingWorkEffortId") == null && first) || (productBom.getString("routingWorkEffortId") != null && productBom.getString("routingWorkEffortId").equals(routingTask.getString("workEffortId")))) {
                        serviceContext.clear();
                        serviceContext.put("workEffortId", productionRunTaskId);
                        // Here we get the ProductAssoc record from the BOMNode
                        // object to be sure to use the
                        // right component (possibly configured).
                        serviceContext.put("productId", node.getProduct().get("productId"));
                        serviceContext.put("statusId", "WIP_INCOMING_FULFIL");
                        serviceContext.put("fromDate", productBom.get("fromDate"));
                        // Here we use the getQuantity method to get the quantity already
                        // computed by the getManufacturingComponents service
                        //double scrapFactor = (productBom.get("scrapFactor") != null)? productBom.getDouble("scrapFactor").doubleValue() : 0;
                        //serviceContext.put("estimatedQuantity", new Double(Math.floor((productBom.getDouble("quantity").doubleValue() * pRQuantity.doubleValue() / (1-(scrapFactor / 100))) + 0.5)));
                        serviceContext.put("estimatedQuantity", new Double(node.getQuantity()));
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
                first = false;
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
        if (!UtilValidate.isEmpty(productionRunId)) {
            ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
            if (productionRun.exist()){
                
                if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_CREATED")) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunPrinted", locale));
                }

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
    
    public static Map changeProductionRunStatus(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String productionRunId = (String) context.get("productionRunId");
        String statusId = (String) context.get("statusId");
        
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        if (!productionRun.exist()){
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotExists", locale));
        }
        String currentStatusId = productionRun.getGenericValue().getString("currentStatusId");
        
        if (statusId != null && currentStatusId.equals(statusId)) {
            result.put("newStatusId", currentStatusId);
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", currentStatusId), locale));
            return result;
        }
        
        // PRUN_CREATED --> PRUN_DOC_PRINTED
        if (currentStatusId.equals("PRUN_CREATED") && (statusId == null || statusId.equals("PRUN_DOC_PRINTED"))) {
            // change only the production run (header) status to PRUN_DOC_PRINTED
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", productionRunId);
            serviceContext.put("currentStatusId", "PRUN_DOC_PRINTED");
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
            result.put("newStatusId", "PRUN_DOC_PRINTED");
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", "PRUN_DOC_PRINTED"), locale));
            return result;
        }
        
        // PRUN_DOC_PRINTED --> PRUN_RUNNING
        // this should be called only when the first task is started
        if (currentStatusId.equals("PRUN_DOC_PRINTED") && (statusId == null || statusId.equals("PRUN_RUNNING"))) {
            // change only the production run (header) status to PRUN_RUNNING
            // First check if there are production runs with precedence not still completed
            try {
                List mandatoryWorkEfforts = EntityUtil.filterByDate(delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdTo", productionRunId, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY")));
                for (int i = 0; i < mandatoryWorkEfforts.size(); i++) {
                    GenericValue mandatoryWorkEffort = (GenericValue)mandatoryWorkEfforts.get(i);
                    if (!(mandatoryWorkEffort.getString("currentStatusId").equals("PRUN_COMPLETED") ||
                         mandatoryWorkEffort.getString("currentStatusId").equals("PRUN_CLOSED"))) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChangedMandatoryProductionRunNotCompleted", locale));
                    }
                }
            } catch(GenericEntityException gee) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }

            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", productionRunId);
            serviceContext.put("currentStatusId", "PRUN_RUNNING");
            serviceContext.put("actualStartDate", UtilDateTime.nowTimestamp());
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
            result.put("newStatusId", "PRUN_RUNNING");
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", "PRUN_DOC_PRINTED"), locale));
            return result;
        }
        
        // PRUN_RUNNING --> PRUN_COMPLETED
        // this should be called only when the last task is completed
        if (currentStatusId.equals("PRUN_RUNNING") && (statusId == null || statusId.equals("PRUN_COMPLETED"))) {
            // change only the production run (header) status to PRUN_COMPLETED
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", productionRunId);
            serviceContext.put("currentStatusId", "PRUN_COMPLETED");
            serviceContext.put("actualCompletionDate", UtilDateTime.nowTimestamp());
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
            result.put("newStatusId", "PRUN_COMPLETED");
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", "PRUN_DOC_PRINTED"), locale));
            return result;
        }
        
        // PRUN_COMPLETED --> PRUN_CLOSED
        if (currentStatusId.equals("PRUN_COMPLETED") && (statusId == null || statusId.equals("PRUN_CLOSED"))) {
            // change the production run status to PRUN_CLOSED
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", productionRunId);
            serviceContext.put("currentStatusId", "PRUN_CLOSED");
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
            // change the production run tasks status to PRUN_CLOSED
            Iterator tasks = productionRun.getProductionRunRoutingTasks().iterator();
            while (tasks.hasNext()) {
                GenericValue task = (GenericValue)tasks.next();
                serviceContext.clear();
                serviceContext.put("workEffortId", task.getString("workEffortId"));
                serviceContext.put("currentStatusId", "PRUN_CLOSED");
                serviceContext.put("userLogin", userLogin);
                resultService = null;
                try {
                    resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
                }
            }
            result.put("newStatusId", "PRUN_CLOSED");
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", "PRUN_CLOSED"), locale));
            return result;
        }
        
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
    }

    public static Map changeProductionRunTaskStatus(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String productionRunId = (String) context.get("productionRunId");
        String taskId = (String) context.get("taskId");
        String statusId = (String) context.get("statusId");
        
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        if (!productionRun.exist()){
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotExists", locale));
        }
        List tasks = productionRun.getProductionRunRoutingTasks();
        GenericValue theTask = null;
        GenericValue oneTask = null;
        boolean allTaskCompleted = true;
        boolean allPrecTaskCompleted = true;
        for (int i = 0; i < tasks.size(); i++) {
            oneTask = (GenericValue)tasks.get(i);
            if (oneTask.getString("workEffortId").equals(taskId)) {
                theTask = oneTask;
            } else {
                if (theTask == null && allPrecTaskCompleted && !oneTask.getString("currentStatusId").equals("PRUN_COMPLETED")) {
                    allPrecTaskCompleted = false;
                }
                if (allTaskCompleted && !oneTask.getString("currentStatusId").equals("PRUN_COMPLETED")) {
                    allTaskCompleted = false;
                }
            }
        }
        if (theTask == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
        }
        
        String currentStatusId = theTask.getString("currentStatusId");
        
        if (statusId != null && currentStatusId.equals(statusId)) {
            result.put("newStatusId", currentStatusId);
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskStatusChanged",UtilMisc.toMap("newStatusId", currentStatusId), locale));
            return result;
        }
        
        // PRUN_CREATED --> PRUN_RUNNING
        // this should be called only when the first task is started
        if (currentStatusId.equals("PRUN_CREATED") && (statusId == null || statusId.equals("PRUN_RUNNING"))) {
            // change the production run task status to PRUN_RUNNING
            // if necessary change the production run (header) status to PRUN_RUNNING
            if (!allPrecTaskCompleted) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskCannotStartPrevTasksNotCompleted", locale));
            }
            if (productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_CREATED")) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskCannotStartDocsNotPrinted", locale));
            }
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", taskId);
            serviceContext.put("currentStatusId", "PRUN_RUNNING");
            serviceContext.put("actualStartDate", UtilDateTime.nowTimestamp());
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
            if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_RUNNING")) {
                serviceContext.clear();
                serviceContext.put("productionRunId", productionRunId);
                serviceContext.put("statusId", "PRUN_RUNNING");
                serviceContext.put("userLogin", userLogin);
                resultService = null;
                try {
                    resultService = dispatcher.runSync("changeProductionRunStatus", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeProductionRunStatus service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
                }
            }
            /*
            if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_RUNNING")) {
                serviceContext.clear();
                serviceContext.put("workEffortId", productionRunId);
                serviceContext.put("currentStatusId", "PRUN_RUNNING");
                serviceContext.put("userLogin", userLogin);
                resultService = null;
                try {
                    resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
                }
            }
            */
            result.put("newStatusId", "PRUN_RUNNING");
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", "PRUN_DOC_PRINTED"), locale));
            return result;
        }
        
        // PRUN_RUNNING --> PRUN_COMPLETED
        // this should be called only when the last task is completed
        if (currentStatusId.equals("PRUN_RUNNING") && (statusId == null || statusId.equals("PRUN_COMPLETED"))) {
            // change only the production run (header) status to PRUN_COMPLETED
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", taskId);
            serviceContext.put("currentStatusId", "PRUN_COMPLETED");
            serviceContext.put("actualCompletionDate", UtilDateTime.nowTimestamp());
            Double quantityToProduce = theTask.getDouble("quantityToProduce");
            if (quantityToProduce == null) {
                quantityToProduce = new Double(0);
            }
            Double quantityProduced = theTask.getDouble("quantityProduced");
            if (quantityProduced == null) {
                quantityProduced = new Double(0);
            }
            Double quantityRejected = theTask.getDouble("quantityRejected");
            if (quantityRejected == null) {
                quantityRejected = new Double(0);
            }
            double totalQuantity = quantityProduced.doubleValue() + quantityRejected.doubleValue();
            double diffQuantity = quantityToProduce.doubleValue() - totalQuantity;
            if (diffQuantity > 0) {
                quantityProduced = new Double(quantityProduced.doubleValue() + diffQuantity);
            }
            serviceContext.put("quantityProduced", quantityProduced);
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
            if (allTaskCompleted) {
                serviceContext.clear();
                serviceContext.put("productionRunId", productionRunId);
                serviceContext.put("statusId", "PRUN_COMPLETED");
                serviceContext.put("userLogin", userLogin);
                resultService = null;
                try {
                    resultService = dispatcher.runSync("changeProductionRunStatus", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
                }
            }
            result.put("newStatusId", "PRUN_COMPLETED");
            result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusChanged",UtilMisc.toMap("newStatusId", "PRUN_DOC_PRINTED"), locale));
            return result;
        }
        
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
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
                
                if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_CREATED")) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunPrinted", locale));
                }

                Timestamp estimatedStartDate = (Timestamp) context.get("estimatedStartDate");
                Timestamp pRestimatedStartDate = productionRun.getEstimatedStartDate();
                if (pRestimatedStartDate.after(estimatedStartDate)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingTaskStartDateBeforePRun", locale));
                }
                
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
                
                if (productionRun.store()) {
                    return ServiceUtil.returnSuccess();
                } else {
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
    
    public static Map addProductionRunComponent(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String productionRunId = (String)context.get("productionRunId");
        String productId = (String)context.get("productId");
        Double quantity = (Double) context.get("estimatedQuantity");
        // Optional input fields
        String workEffortId = (String)context.get("workEffortId");
        
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        List tasks = productionRun.getProductionRunRoutingTasks();
        if (tasks == null || tasks.size() == 0){
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
        }
        
        if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_CREATED")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunPrinted", locale));
        }

        if (workEffortId != null) {
            boolean found = false;
            for (int i = 0; i < tasks.size(); i++) {
                GenericValue oneTask = (GenericValue)tasks.get(i);
                if (oneTask.getString("workEffortId").equals(workEffortId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
            }
        } else {
            workEffortId = EntityUtil.getFirst(tasks).getString("workEffortId");
        }
        
        try {
            // Find the product
            GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            if (product == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductNotExists", locale));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        Map serviceContext = new HashMap();
        serviceContext.clear();
        serviceContext.put("workEffortId", workEffortId);
        serviceContext.put("productId", productId);
        serviceContext.put("statusId", "WIP_INCOMING_FULFIL");
        serviceContext.put("fromDate", now);
        serviceContext.put("estimatedQuantity", quantity);
        serviceContext.put("userLogin", userLogin);
        Map resultService = null;
        try {
            resultService = dispatcher.runSync("createWorkEffortGoodStandard", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the createWorkEffortGoodStandard service", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunComponentNotAdded", locale));
        }
        result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunComponentAdded",UtilMisc.toMap("productionRunId", productionRunId), locale));
        return result;
    }
    
    public static Map updateProductionRunComponent(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String productionRunId = (String)context.get("productionRunId");
        String productId = (String)context.get("productId");
        // Optional input fields
        String workEffortId = (String)context.get("workEffortId"); // the production run task
        Double quantity = (Double) context.get("estimatedQuantity");
        
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        List components = productionRun.getProductionRunComponents();
        if (components == null || components.size() == 0){
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunComponentNotExists", locale));
        }
        
        if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_CREATED")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunPrinted", locale));
        }

        boolean found = false;
        GenericValue theComponent = null;
        for (int i = 0; i < components.size(); i++) {
            theComponent = (GenericValue)components.get(i);
            if (theComponent.getString("productId").equals(productId)) {
                if (workEffortId != null) {
                    if (theComponent.getString("workEffortId").equals(workEffortId)) {
                        found = true;
                        break;
                    }
                } else {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
        }
        
        try {
            // Find the product
            GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            if (product == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductNotExists", locale));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        Map serviceContext = new HashMap();
        serviceContext.clear();
        serviceContext.put("workEffortId", theComponent.getString("workEffortId"));
        serviceContext.put("productId", productId);
        serviceContext.put("fromDate", theComponent.getTimestamp("fromDate"));
        if (quantity != null) {
            serviceContext.put("estimatedQuantity", quantity);
        }
        serviceContext.put("userLogin", userLogin);
        Map resultService = null;
        try {
            resultService = dispatcher.runSync("updateWorkEffortGoodStandard", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the updateWorkEffortGoodStandard service", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunComponentNotAdded", locale));
        }
        result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunComponentUpdated",UtilMisc.toMap("productionRunId", productionRunId), locale));
        return result;
    }
    
    public static Map addProductionRunRoutingTask(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String productionRunId = (String)context.get("productionRunId");
        String routingTaskId = (String)context.get("routingTaskId");
        Long priority = (Long)context.get("priority");
        
        // Optional input fields
        String workEffortName = (String)context.get("workEffortName");
        String description = (String)context.get("description");
        Timestamp estimatedStartDate = (Timestamp)context.get("estimatedStartDate");
        Timestamp estimatedCompletionDate = (Timestamp)context.get("estimatedCompletionDate");
        Double estimatedSetupMillis = (Double)context.get("estimatedSetupMillis");
        Double estimatedMilliSeconds = (Double)context.get("estimatedMilliSeconds");
        
        // The production run is loaded
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        Double pRQuantity = productionRun.getQuantity();
        if (pRQuantity == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
        }
        
        if (!productionRun.getGenericValue().getString("currentStatusId").equals("PRUN_CREATED")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunPrinted", locale));
        }

        if (estimatedStartDate != null) {
            Timestamp pRestimatedStartDate = productionRun.getEstimatedStartDate();
            if (pRestimatedStartDate.after(estimatedStartDate)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingTaskStartDateBeforePRun", locale));
            }
        }
        
        // The routing task is loaded
        GenericValue routingTask = null;
        try {
            routingTask = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", routingTaskId));
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(),  module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingTaskNotExists", locale));
        }
        if (routingTask == null) {
            Debug.logError("Routing task: " + routingTaskId + " is null.",  module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRoutingTaskNotExists", locale));
        }
        
        if (workEffortName == null) {
            workEffortName = (String)routingTask.get("workEffortName");
        }
        if (description == null) {
            description = (String)routingTask.get("description");
        }
        if (estimatedSetupMillis == null) {
            estimatedSetupMillis = (Double)routingTask.get("estimatedSetupMillis");
        }
        if (estimatedMilliSeconds == null) {
            estimatedMilliSeconds = (Double)routingTask.get("estimatedMilliSeconds");
        }
        if (estimatedStartDate == null) {
            estimatedStartDate = productionRun.getEstimatedStartDate();
        }
        if (estimatedCompletionDate == null) {
            // Calculate the estimatedCompletionDate
            long duringTime = (long)(estimatedSetupMillis.doubleValue() + (estimatedMilliSeconds.doubleValue() * pRQuantity.doubleValue()));
            estimatedCompletionDate = TechDataServices.addForward(TechDataServices.getTechDataCalendar(routingTask), estimatedStartDate, duringTime);
        }
        Map serviceContext = new HashMap();
        serviceContext.clear();
        serviceContext.put("priority", priority);
        serviceContext.put("workEffortPurposeTypeId", routingTask.get("workEffortPurposeTypeId"));
        serviceContext.put("workEffortName", workEffortName);
        serviceContext.put("description", description);
        serviceContext.put("fixedAssetId", routingTask.get("fixedAssetId"));
        serviceContext.put("workEffortTypeId", "PROD_ORDER_TASK");
        serviceContext.put("currentStatusId","PRUN_CREATED");
        serviceContext.put("workEffortParentId", productionRunId);
        serviceContext.put("facilityId", productionRun.getGenericValue().getString("facilityId"));
        serviceContext.put("estimatedStartDate", estimatedStartDate);
        serviceContext.put("estimatedCompletionDate", estimatedCompletionDate);
        serviceContext.put("estimatedSetupMillis", estimatedSetupMillis);
        serviceContext.put("estimatedMilliSeconds", estimatedMilliSeconds);
        serviceContext.put("quantityToProduce", pRQuantity);
        serviceContext.put("userLogin", userLogin);
        Map resultService = null;
        try {
            resultService = dispatcher.runSync("createWorkEffort", serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem calling the createWorkEffort service", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingAddProductionRunRoutingTaskNotCreated", locale));
        }
        String productionRunTaskId = (String) resultService.get("workEffortId");
        if (Debug.infoOn()) Debug.logInfo("ProductionRunTaskId created: " + productionRunTaskId, module);
        
        
        productionRun.setEstimatedCompletionDate(productionRun.recalculateEstimatedCompletionDate());
        if (!productionRun.store()) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingAddProductionRunRoutingTaskNotCreated", locale));
        }
        
        result.put("routingTaskId", productionRunTaskId);
        result.put("estimatedStartDate", estimatedStartDate);
        result.put("estimatedCompletionDate", estimatedCompletionDate);
        return result;
    }

    public static Map productionRunProduce(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String productionRunId = (String)context.get("productionRunId");
        
        // Optional input fields
        Double quantity = (Double)context.get("quantity");
        Boolean createSerializedInventory = (Boolean)context.get("createSerializedInventory");
        String lotId = (String)context.get("lotId");
        Boolean createLotIfNeeded = (Boolean)context.get("createLotIfNeeded");
        Boolean autoCreateLot = (Boolean)context.get("autoCreateLot");
        
        // The default is non-serialized inventory item
        if (createSerializedInventory == null) {
            createSerializedInventory = new Boolean(false);
        }
        // The default is to create a lot if the lotId is given, but the lot doesn't exist
        if (createLotIfNeeded == null) {
            createLotIfNeeded = new Boolean(true);
        }
        if (autoCreateLot == null) {
            autoCreateLot = new Boolean(false);
        }
       
        // The production run is loaded
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        // The last task is loaded
        GenericValue lastTask = productionRun.getLastProductionRunRoutingTask();
        if (lastTask == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
        }
        // FIXME: PHANTOM flag?
        String productType = productionRun.getProductProduced().getString("productTypeId");
        if ("PHANTOM".equals("productTypeId")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductIsPhantom", locale));
        }
        Double quantityProduced = productionRun.getGenericValue().getDouble("quantityProduced");
        if (quantityProduced == null) {
            quantityProduced = new Double(0);
        }
        Double quantityDeclared = lastTask.getDouble("quantityProduced");
        if (quantityDeclared == null) {
            quantityDeclared = new Double(0);
        }
        // If the quantity already produced is not lower than the quantity declared, no inventory is created.
        double maxQuantity = quantityDeclared.doubleValue() - quantityProduced.doubleValue();
        if (maxQuantity <= 0) {
            return ServiceUtil.returnSuccess();
        }

        // If quantity was not passed, the max quantity is used
        if (quantity == null) {
            quantity = new Double(maxQuantity);
        }
        //
        if (quantity.doubleValue() > maxQuantity) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunProductProducedNotStillAvailable", locale));
        }
        
        if (lotId == null && autoCreateLot.booleanValue()) {
            lotId = delegator.getNextSeqId("Lot");
            createLotIfNeeded = new Boolean(true);
        }
        if (lotId != null) {
            try {
                // Find the lot
                GenericValue lot = delegator.findByPrimaryKey("Lot", UtilMisc.toMap("lotId", lotId));
                if (lot == null) {
                    if (createLotIfNeeded.booleanValue()) {
                        lot = delegator.makeValue("Lot", UtilMisc.toMap("lotId", lotId, "creationDate", UtilDateTime.nowDate()));
                        lot.create();
                    } else {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingLotNotExists", locale));
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        if (createSerializedInventory.booleanValue()) {
            try {
                int numOfItems = quantity.intValue();
                for (int i = 0; i < numOfItems; i++) {
                    String inventoryItemId = delegator.getNextSeqId("InventoryItem");
                    Map inventoryItemFields = UtilMisc.toMap("inventoryItemId", inventoryItemId,
                                                             "inventoryItemTypeId", "SERIALIZED_INV_ITEM",
                                                             "statusId", "INV_AVAILABLE");
                    inventoryItemFields.put("productId", productionRun.getProductProduced().getString("productId"));
                    inventoryItemFields.put("facilityId", productionRun.getGenericValue().getString("facilityId"));
                    inventoryItemFields.put("datetimeReceived", UtilDateTime.nowDate());
                    inventoryItemFields.put("comments", "Created by production run " + productionRunId);
                    inventoryItemFields.put("serialNumber", productionRunId + "-" + inventoryItemId);
                    inventoryItemFields.put("lotId", lotId);
                    GenericValue inventoryItem = delegator.makeValue("InventoryItem", inventoryItemFields);
                    GenericValue inventoryProduced = delegator.makeValue("WorkEffortInventoryProduced", UtilMisc.toMap("workEffortId", productionRunId , "inventoryItemId", inventoryItemId));
                    inventoryItem.create();
                    inventoryProduced.create();
                    Map serviceContext = new HashMap();
                    serviceContext.clear();
                    serviceContext.put("inventoryItemId", inventoryItemId);
                    serviceContext.put("workEffortId", productionRunId);
                    serviceContext.put("availableToPromiseDiff", new Double(1));
                    serviceContext.put("quantityOnHandDiff", new Double(1));
                    serviceContext.put("userLogin", userLogin);
                    Map resultService = resultService = dispatcher.runSync("createInventoryItemDetail", serviceContext);
                }
            } catch(Exception exc) {
                return ServiceUtil.returnError(exc.getMessage());
            }
        } else {
            try {
                String inventoryItemId = delegator.getNextSeqId("InventoryItem");
                Map inventoryItemFields = UtilMisc.toMap("inventoryItemId", inventoryItemId,
                                                         "inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
                inventoryItemFields.put("productId", productionRun.getProductProduced().getString("productId"));
                inventoryItemFields.put("facilityId", productionRun.getGenericValue().getString("facilityId"));
                inventoryItemFields.put("datetimeReceived", UtilDateTime.nowTimestamp());
                inventoryItemFields.put("comments", "Created by production run " + productionRunId);
                inventoryItemFields.put("lotId", lotId);
                GenericValue inventoryItem = delegator.makeValue("InventoryItem", inventoryItemFields);
                GenericValue inventoryProduced = delegator.makeValue("WorkEffortInventoryProduced", UtilMisc.toMap("workEffortId", productionRunId , "inventoryItemId", inventoryItemId));
                inventoryItem.create();
                inventoryProduced.create();
                Map serviceContext = new HashMap();
                serviceContext.clear();
                serviceContext.put("inventoryItemId", inventoryItemId);
                serviceContext.put("workEffortId", productionRunId);
                serviceContext.put("availableToPromiseDiff", quantity);
                serviceContext.put("quantityOnHandDiff", quantity);
                serviceContext.put("userLogin", userLogin);
                Map resultService = resultService = dispatcher.runSync("createInventoryItemDetail", serviceContext);
            } catch(Exception exc) {
                return ServiceUtil.returnError(exc.getMessage());
            }
            // Now the production run's quantityProduced is updated
            double totalQuantity = quantityProduced.doubleValue() + quantity.doubleValue();
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", productionRunId);
            serviceContext.put("quantityProduced", new Double(totalQuantity));
            serviceContext.put("actualCompletionDate", UtilDateTime.nowTimestamp());
            serviceContext.put("userLogin", userLogin);
            Map resultService = null;
            try {
                resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem calling the updateWorkEffort service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunStatusNotChanged", locale));
            }
        }
        result.put("quantity", quantity);
        return result;
    }
    
    public static Map updateProductionRunTask(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String productionRunId = (String)context.get("productionRunId");
        String workEffortId = (String)context.get("productionRunTaskId");
        String partyId = (String)context.get("partyId");
        
        // Optional input fields
        Timestamp fromDate = (Timestamp)context.get("fromDate");
        Timestamp toDate = (Timestamp)context.get("toDate");
        Double addQuantityProduced = (Double)context.get("addQuantityProduced");
        Double addQuantityRejected = (Double)context.get("addQuantityRejected");
        Double addSetupTime = (Double)context.get("addSetupTime");
        Double addTaskTime = (Double)context.get("addTaskTime");
        String comments = (String)context.get("comments");
        
        if (fromDate == null) {
            fromDate = UtilDateTime.nowTimestamp();
        }
        if (toDate == null) {
            toDate = UtilDateTime.nowTimestamp();
        }
        if (addQuantityProduced == null) {
            addQuantityProduced = new Double(0);
        }
        if (addQuantityRejected == null) {
            addQuantityRejected = new Double(0);
        }
        if (addSetupTime == null) {
            addSetupTime = new Double(0);
        }
        if (addTaskTime == null) {
            addTaskTime = new Double(0);
        }
        if (comments == null) {
            comments = "";
        }
        
        ProductionRun productionRun = new ProductionRun(delegator, productionRunId);
        if (!productionRun.exist()){
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotExists", locale));
        }
        List tasks = productionRun.getProductionRunRoutingTasks();
        GenericValue theTask = null;
        GenericValue oneTask = null;
        for (int i = 0; i < tasks.size(); i++) {
            oneTask = (GenericValue)tasks.get(i);
            if (oneTask.getString("workEffortId").equals(workEffortId)) {
                theTask = oneTask;
                break;
            }
        }
        if (theTask == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotExists", locale));
        }
        
        String currentStatusId = theTask.getString("currentStatusId");
        
        if (!currentStatusId.equals("PRUN_RUNNING")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunTaskNotRunning", locale));
        }

        Double actualMilliSeconds = theTask.getDouble("actualMilliSeconds");
        if (actualMilliSeconds == null) {
            actualMilliSeconds = new Double(0);
        }
        Double actualSetupMillis = theTask.getDouble("actualSetupMillis");
        if (actualSetupMillis == null) {
            actualSetupMillis = new Double(0);
        }

        Double quantityProduced = theTask.getDouble("quantityProduced");
        if (quantityProduced == null) {
            quantityProduced = new Double(0);
        }
        Double quantityRejected = theTask.getDouble("quantityRejected");
        if (quantityRejected == null) {
            quantityRejected = new Double(0);
        }
        double totalMillis = actualMilliSeconds.doubleValue() + addTaskTime.doubleValue();
        double totalSetupMillis = actualSetupMillis.doubleValue() + addSetupTime.doubleValue();
        double totalQuantityProduced = quantityProduced.doubleValue() + addQuantityProduced.doubleValue();
        double totalQuantityRejected = quantityRejected.doubleValue() + addQuantityRejected.doubleValue();
        
        // Create a new TimeEntry
        try {
            String timeEntryId = delegator.getNextSeqId("TimeEntry");
            Map timeEntryFields = UtilMisc.toMap("timeEntryId", timeEntryId,
                                                 "workEffortId", workEffortId);
            timeEntryFields.put("partyId", partyId);
            timeEntryFields.put("roleTypeId", "WORKER"); // FIXME
            timeEntryFields.put("fromDatetime", fromDate);
            timeEntryFields.put("thruDatetime", toDate);
            timeEntryFields.put("hours", addTaskTime); // FIXME
            //timeEntryFields.put("setupTime", addSetupTime); // FIXME
            //timeEntryFields.put("quantityProduced", addQuantityProduced); // FIXME
            //timeEntryFields.put("quantityRejected", addQuantityRejected); // FIXME
            timeEntryFields.put("comments", comments);
            GenericValue timeEntry = delegator.makeValue("TimeEntry", timeEntryFields);
            timeEntry.create();
            
            Map serviceContext = new HashMap();
            serviceContext.clear();
            serviceContext.put("workEffortId", workEffortId);
            serviceContext.put("actualMilliSeconds", new Double(totalMillis));
            serviceContext.put("actualSetupMillis", new Double(totalSetupMillis));
            serviceContext.put("quantityProduced", new Double(totalQuantityProduced));
            serviceContext.put("quantityRejected", new Double(totalQuantityRejected));
            serviceContext.put("userLogin", userLogin);
            Map resultService = resultService = dispatcher.runSync("updateWorkEffort", serviceContext);
        } catch(Exception exc) {
            return ServiceUtil.returnError(exc.getMessage());
        }
        
        return result;
    }
    
    public static Map createProductionRunFromRequirement(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String requirementId = (String)context.get("requirementId");
        // Optional input fields
        Double quantity = (Double)context.get("quantity");
        
        GenericValue requirement = null;
        try {
            requirement = delegator.findByPrimaryKey("Requirement", UtilMisc.toMap("requirementId", requirementId));
        } catch(GenericEntityException gee) {
        }
        if (requirement == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRequirementNotExists", locale));
        }
        // TODO: fix the allowed types
        if (!"MRP_PRO_PROD_ORDER".equals(requirement.getString("requirementTypeId")) &&
               !"WORK_REQUIREMENT".equals(requirement.getString("requirementTypeId"))) {
            return ServiceUtil.returnSuccess();
        }
        
        if (quantity == null) {
            quantity = requirement.getDouble("quantity");
        }
        Map serviceContext = new HashMap();
        serviceContext.clear();
        serviceContext.put("productId", requirement.getString("productId"));
        serviceContext.put("pRQuantity", quantity);
        serviceContext.put("startDate", requirement.getTimestamp("requirementStartDate"));
        serviceContext.put("facilityId", requirement.getString("facilityId"));
        serviceContext.put("workEffortName", "Created from requirement");
        serviceContext.put("userLogin", userLogin);
        Map resultService = null;
        try {
            resultService = dispatcher.runSync("createProductionRun", serviceContext);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotCreated", locale));
        }
        String productionRunId = (String)resultService.get("productionRunId");
        result.put("productionRunId", productionRunId);
        
        try {
            requirement.remove();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRequirementNotDeleted", locale));
        }
        
        result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunCreated",UtilMisc.toMap("productionRunId", productionRunId), locale));
        return result;
    }
    
    public static Map createProductionRunFromConfiguration(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List msgResult = new LinkedList();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // Mandatory input fields
        String facilityId = (String)context.get("facilityId");
        // Optional input fields
        String configId = (String)context.get("configId");
        ProductConfigWrapper config = (ProductConfigWrapper)context.get("config");
        Double quantity = (Double)context.get("quantity");
        String orderId = (String)context.get("orderId");
        String orderItemSeqId = (String)context.get("orderItemSeqId");
        
        if (config == null && configId == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingConfigurationNotAvailable", locale));
        }
        if (config == null && configId != null) {
            // TODO: load the configuration
            return ServiceUtil.returnError("Operation not yet implemented");
        }
        if (!config.isCompleted()) {
            return ServiceUtil.returnError("ProductConfigurationNotValid");
        }
        if (quantity == null) {
            quantity = new Double(1);
        }
        Map serviceContext = new HashMap();
        serviceContext.clear();
        serviceContext.put("productId", config.getProduct().getString("productId"));
        serviceContext.put("pRQuantity", quantity);
        serviceContext.put("startDate", UtilDateTime.nowTimestamp());
        serviceContext.put("facilityId", facilityId);
        //serviceContext.put("workEffortName", "");
        serviceContext.put("userLogin", userLogin);
        Map resultService = null;
        try {
            resultService = dispatcher.runSync("createProductionRun", serviceContext);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotCreated", locale));
        }
        String productionRunId = (String)resultService.get("productionRunId");
        result.put("productionRunId", productionRunId);

        Iterator options = config.getSelectedOptions().iterator();
        HashMap components = new HashMap();
        while (options.hasNext()) {
            ConfigOption co = (ConfigOption)options.next();
            //components.addAll(co.getComponents());
            Iterator selComponents = co.getComponents().iterator();
            while (selComponents.hasNext()) {
                Double componentQuantity = null;
                GenericValue selComponent = (GenericValue)selComponents.next();
                if (componentQuantity == null) {
                    componentQuantity = new Double(1);
                }
                componentQuantity = new Double(quantity.doubleValue() * componentQuantity.doubleValue());
                if (components.containsKey(selComponent.getString("productId"))) {
                    Double totalQuantity = (Double)components.get(selComponent.getString("productId"));
                    componentQuantity = new Double(totalQuantity.doubleValue() + componentQuantity.doubleValue());
                }
                components.put(selComponent.getString("productId"), componentQuantity);
            }
        }
        
        Iterator componentsIt = components.entrySet().iterator();
        while (componentsIt.hasNext()) {
            Map.Entry component = (Map.Entry)componentsIt.next();
            String productId = (String)component.getKey();
            Double componentQuantity = (Double)component.getValue();
            if (componentQuantity == null) {
                componentQuantity = new Double(1);
            }
            resultService = null;
            serviceContext = new HashMap();
            serviceContext.put("productionRunId", productionRunId);
            serviceContext.put("productId", productId);
            serviceContext.put("estimatedQuantity", componentQuantity);
            serviceContext.put("userLogin", userLogin);
            try {
                resultService = dispatcher.runSync("addProductionRunComponent", serviceContext);
            } catch (GenericServiceException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingProductionRunNotCreated", locale));
            }
        }
        try {
            if (productionRunId != null && orderId != null && orderItemSeqId != null) {
                delegator.create("WorkOrderItemFulfillment", UtilMisc.toMap("workEffortId", productionRunId, "orderId", orderId, "orderItemSeqId", orderItemSeqId));
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "ManufacturingRequirementNotDeleted", locale));
        }
        
        result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resource, "ManufacturingProductionRunCreated",UtilMisc.toMap("productionRunId", productionRunId), locale));
        return result;
    }

}
