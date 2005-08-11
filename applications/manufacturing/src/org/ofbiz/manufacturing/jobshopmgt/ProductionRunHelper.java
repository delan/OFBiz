/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Helper for Production Run maintenance
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Rev$
 * @since      3.0
 */
public class ProductionRunHelper {
    
    public static final String module = ProductionRunHelper.class.getName();
    public static final String resource = "ManufacturingUiLabels";
    
    
    /**
     * Get a Production Run.
     *  <li> check if routing - product link exist
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters, productionRunId
     * @return Map with the result of the service, the output parameters are
     * 	<li> the productionRun
     * 	<li> the productionRunProduct
     */
    public static Map getProductionRun(GenericDelegator delegator, String productionRunId) {
        Map result = new HashMap();
        //        Timestamp now = UtilDateTime.nowTimestamp();
        
        try {
            if (productionRunId != null ) {
                GenericValue productionRun = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", productionRunId));
                if (productionRun != null) {
                    List productionRunProducts = productionRun.getRelated("WorkEffortGoodStandard", UtilMisc.toMap("statusId", "WIP_OUTGOING_FULFIL"),null);
                    GenericValue productionRunProduct = EntityUtil.getFirst(productionRunProducts);
                    GenericValue productProduced = productionRunProduct.getRelatedOneCache("Product");
                    List productionRunComponents = productionRun.getRelated("WorkEffortGoodStandard", UtilMisc.toMap("statusId", "WIP_INCOMING_FULFIL"),null);
                    productionRunComponents.addAll(productionRun.getRelated("WorkEffortGoodStandard", UtilMisc.toMap("statusId", "WIP_INCOMING_DONE"),null));
                    List productionRunRoutingTasks = productionRun.getRelated("FromWorkEffortAssoc",UtilMisc.toMap("workEffortTypeId","PROD_ORDER_TASK"),null);
                    
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        return result;
    }

    public static boolean hasTask(GenericDelegator delegator, String taskName, String workEffortId) throws GenericEntityException {
        List tasks = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortParentId", workEffortId, 
                                                         "workEffortTypeId", "PROD_ORDER_TASK",
                                                         "workEffortName", taskName));
        return (tasks != null && tasks.size() > 0);
    }

    public static void getLinkedProductionRuns(GenericDelegator delegator, String productionRunId, List productionRuns)  throws GenericEntityException {
        productionRuns.add(new ProductionRun(delegator, productionRunId));
        List linkedWorkEfforts = EntityUtil.filterByDate(delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdTo", productionRunId, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY")));
        for (int i = 0; i < linkedWorkEfforts.size(); i++) {
            GenericValue link = (GenericValue)linkedWorkEfforts.get(i);
            getLinkedProductionRuns(delegator, link.getString("workEffortIdFrom"), productionRuns);
        }
    }

    public static String getRootProductionRun(GenericDelegator delegator, String productionRunId)  throws GenericEntityException {
        List linkedWorkEfforts = delegator.findByAnd("WorkEffortAssoc", UtilMisc.toMap("workEffortIdFrom", productionRunId, "workEffortAssocTypeId", "WORK_EFF_PRECEDENCY"));
        GenericValue linkedWorkEffort = EntityUtil.getFirst(linkedWorkEfforts);
        if (linkedWorkEffort != null) {
            productionRunId = getRootProductionRun(delegator, linkedWorkEffort.getString("workEffortIdTo"));
        }
        return productionRunId;
    }

}
