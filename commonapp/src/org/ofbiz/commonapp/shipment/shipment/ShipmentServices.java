/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project and repected authors.
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
 * @created    Jun 24, 2002
 * @version    1.0
 */
public class ShipmentServices {

    public static Map createShipmentEstimate(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        List storeAll = new ArrayList();

        String shipMethodAndParty = (String) context.get("shipMethod");
        List shipMethodSplit = StringUtil.split(shipMethodAndParty, "|");

        // Create the basic entity.
        GenericValue estimate = delegator.makeValue("ShipmentCostEstimate",null);
        estimate.set("shipmentCostEstimateId",delegator.getNextSeqId("ShipmentCostEstimate").toString());
        estimate.set("shipmentMethodTypeId",shipMethodSplit.get(1));
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

        if (context.containsKey("wmin") || context.containsKey("wmax")) {
            if (context.containsKey("wmax") && context.containsKey("wmin")) {
                // Lets process weight.
                try {
                    Long sequence = delegator.getNextSeqId("QuantityBreak");
                    GenericValue weightBreak = delegator.makeValue("QuantityBreak",null);
                    weightBreak.set("quantityBreakId",sequence.toString());
                    weightBreak.set("quantityBreakTypeId","SHIP_WEIGHT");
                    weightBreak.set("fromQuantity",Double.valueOf((String)context.get("wmin")));
                    weightBreak.set("thruQuantity",Double.valueOf((String)context.get("wmax")));
                    estimate.set("weightBreakId",sequence.toString());
                    estimate.set("weightUnitPrice",Double.valueOf((String)context.get("wprice")));
                    if ( context.containsKey("wuom") )
                       estimate.set("weightUomId",(String)context.get("wuom"));
                    storeAll.add(weightBreak);
                }
                catch ( Exception e ) { }
            }
            else {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "Weight Span Requires BOTH Fields.");
                return result;
            }
        }

        if (context.containsKey("qmin") || context.containsKey("qmax")) {
            if ( context.containsKey("qmax") && context.containsKey("qmin") ) {
                // Lets process quantity.
                try {
                    Long sequence = delegator.getNextSeqId("QuantityBreak");
                    GenericValue quantityBreak = delegator.makeValue("QuantityBreak",null);
                    quantityBreak.set("quantityBreakId",sequence.toString());
                    quantityBreak.set("quantityBreakTypeId","SHIP_QUANTITY");
                    quantityBreak.set("fromQuantity",Double.valueOf((String)context.get("qmin")));
                    quantityBreak.set("thruQuantity",Double.valueOf((String)context.get("qmax")));
                    estimate.set("quantityBreakId",sequence.toString());
                    estimate.set("quantityUnitPrice",Double.valueOf((String)context.get("qprice")));
                    if ( context.containsKey("quom") )
                        estimate.set("quantityUomId",context.get("quom"));
                    storeAll.add(quantityBreak);
                    }
                catch ( Exception e ) { }
            }
            else {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "Quantity Span Requires BOTH Fields.");
                return result;
            }
        }

        if (context.containsKey("pmin") || context.containsKey("pmax")) {
            if ( context.containsKey("pmax") && context.containsKey("pmin") ) {
                // Lets process price.
                try {
                    Long sequence = delegator.getNextSeqId("QuantityBreak");
                    GenericValue priceBreak = delegator.makeValue("QuantityBreak",null);
                    priceBreak.set("quantityBreakId",sequence.toString());
                    priceBreak.set("quantityBreakTypeId","SHIP_PRICE");
                    priceBreak.set("fromQuantity",Double.valueOf((String)context.get("pmin")));
                    priceBreak.set("thruQuantity",Double.valueOf((String)context.get("pmax")));
                    estimate.set("priceBreakId",sequence.toString());
                    estimate.set("priceUnitPrice",Double.valueOf((String)context.get("pprice")));
                    storeAll.add(priceBreak);
                }
                catch ( Exception e ) { }
            }
            else {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "Price Span Requires BOTH Fields.");
                return result;
            }
        }

        try {
            delegator.storeAll(storeAll);
        }
        catch ( GenericEntityException e ) {
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
            estimate = delegator.findByPrimaryKey("ShipmentCostEstimate",UtilMisc.toMap("shipmentCostEstimateId", shipmentCostEstimateId));
            if (estimate.get("weightBreakId") != null)
                delegator.removeRelated("WeightQuantityBreak", estimate);
            if (estimate.get("quantityBreakId") != null)
                delegator.removeRelated("QuantityQuantityBreak", estimate);
            if (estimate.get("priceBreakId") != null)
                delegator.removeRelated("PriceQuantityBreak", estimate);
            estimate.remove();
        } catch (GenericEntityException e) {
            Debug.logError(e);
            ServiceUtil.returnError("Problem removing entity or related entities (" + e.toString() + ")");
        }
        return ServiceUtil.returnSuccess();
    }
}
