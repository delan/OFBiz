/*
 * $Id$
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.stats.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.order.order.*;
import org.ofbiz.commonapp.party.contact.*;
import org.ofbiz.commonapp.product.catalog.*;
import org.ofbiz.commonapp.marketing.tracking.*;

/**
 * Events used for processing checkout and orders.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
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

    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        StringBuffer errorMessage = new StringBuffer();

        if (cart != null && cart.size() > 0) {
            String shippingMethod = request.getParameter("shipping_method");
            String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
            String checkOutPaymentId = request.getParameter("checkOutPaymentId");
            String billingAccountId = request.getParameter("billing_account_id");
            String correspondingPoId = request.getParameter("corresponding_po_id");
            String shippingInstructions = request.getParameter("shipping_instructions");
            String orderAdditionalEmails = request.getParameter("order_additional_emails");
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

            if (UtilValidate.isNotEmpty(shippingContactMechId)) {
                cart.setShippingContactMechId(shippingContactMechId);
            } else {
                errorMessage.append("<li>Please Select a Shipping Destination");
            }

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
            }
            if (UtilValidate.isNotEmpty(billingAccountId)) {
                cart.setBillingAccountId(billingAccountId);
                cart.setPoNumber(correspondingPoId);
                if (UtilValidate.isEmpty(cart.getPoNumber())) {
                    cart.setPoNumber("(none)");
                }// else ok
            } else if (UtilValidate.isEmpty(checkOutPaymentId)) {
                errorMessage.append("<li>Please Select a Method of Billing");
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

        // store the order - build the context
        Map context = cart.makeCartMap(dispatcher, explodeOrderItems(request));

        //get the TrackingCodeOrder List
        List trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
        context.put("trackingCodeOrders", trackingCodeOrders);
        
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        if (distributorId != null) context.put("distributorId", distributorId);
        if (affiliateId != null) context.put("affiliateId", affiliateId);
        
        // check for an order via order mgr
        String partyId = session.getAttribute("orderPartyId") != null ? (String) session.getAttribute("orderPartyId") :
        		userLogin.getString("partyId");
        
        context.put("userLogin", userLogin);
        context.put("partyId", partyId);
        context.put("prodCatalogId", CatalogWorker.getCurrentCatalogId(request));
        context.put("visitId", VisitHandler.getVisitId(session));

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

        // set the orderId for future use
        request.setAttribute("order_id", orderId);
        request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());

        return "success";
    }

    public static String renderConfirmOrder(HttpServletRequest request, HttpServletResponse response) {
        String contextRoot = (String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
        // getServletContext appears to be new on the session object for Servlet 2.3
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        URL orderPropertiesUrl = null;

        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }

        final String ORDER_SECURITY_CODE = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.confirmation.securityCode");

        String controlPath = (String) request.getAttribute(SiteDefs.CONTROL_PATH);

        if (controlPath == null) {
            Debug.logError("[CheckOutEvents.renderConfirmOrder] CONTROL_PATH is null.", module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }

        // build the server root string
        StringBuffer serverRoot = new StringBuffer();

        /* This is disabled for now because we don't have the stuff in place to do a secure get...
         if (request.isSecure()) {
         String server = UtilProperties.getPropertyValue("url.properties", "force.https.host", request.getServerName());
         String port = UtilProperties.getPropertyValue("url.properties", "port.https", "443");
         serverRoot.append("https://");
         serverRoot.append(server);
         if (!"443".equals(port)) {
         serverRoot.append(":");
         serverRoot.append(port);
         }
         } else {
         */
        String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", request.getServerName());
        String port = UtilProperties.getPropertyValue("url.properties", "port.http", "80");

        serverRoot.append("http://");
        serverRoot.append(server);
        if (!"80".equals(port)) {
            serverRoot.append(":");
            serverRoot.append(port);
        }

        /* } */

        try {
            URL url = new URL(serverRoot.toString() + controlPath + "/confirmorder?order_id=" + request.getAttribute("order_id") + "&security_code=" + ORDER_SECURITY_CODE);

            // as nice as it would be to run this through localhost, we can't because the page has to have the correct host so the urls will be created for the email, etc; we could do this and pass the base url in a parameter...
            // Debug.logInfo("Original URL: " + url, module);
            // url = new URL(url.getProtocol(), "127.0.0.1", url.getPort(), url.getFile());
            if (Debug.infoOn()) Debug.logInfo("About to get confirmorder page from the URL: " + url, module);
            HttpClient httpClient = new HttpClient(url);
            String content = httpClient.get();

            request.setAttribute("confirmorder", content);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }
    }

    public static String emailOrder(HttpServletRequest request, HttpServletResponse response) {
        String contextRoot = (String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
        // getServletContext appears to be new on the session object for Servlet 2.3
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        URL ecommercePropertiesUrl = null;
        URL orderPropertiesUrl = null;

        try {
            ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }
        try {
            final String SMTP_SERVER = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.relay.host");
            final String LOCAL_MACHINE = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "smtp.local.machine");
            final String ORDER_SENDER_EMAIL = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.confirmation.email.from");
            final String ORDER_BCC = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.confirmation.email.bcc");
            final String ORDER_CC = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.confirmation.email.cc");

            final String NOTIFY_FROM = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.notification.email.from");
            final String NOTIFY_TO = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.notification.email.to");
            final String NOTIFY_CC = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.notification.email.cc");
            final String NOTIFY_BCC = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.notification.email.bcc");

            GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);
            String orderAdditionalEmails = (String) request.getAttribute("orderAdditionalEmails");
            StringBuffer emails = new StringBuffer();

            if (orderAdditionalEmails != null) {
                emails.append(orderAdditionalEmails);
            }
            GenericValue party = null;

            try {
                party = userLogin.getRelatedOne("Party");
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                party = null;
            }
            if (party != null) {
                Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));

                while (emailIter != null && emailIter.hasNext()) {
                    GenericValue email = (GenericValue) emailIter.next();

                    emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
                }
            }

            String content = (String) request.getAttribute("confirmorder");

            try {
                // JavaMail contribution from Chris Nelson 11/21/2001
                Properties props = new Properties();

                props.put("mail.smtp.host", SMTP_SERVER);
                Session session = Session.getDefaultInstance(props);

                MimeMessage mail = new MimeMessage(session);

                mail.setFrom(new InternetAddress(ORDER_SENDER_EMAIL));
                mail.addRecipients(Message.RecipientType.TO, emails.toString());

                if (UtilValidate.isNotEmpty(ORDER_CC)) {
                    mail.addRecipients(Message.RecipientType.CC, ORDER_CC);
                }
                if (UtilValidate.isNotEmpty(ORDER_BCC)) {
                    mail.addRecipients(Message.RecipientType.BCC, ORDER_BCC);
                }

                String orderId = (String) request.getAttribute("order_id");

                mail.setSubject(UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " Order" + UtilFormatOut.ifNotEmpty(orderId, " #", "") + " Confirmation");
                // mail.addHeaderLine("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
                mail.setContent(content, "text/html");
                Transport.send(mail);
            } catch (Exception e) {
                Debug.logError(e, module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error e-mailing order confirmation, but it was created and will be processed.");
                return "success"; // "error";
            }

            try {
                // send off the notification email if defined.
                if (UtilValidate.isNotEmpty(NOTIFY_FROM) && UtilValidate.isNotEmpty(NOTIFY_TO)) {
                    Properties props = new Properties();

                    props.put("mail.smtp.host", SMTP_SERVER);
                    Session session = Session.getDefaultInstance(props);

                    MimeMessage mail = new MimeMessage(session);

                    mail.setFrom(new InternetAddress(NOTIFY_FROM));
                    mail.addRecipients(Message.RecipientType.TO, NOTIFY_TO);

                    if (UtilValidate.isNotEmpty(NOTIFY_CC)) {
                        mail.addRecipients(Message.RecipientType.CC, NOTIFY_CC);
                    }
                    if (UtilValidate.isNotEmpty(NOTIFY_BCC)) {
                        mail.addRecipients(Message.RecipientType.BCC, NOTIFY_BCC);
                    }

                    String orderId = (String) request.getAttribute("order_id");

                    mail.setSubject(UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name", "") + " Order" + UtilFormatOut.ifNotEmpty(orderId, " #", "") + " Notification");
                    mail.setContent(content, "text/html");
                    Transport.send(mail);
                }
            } catch (Exception e) {
                Debug.logError(e, module);
            }

        } catch (RuntimeException re) {
            Debug.logError(re, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error e-mailing order confirmation, but it was created and will be processed.");
            return "success"; // "error";
        } catch (Error e) {
            Debug.logError(e, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error e-mailing order confirmation, but it was created and will be processed.");
            return "success"; // "error";
        }
        return "success";
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

        if (Debug.verboseOn()) Debug.logVerbose("ReturnList: " + taxReturn);

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
                Debug.logError(e, "Cannot read order item entity (" + e.getMessage() + ")");
                throw new GeneralException("Cannot read the order item entity", e);
            }
        }
        Map serviceContext = UtilMisc.toMap("itemProductList", products, "itemAmountList", amounts,
                "itemShippingList", shipAmts, "orderShippingAmount", cartShipping, "shippingAddress", shipAddress);

        Map serviceResult = null;

        try {
            serviceResult = dispatcher.runSync(taxService, serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e);
            throw new GeneralException("Problem occured in tax service (" + e.getMessage() + ")", e);
        }

        // the adjustments (returned in order) from taxware.
        List orderAdj = (List) serviceResult.get("orderAdjustments");
        List itemAdj = (List) serviceResult.get("itemAdjustments");

        return UtilMisc.toList(orderAdj, itemAdj);
    }

    public static boolean explodeOrderItems(HttpServletRequest request) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        // Load the order.properties file.
        URL orderPropertiesUrl = null;

        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }
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
            Debug.logError(e);
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
        URL orderPropertiesUrl = null;
        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }

        // Get some payment related strings from order.properties.
        final String HEADER_APPROVE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.approved.status", "ORDER_APPROVED");
        final String ITEM_APPROVE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.approved.status", "ITEM_APPROVED");
        final String HEADER_DECLINE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.declined.status", "ORDER_REJECTED");
        final String ITEM_DECLINE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.declined.status", "ITEM_REJECTED");
        final String HEADER_CANCELLED_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.cancelled.status", "ORDER_CANCELLED");
        final String ITEM_CANCELLED_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.cancelled.status", "ITEM_CANCELLED");        
        final String DECLINE_MESSAGE = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.payment.declined.message", "Error! Set the declined message!");

        // Get the orderId from the cart.
        String orderId = cart.getOrderId();

        // Check the payment method type, if we are offline payment do not invoke the payment service.
        boolean requireAuth = false;
        List paymentMethodIds = cart.getPaymentMethodIds();
        Iterator paymentMethodIter = paymentMethodIds.iterator();

        while (paymentMethodIter.hasNext() && !requireAuth) {
            String paymentMethodId = (String) paymentMethodIter.next();

            if (!paymentMethodId.equals("OFFLINE"))
                requireAuth = true;
        }

        // Invoke payment processing.
        if (requireAuth) {
            Map paymentResult = null;
            try {
                // invoke the payment gateway service.
                paymentResult = dispatcher.runSync("processPayments", UtilMisc.toMap("orderId", orderId));
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
                        return false;
                    } catch (GenericEntityException e) {
                        throw new GeneralException("Problems adjusting item status (" + orderId + ")", e);
                    } catch (GenericServiceException e) {
                        throw new GeneralException("Problems adjusting order status (" + orderId + ")", e);
                    }
                } else {
                    // order WAS approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was a success!", module);
                    try {
                        // set the status on the order header
                        Map statusResult = dispatcher.runSync("changeOrderStatus",
                                UtilMisc.toMap("orderId", orderId, "statusId", HEADER_APPROVE_STATUS));

                        if (statusResult.containsKey("errorMessage") || ModelService.RESPOND_ERROR.equals((String) statusResult.get(ModelService.RESPONSE_MESSAGE))) {
                            Debug.logError("Order status service failed: [" + orderId + "] " + ServiceUtil.makeErrorMessage(statusResult, "", "\n", "", ""), module);
                        }

                        // set the status on the order item(s)
                        GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

                        if (orderHeader != null) {
                            Collection orderItems = orderHeader.getRelated("OrderItem");

                            if (orderItems != null && orderItems.size() > 0) {
                                Iterator orderItemsIter = orderItems.iterator();

                                while (orderItemsIter.hasNext()) {
                                    GenericValue orderItem = (GenericValue) orderItemsIter.next();
                                    orderItem.set("statusId", ITEM_APPROVE_STATUS);
                                    orderItem.store();
                                }
                            }
                        }
                        return true;
                    } catch (GenericEntityException e) {
                        throw new GeneralException("Problems adjusting item status (" + orderId + ")", e);
                    } catch (GenericServiceException e) {
                        throw new GeneralException("Problems adjusting order status (" + orderId + ")", e);
                    }
                }
            } else {
                // result returned null or service failed
                request.setAttribute(SiteDefs.EVENT_MESSAGE, "<li>Problems with payment authorization. Please try again later.");                
                if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);                    
                
                Map statusResult = null;
                try {
                    // set the status on the order header
                    statusResult = dispatcher.runSync("changeOrderStatus",
                            UtilMisc.toMap("orderId", orderId, "statusId", HEADER_CANCELLED_STATUS));
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
                                v.set("statusId", ITEM_CANCELLED_STATUS);
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
                    return false;
                } catch (GenericEntityException e) {
                    throw new GeneralException("Problems adjusting item status (" + orderId + ")", e);
                } catch (GenericServiceException e) {
                    throw new GeneralException("Problems adjusting order status (" + orderId + ")", e);
                }                
            }
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
        URL orderPropertiesUrl = null;
        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }

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
}
