<%
/**
 *  Title: Order Status Page
 *  Description: None
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
 *@author     Andy Zeneski
 *@created    May 22 2001 
 *@version    1.0
 */
%>
<%-- FIXME add status history --%>
<%@ page import="java.util.*, java.text.*" %>

<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*, org.ofbiz.commonapp.party.party.*, org.ofbiz.commonapp.party.contact.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<% pageContext.setAttribute("PageName", "orderstatus"); %>

<%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>

<%
  String orderId = request.getParameter("order_id");
  GenericValue orderHeader = null;
  GenericValue orderRole = null;

  if(orderId != null && orderId.length() > 0) {
    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
    Collection c = delegator.findByAnd("OrderRole",UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"));
    orderRole = (GenericValue) (new ArrayList(c).get(0));
  }%>
<%if (orderHeader != null) pageContext.setAttribute("orderHeader", orderHeader);%>
<ofbiz:if name="orderHeader">
<%
    OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
    List orderItems = orderReadHelper.getOrderItems();
    List orderAdjustments = orderReadHelper.getAdjustments();
    List orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
    double orderSubTotal = orderReadHelper.getOrderItemsSubTotal();

    Collection orderItemList = orderReadHelper.getOrderItems();

    GenericValue shippingAddress = orderReadHelper.getShippingAddress();
    GenericValue billingAddress = orderReadHelper.getBillingAddress(); 
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

    Iterator orderShipmentPreferences = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
    if(orderShipmentPreferences != null && orderShipmentPreferences.hasNext()) {
        shipmentPreference = (GenericValue)orderShipmentPreferences.next();
        carrierPartyId = shipmentPreference.getString("carrierPartyId");
        shipmentMethodTypeId = shipmentPreference.getString("shipmentMethodTypeId");
        shippingInstructions = shipmentPreference.getString("shippingInstructions");
        maySplit = shipmentPreference.getBoolean("maySplit");
        giftMessage = shipmentPreference.getString("giftMessage");
        isGift = shipmentPreference.getBoolean("isGift");
    }

    String customerPoNumber = null;
    Iterator orderItemPOIter = UtilMisc.toIterator(orderItemList);
    if(orderItemPOIter != null && orderItemPOIter.hasNext()) {
        customerPoNumber = ((GenericValue)orderItemPOIter.next()).getString("correspondingPoId");
    }
%>


<%Collection statusChange = delegator.findByAnd("StatusValidChange",UtilMisc.toMap("statusId",orderHeader.getString("statusId")));%>
<%if (statusChange != null) pageContext.setAttribute("statusChange", statusChange);%>
  
<%@ include file="/order/orderinformation.jsp" %>
<br>
<%@ include file="/order/orderitems.jsp" %>

</ofbiz:if> <%-- OrderHeader --%>
<ofbiz:unless name="orderHeader">
<h3>The specified order was not found, please try again.</h3>
</ofbiz:unless>

<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
