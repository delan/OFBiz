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
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%-- FIXME add status history --%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>

<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<% pageContext.setAttribute("PageName", "orderstatus"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%@ include file="/includes/datepicker.js" %>

<%
  String bColorA1 = "#BBBBFF";
  String bColorA2 = "#EEEEFF";
  String bColorB1 = "#99FF99";
  String bColorB2 = "#CCFFCC";
  String bColorB = "";

  String orderId = request.getParameter("order_id");
  GenericValue orderHeader = null;

  if(orderId != null && orderId.length() > 0) {
    orderHeader = helper.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
    if (orderHeader != null) {
        //check ownership
        GenericValue orderRole = helper.findByPrimaryKey("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER"));
        if (orderRole == null) {
            pageContext.removeAttribute("orderHeader");
        }
    }
  }%>
<%if (orderHeader != null) pageContext.setAttribute("orderHeader", orderHeader);%>
<ofbiz:if name="orderHeader">
    <%
      OrderReadHelper order = new OrderReadHelper(orderHeader);

      Collection orderItemList = orderHeader.getRelated("OrderItem");

      GenericValue shippingAddress = order.getShippingAddress();
      GenericValue billingAddress = order.getBillingAddress(); 
      GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount");
     
      GenericValue creditCardInfo = orderHeader.getRelatedOne("OrderPaymentPreference").getRelatedOne("CreditCardInfo");

      GenericValue shipmentPreference = orderHeader.getRelatedOne("OrderShipmentPreference");
      Boolean maySplit = shipmentPreference.getBoolean("maySplit");
      String carrierPartyId = shipmentPreference.getString("carrierPartyId");
      String shipmentMethodTypeId = shipmentPreference.getString("shipmentMethodTypeId");
      String shippingInstructions = shipmentPreference.getString("shippingInstructions");

      String customerPoNumber = ((GenericValue) orderHeader.getRelated("OrderItem").iterator().next()).getString("correspondingPoId");
      Iterator orderAdjustmentIterator = order.getAdjustmentIterator();
%>

<%@ include file="orderinformation.jsp" %>


  <%pageContext.setAttribute("maySelectItems", "true");%>
  <form name="addOrderToCartForm" action="<ofbiz:url><%="/addordertocart/orderstatus?order_id=" + orderId%></ofbiz:url>" method="GET">
  <input type="HIDDEN" name="add_all" value="false">
  <input type="HIDDEN" name="order_id" value="<%=orderId%>">
  <table border="0" cellpadding="1" width="100%"><tr><td>
    <table border="0" cellspacing="0" cellpadding="2" width="100%"><tr>
      <td width="1"><div class="button" nowrap><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="buttontext">Back to Order History</a></div></td>
      <td align="right">
        <table border="0" cellspacing="0" cellpadding="0"><tr>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href='javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()' class="buttontext">Add All to Cart</a></div></td>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href='javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()' class="buttontext">Add Checked to Cart</a></div></td>
        </tr></table>
      </td>
    </tr></table>
  </td></tr></table>

<%@ include file="orderitems.jsp" %>

</ofbiz:if> <%-- Order --%>
<ofbiz:unless name="orderHeader">
<h3>The specified order was not found, please try again.</h3>
</ofbiz:unless>

  <table border="0" cellpadding="1" width="100%"><tr><td>
    <table border="0" cellspacing="0" cellpadding="2" width="100%"><tr>
      <td width="1"><div class="button" nowrap><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="buttontext">Back to Order History</a></div></td>
      <td align="right">
        <table border="0" cellspacing="0" cellpadding="0"><tr>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href='javascript:document.addOrderToCartForm.add_all.value="true";document.addOrderToCartForm.submit()' class="buttontext">Add All to Cart</a></div></td>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href='javascript:document.addOrderToCartForm.add_all.value="false";document.addOrderToCartForm.submit()' class="buttontext">Add Checked to Cart</a></div></td>
        </tr></table>
      </td>
    </tr></table>
  </td></tr></table>

<%@ include file="/includes/rightcolumn.jsp" %>
<%@ include file="/includes/footer.jsp" %>


 