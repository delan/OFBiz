/*
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
 *
 *@author     Andy Zeneski
 *@version    $Revision$
 *@since      2.1
*/

importPackage(Packages.java.lang);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);
importPackage(Packages.org.ofbiz.commonapp.order.order);
importPackage(Packages.org.ofbiz.commonapp.party.contact);

var delegator = request.getAttribute("delegator");
var userLogin = session.getAttribute("userLogin"); 
var orderId = request.getParameter("order_id");
if (orderId == null)
    orderId = request.getAttribute("order_id");
var orderHeader = null;

if (orderId != null && orderId.length() > 0) {
  orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
  if (orderHeader != null) {      
      var orderRole = delegator.findByPrimaryKey("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER"));
      if (orderRole == null) {
          context.remove("orderHeader");
          orderHeader = null;
      }
  }
}

if (orderHeader != null) {
    //context.put("orderHeader", orderHeader);
    var orderReadHelper = new OrderReadHelper(orderHeader);
    var orderItems = orderReadHelper.getOrderItems();
    var orderAdjustments = orderReadHelper.getAdjustments();
    var orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
    var orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
    var headerAdjustmentsToShow = OrderReadHelper.getOrderHeaderAdjustmentToShow(orderHeaderAdjustments, orderSubTotal);

    context.put("orderHeader", OrderReadHelper.getOrderHeaderDisplay(orderHeader, orderHeaderAdjustments, orderSubTotal));
    context.put("localOrderReadHelper", orderReadHelper);
    context.put("orderItems", OrderReadHelper.getOrderItemDisplay(orderItems, orderAdjustments));
    context.put("orderAdjustments", orderAdjustments);
    context.put("orderHeaderAdjustments", orderHeaderAdjustments);
    context.put("orderSubTotal", orderSubTotal);
    context.put("headerAdjustmentsToShow", headerAdjustmentsToShow);
    
    var shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
    shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);
    context.put("orderShippingTotal", shippingAmount);

    var taxAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
    taxAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false);
    context.put("orderTaxTotal", taxAmount);   
    context.put("orderGrandTotal", OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments));
    
    var placingCustomerOrderRoles = delegator.findByAnd("OrderRole",UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
    var placingCustomerOrderRole = EntityUtil.getFirst(placingCustomerOrderRoles);        
    var placingCustomerPerson = placingCustomerOrderRole == null ? null : delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", placingCustomerOrderRole.getString("partyId")));
    context.put("placingCustomerPerson", placingCustomerPerson);

    var shippingAddress = orderReadHelper.getShippingAddress();
    context.put("shippingAddress", shippingAddress);
    var billingAccount = orderHeader.getRelatedOne("BillingAccount");
    context.put("billingAccount", billingAccount);
  
    var orderPaymentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderPaymentPreference"));
    if (orderPaymentPreferences != null && orderPaymentPreferences.hasNext()) {
        var orderPaymentPreference = orderPaymentPreferences.next();
        var paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");        
        var paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType");
        context.put("paymentMethod", paymentMethod);
        context.put("paymentMethodType", paymentMethodType);
        
        if (paymentMethod != null && "CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
            var creditCard = paymentMethod.getRelatedOneCache("CreditCard");
            context.put("creditCard", creditCard);
            context.put("formattedCardNumber", ContactHelper.formatCreditCard(creditCard));
        } else if (paymentMethod != null && "EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {
            var eftAccount = paymentMethod.getRelatedOneCache("EftAccount");
            context.put("eftAccount", eftAccount);
        }        
    }   
       
    var orderShipmentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
    if (orderShipmentPreferences != null && orderShipmentPreferences.hasNext()) {
        var shipmentPreference = orderShipmentPreferences.next();
        context.put("carrierPartyId", shipmentPreference.getString("carrierPartyId"));
        context.put("shipmentMethodTypeId", shipmentPreference.getString("shipmentMethodTypeId"));       
        var shipmentMethodType = delegator.findByPrimaryKey("ShipmentMethodType", UtilMisc.toMap("shipmentMethodTypeId", shipmentPreference.getString("shipmentMethodTypeId")));
        context.put("shipMethDescription", shipmentMethodType.getString("description"));       
        context.put("shippingInstructions", shipmentPreference.getString("shippingInstructions"));
        context.put("maySplit", shipmentPreference.getBoolean("maySplit"));
        context.put("giftMessage", shipmentPreference.getString("giftMessage"));
        context.put("isGift", shipmentPreference.getBoolean("isGift"));
        context.put("trackingNumber", shipmentPreference.getString("trackingNumber"));
    }

    var customerPoNumber = null;
    var orderItemPOIter = UtilMisc.toIterator(orderItems);
    if (orderItemPOIter != null && orderItemPOIter.hasNext()) {
        var orderItemPo = orderItemPOIter.next();
        context.put("customerPoNumber", orderItemPo.getString("correspondingPoId"));
    }
}