<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<FONT FACE="verdana, helvetica, arial, geneva" SIZE="-2" COLOR="#330099"><CENTER>

<ofbiz:object name="cart" property="_SHOPPING_CART_" type="org.ofbiz.ecommerce.shoppingcart.ShoppingCart">
<b> size: <%= cart.size() %>
<FORM METHOD="POST" ACTION="<ofbiz:url>/modifycart</ofbiz:url>">
  <TABLE CELLSPACING="2" CELLPADDING="3" BORDER="0">
    <TR> 
      <TD NOWRAP><font color="#000000"><b><font face="verdana, helvetica, arial, geneva" size="-2">Product</font></b></font></TD>
      <TD NOWRAP><font color="#000000"><b><font face="verdana, helvetica, arial, geneva" size="-2">Quantity</font></b></font></TD>
      <TD NOWRAP><font color="#000000"><b><font face="verdana, helvetica, arial, geneva" size="-2">Price 
        per unit</font></b></font></TD>
      <TD NOWRAP><font color="#000000"><b><font face="verdana, helvetica, arial, geneva" size="-2">Total</font></b></font></TD>
      <TD NOWRAP><font color="#000000"><b><font face="verdana, helvetica, arial, geneva" size="-2">Delete</font></b></font></TD>
    </TR>

  <ofbiz:iterator name="item" type="org.ofbiz.ecommerce.shoppingcart.ShoppingCartItem">  
    <TR bgcolor="#CCCCCC"> 
      <TD><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000"><%= cart.getItemIndex(item) %> - <%= item.getProductId() %> : <%= item.getDescription() %></font></TD>
      <TD NOWRAP ALIGN="center"><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000"><input size="5" type="text" name="update_<%= cart.getItemIndex(item) %>" value="<ofbiz:format><%= item.getQuantity() %></ofbiz:format>"></font></TD>
      <TD NOWRAP ALIGN="right"><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000"><ofbiz:format type="c"><%= item.getBasePrice() %></ofbiz:format></font></TD>
      <TD NOWRAP ALIGN="right"><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000"><ofbiz:format type="c"><%= item.getTotalPrice() %></ofbiz:format></font></TD>
      <TD NOWRAP ALIGN="center"><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000"><input type="checkbox" name="delete_<%= cart.getItemIndex(item) %>" value="0"></font></TD>
    </TR>
  </ofbiz:iterator>

<!--
    <TR>
      <TD COLSPAN="3" ALIGN="right"><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000">Sales 
        tax:</font></TD>
      <TD ALIGN="right"><font face="verdana, helvetica, arial, geneva" size="-2" color="#000000">$0.00</font></TD>
    </TR> 
-->

    <TR> 
      <TD COLSPAN="3" ALIGN="right"> 
        <!-- <HR SIZE=1> -->
        <font face="verdana, helvetica, arial, geneva" size="-2" color="#000000">Total:</font>
  	  </TD>
      <TD ALIGN="right"> 
        <HR SIZE=1>
        <font face="verdana, helvetica, arial, geneva" size="-2" color="#000000"><ofbiz:format type="c"><%= cart.getGrandTotal() %></ofbiz:format></font>
	  </TD>
    </TR>
  </TABLE>
  <CENTER>
    <input type="checkbox" name="always_showcart" <%= cart.viewCartOnAdd() ? "checked" : "" %>>&nbsp;Always view cart after adding an item.
	<br><br>
    <input type="submit" value="Modify Cart">
  </CENTER>
</FORM>
</ofbiz:object>
</CENTER></FONT>
