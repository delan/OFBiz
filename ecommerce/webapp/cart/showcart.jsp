<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*, org.ofbiz.core.entity.*" %>

<ofbiz:object name="cart" property="_SHOPPING_CART_" type="org.ofbiz.ecommerce.shoppingcart.ShoppingCart" />  
<%if(cart != null && cart.size() > 0) {%>
  <%pageContext.setAttribute("cartIter", cart.iterator());%>
  <%org.ofbiz.ecommerce.catalog.CatalogWorker.getRandomCartProductAssoc(pageContext, "associatedProducts");%>
<%}%>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Quick Add</div>
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <a href="<ofbiz:url>/main</ofbiz:url>" class="lightbuttontext">[Continue&nbsp;Shopping]</a>
              <%if(cart != null && cart.size() > 0){%>
                <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
              <%}else{%>
                [Checkout]
              <%}%>
            </div>
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
            <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="quickaddform" style='margin: 0;'>
              <input type='text' name="add_product_id" value="<%=UtilFormatOut.checkNull(request.getParameter("add_product_id"))%>">
              <input type='text' size="5" name="quantity" value="<%=UtilFormatOut.checkNull(request.getParameter("quantity"), "1")%>">
              <input type='submit' value="Add To Cart">
              <%-- <a href="javascript:document.quickaddform.submit()" class="buttontext"><nobr>[Add to Cart]</nobr></a> --%>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<script language="JavaScript">
<!--
  document.quickaddform.add_product_id.focus();
//-->
</script>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping Cart</div>
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <a href="<ofbiz:url>/main</ofbiz:url>" class="lightbuttontext">[Continue&nbsp;Shopping]</a>
              <a href="javascript:document.cartform.submit()" class="lightbuttontext">[Recalculate&nbsp;Cart]</a>
              <%if(cart != null && cart.size() > 0){%>
                <a href="<ofbiz:url>/emptycart</ofbiz:url>" class="lightbuttontext">[Empty&nbsp;Cart]</a>
                <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
              <%}else{%>
                [Empty&nbsp;Cart] [Checkout]
              <%}%>
            </div>
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
  <ofbiz:if name="cartIter">
    <FORM METHOD="POST" ACTION="<ofbiz:url>/modifycart</ofbiz:url>" name='cartform' style='margin: 0;'>
      <table width='100%' CELLSPACING="0" CELLPADDING="4" BORDER="0">
        <TR> 
          <TD NOWRAP><div class='tabletext'><b>Product</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Quantity</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Unit Price</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Total</b></div></TD>
          <%-- <TD NOWRAP align=center><div class='tabletext'><b>Remove</b></div></TD> --%>
        </TR>

        <ofbiz:iterator name="item" property="cartIter" type="org.ofbiz.ecommerce.shoppingcart.ShoppingCartItem">
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
          <TR>
            <TD><div class='tabletext'><%-- <b><%= cart.getItemIndex(item)%></b> - --%><a href='<ofbiz:url>/product?product_id=<%=item.getProductId()%></ofbiz:url>' class='buttontext'><%=item.getProductId()%> - <%=UtilFormatOut.checkNull(item.getName())%></a> : <%=UtilFormatOut.checkNull(item.getDescription())%></div></TD>
            <TD NOWRAP ALIGN="center"><div class='tabletext'><input size="10" type="text" name="update_<%=cart.getItemIndex(item)%>" value="<ofbiz:format><%=item.getQuantity()%></ofbiz:format>"></div></TD>
            <TD NOWRAP ALIGN="right"><div class='tabletext'><ofbiz:format type="c"><%=item.getBasePrice()%></ofbiz:format></div></TD>
            <TD NOWRAP ALIGN="right"><div class='tabletext'><ofbiz:format type="c"><%=item.getTotalPrice()%></ofbiz:format></div></TD>
            <%-- <TD NOWRAP ALIGN="center"><div class='tabletext'><input type="checkbox" name="delete_<%= cart.getItemIndex(item) %>" value="0"></div></TD> --%>
          </TR>
        </ofbiz:iterator>

<%--
        <TR>
          <TD COLSPAN="3" ALIGN="right"><div class='tabletext'>Sales tax:</div></TD>
          <TD ALIGN="right"><div class='tabletext'>$0.00</div></TD>
        </TR> 
--%>

        <TR> 
          <TD COLSPAN="3" ALIGN="right" valign=bottom> 
             <!-- <HR SIZE=1> -->
            <div class='tabletext'><b>Cart Total:</b></div>
          </TD>
          <TD ALIGN="right" valign=bottom>
            <HR SIZE=1>
            <div class='tabletext'><b><ofbiz:format type="c"><%= cart.getGrandTotal() %></ofbiz:format></b></div>
          </TD>
        </TR>
      </table>
    </FORM>
<%--
      <CENTER>
        <input type="checkbox" name="always_showcart" <%= cart.viewCartOnAdd() ? "checked" : "" %>>&nbsp;Always view cart after adding an item.
    	<br><br>
        <input type="submit" value="Update Cart">
      </CENTER>
--%>
  </ofbiz:if>
<ofbiz:unless name="cartIter">
  <div class='head2'>Your shopping cart is empty.</div>
</ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
<%--
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td>
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td valign="middle" align="left">
            &nbsp;
          </td>
          <td valign="middle" align="right">
            <div class='lightbuttontextdisabled'>
              <a href="<ofbiz:url>/main</ofbiz:url>" class="lightbuttontext">[Continue&nbsp;Shopping]</a>
              <a href="javascript:document.cartform.submit()" class="lightbuttontext">[Recalculate&nbsp;Cart]</a>
              <%if(microCart != null && microCart.size() > 0){%>
                <a href="<ofbiz:url>/emptycart</ofbiz:url>" class="lightbuttontext">[Empty&nbsp;Cart]</a>
                <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="lightbuttontext">[Checkout]</a>
              <%}else{%>
                [Empty&nbsp;Cart] [Checkout]
              <%}%>
            </div>
          </td>
        </tr>
      </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
--%>
</TABLE>

 <ofbiz:if name="associatedProducts" size="0">
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;You might also be interested in:</div>
            </td>
            <td valign="middle" align="right">&nbsp;</td>
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
    <table width='100%' CELLSPACING="0" CELLPADDING="4" BORDER="0">
      <%int listIndex = 1;%>
      <!-- random complementary products -->
      <ofbiz:iterator name="product" property="associatedProducts">
        <%if(listIndex > 1) {%>
          <tr><td><hr class='sepbar'></td></tr>
        <%}%>
        <tr>
          <td>
            <%@ include file="/catalog/productsummary.jsp"%>
          </td>
        </tr>
        <%listIndex++;%>
      </ofbiz:iterator>
    </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
 </ofbiz:if>
