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
<%-- FIXME add status and status history --%>
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
  Iterator orderItemIter = null;
  GenericValue shippingAddress = null;
  GenericValue billingAddress = null;
  int numberLines = 0;
  OrderReadHelper order = null;

  if(orderId != null && orderId.length() > 0) {
    orderHeader = helper.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

    if(orderHeader != null)
    {
      order = new OrderReadHelper(orderHeader);
      //XXX should we look in the OrderItemContactMech's here, too?
      Collection shippingLocationList = helper.findByAnd("OrderContactMech", UtilMisc.toMap(
              "orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null);
      if (shippingLocationList.size() > 0)
          shippingAddress = ((GenericValue) shippingLocationList.iterator().next()).getRelatedOne("ContactMech").getRelatedOne("PostalAddress");

      //XXX should we look in the OrderItemContactMech's here, too?
      Collection billingLocationList = helper.findByAnd("OrderContactMech", UtilMisc.toMap(
              "orderId", orderId, "contactMechPurposeTypeId", "BILLING_LOCATION"), null);
      if (billingLocationList.size() > 0) 
          billingAddress = ((GenericValue) billingLocationList.iterator().next()).getRelatedOne("ContactMech").getRelatedOne("PostalAddress");

      pageContext.setAttribute("orderItemList", orderHeader.getRelated("OrderItem"));
    } 
  }

  Iterator statusIterator = null;
%>

<%
  if(orderHeader == null) {
%>
<h3>The specified order was not found, please try again.</h3>
<% 
  } else { %>
<% //check ownership
  GenericValue orderRole = helper.findByPrimaryKey("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER"));
  if(orderRole == null) {
%>
<h3>The specified order does not correspond to your current customer account.  You may not view or edit it.</h3>
<%
  } else {
%>

<p class="head1">Order #<%=orderHeader.getString("orderId")%> Information</p>
  <table border="0" width="100%" cellpadding="1" cellspacing="2">
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td width='1%' nowrap><div class="tabletext"><b>&nbsp;Person Username</b></div></td>
      <td>
        <div class="tabletext">&nbsp;
          <%GenericValue userPerson = userLogin.getRelatedOne("Person");%>
          <%=UtilFormatOut.checkNull(userLogin.getString("userLoginId"))%>
          <%if(userPerson!=null){%>
            (<%=PartyHelper.getPersonName(userPerson)%>)
          <%}%>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td><div class="tabletext"><b>&nbsp;Order Status</b></div></td>
      <td><div class="tabletext">&nbsp;<%=order.getStatusString()%></div></td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td width='1%' nowrap><div class="tabletext"><b>&nbsp;Date Created</b></div></td>
      <td>
        <div class="tabletext">&nbsp;
          <%=UtilDateTime.toDateTimeString(orderHeader.getTimestamp("orderDate"))%>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td width='1%' nowrap><div class="tabletext"><b>&nbsp;Desired Ship Date</b></div></td>

    <%
      String dsDateString = null;
      String dsTimeString = null;
      String shipmentInstructions = null;
      String shipmentTypeId = null;
    %>
    <%
        Collection orderShipmentList = orderHeader.getRelated("OrderShipment");
        if (orderShipmentList.size() > 0) {
            GenericValue orderShipment = (GenericValue) orderShipmentList.iterator().next();
            GenericValue shipment = orderShipment.getRelatedOne("Shipment");
            shipmentInstructions = shipment.getString("handlingInstructions");
            shipmentTypeId = shipment.getString("shipmentTypeId");
            java.sql.Timestamp dsTimestamp = shipment.getTimestamp("estimatedShipDate");
            if(dsTimestamp  != null)
            {
              dsDateString = UtilDateTime.toDateString(dsTimestamp);
              dsTimeString = UtilDateTime.toTimeString(dsTimestamp);
            }
        }//else no OrderShipment
    %>
      <td><div class="tabletext">&nbsp;<%=UtilFormatOut.checkNull(dsDateString)%> <%=UtilFormatOut.checkNull(dsTimeString)%></div></td>
    </tr>
<%--    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td><div class="tabletext"><b>Splitting Preference</b></div></td>
      <td><div class="tabletext"><%=UtilFormatOut.checkNull(orderHeader.getSplittingPreference())%></div></td>
    </tr> --%>
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td width='1%' nowrap><div class="tabletext"><b>&nbsp;Handling Instructions</b></div></td>
      <td><div class="tabletext">&nbsp;<%=UtilFormatOut.checkNull(shipmentInstructions)%></div></td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td width='1%' nowrap><div class="tabletext"><b>&nbsp;Shipping Method</b></div></td>
      <td>
        <div class="tabletext">&nbsp;
          <%=UtilFormatOut.checkNull(shipmentTypeId)%>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td valign="top" width='1%' nowrap><div class="tabletext"><b>&nbsp;Shipping Address</b></div></td>
      <td valign="top">
        <div class="tabletext">
        <% if(shippingAddress != null) { %>
          <%=UtilFormatOut.checkNull(shippingAddress.getString("address1"))%><br>
          <% if(UtilValidate.isNotEmpty(shippingAddress.getString("address2"))) { %> <%=shippingAddress.getString("address2")%><br> <% } %>
          <%=UtilFormatOut.checkNull(shippingAddress.getString("city"))%><br>
          <% GenericValue countryGeo = shippingAddress.getRelatedOne("CountryGeo");
             if(countryGeo != null && UtilValidate.isNotEmpty(countryGeo.getString("name"))) { %> <%=countryGeo.getString("name")%> <% } %>
          <% GenericValue stateProvinceGeo = shippingAddress.getRelatedOne("StateProvinceGeo"); %>
          <%=stateProvinceGeo != null ? UtilFormatOut.checkNull(stateProvinceGeo.getString("name")) : ""%> &nbsp; <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
        <% } else { %>
          <font color="red">ERROR: Shipping Address record lookup failed.</font>
        <% } %>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td valign="top" width='1%' nowrap><div class="tabletext"><b>&nbsp;Payment Info</b></div></td>
      <td valign="top">
        <div class="tabletext">
          <% GenericValue billToPerson = order.getBillToPerson();
  
            String billToPersonName = PartyHelper.getPersonName(billToPerson);
            if(billToPersonName != null && billToPersonName.length() > 0) { %>
            <%=billToPersonName%><br>
          <% } %>

          <% if(billingAddress != null) { %>
            <%=UtilFormatOut.checkNull(billingAddress.getString("address1"))%><br>
            <% if(billingAddress.getString("address2") != null && billingAddress.getString("address2").length() != 0) { %> <%=billingAddress.getString("address2")%><br> <% } %>
            <%=UtilFormatOut.checkNull(billingAddress.getString("city"))%><br>
            <% GenericValue countryGeo = billingAddress.getRelatedOne("CountryGeo");
               if(countryGeo != null && UtilValidate.isNotEmpty(countryGeo.getString("name"))) { %> <%=countryGeo.getString("name")%> <% } %>
            <% GenericValue stateProvinceGeo = billingAddress.getRelatedOne("StateProvinceGeo"); %>
            <%=stateProvinceGeo != null ? UtilFormatOut.checkNull(stateProvinceGeo.getString("name")) : ""%> &nbsp; <%=UtilFormatOut.checkNull(billingAddress.getString("postalCode"))%><br>
          <% } else { %>
            No billing address<br>
          <% } %>

<%--          <% if(orderHeader.getPaymentType() != null && orderHeader.getPaymentType().length() > 0) { %>
            <%=orderHeader.getPaymentType()%>
          <% } %> --%>
<%--          <%
            if(orderHeader.getPaymentNumber() != null)
            {
              if(orderHeader.getPaymentType() != null && (orderHeader.getPaymentType().compareTo("STORE_CREDIT") == 0 || orderHeader.getPaymentType().compareTo("PURCHASE_ORDER") == 0))
              {
          %>
                <%=orderHeader.getPaymentNumber()%>
          <%
              }
              else if(orderHeader.getPaymentNumber().length() > 4)
              {
          %>
                <%=orderHeader.getPaymentNumber().substring(orderHeader.getPaymentNumber().length()-4)%>
          <%
              }
            }
          %>
          <% if(orderHeader.getPaymentExpireDate() != null && orderHeader.getPaymentExpireDate().length() > 0) { %>
            <%=orderHeader.getPaymentExpireDate()%>
          <% } %>--%>
        </div>
      </td>
    </tr>
  </table>
<%-- } else { %>
  <font color="red">ERROR: Sales Order lookup failed.</font> 
<% } --%>
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
  <table border="1" width="100%" cellpadding="2" cellspacing="0">
    <tr bgcolor="<%=bColorA2%>">
      <td width="15%"><div class="tabletext"><b>ID</b></div></td>
      <td width="50%"><div class="tabletext"><b>Description</b></div></td>
      <td width="10%" align="right"><div class="tabletext"><b>Quantity</b></div></td>
      <td width="10%" align="right"><div class="tabletext"><b>Unit Price</b></div></td>
      <td width="15%" align="right" nowrap><div class="tabletext"><b>Line Price</b></div></td>
      <td><div class="tabletext"><b>&nbsp;</b></div></td>
    </tr>
 <ofbiz:iterator name="orderItem" property="orderItemList">
    <%numberLines++;%>
    <%if(bColorB.equals(bColorB1)){bColorB=bColorB2;}else{bColorB=bColorB1;}%>
    <tr bgcolor="<%=bColorB%>">
      <%if(orderItem.getString("productId").equals("shoppingcart.CommentLine")){%>
        <td colspan="4">    
          <b><div class="tabletext"> &gt;&gt; <%=orderItem.getString("itemDescription")%></div></b>
        </td>
      <%}else{%>
        <td>
          <a href="<ofbiz:url><%="/details?product_id=" + orderItem.getString("productId")%></ofbiz:url>" class="buttontext"><%=orderItem.getString("productId")%></a>
        </td>
        <td>
          <a href="<ofbiz:url><%="/details?product_id=" + orderItem.getString("productId")%></ofbiz:url>" class="buttontext"><%=orderItem.getString("itemDescription")%></a>
        </td>
        <td align="right">
            <div nowrap>
              <%=UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%>
            </div>
        </td>
        <td align="right">
            <div nowrap>
              <%=UtilFormatOut.formatQuantity(orderItem.getDouble("unitPrice"))%>
            </div>
        </td>
        <td align="right" nowrap>
          <div class="tabletext"><%=UtilFormatOut.formatPrice(orderItem.getDouble("quantity").doubleValue()*orderItem.getDouble("unitPrice").doubleValue())%></div>
        </td>
      <%}%>
<%-- Removing add a certain quantity of a single item to cart, may still want later...
      <td align="right">
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td>
        <form method="POST" action="<ofbiz:url>/additemsfromorder/orderstatus</ofbiz:url>" vspace="0" hspace="0" name="the<%=orderItem.getTransLineId().intValue()%>form" style=margin:0;>
          <input type="hidden" name="<%="event"%>" value="<%="add_to_cart"%>">
          <input type="hidden" name="<%="order_id"%>" value="<%=orderId%>">
          <input type="hidden" name="<%=HttpRequestConstants.PRODUCT_ID%>" value="<%=orderItem.getString("productId")%>">
          <%
            if(orderItem.getString("itemDescription").indexOf("[[RandomLength:") >= 0)
            {
              String desc = orderItem.getString("itemDescription");
              int index1 = desc.indexOf("[[RandomLength:")+15;
              int index2 = desc.indexOf("]]",index1);
              String RLString = desc.substring(index1, index2);
          %>
            <input type="hidden" name="<%=HttpRequestConstants.RANDOM_LENGTH_STRING%>" value="<%=RLString%>">
          <%
            }
          %>
          <nobr>
            <%if(orderItem.getString("productId").compareTo("shoppingcart.CommentLine") != 0){%>
              <input type="text" size="5" name="<%=HttpRequestConstants.QUANTITY%>" value="<%=UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%>">
            <%}else{%>
              <input type="hidden" name="<%=HttpRequestConstants.QUANTITY%>" value="1">
            <%}%>
            <a href="javascript:document.the<%=orderItem.getTransLineId().intValue()%>form.submit()" class="buttontext">[Add to Cart]</a>
          </nobr>
        </form>
        </td>
        </tr>
        </table>
      </td>
--%>
      <td>
      <input name="item_id" value="<%=orderItem.getString("orderItemSeqId")%>" type="checkbox">
      </td>
    </tr>
  </ofbiz:iterator>
  <ofbiz:unless name="orderItemList" size="0">
  <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
  </ofbiz:unless>

    <% pageContext.setAttribute("orderAdjustmentIterator", order.getAdjustmentIterator()); %>
    <ofbiz:iterator name="orderAdjustmentObject" type="java.lang.Object" property="orderAdjustmentIterator">
    <%OrderReadHelper.Adjustment orderAdjustment = (OrderReadHelper.Adjustment) orderAdjustmentObject;%>
    <tr>
        <td align="right" colspan="4"><div class="tabletext"><b><%=orderAdjustment.getDescription()%></b></div></td>
        <td align="right" nowrap><div class="tabletext"><%= UtilFormatOut.formatPrice(orderAdjustment.getAmount())%></div></td>
    </tr>
    </ofbiz:iterator> 
    <tr>
        <td align="right" colspan="4"><div class="tabletext"><b>Total Due</b></div></td>
       <td align="right" nowrap>
      <div class="tabletext"><%= UtilFormatOut.formatPrice(order.getTotalPrice())%></div>
        </td>
    </tr>
<%-- } else { %>
  <tr><td><font color="red">ERROR: Sales Order lookup failed.</font></td></tr> --%>
</table>

<%} //end if customerId matches %>
<%} //end if orderHeader null %>

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


