/*
 * $Id: ShippingEvents.java,v 1.12 2004/08/12 02:18:13 ajzeneski Exp $
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
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * ShippingEvents - Events used for processing shipping fees
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.12 $
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
                cart.getShippingContactMechId(), cart.getProductStoreId(), cart.getShippableSizes(),
                cart.getFeatureIdQtyMap(), cart.getShippableWeight(), cart.getShippableQuantity(),
                cart.getShippableTotal());
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
                contactMechId, orh.getProductStoreId(), orh.getShippableSizes(), orh.getFeatureIdQtyMap(),
                orh.getShippableWeight(), orh.getShippableQuantity(), orh.getShippableTotal());
    }

    public static Map getShipEstimate(LocalDispatcher dispatcher, GenericDelegator delegator, String orderTypeId,
            String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId, String shippingContactMechId,
            String productStoreId, List itemSizes, Map featureMap, double shippableWeight, double shippableQuantity,
            double shippableTotal) {
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

        if (Debug.verboseOn()) {
            Debug.logVerbose("Shippable Weight : " + shippableWeight, module);
            Debug.logVerbose("Shippable Qty : " + shippableQuantity, module);
            Debug.logVerbose("Shippable Total : " + shippableTotal, module);
        }

        // no shippable items; we won't change any shipping at all
        if (shippableQuantity == 0) {
            Map result = ServiceUtil.returnSuccess();
            result.put("shippingTotal", new Double(0));
            return result;
        }

        // check for an external service call
        Map storeFields = UtilMisc.toMap("productStoreId", productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId,
                "partyId", carrierPartyId, "roleTypeId", carrierRoleTypeId);

        GenericValue storeShipMeth = null;
        try {
            storeShipMeth = delegator.findByPrimaryKeyCache("ProductStoreShipmentMeth", storeFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (storeShipMeth == null) {
            Debug.logError("No ProductStoreShipmentMeth found - " + storeFields, module);
            errorMessageList.add("System error.");
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
        serviceFields.put("shippableFeatureMap", featureMap);
        serviceFields.put("shippableItemSizes", itemSizes);
        serviceFields.put("productStoreId", productStoreId);
        serviceFields.put("carrierRoleTypeId", "CARRIER");
        serviceFields.put("carrierPartyId", carrierPartyId);
        serviceFields.put("shipmentMethodTypeId", shipmentMethodTypeId);
        serviceFields.put("shippingContactMechId", shippingContactMechId);

        // invoke the generic estimate service first
        Map initalEstimate = null;
        try {
            initalEstimate = dispatcher.runSync("calcShipmentCostEstimate", serviceFields);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("System Service Error");
        }
        if (ServiceUtil.isError(initalEstimate)) {
            return initalEstimate;
        } else {
            Double initialShipAmt = (Double) initalEstimate.get("shippingEstimateAmount");
            if (initialShipAmt != null) {
                shippingTotal += initialShipAmt.doubleValue();
            }
        }

        // invoke the external shipping estimate service - amount gets added to the inital total
        if (storeShipMeth.get("serviceName") != null) {
            String serviceName = storeShipMeth.getString("serviceName");
            String configProps = storeShipMeth.getString("configProps");
            if (UtilValidate.isNotEmpty(serviceName)) {
                // prepare the external service context
                serviceFields.put("serviceConfigProps", configProps);
                serviceFields.put("initialEstimateAmt", new Double(shippingTotal));

                // invoke the service
                Map serviceResp = null;
                try {
                    Debug.log("Service : " + serviceName + " / " + configProps + " -- " + serviceFields, module);
                    serviceResp = dispatcher.runSync(serviceName, serviceFields);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("System Service Error");
                }
                if (!ServiceUtil.isError(serviceResp)) {
                    Double externalShipAmt = (Double) serviceResp.get("shippingEstimateAmount");
                    if (externalShipAmt != null) {
                        shippingTotal += externalShipAmt.doubleValue();
                    }
                } else {
                    return serviceResp;
                }
            }
        }

        // return the totals
        Map responseResult = ServiceUtil.returnSuccess();
        responseResult.put("shippingTotal", new Double(shippingTotal));
        return responseResult;
    }

    /*
     * Reserved for future use.
     *
     private static double getUPSRate(ShoppingCart cart, String fromZip, String upsMethod) {
     HttpClient req = new HttpClient();
     HashMap arguments = new HashMap();
     double totalWeight = 0.00000;
     double upsRate = 0.00;

     HashMap services = new HashMap();
     services.put("1DA","Next Day Air");
     services.put("1DM","Next Day Air Early");
     services.put("1DP","Next Day Air Saver");
     services.put("1DAPI","Next Day Air Intra (Puerto Rico)");
     services.put("2DA","2nd Day Air");
     services.put("2DM","2nd Day Air A.M.");
     services.put("3DS","3rd Day");
     services.put("GND","Ground Service");
     services.put("STD","Canada Standard");
     services.put("XPR","Worldwide Express");
     services.put("XDM","Worldwide Express Plus");
     services.put("XPD","Worldwide Expedited");

     if ( !services.containsKey(upsMethod) )
     return 0.00;

     // Get the total weight from the cart.
     Iterator cartItemIterator = cart.iterator();
     while ( cartItemIterator.hasNext() ) {
     ShoppingCartItem item = (ShoppingCartItem) cartItemIterator.next();
     totalWeight += (item.getWeight() * item.getQuantity());
     }
     String weightString = new Double(totalWeight).toString();
     if (Debug.infoOn()) Debug.logInfo("[ShippingEvents.getUPSRate] Total Weight: " + weightString, module);

     // Set up the UPS arguments.
     arguments.put("AppVersion","1.2");
     arguments.put("ResponseType","application/x-ups-rss");
     arguments.put("AcceptUPSLicenseAgreement","yes");

     arguments.put("RateChart","Regular Daily Pickup");              // ?
     arguments.put("PackagingType","00");                                  // Using own container
     arguments.put("ResidentialInd","1");                                     // Assume residential

     arguments.put("ShipperPostalCode",fromZip);                      // Ship From ZipCode
     arguments.put("ConsigneeCountry","US");                            // 2 char country ISO
     arguments.put("ConsigneePostalCode","27703");                 // Ship TO ZipCode
     arguments.put("PackageActualWeight",weightString);          // Total shipment weight

     arguments.put("ActionCode","3");                                         // Specify the shipping type. (4) to get all
     arguments.put("ServiceLevelCode",upsMethod);                   // User's shipping choice (or 1DA for ActionCode 4)

     String upsResponse = null;
     try {
     req.setUrl(UPS_RATES_URL);
     req.setLineFeed(false);
     req.setParameters(arguments);
     upsResponse = req.get();
     }
     catch ( HttpClientException e ) {
     Debug.logError("[ShippingEvents.getUPSRate] Problems getting UPS Rate Infomation.", module);
     return -1;
     }

     if ( upsResponse.indexOf("application/x-ups-error") != -1 ) {
     // get the error message
     }
     else if ( upsResponse.indexOf("application/x-ups-rss") != -1 ) {
     // get the content
     upsResponse = upsResponse.substring(upsResponse.indexOf("UPSOnLine"));
     upsResponse = upsResponse.substring(0,upsResponse.indexOf("--UPSBOUNDARY--") -1 );
     ArrayList respList = new ArrayList();
     while ( upsResponse.indexOf("%") != -1 ) {
     respList.add(upsResponse.substring(0,upsResponse.indexOf("%")));
     upsResponse = upsResponse.substring(upsResponse.indexOf("%") + 1);
     if ( upsResponse.indexOf("%") == -1 )
     respList.add(upsResponse);
     }

     // Debug:
     Iterator i = respList.iterator();
     while ( i.hasNext() ) {
     String value = (String) i.next();
     if (Debug.infoOn()) Debug.logInfo("[ShippingEvents.getUPSRate] Resp List: " + value, module);
     }

     // Shipping method is index 5
     // Shipping rate is index 12
     if ( !respList.get(5).equals(upsMethod) )
     Debug.logInfo("[ShippingEvents.getUPSRate] Shipping method does not match.", module);
     try {
     upsRate = Double.parseDouble((String)respList.get(12));
     }
     catch ( NumberFormatException nfe ) {
     Debug.logError("[ShippingEvents.getUPSRate] Problems parsing rate value.", module);
     }
     }

     return upsRate;
     }
     */

}

