<%
/**
 *  Title: Confirm Order Page
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
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="javax.servlet.jsp.tagext.BodyContent" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>
<%@ page import="org.ofbiz.commonapp.order.order.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%
  String orderId = request.getParameter("order_id");
  GenericValue orderHeader = helper.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
  GenericValue shipmentPreference = null;
  String shippingMethod =  null; 
  GenericValue paymentPreference = null;
  OrderReadHelper order = null;
  GenericValue shippingLocation = null;
  if (orderHeader != null) {
      pageContext.setAttribute("orderHeader", orderHeader);   

      order = new OrderReadHelper(orderHeader);
      shippingLocation = order.getShippingAddress();
      shipmentPreference = orderHeader.getRelatedOne("OrderShipmentPreference");
      shippingMethod = shipmentPreference.getString("carrierPartyId") + " " 
            + shipmentPreference.getRelatedOne("CarrierShipmentMethod").getRelatedOne("ShipmentMethodType").getString("description");
      paymentPreference = orderHeader.getRelatedOne("OrderPaymentPreference");

      pageContext.setAttribute("orderItems", orderHeader.getRelated("OrderItem"));
     
    /*  Address shippingAddress = null;
      if(orderHeader!=null) shippingAddress = AddressHelper.findByPrimaryKey(orderHeader.getShippingAddr());

      Integer paymentAddressId = null;
      if(orderHeader!=null) paymentAddressId = orderHeader.getPaymentAddress();
      Address billingAddress = null;
      if(paymentAddressId != null && paymentAddressId.intValue() > 0)
      {
        billingAddress = AddressHelper.findByPrimaryKey(paymentAddressId);
      }
*/

  }


  String siteName = SiteDefs.SITE_NAME;
%>

<% pageContext.setAttribute("PageName", "confirmorder");%>

<html>
  <head>
  <title>Confirmation Page</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  
  <style>
      p {
          MARGIN: 0.2em;
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 10pt;
      }
      .head1 {
          FONT-FAMILY: Helvetica,sans-serif;
          MARGIN: 0;
          FONT-SIZE: 15pt;
          FONT-WEIGHT: bold;
          COLOR: #3A4C37;
      }
      .head2 {
          FONT-FAMILY: Helvetica,sans-serif;
          MARGIN: 0;
          FONT-SIZE: 12pt;
          FONT-WEIGHT: bold;
          COLOR: #000000;
      }
      .tabletext {
          FONT-FAMILY: Verdana,sans-serif;
          FONT-SIZE: 9pt;
      }
      .commentary {
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 8pt;
          FONT-WEIGHT: bold;
      }
      ul {
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 9pt;
          }
      ol {
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 9pt;
      }
  
  A.buttonlinkbig {
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 14pt;
          FONT-WEIGHT: bold;
      text-decoration: none;
          color: blue;
          }
          A.buttonlinkbig:hover { color: red; }
  
      A.headerlink {
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 8pt;
          FONT-WEIGHT: bold;
      text-decoration: none;
          color: blue;
          }
          A.headerlink:hover { color: red; }
  
          .headertext {
          FONT-FAMILY: Helvetica,sans-serif;
          FONT-SIZE: 8pt;
          FONT-WEIGHT: bold;
                  text-decoration: none;
                  color: #567856;
      }
  </style>
  </head>
  <body bgcolor="white">
  <a name="top"></a>

<ofbiz:if name="orderHeader">
  <table width="90%" cellpadding="2" cellspacing="0" border="0">
      <tr>
        <td colspan="2">
      <h1><div class="head1">Order Confirmation</div></h1>
      <p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>
      <div class="tabletext">Thank you for shopping at <%= siteName %> online. Don't forget to stop back for more great deals, contests, new store openings and specials.<br></div>
      <br>
        </td>
      </tr>
      <tr>  
        <td align="left" colspan="2"><div class="head2"><b>Order &#35;<%= orderHeader.getString("orderId")%></b></div></td>
      </tr>
      <tr>
          <td width="50%" align="left" valign="top"><div class="tabletext"><b>Will be shipped to:</b></div></td>
          <td width="50%" align="left" valign="top"><div class="tabletext"><b>Shipping Instructions:</b></div></td>
      </tr>
      <tr>
          <td align="left" valign="top"><div class="tabletext">
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("toName"), "<b>To:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("address1"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("address2"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("city"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(shippingLocation.getString("postalCode"))%><br>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("countryGeoId"), "", "<br>")%>
        <br>
        <b>Will be billed to:</b> <%=paymentPreference.getRelatedOne("PaymentMethodType").getString("description")%><br>
        <%if ("CREDIT_CARD".equals(paymentPreference.getString("paymentMethodTypeId"))) {%>
            <%GenericValue creditCardInfo = paymentPreference.getRelatedOne("CreditCardInfo");%>
            <%=creditCardInfo.getString("nameOnCard")%><br>
            <%=ContactHelper.formatCreditCard(creditCardInfo)%>
        <%} else {%>
            <%--FIXME: other payment types--%>
            <%=UtilFormatOut.checkNull(paymentPreference.getString("paymentInfoId"))%>
        <%}%>
        </div></td>
        <td align="left" valign="top">
        <div class="tabletext">
        <%=shipmentPreference.getString("shippingInstructions")%></div><br>
        <div class="tabletext"><b>Shipping Method:</b><br>
          <%= shipmentPreference.get("carrierPartyId")%> <%=shipmentPreference.getRelatedOne("ShipmentMethodType").get("description")%>
        </div><br>
      </td>
  </tr>
  <tr>
      <td colspan="2" valign="top">
  
  <table border="1" width="100%" cellpadding="4" cellspacing="0">
      <tr bgcolor="#99BBAA">
        <td bgcolor="#99BBAA" width="15%" valign="bottom"><div class="tabletext"><b>ID</b></div></td>
        <td bgcolor="#99BBAA" width="55%" valign="bottom"><div class="tabletext"><b>Description</b></div></td>
        <td bgcolor="#99BBAA" width="5%" valign="bottom" align="center"><div class="tabletext"><b>Quantity</b></div></td>
        <td bgcolor="#99BBAA" width="15%" valign="bottom" align="center"><div class="tabletext"><b>Unit Price</b></div></td>
        <td bgcolor="#99BBAA" width="15%" valign="bottom" align="center"><div class="tabletext"><b>Total</b></div></td>
      </tr>
  
  
  <ofbiz:iterator name="orderItem" property="orderItems"> 
   <%pageContext.setAttribute("orderItemDescription", orderItem.getString("itemDescription"));%>
   <ofbiz:if name="orderItemDescription" value="shoppingcart.CommentLine">
    <tr>
        <td valign="top" align="left" colspan="5">
          <div class="tabletext"><b> >><%= orderItem.getString("itemDescription")%></b></div>
        </td>
  
    </tr>
   </ofbiz:if>
   <ofbiz:unless name="orderItemDescription" value="shoppingcart.CommentLine">
    <tr>
        <td valign="top" align="left">
          <div class="tabletext"><%= orderItem.getString("productId")%></div>
        </td>
        <td valign="top" align="left">
          <div class="tabletext"><%= orderItem.getString("itemDescription")%></div>
        </td>
        <td align="center" valign="top">
          <div class="tabletext"><%= UtilFormatOut.formatQuantity(orderItem.getDouble("quantity"))%></div>
        </td>
        <td align="right" valign="top">
          <div class="tabletext"><%= UtilFormatOut.formatPrice(orderItem.getDouble("unitPrice"))%>
          <%-- ofbiz:iter name="orderItemAdjustment" property="orderItemAdjustmentIterator">
            <br><table border='0'><tr><td><%=orderItemAdjustment.get("
          --%>
          </div>
        </td>
        <td align="right" valign="top" nowrap>
          <div class="tabletext"><%= UtilFormatOut.formatPrice(orderItem.getDouble("unitPrice").doubleValue()*orderItem.getDouble("quantity").doubleValue())%></div>
        </td>
  
    </tr>
   </ofbiz:unless>
  </ofbiz:iterator>
    <tr>
        <td colspan="2" rowspan="100" valign="middle" align="center" bgcolor="#99BBAA"><div class="commentary">Print this page for your records.</div></td>
    </tr>
    <% pageContext.setAttribute("orderAdjustmentIterator", order.getAdjustmentIterator()); %>
    <ofbiz:iterator name="orderAdjustmentObject" type="java.lang.Object" property="orderAdjustmentIterator">
    <%OrderReadHelper.Adjustment orderAdjustment = (OrderReadHelper.Adjustment) orderAdjustmentObject;%>
    <tr>
        <td align="right" colspan="2"><div class="tabletext"><b><%=orderAdjustment.getDescription()%></b></div></td>
        <td align="right" nowrap><div class="tabletext"><%= UtilFormatOut.formatPrice(orderAdjustment.getAmount())%></div></td>
  
    </tr>
    </ofbiz:iterator>
    <tr>
        <td align="right" colspan="2"><div class="tabletext"><b>Total Due</b></div></td>
       <td align="right" nowrap>
      <div class="tabletext"><%= UtilFormatOut.formatPrice(order.getTotalPrice())%></div>
        </td>
    </tr>
  </table>
  
       </td>
      </tr>
     </table>
  <a href="<ofbiz:url>/orderstatus?order_id=<%=orderId%></ofbiz:url>" class="buttonlinkbig">[View Order]</a>&nbsp;&nbsp;
</ofbiz:if>
<ofbiz:unless name="orderHeader">
    <p> No order found. </p>
</ofbiz:unless>
  <a href="<ofbiz:url>/main</ofbiz:url>" class="buttonlinkbig">[Continue Shopping]</a>
  </body>
  </html>