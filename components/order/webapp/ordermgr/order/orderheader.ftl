<#--
 *  Copyright (c) 2003-2004 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Jean-Luc.Malet@nereide.biz (migration to uiLabelMap)
 *@version    $Rev:$
 *@since      2.2
-->

<#if requestAttributes.uiLabelMap?exists>
  <#assign uiLabelMap = requestAttributes.uiLabelMap>
</#if>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="50%" valign="top" align="left">
      <#-- header box -->
      <table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <tr>
          <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${uiLabelMap.OrderOrder} #${orderId} ${uiLabelMap.CommonInformation}</div>
                </td>
                <td valign="middle" align="right">
                  <#if currentStatus.statusId == "ORDER_CREATED" || currentStatus.statusId == "ORDER_PROCESSING">
                    <div class="tabletext"><a href="<@ofbizUrl>/changeOrderItemStatus?orderId=${orderId}&statusId=ITEM_APPROVED&${paramString}</@ofbizUrl>" class="submenutextright">${uiLabelMap.OrderApproveOrder}</a></div>
                  </#if>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1" cellspacing="0">
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderStatusHistory}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">${uiLabelMap.OrderCurrentStatus}: ${currentStatus.description}</div>
                        <#if orderHeaderStatuses?has_content>
                          <hr class="sepbar">
                          <#list orderHeaderStatuses as orderHeaderStatus>
                            <#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem")>
                            <div class="tabletext">
                              ${loopStatusItem.description} - ${orderHeaderStatus.statusDatetime?default("0000-00-00 00:00:00")?string}
                            </div>
                          </#list>
                        </#if>
                      </td>
                    </tr>
                    <tr><td colspan="7"><hr class="sepbar"></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderDateOrdered}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${orderHeader.orderDate.toString()}
                        </div>
                      </td>
                    </tr>
                    <tr><td colspan="7"><hr class="sepbar"></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Currency</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${orderHeader.currencyUom?default("???")}
                        </div>
                      </td>
                    </tr>
                    <tr><td colspan="7"><hr class="sepbar"></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Created By</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if orderHeader.createdBy?has_content>
                            <a href="/partymgr/control/viewprofile?userlogin_id=${orderHeader.createdBy}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${orderHeader.createdBy}</a>
                          <#else>
                            ???
                          </#if>
                        </div>
                      </td>
                    </tr>
                    <#if distributorId?exists>
                    <tr><td colspan="7"><hr class="sepbar"></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderDistributor}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, distributorId, false)}
                        </div>
                      </td>
                    </tr>
                    </#if>
                    <#if affiliateId?exists>
                    <tr><td colspan="7"><hr class="sepbar"></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderAffiliate}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, affiliateId, false)}
                        </div>
                      </td>
                    </tr>
                    </#if>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <#-- end of header box -->
      <br>
      <#-- payment box -->
      <#if orderPaymentPreferences?has_content || billingAccount?has_content>
        <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
          <tr>
            <td width="100%">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                <tr>
                  <td valign="middle" align="left">
                    <div class="boxhead">&nbsp;${uiLabelMap.AccountingPaymentInformation}</div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td width="100%">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
                <tr>
                  <td>
                    <table width="100%" border="0" cellpadding="1" cellspacing="0">
                    <#list orderPaymentPreferences as orderPaymentPreference>
                      <#if outputted?default("false") == "true">
                        <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      </#if>
                      <#assign outputted = "true">
                      <#-- try the paymentMethod first; if paymentMethodId is specified it overrides paymentMethodTypeId -->
                      <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
                      <#if !paymentMethod?has_content>
                        <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")>
                        <#if paymentMethodType.paymentMethodTypeId == "EXT_BILLACT">
                          <#assign outputted = "false">
                        <#else>
                          <tr>
                            <td align="right" valign="top" width="15%">
                              <div class="tabletext">&nbsp;<b>${paymentMethodType.description?if_exists}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <#if paymentMethodType.paymentMethodTypeId != "EXT_OFFLINE">
                              <td align="left">
                                <div class="tabletext"><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/></div>
                                <#--
                                <div class="tabletext"><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>&nbsp;-&nbsp;${(orderPaymentPreference.authDate.toString())?if_exists}</div>
                                <div class="tabletext">&nbsp;<#if orderPaymentPreference.authRefNum?exists>(Ref: ${orderPaymentPreference.authRefNum})</#if></div>
                                -->
                             </td>
                            <#else>
                              <td align="right">
                                <a valign="top" href="<@ofbizUrl>/receivepayment?${paramString}</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingReceivePayment}</a>
                              </td>
                            </#if>
                          </tr>
                        </#if>
                      <#else>
                        <#if paymentMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
                          <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")?if_exists>
                          <#if creditCard?has_content>
                            <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress")?if_exists>
                          </#if>
                          <tr>
                            <td align="right" valign="top" width="15%">
                              <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingCreditCard}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                              <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                              <div class="tabletext">
                                <#if creditCard?has_content>
                                  <#if creditCard.companyNameOnCard?exists>${creditCard.companyNameOnCard}<br></#if>
                                  <#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}&nbsp</#if>
                                  ${creditCard.firstNameOnCard}&nbsp;
                                  <#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}&nbsp</#if>
                                  ${creditCard.lastNameOnCard}
                                  <#if creditCard.suffixOnCard?has_content>&nbsp;${creditCard.suffixOnCard}</#if>
                                  <br>                                  

                                  <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                    ${creditCard.cardType}
                                    ${creditCard.cardNumber}
                                    ${creditCard.expireDate}
                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.description}<#else>${orderPaymentPreference.statusId}</#if>]
                                  <#else>
                                    ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.description}<#else>${orderPaymentPreference.statusId}</#if>]
                                  </#if>
                                <#else>
                                  ${uiLabelMap.CommonInformationNotAvailable}
                                </#if>
                              </div>
                              <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                              <#if gatewayResponses?has_content>
                                <div class="tabletext">
                                  <hr />
                                  <#list gatewayResponses as gatewayResponse>
                                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                    ${(transactionCode.description)?default("Unknown")}:
                                    ${gatewayResponse.transactionDate.toString()}<br />
                                    (<b>Ref:</b> ${gatewayResponse.referenceNum?if_exists}
                                    <b>AVS:</b> ${gatewayResponse.gatewayAvsResult?default("N/A")}
                                    <b>Score:</b> ${gatewayResponse.gatewayScoreResult?default("N/A")})
                                    <#if gatewayResponse_has_next><hr /></#if>
                                  </#list>
                                </div>
                              </#if>                              
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
                          <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                          <#if eftAccount?has_content>
                            <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress")?if_exists>
                          </#if>
                          <tr>
                            <td align="right" valign="top" width="15%">
                              <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingEFTAccount}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                              <div class="tabletext">
                                <#if eftAccount?has_content>
                                  ${eftAccount.nameOnAccount?if_exists}<br>
                                  <#if eftAccount.companyNameOnAccount?exists>${eftAccount.companyNameOnAccount}<br></#if>
                                  Bank: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br>
                                  Account#: ${eftAccount.accountNumber}
                                <#else>
                                  ${uiLabelMap.CoomonInformationNotAvailable}
                                </#if>
                              </div>
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId?if_exists == "GIFT_CARD">
                          <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")>
                          <#if giftCard?exists>
                            <#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress")?if_exists>
                          </#if>
                          <tr>
                            <td align="right" valign="top" width="15%">
                              <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderGiftCard}</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                              <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                              <div class="tabletext">
                                <#if giftCard?has_content>
                                  <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                    ${giftCard.cardNumber?default("N/A")} [${giftCard.pinNumber?default("N/A")}]
                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.description}<#else>${orderPaymentPreference.statusId}</#if>]
                                  <#else>
                                    <#if giftCard?has_content && giftCard.cardNumber?has_content>
                                      <#assign giftCardNumber = "">
                                      <#assign pcardNumber = giftCard.cardNumber>
                                      <#if pcardNumber?has_content>
                                        <#assign psize = pcardNumber?length - 4>
                                        <#if 0 < psize>
                                          <#list 0 .. psize-1 as foo>
                                            <#assign giftCardNumber = giftCardNumber + "*">
                                          </#list>
                                          <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3]>
                                        <#else>
                                          <#assign giftCardNumber = pcardNumber>
                                        </#if>
                                      </#if>
                                    </#if>
                                    ${giftCardNumber?default("N/A")}
                                    &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.description}<#else>${orderPaymentPreference.statusId}</#if>]
                                  </#if>
                                <#else>
                                  ${uiLabelMap.CommonInformationNotAvailable}
                                </#if>
                              </div>
                            </td>
                          </tr>
                        </#if>
                      </#if>
                      <#if pmBillingAddress?has_content>
                        <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="5"><hr class="sepbar"></td></tr>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;</div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              <#if pmBillingAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b> ${pmBillingAddress.toName}<br></#if>
                              <#if pmBillingAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b> ${pmBillingAddress.attnName}<br></#if>
                              ${pmBillingAddress.address1}<br>
                              <#if pmBillingAddress.address2?has_content>${pmBillingAddress.address2}<br></#if>
                              ${pmBillingAddress.city}<#if pmBillingAddress.stateProvinceGeoId?has_content>, ${pmBillingAddress.stateProvinceGeoId} </#if>
                              ${pmBillingAddress.postalCode?if_exists}<br>
                              ${pmBillingAddress.countryGeoId?if_exists}
                            </div>
                          </td>
                        </tr>
                      </#if>
                    </#list>

                    <#-- billing account -->
                    <#if billingAccount?exists>
                      <#if outputted?default("false") == "true">
                        <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      </#if>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.AccountingBillingAccount}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            #${billingAccount.billingAccountId} - ${billingAccount.description?if_exists}
                          </div>
                        </td>
                      </tr>
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderPurchaseOrderNumber}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${customerPoNumber?if_exists}</div>
                        </td>
                      </tr>
                    </#if>

                    <#-- invoices -->
                    <#if invoices?has_content>
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderInvoices}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <#list invoices as invoice>
                            <div class="tabletext">#<a href="/accounting/control/viewInvoice?invoiceId=${invoice}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">${invoice}</a></div>
                          </#list>
                        </td>
                      </tr>
                    </#if>
                    </table>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </#if>
      <#-- end of payment box -->
    </td>
    <td width="1">&nbsp;&nbsp;</td>
    <td width="50%" valign="top" align="left">
      <#-- contact box -->
      <#if displayParty?has_content || orderContactMechValueMaps?has_content>
      <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
        <tr>
          <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${uiLabelMap.OrderContactInformation}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width="100%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1" cellspacing="0">
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonName}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td NOWRAP align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if displayParty?has_content>
                            ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(displayParty)}
                          </#if>
                          <#if partyId?exists>
                            &nbsp;(<a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" target="partymgr" class="buttontext">${partyId}</a>)
                          </#if>
                        </div>
                      </td>
                    </tr>
                    <#list orderContactMechValueMaps as orderContactMechValueMap>
                      <#assign contactMech = orderContactMechValueMap.contactMech>
                      <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
                      <#--<#assign partyContactMech = orderContactMechValueMap.partyContactMech>-->
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${contactMechPurpose.description}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                            <#assign postalAddress = orderContactMechValueMap.postalAddress>
                            <#if postalAddress?has_content>
                              <div class="tabletext">
                                <#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b> ${postalAddress.toName}<br></#if>
                                <#if postalAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b> ${postalAddress.attnName}<br></#if>
                                ${postalAddress.address1}<br>
                                <#if postalAddress.address2?has_content>${postalAddress.address2}<br></#if>
                                ${postalAddress.city}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if>
                                ${postalAddress.postalCode?if_exists}<br>
                                ${postalAddress.countryGeoId?if_exists}<br>
                                <#if !postalAddress.countryGeoId?exists || postalAddress.countryGeoId == "USA">
                                  <#assign addr1 = postalAddress.address1?if_exists>
                                  <#if (addr1.indexOf(" ") > 0)>
                                    <#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
                                    <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
                                    <a target="_blank" href="http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18" class="buttontext">(lookup:whitepages.com)</a>
                                  </#if>
                                </#if>
                              </div>
                            </#if>
                          <#elseif contactMech.contactMechTypeId == "TELECOM_NUMBER">
                            <#assign telecomNumber = orderContactMechValueMap.telecomNumber>
                            <div class="tabletext">
                              ${telecomNumber.countryCode?if_exists}
                              <#if telecomNumber.areaCode?exists>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber}
                              <#--<#if partyContactMech.extension?exists>ext&nbsp;${partyContactMech.extension}</#if>-->
                              <#if !telecomNumber.countryCode?exists || telecomNumber.countryCode == "011" || telecomNumber.countryCode == "1">
                                <a target="_blank" href="http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8" class="buttontext">(lookup:anywho.com)</a>
                                <a target="_blank" href="http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode}&s=&p=${telecomNumber.contactNumber}&pt=b&x=40&y=9" class="buttontext">(lookup:whitepages.com)</a>
                              </#if>
                            </div>
                          <#elseif contactMech.contactMechTypeId == "EMAIL_ADDRESS">
                            <div class="tabletext">
                              ${contactMech.infoString}
                              <a href="mailto:${contactMech.infoString}" class="buttontext">(${uiLabelMap.OrderSendEmail})</a>
                            </div>
                          <#elseif contactMech.contactMechTypeId == "WEB_ADDRESS">
                            <div class="tabletext">
                              ${contactMech.infoString}
                              <#assign openString = contactMech.infoString>
                              <#if !openString?starts_with("http") && !openString?starts_with("HTTP")>
                                <#assign openString = "http://" + openString>
                              </#if>
                              <a target="_blank" href="${openString}" class="buttontext">(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                            </div>
                          <#else>
                            <div class="tabletext">
                              ${contactMech.infoString?if_exists}
                            </div>
                          </#if>
                        </td>
                      </tr>
                    </#list>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      </#if>
      <#-- end of contact box -->

      <#-- shipping info box -->
      <#if shipGroups?has_content>
        <#list shipGroups as shipGroup>
          <br>
          <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
          <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
          <table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
            <tr>
              <td width="100%">
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                  <tr>
                    <td valign="middle" align="left">
                      <div class="boxhead">&nbsp;${uiLabelMap.OrderShipmentInformation} - ${shipGroup.shipGroupSeqId}</div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td width="100%">
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
                  <tr>
                    <td>

                  <table width="100%" border="0" cellpadding="1" cellspacing="0">
                    <#if shipGroup.contactMechId?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Address</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            ${(shipGroupAddress.address1)?default("")}
                          </div>
                        </td>
                      </tr>
                    </#if>

                    <#if shipGroup.shipmentMethodTypeId?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonMethod}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <#if shipGroup.carrierPartyId?has_content || shipmentMethodType?has_content>
                            <div class="tabletext">
                              <#if shipGroup.carrierPartyId != "_NA_">
                                ${shipGroup.carrierPartyId?if_exists}
                              </#if>
                              ${shipmentMethodType.description?default("")}
                            </div>
                          </#if>
                        </td>
                      </tr>
                    </#if>

                    <#if !shipGroup.contactMechId?has_content && !shipGroup.shipmentMethodTypeId?has_content>
                      <#assign noShipment = "true">
                      <tr>
                        <td colspan="3" align="center">
                          <div class="tableheadtext">Not Shipped</div>
                        </td>
                      </tr>
                    </#if>

                    <#-- tracking number -->
                    <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderTrackingNumber}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
                          <#if shipGroup.trackingNumber?has_content>
                            <div class="tabletext">${shipGroup.trackingNumber}</div>
                          </#if>
                          <#if orderShipmentInfoSummaryList?has_content>
                            <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
                              <#if orderShipmentInfoSummary.shipGroupSeqId?if_exists == shipGroup.shipGroupSeqId?if_exists>
                                <div class="tabletext">
                                  <#if (orderShipmentInfoSummaryList?size > 1)>${orderShipmentInfoSummary.shipmentPackageSeqId}: </#if>
                                  Code: ${orderShipmentInfoSummary.trackingCode?default("[Not Yet Known]")}
                                  <#if orderShipmentInfoSummary.boxNumber?has_content> Box #${orderShipmentInfoSummary.boxNumber}</#if> 
                                  <#if orderShipmentInfoSummary.carrierPartyId?has_content>(Carrier: ${orderShipmentInfoSummary.carrierPartyId})</#if>
                                </div>
                              </#if>
                            </#list>
                          </#if>
                        </td>
                      </tr>
                    </#if>
                    <#if shipGroup.maySplit?has_content && noShipment?default("false") != "true">
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderSplittingPreference}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if shipGroup.maySplit?upper_case == "N">
                                ${uiLabelMap.FacilityWaitEntireOrderReady}
                                <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
                                  <#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED"><a href="<@ofbizUrl>/allowordersplit?orderId=${orderId}&shipGroupSeqId=${shipGroup.shipGroupSeqId}&${paramString}</@ofbizUrl>" class="buttontext">[Allow&nbsp;Split]</a></#if>
                                </#if>
                            <#else>
                                ${uiLabelMap.FacilityShipAvailable}
                            </#if>
                          </div>
                        </td>
                      </tr>
                    </#if>
                    <#if shipGroup.shippingInstructions?has_content>
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonInstructions}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${shipGroup.shippingInstructions}</div>
                        </td>
                      </tr>
                    </#if>
                    <#if shipGroup.isGift?has_content && noShipment?default("false") != "true">
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderGift}?</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if shipGroup.isGift?upper_case == "N">${uiLabelMap.OrderThisOrderNotGift}<#else>${uiLabelMap.OrderThisOrderGift}</#if>
                          </div>
                        </td>
                      </tr>
                    </#if>
                    <#if shipGroup.giftMessage?has_content>
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.OrderGiftMessage}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${shipGroup.giftMessage}</div>
                        </td>
                      </tr>
                    </#if>

                   <#assign shipGroupShipments = shipGroup.getRelated("PrimaryShipment")>
                   <#if shipGroupShipments?has_content>
                      <tr><td colspan="7"><hr class="sepbar"></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${uiLabelMap.FacilityShipments}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                            <#list shipGroupShipments as shipment>
                                <div class="tabletext">#<a href="/facility/control/ViewShipment?shipmentId=${shipment.shipmentId}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">${shipment.shipmentId}</a></div>
                            </#list>
                        </td>
                      </tr>
                   </#if>
                   <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
                     <#if orderHeader.statusId == "ORDER_APPROVED" || orderHeader.statusId == "ORDER_SENT">
                       <tr><td colspan="7"><hr class="sepbar"></td></tr>
                       <tr>
                         <td align="right" valign="top" width="15%">
                           <div class="tabletext">&nbsp;</div>
                         </td>
                         <td width="5">&nbsp;</td>
                         <td align="left" valign="top" width="80%">
                           <div class="tabletext"><a href="/facility/control/EditShipment?primaryOrderId=${orderId}&primaryShipGroupSeqId=${shipGroup.shipGroupSeqId}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">New Shipment For Ship Group [${shipGroup.shipGroupSeqId}]</a></div>
                         </td>
                       </tr>
                     </#if>
                   </#if>

                   <#if !shipGroup_has_next>
                     <tr><td colspan="7"><hr class="sepbar"></td></tr>
                     <tr>
                       <td align="right" valign="top" width="15%">
                         <div class="tabletext">&nbsp;</div>
                       </td>
                       <td width="5">&nbsp;</td>
                       <td align="left" valign="top" width="80%">
                         <#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
                           <#if orderHeader.statusId == "ORDER_APPROVED" || orderHeader.statusId == "ORDER_SENT">
                             <div class="tabletext"><a href="<@ofbizUrl>/quickShipOrder?orderId=${orderId}&${paramString}</@ofbizUrl>" class="buttontext">Quick-Ship Entire Order</a></div>
                           </#if>
                           <#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED">
                             <div class="tabletext"><a href="<@ofbizUrl>/OrderDeliveryScheduleInfo?orderId=${orderId}</@ofbizUrl>" class="buttontext">View/Edit Delivery Schedule Info</a></div>
                           </#if>
                           <#if security.hasEntityPermission("ORDERMGR", "_RETURN", session) && orderHeader.statusId == "ORDER_COMPLETED">
                             <div><a href="<@ofbizUrl>/quickRefundOrder?order_id=${orderId}&orderId=${orderId}&receiveReturn=true</@ofbizUrl>" class="buttontext">Quick-Refund Entire Order</a></div>
                             <div><a href="<@ofbizUrl>/quickreturn?order_id=${orderId}&party_id=${partyId?if_exists}</@ofbizUrl>" class="buttontext">Create Return</a></div>
                           </#if>
                         </#if>
                       </td>
                     </tr>
                   </#if>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      </#list>
      </#if>
      <#-- end of shipping info box -->
    </td>
  </tr>
</table>
