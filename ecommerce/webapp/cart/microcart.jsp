<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>

<%ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>
<font color="white">
<%if(shoppingCart != null && shoppingCart.size() > 0){%>
  Cart has <b><%=shoppingCart.size()%></b> items, <b>$<%=UtilFormatOut.formatQuantity(shoppingCart.getGrandTotal())%></b>
<%}else{%>
  Shopping Cart is <b>Empty</b>
<%}%>
</font>
<br>
<a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="lightbuttontext">[View&nbsp;Cart]</a>
<a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
