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

<% pageContext.setAttribute("PageName", "confirmorder");%>

<%    String siteName = SiteDefs.SITE_NAME; %>
<%    GenericValue userLogin = null; %>

<html>
  <head>
  <base href="<%=session.getAttribute(SiteDefs.SERVER_ROOT_URL)%>">
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

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />
<% 
   final String ORDER_SECURITY_CODE = UtilProperties.getPropertyValue("ecommerce", "order.confirmation.securityCode");
   String securityCode = request.getParameter("security_code");
   if (UtilValidate.isNotEmpty(ORDER_SECURITY_CODE)) {
       if (ORDER_SECURITY_CODE.equals(securityCode)) {
           pageContext.setAttribute("validated", "true");
       } 
   } else {
       response.getWriter().print("ERROR: Order Information is NOT Secure!  Please ask System Administrator to set 'order.confirmation.securityCode' in ecommerce.properties file<p>"); 
   }
%>
    
<ofbiz:if name="validated">
<%
  String orderId = request.getParameter("order_id");
  GenericValue orderHeader = null;

  if(UtilValidate.isNotEmpty(orderId)) {
    orderHeader = helper.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
  }%>
<%if (orderHeader != null) pageContext.setAttribute("orderHeader", orderHeader);%>
<ofbiz:if name="orderHeader">
    <%
      OrderReadHelper order = new OrderReadHelper(orderHeader);
      Collection orderItemList = orderHeader.getRelated("OrderItem");
      Iterator orderAdjustmentIterator = order.getAdjustmentIterator();

      GenericValue shippingAddress = order.getShippingAddress(); 
      GenericValue billingAddress = order.getShippingAddress(); 
      GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount");

      GenericValue creditCardInfo = orderHeader.getRelatedOne("OrderPaymentPreference").getRelatedOne("CreditCardInfo");

      GenericValue shipmentPreference = orderHeader.getRelatedOne("OrderShipmentPreference");
      Boolean maySplit = shipmentPreference.getBoolean("maySplit");
      String carrierPartyId = shipmentPreference.getString("carrierPartyId");
      String shipmentMethodTypeId = shipmentPreference.getString("shipmentMethodTypeId");
      String shippingInstructions = shipmentPreference.getString("shippingInstructions");

      String customerPoNumber = ((GenericValue) orderHeader.getRelated("OrderItem").iterator().next()).getString("correspondingPoId");
%>
<h1><div class="head1">Order Confirmation</div></h1>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>
<div class="tabletext">Thank you for shopping at <%= siteName %> online. Don't forget to stop back for more great deals, contests, new store openings and specials.<br></div>

<%@ include file="orderinformation.jsp" %>
  
<br>
<%@ include file="orderitems.jsp" %>
  
  <a href="<ofbiz:url>/orderstatus?order_id=<%=orderId%></ofbiz:url>" class="buttonlinkbig">[View Order]</a>&nbsp;&nbsp;

</ofbiz:if> <%-- Order Header --%>
<ofbiz:unless name="orderHeader">
    <p> No order found. </p>
</ofbiz:unless>
</ofbiz:if>
<ofbiz:unless name="validated">
<font color="red">Security Error!  You do not have permission to view this page.</font><br>
</ofbiz:unless>
  <a href="<ofbiz:url>/main</ofbiz:url>" class="buttonlinkbig">[Continue Shopping]</a>
  </body>
  </html>