<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%ShoppingCart miniShoppingCart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>
<BR>
<TABLE border=0 width='100%' cellpadding='<%EntityField.run("layoutSettings", "boxBorderWidth", pageContext);%>' cellspacing=0 bgcolor='<%EntityField.run("layoutSettings", "boxBorderColor", pageContext);%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%EntityField.run("layoutSettings", "boxTopPadding", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "boxTopColor", pageContext);%>'>
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
      <table width='100%' border='0' cellpadding='<%EntityField.run("layoutSettings", "boxBottomPadding", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "boxBottomColor", pageContext);%>'>
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
        <a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="buttontext">[View&nbsp;Cart]&nbsp;</a><a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="buttontext">[Checkout]</a>
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
    </TD>
  </TR>
</TABLE>

