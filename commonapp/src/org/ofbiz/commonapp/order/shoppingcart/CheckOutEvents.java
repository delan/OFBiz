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
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.party.contact.*;
import org.ofbiz.commonapp.product.catalog.*;

/**
 * Events used for processing checkout and orders.
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    1.0
 * @created    August 23, 2001
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
                }//else ok
            } else if (UtilValidate.isEmpty(checkOutPaymentId)) {
                errorMessage.append("<li>Please Select a Method of Billing");
            }
        } else {
            errorMessage.append("<li>There are no items in the cart.");
        }

        // TODO: run shipping/tax services here

        if (errorMessage.length() > 0) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE, errorMessage.toString());
            return "error";
        } else {
            return "success";
        }
    }

    // Create order event - uses createOrder service for processing
    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        ShoppingCart cart = (ShoppingCart) request.getSession().getAttribute(SiteDefs.SHOPPING_CART);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute(SiteDefs.USER_LOGIN);

        URL orderPropertiesUrl = null;
        try {
            orderPropertiesUrl = application.getResource("/WEB-INF/order.properties");
        } catch (MalformedURLException e) {
            Debug.logWarning(e, module);
        }

        // Default Payment Info.
        final String PAYMENT_SERVICE = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.payment.service", "NONE");
        final String HEADER_APPROVE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.approved.status", "ORDER_APPROVED");
        final String ITEM_APPROVE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.approved.status", "ITEM_APPROVED");
        final String HEADER_DECLINE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.header.payment.declined.status", "ORDER_REJECTED");
        final String ITEM_DECLINE_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.declined.status", "ITEM_REJECTED");
        final String DECLINE_MESSAGE = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.payment.declined.message", "Error!");

        final String HEADER_CANCEL_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.cancelled.status", "ITEM_CANCELLED");
        final String ITEM_CANCEL_STATUS = UtilProperties.getPropertyValue(orderPropertiesUrl, "order.item.payment.cancelled.status", "ITEM_CANCELLED");

        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        request.getSession().removeAttribute("_QUICK_REORDER_PRODUCTS_");

        String orderId = cart.getOrderId();

        // if the order is already stored cancel the order and create a new one.
        if (!UtilValidate.isEmpty(orderId)) {
            Map cancelOrderContext = UtilMisc.toMap("orderId", orderId, "statusId", HEADER_CANCEL_STATUS);
            Map cancelResult = null;
            try {
                cancelResult = dispatcher.runSync("changeOrderStatus", cancelOrderContext);

                if (cancelResult.containsKey("errorMessage")) {
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>Problems adjusting order status please contact customer service: " + cancelResult.get("errorMessage"));
                    return "error";
                }
                GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                if (orderHeader != null) {
                    Collection orderItems = orderHeader.getRelated("OrderItem");
                    if (orderItems != null && orderItems.size() > 0) {
                        Iterator i = orderItems.iterator();
                        while (i.hasNext()) {
                            GenericValue v = (GenericValue) i.next();
                            v.set("statusId", ITEM_CANCEL_STATUS);
                            v.store();
                        }
                    }
                }
                // blank out the orderId
                cart.setOrderId(null);
            } catch (GenericServiceException gse) {
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "ERROR: Could not update the order status.");
                Debug.logError(gse, module);
                return "error";
            }
            catch (GenericEntityException gee) {
               request.setAttribute(SiteDefs.ERROR_MESSAGE, "ERROR: Could not update the order item status.");
                Debug.logError(gee, module);
                return "error";
            }
        }

        // store the order - build the context
        Map context = cart.makeCartMap();
        String distributorId = (String) request.getSession().getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) request.getSession().getAttribute("_AFFILIATE_ID_");
        if (distributorId != null) context.put("distributorId", distributorId);
        if (affiliateId != null) context.put("affiliateId", affiliateId);
        context.put("userLogin", userLogin);
        context.put("partyId", userLogin.get("partyId"));
        context.put("prodCatalogId", CatalogWorker.getCurrentCatalogId(request));

        // store the order - invoke the service
        Map result = null;
        try {
            result = dispatcher.runSync("storeOrder", context);
            orderId = (String) result.get("orderId");
            if (orderId != null && orderId.length() > 0) {
                cart.setOrderId(orderId);
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

        // check the payment method type, if we are offline payment do not invoke the payment service.
        boolean requireAuth = false;
        List paymentMethodIds = cart.getPaymentMethodIds();
        Iterator paymentMethodIter = paymentMethodIds.iterator();
        while (paymentMethodIter.hasNext() && !requireAuth) {
            String paymentMethodId = (String) paymentMethodIter.next();
            if (!paymentMethodId.equals("OFFLINE"))
                requireAuth = true;
        }

        // Invoke payment processing
        if (requireAuth && PAYMENT_SERVICE != null && !PAYMENT_SERVICE.equalsIgnoreCase("NONE") &&
                !PAYMENT_SERVICE.equalsIgnoreCase("")) {
            Map paymentResult = null;
            try {
                paymentResult = dispatcher.runSync(PAYMENT_SERVICE, UtilMisc.toMap("orderId", orderId));
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
            if (Debug.verboseOn()) Debug.logVerbose("Finsished w/ Payment Service", module);
            if (paymentResult != null && paymentResult.containsKey("authResponse")) {
                String authResp = (String) paymentResult.get("authResponse");
                if (!authResp.equals("SUCCESS")) {
                    // order was NOT approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was NOT a success!", module);
                    request.setAttribute(SiteDefs.ERROR_MESSAGE, "<li>" + DECLINE_MESSAGE);
                    Map statusRes = null;
                    try {
                        statusRes = dispatcher.runSync("changeOrderStatus",
                                UtilMisc.toMap("orderId", orderId, "statusId", HEADER_DECLINE_STATUS));
                        if (statusRes.containsKey("errorMessage"))
                            Debug.logError("Order status service failed: (" + orderId + ") " + statusRes.get("errorMessage"), module);

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
                    } catch (GenericEntityException ee) {
                        Debug.logError(ee, "Problems adjusting order status. (" + orderId + ")", module);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Problems adjusting order item status. (" + orderId + ")", module);
                    }
                    return "error";
                } else {
                    // order WAS approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was a success!", module);
                    Map statusRes = null;
                    try {
                        statusRes = dispatcher.runSync("changeOrderStatus",
                                UtilMisc.toMap("orderId", orderId, "statusId", HEADER_APPROVE_STATUS));
                        if (statusRes.containsKey("errorMessage"))
                            Debug.logError("Order status service failed: (" + orderId + ") " + statusRes.get("errorMessage"), module);

                        GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                        if (orderHeader != null) {
                            Collection orderItems = orderHeader.getRelated("OrderItem");
                            if (orderItems != null && orderItems.size() > 0) {
                                Iterator i = orderItems.iterator();
                                while (i.hasNext()) {
                                    GenericValue v = (GenericValue) i.next();
                                    v.set("statusId", ITEM_APPROVE_STATUS);
                                    v.store();
                                }
                            }
                        }
                    } catch (GenericEntityException ee) {
                        Debug.logError(ee, "Problems adjusting order status. (" + orderId + ")", module);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Problems adjusting order item status. (" + orderId + ")", module);
                    }
                }
            } else {
                // result returned null or service failed
                request.setAttribute(SiteDefs.EVENT_MESSAGE, "<li>Problems with payment authorization. Your order has been saved and will be processed.");
            }
        }

        // only clear the cart if we are finished w/ the customer
        cart.clear();
        return "success";
    }

    public static String renderConfirmOrder(HttpServletRequest request, HttpServletResponse response) {
        String contextRoot = (String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
        //getServletContext appears to be new on the session object for Servlet 2.3
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
        String server = UtilProperties.getPropertyValue("url.properties", "force.http.host", request.getServerName());
        String port = UtilProperties.getPropertyValue("url.properties", "port.http", "80");
        serverRoot.append("http://");
        serverRoot.append(server);
        if (!port.equals("80"))
            serverRoot.append(":" + port);

        try {
            URL url = new URL(serverRoot.toString() + controlPath + "/confirmorder?order_id=" + request.getAttribute("order_id") + "&security_code=" + ORDER_SECURITY_CODE);
            //as nice as it would be to run this through localhost, we can't because the page has to have the correct host so the urls will be created for the email, etc; we could do this and pass the base url in a parameter...
            //Debug.logInfo("Original URL: " + url, module);
            //url = new URL(url.getProtocol(), "127.0.0.1", url.getPort(), url.getFile());
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
        //getServletContext appears to be new on the session object for Servlet 2.3
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
                //mail.addHeaderLine("MIME-Version: 1.0\nContent-type: text/html; charset=us-ascii\n");
                mail.setContent(content, "text/html");
                Transport.send(mail);
            } catch (Exception e) {
                Debug.logError(e, module);
                request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error e-mailing order confirmation, but it was created and will be processed.");
                return "success"; //"error";
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
            return "success"; //"error";
        } catch (Error e) {
            Debug.logError(e, module);
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "Error e-mailing order confirmation, but it was created and will be processed.");
            return "success"; //"error";
        }
        return "success";
    }
}
