/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.commonapp.order.order;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.security.Security;
import org.ofbiz.core.service.DispatchContext;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.UtilMisc;

/**
 * Purchase Order Delivery and Schedule Processing Services
 *
 * @author     Tristan Austin (tristana@twibble.org)
 * @version    $Revision$
 */
public class OrderDeliveryServices {
    public static final String module = OrderDeliveryServices.class.getName();
    public static final String resource = "org.ofbiz.commonapp.order.order.PackageMessages";

    /** Service for creating a new purchase order schedule*/
    public static Map createSchedule(DispatchContext ctx, Map context) {
        return storeSchedule(ctx, context, true);
    }

    /** Service for updating an existing purchase order schedule*/
    public static Map updateSchedule(DispatchContext ctx, Map context) {
       return storeSchedule(ctx, context, false);
    }

    /** Service for creating a new purchase order schedule*/
    private static Map storeSchedule(DispatchContext ctx, Map context, boolean doCreation) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // check security
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "ORDERMGR", "_CREATE");

        if (result.size() > 0) {
            return result;
        }

        try
        {
            // get the order type
            String orderTypeId = (String) context.get("orderTypeId");
            
            // Retrieve the date related fields
            Date deliveryDate = (Date)context.get("deliveryDate");
            String deliveryWorkEffortId = (String)context.get("deliveryWorkEffortId");
            Date pickupDate = (Date)context.get("pickupDate");
            String pickupWorkEffortId = (String)context.get("pickupWorkEffortId");
            
            // Define the main fields of the OrderDeliverySchedule
            Map scheduleParams = UtilMisc.toMap("orderId", context.get("orderId"), "orderItemSeqId", "_NA_");
            scheduleParams.put("shipmentMethodTypeId", context.get("shipmentMethodTypeId"));
            scheduleParams.put("trackingNumber", context.get("trackingNumber"));
            scheduleParams.put("deliveryDate", deliveryDate);
            scheduleParams.put("pickupDate", pickupDate);
            scheduleParams.put("cartons", context.get("cartons"));
            scheduleParams.put("skidsPallets", context.get("skidsPallets"));
            scheduleParams.put("unitsPieces", context.get("unitsPieces"));
            scheduleParams.put("totalCubicSize", context.get("totalCubicSize"));
            scheduleParams.put("totalCubicUomId", context.get("totalCubicUom"));
            scheduleParams.put("totalWeight", context.get("totalWeight"));
            scheduleParams.put("totalWeightUomId", context.get("totalWeightUomId"));
            scheduleParams.put("statusId", context.get("statusId"));

            //Define the data structure to submit
            GenericValue schedule = delegator.makeValue("OrderDeliverySchedule", scheduleParams);

            //Now create the associated work efforts
            Map deliveryWorkEffortMap = makeWorkEffort("Purchase Order Delivery", deliveryDate, userLogin);
            Map pickupWorkEffortMap = makeWorkEffort("Purchase Order Pickup", pickupDate, userLogin);

            //See whether to create or update
            if (doCreation) {
                //First create the delivery work effort
                deliveryWorkEffortMap.put("quickAssignPartyId", partyId);
                Map workEffortResult = dispatcher.runSync("createWorkEffort", deliveryWorkEffortMap);
                deliveryWorkEffortId = (String)workEffortResult.get("workEffortId");

                //Now create the pickup work effort
                pickupWorkEffortMap.put("quickAssignPartyId", partyId);
                workEffortResult = dispatcher.runSync("createWorkEffort", pickupWorkEffortMap);
                pickupWorkEffortId = (String)workEffortResult.get("workEffortId");

                //Finally, create the order delivery schedule in the repository
                schedule.put("deliveryWorkEffortId", deliveryWorkEffortId);
                schedule.put("pickupWorkEffortId", pickupWorkEffortId);
                delegator.create(schedule);
             } else {
                //Get a reference to the existing work efforts
                deliveryWorkEffortMap.put("workEffortId", deliveryWorkEffortId);
                pickupWorkEffortMap.put("workEffortId", pickupWorkEffortId);

                //Update the existing work effort schedules
                dispatcher.runSync("updateWorkEffort", deliveryWorkEffortMap);
                dispatcher.runSync("updateWorkEffort", pickupWorkEffortMap);

                //Finally, update the schedule in the repository
                delegator.store(schedule);
             }
        } catch (GenericServiceException e) {
            Debug.logError(e, "Could not create the WorkEffort associated with the delivery schedule", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Could not create the WorkEffort associated with the delivery schedule");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create OrderDeliverySchedule entity; problems with insert", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Transaction error: " + e.getMessage());
        }

        return result;
    }

    /**
     * This will create the WorkEffor twith the given customized
     * fields for the specified pickup or delivery dates.
     *
     * @param   workEffortName  The title of the work effort
     * @param   date    The date to create the work effort for. This does
     * not set a duration for the work effort.
     * @param   userLogin   The userLogin credentials to associate
     * with the service invocation
     * @throws GenericServiceException If an error occurs invoking the
     * the service to create the WorkEffort.
     */
    private static Map makeWorkEffort(String workEffortName, Date deliveryDate, GenericValue userLogin) throws GenericServiceException {
        String workEffortTypeId = "EVENT";
        String currentStatusId= "CAL_CONFIRMED";
        Map workEffortMap = UtilMisc.toMap("workEffortTypeId", workEffortTypeId, 
                "workEffortName", workEffortName, "currentStatusId", currentStatusId, 
                "userLogin", userLogin);
        workEffortMap.put("estimatedStartDate", deliveryDate);
        workEffortMap.put("estimatedCompletionDate", deliveryDate);
        return workEffortMap;
    }
}
