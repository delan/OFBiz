<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.*" %>

<%@ page import="org.ofbiz.core.entity.*" %>


<% pageContext.setAttribute("PageName", "allcheckoutoptions"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<form method="post" name="checkoutInfoForm" action="checkout.jsp?<%="event"%>=<%=EventConstants.UPDATE_ORDER_ALL_OPTIONS%>" style=margin:0;>
<table width="100%" border="0">
<tr valign="top" align="left">
<td bgcolor="#FFCCCC">
<%
  GenericValue person = (GenericValue)session.getAttribute(HttpSessionConstants.LOGIN_PERSON);
  GenericValue customer = (Customer)session.getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

  boolean hasUserPermission = Security.hasPermission(Security.USER_ADMIN, session);
  if(hasUserPermission)
  {
    GenericValue tempPerson = (GenericValue)session.getAttribute(HttpSessionConstants.ACTING_AS_PERSON);
    if(tempPerson != null) person = tempPerson;
    GenericValue tempCustomer = (Customer)session.getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
    if(tempCustomer != null) customer = tempCustomer;
  }

  GenericHelper helper = (GenericHelper) session.getAttribute("helper");
  String defaultShipMethod = CommonConstants.DEFAULT_SHIPPING_METHOD;
  Iterator shippingMethodIterator = helper.findAllCache("ShipmentMethodType", null).iterator();
%>

  <div class="head2" nowrap><b>How shall WE ship it?</b></div>
  <table width="100%" cellpadding="0" border="0" cellpadding="0" cellspacing="0">
<% if(shippingMethodIterator != null) { %>
  <%
    boolean check = true;
    while(shippingMethodIterator.hasNext())
    {
      GenericValue shippingMethod = (GenericValue)shippingMethodIterator.next();
  %>
    <tr>
      <td width=1% valign="top" >
        <input
          <% if (shippingMethod.getString("shipmentMethodTypeId").equalsIgnoreCase(defaultShipMethod)) { %>
          <%-- if(check){ --%>
             CHECKED
            <%check=false;
          }%>
          type="radio" name="<%=HttpRequestConstants.SHIPPING_METHOD%>"
          value="<%=shippingMethod.getString("shipmentMethodTypeId")%>"
        >
      </td>
      <td valign="top">
        <div class="tabletext"><%=shippingMethod.getString("description") + " [" + shippingMethod.getString("shipmentMethodTypeId") + "]" %></div>
      </td>
    </tr>
  <%}%>
<% } else { %>
    <tr>
      <td width="1%" valign="top">
        <input CHECKED type="radio" name="<%=HttpRequestConstants.SHIPPING_METHOD%>" value="Default">
      </td>
      <td valign="top">
        <div class="tabletext">Use Default: No other shipping methods available.</div>
      </td>
    </tr>
<% } // end if shippingMethodIterator %>
    <tr>
      <td colspan="2">
      Enter account code here, or ship freight collect: <input size="25" type="text" name="<%=HttpRequestConstants.SHIPPING_ACCOUNT%>" value="">
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <hr size="1">
      </td>
    </tr>
    <tr>
      <td colspan=2>
        <div class="head2"><b>Ship all at once, or 'as available'?</b></div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input CHECKED type="radio" name="<%=HttpRequestConstants.SPLITTING_PREFERENCE_CODE%>" value="<%=HttpRequestConstants.SPLITTING_PREFERENCE_NO_SPLIT_CODE%>">
      </td>
      <td valign="top">
        <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input type="radio" name="<%=HttpRequestConstants.SPLITTING_PREFERENCE_CODE%>" value="<%=HttpRequestConstants.SPLITTING_PREFERENCE_SPLIT_CODE%>">
      </td>
      <td valign="top">
        <div class="tabletext">Please ship items I ordered as they become available (you may incur additional shipping charges).</div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <hr size="1">
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div class="head2"><b>Special Instructions</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <textarea cols="30" rows="5" name="<%=HttpRequestConstants.SPECIAL_INSTRUCTIONS%>"></textarea>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <hr size="1">
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div class="head2"><b>Email Addresses</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div class="tabletext">Your order will be sent to the following email addresses:</div>
        <div class="tabletext">
          <b>
          <%=UtilFormatOut.checkNull(person.getEmail()) FIXME%>,
          <%=UtilFormatOut.checkNull(customer.getEmail()) FIXME%>,
          <%=UtilFormatOut.checkNull(customer.getOrderEmail()) FIXME%>
          </b>
        </div>
        <div class="tabletext">Your may update these in your <a href="<%=response.encodeURL(controlPath + "/../user/editprofile.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp" class="buttonlink">profile</a>.</div>
        <br>
        <div class="tabletext">You may add other comma separated email addresses here that will be used only for the current order:</div>
        <input type="text" size="38" name="<%=HttpRequestConstants.ORDER_ADDITIONAL_EMAILS%>" value="">
      </td>
    </tr>
  </table>

</td>
<td bgcolor="#888888" width="1">
</td>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<td bgcolor="#FFFFCC">
<%
  //Customer's shipping addresses list  
  //Class/method not written yet FIXME
  Iterator custShipAddressesIterator = PartyHelper.getPostalAddresses(customer);
%>

<div class="head2" nowrap><b>Where shall WE ship it?</b></div>

  <% if (custShipAddressesIterator != null) { %>
  <a href="<%=response.encodeURL(controlPath + "/../user/profilenewaddress.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp" class="buttonlink">[Add New Address]</a>
  <table width="90%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="100%" height="1" bgcolor="888888"></td>
    </tr>
  <%
    while(custShipAddressesIterator.hasNext())
    {
      GenericValue shippingAddress = (GenericValue) custShipAddressesIterator.next();
  %>
    <tr>
      <td align="left" valign="top" width="100%" nowrap>
        <div class="tabletext">
  <%=UtilFormatOut.checkNull(shippingAddress.getString("address1"))%><br>
  <% String address2 = shippingAddress.getString("address2");
    if(address2 != null && address2.length() != 0) { %> <%=address2%><br> <% } %>
  <%=UtilFormatOut.checkNull(shippingAddress.getString("city"))%><br>
  <% String county = shippingAddress.getString("county");
    if(country != null && country.length() != 0) { %> <%=country%> <% } %>
  <%=UtilFormatOut.checkNull(shippingAddress.getString("state"))%> &nbsp; <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
    <%if(!shippingAddress.get("contactMechId").equals(customer.getAddressId().intValue()) { %>
          <input type="radio" name="<%=HttpRequestConstants.ADDRESS_KEY%>" value="<%=shippingAddress.getAddressId()%>">Use
          <a href="<%=response.encodeURL(controlPath + "/../user/profileeditaddress.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingAddress.getAddressId()%>" class="buttonlink">[Update]</a>
          <a href="<%=response.encodeURL(controlPath + "/allcheckoutoptions.jsp?<%="event"%>=<%=EventConstants.DELETE_SHIPPING_ADDRESS%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingAddress.getAddressId()%>" class="buttonlink">[Delete]</a>
    <% } else { %>
          <input CHECKED type="radio" name="<%=HttpRequestConstants.ADDRESS_KEY%>" value="<%=shippingAddress.getAddressId()%>">Use
          <a href="<%=response.encodeURL(controlPath + "/../user/profileeditaddress.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingAddress.getAddressId()%>" class="buttonlink">[Update]</a>
    <% } %>
        </div>
      </td>
    </tr>
    <tr>
      <td width="100%" height="1" bgcolor="888888"></td>
    </tr>
  <%}%>
  </table>
  <%
  }
  %>
<a href="<%=response.encodeURL(controlPath + "/../user/profilenewaddress.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp" class="buttonlink">[Add New Address]</a>

</td>
<td bgcolor="#888888" width="1">
</td>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<td bgcolor="#CCFFCC">

<%
  Iterator paymentIterator = null;
  if(CommonConstants.CREDITCARD_MANAGE_INFO)
  {
    Collection paymentCollection = CustomerPaymentHelper.findByCustomerId(customer.getCustomerId());
    if(paymentCollection != null)	paymentIterator = paymentCollection.iterator();
  }

  boolean isLynx = UtilFormatOut.isLynx(request);
%>

<div class="head2" nowrap><b>How shall YOU pay?</b></div>
    <div class="tabletext">To pay with store credit, enter your Purchase Order (PO) number here:</div>
    <input type="text" name="<%=HttpRequestConstants.CUSTOMER_PO_NUMBER%>" size="20">
    <nobr><input CHECKED type="radio" name="<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>" value="store_credit_purchase_order">
    Pay with Store Credit<nobr>
<br>
<a href="<%=response.encodeURL(controlPath + "/../user/profilenewcc.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp" class="buttonlink">[Add Credit Card]</a>

<% if(paymentIterator != null) { %>
<table width="90%" cellpadding="1" cellspacing="0" border="0">
  <tr>
    <td colspan="3"><hr size="1"></td>
  </tr>
<%
    while(paymentIterator.hasNext())
    {
      CustomerPayment customerPayment = (CustomerPayment)paymentIterator.next();
%>
  <tr>
    <td width="50%">
      <div class="tabletext">
        <%=customerPayment.getCardType()%>
        <%if(customerPayment.getCardNumber() != null && customerPayment.getCardNumber().length() > 4) {%>
          <%=customerPayment.getCardNumber().substring(customerPayment.getCardNumber().length()-4)%>
        <% } %>
        <%=customerPayment.getExpireDate()%>
      </div>
      <input type="radio" name="<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>" value="<%=customerPayment.getPaymentId()%>">Use
      <a href="<%=response.encodeURL(controlPath + "/../user/profileeditcc.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp&<%=HttpRequestConstants.CUSTOMER_PAYMENT_ID%>=<%=customerPayment.getPaymentId()%>" class="buttonlink">[Update]</a>
    </td>
  </tr>
  <tr>
    <td colspan="3" height="1" bgcolor="888888"></td>
  </tr>
<%  } %>
</table>
<% } else { %>
<h3>There are no credit cards on file.</h3>
<% } //end if paymentIterator %>

<%-- the back and add new credit card buttons --%>
<a href="<%=response.encodeURL(controlPath + "/../user/profilenewcc.jsp?<%=HttpRequestConstants.DONE_PAGE%>=../order/allcheckoutoptions.jsp" class="buttonlink">[Add Credit Card]</a>

</td>
</tr>
</table>
</form>

<table width="100%">
<tr valign="top">
<td align="left">
&nbsp;<a href="<%=response.encodeURL(controlPath + "/../shoppingcart.jsp" class="buttonlinkbig">[Back to Shopping Cart]</a>
</td>
<td align="right">
<a href="<%=response.encodeURL(controlPath + "/javascript:document.checkoutInfoForm.submit()" class="buttonlinkbig">[Continue to Final Order Review]</a>
</td>
</tr>
</table>
<%@ include file="/includes/footer.jsp" %>





