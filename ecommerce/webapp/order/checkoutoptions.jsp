<%-- Copyright (c) 2001 by RelmSoft, Inc. All Rights Reserved. --%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.order.*" %>


<% pageContext.setAttribute("PageName", "checkoutoptions"); %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<form method="post" name="checkoutInfoForm" action="<ofbiz:url>/checkout</ofbiz:url>" style=margin:0;>
<table width="100%" border="0">
<tr valign="top" align="left">
<td bgcolor="#FFCCCC">
<% String defaultShipMethod = "FIXME: US Postal Service (default)"; %>
<% pageContext.setAttribute("carrierShipmentMethodList", helper.findAllCache("CarrierShipmentMethod", null)); %>

  <div class="head2" nowrap><b>How shall WE ship it?</b></div>
  <table width="100%" cellpadding="0" border="0" cellpadding="0" cellspacing="0">
<ofbiz:iterator name="carrierShipmentMethod" property="carrierShipmentMethodList">
    <tr>
      <td width=1% valign="top" >
        <input
          <ofbiz:if name='<%=carrierShipmentMethod.getString("shipmentMethodTypeId")%>' value="<%=defaultShipMethod%>">
             CHECKED
          </ofbiz:if>
          type="radio" name="shipping_method"
          value="<%=carrierShipmentMethod.getString("shipmentMethodTypeId")%>@<%=carrierShipmentMethod.getString("partyId")%>"
        >
      </td>
      <td valign="top">
        <div class="tabletext"><%=carrierShipmentMethod.getString("partyId") + " " + carrierShipmentMethod.getString("shipmentMethodTypeId") %></div>
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
    <tr>
      <td colspan="2">
      Enter account code here, or ship freight collect: <input size="25" type="text" name="SHIPPING_ACCOUNT" value="">
      </td>
    </tr>
<%--    <tr>
      <td colspan="2">
        <hr size="1">
      </td>
    </tr>
    <tr>
      <td colspan=2>
        <div class="head2"><b>Ship all at once, or 'as available'?</b></div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input CHECKED type="radio" name="<%=HttpRequestConstants.SPLITTING_PREFERENCE_CODE%>" value="<%=HttpRequestConstants.SPLITTING_PREFERENCE_NO_SPLIT_CODE%>">
      </td>
      <td valign="top">
        <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input type="radio" name="<%=HttpRequestConstants.SPLITTING_PREFERENCE_CODE%>" value="<%=HttpRequestConstants.SPLITTING_PREFERENCE_SPLIT_CODE%>">
      </td>
      <td valign="top">
        <div class="tabletext">Please ship items I ordered as they become available (you may incur additional shipping charges).</div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <hr size="1">
      </td>
    </tr>  --%>
    <tr>
      <td colspan="2">
        <div class="head2"><b>Special Instructions</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <textarea cols="30" rows="5" name="shipping_instructions"></textarea>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <hr size="1">
      </td>
    </tr>
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
          <% pageContext.setAttribute("emailList",  OrderHelper.getContactMech(userLogin.getRelatedOne("Party"), "EMAIL_ADDRESS", false));%>
          <ofbiz:iterator name="email" property="emailList">
            <%=UtilFormatOut.checkNull(email.getString("infoString"))%>,
          </ofbiz:iterator>
          </b>
        </div>
        <div class="tabletext">Your may update these in your <a href="<ofbiz:url>/viewprofile?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttonlink">profile</a>.</div>
        <br>
        <div class="tabletext">You may add other comma separated email addresses here that will be used only for the current order:</div>
        <input type="text" size="38" name="ORDER_ADDITIONAL_EMAILS" value="">
      </td>
    </tr>
  </table>

</td>
<td bgcolor="#888888" width="1">
</td>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<td bgcolor="#FFFFCC">
<% pageContext.setAttribute("shippingPartyContactPurposeList", helper.findByAnd("PartyContactMechPurpose", UtilMisc.toMap(
        "partyId", userLogin.getString("partyId"),
        "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null)); %>  

<div class="head2" nowrap><b>Where shall WE ship it?</b></div>

  <a href="<ofbiz:url>/editcontactmech?CONTACT_MECH_TYPE_ID=POSTAL_ADDRESS&CM_NEW_PURPOSE_TYPE_ID=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttonlink">[Add New Address]</a>
 <ofbiz:if name="shippingPartyContactPurposeList" size="0">
  <table width="90%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="100%" colspan="2" height="1" bgcolor="888888"></td>
    </tr>
    <ofbiz:iterator name="shippingPartyContactPurpose" property="shippingPartyContactPurposeList">
    <%GenericValue shippingLocation = shippingPartyContactPurpose.getRelatedOne("PartyContactMech")
            .getRelatedOne("ContactMech").getRelatedOne("PostalAddress");
      pageContext.setAttribute("shippingLocation", shippingLocation);%>
    <ofbiz:if name="shippingLocation">
    <tr>
      <td align="left" valign="top" width="1%" nowrap>
          <input type="radio" name="SHIPPING_CONTACT_MECH_ID" value="<%=shippingLocation.get("contactMechId")%>">
      </td>
      <td align="left" valign="top" width="99%" nowrap>
        <div class="tabletext">
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("toName"), "<b>To:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("address1"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("address2"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("city"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(shippingLocation.getString("postalCode"))%><br>
        <%=UtilFormatOut.ifNotEmpty(shippingLocation.getString("countryGeoId"), "", "<br>")%>
<%--          <a href="<ofbiz:url>/profileeditaddress?DONE_PAGE=checkoutoptions.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingLocation.getAddressId()%></ofbiz:url>" class="buttonlink">[Update]</a>
          <a href="<ofbiz:url>/checkoutoptions?<%="event"%>=<%=EventConstants.DELETE_SHIPPING_LOCATION%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=shippingLocation.getAddressId()%></ofbiz:url>" class="buttonlink">[Delete]</a>--%>
        </div>
      </td>
    </tr>
    <tr>
      <td width="100%" colspan="2" height="1" bgcolor="888888"></td>
    </tr>
    </ofbiz:if>
  </ofbiz:iterator>
  </table>
 </ofbiz:if>
</td>
<td bgcolor="#888888" width="1">
</td>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<%-- ======================================================================== --%>
<td bgcolor="#CCFFCC">

<% pageContext.setAttribute("creditCardInfoList", userLogin.getRelatedOne("Party").getRelated("CreditCardInfo")); %>


<div class="head2" nowrap><b>How shall YOU pay?</b></div>
<%-- the add new credit card buttons --%>
<a href="<ofbiz:url>/editcreditcard?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttonlink">[Add Credit Card]</a>

<ofbiz:if name="creditCardInfoList" size="0"> 
<table width="90%" cellpadding="1" cellspacing="0" border="0">
  <tr>
    <td colspan="3"><hr size="1"></td>
  </tr>
    <ofbiz:iterator name="creditCardInfo" property="creditCardInfoList">
  <tr>
    <td width="1%" nowrap>
      <input type="radio" name="PAYMENT_CODE" value="<%=creditCardInfo.getString("creditCardId")%>">
    </td>
    <td width="50%" nowrap>
      <span class="tabletext">
        <%=creditCardInfo.getString("cardType")%>
        <%String cardNumber = creditCardInfo.getString("cardNumber");
          if(cardNumber != null && cardNumber.length() > 4) {%> <%=cardNumber.substring(cardNumber.length()-4)%>  <% } %>
        <%=creditCardInfo.getString("expireDate")%>
      </span>
        <a href="<ofbiz:url>/editcreditcard?DONE_PAGE=checkoutoptions&CREDIT_CARD_ID=<%=creditCardInfo.getString("creditCardId")%></ofbiz:url>" class="buttonlink">[Update]</a>
    </td>
  </tr>
  <tr>
    <td colspan="3" height="1" bgcolor="888888"></td>
  </tr>
</ofbiz:iterator>
</table>
</ofbiz:if>
<ofbiz:unless name="creditCardInfoList" size="0">
   <h3>There are no credit cards on file.</h3>
</ofbiz:unless>

    <div class="tabletext">To pay with store credit, enter your Purchase Order (PO) number here and select the billing address:</div>
    <input type="text" name="customer_po_number" size="20">
<%--    <nobr><input CHECKED type="radio" name="credit_card_id" value="store_credit_purchase_order">
    Pay with Store Credit<nobr>--%>
    <br>

<% pageContext.setAttribute("billingPartyContactPurposeList", helper.findByAnd("PartyContactMechPurpose", UtilMisc.toMap(
        "partyId", userLogin.getString("partyId"),
        "contactMechPurposeTypeId", "BILLING_LOCATION"), null)); %>  

 <ofbiz:if name="billingPartyContactPurposeList" size="0">
  <table width="90%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="100%" colspan="2" height="1" bgcolor="888888"></td>
    </tr>
    <ofbiz:iterator name="billingPartyContactPurpose" property="billingPartyContactPurposeList">
    <%GenericValue billingLocation = billingPartyContactPurpose.getRelatedOne("PartyContactMech")
            .getRelatedOne("ContactMech").getRelatedOne("PostalAddress");
      pageContext.setAttribute("billingLocation", billingLocation);%>
    <ofbiz:if name="billingLocation">
    <tr>
      <td align="left" valign="top" width="1%" nowrap>
          <input type="radio" name="PAYMENT_CODE" value="billing:<%=billingLocation.get("contactMechId")%>">
      </td>
      <td align="left" valign="top" width="99%" nowrap>
        <div class="tabletext">
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("toName"), "<b>To:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("address1"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("address2"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("city"), "", "<br>")%>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(billingLocation.getString("postalCode"))%><br>
        <%=UtilFormatOut.ifNotEmpty(billingLocation.getString("countryGeoId"), "", "<br>")%>
<%--          <a href="<ofbiz:url>/profileeditaddress?DONE_PAGE=checkoutoptions.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=billingLocation.getAddressId()%></ofbiz:url>" class="buttonlink">[Update]</a>
          <a href="<ofbiz:url>/checkoutoptions?<%="event"%>=<%=EventConstants.DELETE_SHIPPING_LOCATION%>&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=billingLocation.getAddressId()%></ofbiz:url>" class="buttonlink">[Delete]</a>--%>
        </div>
      </td>
    </tr>
    <tr>
      <td width="100%" colspan="2" height="1" bgcolor="888888"></td>
    </tr>
    </ofbiz:if>
  </ofbiz:iterator>
  </table>
 </ofbiz:if>
  <a href="<ofbiz:url>/editcontactmech?CONTACT_MECH_TYPE_ID=POSTAL_ADDRESS&CM_NEW_PURPOSE_TYPE_ID=BILLING_LOCATION&DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttonlink">[Add New Address]</a>


</td>
</tr>
</table>
</form>

<table width="100%">
<tr valign="top">
<td align="left">
&nbsp;<a href="<ofbiz:url>/shoppingcart</ofbiz:url>" class="buttonlinkbig">[Back to Shopping Cart]</a>
</td>
<td align="right">
<a href="javascript:document.checkoutInfoForm.submit()" class="buttonlinkbig">[Continue to Final Order Review]</a>
</td>
</tr>
</table>
<%@ include file="/includes/footer.jsp" %>





