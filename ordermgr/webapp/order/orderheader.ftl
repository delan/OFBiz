<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@version    $Revision$
 *@since      2.2
-->

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width='50%' valign='top' align='left'>
      <#-- header box -->
      <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;Order #${orderId} Information</div>
                </td>           
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1" cellspacing='0'>                
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Status History</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class='tabletext'>Current Status: ${currentStatus.description}</div>
                        <#if orderHeaderStatuses?has_content>
                          <hr class="sepbar">
                          <#list orderHeaderStatuses as orderHeaderStatus>
                            <#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem")>
                            <div class='tabletext'>
                              ${loopStatusItem.description} - ${orderHeaderStatus.statusDatetime.toString()}
                            </div>
                          </#list>
                        </#if>
                      </td>
                    </tr>             
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Date Ordered</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${orderHeader.orderDate.toString()}
                        </div>
                      </td>
                    </tr>              
                    <#if distributorId?exists>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Distributor</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${Static["org.ofbiz.commonapp.party.party.PartyHelper"].formatPartyId(distributorId, delegator)}
                        </div>
                      </td>
                    </tr>
                    </#if>                  
                    <#if affiliateId?exists>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Affiliate</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          ${Static["org.ofbiz.commonapp.party.party.PartyHelper"].formatPartyId(affiliateId, delegator)}
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
        <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
                <tr>
                  <td valign="middle" align="left">
                    <div class="boxhead">&nbsp;Payment Information</div>
                  </td>          
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td width='100%'>
              <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
                <tr>
                  <td>
                    <table width="100%" border="0" cellpadding="1" cellspacing='0'>
                    <#list orderPaymentPreferences as orderPaymentPreference>
                      <#if outputted?exists>
                        <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      </#if>
                      <#assign outputted = "true">                  
                      <#-- try the paymentMethod first; if paymentMethodId is specified it overrides paymentMethodTypeId -->
                      <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
                      <#if !paymentMethod?has_content>
                        <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")>
                        <tr>
                          <td>
                            <div class="tabletext">&nbsp;<b>${paymentMethodType.description?if_exists}</b></div>
                          </td>
                          <#if paymentMethodType.paymentMethodTypeId != "EXT_OFFLINE">
                            <td align="right">
                              <div class="tabletext">${orderPaymentPreference.maxAmount?default(0.00)?string.currency}&nbsp;-&nbsp;${(orderPaymentPreference.authDate.toString())?if_exists}</div>
                              <div class="tabletext">&nbsp;<#if orderPaymentPreference.authRefNum?exists>(Ref: ${orderPaymentPreference.authRefNum})</#if></div>
                           </td>
                          <#else>
                            <td align="right">                            
                              <a valign="top" href="<@ofbizUrl>/receivepayment?${paramString}</@ofbizUrl>" class="buttontext">Receive Payment</a>
                            </td>
                          </#if>
                        </tr>
                      <#else>
                        <#if paymentMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
                          <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                          <#assign payments = orderPaymentPreference.getRelated("Payment")>
                          <#if payments?has_content>
                            <#assign payment = payments[0]>
                          </#if>
                          <#if creditCard?exists>
                            <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress")>                        
                          </#if>
                          <tr>
                            <td align="right" valign="top" width="15%">
                              <div class="tabletext">&nbsp;<b>Credit Card</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                              <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                              <div class="tabletext">
                                ${creditCard.nameOnCard?if_exists}<br>
                                <#if creditCard.companyNameOnCard?exists>${creditCard.companyNameOnCard}<br></#if>
                                <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                  ${creditCard.cardType}
                                  ${creditCard.cardNumber}
                                  ${creditCard.expireDate}
                                  &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.description}<#else>${orderPaymentPreference.statusId}</#if>]                                    
                                <#else>
                                  ${Static["org.ofbiz.commonapp.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                </#if>
                              </div>                              
                              <#if orderPaymentPreference.authDate?exists>
                                <div class="tabletext">
                                  Auth&nbsp;:&nbsp;${orderPaymentPreference.authDate.toString()}
                                  &nbsp;&nbsp;(<b>Ref:</b>&nbsp;${orderPaymentPreference.authRefNum?if_exists})
                                </div>
                              </#if>
                              <#if payment?exists && payment.effectiveDate?exists>
                                <div class="tabletext">
                                  Billed&nbsp;:&nbsp;${payment.effectiveDate.toString()}
                                  &nbsp;&nbsp;(<b>Ref:</b>&nbsp;${payment.paymentRefNum?if_exists})
                                </div>
                              </#if>                            
                            </td>
                          </tr>
                        <#elseif paymentMethod.paymentMethodTypeId?if_exists == "EFT_ACCOUNT">                        
                          <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                          <#if eftAccount?exists>
                            <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress")>
                          </#if>
                          <tr>
                            <td align="right" valign="top" width="15%">
                              <div class="tabletext">&nbsp;<b>EFT Account</b></div>
                            </td>
                            <td width="5">&nbsp;</td>
                            <td align="left" valign="top" width="80%">
                              <div class="tabletext">
                                ${eftAccount.nameOnAccount}<br>
                                <#if eftAccount.companyNameOnAccount?exists>${eftAccount.companyNameOnAccount}<br></#if>
                                Bank: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br>
                                Account#: ${eftAccount.accountNumber}                              
                              </div>
                            </td>
                          </tr>                        
                        </#if>
                      </#if>
                      <#if pmBillingAddress?exists>
                        <tr><td>&nbsp;</td><td>&nbsp;</td><td colspan="5"><hr class='sepbar'></td></tr>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;<b>Address</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              <#if pmBillingAddress.toName?has_content><b>To:</b> ${pmBillingAddress.toName}<br></#if>
                              <#if pmBillingAddress.attnName?has_content><b>Attn:</b> ${pmBillingAddress.attnName}<br></#if>
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
                      <#if outputted?exists>
                        <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      </#if>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Billing Account</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            #${billingAccount.billingAccountId} - ${billingAccount.description?if_exists}
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
                          <div class="tabletext">${customerPoNumber?if_exists}</div>
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
    <td width='50%' valign='top' align='left'>
      <#-- contact box -->
      <#if userPerson?has_content || orderContactMechValueMaps?has_content>
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;Contact Information</div>
                </td>          
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
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
                          <#if userPerson?has_content>
                            ${Static["org.ofbiz.commonapp.party.party.PartyHelper"].getPersonName(userPerson)}
                          </#if>
                          <#if partyId?exists>
                            &nbsp;(<a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" target='partymgr' class="buttontext">${partyId}</a>)
                          </#if>
                        </div>
                      </td>
                    </tr>
                    <#list orderContactMechValueMaps as orderContactMechValueMap>
                      <#assign contactMech = orderContactMechValueMap.contactMech>
                      <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
                      <#--<#assign partyContactMech = orderContactMechValueMap.partyContactMech>-->
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
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
                                <#if postalAddress.toName?has_content><b>To:</b> ${postalAddress.toName}<br></#if>
                                <#if postalAddress.attnName?has_content><b>Attn:</b> ${postalAddress.attnName}<br></#if>
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
                                    <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
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
                                <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                                <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode}&s=&p=${telecomNumber.contactNumber}&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                              </#if>
                            </div>
                          <#elseif contactMech.contactMechTypeId == "EMAIL_ADDRESS">
                            <div class="tabletext">
                              ${contactMech.infoString}
                              <a href='mailto:${contactMech.infoString}' class='buttontext'>(send&nbsp;email)</a>
                            </div>
                          <#elseif contactMech.contactMechTypeId == "WEB_ADDRESS">              
                            <div class="tabletext">
                              ${contactMech.infoString}
                              <#assign openString = contactMech.infoString>
                              <#if !openString?starts_with("http") && !openString?starts_with("HTTP")>
                                <#assign openString = "http://" + openString>
                              </#if>                              
                              <a target='_blank' href='${openString}' class='buttontext'>(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
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
      <br>
      </#if>           
      <#-- end of contact box -->
      
      <#-- shipping info box -->
      <table border='0' width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;Shipment Information</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1" cellspacing='0'>
                  <#if shipmentPreference?has_content>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Method</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <#if carrierPartyId?has_content || shipmentMethodType?has_content>
                          <div class="tabletext">
                            ${carrierPartyId?if_exists}
                            ${shipmentMethodType.description?default("")}
                          </div>
                        </#if>
                      </td>
                    </tr>                    
                    <#if maySplit?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Splitting Preference</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">                         
                            <#if maySplit?upper_case == "N">
                                Please wait until the entire order is ready before shipping.
                                <a href="<@ofbizUrl>/allowordersplit?orderId=${orderId}&${paramString}</@ofbizUrl>" class="buttontext">[Allow&nbsp;Split]</a>
                            <#else>
                                Please ship items I ordered as they become available (may incur additional shipping charges).
                            </#if>
                          </div>
                        </td>
                      </tr>
                    </#if>
                    <#if shippingInstructions?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Instructions</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${shippingInstructions}</div>
                        </td>
                      </tr>
                    </#if>
                    <#if isGift?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>                    
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Gift?</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if isGift?upper_case == "N">This order is not a gift.<#else>This order is a gift.</#if>
                          </div>
                        </td>
                      </tr>
                    </#if>
                    <#if giftMessage?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Gift Message</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${giftMessage}</div>
                        </td>
                      </tr>
                    </#if>
                  </#if>     
                  <#if allShipments?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Shipments</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                            <#list allShipments as shipment>
                                <div class="tabletext">Shipment: <a href="/facility/control/ViewShipment?shipmentId=${shipment.shipmentId}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">${shipment.shipmentId}</a></div>
                            </#list>
                        </td>
                      </tr>
                  </#if>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;</div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                            <div class="tabletext"><a href="<@ofbizUrl>/OrderDeliveryScheduleInfo?orderId=${orderId}</@ofbizUrl>" class="buttontext">View/Edit Delivery Schedule Info</a></div>
                            <div class="tabletext"><a href="/facility/control/EditShipment?primaryOrderId=${orderId}&externalLoginKey=${requestAttributes.externalLoginKey}" class="buttontext">New Shipment</a></div>
                        </td>
                      </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table> 
      <#-- end of shipping info box -->
    </td>
  </tr>
</table>
