/*
 * $Id$
 * $Log$
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
            String paymentCode = request.getParameter("payment_code");
            String correspondingPoId = request.getParameter("corresponding_po_id");
            String shippingInstructions = request.getParameter("shipping_instructions");
            String orderAdditionalEmails = request.getParameter("order_additional_emails");

            cart.setShippingMethod(shippingMethod);
            cart.setShippingInstructions(shippingInstructions);
            cart.setOrderAdditionalEmails(orderAdditionalEmails);

            cart.setShippingContactMechId(shippingContactMechId);
            if (paymentCode.startsWith("ccard:")) {
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
        GenericValue userLogin = (GenericValue) request.getAttribute(SiteDefs.USER_LOGIN);
        StringBuffer errorMessage = new StringBuffer();
        if (cart != null && cart.size() > 0) {
            GenericHelper helper = (GenericHelper) request.getAttribute("helper");
            Long orderId = helper.getNextSeqId("OrderHeader");
            GenericValue order = helper.makeValue("OrderHeader", UtilMisc.toMap("orderId", orderId, "orderTypeId", "SALES_ORDER", "orderDate", UtilDateTime.nowTimestamp(), "entryDate", UtilDateTime.nowTimestamp(), "statusId", "Ordered", "billingAccountId", cart.getBillingAccountId())); 
            order.preStoreOther(helper.makeValue("OrderAdjustment", UtilMisc.toMap( "orderAdjustmentId", helper.getNextSeqId("OrderAdjustment"), "orderAdjustmentTypeId", "DISCOUNT_ADJUSTMENT", "orderId", orderId, "orderItemSeqId", "NA", "percentage", new Double(cart.getCartDiscount()))));
            order.preStoreOther(helper.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", helper.getNextSeqId("OrderAdjustment"), "orderAdjustmentTypeId", "SHIPPING_CHARGES", "orderId", orderId, "orderItemSeqId", null, "amount", new Double(cart.getShipping()))));
            order.preStoreOther(helper.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", helper.getNextSeqId("OrderAdjustment"), "orderAdjustmentTypeId", "SALES_TAX", "orderId", orderId, "orderItemSeqId", null, "amount", new Double(cart.getSalesTax()))));
            order.preStoreOther(helper.makeValue("OrderContactMech", UtilMisc.toMap( "contactMechId", cart.getShippingContactMechId(), "contactMechPurposeTypeId", "SHIPPING_LOCATION", "orderId", orderId)));
            
            String shippingMethod = cart.getShippingMethod();
            int delimiterPos = shippingMethod.indexOf('@');
            String shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
            String carrierPartyId = shippingMethod.substring(delimiterPos+1);
            Long shipmentId = helper.getNextSeqId("Shipment");
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
                 
            order.preStoreOther(helper.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", helper.getNextSeqId("OrderStatus"), "statusId", "Requested", "orderId", orderId, "statusDateTime", UtilDateTime.nowTimestamp())));

            String creditCardId = cart.getCreditCardId();
            if (creditCardId != null) {
                order.preStoreOther(helper.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", helper.getNextSeqId("OrderPaymentPreference"), "orderId", orderId, "paymentMethodTypeId", "CREDIT_CARD", "paymentInfoId", creditCardId)));
            } else {
                //XXX CASH should not be assumed!!
                order.preStoreOther(helper.makeValue("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", helper.getNextSeqId("OrderPaymentPreference"), "orderId", orderId, "paymentMethodTypeId", "CASH", "paymentInfoId", creditCardId)));
            }
                    
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
        //FIXME use HttpClient to call /confirmorder, store the HTML in request attributes to be used by emailOrder and renderConfirmOrder
        return "error";
    }

    public static String emailOrder(HttpServletRequest request, HttpServletResponse response) {
        //FIXME use HttpClient to call /confirmorder, store the HTML in request attributes to be used by emailOrder and renderConfirmOrder
        return "error";
    }
}