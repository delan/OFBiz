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
package org.ofbiz.commonapp.shipment.shipment;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * ShipmentServices
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class ShipmentServices {

    public static Map createShipmentEstimate(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        List storeAll = new ArrayList();

        String shipMethodAndParty = (String) context.get("shipMethod");
        List shipMethodSplit = StringUtil.split(shipMethodAndParty, "|");

        // Create the basic entity.
        GenericValue estimate = delegator.makeValue("ShipmentCostEstimate", null);

        estimate.set("shipmentCostEstimateId", delegator.getNextSeqId("ShipmentCostEstimate").toString());
        estimate.set("shipmentMethodTypeId", shipMethodSplit.get(1));
        estimate.set("carrierPartyId", shipMethodSplit.get(0));
        estimate.set("carrierRoleTypeId", "CARRIER");
        estimate.set("geoIdTo", context.get("toGeo"));
        estimate.set("geoIdFrom", context.get("fromGeo"));
        estimate.set("partyId", context.get("partyId"));
        estimate.set("roleTypeId", context.get("roleTypeId"));
        estimate.set("orderPricePercent", context.get("flatPercent"));
        estimate.set("orderFlatPrice", context.get("flatPrice"));
        estimate.set("orderItemFlatPrice", context.get("flatItemPrice"));
        storeAll.add(estimate);

        if (!applyQuantityBreak(context, result, storeAll, delegator, estimate, "w", "weight", "Weight")) {
            return result;
        }

        if (!applyQuantityBreak(context, result, storeAll, delegator, estimate, "q", "quantity", "Quantity")) {
            return result;
        }

        if (!applyQuantityBreak(context, result, storeAll, delegator, estimate, "p", "price", "Price")) {
            return result;
        }

        try {
            delegator.storeAll(storeAll);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Problem reading product features: " + e.toString());
            return result;
        }

        result.put("shipmentCostEstimateId", estimate.get("shipmentCostEstimateId"));
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    public static Map removeShipmentEstimate(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String shipmentCostEstimateId = (String) context.get("shipmentCostEstimateId");

        GenericValue estimate = null;

        try {
            estimate = delegator.findByPrimaryKey("ShipmentCostEstimate", UtilMisc.toMap("shipmentCostEstimateId", shipmentCostEstimateId));
            estimate.remove();
            if (estimate.get("weightBreakId") != null)
                delegator.removeRelated("WeightQuantityBreak", estimate);
            if (estimate.get("quantityBreakId") != null)
                delegator.removeRelated("QuantityQuantityBreak", estimate);
            if (estimate.get("priceBreakId") != null)
                delegator.removeRelated("PriceQuantityBreak", estimate);
        } catch (GenericEntityException e) {
            Debug.logError(e);
            return ServiceUtil.returnError("Problem removing entity or related entities (" + e.toString() + ")");
        }
        return ServiceUtil.returnSuccess();
    }

    private static boolean applyQuantityBreak(Map context, Map result, List storeAll, GenericDelegator delegator, 
    		GenericValue estimate, String prefix, String breakType, String breakTypeString) {                                                                                            
        Double min = (Double) context.get(prefix + "min");
        Double max = (Double) context.get(prefix + "max");
        if (min != null || max != null) {
            if (min != null && max != null) {
                if (min.doubleValue() <= max.doubleValue() || max.doubleValue() == 0) {
                    try {
                        Long sequence = delegator.getNextSeqId("QuantityBreak");
                        GenericValue weightBreak = delegator.makeValue("QuantityBreak", null);
                        weightBreak.set("quantityBreakId", sequence.toString());
                        weightBreak.set("quantityBreakTypeId", "SHIP_" + breakType.toUpperCase());
                        weightBreak.set("fromQuantity", min);
                        weightBreak.set("thruQuantity", max);
                        estimate.set(breakType + "BreakId", sequence.toString());
                        estimate.set(breakType + "UnitPrice", (Double) context.get(prefix + "price"));
                        if (context.containsKey(prefix + "uom")) {
                            estimate.set(breakType + "UomId", (String) context.get(prefix + "uom"));
                        }
                        storeAll.add(0, weightBreak);
                    }
                    catch ( Exception e ) {
                        Debug.logError(e);
                    }
                }
                else {
                    result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    result.put(ModelService.ERROR_MESSAGE, "Max " + breakTypeString +
                            " must not be less than Min " + breakTypeString + ".");
                    return false;
                }
            }
            else {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, breakTypeString+" Span Requires BOTH Fields.");
                return false;
            }
        }
        return true;
    }

}
