/** 
 * $Id$
 */

package org.ofbiz.ecommerce.checkout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

import org.ofbiz.ecommerce.shoppingcart.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ShippingEvents.java
 * <p><b>Description:</b> Events used for processing shipping fees..
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on October 8, 2001, 1:17 PM
 */
public class ShippingEvents {
    
    public static final String UPS_RATES_URL = "http://www.ups.com/using/services/rave/qcostcgi.cgi";
    
    public static String calcShipping(HttpServletRequest request, HttpServletResponse response) {        
        String contextRoot=(String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
        String SHIPPING_MODEL = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.model");
        String SHIPPING_FROM_ZIP = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.fromZip");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
                
        double shippingTotal = calcHandling(cart,contextRoot);
                
        if ( SHIPPING_MODEL.equalsIgnoreCase("UPS") ) {
            HashMap upsMapping = new HashMap();
            upsMapping.put("GROUND@UPS","GND");
            upsMapping.put("AIR@UPS","2DA");
            upsMapping.put("NEXT_DAY@UPS","1DA");
            
            String shippingMethod = request.getParameter("shipping_method");                                
            double shipping = 0.00;
            if ( upsMapping.containsKey(shippingMethod) )
                shipping = getUPSRate(cart,SHIPPING_FROM_ZIP,(String) upsMapping.get(shippingMethod));
                                    
            shippingTotal += shipping;
        }
        
        cart.setShipping(shippingTotal);
        return "success";
    }
    
    private static double calcHandling(ShoppingCart cart, String contextRoot) {
        double baseHandling = 0.00;
        double unitHandling = 0.0000;
        double unitMultiplier = 0.0000;
        double handlingMax = 0.00;
                       
        String BASE_HANDLING = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.baseHandling");
        String UNIT_HANDLING = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.perUnitHandling");
        String HANDLING_UNIT = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.handlingUnit");
        String UNIT_MULTIPLIER = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.unitMultiplier");
        String HANDLING_MAX = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.handlingMax");
        
        try {
            baseHandling = Double.parseDouble(BASE_HANDLING);
            unitHandling = Double.parseDouble(UNIT_HANDLING);
            unitMultiplier = Double.parseDouble(UNIT_MULTIPLIER);
            handlingMax = Double.parseDouble(HANDLING_MAX);
        }
        catch ( NumberFormatException nfe ) {
            Debug.logError("[ShippingEvents.calcHandling] Error parsing property values.");
            return 0.00;
        }
                        
        Iterator cartItemIterator = cart.iterator();
        double totalUnits = 0.00000;
        while ( cartItemIterator.hasNext() ) {            
            ShoppingCartItem item = (ShoppingCartItem) cartItemIterator.next();
            if ( item.shippingApplies() ) {                
                // Valid HANDLING_UNIT(s) include : price/weight/quantity
                if ( HANDLING_UNIT.equalsIgnoreCase("QUANTITY") ) {
                    // calc based on quantity                
                    totalUnits += item.getQuantity();
                }
                else if ( HANDLING_UNIT.equalsIgnoreCase("PRICE") ) {
                    // calc based on price
                    totalUnits += item.getTotalPrice();
                }
                else if ( HANDLING_UNIT.equalsIgnoreCase("WEIGHT") ) {
                    // calc based on weight
                    totalUnits += (item.getWeight() * item.getQuantity());
                }
            }
        }
                        
        double unitTotal = totalUnits * unitMultiplier;
        double handlingTotal = (unitTotal * unitHandling) + baseHandling;
        if ( (handlingMax > 0) && (handlingTotal > handlingMax) )
            return handlingMax;
        else
            return handlingTotal;
    }
    
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
        Debug.logInfo("[ShippingEvents.getUPSRate] Total Weight: " + weightString);
    
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
            Debug.logError("[ShippingEvents.getUPSRate] Problems getting UPS Rate Infomation.");
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
                Debug.logInfo("[ShippingEvents.getUPSRate] Resp List: " + value);
            }
            
            // Shipping method is index 5
            // Shipping rate is index 12
            if ( !respList.get(5).equals(upsMethod) )
                Debug.logInfo("[ShippingEvents.getUPSRate] Shipping method does not match.");                
            try {
                upsRate = Double.parseDouble((String)respList.get(12));
            }
            catch ( NumberFormatException nfe ) {
                Debug.logError("[ShippingEvents.getUPSRate] Problems parsing rate value.");                
            }                                    
        }    
                        
        return upsRate;
    }
}



