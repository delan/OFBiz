<%
    /**
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
     * @author     Andy Zeneski
     * @version    $Revision$
     * @since      2.0
     */
%>

<%EntityField entityField = new EntityField(pageContext);%>

<br>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td width='50%' valign=top align=left>

<%
    GenericValue currentStatus = orderHeader.getRelatedOneCache("StatusItem");
    if (currentStatus != null) pageContext.setAttribute("currentStatus", currentStatus);
    List orderHeaderStatuses = orderReadHelper.getOrderHeaderStatuses();
    if (orderHeaderStatuses != null) pageContext.setAttribute("orderHeaderStatuses", orderHeaderStatuses);

    String partyId = orderRole.getString("partyId");
    GenericValue userPerson = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",orderRole.getString("partyId")));
    String distributorId = orderReadHelper != null ? orderReadHelper.getDistributorId() : null;
    String affiliateId = orderReadHelper != null ? orderReadHelper.getAffiliateId() : null;
    if (distributorId != null) pageContext.setAttribute("distributorId", distributorId);
    if (billingAccount != null) pageContext.setAttribute("billingAccount", billingAccount);
    //if (billingAddress != null) pageContext.setAttribute("billingAddress", billingAddress);
    if (shippingAddress != null) pageContext.setAttribute("shippingAddress", shippingAddress);
    if (maySplit != null) pageContext.setAttribute("maySplit", maySplit);
    if (isGift != null) pageContext.setAttribute("isGift", isGift);
%>

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
                <%--
                <%if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session)) {%>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Change Status</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <ofbiz:if name="orderHeader">
                            <form name="statusUpdate" method="get" action="<ofbiz:url>/changeOrderStatus</ofbiz:url>">
                               <input type="hidden" name="orderId" value="<%=orderHeader.getString("orderId")%>">        
                               <select name="statusId" style="font-size: x-small;">
                                 <option value="<%=orderHeader.getString("statusId")%>"><%=currentStatus == null ? orderHeader.getString("statusId") : currentStatus.getString("description")%></option>
                                 <option value="<%=orderHeader.getString("statusId")%>">----</option>
                                 <ofbiz:iterator name="status" property="statusChange">
                                   <%GenericValue changeStatusItem = status.getRelatedOneCache("ToStatusItem");%>
                                   <option value="<%=status.getString("statusIdTo")%>"><%=changeStatusItem == null ? status.getString("statusIdTo") : changeStatusItem.getString("description")%></option>               
                                 </ofbiz:iterator>
                               </select>
                               <a href="javascript:document.statusUpdate.submit();" class="buttontext">[Save]</a>
                            </form>
                        </ofbiz:if>
                        <ofbiz:unless name="orderHeader">
                          <div class="tabletext"><b>Not Yet Ordered</b></div>
                        </ofbiz:unless>
                      </td>
                    </tr>
                <%}%>
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                --%>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Status History</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                    <div class='tabletext'>Current Status: <ofbiz:entityfield attribute="currentStatus" field="description"/><%-- [<ofbiz:entityfield attribute="orderHeader" field="statusId"/>]--%></div>
                    <ofbiz:if name="orderHeaderStatuses">
                      <hr class="sepbar">
                    </ofbiz:if>
                    <ofbiz:iterator name="orderHeaderStatus" property="orderHeaderStatuses">
                        <%GenericValue loopStatusItem = orderHeaderStatus.getRelatedOneCache("StatusItem");%>
                        <%if (loopStatusItem != null) pageContext.setAttribute("loopStatusItem", loopStatusItem);%>
                        <div class='tabletext'>
                            <ofbiz:entityfield attribute="loopStatusItem" field="description"/>
                            <%--[<ofbiz:entityfield attribute="orderHeaderStatus" field="statusId"/>]--%> -
                            <ofbiz:entityfield attribute="orderHeaderStatus" field="statusDatetime"/>
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
              <ofbiz:if name="affiliateId">
                <tr><td colspan="7"><hr class='sepbar'></td></tr>
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Affiliate</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                      <div class="tabletext"><%=PartyHelper.formatPartyId(affiliateId, delegator)%></div>
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
                    <%GenericValue paymentMethodType = orderPaymentPreference.getRelatedOneCache("PaymentMethodType");%>
                    <%if (paymentMethodType != null) pageContext.setAttribute("paymentMethodType", paymentMethodType);%>
                    <tr>
                      <td>
                        <div class="tabletext">&nbsp;<b><%=UtilFormatOut.checkNull(paymentMethodType.getString("description"))%></b></div>
                      </td>
                      <% if (!paymentMethodType.getString("paymentMethodTypeId").equals("EXT_OFFLINE")) { %>
                      <td align="right">
                        <div class="tabletext"><%=UtilFormatOut.formatPrice(orderPaymentPreference.getDouble("maxAmount"))%><%=UtilFormatOut.ifNotEmpty(UtilFormatOut.formatDate(orderPaymentPreference.getTimestamp("authDate")), "&nbsp;-&nbsp;", "")%></div>
                        <div class="tabletext">&nbsp;<%if (orderPaymentPreference.getString("authRefNum") != null) { %>(Ref: <%=UtilFormatOut.checkNull(orderPaymentPreference.getString("authRefNum"))%>)<%}%></div>
                      </td>
                      <% } else { %>
                      <td align="right">
                        <% String rpqstr = "order_id=" + orderId + "&workEffortId=" + workEffortId + "&partyId=" + assignPartyId + "&roleTypeId=" + assignRoleTypeId + "&fromDate=" + fromDate; %>
                        <a valign="top" href="<ofbiz:url>/receivepayment?<%=rpqstr%></ofbiz:url>" class="buttontext">[Receive Payment]</a>
                      </td>
                      <% } %>
                    </tr>
                  </ofbiz:unless>
                  <ofbiz:if name="paymentMethod"> 
                    <%pageContext.setAttribute("outputted", "true");%>
                    <%if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {%>
                        <%GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");%>
                        <%GenericValue payment = EntityUtil.getFirst(delegator.findByAnd("Payment", UtilMisc.toMap("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"))));%>
                        <%if (payment != null) pageContext.setAttribute("payment", payment);%>
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
                              <%GenericValue oppStatusItem = orderPaymentPreference.getRelatedOneCache("StatusItem");%>
                              <%if (oppStatusItem != null) pageContext.setAttribute("oppStatusItem", oppStatusItem);%>
                              <div class="tabletext">
                                  <%EntityField.run("creditCard", "nameOnCard", pageContext);%><br>
                                  <%EntityField.run("creditCard", "companyNameOnCard", "", "<br>", pageContext);%>
                                  <%if (security.hasEntityPermission("PAY_INFO", "_VIEW", session)) {%>
                                      <%EntityField.run("creditCard", "cardType", pageContext);%>
                                      <%EntityField.run("creditCard", "cardNumber", pageContext);%>
                                      <%EntityField.run("creditCard", "expireDate", pageContext);%>
                                      &nbsp;[<%if (oppStatusItem != null) { EntityField.run("oppStatusItem", "description", pageContext); } else { EntityField.run("orderPaymentPreference", "statusId", pageContext); }%>]
                                  <%} else {%>
                                    <%=ContactHelper.formatCreditCard(creditCard)%>
                                  <%}%>
                              </div>
                              <div class="tabletext">
                                <%if (orderPaymentPreference.get("authDate") != null) {%>
                                  Authorized&nbsp;:&nbsp;<%EntityField.run("orderPaymentPreference", "authDate", pageContext);%>
                                  &nbsp;&nbsp;(<b>Ref:</b>&nbsp;<%EntityField.run("orderPaymentPreference", "authRefNum", pageContext);%>)
                                <%}%>
                                <%if (payment != null && payment.get("effectiveDate") != null) {%>
                                  Billed&nbsp;:&nbsp;<%EntityField.run("payment", "effectiveDate", pageContext);%>
                                  &nbsp;&nbsp;(<b>Ref:</b>&nbsp;<%EntityField.run("payment", "paymentRefNum", pageContext);%>)
                                <%}%>

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
                    <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="5"><hr class='sepbar'></td></tr>
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
            <div class="boxhead">&nbsp;Contact Information</div>
          </td>
          <td valign="middle" align="right">
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
                <tr>
                  <td align="right" valign="top" width="15%">
                    <div class="tabletext">&nbsp;<b>Name</b></div>
                  </td>
                  <td width="5">&nbsp;</td>
                  <td NOWRAP align="left" valign="top" width="80%">
                    <div class="tabletext">
                    <%if(userPerson!=null){%>
                      <%=PartyHelper.getPersonName(userPerson)%>
                    <%}%>
                    &nbsp;(<a href="/partymgr/control/viewprofile?party_id=<%=partyId%>" target='partymgr' class="buttontext"><%=partyId%></a>)
                    </div>
                  </td>
                </tr>
                <ofbiz:iterator name="orderContactMechValueMap" property="orderContactMechValueMaps" type="java.util.Map" expandMap="true">
                  <%GenericValue contactMech = (GenericValue) pageContext.getAttribute("contactMech");%>
                  <%GenericValue contactMechPurpose = (GenericValue) pageContext.getAttribute("contactMechPurposeType");%>
                  <tr><td colspan="7"><hr class='sepbar'></td></tr>
                  <tr>
                    <td align="right" valign="top" width="15%">
                      <div class="tabletext">&nbsp;<b><%=UtilFormatOut.checkNull(contactMechPurpose.getString("description"))%></b></div>
                    </td>
                    <td width="5">&nbsp;</td>
                    <td align="left" valign="top" width="80%">
                      <div class="tabletext">

              <%if ("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("postalAddress", "toName", "<b>To:</b> ", "<br>");%>
                    <%entityField.run("postalAddress", "attnName", "<b>Attn:</b> ", "<br>");%>
                    <%entityField.run("postalAddress", "address1");%><br>
                    <%entityField.run("postalAddress", "address2", "", "<br>");%>
                    <%entityField.run("postalAddress", "city");%>,
                    <%entityField.run("postalAddress", "stateProvinceGeoId");%>
                    <%entityField.run("postalAddress", "postalCode");%>
                    <%entityField.run("postalAddress", "countryGeoId", "<br>", "");%>
                  </div>
                  <%GenericValue postalAddress = (GenericValue) pageContext.getAttribute("postalAddress");%>
                  <%if (postalAddress != null && (UtilValidate.isEmpty(postalAddress.getString("countryGeoId")) || postalAddress.getString("countryGeoId").equals("USA"))) {%>
                    <%String addr1 = UtilFormatOut.checkNull(postalAddress.getString("address1"));%>
                    <%if (addr1.indexOf(' ') > 0) {%>
                      <%String addressNum = addr1.substring(0, addr1.indexOf(' '));%>
                      <%String addressOther = addr1.substring(addr1.indexOf(' ')+1);%>
                      <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=<%=addressNum%>&s_a=<%=addressOther%>&c=<%=UtilFormatOut.checkNull(postalAddress.getString("city"))%>&s=<%=UtilFormatOut.checkNull(postalAddress.getString("stateProvinceGeoId"))%>&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                    <%}%>
                  <%}%>
              <%} else if ("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("telecomNumber", "countryCode");%>
                    <%entityField.run("telecomNumber", "areaCode", "", "-");%><%entityField.run("telecomNumber", "contactNumber");%>
                    <%entityField.run("partyContactMech", "extension", "ext&nbsp;", "");%>
                    <%GenericValue telecomNumber = (GenericValue) pageContext.getAttribute("telecomNumber");%>
                    <%if (telecomNumber != null && (UtilValidate.isEmpty(telecomNumber.getString("countryCode")) || telecomNumber.getString("countryCode").equals("011"))) {%>
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&telephone=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=<%=UtilFormatOut.checkNull(telecomNumber.getString("areaCode"))%>&s=&p=<%=UtilFormatOut.checkNull(telecomNumber.getString("contactNumber"))%>&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    <%}%>
                  </div>
              <%} else if ("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("contactMech", "infoString");%>
                    <a href='mailto:<%entityField.run("contactMech", "infoString");%>' class='buttontext'>(send&nbsp;email)</a>
                  </div>
              <%} else if ("WEB_ADDRESS".equals(contactMech.getString("contactMechTypeId"))) {%>
                  <div class="tabletext">
                    <%entityField.run("contactMech", "infoString");%>
                    <%String openAddress = UtilFormatOut.checkNull(contactMech.getString("infoString"));%>
                    <%if(!openAddress.startsWith("http") && !openAddress.startsWith("HTTP")) openAddress = "http://" + openAddress;%>
                    <a target='_blank' href='<%=openAddress%>' class='buttontext'>(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                  </div>
              <%} else {%>
                  <div class="tabletext">
                    <%entityField.run("contactMech", "infoString");%>
                  </div>
              <%}%>

                      </div>
                    </td>
                  </tr>
                </ofbiz:iterator>
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
            <div class="boxhead">&nbsp;Shipment Information</div>
          </td>
          <td valign="middle" align="right">
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

                <%if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session)) {%>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Tracking Number</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <form name="trackingNumberUpdate" method="post" action="<ofbiz:url>/updateTrackingNumber?<%=qString%></ofbiz:url>">
                           <input type="hidden" name="orderId" value="<%=orderHeader.getString("orderId")%>">
                           <input type="text" style="font-size: x-small;" name="trackingNumber" value="<%=UtilFormatOut.checkNull(trackingNumber)%>">
                           <a href="javascript:document.trackingNumberUpdate.submit();" class="buttontext">[Save]</a>
                        </form>
                      </td>
                    </tr>
                <%} else if (UtilValidate.isNotEmpty(trackingNumber)) {%>
                
                <%--<% if (UtilValidate.isNotEmpty(trackingNumber)) {%>--%>
                    <tr><td colspan="7"><hr class="sepbar"></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Tracking Number</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext"><%=UtilFormatOut.checkNull(trackingNumber)%></div>
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
