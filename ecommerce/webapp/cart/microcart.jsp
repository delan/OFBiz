<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>

<%ShoppingCart microCart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>
<font color="white">
<%if(microCart != null && microCart.size() > 0){%>
  Cart has <b><%=microCart.size()%></b> items, <b>$<%=UtilFormatOut.formatPrice(microCart.getGrandTotal())%></b>
<%}else{%>
  Shopping Cart is <b>Empty</b>
<%}%>
</font>
<div class='lightbuttontextdisabled'>
  <a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="lightbuttontext">[View&nbsp;Cart]</a>
  <%if(microCart != null && microCart.size() > 0){%>
    <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
  <%}else{%>
    [Checkout]
  <%}%>
</div>