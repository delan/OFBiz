
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>

<%ShoppingCart miniShoppingCart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>

<table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
  <tr>
    <td bgcolor='#678475' align=center>
      <div class='boxhead'><b>Cart&nbsp;Summary</b></div>
    </td>
  </tr>

  <tr>
    <td bgcolor='white'>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
<%if(miniShoppingCart != null && miniShoppingCart.size() > 0){%>
    <tr>
      <td colspan="3">
        <a href="<%=response.encodeURL(controlPath + "/view/showcart")%>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<%=response.encodeURL(controlPath + "/shippingAddress")%>" class="buttontext">[Checkout]</a>
      </td>
    </tr>
    <tr>
      <td valign="bottom"><div class="tabletext"><b>#<b></div></td>
      <td valign="bottom"><div class="tabletext"><b>Item<b></div></td>
      <td valign="bottom"><div class="tabletext"><b>Subtotal<b></div></td>
    </tr>
    <%for(int i=0; i<miniShoppingCart.size(); i++){%>
      <%ShoppingCartItem miniShoppingCartItem = miniShoppingCart.findCartItem(i);%>
      <tr>
        <td valign="top"><div class="tabletext" nowrap>
          <%=UtilFormatOut.formatQuantity(miniShoppingCartItem.getQuantity())%></div>
        </td>
        <td valign="top">
          <div><a href="<%=response.encodeURL(controlPath + "/product?product_id=" + miniShoppingCartItem.getProductId())%>" class="buttontext">
          <%=miniShoppingCartItem.getDescription()%></a></div>
        </td>
        <td align="right" valign="top"><div class="tabletext" nowrap>
          <%=UtilFormatOut.formatPrice(miniShoppingCartItem.getTotalPrice())%></div>
        </td>
      </tr>
    <%}%>
    <tr>
      <td colspan="3" align="right">
        <div class="tabletext"><b>Total: $<%=UtilFormatOut.formatPrice(miniShoppingCart.getGrandTotal())%></b></div>
      </td>
    </tr>
    <tr>
      <td colspan="3">
        <a href="<%=response.encodeURL(controlPath + "/view/showcart")%>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<%=response.encodeURL(controlPath + "/shippingAddress")%>" class="buttontext">[Checkout]</a>
      </td>
    </tr>
<%}else{%>
    <tr>
      <td colspan="3"><div class="headertext">Shopping Cart is empty.</div></td>
    </tr>
<%}%>
</table>
    </td>
  </tr>
</table>

