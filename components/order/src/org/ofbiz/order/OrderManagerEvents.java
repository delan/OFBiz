/*
 * $Id: OrderManagerEvents.java,v 1.3 2004/07/31 04:10:39 ajzeneski Exp $
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
package org.ofbiz.order;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.service.LocalDispatcher;

/**
 * Order Manager Events
 *
 * @author <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version $Revision: 1.3 $
 * @since 2.0
 */
public class OrderManagerEvents {

    public static final String module = OrderManagerEvents.class.getName();

    public static String processOfflinePayments(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext application = ((ServletContext) request.getAttribute("servletContext"));
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        if (session.getAttribute("OFFLINE_PAYMENTS") != null) {
            String orderId = (String) request.getAttribute("order_id");
            List toBeStored = new LinkedList();
            List paymentPrefs = null;
            GenericValue placingCustomer = null;
            try {
                paymentPrefs = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId));
                List pRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
                if (pRoles != null && pRoles.size() > 0)
                    placingCustomer = EntityUtil.getFirst(pRoles);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems looking up order payment preferences", module);
                request.setAttribute("_ERROR_MESSAGE_", "<li>Error processing offline payments.");
                return "error";
            }
            if (paymentPrefs != null) {
                Iterator i = paymentPrefs.iterator();
                while (i.hasNext()) {
                    // update the preference to received
                    GenericValue ppref = (GenericValue) i.next();
                    ppref.set("statusId", "PAYMENT_RECEIVED");
                    ppref.set("authDate", UtilDateTime.nowTimestamp());
                    toBeStored.add(ppref);
                    
                    // create a payment record
                    toBeStored.add(OrderChangeHelper.createPaymentFromPreference(ppref, null, placingCustomer.getString("partyId"), "Payment received offline and manually entered."));
                }
                
                // store the updated preferences and newly created payments
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems storing payment information", module);
                    request.setAttribute("_ERROR_MESSAGE_", "<li>Problem storing received payment information.");
                    return "error";
                }
                
                // set the status of the order to approved
                OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            }
        }
        return "success";
    }

    public static String receiveOfflinePayment(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        String orderId = request.getParameter("orderId");
        
        // get the order header & payment preferences       
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems reading order header from datasource.", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problems reading order header information.");
            return "error";
        }

        Double grandTotal = new Double(0.00);
        if (orderHeader != null) {
            grandTotal = orderHeader.getDouble("grandTotal");
        }

        // get the payment types to receive
        List paymentMethodTypes = null;

        try {
            List pmtFields = UtilMisc.toList(new EntityExpr("paymentMethodTypeId", EntityOperator.NOT_EQUAL, "EXT_OFFLINE"));
            paymentMethodTypes = delegator.findByAnd("PaymentMethodType", pmtFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting payment types", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problems with PaymentType lookup.");
            return "error";
        }

        if (paymentMethodTypes == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problems with PaymentType lookup.");
            return "error";
        }

        List toBeStored = new LinkedList();
        GenericValue placingCustomer = null;
        try {
            List pRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
            if (pRoles != null && pRoles.size() > 0)
                placingCustomer = EntityUtil.getFirst(pRoles);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up order payment preferences", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Error processing offline payments.");
            return "error";
        }

        Iterator pmti = paymentMethodTypes.iterator();
        double paymentTally = 0.00;
        while (pmti.hasNext()) {
            GenericValue paymentMethodType = (GenericValue) pmti.next();
            String paymentMethodTypeId = paymentMethodType.getString("paymentMethodTypeId");
            String amountStr = request.getParameter(paymentMethodTypeId + "_amount");
            String paymentReference = request.getParameter(paymentMethodTypeId + "_reference");
            if (!UtilValidate.isEmpty(amountStr)) {
                double paymentTypeAmount = 0.00;
                try {
                    paymentTypeAmount = NumberFormat.getNumberInstance().parse(amountStr).doubleValue();
                } catch (java.text.ParseException pe) {
                    request.setAttribute("_ERROR_MESSAGE_", "<li>Problems payment parsing amount.");
                    return "error";
                }
                if (paymentTypeAmount > 0.00) {
                    paymentTally += paymentTypeAmount;
                    
                    // create the OrderPaymentPreference
                    Map prefFields = UtilMisc.toMap("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference").toString());
                    GenericValue paymentPreference = delegator.makeValue("OrderPaymentPreference", prefFields);
                    paymentPreference.set("paymentMethodTypeId", paymentMethodType.getString("paymentMethodTypeId"));
                    paymentPreference.set("maxAmount", new Double(paymentTypeAmount));
                    paymentPreference.set("statusId", "PAYMENT_RECEIVED");
                    paymentPreference.set("orderId", orderId);
                    toBeStored.add(paymentPreference);
                    
                    // create a payment record
                    toBeStored.add(OrderChangeHelper.createPaymentFromPreference(paymentPreference, paymentReference, placingCustomer.getString("partyId"), "Payment received offline and manually entered."));
                }
            }
        }

        // get the current payment prefs
        GenericValue offlineValue = null;
        List currentPrefs = null;
        try {
            List oppFields = UtilMisc.toList(new EntityExpr("orderId", EntityOperator.EQUALS, orderId),
                    new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
            currentPrefs = delegator.findByAnd("OrderPaymentPreference", oppFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Unable to get existing payment preferences from order", module);
        }
        if (currentPrefs != null && currentPrefs.size() > 0) {
            Iterator cpi = currentPrefs.iterator();
            while (cpi.hasNext()) {
                GenericValue cp = (GenericValue) cpi.next();
                String paymentMethodType = cp.getString("paymentMethodTypeId");
                if ("EXT_OFFLINE".equals(paymentMethodType)) {
                    offlineValue = cp;
                } else {
                    Double cpAmt = cp.getDouble("maxAmount");
                    if (cpAmt != null) {
                        paymentTally += cpAmt.doubleValue();
                    }
                }
            }
        }

        // now finish up
        boolean okayToApprove = false;
        if (paymentTally >= grandTotal.doubleValue()) {
            // cancel the offline preference
            okayToApprove = true;
            if (offlineValue != null) {
                offlineValue.set("statusId", "PAYMENT_CANCELLED");
                toBeStored.add(offlineValue);
            }
        }

        // store the status changes and the newly created payment preferences and payments
        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems storing payment information", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problem storing received payment information.");
            return "error";
        }

        if (okayToApprove) {
            // update the status of the order and items
            OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
        }

        return "success";
    }

}
