<%
/**
 *  Title: Checkout Options Page
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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<% pageContext.setAttribute("PageName", "checkoutoptions"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%ShoppingCart cart = (ShoppingCart)session.getAttribute(SiteDefs.SHOPPING_CART); %>
<%if ((cart != null) && (cart.size() > 0)) pageContext.setAttribute("cart", cart);%>
<ofbiz:if name="cart">
    <%if (cart.getMaySplit() != null) pageContext.setAttribute("maySplit", cart.getMaySplit());%>
</ofbiz:if>

<BR>
<form method="post" name="checkoutInfoForm" action="<ofbiz:url>/checkout</ofbiz:url>" style='margin:0;'>
<table width="100%" border="0" cellpadding='0' cellspacing='0'>
  <tr valign="top" align="left">
    <td height='100%'>
<% pageContext.setAttribute("carrierShipmentMethodList", delegator.findAllCache("CarrierShipmentMethod", null)); %>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead" nowrap>1) How shall we ship it?</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
  <table width="100%" cellpadding="4" border="0" cellpadding="0" cellspacing="0">
  <%String chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();%>
<ofbiz:iterator name="carrierShipmentMethod" property="carrierShipmentMethodList">
    <tr>
      <td width='1%' valign="top" >
        <%String shippingMethod = carrierShipmentMethod.getString("shipmentMethodTypeId") + "@" + carrierShipmentMethod.getString("partyId");%>
        <input
          <ofbiz:if name="cart">
            <%=shippingMethod.equals(chosenShippingMethod) ? "CHECKED" : ""%>
          </ofbiz:if>
          type="radio" name="shipping_method"
          value="<%=shippingMethod%>"
        >
      </td>
      <td valign="top">
        <%String shipMethDescription = "";%>
        <%GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType");%>
        <%if(shipmentMethodType != null) shipMethDescription = shipmentMethodType.getString("description");%>
        <div class="tabletext"><%=carrierShipmentMethod.getString("partyId") + " " + UtilFormatOut.checkNull(shipMethDescription)%></div>
      </td>
    </tr>
</ofbiz:iterator>
<ofbiz:unless name="carrierShipmentMethodList" size="0">
    <tr>
      <td width="1%" valign="top">
        <input CHECKED type="radio" name="shipping_method" value="Default">
      </td>
      <td valign="top">
        <div class="tabletext">Use Default: No other shipping methods available.</div>
      </td>
    </tr>
</ofbiz:unless>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan=2>
        <div class="head2"><b>Ship all at once, or 'as available'?</b></div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input <ofbiz:if name="maySplit" value="false" type="Boolean">CHECKED</ofbiz:if> type="radio" name="may_split" value="false">
      </td>
      <td valign="top">
        <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input <ofbiz:if name="maySplit" value="true" type="Boolean">CHECKED</ofbiz:if> type="radio" name="may_split" value="true">
      </td>
      <td valign="top">
        <div class="tabletext">Please ship items I ordered as they become available (you may incur additional shipping charges).</div>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div class="head2"><b>Special Instructions</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <textarea cols="30" rows="5" name="shipping_instructions"><ofbiz:if name="cart"
            ><%=UtilFormatOut.checkNull(cart.getShippingInstructions())%></ofbiz:if></textarea>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div class="head2"><b>Email Addresses</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div class="tabletext">Your order will be sent to the following email addresses:</div>
        <div class="tabletext">
          <b>
          <% pageContext.setAttribute("emailList",  ContactHelper.getContactMechByType(userLogin.getRelatedOne("Party"), "EMAIL_ADDRESS", false));%>
          <ofbiz:iterator name="email" property="emailList">
            <%=UtilFormatOut.checkNull(email.getString("infoString"))%>,
          </ofbiz:iterator>
          </b>
        </div>
        <div class="tabletext">Your may update these in your <a href="<ofbiz:url>/viewprofile?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">profile</a>.</div>
        <br>
        <div class="tabletext">You may add other comma separated email addresses here that will be used only for the current order:</div>
        <input type="text" size="30" name="order_additional_emails" value='<ofbiz:if name="cart"><%=UtilFormatOut.checkNull(cart.getOrderAdditionalEmails())%></ofbiz:if>'>
      </td>
    </tr>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</td>

<td bgcolor="white" width="1">&nbsp;&nbsp;</td>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<td height='100%'>
<% pageContext.setAttribute("shippingContactMechList", ContactHelper.getContactMech(userLogin.getRelatedOne("Party"), "SHIPPING_LOCATION", "POSTAL_ADDRESS", false)); %>  
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>' style='height: 100%;'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead" nowrap><b>2) Where&nbsp;shall&nbsp;we&nbsp;ship&nbsp;it?</b></div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR style='height: 100%;'>
    <TD width='100%' valign=top>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>' style='height: 100%;'>
        <tr>
          <td valign=top>

<table width="100%" border="0" cellpadding="4" cellspacing="0">
  <tr><td colspan="2"><a href="<ofbiz:url>/editcontactmech?CONTACT_MECH_TYPE_ID=POSTAL_ADDRESS&CM_NEW_PURPOSE_TYPE_ID=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">[Add New Address]</a></td></tr>
 <ofbiz:if name="shippingContactMechList" size="0">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <ofbiz:iterator name="shippingContactMech" property="shippingContactMechList">
    <%GenericValue shippingAddress = shippingContactMech.getRelatedOne("PostalAddress");%>
    <%pageContext.setAttribute("shippingAddress", shippingAddress);%>
      <tr>
        <td align="left" valign="top" width="1%" nowrap>
            <%String shippingContactMechId = (String) shippingAddress.get("contactMechId");%>
            <input type="radio" name="shipping_contact_mech_id" value="<%=shippingContactMechId%>"
              <ofbiz:if name="cart"><%=shippingContactMechId.equals(cart.getShippingContactMechId()) ? "CHECKED" : ""%></ofbiz:if>>
        </td>
        <td align="left" valign="top" width="99%" nowrap>
          <div class="tabletext">
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("toName"), "<b>To:</b> ", "<br>")%>
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address1"), "", "<br>")%>
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address2"), "", "<br>")%>
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("city"), "", "<br>")%>
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
          <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("countryGeoId"), "", "<br>")%>
<%--          <a href="<ofbiz:url>/profileeditaddress?DONE_PAGE=checkoutoptions.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=billingLocation.getAddressId()%></ofbiz:url>" class="buttontext">[Update]</a>
          <a href="<ofbiz:url>/checkoutoptions?<%="event"%>=<%=EventConstants.DELETE_SHIPPING_LOCATION%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=billingLocation.getAddressId()%></ofbiz:url>" class="buttontext">[Delete]</a> --%>
          </div>
        </td>
      </tr>
      <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </ofbiz:iterator>
 </ofbiz:if>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

</td>
<td bgcolor="white" width="1">&nbsp;&nbsp;</td>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<td>

<% pageContext.setAttribute("creditCardInfoList", EntityUtil.filterByDate(userLogin.getRelatedOne("Party").getRelated("CreditCardInfo"))); %>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>' style='height: 100%;'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead" nowrap><b>3) How shall you pay?</b></div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR style='height: 100%;'>
    <TD width='100%' valign=top>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>' style='height: 100%;'>
        <tr>
          <td valign=top>

<table width="100%" cellpadding="4" cellspacing="0" border="0">
  <tr><td colspan="2"><a href="<ofbiz:url>/editcreditcard?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">[Add Credit Card]</a></td></tr>
 <ofbiz:if name="creditCardInfoList" size="0"> 
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  <ofbiz:iterator name="creditCardInfo" property="creditCardInfoList">
      <tr>
        <td width="1%" nowrap>
          <%String creditCardId = creditCardInfo.getString("creditCardId");%>
          <input type="radio" name="credit_card_id" value="<%=creditCardId%>"
            <ofbiz:if name="cart"><%=creditCardId.equals(cart.getCreditCardId()) ? "CHECKED" : ""%></ofbiz:if>>
        </td>
        <td width="50%" nowrap>
          <span class="tabletext">
            <%=ContactHelper.formatCreditCard(creditCardInfo)%>
          </span>
            <a href="<ofbiz:url>/editcreditcard?DONE_PAGE=checkoutoptions&CREDIT_CARD_ID=<%=creditCardInfo.getString("creditCardId")%></ofbiz:url>" class="buttontext">[Update]</a>
        </td>
      </tr>
      <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </ofbiz:iterator>
 </ofbiz:if>
</table>
<ofbiz:unless name="creditCardInfoList" size="0">
   <h3>There are no credit cards on file.</h3>
</ofbiz:unless>

<% pageContext.setAttribute("billingAccountRoleList", delegator.findByAnd("BillingAccountRole", UtilMisc.toMap(
        "partyId", userLogin.getString("partyId"),
        "roleTypeId", "BILL_TO_CUSTOMER"), null)); %>  

 <ofbiz:if name="billingAccountRoleList" size="0">
    <div class="tabletext">To pay with store credit, enter your Purchase Order (PO) number here and select the billing account:</div>
    <input type="text" name="corresponding_po_id" size="20" value="<ofbiz:if name="cart"><%=UtilFormatOut.checkNull(cart.getPoNumber())%></ofbiz:if>">
    <br>

  <table width="90%" border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <ofbiz:iterator name="billingAccountRole" property="billingAccountRoleList">
    <%GenericValue billingAccount = billingAccountRole.getRelatedOne("BillingAccount");%>
    <tr>
      <td align="left" valign="top" width="1%" nowrap>
          <input type="radio" name="billing_account_id" value="<%=billingAccount.getString("billingAccountId")%>"
            <ofbiz:if name="cart"><%=billingAccount.getString("billingAccountId").equals(cart.getBillingAccountId()) ? "CHECKED" : ""%></ofbiz:if>>
      </td>
      <td align="left" valign="top" width="99%" nowrap>
        <div class="tabletext">
        Billing Account #<b><%=UtilFormatOut.checkNull(billingAccount.getString("billingAccountId"))%></b><br>
        <%=UtilFormatOut.checkNull(billingAccount.getString("description"))%>
  <%--          <a href="<ofbiz:url>/profileeditaddress?DONE_PAGE=checkoutoptions?billing_account_id=<%=billingAccount.get("billingAccountId")%></ofbiz:url>" class="buttontext">[Update]</a>
            <a href="<ofbiz:url>/checkoutoptions?event=DELETE_SHIPPING_LOCATION&billing_account_id=<%=billingAccount.get("billingAccountId")%></ofbiz:url>" class="buttontext">[Delete]</a>--%>

<%--    <%GenericValue billingLocation = billingAccountRole.getRelatedOne("BillingAccount").getRelatedOne("PostalAddress");
      pageContext.setAttribute("billingLocation", billingLocation);%>
    <ofbiz:if name="billingLocation">
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("toName"), "<b>To:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("address1"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("address2"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("city"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(billingLocation.getString("postalCode"))%><br>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("countryGeoId"), "", "<br>")%> 
    </ofbiz:if> --%>
        </div>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </ofbiz:iterator>
  </table>
 </ofbiz:if>
<!--XXX  <a href="<ofbiz:url>/requestbillingaccount?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">[Request Billing Account]</a> -->
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

    </td>
  </tr>
</table>
</form>

<table width="100%">
<tr valign="top">
<td align="left">
&nbsp;<a href="<ofbiz:url>/shoppingcart</ofbiz:url>" class="buttontextbig">[Back to Shopping Cart]</a>
</td>
<td align="right">
<a href="javascript:document.checkoutInfoForm.submit()" class="buttontextbig">[Continue to Final Order Review]</a>
</td>
</tr>
</table>
<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
