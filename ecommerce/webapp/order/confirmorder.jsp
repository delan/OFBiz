<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     Eric Pabst
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
--%>

<%@ include file="/includes/envsetup.jsp" %>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.misc.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.ContactHelper" %>
<%@ page import="org.ofbiz.commonapp.party.party.PartyHelper" %>

<html>
  <head>
      <base href="<%=serverRoot%>">
      <title>Confirmation Page</title>
      <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

      <link rel="stylesheet" href="<%=serverRoot + request.getContextPath()%>/includes/maincss.css" type="text/css">
  </head>
  <body bgcolor="white">
  <a name="top"></a>

<% 
   final String ORDER_SECURITY_CODE = UtilProperties.getPropertyValue(application.getResource("/WEB-INF/order.properties"), "order.confirmation.securityCode");
   String securityCode = request.getParameter("security_code");
   if (UtilValidate.isNotEmpty(ORDER_SECURITY_CODE)) {
       if (ORDER_SECURITY_CODE.equals(securityCode)) {
           pageContext.setAttribute("validated", "true");
       } 
   } else {
       response.getWriter().print("ERROR: Order Information is NOT Secure!  Please ask System Administrator to set 'order.confirmation.securityCode' in order.properties file<p>");
   }
%>
    
<ofbiz:if name="validated">
<%
  String orderId = request.getParameter("order_id");
  GenericValue orderHeader = null;

  if(UtilValidate.isNotEmpty(orderId)) {
    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
  }%>
<%if (orderHeader != null) pageContext.setAttribute("orderHeader", orderHeader);%>
<ofbiz:if name="orderHeader">
<h1><div class="head1">Order Confirmation</div></h1>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>
<div class="tabletext">Thank you for shopping at <%EntityField.run("layoutSettings", "companyName", pageContext);%> online. Don't forget to stop back for more great deals, contests, new store openings and specials.<br></div>
</ofbiz:if>
<ofbiz:if name="orderHeader">
<%
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
    if(orderPaymentPreferences != null && orderPaymentPreferences.hasNext()) {
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
    if(orderShipmentPreferences != null && orderShipmentPreferences.hasNext()) {
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
    if(orderItemPOIter != null && orderItemPOIter.hasNext()) {
        customerPoNumber = ((GenericValue)orderItemPOIter.next()).getString("correspondingPoId");
    }
%>
<%@ include file="orderinformation.jsp" %>
  
<br>
<%@ include file="orderitems.jsp" %>
<div align=right>  
  <a href="<%=response.encodeUrl(serverRoot + controlPath + "/orderstatus?order_id=" + orderId)%>" class="buttontextbig">[View Order]</a>&nbsp;&nbsp;
  <a href="<%=response.encodeUrl(serverRoot + controlPath + "/main")%>" class="buttontextbig">[Continue Shopping]</a>
</div>
</ofbiz:if> <%-- Order Header --%>
<ofbiz:unless name="orderHeader">
  <p> Order not found. </p>
  <a href="<%=response.encodeUrl(serverRoot + controlPath + "/main")%>" class="buttontextbig">[Continue Shopping]</a>
</ofbiz:unless>
</ofbiz:if>
<ofbiz:unless name="validated">
<font color="red">Security Error!  You do not have permission to view this page.</font><br>
<a href="<%=response.encodeUrl(serverRoot + controlPath + "/main")%>" class="buttontextbig">[Continue Shopping]</a>
</ofbiz:unless>
  </body>
</html>
