<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*,
                 org.ofbiz.commonapp.order.shoppingcart.ShoppingCart,
                 org.ofbiz.commonapp.order.shoppingcart.ShoppingCartItem" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%ShoppingCart miniShoppingCart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
      <div class='boxhead'><b>Cart&nbsp;Summary</b></div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
<%if(miniShoppingCart != null && miniShoppingCart.size() > 0){%>
    <tr>
      <td colspan="3">
        <a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="buttontext">[Checkout]</a>
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
          <div><a href="<ofbiz:url><%="/product?product_id=" + miniShoppingCartItem.getProductId()%></ofbiz:url>" class="buttontext">
          <%=miniShoppingCartItem.getName()%></a></div>
        </td>
        <td align="right" valign="top"><div class="tabletext" nowrap>
          <%=UtilFormatOut.formatPrice(miniShoppingCartItem.getItemSubTotal())%></div>
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
        <a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="buttontext">[Checkout]</a>
      </td>
    </tr>
<%}else{%>
    <tr>
      <td nowrap colspan="3"><div class="tabletext">Shopping Cart is empty.</div></td>
    </tr>
<%}%>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

