<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*" %>

<%ShoppingCart microCart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART);%>
<div class='insideHeaderText'>
<%if(microCart != null && microCart.size() > 0){%>
  Cart has <b><%=(int)microCart.getTotalQuantity()%></b> items, <b><ofbiz:format type="currency"><%=UtilFormatOut.formatPrice(microCart.getGrandTotal())%></ofbiz:format></b>
<%}else{%>
  Shopping Cart is <b>Empty</b>
<%}%>
  &nbsp;&nbsp;
</div>
<div class='insideHeaderDisabled'>
  <a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="insideHeaderLink">[View&nbsp;Cart]</a>
  <%if(microCart != null && microCart.size() > 0){%>
    <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="insideHeaderLink">[Checkout]</a>
  <%}else{%>
    [Checkout]
  <%}%>
  &nbsp;&nbsp;
</div>
