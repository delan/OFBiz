<#--
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *@since      2.1
-->

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <#-- left side -->
    <td width='50%' valign='top' align='left'>
      <table border='0' cellspacing='0' cellpadding='0' class='boxoutside'>
        <#-- general order info -->
        <tr>
          <td width='100%'>
            <table border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${requestAttributes.uiLabelMap.OrderOrder}&nbsp;<#if orderHeader?has_content>#<a href="<@ofbizUrl>/orderstatus?order_id=${orderHeader.orderId}</@ofbizUrl>" class="lightbuttontext">${orderHeader.orderId}</a>&nbsp;</#if>${requestAttributes.uiLabelMap.CommonInformation}</div>
                </td>
                <#if maySelectItems?default(false) && returnLink?default("N") == "Y">
                  <td valign="middle" align="right" nowrap>
                    <a href="<@ofbizUrl>/makeReturn?order_id=${orderHeader.orderId}</@ofbizUrl>" class="submenutextright">${requestAttributes.uiLabelMap.OrderRequestReturn}</a>
                  </td>
                </#if>
              </tr>
            </table>
          </td>
        </tr>        
        <tr>
          <td width='100%'>
            <table border='0' cellspacing='0' cellpadding='0' class='boxbottom'>              
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#-- placing customer information -->
                    <#if placingCustomerPerson?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.PartyName}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">                           
                            ${placingCustomerPerson.firstName}&nbsp;
                            <#if placingCustomerPerson.middleName?exists>${placingCustomerPerson.middleName}&nbsp;</#if>
                            ${placingCustomerPerson.lastName}
                          </div>
                        </td>
                      </tr>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    </#if>
                    <#-- order status information -->
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.CommonStatus}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <#if orderHeader?has_content>                                                
                          <div class="tabletext">${localOrderReadHelper.getStatusString()}</div>
                        <#else>
                          <div class="tabletext"><b>${requestAttributes.uiLabelMap.OrderNotYetOrdered}</b></div>
                        </#if>
                      </td>
                    </tr>
                    <#-- ordered date -->
                    <#if orderHeader?has_content>   
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.CommonDate}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${orderHeader.orderDate.toString()}</div>
                        </td>
                      </tr>
                    </#if>
                    <#if distributorId?exists>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderDistributor}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${distributorId}</div>
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
      <br>
      
      <#if !orderHeader?has_content || orderPaymentPreferences?has_content || billingAccount?has_content>
      <table border=0 cellspacing='0' cellpadding='0' class='boxoutside'>
        <#-- order payment info -->
        <tr>
          <td width='100%'>
            <table border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${requestAttributes.uiLabelMap.AccountingPaymentInformation}</div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td width='100%'>
            <table border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#-- offline payment address infomation :: change this to use Company's address -->
                    <#if !paymentMethod?has_content && paymentMethodType?has_content>
                      <tr>
                        <#if paymentMethodType.paymentMethodTypeId == "EXT_OFFLINE">
                          <td colspan="3" valign="top">
                            <div class="tabletext" align="center"><b>${requestAttributes.uiLabelMap.AccountingOfflinePayment}</b></div>                            
                            <#if orderHeader?has_content && paymentAddress?has_content> 
                              <div class="tabletext" align="center"><hr class="sepbar"></div>
                              <div class="tabletext" align="center"><b>${requestAttributes.uiLabelMap.OrderSendPaymentTo}:</b></div>
                              <#if paymentAddress.toName?has_content><div class="tabletext" align="center">${paymentAddress.toName}</div></#if>
                              <#if paymentAddress.attnName?has_content><div class="tabletext" align="center"><b>${requestAttributes.uiLabelMap.PartyAddrAttnName}:</b> ${paymentAddress.attnName}</div></#if>
                              <div class="tabletext" align="center">${paymentAddress.address1}</div>
                              <#if paymentAddress.address2?has_content><div class="tabletext" align="center">${paymentAddress.address2}</div></#if>                            
                              <div class="tabletext" align="center">${paymentAddress.city}<#if paymentAddress.stateProvinceGeoId?has_content>, ${paymentAddress.stateProvinceGeoId}</#if> ${paymentAddress.postalCode?if_exists}
                              <div class="tabletext" align="center">${paymentAddress.countryGeoId}</div>                                                                                                                
                              <div class="tabletext" align="center"><hr class="sepbar"></div>
                              <div class="tabletext" align="center"><b>${requestAttributes.uiLabelMap.OrderBesureIncludeOrder} #</b></div>
                            </#if>                         
                          </td>                  
                        <#else>
                          <#assign outputted = true>
                          <td colspan="3" valign="top">
                            <div class="tabletext" align="center"><b>${requestAttributes.uiLabelMap.AccountingPaymentVia} ${paymentMethodType.description}</b></div>
                          </td>
                        </#if>
                      </tr>
                    </#if>
                    <#if paymentMethod?has_content>
                      <#assign outputted = true>
                      <#-- credit card info -->                     
                      <#if creditCard?has_content>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.AccountingCreditCard}</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              ${creditCard.nameOnCard}<br>
                              <#if creditCard.companyNameOnCard?has_content>${creditCard.companyNameOnCard}<br></#if>
                              ${formattedCardNumber}
                            </div>
                          </td>
                        </tr>
                      <#-- EFT account info -->
                      <#elseif eftAccount?has_content>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.AccountingEftAccount}</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              ${eftAccount.nameOnAccount?if_exists}<br>
                              <#if eftAccount.companyNameOnAccount?has_content>${eftAccount.companyNameOnAccount}<br></#if>
                              ${requestAttributes.uiLabelMap.AccountingBank}: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br>
                              ${requestAttributes.uiLabelMap.AccountingAccount} #: ${eftAccount.accountNumber}
                            </div>
                          </td>
                        </tr>
                      </#if>
                    </#if>
                    <#-- billing account info -->
                    <#if billingAccount?has_content>
                      <#if outputted?default(false)>
                        <tr><td colspan="3"><hr class='sepbar'></td></tr>
                      </#if>
                      <#assign outputted = true>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.AccountingBillingAccount}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            #${billingAccount.billingAccountId?if_exists} - ${billingAccount.description?if_exists}
                          </div>
                        </td>
                      </tr>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderPurchaseOrderNumber}</b></div>
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
    </td>
    <td bgcolor="white" width="1">&nbsp;&nbsp;</td>
    <#-- right side -->
    <td width='50%' valign='top' align='left'>
      <table border=0 cellspacing='0' cellpadding='0' class='boxoutside'>        
        <tr>
          <td width='100%'>
            <table border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;${requestAttributes.uiLabelMap.OrderShippingInformation}</div>
                </td>
                <#if maySelectItems?default(false) && !maySplit?default(false)>
                <td valign="middle" align="right">
                  <a href="<@ofbizUrl>/allowordersplit?orderId=${orderHeader.orderId}&order_id=${orderHeader.orderId}</@ofbizUrl>" class="submenutextright">${requestAttributes.uiLabelMap.OrderAllowSplit}</a>
                </td> 
                </#if>               
              </tr>
            </table>
          </td>
        </tr>
        <#-- shipping address -->
        <tr>
          <td width='100%'>
            <table border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#if shippingAddress?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderDestination}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if shippingAddress.toName?has_content><b>${requestAttributes.uiLabelMap.CommonTo}:</b> ${shippingAddress.toName}<br></#if>
                            <#if shippingAddress.attnName?has_content><b>${requestAttributes.uiLabelMap.PartyAddrAttnName}:</b> ${shippingAddress.attnName}<br></#if>
                            ${shippingAddress.address1}<br>
                            <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br></#if>                            
                            ${shippingAddress.city}<#if shippingAddress.stateProvinceGeoId?has_content>, ${shippingAddress.stateProvinceGeoId} </#if>
                            ${shippingAddress.postalCode?if_exists}<br>
                            ${shippingAddress.countryGeoId?if_exists}
                          </div>
                        </td>
                      </tr>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    </#if>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderMethod}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if carrierPartyId?exists && carrierPartyId != "_NA_">${carrierPartyId?if_exists}</#if>
                          ${shipMethDescription?if_exists}
                          <#if shippingAccount?exists><br>${requestAttributes.uiLabelMap.AccountingUseAccount}: ${shippingAccount}</#if>
                        </div>
                      </td>
                    </tr>
                    <#-- tracking number -->
                    <#if trackingNumber?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderTrackingNumber}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
                          <div class="tabletext">${trackingNumber}</div>
                        </td>
                      </tr>
                    </#if>
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <#-- splitting preference -->
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderSplittingPreference}</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if !maySplit?default(false)>${requestAttributes.uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.</#if>
                          <#if maySplit?default(false)>${requestAttributes.uiLabelMap.OrderPleaseShipItemsBecomeAvailable}.</#if>
                        </div>
                      </td>
                    </tr>
                    <#-- shipping instructions -->
                    <#if shippingInstructions?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderIntructions}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${shippingInstructions}</div>
                        </td>
                      </tr>
                    </#if>
                    <#-- gift settings 
                   <tr><td colspan="7"><hr class='sepbar'></td></tr>
                   <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderGift}?</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if !isGift?default(false)>${requestAttributes.uiLabelMap.OrderThisIsNotGift}.</#if>
                          <#if isGift?default(false)>${requestAttributes.uiLabelMap.OrderThisIsGift}.</#if>
                        </div>
                      </td>
                    </tr> -->
                    <#if giftMessage?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>${requestAttributes.uiLabelMap.OrderGiftMessage}</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">${giftMessage}</div>
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
    </td>
  </tr>
</table>

