<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>

<META HTTP-EQUIV="expires" CONTENT="0">

<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>


<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<% ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request); 

  String bColorB1 = "#99FF99";
  String bColorB2 = "#CCFFCC";
  String bColorB = "";
  String tableHeaderColor = "#BBBBFF";
%>
<% pageContext.setAttribute("PageName", "shoppingcart"); %>

<%@ include file="includes/header.jsp" %>
<%@ include file="includes/columnsingle.jsp" %>

<%-- Main Heading --%>
<p class="head1">Shopping Cart</p>

<p>Please review the items in your cart before clicking Check Out. Click Delete to remove an item from the cart altogether. Change an amount in the Quantity column, then click Update Quantities and Grouping before clicking Check Out.</p>

<form method="POST" action="<ofbiz:url>/additem</ofbiz:url>" style=margin:0; name="quickaddform">
  Add Item to Cart:
  Product ID:<input type="text" size="25" name="product_id" value="">
  Quantity:<input type="text" size="5" name="quantity" value="1">
  <input type="submit" value="Add">
</form>

<script language="JavaScript">
<!--
  document.quickaddform.product_id.focus();
//-->
</script>

<form method="POST" action="<ofbiz:url>/addseparator</ofbiz:url>" style=margin:0; name="separatoraddform">
  Add Group Separator <input type="text" size="40" name="cart_new_comment_text"> <input type="submit" value="Add">
</form>
<form method="POST" action="<ofbiz:url>/modifycart" style=margin:0; name="shoppingCartForm">
<%-- ---------------------------------------------------------------------------------- --%>
<%-- SHOPPING CART --%>
<%  if(UtilValideate.isNotEmpty(shoppingCart)) {  %>
  <table border="0" cellpadding="1" width="100%">
    <%-- use this row to set the widths on all the columns --%>
    <tr>
      <td width="5%"><img src="images/shim.gif" width="2" height="1"></td>
      <td width="50%"><img src="images/shim.gif" width="2" height="1"></td>
      <td width="5%"><img src="images/shim.gif" width="2" height="1"></td>
      <td width="10%"><img src="images/shim.gif" width="2" height="1"></td>
      <td width="5%"><img src="images/shim.gif" width="2" height="1"></td>
      <td width="5%"><img src="images/shim.gif" width="2" height="1"></td>
      <td width="5%"><img src="images/shim.gif" width="2" height="1"></td>
    </tr>
    <tr bgcolor="#678475">
      <td colspan="8">
        <table border="0" cellspacing="0" cellpadding="2" width="100%"><tr>
          <td><div class="tabletext"><font color="white"><b>Shopping Cart</b></font></div></td>
          <td align="right">
            <table border="0" cellspacing="0" cellpadding="0"><tr>
              <td width="10"></td><td width="1"><div class="button" nowrap><a href="main.jsp" class="buttonlink">Continue Shopping</a></div></td>
              <%if(UtilValidate.isNotEmpty(shoppingCart){ %>
                <td width="10"></td><td width="1"><div class="button" nowrap><a href="javascript:document.shoppingCartForm.submit()" class="buttonlink">Update Quantities and Grouping</a></div></td>
                <td width="10"></td><td width="1"><div class="button" nowrap><a href="<ofbiz:url>/emptycart</ofbiz:url>" class="buttonlink">Empty Cart</a></div></td>
                <td width="10"></td><td width="1"><div class="button" nowrap><a href="<ofbiz:url>/allcheckoutoptions</ofbiz:url>" class="buttonlink">Checkout</a></div></td>
              <%}%>
            </tr></table>
          </td>
        </tr></table>
      </td>
    </tr>
    <tr bgcolor="<%=tableHeaderColor%>">
      <td><div class="tabletext"><b>Quantity</b><br><nobr>Old[New]</nobr></div></td>
      <td><div class="tabletext"><b>Item</b></div></td>
      <td><div class="tabletext"><b>Your Price</b></div></td>
      <td><div class="tabletext"><b>Subtotal</b></div></td>
      <td><div class="tabletext"><b>Delete</b></div></td>
      <td><div class="tabletext"><b>From</b></div></td>
      <td><div class="tabletext"><b>To</b></div></td>
    </tr>
    <%
      for(int i=0; i<shoppingCart.size(); i++)
      {
        LineItem shoppingCartLine = shoppingCart.getLineItem(i);
    %>
    <%-- with the background color alternating, we don't need these
    <tr>
      <td colspan="9" bgcolor="#899ABC"><img src="images/shim.gif" width="62" height="1"></td>
    </tr>
    --%>
    <%if(bColorB.compareTo(bColorB1)==0){bColorB=bColorB2;}else{bColorB=bColorB1;}%>
    <%if(shoppingCartLine.getSku().compareTo("shoppingcart.CommentLine") == 0){%>
      <tr bgcolor="<%=bColorB%>">
        <td colspan="4"> <b>>> <%=shoppingCartLine.getString("description")%></b> </td>
        <td valign="top"> <a href="shoppingcart.jsp?<%=HttpRequestConstants.EVENT%>=<%=EventConstants.DELETE_ITEM_FROM_SHOPPING_CART%>&<%=HttpRequestConstants.CART_LINE_NUMBER%>=<%=i%>" class="buttonlink">
          &nbsp;[Delete]</a>
      </td>
      <td>
      </td>
      <td align="center">
        <input type="radio" name="<%=HttpRequestConstants.CART_MOVE_TO_LINE%>" value="<%=i%>">
      </td>
      </tr>
    <%}else{%>
      <tr bgcolor="<%=bColorB%>">
        <td valign="middle" align="right">
          <%-- only create the input box if there is no RandomLengthString --%>
          <% if(shoppingCartLine.containsKey(HttpRequestConstants.RANDOM_LENGTH_STRING)) { %>
            <%=CommonUtil.formatQuantity(shoppingCartLine.getQuantity())%>
            <input type="hidden" name="new_quantity_<%=i%>" value="<%=CommonUtil.formatQuantity(shoppingCartLine.getQuantity())%>">
          <% } else { %>
            <nobr><%=CommonUtil.formatQuantity(shoppingCartLine.getQuantity())%>:
              <input type="text" name="new_quantity_<%=i%>" value="<%=CommonUtil.formatQuantity(shoppingCartLine.getQuantity())%>" size="4">
            </nobr>
          <% } %>
        </td>
        <td valign="middle">
          <a href="<%="catalog/details.jsp?" + HttpRequestConstants.PRODUCT_ID + "=" + shoppingCartLine.getSku()%>" class="buttonlink">
            <%=shoppingCartLine.getString("description")%></a>
        </td>
        <td align="right" valign="middle">
          <div class="tabletext" nowrap> <%=CommonUtil.formatPrice(shoppingCartLine.getPrice(customer))%></div>
        </td>
        <td align="right" valign="middle">
          <div class="tabletext" nowrap> <%=CommonUtil.formatPrice(shoppingCartLine.priceTotal(customer))%> </div>
        </td>
        <td valign="top"> <a href="shoppingcart.jsp?<%=HttpRequestConstants.EVENT%>=<%=EventConstants.DELETE_ITEM_FROM_SHOPPING_CART%>&<%=HttpRequestConstants.CART_LINE_NUMBER%>=<%=i%>" class="buttonlink">
          &nbsp;[Delete]</a>
        </td>
        <td align="center">
            <input type="checkbox" name="<%=HttpRequestConstants.CART_MOVE_FROM_LINE%>_<%=i%>" value="true">
        </td>
        <td>
        </td>
      </tr>
    <%}// end if comment line%>
    <%-- Get the RandomLengthString, if one exists... --%>
    <% if(shoppingCartLine.containsKey(HttpRequestConstants.RANDOM_LENGTH_STRING))
       {
         String RLString = shoppingCartLine.getString(HttpRequestConstants.RANDOM_LENGTH_STRING);
    %>
    <tr>
      <td></td>
      <td colspan="8" align="top">
        <% if(RLString != null) { %>
        [[RandomLength:<%=RLString%>]]
        <% } %>
      </td>
    </tr>
    <% } %>
    <% } //end for loop%>
    <%-- with the background color alternating, we don't need these
    <tr>
      <td colspan="9" bgcolor="#899ABC"><img src="images/shim.gif" width="62" height="1"></td>
    </tr>
    --%>
    <tr>
      <td colspan="3"></td>
      <td colspan="2" align="right">
        <div class="tabletext"><b>Total</b></div>
      </td>
      <td align="right" bgcolor="#99BBAA" colspan="2">
        <div class="tabletext" nowrap><b> <%=CommonUtil.formatPrice(shoppingCart.priceTotal(customer))%></b></div>
      </td>
    </tr>
    <% } else { %>
    <tr>
      <td colspan="9">
        <h3>Shopping Cart is Empty.</h3>
      </td>
    </tr>
    <% } %>
    <tr bgcolor="#678475">
      <td colspan="8" align="right">
        <table border="0" cellspacing="0" cellpadding="2" width="100%"><tr>
          <td align="right">
            <table border="0" cellspacing="0" cellpadding="0"><tr>
              <td width="10"></td><td width="1"><div class="button" nowrap><a href="main.jsp" class="buttonlink">Continue Shopping</a></div></td>
              <%if(shoppingCart!=null&&shoppingCart.size()>0){%>
                <td width="10"></td><td width="1"><div class="button" nowrap><a href="javascript:document.shoppingCartForm.submit()" class="buttonlink">Update Quantities and Grouping</a></div></td>
                <td width="10"></td><td width="1"><div class="button" nowrap><a href="shoppingcart.jsp?event=<%=EventConstants.EMPTY_SHOPPING_CART%>" class="buttonlink">Empty Cart</a></div></td>
                <td width="10"></td><td width="1"><div class="button" nowrap><a href="order/allcheckoutoptions.jsp" class="buttonlink">Checkout</a></div></td>
              <%}%>
            </tr></table>
          </td>
        </tr></table>
      </td>
    </tr>
  </table>
<%-- ---------------------------------------------------------------------------------- --%>
</form>

<%@ include file="/includes/footer.jsp" %>



