<%
/**
 *  Title: Order Information
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
<%@ page import="org.ofbiz.commonapp.order.order.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.PartyHelper" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td width='45%' valign=top align=left>

  <ofbiz:if name="orderHeader">
    <table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
      <tr>
        <td bgcolor="#678475">
      <%GenericValue localOrderHeader = (GenericValue) pageContext.getAttribute("orderHeader");%>
      <%OrderReadHelper localOrder = new OrderReadHelper(localOrderHeader);%>

      <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="middle" align="left">
          <div class="boxhead">&nbsp;Order #<%=localOrderHeader.getString("orderId")%> Information</div>
        </td>
      </tr>
      </table>
        </td>
      </tr>
      <tr>
        <td bgcolor='white' colspan='2'>
            <table width="100%" border="0" cellpadding="1">
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Name</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                  <div class="tabletext">
                  <%GenericValue userPerson = userLogin.getRelatedOne("Person");%>
                  <%if(userPerson!=null){%>
                    <%=PartyHelper.getPersonName(userPerson)%>
                  <%}%>
                  <%=UtilFormatOut.ifNotEmpty(userLogin.getString("userLoginId"), " (", ")")%>
                  </div>
                </td>
              </tr>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Status</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <div class="tabletext"><%=localOrder.getStatusString()%></div>
                </td>
              </tr>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Date</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <div class="tabletext">
                    <%=UtilDateTime.toDateTimeString(localOrderHeader.getTimestamp("orderDate"))%>
                    </div>
                </td>
              </tr>
            </table>
        </td>
      </tr>
    </table>
    <br>
  </ofbiz:if>

    <table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
      <tr>
        <td bgcolor="#678475">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="middle" align="left">
          <div class="boxhead">&nbsp;Payment Information</div>
        </td>
      </tr>
      </table>
        </td>
      </tr>
      <tr>
        <td bgcolor='white' colspan='2'>
            <table width="100%" border="0" cellpadding="1">
<ofbiz:if name="creditCardInfo"> 
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Credit Card</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <div class="tabletext">
                      <%=creditCardInfo.getString("nameOnCard")%><br>
                      <%=ContactHelper.formatCreditCard(creditCardInfo)%>
                    </div>
                </td>
              </tr>
  <ofbiz:if name="billingAddress">
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
  </ofbiz:if>
</ofbiz:if>
<ofbiz:if name="billingAddress">
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Billing Account</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <div class="tabletext">
                      <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("toName"), "<b>To:</b> ", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
                      <%=UtilFormatOut.checkNull(billingAddress.getString("address1"))%><br>
                      <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("address2"),  "", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("city"), "", "<br>")%>
                      <%=UtilFormatOut.checkNull(billingAddress.getString("stateProvinceGeoId"))%> &nbsp; <%=UtilFormatOut.checkNull(billingAddress.getString("postalCode"))%><br>
                      <%=UtilFormatOut.ifNotEmpty(billingAddress.getString("countryGeoId"), "", "")%>
                    </div>
                </td>
              </tr>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Purchase Order Number</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <div class="tabletext"><%=UtilFormatOut.checkNull(customerPoNumber)%></div>
                </td>
              </tr>
</ofbiz:if>
            </table>
        </td>
      </tr>
    </table>

</td>
<td width='5'>&nbsp;&nbsp;&nbsp;</td>
<td width='45%' valign=top align=left>


    <table width="100%" border="0" bgcolor="black" cellpadding="4" cellspacing="1">
      <tr>
        <td bgcolor="#678475">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="middle" align="left">
          <div class="boxhead">&nbsp;Shipping Information</div>
        </td>
      </tr>
      </table>
        </td>
      </tr>
      <tr>
        <td bgcolor='white' colspan='2'>
            <table width="100%" border="0" cellpadding="1">
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Destination</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("toName"), "<b>To:</b> ", "<br>")%>
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address1"), "", "<br>")%>
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address2"), "", "<br>")%>
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("city"), "", "<br>")%>
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
                    <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("countryGeoId"), "", "<br>")%>
                </td>
              </tr>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Method</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                  <%=UtilFormatOut.checkNull(carrierPartyId)%> 
                  <%=UtilFormatOut.checkNull(helper.findByPrimaryKey("ShipmentMethodType", UtilMisc.toMap("shipmentMethodTypeId", shipmentMethodTypeId)).getString("description"))%>
                  <%--=UtilFormatOut.ifNotEmpty(shippingAccount, "<br>Use Account: ", "")--%>
                </td>
              </tr>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Splitting Preference</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                    <div class="tabletext">
                    <ofbiz:if name="maySplit" value="false" type="Boolean">
                    Please wait until the entire order is ready before shipping.
                    </ofbiz:if>
                    <ofbiz:if name="maySplit" value="true" type="Boolean">
                    Please ship items I ordered as they become available (may incur additional shipping charges).    
                    </ofbiz:if>
                    </div>
                </td>
              </tr>
              <tr><td colspan="7" height="1" bgcolor="#899ABC"></td></tr>
              <tr>
                <td align="right" valign="top" width="15%">
                  <div class="tabletext">&nbsp;<b>Instructions</b></div>
                </td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="80%">
                <%=UtilFormatOut.checkNull(shippingInstructions)%>
                 </td>
              </tr>
            </table>
        </td>
      </tr>
    </table>
  </td>
 </tr>
</table>
