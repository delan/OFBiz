<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*, org.ofbiz.core.entity.*,
                 org.ofbiz.commonapp.product.promo.ProductPromoWorker, org.ofbiz.commonapp.order.order.OrderReadHelper" %>
<%@ page import="java.util.*, org.ofbiz.commonapp.product.catalog.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<ofbiz:object name="cart" property="_SHOPPING_CART_" type="org.ofbiz.commonapp.order.shoppingcart.ShoppingCart" />
<%if(cart != null && cart.size() > 0) {%>
  <%pageContext.setAttribute("cartIter", cart.iterator());%>
  <%pageContext.setAttribute("cartAdjustments", cart.getAdjustments());%>
  <%org.ofbiz.commonapp.product.catalog.CatalogWorker.getRandomCartProductAssoc(pageContext, "associatedProducts");%>
<%}%>
<%-- get these for the promoText --%>
<%pageContext.setAttribute("productPromos", ProductPromoWorker.getCatalogProductPromos(delegator, request));%>
<%String contentPathPrefix = CatalogWorker.getContentPathPrefix(pageContext);%>
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
      <table width='100%' cellspacing="0" cellpadding="1" border="0">
        <TR> 
          <TD NOWRAP><div class='tabletext'><b>Product</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Quantity</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Unit Price</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Adjustments</b></div></TD>
          <TD NOWRAP align=right><div class='tabletext'><b>Item Total</b></div></TD>
          <TD NOWRAP align=center><div class='tabletext'><b>Remove</b></div></TD>
        </TR>

        <ofbiz:iterator name="item" property="cartIter" type="org.ofbiz.commonapp.order.shoppingcart.ShoppingCartItem">
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
          <tr>
            <td>
                <div class='tabletext'>
                    <%-- <b><%= cart.getItemIndex(item)%></b> - --%>
                    <a href='<ofbiz:url>/product?product_id=<%=item.getProductId()%></ofbiz:url>' class='buttontext'><%=item.getProductId()%> - 
                    <%=UtilFormatOut.checkNull(item.getName())%></a> : 
                    <%=UtilFormatOut.checkNull(item.getDescription())%>

                    <%-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... --%>
                    <%GenericValue itemProduct = item.getProduct();%>
                    <%if (!CatalogWorker.isCatalogInventoryRequired(request, itemProduct) && !CatalogWorker.isCatalogInventoryAvailable(request, item.getProductId(), item.getQuantity())) {%>
                        <b><%=UtilFormatOut.ifNotEmpty(itemProduct.getString("inventoryMessage"), "(", ")")%></b>
                    <%}%>
                </div>
            </td>
            <td nowrap align="center">
              <div class='tabletext'>
                <%if (item.getIsPromo()) {%>
                    <ofbiz:format><%=item.getQuantity()%></ofbiz:format>
                <%} else {%>
                    <input size="6" type="text" name="update_<%=cart.getItemIndex(item)%>" value="<ofbiz:format><%=item.getQuantity()%></ofbiz:format>">
                <%}%>
              </div>
            </td>
            <td nowrap align="right"><div class='tabletext'><ofbiz:format type="currency"><%=item.getBasePrice()%></ofbiz:format></div></TD>
            <td nowrap align="right"><div class='tabletext'><ofbiz:format type="currency"><%=item.getOtherAdjustments()%></ofbiz:format></div></TD>
            <td nowrap align="right"><div class='tabletext'><ofbiz:format type="currency"><%=item.getItemSubTotal()%></ofbiz:format></div></TD>
            <td nowrap align="center"><div class='tabletext'><%if (!item.getIsPromo()) {%><input type="checkbox" name="delete_<%= cart.getItemIndex(item) %>" value="0"><%} else {%>&nbsp;<%}%></div></TD>
          </TR>
        </ofbiz:iterator>

        <ofbiz:if name="cartAdjustments" size="0">
            <%double cartSubTotal = cart.getSubTotal();%>
            <tr><td colspan="7"><hr class='sepbar'></td></tr>
              <tr>
                <td colspan="4" nowrap align="right"><div class='tabletext'>Sub&nbsp;Total:</div></td>
                <td nowrap align="right"><div class='tabletext'><ofbiz:format type="currency"><%=cartSubTotal%></ofbiz:format></div></td>
                <td>&nbsp;</td>
              </tr>
            <ofbiz:iterator name="cartAdjustment" property="cartAdjustments" type="org.ofbiz.core.entity.GenericValue">
              <%GenericValue adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType");%>
              <%if (adjustmentType != null) pageContext.setAttribute("adjustmentType", adjustmentType);%>
              <tr>
                <td colspan="4" nowrap align="right"><div class='tabletext'><i>Adjustment</i> - <ofbiz:entityfield attribute="adjustmentType" field="description" default="&nbsp;"/>:</div></td>
                <td nowrap align="right"><div class='tabletext'><ofbiz:format type="currency"><%=OrderReadHelper.calcOrderAdjustment(cartAdjustment, cartSubTotal)%></ofbiz:format></div></td>
                <td>&nbsp;</td>
              </tr>
            </ofbiz:iterator>
        </ofbiz:if>
<%--
        <TR>
          <TD COLSPAN="3" ALIGN="right"><div class='tabletext'>Sales tax:</div></TD>
          <TD ALIGN="right"><div class='tabletext'>$0.00</div></TD>
        </TR> 
--%>

        <tr> 
          <td colspan="4" align="right" valign=bottom> 
             <!-- <hr size=1> -->
            <div class='tabletext'><b>Cart&nbsp;Total:</b></div>
          </td>
          <td align="right" valign=bottom>
            <hr size=1 class='sepbar'>
            <div class='tabletext'><b><ofbiz:format type="currency"><%=cart.getGrandTotal()%></ofbiz:format></b></div>
          </td>
        </tr>
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

<%-- Make sure that at least one promo has non-empty promoText --%>
<%boolean showPromoText = false;%>
<ofbiz:iterator name="productPromo" property="productPromos">
    <%if (UtilValidate.isNotEmpty(productPromo.getString("promoText"))) { showPromoText = true; }%>
</ofbiz:iterator>
<%pageContext.setAttribute("showPromoText", new Boolean(showPromoText));%>

<ofbiz:if name="showPromoText" type="Boolean">
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Special Offers</div>
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
                <table width='100%' cellspacing="0" cellpadding="1" border="0">
                  <%int listIndex = 1;%>
                  <!-- show promotions text -->
                  <ofbiz:iterator name="productPromo" property="productPromos">
                    <%if (UtilValidate.isNotEmpty(productPromo.getString("promoText"))) {%>
                        <%if (listIndex > 1) {%>
                          <tr><td><hr class='sepbar'></td></tr>
                        <%}%>
                        <tr>
                          <td>
                            <div class='tabletext'><%=productPromo.getString("promoText")%></div>
                          </td>
                        </tr>
                        <%listIndex++;%>
                    <%}%>
                  </ofbiz:iterator>
                </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</ofbiz:if>

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
    <table width='100%' cellspacing="0" cellpadding="1" border="0">
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
