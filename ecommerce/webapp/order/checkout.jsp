<%
/**
 *  Title: Checkout Page
 *  Description: None
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     Eric Pabst
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>

<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.ContactHelper" %>
<%@ page import="org.ofbiz.commonapp.party.party.PartyHelper" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<% pageContext.setAttribute("PageName", "checkout"); %>

<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<p class="head1">Final Checkout Review</p>
<p>NOTE: This is a DEMO store-front.  Orders placed here will NOT be billed, and will NOT be fulfilled.</p>

<%ShoppingCart cart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART); %>
<%pageContext.setAttribute("cart", cart);%>
<ofbiz:if name="cart" size="0">
<%
  GenericValue shippingAddress = cart.getShippingAddress(delegator);
  GenericValue creditCardInfo = cart.getCreditCardInfo(delegator);
  GenericValue billingAddress = cart.getBillingAddress(delegator);
  GenericValue billingAccount = cart.getBillingAccountId() != null ? delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", cart.getBillingAccountId())) : null;

  String shippingInstructions = cart.getShippingInstructions();
  String customerPoNumber = cart.getPoNumber();
  String carrierPartyId = cart.getCarrierPartyId();
  String shipmentMethodTypeId = cart.getShipmentMethodTypeId();
  Boolean maySplit = cart.getMaySplit();
//  if (creditCardInfo == null) {

  Collection orderItemList = cart.makeOrderItems(delegator, null);
  Iterator orderAdjustmentIterator = cart.getAdjustments().iterator();
%>

<%@ include file="orderinformation.jsp" %>

<br>
<%@ include file="orderitems.jsp" %>

  <table border="0" cellpadding="1" width="100%">
   <tr>
      <td colspan="4" align="left">
      <a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="buttontextbig">[Back&nbsp;to&nbsp;Options]</a></td>
      <td align="right">
        <a href="<ofbiz:url>/processorder</ofbiz:url>" class="buttontextbig">[Submit&nbsp;Order]&nbsp;</a>
      </td>
    </tr>
  </table>

</ofbiz:if><%-- cart check --%>
<ofbiz:unless name="cart" size="0">
<h3>ERROR: Shopping cart empty, please start over.</h3>
</ofbiz:unless>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
