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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.commonapp.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.commonapp.order.order.OrderChangeHelper;
import org.ofbiz.commonapp.order.order.OrderReadHelper;
import org.ofbiz.commonapp.party.contact.ContactHelper;
import org.ofbiz.commonapp.product.catalog.CatalogWorker;
import org.ofbiz.core.entity.EntityExpr;
import org.ofbiz.core.entity.EntityOperator;
import org.ofbiz.core.entity.EntityUtil;
import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.service.ServiceUtil;
import org.ofbiz.core.stats.VisitHandler;
import org.ofbiz.core.util.Debug;
import org.ofbiz.core.util.GeneralException;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.StringUtil;
import org.ofbiz.core.util.UtilFormatOut;
import org.ofbiz.core.util.UtilMisc;
import org.ofbiz.core.util.UtilProperties;
import org.ofbiz.core.util.UtilValidate;

/**
 * Events used for processing checkout and orders.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
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
        StringBuffer errorMessage = new StringBuffer();

        if (cart != null && cart.size() > 0) {
            String shippingMethod = request.getParameter("shipping_method");
            String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
            String checkOutPaymentId = request.getParameter("checkOutPaymentId");            
            String correspondingPoId = request.getParameter("corresponding_po_id");
            String shippingInstructions = request.getParameter("shipping_instructions");
            String orderAdditionalEmails = request.getParameter("order_additional_emails");
            String maySplit = request.getParameter("may_split");
            String giftMessage = request.getParameter("gift_message");
            String isGift = request.getParameter("is_gift");
            String billingAccountId = null;
            if (checkOutPaymentId != null && checkOutPaymentId.startsWith("EXT_BILLACT")) {
                billingAccountId = checkOutPaymentId.substring(checkOutPaymentId.indexOf('|')+1);
                checkOutPaymentId = "EXT_BILLACT"; 
            } 
            
            if (UtilValidate.isNotEmpty(shippingMethod)) {
                int delimiterPos = shippingMethod.indexOf('@');
                String shipmentMethodTypeId = null;
                String carrierPartyId = null;

                if (delimiterPos > 0) {
                    shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                    carrierPartyId = shippingMethod.substring(delimiterPos + 1);
                }

                cart.setShipmentMethodTypeId(shipmentMethodTypeId);
                cart.setCarrierPartyId(carrierPartyId);
            } else {
                errorMessage.append("<li>Please Select a Shipping Method");
            }
            cart.setShippingInstructions(shippingInstructions);
            if (UtilValidate.isNotEmpty(maySplit)) {
                cart.setMaySplit(Boolean.valueOf(maySplit));
            } else {
                errorMessage.append("<li>Please Select a Splitting Preference");
            }

            cart.setGiftMessage(giftMessage);
            if (UtilValidate.isNotEmpty(isGift)) {
                cart.setIsGift(Boolean.valueOf(isGift));
            } else {
                errorMessage.append("<li>Please Specify Whether or Not This Order is a Gift");
            }

            cart.setOrderAdditionalEmails(orderAdditionalEmails);

            // set the shipping address
            if (UtilValidate.isNotEmpty(shippingContactMechId)) {
                cart.setShippingContactMechId(shippingContactMechId);
            } else {
                errorMessage.append("<li>Please Select a Shipping Destination");
            }

            // set the payment method option
            if (UtilValidate.isNotEmpty(checkOutPaymentId)) {
                // clear out the old payments
                cart.clearPaymentMethodTypeIds();
                cart.clearPaymentMethodIds();
                // all payment method ids will be numeric, type ids will start with letter
                if (Character.isLetter(checkOutPaymentId.charAt(0))) {
                    cart.addPaymentMethodTypeId(checkOutPaymentId);
                } else {
                    cart.addPaymentMethodId(checkOutPaymentId);
                }            
            } else if (UtilValidate.isEmpty(checkOutPaymentId)) {
                errorMessage.append("<li>Please Select a Method of Billing");
            }
                
            // set the billingAccountId - if null then set it to null (resetting)
            cart.setBillingAccountId(billingAccountId);
            
            // set the PO number              
            if (UtilValidate.isNotEmpty(correspondingPoId)) {
                cart.setPoNumber(correspondingPoId);
            } else {
                cart.setPoNumber("(none)");
            }            
                                              
        } else {
            errorMessage.append("<li>There are no items in the cart.");
        }

        if (errorMessage.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMessage.toString());
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

        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        String orderId = cart.getOrderId();
        
        // format the grandTotal
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        double cartTotal = cart.getGrandTotal();
		String grandTotalString = formatter.format(cartTotal);               
        Double grandTotal = null;
		try {
			grandTotal = new Double(formatter.parse(grandTotalString).doubleValue());
		} catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed currency amount from DecimalFormat", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "ERROR: Could not create order (problem parsing order totals");
            return "error";			
		}

        // store the order - build the context
        Map context = cart.makeCartMap(dispatcher, explodeOrderItems(request));

        //get the TrackingCodeOrder List
        List trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
        context.put("trackingCodeOrders", trackingCodeOrders);
        
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        if (distributorId != null) context.put("distributorId", distributorId);
        if (affiliateId != null) context.put("affiliateId", affiliateId);
        
        // need the partyId; don't use userLogin in case of an order via order mgr
        String partyId = cart.getPartyId();             
               
        context.put("grandTotal", grandTotal);
        context.put("userLogin", userLogin);
        context.put("partyId", partyId);
        context.put("prodCatalogId", CatalogWorker.getCurrentCatalogId(request));
        context.put("visitId", VisitHandler.getVisitId(session));
        context.put("webSiteId", CatalogWorker.getWebSiteId(request));

        // store the order - invoke the service
        Map result = null;

        try {
            result = dispatcher.runSync("storeOrder", context);
            orderId = (String) result.get("orderId");
            if (orderId != null && orderId.length() > 0) {
                cart.setOrderId(orderId);
                if (cart.getFirstAttemptOrderId() == null)
                    cart.setFirstAttemptOrderId(orderId);
            }
        } catch (GenericServiceException e) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "ERROR: Could not create order (problem invoking the service: " + e.getMessage() + ")");
            Debug.logError(e, module);
            return "error";
        }

        // check for error message(s)
        if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE)) ||
            result.containsKey(ModelService.ERROR_MESSAGE) ||
            result.containsKey(ModelService.ERROR_MESSAGE)) {

            request.setAttribute(SiteDefs.ERROR_MESSAGE, ServiceUtil.makeErrorMessage(result, "<li>", "</li>", "Did not complete the order, the following occurred: <ul>", "</ul>"));
            return "error";
        }

        // set the orderId for use by chained events
        request.setAttribute("order_id", orderId);
        request.setAttribute("orderId", orderId);
        request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
        
        // save the emails to the order                              
        List toBeStored = new LinkedList();
               
        GenericValue party = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting Party record", module);
            party = null;
        }
        
        // create order contact mechs for the email address(s)
        if (party != null) {
            Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = (GenericValue) emailIter.next();
                GenericValue orderContactMech = delegator.makeValue("OrderContactMech",
                        UtilMisc.toMap("orderId", orderId, "contactMechId", email.getString("contactMechId"), "contactMechPurposeTypeId", "ORDER_EMAIL"));
                toBeStored.add(orderContactMech);                                   
            }
        }
                                             
        // create dummy contact mechs and order contact mechs for the additional emails    
        String additionalEmails = cart.getOrderAdditionalEmails();  
        List emailList = StringUtil.split(additionalEmails, ",");
        if (emailList == null) emailList = new ArrayList();                                       
        Iterator eli = emailList.iterator();       
        while (eli.hasNext()) {
            String email = (String) eli.next();
            String contactMechId = delegator.getNextSeqId("ContactMech").toString();
            GenericValue contactMech = delegator.makeValue("ContactMech", 
                    UtilMisc.toMap("contactMechId", contactMechId, "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email)); 
                                                    
            GenericValue orderContactMech = delegator.makeValue("OrderContactMech", 
                    UtilMisc.toMap("orderId", orderId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "ORDER_EMAIL"));
            toBeStored.add(contactMech);
            toBeStored.add(orderContactMech);                                                         
        }
        
        if (toBeStored.size() > 0) {
            try {
                if (Debug.verboseOn()) Debug.logVerbose("To Be Stored: " + toBeStored, module);
                delegator.storeAll(toBeStored);
                /* Why is this here?
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {}
                */
            } catch (GenericEntityException e) {
                // not a fatal error; so just print a message
                Debug.logWarning(e, "Problems storing order email contact information", module);
            }
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
        ServletContext application = (ServletContext) request.getAttribute("servletContext");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);

        List items = cart.makeOrderItems();
        List adjs = cart.makeAllAdjustments();
        GenericValue shipAddress = cart.getShippingAddress();

        if (shipAddress == null) {
            throw new GeneralException("Shipping address is not set in the shopping cart.");
        }

        // remove old tax adjustments
        cart.removeAdjustmentByType("SALES_TAX");

        // get the tax adjustments
        List taxReturn = getTaxAdjustments(dispatcher, "calcTax", items, adjs, shipAddress);

        if (Debug.verboseOn()) Debug.logVerbose("ReturnList: " + taxReturn, module);

        List orderAdj = (List) taxReturn.get(0);
        List itemAdj = (List) taxReturn.get(1);

        // pass the order adjustments back
        if (orderAdj != null && orderAdj.size() > 0) {
            Iterator oai = orderAdj.iterator();

            while (oai.hasNext())
                cart.addAdjustment((GenericValue) oai.next());
        }

        // return the order item adjustments
        if (itemAdj != null && itemAdj.size() > 0) {
            List cartItems = cart.items();

            for (int i = 0; i < cartItems.size(); i++) {
                ShoppingCartItem item = (ShoppingCartItem) cartItems.get(i);
                List itemAdjustments = (List) itemAdj.get(i);
                Iterator ida = itemAdjustments.iterator();

                while (ida.hasNext())
                    item.addAdjustment((GenericValue) ida.next());
            }
        }
    }

    // Calc the tax adjustments.
    private static List getTaxAdjustments(LocalDispatcher dispatcher, String taxService, List orderItems,
        List allAdjustments, GenericValue shipAddress) throws GeneralException {
        List products = new ArrayList(orderItems.size());
        List amounts = new ArrayList(orderItems.size());
        List shipAmts = new ArrayList(orderItems.size());

        double orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, allAdjustments);
        List orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(allAdjustments);
        Double cartShipping = new Double(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

        // build up the list of tax calc service parameters
        for (int i = 0; i < orderItems.size(); i++) {
            GenericValue orderItem = (GenericValue) orderItems.get(i);

            try {
                products.add(i, orderItem.getRelatedOne("Product"));  // get the product entity
                amounts.add(i, new Double(OrderReadHelper.getOrderItemSubTotal(orderItem, allAdjustments, true, false))); // get the item amount
                shipAmts.add(i, new Double(OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, allAdjustments, false, false, true))); // get the shipping amount
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot read order item entity (" + e.getMessage() + ")", module);
                throw new GeneralException("Cannot read the order item entity", e);
            }
        }
        Map serviceContext = UtilMisc.toMap("itemProductList", products, "itemAmountList", amounts,
                "itemShippingList", shipAmts, "orderShippingAmount", cartShipping, "shippingAddress", shipAddress);

        Map serviceResult = null;

        try {
            serviceResult = dispatcher.runSync(taxService, serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException("Problem occurred in tax service (" + e.getMessage() + ")", e);
        }

        // the adjustments (returned in order) from taxware.
        List orderAdj = (List) serviceResult.get("orderAdjustments");
        List itemAdj = (List) serviceResult.get("itemAdjustments");

        return UtilMisc.toList(orderAdj, itemAdj);
    }

    public static boolean explodeOrderItems(HttpServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
                
        // Load the order.properties file.
        URL orderPropertiesUrl = CheckOutEvents.getOrderProperties(request);
        return UtilProperties.propertyValueEqualsIgnoreCase(orderPropertiesUrl, "order.item.explode", "Y");
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

        // Load the order.properties file.
        URL orderPropertiesUrl = CheckOutEvents.getOrderProperties(request);    

        // Get some payment related strings from order.properties.        
        final String DECLINE_MESSAGE = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.payment.declined.message", "Error! Set the declined message!");

        // Get the orderId from the cart.
        String orderId = cart.getOrderId();
        
        // Get the paymentMethodTypeId - this will need to change when ecom supports multiple payments
        List paymentMethodTypeId = cart.getPaymentMethodTypeIds(); 

        // Check the payment preferences; if we have ANY w/ status PAYMENT_NOT_AUTH invoke payment service.
        boolean requireAuth = false;
        List paymentPreferences = null;
        try {
            Map paymentFields = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_AUTH");
            paymentPreferences = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            throw new GeneralException("Problems getting payment preferences", e);
        }
        if (paymentPreferences != null && paymentPreferences.size() > 0)
            requireAuth = true;
        
        // Invoke payment processing.
        if (requireAuth) {
            Map paymentResult = null;
            try {
                // invoke the payment gateway service.
                paymentResult = dispatcher.runSync("authOrderPayments", UtilMisc.toMap("orderId", orderId));
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
            if (Debug.verboseOn()) Debug.logVerbose("Finsished w/ Payment Service", module);
            if (paymentResult != null && paymentResult.containsKey("processResult")) {
                String authResp = (String) paymentResult.get("processResult");

                if (!authResp.equals("APPROVED")) {
                    // order was NOT approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was NOT a success!", module);
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>" + DECLINE_MESSAGE);                    
                                       
                     boolean ok = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
                    if (!ok)
                        throw new GeneralException("Problem with order change; see above error");                                                
                        
                    // null out the orderId for next pass.
                    cart.setOrderId(null);
                    return false;                                                               
                } else {
                    // order WAS approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was a success!", module);
                                        
                    // set the order and item status to approved  
                    boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
                    if (!ok)
                        throw new GeneralException("Problem with order change; see above error");                                                                                         
                        
                    return true;                               
                }
            } else {
                // result returned null or service failed
                request.setAttribute(SiteDefs.EVENT_MESSAGE, "<li>Problems with payment authorization. Please try again later.");                
                if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);                    
                                
                // set the order and item status to cancelled and reverse inventory reservations
                boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
                if (!ok)
                    throw new GeneralException("Problem with order change; see above error");
                                              
                // null out the orderId for next pass.
                cart.setOrderId(null);
                return false;                                                                                        
            }
        } else if (cart.getBillingAccountId() != null || paymentMethodTypeId.contains("EXT_COD")) {
            // approve all billing account or COD transactions (would not be able to use account if limit is reached)
            // note this is okay for now since only one payment method can be used; but this will need to be adjusted later
            boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId, orderPropertiesUrl);
            if (!ok)
                throw new GeneralException("Problem with order change; see above error");   
            return true;          
        } else {
            // Handle NO payment gateway as a success.
            return true;
        }
    }
    
    public static String checkOrderBlacklist(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);        
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        
        GenericValue shippingAddressObj = cart.getShippingAddress();
        String shippingAddress = UtilFormatOut.checkNull(shippingAddressObj.getString("address1").toUpperCase());
        List exprs = UtilMisc.toList(new EntityExpr(
                new EntityExpr("blacklistString", true, EntityOperator.EQUALS, shippingAddress, true), EntityOperator.AND,                
                new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_ADDRESS")));
        
        List paymentMethods = cart.getPaymentMethods();
        Iterator i = paymentMethods.iterator();
        while (i.hasNext()) {
            GenericValue paymentMethod = (GenericValue) i.next();
            if (paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
                GenericValue creditCard = null;
                GenericValue billingAddress = null;
                try {
                    creditCard = paymentMethod.getRelatedOne("CreditCard");
                    if (creditCard != null)
                        billingAddress = creditCard.getRelatedOne("PostalAddress");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems getting credit card from payment method", module);
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "Problems reading the database, please try again.");
                    return "error";                    
                }
                if (creditCard != null) {
                    String creditCardNumber = UtilFormatOut.checkNull(creditCard.getString("cardNumber"));
                    exprs.add(new EntityExpr(
                            new EntityExpr("blacklistString", EntityOperator.EQUALS, creditCardNumber), EntityOperator.AND,                            
                            new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_CREDITCARD")));
                }
                if (billingAddress != null) {
                    String address = UtilFormatOut.checkNull(billingAddress.getString("address1").toUpperCase());
                    exprs.add(new EntityExpr(
                            new EntityExpr("blacklistString", true, EntityOperator.EQUALS, address, true), EntityOperator.AND,                            
                            new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_ADDRESS")));
                }  
            }
        }
        
        List blacklistFound = null;
        if (exprs.size() > 0) {            
            try {
                blacklistFound = delegator.findByOr("OrderBlacklist", exprs);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems with OrderBlacklist lookup.", module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Problems reading the database, please try again.");
                return "error";
            }
        }
        
        if (blacklistFound != null && blacklistFound.size() > 0) 
            return "failed";
                                                                                                                          
        return "success";
    }
    
    public static String failedBlacklistCheck(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);        
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
                                    
        // Load the order.properties file.
        URL orderPropertiesUrl = CheckOutEvents.getOrderProperties(request);
     
        // Get some payment related strings from order.properties.       
        final String HEADER_DECLINE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.declined.status", "ORDER_REJECTED");
        final String ITEM_DECLINE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.declined.status", "ITEM_REJECTED");                  
        final String REJECT_MESSAGE = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.payment.rejected.message", "Error! Set the rejected message!");

        // Get the orderId from the cart.
        String orderId = cart.getOrderId();
        
        Map statusResult = null;
        try {
            // set the status on the order header
            statusResult = dispatcher.runSync("changeOrderStatus",
                    UtilMisc.toMap("orderId", orderId, "statusId", HEADER_DECLINE_STATUS));
            if (statusResult.containsKey("errorMessage")) {
                throw new GenericServiceException((String) statusResult.get("errorMessage"));
            }

            // set the status on the order item(s)
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            if (orderHeader != null) {
                Collection orderItems = orderHeader.getRelated("OrderItem");

                if (orderItems != null && orderItems.size() > 0) {
                    Iterator i = orderItems.iterator();
                    while (i.hasNext()) {
                        GenericValue v = (GenericValue) i.next();
                        v.set("statusId", ITEM_DECLINE_STATUS);
                        v.store();
                    }
                }
            }
                        
            // cancel inventory reservations
            try {
                Map cancelResult = dispatcher.runSync("cancelOrderInventoryReservation", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));

                if (ModelService.RESPOND_ERROR.equals((String) cancelResult.get(ModelService.RESPONSE_MESSAGE))) {
                    Debug.logError("cancelOrderInventoryReservation service failed for Order with ID [" + orderId + "] - " + ServiceUtil.makeErrorMessage(cancelResult, "", "\n", "", ""), module);
                }
            } catch (GenericServiceException e) {
                throw new GeneralException("Error in cancelOrderInventoryReservation for Order with ID [" + orderId + "]", e);
            }

            // null out the orderId for next pass.
            cart.setOrderId(null);
        } catch (GeneralException e) {            
            request.setAttribute(SiteDefs.ERROR_MESSAGE, e.getMessage());
            return "error";
        }
        
        // nuke the userlogin
        userLogin.set("enabled", "N");
        try {
            userLogin.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems de-activating userLogin.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Database error.");
            return "error";
        }  
        request.setAttribute(SiteDefs.ERROR_MESSAGE, REJECT_MESSAGE);      
        
        // wipe the cart and session
        cart.clear();
        session.invalidate();
        return "success";
    }   
        
    public static String checkExternalPayment(HttpServletRequest request, HttpServletResponse response) {
        // warning there can only be ONE payment preference for this to work
        // you cannot accept multiple payment type when using an external gateway
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");    
        String orderId = (String) request.getAttribute("order_id");
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));  
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting order header", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting order header. Cannot check external gateways!");
            return "error";
        }
        if (orderHeader != null) {
            List paymentPrefs = null;
            try {
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems getting order payments", module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems getting payment preference. Cannot check external gateways!");
                return "error";
            }
            if (paymentPrefs != null && paymentPrefs.size() > 0) {
                if (paymentPrefs.size() > 1) {
                    Debug.logError("Too many payment preferences, you cannot have more then one when using external gateways", module);
                }
                GenericValue paymentPreference = EntityUtil.getFirst(paymentPrefs);
                String paymentMethodTypeId = paymentPreference.getString("paymentMethodTypeId");
                if (paymentMethodTypeId.startsWith("EXT_")) {
                    String type = paymentMethodTypeId.substring(4);
                    return type.toLowerCase();
                }                
            } 
            return "none";               
        } else {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Error, cannot located order for processing.");
            return "error";        
        }        
    }   
            
    public static String finalizeOrderEntry(HttpServletRequest request, HttpServletResponse response) {        
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
                                         
        // get the mode
        String mode = request.getParameter("finalizeMode");
        
        // set the customer info
        if (mode != null && mode.equals("cust")) {
            String partyId = (String) request.getAttribute("partyId");
            if (partyId != null) {
                request.getSession().setAttribute("orderPartyId", partyId);
            }
        }
        
        // set the shipping method
        if (mode != null && mode.equals("ship")) {
            String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
            if (shippingContactMechId == null) {
                shippingContactMechId = (String) request.getAttribute("contactMechId");                
            }
            if (UtilValidate.isNotEmpty(shippingContactMechId)) {
                cart.setShippingContactMechId(shippingContactMechId);
            } else {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Please enter a shipping address");
                return "error";
            }                
        }
            
        // set the options
        if (mode != null && mode.equals("options")) {            
            String shippingMethod = request.getParameter("shipping_method");                               
            String shippingInstructions = request.getParameter("shipping_instructions");        
            String maySplit = request.getParameter("may_split");
            String giftMessage = request.getParameter("gift_message");
            String isGift = request.getParameter("is_gift");
            if (UtilValidate.isNotEmpty(shippingMethod)) {
                int delimiterPos = shippingMethod.indexOf('@');
                String shipmentMethodTypeId = null;
                String carrierPartyId = null;

                if (delimiterPos > 0) {
                    shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                    carrierPartyId = shippingMethod.substring(delimiterPos + 1);
                }

                cart.setShipmentMethodTypeId(shipmentMethodTypeId);
                cart.setCarrierPartyId(carrierPartyId);
            } else {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Please Select a Shipping Method");
            }  
            cart.setShippingInstructions(shippingInstructions);  
            cart.setGiftMessage(giftMessage);  
            cart.setMaySplit(Boolean.valueOf(maySplit)); 
            cart.setIsGift(Boolean.valueOf(isGift));                  
        }
        
        // payment option; if offline we skip the payment screen
        if (mode != null && mode.equals("payoption")) {
            cart.clearPaymentMethodTypeIds();
            cart.clearPaymentMethodIds();
            String methodType = request.getParameter("paymentMethodType");
            if (methodType != null && methodType.equals("offline")) {                                
                cart.addPaymentMethodTypeId("EXT_OFFLINE");
            }                            
        }
        
        // set the payment
        if (mode != null && mode.equals("payment")) {
            String checkOutPaymentId = request.getParameter("checkOutPaymentId");                            
            if (checkOutPaymentId == null)  
                checkOutPaymentId = (String) request.getAttribute("paymentMethodId");                                               
            if (UtilValidate.isNotEmpty(checkOutPaymentId)) {
                if (!checkOutPaymentId.equals("OFFLINE_PAYMENT")) {                
                    // clear out the old payments
                    cart.clearPaymentMethodTypeIds();
                    cart.clearPaymentMethodIds();
                    // all payment method ids will be numeric, type ids will start with letter
                    if (Character.isLetter(checkOutPaymentId.charAt(0))) {
                        cart.addPaymentMethodTypeId(checkOutPaymentId);
                    } else {
                        cart.addPaymentMethodId(checkOutPaymentId);
                    }
                } else {
                    cart.clearPaymentMethodIds();
                    cart.clearPaymentMethodTypeIds();                    
                    request.setAttribute("OFFLINE_PAYMENT", new Boolean(true));
                }
            }
        } 
        
        // create offline payments
        if (mode != null && mode.equals("offline_payments")) {
            // get a list of payment types
            List paymentTypes = null;
            try {
                paymentTypes = delegator.findAll("PaymentMethodType");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get payment method types from datasource", module);
            }   
            if (paymentTypes != null) {
                Map paymentPrefs = new HashMap();
                double paymentTally = 0.00;
                Iterator pi = paymentTypes.iterator();
                while (pi.hasNext()) {
                    GenericValue paymentMethodType = (GenericValue) pi.next();   
                    String paymentType = null;
                    if (paymentMethodType != null && paymentMethodType.get("paymentMethodTypeId") != null) {
                        paymentType = paymentMethodType.getString("paymentMethodTypeId");
                    }
                    
                    // get the amount by type
                    double paymentAmount = 0.00;
                    if (paymentType != null && !paymentType.equals("OFFLINE")) {                                      
                        String amount = request.getParameter(paymentMethodType.getString("paymentMethodTypeId"));
                        if (amount != null && amount.length() > 0) {
                            try {                                                                                
                                paymentAmount = NumberFormat.getNumberInstance().parse(amount).doubleValue();                                                           
                            } catch (java.text.ParseException pe) {
                                request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems parsing amount.");
                                return "error";
                            }
                        }
                    }
                    
                    // only worry about types w/ an amount
                    if (paymentAmount > 0.00) {
                       paymentPrefs.put(paymentType, new Double(paymentAmount));
                       paymentTally += paymentAmount;                        
                    }                    
                }
                
                double cartTotal = cart.getGrandTotal();
                if (cartTotal != paymentTally) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Totals do not match order total.");
                    return "error";
                } else {
                    Set keySet = paymentPrefs.keySet();
                    Iterator i = keySet.iterator();
                    while (i.hasNext()) {
                        String type = (String) i.next();
                        Double amt = (Double) paymentPrefs.get(type);
                        cart.addPaymentMethodTypeId(type, amt);
                    }
                    request.getSession().setAttribute("OFFLINE_PAYMENTS", new Boolean(true));
                }
            }
        } 
        
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
            
    public static URL getOrderProperties(ServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        // Load the order.properties file.
        URL propsUrl = null;
        try {
            String orderPropertiesStr = (String) request.getAttribute("orderProperties");
            if (orderPropertiesStr != null) {
                propsUrl = new URL(orderPropertiesStr);
            } else {
                propsUrl = application.getResource("/WEB-INF/order.properties");
            }                        
        } catch (MalformedURLException e) {
            Debug.logWarning(e, "Cannot get order.properties URL", module);
        } 
        return propsUrl;       
    }   
       
    public static URL getEcommerceProperties(ServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        // Load the ecommerce.properties file.
        URL propsUrl = null;
        try {
            String ecommercePropertiesStr = (String) request.getAttribute("ecommerceProperties");
            if (ecommercePropertiesStr != null) {
                propsUrl = new URL(ecommercePropertiesStr);
            } else {
                propsUrl = application.getResource("/WEB-INF/ecommerce.properties");
            }                        
        } catch (MalformedURLException e) {
            Debug.logWarning(e, "Cannot get ecommerce.properties URL", module);
        } 
        return propsUrl;       
    }             
}
