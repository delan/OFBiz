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

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.order.shoppingcart.*;

/**
 * ShippingEvents - Events used for processing shipping fees
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ShippingEvents {

    public static String getShipEstimate(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        URL ecommercePropertiesUrl = null;

        try {
            ecommercePropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }

        StringBuffer errorMessage = new StringBuffer();

        // String shippingMethod = request.getParameter("shipping_method");
        // String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
        // if ( shippingMethod != null ) {
        // int atSign = shippingMethod.indexOf('@');
        // shipmentMethodTypeId = shippingMethod.substring(0,atSign);
        // }
        String shippingContactMechId = cart.getShippingContactMechId();
        String shipmentMethodTypeId = cart.getShipmentMethodTypeId();
        String carrierPartyId = cart.getCarrierPartyId();

        if (shipmentMethodTypeId == null || carrierPartyId == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Please Select a Shipping Method");
            return "error";
        }

        // Get the ShipmentCostEstimate(s)
        Collection estimates = null;

        try {
            Map fields = UtilMisc.toMap("shipmentMethodTypeId", shipmentMethodTypeId, "carrierPartyId", carrierPartyId, "carrierRoleTypeId", "CARRIER");

            estimates = delegator.findByAnd("ShipmentCostEstimate", fields);
            if (Debug.verboseOn()) Debug.logVerbose("Estimate fields: " + fields, module);
            if (Debug.verboseOn()) Debug.logVerbose("Estimate(s): " + estimates, module);
        } catch (GenericEntityException e) {
            Debug.logError("[ShippingEvents.getShipEstimate] Cannot get shipping estimates.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "A problem occurred calculating shipping. Fees will be calculated offline.");
            return "succes";
        }
        if (estimates == null || estimates.size() < 1) {
            Debug.logInfo("[ShippingEvents.getShipEstimate] No shipping estimate found.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "A problem occurred calculating shipping. Fees will be calculated offline.");
            return "success";
        }

        if (Debug.infoOn()) Debug.logInfo("[ShippingEvents.getShipEstimate] Estimates begin size: " + estimates.size(), module);

        // Get the PostalAddress
        GenericValue shipAddress = null;

        try {
            shipAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", shippingContactMechId));
        } catch (GenericEntityException e) {
            Debug.logError("[ShippingEvents.getShipEstimate] Cannot get shipping address entity.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "A problem occurred calculating shipping. Fees will be calculated offline.");
            return "success";
        }

        // Get some needed data from the cart.
        double cartWeight = cart.getShippableWeight();
        double cartQty = cart.getShippableQuantity();
        double cartTotal = cart.getShippableTotal();

        // Get the possible estimates.
        ArrayList estimateList = new ArrayList();
        Iterator i = estimates.iterator();

        while (i.hasNext()) {
            GenericValue thisEstimate = (GenericValue) i.next();
            String toGeo = thisEstimate.getString("geoIdTo");

            // Make sure we have a valid GEOID.
            if (toGeo == null || toGeo.equals("") || toGeo.equals(shipAddress.getString("countryGeoId")) ||
                toGeo.equals(shipAddress.getString("stateProvinceGeoId")) ||
                toGeo.equals(shipAddress.getString("postalCodeGeoId"))) {
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
                        if (cartWeight >= min && (max == 0 || cartWeight <= max))
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
                        if (cartQty >= min && (max == 0 || cartQty <= max))
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
                        if (cartTotal >= min && (max == 0 || cartTotal <= max))
                            priceValid = true;
                    }
                    // Now check the tests.
                    if ((useWeight && weightValid) || (useQty && qtyValid) || (usePrice && priceValid))
                        estimateList.add(thisEstimate);
                }
            }
        }

        if (Debug.infoOn()) Debug.logInfo("[ShippingEvents.getShipEstimate] Estimates left after GEO filter: " + estimateList.size(), module);

        if (estimateList.size() < 1) {
            Debug.logInfo("[ShippingEvents.getShipEstimate] No shipping estimate found.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "A problem occurred calculating shipping. Fees will be calculated offline.");
            return "success";
        }

        // Calculate priority based on available data.
        double PRIORITY_PARTY = UtilProperties.getPropertyNumber(ecommercePropertiesUrl, "shipping.priority.partyId");
        double PRIORITY_ROLE = UtilProperties.getPropertyNumber(ecommercePropertiesUrl, "shipping.priority.roleTypeId");
        double PRIORITY_GEO = UtilProperties.getPropertyNumber(ecommercePropertiesUrl, "shipping.priority.geoId");
        double PRIORITY_WEIGHT = UtilProperties.getPropertyNumber(ecommercePropertiesUrl, "shipping.priority.weightSpan");
        double PRIORITY_QTY = UtilProperties.getPropertyNumber(ecommercePropertiesUrl, "shipping.priority.qtySpan");
        double PRIORITY_PRICE = UtilProperties.getPropertyNumber(ecommercePropertiesUrl, "shipping.priority.priceSpan");

        int estimateIndex = 0;

        if (estimateList.size() > 1) {
            int estimatePriority[] = new int[estimateList.size()];

            for (int x = 0; x < estimateList.size(); x++) {
                GenericValue currentEstimate = (GenericValue) estimateList.get(x);

                if (UtilValidate.isNotEmpty(currentEstimate.getString("partyId")))
                    estimatePriority[x] += PRIORITY_PARTY;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("roleTypeId")))
                    estimatePriority[x] += PRIORITY_ROLE;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("geoIdTo")))
                    estimatePriority[x] += PRIORITY_GEO;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("weightBreakId")))
                    estimatePriority[x] += PRIORITY_WEIGHT;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("quantityBreakId")))
                    estimatePriority[x] += PRIORITY_QTY;
                if (UtilValidate.isNotEmpty(currentEstimate.getString("priceBreakId")))
                    estimatePriority[x] += PRIORITY_PRICE;
            }
            Arrays.sort(estimatePriority);
            estimateIndex = estimatePriority.length - 1;
        }

        // Grab the estimate and work with it.
        GenericValue estimate = (GenericValue) estimateList.get(estimateIndex);

        if (Debug.infoOn()) Debug.logInfo("[ShippingEvents.getShipEstimate] Working with estimate: " + estimateIndex, module);

        double orderFlat = 0.00;

        if (estimate.getDouble("orderFlatPrice") != null)
            orderFlat = estimate.getDouble("orderFlatPrice").doubleValue();
        double orderItemFlat = 0.00;

        if (estimate.getDouble("orderItemFlatPrice") != null)
            orderItemFlat = estimate.getDouble("orderItemFlatPrice").doubleValue();
        double orderPercent = 0.00;

        if (estimate.getDouble("orderPricePercent") != null)
            orderPercent = estimate.getDouble("orderPricePercent").doubleValue();

        double weightUnit = 0.00;

        if (estimate.getDouble("weightUnitPrice") != null)
            weightUnit = estimate.getDouble("weightUnitPrice").doubleValue();
        double qtyUnit = 0.00;

        if (estimate.getDouble("quantityUnitPrice") != null)
            qtyUnit = estimate.getDouble("quantityUnitPrice").doubleValue();
        double priceUnit = 0.00;

        if (estimate.getDouble("priceUnitPrice") != null)
            priceUnit = estimate.getDouble("priceUnitPrice").doubleValue();

        double weightAmount = cartWeight * weightUnit;
        double quantityAmount = cartQty * qtyUnit;
        double priceAmount = cartTotal * priceUnit;

        double itemFlatAmount = cartQty * orderItemFlat;
        double orderPercentage = cartTotal * (orderPercent / 100);

        double shippingTotal = weightAmount + quantityAmount + priceAmount + orderFlat + itemFlatAmount + orderPercentage;

        if (Debug.infoOn()) Debug.logInfo("[ShippingEvents.getShipEstimate] Setting shipping amount : " + shippingTotal, module);

        // remove old shipping adjustments if there
        cart.removeAdjustmentByType("SHIPPING_CHARGES");

        GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                UtilMisc.toMap("orderAdjustmentTypeId", "SHIPPING_CHARGES", "amount", new Double(shippingTotal)));

        cart.addAdjustment(orderAdjustment);

        return "success";
    }

    public static String viewShipmentPackageRouteSegLabelImage(HttpServletRequest request, HttpServletResponse response) {
        
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        
        String shipmentId = request.getParameter("shipmentId");
        String shipmentRouteSegmentId = request.getParameter("shipmentRouteSegmentId");
        String shipmentPackageSeqId = request.getParameter("shipmentPackageSeqId");
        
        GenericValue shipmentPackageRouteSeg = null;
        try {
            shipmentPackageRouteSeg = delegator.findByPrimaryKey("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId", shipmentId, "shipmentRouteSegmentId", shipmentRouteSegmentId, "shipmentPackageSeqId", shipmentPackageSeqId));
        } catch (GenericEntityException e) {
            String errorMsg = "Error looking up ShipmentPackageRouteSeg: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMsg);
            return "error";
        }
        
        if (shipmentPackageRouteSeg == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Could not find ShipmentPackageRouteSeg where shipmentId=[" + shipmentId + "], shipmentRouteSegmentId=[" + shipmentRouteSegmentId + "], shipmentPackageSeqId=[" + shipmentPackageSeqId + "]");
            return "error";
        }
        
        ByteWrapper byteWrapper = (ByteWrapper) shipmentPackageRouteSeg.get("labelImage");
        if (byteWrapper == null) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "The ShipmentPackageRouteSeg was found where shipmentId=[" + shipmentId + "], shipmentRouteSegmentId=[" + shipmentRouteSegmentId + "], shipmentPackageSeqId=[" + shipmentPackageSeqId + "], but there was no labelImage on the value.");
            return "error";
        }
        
        try {
            // could set the content type, etc; but what to set it to?
            
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(byteWrapper.getBytes());
            outputStream.flush();
            outputStream.close();

            return "success";
        } catch (IOException e) {
            String errorMsg = "Error writing labelImage to OutputStream: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMsg);
            return "error";
        }
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

