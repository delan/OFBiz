/*
 * $Id: ShippingEvents.java,v 1.10 2004/01/24 14:51:40 ajzeneski Exp $
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

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.common.geo.GeoWorker;

/**
 * ShippingEvents - Events used for processing shipping fees
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.10 $
 * @since      2.0
 */
public class ShippingEvents {

    public static final String module = ShippingEvents.class.getName();

    public static String getShipEstimate(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map result = getShipEstimate(delegator, cart, null);
        ServiceUtil.getMessages(request, result, null, "<li>", "</li>", "<ul>", "</ul>", null, null);
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

    public static Map getShipEstimate(GenericDelegator delegator, ShoppingCart cart, String shippingMethod) {
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
        return getShipEstimate(delegator, cart.getOrderType(), shipmentMethodTypeId, carrierPartyId, cart.getShippingContactMechId(), cart.getProductStoreId(), cart.getShippableSizes(), cart.getFeatureIdQtyMap(), cart.getShippableWeight(), cart.getShippableQuantity(), cart.getShippableTotal());
    }

    public static Map getShipEstimate(GenericDelegator delegator, OrderReadHelper orh) {
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
        return getShipEstimate(delegator, orh.getOrderTypeId(), shipmentMethodTypeId, carrierPartyId, contactMechId, orh.getProductStoreId(), orh.getShippableSizes(), orh.getFeatureIdQtyMap(), orh.getShippableWeight(), orh.getShippableQuantity(), orh.getShippableTotal());
    }

    public static Map getShipEstimate(GenericDelegator delegator, String orderTypeId, String shipmentMethodTypeId, String carrierPartyId, String shippingContactMechId, String productStoreId, List itemSizes, Map featureMap, double shippableWeight, double shippableQuantity, double shippableTotal) {
        String standardMessage = "A problem occurred calculating shipping. Fees will be calculated offline.";
        List errorMessageList = new ArrayList();
        StringBuffer errorMessage = new StringBuffer();

        if (shipmentMethodTypeId == null || carrierPartyId == null) {
            if ("SALES_ORDER".equals(orderTypeId)) {
                errorMessageList.add("Please Select Your Shipping Method.");
                return ServiceUtil.returnError(errorMessageList);
            } else {
                return ServiceUtil.returnSuccess();
            }
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

        // Get the ShipmentCostEstimate(s)
        Collection estimates = null;

        try {
            Map fields = UtilMisc.toMap("productStoreId", productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId, "carrierPartyId", carrierPartyId, "carrierRoleTypeId", "CARRIER");

            estimates = delegator.findByAnd("ShipmentCostEstimate", fields);
            if (Debug.verboseOn()) Debug.logVerbose("Estimate fields: " + fields, module);
            if (Debug.verboseOn()) Debug.logVerbose("Estimate(s): " + estimates, module);
        } catch (GenericEntityException e) {
            Debug.logError("[ShippingEvents.getShipEstimate] Cannot get shipping estimates.", module);
            return ServiceUtil.returnSuccess(standardMessage);
        }
        if (estimates == null || estimates.size() < 1) {
            Debug.logInfo("[ShippingEvents.getShipEstimate] No shipping estimate found.", module);
            return ServiceUtil.returnSuccess(standardMessage);
        }

        if (Debug.verboseOn()) Debug.logVerbose("[ShippingEvents.getShipEstimate] Estimates begin size: " + estimates.size(), module);

        // Get the PostalAddress
        GenericValue shipAddress = null;

        try {
            shipAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", shippingContactMechId));
        } catch (GenericEntityException e) {
            Debug.logError("[ShippingEvents.getShipEstimate] Cannot get shipping address entity.", module);
            return ServiceUtil.returnSuccess(standardMessage);
        }

        // Get the possible estimates.
        ArrayList estimateList = new ArrayList();
        Iterator i = estimates.iterator();

        while (i.hasNext()) {
            GenericValue thisEstimate = (GenericValue) i.next();
            String toGeo = thisEstimate.getString("geoIdTo");
            List toGeoList = GeoWorker.expandGeoGroup(toGeo, delegator);

            // Make sure we have a valid GEOID.
            if (toGeoList == null || toGeoList.size() == 0 ||
                    GeoWorker.containsGeo(toGeoList, shipAddress.getString("countryGeoId"), delegator) ||
                    GeoWorker.containsGeo(toGeoList, shipAddress.getString("stateProvinceGeoId"), delegator) ||
                    GeoWorker.containsGeo(toGeoList, shipAddress.getString("postalCodeGeoId"), delegator)) {

                /*
                if (toGeo == null || toGeo.equals("") || toGeo.equals(shipAddress.getString("countryGeoId")) ||
                toGeo.equals(shipAddress.getString("stateProvinceGeoId")) ||
                toGeo.equals(shipAddress.getString("postalCodeGeoId"))) {
                 */

                GenericValue wv = null;
                GenericValue qv = null;
                GenericValue pv = null;

                try {
                    wv = thisEstimate.getRelatedOne("WeightQuantityBreak");
                } catch (GenericEntityException e) {}
                try {
                    qv = thisEstimate.getRelatedOne("QuantityQuantityBreak");
                } catch (GenericEntityException e) {}
                try {
                    pv = thisEstimate.getRelatedOne("PriceQuantityBreak");
                } catch (GenericEntityException e) {}
                if (wv == null && qv == null && pv == null) {
                    estimateList.add(thisEstimate);
                } else {
                    // Do some testing.
                    boolean useWeight = false;
                    boolean weightValid = false;
                    boolean useQty = false;
                    boolean qtyValid = false;
                    boolean usePrice = false;
                    boolean priceValid = false;

                    if (wv != null) {
                        useWeight = true;
                        double min = 0.0001;
                        double max = 0.0001;

                        try {
                            min = wv.getDouble("fromQuantity").doubleValue();
                            max = wv.getDouble("thruQuantity").doubleValue();
                        } catch (Exception e) {}
                        if (shippableWeight >= min && (max == 0 || shippableWeight <= max))
                            weightValid = true;
                    }
                    if (qv != null) {
                        useQty = true;
                        double min = 0.0001;
                        double max = 0.0001;

                        try {
                            min = qv.getDouble("fromQuantity").doubleValue();
                            max = qv.getDouble("thruQuantity").doubleValue();
                        } catch (Exception e) {}
                        if (shippableQuantity >= min && (max == 0 || shippableQuantity <= max))
                            qtyValid = true;
                    }
                    if (pv != null) {
                        usePrice = true;
                        double min = 0.0001;
                        double max = 0.0001;

                        try {
                            min = pv.getDouble("fromQuantity").doubleValue();
                            max = pv.getDouble("thruQuantity").doubleValue();
                        } catch (Exception e) {}
                        if (shippableTotal >= min && (max == 0 || shippableTotal <= max))
                            priceValid = true;
                    }
                    // Now check the tests.
                    if ((useWeight && weightValid) || (useQty && qtyValid) || (usePrice && priceValid))
                        estimateList.add(thisEstimate);
                }
            }
        }

        if (Debug.verboseOn()) Debug.logVerbose("[ShippingEvents.getShipEstimate] Estimates left after GEO filter: " + estimateList.size(), module);

        if (estimateList.size() < 1) {
            Debug.logInfo("[ShippingEvents.getShipEstimate] No shipping estimate found.", module);
            return ServiceUtil.returnSuccess(standardMessage);
        }

        // Calculate priority based on available data.
        double PRIORITY_PARTY = 9;
        double PRIORITY_ROLE = 8;
        double PRIORITY_GEO = 4;
        double PRIORITY_WEIGHT = 1;
        double PRIORITY_QTY = 1;
        double PRIORITY_PRICE = 1;

        int estimateIndex = 0;

        if (estimateList.size() > 1) {
            TreeMap estimatePriority = new TreeMap();
            //int estimatePriority[] = new int[estimateList.size()];

            for (int x = 0; x < estimateList.size(); x++) {
                GenericValue currentEstimate = (GenericValue) estimateList.get(x);

                int prioritySum = 0;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("partyId")))
                    prioritySum += PRIORITY_PARTY;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("roleTypeId")))
                    prioritySum += PRIORITY_ROLE;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("geoIdTo")))
                    prioritySum += PRIORITY_GEO;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("weightBreakId")))
                    prioritySum += PRIORITY_WEIGHT;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("quantityBreakId")))
                    prioritySum += PRIORITY_QTY;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("priceBreakId")))
                    prioritySum += PRIORITY_PRICE;

                // there will be only one of each priority; latest will replace
                estimatePriority.put(new Integer(prioritySum), currentEstimate);
            }

            // locate the highest priority estimate; or the latest entered
            Object[] estimateArray = estimatePriority.values().toArray();
            estimateIndex = estimateList.indexOf(estimateArray[estimateArray.length - 1]);
        }

        // Grab the estimate and work with it.
        GenericValue estimate = (GenericValue) estimateList.get(estimateIndex);

        //Debug.log("[ShippingEvents.getShipEstimate] Working with estimate [" + estimateIndex + "]: " + estimate, module);

        // flat fees
        double orderFlat = 0.00;
        if (estimate.getDouble("orderFlatPrice") != null)
            orderFlat = estimate.getDouble("orderFlatPrice").doubleValue();

        double orderItemFlat = 0.00;
        if (estimate.getDouble("orderItemFlatPrice") != null)
            orderItemFlat = estimate.getDouble("orderItemFlatPrice").doubleValue();

        double orderPercent = 0.00;
        if (estimate.getDouble("orderPricePercent") != null)
            orderPercent = estimate.getDouble("orderPricePercent").doubleValue();

        double itemFlatAmount = shippableQuantity * orderItemFlat;
        double orderPercentage = shippableTotal * (orderPercent / 100);

        // flat total
        double flatTotal = orderFlat + itemFlatAmount + orderPercentage;

        // spans
        double weightUnit = 0.00;
        if (estimate.getDouble("weightUnitPrice") != null)
            weightUnit = estimate.getDouble("weightUnitPrice").doubleValue();

        double qtyUnit = 0.00;
        if (estimate.getDouble("quantityUnitPrice") != null)
            qtyUnit = estimate.getDouble("quantityUnitPrice").doubleValue();

        double priceUnit = 0.00;
        if (estimate.getDouble("priceUnitPrice") != null)
            priceUnit = estimate.getDouble("priceUnitPrice").doubleValue();

        double weightAmount = shippableWeight * weightUnit;
        double quantityAmount = shippableQuantity * qtyUnit;
        double priceAmount = shippableTotal * priceUnit;

        // span total
        double spanTotal = weightAmount + quantityAmount + priceAmount;

        // feature surcharges
        double featureSurcharge = 0.00;
        String featureGroupId = estimate.getString("productFeatureGroupId");
        Double featurePercent = estimate.getDouble("featurePercent");
        Double featurePrice = estimate.getDouble("featurePrice");
        if (featurePercent == null) {
            featurePercent = new Double(0);
        }
        if (featurePrice == null) {
            featurePrice = new Double(0.00);
        }

        if (featureGroupId != null && featureGroupId.length() > 0 && featureMap != null ) {
            Iterator fii = featureMap.keySet().iterator();
            while (fii.hasNext()) {
                String featureId = (String) fii.next();
                Double quantity = (Double) featureMap.get(featureId);
                GenericValue appl = null;
                Map fields = UtilMisc.toMap("productFeatureGroupId", featureGroupId, "productFeatureId", featureId);
                try {
                    List appls = delegator.findByAndCache("ProductFeatureGroupAppl", fields);
                    appls = EntityUtil.filterByDate(appls);
                    appl = EntityUtil.getFirst(appls);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to lookup feature/group" + fields, module);
                }
                if (appl != null) {
                    featureSurcharge += (shippableTotal * (featurePercent.doubleValue() / 100) * quantity.doubleValue());
                    featureSurcharge += featurePrice.doubleValue() * quantity.doubleValue();
                }
            }
        }

        // size surcharges
        double sizeSurcharge = 0.00;
        Double sizeUnit = estimate.getDouble("oversizeUnit");
        Double sizePrice = estimate.getDouble("oversizePrice");
        if (sizeUnit != null && sizeUnit.doubleValue() > 0) {
            if (itemSizes != null) {
                Iterator isi = itemSizes.iterator();
                while (isi.hasNext()) {
                    Double size = (Double) isi.next();
                    if (size != null && size.doubleValue() >= sizeUnit.doubleValue()) {
                        sizeSurcharge += sizePrice.doubleValue();
                    }
                }
            }
        }

        // surcharges total
        double surchargeTotal = featureSurcharge + sizeSurcharge;

        // shipping total
        double shippingTotal = spanTotal + flatTotal + surchargeTotal;

        if (Debug.verboseOn()) Debug.logVerbose("[ShippingEvents.getShipEstimate] Setting shipping amount : " + shippingTotal, module);

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

