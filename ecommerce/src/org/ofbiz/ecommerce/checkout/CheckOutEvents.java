/*
 * $Id$
 * $Log$
 * Revision 1.5  2001/09/11 21:11:06  jonesde
 * Improved error handling.
 *
 * Revision 1.4  2001/09/11 17:27:14  epabst
 * updated order process to be more complete
 *
 * Revision 1.3  2001/09/10 21:56:22  epabst
 * updated/improved
 *
 * Revision 1.2  2001/09/06 16:02:54  epabst
 * renamed Address vars to Location
 * fixed bug where contactMechPurposeTypeId should end in _LOCATION instead of _ADDRESS
 *
 * Revision 1.1.1.1  2001/08/24 01:01:44  azeneski
 * Initial Import
 *
 */

package org.ofbiz.ecommerce.checkout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.party.contact.ContactHelper;
import org.ofbiz.ecommerce.shoppingcart.*;

/**
 * <p><b>Title:</b> CheckOutEvents.java
 * <p><b>Description:</b> Events used for processing checkout and orders.
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
 * Created on August 23, 2001, 7:58 PM
 */
public class CheckOutEvents {
    public static String setCheckOutOptions(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART); 
        StringBuffer errorMessage = new StringBuffer();
        if (cart != null && cart.size() > 0) {
            String shippingMethod = request.getParameter("shipping_method");
            String shippingContactMechId = request.getParameter("shipping_contact_mech_id");
            String paymentCode = UtilFormatOut.checkNull(request.getParameter("payment_code"));
            String correspondingPoId = request.getParameter("corresponding_po_id");
            String shippingInstructions = request.getParameter("shipping_instructions");
            String orderAdditionalEmails = request.getParameter("order_additional_emails");

            cart.setShippingMethod(shippingMethod);
            cart.setShippingInstructions(shippingInstructions);
            cart.setOrderAdditionalEmails(orderAdditionalEmails);

            cart.setShippingContactMechId(shippingContactMechId);
            if(paymentCode.startsWith("ccard:")) {
                cart.setCreditCardId(paymentCode.substring(6));
            } else if (paymentCode.startsWith("billing:")) {
                cart.setBillingAccountId(paymentCode.substring(8));
                cart.setPoNumber(correspondingPoId);
                if (UtilValidate.isEmpty(cart.getPoNumber())) {      
                    cart.setPoNumber("(none)");
                }//else ok
            } else {
                errorMessage.append("<li>Please Select a Method of Billing");
            }
        } else {
            errorMessage.append("<li>There are no items in the cart.");
        }
        
        if ( errorMessage.length() > 0 ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,errorMessage.toString());
            return "error";
        } else {            
            return "success";
        }
    }

    public static String createOrder(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = (ShoppingCart)request.getSession().getAttribute(SiteDefs.SHOPPING_CART); 
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        StringBuffer errorMessage = new StringBuffer();
        if (cart != null && cart.size() > 0) {
            GenericHelper helper = userLogin.getHelper();
            String orderId = helper.getNextSeqId("OrderHeader").toString();
            GenericValue order = helper.makeValue("OrderHeader", UtilMisc.toMap("orderId", orderId, "orderTypeId", "SALES_ORDER", "orderDate", UtilDateTime.nowTimestamp(), "entryDate", UtilDateTime.nowTimestamp(), "statusId", "Ordered", "shippingInstructions", cart.getShippingInstructions())); 
            order.set("billingAccountId", cart.getBillingAccountId());
            if (cart.getCartDiscount() != 0.0) {
                order.preStoreOther(helper.makeValue("OrderAdjustment", UtilMisc.toMap( "orderAdjustmentId", helper.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "DISCOUNT_ADJUSTMENT", "orderId", orderId, "orderItemSeqId", "NA", "percentage", new Double(cart.getCartDiscount()))));
            }
            order.preStoreOther(helper.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", helper.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "SHIPPING_CHARGES", "orderId", orderId, "orderItemSeqId", null, "amount", new Double(cart.getShipping()))));
            order.preStoreOther(helper.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", helper.getNextSeqId("OrderAdjustment").toString(), "orderAdjustmentTypeId", "SALES_TAX", "orderId", orderId, "orderItemSeqId", null, "amount", new Double(cart.getSalesTax()))));
            order.preStoreOther(helper.makeValue("OrderContactMech", UtilMisc.toMap( "contactMechId", cart.getShippingContactMechId(), "contactMechPurposeTypeId", "SHIPPING_LOCATION", "orderId", orderId)));
            
            String shippingMethod = cart.getShippingMethod();
            int delimiterPos = shippingMethod.indexOf('@');
            String shipmentMethodTypeId = "";
            String carrierPartyId = "";
            if(delimiterPos > 0) {
              shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
              carrierPartyId = shippingMethod.substring(delimiterPos+1);
            }
            
            String shipmentId = helper.getNextSeqId("Shipment").toString();
            order.preStoreOther(helper.makeValue("OrderShipmentPreference", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", DataModelConstants.SEQ_ID_NA, "shipmentMethodTypeId", shipmentMethodTypeId, "carrierPartyId", carrierPartyId, "carrierRoleTypeId", "CARRIER" /*XXX*/, "shippingInstructions", cart.getShippingInstructions())));

            Iterator itemIter = cart.iterator();
            int seqId = 1;
            while (itemIter.hasNext()) {
                ShoppingCartItem item = (ShoppingCartItem) itemIter.next();
                String orderItemSeqId = String.valueOf(seqId++);
                Map map = UtilMisc.toMap(
                        "orderId", orderId,
                        "orderItemSeqId", orderItemSeqId,
                        "orderItemTypeId", "SALES_ORDER_ITEM",
                        "productId", item.getProductId(),
                        "quantity", new Double(item.getQuantity()), 
                        "unitPrice", new Double(item.getPrice()));
                map.putAll(UtilMisc.toMap(
                        "shippingInstructions", cart.getShippingInstructions(),
                        "itemDescription", item.getDescription(),
                        "comment", item.getItemComment(),
                        "correspondingPoId", cart.getPoNumber(),
                        "statusId", "Ordered"));
                order.preStoreOther(helper.makeValue("OrderItem", map));
            }
            final String[] USER_ORDER_ROLE_TYPES = {"END_USER_CUSTOMER", "SHIP_TO_CUSTOMER", 
                    "BILL_TO_CUSTOMER", "PLACING_CUSTOMER"};
            for (int i = 0; i < USER_ORDER_ROLE_TYPES.length; i++) {
                order.preStoreOther(helper.makeValue("OrderRole", UtilMisc.toMap(
                        "orderId", orderId,
                        "partyId", userLogin.get("partyId"),
                        "roleTypeId", USER_ORDER_ROLE_TYPES[i])));
            }
                 
            order.preStoreOther(helper.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", helper.getNextSeqId("OrderStatus").toString(), "statusId", "Requested", "orderId", orderId, "statusDatetime", UtilDateTime.nowTimestamp())));

            String creditCardId = cart.getCreditCardId();
            if (creditCardId != null) {
                order.preStoreOther(helper.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", helper.getNextSeqId("OrderPaymentPref").toString(), "orderId", orderId, "paymentMethodTypeId", "CREDIT_CARD", "paymentInfoId", creditCardId)));
            } else {
                //XXX CASH should not be assumed!!
                order.preStoreOther(helper.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", helper.getNextSeqId("OrderPaymentPref").toString(), "orderId", orderId, "paymentMethodTypeId", "CASH", "paymentInfoId", creditCardId)));
            }
            
            helper.create(order);
            
            cart.clear();
                    
            request.setAttribute("order_id", orderId);
            request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
        } else {
            errorMessage.append("<li>There are no items in the cart.");
        }
        
        if ( errorMessage.length() > 0 ) {
            request.setAttribute(SiteDefs.ERROR_MESSAGE,errorMessage.toString());
            return "error";
        } else {            
            return "success";
        }
    }

    public static String renderConfirmOrder(HttpServletRequest request, HttpServletResponse response) {
        String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);
        //XXX need to add secret code since no security yet
        try {
            java.net.URL url = new java.net.URL("http",request.getServerName(),request.getServerPort(),controlPath + "/confirmorder?order_id=" + request.getAttribute("order_id"));
            HttpClient httpClient = new HttpClient(url);
            String content = httpClient.get();
            request.setAttribute("confirmorder", content);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "error generating order confirmation, but it was recorded and will be processed.");
            return "error";
        }
    }

    public static String emailOrder(HttpServletRequest request, HttpServletResponse response) {        
        final String SMTP_SERVER = UtilProperties.getPropertyValue("servers", "smtp.server");
        final String ORDER_SENDER_EMAIL = UtilProperties.getPropertyValue("servers", "smtp.sender.confirmorder");
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute(SiteDefs.USER_LOGIN);
        StringBuffer emails = new StringBuffer((String) request.getAttribute("orderAdditionalEmails"));
        Iterator emailIter = ContactHelper.getContactMech(userLogin.getRelatedOne("Party"), "EMAIL_ADDRESS", false).iterator();
        while (emailIter.hasNext()) {
            GenericValue email = (GenericValue) emailIter.next();
            emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
        }

        String content = (String) request.getAttribute("confirmorder");
        try {
            SendMailSMTP mail = new SendMailSMTP(SMTP_SERVER, ORDER_SENDER_EMAIL, emails.toString(), content);
            mail.send();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(SiteDefs.ERROR_MESSAGE, "error e-mailing order confirmation, but it was created and will be processed.");
            return "success"; //"error";
        }
    }
}