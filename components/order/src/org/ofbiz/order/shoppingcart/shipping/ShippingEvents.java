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
package org.ofbiz.order.shoppingcart.shipping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;

/**
 * ShippingEvents - Events used for processing shipping fees
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ShippingEvents {

    public static final String module = ShippingEvents.class.getName();

    public static String getShipEstimate(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        Map result = getShipEstimate(dispatcher, delegator, cart, null);
        ServiceUtil.getMessages(request, result, null, "", "", "", "", null, null);
        if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            return "error";
        }

        Double shippingTotal = (Double) result.get("shippingTotal");
        // remove old shipping adjustments if there
        cart.removeAdjustmentByType("SHIPPING_CHARGES");

        // creat the new adjustment and add it to the cart
        if (shippingTotal != null && shippingTotal.doubleValue() > 0) {
            GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                    UtilMisc.toMap("orderAdjustmentTypeId", "SHIPPING_CHARGES", "amount", shippingTotal));
            cart.addAdjustment(orderAdjustment);
        }

        // all done
        return "success";
    }

    public static Map getShipEstimate(LocalDispatcher dispatcher, GenericDelegator delegator, ShoppingCart cart, String shippingMethod) {
        String shipmentMethodTypeId = null;
        String carrierPartyId = null;
        if (UtilValidate.isNotEmpty(shippingMethod)) {
            int delimiterPos = shippingMethod.indexOf('@');
            if (delimiterPos > 0) {
                shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                carrierPartyId = shippingMethod.substring(delimiterPos + 1);
            }
        } else {
            shipmentMethodTypeId = cart.getShipmentMethodTypeId();
            carrierPartyId = cart.getCarrierPartyId();
        }
        return getShipEstimate(dispatcher, delegator, cart.getOrderType(), shipmentMethodTypeId, carrierPartyId, null,
                cart.getShippingContactMechId(), cart.getProductStoreId(), cart.getShippableItemInfo(),
                cart.getShippableWeight(), cart.getShippableQuantity(), cart.getShippableTotal());
    }

    public static Map getShipEstimate(LocalDispatcher dispatcher, GenericDelegator delegator, OrderReadHelper orh) {
        String shippingMethod = orh.getShippingMethodCode();
        String shipmentMethodTypeId = null;
        String carrierPartyId = null;
        if (UtilValidate.isNotEmpty(shippingMethod)) {
            int delimiterPos = shippingMethod.indexOf('@');
            if (delimiterPos > 0) {
                shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                carrierPartyId = shippingMethod.substring(delimiterPos + 1);
            }
        }
        GenericValue shipAddr = orh.getShippingAddress();
        String contactMechId = shipAddr.getString("contactMechId");
        return getShipEstimate(dispatcher, delegator, orh.getOrderTypeId(), shipmentMethodTypeId, carrierPartyId, null,
                contactMechId, orh.getProductStoreId(), orh.getShippableItemInfo(), orh.getShippableWeight(),
                orh.getShippableQuantity(), orh.getShippableTotal());
    }

    public static Map getShipEstimate(LocalDispatcher dispatcher, GenericDelegator delegator, String orderTypeId,
            String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId, String shippingContactMechId,
            String productStoreId, List itemInfo, double shippableWeight, double shippableQuantity,
            double shippableTotal) {
        String standardMessage = "A problem occurred calculating shipping. Fees will be calculated offline.";
        List errorMessageList = new ArrayList();

        if (shipmentMethodTypeId == null || carrierPartyId == null) {
            if ("SALES_ORDER".equals(orderTypeId)) {
                errorMessageList.add("Please Select Your Shipping Method.");
                return ServiceUtil.returnError(errorMessageList);
            } else {
                return ServiceUtil.returnSuccess();
            }
        }

        if (carrierRoleTypeId == null) {
            carrierRoleTypeId = "CARRIER";
        }

        if (shippingContactMechId == null) {
            errorMessageList.add("Please Select Your Shipping Address.");
            return ServiceUtil.returnError(errorMessageList);
        }

        // no shippable items; we won't change any shipping at all
        if (shippableQuantity == 0) {
            Map result = ServiceUtil.returnSuccess();
            result.put("shippingTotal", new Double(0));
            return result;
        }

        // check for an external service call
        GenericValue storeShipMethod = ProductStoreWorker.getProductStoreShipmentMethod(delegator, productStoreId,
                shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId);

        if (storeShipMethod == null) {
            errorMessageList.add("System error");
            return ServiceUtil.returnError(errorMessageList);
        }

        // the initial amount before manual estimates
        double shippingTotal = 0.00;

        // prepare the service invocation fields
        Map serviceFields = new HashMap();
        serviceFields.put("initialEstimateAmt", new Double(shippingTotal));
        serviceFields.put("shippableTotal", new Double(shippableTotal));
        serviceFields.put("shippableQuantity", new Double(shippableQuantity));
        serviceFields.put("shippableWeight", new Double(shippableWeight));        
        serviceFields.put("shippableItemInfo", itemInfo);
        serviceFields.put("productStoreId", productStoreId);
        serviceFields.put("carrierRoleTypeId", "CARRIER");
        serviceFields.put("carrierPartyId", carrierPartyId);
        serviceFields.put("shipmentMethodTypeId", shipmentMethodTypeId);
        serviceFields.put("shippingContactMechId", shippingContactMechId);

        // call the external shipping service
        try {
            Double externalAmt = getExternalShipEstimate(dispatcher, storeShipMethod, serviceFields);
            if (externalAmt != null) {
                shippingTotal += externalAmt.doubleValue();
            }
        } catch (GeneralException e) {
            return ServiceUtil.returnSuccess(standardMessage);
        }

        // update the initial amount
        serviceFields.put("initialEstimateAmt", new Double(shippingTotal));

        // call the generic estimate service
        try {
            Double genericAmt = getGenericShipEstimate(dispatcher, storeShipMethod, serviceFields);
            if (genericAmt != null) {
                shippingTotal += genericAmt.doubleValue();
            }
        } catch (GeneralException e) {
            return ServiceUtil.returnSuccess(standardMessage);
        }

        // return the totals
        Map responseResult = ServiceUtil.returnSuccess();
        responseResult.put("shippingTotal", new Double(shippingTotal));
        return responseResult;
    }

    public static Double getGenericShipEstimate(LocalDispatcher dispatcher, GenericValue storeShipMeth, Map context) throws GeneralException {
        // invoke the generic estimate service next -- append to estimate amount
        Map genericEstimate = null;
        Double genericShipAmt = null;
        try {
            genericEstimate = dispatcher.runSync("calcShipmentCostEstimate", context);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Shipment Service Error", module);
            throw new GeneralException();
        }
        if (ServiceUtil.isError(genericEstimate)) {
            Debug.logError(ServiceUtil.getErrorMessage(genericEstimate), module);
            throw new GeneralException();
        } else {
            genericShipAmt = (Double) genericEstimate.get("shippingEstimateAmount");
        }
        return genericShipAmt;
    }

    public static Double getExternalShipEstimate(LocalDispatcher dispatcher, GenericValue storeShipMeth, Map context) throws GeneralException {
        // invoke the external shipping estimate service
        Double externalShipAmt = null;
        if (storeShipMeth.get("serviceName") != null) {
            String serviceName = storeShipMeth.getString("serviceName");
            String configProps = storeShipMeth.getString("configProps");
            if (UtilValidate.isNotEmpty(serviceName)) {
                // prepare the external service context
                context.put("serviceConfigProps", configProps);

                // invoke the service
                Map serviceResp = null;
                try {
                    Debug.log("Service : " + serviceName + " / " + configProps + " -- " + context, module);
                    serviceResp = dispatcher.runSync(serviceName, context);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Shipment Service Error", module);
                    throw new GeneralException();
                }
                if (!ServiceUtil.isError(serviceResp)) {
                    externalShipAmt = (Double) serviceResp.get("shippingEstimateAmount");
                } else {
                    Debug.logError(ServiceUtil.getErrorMessage(serviceResp), module);
                    throw new GeneralException();
                }
            }
        }
        return externalShipAmt;
    }
}

