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

var delegator = request.getAttribute("delegator");
var userLogin = session.getAttribute("userLogin");
var cart = session.getAttribute(SiteDefs.SHOPPING_CART);
context.put("cart", cart);

var orderItems = cart.makeOrderItems();
context.put("orderItems", orderItems);

var orderAdjustments = cart.makeAllAdjustments();
context.put("orderAdjustments", orderAdjustments);

var orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(orderAdjustments);
context.put("orderHeaderAdjustments", orderHeaderAdjustments);

var orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, orderAdjustments)
context.put("orderSubTotal", orderSubTotal);
context.put("placingCustomerPerson", userLogin == null ? null : userLogin.getRelatedOne("Person"));
context.put("shippingAddress", cart.getShippingAddress());

var paymentMethods = cart.getPaymentMethods();
var paymentMethod = null;
if (paymentMethod != null && paymentMethods.size() > 0) {
    paymentMethod = paymentMethods.get(0);
}
context.put("paymentMethod", paymentMethod);

if (paymentMethod != null && "CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
    var creditCard = paymentMethod.getRelatedOneCache("CreditCard");
    context.put("creditCard", creditCard);
    context.put("formattedCardNumber", ContactHelper.formatCreditCard(creditCard));
} else if (paymentMethod != null && "EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {
    var eftAccount = paymentMethod.getRelatedOneCache("EftAccount");
    context.put("eftAccount", eftAccount);
}

var paymentMethodTypeIds = cart.getPaymentMethodTypeIds();
var paymentMethodType = null;
var paymentMethodTypeId = null;
if (paymentMethodTypeIds != null && paymentMethodTypeIds.size() > 0) {
    paymentMethodTypeId = paymentMethodTypeIds.get(0);
    paymentMethodType = delegator.findByPrimaryKey("PaymentMethodType", UtilMisc.toMap("paymentMethodTypeId", paymentMethodTypeId));
}   
context.put("paymentMethodType", paymentMethodType);

var billingAddress = null;
if (paymentMethod != null) {
    var creditCard = paymentMethod.getRelatedOne("CreditCard");
    if (creditCard != null) {
        billingAddress = creditCard.getRelatedOne("PostalAddress");
    }
}
context.put("billingAddress", billingAddress);

context.put("billingAccount", cart.getBillingAccountId() != null ? delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", cart.getBillingAccountId())) : null);
context.put("customerPoNumber", cart.getPoNumber());
context.put("carrierPartyId", cart.getCarrierPartyId());
context.put("shipmentMethodTypeId", cart.getShipmentMethodTypeId());
context.put("shippingInstructions", cart.getShippingInstructions());
context.put("maySplit", cart.getMaySplit());
context.put("giftMessage", cart.getGiftMessage());
context.put("isGift", cart.getIsGift());

var shipmentMethodType = delegator.findByPrimaryKey("ShipmentMethodType", UtilMisc.toMap("shipmentMethodTypeId", cart.getShipmentMethodTypeId()));
if (shipmentMethodType != null) context.put("shipMethDescription", shipmentMethodType.getString("description"));

var headerAdjustmentsToShow = OrderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);
context.put("headerAdjustmentsToShow", headerAdjustmentsToShow);

var shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
shippingAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);
context.put("orderShippingTotal", shippingAmount);

var taxAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, true, false);
taxAmount += OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, true, false);
context.put("orderTaxTotal", taxAmount);   
context.put("orderGrandTotal", OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments));
              
