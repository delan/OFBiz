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
 *@version    $Rev:$
 *@since      2.1
-->


      <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>        
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
              <tr>
                <td valign="middle" align="left">
                  <div class="boxhead">&nbsp;Order Confirmation<#if orderHeader?exists>&nbsp;#<a href="<@ofbizUrl>/orderview?order_id=${orderHeader.orderId}</@ofbizUrl>" class="lightbuttontext">${orderHeader.orderId}</a></#if></div>
                </td>
                <#if !orderHeader?exists>
                <td align="right">
                  <div class="tabletext">
                    <a href="<@ofbizUrl>/orderentry</@ofbizUrl>" class="submenutext">Items</a><#if cart?has_content && cart.getOrderType() != "PURCHASE_ORDER"><a href="<@ofbizUrl>/setShipping</@ofbizUrl>" class="submenutext">Shipping</a><a href="<@ofbizUrl>/setOptions</@ofbizUrl>" class="submenutext">Options</a><a href="<@ofbizUrl>/setBilling</@ofbizUrl>" class="submenutext">Payment</a></#if><a href="<@ofbizUrl>/setAdditionalParty</@ofbizUrl>" class="submenutext">Parties</a><a href="<@ofbizUrl>/processorder</@ofbizUrl>" class="submenutextright">Create Order</a>
                  </div>
                </td>   
                </#if>                             
              </tr>
            </table>
          </td>
        </tr>
        <#-- shipping address -->
        <tr>
          <td width='100%'>
            <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
              <tr>
                <td>
                  <table width="100%" border="0" cellpadding="1">
                    <#if shippingAddress?has_content>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Destination</b></div>
                        </td>
                        <td width="5">&nbsp;</td>
                        <td align="left" valign="top" width="80%">
                          <div class="tabletext">
                            <#if shippingAddress.toName?has_content><b>To:</b> ${shippingAddress.toName}<br></#if>
                            <#if shippingAddress.attnName?has_content><b>Attn:</b> ${shippingAddress.attnName}<br></#if>
                            ${shippingAddress.address1}<br>
                            <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br></#if>                            
                            ${shippingAddress.city}<#if shippingAddress.stateProvinceGeoId?has_content>, ${shippingAddress.stateProvinceGeoId} </#if>
                            ${shippingAddress.postalCode}<br>
                            ${shippingAddress.countryGeoId}
                          </div>
                        </td>
                      </tr>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    </#if>
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Method</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if carrierPartyId?exists && carrierPartyId != "_NA_">${carrierPartyId?if_exists}</#if>
                          ${shipMethDescription?if_exists}
                          <#if shippingAccount?exists><br>Use Account: ${shippingAccount}</#if>
                        </div>
                      </td>
                    </tr>
                    <#-- tracking number -->
                    <#if trackingNumber?has_content>
                      <tr><td colspan="7"><hr class='sepbar'></td></tr>
                      <tr>
                        <td align="right" valign="top" width="15%">
                          <div class="tabletext">&nbsp;<b>Tracking Number</b></div>
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
                        <div class="tabletext">&nbsp;<b>Splitting Preference</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if maySplit?default("N") == "N">Please wait until the entire order is ready before shipping.</#if>
                          <#if maySplit?default("Y") == "Y">Please ship items I ordered as they become available (may incur additional shipping charges).</#if>
                        </div>
                      </td>
                    </tr>
                    <#-- shipping instructions -->
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
                    <tr><td colspan="7"><hr class='sepbar'></td></tr>
                    <#-- gift settings -->
                    <tr>
                      <td align="right" valign="top" width="15%">
                        <div class="tabletext">&nbsp;<b>Gift?</b></div>
                      </td>
                      <td width="5">&nbsp;</td>
                      <td align="left" valign="top" width="80%">
                        <div class="tabletext">
                          <#if isGift?default("N") == "N">This order is not a gift.</#if>
                          <#if isGift?default("N") == "Y">This order is a gift.</#if>
                        </div>
                      </td>
                    </tr>
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
                  </table>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
   
      <#if paymentMethod?has_content || paymentMethodType?has_content || billingAccount?has_content>
      <br>      
      <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
        <#-- order payment info -->
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
                  <table width="100%" border="0" cellpadding="1">
                    <#-- offline payment address infomation :: change this to use Company's address -->
                    <#if !paymentMethod?has_content && paymentMethodType?has_content>
                      <tr>
                        <#if paymentMethodType.paymentMethodTypeId == "EXT_OFFLINE">
                          <td colspan="3" valign="top">
                            <div class="tabletext" align="center"><b>Offline Payment</b></div>                            
                            <#if orderHeader?has_content && paymentAddress?has_content> 
                              <div class="tabletext" align="center"><hr class="sepbar"></div>
                              <div class="tabletext" align="center"><b>Please Send Payment To:</b></div>
                              <#if paymentAddress.toName?has_content><div class="tabletext" align="center">${paymentAddress.toName}</div></#if>
                              <#if paymentAddress.attnName?has_content><div class="tabletext" align="center"><b>Attn:</b> ${paymentAddress.attnName}</div></#if>
                              <div class="tabletext" align="center">${paymentAddress.address1}</div>
                              <#if paymentAddress.address2?has_content><div class="tabletext" align="center">${paymentAddress.address2}</div></#if>                            
                              <div class="tabletext" align="center">${paymentAddress.city}<#if paymentAddress.stateProvinceGeoId?has_content>, ${paymentAddress.stateProvinceGeoId}</#if> ${paymentAddress.postalCode}
                              <div class="tabletext" align="center">${paymentAddress.countryGeoId}</div>                                                                                                                
                              <div class="tabletext" align="center"><hr class="sepbar"></div>
                              <div class="tabletext" align="center"><b>Be sure to include your order #</b></div>
                            </#if>                         
                          </td>                  
                        <#else>
                          <#assign outputted = true>
                          <td colspan="3" valign="top">
                            <div class="tabletext" align="center"><b>Payment Via ${paymentMethodType.description}</b></div>
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
                            <div class="tabletext">&nbsp;<b>Credit Card</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              <#if creditCard.companyNameOnCard?has_content>${creditCard.companyNameOnCard}<br></#if>
                              <#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}&nbsp</#if>
                              ${creditCard.firstNameOnCard}&nbsp;
                              <#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}&nbsp</#if>
                              ${creditCard.lastNameOnCard}
                              <#if creditCard.suffixOnCard?has_content>&nbsp;${creditCard.suffixOnCard}</#if>
                              <br>
                              ${formattedCardNumber}
                            </div>
                          </td>
                        </tr>
                      <#-- EFT account info -->
                      <#elseif eftAccount?has_content>
                        <tr>
                          <td align="right" valign="top" width="15%">
                            <div class="tabletext">&nbsp;<b>EFT Account</b></div>
                          </td>
                          <td width="5">&nbsp;</td>
                          <td align="left" valign="top" width="80%">
                            <div class="tabletext">
                              ${eftAccount.nameOnAccount}<br>
                              <#if eftAccount.companyNameOnAccount?has_content>${eftAccount.companyNameOnAccount}<br></#if>
                              Bank: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br>
                              Account #: ${eftAccount.accountNumber}
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
                          <div class="tabletext">&nbsp;<b>Billing Account</b></div>
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

