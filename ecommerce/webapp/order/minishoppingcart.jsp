<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved.     --%>

<%@ page import="com.relmsoft.commerce.shoppingcart.*" %>
<%@ page import="com.relmsoft.commerce.*" %>

<%
  Customer cartCustomer = (Customer)session.getAttribute(HttpSessionConstants.LOGIN_CUSTOMER);

  boolean hasUserPermission = Security.hasPermission(Security.USER_ADMIN, session);
  if(hasUserPermission)
  {
    Customer tempCustomer = (Customer)session.getAttribute(HttpSessionConstants.ACTING_AS_CUSTOMER);
    if(tempCustomer != null) cartCustomer = tempCustomer;
  }
%>
<% Cart shoppingCart = (Cart)session.getAttribute(HttpSessionConstants.SHOPPING_CART);
   if (CommonUtil.isLynx(request)) { %>
   Shopping Cart: <a href="/commerce/shoppingcart.jsp" class="headerlink">[View/Checkout]</a>
   <% if (shoppingCart != null && shoppingCart.size() > 0 ) { %>
   <%=shoppingCart.size()%> items in cart
   <% } else { %>
   Shopping Cart is Empty
   <% } %>
   <br>
<% } else { %>

  <table border="0" cellpadding="0" width="150">
    <%-- use this row to set the widths on all the columns --%>
    <tr>
      <td width="5%"><img src="/commerce/images/shim.gif" width="2" height="1"></td>
      <td width="50%"><img src="/commerce/images/shim.gif" width="2" height="1"></td>
      <td width="10%"><img src="/commerce/images/shim.gif" width="2" height="1"></td>
    </tr>

    <tr>
      <td colspan="3" class="headeraccenttable">
        <div class="headeraccenttext"><b>&nbsp;Cart Summary</b></div>
      </td>
    </tr>

    <tr>
      <td colspan="3">
          <a href="/commerce/shoppingcart.jsp" class="headerlink">View/Checkout</a>
        </td>
    </tr>
<% if(shoppingCart != null && shoppingCart.size() > 0) { %>
<%-- ---------------------------------------------------------------------------------- --%>
<%-- SHOPPING CART --%>
    <tr>
      <td valign="bottom"><div class="headertext">#</div></td>
      <td valign="bottom"><div class="headertext">Item</div></td>
      <td valign="bottom"><div class="headertext">Subtotal</div></td>
    </tr>
    <tr>
      <td colspan="3"  class="headeraccenttable"></td>
    </tr>

    <%-- iterate through all shopping cart lines --%>

<%  for(int i=0; i<shoppingCart.size(); i++)
    {
      LineItem shoppingCartLine = shoppingCart.getLineItem(i);
%>
    <tr>
      <td colspan="3"  class="headeraccenttable"></td>
    </tr>
<%    if(shoppingCartLine.getSku().compareTo("shoppingcart.CommentLine") == 0) { %>
    <tr>
      <td valign="top" colspan="3"><div class="minicarttext">
        >> <%=shoppingCartLine.getString("description")%></div>
      </td>
    </tr>
<%    } else { %>
    <tr>
      <td valign="top"><div class="minicarttext" nowrap>
        <%=CommonUtil.formatQuantity(shoppingCartLine.getQuantity())%></div>
      </td>
      <td valign="top">
        <div><a href="<%= "/commerce/catalog/details.jsp?" + HttpRequestConstants.PRODUCT_ID + "=" + shoppingCartLine.getSku() %>" class="minicartlink">
  <%=shoppingCartLine.getString("description")%></a></div>
      </td>
      <td align="right" valign="top"><div class="minicarttext" nowrap>
        <%=CommonUtil.formatPrice(shoppingCartLine.priceTotal(cartCustomer))%></div>
      </td>
    </tr>
<%
      } //end if comment line
    } //end for loop
%>
    <tr>
      <td colspan="3"  class="headeraccenttable"></td>
    </tr>
    <tr>
      <td colspan="3" align="right"><div class="headertext">Total
  <%=CommonUtil.formatPrice(shoppingCart.priceTotal(cartCustomer))%>
      </div></td>
    </tr>
    <tr>
      <td colspan="3">
          <a href="/commerce/shoppingcart.jsp" class="headerlink">View/Checkout</a>
        </td>
    </tr>
<% } else { %>
    <tr>
      <td colspan="3"><div class="headertext">Shopping Cart is empty.</div></td>
    </tr>
<% } %>
    <tr>
      <td colspan="3"  class="headeraccenttable"></td>
    </tr>
  </table>
<% } %>
<%-- end functional code presentation --%>


