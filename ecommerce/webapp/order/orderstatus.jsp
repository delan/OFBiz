<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>

<%@ page import="org.ofbiz.ecommerce.order.OrderHelper" %>

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

  String orderId = request.getParameter("order_identifier");
  GenericValue orderHeader = null;
  Iterator orderItemIter = null;
  GenericValue shippingAddress = null;
  GenericValue billingAddress = null;
  int numberLines = 0;

  if(orderId != null && orderId.length() > 0) {
    orderHeader = helper.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

    if(orderHeader != null)
    {
      orderItemIter = orderHeader.getRelated("OrderItem").iterator();
      Collection shippingLocationList = helper.findByAnd("OrderItemContactMech", UtilMisc.toMap(
              "orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null);
      if (shippingLocationList.size() > 0)
          shippingAddress = ((GenericValue) shippingLocationList.iterator().next()).getRelatedOne("ContactMech").getRelatedOne("PostalAddress");

      Collection billingLocationList = helper.findByAnd("OrderItemContactMech", UtilMisc.toMap(
              "orderId", orderId, "contactMechPurposeTypeId", "BILLING_LOCATION"), null);
      if (billingLocationList.size() > 0) 
          billingAddress = ((GenericValue) billingLocationList.iterator().next()).getRelatedOne("ContactMech").getRelatedOne("PostalAddress");
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
  <table border="1" width="100%" cellpadding="1" cellspacing="0">
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td><div class="tabletext"><b>Person Username</b></div></td>
      <td>
        <div class="tabletext">
          <%GenericValue userPerson = userLogin.getRelatedOne("Person");%>
          <%=UtilFormatOut.checkNull(userLogin.getString("userLoginId"))%>
          <%if(userPerson!=null){%>
            (<%=OrderHelper.getPersonName(userPerson)%>)
          <%}%>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td><div class="tabletext"><b>Order Status</b></div></td>
      <td><div class="tabletext"><%=OrderHelper.getOrderStatusString(orderHeader)%></div></td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td><div class="tabletext"><b>Date Created</b></div></td>
      <td>
        <div class="tabletext">
          <%=UtilDateTime.toDateTimeString(orderHeader.getTimestamp("orderDate"))%>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td><div class="tabletext"><b>Desired Ship Date</b></div></td>

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
      <td><div class="tabletext"><%=UtilFormatOut.checkNull(dsDateString)%> <%=UtilFormatOut.checkNull(dsTimeString)%></div></td>
    </tr>
<%--    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td><div class="tabletext"><b>Splitting Preference</b></div></td>
      <td><div class="tabletext"><%=UtilFormatOut.checkNull(orderHeader.getSplittingPreference())%></div></td>
    </tr> --%>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td><div class="tabletext"><b>Handling Instructions</b></div></td>
      <td><div class="tabletext"><%=UtilFormatOut.checkNull(shipmentInstructions)%></div></td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td><div class="tabletext"><b>Shipping Method</b></div></td>
      <td>
        <div class="tabletext">
          <%=UtilFormatOut.checkNull(shipmentTypeId)%>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA2%>">
      <td valign="top"><div class="tabletext"><b>Shipping Address</b></div></td>
      <td valign="top">
        <div class="tabletext">
        <% if(shippingAddress != null) { %>
          <%=UtilFormatOut.checkNull(shippingAddress.getString("address1"))%><br>
          <% if(shippingAddress.getString("address2") != null && shippingAddress.getString("address2").length() != 0) { %> <%=shippingAddress.getString("address2")%><br> <% } %>
          <%=UtilFormatOut.checkNull(shippingAddress.getString("city"))%><br>
          <% if(shippingAddress.getRelatedOne("CountryGeo").getString("name") != null && shippingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0) { %> <%=shippingAddress.getRelatedOne("CountryGeo").getString("name")%> <% } %>
          <%=UtilFormatOut.checkNull(shippingAddress.getRelatedOne("StateProvinceGeo").getString("name"))%> &nbsp; <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
        <% } else { %>
          <font color="red">ERROR: Shipping Address record lookup failed.</font>
        <% } %>
        </div>
      </td>
    </tr>
    <tr valign="top" bgcolor="<%=bColorA1%>">
      <td valign="top"><div class="tabletext"><b>Payment Info</b></div></td>
      <td valign="top">
        <div class="tabletext">
          <% GenericValue billToPerson = OrderHelper.getBillToPerson(orderHeader);
  
            String billToPersonName = OrderHelper.getPersonName(billToPerson);
            if(billToPersonName != null && billToPersonName.length() > 0) { %>
            <%=billToPersonName%><br>
          <% } %>

          <% if(billingAddress != null) { %>
            <%=UtilFormatOut.checkNull(billingAddress.getString("address1"))%><br>
            <% if(billingAddress.getString("address2") != null && billingAddress.getString("address2").length() != 0) { %> <%=billingAddress.getString("address2")%><br> <% } %>
            <%=UtilFormatOut.checkNull(billingAddress.getString("city"))%><br>
            <% if(billingAddress.getRelatedOne("CountryGeo").getString("name") != null && billingAddress.getRelatedOne("CountryGeo").getString("name").length() != 0) { %> <%=billingAddress.getRelatedOne("CountryGeo").getString("name")%> <% } %>
            <%=UtilFormatOut.checkNull(billingAddress.getRelatedOne("StateProvinceGeo").getString("name"))%> &nbsp; <%=UtilFormatOut.checkNull(billingAddress.getString("postalCode"))%><br>
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
  <table border="0" cellpadding="1" width="100%"><tr><td>
    <table border="0" cellspacing="0" cellpadding="2" width="100%"><tr>
      <td width="1"><div class="button" nowrap><a href="<%=response.encodeURL(controlPath + "/orderhistory")%>" class="buttontext">Back to Order History</a></div></td>
      <td align="right">
        <table border="0" cellspacing="0" cellpadding="0"><tr>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href="<%=response.encodeURL(controlPath + "/orderstatus?event=add_order_to_cart&order_identifier=" + orderId + "&order_add_mode=order")%>" class="buttontext">Add All to Cart</a></div></td>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href="javascript:cartSubmitLines()" class="buttontext">Add Checked to Cart</a></div></td>
        </tr></table>
      </td>
    </tr></table>
  </td></tr></table>
  <table border="1" width="100%" cellpadding="2" cellspacing="0">
    <tr bgcolor="<%=bColorA2%>">
      <td width="15%"><div class="tabletext"><b>ID</b></div></td>
      <td width="60%"><div class="tabletext"><b>Description</b></div></td>
      <td width="10%" align="right"><div class="tabletext"><b>Quantity</b></div></td>
      <td width="15%" align="right" nowrap><div class="tabletext"><b>Line Price</b></div></td>
      <td><div class="tabletext"><b>&nbsp;</b></div></td>
    </tr>
<%if(orderItemIter != null && orderItemIter.hasNext()){%>
  <%while(orderItemIter.hasNext()){%>
    <%numberLines++;%>
    <%GenericValue orderItem = (GenericValue)orderItemIter.next();%>
    <%if(bColorB.equals(bColorB1)){bColorB=bColorB2;}else{bColorB=bColorB1;}%>
    <tr bgcolor="<%=bColorB%>">
      <%if(orderItem.getString("productId").equals("shoppingcart.CommentLine")){%>
        <td colspan="4">    
          <b><div class="tabletext"> &gt;&gt; <%=orderItem.getString("itemDescription")%></div></b>
        </td>
      <%}else{%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/details?product_id=" + orderItem.getString("productId"))%>" class="itemlink"><%=orderItem.getString("productId")%></a>
        </td>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/details?product_id=" + orderItem.getString("productId"))%>" class="itemlink"><%=orderItem.getString("itemDescription")%></a>
        </td>
        <td align="right">
            <div nowrap>
              <%=UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%>
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
        <form method="POST" action="orderstatus.jsp" vspace="0" hspace="0" name="the<%=orderItem.getTransLineId().intValue()%>form" style=margin:0;>
          <input type="hidden" name="<%="event"%>" value="<%="add_to_cart"%>">
          <input type="hidden" name="<%="order_identifier"%>" value="<%=orderId%>">
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
        <form style=margin:0;>
          <input type="checkbox" onchange="changeTransCheckbox(<%=orderItem.getString("orderItemSeqId")%>)">
        </form>
      </td>
    </tr>
  <%}//end while%>

  <script language="javascript">
  <!--
    var transLines = new Array;
    for(i=0;i<<%=numberLines%>;i++)
    {
      transLines[i] = false;
    }
    function changeTransCheckbox(lineNum)
    {
      transLines[lineNum] = !transLines[lineNum];
    }
    function eventSubmitLines()
    {
      var urlString = "orderstatus.jsp?<%="event"%>=<%="update_event" + "&order_identifier"%>=<%=orderId%>&order_add_move=line&event_type=Delivery";
      urlString = urlString + "&<%="event_id"%>=" + addToEventForm.<%="event_id"%>.value;
      var dosubmit=false;
      for(i=0;i<<%=numberLines%>;i++)
      {
        if(transLines[i])
        {
          dosubmit=true;
          urlString = urlString + "&<%="order_line_id"%>_" + i + "=true";
        }
      }
      if(dosubmit)
      {
        document.location=urlString;
      }
    }
    function cartSubmitLines()
    {
      var urlString = "orderstatus.jsp?<%="event"%>=<%="add_order_to_cart&order_identifier"%>=<%=orderId + "&order_add_move"%>=line";
      var dosubmit=false;
      for(i=0;i<<%=numberLines%>;i++)
      {
        if(transLines[i])
        {
          dosubmit=true;
          urlString = urlString + "&<%="order_line_id"%>_" + i + "=true";
        }
      }
      if(dosubmit)
      {
        document.location=urlString;
      }
    }
  //-->
  </script>

<%}else{%>
  <tr><td><font color="red">ERROR: Sales Order Lines lookup failed.</font></td></tr>
<%}%>

<% if(orderHeader != null) { %>
    <tr bgcolor="<%=bColorA1%>">
      <td align="right" colspan="3"><div class="tabletext"><b>Shipping & handling</b></div></td>
      <td align="right" nowrap>
        <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderHelper.getOrderShippingTotal(orderHeader))%></div>
      </td>
    </tr>
    <tr bgcolor="<%=bColorA2%>">
      <td align="right" colspan="3"><div class="tabletext"><b>Total tax</b></div></td>
       <td align="right" nowrap>
        <div class="tabletext">0.00</div>
      </td>
    </tr>
    <tr bgcolor="<%=bColorA1%>">
      <td align="right" colspan="3"><div class="tabletext"><b>Total due</b></div></td>
      <td align="right" nowrap>
        <div class="tabletext"><%=UtilFormatOut.formatPrice(OrderHelper.getOrderTotalPrice(orderHeader))%></div>
      </td>
    </tr>
<%-- } else { %>
  <tr><td><font color="red">ERROR: Sales Order lookup failed.</font></td></tr> --%>
<% } %>
</table>

<% } //end if customerId matches %>
<% } //end if orderHeader null %>

  <table border="0" cellpadding="1" width="100%"><tr><td>
    <table border="0" cellspacing="0" cellpadding="2" width="100%"><tr>
      <td width="1"><div class="button" nowrap><a href="<%=response.encodeURL(controlPath + "/orderhistory")%>" class="buttontext">Back to Order History</a></div></td>
      <td align="right">
        <table border="0" cellspacing="0" cellpadding="0"><tr>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href="<%=response.encodeURL(controlPath + "/orderstatus?event=add_order_to_cart&order_identifier=" + orderId + "&order_add_move=order")%>" class="buttontext">Add All to Cart</a></div></td>
          <td width="10"></td><td width="1"><div class="button" nowrap><a href="javascript:cartSubmitLines()" class="buttontext">Add Checked to Cart</a></div></td>
        </tr></table>
      </td>
    </tr></table>
  </td></tr></table>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>


