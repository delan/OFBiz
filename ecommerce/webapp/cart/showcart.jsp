<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<% pageContext.setAttribute("PageName", "showcart"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<BR>
<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor="#678475">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping Cart</div>
          </td>
          <td valign="middle" align="right">
            <a href="javascript:document.cartform.submit()" class="lightbuttontext">[Update Cart]</a>
            <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td bgcolor='white' colspan='2'>

<ofbiz:if name="_SHOPPING_CART_">
  <ofbiz:object name="cart" property="_SHOPPING_CART_" type="org.ofbiz.ecommerce.shoppingcart.ShoppingCart">  
    <FORM METHOD="POST" ACTION="<ofbiz:url>/modifycart</ofbiz:url>" name='cartform'>
      <TABLE width='100%' CELLSPACING="2" CELLPADDING="3" BORDER="0">
        <TR> 
          <TD NOWRAP><div class='tabletext'><b>Product</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Quantity</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Price per unit</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Total</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Delete</b></div></TD>
        </TR>

        <ofbiz:iterator name="item" type="org.ofbiz.ecommerce.shoppingcart.ShoppingCartItem">  
          <TR bgcolor="#CCCCCC"> 
            <TD><div class='tabletext'><b><%= cart.getItemIndex(item)%></b> - <%= item.getProductId()%> - <%= item.getName()%> : <%= item.getDescription()%></div></TD>
            <TD NOWRAP ALIGN="center"><div class='tabletext'><input size="5" type="text" name="update_<%= cart.getItemIndex(item) %>" value="<ofbiz:format><%= item.getQuantity() %></ofbiz:format>"></div></TD>
            <TD NOWRAP ALIGN="right"><div class='tabletext'><ofbiz:format type="c"><%= item.getBasePrice() %></ofbiz:format></div></TD>
            <TD NOWRAP ALIGN="right"><div class='tabletext'><ofbiz:format type="c"><%= item.getTotalPrice() %></ofbiz:format></div></TD>
            <TD NOWRAP ALIGN="center"><div class='tabletext'><input type="checkbox" name="delete_<%= cart.getItemIndex(item) %>" value="0"></div></TD>
          </TR>
        </ofbiz:iterator>

<!--
        <TR>
          <TD COLSPAN="3" ALIGN="right"><div class='tabletext'>Sales tax:</div></TD>
          <TD ALIGN="right"><div class='tabletext'>$0.00</div></TD>
        </TR> 
-->

        <TR> 
          <TD COLSPAN="3" ALIGN="right"> 
            <!-- <HR SIZE=1> -->
            <div class='tabletext'>Cart Total:</div>
          </TD>
          <TD ALIGN="right"> 
            <HR SIZE=1>
            <div class='tabletext'><ofbiz:format type="c"><%= cart.getGrandTotal() %></ofbiz:format></div>
          </TD>
        </TR>
      </TABLE>
<%--
      <CENTER>
        <input type="checkbox" name="always_showcart" <%= cart.viewCartOnAdd() ? "checked" : "" %>>&nbsp;Always view cart after adding an item.
    	<br><br>
        <input type="submit" value="Update Cart">
      </CENTER>
--%>
    </FORM>
  </ofbiz:object>
</ofbiz:if>
<ofbiz:unless name="_SHOPPING_CART_">
  <div class='head2'>Your shopping cart is empty.</div>
</ofbiz:unless>
    </td>
  </tr>
  <tr>
    <td bgcolor="#678475">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="middle" align="left">
            &nbsp;
          </td>
          <td valign="middle" align="right">
            <a href="javascript:document.cartform.submit()" class="lightbuttontext">[Update Cart]</a>
            <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
