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
package org.ofbiz.commonapp.order.shoppingcart;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.commonapp.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.commonapp.product.catalog.CatalogWorker;
import org.ofbiz.commonapp.product.store.ProductStoreWorker;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.stats.VisitHandler;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.GeneralException;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilHttp;

/**
 * Events used for processing checkout and orders.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Revision$
 * @since      2.0
 */
public class CheckOutEvents {

    public static final String module = CheckOutEvents.class.getName();

    public static String cartNotEmpty(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);

        if (cart != null && cart.size() > 0) {
            return "success";
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Cart is empty.");
            return "error";
        }
    }

    public static String setPartialCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        String resp = setCheckOutOptions(request, response);
        request.setAttribute(SiteDefs.ERROR_MESSAGE, null);
        return "success";
    }       
         
    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);        
        String shippingMethod = request.getParameter("shipping_method");
        String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
        String checkOutPaymentId = request.getParameter("checkOutPaymentId");            
        String correspondingPoId = request.getParameter("corresponding_po_id");
        String shippingInstructions = request.getParameter("shipping_instructions");
        String orderAdditionalEmails = request.getParameter("order_additional_emails");
        String maySplit = request.getParameter("may_split");
        String giftMessage = request.getParameter("gift_message");
        String isGift = request.getParameter("is_gift");
        Map callResult;
        
        CheckOutHelper checkOutHelper = new CheckOutHelper(null, null, cart);
        callResult = checkOutHelper.setCheckOutOptions(shippingMethod, shippingContactMechId, 
            checkOutPaymentId, correspondingPoId, shippingInstructions, orderAdditionalEmails,
            maySplit, giftMessage, isGift);
            
       ServiceUtil.getMessages(request, callResult, null, "<li>", "</li>", "<ul>", "</ul>", null, null);

        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    // Create order event - uses createOrder service for processing
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map callResult;

        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        boolean areOrderItemsExploded = explodeOrderItems(request);
        
        //get the TrackingCodeOrder List        
        List trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        String visitId = VisitHandler.getVisitId(session);
        String webSiteId = CatalogWorker.getWebSiteId(request);
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        
        callResult = checkOutHelper.createOrder(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, 
            visitId, webSiteId, productStoreId);
        
        ServiceUtil.getMessages(request, callResult, null, "<li>", "</li>", "<ul>", "</ul>", null, null);
        
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
            // set the orderId for use by chained events
            String orderId = cart.getOrderId();
            request.setAttribute("order_id", orderId);
            request.setAttribute("orderId", orderId);
            request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
        }
        
        
        return cart.getOrderType().toLowerCase();
    }
           
    // Event wrapper for the tax calc.
    public static String calcTax(HttpServletRequest request, HttpServletResponse response) {
        try {
            calcTax(request);
        } catch (GeneralException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, e.getMessage());
            return "error";
        }
        return "success";
    }

    // Invoke the taxCalc
    private static void calcTax(HttpServletRequest request) throws GeneralException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        
        //Calculate and add the tax adjustments
        checkOutHelper.calcAndAddTax(productStoreId);
    }

    public static boolean explodeOrderItems(HttpServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
                
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore.get("explodeOrderItems") == null) {
        	return false;
        }
        return productStore.getBoolean("explodeOrderItems").booleanValue();
    }

    // Event wrapper for processPayment.
    public static String processPayment(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (processPayment(request))
                return "success";
            else
                return "fail";
        } catch (GeneralException e) {
            Debug.logError(e, "", module);
            return "error";
        }
    }

    private static boolean processPayment(HttpServletRequest request) throws GeneralException {
        HttpSession session = request.getSession();
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);

        // Load the ProductStore settings
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        Map callResult = checkOutHelper.processPayment(productStore, userLogin);    
        
        //Generate any messages required
        ServiceUtil.getMessages(request, callResult, null, "<li>", "</li>", "<ul>", "</ul>", null, null);

        //Determine whether it was a success or failure
        return (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS));
    }
    
    public static String checkOrderBlacklist(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);        
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        CheckOutHelper checkOutHelper = new CheckOutHelper(null, delegator, cart);
        String result;
        
        Map callResult = checkOutHelper.checkOrderBlacklist(userLogin);
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            result = (String)callResult.get(ModelService.ERROR_MESSAGE);
        } else {
            result = (String)callResult.get(ModelService.SUCCESS_MESSAGE);
        }
        
        return result;
    }
    
    public static String failedBlacklistCheck(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);        
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        String result;               
                                    
        // Load the properties store
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map callResult = checkOutHelper.failedBlacklistCheck(userLogin, productStore);    
        
        //Generate any messages required
        ServiceUtil.getMessages(request, callResult, null, "<li>", "</li>", "<ul>", "</ul>", null, null);      
        
        // wipe the session
        session.invalidate();
        
        //Determine whether it was a success or not
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            result = (String)callResult.get(ModelService.ERROR_MESSAGE);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, result);
            result = "error";
        } else {
            result = (String)callResult.get(ModelService.ERROR_MESSAGE);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, result);
            result = "success";
        }
        return result;
    }   
        
    public static String checkExternalPayment(HttpServletRequest request, HttpServletResponse response) {
        // warning there can only be ONE payment preference for this to work
        // you cannot accept multiple payment type when using an external gateway
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String result;
        
        String orderId = (String) request.getAttribute("order_id");   
        CheckOutHelper checkOutHelper = new CheckOutHelper(null, delegator, null);
        Map callResult = checkOutHelper.checkExternalPayment(orderId); 
        
        //Generate any messages required
        ServiceUtil.getMessages(request, callResult, null, "<li>", "</li>", "<ul>", "</ul>", null, null);   
        
        // any error messages have prepared for display, return the type ('error' if failed)
        result = (String)callResult.get("type");
        return result;
    }   
            
    public static String finalizeOrderEntry(HttpServletRequest request, HttpServletResponse response) {        
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map paramMap = UtilHttp.getParameterMap(request);
        Boolean offlinePayments;
        String shippingContactMechId = null;
        String shippingMethod = null;                               
        String shippingInstructions = null;        
        String maySplit = null;
        String giftMessage = null;
        String isGift = null;
        String methodType = null;
        String checkOutPaymentId = null;
        
        String mode = request.getParameter("finalizeMode");
        
        //set the customer info
        if (mode != null && mode.equals("cust")) {
            String partyId = (String) request.getAttribute("partyId");
            if (partyId != null) {
                request.getSession().setAttribute("orderPartyId", partyId);
            }
        }
        
        // Get the shipping method
        shippingContactMechId = request.getParameter("shipping_contact_mech_id");
        if (shippingContactMechId == null) {
            shippingContactMechId = (String) request.getAttribute("contactMechId");                
        }
        
        // Get the options
        shippingMethod = request.getParameter("shipping_method");                               
        shippingInstructions = request.getParameter("shipping_instructions");        
        maySplit = request.getParameter("may_split");
        giftMessage = request.getParameter("gift_message");
        isGift = request.getParameter("is_gift");
        
        // payment option; if offline we skip the payment screen
        methodType = request.getParameter("paymentMethodType");
        
        // get the payment
        checkOutPaymentId = request.getParameter("checkOutPaymentId");                            
        if (checkOutPaymentId == null) {
            checkOutPaymentId = (String) request.getAttribute("paymentMethodId");
        }
        
        CheckOutHelper checkOutHelper = new CheckOutHelper(null, delegator, cart);
        Map callResult = checkOutHelper.finalizeOrderEntry(mode, shippingContactMechId, shippingMethod, shippingInstructions, 
            maySplit, giftMessage, isGift, methodType, checkOutPaymentId, paramMap);
        
        //Generate any messages required
        ServiceUtil.getMessages(request, callResult, null, "<li>", "</li>", "<ul>", "</ul>", null, null);      
         
        //Determine whether it was a success or not
        if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
            return "error";
        } else {
            // Seems a bit suspicious that these properties have slightly different names
            offlinePayments = (Boolean)callResult.get("OFFLINE_PAYMENT");
            request.setAttribute("OFFLINE_PAYMENT", offlinePayments);
            offlinePayments = (Boolean)callResult.get("OFFLINE_PAYMENTS");
            request.getSession().setAttribute("OFFLINE_PAYMENTS", offlinePayments);
        }
        
        
        // Determine where to direct the browser
        String requireCustomer = request.getParameter("finalizeReqCustInfo");
        String requireShipping = request.getParameter("finalizeReqShipInfo");
        String requireOptions = request.getParameter("finalizeReqOptions");
        String requirePayment = request.getParameter("finalizeReqPayInfo");                               
                                
        if (requireCustomer == null)
            requireCustomer = "true";           
        if (requireShipping == null)
            requireShipping = "true";
        if (requireOptions == null)
            requireOptions = "true";
        if (requirePayment == null)
            requirePayment = "true";        

        String shipContactMechId = cart.getShippingContactMechId();
        String customerPartyId = cart.getPartyId();
        String shipmentMethodTypeId = cart.getShipmentMethodTypeId();
        List paymentMethodIds = cart.getPaymentMethodIds();
        List paymentMethodTypeIds = cart.getPaymentMethodTypeIds();
        
        if (requireCustomer.equalsIgnoreCase("true") && (customerPartyId == null || customerPartyId.equals("_NA_")))
            return "customer";
            
        if (requireShipping.equalsIgnoreCase("true") && shipContactMechId == null)
            return "shipping";
         
        if (requireOptions.equalsIgnoreCase("true") && shipmentMethodTypeId == null)   
            return "options";
        
        if (requirePayment.equalsIgnoreCase("true")) {
            if (paymentMethodIds == null || paymentMethodIds.size() == 0) {
                if (paymentMethodTypeIds == null || paymentMethodTypeIds.size() == 0) {
                    return "payment";
                }
            }
        }
                        
        return "success";
    }   
}
