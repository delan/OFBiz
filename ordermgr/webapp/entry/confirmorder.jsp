<%--
 *  Description: None
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
 *
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, org.ofbiz.commonapp.order.shoppingcart.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.ContactHelper" %>
<%@ page import="org.ofbiz.commonapp.party.party.PartyHelper" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%String controlPath = (String) request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%String serverRoot = (String) request.getAttribute(SiteDefs.SERVER_ROOT_URL);%>

<%
	String orderId = (String) request.getAttribute("order_id");
	GenericValue orderHeader = null;
	if (orderId != null) 
		orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
	if (orderHeader != null) {
		pageContext.setAttribute("orderHeader", orderHeader);
    	OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
    	List orderItems = orderReadHelper.getOrderItems();
    	List orderAdjustments = orderReadHelper.getAdjustments();
    	List orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
    	double orderSubTotal = orderReadHelper.getOrderItemsSubTotal();	
    		
	    GenericValue shippingAddress = orderReadHelper.getShippingAddress();
    	GenericValue billingAddress = null;
    	GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount");

	    GenericValue paymentMethod = null;
    	Iterator orderPaymentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderPaymentPreference"));
    	if (orderPaymentPreferences != null && orderPaymentPreferences.hasNext()) {
      	    GenericValue orderPaymentPreference = (GenericValue)orderPaymentPreferences.next();
       	    paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");
        }

    	GenericValue shipmentPreference = null;
    	String carrierPartyId = null;
    	String shipmentMethodTypeId = null;
    	String shippingInstructions = null;
    	Boolean maySplit = null;
    	String giftMessage = null;
    	Boolean isGift = null;
    	String trackingNumber = null;
    	Iterator orderShipmentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
    	if (orderShipmentPreferences != null && orderShipmentPreferences.hasNext()) {
        	shipmentPreference = (GenericValue)orderShipmentPreferences.next();
        	carrierPartyId = shipmentPreference.getString("carrierPartyId");
        	shipmentMethodTypeId = shipmentPreference.getString("shipmentMethodTypeId");
        	shippingInstructions = shipmentPreference.getString("shippingInstructions");
        	trackingNumber = shipmentPreference.getString("trackingNumber");
        	maySplit = shipmentPreference.getBoolean("maySplit");
        	giftMessage = shipmentPreference.getString("giftMessage");
        
        	isGift = shipmentPreference.getBoolean("isGift");
   	    }

    	String customerPoNumber = null;
    	Iterator orderItemPOIter = UtilMisc.toIterator(orderItems);
    	if (orderItemPOIter != null && orderItemPOIter.hasNext()) {
        	customerPoNumber = ((GenericValue)orderItemPOIter.next()).getString("correspondingPoId");
    	}    	
%>

<%@ include file="/entry/order/orderinformation.jsp" %>
  
<br>
<%@ include file="/entry/order/orderitems.jsp" %>

<% } else { %>
<%ShoppingCart cart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>
<%pageContext.setAttribute("cart", cart);%>
<ofbiz:if name="cart" size="0">
<%
    List orderItems = cart.makeOrderItems();
    List orderAdjustments = cart.makeAllAdjustments();
    List orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(orderAdjustments);
    double orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, orderAdjustments);

    GenericValue shippingAddress = cart.getShippingAddress();
    List paymentMethods = cart.getPaymentMethods();
    GenericValue paymentMethod = null;
    if (paymentMethods != null && paymentMethods.size() > 0) {
        paymentMethod = (GenericValue) paymentMethods.get(0);
    }
    GenericValue billingAddress = null;
    if (paymentMethod != null) {
        GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
        if (creditCard != null) {
            billingAddress = creditCard.getRelatedOne("PostalAddress");
        }
    }
    GenericValue billingAccount = cart.getBillingAccountId() != null ? delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", cart.getBillingAccountId())) : null;

    String customerPoNumber = cart.getPoNumber();
    String carrierPartyId = cart.getCarrierPartyId();
    String shipmentMethodTypeId = cart.getShipmentMethodTypeId();
    String shippingInstructions = cart.getShippingInstructions();
    String trackingNumber = null;
    Boolean maySplit = cart.getMaySplit();
    String giftMessage = cart.getGiftMessage();
    Boolean isGift = cart.getIsGift();    
%>

<%@ include file="/entry/order/orderinformation.jsp" %>
  
<br>
<%@ include file="/entry/order/orderitems.jsp" %>


</ofbiz:if><%-- cart check --%>
<ofbiz:unless name="cart" size="0">
<h3>ERROR: Shopping cart empty, please start over.</h3>
</ofbiz:unless>
<%}%>
