package org.ofbiz.ecommerce.checkout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

import org.ofbiz.ecommerce.shoppingcart.*;
import org.ofbiz.core.util.*;

public class ShippingEvents {
    
    public static final String UPS_RATES_URL = "http://www.ups.com/using/services/rave/qcostcgi.cgi";
    
    public static String calcShipping(HttpServletRequest request, HttpServletResponse response) {        
        String contextRoot=(String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
        String SHIPPING_MODEL = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.model");
        String SHIPPING_FROM_ZIP = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "shipping.fromZip");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
                
        double shippingTotal = 0.00;
        
        // Setup the available models.
        if ( SHIPPING_MODEL.equalsIgnoreCase("NONE") ) {
            double handling = calcHandling(cart,contextRoot);
            shippingTotal = handling;
        }
        else if ( SHIPPING_MODEL.equalsIgnoreCase("UPS") ) {
            HashMap upsMapping = new HashMap();
            upsMapping.put("GROUND@UPS","GND");
            upsMapping.put("AIR@UPS","2DA");
            upsMapping.put("NEXT_DAY@UPS","1DA");
            
            String shippingMethod = request.getParameter("shipping_method");                        
            double handling = calcHandling(cart,contextRoot);
            double shipping = 0.00;
            if ( upsMapping.containsKey(shippingMethod) )
                shipping = getUPSRate(cart,SHIPPING_FROM_ZIP,(String) upsMapping.get(shippingMethod));
                                    
            shippingTotal = shipping + handling;
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
        if ( handlingTotal > handlingMax )
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



