<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>


<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>
<%@ page import="org.ofbiz.ecommerce.order.OrderHelper" %>
<%@ page import="org.ofbiz.core.entity.*" %>
 

 
 

<% pageContext.setAttribute("PageName", "checkout"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<p class="head1">Final Checkout Review</p>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>

<ofbiz:if name="cart" size="0">

<%ShoppingCart cart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART); %>
<%
  GenericValue creditCardInfo = cart.getCreditCardInfo(helper);
  GenericValue shippingAddress = cart.getShippingAddress(helper);
  GenericValue  billingAddress = cart.getShippingAddress(helper);

  pageContext.setAttribute("creditCardInfo", creditCardInfo);
  pageContext.setAttribute("shippingAddress", shippingAddress);
  pageContext.setAttribute("billingAddress", billingAddress);
  pageContext.setAttribute("cartItems", cart.items());
%>

<ofbiz:unless name="shippingAddress">
  <h3>ERROR: Shipping address must be specified, please go back or start over.</h3>
</ofbiz:unless>
<ofbiz:if name="shippingAddresss">
<table width="100%" cellpadding="3" cellspacing="0" border="0">
  <%-- row to set cell widths --%>
  <tr>
    <td width="5%"></td>
    <td width="95%"></td>
  </tr>
 <tr>
    <td colspan="2" align="left"><div class="head2">Shipping Destination</div></td>
 </tr>

 <tr>
    <td>&nbsp;</td>
    <td align="left" valign="top" nowrap>
      <div class="tabletext">
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("toName"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("attnName"), "Attn: ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address1"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address2"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("city"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getRelatedOne("StateProvinceGeo").getString("name"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
        <%=UtilFormatOut.ifNotEmpty(shippingAddress.getRelatedOne("CountryGeo").getString("name"), "", "<br>")%>
      </div>
    </td>
  </tr>
<%--  <tr>
    <td align="left" colspan="2"><div class="head2">Shipment Splitting Preference</div></td>
  </tr>
  <tr>
      <td>&nbsp;</td>
      <td align="left">
        <div class="tabletext"><%=splittingPreferenceCode%></div>
      </td>
  </tr>  --%>



<%--  <tr>
    <td align="left" colspan="2"><div class="head2">Special Instructions</div></td>
  </tr>
  <tr>
      <td>&nbsp;</td>
      <td align="left">
  <div class="tabletext"><%=specialInstructions %></div>
      </td>
  </ tr>  --%>

<%--  <tr>
    <td align="left" colspan="5"><div class="head2">Shipping Method</div></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td align="left">
        <div class="tabletext">
          <%=UtilFormatOut.checkNull(shippingMethod)%>
          <%=UtilFormatOut.ifNotEmpty(shippingAccount, "<br>Use Account: ", "")%>
        </div>
    </td>
  </tr> --%>
<%-- <ofbiz:if name="customerPaymentType" value="STORE_CREDIT">
      <tr>
        <td align="left" colspan="5"><div class="head2">Payment Information</div></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td align="left">
            <div class="tabletext"><b>Store Credit</b></div>
        </td>
      </tr>
</ofbiz:if>
<ofbiz:if name="customerPaymentType" value="PURCHASE_ORDER">
      <tr>
        <td align="left" colspan="5"><div class="head2">Payment Information</div></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td align="left">
            <div class="tabletext"><b>Purchase Order Number: <%=UtilFormatOut.checkNull(customerPoNumber)%></b></div>
        </td>
      </tr>
</ofbiz:if>
<ofbiz:if name="customerPaymentType" value="CREDIT_CARD"> --%>
          <tr>
            <td align="left" colspan="5"><div class="head2">Payment Information</div></td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td align="left" valign="top" nowrap>
              <div class="tabletext">
                <%=creditCardInfo.getString("nameOnCard")%><br>
                <%=UtilFormatOut.checkNull(billingAddress.getString("address1"))%><br>
                <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("address2"),  "", "<br>")%>
                <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("city"), "", "<br>")%>
                <%=UtilFormatOut.checkNull(billingAddress.getRelatedOne("StateProvinceGeo").getString("name"))%> &nbsp; <%=UtilFormatOut.checkNull(billingAddress.getString("postalCode"))%><br>
                <%=UtilFormatOut.ifNotEmpty(billingAddress.getRelatedOne("CountryGeo").getString("name"), "", "")%>
              </div>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td align="left">
                <div class="tabletext"><b><%=creditCardInfo.getString("cardType")%>: <%=creditCardInfo.getString("cardNumber").substring(creditCardInfo.getString("cardNumber").length()-4)%> <%=creditCardInfo.getString("expireDate")%></b></div>
            </td>
          </tr>
      <%-- </ofbiz:if> --%>
<%-- </ofbiz:if> --%>
</table>
<%-- table for shoppingcartline items --%>
  <table border="0" cellpadding="1" width="100%">
    <%-- use this row to set the widths on all the columns --%>
    <tr>
      <td width="15%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="50%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="5%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="15%"><img src="../images/shim.gif" width="2" height="1"></td>
      <td width="15%"><img src="../images/shim.gif" width="2" height="1"></td>
    </tr>

    <tr>
      <td colspan="5" bgcolor="#678475">
      <div class="tabletext"><font color="white"><b>Order</b></font></div>
      </td>
    </tr>

    <tr>
      <td align="left" valign="bottom"><div class="tabletext"><b>ID</b></div></td>
      <td align="left" valign="bottom"><div class="tabletext"><b>Description</b></div></td>
      <td align="center" valign="bottom"><div class="tabletext"><b>Quantity</b></div></td>
      <td align="center" valign="bottom"><div class="tabletext"><b>Our Price</b></div></td>
      <td align="center" valign="bottom"><div class="tabletext"><b>SubTotal</b></div></td>
    </tr>
    <tr>
      <td colspan="5"><hr size="1"></td>
    </tr>

 <ofbiz:iterator name="cartItem" type="ShoppingCartItem" property="cartItems">
<% pageContext.setAttribute("productId", cartItem.getProductId()); %>
  <ofbiz:if name="productId" value="shoppingcart.CommentLine">
    <tr>
      <td valign="middle" colspan="5">
        <b><div class="tabletext"> >> <%=cartItem.getDescription()%></div></b>
      </td>
    </tr>
  </ofbiz:if>
  <ofbiz:unless name="productId" value="shoppingcart.CommentLine">
    <tr>
      <td nowrap valign="top">
        <div class="tabletext"><%=cartItem.getProductId()%></div>
      </td>
      <td valign="top">
        <div class="tabletext"><%=cartItem.getDescription()%>
        <%-- //Get the RandomLengthString, if one exists...
           String RLString = null;
        %>
        <% if(cartItem.containsKey("RANDOM_LENGTH_STRING"))
          {
            RLString = cartItem.getString("RANDOM_LENGTH_STRING");
            if(RLString != null)
            {
        %>
              [[RandomLength:<%=(String)RLString%>]]
        <%
            }
          }
        --%>
        </div>
      </td>
      <td align="center" valign="top">
        <div class="tabletext"><%=UtilFormatOut.formatQuantity(cartItem.getQuantity())%></div>
      </td>
      <td align="right" nowrap valign="top">
        <div class="tabletext"><%-- cartItem.getUnitPrice().getCurrency() --%>
        <%=UtilFormatOut.formatPrice(cartItem.getPrice())%></div>
      </td>
      <td align="right" nowrap valign="top">
        <div class="tabletext"><%-- lineTotal.getCurrency() --%>
        <%=UtilFormatOut.formatPrice(cartItem.getTotalPrice())%></div>
      </td>
    </tr>
  </ofbiz:unless>
    <tr>
      <td colspan="5" height="1"><hr size="1"></td>
    </tr>
</ofbiz:iterator>

<%
String prevPage = "/selectaddress";
//if(CommonConstants.CREDITCARD_MANAGE_INFO) prevPage = "/checkoutoptions";
%>
    <tr>
      <td colspan="4" align="right"><div class="tabletext">Item Total</div></td>
      <td align="right" nowrap>
  <div class="tabletext"><%=UtilFormatOut.formatPrice(cart.getItemTotal())%></div>
      </td>
    </tr>
    <tr>
      <td colspan="4" align="right"><div class="tabletext">Shipping & handling</div></td>
      <td align="right" nowrap>
  <div class="tabletext"><%=UtilFormatOut.formatPrice(cart.getShipping())%></div>
      </td>
    </tr>

    <tr>
      <td colspan="4" align="right"><div class="tabletext">Total tax</div></td>
      <td align="right" nowrap>
  <div class="tabletext"><%=UtilFormatOut.formatPrice(cart.getSalesTax())%></div>
      </td>
    </tr>

    <tr>
      <td colspan="4" align="right"><div class="tabletext"><b>Total due</b></div></td>
     <td align="right" bgcolor="#99BBAA" nowrap>
  <div class="tabletext"><b><%-- total.getCurrency() --%>
  <%=UtilFormatOut.formatPrice(cart.getGrandTotal())%></b></div>
      </td>
    </tr>
    <tr>
      <td colspan="5"><hr size="1"></td>
    </tr>
   <tr>
      <td colspan="4" align="left">
      <a href="<ofbiz:url><%=prevPage%></ofbiz:url>" class="buttonlinkbig">&nbsp;[Back]</a></td>
      <td align="right">
        <a href="<ofbiz:url>/confirmorder?event=CREATE</ofbiz:url>" class="buttonlinkbig">[Submit&nbsp;Order]&nbsp;</a>
      </td>
    </tr>
  </table>
</ofbiz:if><%-- Shipping address check --%>

</ofbiz:if><%-- cart check --%>
<ofbiz:unless name="cart" size="0">
<h3>ERROR: Shopping cart empty, please start over.</h3>
</ofbiz:unless>



<%@ include file="/includes/footer.jsp" %>


