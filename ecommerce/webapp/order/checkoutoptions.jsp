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
 *@author     Andy Zeneski
 *@created    May 22 2001
 *@version    1.0
 */
%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<ofbiz:object name="userLogin" property="userLogin" type="org.ofbiz.core.entity.GenericValue" />  
<%ShoppingCart cart = (ShoppingCart) session.getAttribute(SiteDefs.SHOPPING_CART);%>
<%if ((cart != null) && (cart.size() > 0)) pageContext.setAttribute("cart", cart);%>
<ofbiz:if name="cart">
    <%if (cart.getMaySplit() != null) pageContext.setAttribute("maySplit", cart.getMaySplit());%>
</ofbiz:if>
<%GenericValue party = userLogin.getRelatedOne("Party");%>
<%pageContext.setAttribute("carrierShipmentMethodList", delegator.findAllCache("CarrierShipmentMethod", null)); %>
<%pageContext.setAttribute("shippingContactMechList", ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false)); %>  
<%pageContext.setAttribute("paymentMethodList", EntityUtil.filterByDate(party.getRelated("PaymentMethod"))); %>
<%pageContext.setAttribute("emailList",  ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));%>
<%pageContext.setAttribute("billingAccountRoleList", delegator.findByAnd("BillingAccountRole", UtilMisc.toMap(
        "partyId", userLogin.getString("partyId"),
        "roleTypeId", "BILL_TO_CUSTOMER"), null)); %>  

<BR>
<form method="post" name="checkoutInfoForm" action="<ofbiz:url>/checkout</ofbiz:url>" style='margin:0;'>
<table width="100%" border="0" cellpadding='0' cellspacing='0'>
  <tr valign="top" align="left">
    <td height='100%'>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead">1)&nbsp;How&nbsp;shall&nbsp;we&nbsp;ship&nbsp;it?</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR style='height: 100%;'>
    <TD width='100%' valign=top height='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
        <tr>
          <td>
  <table width="100%" cellpadding="1" border="0" cellpadding="0" cellspacing="0">
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
        <%String shipMethDescription = ""; String shipParty = "";%>
        <%GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType");%>
        <%if(shipmentMethodType != null) shipMethDescription = shipmentMethodType.getString("description");%>
		<%if(!carrierShipmentMethod.getString("partyId").equals("_NA_")) shipParty = carrierShipmentMethod.getString("partyId");%>
        <div class="tabletext"><%=UtilFormatOut.checkNull(shipParty) + " " + UtilFormatOut.checkNull(shipMethDescription)%></div>
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
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside' style='height: 100%;'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead">2)&nbsp;Where&nbsp;shall&nbsp;we&nbsp;ship&nbsp;it?</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR style='height: 100%;'>
    <TD width='100%' valign=top height='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
        <tr>
          <td valign=top>

<table width="100%" border="0" cellpadding="1" cellspacing="0">
  <tr><td colspan="2"><a href="<ofbiz:url>/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">[Add New Address]</a></td></tr>
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
            <ofbiz:entityfield attribute="shippingAddress" field="toName" prefix="<b>To:</b> " suffix="<br>"/>
            <ofbiz:entityfield attribute="shippingAddress" field="attnName" prefix="<b>Attn:</b> " suffix="<br>"/>
            <ofbiz:entityfield attribute="shippingAddress" field="address1"/><br>
            <ofbiz:entityfield attribute="shippingAddress" field="address2" prefix="" suffix="<br>"/>
            <ofbiz:entityfield attribute="shippingAddress" field="city"/>,
            <ofbiz:entityfield attribute="shippingAddress" field="stateProvinceGeoId"/>
            <ofbiz:entityfield attribute="shippingAddress" field="postalCode"/>
            <ofbiz:entityfield attribute="shippingAddress" field="countryGeoId" prefix="<br>" suffix=""/>
            <%--<a href="<ofbiz:url>/profileeditaddress?DONE_PAGE=checkoutoptions.jsp&<%=HttpRequestConstants.ADDRESS_KEY%>=<%=billingLocation.getAddressId()%></ofbiz:url>" class="buttontext">[Update]</a>
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
<td height='100%'>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside' style='height: 100%;'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=left>
            <div class="boxhead">3)&nbsp;How&nbsp;shall&nbsp;you&nbsp;pay?</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR style='height: 100%;'>
    <TD width='100%' valign=top height='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom' style='height: 100%;'>
        <tr>
          <td valign=top>

<table width="100%" cellpadding="1" cellspacing="0" border="0">
  <tr><td colspan="2">
    <span class='tabletext'>Add:</span>
    <a href="<ofbiz:url>/editcreditcard?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">[Credit Card]</a>
    <a href="<ofbiz:url>/editeftaccount?DONE_PAGE=checkoutoptions</ofbiz:url>" class="buttontext">[EFT Account]</a>
  </td></tr>
 <ofbiz:if name="paymentMethodList" size="0"> 
  <tr><td colspan="2"><hr class='sepbar'></td></tr>
  <ofbiz:iterator name="paymentMethod" property="paymentMethodList">
      <%if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
          <%GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");%>
          <tr>
            <td width="1%" nowrap>
              <%String paymentMethodId = paymentMethod.getString("paymentMethodId");%>
              <input type="radio" name="paymentMethodId" value="<%=paymentMethodId%>"
              <ofbiz:if name="cart"><%=paymentMethodId.equals(cart.getPaymentMethodId()) ? "CHECKED" : ""%></ofbiz:if>>
            </td>
            <td width="50%" nowrap>
              <span class="tabletext">CC:&nbsp;<%=ContactHelper.formatCreditCard(creditCard)%></span>
              <a href="<ofbiz:url>/editcreditcard?DONE_PAGE=checkoutoptions&paymentMethodId=<%=paymentMethod.getString("paymentMethodId")%></ofbiz:url>" class="buttontext">[Update]</a>
            </td>
          </tr>
      <%} else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
          <%GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");%>
          <%pageContext.setAttribute("eftAccount", eftAccount);%>
          <tr>
            <td width="1%" nowrap>
              <%String paymentMethodId = paymentMethod.getString("paymentMethodId");%>
              <input type="radio" name="paymentMethodId" value="<%=paymentMethodId%>"
              <ofbiz:if name="cart"><%=paymentMethodId.equals(cart.getPaymentMethodId()) ? "CHECKED" : ""%></ofbiz:if>>
            </td>
            <td width="50%" nowrap>
              <span class="tabletext">
                EFT:&nbsp;<%EntityField.run("eftAccount", "bankName", pageContext);%>
                <%EntityField.run("eftAccount", "accountNumber", ": ", "", pageContext);%>
              </span>
              <a href="<ofbiz:url>/editeftaccount?DONE_PAGE=checkoutoptions&paymentMethodId=<%=paymentMethod.getString("paymentMethodId")%></ofbiz:url>" class="buttontext">[Update]</a>
            </td>
          </tr>
      <%}%>
      <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </ofbiz:iterator>
 </ofbiz:if>
</table>
<ofbiz:unless name="paymentMethodList" size="0">
   <h4>There are no payment methods on file.</h4>
</ofbiz:unless>

 <ofbiz:if name="billingAccountRoleList" size="0">
    <div class="tabletext">To pay with store credit, enter your Purchase Order (PO) number here and select the billing account:</div>
    <input type="text" name="corresponding_po_id" size="20" value='<ofbiz:if name="cart"><%=UtilFormatOut.checkNull(cart.getPoNumber())%></ofbiz:if>'>
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
<%--
            <a href="<ofbiz:url>/profileeditaddress?DONE_PAGE=checkoutoptions?billing_account_id=<%=billingAccount.get("billingAccountId")%></ofbiz:url>" class="buttontext">[Update]</a>
            <a href="<ofbiz:url>/checkoutoptions?event=DELETE_SHIPPING_LOCATION&billing_account_id=<%=billingAccount.get("billingAccountId")%></ofbiz:url>" class="buttontext">[Delete]</a>
--%>
<%--    
    <%GenericValue billingLocation = billingAccountRole.getRelatedOne("BillingAccount").getRelatedOne("PostalAddress");
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
&nbsp;<a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="buttontextbig">[Back to Shopping Cart]</a>
</td>
<td align="right">
<a href="javascript:document.checkoutInfoForm.submit()" class="buttontextbig">[Continue to Final Order Review]</a>
</td>
</tr>
</table>
