<%--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski 
 *@version    $Revision$
 *@since      2.0
--%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.order.shoppingcart.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.commonapp.party.contact.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="shoppingCart" type="org.ofbiz.commonapp.order.shoppingcart.ShoppingCart" scope="session"/>

<%if(security.hasEntityPermission("ORDERMGR", "_CREATE", session)) {%>

<%	
	String partyId = (String) session.getAttribute("orderPartyId");
	GenericValue party = null;

	if (partyId != null)
		party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
	
	if (party != null) 
		pageContext.setAttribute("party", party);

	String shipContactMechId = shoppingCart.getShippingContactMechId();

	List shipmentMethodList = delegator.findAllCache("CarrierShipmentMethod", UtilMisc.toList("sequenceNumber")); 
	if (shipmentMethodList != null)
		pageContext.setAttribute("carrierShipmentMethodList", shipmentMethodList);

	String chosenShippingMethod = shoppingCart.getShipmentMethodTypeId() + '@' + shoppingCart.getCarrierPartyId();

	if (shoppingCart.getMaySplit() != null) pageContext.setAttribute("maySplit", shoppingCart.getMaySplit());
	if (shoppingCart.getIsGift() != null) pageContext.setAttribute("isGift", shoppingCart.getIsGift());
%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='70%' >
            <div class='boxhead'>&nbsp;Order Option Settings</div>
          </TD> 
          <TD align="right">
            <div class="tabletext">
              <a href="<ofbiz:url>/setOptions</ofbiz:url>" class="lightbuttontext">[Refresh]</a>
              <a href="javascript:document.optsetupform.submit();" class="lightbuttontext">[Continue]</a>
            </div>
          </TD>         
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>

<table width="100%" cellpadding="1" border="0" cellpadding="0" cellspacing="0">
<ofbiz:iterator name="carrierShipmentMethod" property="carrierShipmentMethodList">
    <tr>
      <td width='1%' valign="top" >
        <%String shippingMethod = carrierShipmentMethod.getString("shipmentMethodTypeId") + "@" + carrierShipmentMethod.getString("partyId");%>
        <input <ofbiz:if name="shoppingCart"><%=shippingMethod.equals(chosenShippingMethod) ? "CHECKED" : ""%></ofbiz:if>
          type="radio" name="shipping_method" value="<%=shippingMethod%>">
      </td>
      <td valign="center">
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
        <input <ofbiz:unless name="maySplit" value="true" type="Boolean">CHECKED</ofbiz:unless> type="radio" name="may_split" value="false">
      </td>
      <td valign="center">
        <div class="tabletext">Please wait until the entire order is ready before shipping.</div>
      </td>
    </tr>
    <tr>
      <td valign="top">
        <input <ofbiz:if name="maySplit" value="true" type="Boolean">CHECKED</ofbiz:if> type="radio" name="may_split" value="true">
      </td>
      <td valign="center">
        <div class="tabletext">Please ship items as they become available.</div>
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
        <textarea cols="30" rows="3" name="shipping_instructions"><ofbiz:if name="shoppingCart"><%=UtilFormatOut.checkNull(shoppingCart.getShippingInstructions())%></ofbiz:if></textarea>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div>
            <span class="head2"><b>Is This a Gift?</b></span>
            <input <ofbiz:if name="isGift" value="true" type="Boolean">CHECKED</ofbiz:if> type="radio" name="is_gift" value="true"><span class="tabletext">Yes</span>
            <input <ofbiz:unless name="isGift" value="true" type="Boolean">CHECKED</ofbiz:unless> type="radio" name="is_gift" value="false"><span class="tabletext">No</span>
        </div>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div class="head2"><b>Gift Message</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <textarea cols="30" rows="3" name="gift_message"><ofbiz:if name="shoppingCart"><%=UtilFormatOut.checkNull(shoppingCart.getGiftMessage())%></ofbiz:if></textarea>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
	<tr>
      <td colspan="2">
        <div class="head2"><b>Payment Information</b></div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <textarea cols="30" rows="3" name="gift_message"><ofbiz:if name="shoppingCart"><%=UtilFormatOut.checkNull(shoppingCart.getGiftMessage())%></ofbiz:if></textarea>
      </td>
    </tr>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_CREATE" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
