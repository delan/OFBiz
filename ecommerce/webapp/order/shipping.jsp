<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
 
<% pageContext.setAttribute("PageName", "shipping"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

    <p class="head1">Shipping</p>

<%
  String defaultShipMethod = CommonConstants.DEFAULT_SHIPPING_METHOD;
  Iterator shippingMethodIterator = ShippingMethodHelper.findAllIterator();
%>

<br>
<form method="post" name="enterShippingInfoForm" action="/commerce/order/selectaddress.jsp?event=<%=EventConstants.UPDATE_ORDER_SHIPPING_PREFERENCES%>">
  <table width="90%" cellpadding="2">
    <tr>
      <td colspan=2>
        <div class="head2"><b>How do you want this order shipped?</b></div>
      </td>
    </tr>
<% if(shippingMethodIterator != null) { %>
  <%
    boolean check = true;
    while(shippingMethodIterator.hasNext())
    {
      ShippingMethod shippingMethod = (ShippingMethod)shippingMethodIterator.next();
  %>
    <tr>
      <td width=1% valign="top" >
        <input
          <% if (shippingMethod.getMethodId().compareToIgnoreCase(defaultShipMethod) == 0 ) { %>
          <%-- if(check){ --%>
             CHECKED
            <%check=false;
          }%>
          type="radio" name="<%=HttpRequestConstants.SHIPPING_METHOD%>"
          value="<%=shippingMethod.getMethodId()%>"
        >
      </td>
      <td valign="top">
        <div class="tabletext"><%=shippingMethod.getString("description") + " [" + shippingMethod.getMethodId() + "]" %></div>
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
      <td>&nbsp;</td>
      <td>
        <textarea cols="35" rows="3" name="<%=HttpRequestConstants.SPECIAL_INSTRUCTIONS%>"></textarea>
      </td>
    </tr>
    <tr>
      <td align="left" colspan="1">
        <a href="<%=response.encodeURL(controlPath + "//commerce/shoppingcart.jsp" class="buttonlink">[Back]</a>
      </td>
      <td align="right">
        <%-- The submit button...  --%>
        <%-- <input type="image" value="[Continue]" border="0" src="/commerce/images/btn_continue.gif"> --%>
        <a href="<%=response.encodeURL(controlPath + "/javascript:document.enterShippingInfoForm.submit()" class="buttonlink">[Continue]</a>
      </td>
    </tr>
  </table>
</form>

<%@ include file="/includes/footer.jsp" %>





