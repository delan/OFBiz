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
 *@author     Andy Zeneski
 *@created    May 22 2001
 *@version    1.0
 */
%>
<br>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td width='50%' valign=top align=left>

<%
    GenericValue currentStatus = orderHeader.getRelatedOneCache("StatusItem");
    if (currentStatus != null) pageContext.setAttribute("currentStatus", currentStatus);
    List orderHeaderStatuses = orderReadHelper.getOrderHeaderStatuses();
    if (orderHeaderStatuses != null) pageContext.setAttribute("orderHeaderStatuses", orderHeaderStatuses);
%>
<%String partyId = orderRole.getString("partyId");%>
<%GenericValue userPerson = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",orderRole.getString("partyId")));%>    
<%String distributorId = orderReadHelper != null ? orderReadHelper.getDistributorId() : null;%>
<%if (distributorId != null) pageContext.setAttribute("distributorId", distributorId);%>
<%if (billingAccount != null) pageContext.setAttribute("billingAccount", billingAccount);%>
<%--if (billingAddress != null) pageContext.setAttribute("billingAddress", billingAddress);--%>
<%if (shippingAddress != null) pageContext.setAttribute("shippingAddress", shippingAddress);%>
<%if (maySplit != null) pageContext.setAttribute("maySplit", maySplit);%>
<%if (isGift != null) pageContext.setAttribute("isGift", isGift);%>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Order <ofbiz:if name="orderHeader">#<%=orderHeader.getString("orderId")%> </ofbiz:if>Information</div>
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
              <table width="100%" border="0" cellpadding="1" cellspacing='0'>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Name</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                    <div class="tabletext">
                    <%if(userPerson!=null){%>
                      <%=PartyHelper.getPersonName(userPerson)%>
                    <%}%>        
                    </div>
                  </td>
                </tr>
                <%if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session)) {%>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Change Status</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <ofbiz:if name="orderHeader">
                            <form name="statusUpdate" method="get" action="<ofbiz:url>/changeOrderStatus</ofbiz:url>">
                               <input type="hidden" name="orderId" value="<%=orderHeader.getString("orderId")%>">        
                               <select name="statusId">
                                 <option value="<%=orderHeader.getString("statusId")%>"><%=orderHeader.getString("statusId")%></option>
                                 <option value="<%=orderHeader.getString("statusId")%>">----</option>
                                 <ofbiz:iterator name="status" property="statusChange">
                                   <option value="<%=status.getString("statusIdTo")%>"><%=status.getString("statusIdTo")%></option>               
                                 </ofbiz:iterator>
                               </select>
                               <a href="javascript:document.statusUpdate.submit();" class="buttontext">[Save]</a>
                              <%--<div class="tabletext"><%=orderReadHelper.getStatusString()%></div>--%>
                            </form>
                        </ofbiz:if>
                        <ofbiz:unless name="orderHeader">
                          <div class="tabletext"><b>Not Yet Ordered</b></div>
                        </ofbiz:unless>
                      </td>
                    </tr>
                <%}%>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Status History</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                    <div class='tabletext'>Current Status: <ofbiz:entityfield attribute="currentStatus" field="description"/> [<ofbiz:entityfield attribute="orderHeader" field="statusId"/>]</div>
                    <ofbiz:iterator name="orderHeaderStatus" property="orderHeaderStatuses">
                        <%GenericValue loopStatusItem = orderHeaderStatus.getRelatedOneCache("StatusItem");%>
                        <%if (loopStatusItem != null) pageContext.setAttribute("loopStatusItem", loopStatusItem);%>
                        <div class='tabletext'>
                            <ofbiz:entityfield attribute="orderHeaderStatus" field="statusDatetime"/>: 
                            <ofbiz:entityfield attribute="loopStatusItem" field="description"/>
                            [<ofbiz:entityfield attribute="orderHeaderStatus" field="statusId"/>]
                        </div>
                    </ofbiz:iterator>
                  </td>
                </tr>
              <ofbiz:if name="orderHeader">
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td align="right" valign="top" width="15%">
                      <div class="tabletext">&nbsp;<b>Date Ordered</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext">
                          <ofbiz:entityfield attribute="orderHeader" field="orderDate"/>
                      </div>
                  </td>
                </tr>
              </ofbiz:if>
              <ofbiz:if name="distributorId">
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Distributor</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext"><%=PartyHelper.formatPartyId(distributorId, delegator)%></div>
                  </td>
                </tr>
              </ofbiz:if>
              </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

    <br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Payment Information</div>
          </td>
          <td valign="middle" align="right">
            <%--<a href="javascript:void(0);" class="lightbuttontext">[Edit]</a>--%>
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
              <table width="100%" border="0" cellpadding="1" cellspacing='0'>
              <ofbiz:iterator name="orderPaymentPreference" property="orderPaymentPreferences">
                  <ofbiz:if name="outputted">
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                  </ofbiz:if>
                  <%pageContext.setAttribute("outputted", "true");%>
                  
                  <%-- try the paymentMethod first; if paymentMethodId is specified it overrides paymentMethodTypeId --%>
                  <%GenericValue paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");%>
                  <%if (paymentMethod != null) pageContext.setAttribute("paymentMethod", paymentMethod);%>
                  <ofbiz:unless name="paymentMethod">
                    <%GenericValue paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType");%>
                    <%if (paymentMethodType != null) pageContext.setAttribute("paymentMethodType", paymentMethodType);%>
                    <tr>
                      <td colspan="2" valign="top">
                        <div class="tabletext">&nbsp;<b><%EntityField.run("paymentMethodType", "description", pageContext);%></b> [<%EntityField.run("orderPaymentPreference", "paymentMethodTypeId", pageContext);%>]</div>
                      </td>
                    </tr>
                  </ofbiz:unless>
                  <ofbiz:if name="paymentMethod"> 
                    <%pageContext.setAttribute("outputted", "true");%>
                    <%if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
                        <%GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");%>
                        <%if (creditCard != null) {%>
                            <%pageContext.setAttribute("creditCard", creditCard);%>
                            <%GenericValue pmBillingAddress = creditCard.getRelatedOne("PostalAddress");%>
                            <%if (pmBillingAddress != null) pageContext.setAttribute("pmBillingAddress", pmBillingAddress);%>
                        <%}%>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;<b>Credit Card</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                              <div class="tabletext">
                                  <%EntityField.run("creditCard", "nameOnCard", pageContext);%><br>
                                  <%EntityField.run("creditCard", "companyNameOnCard", "", "<br>", pageContext);%>
                                  <%if (security.hasEntityPermission("PAY_INFO", "_VIEW", session)) {%>
                                      <%EntityField.run("creditCard", "cardType", pageContext);%>
                                      <%EntityField.run("creditCard", "cardNumber", pageContext);%>
                                      <%EntityField.run("creditCard", "expireDate", pageContext);%>
                                  <%} else {%>
                                    <%=ContactHelper.formatCreditCard(creditCard)%>
                                  <%}%>
                              </div>
                          </td>
                        </tr>
                    <%} else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
                        <%GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");%>
                        <%if (eftAccount != null) {%>
                            <%pageContext.setAttribute("eftAccount", eftAccount);%>
                            <%GenericValue pmBillingAddress = eftAccount.getRelatedOne("PostalAddress");%>
                            <%if (pmBillingAddress != null) pageContext.setAttribute("pmBillingAddress", pmBillingAddress);%>
                        <%}%>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;<b>EFT Account</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                              <div class="tabletext">
                                  <%EntityField.run("eftAccount", "nameOnAccount", pageContext);%> 
                                  <%EntityField.run("eftAccount", "companyNameOnAccount", "<br>", "", pageContext);%> 
                                  <%EntityField.run("eftAccount", "bankName", "<br>Bank: ", "", pageContext);%>
                                  <%EntityField.run("eftAccount", "routingNumber", ", ", "", pageContext);%>
                                  <%EntityField.run("eftAccount", "accountNumber", "<br>Account #: ", "", pageContext);%>
                              </div>
                          </td>
                        </tr>
                    <%}%>
                  </ofbiz:if>
                  <ofbiz:if name="pmBillingAddress">
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Address</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                              <%EntityField.run("pmBillingAddress", "toName", "<b>To:</b> ", "<br>", pageContext);%> 
                              <%EntityField.run("pmBillingAddress", "attnName", "<b>Attn:</b> ", "<br>", pageContext);%> 
                              <%EntityField.run("pmBillingAddress", "address1", pageContext);%><br>
                              <%EntityField.run("pmBillingAddress", "address2", "", "<br>", pageContext);%> 
                              <%EntityField.run("pmBillingAddress", "city", "", ", ", pageContext);%> 
                              <%EntityField.run("pmBillingAddress", "stateProvinceGeoId", pageContext);%>&nbsp;<%EntityField.run("pmBillingAddress", "postalCode", pageContext);%><br>
                              <%EntityField.run("pmBillingAddress", "countryGeoId", "", "", pageContext);%> 
                          </div>
                      </td>
                    </tr>
                  </ofbiz:if>
              </ofbiz:iterator>
              
              <%-- billing account --%>
              <ofbiz:if name="billingAccount">
                <ofbiz:if name="outputted">
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                </ofbiz:if>
                <%pageContext.setAttribute("outputted", "true");%>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Billing Account</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext">
                        #<%=billingAccount.getString("billingAccountId")%> - <%=UtilFormatOut.checkNull(billingAccount.getString("description"))%>
                      </div>
                  </td>
                </tr>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
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
              <%-- <ofbiz:if name="billingAddress">
                <ofbiz:if name="outputted">
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                </ofbiz:if>
                <%pageContext.setAttribute("outputted", "true");%>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Billing Address</b></div>
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
              </ofbiz:if> --%>
              </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

</td>
<td bgcolor="white" width="1">&nbsp;&nbsp;</td>
<td width='50%' valign=top align=left>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shipping Information</div>
          </td>
          <td valign="middle" align="right">
            <%--<a href="javascript:void(0);" class="lightbuttontext">[Edit]</a>--%>
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
              <table width="100%" border="0" cellpadding="1" cellspacing='0'>
                <ofbiz:if name="shippingAddress">
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Destination</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext">
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("toName"), "<b>To:</b> ", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("attnName"), "<b>Attn:</b> ", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address1"), "", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("address2"), "", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("city"), "", "<br>")%>
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("stateProvinceGeoId"), "", "&nbsp;")%> <%=UtilFormatOut.checkNull(shippingAddress.getString("postalCode"))%><br>
                      <%=UtilFormatOut.ifNotEmpty(shippingAddress.getString("countryGeoId"), "", "<br>")%>
                      </div>
                  </td>
                </tr>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                </ofbiz:if>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Method</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext">
                      <%=UtilFormatOut.checkNull(carrierPartyId)%> 
                      <%String shipMethDescription = "";%>
                      <%GenericValue shipmentMethodType = delegator.findByPrimaryKey("ShipmentMethodType", UtilMisc.toMap("shipmentMethodTypeId", shipmentMethodTypeId));%>
                      <%if(shipmentMethodType != null) shipMethDescription = shipmentMethodType.getString("description");%>
                      <%=UtilFormatOut.checkNull(shipMethDescription)%>
                      <%--=UtilFormatOut.ifNotEmpty(shippingAccount, "<br>Use Account: ", "")--%>
                      </div>
                  </td>
                </tr>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Splitting Preference</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext">
                      <ofbiz:unless name="maySplit">Please wait until the entire order is ready before shipping.</ofbiz:unless>
                      <ofbiz:if name="maySplit">Please ship items I ordered as they become available (may incur additional shipping charges).</ofbiz:if>
                      </div>
                  </td>
                </tr>
                <%if (UtilValidate.isNotEmpty(shippingInstructions)) {%>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Instructions</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                          <div class="tabletext"><%=UtilFormatOut.checkNull(shippingInstructions)%></div>
                       </td>
                    </tr>
                <%}%>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Gift?</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext">
                      <ofbiz:unless name="isGift">This order is not a gift.</ofbiz:unless>
                      <ofbiz:if name="isGift">This order is a gift.</ofbiz:if>
                      </div>
                  </td>
                </tr>
                <%if (UtilValidate.isNotEmpty(giftMessage)) {%>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Gift Message</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                          <div class="tabletext"><%=UtilFormatOut.checkNull(giftMessage)%></div>
                       </td>
                    </tr>
                <%}%>
              </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
  </td>
 </tr>
</table>
